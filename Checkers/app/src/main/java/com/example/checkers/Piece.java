package com.example.checkers;

import android.widget.ImageView;

public class Piece {

    private ImageView image;
    private int x;
    private int y;
    private boolean isKing;
    private boolean isBlack; // color-wise

    public Piece(ImageView image, int x, int y, boolean isBlack) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
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

    public boolean isKing() {
        return this.isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

    public boolean isBlack() {
        return this.isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }
}
