package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import rx.Notification;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.plugins.RxJavaHooks;

public final class OperatorMaterialize<T>
  implements Observable.Operator<Notification<T>, T>
{
  public static <T> OperatorMaterialize<T> instance()
  {
    return Holder.INSTANCE;
  }

  public Subscriber<? super T> call(Subscriber<? super Notification<T>> paramSubscriber)
  {
    ParentSubscriber localParentSubscriber = new ParentSubscriber(paramSubscriber);
    paramSubscriber.add(localParentSubscriber);
    paramSubscriber.setProducer(new Producer(localParentSubscriber)
    {
      public void request(long paramLong)
      {
        if (paramLong > 0L)
          this.val$parent.requestMore(paramLong);
      }
    });
    return localParentSubscriber;
  }

  static final class Holder
  {
    static final OperatorMaterialize<Object> INSTANCE = new OperatorMaterialize();
  }

  static class ParentSubscriber<T> extends Subscriber<T>
  {
    private boolean busy;
    private final Subscriber<? super Notification<T>> child;
    private boolean missed;
    private final AtomicLong requested = new AtomicLong();
    private volatile Notification<T> terminalNotification;

    ParentSubscriber(Subscriber<? super Notification<T>> paramSubscriber)
    {
      this.child = paramSubscriber;
    }

    private void decrementRequested()
    {
      AtomicLong localAtomicLong = this.requested;
      long l;
      do
      {
        l = localAtomicLong.get();
        if (l == 9223372036854775807L)
          return;
      }
      while (!localAtomicLong.compareAndSet(l, l - 1L));
    }

    private void drain()
    {
      monitorenter;
      while (true)
      {
        try
        {
          if (!this.busy)
            continue;
          this.missed = true;
          return;
          monitorexit;
          AtomicLong localAtomicLong = this.requested;
          if (this.child.isUnsubscribed())
            break;
          Notification localNotification = this.terminalNotification;
          if ((localNotification != null) && (localAtomicLong.get() > 0L))
          {
            this.terminalNotification = null;
            this.child.onNext(localNotification);
            if (this.child.isUnsubscribed())
              break;
            this.child.onCompleted();
            return;
          }
        }
        finally
        {
          monitorexit;
        }
        monitorenter;
        try
        {
          if (!this.missed)
          {
            this.busy = false;
            return;
          }
        }
        finally
        {
          monitorexit;
        }
        monitorexit;
      }
    }

    public void onCompleted()
    {
      this.terminalNotification = Notification.createOnCompleted();
      drain();
    }

    public void onError(Throwable paramThrowable)
    {
      this.terminalNotification = Notification.createOnError(paramThrowable);
      RxJavaHooks.onError(paramThrowable);
      drain();
    }

    public void onNext(T paramT)
    {
      this.child.onNext(Notification.createOnNext(paramT));
      decrementRequested();
    }

    public void onStart()
    {
      request(0L);
    }

    void requestMore(long paramLong)
    {
      BackpressureUtils.getAndAddRequest(this.requested, paramLong);
      request(paramLong);
      drain();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorMaterialize
 * JD-Core Version:    0.6.0
 */