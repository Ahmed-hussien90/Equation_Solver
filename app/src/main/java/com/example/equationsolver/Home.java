package com.example.equationsolver;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Home extends AppCompatActivity {

    private ImageView galaryImage;
    private ImageView camImage;
    private CropImageView cropImageView;
    private Button resButton;
    private LinearLayout rotateLayout;
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

        cropImageView = findViewById(R.id.cropImageView);
        rotateLayout = findViewById(R.id.rotateLayout);
        drawerLayout = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);


        navigationView.getMenu().findItem(R.id.nav_history).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getBaseContext(), History.class));
                return false;
            }
        });

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        baseAPI = new TessBaseAPI();
        baseAPI.setDebug(true);
        baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);

        File extDir ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        }
        else
        {
            extDir = Environment.getExternalStorageDirectory();
        }

        File appDir = new File(extDir, "TessOCR");
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
                inspectBMP(cropImageView.getCroppedImage());
                Intent resIntent = new Intent(getApplicationContext(), ResultActivity.class);
                if (resText != null)
                    resIntent.putExtra("res", resText);
                startActivity(resIntent);

            }
        });

        findViewById(R.id.rotate_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.rotateImage(-90);

            }
        });

        findViewById(R.id.rotate_right).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.rotateImage(90);

            }
        });
    }

    private void inspectBMP(Bitmap bmp) {
        baseAPI.setImage(bmp);
        String text = baseAPI.getUTF8Text();
        baseAPI.clear();

        this.resText = text;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_GALLERY:

                if (resultCode == RESULT_OK) {

                    InputStream is = null;
                    try {
                        is = getContentResolver().openInputStream(data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inSampleSize = 4;
                    options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
                    Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
                    cropImageView.setImageBitmap(bmp);
                    rotateLayout.setVisibility(View.VISIBLE);

                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (imageUri != null) {
                        InputStream is = null;
                        try {
                            is = getContentResolver().openInputStream(imageUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        options.inSampleSize = 4;
                        options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
                        Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
                        cropImageView.setImageBitmap(bmp);
                        rotateLayout.setVisibility(View.VISIBLE);

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