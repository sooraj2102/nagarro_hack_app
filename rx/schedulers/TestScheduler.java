package rx.schedulers;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.Subscriptions;

public class TestScheduler extends Scheduler
{
  static long counter;
  final Queue<TimedAction> queue = new PriorityQueue(11, new CompareActionsByTime());
  long time;

  private void triggerActions(long paramLong)
  {
    TimedAction localTimedAction;
    if (!this.queue.isEmpty())
    {
      localTimedAction = (TimedAction)this.queue.peek();
      if (localTimedAction.time <= paramLong);
    }
    else
    {
      this.time = paramLong;
      return;
    }
    long l;
    if (localTimedAction.time == 0L)
      l = this.time;
    while (true)
    {
      this.time = l;
      this.queue.remove();
      if (localTimedAction.scheduler.isUnsubscribed())
        break;
      localTimedAction.action.call();
      break;
      l = localTimedAction.time;
    }
  }

  public void advanceTimeBy(long paramLong, TimeUnit paramTimeUnit)
  {
    advanceTimeTo(this.time + paramTimeUnit.toNanos(paramLong), TimeUnit.NANOSECONDS);
  }

  public void advanceTimeTo(long paramLong, TimeUnit paramTimeUnit)
  {
    triggerActions(paramTimeUnit.toNanos(paramLong));
  }

  public Scheduler.Worker createWorker()
  {
    return new InnerTestScheduler();
  }

  public long now()
  {
    return TimeUnit.NANOSECONDS.toMillis(this.time);
  }

  public void triggerActions()
  {
    triggerActions(this.time);
  }

  static final class CompareActionsByTime
    implements Comparator<TestScheduler.TimedAction>
  {
    public int compare(TestScheduler.TimedAction paramTimedAction1, TestScheduler.TimedAction paramTimedAction2)
    {
      if (paramTimedAction1.time == paramTimedAction2.time)
        if (TestScheduler.TimedAction.access$000(paramTimedAction1) >= TestScheduler.TimedAction.access$000(paramTimedAction2));
      do
      {
        return -1;
        if (TestScheduler.TimedAction.access$000(paramTimedAction1) > TestScheduler.TimedAction.access$000(paramTimedAction2))
          return 1;
        return 0;
      }
      while (paramTimedAction1.time < paramTimedAction2.time);
      if (paramTimedAction1.time > paramTimedAction2.time)
        return 1;
      return 0;
    }
  }

  final class InnerTestScheduler extends Scheduler.Worker
  {
    private final BooleanSubscription s = new BooleanSubscription();

    InnerTestScheduler()
    {
    }

    public boolean isUnsubscribed()
    {
      return this.s.isUnsubscribed();
    }

    public long now()
    {
      return TestScheduler.this.now();
    }

    public Subscription schedule(Action0 paramAction0)
    {
      TestScheduler.TimedAction localTimedAction = new TestScheduler.TimedAction(this, 0L, paramAction0);
      TestScheduler.this.queue.add(localTimedAction);
      return Subscriptions.create(new Action0(localTimedAction)
      {
        public void call()
        {
          TestScheduler.this.queue.remove(this.val$timedAction);
        }
      });
    }

    public Subscription schedule(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit)
    {
      TestScheduler.TimedAction localTimedAction = new TestScheduler.TimedAction(this, TestScheduler.this.time + paramTimeUnit.toNanos(paramLong), paramAction0);
      TestScheduler.this.queue.add(localTimedAction);
      return Subscriptions.create(new Action0(localTimedAction)
      {
        public void call()
        {
          TestScheduler.this.queue.remove(this.val$timedAction);
        }
      });
    }

    public void unsubscribe()
    {
      this.s.unsubscribe();
    }
  }

  static final class TimedAction
  {
    final Action0 action;
    private final long count;
    final Scheduler.Worker scheduler;
    final long time;

    TimedAction(Scheduler.Worker paramWorker, long paramLong, Action0 paramAction0)
    {
      long l = TestScheduler.counter;
      TestScheduler.counter = 1L + l;
      this.count = l;
      this.time = paramLong;
      this.action = paramAction0;
      this.scheduler = paramWorker;
    }

    public String toString()
    {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Long.valueOf(this.time);
      arrayOfObject[1] = this.action.toString();
      return String.format("TimedAction(time = %d, action = %s)", arrayOfObject);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.schedulers.TestScheduler
 * JD-Core Version:    0.6.0
 */