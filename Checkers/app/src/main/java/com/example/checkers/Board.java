package com.example.checkers;

import android.util.Log;
import android.widget.ImageView;

import java.util.Objects;


public class Board {

    public static final int SIZE = 8; // Board must be square-shaped (default is 8x8)
    private Piece[][] boardArray;

    public Board() {
        this.boardArray = new Piece[Board.SIZE][Board.SIZE];
    }


    public Piece[][] getBoardArray() {
        return this.boardArray;
    }

    public void setBoardArray(Piece[][] boardArray) {
        this.boardArray = boardArray;
    }




}
