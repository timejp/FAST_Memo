package com.timejh.memo.database;

import android.content.Context;

import com.timejh.memo.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokijh on 2017. 2. 14..
 */

public class MemoDataBaseManager {

    public static final String TAG = "DataBaseManager";

    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 1;

    private Context context;

    private DataBaseHelper dbHelper;

    public MemoDataBaseManager(Context context) {
        this.context = context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void insert(Memo data) throws SQLException {
        dbHelper.writableQuery("INSERT INTO `" + Memo.TABLE_NAME + "` (`title`, `content`, `date`) VALUES ('" + data.getTitle() + "','" + data.getContent() + "','" + data.getDate() + "');");

        long memo_id = getLastID(Memo.TABLE_NAME);
        insertImage(data.getImages(), memo_id);
    }

    public long getLastID(String TABLENAME) {
        StringBuffer query = new StringBuffer();
        query.append("SELECT `id` FROM `" + TABLENAME + "` ORDER BY `id` DESC limit 1");
        String data = dbHelper.readableQuery(query.toString(), 1);
        Logger.d(TAG, data);
        String num = data.split("--next--")[0].split("\\|")[0];
        return Long.parseLong(num);
    }

    public List<Memo> select() throws SQLException {
        return stringToData(dbHelper.readableQuery("SELECT * FROM `" + Memo.TABLE_NAME + "`;", Memo.COLUM_COUNT));
    }

    public void delete(Memo data) throws SQLException {
        dbHelper.writableQuery("DELETE FROM `" + Memo.TABLE_NAME + "` WHERE `id` = '" + data.getId() + "';");

        deleteAllImage(data.getId());
    }

    public void update(Memo data) throws SQLException {
        dbHelper.writableQuery("UPDATE `" + Memo.TABLE_NAME + "` SET `title` = '" + data.getTitle() + "', `content` = '" + data.getContent() + "', `date` = '" + data.getDate() + "' WHERE `id` = '" + data.getId() + "';");

        updateImage(data.getImages(), data.getId());
    }

    private List<Memo> stringToData(String data) {
        List<Memo> datas = new ArrayList<>();
        String[] splits = data.split("--next--");
        for (String items : splits) {
            String[] item = items.split("\\|");
            Image[] images = null;
            if (!item[0].equals("null") && !item[0].equals("")) {
                images = selectImage(Long.parseLong(item[0]));
                datas.add(new Memo(Long.parseLong(item[0]), item[1], item[2], item[3], images));
            }
        }
        return datas;
    }

    private Image[] selectImage(long id) {
        String data = dbHelper.readableQuery("SELECT `id`, `image_id` FROM `imagematch` WHERE `memo_id` = '" + id + "';", 2);
        List<Image> datas = new ArrayList<>();
        String[] splits = data.split("--next--");
        for (String items : splits) {
            String[] item = items.split("\\|");
            if (item.length > 1 && !item[1].equals("null") && !item[1].equals("")) {
                Logger.d(TAG, "selectImage : imageID : " + item[1]);
                String data2 = dbHelper.readableQuery("SELECT `image` FROM `imagedata` WHERE `id` = '" + item[1] + "';", 1);
                String[] data2splits = data2.split("--next--");
                for (String items2 : data2splits) {
                    String imageUri = items2.split("\\|")[0];
                    datas.add(new Image(Long.parseLong(item[0]), id, Long.parseLong(item[1]), imageUri));
                }
            }
        }
        if (datas.size() > 0) {
            Image[] images = new Image[datas.size()];
            datas.toArray(images);
            return images;
        } else
            return null;
    }

    private void insertImage(Image[] images, long id) {
        if (images != null)
            for (Image image : images) {
                insertImage(image, id);
            }
    }

    private void insertImage(Image image, long id) {
        long inid = -1;
        String imageid = dbHelper.readableQuery("SELECT id FROM `" + Image.TABLE_NAME + "` WHERE `image` = '" + image.getUri() + "';", 1);
        Logger.d(TAG + " ImageID ", imageid);
        String[] splits = imageid.split("--next--");
        for (String items : splits) {
            String[] item = items.split("\\|");
            if (item.length > 0 && !item[0].equals("null") && !item[0].equals(""))
                inid = Long.parseLong(item[0]);
        }

        if (inid == -1) { //기존에 사진이 있는지 없는지 검사후 -1이면 없음으로 저장
            dbHelper.writableQuery("INSERT INTO `" + Image.TABLE_NAME + "` (`image`) VALUES ('" + image.getUri() + "');");
            inid = getLastID("imagedata"); //저장한 Image ID가져오기
        }
        Logger.d(TAG, "inid : " + inid);

        //중복검사
        String duple = dbHelper.readableQuery("SELECT id FROM `imagematch` WHERE `memo_id` = '" + id + "' AND `image_id` = '" + inid + "';", 1);
        splits = duple.split("--next--");
        for (String items : splits) {
            String[] item = items.split("\\|");
            if (item.length > 0 && !item[0].equals("null") && !item[0].equals(""))
                return;//중복되는 값이 있으니 종료
        }

        dbHelper.writableQuery("INSERT INTO `imagematch` (`memo_id`, `image_id`) VALUES ('" + id + "', '" + inid + "');");
    }

    private void updateImage(Image[] images, long id) {
        deleteAllImage(id);
        if (images != null)
            for (Image image : images) {
                insertImage(image, id);
            }
    }

    private void deleteAllImage(long id) {
        dbHelper.writableQuery("DELETE FROM `imagematch` WHERE `memo_id` = '" + id + "';");
    }

    private void deleteImage(long image_id) {
        dbHelper.writableQuery("DELETE FROM `imagematch` WHERE `id` = '" + image_id + "';");
    }
}
