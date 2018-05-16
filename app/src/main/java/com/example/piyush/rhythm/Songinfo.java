package com.example.piyush.rhythm;

import android.provider.MediaStore;

/**
 * Created by piyush on 22/4/18.
 */

public class Songinfo {
    private String Songname,Artistname,Songurl;
    public Songinfo()
    {

    }

    public Songinfo(String songname,String artistname,String songurl)
    {
        Songname=songname;
        Artistname=artistname;
        Songurl=songurl;
    }

    public String getSongname() {
        return Songname;
    }
    public void setSongname(String songname) {
       Songname=songname;
    }

    public String getArtistname() {
        return Artistname;
    }
    public void setArtistname(String artistname) {
        Artistname=artistname;
    }
    public String getSongurl() {
        return Songurl;
    }
    public void setSongurl(String songurl) {
        Songurl=songurl;
    }
}
