package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeMergeDelayErrorArray
  implements Completable.OnSubscribe
{
  final Completable[] sources;

  public CompletableOnSubscribeMergeDelayErrorArray(Completable[] paramArrayOfCompletable)
  {
    this.sources = paramArrayOfCompletable;
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    CompositeSubscription localCompositeSubscription = new CompositeSubscription();
    AtomicInteger localAtomicInteger = new AtomicInteger(1 + this.sources.length);
    ConcurrentLinkedQueue localConcurrentLinkedQueue = new ConcurrentLinkedQueue();
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
        localConcurrentLinkedQueue.offer(new NullPointerException("A completable source is null"));
        localAtomicInteger.decrementAndGet();
      }
      while (true)
      {
        j++;
        break;
        localCompletable.unsafeSubscribe(new CompletableSubscriber(localCompositeSubscription, localConcurrentLinkedQueue, localAtomicInteger, paramCompletableSubscriber)
        {
          public void onCompleted()
          {
            tryTerminate();
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$q.offer(paramThrowable);
            tryTerminate();
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            this.val$set.add(paramSubscription);
          }

          void tryTerminate()
          {
            if (this.val$wip.decrementAndGet() == 0)
            {
              if (this.val$q.isEmpty())
                this.val$s.onCompleted();
            }
            else
              return;
            this.val$s.onError(CompletableOnSubscribeMerge.collectErrors(this.val$q));
          }
        });
      }
    }
    while (localAtomicInteger.decrementAndGet() != 0);
    if (localConcurrentLinkedQueue.isEmpty())
    {
      paramCompletableSubscriber.onCompleted();
      return;
    }
    paramCompletableSubscriber.onError(CompletableOnSubscribeMerge.collectErrors(localConcurrentLinkedQueue));
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableOnSubscribeMergeDelayErrorArray
 * JD-Core Version:    0.6.0
 */