package com.example.checkers;

import android.widget.ImageView;


public class Board {

    public static final int SIZE = 8; // Board must be square-shaped (default is 8x8)
    protected Piece[][] boardArray;

    public Board(Piece[][] boardArray) {
        this.boardArray = boardArray;
    }

//    public Board(ImageView[][] imageViewsTiles)
//    {
//        this(imageViewsTiles, null);
//    }


    public Piece[][] getBoardArray() {
        return this.boardArray;
    }

    public void setBoardArray(Piece[][] boardArray) {
        this.boardArray = boardArray;
    }

    /**
     * Check if the given tile is darkwood colored or not (darkwood colored tile means that a checker can be placed on it).
     *
     * @param x         the x axis of the tile on the board.
     * @param y         the y axis of the tile on the baord.
     * @return          True if a checker can be placed on a given tile (represented by x and y axis), false otherwise.
     */
    public boolean isTileForChecker(int x, int y)
    {
        return (x + y) % 2 == 1; // this is true for every tile that a checker can be on (darkwood colored)
    }

}
