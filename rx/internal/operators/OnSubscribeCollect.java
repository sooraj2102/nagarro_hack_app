package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action2;
import rx.functions.Func0;

public final class OnSubscribeCollect<T, R>
  implements Observable.OnSubscribe<R>
{
  final Func0<R> collectionFactory;
  final Action2<R, ? super T> collector;
  final Observable<T> source;

  public OnSubscribeCollect(Observable<T> paramObservable, Func0<R> paramFunc0, Action2<R, ? super T> paramAction2)
  {
    this.source = paramObservable;
    this.collectionFactory = paramFunc0;
    this.collector = paramAction2;
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    try
    {
      Object localObject = this.collectionFactory.call();
      new CollectSubscriber(paramSubscriber, localObject, this.collector).subscribeTo(this.source);
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwIfFatal(localThrowable);
      paramSubscriber.onError(localThrowable);
    }
  }

  static final class CollectSubscriber<T, R> extends DeferredScalarSubscriberSafe<T, R>
  {
    final Action2<R, ? super T> collector;

    public CollectSubscriber(Subscriber<? super R> paramSubscriber, R paramR, Action2<R, ? super T> paramAction2)
    {
      super();
      this.value = paramR;
      this.hasValue = true;
      this.collector = paramAction2;
    }

    public void onNext(T paramT)
    {
      if (this.done)
        return;
      try
      {
        this.collector.call(this.value, paramT);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        unsubscribe();
        onError(localThrowable);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeCollect
 * JD-Core Version:    0.6.0
 */