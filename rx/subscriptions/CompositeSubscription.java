package rx.subscriptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import rx.Subscription;
import rx.exceptions.Exceptions;

public final class CompositeSubscription
  implements Subscription
{
  private Set<Subscription> subscriptions;
  private volatile boolean unsubscribed;

  public CompositeSubscription()
  {
  }

  public CompositeSubscription(Subscription[] paramArrayOfSubscription)
  {
    this.subscriptions = new HashSet(Arrays.asList(paramArrayOfSubscription));
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
          if (this.subscriptions == null)
            this.subscriptions = new HashSet(4);
          this.subscriptions.add(paramSubscription);
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

  public void addAll(Subscription[] paramArrayOfSubscription)
  {
    if (!this.unsubscribed)
      monitorenter;
    while (true)
    {
      int m;
      try
      {
        if (this.unsubscribed)
          continue;
        if (this.subscriptions != null)
          continue;
        this.subscriptions = new HashSet(paramArrayOfSubscription.length);
        int k = paramArrayOfSubscription.length;
        m = 0;
        if (m >= k)
          continue;
        Subscription localSubscription = paramArrayOfSubscription[m];
        if (!localSubscription.isUnsubscribed())
        {
          this.subscriptions.add(localSubscription);
          break label117;
          return;
          monitorexit;
          int i = paramArrayOfSubscription.length;
          int j = 0;
          if (j >= i)
            break;
          paramArrayOfSubscription[j].unsubscribe();
          j++;
          continue;
        }
      }
      finally
      {
        monitorexit;
      }
      label117: m++;
    }
  }

  public void clear()
  {
    if (!this.unsubscribed)
    {
      monitorenter;
      try
      {
        if ((this.unsubscribed) || (this.subscriptions == null))
          return;
        Set localSet = this.subscriptions;
        this.subscriptions = null;
        monitorexit;
        unsubscribeFromAll(localSet);
        return;
      }
      finally
      {
        monitorexit;
      }
    }
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
          Set localSet = this.subscriptions;
          i = 0;
          if (localSet != null)
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
        if ((this.unsubscribed) || (this.subscriptions == null))
          return;
        boolean bool = this.subscriptions.remove(paramSubscription);
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
        Set localSet = this.subscriptions;
        this.subscriptions = null;
        monitorexit;
        unsubscribeFromAll(localSet);
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
 * Qualified Name:     rx.subscriptions.CompositeSubscription
 * JD-Core Version:    0.6.0
 */