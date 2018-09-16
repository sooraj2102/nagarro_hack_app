package rx.internal.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedQueue<T>
  implements Queue<T>, Cloneable
{
  private final Queue<T> list = new LinkedList();
  private final int size;

  public SynchronizedQueue()
  {
    this.size = -1;
  }

  public SynchronizedQueue(int paramInt)
  {
    this.size = paramInt;
  }

  public boolean add(T paramT)
  {
    monitorenter;
    try
    {
      boolean bool = this.list.add(paramT);
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean addAll(Collection<? extends T> paramCollection)
  {
    monitorenter;
    try
    {
      boolean bool = this.list.addAll(paramCollection);
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public void clear()
  {
    monitorenter;
    try
    {
      this.list.clear();
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public Object clone()
  {
    monitorenter;
    try
    {
      SynchronizedQueue localSynchronizedQueue = new SynchronizedQueue(this.size);
      localSynchronizedQueue.addAll(this.list);
      monitorexit;
      return localSynchronizedQueue;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean contains(Object paramObject)
  {
    monitorenter;
    try
    {
      boolean bool = this.list.contains(paramObject);
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean containsAll(Collection<?> paramCollection)
  {
    monitorenter;
    try
    {
      boolean bool = this.list.containsAll(paramCollection);
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public T element()
  {
    monitorenter;
    try
    {
      Object localObject2 = this.list.element();
      monitorexit;
      return localObject2;
    }
    finally
    {
      localObject1 = finally;
      monitorexit;
    }
    throw localObject1;
  }

  public boolean equals(Object paramObject)
  {
    int i;
    if (this == paramObject)
      i = 1;
    Class localClass1;
    Class localClass2;
    do
    {
      do
      {
        return i;
        i = 0;
      }
      while (paramObject == null);
      localClass1 = getClass();
      localClass2 = paramObject.getClass();
      i = 0;
    }
    while (localClass1 != localClass2);
    SynchronizedQueue localSynchronizedQueue = (SynchronizedQueue)paramObject;
    return this.list.equals(localSynchronizedQueue.list);
  }

  public int hashCode()
  {
    return this.list.hashCode();
  }

  public boolean isEmpty()
  {
    monitorenter;
    try
    {
      boolean bool = this.list.isEmpty();
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public Iterator<T> iterator()
  {
    monitorenter;
    try
    {
      Iterator localIterator = this.list.iterator();
      monitorexit;
      return localIterator;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean offer(T paramT)
  {
    monitorenter;
    try
    {
      if ((this.size <= -1) || (1 + this.list.size() <= this.size))
      {
        boolean bool = this.list.offer(paramT);
        if (!bool);
      }
      for (int i = 1; ; i = 0)
        return i;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public T peek()
  {
    monitorenter;
    try
    {
      Object localObject2 = this.list.peek();
      monitorexit;
      return localObject2;
    }
    finally
    {
      localObject1 = finally;
      monitorexit;
    }
    throw localObject1;
  }

  public T poll()
  {
    monitorenter;
    try
    {
      Object localObject2 = this.list.poll();
      monitorexit;
      return localObject2;
    }
    finally
    {
      localObject1 = finally;
      monitorexit;
    }
    throw localObject1;
  }

  public T remove()
  {
    monitorenter;
    try
    {
      Object localObject2 = this.list.remove();
      monitorexit;
      return localObject2;
    }
    finally
    {
      localObject1 = finally;
      monitorexit;
    }
    throw localObject1;
  }

  public boolean remove(Object paramObject)
  {
    monitorenter;
    try
    {
      boolean bool = this.list.remove(paramObject);
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean removeAll(Collection<?> paramCollection)
  {
    monitorenter;
    try
    {
      boolean bool = this.list.removeAll(paramCollection);
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean retainAll(Collection<?> paramCollection)
  {
    monitorenter;
    try
    {
      boolean bool = this.list.retainAll(paramCollection);
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public int size()
  {
    monitorenter;
    try
    {
      int i = this.list.size();
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public Object[] toArray()
  {
    monitorenter;
    try
    {
      Object[] arrayOfObject = this.list.toArray();
      monitorexit;
      return arrayOfObject;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public <R> R[] toArray(R[] paramArrayOfR)
  {
    monitorenter;
    try
    {
      Object[] arrayOfObject = this.list.toArray(paramArrayOfR);
      monitorexit;
      return arrayOfObject;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public String toString()
  {
    monitorenter;
    try
    {
      String str = this.list.toString();
      monitorexit;
      return str;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.SynchronizedQueue
 * JD-Core Version:    0.6.0
 */