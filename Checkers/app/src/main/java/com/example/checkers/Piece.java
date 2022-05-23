package com.example.checkers;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import static com.example.checkers.DatabaseUtils.addDataToDatabase;
import static com.example.checkers.DatabaseUtils.getGuestUsername;
import static com.example.checkers.DatabaseUtils.isHost;
import static com.example.checkers.DatabaseUtils.updateBlackTurnInDb;
import static com.example.checkers.DatabaseUtils.uploadPieceLocationToDb;
import static com.example.checkers.MyOnClickListenerForPieceMoves.appContext;
import static com.example.checkers.MyOnClickListenerForPieceMoves.gameplayRef;
import static com.example.checkers.MyOnClickListenerForPieceMoves.guestMovesUpdatesListener;
import static com.example.checkers.MyOnClickListenerForPieceMoves.hostMovesUpdatesListener;
import static com.example.checkers.MyOnClickListenerForPieceMoves.lastUsedImageViews;
import static com.example.checkers.MyOnClickListenerForPieceMoves.roomRef;
import static com.example.checkers.WaitingRoomActivity.playerName;
import static com.example.checkers.WaitingRoomActivity.roomName;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

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

    public void rightDiagonal(Move rightMove, ImageView rightPieceImage, boolean isBlack, boolean isKing, boolean isJump, int jumpedPieceX, Board board) {
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
                board.getBoardArray()[endX][endY] = new Piece(endX, endY, isBlack, isKing);
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

                isGameOver(board);

                // set onClick for the new piece (location)
                rightPieceImage.setClickable(true);
                rightPieceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyOnClickListenerForPieceMoves onClickListenerForPieceMoves = new MyOnClickListenerForPieceMoves(board.getBoardArray()[endX][endY], board);
                        onClickListenerForPieceMoves.displayMoveOptionsAndMove(endX, endY, isBlack, board.getBoardArray()[endX][endY].isKing(), rightPieceImage); // recursively show more move options
                    }
                });

                // updating next turn - passing the turn to the other player
                updateBlackTurnInDb(!isBlack, gameplayRef);

                // upload new piece location to db
                uploadPieceLocationToDb(rightMove, isJump, jumpedPieceX, jumpedPieceY, board.getBoardArray()[endX][endY].isKing());
            }
        });
    }

    public void leftDiagonal(Move leftMove, ImageView leftPieceImage, boolean isBlack, boolean isKing, boolean isJump, int jumpedPieceX, Board board) {
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
                board.getBoardArray()[endX][endY] = new Piece(/*leftPieceImage,*/ endX, endY, isBlack, isKing);
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

                isGameOver(board);

                // set onClick for the new piece (location)
                leftPieceImage.setClickable(true);
                leftPieceImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyOnClickListenerForPieceMoves onClickListenerForPieceMoves = new MyOnClickListenerForPieceMoves(board.getBoardArray()[endX][endY], board);
                        onClickListenerForPieceMoves.displayMoveOptionsAndMove(endX, endY, isBlack, board.getBoardArray()[endX][endY].isKing(), leftPieceImage); // recursively show more move options
                    }
                });

                // updating next turn - passing the turn to the other player
                updateBlackTurnInDb(!isBlack, gameplayRef);

                // upload new piece location to db
                uploadPieceLocationToDb(leftMove, isJump, jumpedPieceX, jumpedPieceY, board.getBoardArray()[endX][endY].isKing());
            }
        });
    }

    public void clearPossibleLocationMarkers(Board board) {
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
    public void unsetOnClickLastImageViews(Board board) {
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

    public void isGameOver(Board board) {
        int redPieces = 0;
        int blackPieces = 0;
        for (int i = 0; i < Board.SIZE; i++)
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.getBoardArray()[i][j] != null) {
                    if (board.getBoardArray()[i][j].isBlack())
                        blackPieces++;
                    else
                        redPieces++;
                }
            }
        // black won
        if (redPieces == 0){
            // update in db that black won (the host)
            DocumentReference hostMovesUpdatesRef = gameplayRef.document("hostMovesUpdates");
            Map<String, Object> updateGameOver = new HashMap<>();
            updateGameOver.put("isGameOver", true);
            addDataToDatabase(updateGameOver, hostMovesUpdatesRef);

            // show locally on black's phone that he won
            gameOver(true);
        }

        // red won
        else if (blackPieces == 0)
        {
            DocumentReference guestMovesUpdatesRef = gameplayRef.document("guestMovesUpdates");
            Map<String, Object> updateGameOver = new HashMap<>();
            updateGameOver.put("isGameOver", true);
            addDataToDatabase(updateGameOver, guestMovesUpdatesRef);

            // show locally on red's phone that he won
            gameOver(false);
        }

    }

    public void gameOver(boolean isBlack) {
        //Log.d(TAG, "GAMEOVERRRRRRRRR*********");

        boolean host = isHost(roomName, playerName);

        AlertDialog.Builder gameRequestDialogBuilder = new AlertDialog.Builder(appContext, AlertDialog.THEME_HOLO_LIGHT);
        gameRequestDialogBuilder.setCancelable(false);
        gameRequestDialogBuilder.setTitle("Game is Over!");
        gameRequestDialogBuilder.setPositiveButton("Return Back To The Lobby", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                appContext.startActivity(new Intent(appContext, WaitingRoomActivity.class));
                ((Activity) appContext).finish(); // finish GameActivity
            }
        });

        if (isBlack)
        {
            // show popup that the host won (roomName = hostname)
            String hostUsername = roomName;
            gameRequestDialogBuilder.setMessage(hostUsername + " has won the game! he is probably better.");
        }
        else
        {
            // show popup that the guest won (getGuestUsername())
            String guestUsername;
            if (host) // on the host phone (he doesn't have the guest's username, so he has to get it from db
                guestUsername = getGuestUsername(roomRef);
            else // on the guest phone (the local username is stored in playerName)
                guestUsername = playerName;

            gameRequestDialogBuilder.setMessage(guestUsername + " has won the game! he is probably better.");
        }

        if (host)
        {
            // remove the guest from the room
            Map<String, Object> updates = new HashMap<>();
            updates.put("guest", FieldValue.delete()); // mark "guest" field as deletable on the database (remove it)
            updates.put("isInGame", false); // update isInGame to false
            addDataToDatabase(updates, roomRef);
        }

        AlertDialog gameRequestDialog;
        gameRequestDialog = gameRequestDialogBuilder.create();
        gameRequestDialog.show();

        // clean-up stuff
        if (hostMovesUpdatesListener != null)
            hostMovesUpdatesListener.remove();
        if (guestMovesUpdatesListener != null)
            guestMovesUpdatesListener.remove();

        // REMEMBER:
        // remove listeners, each hostMovesUpdates and guestMovesUpdates.
        // remove the guest from the room (like when the host declines or guest cancels in WaitingRoom)
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
