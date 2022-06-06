package com.example.checkers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import static com.example.checkers.DBUtils.getIsBlackTurn;
import static com.example.checkers.DBUtils.isHost;
import static com.example.checkers.LobbyActivity.roomRef;

public class OnClickListenerForPieceMoves implements View.OnClickListener {

    public static final String TAG = "MyListenerForPieceMoves";
    public static ImageView[] lastUsedImageViews; // for removing the setOnClickListeners that we set and the player did not choose, so there will not be hanging listeners.
    public static Context appContext; // for showing dialogs
    public Piece piece;
    public TextView currentTurn;
    private final Board board;
    public static CollectionReference gameplayRef;


    public OnClickListenerForPieceMoves(Piece piece, Board board, TextView currentTurn) {
        this.piece = piece;
        this.board = board;
        this.currentTurn = currentTurn;
        gameplayRef = roomRef.collection("gameplay");
        appContext = null;
        lastUsedImageViews = new ImageView[10];
    }

    @Override
    public void onClick(View v) {
        displayMoveOptionsAndMove(this.piece.getX(), this.piece.getY(), this.piece.isBlack(), this.piece.isKing(), (ImageView) v);
    }

    public void displayMoveOptionsAndMove(int x, int y, boolean isBlack, boolean isKing, ImageView pieceImage) {

        appContext = pieceImage.getContext();
        this.piece.clearPossibleLocationMarkers(board);
        this.piece.unsetOnClickLastImageViews(board);

        boolean isBlackTurn = getIsBlackTurn();

        if (isHost()) // for the host (for black)
        {
            if (isBlack && isBlackTurn) {
                highlightPiece(true, isKing, pieceImage);
                if (!isKing) {
                    // move black
                    ((BlackPiece) this.piece).move(board);
                } else {
                    this.piece = new KingPiece(x, y, true, currentTurn);
                    ((KingPiece) this.piece).move(board);
                }
            }

        } else // for the guest (for red)
        {
            if (!isBlack && !isBlackTurn) {

                highlightPiece(false, isKing, pieceImage);
                if (!isKing) {

                    ((RedPiece) this.piece).move(board);
                } else {
                    this.piece = new KingPiece(x, y, false, currentTurn);
                    ((KingPiece) this.piece).move(board);
                }
            }

        }

//        ****play locally****
//        if (isBlack) {
//            highlightPiece(true, isKing, pieceImage);
//            if (!isKing) {
//                this.piece = new BlackPiece(x, y, true, false);
//                ((BlackPiece) this.piece).move(board);
//            } else {
//                this.piece = new KingPiece(x, y, true);
//                ((KingPiece) this.piece).move(board);
//            }
//        } else if (!isBlack) {
//            highlightPiece(false, isKing, pieceImage);
//            if (!isKing) {
//
//                this.piece = new RedPiece(x, y, false, false);
//                ((RedPiece) this.piece).move(board);
//
//            } else {
//                this.piece = new KingPiece(x, y, true);
//                ((KingPiece) this.piece).move(board);
//            }
//        }
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


}

