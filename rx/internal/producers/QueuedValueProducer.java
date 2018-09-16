package rx.internal.producers;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.internal.operators.BackpressureUtils;

public final class QueuedValueProducer<T> extends AtomicLong
  implements Producer
{
  static final Object NULL_SENTINEL = new Object();
  private static final long serialVersionUID = 7277121710709137047L;
  final Subscriber<? super T> child;
  final Queue<Object> queue;
  final AtomicInteger wip;

  public QueuedValueProducer(Subscriber<? super T> paramSubscriber)
  {
  }

  public QueuedValueProducer(Subscriber<? super T> paramSubscriber, Queue<Object> paramQueue)
  {
    this.child = paramSubscriber;
    this.queue = paramQueue;
    this.wip = new AtomicInteger();
  }

  private void drain()
  {
    Subscriber localSubscriber;
    Queue localQueue;
    if (this.wip.getAndIncrement() == 0)
    {
      localSubscriber = this.child;
      localQueue = this.queue;
    }
    label44: 
    do
    {
      if (localSubscriber.isUnsubscribed());
      long l2;
      Object localObject1;
      while (true)
      {
        return;
        this.wip.lazySet(1);
        long l1 = get();
        l2 = 0L;
        if (l1 == 0L)
          break label134;
        localObject1 = localQueue.poll();
        if (localObject1 == null)
          break label134;
        try
        {
          if (localObject1 == NULL_SENTINEL)
            localSubscriber.onNext(null);
          while (!localSubscriber.isUnsubscribed())
          {
            l1 -= 1L;
            l2 += 1L;
            break label44;
            Object localObject2 = localObject1;
            localSubscriber.onNext(localObject2);
          }
        }
        catch (Throwable localThrowable)
        {
          if (localObject1 == NULL_SENTINEL);
        }
      }
      while (true)
      {
        Exceptions.throwOrReport(localThrowable, localSubscriber, localObject1);
        return;
        localObject1 = null;
      }
      if ((l2 == 0L) || (get() == 9223372036854775807L))
        continue;
      addAndGet(-l2);
    }
    while (this.wip.decrementAndGet() != 0);
    label134:
  }

  public boolean offer(T paramT)
  {
    if (paramT == null)
    {
      if (this.queue.offer(NULL_SENTINEL));
    }
    else
      do
        return false;
      while (!this.queue.offer(paramT));
    drain();
    return true;
  }

  public void request(long paramLong)
  {
    if (paramLong < 0L)
      throw new IllegalArgumentException("n >= 0 required");
    if (paramLong > 0L)
    {
      BackpressureUtils.getAndAddRequest(this, paramLong);
      drain();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.producers.QueuedValueProducer
 * JD-Core Version:    0.6.0
 */