package com.sanket_satpute_20.playme.project.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Albums implements Serializable {

    String album_name;
    ArrayList<MusicFiles> album_files;
    boolean isExpanded , isPlaying;

    public Albums(String album_name, ArrayList<MusicFiles> album_files, boolean isExpanded, boolean isPlaying) {
        this.album_name = album_name;
        this.album_files = album_files;
        this.isExpanded = isExpanded;
        this.isPlaying = isPlaying;
    }

    public Albums() {    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public ArrayList<MusicFiles> getAlbum_files() {
        return album_files;
    }

    public void setAlbum_files(ArrayList<MusicFiles> album_files) {
        this.album_files = album_files;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        isExpanded = isPlaying;
    }
}
