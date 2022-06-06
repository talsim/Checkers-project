package com.example.checkers;

/**
 * This class defines the board of the game.
 *
 * @author Tal Simhayev
 * @version 1.0
 */
public class Board {

    public static final int SIZE = 8; // Board must be square-shaped (default is 8x8)
    /**
     * The actual board that is a 2D array of Piece object.
     */
    private Piece[][] boardArray; // 2D board

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
