package com.sanket_satpute_20.playme.project.account.data.model;

import java.io.Serializable;

public class MTransactionTime implements Serializable {
    private String transaction_status_name;
    private String transaction_status_time;
    private String transaction_status_date;
    private String transaction_status; // processing , pending, success, failed

    public MTransactionTime(String transaction_status_name, String transaction_status_time, String transaction_status_date, String transaction_status) {
        this.transaction_status_name = transaction_status_name;
        this.transaction_status_time = transaction_status_time;
        this.transaction_status_date = transaction_status_date;
        this.transaction_status = transaction_status;
    }

    public MTransactionTime() {}

    public String getTransaction_status_name() {
        return transaction_status_name;
    }

    public void setTransaction_status_name(String transaction_status_name) {
        this.transaction_status_name = transaction_status_name;
    }

    public String getTransaction_status_time() {
        return transaction_status_time;
    }

    public void setTransaction_status_time(String transaction_status_time) {
        this.transaction_status_time = transaction_status_time;
    }

    public String getTransaction_status_date() {
        return transaction_status_date;
    }

    public void setTransaction_status_date(String transaction_status_date) {
        this.transaction_status_date = transaction_status_date;
    }

    public String getTransaction_status() {
        return transaction_status;
    }

    public void setTransaction_status(String transaction_status) {
        this.transaction_status = transaction_status;
    }
}
