package com.example.checkers;

import static com.example.checkers.OnClickListenerForPieceMoves.lastUsedImageViews;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class defines a king piece.
 *
 * @author Tal Simhayev
 * @version 1.0
 */
public class KingPiece extends Piece {

    public KingPiece(int x, int y, boolean isBlack, TextView currentTurn) {
        super(x, y, isBlack, true, currentTurn);
    }

    /**
     * Check if the piece can move or not.
     *
     * @param board The Board object that holds the current state of the game.
     * @return true if black piece can move, false otherwise.
     */
    @Override
    public boolean canMove(Board board) {

        boolean blackLeftDiagonal = isBlackLeftDiagonalAvailable(board);
        boolean blackRightDiagonal = isBlackRightDiagonalAvailable(board);
        boolean redLeftDiagonal = isRedLeftDiagonalAvailable(board);
        boolean redRightDiagonal = isRedRightDiagonalAvailable(board);

        boolean blackLeftJumpDiagonal = isBlackLeftJumpDiagonalAvailable(board);
        boolean blackRightJumpDiagonal = isBlackRightJumpDiagonalAvailable(board);
        boolean redLeftJumpDiagonal = isRedLeftJumpDiagonalAvailable(board);
        boolean redRightJumpDiagonal = isRedRightJumpDiagonalAvailable(board);

        return (blackLeftDiagonal || blackRightDiagonal || redLeftDiagonal || redRightDiagonal ||
                blackLeftJumpDiagonal || blackRightJumpDiagonal || redLeftJumpDiagonal || redRightJumpDiagonal);
    }


    /**
     * Update the new piece in the board to be an object of KingPiece.
     *
     * @param board The Board object that holds the current state of the game.
     * @param endX  The end X cord of the move.
     * @param endY  The end Y cord of the move.
     */
    @Override
    protected void updateBoardArray(Board board, int endX, int endY) {
        board.getBoardArray()[endX][endY] = new KingPiece(endX, endY, isBlack, currentTurn);
    }

    /**
     * Move according to king piece logic.
     *
     * @param board The Board object that holds the current state of the game.
     */
    public void move(Board board) {
        /* -------------------------- left diagonal BLACK -------------------------- */
        if (isBlackLeftDiagonalAvailable(board)) {
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x - 1][y - 1];
            lastUsedImageViews[0] = leftPieceImage;
            Move leftMove = new Move(x, y, x - 1, y - 1);
            leftDiagonal(leftMove, leftPieceImage, isBlack, false, 0, board);
        }

