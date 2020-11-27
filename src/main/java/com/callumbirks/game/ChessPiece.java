package com.callumbirks.game;

import java.util.Arrays;
import java.util.Objects;

public class ChessPiece {
    private final Colour colour;
    private final PieceType pieceType;
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

    public int getX() {
        return this.position[0];
    }

    public int getY() {
        return this.position[1];
    }

    public void setPosition(int x, int y) {
        this.position = new int[]{x, y};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return colour == that.colour &&
                pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(colour, pieceType);
    }
}
