package com.example.piyush.rhythm;

/**
 * Created by piyush on 22/4/18.
 */

public class Songinfo {
    public String songname,artistname,songurl;
    public Songinfo()
    {

    }
    public Songinfo(String songname,String artistname,String songurl)
    {
        this.songname=songname;
        this.artistname=artistname;
        this.songurl=songurl;
    }

    public String getArtistname() {
        return artistname;
    }

    public String getSongname() {
        return songname;
    }

    public String getSongurl() {
        return songurl;
    }
}
