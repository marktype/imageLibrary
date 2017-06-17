package com.example.administrator.testlibapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.example.mylibrary.NetManager;

public class Main2Activity extends AppCompatActivity {
    private Button mBtn,mWifi,mMob,mMoblieAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mBtn = (Button) findViewById(R.id.check_net);
        mWifi = (Button) findViewById(R.id.check_wifi_net);
        mMob = (Button) findViewById(R.id.check_moblie_net);
        mMoblieAddress = (Button) findViewById(R.id.get_moblie_address);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetManager.isConnected(Main2Activity.this)){
                    NetManager.getNetManagerInstance().showToast(Main2Activity.this,"网络已经连接");
                }else {
//                    NetManager.getNetManagerInstance().setNetwork(Main2Activity.this);
                    NetManager.getNetManagerInstance().showProgressDialog(Main2Activity.this);
                }

//                NetManager.getNetManagerInstance().checkNetConnection(Main2Activity.this);
            }
        });

        mWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetManager.getNetManagerInstance().isWifiConnected(Main2Activity.this)){
                    NetManager.getNetManagerInstance().showToast(Main2Activity.this,"wifi连接");
                }else {
                    NetManager.getNetManagerInstance().showToast(Main2Activity.this,"wifi没有连接");
                }

            }
        });

        mMob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetManager.getNetManagerInstance().isMobileConnected(Main2Activity.this)){
                    NetManager.getNetManagerInstance().showToast(Main2Activity.this,"moblie连接");
                }else {
                    NetManager.getNetManagerInstance().showToast(Main2Activity.this,"moblie没有连接");
                }
            }
        });
        mMoblieAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetManager.getNetManagerInstance().showToast(Main2Activity.this,NetManager.
                        getNetManagerInstance().getIPAddress(Main2Activity.this));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        NetManager.getNetManagerInstance().cancelProgressDialog();

        return super.onKeyDown(keyCode, event);
    }
}
