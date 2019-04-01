package com.mishaki.scatter;

/**
 * CURRENT:在当前线程执行<br/>
 * MAIN:切换到主线程执行<br/>
 * ASYNC:切换到子线程执行<br/>
 * ASYNC_QUEUE:子线程队列<br/>
 * ASYNC和ASYNC_QUEUE的区别是:ASYNC每个方法都会开启一条线程执行,而ASYNC_QUEUE是等待<br/>
 * 上个方法执行完成后再执行下个方法.可以通过设置priority的属性而执行某个流水线任务
 */
public enum ThreadMode {
    CURRENT,MAIN, ASYNC,ASYNC_QUEUE
}
