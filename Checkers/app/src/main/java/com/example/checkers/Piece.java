package com.example.checkers;


import static com.example.checkers.DBUtils.isGameOver;
import static com.example.checkers.DBUtils.updateBlackTurnInDb;
import static com.example.checkers.DBUtils.uploadPieceLocationToDb;
import static com.example.checkers.OnClickListenerForPieceMoves.gameplayRef;
import static com.example.checkers.OnClickListenerForPieceMoves.lastUsedImageViews;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Piece {

    protected int x;
    protected int y;
    protected boolean isKing;
    protected boolean isBlack; // color-wise
    protected final TextView currentTurn;

    public Piece(int x, int y, boolean isBlack, boolean isKing, TextView currentTurn) {
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
        this.isKing = isKing;
        this.currentTurn = currentTurn;
    }

    public Piece(int x, int y, boolean isBlack, TextView currentTurn) {
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
        this.isKing = false;
        this.currentTurn = currentTurn;
    }


    public boolean canMove(Board board) {
        if (this.isKing) // if King piece : can king move?
            return ((KingPiece) this).canMove(board);
        else if (this.isBlack) // else if Black piece : can black move?
            return ((BlackPiece) this).canMove(board);
        else // else Red piece : can red move?
            return ((RedPiece) this).canMove(board);
    }

    protected void rightDiagonal(Move rightMove, ImageView rightPieceImage, boolean isBlack, boolean isKing, boolean isJump, int jumpedPieceX, Board board) {
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
                updateBoardArray(board, endX, endY);
                board.getBoardArray()[startX][startY] = null; // remove old piece
                int jumpedPieceY = startY + 1;
                if (isJump) {
                    // delete the jumped piece
                    GameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setImageResource(android.R.color.transparent);
                    GameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setClickable(false);
                    board.getBoardArray()[jumpedPieceX][jumpedPieceY] = null;
                }
                clearPossibleLocationMarkers(board);
                unsetOnClickLastImageViews(board);

                // check if needs to be king
                if (Logic.isPieceNeeds2BeKing(isBlack, endX))
                    board.getBoardArray()[endX][endY].setKing();

                rightMove.perform(isBlack, board.getBoardArray()[endX][endY].isKing());

                // set onClick for the new piece (location)
                rightPieceImage.setClickable(true);
                rightPieceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OnClickListenerForPieceMoves onClickListenerForPieceMoves = new OnClickListenerForPieceMoves(board.getBoardArray()[endX][endY], board, currentTurn);
                        onClickListenerForPieceMoves.displayMoveOptionsAndMove(endX, endY, isBlack, board.getBoardArray()[endX][endY].isKing(), rightPieceImage); // recursively show more move options
                    }
                });

                // updating next turn - passing the turn to the other player
                updateBlackTurnInDb(!isBlack, gameplayRef);
                currentTurn.setText(R.string.not_your_turn);

                // upload new piece location to db
                uploadPieceLocationToDb(rightMove, isJump, jumpedPieceX, jumpedPieceY, board.getBoardArray()[endX][endY].isKing());

                isGameOver(board);
            }
        });
    }

    protected void leftDiagonal(Move leftMove, ImageView leftPieceImage, boolean isBlack, boolean isKing, boolean isJump, int jumpedPieceX, Board board) {
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
                updateBoardArray(board, endX, endY);
                board.getBoardArray()[startX][startY] = null; // remove old piece
                int jumpedPieceY = startY - 1;
                if (isJump) {
                    // delete the jumped piece
                    GameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setImageResource(android.R.color.transparent);
                    GameActivity.imageViewsTiles[jumpedPieceX][jumpedPieceY].setClickable(false);
                    board.getBoardArray()[jumpedPieceX][jumpedPieceY] = null;
                }
                clearPossibleLocationMarkers(board);
                unsetOnClickLastImageViews(board);

                // check if needs to be king
                if (Logic.isPieceNeeds2BeKing(isBlack, endX))
                    board.getBoardArray()[endX][endY].setKing();

                leftMove.perform(isBlack, board.getBoardArray()[endX][endY].isKing());

                // set onClick for the new piece (location)
                leftPieceImage.setClickable(true);
                leftPieceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OnClickListenerForPieceMoves onClickListenerForPieceMoves = new OnClickListenerForPieceMoves(board.getBoardArray()[endX][endY], board, currentTurn);
                        onClickListenerForPieceMoves.displayMoveOptionsAndMove(endX, endY, isBlack, board.getBoardArray()[endX][endY].isKing(), leftPieceImage); // recursively show more move options
                    }
                });

                // updating next turn - passing the turn to the other player
                updateBlackTurnInDb(!isBlack, gameplayRef);
                currentTurn.setText(R.string.not_your_turn);

                // upload new piece location to db
                uploadPieceLocationToDb(leftMove, isJump, jumpedPieceX, jumpedPieceY, board.getBoardArray()[endX][endY].isKing());

                isGameOver(board);
            }
        });
    }

    // This function is overridden by all the subclasses of Piece, because their board-update is different.
    protected void updateBoardArray(Board board, int endX, int endY)
    {
        board.getBoardArray()[endX][endY] = new Piece(endX, endY, isBlack, isKing, currentTurn);
    }


    protected void clearPossibleLocationMarkers(Board board) {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (Logic.isTileForChecker(i, j)) {
                    if (board.getBoardArray()[i][j] != null) {
                        Integer tag = (Integer) GameActivity.imageViewsTiles[i][j].getTag();
                        if (tag != null) {
                            if (board.getBoardArray()[i][j].isBlack()) {
                                if (tag == R.drawable.black_piece_highlighted) {
                                    GameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.black_piece);
                                } else if (tag == R.drawable.black_king_highlighted) // king
                                {
                                    GameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.black_king);
                                }

                            } else {
                                if (tag == R.drawable.red_piece_highlighted) {
                                    GameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.red_piece);
                                } else if (tag == R.drawable.red_king_highlighted)// king
                                {
                                    GameActivity.imageViewsTiles[i][j].setImageResource(R.drawable.red_king);
                                }


                            }
                        }
                    } else // remove possible_loc_marker
                        GameActivity.imageViewsTiles[i][j].setImageResource(android.R.color.transparent);
                }
            }
        }
    }

    // responsible for removing the setOnClickListeners that we set and the player did not choose to go, so there will not be hanging listeners.
    protected void unsetOnClickLastImageViews(Board board) {
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
