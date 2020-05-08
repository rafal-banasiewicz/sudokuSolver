package com.example.sudokusolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Half;
import android.view.View;

import androidx.annotation.RequiresApi;


public class SudokuBoardView {

    private Paint thickLinePaint = new Paint() {
        Style style = Style.STROKE;
        int color = Color.BLACK;
        float strokeWidth = 4F;
    };

    void onCreate(Canvas canvas) {
        canvas.drawRect(10,10,100,100, thickLinePaint);
    }
}
