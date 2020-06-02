package com.example.sudokusolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

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

    //solving utilities
    private Integer mNumberOfAttempts;
    private List<Integer> mCandidates = new ArrayList<>();

    public void write() {
        this.mValue = mCandidates.get(0);
    }

    public void setCandidates(List<Integer> candidates) {
        this.mCandidates = candidates;

    }

    public Integer candidate() {
        return mCandidates.get(0);
    }

    public void nextCandidate() {
        Collections.rotate(mCandidates, -1);
    }

    public List<Integer> getCandidates() {
        return mCandidates;
    }

    public boolean candidatesRunOut() {
        if(mNumberOfAttempts == mCandidates.size()) return true;
        else return false;
    }

    public void resetAttempts() {
        mNumberOfAttempts = 0;
    }

    public void setAttempt() {
        mNumberOfAttempts++;
    }


    public boolean empty() {
        if(mValue == 0) return true;
        else return false;
    }
}
