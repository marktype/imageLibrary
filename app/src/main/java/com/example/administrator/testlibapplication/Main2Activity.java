package com.example.administrator.testlibapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.example.mylibrary.AppHintUtils;
import com.example.mylibrary.NetUtils;

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
                if (NetUtils.isConnected(Main2Activity.this)){
                    AppHintUtils.getHintInstance().showToast(Main2Activity.this,"网络已经连接");
                }else {
//                    NetManager.getNetManagerInstance().setNetwork(Main2Activity.this);
                    AppHintUtils.getHintInstance().showProgressDialog(Main2Activity.this);
                }

//                NetManager.getNetManagerInstance().checkNetConnection(Main2Activity.this);
            }
        });

        mWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtils.getNetManagerInstance().isWifiConnected(Main2Activity.this)){
                    AppHintUtils.getHintInstance().showToast(Main2Activity.this,"wifi连接");
                }else {
                    AppHintUtils.getHintInstance().showToast(Main2Activity.this,"wifi没有连接");
                }

            }
        });

        mMob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtils.getNetManagerInstance().isMobileConnected(Main2Activity.this)){
                    AppHintUtils.getHintInstance().showToast(Main2Activity.this,"moblie连接");
                }else {
                    AppHintUtils.getHintInstance().showToast(Main2Activity.this,"moblie没有连接");
                }
            }
        });
        mMoblieAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHintUtils.getHintInstance().showToast(Main2Activity.this,NetUtils.
                        getNetManagerInstance().getIPAddress(Main2Activity.this));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppHintUtils.getHintInstance().cancelProgressDialog();

        return super.onKeyDown(keyCode, event);
    }
}
