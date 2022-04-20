package com.example.checkers;

import android.widget.ImageView;

// class that defines a simple move on the board
public class Move {
    private int startX, startY, endX, endY;
    private Board board;

    public Move(Board board, int startX, int startY, int endX, int endY) {
        this.board = board;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

//    public void performMove() {
//        ImageView currPos = this.board.getTiles()[this.startX][this.startY];
//        ImageView nextPos = this.board.getTiles()[this.endX][this.endY];
//        currPos.setImageResource(android.R.color.transparent);
//        currPos.setClickable(false);
//        nextPos.setImageResource(R.drawable.black_piece); // ################ CHANGE THIS TO DYNAMICALLY GET COLOR!!!!!!!!!!
//    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return this.startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return this.endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return this.endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

}
