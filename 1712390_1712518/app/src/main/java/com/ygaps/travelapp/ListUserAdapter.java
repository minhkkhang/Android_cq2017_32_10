package com.ygaps.travelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.ReviewRequest;
import com.ygaps.travelapp.pojo.Tour;
import com.ygaps.travelapp.pojo.UserInfoObj;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListUserAdapter extends BaseAdapter {
    private ArrayList<UserInfoObj> listData,originData;
    private ArrayList<Integer>invitedIds;
    private LayoutInflater layoutInflater;
    private Context context;
    private String token;
    public ListUserAdapter(Context aContext,  ArrayList<UserInfoObj> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        originData=new ArrayList<>();
        invitedIds=new ArrayList<>();
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }
    public Integer getUserId(int position){return listData.get(position).getId();}
    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListUserAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_user, null);
            holder = new ListUserAdapter.ViewHolder();
            holder.userAvatarView=convertView.findViewById(R.id.user_info_avatar);
            holder.userNameView=convertView.findViewById(R.id.user_info_name);
            holder.userIDView=convertView.findViewById(R.id.user_info_ID);
            holder.userDetailView=convertView.findViewById(R.id.user_info_detail);
            holder.userInvited=convertView.findViewById(R.id.user_info_invite);
            convertView.setTag(holder);
        } else {
            holder = (ListUserAdapter.ViewHolder) convertView.getTag();
        }
        UserInfoObj user= this.listData.get(position);
        if(user.getId()==null)return convertView;
        holder.userNameView.setText(user.getFullName());

        StringBuilder builder = new StringBuilder();
        builder.append("ID:");
        builder.append(user.getId().toString());
        holder.userIDView.setText(builder.toString());

        builder=new StringBuilder();
        if(user.getEmail()!=null){
            builder.append("Email: ");
            builder.append(user.getEmail().toString());
            builder.append(System.getProperty("line.separator"));
        }
        if(user.getPhone()!=null){
            builder.append("Phone number: ");
            builder.append(user.getPhone().toString());
            builder.append(System.getProperty("line.separator"));
        }
        if(user.getDob()!=null){
            builder.append("Birthday: ");
            builder.append(user.getDob().substring(8,10));
            builder.append("/");
            builder.append(user.getDob().substring(5,7));
            builder.append("/");
            builder.append(user.getDob().substring(0,4));
            builder.append(System.getProperty("line.separator"));
        }
        builder.append("Gender: ");
        if(user.getGender()==null)builder.append("Other");
        else{
            if(user.getGender()==1)builder.append("male");
            else builder.append("female");
            holder.userDetailView.setText(builder.toString());
        }
        if(user.getAvatar()!=null){
            if(!user.getAvatar().isEmpty()){
                Picasso.with(this.context).load(user.getAvatar())
                        .centerCrop()
                        .fit()
                        .into(holder.userAvatarView);
            }
            else{
                holder.userAvatarView.setImageResource(R.drawable.man);
            }
        }else{
            holder.userAvatarView.setImageResource(R.drawable.man);
        }
        if(invitedIds.contains(user.getId()))holder.userInvited.setVisibility(View.VISIBLE);
        else holder.userInvited.setVisibility(View.GONE);
        return convertView;
    }

    static class ViewHolder {
        ImageView userAvatarView;
        TextView userNameView;
        TextView userIDView;
        TextView userDetailView;
        TextView userInvited;
    }
    public void Filter(CharSequence constraint){
        ArrayList<UserInfoObj> FilteredList=new ArrayList<>();
        if(constraint==null || constraint.toString().compareTo("")==0){
            listData=originData;
            notifyDataSetChanged();
            return;
        }
        CharSequence cons=constraint.toString().toLowerCase();
        for(int i=0;i<originData.size();i++){
            UserInfoObj temp=originData.get(i);
            if(temp.getFullName()!=null){
                if(temp.getFullName().toLowerCase().contains(cons))
                    FilteredList.add(temp);
            }
        }
        listData=FilteredList;
        notifyDataSetChanged();
    }

    public void SetOrigin(List<UserInfoObj>list){
        originData.clear();
        originData.addAll(list);
    }
    public void SetInvitedIds(Integer id){
        if(!invitedIds.contains(id))invitedIds.add(id);
    }
    public boolean isInvitedId(Integer id){
        return invitedIds.contains(id);
    }
}
