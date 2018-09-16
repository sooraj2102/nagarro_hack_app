package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.internal.util.atomic.SpscAtomicArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;

public final class OnSubscribePublishMulticast<T> extends AtomicInteger
  implements Observable.OnSubscribe<T>, Observer<T>, Subscription
{
  static final PublishProducer<?>[] EMPTY = new PublishProducer[0];
  static final PublishProducer<?>[] TERMINATED = new PublishProducer[0];
  private static final long serialVersionUID = -3741892510772238743L;
  final boolean delayError;
  volatile boolean done;
  Throwable error;
  final ParentSubscriber<T> parent;
  final int prefetch;
  volatile Producer producer;
  final Queue<T> queue;
  volatile PublishProducer<T>[] subscribers;

  public OnSubscribePublishMulticast(int paramInt, boolean paramBoolean)
  {
    if (paramInt <= 0)
      throw new IllegalArgumentException("prefetch > 0 required but it was " + paramInt);
    this.prefetch = paramInt;
    this.delayError = paramBoolean;
    if (UnsafeAccess.isUnsafeAvailable());
    for (this.queue = new SpscArrayQueue(paramInt); ; this.queue = new SpscAtomicArrayQueue(paramInt))
    {
      this.subscribers = ((PublishProducer[])EMPTY);
      this.parent = new ParentSubscriber(this);
      return;
    }
  }

  boolean add(PublishProducer<T> paramPublishProducer)
  {
    if (this.subscribers == TERMINATED)
      return false;
    monitorenter;
    PublishProducer[] arrayOfPublishProducer1;
    try
    {
      arrayOfPublishProducer1 = this.subscribers;
      if (arrayOfPublishProducer1 == TERMINATED)
        return false;
    }
    finally
    {
      monitorexit;
    }
    int i = arrayOfPublishProducer1.length;
    PublishProducer[] arrayOfPublishProducer2 = new PublishProducer[i + 1];
    System.arraycopy(arrayOfPublishProducer1, 0, arrayOfPublishProducer2, 0, i);
    arrayOfPublishProducer2[i] = paramPublishProducer;
    this.subscribers = arrayOfPublishProducer2;
    monitorexit;
    return true;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    PublishProducer localPublishProducer = new PublishProducer(paramSubscriber, this);
    paramSubscriber.add(localPublishProducer);
    paramSubscriber.setProducer(localPublishProducer);
    if (add(localPublishProducer))
    {
      if (localPublishProducer.isUnsubscribed())
      {
        remove(localPublishProducer);
        return;
      }
      drain();
      return;
    }
    Throwable localThrowable = this.error;
    if (localThrowable != null)
    {
      paramSubscriber.onError(localThrowable);
      return;
    }
    paramSubscriber.onCompleted();
  }

  boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 1;
    if (paramBoolean1)
      if (this.delayError)
      {
        if (paramBoolean2)
        {
          PublishProducer[] arrayOfPublishProducer3 = terminate();
          Throwable localThrowable2 = this.error;
          if (localThrowable2 != null)
          {
            int i3 = arrayOfPublishProducer3.length;
            for (int i4 = 0; i4 < i3; i4++)
              arrayOfPublishProducer3[i4].actual.onError(localThrowable2);
          }
          int i1 = arrayOfPublishProducer3.length;
          for (int i2 = 0; i2 < i1; i2++)
            arrayOfPublishProducer3[i2].actual.onCompleted();
        }
      }
      else
      {
        Throwable localThrowable1 = this.error;
        if (localThrowable1 != null)
        {
          this.queue.clear();
          PublishProducer[] arrayOfPublishProducer2 = terminate();
          int m = arrayOfPublishProducer2.length;
          for (int n = 0; n < m; n++)
            arrayOfPublishProducer2[n].actual.onError(localThrowable1);
        }
        if (paramBoolean2)
        {
          PublishProducer[] arrayOfPublishProducer1 = terminate();
          int j = arrayOfPublishProducer1.length;
          for (int k = 0; k < j; k++)
            arrayOfPublishProducer1[k].actual.onCompleted();
        }
      }
    i = 0;
    return i;
  }

  void drain()
  {
    if (getAndIncrement() != 0)
      return;
    Queue localQueue = this.queue;
    int i = 0;
    label200: label204: label206: label249: 
    do
    {
      long l1 = 9223372036854775807L;
      PublishProducer[] arrayOfPublishProducer = this.subscribers;
      int j = arrayOfPublishProducer.length;
      int k = arrayOfPublishProducer.length;
      for (int m = 0; m < k; m++)
        l1 = Math.min(l1, arrayOfPublishProducer[m].get());
      if (j != 0)
      {
        long l2 = 0L;
        while (true)
        {
          boolean bool1;
          Object localObject;
          if (l2 != l1)
          {
            bool1 = this.done;
            localObject = localQueue.poll();
            if (localObject != null)
              break label200;
          }
          for (boolean bool2 = true; ; bool2 = false)
          {
            if (checkTerminated(bool1, bool2))
              break label204;
            if (!bool2)
              break label206;
            if ((l2 == l1) && (checkTerminated(this.done, localQueue.isEmpty())))
              break;
            if (l2 == 0L)
              break label249;
            Producer localProducer = this.producer;
            if (localProducer != null)
              localProducer.request(l2);
            int n = arrayOfPublishProducer.length;
            for (int i1 = 0; i1 < n; i1++)
              BackpressureUtils.produced(arrayOfPublishProducer[i1], l2);
          }
          break;
          int i2 = arrayOfPublishProducer.length;
          for (int i3 = 0; i3 < i2; i3++)
            arrayOfPublishProducer[i3].actual.onNext(localObject);
          l2 += 1L;
        }
      }
      i = addAndGet(-i);
    }
    while (i != 0);
  }

  public boolean isUnsubscribed()
  {
    return this.parent.isUnsubscribed();
  }

  public void onCompleted()
  {
    this.done = true;
    drain();
  }

  public void onError(Throwable paramThrowable)
  {
    this.error = paramThrowable;
    this.done = true;
    drain();
  }

  public void onNext(T paramT)
  {
    if (!this.queue.offer(paramT))
    {
      this.parent.unsubscribe();
      this.error = new MissingBackpressureException("Queue full?!");
      this.done = true;
    }
    drain();
  }

  void remove(PublishProducer<T> paramPublishProducer)
  {
    PublishProducer[] arrayOfPublishProducer1 = this.subscribers;
    if ((arrayOfPublishProducer1 == TERMINATED) || (arrayOfPublishProducer1 == EMPTY))
      return;
    monitorenter;
    PublishProducer[] arrayOfPublishProducer2;
    try
    {
      arrayOfPublishProducer2 = this.subscribers;
      if ((arrayOfPublishProducer2 == TERMINATED) || (arrayOfPublishProducer2 == EMPTY))
        return;
    }
    finally
    {
      monitorexit;
    }
    int i = -1;
    int j = arrayOfPublishProducer2.length;
    for (int k = 0; ; k++)
    {
      if (k < j)
      {
        if (arrayOfPublishProducer2[k] != paramPublishProducer)
          continue;
        i = k;
      }
      if (i < 0)
      {
        monitorexit;
        return;
      }
      PublishProducer[] arrayOfPublishProducer3;
      if (j == 1)
        arrayOfPublishProducer3 = (PublishProducer[])EMPTY;
      while (true)
      {
        this.subscribers = arrayOfPublishProducer3;
        monitorexit;
        return;
        arrayOfPublishProducer3 = new PublishProducer[j - 1];
        System.arraycopy(arrayOfPublishProducer2, 0, arrayOfPublishProducer3, 0, i);
        System.arraycopy(arrayOfPublishProducer2, i + 1, arrayOfPublishProducer3, i, -1 + (j - i));
      }
    }
  }

  void setProducer(Producer paramProducer)
  {
    this.producer = paramProducer;
    paramProducer.request(this.prefetch);
  }

  public Subscriber<T> subscriber()
  {
    return this.parent;
  }

  PublishProducer<T>[] terminate()
  {
    PublishProducer[] arrayOfPublishProducer1 = this.subscribers;
    if (arrayOfPublishProducer1 != TERMINATED)
    {
      monitorenter;
      try
      {
        PublishProducer[] arrayOfPublishProducer2 = this.subscribers;
        if (arrayOfPublishProducer2 != TERMINATED)
          this.subscribers = ((PublishProducer[])TERMINATED);
        return arrayOfPublishProducer2;
      }
      finally
      {
        monitorexit;
      }
    }
    return arrayOfPublishProducer1;
  }

  public void unsubscribe()
  {
    this.parent.unsubscribe();
  }

  static final class ParentSubscriber<T> extends Subscriber<T>
  {
    final OnSubscribePublishMulticast<T> state;

    public ParentSubscriber(OnSubscribePublishMulticast<T> paramOnSubscribePublishMulticast)
    {
      this.state = paramOnSubscribePublishMulticast;
    }

    public void onCompleted()
    {
      this.state.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      this.state.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      this.state.onNext(paramT);
    }

    public void setProducer(Producer paramProducer)
    {
      this.state.setProducer(paramProducer);
    }
  }

  static final class PublishProducer<T> extends AtomicLong
    implements Producer, Subscription
  {
    private static final long serialVersionUID = 960704844171597367L;
    final Subscriber<? super T> actual;
    final AtomicBoolean once;
    final OnSubscribePublishMulticast<T> parent;

    public PublishProducer(Subscriber<? super T> paramSubscriber, OnSubscribePublishMulticast<T> paramOnSubscribePublishMulticast)
    {
      this.actual = paramSubscriber;
      this.parent = paramOnSubscribePublishMulticast;
      this.once = new AtomicBoolean();
    }

    public boolean isUnsubscribed()
    {
      return this.once.get();
    }

    public void request(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
      if (paramLong != 0L)
      {
        BackpressureUtils.getAndAddRequest(this, paramLong);
        this.parent.drain();
      }
    }

    public void unsubscribe()
    {
      if (this.once.compareAndSet(false, true))
        this.parent.remove(this);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribePublishMulticast
 * JD-Core Version:    0.6.0
 */