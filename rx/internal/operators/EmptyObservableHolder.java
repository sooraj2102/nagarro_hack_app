package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public enum EmptyObservableHolder
  implements Observable.OnSubscribe<Object>
{
  static final Observable<Object> EMPTY;

  static
  {
    EmptyObservableHolder[] arrayOfEmptyObservableHolder = new EmptyObservableHolder[1];
    arrayOfEmptyObservableHolder[0] = INSTANCE;
    $VALUES = arrayOfEmptyObservableHolder;
    EMPTY = Observable.create(INSTANCE);
  }

  public static <T> Observable<T> instance()
  {
    return EMPTY;
  }

  public void call(Subscriber<? super Object> paramSubscriber)
  {
    paramSubscriber.onCompleted();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.EmptyObservableHolder
 * JD-Core Version:    0.6.0
 */