        /* -------------------------- right diagonal BLACK -------------------------- */
        if (isBlackRightDiagonalAvailable(board)) {
            Move rightMove = new Move(x, y, x - 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x - 1][y + 1];
            lastUsedImageViews[1] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, isBlack, false, 0, board);
        }


        /* -------------------------- left diagonal RED -------------------------- */
        if (isRedLeftDiagonalAvailable(board)) {
            Move leftMove = new Move(x, y, x + 1, y - 1);
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x + 1][y - 1];
            lastUsedImageViews[2] = leftPieceImage;
            leftDiagonal(leftMove, leftPieceImage, isBlack, false, 0, board);
        }

        /* -------------------------- right diagonal RED -------------------------- */
        if (isRedRightDiagonalAvailable(board)) {
            Move rightMove = new Move(x, y, x + 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x + 1][y + 1];
            lastUsedImageViews[3] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, isBlack, false, 0, board);
        }


        // JUMPS - CHECKS


        /* -------------------------- left-JUMP diagonal BLACK -------------------------- */
        if (isBlackLeftJumpDiagonalAvailable(board)) {
            ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y - 2];
            lastUsedImageViews[4] = leftJumpPieceImage;
            Move leftJumpMove = new Move(x, y, x - 2, y - 2);
            leftDiagonal(leftJumpMove, leftJumpPieceImage, isBlack, true, x - 1, board);
        }
        /* -------------------------- right-JUMP diagonal BLACK -------------------------- */
        if (isBlackRightJumpDiagonalAvailable(board)) {
            ImageView rightJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y + 2];
            lastUsedImageViews[5] = rightJumpPieceImage;
            Move rightJumpMove = new Move(x, y, x - 2, y + 2);
            rightDiagonal(rightJumpMove, rightJumpPieceImage, isBlack, true, x - 1, board);
        }
        /* -------------------------- left-JUMP diagonal RED -------------------------- */
        if (isRedLeftJumpDiagonalAvailable(board)) {
            ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y - 2];
            lastUsedImageViews[6] = leftJumpPieceImage;
            Move leftJumpMove = new Move(x, y, x + 2, y - 2);
            leftDiagonal(leftJumpMove, leftJumpPieceImage, isBlack, true, x + 1, board);
        }
        /* -------------------------- right-JUMP diagonal RED -------------------------- */
        if (isRedRightJumpDiagonalAvailable(board)) {
            ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y + 2];
            lastUsedImageViews[7] = leftJumpPieceImage;
            Move leftJumpMove = new Move(x, y, x + 2, y + 2);
            rightDiagonal(leftJumpMove, leftJumpPieceImage, isBlack, true, x + 1, board);
        }
    }

    /**
     * Check if the black-left diagonal is available.
     *
     * @param board The Board object that holds the current state of the game.
     * @return true if the diagonal is available, false otherwise.
     */
    private boolean isBlackLeftDiagonalAvailable(Board board) {
        return (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */);
    }

    /**
     * Check if the black-right diagonal is available.
     *
     * @param board The Board object that holds the current state of the game.
     * @return true if the diagonal is available, false otherwise.
     */
    private boolean isBlackRightDiagonalAvailable(Board board) {
        return (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */);
    }

    /**
     * Check if the red-left diagonal is available.
     *
     * @param board The Board object that holds the current state of the game.
     * @return true if the diagonal is available, false otherwise.
     */
    private boolean isRedLeftDiagonalAvailable(Board board) {
        return (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */);
    }

    /**
     * Check if the red-right diagonal is available.
     *
     * @param board The Board object that holds the current state of the game.
     * @return true if the diagonal is available, false otherwise.
     */
    private boolean isRedRightDiagonalAvailable(Board board) {
        return (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */);
    }


    /**
     * Check if the black-left-jump diagonal is available.
     *
     * @param board The Board object that holds the current state of the game.
     * @return true if the diagonal is available, false otherwise.
     */
    private boolean isBlackLeftJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && canPieceBeEaten(x - 1, y - 1, board) /* if the piece to be eaten is black color (because we are red in this condition) */);
    }

    /**
     * Check if the black-right-jump diagonal is available.
     *
     * @param board The Board object that holds the current state of the game.
     * @return true if the diagonal is available, false otherwise.
     */
    private boolean isBlackRightJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && canPieceBeEaten(x - 1, y + 1, board) /* if the piece to be eaten is black color (because we are red in this condition) */);
    }

    /**
     * Check if the red-left-jump diagonal is available.
     *
     * @param board The Board object that holds the current state of the game.
     * @return true if the diagonal is available, false otherwise.
     */
    private boolean isRedLeftJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && canPieceBeEaten(x + 1, y - 1, board) /* if the piece to be eaten is black color (because we are red in this condition) */);
    }

    /**
     * Check if the red-right-jump diagonal is available.
     *
     * @param board The Board object that holds the current state of the game.
     * @return true if the diagonal is available, false otherwise.
     */
    private boolean isRedRightJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && canPieceBeEaten(x + 1, y + 1, board) /* if the piece to be eaten is black color (because we are red in this condition) */);
    }

    /**
     * Check if the piece at the location (x,y) in the board can be eatan.
     * Only in the eating-checks we do, we need to check for black that the eaten piece is really a RED COLOR piece (so we can't eat our own color), and the same for a red piece.
     *
     * @param x     The x cord of the eatan piece
     * @param y     The y cord of the eatan piece
     * @param board The Board object that holds the current state of the game.
     * @return true if the piece can be eaten, false otherwise.
     */
    private boolean canPieceBeEaten(int x, int y, Board board) {
        if (isBlack)
            return !board.getBoardArray()[x][y].isBlack(); // check if there is red piece in front of me
        return board.getBoardArray()[x][y].isBlack(); // else, check if there is black piece in front of me
    }
}
