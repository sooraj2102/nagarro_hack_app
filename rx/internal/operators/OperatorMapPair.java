package rx.internal.operators;

import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.plugins.RxJavaHooks;

public final class OperatorMapPair<T, U, R>
  implements Observable.Operator<Observable<? extends R>, T>
{
  final Func1<? super T, ? extends Observable<? extends U>> collectionSelector;
  final Func2<? super T, ? super U, ? extends R> resultSelector;

  public OperatorMapPair(Func1<? super T, ? extends Observable<? extends U>> paramFunc1, Func2<? super T, ? super U, ? extends R> paramFunc2)
  {
    this.collectionSelector = paramFunc1;
    this.resultSelector = paramFunc2;
  }

  public static <T, U> Func1<T, Observable<U>> convertSelector(Func1<? super T, ? extends Iterable<? extends U>> paramFunc1)
  {
    return new Func1(paramFunc1)
    {
      public Observable<U> call(T paramT)
      {
        return Observable.from((Iterable)this.val$selector.call(paramT));
      }
    };
  }

  public Subscriber<? super T> call(Subscriber<? super Observable<? extends R>> paramSubscriber)
  {
    MapPairSubscriber localMapPairSubscriber = new MapPairSubscriber(paramSubscriber, this.collectionSelector, this.resultSelector);
    paramSubscriber.add(localMapPairSubscriber);
    return localMapPairSubscriber;
  }

  static final class MapPairSubscriber<T, U, R> extends Subscriber<T>
  {
    final Subscriber<? super Observable<? extends R>> actual;
    final Func1<? super T, ? extends Observable<? extends U>> collectionSelector;
    boolean done;
    final Func2<? super T, ? super U, ? extends R> resultSelector;

    public MapPairSubscriber(Subscriber<? super Observable<? extends R>> paramSubscriber, Func1<? super T, ? extends Observable<? extends U>> paramFunc1, Func2<? super T, ? super U, ? extends R> paramFunc2)
    {
      this.actual = paramSubscriber;
      this.collectionSelector = paramFunc1;
      this.resultSelector = paramFunc2;
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
        Observable localObservable = (Observable)this.collectionSelector.call(paramT);
        this.actual.onNext(localObservable.map(new OperatorMapPair.OuterInnerMapper(paramT, this.resultSelector)));
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

  static final class OuterInnerMapper<T, U, R>
    implements Func1<U, R>
  {
    final T outer;
    final Func2<? super T, ? super U, ? extends R> resultSelector;

    public OuterInnerMapper(T paramT, Func2<? super T, ? super U, ? extends R> paramFunc2)
    {
      this.outer = paramT;
      this.resultSelector = paramFunc2;
    }

    public R call(U paramU)
    {
      return this.resultSelector.call(this.outer, paramU);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorMapPair
 * JD-Core Version:    0.6.0
 */