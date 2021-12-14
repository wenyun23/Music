package com.example.music;

import android.media.MediaPlayer;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//用于放一些公共资源
public class ZiYuan {
    public static List<Map<String,String >> data=new ArrayList<>();//定义一个List集合,类型为Map
    public static int index=0;//获取点击的是哪一个视频
    public static boolean tage=true;//true表示让图片旋转，false反之
    public static MediaPlayer mediaPlayer;

    public static void stopmusic(){

        if (ZiYuan.mediaPlayer!=null&&ZiYuan.mediaPlayer.isPlaying()){
            Log.d("TAG", "stopmusic: 1");
            ZiYuan.mediaPlayer.stop();
            ZiYuan.mediaPlayer.reset();
            tage=false;//让logd不转
        }else {//当是暂停时,点了back，也要释放音乐
            Log.d("TAG", "stopmusic: 2");
            ZiYuan.mediaPlayer.reset();
            tage=false;//让logd不转
        }
    }
}
