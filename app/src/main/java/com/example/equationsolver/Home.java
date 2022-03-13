package com.example.equationsolver;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Home extends AppCompatActivity {

    private ImageView galaryImage;
    private ImageView camImage;
    private Button resButton;
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;

    private Uri imageUri;
    private TessBaseAPI baseAPI;

    private String resText = null;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);


        navigationView.getMenu().findItem(R.id.nav_history).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getBaseContext(),History.class));
                return false;
            }
        });

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        baseAPI = new TessBaseAPI();

        File extDir = Environment.getExternalStorageDirectory();
        File appDir = new File(extDir, "TessOCR");
        if (!appDir.isDirectory())
            appDir.mkdir();
        final File baseDir = new File(appDir, "tessdata");
        if (!baseDir.isDirectory())
            baseDir.mkdir();


        baseAPI.setDebug(true);
        baseAPI.setPageSegMode(TessBaseAPI.OEM_CUBE_ONLY);

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
        boolean test = baseAPI.init(appDir.getPath(), "eng+equ"); //Equation training file
        if (test) {
            Toast.makeText(getBaseContext(), "TESS Initialized", Toast.LENGTH_SHORT).show();
        }
        galaryImage = findViewById(R.id.imageView2);
        galaryImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, REQUEST_GALLERY);
            }
        });

        camImage = findViewById(R.id.imageView1);
        camImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String filename = "Tess - " + System.currentTimeMillis() + ".jpg";

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent i = new Intent();
                i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(i, REQUEST_CAMERA);
            }
        });
        resButton = findViewById(R.id.button1);

        resButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent resIntent = new Intent(getApplicationContext(), ResultActivity.class);
                if (resText != null)
                    resIntent.putExtra("res", resText);
                startActivity(resIntent);

            }
        });
    }

    private void inspectBMP(Bitmap bmp) {
        baseAPI.setImage(bmp);
        String text = baseAPI.getUTF8Text();
        baseAPI.clear();

        this.resText = text;
    }

    private void inspect(Uri uri) {

        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 4;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            Bitmap bmp = BitmapFactory.decodeStream(is, null, options);

            ImageView image = findViewById(R.id.editedImage);
            image.setImageBitmap(bmp);
            BitmapDrawable abmp = (BitmapDrawable) image.getDrawable();
            bmp = abmp.getBitmap();
            Bitmap temp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

            int srcData[] = new int[bmp.getWidth() * bmp.getHeight()];
            int ptr = 0;
            for (int i = 0; i < bmp.getWidth(); i++) {
                for (int j = 0; j < bmp.getHeight(); j++) {
                    int p = bmp.getPixel(i, j);
                    int r = (int) (Color.red(p) * 0.33);
                    int g = (int) (Color.green(p) * 0.59);
                    int b = (int) (Color.blue(p) * 0.11);

                    int av = (r + g + b) / 3;
                    int n = r + g + b;
                    r = g = b = n;

                    srcData[ptr] = 0xFF & p;
                    ptr++;

                    temp.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
                }
            }


            int thresh = otsuThresholding(srcData);
            double gamma_val = 1.25;
            int[] gamma_lut = gamma_LUT(gamma_val);

            Bitmap temp2 = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
            for (int i = 0; i < temp.getWidth(); i++) {
                for (int j = 0; j < temp.getHeight(); j++) {
                    int p = temp.getPixel(i, j);

                    int r = (gamma_lut[Color.red(p)]);
                    int g = (gamma_lut[Color.green(p)]);
                    int b = (gamma_lut[Color.blue(p)]);

                    int n = (int) (r * 0.33 + g * 0.59 + b * 0.11);


                    if (n < thresh)
                        temp2.setPixel(i, j, Color.argb(Color.alpha(p), 255, 255, 255));
                    else
                        temp2.setPixel(i, j, Color.argb(Color.alpha(p), 0, 0, 0));
                }
            }

            image.setImageBitmap(temp2);
            inspectBMP(temp);
            Toast.makeText(getApplicationContext(), "Threshlold=" + thresh, Toast.LENGTH_LONG).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private int otsuThresholding(int[] src) {
        int ptr = 0;
        int hist[] = new int[256];
        while (ptr < src.length) {
            int h = src[ptr];
            hist[h]++;
            ptr++;
        }
        int total = src.length;
        float sum = 0;
        for (int t = 0; t < 256; t++) sum += t * hist[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += hist[t];               // Weight Background
            if (wB == 0) continue;

            wF = total - wB;                 // Weight Foreground
            if (wF == 0) break;

            sumB += (float) (t * hist[t]);

            float mB = sumB / wB;            // Mean Background
            float mF = (sum - sumB) / wF;    // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }

        return threshold;

    }

    private static int[] gamma_LUT(double gamma_new) {
        int[] gamma_LUT = new int[256];

        for (int i = 0; i < gamma_LUT.length; i++) {
            gamma_LUT[i] = (int) (255 * (Math.pow((double) i / (double) 255, gamma_new)));
        }

        return gamma_LUT;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
            case REQUEST_GALLERY:

                if (resultCode == RESULT_OK) {

                    inspect(data.getData());
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (imageUri != null) {
                        inspect(imageUri);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
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