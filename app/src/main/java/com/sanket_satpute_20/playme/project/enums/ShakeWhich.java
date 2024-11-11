package com.sanket_satpute_20.playme.project.enums;

public enum ShakeWhich {
    NEXTPLAYNORMAL,
    OPTIONPLAY;

    public static ShakeWhich shakeWhichTo(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            return NEXTPLAYNORMAL;
        }
    }
}