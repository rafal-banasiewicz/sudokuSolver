package com.example.sudokusolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

public class SudokuBoardActivity extends AppCompatActivity implements SudokuBoardView.OnTouchListener {

    private SudokuBoardViewModel viewModel; // przy wyjsciu z activity bądz zmianie orientacji, stan diagramu zostanie niezmieniony

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_board);

        SudokuBoardView sudokuBoardView = findViewById(R.id.sudokuBoardView);
        sudokuBoardView.registerListener(this);

        viewModel = ViewModelProviders.of(this).get(SudokuBoardViewModel.class);
        viewModel.solver.selectedCellLiveData.observe(this, new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(Pair<Integer, Integer> cell) {
                updateSelectedCellUI(cell);
            }
        });
    }

    private void updateSelectedCellUI(Pair<Integer, Integer> cell) {
        SudokuBoardView sudokuBoardView = findViewById(R.id.sudokuBoardView);
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second);
    }

    @Override
    public void onCellTouched(Integer row, Integer col) {
        viewModel.solver.updateSelectedCell(row, col);
    }
}
