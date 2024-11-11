package com.sanket_satpute_20.playme.project.model;

import java.io.Serializable;

public class MusicFiles implements Serializable {
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String id;
    private int recent_play_day_count;
    private int most_played_day_count;
    private int song_most_played_count;
    private boolean isSelected;

    public MusicFiles() {}

    public MusicFiles(String path, String title, String artist, String album, String duration, String id) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSong_most_played_count() {
        return song_most_played_count;
    }

    public void setSong_most_played_count(int song_most_played_count) {
        this.song_most_played_count = song_most_played_count;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public int getRecent_play_day_count() {
        return recent_play_day_count;
    }

    public void setRecent_play_day_count(int recent_play_day_count) {
        this.recent_play_day_count = recent_play_day_count;
    }

    public int getMost_played_day_count() {
        return most_played_day_count;
    }

    public void setMost_played_day_count(int most_played_day_count) {
        this.most_played_day_count = most_played_day_count;
    }
}
