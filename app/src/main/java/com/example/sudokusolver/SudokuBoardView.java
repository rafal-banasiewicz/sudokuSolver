package com.example.sudokusolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SudokuBoardView extends View{

    private int sqrtSize = 3;
    private int size = 9;

    private float cellSizePixels = 0;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private SudokuBoardView.OnTouchListener listener = null;


    Paint thickLinePaint = new Paint();
    Paint thinLinePaint = new Paint();
    Paint selectedCellPaint = new Paint();
    Paint conflictingCellPaint = new Paint();

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

    private void setSelectedCellPaint(){
        selectedCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectedCellPaint.setColor(Color.parseColor("#6ead3a"));
    }

    private void setConflictingCellPaint(){
        conflictingCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        conflictingCellPaint.setColor(Color.parseColor("#efedef"));
    }

    public void onDraw(Canvas canvas) {
        cellSizePixels = (float)this.getWidth() / size;
        fillCells(canvas);
        drawLines(canvas);
    }

    private void fillCells(Canvas canvas) {
        if (selectedRow == -1 || selectedCol == -1) return;
        for (int r = 0; r <= size; r++){
            for (int c = 0; c <= size; c++){
                if (r == selectedRow && c == selectedCol) {
                    fillCell(canvas, r, c, selectedCellPaint);
                } else if (r == selectedRow || c == selectedCol) {
                    fillCell(canvas, r, c, conflictingCellPaint);
                } else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize){
                    fillCell(canvas, r, c, conflictingCellPaint);
                }
            }
        }
    }

    private void fillCell(Canvas canvas, int r, int c, Paint paint) {
        setSelectedCellPaint();
        setConflictingCellPaint();
        canvas.drawRect(c * cellSizePixels, r * cellSizePixels, (c+1) * cellSizePixels, (r+1) * cellSizePixels, paint);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            handleTouchEvent(event.getX(), event.getY());
            return true;
        }
        else return false;
    }

    private void handleTouchEvent(float x, float y) {
        int possibleSelectedRow = (int)(y / cellSizePixels);
        int possibleSelectedCol = (int)(x / cellSizePixels);
        listener.onCellTouched(possibleSelectedRow, possibleSelectedCol);
    }

    void updateSelectedCellUI(Integer row, Integer col) {
        selectedRow = row;
        selectedCol = col;
        invalidate();
    }

    void registerListener(SudokuBoardView.OnTouchListener listener) {
        this.listener = listener;
    }

    interface OnTouchListener {
        void onCellTouched(Integer row, Integer col);
    }



}
