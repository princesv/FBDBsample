package com.prince.fbdbsample2;

public class Artist {
    String artistName;
    String artistGenere;
    String artistId;
    public Artist(){

    }

    public Artist(String artistId,String artistName,String artistGenere) {
        this.artistName = artistName;
        this.artistGenere=artistGenere;
        this.artistId=artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistGenere() {
        return artistGenere;
    }

    public String getArtistId() {
        return artistId;
    }
}
