package com.sanket_satpute_20.playme.project.account.data.model;

import java.io.Serializable;

public class UMoney implements Serializable {

    private String money_id;
    private int coins;
    private int money;
    private String moneyDate;
    private String moneyTime;

    public UMoney() {
    }

    public UMoney(String money_id, int coins, int money, String moneyDate, String moneyTime) {
        this.money_id = money_id;
        this.coins = coins;
        this.money = money;
        this.moneyDate = moneyDate;
        this.moneyTime = moneyTime;
    }

    public String getMoney_id() {
        return money_id;
    }

    public void setMoney_id(String money_id) {
        this.money_id = money_id;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getMoneyDate() {
        return moneyDate;
    }

    public void setMoneyDate(String moneyDate) {
        this.moneyDate = moneyDate;
    }

    public String getMoneyTime() {
        return moneyTime;
    }

    public void setMoneyTime(String moneyTime) {
        this.moneyTime = moneyTime;
    }
}
