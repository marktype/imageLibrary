package com.example.administrator.testlibapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mylibrary.ImageUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Button mOne,mTwo,mThree,mFour;
    private  Uri uri;
    private ImageView mImage;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOne = (Button) findViewById(R.id.get_perssion);
        mTwo = (Button) findViewById(R.id.check_sdcard);
        mThree = (Button) findViewById(R.id.open_carmera);
        mImage = (ImageView) findViewById(R.id.image);
        mFour = (Button) findViewById(R.id.open_picture);

        mOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtils.checkPermissions(MainActivity.this,123);
            }
        });

        mTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImageUtils.hasSdcard()){
                    Toast.makeText(MainActivity.this,"有sdcard",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this,"没有sdcard",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file = ImageUtils.createImageFile();
               uri = ImageUtils.getSaveUriAll(MainActivity.this,file);
               Log.d("tag","uri---"+uri);
                ImageUtils.openCamera(MainActivity.this,uri,12);
            }
        });
        mFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtils.openAlbum(MainActivity.this,15);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("tag","requestCode----"+requestCode);
        //如果返回码是照相机返回码,就进行以下处理
        if (requestCode == 12) {
            Log.d("tag","uri-----2222-----"+uri);
            ImageUtils.cropPhoto(this,uri,400,600,13,false);

        }else if (requestCode == 13){
            Log.d("tag","data----"+data);
            Log.d("tag","file------"+file.getAbsolutePath()+"-----uri-----"+uri);
            Glide.with(MainActivity.this).load(uri).into(mImage);
//            mImage.setImageURI(uri);
        }else if (requestCode == 124){
            Toast.makeText(this,"124",Toast.LENGTH_SHORT).show();
        }else if (requestCode == 15){
            Log.d("tag","data---------"+data);
            if (data != null){
                Uri contentUri = data.getData();
                ImageUtils.cropPhoto(this,contentUri,400,600,17,true);
            }

        }else if (requestCode == 25){
            Log.d("tag","data----"+data);
            mImage.setImageURI(uri);
        }else if (requestCode == 17){
            Log.d("tag","data----17-----"+data);
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();

            if (bundle != null){
                Bitmap bitmap = bundle.getParcelable("data");
                mImage.setImageBitmap(bitmap);
            }
        }

    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ImageUtils.callBackResPermission(this,requestCode,permissions,grantResults,123);
    }

}
