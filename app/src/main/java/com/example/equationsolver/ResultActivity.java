package com.example.equationsolver;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.equationsolver.solver.QuadraticEq;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private TextView inputText;
    private ImageView imageView;
    private TessBaseAPI baseAPI;
    private TextView solText;
    private String input = "";


    AsyncTask<Void, Void, Void> copy = new copyTask();
    AsyncTask<Void, Void, Void> ocr = new ocrTask();


    ProgressDialog ocrProgress;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.example.equationsolver/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_acitivity);

        inputText = findViewById(R.id.textView1);
        imageView = findViewById(R.id.Image1);
        solText = findViewById(R.id.solText);
        ocrProgress = new ProgressDialog(ResultActivity.this);
        ocrProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        ocrProgress.setIndeterminate(true);
        ocrProgress.setCancelable(false);
        ocrProgress.setTitle("OCR");
        ocrProgress.setMessage("Extracting Equations, please wait");

        imageView.setImageBitmap(Binarzition.umbralization);

        copy.execute();
        ocr.execute();

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "data").build();

        DataDao dataDao = db.dataDao();
        List<Data> data = dataDao.getAll();

    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("trainneddata");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            Log.i("files",filename);
            InputStream in = null;
            OutputStream out = null;
            String dirout= DATA_PATH + "tessdata/";
            File outFile = new File(dirout, filename);
            if(!outFile.exists()) {
                try {
                    in = assetManager.open("trainneddata/"+filename);
                    (new File(dirout)).mkdirs();
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (IOException e) {
                    Log.e("tag", "Error creating files", e);
                }
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void recognizeText(Bitmap bmp) {

        baseAPI = new TessBaseAPI();
        baseAPI.setDebug(true);
        baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);

        boolean test = baseAPI.init(DATA_PATH, "eng+equ"); //Equation training file

        baseAPI.setImage(bmp);
        String text = baseAPI.getUTF8Text();
        baseAPI.clear();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputText.setText(text);
            }
        });
    }

    private void solve() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                input = inputText.getText().toString();
                System.out.println("input" + input);
                QuadraticEq solver = new QuadraticEq();
                String res = solver.parseString(input);
                solText.setText(res);

            }
        });


    }

    private class copyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            copyAssets();
            return null;
        }
    }

    private class ocrTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ocrProgress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ocrProgress.cancel();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            recognizeText(Binarzition.umbralization);
            solve();
            return null;
        }
    }


}