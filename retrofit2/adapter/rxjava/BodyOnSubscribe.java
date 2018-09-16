package retrofit2.adapter.rxjava;

import retrofit2.Response;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;

final class BodyOnSubscribe<T>
  implements Observable.OnSubscribe<T>
{
  private final Observable.OnSubscribe<Response<T>> upstream;

  BodyOnSubscribe(Observable.OnSubscribe<Response<T>> paramOnSubscribe)
  {
    this.upstream = paramOnSubscribe;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    this.upstream.call(new BodySubscriber(paramSubscriber));
  }

  private static class BodySubscriber<R> extends Subscriber<Response<R>>
  {
    private final Subscriber<? super R> subscriber;
    private boolean subscriberTerminated;

    BodySubscriber(Subscriber<? super R> paramSubscriber)
    {
      super();
      this.subscriber = paramSubscriber;
    }

    public void onCompleted()
    {
      if (!this.subscriberTerminated)
        this.subscriber.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      if (!this.subscriberTerminated)
      {
        this.subscriber.onError(paramThrowable);
        return;
      }
      AssertionError localAssertionError = new AssertionError("This should never happen! Report as a Retrofit bug with the full stacktrace.");
      localAssertionError.initCause(paramThrowable);
      RxJavaPlugins.getInstance().getErrorHandler().handleError(localAssertionError);
    }

    public void onNext(Response<R> paramResponse)
    {
      if (paramResponse.isSuccessful())
      {
        this.subscriber.onNext(paramResponse.body());
        return;
      }
      this.subscriberTerminated = true;
      HttpException localHttpException = new HttpException(paramResponse);
      try
      {
        this.subscriber.onError(localHttpException);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        CompositeException localCompositeException = new CompositeException(new Throwable[] { localHttpException, localThrowable });
        RxJavaPlugins.getInstance().getErrorHandler().handleError(localCompositeException);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.adapter.rxjava.BodyOnSubscribe
 * JD-Core Version:    0.6.0
 */