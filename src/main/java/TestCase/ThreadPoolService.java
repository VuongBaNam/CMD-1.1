package TestCase;

import java.util.concurrent.ScheduledExecutorService;

public interface ThreadPoolService {

    public ScheduledExecutorService getScheduledExecutor();
}
