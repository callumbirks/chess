package com.callumbirks.game;

import javafx.animation.Timeline;

import java.util.ArrayList;
import java.util.List;

import static com.callumbirks.game.Colour.*;
import static com.callumbirks.game.PieceType.*;

public class Game {
    private ChessPiece[][] pieces;
    private Timeline timer;
    private GameSettings settings;

    public Game() {
        pieces = new ChessPiece[8][8];
        settings = new GameSettings();
        initialisePieces();
    }

    private void initialisePieces() {
        initBackRow(BLACK);
        initPawns(BLACK);
        initBackRow(WHITE);
        initPawns(WHITE);
        initEmpty();
    }

    private void initEmpty() {
        for(int i = 2; i < 6; i++) {
            for(int j = 0; j < 8; j++) {
                pieces[i][j] = new ChessPiece(EMPTY, i, j);
            }
        }
    }

    private void initBackRow(Colour colour) {
        int row = switch(colour) {
            case BLACK -> 0;
            case WHITE -> 7;
        };
        pieces[row][0] = new ChessPiece(colour,ROOK, row, 0);
        pieces[row][1] = new ChessPiece(colour,KNIGHT, row, 1);
        pieces[row][2] = new ChessPiece(colour,BISHOP, row, 2);
        pieces[row][3] = new ChessPiece(colour,QUEEN, row, 3);
        pieces[row][4] = new ChessPiece(colour,KING, row, 4);
        pieces[row][5] = new ChessPiece(colour,BISHOP, row, 5);
        pieces[row][6] = new ChessPiece(colour,KNIGHT, row, 6);
        pieces[row][7] = new ChessPiece(colour,ROOK, row, 7);
    }

    private void initPawns(Colour colour) {
        int row = switch(colour) {
            case BLACK -> 1;
            case WHITE -> 6;
        };
        for(int i = 0; i < 8; i++) {
            pieces[row][i] = new ChessPiece(colour,PAWN, row, i);
        }
    }

    public ChessPiece[][] getPieces() {
        return pieces;
    }

    public void setPieces(ChessPiece[][] pieces) {
        this.pieces = pieces;
    }

    public ChessPiece getPiece(int x, int y) {
        return pieces[x][y];
    }

    public boolean isTimerEnabled() {
        return settings.isTimerEnabled();
    }

    public void setTimerEnabled(boolean b) {
        settings.setTimerEnabled(b);
    }

    public void movePiece(ChessPiece piece, int[] newPos) {
        ChessPiece newPiece = new ChessPiece(piece.getColour(), piece.getPieceType(), newPos[0], newPos[1]);
        if(pieces[newPos[0]][newPos[1]].getPieceType() == EMPTY) {
            pieces[piece.getX()][piece.getY()] = pieces[newPos[0]][newPos[1]];
            pieces[piece.getX()][piece.getY()].setPosition(piece.getX(), piece.getY());
        }
        else {
            pieces[piece.getX()][piece.getY()] = new ChessPiece(EMPTY, piece.getX(), piece.getY());
        }
        pieces[newPos[0]][newPos[1]] = newPiece;
    }

    public List<ChessPiece> getMoves(ChessPiece piece) {
        List<ChessPiece> validMoves = new ArrayList<>();
        for(ChessPiece[] file : pieces) {
            for(ChessPiece square : file) {
                int[] newPos = { square.getX(), square.getY() };
                if(square.getPieceType() == EMPTY) {
                    if(piece.isValidMove(square, false, pieces))
                        validMoves.add(pieces[newPos[0]][newPos[1]]);
                }
                else if(square.getColour() != piece.getColour()) {
                    if(piece.isValidMove(square, true, pieces))
                        validMoves.add(pieces[newPos[0]][newPos[1]]);
                }
            }
        }
        return validMoves;
    }
}
