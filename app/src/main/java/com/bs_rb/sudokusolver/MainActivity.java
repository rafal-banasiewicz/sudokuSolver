package com.bs_rb.sudokusolver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;

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

        btnCameraView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CameraActivity.class);
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

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");


    }
    public void displayImage(Mat img){


        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();

        bitmap = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(img, bitmap);

        imageView.setImageBitmap(bitmap);
    }
}
