package com.bs_rb.sudokusolver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class SudokuBoardActivity extends AppCompatActivity implements SudokuBoardView.OnTouchListener {

    private SudokuBoardViewModel viewModel; // przy wyjsciu z activity bądz zmianie orientacji, stan diagramu zostanie niezmieniony
    private ArrayList<Button> buttons = new ArrayList<>();

    static Button solveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_board);

        SudokuBoardView sudokuBoardView = findViewById(R.id.sudokuBoardView);
        sudokuBoardView.registerListener(this);

        viewModel = ViewModelProviders.of(this).get(SudokuBoardViewModel.class);
        viewModel.solver.selectedCellLiveData.observe(this, this::updateSelectedCellUI);
        viewModel.solver.cellsLiveData.observe(this, this::updateCells);

        ImageButton deleteImBtn = findViewById(R.id.deleteButton);
        deleteImBtn.setOnClickListener(v -> viewModel.solver.delete());

        solveBtn = findViewById(R.id.solveButton);
        solveBtn.setOnClickListener(v -> viewModel.solver.solve());

        Button clearBoardBtn = findViewById(R.id.clearBoardButton);
        clearBoardBtn.setOnClickListener(v -> viewModel.solver.clearBoard());

        //initialize buttons
        Button oneBtn = findViewById(R.id.oneButton);
        Button twoBtn = findViewById(R.id.twoButton);
        Button threeBtn = findViewById(R.id.threeButton);
        Button fourBtn = findViewById(R.id.fourButton);
        Button fiveBtn = findViewById(R.id.fiveButton);
        Button sixBtn = findViewById(R.id.sixButton);
        Button sevenBtn = findViewById(R.id.sevenButton);
        Button eightBtn = findViewById(R.id.eightButton);
        Button nineBtn = findViewById(R.id.nineButton);

        buttons.add(oneBtn);
        buttons.add(twoBtn);
        buttons.add(threeBtn);
        buttons.add(fourBtn);
        buttons.add(fiveBtn);
        buttons.add(sixBtn);
        buttons.add(sevenBtn);
        buttons.add(eightBtn);
        buttons.add(nineBtn);

        for(int i = 0; i < buttons.size(); i++) {
            int input = i;
            buttons.get(i).setOnClickListener(v -> viewModel.solver.handleInput(input + 1));
        }

        Intent intent = getIntent();

        if(intent.getExtras() != null) {
            ArrayList<Integer> num = intent.getIntegerArrayListExtra("NUMBERS");
            viewModel.solver.loadNumbersOCR(num);
        }
    }

    private void updateCells(ArrayList<Cell> cells) {
        if(cells == null) return;

        SudokuBoardView sudokuBoardView = findViewById(R.id.sudokuBoardView);
        sudokuBoardView.updateCells(cells);

    }

    private void updateSelectedCellUI(Pair<Integer, Integer> cell) {
        if(cell == null) return;
        SudokuBoardView sudokuBoardView = findViewById(R.id.sudokuBoardView);
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second);
    }

    @Override
    public void onCellTouched(Integer row, Integer col) {
        viewModel.solver.updateSelectedCell(row, col);
    }

    static void setSolveBtn(boolean state) {
        solveBtn.setEnabled(state);
    }
}
