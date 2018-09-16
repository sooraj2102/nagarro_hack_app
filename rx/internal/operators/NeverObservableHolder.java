package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public enum NeverObservableHolder
  implements Observable.OnSubscribe<Object>
{
  static final Observable<Object> NEVER;

  static
  {
    NeverObservableHolder[] arrayOfNeverObservableHolder = new NeverObservableHolder[1];
    arrayOfNeverObservableHolder[0] = INSTANCE;
    $VALUES = arrayOfNeverObservableHolder;
    NEVER = Observable.create(INSTANCE);
  }

  public static <T> Observable<T> instance()
  {
    return NEVER;
  }

  public void call(Subscriber<? super Object> paramSubscriber)
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.NeverObservableHolder
 * JD-Core Version:    0.6.0
 */