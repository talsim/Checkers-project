package com.example.checkers;

import static com.example.checkers.OnClickListenerForPieceMoves.lastUsedImageViews;

import android.widget.ImageView;

public class RedPiece extends Piece {

    public RedPiece(int x, int y, boolean isBlack, boolean isKing) {
        super(x, y, isBlack, isKing);
    }

    // move according to red logic
    public void move(Board board) {
        /* -------------------------- left diagonal -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */) {
            Move leftMove = new Move(x, y, x + 1, y - 1);
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x + 1][y - 1];
            lastUsedImageViews[4] = leftPieceImage;
            leftDiagonal(leftMove, leftPieceImage, false, false, false, 0, board);
        }

        /* -------------------------- left-JUMP diagonal -------------------------- */

        if (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && board.getBoardArray()[x + 1][y - 1].isBlack()) {
            ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y - 2];
            lastUsedImageViews[5] = leftJumpPieceImage;
            Move leftJumpMove = new Move(x, y, x + 2, y - 2);
            leftDiagonal(leftJumpMove, leftJumpPieceImage, false, false, true, x + 1, board);
        }

        /* -------------------------- right diagonal -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x + 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x + 1][y + 1];
            lastUsedImageViews[6] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, false, false, false, 0, board);
        }

        /* -------------------------- right-JUMP diagonal -------------------------- */
        if (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && board.getBoardArray()[x + 1][y + 1].isBlack()) {
            ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y + 2];
            lastUsedImageViews[7] = leftJumpPieceImage;
            Move leftJumpMove = new Move(x, y, x + 2, y + 2);
            rightDiagonal(leftJumpMove, leftJumpPieceImage, false, false, true, x + 1, board);
        }
    }

}
