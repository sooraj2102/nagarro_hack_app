package rx;

public abstract interface Subscription
{
  public abstract boolean isUnsubscribed();

  public abstract void unsubscribe();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.Subscription
 * JD-Core Version:    0.6.0
 */