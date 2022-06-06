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

        boolean leftBlack = isLeftDiagonalAvailableBlack(board);
        boolean leftJumpRed = isLeftJumpDiagonalAvailableRed(board);
        boolean rightJumpBlack = isRightJumpDiagonalAvailableBlack(board);
        boolean rightJumpRed = isRightJumpDiagonalAvailableRed(board);

        // THERE NEEDS TO BE SEPARATION BETWEEN THE isBlack AND !isBlack (!!!)

        boolean rightBlack = isRightDiagonalAvailableBlack(board);
        boolean leftRed = isLeftDiagonalAvailableRed(board);
        boolean rightRed = isRightDiagonalAvailableRed(board);
        boolean leftJumpBlack = isLeftJumpDiagonalAvailableBlack(board);


        boolean leftJumpBlackOther = isLeftJumpDiagonalAvailableBlackOther(board);
        boolean rightJumpBlackOther = isRightJumpDiagonalAvailableBlackOther(board);
        boolean leftJumpRedOther = isLeftJumpDiagonalAvailableRedOther(board);
        boolean rightJumpRedOther = isRightJumpDiagonalAvailableRedOther(board);


        return (leftBlack || leftJumpRed || rightJumpBlack || rightJumpRed ||
                rightBlack || leftRed || rightRed || leftJumpBlack ||
                leftJumpBlackOther || rightJumpBlackOther || leftJumpRedOther || rightJumpRedOther);
    }

    @Override
    protected void updateBoardArray(Board board, int endX, int endY) {
        board.getBoardArray()[endX][endY] = new KingPiece(endX, endY, isBlack, currentTurn);
    }


    public void move(Board board) {
        /* -------------------------- left diagonal BLACK -------------------------- */
        if (isLeftDiagonalAvailableBlack(board)) {
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x - 1][y - 1];
            lastUsedImageViews[0] = leftPieceImage;
            Move leftMove = new Move(x, y, x - 1, y - 1);
            leftDiagonal(leftMove, leftPieceImage, isBlack, true, false, 0, board);
        }

        /* -------------------------- right diagonal BLACK -------------------------- */
        if (isRightDiagonalAvailableBlack(board)) {
            Move rightMove = new Move(x, y, x - 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x - 1][y + 1];
            lastUsedImageViews[1] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, isBlack, true, false, 0, board);
        }


        /* -------------------------- left diagonal RED -------------------------- */
        if (isLeftDiagonalAvailableRed(board)) {
            Move leftMove = new Move(x, y, x + 1, y - 1);
            ImageView leftPieceImage = GameActivity.imageViewsTiles[x + 1][y - 1];
            lastUsedImageViews[2] = leftPieceImage;
            leftDiagonal(leftMove, leftPieceImage, isBlack, true, false, 0, board);
        }

        /* -------------------------- right diagonal RED -------------------------- */
        if (isRightDiagonalAvailableRed(board)) {
            Move rightMove = new Move(x, y, x + 1, y + 1);
            ImageView rightPieceImage = GameActivity.imageViewsTiles[x + 1][y + 1];
            lastUsedImageViews[3] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, isBlack, true, false, 0, board);
        }


        if (isBlack) {
            /* -------------------------- left-JUMP diagonal BLACK -------------------------- */
            if (isLeftJumpDiagonalAvailableBlack(board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y - 2];
                lastUsedImageViews[4] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x - 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, true, true, true, x - 1, board);
            }
            /* -------------------------- right-JUMP diagonal BLACK -------------------------- */
            if (isRightJumpDiagonalAvailableBlack(board)) {
                ImageView rightJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y + 2];
                lastUsedImageViews[5] = rightJumpPieceImage;
                Move rightJumpMove = new Move(x, y, x - 2, y + 2);
                rightDiagonal(rightJumpMove, rightJumpPieceImage, true, true, true, x - 1, board);
            }
            /* -------------------------- left-JUMP diagonal RED -------------------------- */
            if (isLeftJumpDiagonalAvailableRed(board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y - 2];
                lastUsedImageViews[6] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, true, true, true, x + 1, board);
            }
            /* -------------------------- right-JUMP diagonal RED -------------------------- */
            if (isRightJumpDiagonalAvailableRed(board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y + 2];
                lastUsedImageViews[7] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y + 2);
                rightDiagonal(leftJumpMove, leftJumpPieceImage, true, true, true, x + 1, board);
            }
        } else {
            /* -------------------------- left-JUMP diagonal BLACK -------------------------- */
            if (isLeftJumpDiagonalAvailableBlackOther(board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y - 2];
                lastUsedImageViews[4] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x - 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, false, true, true, x - 1, board);
            }
            /* -------------------------- right-JUMP diagonal BLACK -------------------------- */
            if (isRightJumpDiagonalAvailableBlackOther(board)) {
                ImageView rightJumpPieceImage = GameActivity.imageViewsTiles[x - 2][y + 2];
                lastUsedImageViews[5] = rightJumpPieceImage;
                Move rightJumpMove = new Move(x, y, x - 2, y + 2);
                rightDiagonal(rightJumpMove, rightJumpPieceImage, false, true, true, x - 1, board);
            }
            /* -------------------------- left-JUMP diagonal RED -------------------------- */
            if (isLeftJumpDiagonalAvailableRedOther(board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y - 2];
                lastUsedImageViews[6] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, false, true, true, x + 1, board);
            }
            /* -------------------------- right-JUMP diagonal RED -------------------------- */
            if (isRightJumpDiagonalAvailableRedOther(board)) {
                ImageView leftJumpPieceImage = GameActivity.imageViewsTiles[x + 2][y + 2];
                lastUsedImageViews[7] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y + 2);
                rightDiagonal(leftJumpMove, leftJumpPieceImage, false, true, true, x + 1, board);
            }
        }
    }

    private boolean isLeftDiagonalAvailableBlack(Board board) {
        return (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */);
    }

    private boolean isRightDiagonalAvailableBlack(Board board) {
        return (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */);
    }

    private boolean isLeftDiagonalAvailableRed(Board board) {
        return (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */);
    }

    private boolean isRightDiagonalAvailableRed(Board board) {
        return (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */);
    }

    // ISBLACK

    private boolean isBlackLeftJumpDiagonalAvailableForBlack(Board board) {
        return (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && !board.getBoardArray()[x - 1][y - 1].isBlack() /* if the piece to be eaten is red color (because we are black in this condition) */);
    }

    private boolean isBlackRightJumpDiagonalAvailableForBlack(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && !board.getBoardArray()[x - 1][y + 1].isBlack() /* if the piece to be eaten is red color (because we are black in this condition) */);
    }

    private boolean isRedLeftJumpDiagonalAvailableForBlack(Board board) {
        return (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && !board.getBoardArray()[x + 1][y - 1].isBlack() /* if the piece to be eaten is red color (because we are black in this condition) */);
    }

    private boolean isRedRightJumpDiagonalAvailableForBlack(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && !board.getBoardArray()[x + 1][y + 1].isBlack() /* if the piece to be eaten is red color (because we are black in this condition) */);
    }


    // ! ISBLACK - *********CONTINUE HERE********

    private boolean isBlackLeftJumpDiagonalAvailableForRed(Board board) {
        return (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x - 1, y - 1, board));
    }

    private boolean isBlackRightJumpDiagonalAvailableForRed(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x - 1, y + 1, board));
    }

    private boolean isRedLeftJumpDiagonalAvailableForRed(Board board) {
        return (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x + 1, y - 1, board));
    }

    private boolean isRedRightJumpDiagonalAvailableForRed(Board board) {
        return (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x + 1, y + 1, board));
    }


    // only in the eating-checks we do, we need to check differently for black or red
    private boolean isCheckerBehindNeeds2BeRedOrBlack(boolean isPieceBlackColor, int x, int y, Board board) {
        if (isPieceBlackColor)
            return !board.getBoardArray()[x][y].isBlack(); // check if there is red piece in front of me
        return board.getBoardArray()[x][y].isBlack(); // else, check if there is black piece in front of me
    }
}
