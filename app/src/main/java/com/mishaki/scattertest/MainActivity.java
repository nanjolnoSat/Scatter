package com.mishaki.scattertest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mishaki.scatter.Receive;
import com.mishaki.scatter.ThreadMode;
import com.mishaki.scatter.util.ScatterUtil;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScatterUtil.register(this);
        //向Tag唯恐的方法签名符合的方法发送事件
        ScatterUtil.post("receiveMethod");
        //向所有方法签名符合的方法发送事件(包括Tag不为空的)
        ScatterUtil.postContainTag("containTag");
        //向所有tag相等的方法签名一样的方法发送事件
        ScatterUtil.postTag("a");
        //向方法签名为空的方法发送事件
        ScatterUtil.post();
    }

    @Receive
    public void receiveMethod(String param){
        Log.v(TAG,"param:" + param);
    }

    @Receive(tag = "a")
    public void tagA() {
        Log.v(TAG, "tagA");
    }

    @Receive(tag = "b")
    public void tagB(String param) {
        Log.v(TAG, "tagB:" + param);
    }

    @Receive(threadMode = ThreadMode.ASYNC)
    public void backgroundThread() {
        Log.v(TAG, "thread:" + Thread.currentThread());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScatterUtil.unregister(this);
    }
}
