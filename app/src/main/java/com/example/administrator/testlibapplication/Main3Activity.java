package com.example.administrator.testlibapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


    }


    /**
     * 封装recyclerview使用
     */
    private RecyclerView mRecycle;
//    public void showData(){
//        mRecycle = (RecyclerView) findViewById(R.id.recycle);
//        mRecycle.setVisibility(View.VISIBLE);
//        mRecycle.setLayoutManager(new LinearLayoutManager(this));
//        RecyclerviewAdapter adapter = new RecyclerviewAdapter(this,dataList,R.layout.image_list_layout);
//        mRecycle.setAdapter(adapter);
//        adapter.setOnBindViewHolder(new RecyclerviewAdapter.BindViewHolder() {
//            @Override
//            public void onBind(RecyclerviewAdapter.MyViewHolder p0, int p1, Object content) {
//
//            }
//        });
//    }
}
