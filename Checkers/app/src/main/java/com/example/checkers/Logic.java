package com.example.checkers;

public class Logic {

    public static boolean isPieceNeeds2BeKing(boolean isBlack, int x) {
        if (isBlack)
            return isBlackNeeds2BeKing(x);
        return isRedNeeds2BeKing(x);
    }

    public static boolean isBlackNeeds2BeKing(int x) {
        return x == 0;
    }

    public static boolean isRedNeeds2BeKing(int x) {
        return x == 7;
    }

    public static boolean isOnRightEdge(int y) {
        return y == 7;
    }

    public static boolean isOnLeftEdge(int y) {
        return y == 0;
    }

    public static boolean canRedMoveDown(int x) {
        return x + 1 <= 7; // check if red reached the end (already king)
    }

    public static boolean canBlackMoveUp(int x) {
        return x - 1 >= 0; // check if black reached the end (already king)
    }

    // if not on edge and has space to jump
    public static boolean hasSpaceForLeftJump(int x, int y, boolean isBlack) {
        if (isBlack)
            return x - 2 >= 0 && y - 2 >= 0;
        return x + 2 <= 7 && y - 2 >= 0;
    }

    // if not on edge and has space to jump
    public static boolean hasSpaceForRightJump(int x, int y, boolean isBlack) {
        if(isBlack)
            return x - 2 >= 0 && y + 2 <= 7;
        return x + 2 <= 7 && y + 2 <= 7;
    }

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
