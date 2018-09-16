package rx;

import rx.internal.util.SubscriptionList;

public abstract class SingleSubscriber<T>
  implements Subscription
{
  private final SubscriptionList cs = new SubscriptionList();

  public final void add(Subscription paramSubscription)
  {
    this.cs.add(paramSubscription);
  }

  public final boolean isUnsubscribed()
  {
    return this.cs.isUnsubscribed();
  }

  public abstract void onError(Throwable paramThrowable);

  public abstract void onSuccess(T paramT);

  public final void unsubscribe()
  {
    this.cs.unsubscribe();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.SingleSubscriber
 * JD-Core Version:    0.6.0
 */