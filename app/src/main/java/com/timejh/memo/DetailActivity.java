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
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.timejh.memo.database.Image;
import com.timejh.memo.database.Memo;

public class DetailActivity extends AppCompatActivity {

    private final int REQ_CAMERA = 101; // 카메라 요청코드
    private final int REQ_GALLERY = 102; // 갤러리 요청코드

    public static final int NONE = 0;
    public static final int EDIT = 1;

    private EditText ed_title;
    private EditText ed_content;
    private TextView tv_date;
    private ViewPager viewPager;
    private DetailImageAdapter imageAdapter;

    private FloatingActionMenu materialDesignFAM;
    private FloatingActionMenu materialDesignFAM_edit;
    private FloatingActionButton floatingActionButton_gallery, floatingActionButton_camera, floatingActionButton_cancel, floatingActionButton_save;
    private FloatingActionButton floatingActionButton_delete, floatingActionButton_edit;

    private Uri fileUri = null;

    private int MODE = NONE;

    private Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ed_title = (EditText) findViewById(R.id.ed_title);
        ed_content = (EditText) findViewById(R.id.ed_content);
        tv_date = (TextView) findViewById(R.id.tv_date);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        imageAdapter = new DetailImageAdapter(this, viewPager);

        viewPager.setAdapter(imageAdapter);
        viewPager.setVisibility(View.GONE);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        materialDesignFAM_edit = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu_edit);
        floatingActionButton_camera = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_camenra);
        floatingActionButton_gallery = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_gallery);
        floatingActionButton_cancel = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_cancel);
        floatingActionButton_save = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_save);
        floatingActionButton_edit = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_edit);
        floatingActionButton_delete = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_delete);

        floatingActionButton_camera.setOnClickListener(onClickListenerEdit);
        floatingActionButton_gallery.setOnClickListener(onClickListenerEdit);
        floatingActionButton_cancel.setOnClickListener(onClickListenerEdit);
        floatingActionButton_save.setOnClickListener(onClickListenerEdit);
        floatingActionButton_edit.setOnClickListener(onClickListener);
        floatingActionButton_delete.setOnClickListener(onClickListener);

        Intent intent = getIntent();
        int imagesCount = intent.getIntExtra("imageCount", 0);
        Image[] images = new Image[imagesCount];
        for(int i=0;i<imagesCount;i++) {
            viewPager.setVisibility(View.VISIBLE);
            images[i] = new Image(intent.getLongExtra("image_id"+i,0),intent.getLongExtra("imageid"+i, 0), intent.getLongExtra("imagememoid"+i, 0), intent.getStringExtra("image" + i));
        }
        memo = new Memo(intent.getLongExtra("id", 0), intent.getStringExtra("title"), intent.getStringExtra("content"), intent.getStringExtra("date"), images);

        ed_title.setText(memo.getTitle());
        ed_content.setText(memo.getContent());
        tv_date.setText(memo.getDate());
        for(Image image : images) {
            imageAdapter.add(image);
        }

        //MODE Setting
        MODE = intent.getIntExtra("MODE", NONE);
        changeMode();
    }

    private View.OnClickListener onClickListenerEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            materialDesignFAM_edit.close(true);
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
                    MODE = NONE;
                    changeMode();
                    break;
                case R.id.material_design_floating_action_menu_item_save:
                    intent = getIntent();
                    intent.putExtra("id", memo.getId());
                    intent.putExtra("date", memo.getDate());
                    intent.putExtra("title", ed_title.getText().toString());
                    intent.putExtra("content", ed_content.getText().toString());
                    intent.putExtra("imageCount", imageAdapter.getCount());
                    for (int i = 0; i < imageAdapter.getCount(); i++) {
                        intent.putExtra("image_id" + i, imageAdapter.getImage(i).getId());
                        intent.putExtra("imageid" + i, imageAdapter.getImage(i).getImage_id());
                        intent.putExtra("imagememoid" + i, imageAdapter.getImage(i).getMemo_id());
                        intent.putExtra("image" + i, imageAdapter.getImage(i).getUri());
                    }
                    intent.putExtra("mode", true);
                    setResult(RESULT_OK, intent);
                    MODE = NONE;
                    changeMode();
                    break;
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            materialDesignFAM.close(true);
            switch (v.getId()) {
                case R.id.material_design_floating_action_menu_item_edit:
                    MODE = EDIT;
                    changeMode();
                    break;
                case R.id.material_design_floating_action_menu_item_delete:
                    Intent intent = getIntent();
                    intent.putExtra("id", memo.getId());
                    intent.putExtra("date", memo.getDate());
                    intent.putExtra("title", memo.getTitle());
                    intent.putExtra("content", memo.getContent());
                    intent.putExtra("imageCount", imageAdapter.getCount());
                    for (int i = 0; i < imageAdapter.getCount(); i++) {
                        intent.putExtra("image_id" + i, imageAdapter.getImage(i).getId());
                        intent.putExtra("imageid" + i, imageAdapter.getImage(i).getImage_id());
                        intent.putExtra("imagememoid" + i, imageAdapter.getImage(i).getMemo_id());
                        intent.putExtra("image" + i, imageAdapter.getImage(i).getUri());
                    }
                    intent.putExtra("mode", false);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };

    private void changeMode() {
        switch (MODE) {
            case NONE:
                ed_title.setEnabled(false);
                ed_content.setEnabled(false);
                materialDesignFAM_edit.setVisibility(View.GONE);
                materialDesignFAM.setVisibility(View.VISIBLE);
                break;
            case EDIT:
                ed_title.setEnabled(true);
                ed_content.setEnabled(true);
                materialDesignFAM_edit.setVisibility(View.VISIBLE);
                materialDesignFAM.setVisibility(View.GONE);
                break;
        }
        imageAdapter.setMode(MODE);
    }
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
