package rx.internal.util.unsafe;

import rx.internal.util.SuppressAnimalSniffer;
import sun.misc.Unsafe;

@SuppressAnimalSniffer
public final class SpscArrayQueue<E> extends SpscArrayQueueL3Pad<E>
{
  public SpscArrayQueue(int paramInt)
  {
    super(paramInt);
  }

  private long lvConsumerIndex()
  {
    return UnsafeAccess.UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
  }

  private long lvProducerIndex()
  {
    return UnsafeAccess.UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
  }

  private void soConsumerIndex(long paramLong)
  {
    UnsafeAccess.UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, paramLong);
  }

  private void soProducerIndex(long paramLong)
  {
    UnsafeAccess.UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, paramLong);
  }

  public boolean isEmpty()
  {
    return lvProducerIndex() == lvConsumerIndex();
  }

  public boolean offer(E paramE)
  {
    if (paramE == null)
      throw new NullPointerException("null elements not allowed");
    Object[] arrayOfObject = this.buffer;
    long l1 = this.producerIndex;
    long l2 = calcElementOffset(l1);
    if (lvElement(arrayOfObject, l2) != null)
      return false;
    soElement(arrayOfObject, l2, paramE);
    soProducerIndex(1L + l1);
    return true;
  }

  public E peek()
  {
    return lvElement(calcElementOffset(this.consumerIndex));
  }

  public E poll()
  {
    long l1 = this.consumerIndex;
    long l2 = calcElementOffset(l1);
    Object[] arrayOfObject = this.buffer;
    Object localObject = lvElement(arrayOfObject, l2);
    if (localObject == null)
      return null;
    soElement(arrayOfObject, l2, null);
    soConsumerIndex(1L + l1);
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
 * Qualified Name:     rx.internal.util.unsafe.SpscArrayQueue
 * JD-Core Version:    0.6.0
 */