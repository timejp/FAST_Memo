package com.timejh.memo;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.timejh.memo.database.Image;

public class AddActivity extends AppCompatActivity {

    private final int REQ_CAMERA = 101; // 카메라 요청코드
    private final int REQ_GALLERY = 102; // 갤러리 요청코드

    private EditText ed_title;
    private EditText ed_content;
    private ViewPager viewPager;
    private ImageAdapter imageAdapter;

    private FloatingActionMenu materialDesignFAM;
    private FloatingActionButton floatingActionButton_gallery, floatingActionButton_camera, floatingActionButton_cancel, floatingActionButton_save;

    private Uri fileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ed_title = (EditText) findViewById(R.id.ed_title);
        ed_content = (EditText) findViewById(R.id.ed_content);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        imageAdapter = new ImageAdapter(this, viewPager);

        viewPager.setAdapter(imageAdapter);
        viewPager.setVisibility(View.GONE);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton_camera = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_camenra);
        floatingActionButton_gallery = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_gallery);
        floatingActionButton_cancel = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_cancel);
        floatingActionButton_save = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_save);

        floatingActionButton_camera.setOnClickListener(clickListener);
        floatingActionButton_gallery.setOnClickListener(clickListener);
        floatingActionButton_cancel.setOnClickListener(clickListener);
        floatingActionButton_save.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            materialDesignFAM.close(true);
            Intent intent = null;
            switch (v.getId()) {
                case R.id.material_design_floating_action_menu_item_camenra: //카메라 버튼 동작
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // 롤리팝 이상 버전에서는 아래 코드를 반영해야 한다.
                    // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ContentValues values = new ContentValues(1);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    // --- 여기 까지 컨텐트 uri 강제세팅 ---

                    startActivityForResult(intent, REQ_CAMERA);
                    break;

                case R.id.material_design_floating_action_menu_item_gallery: // 갤러리에서 이미지 불러오기
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*"); // 외부저장소에 있는 이미지만 가져오기 위한 필터링
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY);
                    break;
                case R.id.material_design_floating_action_menu_item_cancel:
                    finish();
                    break;
                case R.id.material_design_floating_action_menu_item_save:
                    intent = getIntent();
                    intent.putExtra("title", ed_title.getText().toString());
                    intent.putExtra("content", ed_content.getText().toString());
                    intent.putExtra("imageCount", imageAdapter.getCount());
                    for (int i = 0; i < imageAdapter.getCount(); i++) {
                        intent.putExtra("image" + i, imageAdapter.getImage(i).getUri());
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQ_GALLERY:
                if (resultCode == RESULT_OK) {
                    fileUri = data.getData();
                    viewPager.setVisibility(View.VISIBLE);
                    imageAdapter.add(new Image(fileUri.toString()));
                } else {
                    fileUri = null;
                }
                break;
            case REQ_CAMERA:
                if (requestCode == REQ_CAMERA && resultCode == RESULT_OK) { // 사진 확인처리됨 RESULT_OK = -1
                    // 롤리팝 체크
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        fileUri = data.getData();
                    }
                    if (fileUri != null) {
                        viewPager.setVisibility(View.VISIBLE);
                        imageAdapter.add(new Image(fileUri.toString()));
                    } else {
                        Toast.makeText(this, "사진파일이 없습니다", Toast.LENGTH_LONG).show();
                    }
                } else {
                    fileUri = null;
                }
                break;
        }
    }
}
