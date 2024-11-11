package com.sanket_satpute_20.playme.project.account.enums;

public enum CurrencyType {
    USD, INR;

    public static CurrencyType myCurrencyTo(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            return INR;
        }
    }
}
