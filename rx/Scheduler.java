package rx;

import java.util.concurrent.TimeUnit;
import rx.annotations.Experimental;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.schedulers.SchedulerWhen;
import rx.internal.subscriptions.SequentialSubscription;

public abstract class Scheduler
{
  static final long CLOCK_DRIFT_TOLERANCE_NANOS = TimeUnit.MINUTES.toNanos(Long.getLong("rx.scheduler.drift-tolerance", 15L).longValue());

  public abstract Worker createWorker();

  public long now()
  {
    return System.currentTimeMillis();
  }

  @Experimental
  public <S extends Scheduler,  extends Subscription> S when(Func1<Observable<Observable<Completable>>, Completable> paramFunc1)
  {
    return new SchedulerWhen(paramFunc1, this);
  }

  public static abstract class Worker
    implements Subscription
  {
    public long now()
    {
      return System.currentTimeMillis();
    }

    public abstract Subscription schedule(Action0 paramAction0);

    public abstract Subscription schedule(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit);

    public Subscription schedulePeriodically(Action0 paramAction0, long paramLong1, long paramLong2, TimeUnit paramTimeUnit)
    {
      long l1 = paramTimeUnit.toNanos(paramLong2);
      long l2 = TimeUnit.MILLISECONDS.toNanos(now());
      long l3 = l2 + paramTimeUnit.toNanos(paramLong1);
      SequentialSubscription localSequentialSubscription1 = new SequentialSubscription();
      SequentialSubscription localSequentialSubscription2 = new SequentialSubscription(localSequentialSubscription1);
      localSequentialSubscription1.replace(schedule(new Action0(l2, l3, paramAction0, localSequentialSubscription2, l1)
      {
        long count;
        long lastNowNanos = this.val$firstNowNanos;
        long startInNanos = this.val$firstStartInNanos;

        public void call()
        {
          this.val$action.call();
          long l1;
          long l2;
          if (!this.val$mas.isUnsubscribed())
          {
            l1 = TimeUnit.MILLISECONDS.toNanos(Scheduler.Worker.this.now());
            if ((l1 + Scheduler.CLOCK_DRIFT_TOLERANCE_NANOS >= this.lastNowNanos) && (l1 < this.lastNowNanos + this.val$periodInNanos + Scheduler.CLOCK_DRIFT_TOLERANCE_NANOS))
              break label134;
            l2 = l1 + this.val$periodInNanos;
            long l3 = this.val$periodInNanos;
            long l4 = 1L + this.count;
            this.count = l4;
            this.startInNanos = (l2 - l3 * l4);
          }
          while (true)
          {
            this.lastNowNanos = l1;
            long l5 = l2 - l1;
            this.val$mas.replace(Scheduler.Worker.this.schedule(this, l5, TimeUnit.NANOSECONDS));
            return;
            label134: long l6 = this.startInNanos;
            long l7 = 1L + this.count;
            this.count = l7;
            l2 = l6 + l7 * this.val$periodInNanos;
          }
        }
      }
      , paramLong1, paramTimeUnit));
      return localSequentialSubscription2;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.Scheduler
 * JD-Core Version:    0.6.0
 */