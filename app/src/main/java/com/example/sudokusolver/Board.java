package com.example.sudokusolver;


import java.util.ArrayList;

public class Board {

    private Integer mSize;
    private ArrayList<Cell> mCells;

    Board(Integer size, ArrayList<Cell> cells) {
        mSize = size;
        mCells = cells;
    }

    Cell getCell(Integer row, Integer col) {
        return mCells.get(row * mSize + col);
    }

    ArrayList<Cell> getCells() {
        return mCells;
    }
}
