package rx.internal.operators;

import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.functions.Action1;

public final class SingleDoOnEvent<T>
  implements Single.OnSubscribe<T>
{
  final Action1<Throwable> onError;
  final Action1<? super T> onSuccess;
  final Single<T> source;

  public SingleDoOnEvent(Single<T> paramSingle, Action1<? super T> paramAction1, Action1<Throwable> paramAction11)
  {
    this.source = paramSingle;
    this.onSuccess = paramAction1;
    this.onError = paramAction11;
  }

  public void call(SingleSubscriber<? super T> paramSingleSubscriber)
  {
    SingleDoOnEventSubscriber localSingleDoOnEventSubscriber = new SingleDoOnEventSubscriber(paramSingleSubscriber, this.onSuccess, this.onError);
    paramSingleSubscriber.add(localSingleDoOnEventSubscriber);
    this.source.subscribe(localSingleDoOnEventSubscriber);
  }

  static final class SingleDoOnEventSubscriber<T> extends SingleSubscriber<T>
  {
    final SingleSubscriber<? super T> actual;
    final Action1<Throwable> onError;
    final Action1<? super T> onSuccess;

    SingleDoOnEventSubscriber(SingleSubscriber<? super T> paramSingleSubscriber, Action1<? super T> paramAction1, Action1<Throwable> paramAction11)
    {
      this.actual = paramSingleSubscriber;
      this.onSuccess = paramAction1;
      this.onError = paramAction11;
    }

    public void onError(Throwable paramThrowable)
    {
      try
      {
        this.onError.call(paramThrowable);
        this.actual.onError(paramThrowable);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        this.actual.onError(new CompositeException(new Throwable[] { paramThrowable, localThrowable }));
      }
    }

    public void onSuccess(T paramT)
    {
      try
      {
        this.onSuccess.call(paramT);
        this.actual.onSuccess(paramT);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwOrReport(localThrowable, this, paramT);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.SingleDoOnEvent
 * JD-Core Version:    0.6.0
 */