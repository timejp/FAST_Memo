package com.timejh.memo;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.timejh.memo.database.Image;
import com.timejh.memo.database.Memo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokijh on 2017. 1. 31..
 */

public class RecyclerCustomAdapter extends RecyclerView.Adapter<RecyclerCustomAdapter.CustomViewHolder> {

    private List<Memo> datas;
    private Context context;

    public RecyclerCustomAdapter(Context context, List<Memo> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void add(Memo memo) {
        datas.add(memo);
        this.notifyDataSetChanged();
    }

    public void set(List<Memo> datas) {
        this.datas = datas;
        this.notifyDataSetChanged();
    }

    public void remove(int posision) {
        datas.remove(posision);
        this.notifyDataSetChanged();
    }

    public Memo get(int position) {
        return datas.get(position);
    }

    // View 를 생성해서 홀더에 저장하는 역할
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CustomViewHolder(view);
    }

    // ListView에서의 getView를 대체하는 함수
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Memo memo = datas.get(position);
        Image[] images = memo.getImages();
        if (images != null) {
            holder.imageLayout.setVisibility(View.VISIBLE);
            switch (images.length) {
                default:
                case 4:
                    holder.imageViews[3].setVisibility(View.VISIBLE);
                    setImageIntoImageView(images[3].getUri(), holder.imageViews[3]);
                case 3:
                    holder.imageViews[2].setVisibility(View.VISIBLE);
                    holder.layoutdown.setVisibility(View.VISIBLE);
                    setImageIntoImageView(images[2].getUri(), holder.imageViews[2]);
                case 2:
                    holder.imageViews[1].setVisibility(View.VISIBLE);
                    setImageIntoImageView(images[1].getUri(), holder.imageViews[1]);
                case 1:
                    holder.imageViews[0].setVisibility(View.VISIBLE);
                    holder.layoutup.setVisibility(View.VISIBLE);
                    setImageIntoImageView(images[0].getUri(), holder.imageViews[0]);
                case 0:
                    break;
            }
            if(images.length > 2) {
                holder.layout_card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.DPFromPixel(context, 620)));
            } else {
                holder.layout_card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.DPFromPixel(context, 450)));
            }
        } else {
            holder.layout_card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.DPFromPixel(context, 300)));
            holder.imageLayout.setVisibility(View.GONE);
            holder.layoutup.setVisibility(View.GONE);
            holder.layoutdown.setVisibility(View.GONE);
            holder.imageViews[0].setVisibility(View.GONE);
            holder.imageViews[1].setVisibility(View.GONE);
            holder.imageViews[2].setVisibility(View.GONE);
            holder.imageViews[3].setVisibility(View.GONE);
        }
        holder.tv_title.setText(memo.getTitle());
        holder.tv_content.setText(memo.getContent());
        holder.tv_date.setText(memo.getDate());
    }

    private void setImageIntoImageView(String image, ImageView imageView) {
        Glide.with(context).load(Uri.parse(image)).into(imageView);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    // Recycle View 에서 사용하는 뷰 홀더
    // 이 뷰홀더를 사용하는 Adapter는 generic으로 선언된 부모 객체를 상속받아야 함
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_date;
        TextView tv_content;
        CardView layout_card;
        LinearLayout imageLayout;
        LinearLayout layoutup;
        LinearLayout layoutdown;
        ImageView[] imageViews = new ImageView[4];

        public CustomViewHolder(View itemView) {
            super(itemView);
            layout_card = (CardView) itemView.findViewById(R.id.layout_card);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            imageLayout = (LinearLayout) itemView.findViewById(R.id.imageLayout);
            layoutup = (LinearLayout) itemView.findViewById(R.id.layoutup);
            layoutdown = (LinearLayout) itemView.findViewById(R.id.layoutdown);
            imageViews[0] = (ImageView)itemView.findViewById(R.id.image1);
            imageViews[1] = (ImageView)itemView.findViewById(R.id.image2);
            imageViews[2] = (ImageView)itemView.findViewById(R.id.image3);
            imageViews[3] = (ImageView)itemView.findViewById(R.id.image4);
        }
    }
}
