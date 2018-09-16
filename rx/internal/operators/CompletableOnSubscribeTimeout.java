package rx.internal.operators;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeTimeout
  implements Completable.OnSubscribe
{
  final Completable other;
  final Scheduler scheduler;
  final Completable source;
  final long timeout;
  final TimeUnit unit;

  public CompletableOnSubscribeTimeout(Completable paramCompletable1, long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler, Completable paramCompletable2)
  {
    this.source = paramCompletable1;
    this.timeout = paramLong;
    this.unit = paramTimeUnit;
    this.scheduler = paramScheduler;
    this.other = paramCompletable2;
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    CompositeSubscription localCompositeSubscription = new CompositeSubscription();
    paramCompletableSubscriber.onSubscribe(localCompositeSubscription);
    AtomicBoolean localAtomicBoolean = new AtomicBoolean();
    Scheduler.Worker localWorker = this.scheduler.createWorker();
    localCompositeSubscription.add(localWorker);
    localWorker.schedule(new Action0(localAtomicBoolean, localCompositeSubscription, paramCompletableSubscriber)
    {
      public void call()
      {
        if (this.val$once.compareAndSet(false, true))
        {
          this.val$set.clear();
          if (CompletableOnSubscribeTimeout.this.other == null)
            this.val$s.onError(new TimeoutException());
        }
        else
        {
          return;
        }
        CompletableOnSubscribeTimeout.this.other.unsafeSubscribe(new CompletableSubscriber()
        {
          public void onCompleted()
          {
            CompletableOnSubscribeTimeout.1.this.val$set.unsubscribe();
            CompletableOnSubscribeTimeout.1.this.val$s.onCompleted();
          }

          public void onError(Throwable paramThrowable)
          {
            CompletableOnSubscribeTimeout.1.this.val$set.unsubscribe();
            CompletableOnSubscribeTimeout.1.this.val$s.onError(paramThrowable);
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            CompletableOnSubscribeTimeout.1.this.val$set.add(paramSubscription);
          }
        });
      }
    }
    , this.timeout, this.unit);
    this.source.unsafeSubscribe(new CompletableSubscriber(localCompositeSubscription, localAtomicBoolean, paramCompletableSubscriber)
    {
      public void onCompleted()
      {
        if (this.val$once.compareAndSet(false, true))
        {
          this.val$set.unsubscribe();
          this.val$s.onCompleted();
        }
      }

      public void onError(Throwable paramThrowable)
      {
        if (this.val$once.compareAndSet(false, true))
        {
          this.val$set.unsubscribe();
          this.val$s.onError(paramThrowable);
          return;
        }
        RxJavaHooks.onError(paramThrowable);
      }

      public void onSubscribe(Subscription paramSubscription)
      {
        this.val$set.add(paramSubscription);
      }
    });
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableOnSubscribeTimeout
 * JD-Core Version:    0.6.0
 */