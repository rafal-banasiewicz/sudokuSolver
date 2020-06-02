package com.example.sudokusolver;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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


    //solving utilities

    Stack<Integer> mPreviousRows = new Stack<>();
    Stack<Integer> mPreviousCols = new Stack<>();

    public void scanRow(List<Integer> impossibleNumbers, Integer currentRow) {
        Integer numIdx = 0;

        for(Integer col = 0; col < mSize; col++) {
            numIdx = mCells.get(currentRow * mSize + col).getValue();

            if(numIdx >= 1) {
                impossibleNumbers.set(numIdx - 1, numIdx);
            }
        }
    }

    public void scanCol(List<Integer> impossibleNumbers, Integer currentCol) {
        Integer numIdx = 0;

        for(Integer row = 0; row < mSize; row++) {
            numIdx = mCells.get(row * mSize + currentCol).getValue();

            if(numIdx >= 1) {
                impossibleNumbers.set(numIdx - 1, numIdx);
            }
        }
    }

    public void scanSubGrid(List<Integer> impossibleNumbers, Integer currentRow, Integer currentCol) {
        Integer startRow = (currentRow / 3) * 3;
        Integer startCol = (currentCol / 3) * 3;

        Integer numIdx = 0;

        for (Integer row = startRow; row < startRow + 3; row++) {
            for (Integer col = startCol; col < startCol + 3; col++) {
                numIdx = mCells.get(row * mSize + col).getValue();
                if (numIdx >= 1) impossibleNumbers.set(numIdx - 1, numIdx);
            }
        }
    }

    public void extractCandidates(List<Integer> nonCandidates, Integer currentRow, Integer currentCol) {
        Integer candidate = 1;
        List<Integer> candidates = new ArrayList<>();

        for (Integer nC : nonCandidates) {
            if(nC == 0) candidates.add(candidate);
            candidate++;
        }

        mCells.get(currentRow * mSize + currentCol).setCandidates(candidates);
    }

    public void findNonCandidates() {
        List<Integer> nonCandidates = new ArrayList<>(mSize);

        for (Integer row = 0; row < mSize; row++) {
            for (Integer col = 0; col < mSize; col++) {
                if (!mCells.get(row * mSize + col).empty()) continue;

                nonCandidates.clear();

                for(Integer i = 0; i < mSize; i++) nonCandidates.add(0);

                mCells.get(row * mSize + col).getCandidates().clear();
                mCells.get(row * mSize + col).resetAttempts();

                this.scanRow(nonCandidates, row);
                this.scanCol(nonCandidates, col);
                this.scanSubGrid(nonCandidates, row, col);
                this.extractCandidates(nonCandidates, row, col);
            }
        }

    }

    public void writeSafeNumbers() {
        boolean writedOnce = false;
        boolean writed = true;

        while (writed) {
            this.findNonCandidates();
            writedOnce = false;

            for (Integer row = 0; row < mSize; row++) {
                for (Integer col = 0; col < mSize; col++) {
                    if(mCells.get(row * mSize + col).empty() && mCells.get(row * mSize + col).getCandidates().size() == 1) {
                        mCells.get(row * mSize + col).write();
                        writedOnce = true;
                    }
                }
            }
            writed = writedOnce;
        }
    }

    public boolean collision(Integer currentRow, Integer currentCol, Integer candidate) {
        for (Integer pos = 0; pos < mSize; pos++) {
            if (candidate.equals(mCells.get(currentRow * mSize + pos).getValue())
                    || candidate == mCells.get(pos * mSize + currentCol).getValue()) return true;
        }
        Integer row = (currentRow / 3) * 3;
        Integer col = (currentCol / 3) * 3;

        for (Integer r = row; r < row + 3; r++) {
            for (Integer c = col; c < col + 3; c++) {
                if (candidate.equals(mCells.get(r * mSize + c).getValue())) return true;
            }
        }
        return false;
    }

    public Pair<Integer, Integer> comeback(Integer row, Integer col) {
        Pair<Integer, Integer> pair = new Pair<>(row, col);

        do {
            mCells.get(pair.first * mSize + pair.second).resetAttempts();
            mPreviousRows.pop();
            mPreviousCols.pop();

            pair = new Pair<>(mPreviousRows.peek(), mPreviousCols.peek());

            mCells.get(pair.first * mSize + pair.second).setValue(0);
            mCells.get(pair.first * mSize + pair.second).nextCandidate();

        } while ( mCells.get(pair.first * mSize + pair.second).candidatesRunOut());

        return pair;
    }

    public void solve() {
        this.writeSafeNumbers();

        Integer candidate = 0;
        boolean comedBack = false;

        for (Integer row = 0; row < mSize;) {
            for ( Integer col = 0; col < mSize;) {

                if(mCells.get(row * mSize + col).empty()) {
                    mPreviousRows.push(row);
                    mPreviousCols.push(col);

                    if (comedBack) {
                        mPreviousRows.pop();
                        mPreviousCols.pop();
                    }

                    candidate = mCells.get(row * mSize + col).candidate();

                    while ( collision(row, col, candidate) && !mCells.get(row * mSize + col).candidatesRunOut()) {
                        mCells.get(row * mSize + col).nextCandidate();
                        candidate = mCells.get(row * mSize + col).candidate();
                        mCells.get(row * mSize + col).setAttempt();
                    }

                    if (mCells.get(row * mSize + col).candidatesRunOut()) {
                        Pair<Integer, Integer> pair = new Pair<>(comeback(row, col).first, comeback(row,col).second);
                        row = pair.first;
                        col = pair.second;
                        comedBack = true;
                        continue;
                    }

                    comedBack = false;

                    mCells.get(row * mSize + col).setValue(candidate);
                    mCells.get(row * mSize + col).setAttempt();
                }
                col++;
            }
            row++;
        }
    }
}
