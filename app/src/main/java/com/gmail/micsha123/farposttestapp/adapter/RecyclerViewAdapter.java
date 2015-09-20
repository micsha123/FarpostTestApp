package com.gmail.micsha123.farposttestapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.micsha123.farposttestapp.R;
import com.gmail.micsha123.farposttestapp.data.Links;

import java.util.ArrayList;
/** Adapter for RecyclerView*/
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> links;
    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindRequest(links.get(position));
    }

    @Override
    public int getItemCount() {
        links = Links.getInstance(context).getLinks();
        return links.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView link;

        public ViewHolder(View itemView) {
            super(itemView);
            link = (TextView) itemView.findViewById(R.id.text_link);
        }

        public void bindRequest(String string){
            link.setText(string);
        }
    }
}