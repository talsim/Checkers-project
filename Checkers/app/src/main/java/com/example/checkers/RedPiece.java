package com.example.checkers;

import static com.example.checkers.OnClickListenerForPieceMoves.lastUsedImageViews;

import android.widget.ImageView;
import android.widget.TextView;

public class RedPiece extends Piece {

    public RedPiece(int x, int y, TextView currentTurn) {
        super(x, y, false, false, currentTurn);
    }

    @Override
    public boolean canMove(Board board) {
        boolean left = isLeftDiagonalAvailable(board);
        boolean leftJump = isLeftJumpDiagonalAvailable(board);
        boolean right = isRightDiagonalAvailable(board);
        boolean rightJump = isRightJumpDiagonalAvailable(board);

        return left || leftJump || right || rightJump;
    }

    @Override
    protected void updateBoardArray(Board board, int endX, int endY) {
        board.getBoardArray()[endX][endY] = new RedPiece(endX, endY, currentTurn);
    }

    // move according to red logic
    public void move(Board board) {
        /* -------------------------- left diagonal -------------------------- */
        if (isLeftDiagonalAvailable(board)) {
            Move leftMove = new Move(x, y, x + 1, y - 1);
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x + 1][y - 1];
            lastUsedImageViews[4] = leftPieceImage;
            leftDiagonal(leftMove, leftPieceImage, false, false, false, 0, board);
        }

        /* -------------------------- left-JUMP diagonal -------------------------- */

        if (isLeftJumpDiagonalAvailable(board)) {
            ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y - 2];
            lastUsedImageViews[5] = leftJumpPieceImage;
            Move leftJumpMove = new Move(x, y, x + 2, y - 2);
            leftDiagonal(leftJumpMove, leftJumpPieceImage, false, false, true, x + 1, board);
        }

        /* -------------------------- right diagonal -------------------------- */
        if (isRightDiagonalAvailable(board)) {
            Move rightMove = new Move(x, y, x + 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x + 1][y + 1];
            lastUsedImageViews[6] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, false, false, false, 0, board);
        }

        /* -------------------------- right-JUMP diagonal -------------------------- */
        if (isRightJumpDiagonalAvailable(board)) {
            ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y + 2];
            lastUsedImageViews[7] = leftJumpPieceImage;
            Move leftJumpMove = new Move(x, y, x + 2, y + 2);
            rightDiagonal(leftJumpMove, leftJumpPieceImage, false, false, true, x + 1, board);
        }
    }

    private boolean isLeftDiagonalAvailable(Board board) {
        return (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */);
    }

    private boolean isLeftJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && board.getBoardArray()[x + 1][y - 1].isBlack());
    }

    private boolean isRightDiagonalAvailable(Board board) {
        return (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */);
    }

    private boolean isRightJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && board.getBoardArray()[x + 1][y + 1].isBlack());
    }

}
