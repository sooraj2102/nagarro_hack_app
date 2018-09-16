package rx.internal.operators;

import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

public final class CompletableFlatMapSingleToCompletable<T>
  implements Completable.OnSubscribe
{
  final Func1<? super T, ? extends Completable> mapper;
  final Single<T> source;

  public CompletableFlatMapSingleToCompletable(Single<T> paramSingle, Func1<? super T, ? extends Completable> paramFunc1)
  {
    this.source = paramSingle;
    this.mapper = paramFunc1;
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    SourceSubscriber localSourceSubscriber = new SourceSubscriber(paramCompletableSubscriber, this.mapper);
    paramCompletableSubscriber.onSubscribe(localSourceSubscriber);
    this.source.subscribe(localSourceSubscriber);
  }

  static final class SourceSubscriber<T> extends SingleSubscriber<T>
    implements CompletableSubscriber
  {
    final CompletableSubscriber actual;
    final Func1<? super T, ? extends Completable> mapper;

    public SourceSubscriber(CompletableSubscriber paramCompletableSubscriber, Func1<? super T, ? extends Completable> paramFunc1)
    {
      this.actual = paramCompletableSubscriber;
      this.mapper = paramFunc1;
    }

    public void onCompleted()
    {
      this.actual.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      this.actual.onError(paramThrowable);
    }

    public void onSubscribe(Subscription paramSubscription)
    {
      add(paramSubscription);
    }

    public void onSuccess(T paramT)
    {
      Completable localCompletable;
      try
      {
        localCompletable = (Completable)this.mapper.call(paramT);
        if (localCompletable == null)
        {
          onError(new NullPointerException("The mapper returned a null Completable"));
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        onError(localThrowable);
        return;
      }
      localCompletable.subscribe(this);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableFlatMapSingleToCompletable
 * JD-Core Version:    0.6.0
 */