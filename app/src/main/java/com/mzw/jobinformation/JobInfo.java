package com.mzw.jobinformation;


public class JobInfo {

    private String id;
    private String date;
    private String title;
    private String corporation;
    private boolean liked = false;

    protected JobInfo(String id, String date, String title, String corporation) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.corporation = corporation;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getCorporation() {
        return corporation;
    }

    public void setLiked(boolean b) {
        liked = b;
    }
}
