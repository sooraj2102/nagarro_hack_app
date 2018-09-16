package rx.internal.subscriptions;

import rx.Subscription;

public enum Unsubscribed
  implements Subscription
{
  static
  {
    Unsubscribed[] arrayOfUnsubscribed = new Unsubscribed[1];
    arrayOfUnsubscribed[0] = INSTANCE;
    $VALUES = arrayOfUnsubscribed;
  }

  public boolean isUnsubscribed()
  {
    return true;
  }

  public void unsubscribe()
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.subscriptions.Unsubscribed
 * JD-Core Version:    0.6.0
 */