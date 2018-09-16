package rx.internal.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import rx.internal.schedulers.GenericScheduledExecutorService;
import rx.internal.schedulers.SchedulerLifecycle;
import rx.internal.util.unsafe.MpmcArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.plugins.RxJavaHooks;

public abstract class ObjectPool<T>
  implements SchedulerLifecycle
{
  final int maxSize;
  final int minSize;
  private final AtomicReference<Future<?>> periodicTask;
  Queue<T> pool;
  private final long validationInterval;

  public ObjectPool()
  {
    this(0, 0, 67L);
  }

  private ObjectPool(int paramInt1, int paramInt2, long paramLong)
  {
    this.minSize = paramInt1;
    this.maxSize = paramInt2;
    this.validationInterval = paramLong;
    this.periodicTask = new AtomicReference();
    initialize(paramInt1);
    start();
  }

  private void initialize(int paramInt)
  {
    if (UnsafeAccess.isUnsafeAvailable());
    for (this.pool = new MpmcArrayQueue(Math.max(this.maxSize, 1024)); ; this.pool = new ConcurrentLinkedQueue())
      for (int i = 0; i < paramInt; i++)
        this.pool.add(createObject());
  }

  public T borrowObject()
  {
    Object localObject = this.pool.poll();
    if (localObject == null)
      localObject = createObject();
    return localObject;
  }

  protected abstract T createObject();

  public void returnObject(T paramT)
  {
    if (paramT == null)
      return;
    this.pool.offer(paramT);
  }

  public void shutdown()
  {
    Future localFuture = (Future)this.periodicTask.getAndSet(null);
    if (localFuture != null)
      localFuture.cancel(false);
  }

  public void start()
  {
    while (true)
    {
      if (this.periodicTask.get() != null)
        label10: return;
      ScheduledExecutorService localScheduledExecutorService = GenericScheduledExecutorService.getInstance();
      try
      {
        ScheduledFuture localScheduledFuture = localScheduledExecutorService.scheduleAtFixedRate(new Runnable()
        {
          public void run()
          {
            int i = ObjectPool.this.pool.size();
            if (i < ObjectPool.this.minSize)
            {
              int m = ObjectPool.this.maxSize - i;
              for (int n = 0; n < m; n++)
                ObjectPool.this.pool.add(ObjectPool.this.createObject());
            }
            if (i > ObjectPool.this.maxSize)
            {
              int j = i - ObjectPool.this.maxSize;
              for (int k = 0; k < j; k++)
                ObjectPool.this.pool.poll();
            }
          }
        }
        , this.validationInterval, this.validationInterval, TimeUnit.SECONDS);
        if (this.periodicTask.compareAndSet(null, localScheduledFuture))
          break label10;
        localScheduledFuture.cancel(false);
      }
      catch (RejectedExecutionException localRejectedExecutionException)
      {
        RxJavaHooks.onError(localRejectedExecutionException);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.ObjectPool
 * JD-Core Version:    0.6.0
 */