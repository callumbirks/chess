package com.callumbirks.game;

import java.util.Arrays;

public class ChessPiece {
    private Colour colour;
    private PieceType pieceType;
    private int[] position;

    public ChessPiece(PieceType pieceType, int x, int y) {
        this.colour = null;
        this.pieceType = pieceType;
        this.position = new int[]{x, y};
    }

    public ChessPiece(Colour colour, PieceType pieceType, int x, int y) {
        this.colour = colour;
        this.pieceType = pieceType;
        this.position = new int[]{x, y};
    }

    public Colour getColour() {
        return colour;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public boolean isValidMove(ChessPiece newPos, boolean takesPiece, ChessPiece[][] pieces) {
        int xDiff = xDiff(newPos.getX());
        int yDiff = yDiff(newPos.getY());
        if (colour == Colour.WHITE)
            xDiff *= -1;
        final boolean isStraightLine = (Math.abs(xDiff) > 0 && Math.abs(yDiff) == 0) ||
                (Math.abs(xDiff) == 0 && Math.abs(yDiff) > 0);
        final boolean isDiagonal = Math.abs(xDiff) == Math.abs(yDiff);
        switch (pieceType) {
            case PAWN -> {
                if (takesPiece) {
                    return Math.abs(yDiff) == 1 && xDiff == 1;
                } else if (hasPawnMoved()) {
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
                return isDiagonal && !pieceBlocking(newPos, pieces, this.pieceType);
            }
            case ROOK -> {
                return isStraightLine && !pieceBlocking(newPos, pieces, this.pieceType);
            }
            case QUEEN -> {
                return (isDiagonal && !pieceBlocking(newPos, pieces, PieceType.BISHOP) ||
                        (isStraightLine && !pieceBlocking(newPos, pieces, PieceType.ROOK)));
            }
            case KING -> {
                return (Math.abs(xDiff) <= 1 && Math.abs(yDiff) <= 1);
            }
        }
        return false;
    }

    //TODO: Fix being blocked with opposite colour pieces
    private boolean pieceBlocking(ChessPiece newPos, ChessPiece[][] pieces, PieceType pieceType) {
        ChessPiece blockingPiece = null;
        for (ChessPiece[] file : pieces) {
            for (ChessPiece piece : file) {
                switch (pieceType) {
                    case BISHOP -> {
                        if (Math.abs(xDiff(piece.getX())) == Math.abs(yDiff(piece.getY()))) {
                            blockingPiece = (piece.getPieceType() != PieceType.EMPTY ? piece : blockingPiece);
                            if (blockingPiece != null && isBlocked(newPos, blockingPiece, pieceType)) return true;
                        }
                    }
                    case ROOK -> {
                        if ((Math.abs(xDiff(piece.getX())) == 0 && Math.abs(yDiff(piece.getY())) > 0) ||
                                (Math.abs(xDiff(piece.getX())) > 0 && Math.abs(yDiff(piece.getY())) == 0)) {
                            blockingPiece = (piece.getPieceType() != PieceType.EMPTY ? piece : blockingPiece);
                            if (blockingPiece != null && isBlocked(newPos, blockingPiece, pieceType)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isBlocked(ChessPiece newPos, ChessPiece blockingPiece, PieceType pieceType) {
        int xDiff = xDiff(newPos.getX());
        int yDiff = yDiff(newPos.getY());
        if (Math.signum(xDiff) == Math.signum(xDiff(blockingPiece.getX())) &&
                Math.signum(yDiff) == Math.signum(yDiff(blockingPiece.getY()))) {
            return Math.abs(xDiff) >= Math.abs(xDiff(blockingPiece.getX())) &&
                    Math.abs(yDiff) >= Math.abs(yDiff(blockingPiece.getY()));
//            if (blockingPiece.getColour() == this.getColour())
//                return Math.abs(xDiff) >= Math.abs(xDiff(blockingPiece.getX())) &&
//                        Math.abs(yDiff) >= Math.abs(yDiff(blockingPiece.getY()));
//            else
//                return Math.abs(xDiff) > Math.abs(xDiff(blockingPiece.getX())) &&
//                        Math.abs(yDiff) > Math.abs(yDiff(blockingPiece.getY()));
        }
        return false;
    }

    private int xDiff(int x) {
        return x - position[0];
    }

    private int yDiff(int y) {
        return y - position[1];
    }

    private boolean hasPawnMoved() {
        return switch (colour) {
            case WHITE -> position[0] != 6;
            case BLACK -> position[0] != 1;
        };
    }

    public int getX() {
        return this.position[0];
    }

    public int getY() {
        return this.position[1];
    }

    public void setPosition(int x, int y) {
        this.position = new int[]{x, y};
    }
}
