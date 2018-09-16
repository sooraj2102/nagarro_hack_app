package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;

public final class OnSubscribeMap<T, R>
  implements Observable.OnSubscribe<R>
{
  final Observable<T> source;
  final Func1<? super T, ? extends R> transformer;

  public OnSubscribeMap(Observable<T> paramObservable, Func1<? super T, ? extends R> paramFunc1)
  {
    this.source = paramObservable;
    this.transformer = paramFunc1;
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    MapSubscriber localMapSubscriber = new MapSubscriber(paramSubscriber, this.transformer);
    paramSubscriber.add(localMapSubscriber);
    this.source.unsafeSubscribe(localMapSubscriber);
  }

  static final class MapSubscriber<T, R> extends Subscriber<T>
  {
    final Subscriber<? super R> actual;
    boolean done;
    final Func1<? super T, ? extends R> mapper;

    public MapSubscriber(Subscriber<? super R> paramSubscriber, Func1<? super T, ? extends R> paramFunc1)
    {
      this.actual = paramSubscriber;
      this.mapper = paramFunc1;
    }

    public void onCompleted()
    {
      if (this.done)
        return;
      this.actual.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.done)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.done = true;
      this.actual.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      try
      {
        Object localObject = this.mapper.call(paramT);
        this.actual.onNext(localObject);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        unsubscribe();
        onError(OnErrorThrowable.addValueAsLastCause(localThrowable, paramT));
      }
    }

    public void setProducer(Producer paramProducer)
    {
      this.actual.setProducer(paramProducer);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeMap
 * JD-Core Version:    0.6.0
 */