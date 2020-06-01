package com.example.sudokusolver;
import android.util.Pair;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;

class Solver {
    public static MutableLiveData<Pair<Integer,Integer>> selectedCellLiveData = new MutableLiveData<>();
    public static MutableLiveData<ArrayList<Cell>> cellsLiveData = new MutableLiveData<>();

    private static Integer selectedRow = -1;
    private static Integer selectedCol = -1;

    private static Board board;

    static {
        ArrayList<Cell> cells = new ArrayList<>(9 * 9);

        for(int i = 0; i < 9 * 9; i++) {
            cells.add(new Cell(i / 9, i % 9, i % 9));
        }
        board = new Board(9, cells);

        selectedCellLiveData.postValue(new Pair(selectedRow, selectedCol)); //podanie informacji o zaznaczonej komórce siatki
        cellsLiveData.postValue(board.getCells());
    }

    public void handleInput(Integer number) {
        if(selectedRow == -1 || selectedCol == -1) return;

        board.getCell(selectedRow,selectedCol).setValue(number);
        cellsLiveData.postValue(board.getCells());
    }

    public void updateSelectedCell(Integer row, Integer col) {
        selectedRow = row;
        selectedCol = col;
        selectedCellLiveData.postValue(new Pair(row,col));
    }


}
