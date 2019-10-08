package chen.com.library.wifi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

    private ExecutorService executorService;

    public static ThreadPool threadPool;

    private static int MAX_THREAD = Integer.MAX_VALUE;

    public static int BLOCKING_QUEUE_SIZE = Integer.MAX_VALUE;

    public ThreadPool() {
        super();
        executorService = new ThreadPoolExecutor(10, MAX_THREAD, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });
    }

    static {
        threadPool = new ThreadPool();
    }

    public static ThreadPool getInstance() {
        return threadPool;
    }

    public static void execute(Runnable runnable) {
        getInstance().executorService.execute(runnable);
    }

    public static void shutdown() {
        getInstance().executorService.shutdown();
    }

}
