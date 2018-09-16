package rx.internal.schedulers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.operators.BufferUntilSubscriber;
import rx.observers.SerializedObserver;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;

@Experimental
public class SchedulerWhen extends Scheduler
  implements Subscription
{
  static final Subscription SUBSCRIBED = new Subscription()
  {
    public boolean isUnsubscribed()
    {
      return false;
    }

    public void unsubscribe()
    {
    }
  };
  static final Subscription UNSUBSCRIBED = Subscriptions.unsubscribed();
  private final Scheduler actualScheduler;
  private final Subscription subscription;
  private final Observer<Observable<Completable>> workerObserver;

  public SchedulerWhen(Func1<Observable<Observable<Completable>>, Completable> paramFunc1, Scheduler paramScheduler)
  {
    this.actualScheduler = paramScheduler;
    PublishSubject localPublishSubject = PublishSubject.create();
    this.workerObserver = new SerializedObserver(localPublishSubject);
    this.subscription = ((Completable)paramFunc1.call(localPublishSubject.onBackpressureBuffer())).subscribe();
  }

  public Scheduler.Worker createWorker()
  {
    Scheduler.Worker localWorker = this.actualScheduler.createWorker();
    BufferUntilSubscriber localBufferUntilSubscriber = BufferUntilSubscriber.create();
    SerializedObserver localSerializedObserver = new SerializedObserver(localBufferUntilSubscriber);
    Observable localObservable = localBufferUntilSubscriber.map(new Func1(localWorker)
    {
      public Completable call(SchedulerWhen.ScheduledAction paramScheduledAction)
      {
        return Completable.create(new Completable.OnSubscribe(paramScheduledAction)
        {
          public void call(CompletableSubscriber paramCompletableSubscriber)
          {
            paramCompletableSubscriber.onSubscribe(this.val$action);
            SchedulerWhen.ScheduledAction.access$000(this.val$action, SchedulerWhen.1.this.val$actualWorker);
            paramCompletableSubscriber.onCompleted();
          }
        });
      }
    });
    2 local2 = new Scheduler.Worker(localWorker, localSerializedObserver)
    {
      private final AtomicBoolean unsubscribed = new AtomicBoolean();

      public boolean isUnsubscribed()
      {
        return this.unsubscribed.get();
      }

      public Subscription schedule(Action0 paramAction0)
      {
        SchedulerWhen.ImmediateAction localImmediateAction = new SchedulerWhen.ImmediateAction(paramAction0);
        this.val$actionObserver.onNext(localImmediateAction);
        return localImmediateAction;
      }

      public Subscription schedule(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit)
      {
        SchedulerWhen.DelayedAction localDelayedAction = new SchedulerWhen.DelayedAction(paramAction0, paramLong, paramTimeUnit);
        this.val$actionObserver.onNext(localDelayedAction);
        return localDelayedAction;
      }

      public void unsubscribe()
      {
        if (this.unsubscribed.compareAndSet(false, true))
        {
          this.val$actualWorker.unsubscribe();
          this.val$actionObserver.onCompleted();
        }
      }
    };
    this.workerObserver.onNext(localObservable);
    return local2;
  }

  public boolean isUnsubscribed()
  {
    return this.subscription.isUnsubscribed();
  }

  public void unsubscribe()
  {
    this.subscription.unsubscribe();
  }

  private static class DelayedAction extends SchedulerWhen.ScheduledAction
  {
    private final Action0 action;
    private final long delayTime;
    private final TimeUnit unit;

    public DelayedAction(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit)
    {
      this.action = paramAction0;
      this.delayTime = paramLong;
      this.unit = paramTimeUnit;
    }

    protected Subscription callActual(Scheduler.Worker paramWorker)
    {
      return paramWorker.schedule(this.action, this.delayTime, this.unit);
    }
  }

  private static class ImmediateAction extends SchedulerWhen.ScheduledAction
  {
    private final Action0 action;

    public ImmediateAction(Action0 paramAction0)
    {
      this.action = paramAction0;
    }

    protected Subscription callActual(Scheduler.Worker paramWorker)
    {
      return paramWorker.schedule(this.action);
    }
  }

  private static abstract class ScheduledAction extends AtomicReference<Subscription>
    implements Subscription
  {
    public ScheduledAction()
    {
      super();
    }

    private void call(Scheduler.Worker paramWorker)
    {
      Subscription localSubscription1 = (Subscription)get();
      if (localSubscription1 == SchedulerWhen.UNSUBSCRIBED);
      Subscription localSubscription2;
      do
      {
        do
          return;
        while (localSubscription1 != SchedulerWhen.SUBSCRIBED);
        localSubscription2 = callActual(paramWorker);
      }
      while (compareAndSet(SchedulerWhen.SUBSCRIBED, localSubscription2));
      localSubscription2.unsubscribe();
    }

    protected abstract Subscription callActual(Scheduler.Worker paramWorker);

    public boolean isUnsubscribed()
    {
      return ((Subscription)get()).isUnsubscribed();
    }

    public void unsubscribe()
    {
      Subscription localSubscription2;
      do
      {
        Subscription localSubscription1 = SchedulerWhen.UNSUBSCRIBED;
        do
        {
          localSubscription2 = (Subscription)get();
          if (localSubscription2 == SchedulerWhen.UNSUBSCRIBED)
            return;
        }
        while (!compareAndSet(localSubscription2, localSubscription1));
      }
      while (localSubscription2 == SchedulerWhen.SUBSCRIBED);
      localSubscription2.unsubscribe();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.SchedulerWhen
 * JD-Core Version:    0.6.0
 */