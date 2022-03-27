package com.example.equationsolver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private TextView inputText;
    private ImageView imageView;
    String inputText1 = "";
    Bitmap imageBtm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_acitivity);

        inputText = findViewById(R.id.textView1);
        imageView = findViewById(R.id.Image1);

        if (getIntent().getStringExtra("res") != null) {
            inputText1 = getIntent().getStringExtra("res");
            inputText.setText(inputText1);

        }

        imageView.setImageBitmap(Binarzition.umbralization);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "data").build();

        DataDao dataDao = db.dataDao();
        List<Data> data = dataDao.getAll();

    }
}