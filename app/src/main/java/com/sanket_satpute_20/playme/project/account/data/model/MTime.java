package com.sanket_satpute_20.playme.project.account.data.model;

import java.io.Serializable;

public class MTime implements Serializable {

    private String usage_time_id;
    private long time_date_count;
    private long used_time_app;
    private long daily_limit;
    private String daily_limit_status;
    private String daily_limit_date;
    private boolean isDailyRewardCollected;
    private int dailyAdWatchCountLimit;

    public MTime() {
    }

    public MTime(String usage_time_id, long time_date_count, long used_time_app, long daily_limit, String daily_limit_status, String daily_limit_date, boolean isDailyRewardCollected, int dailyAdWatchCountLimit) {
        this.usage_time_id = usage_time_id;
        this.time_date_count = time_date_count;
        this.used_time_app = used_time_app;
        this.daily_limit = daily_limit;
        this.daily_limit_status = daily_limit_status;
        this.daily_limit_date = daily_limit_date;
        this.isDailyRewardCollected = isDailyRewardCollected;
        this.dailyAdWatchCountLimit = dailyAdWatchCountLimit;
    }

    public String getUsage_time_id() {
        return usage_time_id;
    }

    public void setUsage_time_id(String usage_time_id) {
        this.usage_time_id = usage_time_id;
    }

    public long getUsed_time_app() {
        return used_time_app;
    }

    public void setUsed_time_app(long used_time_app) {
        this.used_time_app = used_time_app;
    }

    public long getDaily_limit() {
        return daily_limit;
    }

    public void setDaily_limit(long daily_limit) {
        this.daily_limit = daily_limit;
    }

    public String getDaily_limit_status() {
        return daily_limit_status;
    }

    public void setDaily_limit_status(String daily_limit_status) {
        this.daily_limit_status = daily_limit_status;
    }

    public String getDaily_limit_date() {
        return daily_limit_date;
    }

    public void setDaily_limit_date(String daily_limit_date) {
        this.daily_limit_date = daily_limit_date;
    }

    public long getTime_date_count() {
        return time_date_count;
    }

    public void setTime_date_count(long time_date_count) {
        this.time_date_count = time_date_count;
    }

    public boolean isDailyRewardCollected() {
        return isDailyRewardCollected;
    }

    public void setDailyRewardCollected(boolean dailyRewardCollected) {
        isDailyRewardCollected = dailyRewardCollected;
    }

    public int getDailyAdWatchCountLimit() {
        return dailyAdWatchCountLimit;
    }

    public void setDailyAdWatchCountLimit(int dailyAdWatchCountLimit) {
        this.dailyAdWatchCountLimit = dailyAdWatchCountLimit;
    }
}
