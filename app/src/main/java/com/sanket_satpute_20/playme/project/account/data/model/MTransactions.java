package com.sanket_satpute_20.playme.project.account.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MTransactions implements Serializable {

    private String transaction_id;
    private String transaction_date;
    private String transaction_time;
    private long transaction_money;
    private String transaction_status; // failed, pending, success
    private ArrayList<MTransactionTime> transaction_time_list;
    private String msg;
    private String msgTime;
    private String msgDate;

    public MTransactions() {

    }

    public MTransactions(String transaction_id, String transaction_date, String transaction_time, long transaction_money,
                         String transaction_status, ArrayList<MTransactionTime> transaction_time_list, String msg,
                         String msgTime, String msgDate) {
        this.transaction_id = transaction_id;
        this.transaction_date = transaction_date;
        this.transaction_time = transaction_time;
        this.transaction_money = transaction_money;
        this.transaction_status = transaction_status;
        this.transaction_time_list = transaction_time_list;
        this.msg = msg;
        this.msgTime = msgTime;
        this.msgDate = msgDate;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public long getTransaction_money() {
        return transaction_money;
    }

    public void setTransaction_money(long transaction_money) {
        this.transaction_money = transaction_money;
    }

    public String getTransaction_status() {
        return transaction_status;
    }

    public void setTransaction_status(String transaction_status) {
        this.transaction_status = transaction_status;
    }

    public String getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(String transaction_time) {
        this.transaction_time = transaction_time;
    }

    public ArrayList<MTransactionTime> getTransaction_time_list() {
        return transaction_time_list;
    }

    public void setTransaction_time_list(ArrayList<MTransactionTime> transaction_time_list) {
        this.transaction_time_list = transaction_time_list;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }
}
