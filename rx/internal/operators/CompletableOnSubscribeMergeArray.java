package rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Subscription;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeMergeArray
  implements Completable.OnSubscribe
{
  final Completable[] sources;

  public CompletableOnSubscribeMergeArray(Completable[] paramArrayOfCompletable)
  {
    this.sources = paramArrayOfCompletable;
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    CompositeSubscription localCompositeSubscription = new CompositeSubscription();
    AtomicInteger localAtomicInteger = new AtomicInteger(1 + this.sources.length);
    AtomicBoolean localAtomicBoolean = new AtomicBoolean();
    paramCompletableSubscriber.onSubscribe(localCompositeSubscription);
    Completable[] arrayOfCompletable = this.sources;
    int i = arrayOfCompletable.length;
    int j = 0;
    Completable localCompletable;
    if (j < i)
    {
      localCompletable = arrayOfCompletable[j];
      if (!localCompositeSubscription.isUnsubscribed());
    }
    do
    {
      return;
      if (localCompletable == null)
      {
        localCompositeSubscription.unsubscribe();
        NullPointerException localNullPointerException = new NullPointerException("A completable source is null");
        if (localAtomicBoolean.compareAndSet(false, true))
        {
          paramCompletableSubscriber.onError(localNullPointerException);
          return;
        }
        RxJavaHooks.onError(localNullPointerException);
      }
      localCompletable.unsafeSubscribe(new CompletableSubscriber(localCompositeSubscription, localAtomicBoolean, paramCompletableSubscriber, localAtomicInteger)
      {
        public void onCompleted()
        {
          if ((this.val$wip.decrementAndGet() == 0) && (this.val$once.compareAndSet(false, true)))
            this.val$s.onCompleted();
        }

        public void onError(Throwable paramThrowable)
        {
          this.val$set.unsubscribe();
          if (this.val$once.compareAndSet(false, true))
          {
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
      j++;
      break;
    }
    while ((localAtomicInteger.decrementAndGet() != 0) || (!localAtomicBoolean.compareAndSet(false, true)));
    paramCompletableSubscriber.onCompleted();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableOnSubscribeMergeArray
 * JD-Core Version:    0.6.0
 */