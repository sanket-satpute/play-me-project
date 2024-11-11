package com.sanket_satpute_20.playme.project.model;

public class ChangeLogApp {

    String date;
    String version_name;
    String []change_log_futures;

    public ChangeLogApp(String date, String version_name, String[] change_log_futures) {
        this.date = date;
        this.version_name = version_name;
        this.change_log_futures = change_log_futures;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String[] getChange_log_futures() {
        return change_log_futures;
    }

    public void setChange_log_futures(String[] change_log_futures) {
        this.change_log_futures = change_log_futures;
    }
}
