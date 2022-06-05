package com.example.checkers;

import static com.example.checkers.OnClickListenerForPieceMoves.lastUsedImageViews;

import android.widget.ImageView;
import android.widget.TextView;

// class that defines a black piece
public class BlackPiece extends Piece {

    public BlackPiece(int x, int y, TextView currentTurn) {
        super(x, y, true, false, currentTurn);
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
        board.getBoardArray()[endX][endY] = new BlackPiece(endX, endY, currentTurn);
    }

    public void move(Board board) {
        /* -------------------------- left diagonal -------------------------- */
        if (isLeftDiagonalAvailable(board)) {
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x - 1][y - 1];
            lastUsedImageViews[0] = leftPieceImage;
            Move leftMove = new Move(x, y, x - 1, y - 1);
            leftDiagonal(leftMove, leftPieceImage, true, false, false, 0, board);
        }

        /* -------------------------- left-JUMP diagonal -------------------------- */
        if (isLeftJumpDiagonalAvailable(board)) {
            ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y - 2];
            lastUsedImageViews[1] = leftJumpPieceImage;
            Move leftJumpMove = new Move(x, y, x - 2, y - 2);
            leftDiagonal(leftJumpMove, leftJumpPieceImage, true, false, true, x - 1, board);
        }

        /* -------------------------- right diagonal -------------------------- */
        if (isRightDiagonalAvailable(board)) {
            Move rightMove = new Move(x, y, x - 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x - 1][y + 1];
            lastUsedImageViews[2] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, true, false, false, 0, board);
        }

        /* -------------------------- right-JUMP diagonal -------------------------- */
        if (isRightJumpDiagonalAvailable(board)) {
            ImageView rightJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y + 2];
            lastUsedImageViews[3] = rightJumpPieceImage;
            Move rightJumpMove = new Move(x, y, x - 2, y + 2);
            rightDiagonal(rightJumpMove, rightJumpPieceImage, true, false, true, x - 1, board);
        }
    }

    private boolean isLeftDiagonalAvailable(Board board) {
        return (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */);
    }

    private boolean isLeftJumpDiagonalAvailable(Board board) {
        return ((Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && !board.getBoardArray()[x - 1][y - 1].isBlack()));
    }

    private boolean isRightDiagonalAvailable(Board board) {
        return (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */);
    }

    private boolean isRightJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && !board.getBoardArray()[x - 1][y + 1].isBlack());
    }


}
