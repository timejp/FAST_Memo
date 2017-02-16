package com.timejh.memo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.timejh.memo.database.Image;
import com.timejh.memo.database.Memo;
import com.timejh.memo.database.MemoDataBaseManager;

import java.sql.SQLException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final int REQ_PERMISSION = 9999; // 권한 요청코드

    private final int REQ_ADD = 1001;
    private final int REQ_DETAIL = 1002;

    private FloatingActionMenu materialDesignFAM;
    private FloatingActionButton floatingActionButton_add;

    private RecyclerView recyclerView;

    private RecyclerCustomAdapter recyclerCustomAdapter;

    private MemoDataBaseManager memoDataBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    private void init() {
        try {
            memoDataBaseManager = new MemoDataBaseManager(this);
            // 1. Recycler View 가져오기
            recyclerView = (RecyclerView) findViewById(R.id.list);
            // 2. Adapter 설정하기
            recyclerCustomAdapter = new RecyclerCustomAdapter(this, memoDataBaseManager.select());
            // 3. Recycler View Adapter 세팅하기
            recyclerView.setAdapter(recyclerCustomAdapter);
            // 4. Recycler View 메니져 등록하기... (뷰의 모양 결정 : 그리드, 리스트, 비대칭 뷰)
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            goToDetailActivity(position, DetailActivity.NONE);
                            materialDesignFAM.close(true);
                        }

                @Override
                public void onLongItemClick(View view, final int position) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                            ab.setTitle("");
                            ab.setMessage("삭제하면 복구 할 수 없습니다.");
                            ab.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToDetailActivity(position, DetailActivity.EDIT);
                                    materialDesignFAM.close(true);
                                    dialog.dismiss();
                                }
                            });
                            ab.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    delete(recyclerCustomAdapter.get(position));
                                    materialDesignFAM.close(true);

                                    dialog.dismiss();
                                }
                            });
                            ab.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            ab.show();
                        }
                    })
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton_add = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_add);

        floatingActionButton_add.setOnClickListener(onClickListener);
    }

    private void goToDetailActivity(int position, int MODE) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Memo memo = recyclerCustomAdapter.get(position);
        Image[] images = memo.getImages();
        intent.putExtra("MODE", MODE);
        intent.putExtra("id", memo.getId());
        intent.putExtra("date", memo.getDate());
        intent.putExtra("title", memo.getTitle());
        intent.putExtra("content", memo.getContent());
        if (images == null)
            images = new Image[0];
        intent.putExtra("imageCount", images.length);
        for (int i = 0; i < images.length; i++) {
            intent.putExtra("image_id" + i, images[i].getId());
            intent.putExtra("imageid" + i, images[i].getImage_id());
            intent.putExtra("imagememoid" + i, images[i].getMemo_id());
            intent.putExtra("image" + i, images[i].getUri());
        }
        startActivityForResult(intent, REQ_DETAIL);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.material_design_floating_action_menu_item_add:
                    startActivityForResult(new Intent(MainActivity.this, AddActivity.class), REQ_ADD);
                    break;
            }
            materialDesignFAM.close(true);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Image[] images = null;
            Memo memo = null;
            switch (requestCode) {
                case REQ_ADD:
                    String title = data.getStringExtra("title");
                    String content = data.getStringExtra("content");
                    int imagecount = data.getIntExtra("imageCount", 0);
                    images = new Image[imagecount];
                    for (int i = 0; i < imagecount; i++) {
                        images[i] = new Image(data.getStringExtra("image" + i));
                    }
                    memo = new Memo(title, content, new Date(System.currentTimeMillis()).toString(), images);
                    save(memo);
                    break;
                case REQ_DETAIL:
                    int imagesCount = data.getIntExtra("imageCount", 0);
                    images = new Image[imagesCount];
                    for (int i = 0; i < imagesCount; i++) {
                        images[i] = new Image(data.getLongExtra("image_id" + i, 0), data.getLongExtra("imageid" + i, 0), data.getLongExtra("imagememoid" + i, 0), data.getStringExtra("image" + i));
                    }
                    memo = new Memo(data.getLongExtra("id", 0), data.getStringExtra("title"), data.getStringExtra("content"), data.getStringExtra("date"), images);

                    if (data.getBooleanExtra("mode", true)) {
                        update(memo);
                    } else {
                        delete(memo);
                    }

            }
        }
    }

    private void delete(Memo memo) {
        try {
            memoDataBaseManager.delete(memo);
            recyclerCustomAdapter.set(memoDataBaseManager.select());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(Memo memo) {
        try {
            memoDataBaseManager.update(memo);
            recyclerCustomAdapter.set(memoDataBaseManager.select());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void save(Memo memo) {
        try {
            memoDataBaseManager.insert(memo);
            recyclerCustomAdapter.add(memo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 권한관리
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionControl.checkPermission(this, REQ_PERMISSION)) {
                init();
            }
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            if (PermissionControl.onCheckResult(grantResults)) {
                init();
            } else {
                Toast.makeText(this, "권한을 허용하지 않으시면 프로그램을 실행할 수 없습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
