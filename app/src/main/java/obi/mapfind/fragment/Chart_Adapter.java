package obi.mapfind.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import obi.mapfind.CircleTransform;
import obi.mapfind.Other_Profile;
import obi.mapfind.R;

public class Chart_Adapter extends RecyclerView.Adapter<Chart_Adapter.MyViewHolder> {
private List<Chart_Getter> chartList;
        Context mContext;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView name,address,lik;
    private ImageView image;

    public MyViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.name);
       image = view.findViewById(R.id.image);
        address = view.findViewById(R.id.address);
    }
}

    public Chart_Adapter(Context context, List<Chart_Getter> chartList) {
        this.chartList = chartList;
        this.mContext = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chart_adapter, parent, false);
        mContext = itemView.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.name.setText(chartList.get(position).getName());
        holder.address.setText(chartList.get(position).getAddress());
        if(!chartList.get(position).getImage().contentEquals("")) {

                Picasso.get()
                        .load(chartList.get(position).getImage())
                        .placeholder(R.drawable.placeholder)
                        .resize(120, 120)
                        .transform(new CircleTransform())
                        .into(holder.image);

        }


    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }
    public void delete(int pos){
        chartList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, chartList.size());
    }

}