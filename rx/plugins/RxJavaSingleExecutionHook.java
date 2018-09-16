package rx.plugins;

import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.Subscription;

public abstract class RxJavaSingleExecutionHook
{
  @Deprecated
  public <T> Single.OnSubscribe<T> onCreate(Single.OnSubscribe<T> paramOnSubscribe)
  {
    return paramOnSubscribe;
  }

  @Deprecated
  public <T, R> Observable.Operator<? extends R, ? super T> onLift(Observable.Operator<? extends R, ? super T> paramOperator)
  {
    return paramOperator;
  }

  @Deprecated
  public <T> Throwable onSubscribeError(Throwable paramThrowable)
  {
    return paramThrowable;
  }

  @Deprecated
  public <T> Subscription onSubscribeReturn(Subscription paramSubscription)
  {
    return paramSubscription;
  }

  @Deprecated
  public <T> Observable.OnSubscribe<T> onSubscribeStart(Single<? extends T> paramSingle, Observable.OnSubscribe<T> paramOnSubscribe)
  {
    return paramOnSubscribe;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.plugins.RxJavaSingleExecutionHook
 * JD-Core Version:    0.6.0
 */