package com.mishaki.scatter;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class MainConverter extends Handler {
    MainConverter() {
        super(Looper.getMainLooper());
    }

    /**
     * 加入主线程执行队列
     */
    void taskQueue(MethodInfo methodInfo,Object[] args){
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.methodInfo = methodInfo;
        taskInfo.args = args;
        Message message = new Message();
        message.obj = taskInfo;
        message.what = methodInfo.invokeObj.hashCode();
        sendMessage(message);
    }

    void release(Object receiverObject){
        removeMessages(receiverObject.hashCode());
    }

    @Override
    public void handleMessage(Message msg) {
        TaskInfo taskInfo = (TaskInfo) msg.obj;
        Scatter.post(taskInfo.methodInfo,taskInfo.args);
    }
}
