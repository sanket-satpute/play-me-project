package com.sanket_satpute_20.playme.project.enums;

public enum PlayActWhich {
    ROUND,
    SQUARE;

    public static PlayActWhich playActWhichTo(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            return ROUND;
        }
    }
}