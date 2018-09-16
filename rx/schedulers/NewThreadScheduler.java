package rx.schedulers;

import rx.Scheduler;
import rx.Scheduler.Worker;

@Deprecated
public final class NewThreadScheduler extends Scheduler
{
  private NewThreadScheduler()
  {
    throw new IllegalStateException("No instances!");
  }

  public Scheduler.Worker createWorker()
  {
    return null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.schedulers.NewThreadScheduler
 * JD-Core Version:    0.6.0
 */