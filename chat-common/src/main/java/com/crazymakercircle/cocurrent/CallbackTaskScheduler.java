package com.crazymakercircle.cocurrent;

import com.crazymakercircle.util.ThreadUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Created by 尼恩 at 疯狂创客圈
 */
public class CallbackTaskScheduler  {
    //方法二是使用自建的线程池时，专用于处理耗时操作
    static ListeningExecutorService guavaPool = null;

    static {
        ExecutorService jPool = ThreadUtil.getMixedTargetThreadPool();
        guavaPool = MoreExecutors.listeningDecorator(jPool);
    }


    private CallbackTaskScheduler() {
    }

    /**
     * 添加任务
     * @param executeTask
     */
    public static <R> void add(CallbackTask<R> executeTask) {


        ListenableFuture<R> future = guavaPool.submit(new Callable<R>() {
            public R call() throws Exception {

                R r = executeTask.execute();
                return r;
            }

        });

        Futures.addCallback(future, new FutureCallback<R>() {
            public void onSuccess(R r) {
                executeTask.onBack(r);
            }

            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        });


    }

}
