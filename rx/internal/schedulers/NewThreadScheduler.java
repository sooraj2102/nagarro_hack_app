package rx.internal.schedulers;

import java.util.concurrent.ThreadFactory;
import rx.Scheduler;
import rx.Scheduler.Worker;

public final class NewThreadScheduler extends Scheduler
{
  private final ThreadFactory threadFactory;

  public NewThreadScheduler(ThreadFactory paramThreadFactory)
  {
    this.threadFactory = paramThreadFactory;
  }

  public Scheduler.Worker createWorker()
  {
    return new NewThreadWorker(this.threadFactory);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.NewThreadScheduler
 * JD-Core Version:    0.6.0
 */