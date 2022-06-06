package com.example.checkers;

/**
 * This class contains static helper functions for the Logic implementation in the game.
 *
 * @author Tal Simhayev
 * @version 1.0
 */
public class Logic {

    /**
     * Wrapper function for isBlackNeeds2BeKing and isRedNeeds2BeKing.
     *
     * @param isBlack The piece color.
     * @param x       The given x cord.
     * @return true or false according if the piece needs to be king or not.
     */
    public static boolean isPieceNeeds2BeKing(boolean isBlack, int x) {
        if (isBlack)
            return isBlackNeeds2BeKing(x);
        return isRedNeeds2BeKing(x);
    }

    /**
     * Check if red is at the bottom of the board.
     *
     * @param x The given x cord.
     * @return true if the x cord is in the end for black, thus he needs to become king, false otherwise.
     */
    private static boolean isBlackNeeds2BeKing(int x) {
        return x == 0;
    }

    /**
     * Check if red is at the top of the board.
     *
     * @param x The given x cord.
     * @return true if the x cord is in the end for red, thus he needs to become king, false otherwise.
     */
    private static boolean isRedNeeds2BeKing(int x) {
        return x == 7;
    }

    /**
     * Check if the given y cord is on right edge.
     *
     * @param y The given y cord.
     * @return true if the y cord is on the right edge of the board, false otherwise.
     */
    public static boolean isOnRightEdge(int y) {
        return y == 7;
    }

    /**
     * Check if the given y cord is on left edge.
     *
     * @param y The given y cord.
     * @return true if the y cord is on the left edge of the board, false otherwise.
     */
    public static boolean isOnLeftEdge(int y) {
        return y == 0;
    }

    /**
     * Check if red has space to move.
     *
     * @param x The given x cord.
     * @return true if the x cord is not in the end of the board for red, false otherwise.
     */
    public static boolean canRedMoveDown(int x) {
        return x + 1 <= 7; // if red reached the end (already king)
    }

    /**
     * Check if black has space to move.
     *
     * @param x The given x cord.
     * @return true if the x cord is not in the end of the board for black, false otherwise.
     */
    public static boolean canBlackMoveUp(int x) {
        return x - 1 >= 0; // if black reached the end (already king)
    }

    /**
     * Check if not on edge and has space to jump.
     *
     * @param x       The given x cord.
     * @param y       The given y cord.
     * @param isBlack The color of the piece.
     * @return true if piece is not on the edge for left jump, false otherwise.
     */
    public static boolean hasSpaceForLeftJump(int x, int y, boolean isBlack) {
        if (isBlack)
            return x - 2 >= 0 && y - 2 >= 0;
        return x + 2 <= 7 && y - 2 >= 0;
    }

    /**
     * Check if not on edge and has space to jump.
     *
     * @param x       The given x cord.
     * @param y       The given y cord.
     * @param isBlack The color of the piece.
     * @return true if piece is not on the edge for right jump, false otherwise.
     */
    public static boolean hasSpaceForRightJump(int x, int y, boolean isBlack) {
        if (isBlack)
            return x - 2 >= 0 && y + 2 <= 7;
        return x + 2 <= 7 && y + 2 <= 7;
    }

    /**
     * Check if the given x and y axis are taken on the board by another piece.
     *
     * @param board The Board object that holds the current state of the game.
     * @param x     The given x cord.
     * @param y     The given y cord.
     * @return true if the tile is not taken by another piece in the board, false otherwise.
     */
    public static boolean isTileAvailable(Board board, int x, int y) {
        return board.getBoardArray()[x][y] == null;
    }

    /**
     * Check if the given tile is darkwood colored or not (darkwood colored tile means that a checker can be placed on it).
     *
     * @param x the x axis of the tile on the board.
     * @param y the y axis of the tile on the baord.
     * @return True if a checker can be placed on a given tile (represented by x and y axis), false otherwise.
     */
    public static boolean isTileForChecker(int x, int y) {
        return (x + y) % 2 == 1; // this is true for every tile that a checker can be on (darkwood colored)
    }

}
