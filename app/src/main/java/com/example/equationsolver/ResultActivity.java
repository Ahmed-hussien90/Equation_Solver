package com.example.equationsolver;

import android.app.ProgressDialog;
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

    private File appDir;

    ProgressDialog ocrProgress;

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

        File extDir;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        } else {
            extDir = Environment.getExternalStorageDirectory();
        }

        appDir = new File(extDir, "TessOCR");
        if (!appDir.isDirectory())
            appDir.mkdir();
        final File baseDir = new File(appDir, "tessdata");
        if (!baseDir.isDirectory())
            baseDir.mkdir();


        try {
            String fileList[] = getAssets().list("");
            for (String fileName : fileList) {
                String pathToDataFile = baseDir + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff)) > 0) {
                        out.write(buff, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e("ss", e.getMessage());
        }
    }

    private void recognizeText(Bitmap bmp) {

        baseAPI = new TessBaseAPI();
        baseAPI.setDebug(true);
        baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);

        boolean test = baseAPI.init(appDir.getPath(), "eng+equ"); //Equation training file

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