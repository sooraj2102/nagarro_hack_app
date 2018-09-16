package rx.internal.operators;

import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.plugins.RxJavaHooks;

public final class OnSubscribeLift<T, R>
  implements Observable.OnSubscribe<R>
{
  final Observable.Operator<? extends R, ? super T> operator;
  final Observable.OnSubscribe<T> parent;

  public OnSubscribeLift(Observable.OnSubscribe<T> paramOnSubscribe, Observable.Operator<? extends R, ? super T> paramOperator)
  {
    this.parent = paramOnSubscribe;
    this.operator = paramOperator;
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    try
    {
      Subscriber localSubscriber = (Subscriber)RxJavaHooks.onObservableLift(this.operator).call(paramSubscriber);
      try
      {
        localSubscriber.onStart();
        this.parent.call(localSubscriber);
        return;
      }
      catch (Throwable localThrowable2)
      {
        Exceptions.throwIfFatal(localThrowable2);
        localSubscriber.onError(localThrowable2);
        return;
      }
    }
    catch (Throwable localThrowable1)
    {
      Exceptions.throwIfFatal(localThrowable1);
      paramSubscriber.onError(localThrowable1);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeLift
 * JD-Core Version:    0.6.0
 */