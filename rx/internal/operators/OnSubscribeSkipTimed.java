package rx.internal.operators;

import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.functions.Action0;

public final class OnSubscribeSkipTimed<T>
  implements Observable.OnSubscribe<T>
{
  final Scheduler scheduler;
  final Observable<T> source;
  final long time;
  final TimeUnit unit;

  public OnSubscribeSkipTimed(Observable<T> paramObservable, long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    this.source = paramObservable;
    this.time = paramLong;
    this.unit = paramTimeUnit;
    this.scheduler = paramScheduler;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    Scheduler.Worker localWorker = this.scheduler.createWorker();
    SkipTimedSubscriber localSkipTimedSubscriber = new SkipTimedSubscriber(paramSubscriber);
    localSkipTimedSubscriber.add(localWorker);
    paramSubscriber.add(localSkipTimedSubscriber);
    localWorker.schedule(localSkipTimedSubscriber, this.time, this.unit);
    this.source.unsafeSubscribe(localSkipTimedSubscriber);
  }

  static final class SkipTimedSubscriber<T> extends Subscriber<T>
    implements Action0
  {
    final Subscriber<? super T> child;
    volatile boolean gate;

    SkipTimedSubscriber(Subscriber<? super T> paramSubscriber)
    {
      this.child = paramSubscriber;
    }

    public void call()
    {
      this.gate = true;
    }

    public void onCompleted()
    {
      try
      {
        this.child.onCompleted();
        return;
      }
      finally
      {
        unsubscribe();
      }
      throw localObject;
    }

    public void onError(Throwable paramThrowable)
    {
      try
      {
        this.child.onError(paramThrowable);
        return;
      }
      finally
      {
        unsubscribe();
      }
      throw localObject;
    }

    public void onNext(T paramT)
    {
      if (this.gate)
        this.child.onNext(paramT);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeSkipTimed
 * JD-Core Version:    0.6.0
 */