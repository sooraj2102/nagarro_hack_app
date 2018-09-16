package retrofit2.adapter.rxjava;

import retrofit2.Response;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;

final class ResultOnSubscribe<T>
  implements Observable.OnSubscribe<Result<T>>
{
  private final Observable.OnSubscribe<Response<T>> upstream;

  ResultOnSubscribe(Observable.OnSubscribe<Response<T>> paramOnSubscribe)
  {
    this.upstream = paramOnSubscribe;
  }

  public void call(Subscriber<? super Result<T>> paramSubscriber)
  {
    this.upstream.call(new ResultSubscriber(paramSubscriber));
  }

  private static class ResultSubscriber<R> extends Subscriber<Response<R>>
  {
    private final Subscriber<? super Result<R>> subscriber;

    ResultSubscriber(Subscriber<? super Result<R>> paramSubscriber)
    {
      super();
      this.subscriber = paramSubscriber;
    }

    public void onCompleted()
    {
      this.subscriber.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      try
      {
        this.subscriber.onNext(Result.error(paramThrowable));
        this.subscriber.onCompleted();
        return;
      }
      catch (Throwable localThrowable1)
      {
        try
        {
          this.subscriber.onError(localThrowable1);
          return;
        }
        catch (Throwable localThrowable2)
        {
          Exceptions.throwIfFatal(localThrowable2);
          CompositeException localCompositeException = new CompositeException(new Throwable[] { localThrowable1, localThrowable2 });
          RxJavaPlugins.getInstance().getErrorHandler().handleError(localCompositeException);
        }
      }
    }

    public void onNext(Response<R> paramResponse)
    {
      this.subscriber.onNext(Result.response(paramResponse));
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.adapter.rxjava.ResultOnSubscribe
 * JD-Core Version:    0.6.0
 */