package com.example.a1712390_1712518;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.Placeholder;

import com.example.a1712390_1712518.pojo.Tour;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ListTourAdapter extends BaseAdapter  {
    private ArrayList<Tour> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListTourAdapter(Context aContext,  ArrayList<Tour> listData) {
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

    public View getView(int position, View convertView, ViewGroup parent) {
        ListTourAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_tour, null);
            holder = new ListTourAdapter.ViewHolder();
            holder.tourAvatarView= (ImageView) convertView.findViewById(R.id.tour_avatar);
            holder.tourNameView = (TextView) convertView.findViewById(R.id.tour_name);
            holder.tourIDView = (TextView) convertView.findViewById(R.id.tour_id);
            holder.tourDateView = (TextView) convertView.findViewById(R.id.tour_Date);
            holder.tourCostView = (TextView) convertView.findViewById(R.id.tour_Cost);
            holder.tourAdultView = (TextView) convertView.findViewById(R.id.tour_adult);
            holder.tourChildrenView = (TextView) convertView.findViewById(R.id.tour_children);
            convertView.setTag(holder);
        } else {
            holder = (ListTourAdapter.ViewHolder) convertView.getTag();
        }

        Tour tour = this.listData.get(position);
        holder.tourNameView.setText(tour.getName());
        if(holder.tourNameView.getText().toString().compareTo("")==0){
            holder.tourNameView.setText("EMPTY_NAME");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("ID:");
        builder.append(tour.getId().toString());
        holder.tourIDView.setText(builder.toString());

        builder=new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm z");
        Calendar calendar=Calendar.getInstance();
        try{
            calendar.setTimeInMillis(Long.parseLong(tour.getStartDate()));
        }catch(Exception e){
            e.printStackTrace();
        }
        builder.append("Start Date: ");
        builder.append(format.format(calendar.getTime()));
        builder.append(System.getProperty("line.separator"));
        try{
            calendar.setTimeInMillis(Long.parseLong(tour.getEndDate()));
        }catch(Exception e){
            e.printStackTrace();
        }
        builder.append("End Date: ");
        builder.append(format.format(calendar.getTime()));
        holder.tourDateView.setText(builder.toString());

        builder = new StringBuilder();
        builder.append(tour.getMinCost());
        builder.append("VND ->");
        builder.append(tour.getMaxCost());
        builder.append("VND");
        holder.tourCostView.setText(builder.toString());
        if(tour.getAdults()!=null){
            holder.tourAdultView.setText(tour.getAdults().toString());
        }
        else holder.tourAdultView.setText("0");

        if(tour.getChilds()!=null){
            holder.tourChildrenView.setText(tour.getChilds().toString());
        }
        else holder.tourChildrenView.setText("0");
        if(tour.getAvatar()!=null){
            if(!tour.getAvatar().isEmpty()){
                Picasso.with(this.context).load(tour.getAvatar())
                        .centerCrop()
                        .fit()
                        .into(holder.tourAvatarView);
            }
            else{
                holder.tourAvatarView.setImageResource(R.drawable.ic_launcher_background);
            }
        }else{
            holder.tourAvatarView.setImageResource(R.drawable.ic_launcher_background);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView tourAvatarView;
        TextView tourNameView;
        TextView tourIDView;
        TextView tourDateView;
        TextView tourCostView;
        TextView tourAdultView;
        TextView tourChildrenView;
    }

}
