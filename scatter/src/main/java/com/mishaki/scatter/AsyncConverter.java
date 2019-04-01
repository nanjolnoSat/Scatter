package com.mishaki.scatter;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class AsyncConverter implements Runnable {
    private ArrayDeque<TaskInfo> taskList = new ArrayDeque<>();

    private Executor executor = Executors.newCachedThreadPool();

    /**
     * 加入子线程任务队列
     */
    void taskQueue(MethodInfo methodInfo, Object[] args) {
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