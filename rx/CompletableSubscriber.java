package rx;

import rx.annotations.Experimental;

@Experimental
public abstract interface CompletableSubscriber
{
  public abstract void onCompleted();

  public abstract void onError(Throwable paramThrowable);

  public abstract void onSubscribe(Subscription paramSubscription);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.CompletableSubscriber
 * JD-Core Version:    0.6.0
 */