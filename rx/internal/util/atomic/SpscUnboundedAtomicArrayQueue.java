package rx.internal.util.atomic;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import rx.internal.util.unsafe.Pow2;

public final class SpscUnboundedAtomicArrayQueue<T>
  implements Queue<T>
{
  private static final Object HAS_NEXT;
  static final int MAX_LOOK_AHEAD_STEP = Integer.getInteger("jctools.spsc.max.lookahead.step", 4096).intValue();
  AtomicReferenceArray<Object> consumerBuffer;
  final AtomicLong consumerIndex;
  int consumerMask;
  AtomicReferenceArray<Object> producerBuffer;
  final AtomicLong producerIndex;
  long producerLookAhead;
  int producerLookAheadStep;
  int producerMask;

  static
  {
    HAS_NEXT = new Object();
  }

  public SpscUnboundedAtomicArrayQueue(int paramInt)
  {
    int i = Pow2.roundToPowerOfTwo(Math.max(8, paramInt));
    int j = i - 1;
    this.producerIndex = new AtomicLong();
    this.consumerIndex = new AtomicLong();
    AtomicReferenceArray localAtomicReferenceArray = new AtomicReferenceArray(i + 1);
    this.producerBuffer = localAtomicReferenceArray;
    this.producerMask = j;
    adjustLookAheadStep(i);
    this.consumerBuffer = localAtomicReferenceArray;
    this.consumerMask = j;
    this.producerLookAhead = (j - 1);
    soProducerIndex(0L);
  }

  private void adjustLookAheadStep(int paramInt)
  {
    this.producerLookAheadStep = Math.min(paramInt / 4, MAX_LOOK_AHEAD_STEP);
  }

  private static int calcDirectOffset(int paramInt)
  {
    return paramInt;
  }

  private static int calcWrappedOffset(long paramLong, int paramInt)
  {
    return calcDirectOffset(paramInt & (int)paramLong);
  }

  private long lpConsumerIndex()
  {
    return this.consumerIndex.get();
  }

  private long lpProducerIndex()
  {
    return this.producerIndex.get();
  }

  private long lvConsumerIndex()
  {
    return this.consumerIndex.get();
  }

  private static <E> Object lvElement(AtomicReferenceArray<Object> paramAtomicReferenceArray, int paramInt)
  {
    return paramAtomicReferenceArray.get(paramInt);
  }

  private AtomicReferenceArray<Object> lvNext(AtomicReferenceArray<Object> paramAtomicReferenceArray)
  {
    return (AtomicReferenceArray)lvElement(paramAtomicReferenceArray, calcDirectOffset(-1 + paramAtomicReferenceArray.length()));
  }

  private long lvProducerIndex()
  {
    return this.producerIndex.get();
  }

  private T newBufferPeek(AtomicReferenceArray<Object> paramAtomicReferenceArray, long paramLong, int paramInt)
  {
    this.consumerBuffer = paramAtomicReferenceArray;
    return lvElement(paramAtomicReferenceArray, calcWrappedOffset(paramLong, paramInt));
  }

  private T newBufferPoll(AtomicReferenceArray<Object> paramAtomicReferenceArray, long paramLong, int paramInt)
  {
    this.consumerBuffer = paramAtomicReferenceArray;
    int i = calcWrappedOffset(paramLong, paramInt);
    Object localObject = lvElement(paramAtomicReferenceArray, i);
    if (localObject == null)
      return null;
    soConsumerIndex(1L + paramLong);
    soElement(paramAtomicReferenceArray, i, null);
    return localObject;
  }

  private void resize(AtomicReferenceArray<Object> paramAtomicReferenceArray, long paramLong1, int paramInt, T paramT, long paramLong2)
  {
    AtomicReferenceArray localAtomicReferenceArray = new AtomicReferenceArray(paramAtomicReferenceArray.length());
    this.producerBuffer = localAtomicReferenceArray;
    this.producerLookAhead = (paramLong1 + paramLong2 - 1L);
    soProducerIndex(paramLong1 + 1L);
    soElement(localAtomicReferenceArray, paramInt, paramT);
    soNext(paramAtomicReferenceArray, localAtomicReferenceArray);
    soElement(paramAtomicReferenceArray, paramInt, HAS_NEXT);
  }

  private void soConsumerIndex(long paramLong)
  {
    this.consumerIndex.lazySet(paramLong);
  }

  private static void soElement(AtomicReferenceArray<Object> paramAtomicReferenceArray, int paramInt, Object paramObject)
  {
    paramAtomicReferenceArray.lazySet(paramInt, paramObject);
  }

  private void soNext(AtomicReferenceArray<Object> paramAtomicReferenceArray1, AtomicReferenceArray<Object> paramAtomicReferenceArray2)
  {
    soElement(paramAtomicReferenceArray1, calcDirectOffset(-1 + paramAtomicReferenceArray1.length()), paramAtomicReferenceArray2);
  }

  private void soProducerIndex(long paramLong)
  {
    this.producerIndex.lazySet(paramLong);
  }

  private boolean writeToQueue(AtomicReferenceArray<Object> paramAtomicReferenceArray, T paramT, long paramLong, int paramInt)
  {
    soProducerIndex(1L + paramLong);
    soElement(paramAtomicReferenceArray, paramInt, paramT);
    return true;
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
    return lvProducerIndex() == lvConsumerIndex();
  }

  public Iterator<T> iterator()
  {
    throw new UnsupportedOperationException();
  }

  public boolean offer(T paramT)
  {
    if (paramT == null)
      throw new NullPointerException();
    AtomicReferenceArray localAtomicReferenceArray = this.producerBuffer;
    long l = lpProducerIndex();
    int i = this.producerMask;
    int j = calcWrappedOffset(l, i);
    if (l < this.producerLookAhead)
      return writeToQueue(localAtomicReferenceArray, paramT, l, j);
    int k = this.producerLookAheadStep;
    if (lvElement(localAtomicReferenceArray, calcWrappedOffset(l + k, i)) == null)
    {
      this.producerLookAhead = (l + k - 1L);
      return writeToQueue(localAtomicReferenceArray, paramT, l, j);
    }
    if (lvElement(localAtomicReferenceArray, calcWrappedOffset(1L + l, i)) != null)
      return writeToQueue(localAtomicReferenceArray, paramT, l, j);
    resize(localAtomicReferenceArray, l, j, paramT, i);
    return true;
  }

  public T peek()
  {
    AtomicReferenceArray localAtomicReferenceArray = this.consumerBuffer;
    long l = lpConsumerIndex();
    int i = this.consumerMask;
    Object localObject = lvElement(localAtomicReferenceArray, calcWrappedOffset(l, i));
    if (localObject == HAS_NEXT)
      localObject = newBufferPeek(lvNext(localAtomicReferenceArray), l, i);
    return localObject;
  }

  public T poll()
  {
    AtomicReferenceArray localAtomicReferenceArray = this.consumerBuffer;
    long l = lpConsumerIndex();
    int i = this.consumerMask;
    int j = calcWrappedOffset(l, i);
    Object localObject = lvElement(localAtomicReferenceArray, j);
    if (localObject == HAS_NEXT);
    for (int k = 1; (localObject != null) && (k == 0); k = 0)
    {
      soConsumerIndex(1L + l);
      soElement(localAtomicReferenceArray, j, null);
      return localObject;
    }
    if (k != 0)
      return newBufferPoll(lvNext(localAtomicReferenceArray), l, i);
    return null;
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
    long l1 = lvConsumerIndex();
    long l2;
    long l3;
    do
    {
      l2 = l1;
      l3 = lvProducerIndex();
      l1 = lvConsumerIndex();
    }
    while (l2 != l1);
    return (int)(l3 - l1);
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
 * Qualified Name:     rx.internal.util.atomic.SpscUnboundedAtomicArrayQueue
 * JD-Core Version:    0.6.0
 */