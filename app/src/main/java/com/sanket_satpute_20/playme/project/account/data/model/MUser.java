package com.sanket_satpute_20.playme.project.account.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MUser implements Serializable {

    private String first_name; // **
    private String last_name; // **
    private String full_name;   // **
    private long user_count_no; // **
    private String m_user_id; // **
    private String user_name; // by user
    private String email_id; // **
    private String accountCreationDate;
    private String accountCreationTime;
    private String upiID;
    private long totalMoney;
    private long totalCoins;
    private long totalEarnedMoneyFromPlayMe;
    private String profilePicturePath;
    private boolean isActive;
    private boolean isDeactivated;
    private long withdrawingMoney;
    private boolean isNotifyMsgAvailable;
    private String birthDate;

    private long phone_number; // **
    private byte[] picture; // (optional)
    private String password; // by user

    private ArrayList<UMoney> uMoney;
    private ArrayList<MTransactions> mTransactions;
    private ArrayList<MTime> mTime;

    public MUser(String first_name, String last_name, String full_name,
                 long user_count_no, String m_user_id, String user_name, String email_id,
                 long phone_number, byte[] picture, String password, ArrayList<UMoney> uMoney, ArrayList<MTransactions> mTransactions,
                 ArrayList<MTime> mTime, String accountCreationDate, String accountCreationTime, String upiID, long totalMoney, long totalCoins,
                 long totalEarnedMoneyFromPlayMe, String profilePicturePath, boolean isActive, boolean isDeactivated, long withdrawingMoney,
                 boolean isNotifyMsgAvailable, String birthDate) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.full_name = full_name;
        this.user_count_no = user_count_no;
        this.m_user_id = m_user_id;
        this.user_name = user_name;
        this.email_id = email_id;
        this.phone_number = phone_number;
        this.picture = picture;
        this.password = password;
        this.uMoney = uMoney;
        this.mTransactions = mTransactions;
        this.mTime = mTime;
        this.accountCreationDate = accountCreationDate;
        this.accountCreationTime = accountCreationTime;
        this.upiID = upiID;
        this.totalMoney = totalMoney;
        this.totalCoins = totalCoins;
        this.totalEarnedMoneyFromPlayMe = totalEarnedMoneyFromPlayMe;
        this.profilePicturePath = profilePicturePath;
        this.isActive = isActive;
        this.isDeactivated = isDeactivated;
        this.withdrawingMoney = withdrawingMoney;
        this.isNotifyMsgAvailable = isNotifyMsgAvailable;
        this.birthDate = birthDate;
    }

    public MUser() {
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public long getUser_count_no() {
        return user_count_no;
    }

    public void setUser_count_no(long user_count_no) {
        this.user_count_no = user_count_no;
    }

    public String getM_user_id() {
        return m_user_id;
    }

    public void setM_user_id(String m_user_id) {
        this.m_user_id = m_user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getAccountCreationDate() {
        return accountCreationDate;
    }

    public void setAccountCreationDate(String accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<UMoney> getuMoney() {
        return uMoney;
    }

    public void setuMoney(ArrayList<UMoney> uMoney) {
        this.uMoney = uMoney;
    }

    public ArrayList<MTransactions> getmTransactions() {
        return mTransactions;
    }

    public void setmTransactions(ArrayList<MTransactions> mTransactions) {
        this.mTransactions = mTransactions;
    }

    public ArrayList<MTime> getmTime() {
        return mTime;
    }

    public void setmTime(ArrayList<MTime> mTime) {
        this.mTime = mTime;
    }

    public String getUpiID() {
        return upiID;
    }

    public void setUpiID(String upiID) {
        this.upiID = upiID;
    }

    public String getAccountCreationTime() {
        return accountCreationTime;
    }

    public void setAccountCreationTime(String accountCreationTime) {
        this.accountCreationTime = accountCreationTime;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public long getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(long totalCoins) {
        this.totalCoins = totalCoins;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isDeactivated() {
        return isDeactivated;
    }

    public void setDeactivated(boolean deactivated) {
        isDeactivated = deactivated;
    }

    public long getTotalEarnedMoneyFromPlayMe() {
        return totalEarnedMoneyFromPlayMe;
    }

    public void setTotalEarnedMoneyFromPlayMe(long totalEarnedMoneyFromPlayMe) {
        this.totalEarnedMoneyFromPlayMe = totalEarnedMoneyFromPlayMe;
    }

    public long getWithdrawingMoney() {
        return withdrawingMoney;
    }

    public void setWithdrawingMoney(long withdrawingMoney) {
        this.withdrawingMoney = withdrawingMoney;
    }

    public boolean getIsNotifyMsgAvailable() {
        return isNotifyMsgAvailable;
    }

    public void setIsNotifyMsgAvailable(boolean notifyMsgAvailable) {
        isNotifyMsgAvailable = notifyMsgAvailable;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
