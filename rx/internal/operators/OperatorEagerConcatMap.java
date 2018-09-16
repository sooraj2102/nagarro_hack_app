package rx.internal.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.util.atomic.SpscAtomicArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.subscriptions.Subscriptions;

public final class OperatorEagerConcatMap<T, R>
  implements Observable.Operator<R, T>
{
  final int bufferSize;
  final Func1<? super T, ? extends Observable<? extends R>> mapper;
  private final int maxConcurrent;

  public OperatorEagerConcatMap(Func1<? super T, ? extends Observable<? extends R>> paramFunc1, int paramInt1, int paramInt2)
  {
    this.mapper = paramFunc1;
    this.bufferSize = paramInt1;
    this.maxConcurrent = paramInt2;
  }

  public Subscriber<? super T> call(Subscriber<? super R> paramSubscriber)
  {
    EagerOuterSubscriber localEagerOuterSubscriber = new EagerOuterSubscriber(this.mapper, this.bufferSize, this.maxConcurrent, paramSubscriber);
    localEagerOuterSubscriber.init();
    return localEagerOuterSubscriber;
  }

  static final class EagerInnerSubscriber<T> extends Subscriber<T>
  {
    volatile boolean done;
    Throwable error;
    final NotificationLite<T> nl;
    final OperatorEagerConcatMap.EagerOuterSubscriber<?, T> parent;
    final Queue<Object> queue;

    public EagerInnerSubscriber(OperatorEagerConcatMap.EagerOuterSubscriber<?, T> paramEagerOuterSubscriber, int paramInt)
    {
      this.parent = paramEagerOuterSubscriber;
      if (UnsafeAccess.isUnsafeAvailable());
      for (Object localObject = new SpscArrayQueue(paramInt); ; localObject = new SpscAtomicArrayQueue(paramInt))
      {
        this.queue = ((Queue)localObject);
        this.nl = NotificationLite.instance();
        request(paramInt);
        return;
      }
    }

    public void onCompleted()
    {
      this.done = true;
      this.parent.drain();
    }

    public void onError(Throwable paramThrowable)
    {
      this.error = paramThrowable;
      this.done = true;
      this.parent.drain();
    }

    public void onNext(T paramT)
    {
      this.queue.offer(this.nl.next(paramT));
      this.parent.drain();
    }

    void requestMore(long paramLong)
    {
      request(paramLong);
    }
  }

  static final class EagerOuterProducer extends AtomicLong
    implements Producer
  {
    private static final long serialVersionUID = -657299606803478389L;
    final OperatorEagerConcatMap.EagerOuterSubscriber<?, ?> parent;

    public EagerOuterProducer(OperatorEagerConcatMap.EagerOuterSubscriber<?, ?> paramEagerOuterSubscriber)
    {
      this.parent = paramEagerOuterSubscriber;
    }

    public void request(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalStateException("n >= 0 required but it was " + paramLong);
      if (paramLong > 0L)
      {
        BackpressureUtils.getAndAddRequest(this, paramLong);
        this.parent.drain();
      }
    }
  }

  static final class EagerOuterSubscriber<T, R> extends Subscriber<T>
  {
    final Subscriber<? super R> actual;
    final int bufferSize;
    volatile boolean cancelled;
    volatile boolean done;
    Throwable error;
    final Func1<? super T, ? extends Observable<? extends R>> mapper;
    private OperatorEagerConcatMap.EagerOuterProducer sharedProducer;
    final Queue<OperatorEagerConcatMap.EagerInnerSubscriber<R>> subscribers;
    final AtomicInteger wip;

    public EagerOuterSubscriber(Func1<? super T, ? extends Observable<? extends R>> paramFunc1, int paramInt1, int paramInt2, Subscriber<? super R> paramSubscriber)
    {
      this.mapper = paramFunc1;
      this.bufferSize = paramInt1;
      this.actual = paramSubscriber;
      this.subscribers = new LinkedList();
      this.wip = new AtomicInteger();
      long l;
      if (paramInt2 == 2147483647)
        l = 9223372036854775807L;
      while (true)
      {
        request(l);
        return;
        l = paramInt2;
      }
    }

    void cleanup()
    {
      synchronized (this.subscribers)
      {
        ArrayList localArrayList = new ArrayList(this.subscribers);
        this.subscribers.clear();
        Iterator localIterator = localArrayList.iterator();
        if (localIterator.hasNext())
          ((Subscription)localIterator.next()).unsubscribe();
      }
    }

    void drain()
    {
      if (this.wip.getAndIncrement() != 0)
        return;
      int i = 1;
      OperatorEagerConcatMap.EagerOuterProducer localEagerOuterProducer = this.sharedProducer;
      Subscriber localSubscriber = this.actual;
      NotificationLite localNotificationLite = NotificationLite.instance();
      if (this.cancelled)
      {
        cleanup();
        return;
      }
      boolean bool1 = this.done;
      OperatorEagerConcatMap.EagerInnerSubscriber localEagerInnerSubscriber;
      int j;
      while (true)
      {
        synchronized (this.subscribers)
        {
          localEagerInnerSubscriber = (OperatorEagerConcatMap.EagerInnerSubscriber)this.subscribers.peek();
          if (localEagerInnerSubscriber == null)
          {
            j = 1;
            if (!bool1)
              break;
            Throwable localThrowable3 = this.error;
            if (localThrowable3 == null)
              break label121;
            cleanup();
            localSubscriber.onError(localThrowable3);
            return;
          }
        }
        j = 0;
        continue;
        label121: if (j == 0)
          break;
        localSubscriber.onCompleted();
        return;
      }
      long l1;
      long l2;
      Queue localQueue2;
      if (j == 0)
      {
        l1 = localEagerOuterProducer.get();
        l2 = 0L;
        localQueue2 = localEagerInnerSubscriber.queue;
      }
      while (true)
      {
        boolean bool2 = localEagerInnerSubscriber.done;
        Object localObject2 = localQueue2.peek();
        int k;
        if (localObject2 == null)
          k = 1;
        while (bool2)
        {
          Throwable localThrowable2 = localEagerInnerSubscriber.error;
          if (localThrowable2 != null)
          {
            cleanup();
            localSubscriber.onError(localThrowable2);
            return;
            k = 0;
            continue;
          }
          if (k == 0)
            break;
        }
        boolean bool3;
        do
        {
          do
          {
            synchronized (this.subscribers)
            {
              this.subscribers.poll();
              localEagerInnerSubscriber.unsubscribe();
              m = 1;
              request(1L);
              if (l2 != 0L)
              {
                if (l1 != 9223372036854775807L)
                  BackpressureUtils.produced(localEagerOuterProducer, l2);
                if (m == 0)
                  localEagerInnerSubscriber.requestMore(l2);
              }
              if (m != 0)
                break;
              i = this.wip.addAndGet(-i);
              if (i != 0)
                break;
              return;
            }
            m = 0;
          }
          while (k != 0);
          bool3 = l1 < l2;
          int m = 0;
        }
        while (!bool3);
        localQueue2.poll();
        try
        {
          localSubscriber.onNext(localNotificationLite.getValue(localObject2));
          l2 += 1L;
        }
        catch (Throwable localThrowable1)
        {
          Exceptions.throwOrReport(localThrowable1, localSubscriber, localObject2);
        }
      }
    }

    void init()
    {
      this.sharedProducer = new OperatorEagerConcatMap.EagerOuterProducer(this);
      add(Subscriptions.create(new Action0()
      {
        public void call()
        {
          OperatorEagerConcatMap.EagerOuterSubscriber.this.cancelled = true;
          if (OperatorEagerConcatMap.EagerOuterSubscriber.this.wip.getAndIncrement() == 0)
            OperatorEagerConcatMap.EagerOuterSubscriber.this.cleanup();
        }
      }));
      this.actual.add(this);
      this.actual.setProducer(this.sharedProducer);
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
      Observable localObservable;
      OperatorEagerConcatMap.EagerInnerSubscriber localEagerInnerSubscriber;
      do
      {
        try
        {
          localObservable = (Observable)this.mapper.call(paramT);
          if (this.cancelled)
            return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwOrReport(localThrowable, this.actual, paramT);
          return;
        }
        localEagerInnerSubscriber = new OperatorEagerConcatMap.EagerInnerSubscriber(this, this.bufferSize);
        synchronized (this.subscribers)
        {
          if (this.cancelled)
            return;
        }
        this.subscribers.add(localEagerInnerSubscriber);
        monitorexit;
      }
      while (this.cancelled);
      localObservable.unsafeSubscribe(localEagerInnerSubscriber);
      drain();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorEagerConcatMap
 * JD-Core Version:    0.6.0
 */