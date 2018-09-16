package rx.internal.util;

import java.util.concurrent.atomic.AtomicBoolean;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.producers.SingleProducer;
import rx.internal.schedulers.EventLoopsScheduler;
import rx.observers.Subscribers;
import rx.plugins.RxJavaHooks;

public final class ScalarSynchronousObservable<T> extends Observable<T>
{
  static final boolean STRONG_MODE = Boolean.valueOf(System.getProperty("rx.just.strong-mode", "false")).booleanValue();
  final T t;

  protected ScalarSynchronousObservable(T paramT)
  {
    super(RxJavaHooks.onCreate(new JustOnSubscribe(paramT)));
    this.t = paramT;
  }

  public static <T> ScalarSynchronousObservable<T> create(T paramT)
  {
    return new ScalarSynchronousObservable(paramT);
  }

  static <T> Producer createProducer(Subscriber<? super T> paramSubscriber, T paramT)
  {
    if (STRONG_MODE)
      return new SingleProducer(paramSubscriber, paramT);
    return new WeakSingleProducer(paramSubscriber, paramT);
  }

  public T get()
  {
    return this.t;
  }

  public <R> Observable<R> scalarFlatMap(Func1<? super T, ? extends Observable<? extends R>> paramFunc1)
  {
    return create(new Observable.OnSubscribe(paramFunc1)
    {
      public void call(Subscriber<? super R> paramSubscriber)
      {
        Observable localObservable = (Observable)this.val$func.call(ScalarSynchronousObservable.this.t);
        if ((localObservable instanceof ScalarSynchronousObservable))
        {
          paramSubscriber.setProducer(ScalarSynchronousObservable.createProducer(paramSubscriber, ((ScalarSynchronousObservable)localObservable).t));
          return;
        }
        localObservable.unsafeSubscribe(Subscribers.wrap(paramSubscriber));
      }
    });
  }

  public Observable<T> scalarScheduleOn(Scheduler paramScheduler)
  {
    if ((paramScheduler instanceof EventLoopsScheduler));
    for (Object localObject = new Func1((EventLoopsScheduler)paramScheduler)
    {
      public Subscription call(Action0 paramAction0)
      {
        return this.val$els.scheduleDirect(paramAction0);
      }
    }
    ; ; localObject = new Func1(paramScheduler)
    {
      public Subscription call(Action0 paramAction0)
      {
        Scheduler.Worker localWorker = this.val$scheduler.createWorker();
        localWorker.schedule(new Action0(paramAction0, localWorker)
        {
          public void call()
          {
            try
            {
              this.val$a.call();
              return;
            }
            finally
            {
              this.val$w.unsubscribe();
            }
            throw localObject;
          }
        });
        return localWorker;
      }
    })
      return create(new ScalarAsyncOnSubscribe(this.t, (Func1)localObject));
  }

  static final class JustOnSubscribe<T>
    implements Observable.OnSubscribe<T>
  {
    final T value;

    JustOnSubscribe(T paramT)
    {
      this.value = paramT;
    }

    public void call(Subscriber<? super T> paramSubscriber)
    {
      paramSubscriber.setProducer(ScalarSynchronousObservable.createProducer(paramSubscriber, this.value));
    }
  }

  static final class ScalarAsyncOnSubscribe<T>
    implements Observable.OnSubscribe<T>
  {
    final Func1<Action0, Subscription> onSchedule;
    final T value;

    ScalarAsyncOnSubscribe(T paramT, Func1<Action0, Subscription> paramFunc1)
    {
      this.value = paramT;
      this.onSchedule = paramFunc1;
    }

    public void call(Subscriber<? super T> paramSubscriber)
    {
      paramSubscriber.setProducer(new ScalarSynchronousObservable.ScalarAsyncProducer(paramSubscriber, this.value, this.onSchedule));
    }
  }

  static final class ScalarAsyncProducer<T> extends AtomicBoolean
    implements Producer, Action0
  {
    private static final long serialVersionUID = -2466317989629281651L;
    final Subscriber<? super T> actual;
    final Func1<Action0, Subscription> onSchedule;
    final T value;

    public ScalarAsyncProducer(Subscriber<? super T> paramSubscriber, T paramT, Func1<Action0, Subscription> paramFunc1)
    {
      this.actual = paramSubscriber;
      this.value = paramT;
      this.onSchedule = paramFunc1;
    }

    public void call()
    {
      Subscriber localSubscriber = this.actual;
      if (localSubscriber.isUnsubscribed());
      while (true)
      {
        return;
        Object localObject = this.value;
        try
        {
          localSubscriber.onNext(localObject);
          if (localSubscriber.isUnsubscribed())
            continue;
          localSubscriber.onCompleted();
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwOrReport(localThrowable, localSubscriber, localObject);
        }
      }
    }

    public void request(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
      if ((paramLong != 0L) && (compareAndSet(false, true)))
        this.actual.add((Subscription)this.onSchedule.call(this));
    }

    public String toString()
    {
      return "ScalarAsyncProducer[" + this.value + ", " + get() + "]";
    }
  }

  static final class WeakSingleProducer<T>
    implements Producer
  {
    final Subscriber<? super T> actual;
    boolean once;
    final T value;

    public WeakSingleProducer(Subscriber<? super T> paramSubscriber, T paramT)
    {
      this.actual = paramSubscriber;
      this.value = paramT;
    }

    public void request(long paramLong)
    {
      if (this.once);
      while (true)
      {
        return;
        if (paramLong < 0L)
          throw new IllegalStateException("n >= required but it was " + paramLong);
        if (paramLong == 0L)
          continue;
        this.once = true;
        Subscriber localSubscriber = this.actual;
        if (localSubscriber.isUnsubscribed())
          continue;
        Object localObject = this.value;
        try
        {
          localSubscriber.onNext(localObject);
          if (localSubscriber.isUnsubscribed())
            continue;
          localSubscriber.onCompleted();
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwOrReport(localThrowable, localSubscriber, localObject);
        }
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.ScalarSynchronousObservable
 * JD-Core Version:    0.6.0
 */