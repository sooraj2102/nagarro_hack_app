package rx.internal.operators;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.util.atomic.SpscLinkedArrayQueue;
import rx.subjects.Subject;
import rx.subjects.UnicastSubject;
import rx.subscriptions.Subscriptions;

public final class OperatorWindowWithSize<T>
  implements Observable.Operator<Observable<T>, T>
{
  final int size;
  final int skip;

  public OperatorWindowWithSize(int paramInt1, int paramInt2)
  {
    this.size = paramInt1;
    this.skip = paramInt2;
  }

  public Subscriber<? super T> call(Subscriber<? super Observable<T>> paramSubscriber)
  {
    if (this.skip == this.size)
    {
      WindowExact localWindowExact = new WindowExact(paramSubscriber, this.size);
      paramSubscriber.add(localWindowExact.cancel);
      paramSubscriber.setProducer(localWindowExact.createProducer());
      return localWindowExact;
    }
    if (this.skip > this.size)
    {
      WindowSkip localWindowSkip = new WindowSkip(paramSubscriber, this.size, this.skip);
      paramSubscriber.add(localWindowSkip.cancel);
      paramSubscriber.setProducer(localWindowSkip.createProducer());
      return localWindowSkip;
    }
    WindowOverlap localWindowOverlap = new WindowOverlap(paramSubscriber, this.size, this.skip);
    paramSubscriber.add(localWindowOverlap.cancel);
    paramSubscriber.setProducer(localWindowOverlap.createProducer());
    return localWindowOverlap;
  }

  static final class WindowExact<T> extends Subscriber<T>
    implements Action0
  {
    final Subscriber<? super Observable<T>> actual;
    final Subscription cancel;
    int index;
    final int size;
    Subject<T, T> window;
    final AtomicInteger wip;

    public WindowExact(Subscriber<? super Observable<T>> paramSubscriber, int paramInt)
    {
      this.actual = paramSubscriber;
      this.size = paramInt;
      this.wip = new AtomicInteger(1);
      this.cancel = Subscriptions.create(this);
      add(this.cancel);
      request(0L);
    }

    public void call()
    {
      if (this.wip.decrementAndGet() == 0)
        unsubscribe();
    }

    Producer createProducer()
    {
      return new Producer()
      {
        public void request(long paramLong)
        {
          if (paramLong < 0L)
            throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
          if (paramLong != 0L)
          {
            long l = BackpressureUtils.multiplyCap(OperatorWindowWithSize.WindowExact.this.size, paramLong);
            OperatorWindowWithSize.WindowExact.this.request(l);
          }
        }
      };
    }

    public void onCompleted()
    {
      Subject localSubject = this.window;
      if (localSubject != null)
      {
        this.window = null;
        localSubject.onCompleted();
      }
      this.actual.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      Subject localSubject = this.window;
      if (localSubject != null)
      {
        this.window = null;
        localSubject.onError(paramThrowable);
      }
      this.actual.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      int i = this.index;
      Object localObject = this.window;
      if (i == 0)
      {
        this.wip.getAndIncrement();
        localObject = UnicastSubject.create(this.size, this);
        this.window = ((Subject)localObject);
        this.actual.onNext(localObject);
      }
      int j = i + 1;
      ((Subject)localObject).onNext(paramT);
      if (j == this.size)
      {
        this.index = 0;
        this.window = null;
        ((Subject)localObject).onCompleted();
        return;
      }
      this.index = j;
    }
  }

  static final class WindowOverlap<T> extends Subscriber<T>
    implements Action0
  {
    final Subscriber<? super Observable<T>> actual;
    final Subscription cancel;
    volatile boolean done;
    final AtomicInteger drainWip;
    Throwable error;
    int index;
    int produced;
    final Queue<Subject<T, T>> queue;
    final AtomicLong requested;
    final int size;
    final int skip;
    final ArrayDeque<Subject<T, T>> windows;
    final AtomicInteger wip;

    public WindowOverlap(Subscriber<? super Observable<T>> paramSubscriber, int paramInt1, int paramInt2)
    {
      this.actual = paramSubscriber;
      this.size = paramInt1;
      this.skip = paramInt2;
      this.wip = new AtomicInteger(1);
      this.windows = new ArrayDeque();
      this.drainWip = new AtomicInteger();
      this.requested = new AtomicLong();
      this.cancel = Subscriptions.create(this);
      add(this.cancel);
      request(0L);
      this.queue = new SpscLinkedArrayQueue((paramInt1 + (paramInt2 - 1)) / paramInt2);
    }

    public void call()
    {
      if (this.wip.decrementAndGet() == 0)
        unsubscribe();
    }

    boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2, Subscriber<? super Subject<T, T>> paramSubscriber, Queue<Subject<T, T>> paramQueue)
    {
      if (paramSubscriber.isUnsubscribed())
      {
        paramQueue.clear();
        return true;
      }
      if (paramBoolean1)
      {
        Throwable localThrowable = this.error;
        if (localThrowable != null)
        {
          paramQueue.clear();
          paramSubscriber.onError(localThrowable);
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

    Producer createProducer()
    {
      return new WindowOverlapProducer();
    }

    void drain()
    {
      AtomicInteger localAtomicInteger = this.drainWip;
      if (localAtomicInteger.getAndIncrement() != 0)
        return;
      Subscriber localSubscriber = this.actual;
      Queue localQueue = this.queue;
      int i = 1;
      label26: long l1 = this.requested.get();
      long l2 = 0L;
      while (true)
      {
        boolean bool1;
        Subject localSubject;
        if (l2 != l1)
        {
          bool1 = this.done;
          localSubject = (Subject)localQueue.poll();
          if (localSubject != null)
            break label158;
        }
        label158: for (boolean bool2 = true; ; bool2 = false)
        {
          if (checkTerminated(bool1, bool2, localSubscriber, localQueue))
            break label162;
          if (!bool2)
            break label164;
          if ((l2 == l1) && (checkTerminated(this.done, localQueue.isEmpty(), localSubscriber, localQueue)))
            break;
          if ((l2 != 0L) && (l1 != 9223372036854775807L))
            this.requested.addAndGet(-l2);
          i = localAtomicInteger.addAndGet(-i);
          if (i != 0)
            break label26;
          return;
        }
        label162: break;
        label164: localSubscriber.onNext(localSubject);
        l2 += 1L;
      }
    }

    public void onCompleted()
    {
      Iterator localIterator = this.windows.iterator();
      while (localIterator.hasNext())
        ((Subject)localIterator.next()).onCompleted();
      this.windows.clear();
      this.done = true;
      drain();
    }

    public void onError(Throwable paramThrowable)
    {
      Iterator localIterator = this.windows.iterator();
      while (localIterator.hasNext())
        ((Subject)localIterator.next()).onError(paramThrowable);
      this.windows.clear();
      this.error = paramThrowable;
      this.done = true;
      drain();
    }

    public void onNext(T paramT)
    {
      int i = this.index;
      ArrayDeque localArrayDeque = this.windows;
      if ((i == 0) && (!this.actual.isUnsubscribed()))
      {
        this.wip.getAndIncrement();
        UnicastSubject localUnicastSubject = UnicastSubject.create(16, this);
        localArrayDeque.offer(localUnicastSubject);
        this.queue.offer(localUnicastSubject);
        drain();
      }
      Iterator localIterator = this.windows.iterator();
      while (localIterator.hasNext())
        ((Subject)localIterator.next()).onNext(paramT);
      int j = 1 + this.produced;
      if (j == this.size)
      {
        this.produced = (j - this.skip);
        Subject localSubject = (Subject)localArrayDeque.poll();
        if (localSubject != null)
          localSubject.onCompleted();
      }
      int k;
      while (true)
      {
        k = i + 1;
        if (k != this.skip)
          break;
        this.index = 0;
        return;
        this.produced = j;
      }
      this.index = k;
    }

    final class WindowOverlapProducer extends AtomicBoolean
      implements Producer
    {
      private static final long serialVersionUID = 4625807964358024108L;

      WindowOverlapProducer()
      {
      }

      public void request(long paramLong)
      {
        if (paramLong < 0L)
          throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
        OperatorWindowWithSize.WindowOverlap localWindowOverlap;
        if (paramLong != 0L)
        {
          localWindowOverlap = OperatorWindowWithSize.WindowOverlap.this;
          if ((get()) || (!compareAndSet(false, true)))
            break label97;
          localWindowOverlap.request(BackpressureUtils.addCap(BackpressureUtils.multiplyCap(localWindowOverlap.skip, paramLong - 1L), localWindowOverlap.size));
        }
        while (true)
        {
          BackpressureUtils.getAndAddRequest(localWindowOverlap.requested, paramLong);
          localWindowOverlap.drain();
          return;
          label97: long l = BackpressureUtils.multiplyCap(localWindowOverlap.skip, paramLong);
          OperatorWindowWithSize.WindowOverlap.this.request(l);
        }
      }
    }
  }

  static final class WindowSkip<T> extends Subscriber<T>
    implements Action0
  {
    final Subscriber<? super Observable<T>> actual;
    final Subscription cancel;
    int index;
    final int size;
    final int skip;
    Subject<T, T> window;
    final AtomicInteger wip;

    public WindowSkip(Subscriber<? super Observable<T>> paramSubscriber, int paramInt1, int paramInt2)
    {
      this.actual = paramSubscriber;
      this.size = paramInt1;
      this.skip = paramInt2;
      this.wip = new AtomicInteger(1);
      this.cancel = Subscriptions.create(this);
      add(this.cancel);
      request(0L);
    }

    public void call()
    {
      if (this.wip.decrementAndGet() == 0)
        unsubscribe();
    }

    Producer createProducer()
    {
      return new WindowSkipProducer();
    }

    public void onCompleted()
    {
      Subject localSubject = this.window;
      if (localSubject != null)
      {
        this.window = null;
        localSubject.onCompleted();
      }
      this.actual.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      Subject localSubject = this.window;
      if (localSubject != null)
      {
        this.window = null;
        localSubject.onError(paramThrowable);
      }
      this.actual.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      int i = this.index;
      Object localObject = this.window;
      if (i == 0)
      {
        this.wip.getAndIncrement();
        localObject = UnicastSubject.create(this.size, this);
        this.window = ((Subject)localObject);
        this.actual.onNext(localObject);
      }
      int j = i + 1;
      if (localObject != null)
        ((Subject)localObject).onNext(paramT);
      if (j == this.size)
      {
        this.index = j;
        this.window = null;
        ((Subject)localObject).onCompleted();
        return;
      }
      if (j == this.skip)
      {
        this.index = 0;
        return;
      }
      this.index = j;
    }

    final class WindowSkipProducer extends AtomicBoolean
      implements Producer
    {
      private static final long serialVersionUID = 4625807964358024108L;

      WindowSkipProducer()
      {
      }

      public void request(long paramLong)
      {
        if (paramLong < 0L)
          throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
        OperatorWindowWithSize.WindowSkip localWindowSkip;
        if (paramLong != 0L)
        {
          localWindowSkip = OperatorWindowWithSize.WindowSkip.this;
          if ((!get()) && (compareAndSet(false, true)))
            localWindowSkip.request(BackpressureUtils.addCap(BackpressureUtils.multiplyCap(paramLong, localWindowSkip.size), BackpressureUtils.multiplyCap(localWindowSkip.skip - localWindowSkip.size, paramLong - 1L)));
        }
        else
        {
          return;
        }
        localWindowSkip.request(BackpressureUtils.multiplyCap(paramLong, localWindowSkip.skip));
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorWindowWithSize
 * JD-Core Version:    0.6.0
 */