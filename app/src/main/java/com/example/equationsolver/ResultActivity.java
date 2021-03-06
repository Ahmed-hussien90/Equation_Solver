package com.example.equationsolver;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.equationsolver.solver.Solver;
import com.google.android.material.navigation.NavigationView;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import es.dmoral.toasty.Toasty;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ResultActivity extends AppCompatActivity {

    private TextView inputText;
    private ImageView imageView;
    private TessBaseAPI baseAPI;
    private TextView solText;
    private String input = "";
    private LinearLayout inputLayout;
    private LinearLayout resLayout;
    private Button saveBtn;
    private Button shareBtn;
    private ImageButton copyBtn;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;



    AsyncTask<Void, Void, Void> copy = new copyTask();
    AsyncTask<Void, Void, Void> ocr = new ocrTask();


    ProgressDialog ocrProgress;

    private static String DATA_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_acitivity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            DATA_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/com.example.equationsolverd/";
        } else {
            DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.example.equationsolverd/";
        }

        inputText = findViewById(R.id.textView1);
        imageView = findViewById(R.id.Image1);
        solText = findViewById(R.id.solText);
        resLayout = findViewById(R.id.resLayout);
        inputLayout = findViewById(R.id.inputLayout);
        saveBtn = findViewById(R.id.saveBtn);
        shareBtn = findViewById(R.id.shareBtn);
        copyBtn = findViewById(R.id.copyBtn);
        resLayout.setVisibility(View.GONE);
        inputLayout.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        ocrProgress = new ProgressDialog(ResultActivity.this);
        ocrProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        ocrProgress.setIndeterminate(true);
        ocrProgress.setCancelable(false);
        ocrProgress.setTitle("OCR");
        ocrProgress.setMessage("Extracting Equations, please wait");

        drawerLayout = findViewById(R.id.drawerlayout2);
        NavigationView navigationView = findViewById(R.id.nav_view2);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.getMenu().findItem(R.id.nav_history).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getBaseContext(), History.class));
                return false;
            }
        });

        navigationView.getMenu().findItem(R.id.nav_contact).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"Ahmed.hs9090@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "math solver app");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ResultActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


        imageView.setImageBitmap(Binarzition.umbralization);

        copy.execute();
        ocr.execute();

        AppDatabase db = AppDatabase.getInstance(this);
        DataDao dataDao = db.dataDao();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dataDao.insertAll(new Data(inputText.getText().toString(), solText.getText().toString(), null))
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                ResultActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toasty.success(ResultActivity.this, " Saved ", Toast.LENGTH_SHORT, true).show();
                                    }
                                });
                            }

                            @Override
                            public void onError(Throwable e) {
                                ResultActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toasty.error(ResultActivity.this, "Failed", Toast.LENGTH_SHORT, true).show();
                                    }
                                });
                            }
                        });

            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(android.content.Intent.ACTION_SEND);

                intent.setType("text/plain");

                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share solution");

                String shareText = "problem : " + inputText.getText().toString() + "\n" + "solution : " + solText.getText().toString();

                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);

                startActivity(Intent.createChooser(intent, "Share"));
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(solText.getText().toString());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("simple text", solText.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }

                Toasty.success(ResultActivity.this, " Copied ", Toast.LENGTH_SHORT, true).show();


            }
        });
    }


    private void copyAssets() {

        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("trainneddata");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for (String filename : files) {
            Log.i("files", filename);
            InputStream in = null;
            OutputStream out = null;
            String dirout = DATA_PATH + "tessdata/";
            File outFile = new File(dirout, filename);
            if (!outFile.exists()) {
                try {
                    in = assetManager.open("trainneddata/" + filename);
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
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void recognizeText(Bitmap bmp) {

        baseAPI = new TessBaseAPI();
        baseAPI.setDebug(true);

        boolean test = baseAPI.init(DATA_PATH, "eng+equ+osd"); //Equation training file

        baseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-=*%(),/");
        baseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$&[]}{;:'\"\\|~`<>?");
        baseAPI.setVariable("equationdetect_save_merged_image", "T");

        baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);

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
                Solver solver = new Solver();
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
            Animation slideInLAnim;
            slideInLAnim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
            inputLayout.setVisibility(View.VISIBLE);
            resLayout.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
            inputLayout.startAnimation(slideInLAnim);
            resLayout.startAnimation(slideInLAnim);
            saveBtn.startAnimation(slideInLAnim);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(Binarzition.umbralization == null){
                System.out.println("RESULT NUUuuuuLLLL");
            }else{
                System.out.println("RESULT not null NUUuuuuLLLL");
            }
            recognizeText(Binarzition.umbralization);
            solve();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


}