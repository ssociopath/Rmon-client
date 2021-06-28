package utils;
import java.util.concurrent.*;

/**
 * @author bobo
 * @date 2021/6/28
 */

public class ThreadPoolUtil {

    private volatile static ThreadPoolExecutor threadPool;
    public static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    public static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    public static final int KEEP_ALIVE_TIME = 1000;
    public static final int BLOCK_QUEUE_SIZE = 1000;

    public static void executor(Runnable runnable) {
        getThreadPoolExecutor().execute(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return getThreadPoolExecutor().submit(callable);
    }

    /**
     * 获取线程池对象
     *
     * @return
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPool != null) {
            return threadPool;
        } else {
            synchronized (ThreadPoolUtil.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(BLOCK_QUEUE_SIZE), new ThreadPoolExecutor.CallerRunsPolicy());
                }
            }
            return threadPool;
        }
    }
}
