package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.Operator;
import rx.Producer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.exceptions.MissingBackpressureException;
import rx.functions.Action0;
import rx.internal.schedulers.ImmediateScheduler;
import rx.internal.schedulers.TrampolineScheduler;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.atomic.SpscAtomicArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

public final class OperatorObserveOn<T>
  implements Observable.Operator<T, T>
{
  private final int bufferSize;
  private final boolean delayError;
  private final Scheduler scheduler;

  public OperatorObserveOn(Scheduler paramScheduler, boolean paramBoolean)
  {
    this(paramScheduler, paramBoolean, RxRingBuffer.SIZE);
  }

  public OperatorObserveOn(Scheduler paramScheduler, boolean paramBoolean, int paramInt)
  {
    this.scheduler = paramScheduler;
    this.delayError = paramBoolean;
    if (paramInt > 0);
    while (true)
    {
      this.bufferSize = paramInt;
      return;
      paramInt = RxRingBuffer.SIZE;
    }
  }

  public static <T> Observable.Operator<T, T> rebatch(int paramInt)
  {
    return new Observable.Operator(paramInt)
    {
      public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
      {
        OperatorObserveOn.ObserveOnSubscriber localObserveOnSubscriber = new OperatorObserveOn.ObserveOnSubscriber(Schedulers.immediate(), paramSubscriber, false, this.val$n);
        localObserveOnSubscriber.init();
        return localObserveOnSubscriber;
      }
    };
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    if ((this.scheduler instanceof ImmediateScheduler));
    do
      return paramSubscriber;
    while ((this.scheduler instanceof TrampolineScheduler));
    ObserveOnSubscriber localObserveOnSubscriber = new ObserveOnSubscriber(this.scheduler, paramSubscriber, this.delayError, this.bufferSize);
    localObserveOnSubscriber.init();
    return localObserveOnSubscriber;
  }

  static final class ObserveOnSubscriber<T> extends Subscriber<T>
    implements Action0
  {
    final Subscriber<? super T> child;
    final AtomicLong counter = new AtomicLong();
    final boolean delayError;
    long emitted;
    Throwable error;
    volatile boolean finished;
    final int limit;
    final NotificationLite<T> on;
    final Queue<Object> queue;
    final Scheduler.Worker recursiveScheduler;
    final AtomicLong requested = new AtomicLong();

    public ObserveOnSubscriber(Scheduler paramScheduler, Subscriber<? super T> paramSubscriber, boolean paramBoolean, int paramInt)
    {
      this.child = paramSubscriber;
      this.recursiveScheduler = paramScheduler.createWorker();
      this.delayError = paramBoolean;
      this.on = NotificationLite.instance();
      int i;
      if (paramInt > 0)
      {
        i = paramInt;
        this.limit = (i - (i >> 2));
        if (!UnsafeAccess.isUnsafeAvailable())
          break label106;
      }
      int j;
      label106: for (this.queue = new SpscArrayQueue(i); ; this.queue = new SpscAtomicArrayQueue(j))
      {
        request(i);
        return;
        j = RxRingBuffer.SIZE;
        break;
      }
    }

    public void call()
    {
      long l1 = 1L;
      long l2 = this.emitted;
      Queue localQueue = this.queue;
      Subscriber localSubscriber = this.child;
      NotificationLite localNotificationLite = this.on;
      long l3 = this.requested.get();
      while (true)
      {
        Object localObject;
        boolean bool2;
        if (l3 != l2)
        {
          boolean bool1 = this.finished;
          localObject = localQueue.poll();
          if (localObject == null)
          {
            bool2 = true;
            if (!checkTerminated(bool1, bool2, localSubscriber, localQueue))
              break label86;
          }
        }
        label86: 
        do
        {
          return;
          bool2 = false;
          break;
          if (!bool2)
            break label142;
        }
        while ((l3 == l2) && (checkTerminated(this.finished, localQueue.isEmpty(), localSubscriber, localQueue)));
        this.emitted = l2;
        l1 = this.counter.addAndGet(-l1);
        if (l1 != 0L)
          break;
        return;
        label142: localSubscriber.onNext(localNotificationLite.getValue(localObject));
        l2 += 1L;
        if (l2 != this.limit)
          continue;
        l3 = BackpressureUtils.produced(this.requested, l2);
        request(l2);
        l2 = 0L;
      }
    }

    boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2, Subscriber<? super T> paramSubscriber, Queue<Object> paramQueue)
    {
      if (paramSubscriber.isUnsubscribed())
      {
        paramQueue.clear();
        return true;
      }
      Throwable localThrowable2;
      if (paramBoolean1)
      {
        if (!this.delayError)
          break label76;
        if (paramBoolean2)
        {
          localThrowable2 = this.error;
          if (localThrowable2 == null)
            break label57;
        }
      }
      label57: label76: 
      do
      {
        try
        {
          paramSubscriber.onError(localThrowable2);
          while (true)
          {
            return false;
            paramSubscriber.onCompleted();
          }
        }
        finally
        {
          this.recursiveScheduler.unsubscribe();
        }
        Throwable localThrowable1 = this.error;
        if (localThrowable1 == null)
          continue;
        paramQueue.clear();
        try
        {
          paramSubscriber.onError(localThrowable1);
          return true;
        }
        finally
        {
          this.recursiveScheduler.unsubscribe();
        }
      }
      while (!paramBoolean2);
      try
      {
        paramSubscriber.onCompleted();
        return true;
      }
      finally
      {
        this.recursiveScheduler.unsubscribe();
      }
      throw localObject1;
    }

    void init()
    {
      Subscriber localSubscriber = this.child;
      localSubscriber.setProducer(new Producer()
      {
        public void request(long paramLong)
        {
          if (paramLong > 0L)
          {
            BackpressureUtils.getAndAddRequest(OperatorObserveOn.ObserveOnSubscriber.this.requested, paramLong);
            OperatorObserveOn.ObserveOnSubscriber.this.schedule();
          }
        }
      });
      localSubscriber.add(this.recursiveScheduler);
      localSubscriber.add(this);
    }

    public void onCompleted()
    {
      if ((isUnsubscribed()) || (this.finished))
        return;
      this.finished = true;
      schedule();
    }

    public void onError(Throwable paramThrowable)
    {
      if ((isUnsubscribed()) || (this.finished))
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.error = paramThrowable;
      this.finished = true;
      schedule();
    }

    public void onNext(T paramT)
    {
      if ((isUnsubscribed()) || (this.finished))
        return;
      if (!this.queue.offer(this.on.next(paramT)))
      {
        onError(new MissingBackpressureException());
        return;
      }
      schedule();
    }

    protected void schedule()
    {
      if (this.counter.getAndIncrement() == 0L)
        this.recursiveScheduler.schedule(this);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorObserveOn
 * JD-Core Version:    0.6.0
 */