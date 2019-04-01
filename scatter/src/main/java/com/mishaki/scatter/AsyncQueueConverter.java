package com.mishaki.scatter;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class AsyncQueueConverter implements Runnable {
    private ArrayDeque<TaskInfo> taskList = new ArrayDeque<>();

    private Executor executor = Executors.newSingleThreadExecutor();

    /**
     * 加入单子线程队列
     */
    void taskQueue(MethodInfo methodInfo,Object[] args ) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.args = args;
        taskInfo.methodInfo = methodInfo;
        taskList.add(taskInfo);
        executor.execute(this);
    }

    @Override
    public void run() {
        TaskInfo taskInfo = taskList.remove();
        Scatter.post(taskInfo.methodInfo, taskInfo.args);
    }
}