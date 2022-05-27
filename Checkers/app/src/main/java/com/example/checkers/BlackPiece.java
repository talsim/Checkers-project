package com.example.checkers;

import static com.example.checkers.OnClickListenerForPieceMoves.lastUsedImageViews;

import android.widget.ImageView;

public class BlackPiece extends Piece{

    public BlackPiece(int x, int y, boolean isBlack, boolean isKing) {
        super(x, y, isBlack, isKing);
    }

    public void move(Board board)
    {
        /* -------------------------- left diagonal -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */) {
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x - 1][y - 1];
            lastUsedImageViews[0] = leftPieceImage;
            Move leftMove = new Move(x, y, x - 1, y - 1);
            leftDiagonal(leftMove, leftPieceImage, true, false, false, 0, board);
        }

        /* -------------------------- left-JUMP diagonal -------------------------- */
        if (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && !board.getBoardArray()[x - 1][y - 1].isBlack()) {
            ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y - 2];
            lastUsedImageViews[1] = leftJumpPieceImage;
            Move leftJumpMove = new Move(x, y, x - 2, y - 2);
            leftDiagonal(leftJumpMove, leftJumpPieceImage, true, false, true, x - 1, board);
        }

        /* -------------------------- right diagonal -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x - 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x - 1][y + 1];
            lastUsedImageViews[2] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, true, false, false, 0, board);
        }

        /* -------------------------- right-JUMP diagonal -------------------------- */
        if (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && !board.getBoardArray()[x - 1][y + 1].isBlack()) {
            ImageView rightJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y + 2];
            lastUsedImageViews[3] = rightJumpPieceImage;
            Move rightJumpMove = new Move(x, y, x - 2, y + 2);
            rightDiagonal(rightJumpMove, rightJumpPieceImage, true, false, true, x - 1, board);
        }
    }

}
