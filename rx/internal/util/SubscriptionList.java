package rx.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import rx.Subscription;
import rx.exceptions.Exceptions;

public final class SubscriptionList
  implements Subscription
{
  private List<Subscription> subscriptions;
  private volatile boolean unsubscribed;

  public SubscriptionList()
  {
  }

  public SubscriptionList(Subscription paramSubscription)
  {
    this.subscriptions = new LinkedList();
    this.subscriptions.add(paramSubscription);
  }

  public SubscriptionList(Subscription[] paramArrayOfSubscription)
  {
    this.subscriptions = new LinkedList(Arrays.asList(paramArrayOfSubscription));
  }

  private static void unsubscribeFromAll(Collection<Subscription> paramCollection)
  {
    if (paramCollection == null)
      return;
    ArrayList localArrayList = null;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Subscription localSubscription = (Subscription)localIterator.next();
      try
      {
        localSubscription.unsubscribe();
      }
      catch (Throwable localThrowable)
      {
        if (localArrayList == null)
          localArrayList = new ArrayList();
        localArrayList.add(localThrowable);
      }
    }
    Exceptions.throwIfAny(localArrayList);
  }

  public void add(Subscription paramSubscription)
  {
    if (paramSubscription.isUnsubscribed())
      return;
    if (!this.unsubscribed)
    {
      monitorenter;
      try
      {
        if (!this.unsubscribed)
        {
          Object localObject2 = this.subscriptions;
          if (localObject2 == null)
          {
            localObject2 = new LinkedList();
            this.subscriptions = ((List)localObject2);
          }
          ((List)localObject2).add(paramSubscription);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      monitorexit;
    }
    paramSubscription.unsubscribe();
  }

  public void clear()
  {
    if (!this.unsubscribed)
      monitorenter;
    try
    {
      List localList = this.subscriptions;
      this.subscriptions = null;
      monitorexit;
      unsubscribeFromAll(localList);
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public boolean hasSubscriptions()
  {
    if (!this.unsubscribed)
    {
      monitorenter;
      try
      {
        boolean bool1 = this.unsubscribed;
        int i = 0;
        if (!bool1)
        {
          List localList = this.subscriptions;
          i = 0;
          if (localList != null)
          {
            boolean bool2 = this.subscriptions.isEmpty();
            i = 0;
            if (!bool2)
              i = 1;
          }
        }
        return i;
      }
      finally
      {
        monitorexit;
      }
    }
    return false;
  }

  public boolean isUnsubscribed()
  {
    return this.unsubscribed;
  }

  public void remove(Subscription paramSubscription)
  {
    if (!this.unsubscribed)
    {
      monitorenter;
      try
      {
        List localList = this.subscriptions;
        if ((this.unsubscribed) || (localList == null))
          return;
        boolean bool = localList.remove(paramSubscription);
        monitorexit;
        if (bool)
        {
          paramSubscription.unsubscribe();
          return;
        }
      }
      finally
      {
        monitorexit;
      }
    }
  }

  public void unsubscribe()
  {
    if (!this.unsubscribed)
    {
      monitorenter;
      try
      {
        if (this.unsubscribed)
          return;
        this.unsubscribed = true;
        List localList = this.subscriptions;
        this.subscriptions = null;
        monitorexit;
        unsubscribeFromAll(localList);
        return;
      }
      finally
      {
        monitorexit;
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.SubscriptionList
 * JD-Core Version:    0.6.0
 */