package rx.internal.schedulers;

public abstract interface SchedulerLifecycle
{
  public abstract void shutdown();

  public abstract void start();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.SchedulerLifecycle
 * JD-Core Version:    0.6.0
 */