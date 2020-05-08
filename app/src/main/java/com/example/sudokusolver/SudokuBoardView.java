package com.example.sudokusolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;



public class SudokuBoardView extends View{

    private int sqrtSize = 3;
    private int size = 9;

    private float cellSizePixels = 0;


    Paint thickLinePaint = new Paint();
    Paint thinLinePaint = new Paint();

    public SudokuBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void setThickLinePaint(){
        thickLinePaint.setColor(Color.BLACK);
        thickLinePaint.setStyle(Paint.Style.STROKE);
        thickLinePaint.setStrokeWidth(4F);
    }

    private void setThinLinePaint(){
        thinLinePaint.setColor(Color.BLACK);
        thinLinePaint.setStyle(Paint.Style.STROKE);
        thinLinePaint.setStrokeWidth(2F);
    }

    public void onDraw(Canvas canvas) {
        cellSizePixels = (float)this.getWidth() / size;
        drawLines(canvas);
    }

    private void drawLines (Canvas canvas) {
        setThickLinePaint();
        setThinLinePaint();
        canvas.drawRect(0F,0F,(float)this.getWidth(),(float)this.getHeight(),thickLinePaint);
        Paint paintToUse;
        for(int i = 1; i<=size ;i++) {
            if( i % sqrtSize == 0) paintToUse = thickLinePaint;
            else paintToUse = thinLinePaint;

            canvas.drawLine(
                    i * cellSizePixels,
                    0F,
                    i * cellSizePixels,
                    (float)this.getHeight(),
                    paintToUse
            );

            canvas.drawLine(
                    0F,
                    i * cellSizePixels,
                    (float)this.getWidth(),
                    i * cellSizePixels,
                    paintToUse
            );


        }

    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sizePixels, sizePixels);
    }


}
