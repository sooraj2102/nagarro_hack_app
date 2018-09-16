package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.AsyncEmitter;
import rx.AsyncEmitter.BackpressureMode;
import rx.AsyncEmitter.Cancellable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.exceptions.MissingBackpressureException;
import rx.functions.Action1;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.atomic.SpscUnboundedAtomicArrayQueue;
import rx.internal.util.unsafe.SpscUnboundedArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.SerialSubscription;

public final class OnSubscribeFromEmitter<T>
  implements Observable.OnSubscribe<T>
{
  final Action1<AsyncEmitter<T>> asyncEmitter;
  final AsyncEmitter.BackpressureMode backpressure;

  public OnSubscribeFromEmitter(Action1<AsyncEmitter<T>> paramAction1, AsyncEmitter.BackpressureMode paramBackpressureMode)
  {
    this.asyncEmitter = paramAction1;
    this.backpressure = paramBackpressureMode;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    Object localObject;
    switch (1.$SwitchMap$rx$AsyncEmitter$BackpressureMode[this.backpressure.ordinal()])
    {
    default:
      localObject = new BufferAsyncEmitter(paramSubscriber, RxRingBuffer.SIZE);
    case 1:
    case 2:
    case 3:
    case 4:
    }
    while (true)
    {
      paramSubscriber.add((Subscription)localObject);
      paramSubscriber.setProducer((Producer)localObject);
      this.asyncEmitter.call(localObject);
      return;
      localObject = new NoneAsyncEmitter(paramSubscriber);
      continue;
      localObject = new ErrorAsyncEmitter(paramSubscriber);
      continue;
      localObject = new DropAsyncEmitter(paramSubscriber);
      continue;
      localObject = new LatestAsyncEmitter(paramSubscriber);
    }
  }

  static abstract class BaseAsyncEmitter<T> extends AtomicLong
    implements AsyncEmitter<T>, Producer, Subscription
  {
    private static final long serialVersionUID = 7326289992464377023L;
    final Subscriber<? super T> actual;
    final SerialSubscription serial;

    public BaseAsyncEmitter(Subscriber<? super T> paramSubscriber)
    {
      this.actual = paramSubscriber;
      this.serial = new SerialSubscription();
    }

    public final boolean isUnsubscribed()
    {
      return this.serial.isUnsubscribed();
    }

    public void onCompleted()
    {
      if (this.actual.isUnsubscribed())
        return;
      try
      {
        this.actual.onCompleted();
        return;
      }
      finally
      {
        this.serial.unsubscribe();
      }
      throw localObject;
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.actual.isUnsubscribed())
        return;
      try
      {
        this.actual.onError(paramThrowable);
        return;
      }
      finally
      {
        this.serial.unsubscribe();
      }
      throw localObject;
    }

    void onRequested()
    {
    }

    void onUnsubscribed()
    {
    }

    public final void request(long paramLong)
    {
      if (BackpressureUtils.validate(paramLong))
      {
        BackpressureUtils.getAndAddRequest(this, paramLong);
        onRequested();
      }
    }

    public final long requested()
    {
      return get();
    }

    public final void setCancellation(AsyncEmitter.Cancellable paramCancellable)
    {
      setSubscription(new OnSubscribeFromEmitter.CancellableSubscription(paramCancellable));
    }

    public final void setSubscription(Subscription paramSubscription)
    {
      this.serial.set(paramSubscription);
    }

    public final void unsubscribe()
    {
      this.serial.unsubscribe();
      onUnsubscribed();
    }
  }

  static final class BufferAsyncEmitter<T> extends OnSubscribeFromEmitter.BaseAsyncEmitter<T>
  {
    private static final long serialVersionUID = 2427151001689639875L;
    volatile boolean done;
    Throwable error;
    final NotificationLite<T> nl;
    final Queue<Object> queue;
    final AtomicInteger wip;

    public BufferAsyncEmitter(Subscriber<? super T> paramSubscriber, int paramInt)
    {
      super();
      if (UnsafeAccess.isUnsafeAvailable());
      for (Object localObject = new SpscUnboundedArrayQueue(paramInt); ; localObject = new SpscUnboundedAtomicArrayQueue(paramInt))
      {
        this.queue = ((Queue)localObject);
        this.wip = new AtomicInteger();
        this.nl = NotificationLite.instance();
        return;
      }
    }

    void drain()
    {
      if (this.wip.getAndIncrement() != 0)
        return;
      int i = 1;
      Subscriber localSubscriber = this.actual;
      Queue localQueue = this.queue;
      label164: 
      do
      {
        long l1 = get();
        long l2 = 0L;
        while (true)
        {
          Object localObject;
          if (l2 != l1)
          {
            if (localSubscriber.isUnsubscribed())
            {
              localQueue.clear();
              return;
            }
            boolean bool3 = this.done;
            localObject = localQueue.poll();
            int j;
            if (localObject == null)
              j = 1;
            while ((bool3) && (j != 0))
            {
              Throwable localThrowable2 = this.error;
              if (localThrowable2 != null)
              {
                super.onError(localThrowable2);
                return;
                j = 0;
                continue;
              }
              super.onCompleted();
              return;
            }
            if (j == 0);
          }
          else
          {
            if (l2 != l1)
              break;
            if (!localSubscriber.isUnsubscribed())
              break label164;
            localQueue.clear();
            return;
          }
          localSubscriber.onNext(this.nl.getValue(localObject));
          l2 += 1L;
          continue;
          boolean bool1 = this.done;
          boolean bool2 = localQueue.isEmpty();
          if ((!bool1) || (!bool2))
            break;
          Throwable localThrowable1 = this.error;
          if (localThrowable1 != null)
          {
            super.onError(localThrowable1);
            return;
          }
          super.onCompleted();
          return;
        }
        if (l2 != 0L)
          BackpressureUtils.produced(this, l2);
        i = this.wip.addAndGet(-i);
      }
      while (i != 0);
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
      this.queue.offer(this.nl.next(paramT));
      drain();
    }

    void onRequested()
    {
      drain();
    }

    void onUnsubscribed()
    {
      if (this.wip.getAndIncrement() == 0)
        this.queue.clear();
    }
  }

  static final class CancellableSubscription extends AtomicReference<AsyncEmitter.Cancellable>
    implements Subscription
  {
    private static final long serialVersionUID = 5718521705281392066L;

    public CancellableSubscription(AsyncEmitter.Cancellable paramCancellable)
    {
      super();
    }

    public boolean isUnsubscribed()
    {
      return get() == null;
    }

    public void unsubscribe()
    {
      AsyncEmitter.Cancellable localCancellable;
      if (get() != null)
      {
        localCancellable = (AsyncEmitter.Cancellable)getAndSet(null);
        if (localCancellable == null);
      }
      try
      {
        localCancellable.cancel();
        return;
      }
      catch (Exception localException)
      {
        Exceptions.throwIfFatal(localException);
        RxJavaHooks.onError(localException);
      }
    }
  }

  static final class DropAsyncEmitter<T> extends OnSubscribeFromEmitter.NoOverflowBaseAsyncEmitter<T>
  {
    private static final long serialVersionUID = 8360058422307496563L;

    public DropAsyncEmitter(Subscriber<? super T> paramSubscriber)
    {
      super();
    }

    void onOverflow()
    {
    }
  }

  static final class ErrorAsyncEmitter<T> extends OnSubscribeFromEmitter.NoOverflowBaseAsyncEmitter<T>
  {
    private static final long serialVersionUID = 338953216916120960L;
    private boolean done;

    public ErrorAsyncEmitter(Subscriber<? super T> paramSubscriber)
    {
      super();
    }

    public void onCompleted()
    {
      if (this.done)
        return;
      this.done = true;
      super.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.done)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.done = true;
      super.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      if (this.done)
        return;
      super.onNext(paramT);
    }

    void onOverflow()
    {
      onError(new MissingBackpressureException("fromEmitter: could not emit value due to lack of requests"));
    }
  }

  static final class LatestAsyncEmitter<T> extends OnSubscribeFromEmitter.BaseAsyncEmitter<T>
  {
    private static final long serialVersionUID = 4023437720691792495L;
    volatile boolean done;
    Throwable error;
    final NotificationLite<T> nl = NotificationLite.instance();
    final AtomicReference<Object> queue = new AtomicReference();
    final AtomicInteger wip = new AtomicInteger();

    public LatestAsyncEmitter(Subscriber<? super T> paramSubscriber)
    {
      super();
    }

    void drain()
    {
      if (this.wip.getAndIncrement() != 0)
        return;
      int i = 1;
      Subscriber localSubscriber = this.actual;
      AtomicReference localAtomicReference = this.queue;
      label161: 
      do
      {
        long l1 = get();
        long l2 = 0L;
        while (true)
        {
          Object localObject;
          if (l2 != l1)
          {
            if (localSubscriber.isUnsubscribed())
            {
              localAtomicReference.lazySet(null);
              return;
            }
            boolean bool2 = this.done;
            localObject = localAtomicReference.getAndSet(null);
            int k;
            if (localObject == null)
              k = 1;
            while ((bool2) && (k != 0))
            {
              Throwable localThrowable2 = this.error;
              if (localThrowable2 != null)
              {
                super.onError(localThrowable2);
                return;
                k = 0;
                continue;
              }
              super.onCompleted();
              return;
            }
            if (k == 0);
          }
          else
          {
            if (l2 != l1)
              break;
            if (!localSubscriber.isUnsubscribed())
              break label161;
            localAtomicReference.lazySet(null);
            return;
          }
          localSubscriber.onNext(this.nl.getValue(localObject));
          l2 += 1L;
          continue;
          boolean bool1 = this.done;
          int j;
          if (localAtomicReference.get() == null)
            j = 1;
          while ((bool1) && (j != 0))
          {
            Throwable localThrowable1 = this.error;
            if (localThrowable1 != null)
            {
              super.onError(localThrowable1);
              return;
              j = 0;
              continue;
            }
            super.onCompleted();
            return;
          }
        }
        if (l2 != 0L)
          BackpressureUtils.produced(this, l2);
        i = this.wip.addAndGet(-i);
      }
      while (i != 0);
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
      this.queue.set(this.nl.next(paramT));
      drain();
    }

    void onRequested()
    {
      drain();
    }

    void onUnsubscribed()
    {
      if (this.wip.getAndIncrement() == 0)
        this.queue.lazySet(null);
    }
  }

  static abstract class NoOverflowBaseAsyncEmitter<T> extends OnSubscribeFromEmitter.BaseAsyncEmitter<T>
  {
    private static final long serialVersionUID = 4127754106204442833L;

    public NoOverflowBaseAsyncEmitter(Subscriber<? super T> paramSubscriber)
    {
      super();
    }

    public void onNext(T paramT)
    {
      if (this.actual.isUnsubscribed())
        return;
      if (get() != 0L)
      {
        this.actual.onNext(paramT);
        BackpressureUtils.produced(this, 1L);
        return;
      }
      onOverflow();
    }

    abstract void onOverflow();
  }

  static final class NoneAsyncEmitter<T> extends OnSubscribeFromEmitter.BaseAsyncEmitter<T>
  {
    private static final long serialVersionUID = 3776720187248809713L;

    public NoneAsyncEmitter(Subscriber<? super T> paramSubscriber)
    {
      super();
    }

    public void onNext(T paramT)
    {
      if (this.actual.isUnsubscribed())
        return;
      this.actual.onNext(paramT);
      long l;
      do
      {
        l = get();
        if (l == 0L)
          break;
      }
      while (!compareAndSet(l, l - 1L));
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeFromEmitter
 * JD-Core Version:    0.6.0
 */