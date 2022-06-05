package com.example.checkers;

import static com.example.checkers.OnClickListenerForPieceMoves.lastUsedImageViews;

import android.widget.ImageView;
import android.widget.TextView;

public class KingPiece extends Piece {

    public KingPiece(int x, int y, boolean isBlack, TextView currentTurn) {
        super(x, y, isBlack, currentTurn);
    }

    public void move(Board board) {
        /* -------------------------- left diagonal BLACK -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */) {
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x - 1][y - 1];
            lastUsedImageViews[0] = leftPieceImage;
            Move leftMove = new Move(x, y, x - 1, y - 1);
            leftDiagonal(leftMove, leftPieceImage, isBlack, true, false, 0, board);
        }

        /* -------------------------- right diagonal BLACK -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x - 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x - 1][y + 1];
            lastUsedImageViews[1] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, isBlack, true, false, 0, board);
        }



        /* -------------------------- left diagonal RED -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */) {
            Move leftMove = new Move(x, y, x + 1, y - 1);
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x + 1][y - 1];
            lastUsedImageViews[2] = leftPieceImage;
            leftDiagonal(leftMove, leftPieceImage, isBlack, true, false, 0, board);
        }

        /* -------------------------- right diagonal RED -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x + 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x + 1][y + 1];
            lastUsedImageViews[3] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, isBlack, true, false, 0, board);
        }


        if (isBlack) {
            /* -------------------------- left-JUMP diagonal BLACK -------------------------- */
            if (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(true, x - 1, y - 1, board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y - 2];
                lastUsedImageViews[4] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x - 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, true, true, true, x - 1, board);
            }
            /* -------------------------- right-JUMP diagonal BLACK -------------------------- */
            if (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(true, x - 1, y + 1, board)) {
                ImageView rightJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y + 2];
                lastUsedImageViews[5] = rightJumpPieceImage;
                Move rightJumpMove = new Move(x, y, x - 2, y + 2);
                rightDiagonal(rightJumpMove, rightJumpPieceImage, true, true, true, x - 1, board);
            }
            /* -------------------------- left-JUMP diagonal RED -------------------------- */
            if (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(true, x + 1, y - 1, board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y - 2];
                lastUsedImageViews[6] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, true, true, true, x + 1, board);
            }
            /* -------------------------- right-JUMP diagonal RED -------------------------- */
            if (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(true, x + 1, y + 1, board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y + 2];
                lastUsedImageViews[7] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y + 2);
                rightDiagonal(leftJumpMove, leftJumpPieceImage, true, true, true, x + 1, board);
            }
        } else {
            /* -------------------------- left-JUMP diagonal BLACK -------------------------- */
            if (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x - 1, y - 1, board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y - 2];
                lastUsedImageViews[4] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x - 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, false, true, true, x - 1, board);
            }
            /* -------------------------- right-JUMP diagonal BLACK -------------------------- */
            if (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x - 1, y + 1, board)) {
                ImageView rightJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y + 2];
                lastUsedImageViews[5] = rightJumpPieceImage;
                Move rightJumpMove = new Move(x, y, x - 2, y + 2);
                rightDiagonal(rightJumpMove, rightJumpPieceImage, false, true, true, x - 1, board);
            }
            /* -------------------------- left-JUMP diagonal RED -------------------------- */
            if (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x + 1, y - 1, board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y - 2];
                lastUsedImageViews[6] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, false, true, true, x + 1, board);
            }
            /* -------------------------- right-JUMP diagonal RED -------------------------- */
            if (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x + 1, y + 1, board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y + 2];
                lastUsedImageViews[7] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y + 2);
                rightDiagonal(leftJumpMove, leftJumpPieceImage, false, true, true, x + 1, board);
            }
        }
    }

    @Override
    public boolean canMove(Board board) {
        return true;
    }

    // only in the eating-checks we do, we need to check differently for black or red
    private boolean isCheckerBehindNeeds2BeRedOrBlack(boolean isBlack, int x, int y, Board board) {
        if (isBlack)
            return !board.getBoardArray()[x][y].isBlack(); // check if there is red piece behind me
        return board.getBoardArray()[x][y].isBlack(); // else, check if there is black piece behind me
    }
}
