package com.example.equationsolver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.equationsolver.solver.Solver;

import es.dmoral.toasty.Toasty;

public class Calculate extends AppCompatActivity {
    private EditText editFormula;
    private Button solveBtn;
    private LinearLayout resLayout;
    private TextView resText;
    private ImageButton copyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        editFormula = findViewById(R.id.formulaText);
        solveBtn = findViewById(R.id.solveBTN);
        resLayout = findViewById(R.id.resLayout2);
        resText = findViewById(R.id.solText2);
        copyBtn = findViewById(R.id.copyBtn2);

        resLayout.setVisibility(View.GONE);



        solveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editFormula.onEditorAction(EditorInfo.IME_ACTION_DONE);
                String input = editFormula.getText().toString();
                Solver solver = new Solver();
                String res = solver.parseString(input);
                resText.setText(res);
                Animation slideInLAnim;
                slideInLAnim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
                resLayout.setVisibility(View.VISIBLE);
                resLayout.startAnimation(slideInLAnim);
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(resText.getText().toString());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("simple text", resText.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }

                Toasty.success(Calculate.this, " Copied ", Toast.LENGTH_SHORT, true).show();


            }
        });


    }
}