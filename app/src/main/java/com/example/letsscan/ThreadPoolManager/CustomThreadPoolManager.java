package com.example.letsscan.ThreadPoolManager;

import android.os.Message;
import android.util.Log;
import java.lang.Thread;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolManager {
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static int NUMBER_OF_CORES = 2;
    private static CustomThreadPoolManager sInstance = new CustomThreadPoolManager();
    private final ExecutorService mExecutorService;
    private List<Future> mRunningTaskList = new ArrayList();
    private final BlockingQueue<Runnable> mTaskQueue = new LinkedBlockingQueue();
    private WeakReference<UiThreadCallback> uiThreadCallbackWeakReference;

    private CustomThreadPoolManager() {
        Log.e(TP_Util.LOG_TAG, "Available cores: " + NUMBER_OF_CORES);
        this.mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES, 4, 1, KEEP_ALIVE_TIME_UNIT, this.mTaskQueue, new BackgroundThreadFactory());
    }

    public static CustomThreadPoolManager getsInstance() {
        return sInstance;
    }

    public void addCallable(Callable callable) {
        this.mRunningTaskList.add(this.mExecutorService.submit(callable));
    }

    public void cancelAllTasks() {
        synchronized (this) {
            this.mTaskQueue.clear();
            for (Future next : this.mRunningTaskList) {
                if (!next.isDone()) {
                    next.cancel(true);
                }
            }
            this.mRunningTaskList.clear();
        }
        sendMessageToUiThread(TP_Util.createMessage(1, "All tasks in the thread pool are cancelled"));
    }

    public void setUiThreadCallback(UiThreadCallback uiThreadCallback) {
        this.uiThreadCallbackWeakReference = new WeakReference<>(uiThreadCallback);
    }

    public void sendMessageToUiThread(Message message) {
        WeakReference<UiThreadCallback> weakReference = this.uiThreadCallbackWeakReference;
        if (weakReference != null && weakReference.get() != null) {
            ((UiThreadCallback) this.uiThreadCallbackWeakReference.get()).publishToUiThread(message);
        }
    }

    private static class BackgroundThreadFactory implements ThreadFactory {
        private static int sTag = 1;

        private BackgroundThreadFactory() {
        }

        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("CustomThread" + sTag);
            thread.setPriority(10);
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread thread, Throwable th) {
                    Log.e(TP_Util.LOG_TAG, thread.getName() + " encountered an error: " + th.getMessage());
                }
            });
            return thread;
        }
    }
}
