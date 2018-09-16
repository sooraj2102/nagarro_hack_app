package rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import rx.Notification;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Producer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.producers.ProducerArbiter;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.SerialSubscription;

public final class OnSubscribeRedo<T>
  implements Observable.OnSubscribe<T>
{
  static final Func1<Observable<? extends Notification<?>>, Observable<?>> REDO_INFINITE = new Func1()
  {
    public Observable<?> call(Observable<? extends Notification<?>> paramObservable)
    {
      return paramObservable.map(new Func1()
      {
        public Notification<?> call(Notification<?> paramNotification)
        {
          return Notification.createOnNext(null);
        }
      });
    }
  };
  private final Func1<? super Observable<? extends Notification<?>>, ? extends Observable<?>> controlHandlerFunction;
  private final Scheduler scheduler;
  final Observable<T> source;
  final boolean stopOnComplete;
  final boolean stopOnError;

  private OnSubscribeRedo(Observable<T> paramObservable, Func1<? super Observable<? extends Notification<?>>, ? extends Observable<?>> paramFunc1, boolean paramBoolean1, boolean paramBoolean2, Scheduler paramScheduler)
  {
    this.source = paramObservable;
    this.controlHandlerFunction = paramFunc1;
    this.stopOnComplete = paramBoolean1;
    this.stopOnError = paramBoolean2;
    this.scheduler = paramScheduler;
  }

  public static <T> Observable<T> redo(Observable<T> paramObservable, Func1<? super Observable<? extends Notification<?>>, ? extends Observable<?>> paramFunc1, Scheduler paramScheduler)
  {
    return Observable.create(new OnSubscribeRedo(paramObservable, paramFunc1, false, false, paramScheduler));
  }

  public static <T> Observable<T> repeat(Observable<T> paramObservable)
  {
    return repeat(paramObservable, Schedulers.trampoline());
  }

  public static <T> Observable<T> repeat(Observable<T> paramObservable, long paramLong)
  {
    return repeat(paramObservable, paramLong, Schedulers.trampoline());
  }

  public static <T> Observable<T> repeat(Observable<T> paramObservable, long paramLong, Scheduler paramScheduler)
  {
    if (paramLong == 0L)
      return Observable.empty();
    if (paramLong < 0L)
      throw new IllegalArgumentException("count >= 0 expected");
    return repeat(paramObservable, new RedoFinite(paramLong - 1L), paramScheduler);
  }

  public static <T> Observable<T> repeat(Observable<T> paramObservable, Scheduler paramScheduler)
  {
    return repeat(paramObservable, REDO_INFINITE, paramScheduler);
  }

  public static <T> Observable<T> repeat(Observable<T> paramObservable, Func1<? super Observable<? extends Notification<?>>, ? extends Observable<?>> paramFunc1)
  {
    return Observable.create(new OnSubscribeRedo(paramObservable, paramFunc1, false, true, Schedulers.trampoline()));
  }

  public static <T> Observable<T> repeat(Observable<T> paramObservable, Func1<? super Observable<? extends Notification<?>>, ? extends Observable<?>> paramFunc1, Scheduler paramScheduler)
  {
    return Observable.create(new OnSubscribeRedo(paramObservable, paramFunc1, false, true, paramScheduler));
  }

  public static <T> Observable<T> retry(Observable<T> paramObservable)
  {
    return retry(paramObservable, REDO_INFINITE);
  }

  public static <T> Observable<T> retry(Observable<T> paramObservable, long paramLong)
  {
    if (paramLong < 0L)
      throw new IllegalArgumentException("count >= 0 expected");
    if (paramLong == 0L)
      return paramObservable;
    return retry(paramObservable, new RedoFinite(paramLong));
  }

  public static <T> Observable<T> retry(Observable<T> paramObservable, Func1<? super Observable<? extends Notification<?>>, ? extends Observable<?>> paramFunc1)
  {
    return Observable.create(new OnSubscribeRedo(paramObservable, paramFunc1, true, false, Schedulers.trampoline()));
  }

  public static <T> Observable<T> retry(Observable<T> paramObservable, Func1<? super Observable<? extends Notification<?>>, ? extends Observable<?>> paramFunc1, Scheduler paramScheduler)
  {
    return Observable.create(new OnSubscribeRedo(paramObservable, paramFunc1, true, false, paramScheduler));
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    AtomicBoolean localAtomicBoolean = new AtomicBoolean(true);
    AtomicLong localAtomicLong = new AtomicLong();
    Scheduler.Worker localWorker = this.scheduler.createWorker();
    paramSubscriber.add(localWorker);
    SerialSubscription localSerialSubscription = new SerialSubscription();
    paramSubscriber.add(localSerialSubscription);
    SerializedSubject localSerializedSubject = BehaviorSubject.create().toSerialized();
    localSerializedSubject.subscribe(Subscribers.empty());
    ProducerArbiter localProducerArbiter = new ProducerArbiter();
    2 local2 = new Action0(paramSubscriber, localSerializedSubject, localProducerArbiter, localAtomicLong, localSerialSubscription)
    {
      public void call()
      {
        if (this.val$child.isUnsubscribed())
          return;
        1 local1 = new Subscriber()
        {
          boolean done;

          private void decrementConsumerCapacity()
          {
            long l;
            do
              l = OnSubscribeRedo.2.this.val$consumerCapacity.get();
            while ((l != 9223372036854775807L) && (!OnSubscribeRedo.2.this.val$consumerCapacity.compareAndSet(l, l - 1L)));
          }

          public void onCompleted()
          {
            if (!this.done)
            {
              this.done = true;
              unsubscribe();
              OnSubscribeRedo.2.this.val$terminals.onNext(Notification.createOnCompleted());
            }
          }

          public void onError(Throwable paramThrowable)
          {
            if (!this.done)
            {
              this.done = true;
              unsubscribe();
              OnSubscribeRedo.2.this.val$terminals.onNext(Notification.createOnError(paramThrowable));
            }
          }

          public void onNext(T paramT)
          {
            if (!this.done)
            {
              OnSubscribeRedo.2.this.val$child.onNext(paramT);
              decrementConsumerCapacity();
              OnSubscribeRedo.2.this.val$arbiter.produced(1L);
            }
          }

          public void setProducer(Producer paramProducer)
          {
            OnSubscribeRedo.2.this.val$arbiter.setProducer(paramProducer);
          }
        };
        this.val$sourceSubscriptions.set(local1);
        OnSubscribeRedo.this.source.unsafeSubscribe(local1);
      }
    };
    localWorker.schedule(new Action0((Observable)this.controlHandlerFunction.call(localSerializedSubject.lift(new Observable.Operator()
    {
      public Subscriber<? super Notification<?>> call(Subscriber<? super Notification<?>> paramSubscriber)
      {
        return new Subscriber(paramSubscriber, paramSubscriber)
        {
          public void onCompleted()
          {
            this.val$filteredTerminals.onCompleted();
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$filteredTerminals.onError(paramThrowable);
          }

          public void onNext(Notification<?> paramNotification)
          {
            if ((paramNotification.isOnCompleted()) && (OnSubscribeRedo.this.stopOnComplete))
            {
              this.val$filteredTerminals.onCompleted();
              return;
            }
            if ((paramNotification.isOnError()) && (OnSubscribeRedo.this.stopOnError))
            {
              this.val$filteredTerminals.onError(paramNotification.getThrowable());
              return;
            }
            this.val$filteredTerminals.onNext(paramNotification);
          }

          public void setProducer(Producer paramProducer)
          {
            paramProducer.request(9223372036854775807L);
          }
        };
      }
    })), paramSubscriber, localAtomicLong, localWorker, local2, localAtomicBoolean)
    {
      public void call()
      {
        this.val$restarts.unsafeSubscribe(new Subscriber(this.val$child)
        {
          public void onCompleted()
          {
            OnSubscribeRedo.4.this.val$child.onCompleted();
          }

          public void onError(Throwable paramThrowable)
          {
            OnSubscribeRedo.4.this.val$child.onError(paramThrowable);
          }

          public void onNext(Object paramObject)
          {
            if (!OnSubscribeRedo.4.this.val$child.isUnsubscribed())
            {
              if (OnSubscribeRedo.4.this.val$consumerCapacity.get() > 0L)
                OnSubscribeRedo.4.this.val$worker.schedule(OnSubscribeRedo.4.this.val$subscribeToSource);
            }
            else
              return;
            OnSubscribeRedo.4.this.val$resumeBoundary.compareAndSet(false, true);
          }

          public void setProducer(Producer paramProducer)
          {
            paramProducer.request(9223372036854775807L);
          }
        });
      }
    });
    paramSubscriber.setProducer(new Producer(localAtomicLong, localProducerArbiter, localAtomicBoolean, localWorker, local2)
    {
      public void request(long paramLong)
      {
        if (paramLong > 0L)
        {
          BackpressureUtils.getAndAddRequest(this.val$consumerCapacity, paramLong);
          this.val$arbiter.request(paramLong);
          if (this.val$resumeBoundary.compareAndSet(true, false))
            this.val$worker.schedule(this.val$subscribeToSource);
        }
      }
    });
  }

  public static final class RedoFinite
    implements Func1<Observable<? extends Notification<?>>, Observable<?>>
  {
    final long count;

    public RedoFinite(long paramLong)
    {
      this.count = paramLong;
    }

    public Observable<?> call(Observable<? extends Notification<?>> paramObservable)
    {
      return paramObservable.map(new Func1()
      {
        int num;

        public Notification<?> call(Notification<?> paramNotification)
        {
          if (OnSubscribeRedo.RedoFinite.this.count == 0L);
          do
          {
            return paramNotification;
            this.num = (1 + this.num);
          }
          while (this.num > OnSubscribeRedo.RedoFinite.this.count);
          return Notification.createOnNext(Integer.valueOf(this.num));
        }
      }).dematerialize();
    }
  }

  public static final class RetryWithPredicate
    implements Func1<Observable<? extends Notification<?>>, Observable<? extends Notification<?>>>
  {
    final Func2<Integer, Throwable, Boolean> predicate;

    public RetryWithPredicate(Func2<Integer, Throwable, Boolean> paramFunc2)
    {
      this.predicate = paramFunc2;
    }

    public Observable<? extends Notification<?>> call(Observable<? extends Notification<?>> paramObservable)
    {
      return paramObservable.scan(Notification.createOnNext(Integer.valueOf(0)), new Func2()
      {
        public Notification<Integer> call(Notification<Integer> paramNotification, Notification<?> paramNotification1)
        {
          int i = ((Integer)paramNotification.getValue()).intValue();
          if (((Boolean)OnSubscribeRedo.RetryWithPredicate.this.predicate.call(Integer.valueOf(i), paramNotification1.getThrowable())).booleanValue())
            paramNotification1 = Notification.createOnNext(Integer.valueOf(i + 1));
          return paramNotification1;
        }
      });
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeRedo
 * JD-Core Version:    0.6.0
 */