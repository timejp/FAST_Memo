package com.timejh.memo.database;

/**
 * Created by tokijh on 2017. 2. 14..
 */

public class Memo {

    public static final String TABLE_NAME = "memo";
    public static final int COLUM_COUNT = 4;

    private long id;
    private String title;
    private String content;
    private String date;
    private Image[] images;

    public Memo(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Memo(String title, String content, String date, Image[] images) {
        this.title = title;
        this.content = content;
        this.images = images;
        this.date = date;
    }

    public Memo(long id, String title, String content, String date, Image[] images) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.images = images;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
