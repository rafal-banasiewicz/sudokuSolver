package com.example.sudokusolver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnBoardView;
    Button btnCameraView;
    Button btnLoadImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBoardView = findViewById(R.id.btnBoardView);
        btnCameraView = findViewById(R.id.btnCameraView);
        btnLoadImage = findViewById(R.id.btnLoadImage);

        btnBoardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SudokuBoardActivity.class);
                startActivity(intent);
            }
        });

        btnLoadImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),GalleryActivity.class);
                startActivity(intent);
            }
        });



    }
}
