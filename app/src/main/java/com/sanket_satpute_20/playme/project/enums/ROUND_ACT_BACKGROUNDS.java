package com.sanket_satpute_20.playme.project.enums;

public enum ROUND_ACT_BACKGROUNDS {
    DEFAULT,
    COLORED,
    GRADIENT,
    BLUR;

    public static ROUND_ACT_BACKGROUNDS shakeWhichTo(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            return DEFAULT;
        }
    }
}
