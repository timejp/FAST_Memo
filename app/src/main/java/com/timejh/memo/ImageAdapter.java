package com.timejh.memo;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.timejh.memo.database.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokijh on 2017. 2. 2..
 */

public class ImageAdapter extends PagerAdapter {

    private static final String TAG = "ImageAdapter";

    private List<Image> images;
    private Context context;
    private LayoutInflater inflater;
    private ViewPager viewPager;

    public ImageAdapter(Context context, ViewPager viewPager) {
        images = new ArrayList<>();
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewPager = viewPager;
    }

    public void add(Image image) {
        images.add(image);
        this.notifyDataSetChanged();
    }

    public void set(List<Image> images) {
        this.images = images;
        this.notifyDataSetChanged();
    }

    public void remove(int position, View view) {
        images.remove(position);
        viewPager.removeView(view);
        if(images.size() <= 0)
            viewPager.setVisibility(View.GONE);
        this.notifyDataSetChanged();
    }

    public Image getImage(int position) {
        return images.get(position);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View view = inflater.inflate(R.layout.viewpager_item, null);
        ImageView iv_image = (ImageView) view.findViewById(R.id.imageView);
        Glide.with(context).load(Uri.parse(images.get(position).getUri())).into(iv_image);
        iv_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setTitle("삭제하겠습니까?");
                ab.setMessage("삭제하면 복구 할 수 없습니다.");
                ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(position, view);
                        dialog.dismiss();
                    }
                });
                ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ab.show();
                return false;
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
