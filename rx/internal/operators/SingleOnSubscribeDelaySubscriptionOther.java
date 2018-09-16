package rx.internal.operators;

import rx.Observable;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.SerialSubscription;

public final class SingleOnSubscribeDelaySubscriptionOther<T>
  implements Single.OnSubscribe<T>
{
  final Single<? extends T> main;
  final Observable<?> other;

  public SingleOnSubscribeDelaySubscriptionOther(Single<? extends T> paramSingle, Observable<?> paramObservable)
  {
    this.main = paramSingle;
    this.other = paramObservable;
  }

  public void call(SingleSubscriber<? super T> paramSingleSubscriber)
  {
    1 local1 = new SingleSubscriber(paramSingleSubscriber)
    {
      public void onError(Throwable paramThrowable)
      {
        this.val$subscriber.onError(paramThrowable);
      }

      public void onSuccess(T paramT)
      {
        this.val$subscriber.onSuccess(paramT);
      }
    };
    SerialSubscription localSerialSubscription = new SerialSubscription();
    paramSingleSubscriber.add(localSerialSubscription);
    2 local2 = new Subscriber(local1, localSerialSubscription)
    {
      boolean done;

      public void onCompleted()
      {
        if (this.done)
          return;
        this.done = true;
        this.val$serial.set(this.val$child);
        SingleOnSubscribeDelaySubscriptionOther.this.main.subscribe(this.val$child);
      }

      public void onError(Throwable paramThrowable)
      {
        if (this.done)
        {
          RxJavaHooks.onError(paramThrowable);
          return;
        }
        this.done = true;
        this.val$child.onError(paramThrowable);
      }

      public void onNext(Object paramObject)
      {
        onCompleted();
      }
    };
    localSerialSubscription.set(local2);
    this.other.subscribe(local2);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.SingleOnSubscribeDelaySubscriptionOther
 * JD-Core Version:    0.6.0
 */