package rx.internal.operators;

import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.plugins.RxJavaHooks;

public final class OperatorDoAfterTerminate<T>
  implements Observable.Operator<T, T>
{
  final Action0 action;

  public OperatorDoAfterTerminate(Action0 paramAction0)
  {
    if (paramAction0 == null)
      throw new NullPointerException("Action can not be null");
    this.action = paramAction0;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    return new Subscriber(paramSubscriber, paramSubscriber)
    {
      void callAction()
      {
        try
        {
          OperatorDoAfterTerminate.this.action.call();
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwIfFatal(localThrowable);
          RxJavaHooks.onError(localThrowable);
        }
      }

      public void onCompleted()
      {
        try
        {
          this.val$child.onCompleted();
          return;
        }
        finally
        {
          callAction();
        }
        throw localObject;
      }

      public void onError(Throwable paramThrowable)
      {
        try
        {
          this.val$child.onError(paramThrowable);
          return;
        }
        finally
        {
          callAction();
        }
        throw localObject;
      }

      public void onNext(T paramT)
      {
        this.val$child.onNext(paramT);
      }
    };
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorDoAfterTerminate
 * JD-Core Version:    0.6.0
 */