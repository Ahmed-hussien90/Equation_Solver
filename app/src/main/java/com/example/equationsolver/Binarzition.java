package com.example.equationsolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.GrayQuant;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class Binarzition extends AppCompatActivity implements View.OnClickListener, AppCompatSeekBar.OnSeekBarChangeListener {
    private ImageView img;
    private Toolbar toolbar;
    private SeekBar seekBar;
    private Pix pix;
    private FloatingActionButton fab;
    public static Bitmap umbralization;
    private TessBaseAPI baseAPI;
    private String resText = null;
    private Pix pix8;
    private File appDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binarzition);


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

        baseAPI = new TessBaseAPI();
        baseAPI.setDebug(true);
        baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);

        boolean test = baseAPI.init(appDir.getPath(), "eng+equ"); //Equation training file
        if (test) {
            Toast.makeText(getBaseContext(), "TESS Initialized", Toast.LENGTH_SHORT).show();
        }

        img = (ImageView) findViewById(R.id.croppedImage);
        fab = (FloatingActionButton) findViewById(R.id.nextStep);
        fab.setOnClickListener(this);
        pix = com.googlecode.leptonica.android.ReadFile.readBitmap(RotateAndCrop.croppedImage);
        pix8 = Convert.convertTo8(pix);

        OtsuThresholder otsuThresholder = new OtsuThresholder();
        int threshold = otsuThresholder.doThreshold(pix.getData());
        /* increase threshold because is better*/
        threshold += 20;
        umbralization = com.googlecode.leptonica.android.WriteFile.writeBitmap(GrayQuant.pixThresholdToBinary(pix8,threshold));
        img.setImageBitmap(umbralization);
        seekBar = findViewById(R.id.umbralization);
        seekBar.setProgress(Integer.valueOf((50 * threshold)/254));
        seekBar.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        umbralization = com.googlecode.leptonica.android.WriteFile.writeBitmap(
                GrayQuant.pixThresholdToBinary(pix8,Integer.valueOf(((254 * seekBar.getProgress())/50)))
        );
        img.setImageBitmap(umbralization);

    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.nextStep) {
            inspectBMP(umbralization);
            Intent resIntent = new Intent(Binarzition.this, ResultActivity.class);
            resIntent.putExtra("res", resText);
            startActivity(resIntent);
        }

    }

    private void inspectBMP(Bitmap bmp) {
        baseAPI.setImage(bmp);
        String text = baseAPI.getUTF8Text();
        baseAPI.clear();

        this.resText = text;
    }
}
