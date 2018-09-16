package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;

public final class OnSubscribeFilter<T>
  implements Observable.OnSubscribe<T>
{
  final Func1<? super T, Boolean> predicate;
  final Observable<T> source;

  public OnSubscribeFilter(Observable<T> paramObservable, Func1<? super T, Boolean> paramFunc1)
  {
    this.source = paramObservable;
    this.predicate = paramFunc1;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    FilterSubscriber localFilterSubscriber = new FilterSubscriber(paramSubscriber, this.predicate);
    paramSubscriber.add(localFilterSubscriber);
    this.source.unsafeSubscribe(localFilterSubscriber);
  }

  static final class FilterSubscriber<T> extends Subscriber<T>
  {
    final Subscriber<? super T> actual;
    boolean done;
    final Func1<? super T, Boolean> predicate;

    public FilterSubscriber(Subscriber<? super T> paramSubscriber, Func1<? super T, Boolean> paramFunc1)
    {
      this.actual = paramSubscriber;
      this.predicate = paramFunc1;
      request(0L);
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
        boolean bool = ((Boolean)this.predicate.call(paramT)).booleanValue();
        if (bool)
        {
          this.actual.onNext(paramT);
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        unsubscribe();
        onError(OnErrorThrowable.addValueAsLastCause(localThrowable, paramT));
        return;
      }
      request(1L);
    }

    public void setProducer(Producer paramProducer)
    {
      super.setProducer(paramProducer);
      this.actual.setProducer(paramProducer);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeFilter
 * JD-Core Version:    0.6.0
 */