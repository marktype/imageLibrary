package com.example.administrator.testlibapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by fj on 2017/6/7.
 * RecyclerView封装
 */

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder>{
    private Context context;
    private ArrayList list;
    private int layout;
    private BindViewHolder bind;
    public RecyclerviewAdapter(Context context, ArrayList list, int layout){
        this.context = context;
        this.list = list;
        this.layout = layout;
        notifyDataSetChanged();
    }
    public void setOnBindViewHolder(BindViewHolder bind){
        this.bind = bind;
    }

    public void addData(ArrayList list){
        if (list != null){
            this.list.addAll(list);
        }else{
            this.list = list;
        }
        notifyDataSetChanged();
    }


    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(layout, parent, false));
    }


    public void onBindViewHolder(MyViewHolder holder, int position) {
        bind.onBind(holder,position,list.get(position));
    }


    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private View view;
        MyViewHolder(View view){
            super(view);
            this.view = view;
        }
        public View getView(int id){
            return view.findViewById(id);
        }
    }
    interface BindViewHolder<T>{
        void onBind(MyViewHolder p0, int p1, T content);
    }
}
