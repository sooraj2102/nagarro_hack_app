package rx.internal.schedulers;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.MultipleAssignmentSubscription;
import rx.subscriptions.Subscriptions;

public final class ExecutorScheduler extends Scheduler
{
  final Executor executor;

  public ExecutorScheduler(Executor paramExecutor)
  {
    this.executor = paramExecutor;
  }

  public Scheduler.Worker createWorker()
  {
    return new ExecutorSchedulerWorker(this.executor);
  }

  static final class ExecutorSchedulerWorker extends Scheduler.Worker
    implements Runnable
  {
    final Executor executor;
    final ConcurrentLinkedQueue<ScheduledAction> queue;
    final ScheduledExecutorService service;
    final CompositeSubscription tasks;
    final AtomicInteger wip;

    public ExecutorSchedulerWorker(Executor paramExecutor)
    {
      this.executor = paramExecutor;
      this.queue = new ConcurrentLinkedQueue();
      this.wip = new AtomicInteger();
      this.tasks = new CompositeSubscription();
      this.service = GenericScheduledExecutorService.getInstance();
    }

    public boolean isUnsubscribed()
    {
      return this.tasks.isUnsubscribed();
    }

    public void run()
    {
      while (true)
      {
        if (this.tasks.isUnsubscribed())
          this.queue.clear();
        ScheduledAction localScheduledAction;
        do
        {
          return;
          localScheduledAction = (ScheduledAction)this.queue.poll();
        }
        while (localScheduledAction == null);
        if (!localScheduledAction.isUnsubscribed())
        {
          if (this.tasks.isUnsubscribed())
            break;
          localScheduledAction.run();
        }
        else if (this.wip.decrementAndGet() == 0)
        {
          return;
        }
      }
      this.queue.clear();
    }

    public Subscription schedule(Action0 paramAction0)
    {
      Object localObject;
      if (isUnsubscribed())
        localObject = Subscriptions.unsubscribed();
      do
      {
        return localObject;
        localObject = new ScheduledAction(RxJavaHooks.onScheduledAction(paramAction0), this.tasks);
        this.tasks.add((Subscription)localObject);
        this.queue.offer(localObject);
      }
      while (this.wip.getAndIncrement() != 0);
      try
      {
        this.executor.execute(this);
        return localObject;
      }
      catch (RejectedExecutionException localRejectedExecutionException)
      {
        this.tasks.remove((Subscription)localObject);
        this.wip.decrementAndGet();
        RxJavaHooks.onError(localRejectedExecutionException);
      }
      throw localRejectedExecutionException;
    }

    public Subscription schedule(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit)
    {
      if (paramLong <= 0L)
        return schedule(paramAction0);
      if (isUnsubscribed())
        return Subscriptions.unsubscribed();
      Action0 localAction0 = RxJavaHooks.onScheduledAction(paramAction0);
      MultipleAssignmentSubscription localMultipleAssignmentSubscription1 = new MultipleAssignmentSubscription();
      MultipleAssignmentSubscription localMultipleAssignmentSubscription2 = new MultipleAssignmentSubscription();
      localMultipleAssignmentSubscription2.set(localMultipleAssignmentSubscription1);
      this.tasks.add(localMultipleAssignmentSubscription2);
      Subscription localSubscription = Subscriptions.create(new Action0(localMultipleAssignmentSubscription2)
      {
        public void call()
        {
          ExecutorScheduler.ExecutorSchedulerWorker.this.tasks.remove(this.val$mas);
        }
      });
      ScheduledAction localScheduledAction = new ScheduledAction(new Action0(localMultipleAssignmentSubscription2, localAction0, localSubscription)
      {
        public void call()
        {
          if (this.val$mas.isUnsubscribed());
          Subscription localSubscription;
          do
          {
            return;
            localSubscription = ExecutorScheduler.ExecutorSchedulerWorker.this.schedule(this.val$decorated);
            this.val$mas.set(localSubscription);
          }
          while (localSubscription.getClass() != ScheduledAction.class);
          ((ScheduledAction)localSubscription).add(this.val$removeMas);
        }
      });
      localMultipleAssignmentSubscription1.set(localScheduledAction);
      try
      {
        localScheduledAction.add(this.service.schedule(localScheduledAction, paramLong, paramTimeUnit));
        return localSubscription;
      }
      catch (RejectedExecutionException localRejectedExecutionException)
      {
        RxJavaHooks.onError(localRejectedExecutionException);
      }
      throw localRejectedExecutionException;
    }

    public void unsubscribe()
    {
      this.tasks.unsubscribe();
      this.queue.clear();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.ExecutorScheduler
 * JD-Core Version:    0.6.0
 */