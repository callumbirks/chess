package com.callumbirks.game;

public class ChessPiece {
    private Colour colour;
    private PieceType pieceType;
    private int[] position;

    public ChessPiece(int x, int y) {
        this.colour = null;
        this.pieceType = null;
        this.position = new int[] { x, y };
    }

    public ChessPiece(Colour colour, PieceType pieceType, int x, int y) {
        this.colour = colour;
        this.pieceType = pieceType;
        this.position = new int[] { x, y };
    }

    public Colour getColour() {
        return colour;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public boolean isValidMove(int[] newPosition, boolean takesPiece) {
        int xDiff = xDiff(newPosition);
        int yDiff = yDiff(newPosition);
        if(colour == Colour.BLACK)
            yDiff *= -1;
        switch (pieceType) {
            case PAWN -> {
                if (takesPiece) {
                    return Math.abs(xDiff) == 1 && (yDiff) == 1;
                } else if (hasPawnMoved()) {
                    return xDiff == 0 && (yDiff) == 1;
                } else {
                    return xDiff == 0 && (yDiff == 1 || yDiff == 2);
                }
            }
            case KNIGHT -> {

            }
        }
        return false;
    }

    private int xDiff(int[] newPosition) {
        return newPosition[0] - position[0];
    }

    private int yDiff(int[] newPosition) {
        return newPosition[1] - position[1];
    }

    private boolean hasPawnMoved() {
        return switch (colour) {
            case WHITE -> position[1] != 6;
            case BLACK -> position[1] != 1;
        };
    }

    public int getX() {
        return this.position[0];
    }

    public int getY() {
        return this.position[1];
    }

    public void setPosition (int x, int y){
        this.position = new int[] { x, y };
    }
}
