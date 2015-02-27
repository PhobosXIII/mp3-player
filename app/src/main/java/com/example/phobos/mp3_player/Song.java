package com.example.phobos.mp3_player;

public class Song {
    private String url;
    private String fileName;
    private String title;

    public Song(String url) {
        this.url = url;
    }

    public Song(String url, String fileName, String title) {
        this.url = url;
        this.fileName = fileName;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;

        return !(url != null ? !url.equals(song.url) : song.url != null);

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
