package com.ygaps.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ygaps.travelapp.pojo.Tour;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListTourAdapter extends BaseAdapter {
    private ArrayList<Tour> listData;
    private ArrayList<Tour> originData;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListTourAdapter(Context aContext,  ArrayList<Tour> listData) {
        this.context = aContext;
        this.listData = listData;
        this.originData=listData;
        layoutInflater = LayoutInflater.from(aContext);
    }
    public int getTourId(int position){
        return listData.get(position).getId();
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
            holder.tourStatusView=convertView.findViewById(R.id.tour_status);
            holder.tourHostAvatar=convertView.findViewById(R.id.tour_host_avatar);
            holder.tourHostName=convertView.findViewById(R.id.tour_host_name);
            holder.tourHostId=convertView.findViewById(R.id.tour_host_ID);
            holder.tourHostDetail=convertView.findViewById(R.id.tour_host_detail);
            holder.tourHostLayout=convertView.findViewById(R.id.tour_hostLayout);
            convertView.setTag(holder);
        } else {
            holder = (ListTourAdapter.ViewHolder) convertView.getTag();
        }

        Tour tour = this.listData.get(position);
        holder.tourNameView.setText(tour.getName());

        StringBuilder builder = new StringBuilder();
        builder.append("ID: ");
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
                holder.tourAvatarView.setImageResource(R.drawable.wallpaper);
            }
        }else{
            holder.tourAvatarView.setImageResource(R.drawable.wallpaper);
        }

        switch (tour.getStatus()){
            case 0:{
                holder.tourStatusView.setText("Status: Open");
                break;
            }
            case 1:{
                holder.tourStatusView.setText("Status: Started");
                break;
            }
            case 2:{
                holder.tourStatusView.setText("Status: Closed");
                break;
            }
            default:{
                holder.tourStatusView.setVisibility(View.GONE);
                break;}
        }

        if(tour.getHostId()!=null && tour.getIsHost()==null){
            holder.tourHostLayout.setVisibility(View.VISIBLE);
            holder.tourHostName.setText(tour.getHostName());

            builder = new StringBuilder();
            builder.append("ID:");
            builder.append(tour.getHostId().toString());
            holder.tourHostId.setText(builder.toString());

            builder=new StringBuilder();
            if(tour.getHostEmail()!=null){
                builder.append("Email: ");
                builder.append(tour.getHostEmail().toString());
                builder.append(System.getProperty("line.separator"));
            }
            if(tour.getHostPhone()!=null){
                builder.append("Phone number: ");
                builder.append(tour.getHostPhone().toString());
                builder.append(System.getProperty("line.separator"));
            }

            calendar=Calendar.getInstance();
            try{
                calendar.setTimeInMillis(Long.parseLong(tour.getCreatedOn()));
            }catch(Exception e){
                e.printStackTrace();
            }
            builder.append("Created on: ");
            builder.append(format.format(calendar.getTime()));
            holder.tourHostDetail.setText(builder.toString());
            if(tour.getHostAvatar()!=null){
                if(!tour.getHostAvatar().isEmpty()){
                    Picasso.with(this.context).load(tour.getHostAvatar())
                            .centerCrop()
                            .fit()
                            .into(holder.tourHostAvatar);
                }
                else{
                    holder.tourHostAvatar.setImageResource(R.drawable.man);
                }
            }else{
                holder.tourHostAvatar.setImageResource(R.drawable.man);
            }
        }
        else holder.tourHostLayout.setVisibility(View.GONE);

        return convertView;
    }
    public void Filter(CharSequence constraint){
        ArrayList<Tour> FilteredList=new ArrayList<>();
        if(constraint==null || constraint.toString().compareTo("")==0){
            listData=originData;
            notifyDataSetChanged();
            return;
        }
        CharSequence cons=constraint.toString().toLowerCase();
        for(int i=0;i<originData.size();i++){
            Tour temp=originData.get(i);
            if(temp.getName()!=null){
                if(temp.getName().toLowerCase().contains(cons))
                    FilteredList.add(temp);
            }
        }
        listData=FilteredList;
        notifyDataSetChanged();
    }
    static class ViewHolder {
        ImageView tourAvatarView;
        TextView tourNameView;
        TextView tourIDView;
        TextView tourDateView;
        TextView tourCostView;
        TextView tourAdultView;
        TextView tourChildrenView;
        TextView tourStatusView;

        LinearLayout tourHostLayout;
        TextView tourHostName;
        ImageView tourHostAvatar;
        TextView tourHostDetail;
        TextView tourHostId;
    }
    public void addItems(List<Tour> tours){
        originData.clear();
        originData.addAll(tours);
        listData=originData;
        notifyDataSetChanged();
    }
}
