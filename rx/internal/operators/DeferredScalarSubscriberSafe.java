package rx.internal.operators;

import rx.Subscriber;
import rx.plugins.RxJavaHooks;

public abstract class DeferredScalarSubscriberSafe<T, R> extends DeferredScalarSubscriber<T, R>
{
  protected boolean done;

  public DeferredScalarSubscriberSafe(Subscriber<? super R> paramSubscriber)
  {
    super(paramSubscriber);
  }

  public void onCompleted()
  {
    if (this.done)
      return;
    this.done = true;
    super.onCompleted();
  }

  public void onError(Throwable paramThrowable)
  {
    if (!this.done)
    {
      this.done = true;
      super.onError(paramThrowable);
      return;
    }
    RxJavaHooks.onError(paramThrowable);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.DeferredScalarSubscriberSafe
 * JD-Core Version:    0.6.0
 */