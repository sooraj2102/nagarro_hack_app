package rx.internal.util.unsafe;

import rx.internal.util.SuppressAnimalSniffer;
import rx.internal.util.atomic.LinkedQueueNode;
import sun.misc.Unsafe;

@SuppressAnimalSniffer
public final class MpscLinkedQueue<E> extends BaseLinkedQueue<E>
{
  public MpscLinkedQueue()
  {
    this.consumerNode = new LinkedQueueNode();
    xchgProducerNode(this.consumerNode);
  }

  public boolean offer(E paramE)
  {
    if (paramE == null)
      throw new NullPointerException("null elements not allowed");
    LinkedQueueNode localLinkedQueueNode = new LinkedQueueNode(paramE);
    xchgProducerNode(localLinkedQueueNode).soNext(localLinkedQueueNode);
    return true;
  }

  public E peek()
  {
    LinkedQueueNode localLinkedQueueNode1 = this.consumerNode;
    LinkedQueueNode localLinkedQueueNode2 = localLinkedQueueNode1.lvNext();
    if (localLinkedQueueNode2 != null)
      return localLinkedQueueNode2.lpValue();
    if (localLinkedQueueNode1 != lvProducerNode())
    {
      LinkedQueueNode localLinkedQueueNode3;
      do
        localLinkedQueueNode3 = localLinkedQueueNode1.lvNext();
      while (localLinkedQueueNode3 == null);
      return localLinkedQueueNode3.lpValue();
    }
    return null;
  }

  public E poll()
  {
    LinkedQueueNode localLinkedQueueNode1 = lpConsumerNode();
    LinkedQueueNode localLinkedQueueNode2 = localLinkedQueueNode1.lvNext();
    if (localLinkedQueueNode2 != null)
    {
      Object localObject2 = localLinkedQueueNode2.getAndNullValue();
      spConsumerNode(localLinkedQueueNode2);
      return localObject2;
    }
    if (localLinkedQueueNode1 != lvProducerNode())
    {
      LinkedQueueNode localLinkedQueueNode3;
      do
        localLinkedQueueNode3 = localLinkedQueueNode1.lvNext();
      while (localLinkedQueueNode3 == null);
      Object localObject1 = localLinkedQueueNode3.getAndNullValue();
      this.consumerNode = localLinkedQueueNode3;
      return localObject1;
    }
    return null;
  }

  protected LinkedQueueNode<E> xchgProducerNode(LinkedQueueNode<E> paramLinkedQueueNode)
  {
    LinkedQueueNode localLinkedQueueNode;
    do
      localLinkedQueueNode = this.producerNode;
    while (!UnsafeAccess.UNSAFE.compareAndSwapObject(this, P_NODE_OFFSET, localLinkedQueueNode, paramLinkedQueueNode));
    return (LinkedQueueNode)localLinkedQueueNode;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.MpscLinkedQueue
 * JD-Core Version:    0.6.0
 */