package com.sanket_satpute_20.playme.project.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {

    public String playlist_name;
    public ArrayList<MusicFiles> playlist;
    public String createdBy;
    public String createdOn;

    public Playlist() {}

    public Playlist(String playlist_name, ArrayList<MusicFiles> playlist, String createdBy, String createdOn) {
        this.playlist_name = playlist_name;
        this.playlist = playlist;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public ArrayList<MusicFiles> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ArrayList<MusicFiles> playlist) {
        this.playlist = playlist;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
