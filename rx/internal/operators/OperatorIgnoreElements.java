package rx.internal.operators;

import rx.Observable.Operator;
import rx.Subscriber;

public class OperatorIgnoreElements<T>
  implements Observable.Operator<T, T>
{
  public static <T> OperatorIgnoreElements<T> instance()
  {
    return Holder.INSTANCE;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    1 local1 = new Subscriber(paramSubscriber)
    {
      public void onCompleted()
      {
        this.val$child.onCompleted();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$child.onError(paramThrowable);
      }

      public void onNext(T paramT)
      {
      }
    };
    paramSubscriber.add(local1);
    return local1;
  }

  static final class Holder
  {
    static final OperatorIgnoreElements<?> INSTANCE = new OperatorIgnoreElements();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorIgnoreElements
 * JD-Core Version:    0.6.0
 */