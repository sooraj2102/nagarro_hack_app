package rx.internal.subscriptions;

import java.util.concurrent.atomic.AtomicReference;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public final class SequentialSubscription extends AtomicReference<Subscription>
  implements Subscription
{
  private static final long serialVersionUID = 995205034283130269L;

  public SequentialSubscription()
  {
  }

  public SequentialSubscription(Subscription paramSubscription)
  {
    lazySet(paramSubscription);
  }

  public Subscription current()
  {
    Subscription localSubscription = (Subscription)super.get();
    if (localSubscription == Unsubscribed.INSTANCE)
      localSubscription = Subscriptions.unsubscribed();
    return localSubscription;
  }

  public boolean isUnsubscribed()
  {
    return get() == Unsubscribed.INSTANCE;
  }

  public boolean replace(Subscription paramSubscription)
  {
    Subscription localSubscription;
    do
    {
      localSubscription = (Subscription)get();
      if (localSubscription != Unsubscribed.INSTANCE)
        continue;
      if (paramSubscription != null)
        paramSubscription.unsubscribe();
      return false;
    }
    while (!compareAndSet(localSubscription, paramSubscription));
    return true;
  }

  public boolean replaceWeak(Subscription paramSubscription)
  {
    Subscription localSubscription = (Subscription)get();
    if (localSubscription == Unsubscribed.INSTANCE)
      if (paramSubscription != null)
        paramSubscription.unsubscribe();
    while (true)
    {
      return false;
      if (compareAndSet(localSubscription, paramSubscription))
        return true;
      if ((Subscription)get() != Unsubscribed.INSTANCE)
        break;
      if (paramSubscription == null)
        continue;
      paramSubscription.unsubscribe();
      return false;
    }
    return true;
  }

  public void unsubscribe()
  {
    if ((Subscription)get() != Unsubscribed.INSTANCE)
    {
      Subscription localSubscription = (Subscription)getAndSet(Unsubscribed.INSTANCE);
      if ((localSubscription != null) && (localSubscription != Unsubscribed.INSTANCE))
        localSubscription.unsubscribe();
    }
  }

  public boolean update(Subscription paramSubscription)
  {
    Subscription localSubscription;
    do
    {
      localSubscription = (Subscription)get();
      if (localSubscription != Unsubscribed.INSTANCE)
        continue;
      if (paramSubscription != null)
        paramSubscription.unsubscribe();
      return false;
    }
    while (!compareAndSet(localSubscription, paramSubscription));
    if (localSubscription != null)
      localSubscription.unsubscribe();
    return true;
  }

  public boolean updateWeak(Subscription paramSubscription)
  {
    int i = 1;
    Subscription localSubscription1 = (Subscription)get();
    if (localSubscription1 == Unsubscribed.INSTANCE)
    {
      if (paramSubscription != null)
        paramSubscription.unsubscribe();
      i = 0;
    }
    Subscription localSubscription2;
    do
    {
      do
        return i;
      while (compareAndSet(localSubscription1, paramSubscription));
      localSubscription2 = (Subscription)get();
      if (paramSubscription == null)
        continue;
      paramSubscription.unsubscribe();
    }
    while (localSubscription2 == Unsubscribed.INSTANCE);
    return false;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.subscriptions.SequentialSubscription
 * JD-Core Version:    0.6.0
 */