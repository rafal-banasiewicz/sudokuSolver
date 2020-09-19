package com.bs_rb.sudokusolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Cell {

    private Integer mRow;
    private Integer mCol;
    private Integer mValue;
    private boolean mFromUser = false;

    Cell(Integer row, Integer col, Integer value) {
        mRow = row;
        mCol = col;
        mValue = value;

        mNumberOfAttempts = 0;
        mCandidates = new ArrayList<>();

    }

    public void setValue(Integer value) {
        mValue = value;
        mFromUser = true;
    }
    public void setValueOCR(Integer value) {
        mValue = value;
        mFromUser = false;
    }

    public boolean isFromUser() { return mFromUser; }

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
    private List<Integer> mCandidates;

    public void setValue() {
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
        return mNumberOfAttempts == mCandidates.size();
    }

    public void resetAttempts() {
        mNumberOfAttempts = 0;
    }

    public void setAttempt() {
        mNumberOfAttempts++;
    }


    public boolean empty() {
        return mValue == 0;
    }
}
