package rx.internal.operators;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Subscription;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeMergeIterable
  implements Completable.OnSubscribe
{
  final Iterable<? extends Completable> sources;

  public CompletableOnSubscribeMergeIterable(Iterable<? extends Completable> paramIterable)
  {
    this.sources = paramIterable;
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    CompositeSubscription localCompositeSubscription = new CompositeSubscription();
    paramCompletableSubscriber.onSubscribe(localCompositeSubscription);
    label280: 
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
      AtomicBoolean localAtomicBoolean = new AtomicBoolean();
      while (true)
      {
        Completable localCompletable;
        NullPointerException localNullPointerException;
        while (true)
        {
          while (true)
          {
            if (localCompositeSubscription.isUnsubscribed())
              break label280;
            try
            {
              boolean bool = localIterator.hasNext();
              if (!bool)
              {
                if ((localAtomicInteger.decrementAndGet() != 0) || (!localAtomicBoolean.compareAndSet(false, true)))
                  break;
                paramCompletableSubscriber.onCompleted();
                return;
              }
            }
            catch (Throwable localThrowable2)
            {
              localCompositeSubscription.unsubscribe();
              if (localAtomicBoolean.compareAndSet(false, true))
              {
                paramCompletableSubscriber.onError(localThrowable2);
                return;
              }
              RxJavaHooks.onError(localThrowable2);
              return;
            }
          }
          if (localCompositeSubscription.isUnsubscribed())
            break;
          try
          {
            localCompletable = (Completable)localIterator.next();
            if (localCompositeSubscription.isUnsubscribed())
              break;
            if (localCompletable != null)
              break label254;
            localCompositeSubscription.unsubscribe();
            localNullPointerException = new NullPointerException("A completable source is null");
            if (localAtomicBoolean.compareAndSet(false, true))
            {
              paramCompletableSubscriber.onError(localNullPointerException);
              return;
            }
          }
          catch (Throwable localThrowable3)
          {
            localCompositeSubscription.unsubscribe();
            if (localAtomicBoolean.compareAndSet(false, true))
            {
              paramCompletableSubscriber.onError(localThrowable3);
              return;
            }
            RxJavaHooks.onError(localThrowable3);
            return;
          }
        }
        RxJavaHooks.onError(localNullPointerException);
        return;
        label254: localAtomicInteger.getAndIncrement();
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
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableOnSubscribeMergeIterable
 * JD-Core Version:    0.6.0
 */