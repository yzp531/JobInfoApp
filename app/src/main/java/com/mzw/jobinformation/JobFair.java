package com.mzw.jobinformation;


public class JobFair {
    private String id;
    private String date;
    private String title;
    private String corporation;
    private String time;
    private String place;
    private boolean liked = false;

    protected JobFair(String id, String date, String title, String corporation, String time, String place) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.corporation = corporation;
        this.time = time;
        this.place = place;
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

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public void setLiked(boolean b) {
        liked = b;
    }
}
