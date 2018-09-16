package rx.internal.operators;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.Operator;
import rx.Producer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Func1;

public final class OperatorTakeLastTimed<T>
  implements Observable.Operator<T, T>
{
  final long ageMillis;
  final int count;
  final Scheduler scheduler;

  public OperatorTakeLastTimed(int paramInt, long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("count could not be negative");
    this.ageMillis = paramTimeUnit.toMillis(paramLong);
    this.scheduler = paramScheduler;
    this.count = paramInt;
  }

  public OperatorTakeLastTimed(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    this.ageMillis = paramTimeUnit.toMillis(paramLong);
    this.scheduler = paramScheduler;
    this.count = -1;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    TakeLastTimedSubscriber localTakeLastTimedSubscriber = new TakeLastTimedSubscriber(paramSubscriber, this.count, this.ageMillis, this.scheduler);
    paramSubscriber.add(localTakeLastTimedSubscriber);
    paramSubscriber.setProducer(new Producer(localTakeLastTimedSubscriber)
    {
      public void request(long paramLong)
      {
        this.val$parent.requestMore(paramLong);
      }
    });
    return localTakeLastTimedSubscriber;
  }

  static final class TakeLastTimedSubscriber<T> extends Subscriber<T>
    implements Func1<Object, T>
  {
    final Subscriber<? super T> actual;
    final long ageMillis;
    final int count;
    final NotificationLite<T> nl;
    final ArrayDeque<Object> queue;
    final ArrayDeque<Long> queueTimes;
    final AtomicLong requested;
    final Scheduler scheduler;

    public TakeLastTimedSubscriber(Subscriber<? super T> paramSubscriber, int paramInt, long paramLong, Scheduler paramScheduler)
    {
      this.actual = paramSubscriber;
      this.count = paramInt;
      this.ageMillis = paramLong;
      this.scheduler = paramScheduler;
      this.requested = new AtomicLong();
      this.queue = new ArrayDeque();
      this.queueTimes = new ArrayDeque();
      this.nl = NotificationLite.instance();
    }

    public T call(Object paramObject)
    {
      return this.nl.getValue(paramObject);
    }

    protected void evictOld(long paramLong)
    {
      long l = paramLong - this.ageMillis;
      while (true)
      {
        Long localLong = (Long)this.queueTimes.peek();
        if ((localLong == null) || (localLong.longValue() >= l))
          return;
        this.queue.poll();
        this.queueTimes.poll();
      }
    }

    public void onCompleted()
    {
      evictOld(this.scheduler.now());
      this.queueTimes.clear();
      BackpressureUtils.postCompleteDone(this.requested, this.queue, this.actual, this);
    }

    public void onError(Throwable paramThrowable)
    {
      this.queue.clear();
      this.queueTimes.clear();
      this.actual.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      if (this.count != 0)
      {
        long l = this.scheduler.now();
        if (this.queue.size() == this.count)
        {
          this.queue.poll();
          this.queueTimes.poll();
        }
        evictOld(l);
        this.queue.offer(this.nl.next(paramT));
        this.queueTimes.offer(Long.valueOf(l));
      }
    }

    void requestMore(long paramLong)
    {
      BackpressureUtils.postCompleteRequest(this.requested, paramLong, this.queue, this.actual, this);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorTakeLastTimed
 * JD-Core Version:    0.6.0
 */