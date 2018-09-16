package rx.internal.util.atomic;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class SpscAtomicArrayQueue<E> extends AtomicReferenceArrayQueue<E>
{
  private static final Integer MAX_LOOK_AHEAD_STEP = Integer.getInteger("jctools.spsc.max.lookahead.step", 4096);
  final AtomicLong consumerIndex = new AtomicLong();
  final int lookAheadStep;
  final AtomicLong producerIndex = new AtomicLong();
  long producerLookAhead;

  public SpscAtomicArrayQueue(int paramInt)
  {
    super(paramInt);
    this.lookAheadStep = Math.min(paramInt / 4, MAX_LOOK_AHEAD_STEP.intValue());
  }

  private long lvConsumerIndex()
  {
    return this.consumerIndex.get();
  }

  private long lvProducerIndex()
  {
    return this.producerIndex.get();
  }

  private void soConsumerIndex(long paramLong)
  {
    this.consumerIndex.lazySet(paramLong);
  }

  private void soProducerIndex(long paramLong)
  {
    this.producerIndex.lazySet(paramLong);
  }

  public boolean isEmpty()
  {
    return lvProducerIndex() == lvConsumerIndex();
  }

  public boolean offer(E paramE)
  {
    if (paramE == null)
      throw new NullPointerException("Null is not a valid element");
    AtomicReferenceArray localAtomicReferenceArray = this.buffer;
    int i = this.mask;
    long l = this.producerIndex.get();
    int j = calcElementOffset(l, i);
    if (l >= this.producerLookAhead)
    {
      int k = this.lookAheadStep;
      if (lvElement(localAtomicReferenceArray, calcElementOffset(l + k, i)) != null)
        break label105;
      this.producerLookAhead = (l + k);
    }
    label105: 
    do
    {
      soElement(localAtomicReferenceArray, j, paramE);
      soProducerIndex(1L + l);
      return true;
    }
    while (lvElement(localAtomicReferenceArray, j) == null);
    return false;
  }

  public E peek()
  {
    return lvElement(calcElementOffset(this.consumerIndex.get()));
  }

  public E poll()
  {
    long l = this.consumerIndex.get();
    int i = calcElementOffset(l);
    AtomicReferenceArray localAtomicReferenceArray = this.buffer;
    Object localObject = lvElement(localAtomicReferenceArray, i);
    if (localObject == null)
      return null;
    soElement(localAtomicReferenceArray, i, null);
    soConsumerIndex(1L + l);
    return localObject;
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
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.atomic.SpscAtomicArrayQueue
 * JD-Core Version:    0.6.0
 */