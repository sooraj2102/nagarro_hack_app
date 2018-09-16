package rx.internal.util.atomic;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import rx.internal.util.unsafe.Pow2;

public final class SpscExactAtomicArrayQueue<T> extends AtomicReferenceArray<T>
  implements Queue<T>
{
  private static final long serialVersionUID = 6210984603741293445L;
  final int capacitySkip;
  final AtomicLong consumerIndex;
  final int mask;
  final AtomicLong producerIndex;

  public SpscExactAtomicArrayQueue(int paramInt)
  {
    super(Pow2.roundToPowerOfTwo(paramInt));
    int i = length();
    this.mask = (i - 1);
    this.capacitySkip = (i - paramInt);
    this.producerIndex = new AtomicLong();
    this.consumerIndex = new AtomicLong();
  }

  public boolean add(T paramT)
  {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(Collection<? extends T> paramCollection)
  {
    throw new UnsupportedOperationException();
  }

  public void clear()
  {
    while ((poll() != null) || (!isEmpty()));
  }

  public boolean contains(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }

  public boolean containsAll(Collection<?> paramCollection)
  {
    throw new UnsupportedOperationException();
  }

  public T element()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isEmpty()
  {
    return this.producerIndex == this.consumerIndex;
  }

  public Iterator<T> iterator()
  {
    throw new UnsupportedOperationException();
  }

  public boolean offer(T paramT)
  {
    if (paramT == null)
      throw new NullPointerException();
    long l = this.producerIndex.get();
    int i = this.mask;
    if (get(i & (int)(l + this.capacitySkip)) != null)
      return false;
    int j = i & (int)l;
    this.producerIndex.lazySet(1L + l);
    lazySet(j, paramT);
    return true;
  }

  public T peek()
  {
    return get((int)this.consumerIndex.get() & this.mask);
  }

  public T poll()
  {
    long l = this.consumerIndex.get();
    int i = (int)l & this.mask;
    Object localObject = get(i);
    if (localObject == null)
      return null;
    this.consumerIndex.lazySet(1L + l);
    lazySet(i, null);
    return localObject;
  }

  public T remove()
  {
    throw new UnsupportedOperationException();
  }

  public boolean remove(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(Collection<?> paramCollection)
  {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(Collection<?> paramCollection)
  {
    throw new UnsupportedOperationException();
  }

  public int size()
  {
    long l1 = this.consumerIndex.get();
    while (true)
    {
      long l2 = this.producerIndex.get();
      long l3 = this.consumerIndex.get();
      if (l1 == l3)
        return (int)(l2 - l3);
      l1 = l3;
    }
  }

  public Object[] toArray()
  {
    throw new UnsupportedOperationException();
  }

  public <E> E[] toArray(E[] paramArrayOfE)
  {
    throw new UnsupportedOperationException();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.atomic.SpscExactAtomicArrayQueue
 * JD-Core Version:    0.6.0
 */