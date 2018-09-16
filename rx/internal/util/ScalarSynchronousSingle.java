package rx.internal.util;

import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.schedulers.EventLoopsScheduler;

public final class ScalarSynchronousSingle<T> extends Single<T>
{
  final T value;

  protected ScalarSynchronousSingle(T paramT)
  {
    super(new Single.OnSubscribe()
    {
      public void call(SingleSubscriber<? super T> paramSingleSubscriber)
      {
        paramSingleSubscriber.onSuccess(ScalarSynchronousSingle.this);
      }
    });
    this.value = paramT;
  }

  public static <T> ScalarSynchronousSingle<T> create(T paramT)
  {
    return new ScalarSynchronousSingle(paramT);
  }

  public T get()
  {
    return this.value;
  }

  public <R> Single<R> scalarFlatMap(Func1<? super T, ? extends Single<? extends R>> paramFunc1)
  {
    return create(new Single.OnSubscribe(paramFunc1)
    {
      public void call(SingleSubscriber<? super R> paramSingleSubscriber)
      {
        Single localSingle = (Single)this.val$func.call(ScalarSynchronousSingle.this.value);
        if ((localSingle instanceof ScalarSynchronousSingle))
        {
          paramSingleSubscriber.onSuccess(((ScalarSynchronousSingle)localSingle).value);
          return;
        }
        1 local1 = new Subscriber(paramSingleSubscriber)
        {
          public void onCompleted()
          {
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$child.onError(paramThrowable);
          }

          public void onNext(R paramR)
          {
            this.val$child.onSuccess(paramR);
          }
        };
        paramSingleSubscriber.add(local1);
        localSingle.unsafeSubscribe(local1);
      }
    });
  }

  public Single<T> scalarScheduleOn(Scheduler paramScheduler)
  {
    if ((paramScheduler instanceof EventLoopsScheduler))
      return create(new DirectScheduledEmission((EventLoopsScheduler)paramScheduler, this.value));
    return create(new NormalScheduledEmission(paramScheduler, this.value));
  }

  static final class DirectScheduledEmission<T>
    implements Single.OnSubscribe<T>
  {
    private final EventLoopsScheduler es;
    private final T value;

    DirectScheduledEmission(EventLoopsScheduler paramEventLoopsScheduler, T paramT)
    {
      this.es = paramEventLoopsScheduler;
      this.value = paramT;
    }

    public void call(SingleSubscriber<? super T> paramSingleSubscriber)
    {
      paramSingleSubscriber.add(this.es.scheduleDirect(new ScalarSynchronousSingle.ScalarSynchronousSingleAction(paramSingleSubscriber, this.value)));
    }
  }

  static final class NormalScheduledEmission<T>
    implements Single.OnSubscribe<T>
  {
    private final Scheduler scheduler;
    private final T value;

    NormalScheduledEmission(Scheduler paramScheduler, T paramT)
    {
      this.scheduler = paramScheduler;
      this.value = paramT;
    }

    public void call(SingleSubscriber<? super T> paramSingleSubscriber)
    {
      Scheduler.Worker localWorker = this.scheduler.createWorker();
      paramSingleSubscriber.add(localWorker);
      localWorker.schedule(new ScalarSynchronousSingle.ScalarSynchronousSingleAction(paramSingleSubscriber, this.value));
    }
  }

  static final class ScalarSynchronousSingleAction<T>
    implements Action0
  {
    private final SingleSubscriber<? super T> subscriber;
    private final T value;

    ScalarSynchronousSingleAction(SingleSubscriber<? super T> paramSingleSubscriber, T paramT)
    {
      this.subscriber = paramSingleSubscriber;
      this.value = paramT;
    }

    public void call()
    {
      try
      {
        this.subscriber.onSuccess(this.value);
        return;
      }
      catch (Throwable localThrowable)
      {
        this.subscriber.onError(localThrowable);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.ScalarSynchronousSingle
 * JD-Core Version:    0.6.0
 */