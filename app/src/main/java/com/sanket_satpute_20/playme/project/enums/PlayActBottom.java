package com.sanket_satpute_20.playme.project.enums;

public enum PlayActBottom {
    LIST,
    FRAMES;

    public static PlayActBottom playActBottomTo(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            return FRAMES;
        }
    }
}