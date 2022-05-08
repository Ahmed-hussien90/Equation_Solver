package com.example.equationsolver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RotateAndCrop extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton saveBtn;
    public static Bitmap croppedImage;
    CropImageView cropImageView;
    ImageView rotateL;
    ImageView rotateR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_and_crop);

        cropImageView = findViewById(R.id.cropImageView);
        rotateL = findViewById(R.id.rotate_left);
        rotateR = findViewById(R.id.rotate_right);

        Bundle extras = getIntent().getExtras();
        Uri uri = Uri.parse(extras.getString("imgUri"));

        cropImageView.setImageUriAsync(uri);


        saveBtn = (FloatingActionButton) findViewById(R.id.Fbtn);
        saveBtn.setOnClickListener(this);
        rotateL.setOnClickListener(this);
        rotateR.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.Fbtn) {
            cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
                @Override
                public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                    croppedImage = result.getBitmap();
                    Intent intent = new Intent(RotateAndCrop.this, Binarzition.class);
                    startActivity(intent);
                }
            });
            cropImageView.getCroppedImageAsync();

        } else if (view.getId() == R.id.rotate_left) {
            cropImageView.rotateImage(-90);

        } else if (view.getId() == R.id.rotate_right) {
            cropImageView.rotateImage(90);
        }

    }



}
