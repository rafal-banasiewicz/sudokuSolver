package com.example.sudokusolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class SudokuBoardView extends View{

    private int sqrtSize = 3;
    private int size = 9;

    private float cellSizePixels = 0;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private SudokuBoardView.OnTouchListener listener = null;

    private ArrayList<Cell> cells;

    Paint thickLinePaint = new Paint();
    Paint thinLinePaint = new Paint();
    Paint selectedCellPaint = new Paint();
    Paint conflictingCellPaint = new Paint();
    Paint textPaint = new Paint();


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
//temporary
    private void setTextPaint() {
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(100F);
        //textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


    }

    public void onDraw(Canvas canvas) {
        cellSizePixels = (float)this.getWidth() / size;
        fillCells(canvas);
        drawLines(canvas);
        setTextPaint();
        drawText(canvas);
    }


    private void fillCells(Canvas canvas) {
        for(Cell cell : cells) {
            int r = cell.getRow();
            int c = cell.getCol();

            if (r == selectedRow && c == selectedCol) {
                fillCell(canvas, r, c, selectedCellPaint);
            } else if (r == selectedRow || c == selectedCol) {
                fillCell(canvas, r, c, conflictingCellPaint);
            } else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize){
                fillCell(canvas, r, c, conflictingCellPaint);
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

    private void drawText(Canvas canvas) {
        for (Cell cell : cells) {

            int row = cell.getRow();
            int col = cell.getCol();

            Integer value = cell.getValue();

            if(value > 0) {
                String valueString = value.toString();

                Rect textBounds = new Rect();
                textPaint.getTextBounds(valueString, 0, valueString.length(), textBounds);
                float textWidth = textPaint.measureText(valueString);
                int textHeight = textBounds.height();

                canvas.drawText(valueString, (col * cellSizePixels + textWidth / 2 ),
                        (row * cellSizePixels + textHeight + textHeight / 3), textPaint);
            }
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

    public void updateCells(ArrayList<Cell> cells) {
        this.cells = cells;
        invalidate();
    }

    void registerListener(SudokuBoardView.OnTouchListener listener) {
        this.listener = listener;
    }



    interface OnTouchListener {
        void onCellTouched(Integer row, Integer col);
    }



}
