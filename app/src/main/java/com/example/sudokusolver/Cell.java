package com.example.sudokusolver;

public class Cell {


    private Integer mRow;
    private Integer mCol;
    private Integer mValue;

    Cell(Integer row, Integer col, Integer value) {
        mRow = row;
        mCol = col;
        mValue = value;
    }

    public void setValue(Integer value) {
        mValue = value;
    }

    public Integer getRow() {
        return mRow;
    }

    public Integer getCol() {
        return mCol;
    }

    public Integer getValue() {
        return mValue;
    }


}
