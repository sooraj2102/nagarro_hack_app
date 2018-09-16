package rx.internal.util.unsafe;

import rx.internal.util.SuppressAnimalSniffer;
import sun.misc.Unsafe;

@SuppressAnimalSniffer
abstract class MpmcArrayQueueConsumerField<E> extends MpmcArrayQueueL2Pad<E>
{
  private static final long C_INDEX_OFFSET = UnsafeAccess.addressOf(MpmcArrayQueueConsumerField.class, "consumerIndex");
  private volatile long consumerIndex;

  public MpmcArrayQueueConsumerField(int paramInt)
  {
    super(paramInt);
  }

  protected final boolean casConsumerIndex(long paramLong1, long paramLong2)
  {
    return UnsafeAccess.UNSAFE.compareAndSwapLong(this, C_INDEX_OFFSET, paramLong1, paramLong2);
  }

  protected final long lvConsumerIndex()
  {
    return this.consumerIndex;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.MpmcArrayQueueConsumerField
 * JD-Core Version:    0.6.0
 */