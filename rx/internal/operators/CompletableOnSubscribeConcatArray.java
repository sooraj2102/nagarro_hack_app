package rx.internal.operators;

import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Subscription;
import rx.subscriptions.SerialSubscription;

public final class CompletableOnSubscribeConcatArray
  implements Completable.OnSubscribe
{
  final Completable[] sources;

  public CompletableOnSubscribeConcatArray(Completable[] paramArrayOfCompletable)
  {
    this.sources = paramArrayOfCompletable;
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    ConcatInnerSubscriber localConcatInnerSubscriber = new ConcatInnerSubscriber(paramCompletableSubscriber, this.sources);
    paramCompletableSubscriber.onSubscribe(localConcatInnerSubscriber.sd);
    localConcatInnerSubscriber.next();
  }

  static final class ConcatInnerSubscriber extends AtomicInteger
    implements CompletableSubscriber
  {
    private static final long serialVersionUID = -7965400327305809232L;
    final CompletableSubscriber actual;
    int index;
    final SerialSubscription sd;
    final Completable[] sources;

    public ConcatInnerSubscriber(CompletableSubscriber paramCompletableSubscriber, Completable[] paramArrayOfCompletable)
    {
      this.actual = paramCompletableSubscriber;
      this.sources = paramArrayOfCompletable;
      this.sd = new SerialSubscription();
    }

    void next()
    {
      if (this.sd.isUnsubscribed());
      label10: 
      do
      {
        Completable[] arrayOfCompletable;
        do
        {
          return;
          break label10;
          continue;
          while (getAndIncrement() != 0);
          arrayOfCompletable = this.sources;
        }
        while (this.sd.isUnsubscribed());
        int i = this.index;
        this.index = (i + 1);
        if (i == arrayOfCompletable.length)
        {
          this.actual.onCompleted();
          return;
        }
        arrayOfCompletable[i].unsafeSubscribe(this);
      }
      while (decrementAndGet() != 0);
    }

    public void onCompleted()
    {
      next();
    }

    public void onError(Throwable paramThrowable)
    {
      this.actual.onError(paramThrowable);
    }

    public void onSubscribe(Subscription paramSubscription)
    {
      this.sd.set(paramSubscription);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableOnSubscribeConcatArray
 * JD-Core Version:    0.6.0
 */