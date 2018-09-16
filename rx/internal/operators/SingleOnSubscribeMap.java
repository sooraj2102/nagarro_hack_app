package rx.internal.operators;

import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;

public final class SingleOnSubscribeMap<T, R>
  implements Single.OnSubscribe<R>
{
  final Single<T> source;
  final Func1<? super T, ? extends R> transformer;

  public SingleOnSubscribeMap(Single<T> paramSingle, Func1<? super T, ? extends R> paramFunc1)
  {
    this.source = paramSingle;
    this.transformer = paramFunc1;
  }

  public void call(SingleSubscriber<? super R> paramSingleSubscriber)
  {
    MapSubscriber localMapSubscriber = new MapSubscriber(paramSingleSubscriber, this.transformer);
    paramSingleSubscriber.add(localMapSubscriber);
    this.source.subscribe(localMapSubscriber);
  }

  static final class MapSubscriber<T, R> extends SingleSubscriber<T>
  {
    final SingleSubscriber<? super R> actual;
    boolean done;
    final Func1<? super T, ? extends R> mapper;

    public MapSubscriber(SingleSubscriber<? super R> paramSingleSubscriber, Func1<? super T, ? extends R> paramFunc1)
    {
      this.actual = paramSingleSubscriber;
      this.mapper = paramFunc1;
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

    public void onSuccess(T paramT)
    {
      try
      {
        Object localObject = this.mapper.call(paramT);
        this.actual.onSuccess(localObject);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        unsubscribe();
        onError(OnErrorThrowable.addValueAsLastCause(localThrowable, paramT));
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.SingleOnSubscribeMap
 * JD-Core Version:    0.6.0
 */