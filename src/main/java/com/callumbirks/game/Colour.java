package com.callumbirks.game;

public enum Colour {
    WHITE,
    BLACK;

    @Override
    public String toString() {
        if(this.equals(WHITE))
            return "white";
        else
            return "black";
    }
}
