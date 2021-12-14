package com.example.music;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// "/storage/emulated/0"等于"Environment.getExternalStorageDirectory().getPath()"
public class MuiscList extends AppCompatActivity {
    ListView list_music;
    ImageView image_list;
    SimpleAdapter adapter;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_muisc_list);

        init();//初始化ui组件
    }

    private void init() {
        list_music = findViewById(R.id.list_music);
        image_list=findViewById(R.id.image_list);
        drawerLayout= findViewById(R.id.dlayout);

        getAllMusic();

        //利用SimpleAdapter(简单的适配器),从一个Map中根据key值取相应东西,
        //并设置到对应的layout的组件上
        adapter = new SimpleAdapter(
                MuiscList.this,
                ZiYuan.data,//资源文件
                R.layout.list_layout,//要设置的布局文件
                new String[]{"name", "date"},//key值(根据key值取map中的values)
                new int[]{R.id.music_name, R.id.music_data}//要放置在布局文件的具体那个组件
        );

        list_music.setAdapter(adapter);

        image_list.setOnClickListener(view -> startDrawer());

        list_music.setOnItemClickListener((adapterView, view, i, l) -> {

            Intent intent = new Intent(MuiscList.this, MusicPlay.class);
            ZiYuan.index = i;//index为索引,i表示点击的是列表的第几列
            startActivity(intent);
        });
    }

    private void getAllMusic() {//读取手机sd卡上的音乐文件

        //获取sd卡的状态,Environment环境变量，MEDIA_MOUNTED表示sd卡可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            @SuppressLint("SdCardPath")

            //"/mnt/sdcard/Music"这是sdcard卡默认的位置
            File file = new File("/mnt/sdcard/Music");

            // /storage/emulated/0/kgmusic/download/手机"酷狗音乐"文件路径
            //File file=new File("/storage/emulated/0/kgmusic/");

            traverse(file);

        } else {
            Toast.makeText(this, "sd卡不可用", Toast.LENGTH_SHORT).show();
        }
    }

    private void traverse(File file) {//遍历整个文件,取.mp3的文件
        File[] music = file.listFiles();

        if (music != null) {
            Map<String, String> map;

            for (File value : music) {//遍历

                //如果是文件,且是.mp3结尾的文件
                if (value.isFile() && value.getName().endsWith(".mp3")) {
                    map = new HashMap<>();

                    //获取绝对路径(例如:/mnt/sdcard/Music/Ahrix - Nova.mp3),放入map中
                    map.put("path", value.getAbsolutePath());

                    //获取音乐名称(例如:Ahrix - Nova.mp3)
                    map.put("name", value.getName());

                    //lastModified表示文件最后一次修改的时间
                    map.put("date", Time(value.lastModified()));


                    ZiYuan.data.add(map);//将map添加到List中
                }

                if (value.isDirectory()) {//如果是目录，就递归再次遍历
                    traverse(value);
                }
            }
        } else {
            Toast.makeText(this, "Music中没有.Mp3的文件", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("WrongConstant")
    public void startDrawer(){
        drawerLayout.openDrawer(Gravity.START);
    }

    //格式化播放时间
    private String Time(long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月ss日");
        return sdf.format(new Date(time));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ZiYuan.mediaPlayer != null && ZiYuan.mediaPlayer.isPlaying())
            ZiYuan.stopmusic();
    }
}