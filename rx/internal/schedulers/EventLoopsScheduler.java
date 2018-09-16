package rx.internal.schedulers;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.util.RxThreadFactory;
import rx.internal.util.SubscriptionList;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public final class EventLoopsScheduler extends Scheduler
  implements SchedulerLifecycle
{
  static final String KEY_MAX_THREADS = "rx.scheduler.max-computation-threads";
  static final int MAX_THREADS;
  static final FixedSchedulerPool NONE;
  static final PoolWorker SHUTDOWN_WORKER;
  final AtomicReference<FixedSchedulerPool> pool;
  final ThreadFactory threadFactory;

  static
  {
    int i = Integer.getInteger("rx.scheduler.max-computation-threads", 0).intValue();
    int j = Runtime.getRuntime().availableProcessors();
    if ((i <= 0) || (i > j));
    for (int k = j; ; k = i)
    {
      MAX_THREADS = k;
      SHUTDOWN_WORKER = new PoolWorker(RxThreadFactory.NONE);
      SHUTDOWN_WORKER.unsubscribe();
      NONE = new FixedSchedulerPool(null, 0);
      return;
    }
  }

  public EventLoopsScheduler(ThreadFactory paramThreadFactory)
  {
    this.threadFactory = paramThreadFactory;
    this.pool = new AtomicReference(NONE);
    start();
  }

  public Scheduler.Worker createWorker()
  {
    return new EventLoopWorker(((FixedSchedulerPool)this.pool.get()).getEventLoop());
  }

  public Subscription scheduleDirect(Action0 paramAction0)
  {
    return ((FixedSchedulerPool)this.pool.get()).getEventLoop().scheduleActual(paramAction0, -1L, TimeUnit.NANOSECONDS);
  }

  public void shutdown()
  {
    FixedSchedulerPool localFixedSchedulerPool;
    do
    {
      localFixedSchedulerPool = (FixedSchedulerPool)this.pool.get();
      if (localFixedSchedulerPool == NONE)
        return;
    }
    while (!this.pool.compareAndSet(localFixedSchedulerPool, NONE));
    localFixedSchedulerPool.shutdown();
  }

  public void start()
  {
    FixedSchedulerPool localFixedSchedulerPool = new FixedSchedulerPool(this.threadFactory, MAX_THREADS);
    if (!this.pool.compareAndSet(NONE, localFixedSchedulerPool))
      localFixedSchedulerPool.shutdown();
  }

  static final class EventLoopWorker extends Scheduler.Worker
  {
    private final SubscriptionList both;
    private final EventLoopsScheduler.PoolWorker poolWorker;
    private final SubscriptionList serial = new SubscriptionList();
    private final CompositeSubscription timed = new CompositeSubscription();

    EventLoopWorker(EventLoopsScheduler.PoolWorker paramPoolWorker)
    {
      Subscription[] arrayOfSubscription = new Subscription[2];
      arrayOfSubscription[0] = this.serial;
      arrayOfSubscription[1] = this.timed;
      this.both = new SubscriptionList(arrayOfSubscription);
      this.poolWorker = paramPoolWorker;
    }

    public boolean isUnsubscribed()
    {
      return this.both.isUnsubscribed();
    }

    public Subscription schedule(Action0 paramAction0)
    {
      if (isUnsubscribed())
        return Subscriptions.unsubscribed();
      return this.poolWorker.scheduleActual(new Action0(paramAction0)
      {
        public void call()
        {
          if (EventLoopsScheduler.EventLoopWorker.this.isUnsubscribed())
            return;
          this.val$action.call();
        }
      }
      , 0L, null, this.serial);
    }

    public Subscription schedule(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit)
    {
      if (isUnsubscribed())
        return Subscriptions.unsubscribed();
      return this.poolWorker.scheduleActual(new Action0(paramAction0)
      {
        public void call()
        {
          if (EventLoopsScheduler.EventLoopWorker.this.isUnsubscribed())
            return;
          this.val$action.call();
        }
      }
      , paramLong, paramTimeUnit, this.timed);
    }

    public void unsubscribe()
    {
      this.both.unsubscribe();
    }
  }

  static final class FixedSchedulerPool
  {
    final int cores;
    final EventLoopsScheduler.PoolWorker[] eventLoops;
    long n;

    FixedSchedulerPool(ThreadFactory paramThreadFactory, int paramInt)
    {
      this.cores = paramInt;
      this.eventLoops = new EventLoopsScheduler.PoolWorker[paramInt];
      for (int i = 0; i < paramInt; i++)
        this.eventLoops[i] = new EventLoopsScheduler.PoolWorker(paramThreadFactory);
    }

    public EventLoopsScheduler.PoolWorker getEventLoop()
    {
      int i = this.cores;
      if (i == 0)
        return EventLoopsScheduler.SHUTDOWN_WORKER;
      EventLoopsScheduler.PoolWorker[] arrayOfPoolWorker = this.eventLoops;
      long l = this.n;
      this.n = (1L + l);
      return arrayOfPoolWorker[(int)(l % i)];
    }

    public void shutdown()
    {
      EventLoopsScheduler.PoolWorker[] arrayOfPoolWorker = this.eventLoops;
      int i = arrayOfPoolWorker.length;
      for (int j = 0; j < i; j++)
        arrayOfPoolWorker[j].unsubscribe();
    }
  }

  static final class PoolWorker extends NewThreadWorker
  {
    PoolWorker(ThreadFactory paramThreadFactory)
    {
      super();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.EventLoopsScheduler
 * JD-Core Version:    0.6.0
 */