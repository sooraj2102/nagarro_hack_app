package rx.internal.util.unsafe;

import java.util.AbstractQueue;

abstract class SpscUnboundedArrayQueueProducerFields<E> extends AbstractQueue<E>
{
  protected long producerIndex;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.SpscUnboundedArrayQueueProducerFields
 * JD-Core Version:    0.6.0
 */