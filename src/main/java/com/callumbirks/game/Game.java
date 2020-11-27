package com.callumbirks.game;

import java.util.ArrayList;
import java.util.List;

import static com.callumbirks.game.Colour.*;
import static com.callumbirks.game.PieceType.*;

public class Game {
    private ChessPiece[][] pieces;
    private GameSettings settings;
    private boolean started;
    private boolean inCheck;
    private List<ChessPiece> checkingPieces;

    public Game() {
        pieces = new ChessPiece[8][8];
        settings = new GameSettings();
        initialisePieces();
        started = false;
        checkingPieces = new ArrayList<>();
    }

    public boolean isInCheck() {
        return inCheck;
    }

    public void setInCheck(boolean inCheck) {
        this.inCheck = inCheck;
    }

    public ChessPiece[][] getPieces() {
        return pieces;
    }

    public ChessPiece getPiece(int x, int y) {
        return pieces[x][y];
    }

    public boolean isStarted() {
        return started;
    }

    public void startGame() {
        started = true;
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
        getMoves(newPiece);
    }

    public List<ChessPiece> getMoves(ChessPiece piece) {
        List<ChessPiece> validMoves = new ArrayList<>();
        for(ChessPiece[] file : pieces) {
            for(ChessPiece square : file) {
                int[] newPos = { square.getX(), square.getY() };
                if(square.getPieceType() == EMPTY) {
                    if(isValidMove(piece, square, false))
                        validMoves.add(pieces[newPos[0]][newPos[1]]);
                }
                else if(square.getColour() != piece.getColour()) {
                    if(isValidMove(piece, square, true))
                        validMoves.add(pieces[newPos[0]][newPos[1]]);
                }
            }
        }
        ChessPiece whiteKing = new ChessPiece(WHITE, KING, 0, 0);
        ChessPiece blackKing = new ChessPiece(BLACK, KING, 0, 0);
        for(ChessPiece move : validMoves) {
            if(move.equals(whiteKing) || move.equals(blackKing)) {
                setInCheck(true);
                checkingPieces.add(piece);
            }
        }
        return validMoves;
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

    private boolean isValidMove(ChessPiece current, ChessPiece newPos, boolean takesPiece) {
        int xDiff = xDiff(newPos.getX(), current);
        int yDiff = yDiff(newPos.getY(), current);
        if (current.getColour() == Colour.WHITE)
            xDiff *= -1;
        final boolean isStraightLine = (Math.abs(xDiff) > 0 && Math.abs(yDiff) == 0) ||
                (Math.abs(xDiff) == 0 && Math.abs(yDiff) > 0);
        final boolean isDiagonal = Math.abs(xDiff) == Math.abs(yDiff);
        switch (current.getPieceType()) {
            case PAWN -> {
                if (takesPiece) {
                    return Math.abs(yDiff) == 1 && xDiff == 1;
                } else if (hasPawnMoved(current)) {
                    return yDiff == 0 && xDiff == 1;
                } else {
                    return yDiff == 0 && (xDiff == 1 || xDiff == 2);
                }
            }
            case KNIGHT -> {
                return (Math.abs(xDiff) == 2 && Math.abs(yDiff) == 1) ||
                        (Math.abs(xDiff) == 1 && Math.abs(yDiff) == 2);
            }
            case BISHOP -> {
                return isDiagonal && pieceNotBlocked(current, newPos, pieces, current.getPieceType());
            }
            case ROOK -> {
                return isStraightLine && pieceNotBlocked(current, newPos, pieces, current.getPieceType());
            }
            case QUEEN -> {
                return (isDiagonal && pieceNotBlocked(current, newPos, pieces, PieceType.BISHOP) ||
                        (isStraightLine && pieceNotBlocked(current, newPos, pieces, PieceType.ROOK)));
            }
            case KING -> {
                return (Math.abs(xDiff) <= 1 && Math.abs(yDiff) <= 1);
            }
        }
        return false;
    }

    private boolean pieceNotBlocked(ChessPiece current, ChessPiece newPos, ChessPiece[][] pieces, PieceType pieceType) {
        ChessPiece blockingPiece = null;
        for (ChessPiece[] file : pieces) {
            for (ChessPiece piece : file) {
                switch (pieceType) {
                    case BISHOP -> {
                        if (Math.abs(xDiff(piece.getX(), current)) == Math.abs(yDiff(piece.getY(), current))) {
                            blockingPiece = (piece.getPieceType() != PieceType.EMPTY ? piece : blockingPiece);
                            if (blockingPiece != null && isBlocked(current, newPos, blockingPiece)) return false;
                        }
                    }
                    case ROOK -> {
                        if ((Math.abs(xDiff(piece.getX(), current)) == 0 && Math.abs(yDiff(piece.getY(), current)) > 0) ||
                                (Math.abs(xDiff(piece.getX(), current)) > 0 && Math.abs(yDiff(piece.getY(), current)) == 0)) {
                            blockingPiece = (piece.getPieceType() != PieceType.EMPTY ? piece : blockingPiece);
                            if (blockingPiece != null && isBlocked(current, newPos, blockingPiece)) return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isBlocked(ChessPiece current, ChessPiece newPos, ChessPiece blockingPiece) {
        int xDiff = xDiff(newPos.getX(), current);
        int yDiff = yDiff(newPos.getY(), current);
        final float blockingPieceXDirection = Math.signum(xDiff(blockingPiece.getX(), current));
        final float blockingPieceYDirection = Math.signum(yDiff(blockingPiece.getY(), current));

        if (Math.signum(xDiff) == blockingPieceXDirection &&
                Math.signum(yDiff) == blockingPieceYDirection) {
            final int xDistanceToBlockingPiece = Math.abs(xDiff(blockingPiece.getX(), current));
            final int yDistanceToBlockingPiece = Math.abs(yDiff(blockingPiece.getY(), current));
            if(current.getColour() != blockingPiece.getColour()) {
                return (Math.abs(xDiff) > xDistanceToBlockingPiece &&
                        Math.abs(yDiff) > yDistanceToBlockingPiece) ||
                        (Math.abs(xDiff) >= xDistanceToBlockingPiece &&
                                Math.abs(yDiff) > yDistanceToBlockingPiece) ||
                        (Math.abs(xDiff) > xDistanceToBlockingPiece &&
                                Math.abs(yDiff) >= yDistanceToBlockingPiece);
            }
            else return Math.abs(xDiff) >= xDistanceToBlockingPiece &&
                    Math.abs(yDiff) >= yDistanceToBlockingPiece;
        }
        return false;
    }

    private int xDiff(int x, ChessPiece current) {
        return x - current.getX();
    }

    private int yDiff(int y, ChessPiece current) {
        return y - current.getY();
    }

    private boolean hasPawnMoved(ChessPiece piece) {
        return switch (piece.getColour()) {
            case WHITE -> piece.getX() != 6;
            case BLACK -> piece.getX() != 1;
        };
    }
}
