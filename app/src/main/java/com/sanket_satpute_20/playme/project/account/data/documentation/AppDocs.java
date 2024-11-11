package com.sanket_satpute_20.playme.project.account.data.documentation;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class AppDocs {

    private String title;
    private ArrayList<Integer> pictures;
    private String helper_str;
    private ArrayList<ArrayList<AppDocsComments>> sideByPictureThatInfo;

    public AppDocs() {}

    public AppDocs(String title, ArrayList<Integer> pictures, String helper_str, ArrayList<ArrayList<AppDocsComments>> sideByPictureThatInfo) {
        this.title = title;
        this.pictures = pictures;
        this.helper_str = helper_str;
        this.sideByPictureThatInfo = sideByPictureThatInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Integer> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<Integer> pictures) {
        this.pictures = pictures;
    }

    public String getHelper_str() {
        return helper_str;
    }

    public void setHelper_str(String helper_str) {
        this.helper_str = helper_str;
    }

    public ArrayList<ArrayList<AppDocsComments>> getSideByPictureThatInfo() {
        return sideByPictureThatInfo;
    }

    public void setSideByPictureThatInfo(ArrayList<ArrayList<AppDocsComments>> sideByPictureThatInfo) {
        this.sideByPictureThatInfo = sideByPictureThatInfo;
    }
}
