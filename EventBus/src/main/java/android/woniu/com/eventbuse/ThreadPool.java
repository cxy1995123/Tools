package android.woniu.com.eventbuse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

    private ExecutorService executor;
    private LinkedBlockingQueue<Runnable> blockingQueue;

    public ThreadPool() {
        blockingQueue = new LinkedBlockingQueue<>();
        executor = new ThreadPoolExecutor(8, 24,
                0L, TimeUnit.MILLISECONDS,
                blockingQueue, Executors.defaultThreadFactory());
    }

    public void run(Runnable runnable) {
        executor.execute(runnable);
    }

}
