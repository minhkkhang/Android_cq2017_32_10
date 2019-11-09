package com.example.demomainpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.TourItemViewHolder> {
    private List<Tours>tours;
    private Context context;

    public RecyclerDataAdapter(List<Tours>tours,Context c)
    {
        this.tours=tours;
        this.context=c;
    }


    @NonNull
    @Override
    public TourItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_tour, parent, false);

        return new TourItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TourItemViewHolder holder, int position) {
        Tours t=tours.get(position);
        holder.tv_tourname.setText(t.name);
        holder.tv_id.setText(t.id);
        holder.tv_startDate.setText(t.startDate);
        holder.tv_endDate.setText(t.endDate);
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }
    public static class TourItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_tourname;
        public TextView tv_id;
        public TextView tv_startDate;
        public TextView tv_endDate;

        public TourItemViewHolder(View itemView) {
            super(itemView);
            tv_tourname = (TextView) itemView.findViewById(R.id.tourname);
            tv_id = (TextView) itemView.findViewById(R.id.id);
            tv_startDate = (TextView) itemView.findViewById(R.id.startDate);
            tv_endDate=(TextView) itemView.findViewById(R.id.endDate);
        }
    }
}
