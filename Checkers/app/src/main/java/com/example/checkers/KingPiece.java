package com.example.checkers;

import android.widget.ImageView;

public class KingPiece extends Piece{

    public KingPiece(int x, int y, boolean isBlack) {
        super(x, y, isBlack);
    }

    @Override
    public void move() {
        super.moveRed();
        super.moveBlack();
    }
}
