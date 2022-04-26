package com.example.checkers;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MyOnClickListenerForPieceMoves implements View.OnClickListener {

    public static final String TAG = "MyListenerForPieceMoves";
    private static ImageView[] lastUsedImageViews; // for removing the setOnClickListeners that we set and the player did not choose, so there will not be hanging listeners.
    public static boolean isBlackTurn;
    private final Piece piece;
    private final Board board;

    public MyOnClickListenerForPieceMoves(Piece piece, Board board) {
        this.piece = piece;
        this.board = board;
        isBlackTurn = true;
        lastUsedImageViews = new ImageView[6]; //  ################### CHANGE THE SIZE OF THE ARRAY ACCORDING TO THE AMOUNT OF setOnClickListeners YOU HAVE!!!!!!!!!!!#############
    }

    @Override
    public void onClick(View v) {
        displayMoveOptionsAndMove(this.piece.getX(), this.piece.getY(), this.piece.isBlack(), this.piece.isKing(), (ImageView) v);
    }

    private void displayMoveOptionsAndMove(int x, int y, boolean isBlack, boolean isKing, ImageView pieceImage) {

        clearPossibleLocationMarkers();
        unsetOnClickLastImageViews();

        // for black
        if (isBlack && isBlackTurn) {
            highlightPiece(true, isKing, pieceImage);
            if (!isKing) {
                /* -------------------------- left diagonal -------------------------- */
                if (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */) {
                    ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x - 1][y - 1];
                    lastUsedImageViews[0] = leftPieceImage;
                    Move leftMove = new Move(x, y, x - 1, y - 1);
                    leftDiagonal(leftMove, leftPieceImage, true, false);
                }

                /* -------------------------- left-JUMP diagonal -------------------------- */


                /* -------------------------- right diagonal -------------------------- */
                if (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */) {
                    Move rightMove = new Move(x, y, x - 1, y + 1);
                    ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x - 1][y + 1];
                    lastUsedImageViews[1] = rightPieceImage;
                    rightDiagonal(rightMove, rightPieceImage, true, false);
                }

                /* -------------------------- right-JUMP diagonal -------------------------- */

            }
            else
                blackKingMove(x, y);
        }
        // for red
        else if (!isBlack && !isBlackTurn){
            highlightPiece(false, isKing, pieceImage);
            if (!isKing) {
                /* -------------------------- left diagonal -------------------------- */
                if (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */) {
                    Move leftMove = new Move(x, y, x + 1, y - 1);
                    ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x + 1][y - 1];
                    lastUsedImageViews[2] = leftPieceImage;
                    leftDiagonal(leftMove, leftPieceImage, false, false);
                }

                /* -------------------------- left-JUMP diagonal -------------------------- */


                /* -------------------------- right diagonal -------------------------- */
                if (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */) {
                    Move rightMove = new Move(x, y, x + 1, y + 1);
                    ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x + 1][y + 1];
                    lastUsedImageViews[3] = rightPieceImage;
                    rightDiagonal(rightMove, rightPieceImage, false, false);
                }

                /* -------------------------- right-JUMP diagonal -------------------------- */

            }
            else
                redKingMove(x, y);
        }
    }

    private void blackKingMove(int x, int y)
    {
        /* -------------------------- left diagonal BLACK -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */) {
            ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x - 1][y - 1];
            lastUsedImageViews[0] = leftPieceImage;
            Move leftMove = new Move(x, y, x - 1, y - 1);
            leftDiagonal(leftMove, leftPieceImage, true, true);
        }

        /* -------------------------- right diagonal BLACK -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x - 1, y + 1);
            ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x - 1][y + 1];
            lastUsedImageViews[1] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, true, true);
        }

        /* -------------------------- left diagonal RED -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */) {
            Move leftMove = new Move(x, y, x + 1, y - 1);
            ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x + 1][y - 1];
            lastUsedImageViews[2] = leftPieceImage;
            leftDiagonal(leftMove, leftPieceImage, true, true);
        }
        /* -------------------------- right diagonal RED -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x + 1, y + 1);
            ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x + 1][y + 1];
            lastUsedImageViews[3] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, true, true);
        }
    }

    private void redKingMove(int x, int y)
    {
        /* -------------------------- left diagonal BLACK -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */) {
            ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x - 1][y - 1];
            lastUsedImageViews[0] = leftPieceImage;
            Move leftMove = new Move(x, y, x - 1, y - 1);
            leftDiagonal(leftMove, leftPieceImage, false, true);
        }

        /* -------------------------- right diagonal BLACK -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x - 1, y + 1);
            ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x - 1][y + 1];
            lastUsedImageViews[1] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, false, true);
        }

        /* -------------------------- left diagonal RED -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */) {
            Move leftMove = new Move(x, y, x + 1, y - 1);
            ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x + 1][y - 1];
            lastUsedImageViews[2] = leftPieceImage;
            leftDiagonal(leftMove, leftPieceImage, false, true);
        }
        /* -------------------------- right diagonal RED -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x + 1, y + 1);
            ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x + 1][y + 1];
            lastUsedImageViews[3] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, false, true);
        }
    }



    private void rightDiagonal(Move rightMove, ImageView rightPieceImage, boolean isBlack, boolean isKing){
        rightPieceImage.setImageResource(R.drawable.possible_location_marker);
        rightPieceImage.setClickable(true);
        rightPieceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int endX = rightMove.getEndX();
                int endY = rightMove.getEndY();
                rightMove.perform(isBlack, Logic.isBlackNeeds2BeKing(endX));

                // updating boardArray
                board.getBoardArray()[endX][endY] = new Piece(rightPieceImage, endX, endY, isBlack, isKing);
                board.getBoardArray()[rightMove.getStartX()][rightMove.getStartY()] = null; // remove old piece
                clearPossibleLocationMarkers();
                unsetOnClickLastImageViews();

                // check if needs to be king
                if (Logic.isPieceNeeds2BeKing(isBlack, endX))
                    board.getBoardArray()[endX][endY].setKing();


                // set onClick for the new piece (location)
                rightPieceImage.setClickable(true);
                rightPieceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayMoveOptionsAndMove(endX, endY, isBlack, board.getBoardArray()[endX][endY].isKing(), rightPieceImage); // recursively show more move options
                    }
                });
                // pass the turn to the other player
                isBlackTurn = !isBlack;
            }
        });
    }

    private void leftDiagonal(Move leftMove, ImageView leftPieceImage, boolean isBlack, boolean isKing){
        leftPieceImage.setImageResource(R.drawable.possible_location_marker);
        leftPieceImage.setClickable(true);
        leftPieceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int endX = leftMove.getEndX();
                int endY = leftMove.getEndY();
                leftMove.perform(isBlack, isKing);

                // updating boardArray
                board.getBoardArray()[endX][endY] = new Piece(leftPieceImage, endX, endY, isBlack, isKing);
                board.getBoardArray()[leftMove.getStartX()][leftMove.getStartY()] = null; // remove old piece
                clearPossibleLocationMarkers();
                unsetOnClickLastImageViews();

                // check if needs to be king
                if (Logic.isPieceNeeds2BeKing(isBlack, endX))
                    board.getBoardArray()[endX][endY].setKing();

                // set onClick for the new piece (location)
                leftPieceImage.setClickable(true);
                leftPieceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayMoveOptionsAndMove(endX, endY, isBlack, board.getBoardArray()[endX][endY].isKing(), leftPieceImage); // recursively show more move options
                    }
                });
                // pass the turn to the other player
                isBlackTurn = !isBlack;
            }
        });
    }

    private void highlightPiece(boolean isBlack, boolean isKing, ImageView piece) {
        if (isBlack) {
            if (isKing) {
                piece.setImageResource(R.drawable.black_king_highlighted);
                piece.setTag(R.drawable.black_king_highlighted);
            } else {
                piece.setImageResource(R.drawable.black_piece_highlighted);
                piece.setTag(R.drawable.black_piece_highlighted);
            }

        } else {
            if (isKing) {
                piece.setImageResource(R.drawable.red_king_highlighted);
                piece.setTag(R.drawable.red_king_highlighted);
            } else {
                piece.setImageResource(R.drawable.red_piece_highlighted);
                piece.setTag(R.drawable.red_piece_highlighted);
            }

        }
    }

    public void clearPossibleLocationMarkers() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (Logic.isTileForChecker(i, j)) {
                    if (board.getBoardArray()[i][j] != null) {
                        Integer tag = (Integer) board.getBoardArray()[i][j].getImage().getTag();
                        if (tag != null) {
                            if (board.getBoardArray()[i][j].isBlack()) {
                                if (tag == R.drawable.black_piece_highlighted) {
                                    StartGameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.black_piece);
                                }
                                else if (tag == R.drawable.black_king_highlighted) // king
                                {
                                    StartGameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.black_king);
                                    Log.d(TAG, "black king is ImageReSORUCE");
                                }

                            } else {
                                if (tag == R.drawable.red_piece_highlighted) {
                                    StartGameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.red_piece);
                                }
                                else if (tag == R.drawable.red_king_highlighted)// king
                                {
                                    StartGameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.red_king);
                                    Log.d(TAG, "RED KinG SI foo");
                                }


                            }
                        }
                    } else // remove possible_loc_marker
                        StartGameActivity.imageViewsTiles[i][j].setImageResource(android.R.color.transparent);
                }
            }
        }
    }

    // responsible for removing the setOnClickListeners that we set and the player did not choose to go, so there will not be hanging listeners.
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
                String axis = idStr.substring(idStr.length() - 2);
                int x = Character.getNumericValue(axis.charAt(0));
                int y = Character.getNumericValue(axis.charAt(1));


                if (board.getBoardArray()[x][y] == null) {
                    image.setClickable(false);
                    image.setOnClickListener(null);
                }
            }
        }


    }
}

