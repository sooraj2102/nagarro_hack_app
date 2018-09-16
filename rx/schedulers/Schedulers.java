package rx.schedulers;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import rx.Scheduler;
import rx.annotations.Experimental;
import rx.internal.schedulers.ExecutorScheduler;
import rx.internal.schedulers.GenericScheduledExecutorService;
import rx.internal.schedulers.ImmediateScheduler;
import rx.internal.schedulers.SchedulerLifecycle;
import rx.internal.schedulers.TrampolineScheduler;
import rx.internal.util.ObjectPool;
import rx.internal.util.RxRingBuffer;
import rx.plugins.RxJavaHooks;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;

public final class Schedulers
{
  private static final AtomicReference<Schedulers> INSTANCE = new AtomicReference();
  private final Scheduler computationScheduler;
  private final Scheduler ioScheduler;
  private final Scheduler newThreadScheduler;

  private Schedulers()
  {
    RxJavaSchedulersHook localRxJavaSchedulersHook = RxJavaPlugins.getInstance().getSchedulersHook();
    Scheduler localScheduler1 = localRxJavaSchedulersHook.getComputationScheduler();
    Scheduler localScheduler2;
    if (localScheduler1 != null)
    {
      this.computationScheduler = localScheduler1;
      localScheduler2 = localRxJavaSchedulersHook.getIOScheduler();
      if (localScheduler2 == null)
        break label67;
    }
    label67: for (this.ioScheduler = localScheduler2; ; this.ioScheduler = RxJavaSchedulersHook.createIoScheduler())
    {
      Scheduler localScheduler3 = localRxJavaSchedulersHook.getNewThreadScheduler();
      if (localScheduler3 == null)
        break label77;
      this.newThreadScheduler = localScheduler3;
      return;
      this.computationScheduler = RxJavaSchedulersHook.createComputationScheduler();
      break;
    }
    label77: this.newThreadScheduler = RxJavaSchedulersHook.createNewThreadScheduler();
  }

  public static Scheduler computation()
  {
    return RxJavaHooks.onComputationScheduler(getInstance().computationScheduler);
  }

  public static Scheduler from(Executor paramExecutor)
  {
    return new ExecutorScheduler(paramExecutor);
  }

  private static Schedulers getInstance()
  {
    while (true)
    {
      Schedulers localSchedulers = (Schedulers)INSTANCE.get();
      if (localSchedulers != null);
      do
      {
        return localSchedulers;
        localSchedulers = new Schedulers();
      }
      while (INSTANCE.compareAndSet(null, localSchedulers));
      localSchedulers.shutdownInstance();
    }
  }

  public static Scheduler immediate()
  {
    return ImmediateScheduler.INSTANCE;
  }

  public static Scheduler io()
  {
    return RxJavaHooks.onIOScheduler(getInstance().ioScheduler);
  }

  public static Scheduler newThread()
  {
    return RxJavaHooks.onNewThreadScheduler(getInstance().newThreadScheduler);
  }

  @Experimental
  public static void reset()
  {
    Schedulers localSchedulers = (Schedulers)INSTANCE.getAndSet(null);
    if (localSchedulers != null)
      localSchedulers.shutdownInstance();
  }

  public static void shutdown()
  {
    Schedulers localSchedulers = getInstance();
    localSchedulers.shutdownInstance();
    monitorenter;
    try
    {
      GenericScheduledExecutorService.INSTANCE.shutdown();
      RxRingBuffer.SPSC_POOL.shutdown();
      RxRingBuffer.SPMC_POOL.shutdown();
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public static void start()
  {
    Schedulers localSchedulers = getInstance();
    localSchedulers.startInstance();
    monitorenter;
    try
    {
      GenericScheduledExecutorService.INSTANCE.start();
      RxRingBuffer.SPSC_POOL.start();
      RxRingBuffer.SPMC_POOL.start();
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public static TestScheduler test()
  {
    return new TestScheduler();
  }

  public static Scheduler trampoline()
  {
    return TrampolineScheduler.INSTANCE;
  }

  void shutdownInstance()
  {
    monitorenter;
    try
    {
      if ((this.computationScheduler instanceof SchedulerLifecycle))
        ((SchedulerLifecycle)this.computationScheduler).shutdown();
      if ((this.ioScheduler instanceof SchedulerLifecycle))
        ((SchedulerLifecycle)this.ioScheduler).shutdown();
      if ((this.newThreadScheduler instanceof SchedulerLifecycle))
        ((SchedulerLifecycle)this.newThreadScheduler).shutdown();
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  void startInstance()
  {
    monitorenter;
    try
    {
      if ((this.computationScheduler instanceof SchedulerLifecycle))
        ((SchedulerLifecycle)this.computationScheduler).start();
      if ((this.ioScheduler instanceof SchedulerLifecycle))
        ((SchedulerLifecycle)this.ioScheduler).start();
      if ((this.newThreadScheduler instanceof SchedulerLifecycle))
        ((SchedulerLifecycle)this.newThreadScheduler).start();
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.schedulers.Schedulers
 * JD-Core Version:    0.6.0
 */