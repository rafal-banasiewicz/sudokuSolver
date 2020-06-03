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

        mPreviousRows = new Stack<>();
        mPreviousCols = new Stack<>();
    }

    Cell getCell(Integer row, Integer col) {
        return mCells.get(row * mSize + col);
    }

    ArrayList<Cell> getCells() {
        return mCells;
    }


    //solving utilities

    Stack<Integer> mPreviousRows;
    Stack<Integer> mPreviousCols;

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
            numIdx = getCell(row, currentCol).getValue();

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
                numIdx = this.getCell(row, col).getValue();
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

        this.getCell(currentRow, currentCol).setCandidates(candidates);
    }

    public void findNonCandidates() {
        List<Integer> nonCandidates = new ArrayList<>(mSize);

        for (Integer row = 0; row < mSize; row++) {
            for (Integer col = 0; col < mSize; col++) {
                if (!this.getCell(row, col).empty()) continue;

                nonCandidates.clear();

                for(Integer i = 0; i < mSize; i++) nonCandidates.add(0);

                this.getCell(row, col).getCandidates().clear();
                this.getCell(row, col).resetAttempts();

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
                    if(this.getCell(row, col).empty() && this.getCell(row, col).getCandidates().size() == 1) {
                        this.getCell(row, col).setValue();
                        writedOnce = true;
                    }
                }
            }
            writed = writedOnce;
        }
    }

    public boolean collision(Integer currentRow, Integer currentCol, Integer candidate) {
        for (Integer pos = 0; pos < mSize; pos++) {
            if (candidate.equals(this.getCell(currentRow, pos).getValue())
                    || candidate == this.getCell(pos, currentCol).getValue()) return true;
        }
        Integer row = (currentRow / 3) * 3;
        Integer col = (currentCol / 3) * 3;

        for (Integer r = row; r < row + 3; r++) {
            for (Integer c = col; c < col + 3; c++) {
                if (candidate.equals(this.getCell(r, c).getValue())) return true;
            }
        }
        return false;
    }

    public Pair<Integer, Integer> comeback(Integer row, Integer col) {


        do {
            this.getCell(row, col).resetAttempts();
            mPreviousRows.pop();
            mPreviousCols.pop();

            row = mPreviousRows.peek();
            col = mPreviousCols.peek();

            this.getCell(row, col).setValue(0);
            this.getCell(row, col).nextCandidate();

        } while ( this.getCell(row, col).candidatesRunOut());

        return new Pair<>(row, col);
    }

    public void solve() {
        this.writeSafeNumbers();

        Integer candidate = 0;
        boolean comedBack = false;

        for (Integer row = 0; row < mSize;) {
            for ( Integer col = 0; col < mSize;) {

                if(this.getCell(row, col).empty()) {
                    mPreviousRows.push(row);
                    mPreviousCols.push(col);

                    if (comedBack) {
                        mPreviousRows.pop();
                        mPreviousCols.pop();
                    }

                    candidate = this.getCell(row, col).candidate();

                    while ( collision(row, col, candidate) && !this.getCell(row, col).candidatesRunOut()) {
                        this.getCell(row, col).nextCandidate();
                        candidate = this.getCell(row, col).candidate();
                        this.getCell(row, col).setAttempt();
                    }

                    if (this.getCell(row, col).candidatesRunOut()) {
                        Pair<Integer, Integer> pair = comeback(row, col);
                        row = pair.first;
                        col = pair.second;
                        comedBack = true;
                        continue;
                    }

                    comedBack = false;

                    this.getCell(row, col).setValue(candidate);
                    this.getCell(row, col).setAttempt();
                }
                col++;
            }
            row++;
        }
    }
}
