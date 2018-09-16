package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func2;

public final class OnSubscribeReduceSeed<T, R>
  implements Observable.OnSubscribe<R>
{
  final R initialValue;
  final Func2<R, ? super T, R> reducer;
  final Observable<T> source;

  public OnSubscribeReduceSeed(Observable<T> paramObservable, R paramR, Func2<R, ? super T, R> paramFunc2)
  {
    this.source = paramObservable;
    this.initialValue = paramR;
    this.reducer = paramFunc2;
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    new ReduceSeedSubscriber(paramSubscriber, this.initialValue, this.reducer).subscribeTo(this.source);
  }

  static final class ReduceSeedSubscriber<T, R> extends DeferredScalarSubscriber<T, R>
  {
    final Func2<R, ? super T, R> reducer;

    public ReduceSeedSubscriber(Subscriber<? super R> paramSubscriber, R paramR, Func2<R, ? super T, R> paramFunc2)
    {
      super();
      this.value = paramR;
      this.hasValue = true;
      this.reducer = paramFunc2;
    }

    public void onNext(T paramT)
    {
      try
      {
        this.value = this.reducer.call(this.value, paramT);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        unsubscribe();
        this.actual.onError(localThrowable);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeReduceSeed
 * JD-Core Version:    0.6.0
 */