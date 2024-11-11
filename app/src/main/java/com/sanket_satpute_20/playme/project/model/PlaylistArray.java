package com.sanket_satpute_20.playme.project.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaylistArray  implements Serializable {

    public static ArrayList<Playlist> ref = new ArrayList<>();

    public PlaylistArray(ArrayList<Playlist> ref) {
        PlaylistArray.ref = ref;
    }

    public PlaylistArray() {}

    public ArrayList<Playlist> getRef() {
        return ref;
    }

    public void setRef(ArrayList<Playlist> ref) {
        PlaylistArray.ref = ref;
    }

    public void setRef(Playlist ref) {
        PlaylistArray.ref.add(ref);
    }
}
