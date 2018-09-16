package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.MissingBackpressureException;
import rx.functions.Func1;
import rx.internal.producers.ProducerArbiter;
import rx.internal.util.ExceptionsUtils;
import rx.internal.util.ScalarSynchronousObservable;
import rx.internal.util.atomic.SpscAtomicArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.observers.SerializedSubscriber;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.SerialSubscription;

public final class OnSubscribeConcatMap<T, R>
  implements Observable.OnSubscribe<R>
{
  public static final int BOUNDARY = 1;
  public static final int END = 2;
  public static final int IMMEDIATE;
  final int delayErrorMode;
  final Func1<? super T, ? extends Observable<? extends R>> mapper;
  final int prefetch;
  final Observable<? extends T> source;

  public OnSubscribeConcatMap(Observable<? extends T> paramObservable, Func1<? super T, ? extends Observable<? extends R>> paramFunc1, int paramInt1, int paramInt2)
  {
    this.source = paramObservable;
    this.mapper = paramFunc1;
    this.prefetch = paramInt1;
    this.delayErrorMode = paramInt2;
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    if (this.delayErrorMode == 0);
    for (Object localObject = new SerializedSubscriber(paramSubscriber); ; localObject = paramSubscriber)
    {
      ConcatMapSubscriber localConcatMapSubscriber = new ConcatMapSubscriber((Subscriber)localObject, this.mapper, this.prefetch, this.delayErrorMode);
      paramSubscriber.add(localConcatMapSubscriber);
      paramSubscriber.add(localConcatMapSubscriber.inner);
      paramSubscriber.setProducer(new Producer(localConcatMapSubscriber)
      {
        public void request(long paramLong)
        {
          this.val$parent.requestMore(paramLong);
        }
      });
      if (!paramSubscriber.isUnsubscribed())
        this.source.unsafeSubscribe(localConcatMapSubscriber);
      return;
    }
  }

  static final class ConcatMapInnerScalarProducer<T, R>
    implements Producer
  {
    boolean once;
    final OnSubscribeConcatMap.ConcatMapSubscriber<T, R> parent;
    final R value;

    public ConcatMapInnerScalarProducer(R paramR, OnSubscribeConcatMap.ConcatMapSubscriber<T, R> paramConcatMapSubscriber)
    {
      this.value = paramR;
      this.parent = paramConcatMapSubscriber;
    }

    public void request(long paramLong)
    {
      if ((!this.once) && (paramLong > 0L))
      {
        this.once = true;
        OnSubscribeConcatMap.ConcatMapSubscriber localConcatMapSubscriber = this.parent;
        localConcatMapSubscriber.innerNext(this.value);
        localConcatMapSubscriber.innerCompleted(1L);
      }
    }
  }

  static final class ConcatMapInnerSubscriber<T, R> extends Subscriber<R>
  {
    final OnSubscribeConcatMap.ConcatMapSubscriber<T, R> parent;
    long produced;

    public ConcatMapInnerSubscriber(OnSubscribeConcatMap.ConcatMapSubscriber<T, R> paramConcatMapSubscriber)
    {
      this.parent = paramConcatMapSubscriber;
    }

    public void onCompleted()
    {
      this.parent.innerCompleted(this.produced);
    }

    public void onError(Throwable paramThrowable)
    {
      this.parent.innerError(paramThrowable, this.produced);
    }

    public void onNext(R paramR)
    {
      this.produced = (1L + this.produced);
      this.parent.innerNext(paramR);
    }

    public void setProducer(Producer paramProducer)
    {
      this.parent.arbiter.setProducer(paramProducer);
    }
  }

  static final class ConcatMapSubscriber<T, R> extends Subscriber<T>
  {
    volatile boolean active;
    final Subscriber<? super R> actual;
    final ProducerArbiter arbiter;
    final int delayErrorMode;
    volatile boolean done;
    final AtomicReference<Throwable> error;
    final SerialSubscription inner;
    final Func1<? super T, ? extends Observable<? extends R>> mapper;
    final Queue<Object> queue;
    final AtomicInteger wip;

    public ConcatMapSubscriber(Subscriber<? super R> paramSubscriber, Func1<? super T, ? extends Observable<? extends R>> paramFunc1, int paramInt1, int paramInt2)
    {
      this.actual = paramSubscriber;
      this.mapper = paramFunc1;
      this.delayErrorMode = paramInt2;
      this.arbiter = new ProducerArbiter();
      this.wip = new AtomicInteger();
      this.error = new AtomicReference();
      if (UnsafeAccess.isUnsafeAvailable());
      for (Object localObject = new SpscArrayQueue(paramInt1); ; localObject = new SpscAtomicArrayQueue(paramInt1))
      {
        this.queue = ((Queue)localObject);
        this.inner = new SerialSubscription();
        request(paramInt1);
        return;
      }
    }

    void drain()
    {
      if (this.wip.getAndIncrement() != 0);
      label16: label275: label318: label324: 
      while (true)
      {
        return;
        int i = this.delayErrorMode;
        while (true)
        {
          if (this.actual.isUnsubscribed())
            break label324;
          Observable localObservable;
          if (!this.active)
          {
            if ((i == 1) && (this.error.get() != null))
            {
              Throwable localThrowable3 = ExceptionsUtils.terminate(this.error);
              if (ExceptionsUtils.isTerminated(localThrowable3))
                break;
              this.actual.onError(localThrowable3);
              return;
            }
            boolean bool = this.done;
            Object localObject = this.queue.poll();
            int j;
            if (localObject == null)
              j = 1;
            while (true)
              if ((bool) && (j != 0))
              {
                Throwable localThrowable2 = ExceptionsUtils.terminate(this.error);
                if (localThrowable2 == null)
                {
                  this.actual.onCompleted();
                  return;
                  j = 0;
                  continue;
                }
                if (ExceptionsUtils.isTerminated(localThrowable2))
                  break;
                this.actual.onError(localThrowable2);
                return;
              }
            if (j == 0)
            {
              try
              {
                localObservable = (Observable)this.mapper.call(NotificationLite.instance().getValue(localObject));
                if (localObservable == null)
                {
                  drainError(new NullPointerException("The source returned by the mapper was null"));
                  return;
                }
              }
              catch (Throwable localThrowable1)
              {
                Exceptions.throwIfFatal(localThrowable1);
                drainError(localThrowable1);
                return;
              }
              if (localObservable == Observable.empty())
                break label318;
              if (!(localObservable instanceof ScalarSynchronousObservable))
                break label275;
              ScalarSynchronousObservable localScalarSynchronousObservable = (ScalarSynchronousObservable)localObservable;
              this.active = true;
              this.arbiter.setProducer(new OnSubscribeConcatMap.ConcatMapInnerScalarProducer(localScalarSynchronousObservable.get(), this));
            }
          }
          while (true)
          {
            request(1L);
            if (this.wip.decrementAndGet() != 0)
              break label16;
            return;
            OnSubscribeConcatMap.ConcatMapInnerSubscriber localConcatMapInnerSubscriber = new OnSubscribeConcatMap.ConcatMapInnerSubscriber(this);
            this.inner.set(localConcatMapInnerSubscriber);
            if (localConcatMapInnerSubscriber.isUnsubscribed())
              break;
            this.active = true;
            localObservable.unsafeSubscribe(localConcatMapInnerSubscriber);
          }
          request(1L);
        }
      }
    }

    void drainError(Throwable paramThrowable)
    {
      unsubscribe();
      if (ExceptionsUtils.addThrowable(this.error, paramThrowable))
      {
        Throwable localThrowable = ExceptionsUtils.terminate(this.error);
        if (!ExceptionsUtils.isTerminated(localThrowable))
          this.actual.onError(localThrowable);
        return;
      }
      pluginError(paramThrowable);
    }

    void innerCompleted(long paramLong)
    {
      if (paramLong != 0L)
        this.arbiter.produced(paramLong);
      this.active = false;
      drain();
    }

    void innerError(Throwable paramThrowable, long paramLong)
    {
      if (!ExceptionsUtils.addThrowable(this.error, paramThrowable))
      {
        pluginError(paramThrowable);
        return;
      }
      if (this.delayErrorMode == 0)
      {
        Throwable localThrowable = ExceptionsUtils.terminate(this.error);
        if (!ExceptionsUtils.isTerminated(localThrowable))
          this.actual.onError(localThrowable);
        unsubscribe();
        return;
      }
      if (paramLong != 0L)
        this.arbiter.produced(paramLong);
      this.active = false;
      drain();
    }

    void innerNext(R paramR)
    {
      this.actual.onNext(paramR);
    }

    public void onCompleted()
    {
      this.done = true;
      drain();
    }

    public void onError(Throwable paramThrowable)
    {
      if (ExceptionsUtils.addThrowable(this.error, paramThrowable))
      {
        this.done = true;
        if (this.delayErrorMode == 0)
        {
          Throwable localThrowable = ExceptionsUtils.terminate(this.error);
          if (!ExceptionsUtils.isTerminated(localThrowable))
            this.actual.onError(localThrowable);
          this.inner.unsubscribe();
          return;
        }
        drain();
        return;
      }
      pluginError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      if (!this.queue.offer(NotificationLite.instance().next(paramT)))
      {
        unsubscribe();
        onError(new MissingBackpressureException());
        return;
      }
      drain();
    }

    void pluginError(Throwable paramThrowable)
    {
      RxJavaHooks.onError(paramThrowable);
    }

    void requestMore(long paramLong)
    {
      if (paramLong > 0L)
        this.arbiter.request(paramLong);
      do
        return;
      while (paramLong >= 0L);
      throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeConcatMap
 * JD-Core Version:    0.6.0
 */