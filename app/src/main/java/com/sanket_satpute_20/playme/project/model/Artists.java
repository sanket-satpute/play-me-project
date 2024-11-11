package com.sanket_satpute_20.playme.project.model;

import java.io.Serializable;
import java.util.ArrayList;


public class Artists implements Serializable {

    protected String artistName;
    protected ArrayList<MusicFiles> artistFiles;
    protected byte[] artistHomePic;
    protected int timeSize;

    public Artists(String artist, ArrayList<MusicFiles> es) {
        this.artistName = artist;
        this.artistFiles = es;
    }

    public String getArtistName() {
        return artistName;
    }

    public ArrayList<MusicFiles> getArtistFiles() {
        return artistFiles;
    }


    public byte[] getArtistHomePic() {
        return artistHomePic;
    }

    public int getTimeSize() {
        for (MusicFiles f: artistFiles) {
            timeSize += Integer.parseInt(f.getDuration());
        }
        return timeSize;
    }
}
