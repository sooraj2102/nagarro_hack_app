package rx.internal.operators;

import java.util.NoSuchElementException;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func2;
import rx.plugins.RxJavaHooks;

public final class OnSubscribeReduce<T>
  implements Observable.OnSubscribe<T>
{
  final Func2<T, T, T> reducer;
  final Observable<T> source;

  public OnSubscribeReduce(Observable<T> paramObservable, Func2<T, T, T> paramFunc2)
  {
    this.source = paramObservable;
    this.reducer = paramFunc2;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    ReduceSubscriber localReduceSubscriber = new ReduceSubscriber(paramSubscriber, this.reducer);
    paramSubscriber.add(localReduceSubscriber);
    paramSubscriber.setProducer(new Producer(localReduceSubscriber)
    {
      public void request(long paramLong)
      {
        this.val$parent.downstreamRequest(paramLong);
      }
    });
    this.source.unsafeSubscribe(localReduceSubscriber);
  }

  static final class ReduceSubscriber<T> extends Subscriber<T>
  {
    static final Object EMPTY = new Object();
    final Subscriber<? super T> actual;
    boolean done;
    final Func2<T, T, T> reducer;
    T value;

    public ReduceSubscriber(Subscriber<? super T> paramSubscriber, Func2<T, T, T> paramFunc2)
    {
      this.actual = paramSubscriber;
      this.reducer = paramFunc2;
      this.value = EMPTY;
      request(0L);
    }

    void downstreamRequest(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
      if (paramLong != 0L)
        request(9223372036854775807L);
    }

    public void onCompleted()
    {
      if (this.done)
        return;
      this.done = true;
      Object localObject = this.value;
      if (localObject != EMPTY)
      {
        this.actual.onNext(localObject);
        this.actual.onCompleted();
        return;
      }
      this.actual.onError(new NoSuchElementException());
    }

    public void onError(Throwable paramThrowable)
    {
      if (!this.done)
      {
        this.done = true;
        this.actual.onError(paramThrowable);
        return;
      }
      RxJavaHooks.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      if (this.done)
        return;
      Object localObject = this.value;
      if (localObject == EMPTY)
      {
        this.value = paramT;
        return;
      }
      try
      {
        this.value = this.reducer.call(localObject, paramT);
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
 * Qualified Name:     rx.internal.operators.OnSubscribeReduce
 * JD-Core Version:    0.6.0
 */