package rx.internal.util.unsafe;

import rx.internal.util.SuppressAnimalSniffer;

@SuppressAnimalSniffer
abstract class SpmcArrayQueueProducerIndexCacheField<E> extends SpmcArrayQueueMidPad<E>
{
  private volatile long producerIndexCache;

  public SpmcArrayQueueProducerIndexCacheField(int paramInt)
  {
    super(paramInt);
  }

  protected final long lvProducerIndexCache()
  {
    return this.producerIndexCache;
  }

  protected final void svProducerIndexCache(long paramLong)
  {
    this.producerIndexCache = paramLong;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.SpmcArrayQueueProducerIndexCacheField
 * JD-Core Version:    0.6.0
 */