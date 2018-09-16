package rx.internal.schedulers;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.util.RxThreadFactory;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public final class CachedThreadScheduler extends Scheduler
  implements SchedulerLifecycle
{
  private static final long KEEP_ALIVE_TIME = 60L;
  private static final TimeUnit KEEP_ALIVE_UNIT = TimeUnit.SECONDS;
  static final CachedWorkerPool NONE;
  static final ThreadWorker SHUTDOWN_THREADWORKER = new ThreadWorker(RxThreadFactory.NONE);
  final AtomicReference<CachedWorkerPool> pool;
  final ThreadFactory threadFactory;

  static
  {
    SHUTDOWN_THREADWORKER.unsubscribe();
    NONE = new CachedWorkerPool(null, 0L, null);
    NONE.shutdown();
  }

  public CachedThreadScheduler(ThreadFactory paramThreadFactory)
  {
    this.threadFactory = paramThreadFactory;
    this.pool = new AtomicReference(NONE);
    start();
  }

  public Scheduler.Worker createWorker()
  {
    return new EventLoopWorker((CachedWorkerPool)this.pool.get());
  }

  public void shutdown()
  {
    CachedWorkerPool localCachedWorkerPool;
    do
    {
      localCachedWorkerPool = (CachedWorkerPool)this.pool.get();
      if (localCachedWorkerPool == NONE)
        return;
    }
    while (!this.pool.compareAndSet(localCachedWorkerPool, NONE));
    localCachedWorkerPool.shutdown();
  }

  public void start()
  {
    CachedWorkerPool localCachedWorkerPool = new CachedWorkerPool(this.threadFactory, 60L, KEEP_ALIVE_UNIT);
    if (!this.pool.compareAndSet(NONE, localCachedWorkerPool))
      localCachedWorkerPool.shutdown();
  }

  static final class CachedWorkerPool
  {
    private final CompositeSubscription allWorkers;
    private final ScheduledExecutorService evictorService;
    private final Future<?> evictorTask;
    private final ConcurrentLinkedQueue<CachedThreadScheduler.ThreadWorker> expiringWorkerQueue;
    private final long keepAliveTime;
    private final ThreadFactory threadFactory;

    CachedWorkerPool(ThreadFactory paramThreadFactory, long paramLong, TimeUnit paramTimeUnit)
    {
      this.threadFactory = paramThreadFactory;
      long l;
      if (paramTimeUnit != null)
        l = paramTimeUnit.toNanos(paramLong);
      while (true)
      {
        this.keepAliveTime = l;
        this.expiringWorkerQueue = new ConcurrentLinkedQueue();
        this.allWorkers = new CompositeSubscription();
        ScheduledExecutorService localScheduledExecutorService = null;
        ScheduledFuture localScheduledFuture = null;
        if (paramTimeUnit != null)
        {
          localScheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory(paramThreadFactory)
          {
            public Thread newThread(Runnable paramRunnable)
            {
              Thread localThread = this.val$threadFactory.newThread(paramRunnable);
              localThread.setName(localThread.getName() + " (Evictor)");
              return localThread;
            }
          });
          NewThreadWorker.tryEnableCancelPolicy(localScheduledExecutorService);
          localScheduledFuture = localScheduledExecutorService.scheduleWithFixedDelay(new Runnable()
          {
            public void run()
            {
              CachedThreadScheduler.CachedWorkerPool.this.evictExpiredWorkers();
            }
          }
          , this.keepAliveTime, this.keepAliveTime, TimeUnit.NANOSECONDS);
        }
        this.evictorService = localScheduledExecutorService;
        this.evictorTask = localScheduledFuture;
        return;
        l = 0L;
      }
    }

    void evictExpiredWorkers()
    {
      if (!this.expiringWorkerQueue.isEmpty())
      {
        long l = now();
        Iterator localIterator = this.expiringWorkerQueue.iterator();
        while (localIterator.hasNext())
        {
          CachedThreadScheduler.ThreadWorker localThreadWorker = (CachedThreadScheduler.ThreadWorker)localIterator.next();
          if (localThreadWorker.getExpirationTime() > l)
            break;
          if (!this.expiringWorkerQueue.remove(localThreadWorker))
            continue;
          this.allWorkers.remove(localThreadWorker);
        }
      }
    }

    CachedThreadScheduler.ThreadWorker get()
    {
      if (this.allWorkers.isUnsubscribed())
        return CachedThreadScheduler.SHUTDOWN_THREADWORKER;
      while (!this.expiringWorkerQueue.isEmpty())
      {
        CachedThreadScheduler.ThreadWorker localThreadWorker2 = (CachedThreadScheduler.ThreadWorker)this.expiringWorkerQueue.poll();
        if (localThreadWorker2 != null)
          return localThreadWorker2;
      }
      CachedThreadScheduler.ThreadWorker localThreadWorker1 = new CachedThreadScheduler.ThreadWorker(this.threadFactory);
      this.allWorkers.add(localThreadWorker1);
      return localThreadWorker1;
    }

    long now()
    {
      return System.nanoTime();
    }

    void release(CachedThreadScheduler.ThreadWorker paramThreadWorker)
    {
      paramThreadWorker.setExpirationTime(now() + this.keepAliveTime);
      this.expiringWorkerQueue.offer(paramThreadWorker);
    }

    void shutdown()
    {
      try
      {
        if (this.evictorTask != null)
          this.evictorTask.cancel(true);
        if (this.evictorService != null)
          this.evictorService.shutdownNow();
        return;
      }
      finally
      {
        this.allWorkers.unsubscribe();
      }
      throw localObject;
    }
  }

  static final class EventLoopWorker extends Scheduler.Worker
    implements Action0
  {
    private final CompositeSubscription innerSubscription = new CompositeSubscription();
    final AtomicBoolean once;
    private final CachedThreadScheduler.CachedWorkerPool pool;
    private final CachedThreadScheduler.ThreadWorker threadWorker;

    EventLoopWorker(CachedThreadScheduler.CachedWorkerPool paramCachedWorkerPool)
    {
      this.pool = paramCachedWorkerPool;
      this.once = new AtomicBoolean();
      this.threadWorker = paramCachedWorkerPool.get();
    }

    public void call()
    {
      this.pool.release(this.threadWorker);
    }

    public boolean isUnsubscribed()
    {
      return this.innerSubscription.isUnsubscribed();
    }

    public Subscription schedule(Action0 paramAction0)
    {
      return schedule(paramAction0, 0L, null);
    }

    public Subscription schedule(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit)
    {
      if (this.innerSubscription.isUnsubscribed())
        return Subscriptions.unsubscribed();
      ScheduledAction localScheduledAction = this.threadWorker.scheduleActual(new Action0(paramAction0)
      {
        public void call()
        {
          if (CachedThreadScheduler.EventLoopWorker.this.isUnsubscribed())
            return;
          this.val$action.call();
        }
      }
      , paramLong, paramTimeUnit);
      this.innerSubscription.add(localScheduledAction);
      localScheduledAction.addParent(this.innerSubscription);
      return localScheduledAction;
    }

    public void unsubscribe()
    {
      if (this.once.compareAndSet(false, true))
        this.threadWorker.schedule(this);
      this.innerSubscription.unsubscribe();
    }
  }

  static final class ThreadWorker extends NewThreadWorker
  {
    private long expirationTime = 0L;

    ThreadWorker(ThreadFactory paramThreadFactory)
    {
      super();
    }

    public long getExpirationTime()
    {
      return this.expirationTime;
    }

    public void setExpirationTime(long paramLong)
    {
      this.expirationTime = paramLong;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.CachedThreadScheduler
 * JD-Core Version:    0.6.0
 */