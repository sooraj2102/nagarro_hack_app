package rx.internal.util.unsafe;

public abstract interface MessagePassingQueue<M>
{
  public abstract boolean isEmpty();

  public abstract boolean offer(M paramM);

  public abstract M peek();

  public abstract M poll();

  public abstract int size();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.MessagePassingQueue
 * JD-Core Version:    0.6.0
 */