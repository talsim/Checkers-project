package com.example.checkers;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MyOnClickListenerForPieceMoves implements View.OnClickListener {

    public static final String TAG = "MyListenerForPieceMoves";
    private static ImageView[] lastUsedImageViews; // for removing the setOnClickListeners that we set and the player did not choose, so there will not be hanging listeners.
    private final Piece piece;
    private final Board board;

    public MyOnClickListenerForPieceMoves(Piece piece, Board board) {
        this.piece = piece;
        this.board = board;
        lastUsedImageViews = new ImageView[4]; //  ################### CHANGE THE SIZE OF THE ARRAY ACCORDING TO THE AMOUNT OF setOnClickListeners YOU HAVE!!!!!!!!!!!#############
    }

    @Override
    public void onClick(View v) {
        displayMoveOptionsAndMove(this.piece.getX(), this.piece.getY(), this.piece.isBlack());
    }

    private void displayMoveOptionsAndMove(int x, int y, boolean isBlack) {

        clearPossibleLocationMarkers();
        unsetOnClickLastImageViews();


        // for black
        if (isBlack) {
            /* -------------------------- left diagonal -------------------------- */
            if (Logic.canBlackMove(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */) {
                Move leftMove = new Move(x, y, x - 1, y - 1);
                ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x - 1][y - 1];
                lastUsedImageViews[0] = leftPieceImage;
                leftPieceImage.setImageResource(R.drawable.possible_location_marker);
                leftPieceImage.setClickable(true);
                leftPieceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leftMove.perform(true);

                        // updating boardArray
                        int endX = leftMove.getEndX();
                        int endY = leftMove.getEndY();
                        board.getBoardArray()[endX][endY] = new Piece(leftPieceImage, endX, endY, true);
                        board.getBoardArray()[x][y] = null; // remove old piece
                        clearPossibleLocationMarkers();
                        unsetOnClickLastImageViews();

                        // set onClick for the new piece (location)
                        leftPieceImage.setClickable(true);
                        leftPieceImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                displayMoveOptionsAndMove(endX, endY, true); // recursively show more move options
                            }
                        });
                    }
                });
            }

            /* -------------------------- right diagonal -------------------------- */
            if (Logic.canBlackMove(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */) {
                Move rightMove = new Move(x, y, x - 1, y + 1);
                ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x - 1][y + 1];
                lastUsedImageViews[1] = rightPieceImage;
                rightPieceImage.setImageResource(R.drawable.possible_location_marker);
                rightPieceImage.setClickable(true);
                rightPieceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rightMove.perform(true);

                        // updating boardArray
                        int endX = rightMove.getEndX();
                        int endY = rightMove.getEndY();
                        board.getBoardArray()[endX][endY] = new Piece(rightPieceImage, endX, endY, true);
                        board.getBoardArray()[x][y] = null; // remove old piece
                        clearPossibleLocationMarkers();
                        unsetOnClickLastImageViews();

                        // set onClick for the new piece (location)
                        rightPieceImage.setClickable(true);
                        rightPieceImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                displayMoveOptionsAndMove(endX, endY, true); // recursively show more move options
                            }
                        });
                    }
                });
            }
        }
    }

    public void clearPossibleLocationMarkers() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.getBoardArray()[i][j] == null && Logic.isTileForChecker(i, j))
                    StartGameActivity.imageViewsTiles[i][j].setImageResource(android.R.color.transparent);
            }
        }
    }

    public void unsetOnClickLastImageViews() {
        // how to make sure that at the place of the image there isn't also a checkers piece:
        // 1. get id of image; extract X and Y axis from it
        // 2. compare those X and Y in boardArray
        // 3. if a piece is found at that location: do not unset it!
        // 4. else: unset it.
        for (ImageView image : lastUsedImageViews) {
            if (image != null) {
                // get id of image and extract axis
                String idStr = image.getResources().getResourceEntryName(image.getId());
                String XAndY = idStr.substring(idStr.length() - 2);
                int x = Character.getNumericValue(XAndY.charAt(0));
                int y = Character.getNumericValue(XAndY.charAt(1));


                if (board.getBoardArray()[x][y] == null) {
                    image.setClickable(false);
                    image.setOnClickListener(null);
                }
            }
        }


    }
}

