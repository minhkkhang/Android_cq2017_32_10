package com.example.a1712390_1712518;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a1712390_1712518.pojo.TourComment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ListReviewAdapter extends BaseAdapter {

    private ArrayList<TourComment> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListReviewAdapter(Context aContext,  ArrayList<TourComment> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListReviewAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_review, null);
            holder = new ListReviewAdapter.ViewHolder();
            holder.reviewAvatar= (ImageView) convertView.findViewById(R.id.review_avatar);
            holder.reviewRate = (TextView) convertView.findViewById(R.id.review_rate);
            holder.reviewName = (TextView) convertView.findViewById(R.id.review_name);
            holder.reviewReview = (TextView) convertView.findViewById(R.id.review_review);
            holder.reviewDate = (TextView) convertView.findViewById(R.id.review_date);
            convertView.setTag(holder);
        } else {
            holder = (ListReviewAdapter.ViewHolder) convertView.getTag();
        }

        TourComment review=this.listData.get(position);

        if(review.getAvatar()!=null){
            if(!review.getAvatar().isEmpty()){
                Picasso.with(this.context).load(review.getAvatar())
                        .centerCrop()
                        .fit()
                        .into(holder.reviewAvatar);
            }
            else{
                holder.reviewAvatar.setImageResource(R.mipmap.ic_launcher);
            }
        }else{
            holder.reviewAvatar.setImageResource(R.mipmap.ic_launcher);
        }

        holder.reviewName.setText(review.getName());
        holder.reviewReview.setText(review.getFeedback());
        StringBuilder builder=new StringBuilder();
        builder.append("Rating: ");
        builder.append(review.getPoint().toString());
        holder.reviewRate.setText(builder.toString());

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm z");
        Calendar calendar=Calendar.getInstance();
        try{
            calendar.setTimeInMillis(Long.parseLong(review.getCreatedOn()));
        }catch(Exception e){
            e.printStackTrace();
        }
        holder.reviewDate.setText(format.format(calendar.getTime()));
        return convertView;
    }

    static class ViewHolder{
        ImageView reviewAvatar;
        TextView reviewName;
        TextView reviewReview;
        TextView reviewRate;
        TextView reviewDate;
    }
}
