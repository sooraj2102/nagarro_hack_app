package rx.internal.util.atomic;

public final class MpscLinkedAtomicQueue<E> extends BaseLinkedAtomicQueue<E>
{
  public MpscLinkedAtomicQueue()
  {
    LinkedQueueNode localLinkedQueueNode = new LinkedQueueNode();
    spConsumerNode(localLinkedQueueNode);
    xchgProducerNode(localLinkedQueueNode);
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
    LinkedQueueNode localLinkedQueueNode1 = lpConsumerNode();
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
      spConsumerNode(localLinkedQueueNode3);
      return localObject1;
    }
    return null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.atomic.MpscLinkedAtomicQueue
 * JD-Core Version:    0.6.0
 */