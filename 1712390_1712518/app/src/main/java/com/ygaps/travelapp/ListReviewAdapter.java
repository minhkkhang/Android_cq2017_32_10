package com.ygaps.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ygaps.travelapp.pojo.TourComment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ListReviewAdapter extends RecyclerView.Adapter<ListReviewAdapter.MyViewHolder> {

    private ArrayList<TourComment> listData;
    private Context context;
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView reviewAvatar;
        TextView reviewName;
        TextView reviewReview;
        TextView reviewRate;
        TextView reviewDate;
        public MyViewHolder(View v){
            super(v);
            reviewAvatar=v.findViewById(R.id.review_avatar);
            reviewName=v.findViewById(R.id.review_name);
            reviewReview=v.findViewById(R.id.review_review);
            reviewRate=v.findViewById(R.id.review_rate);
            reviewDate=v.findViewById(R.id.review_date);
        }
    }
    public ListReviewAdapter(ArrayList<TourComment> listData) {
        this.listData = listData;
    }
    @NonNull
    @Override
    public ListReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_review,parent,false);
        MyViewHolder vh=new MyViewHolder(v);
        context=parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TourComment review=this.listData.get(position);

        if(review.getAvatar()!=null){
            if(!review.getAvatar().isEmpty()){
                Picasso.with(this.context).load(review.getAvatar())
                        .centerCrop()
                        .fit()
                        .into(holder.reviewAvatar);
            }
            else{
                holder.reviewAvatar.setImageResource(R.drawable.man);
            }
        }else{
            holder.reviewAvatar.setImageResource(R.drawable.man);
        }

        holder.reviewName.setText(review.getName());

        if(review.getFeedback()!=null)holder.reviewReview.setText(review.getFeedback());
        if(review.getComment()!=null)holder.reviewReview.setText(review.getComment());
        if(review.getReview()!=null)holder.reviewReview.setText(review.getReview());

        StringBuilder builder=new StringBuilder();
        if(review.getPoint()!=null){
            builder.append("Rating: ");
            builder.append(review.getPoint().toString());
            holder.reviewRate.setText(builder.toString());
        }

        if(review.getCreatedOn()==null)return;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm z");
        Calendar calendar=Calendar.getInstance();
        try{
            calendar.setTimeInMillis(Long.parseLong(review.getCreatedOn()));
        }catch(Exception e){
            e.printStackTrace();
        }
        holder.reviewDate.setText(format.format(calendar.getTime()));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


}
