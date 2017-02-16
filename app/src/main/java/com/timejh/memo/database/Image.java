package com.timejh.memo.database;

/**
 * Created by tokijh on 2017. 2. 14..
 */

public class Image {

    public static final String TABLE_NAME = "imagedata";
    public static final int COLUM_COUNT = 2;

    private long id;
    private long memo_id;
    private long image_id;

    private String uri;

    public Image(String uri) {
        this.uri = uri;
    }

    public Image(long id, long memo_id, long image_id, String uri) {
        this.id = id;
        this.memo_id = memo_id;
        this.image_id = image_id;
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMemo_id() {
        return memo_id;
    }

    public void setMemo_id(long memo_id) {
        this.memo_id = memo_id;
    }

    public long getImage_id() {
        return image_id;
    }

    public void setImage_id(long image_id) {
        this.image_id = image_id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
