package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.functions.Action0;

public final class OperatorSubscribeOn<T>
  implements Observable.OnSubscribe<T>
{
  final Scheduler scheduler;
  final Observable<T> source;

  public OperatorSubscribeOn(Observable<T> paramObservable, Scheduler paramScheduler)
  {
    this.scheduler = paramScheduler;
    this.source = paramObservable;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    Scheduler.Worker localWorker = this.scheduler.createWorker();
    paramSubscriber.add(localWorker);
    localWorker.schedule(new Action0(paramSubscriber, localWorker)
    {
      public void call()
      {
        Thread localThread = Thread.currentThread();
        1 local1 = new Subscriber(this.val$subscriber, localThread)
        {
          public void onCompleted()
          {
            try
            {
              OperatorSubscribeOn.1.this.val$subscriber.onCompleted();
              return;
            }
            finally
            {
              OperatorSubscribeOn.1.this.val$inner.unsubscribe();
            }
            throw localObject;
          }

          public void onError(Throwable paramThrowable)
          {
            try
            {
              OperatorSubscribeOn.1.this.val$subscriber.onError(paramThrowable);
              return;
            }
            finally
            {
              OperatorSubscribeOn.1.this.val$inner.unsubscribe();
            }
            throw localObject;
          }

          public void onNext(T paramT)
          {
            OperatorSubscribeOn.1.this.val$subscriber.onNext(paramT);
          }

          public void setProducer(Producer paramProducer)
          {
            OperatorSubscribeOn.1.this.val$subscriber.setProducer(new Producer(paramProducer)
            {
              public void request(long paramLong)
              {
                if (OperatorSubscribeOn.1.1.this.val$t == Thread.currentThread())
                {
                  this.val$p.request(paramLong);
                  return;
                }
                OperatorSubscribeOn.1.this.val$inner.schedule(new Action0(paramLong)
                {
                  public void call()
                  {
                    OperatorSubscribeOn.1.1.1.this.val$p.request(this.val$n);
                  }
                });
              }
            });
          }
        };
        OperatorSubscribeOn.this.source.unsafeSubscribe(local1);
      }
    });
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorSubscribeOn
 * JD-Core Version:    0.6.0
 */