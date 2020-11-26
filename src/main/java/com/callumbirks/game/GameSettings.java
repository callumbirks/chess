package com.callumbirks.game;

public class GameSettings {
    private boolean timerEnabled;

    public GameSettings() {
        timerEnabled = false;
    }

    public boolean isTimerEnabled() {
        return timerEnabled;
    }

    public void setTimerEnabled(boolean timerEnabled) {
        this.timerEnabled = timerEnabled;
    }
}
