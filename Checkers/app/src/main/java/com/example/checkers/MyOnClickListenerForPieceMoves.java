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
        lastUsedImageViews = new ImageView[10];
    }

    @Override
    public void onClick(View v) {
        displayMoveOptionsAndMove(this.piece.getX(), this.piece.getY(), this.piece.isBlack(), this.piece.isKing(), (ImageView) v);
    }

    public void displayMoveOptionsAndMove(int x, int y, boolean isBlack, boolean isKing, ImageView pieceImage) {

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
                    leftDiagonal(leftMove, leftPieceImage, true, false, false, 0);
                }

                /* -------------------------- left-JUMP diagonal -------------------------- */
                if (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && !board.getBoardArray()[x - 1][y - 1].isBlack()) {
                    ImageView leftJumpPieceImage = StartGameActivity.imageViewsTiles[x - 2][y - 2];
                    lastUsedImageViews[1] = leftJumpPieceImage;
                    Move leftJumpMove = new Move(x, y, x - 2, y - 2);
                    leftDiagonal(leftJumpMove, leftJumpPieceImage, true, false, true, x - 1);
                }

                /* -------------------------- right diagonal -------------------------- */
                if (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */) {
                    Move rightMove = new Move(x, y, x - 1, y + 1);
                    ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x - 1][y + 1];
                    lastUsedImageViews[2] = rightPieceImage;
                    rightDiagonal(rightMove, rightPieceImage, true, false, false, 0);
                }

                /* -------------------------- right-JUMP diagonal -------------------------- */
                if (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && !board.getBoardArray()[x - 1][y + 1].isBlack()) {
                    ImageView rightJumpPieceImage = StartGameActivity.imageViewsTiles[x - 2][y + 2];
                    lastUsedImageViews[3] = rightJumpPieceImage;
                    Move rightJumpMove = new Move(x, y, x - 2, y + 2);
                    rightDiagonal(rightJumpMove, rightJumpPieceImage, true, false, true, x - 1);
                }
            } else
                kingMove(x, y, true);
        }
        // for red
        else if (!isBlack && !isBlackTurn) {
            highlightPiece(false, isKing, pieceImage);
            if (!isKing) {
                /* -------------------------- left diagonal -------------------------- */
                if (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */) {
                    Move leftMove = new Move(x, y, x + 1, y - 1);
                    ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x + 1][y - 1];
                    lastUsedImageViews[4] = leftPieceImage;
                    leftDiagonal(leftMove, leftPieceImage, false, false, false, 0);
                }

                /* -------------------------- left-JUMP diagonal -------------------------- */

                if (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && board.getBoardArray()[x + 1][y - 1].isBlack()) {
                    ImageView leftJumpPieceImage = StartGameActivity.imageViewsTiles[x + 2][y - 2];
                    lastUsedImageViews[5] = leftJumpPieceImage;
                    Move leftJumpMove = new Move(x, y, x + 2, y - 2);
                    leftDiagonal(leftJumpMove, leftJumpPieceImage, false, false, true, x + 1);
                }

                /* -------------------------- right diagonal -------------------------- */
                if (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */) {
                    Move rightMove = new Move(x, y, x + 1, y + 1);
                    ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x + 1][y + 1];
                    lastUsedImageViews[6] = rightPieceImage;
                    rightDiagonal(rightMove, rightPieceImage, false, false, false, 0);
                }

                /* -------------------------- right-JUMP diagonal -------------------------- */
                if (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && board.getBoardArray()[x + 1][y + 1].isBlack()) {
                    ImageView leftJumpPieceImage = StartGameActivity.imageViewsTiles[x + 2][y + 2];
                    lastUsedImageViews[7] = leftJumpPieceImage;
                    Move leftJumpMove = new Move(x, y, x + 2, y + 2);
                    rightDiagonal(leftJumpMove, leftJumpPieceImage, false, false, true, x + 1);
                }

            } else
                kingMove(x, y, false);
        }
    }

    private void kingMove(int x, int y, boolean isBlack) {
        /* -------------------------- left diagonal BLACK -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x - 1, y - 1) /* left tile */) {
            ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x - 1][y - 1];
            lastUsedImageViews[0] = leftPieceImage;
            Move leftMove = new Move(x, y, x - 1, y - 1);
            leftDiagonal(leftMove, leftPieceImage, isBlack, true, false, 0);
        }

        /* -------------------------- right diagonal BLACK -------------------------- */
        if (Logic.canBlackMoveUp(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x - 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x - 1, y + 1);
            ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x - 1][y + 1];
            lastUsedImageViews[1] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, isBlack, true, false, 0);
        }



        /* -------------------------- left diagonal RED -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnLeftEdge(y) && Logic.isTileAvailable(board, x + 1, y - 1) /* left tile */) {
            Move leftMove = new Move(x, y, x + 1, y - 1);
            ImageView leftPieceImage = StartGameActivity.imageViewsTiles[x + 1][y - 1];
            lastUsedImageViews[2] = leftPieceImage;
            leftDiagonal(leftMove, leftPieceImage, isBlack, true, false, 0);
        }

        /* -------------------------- right diagonal RED -------------------------- */
        if (Logic.canRedMoveDown(x) && !Logic.isOnRightEdge(y) && Logic.isTileAvailable(board, x + 1, y + 1) /* right tile */) {
            Move rightMove = new Move(x, y, x + 1, y + 1);
            ImageView rightPieceImage = StartGameActivity.imageViewsTiles[x + 1][y + 1];
            lastUsedImageViews[3] = rightPieceImage;
            rightDiagonal(rightMove, rightPieceImage, isBlack, true, false, 0);
        }


        if (isBlack) {
            /* -------------------------- left-JUMP diagonal BLACK -------------------------- */
            if (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(true, x - 1, y - 1)) {
                ImageView leftJumpPieceImage = StartGameActivity.imageViewsTiles[x - 2][y - 2];
                lastUsedImageViews[4] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x - 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, true, true, true, x - 1);
            }
            /* -------------------------- right-JUMP diagonal BLACK -------------------------- */
            if (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(true, x - 1, y + 1)) {
                ImageView rightJumpPieceImage = StartGameActivity.imageViewsTiles[x - 2][y + 2];
                lastUsedImageViews[5] = rightJumpPieceImage;
                Move rightJumpMove = new Move(x, y, x - 2, y + 2);
                rightDiagonal(rightJumpMove, rightJumpPieceImage, true, true, true, x - 1);
            }
            /* -------------------------- left-JUMP diagonal RED -------------------------- */
            if (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(true, x + 1, y - 1)) {
                ImageView leftJumpPieceImage = StartGameActivity.imageViewsTiles[x + 2][y - 2];
                lastUsedImageViews[6] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, true, true, true, x + 1);
            }
            /* -------------------------- right-JUMP diagonal RED -------------------------- */
            if (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(true, x + 1, y + 1)) {
                ImageView leftJumpPieceImage = StartGameActivity.imageViewsTiles[x + 2][y + 2];
                lastUsedImageViews[7] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y + 2);
                rightDiagonal(leftJumpMove, leftJumpPieceImage, true, true, true, x + 1);
            }
        } else {
            /* -------------------------- left-JUMP diagonal BLACK -------------------------- */
            if (Logic.hasSpaceForLeftJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y - 2) && !Logic.isTileAvailable(board, x - 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x - 1, y - 1)) {
                ImageView leftJumpPieceImage = StartGameActivity.imageViewsTiles[x - 2][y - 2];
                lastUsedImageViews[4] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x - 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, false, true, true, x - 1);
            }
            /* -------------------------- right-JUMP diagonal BLACK -------------------------- */
            if (Logic.hasSpaceForRightJump(x, y, true) && Logic.isTileAvailable(board, x - 2, y + 2) && !Logic.isTileAvailable(board, x - 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x - 1, y + 1)) {
                ImageView rightJumpPieceImage = StartGameActivity.imageViewsTiles[x - 2][y + 2];
                lastUsedImageViews[5] = rightJumpPieceImage;
                Move rightJumpMove = new Move(x, y, x - 2, y + 2);
                rightDiagonal(rightJumpMove, rightJumpPieceImage, false, true, true, x - 1);
            }
            /* -------------------------- left-JUMP diagonal RED -------------------------- */
            if (Logic.hasSpaceForLeftJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y - 2) && !Logic.isTileAvailable(board, x + 1, y - 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x + 1, y - 1)) {
                ImageView leftJumpPieceImage = StartGameActivity.imageViewsTiles[x + 2][y - 2];
                lastUsedImageViews[6] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y - 2);
                leftDiagonal(leftJumpMove, leftJumpPieceImage, false, true, true, x + 1);
            }
            /* -------------------------- right-JUMP diagonal RED -------------------------- */
            if (Logic.hasSpaceForRightJump(x, y, false) && Logic.isTileAvailable(board, x + 2, y + 2) && !Logic.isTileAvailable(board, x + 1, y + 1) && isCheckerBehindNeeds2BeRedOrBlack(false, x + 1, y + 1)) {
                ImageView leftJumpPieceImage = StartGameActivity.imageViewsTiles[x + 2][y + 2];
                lastUsedImageViews[7] = leftJumpPieceImage;
                Move leftJumpMove = new Move(x, y, x + 2, y + 2);
                rightDiagonal(leftJumpMove, leftJumpPieceImage, false, true, true, x + 1);
            }
        }
    }

    // only in the eating-checks we do, we need to check differently for black or red
    private boolean isCheckerBehindNeeds2BeRedOrBlack(boolean isBlack, int x, int y) {
        if (isBlack)
            return !board.getBoardArray()[x][y].isBlack(); // check if there is red piece behind me
        return board.getBoardArray()[x][y].isBlack(); // else, check if there is black piece behind me
    }


    private void rightDiagonal(Move rightMove, ImageView rightPieceImage, boolean isBlack, boolean isKing, boolean isJump, int jumpedPieceX) {
        rightPieceImage.setImageResource(R.drawable.possible_location_marker);
        rightPieceImage.setClickable(true);
        rightPieceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int endX = rightMove.getEndX();
                int endY = rightMove.getEndY();
                int startX = rightMove.getStartX();
                int startY = rightMove.getStartY();

                // updating boardArray
                board.getBoardArray()[endX][endY] = new Piece(rightPieceImage, endX, endY, isBlack, isKing);
                board.getBoardArray()[startX][startY] = null; // remove old piece
                if (isJump) {
                    int jumpedPieceY = startY + 1;

                    // delete the jumped piece
                    StartGameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setImageResource(android.R.color.transparent);
                    StartGameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setClickable(false);
                    board.getBoardArray()[jumpedPieceX][jumpedPieceY] = null;
                }
                clearPossibleLocationMarkers();
                unsetOnClickLastImageViews();

                // check if needs to be king
                if (Logic.isPieceNeeds2BeKing(isBlack, endX))
                    board.getBoardArray()[endX][endY].setKing();

                rightMove.perform(isBlack, board.getBoardArray()[endX][endY].isKing());

                isGameOver();

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

    private void leftDiagonal(Move leftMove, ImageView leftPieceImage, boolean isBlack, boolean isKing, boolean isJump, int jumpedPieceX) {
        leftPieceImage.setImageResource(R.drawable.possible_location_marker);
        leftPieceImage.setClickable(true);
        leftPieceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int endX = leftMove.getEndX();
                int endY = leftMove.getEndY();
                int startX = leftMove.getStartX();
                int startY = leftMove.getStartY();


                // updating boardArray
                board.getBoardArray()[endX][endY] = new Piece(leftPieceImage, endX, endY, isBlack, isKing);
                board.getBoardArray()[startX][startY] = null; // remove old piece
                if (isJump) {
                    int jumpedPieceY = startY - 1;

                    // delete the jumped piece
                    StartGameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setImageResource(android.R.color.transparent);
                    StartGameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setClickable(false);
                    board.getBoardArray()[jumpedPieceX][jumpedPieceY] = null;
                }
                clearPossibleLocationMarkers();
                unsetOnClickLastImageViews();

                // check if needs to be king
                if (Logic.isPieceNeeds2BeKing(isBlack, endX))
                    board.getBoardArray()[endX][endY].setKing();

                leftMove.perform(isBlack, board.getBoardArray()[endX][endY].isKing());

                isGameOver();

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

    public void isGameOver() {
        int redPieces = 0;
        int blackPieces = 0;
        for (int i = 0; i < Board.SIZE; i++)
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.getBoardArray()[i][j] != null)
                {
                    if (board.getBoardArray()[i][j].isBlack())
                        blackPieces++;
                    else
                        redPieces++;
                }
            }
        if (redPieces == 0)
            gameOver(false);
        else if(blackPieces == 0)
            gameOver(true);
    }

    private void gameOver(boolean isBlack) {
        Log.d(TAG, "GAMEOVERRRRRRRRR*********");
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
                        Integer tag = (Integer) StartGameActivity.imageViewsTiles[i][j].getTag();
                        if (tag != null) {
                            if (board.getBoardArray()[i][j].isBlack()) {
                                if (tag == R.drawable.black_piece_highlighted) {
                                    StartGameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.black_piece);
                                    Log.d(TAG, "black piece");
                                } else if (tag == R.drawable.black_king_highlighted) // king
                                {
                                    StartGameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.black_king);
                                    Log.d(TAG, "black king");
                                }

                            } else {
                                if (tag == R.drawable.red_piece_highlighted) {
                                    StartGameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.red_piece);
                                    Log.d(TAG, "red piece");
                                } else if (tag == R.drawable.red_king_highlighted)// king
                                {
                                    StartGameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.red_king);
                                    Log.d(TAG, "red king");
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

