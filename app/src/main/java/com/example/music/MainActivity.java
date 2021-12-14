package com.example.music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button btn_skip;
    Context context;
    int time = 5;//初始值5秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.guangao);
        btn_skip = findViewById(R.id.btn_skip);
        btn_skip.setOnClickListener(view -> time = -1);

        requestPower();//动态权限获取
    }

    //<子线程初始化传递的参数，子线程运行过程中传递的参数,子线程运行结束后回传的参数>
    @SuppressLint("StaticFieldLeak")
    class Guang extends AsyncTask<Void, Integer, Void> {

        @SuppressLint("SetTextI18n")
        @Override
        //子线程运行过程中数据交互，这个方法可以修改主线程的UI值
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            textView.setText("广告倒计时 " + values[0] + "秒");//因为只有一个整型数,所以是Values[0]
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (time >= 0) {
                //publishProgress方法可以直接将值传给上面的onProgressUpdate方法
                publishProgress(time);
                time--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //当为零秒的时，跳转第二个界面
            Intent intent = new Intent(MainActivity.this, MuiscList.class);
            startActivity(intent);
            finish();
            return null;
        }

    }

    private void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_APN_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            //如果没有权限
            Log.d("TAG", "requestPower: 没有权限");

            //就申请权限
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {//有权限
            Toast.makeText(context, "权限获取成功", Toast.LENGTH_SHORT).show();
        }
    }

    //处理回调,当用户点击拒绝权限时，这里会用到
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //表示授予权限成功
            new Guang().execute();//调用AsyncTask进行倒计时

        } else {
            //表示权限被拒绝
            Log.d("TAG", "权限获取失败: ");
            //强制关闭程序
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}