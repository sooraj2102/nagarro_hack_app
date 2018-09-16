package rx.subscriptions;

import rx.Subscription;
import rx.internal.subscriptions.SequentialSubscription;

public final class SerialSubscription
  implements Subscription
{
  final SequentialSubscription state = new SequentialSubscription();

  public Subscription get()
  {
    return this.state.current();
  }

  public boolean isUnsubscribed()
  {
    return this.state.isUnsubscribed();
  }

  public void set(Subscription paramSubscription)
  {
    if (paramSubscription == null)
      throw new IllegalArgumentException("Subscription can not be null");
    this.state.update(paramSubscription);
  }

  public void unsubscribe()
  {
    this.state.unsubscribe();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.subscriptions.SerialSubscription
 * JD-Core Version:    0.6.0
 */