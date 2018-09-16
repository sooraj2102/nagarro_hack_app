package retrofit2.adapter.rxjava;

import java.util.concurrent.atomic.AtomicInteger;
import retrofit2.Call;
import retrofit2.Response;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;

final class CallArbiter<T> extends AtomicInteger
  implements Subscription, Producer
{
  private static final int STATE_HAS_RESPONSE = 2;
  private static final int STATE_REQUESTED = 1;
  private static final int STATE_TERMINATED = 3;
  private static final int STATE_WAITING;
  private final Call<T> call;
  private volatile Response<T> response;
  private final Subscriber<? super Response<T>> subscriber;

  CallArbiter(Call<T> paramCall, Subscriber<? super Response<T>> paramSubscriber)
  {
    super(0);
    this.call = paramCall;
    this.subscriber = paramSubscriber;
  }

  private void deliverResponse(Response<T> paramResponse)
  {
    try
    {
      if (!isUnsubscribed())
        this.subscriber.onNext(paramResponse);
    }
    catch (Throwable localThrowable1)
    {
      try
      {
        this.subscriber.onCompleted();
        return;
        localThrowable1 = localThrowable1;
        Exceptions.throwIfFatal(localThrowable1);
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
          return;
        }
      }
      catch (Throwable localThrowable3)
      {
        Exceptions.throwIfFatal(localThrowable3);
        RxJavaPlugins.getInstance().getErrorHandler().handleError(localThrowable3);
      }
    }
  }

  void emitError(Throwable paramThrowable)
  {
    set(3);
    if (!isUnsubscribed());
    try
    {
      this.subscriber.onError(paramThrowable);
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwIfFatal(localThrowable);
      CompositeException localCompositeException = new CompositeException(new Throwable[] { paramThrowable, localThrowable });
      RxJavaPlugins.getInstance().getErrorHandler().handleError(localCompositeException);
    }
  }

  void emitResponse(Response<T> paramResponse)
  {
    do
    {
      do
      {
        int i = get();
        switch (i)
        {
        default:
          throw new IllegalStateException("Unknown state: " + i);
        case 0:
          this.response = paramResponse;
        case 1:
        case 2:
        case 3:
        }
      }
      while (!compareAndSet(0, 2));
      return;
    }
    while (!compareAndSet(1, 3));
    deliverResponse(paramResponse);
    return;
    throw new AssertionError();
  }

  public boolean isUnsubscribed()
  {
    return this.call.isCanceled();
  }

  public void request(long paramLong)
  {
    if (paramLong == 0L)
      return;
    do
    {
      do
      {
        int i = get();
        switch (i)
        {
        case 1:
        case 3:
        default:
          throw new IllegalStateException("Unknown state: " + i);
        case 0:
        case 2:
        }
      }
      while (!compareAndSet(0, 1));
      return;
    }
    while (!compareAndSet(2, 3));
    deliverResponse(this.response);
  }

  public void unsubscribe()
  {
    this.call.cancel();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.adapter.rxjava.CallArbiter
 * JD-Core Version:    0.6.0
 */