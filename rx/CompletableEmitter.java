package rx;

import rx.annotations.Experimental;

@Experimental
public abstract interface CompletableEmitter
{
  public abstract void onCompleted();

  public abstract void onError(Throwable paramThrowable);

  public abstract void setCancellation(AsyncEmitter.Cancellable paramCancellable);

  public abstract void setSubscription(Subscription paramSubscription);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.CompletableEmitter
 * JD-Core Version:    0.6.0
 */