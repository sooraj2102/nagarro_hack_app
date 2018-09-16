package rx.internal.operators;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Subscription;
import rx.internal.util.unsafe.MpscLinkedQueue;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeMergeDelayErrorIterable
  implements Completable.OnSubscribe
{
  final Iterable<? extends Completable> sources;

  public CompletableOnSubscribeMergeDelayErrorIterable(Iterable<? extends Completable> paramIterable)
  {
    this.sources = paramIterable;
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    CompositeSubscription localCompositeSubscription = new CompositeSubscription();
    paramCompletableSubscriber.onSubscribe(localCompositeSubscription);
    MpscLinkedQueue localMpscLinkedQueue;
    label170: label304: label330: 
    while (true)
    {
      Iterator localIterator;
      try
      {
        localIterator = this.sources.iterator();
        if (localIterator == null)
        {
          paramCompletableSubscriber.onError(new NullPointerException("The source iterator returned is null"));
          return;
        }
      }
      catch (Throwable localThrowable1)
      {
        paramCompletableSubscriber.onError(localThrowable1);
        return;
      }
      AtomicInteger localAtomicInteger = new AtomicInteger(1);
      localMpscLinkedQueue = new MpscLinkedQueue();
      while (true)
      {
        Completable localCompletable;
        while (true)
        {
          while (true)
          {
            if (localCompositeSubscription.isUnsubscribed())
              break label330;
            try
            {
              boolean bool = localIterator.hasNext();
              if (bool)
                break label170;
              if (localAtomicInteger.decrementAndGet() != 0)
                break;
              if (!localMpscLinkedQueue.isEmpty())
                break label332;
              paramCompletableSubscriber.onCompleted();
              return;
            }
            catch (Throwable localThrowable2)
            {
              localMpscLinkedQueue.offer(localThrowable2);
            }
          }
          if (localAtomicInteger.decrementAndGet() != 0)
            break;
          if (localMpscLinkedQueue.isEmpty())
          {
            paramCompletableSubscriber.onCompleted();
            return;
          }
          paramCompletableSubscriber.onError(CompletableOnSubscribeMerge.collectErrors(localMpscLinkedQueue));
          return;
          if (localCompositeSubscription.isUnsubscribed())
            break;
          try
          {
            localCompletable = (Completable)localIterator.next();
            if (localCompositeSubscription.isUnsubscribed())
              break;
            if (localCompletable != null)
              break label304;
            localMpscLinkedQueue.offer(new NullPointerException("A completable source is null"));
            if (localAtomicInteger.decrementAndGet() != 0)
              break;
            if (!localMpscLinkedQueue.isEmpty())
              break label292;
            paramCompletableSubscriber.onCompleted();
            return;
          }
          catch (Throwable localThrowable3)
          {
            localMpscLinkedQueue.offer(localThrowable3);
          }
        }
        if (localAtomicInteger.decrementAndGet() != 0)
          break;
        if (localMpscLinkedQueue.isEmpty())
        {
          paramCompletableSubscriber.onCompleted();
          return;
        }
        paramCompletableSubscriber.onError(CompletableOnSubscribeMerge.collectErrors(localMpscLinkedQueue));
        return;
        paramCompletableSubscriber.onError(CompletableOnSubscribeMerge.collectErrors(localMpscLinkedQueue));
        return;
        localAtomicInteger.getAndIncrement();
        localCompletable.unsafeSubscribe(new CompletableSubscriber(localCompositeSubscription, localMpscLinkedQueue, localAtomicInteger, paramCompletableSubscriber)
        {
          public void onCompleted()
          {
            tryTerminate();
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$queue.offer(paramThrowable);
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
              if (this.val$queue.isEmpty())
                this.val$s.onCompleted();
            }
            else
              return;
            this.val$s.onError(CompletableOnSubscribeMerge.collectErrors(this.val$queue));
          }
        });
      }
    }
    label292: label332: paramCompletableSubscriber.onError(CompletableOnSubscribeMerge.collectErrors(localMpscLinkedQueue));
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableOnSubscribeMergeDelayErrorIterable
 * JD-Core Version:    0.6.0
 */