package com.example.music;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.music.databinding.ActivityMusicPlayBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MusicPlay extends AppCompatActivity implements View.OnClickListener {
    ActivityMusicPlayBinding binding;

    private int du = 0;//用来控制cd的转动的角度
    private int flag = 1;//1表示开始播放，2表示暂停播放
    private int list_type = 1;//1表示开始顺序播放，2表示循环播放,3表示随机播放
    BiaoTi.setName name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //为viewBinding申请权限
        binding = ActivityMusicPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();//初始化UI组件

        ZiYuan.tage = true;//每次进入播放界面cd开始转动
        new Rotate().execute();//播放音乐的同时，使cd旋转,这里也使用AsyTaster子线程控制
        play_music();//调用播放文件
    }

    private void init() {

        binding.imgRepter.setOnClickListener(this);
        binding.imgUp.setOnClickListener(this);
        binding.imgStart.setOnClickListener(this);
        binding.imgDown.setOnClickListener(this);
        binding.imgList.setOnClickListener(this);

        //给进度添加监听事件
        binding.seektiao.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {//onStopTrackingTouch表示手动拖动进度条结束后

                if (ZiYuan.mediaPlayer != null && ZiYuan.mediaPlayer.isPlaying()) {
                    //获取手动拖动进度条的位置，减一秒,不然下一首歌资源准备不过来
                    int position = seekBar.getProgress() - 1000;
                    ZiYuan.mediaPlayer.seekTo(position);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {//重写监听方法

        switch (view.getId()) {

            case R.id.img_up: {//上一首
                //当"暂停"音乐点击上一首要做如下处理,"开始"图标不会进来
                if (!ZiYuan.mediaPlayer.isPlaying()) {
                    ZiYuan.mediaPlayer.start();//此时音乐肯定没有播放,所有让他播放
                    binding.imgStart.setImageResource(R.drawable.pausecircle);//设置成播放图标

                    ZiYuan.tage = true;
                    new Rotate().execute();//cd转动
                }

                if (list_type == 1 || list_type == 2) {//如果是顺序播放,或循环播放
                    ZiYuan.index--;//索引--,就是前一首的下标
                } else ZiYuan.index = new Random().nextInt(ZiYuan.data.size());//随机播放

                if (ZiYuan.index < 0) {//如果是第一首,就直接到最后一首
                    ZiYuan.index = ZiYuan.data.size() - 1;
                }

                name = new BiaoTi.setName();//设置歌曲名字
                play_music();//调用进行播放音乐
            }break;

            case R.id.img_start: {//开始
                if (flag == 1) {//如果是开始播放状态
                    binding.imgStart.setImageResource(R.drawable.playcircle);
                    flag = 2;//将标记改为暂停状态

                    if (ZiYuan.mediaPlayer != null && ZiYuan.mediaPlayer.isPlaying()) {
                        ZiYuan.mediaPlayer.pause();//音乐暂停
                        ZiYuan.tage = false;//让中间的cd也暂停
                    }
                } else {//反之
                    binding.imgStart.setImageResource(R.drawable.pausecircle);
                    flag = 1;

                    if (ZiYuan.mediaPlayer != null) {
                        ZiYuan.mediaPlayer.start();

                        ZiYuan.tage = true;
                        new Rotate().execute();
                    }
                }

            }break;

            case R.id.img_down: {//下一首
                if (!ZiYuan.mediaPlayer.isPlaying()) {//同上
                    ZiYuan.mediaPlayer.start();
                    binding.imgStart.setImageResource(R.drawable.pausecircle);
                    ZiYuan.tage = true;
                    new Rotate().execute();
                }

                if (list_type == 1 || list_type == 2) {
                    ZiYuan.index++;
                } else ZiYuan.index = new Random().nextInt(ZiYuan.data.size());

                if (ZiYuan.index > ZiYuan.data.size() - 1) {
                    ZiYuan.index = 0;
                }
                name = new BiaoTi.setName();
                play_music();
            }break;


            case R.id.img_repter: {//播放顺序设置
                switch (list_type) {
                    case 1: {
                        binding.imgRepter.setImageResource(R.drawable.repeat);
                        list_type = 2;
                        Toast.makeText(this, "循环播放", Toast.LENGTH_SHORT).show();
                    }break;

                    case 2: {
                        binding.imgRepter.setImageResource(R.drawable.suij);
                        list_type = 3;
                        Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                    }break;

                    default: {
                        binding.imgRepter.setImageResource(R.drawable.shunx);
                        list_type = 1;
                        Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
                    } break;
                }
            }break;

            case R.id.img_list: {//歌曲列表
                Toast.makeText(this, "功能暂在编码中", Toast.LENGTH_SHORT).show();

            }break;

            default:break;
        }

    }

    @SuppressLint("StaticFieldLeak")
    class Rotate extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            //使图片旋转,和第一个界面倒计时一样的原理
            binding.imgCd.setRotation(values[0]);

            //获得当前音乐播放的值，并放入seekbar中
            binding.seektiao.setProgress(ZiYuan.mediaPlayer.getCurrentPosition());

            //同理左边是和进度条同步的
            binding.textLeft.setText(showtim(ZiYuan.mediaPlayer.getCurrentPosition()));

            //当音乐播放到最大时长时,下一首,减八百时因为有误差
            if (binding.seektiao.getProgress() >= ZiYuan.mediaPlayer.getDuration() - 800) {

                if (ZiYuan.mediaPlayer.isPlaying()) {//要保证音乐在播放才下一首

                    if (list_type == 1) {//如果是顺序播放
                        ZiYuan.index++;//自动下一首
                    } else if (list_type == 3) {//随机播放,设置索引为 [0至总歌曲数量)
                        ZiYuan.index = new Random().nextInt(ZiYuan.data.size());
                    }//循环播放则不用设置

                    if (ZiYuan.index >= ZiYuan.data.size() - 1) {//判断是否是最后一首
                        ZiYuan.index = 0;
                    }

                    name = new BiaoTi.setName();
                    play_music();//播放音频
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (ZiYuan.tage) {//如果是true表示要logd要转到,否则不用
                du += 1;
                publishProgress(du);
                if (du >= 360) {
                    du = 0;
                }
                try {
                    Thread.sleep(50);//休眠
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public void play_music() {//初始化全局音乐播放对象

        //判断音乐播放器是否为空
        if (ZiYuan.mediaPlayer == null) {
            ZiYuan.mediaPlayer = new MediaPlayer();
        }
        //再判断音乐播放器是否在播放
        if (ZiYuan.mediaPlayer.isPlaying()) {//如果是
            ZiYuan.mediaPlayer.stop();//停止播放
            ZiYuan.mediaPlayer.reset();//重置音乐资源
        }

        //设置要播放的音乐文件
        //通过全局静态index和(data中存储的音乐资源)获取绝对路径
        String path = ZiYuan.data.get(ZiYuan.index).get("path");
        try {
            Log.d("TAG", "play_music: " + path);
            ZiYuan.mediaPlayer.setDataSource(path);//根据路径找到资源
            ZiYuan.mediaPlayer.prepare();//初始化音乐资源
            ZiYuan.mediaPlayer.start();//开始播放音乐

            SeekBarZhi();//音乐播放的同时进度条也要跟着开始
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SeekBarZhi() {//设置seekbar的进度条

        int max = ZiYuan.mediaPlayer.getDuration();//获取音乐的最大值
        binding.seektiao.setMax(max);//给进度条设置最大值

        binding.textLeft.setText(showtim(0));//设置左边的初始值
        binding.seektiao.setProgress(0);//设置进度条的初始值
        binding.textRight.setText(showtim(max));//设置右边的初始值
    }

    @Override
    public void onBackPressed() {//监听系统的那个返回键，按左下角返回键，音乐也要停止
        super.onBackPressed();
        ZiYuan.stopmusic();
    }

    //格式化播放时间
    private String showtim(long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        return sdf.format(new Date(time));
    }

}