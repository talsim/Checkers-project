package com.example.checkers;

import android.widget.ImageView;

public class Board {

    public static final int SIZE = 8; // Board must be square-shaped (default is 8x8)
    protected ImageView[][] tiles;

    public Board(ImageView[][] tiles) {
        this.tiles = tiles;

        drawPiecesOnTiles();
    }

    // Responsible for drawing the pieces on the board
    public void drawPiecesOnTiles() {
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                if (x <= 2 && isTileForChecker(x, y))
                    tiles[x][y].setImageResource(R.drawable.red_piece);
                if (x >= 5 && isTileForChecker(x, y))
                    tiles[x][y].setImageResource(R.drawable.black_piece);
            }
        }
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
