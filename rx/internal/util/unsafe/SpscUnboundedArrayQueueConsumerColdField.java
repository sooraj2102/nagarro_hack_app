package rx.internal.util.unsafe;

abstract class SpscUnboundedArrayQueueConsumerColdField<E> extends SpscUnboundedArrayQueueL2Pad<E>
{
  protected E[] consumerBuffer;
  protected long consumerMask;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.SpscUnboundedArrayQueueConsumerColdField
 * JD-Core Version:    0.6.0
 */