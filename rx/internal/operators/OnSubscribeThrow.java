package rx.internal.operators;

import rx.Observable.OnSubscribe;
import rx.Subscriber;

public final class OnSubscribeThrow<T>
  implements Observable.OnSubscribe<T>
{
  private final Throwable exception;

  public OnSubscribeThrow(Throwable paramThrowable)
  {
    this.exception = paramThrowable;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    paramSubscriber.onError(this.exception);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeThrow
 * JD-Core Version:    0.6.0
 */