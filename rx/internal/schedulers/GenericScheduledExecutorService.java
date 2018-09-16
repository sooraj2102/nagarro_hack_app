package rx.internal.schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

public final class GenericScheduledExecutorService
  implements SchedulerLifecycle
{
  public static final GenericScheduledExecutorService INSTANCE;
  private static final ScheduledExecutorService[] NONE = new ScheduledExecutorService[0];
  private static final ScheduledExecutorService SHUTDOWN = Executors.newScheduledThreadPool(0);
  private static int roundRobin;
  private final AtomicReference<ScheduledExecutorService[]> executor = new AtomicReference(NONE);

  static
  {
    SHUTDOWN.shutdown();
    INSTANCE = new GenericScheduledExecutorService();
  }

  private GenericScheduledExecutorService()
  {
    start();
  }

  public static ScheduledExecutorService getInstance()
  {
    ScheduledExecutorService[] arrayOfScheduledExecutorService = (ScheduledExecutorService[])INSTANCE.executor.get();
    if (arrayOfScheduledExecutorService == NONE)
      return SHUTDOWN;
    int i = 1 + roundRobin;
    if (i >= arrayOfScheduledExecutorService.length)
      i = 0;
    roundRobin = i;
    return arrayOfScheduledExecutorService[i];
  }

  public void shutdown()
  {
    ScheduledExecutorService[] arrayOfScheduledExecutorService = (ScheduledExecutorService[])this.executor.get();
    if (arrayOfScheduledExecutorService == NONE);
    while (true)
    {
      return;
      if (!this.executor.compareAndSet(arrayOfScheduledExecutorService, NONE))
        break;
      int i = arrayOfScheduledExecutorService.length;
      for (int j = 0; j < i; j++)
      {
        ScheduledExecutorService localScheduledExecutorService = arrayOfScheduledExecutorService[j];
        NewThreadWorker.deregisterExecutor(localScheduledExecutorService);
        localScheduledExecutorService.shutdownNow();
      }
    }
  }

  public void start()
  {
    int i = Runtime.getRuntime().availableProcessors();
    if (i > 4)
      i /= 2;
    if (i > 8)
      i = 8;
    ScheduledExecutorService[] arrayOfScheduledExecutorService = new ScheduledExecutorService[i];
    for (int j = 0; j < i; j++)
      arrayOfScheduledExecutorService[j] = GenericScheduledExecutorServiceFactory.create();
    if (this.executor.compareAndSet(NONE, arrayOfScheduledExecutorService))
    {
      int n = arrayOfScheduledExecutorService.length;
      for (int i1 = 0; i1 < n; i1++)
      {
        ScheduledExecutorService localScheduledExecutorService = arrayOfScheduledExecutorService[i1];
        if ((NewThreadWorker.tryEnableCancelPolicy(localScheduledExecutorService)) || (!(localScheduledExecutorService instanceof ScheduledThreadPoolExecutor)))
          continue;
        NewThreadWorker.registerExecutor((ScheduledThreadPoolExecutor)localScheduledExecutorService);
      }
    }
    int k = arrayOfScheduledExecutorService.length;
    for (int m = 0; m < k; m++)
      arrayOfScheduledExecutorService[m].shutdownNow();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.GenericScheduledExecutorService
 * JD-Core Version:    0.6.0
 */