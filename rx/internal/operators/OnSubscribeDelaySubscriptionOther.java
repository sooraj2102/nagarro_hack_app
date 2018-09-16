package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.observers.Subscribers;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public final class OnSubscribeDelaySubscriptionOther<T, U>
  implements Observable.OnSubscribe<T>
{
  final Observable<? extends T> main;
  final Observable<U> other;

  public OnSubscribeDelaySubscriptionOther(Observable<? extends T> paramObservable, Observable<U> paramObservable1)
  {
    this.main = paramObservable;
    this.other = paramObservable1;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    SerialSubscription localSerialSubscription = new SerialSubscription();
    paramSubscriber.add(localSerialSubscription);
    1 local1 = new Subscriber(Subscribers.wrap(paramSubscriber), localSerialSubscription)
    {
      boolean done;

      public void onCompleted()
      {
        if (this.done)
          return;
        this.done = true;
        this.val$serial.set(Subscriptions.unsubscribed());
        OnSubscribeDelaySubscriptionOther.this.main.unsafeSubscribe(this.val$child);
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

      public void onNext(U paramU)
      {
        onCompleted();
      }
    };
    localSerialSubscription.set(local1);
    this.other.unsafeSubscribe(local1);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeDelaySubscriptionOther
 * JD-Core Version:    0.6.0
 */