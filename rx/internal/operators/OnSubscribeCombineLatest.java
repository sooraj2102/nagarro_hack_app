package rx.internal.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.functions.FuncN;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.atomic.SpscLinkedArrayQueue;
import rx.plugins.RxJavaHooks;

public final class OnSubscribeCombineLatest<T, R>
  implements Observable.OnSubscribe<R>
{
  final int bufferSize;
  final FuncN<? extends R> combiner;
  final boolean delayError;
  final Observable<? extends T>[] sources;
  final Iterable<? extends Observable<? extends T>> sourcesIterable;

  public OnSubscribeCombineLatest(Iterable<? extends Observable<? extends T>> paramIterable, FuncN<? extends R> paramFuncN)
  {
    this(null, paramIterable, paramFuncN, RxRingBuffer.SIZE, false);
  }

  public OnSubscribeCombineLatest(Observable<? extends T>[] paramArrayOfObservable, Iterable<? extends Observable<? extends T>> paramIterable, FuncN<? extends R> paramFuncN, int paramInt, boolean paramBoolean)
  {
    this.sources = paramArrayOfObservable;
    this.sourcesIterable = paramIterable;
    this.combiner = paramFuncN;
    this.bufferSize = paramInt;
    this.delayError = paramBoolean;
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    Object localObject = this.sources;
    int i = 0;
    if (localObject == null)
      if ((this.sourcesIterable instanceof List))
      {
        List localList = (List)this.sourcesIterable;
        localObject = (Observable[])(Observable[])localList.toArray(new Observable[localList.size()]);
        i = localObject.length;
      }
    while (i == 0)
    {
      paramSubscriber.onCompleted();
      return;
      localObject = new Observable[8];
      Iterator localIterator = this.sourcesIterable.iterator();
      while (localIterator.hasNext())
      {
        Observable localObservable = (Observable)localIterator.next();
        if (i == localObject.length)
        {
          Observable[] arrayOfObservable = new Observable[i + (i >> 2)];
          System.arraycopy(localObject, 0, arrayOfObservable, 0, i);
          localObject = arrayOfObservable;
        }
        int j = i + 1;
        localObject[i] = localObservable;
        i = j;
      }
      continue;
      i = localObject.length;
    }
    new LatestCoordinator(paramSubscriber, this.combiner, i, this.bufferSize, this.delayError).subscribe(localObject);
  }

  static final class CombinerSubscriber<T, R> extends Subscriber<T>
  {
    boolean done;
    final int index;
    final NotificationLite<T> nl;
    final OnSubscribeCombineLatest.LatestCoordinator<T, R> parent;

    public CombinerSubscriber(OnSubscribeCombineLatest.LatestCoordinator<T, R> paramLatestCoordinator, int paramInt)
    {
      this.parent = paramLatestCoordinator;
      this.index = paramInt;
      this.nl = NotificationLite.instance();
      request(paramLatestCoordinator.bufferSize);
    }

    public void onCompleted()
    {
      if (this.done)
        return;
      this.done = true;
      this.parent.combine(null, this.index);
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.done)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.parent.onError(paramThrowable);
      this.done = true;
      this.parent.combine(null, this.index);
    }

    public void onNext(T paramT)
    {
      if (this.done)
        return;
      this.parent.combine(this.nl.next(paramT), this.index);
    }

    public void requestMore(long paramLong)
    {
      request(paramLong);
    }
  }

  static final class LatestCoordinator<T, R> extends AtomicInteger
    implements Producer, Subscription
  {
    static final Object MISSING = new Object();
    private static final long serialVersionUID = 8567835998786448817L;
    int active;
    final Subscriber<? super R> actual;
    final int bufferSize;
    volatile boolean cancelled;
    final FuncN<? extends R> combiner;
    int complete;
    final boolean delayError;
    volatile boolean done;
    final AtomicReference<Throwable> error;
    final Object[] latest;
    final SpscLinkedArrayQueue<Object> queue;
    final AtomicLong requested;
    final OnSubscribeCombineLatest.CombinerSubscriber<T, R>[] subscribers;

    public LatestCoordinator(Subscriber<? super R> paramSubscriber, FuncN<? extends R> paramFuncN, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.actual = paramSubscriber;
      this.combiner = paramFuncN;
      this.bufferSize = paramInt2;
      this.delayError = paramBoolean;
      this.latest = new Object[paramInt1];
      Arrays.fill(this.latest, MISSING);
      this.subscribers = new OnSubscribeCombineLatest.CombinerSubscriber[paramInt1];
      this.queue = new SpscLinkedArrayQueue(paramInt2);
      this.requested = new AtomicLong();
      this.error = new AtomicReference();
    }

    void cancel(Queue<?> paramQueue)
    {
      paramQueue.clear();
      OnSubscribeCombineLatest.CombinerSubscriber[] arrayOfCombinerSubscriber = this.subscribers;
      int i = arrayOfCombinerSubscriber.length;
      for (int j = 0; j < i; j++)
        arrayOfCombinerSubscriber[j].unsubscribe();
    }

    boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2, Subscriber<?> paramSubscriber, Queue<?> paramQueue, boolean paramBoolean3)
    {
      if (this.cancelled)
      {
        cancel(paramQueue);
        return true;
      }
      if (paramBoolean1)
        if (paramBoolean3)
        {
          if (paramBoolean2)
          {
            Throwable localThrowable2 = (Throwable)this.error.get();
            if (localThrowable2 != null)
            {
              paramSubscriber.onError(localThrowable2);
              return true;
            }
            paramSubscriber.onCompleted();
            return true;
          }
        }
        else
        {
          Throwable localThrowable1 = (Throwable)this.error.get();
          if (localThrowable1 != null)
          {
            cancel(paramQueue);
            paramSubscriber.onError(localThrowable1);
            return true;
          }
          if (paramBoolean2)
          {
            paramSubscriber.onCompleted();
            return true;
          }
        }
      return false;
    }

    void combine(Object paramObject, int paramInt)
    {
      OnSubscribeCombineLatest.CombinerSubscriber localCombinerSubscriber = this.subscribers[paramInt];
      monitorenter;
      while (true)
      {
        int i;
        Object localObject2;
        int j;
        try
        {
          i = this.latest.length;
          localObject2 = this.latest[paramInt];
          j = this.active;
          if (localObject2 != MISSING)
            continue;
          j++;
          this.active = j;
          int k = this.complete;
          if (paramObject != null)
            continue;
          k++;
          this.complete = k;
          break label228;
          if (k == i)
            break label241;
          n = 0;
          if (paramObject != null)
            continue;
          Object localObject3 = MISSING;
          n = 0;
          if (localObject2 == localObject3)
            break label241;
          if (n == 0)
            if ((paramObject != null) && (m != 0))
            {
              this.queue.offer(localCombinerSubscriber, this.latest.clone());
              label131: monitorexit;
              if ((m != 0) || (paramObject == null))
                break label223;
              localCombinerSubscriber.requestMore(1L);
              return;
              this.latest[paramInt] = localCombinerSubscriber.nl.getValue(paramObject);
            }
        }
        finally
        {
          monitorexit;
        }
        label223: label228: 
        do
        {
          m = 0;
          break;
          if ((paramObject != null) || (this.error.get() == null) || ((localObject2 != MISSING) && (this.delayError)))
            break label131;
          this.done = true;
          break label131;
          this.done = true;
          break label131;
          drain();
          return;
        }
        while (j != i);
        int m = 1;
        continue;
        label241: int n = 1;
      }
    }

    void drain()
    {
      if (getAndIncrement() != 0)
        label7: return;
      SpscLinkedArrayQueue localSpscLinkedArrayQueue;
      Subscriber localSubscriber;
      boolean bool1;
      AtomicLong localAtomicLong;
      int i;
      while (checkTerminated(this.done, localSpscLinkedArrayQueue.isEmpty(), localSubscriber, localSpscLinkedArrayQueue, bool1))
      {
        localSpscLinkedArrayQueue = this.queue;
        localSubscriber = this.actual;
        bool1 = this.delayError;
        localAtomicLong = this.requested;
        i = 1;
      }
      long l1 = localAtomicLong.get();
      long l2 = 0L;
      while (true)
      {
        boolean bool2;
        OnSubscribeCombineLatest.CombinerSubscriber localCombinerSubscriber;
        if (l2 != l1)
        {
          bool2 = this.done;
          localCombinerSubscriber = (OnSubscribeCombineLatest.CombinerSubscriber)localSpscLinkedArrayQueue.peek();
          if (localCombinerSubscriber != null)
            break label153;
        }
        label153: for (boolean bool3 = true; ; bool3 = false)
        {
          if (checkTerminated(bool2, bool3, localSubscriber, localSpscLinkedArrayQueue, bool1))
            break label157;
          if (!bool3)
            break label159;
          if ((l2 != 0L) && (l1 != 9223372036854775807L))
            BackpressureUtils.produced(localAtomicLong, l2);
          i = addAndGet(-i);
          if (i != 0)
            break;
          return;
        }
        label157: break label7;
        label159: localSpscLinkedArrayQueue.poll();
        Object[] arrayOfObject = (Object[])(Object[])localSpscLinkedArrayQueue.poll();
        if (arrayOfObject == null)
        {
          this.cancelled = true;
          cancel(localSpscLinkedArrayQueue);
          localSubscriber.onError(new IllegalStateException("Broken queue?! Sender received but not the array."));
          return;
        }
        try
        {
          Object localObject = this.combiner.call(arrayOfObject);
          localSubscriber.onNext(localObject);
          localCombinerSubscriber.requestMore(1L);
          l2 += 1L;
        }
        catch (Throwable localThrowable)
        {
          this.cancelled = true;
          cancel(localSpscLinkedArrayQueue);
          localSubscriber.onError(localThrowable);
        }
      }
    }

    public boolean isUnsubscribed()
    {
      return this.cancelled;
    }

    void onError(Throwable paramThrowable)
    {
      AtomicReference localAtomicReference = this.error;
      while (true)
      {
        Throwable localThrowable = (Throwable)localAtomicReference.get();
        Object localObject;
        if (localThrowable != null)
          if ((localThrowable instanceof CompositeException))
          {
            ArrayList localArrayList = new ArrayList(((CompositeException)localThrowable).getExceptions());
            localArrayList.add(paramThrowable);
            localObject = new CompositeException(localArrayList);
          }
        while (localAtomicReference.compareAndSet(localThrowable, localObject))
        {
          return;
          localObject = new CompositeException(Arrays.asList(new Throwable[] { localThrowable, paramThrowable }));
          continue;
          localObject = paramThrowable;
        }
      }
    }

    public void request(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= required but it was " + paramLong);
      if (paramLong != 0L)
      {
        BackpressureUtils.getAndAddRequest(this.requested, paramLong);
        drain();
      }
    }

    public void subscribe(Observable<? extends T>[] paramArrayOfObservable)
    {
      OnSubscribeCombineLatest.CombinerSubscriber[] arrayOfCombinerSubscriber = this.subscribers;
      int i = arrayOfCombinerSubscriber.length;
      for (int j = 0; j < i; j++)
        arrayOfCombinerSubscriber[j] = new OnSubscribeCombineLatest.CombinerSubscriber(this, j);
      lazySet(0);
      this.actual.add(this);
      this.actual.setProducer(this);
      for (int k = 0; ; k++)
      {
        if ((k >= i) || (this.cancelled))
          return;
        paramArrayOfObservable[k].subscribe(arrayOfCombinerSubscriber[k]);
      }
    }

    public void unsubscribe()
    {
      if (!this.cancelled)
      {
        this.cancelled = true;
        if (getAndIncrement() == 0)
          cancel(this.queue);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeCombineLatest
 * JD-Core Version:    0.6.0
 */