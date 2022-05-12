package com.example.checkers;

import android.widget.ImageView;

public class KingPiece extends Piece{

    public KingPiece(ImageView image, int x, int y, boolean isBlack) {
        super(image, x, y, isBlack);
    }

    @Override
    public void move() {
        super.moveRed();
        super.moveBlack();
    }
}
