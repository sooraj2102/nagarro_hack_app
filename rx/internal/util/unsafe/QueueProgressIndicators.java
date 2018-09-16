package rx.internal.util.unsafe;

public abstract interface QueueProgressIndicators
{
  public abstract long currentConsumerIndex();

  public abstract long currentProducerIndex();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.QueueProgressIndicators
 * JD-Core Version:    0.6.0
 */