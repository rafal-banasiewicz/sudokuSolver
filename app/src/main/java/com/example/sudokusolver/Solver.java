package com.example.sudokusolver;

import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

class Solver {
    public static MutableLiveData<Pair<Integer,Integer>> selectedCellLiveData = new MutableLiveData<>();

    private static Integer selectedRow = -1;
    private static Integer selectedCol = -1;

    static {
        selectedCellLiveData.postValue(new Pair(selectedRow, selectedCol)); //podanie informacji o zaznaczonej komórce siatki
    }

    public void updateSelectedCell(Integer row, Integer col) {
        selectedRow = row;
        selectedCol = col;
        selectedCellLiveData.postValue(new Pair(row,col));
    }


}
