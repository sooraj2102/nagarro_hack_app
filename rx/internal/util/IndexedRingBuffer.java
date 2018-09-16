package rx.internal.util;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import rx.Subscription;
import rx.functions.Func1;

public final class IndexedRingBuffer<E>
  implements Subscription
{
  private static final ObjectPool<IndexedRingBuffer<?>> POOL = new ObjectPool()
  {
    protected IndexedRingBuffer<?> createObject()
    {
      return new IndexedRingBuffer();
    }
  };
  static final int SIZE;
  private final ElementSection<E> elements = new ElementSection();
  final AtomicInteger index = new AtomicInteger();
  private final IndexSection removed = new IndexSection();
  final AtomicInteger removedIndex = new AtomicInteger();

  static
  {
    int i = 128;
    if (PlatformDependent.isAndroid())
      i = 8;
    String str = System.getProperty("rx.indexed-ring-buffer.size");
    if (str != null);
    try
    {
      int j = Integer.parseInt(str);
      i = j;
      SIZE = i;
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      while (true)
        System.err.println("Failed to set 'rx.indexed-ring-buffer.size' with value " + str + " => " + localNumberFormatException.getMessage());
    }
  }

  private int forEach(Func1<? super E, Boolean> paramFunc1, int paramInt1, int paramInt2)
  {
    int i = this.index.get();
    int j = paramInt1;
    ElementSection localElementSection = this.elements;
    if (paramInt1 >= SIZE)
    {
      localElementSection = getElementSection(paramInt1);
      paramInt1 %= SIZE;
    }
    while (true)
    {
      int k;
      if (localElementSection != null)
      {
        k = paramInt1;
        if (k >= SIZE)
          break label120;
        if ((j < i) && (j < paramInt2));
      }
      else
      {
        return j;
      }
      Object localObject = localElementSection.array.get(k);
      if (localObject == null);
      int m;
      do
      {
        k++;
        j++;
        break;
        m = j;
      }
      while (((Boolean)paramFunc1.call(localObject)).booleanValue());
      return m;
      label120: localElementSection = (ElementSection)localElementSection.next.get();
      paramInt1 = 0;
    }
  }

  private ElementSection<E> getElementSection(int paramInt)
  {
    ElementSection localElementSection;
    if (paramInt < SIZE)
      localElementSection = this.elements;
    while (true)
    {
      return localElementSection;
      int i = paramInt / SIZE;
      localElementSection = this.elements;
      for (int j = 0; j < i; j++)
        localElementSection = localElementSection.getNext();
    }
  }

  private int getIndexForAdd()
  {
    monitorenter;
    try
    {
      int i = getIndexFromPreviouslyRemoved();
      int k;
      if (i >= 0)
        if (i < SIZE)
        {
          k = this.removed.getAndSet(i, -1);
          if (k == this.index.get())
            this.index.getAndIncrement();
        }
      while (true)
      {
        return k;
        int j = i % SIZE;
        k = getIndexSection(i).getAndSet(j, -1);
        break;
        int m = this.index.getAndIncrement();
        k = m;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  private int getIndexFromPreviouslyRemoved()
  {
    monitorenter;
    try
    {
      int i;
      boolean bool;
      do
      {
        i = this.removedIndex.get();
        if (i <= 0)
          break;
        bool = this.removedIndex.compareAndSet(i, i - 1);
      }
      while (!bool);
      for (int j = i - 1; ; j = -1)
        return j;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  private IndexSection getIndexSection(int paramInt)
  {
    IndexSection localIndexSection;
    if (paramInt < SIZE)
      localIndexSection = this.removed;
    while (true)
    {
      return localIndexSection;
      int i = paramInt / SIZE;
      localIndexSection = this.removed;
      for (int j = 0; j < i; j++)
        localIndexSection = localIndexSection.getNext();
    }
  }

  public static <T> IndexedRingBuffer<T> getInstance()
  {
    return (IndexedRingBuffer)POOL.borrowObject();
  }

  private void pushRemovedIndex(int paramInt)
  {
    monitorenter;
    try
    {
      int i = this.removedIndex.getAndIncrement();
      if (i < SIZE)
        this.removed.set(i, paramInt);
      while (true)
      {
        return;
        int j = i % SIZE;
        getIndexSection(i).set(j, paramInt);
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public int add(E paramE)
  {
    int i = getIndexForAdd();
    if (i < SIZE)
    {
      this.elements.array.set(i, paramE);
      return i;
    }
    int j = i % SIZE;
    getElementSection(i).array.set(j, paramE);
    return i;
  }

  public int forEach(Func1<? super E, Boolean> paramFunc1)
  {
    return forEach(paramFunc1, 0);
  }

  public int forEach(Func1<? super E, Boolean> paramFunc1, int paramInt)
  {
    int i = forEach(paramFunc1, paramInt, this.index.get());
    if ((paramInt > 0) && (i == this.index.get()))
      i = forEach(paramFunc1, 0, paramInt);
    do
      return i;
    while (i != this.index.get());
    return 0;
  }

  public boolean isUnsubscribed()
  {
    return false;
  }

  public void releaseToPool()
  {
    int i = this.index.get();
    int j = 0;
    for (ElementSection localElementSection = this.elements; ; localElementSection = (ElementSection)localElementSection.next.get())
    {
      int k;
      if (localElementSection != null)
        k = 0;
      while (k < SIZE)
      {
        if (j >= i)
        {
          this.index.set(0);
          this.removedIndex.set(0);
          POOL.returnObject(this);
          return;
        }
        localElementSection.array.set(k, null);
        k++;
        j++;
      }
    }
  }

  public E remove(int paramInt)
  {
    if (paramInt < SIZE);
    int i;
    for (Object localObject = this.elements.array.getAndSet(paramInt, null); ; localObject = getElementSection(paramInt).array.getAndSet(i, null))
    {
      pushRemovedIndex(paramInt);
      return localObject;
      i = paramInt % SIZE;
    }
  }

  public void unsubscribe()
  {
    releaseToPool();
  }

  static final class ElementSection<E>
  {
    final AtomicReferenceArray<E> array = new AtomicReferenceArray(IndexedRingBuffer.SIZE);
    final AtomicReference<ElementSection<E>> next = new AtomicReference();

    ElementSection<E> getNext()
    {
      if (this.next.get() != null)
        return (ElementSection)this.next.get();
      ElementSection localElementSection = new ElementSection();
      if (this.next.compareAndSet(null, localElementSection))
        return localElementSection;
      return (ElementSection)this.next.get();
    }
  }

  static class IndexSection
  {
    private final AtomicReference<IndexSection> _next = new AtomicReference();
    private final AtomicIntegerArray unsafeArray = new AtomicIntegerArray(IndexedRingBuffer.SIZE);

    public int getAndSet(int paramInt1, int paramInt2)
    {
      return this.unsafeArray.getAndSet(paramInt1, paramInt2);
    }

    IndexSection getNext()
    {
      if (this._next.get() != null)
        return (IndexSection)this._next.get();
      IndexSection localIndexSection = new IndexSection();
      if (this._next.compareAndSet(null, localIndexSection))
        return localIndexSection;
      return (IndexSection)this._next.get();
    }

    public void set(int paramInt1, int paramInt2)
    {
      this.unsafeArray.set(paramInt1, paramInt2);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.IndexedRingBuffer
 * JD-Core Version:    0.6.0
 */