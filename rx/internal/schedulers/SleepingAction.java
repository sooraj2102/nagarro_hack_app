package rx.internal.schedulers;

import rx.Scheduler.Worker;
import rx.exceptions.Exceptions;
import rx.functions.Action0;

class SleepingAction
  implements Action0
{
  private final long execTime;
  private final Scheduler.Worker innerScheduler;
  private final Action0 underlying;

  public SleepingAction(Action0 paramAction0, Scheduler.Worker paramWorker, long paramLong)
  {
    this.underlying = paramAction0;
    this.innerScheduler = paramWorker;
    this.execTime = paramLong;
  }

  public void call()
  {
    if (this.innerScheduler.isUnsubscribed());
    while (true)
    {
      return;
      long l = this.execTime - this.innerScheduler.now();
      if (l > 0L);
      try
      {
        Thread.sleep(l);
        if (this.innerScheduler.isUnsubscribed())
          continue;
        this.underlying.call();
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        while (true)
        {
          Thread.currentThread().interrupt();
          Exceptions.propagate(localInterruptedException);
        }
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.SleepingAction
 * JD-Core Version:    0.6.0
 */