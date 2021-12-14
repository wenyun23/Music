package com.example.music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

//设置顶部标题栏
public class BiaoTi extends LinearLayout {
    @SuppressLint("StaticFieldLeak")
    static TextView img_title;
    public ImageView img_back;

    @SuppressLint("WrongConstant")
    public BiaoTi(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.title_layout, this);
        img_back = findViewById(R.id.img_back);
        img_title = findViewById(R.id.img_title);

        img_back.setOnClickListener(view -> {//返回上一级Activity
            ZiYuan.stopmusic();//按返回键音乐播放也要停止
            ((Activity) getContext()).finish();
        });

        if (ZiYuan.data.size() != 0){
            img_title.setText(ZiYuan.data.get(ZiYuan.index).get("name"));
        }
    }

    static class setName {
        public setName() {
            img_title.setText(ZiYuan.data.get(ZiYuan.index).get("name"));
        }
    }
}
