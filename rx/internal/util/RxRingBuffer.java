package rx.internal.util;

import java.io.PrintStream;
import java.util.Queue;
import rx.Observer;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.internal.operators.NotificationLite;
import rx.internal.util.unsafe.SpmcArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;

public class RxRingBuffer
  implements Subscription
{
  private static final NotificationLite<Object> ON = NotificationLite.instance();
  public static final int SIZE;
  public static final ObjectPool<Queue<Object>> SPMC_POOL;
  public static final ObjectPool<Queue<Object>> SPSC_POOL;
  private final ObjectPool<Queue<Object>> pool;
  private Queue<Object> queue;
  private final int size;
  public volatile Object terminalState;

  static
  {
    int i = 128;
    if (PlatformDependent.isAndroid())
      i = 16;
    String str = System.getProperty("rx.ring-buffer.size");
    if (str != null);
    try
    {
      int j = Integer.parseInt(str);
      i = j;
      SIZE = i;
      SPSC_POOL = new ObjectPool()
      {
        protected SpscArrayQueue<Object> createObject()
        {
          return new SpscArrayQueue(RxRingBuffer.SIZE);
        }
      };
      SPMC_POOL = new ObjectPool()
      {
        protected SpmcArrayQueue<Object> createObject()
        {
          return new SpmcArrayQueue(RxRingBuffer.SIZE);
        }
      };
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      while (true)
        System.err.println("Failed to set 'rx.buffer.size' with value " + str + " => " + localNumberFormatException.getMessage());
    }
  }

  RxRingBuffer()
  {
    this(new SynchronizedQueue(SIZE), SIZE);
  }

  private RxRingBuffer(Queue<Object> paramQueue, int paramInt)
  {
    this.queue = paramQueue;
    this.pool = null;
    this.size = paramInt;
  }

  private RxRingBuffer(ObjectPool<Queue<Object>> paramObjectPool, int paramInt)
  {
    this.pool = paramObjectPool;
    this.queue = ((Queue)paramObjectPool.borrowObject());
    this.size = paramInt;
  }

  public static RxRingBuffer getSpmcInstance()
  {
    if (UnsafeAccess.isUnsafeAvailable())
      return new RxRingBuffer(SPMC_POOL, SIZE);
    return new RxRingBuffer();
  }

  public static RxRingBuffer getSpscInstance()
  {
    if (UnsafeAccess.isUnsafeAvailable())
      return new RxRingBuffer(SPSC_POOL, SIZE);
    return new RxRingBuffer();
  }

  public boolean accept(Object paramObject, Observer paramObserver)
  {
    return ON.accept(paramObserver, paramObject);
  }

  public Throwable asError(Object paramObject)
  {
    return ON.getError(paramObject);
  }

  public int available()
  {
    return this.size - count();
  }

  public int capacity()
  {
    return this.size;
  }

  public int count()
  {
    Queue localQueue = this.queue;
    if (localQueue == null)
      return 0;
    return localQueue.size();
  }

  public Object getValue(Object paramObject)
  {
    return ON.getValue(paramObject);
  }

  public boolean isCompleted(Object paramObject)
  {
    return ON.isCompleted(paramObject);
  }

  public boolean isEmpty()
  {
    Queue localQueue = this.queue;
    return (localQueue == null) || (localQueue.isEmpty());
  }

  public boolean isError(Object paramObject)
  {
    return ON.isError(paramObject);
  }

  public boolean isUnsubscribed()
  {
    return this.queue == null;
  }

  public void onCompleted()
  {
    if (this.terminalState == null)
      this.terminalState = ON.completed();
  }

  public void onError(Throwable paramThrowable)
  {
    if (this.terminalState == null)
      this.terminalState = ON.error(paramThrowable);
  }

  public void onNext(Object paramObject)
    throws MissingBackpressureException
  {
    int i = 0;
    monitorenter;
    int j;
    try
    {
      Queue localQueue = this.queue;
      if (localQueue != null)
        if (!localQueue.offer(ON.next(paramObject)))
          j = 1;
      while (true)
      {
        monitorexit;
        if (i == 0)
          break;
        throw new IllegalStateException("This instance has been unsubscribed and the queue is no longer usable.");
        i = 0;
        j = 0;
        continue;
        i = 1;
        j = 0;
      }
    }
    finally
    {
      monitorexit;
    }
    if (j != 0)
      throw new MissingBackpressureException();
  }

  public Object peek()
  {
    monitorenter;
    try
    {
      Queue localQueue = this.queue;
      if (localQueue == null)
        return null;
      Object localObject2 = localQueue.peek();
      Object localObject3 = this.terminalState;
      if ((localObject2 == null) && (localObject3 != null) && (localQueue.peek() == null))
        localObject2 = localObject3;
      return localObject2;
    }
    finally
    {
      monitorexit;
    }
    throw localObject1;
  }

  public Object poll()
  {
    monitorenter;
    try
    {
      Queue localQueue = this.queue;
      if (localQueue == null)
        return null;
      Object localObject2 = localQueue.poll();
      Object localObject3 = this.terminalState;
      if ((localObject2 == null) && (localObject3 != null) && (localQueue.peek() == null))
      {
        localObject2 = localObject3;
        this.terminalState = null;
      }
      return localObject2;
    }
    finally
    {
      monitorexit;
    }
    throw localObject1;
  }

  public void release()
  {
    monitorenter;
    try
    {
      Queue localQueue = this.queue;
      ObjectPool localObjectPool = this.pool;
      if ((localObjectPool != null) && (localQueue != null))
      {
        localQueue.clear();
        this.queue = null;
        localObjectPool.returnObject(localQueue);
      }
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public void unsubscribe()
  {
    release();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.RxRingBuffer
 * JD-Core Version:    0.6.0
 */