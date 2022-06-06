package com.example.checkers;

import static com.example.checkers.OnClickListenerForPieceMoves.lastUsedImageViews;

import android.widget.ImageView;
import android.widget.TextView;

public class KingPiece extends Piece {

    public KingPiece(int x, int y, boolean isBlack, TextView currentTurn) {
        super(x, y, isBlack, true, currentTurn);
    }

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


    @Override
    protected void updateBoardArray(Board board, int endX, int endY) {
        board.getBoardArray()[endX][endY] = new KingPiece(endX, endY, isBlack, currentTurn);
    }

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

    private boolean isBlackLeftDiagonalAvailable(Board board) {
        return (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */);
    }

    private boolean isBlackRightDiagonalAvailable(Board board) {
        return (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */);
    }

    private boolean isRedLeftDiagonalAvailable(Board board) {
        return (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */);
    }

    private boolean isRedRightDiagonalAvailable(Board board) {
        return (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */);
    }

    // JUMPS - CHECKS

    private boolean isBlackLeftJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && isEatenCheckerRedOrBlack(x-1, y-1, board) /* if the piece to be eaten is black color (because we are red in this condition) */);
    }

    private boolean isBlackRightJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && isEatenCheckerRedOrBlack(x-1, y+1, board) /* if the piece to be eaten is black color (because we are red in this condition) */);
    }

    private boolean isRedLeftJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && isEatenCheckerRedOrBlack(x+1, y-1, board) /* if the piece to be eaten is black color (because we are red in this condition) */);
    }

    private boolean isRedRightJumpDiagonalAvailable(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && isEatenCheckerRedOrBlack(x+1, y+1, board) /* if the piece to be eaten is black color (because we are red in this condition) */);
    }

    // only in the eating-checks we do, we need to check for black that the eaten piece is really a RED COLOR piece (so we can't eat our own color), and the same for a red piece.
    private boolean isEatenCheckerRedOrBlack(int x, int y, Board board) {
        if (isBlack)
            return !board.getBoardArray()[x][y].isBlack(); // check if there is red piece in front of me
        return board.getBoardArray()[x][y].isBlack(); // else, check if there is black piece in front of me
    }
}
