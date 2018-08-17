package TestCase;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool implements ThreadPoolService {

    ScheduledExecutorService executor = null;

    public ThreadPool(){
        final ThreadGroup tg = new ThreadGroup("Scheduled Task Threads");
        ThreadFactory f = new ThreadFactory() {
            AtomicInteger id = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(tg, runnable,
                        "Scheduled-" + id.getAndIncrement());
            }
        };
        executor = Executors.newScheduledThreadPool(5, f);
    }
    @Override
    public ScheduledExecutorService getScheduledExecutor() {
        return executor;
    }
}
