package com.example.checkers;


import android.view.View;
import android.widget.ImageView;

public class Piece {

    protected int x;
    protected int y;
    protected boolean isKing;
    protected boolean isBlack; // color-wise

    public Piece(int x, int y, boolean isBlack, boolean isKing) {
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
        this.isKing = isKing;
    }

    public Piece(int x, int y, boolean isBlack) {
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
        this.isKing = false;
    }

    public void move()
    {
        if (!this.isBlack)
            moveRed();
        else
            moveBlack();
    }

    // move independently of the color, just show the 2 diagonals that a red piece can do
    public void moveRed()
    {
//        rightPieceImage.setImageResource(R.drawable.possible_location_marker);
//        rightPieceImage.setClickable(true);
//        rightPieceImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int endX = rightMove.getEndX();
//                int endY = rightMove.getEndY();
//                int startX = rightMove.getStartX();
//                int startY = rightMove.getStartY();
//
//                // updating boardArray
//                board.getBoardArray()[endX][endY] = new Piece(rightPieceImage, endX, endY, isBlack, isKing);
//                board.getBoardArray()[startX][startY] = null; // remove old piece
//                if (isJump) {
//                    int jumpedPieceY = startY + 1;
//
//                    // delete the jumped piece
//                    StartGameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setImageResource(android.R.color.transparent);
//                    StartGameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setClickable(false);
//                    board.getBoardArray()[jumpedPieceX][jumpedPieceY] = null;
//                }
//                clearPossibleLocationMarkers();
//                unsetOnClickLastImageViews();
//
//                // check if needs to be king
//                if (Logic.isPieceNeeds2BeKing(isBlack, endX) && !isKing)
//                    board.getBoardArray()[endX][endY].setKing();
//
//                rightMove.perform(isBlack, board.getBoardArray()[endX][endY].isKing());
//
//                isGameOver();
//
//                // set onClick for the new piece (location)
//                rightPieceImage.setClickable(true);
//                rightPieceImage.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        displayMoveOptionsAndMove(endX, endY, isBlack, board.getBoardArray()[endX][endY].isKing(), rightPieceImage); // recursively show more move options
//                    }
//                });
//                // pass the turn to the other player
//                isBlackTurn = !isBlack;
//            }
//        });
    }

    public void moveBlack()
    {

    }

    public boolean isKing() {
        return this.isKing;
    }

    public void setKing() {
        this.isKing = true;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isBlack() {
        return this.isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }
}
