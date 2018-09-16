package rx.internal.operators;

import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.plugins.RxJavaHooks;

public final class SingleDoAfterTerminate<T>
  implements Single.OnSubscribe<T>
{
  final Action0 action;
  final Single<T> source;

  public SingleDoAfterTerminate(Single<T> paramSingle, Action0 paramAction0)
  {
    this.source = paramSingle;
    this.action = paramAction0;
  }

  public void call(SingleSubscriber<? super T> paramSingleSubscriber)
  {
    SingleDoAfterTerminateSubscriber localSingleDoAfterTerminateSubscriber = new SingleDoAfterTerminateSubscriber(paramSingleSubscriber, this.action);
    paramSingleSubscriber.add(localSingleDoAfterTerminateSubscriber);
    this.source.subscribe(localSingleDoAfterTerminateSubscriber);
  }

  static final class SingleDoAfterTerminateSubscriber<T> extends SingleSubscriber<T>
  {
    final Action0 action;
    final SingleSubscriber<? super T> actual;

    public SingleDoAfterTerminateSubscriber(SingleSubscriber<? super T> paramSingleSubscriber, Action0 paramAction0)
    {
      this.actual = paramSingleSubscriber;
      this.action = paramAction0;
    }

    void doAction()
    {
      try
      {
        this.action.call();
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        RxJavaHooks.onError(localThrowable);
      }
    }

    public void onError(Throwable paramThrowable)
    {
      try
      {
        this.actual.onError(paramThrowable);
        return;
      }
      finally
      {
        doAction();
      }
      throw localObject;
    }

    public void onSuccess(T paramT)
    {
      try
      {
        this.actual.onSuccess(paramT);
        return;
      }
      finally
      {
        doAction();
      }
      throw localObject;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.SingleDoAfterTerminate
 * JD-Core Version:    0.6.0
 */