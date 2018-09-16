package rx.observables;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.internal.operators.OnSubscribeAutoConnect;
import rx.internal.operators.OnSubscribeRefCount;

public abstract class ConnectableObservable<T> extends Observable<T>
{
  protected ConnectableObservable(Observable.OnSubscribe<T> paramOnSubscribe)
  {
    super(paramOnSubscribe);
  }

  public Observable<T> autoConnect()
  {
    return autoConnect(1);
  }

  public Observable<T> autoConnect(int paramInt)
  {
    return autoConnect(paramInt, Actions.empty());
  }

  public Observable<T> autoConnect(int paramInt, Action1<? super Subscription> paramAction1)
  {
    if (paramInt <= 0)
    {
      connect(paramAction1);
      return this;
    }
    return create(new OnSubscribeAutoConnect(this, paramInt, paramAction1));
  }

  public final Subscription connect()
  {
    Subscription[] arrayOfSubscription = new Subscription[1];
    connect(new Action1(arrayOfSubscription)
    {
      public void call(Subscription paramSubscription)
      {
        this.val$out[0] = paramSubscription;
      }
    });
    return arrayOfSubscription[0];
  }

  public abstract void connect(Action1<? super Subscription> paramAction1);

  public Observable<T> refCount()
  {
    return create(new OnSubscribeRefCount(this));
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.observables.ConnectableObservable
 * JD-Core Version:    0.6.0
 */