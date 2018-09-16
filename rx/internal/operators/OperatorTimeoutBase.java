package rx.internal.operators;

import java.util.concurrent.TimeoutException;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.internal.producers.ProducerArbiter;
import rx.observers.SerializedSubscriber;
import rx.subscriptions.SerialSubscription;

class OperatorTimeoutBase<T>
  implements Observable.Operator<T, T>
{
  final FirstTimeoutStub<T> firstTimeoutStub;
  final Observable<? extends T> other;
  final Scheduler scheduler;
  final TimeoutStub<T> timeoutStub;

  OperatorTimeoutBase(FirstTimeoutStub<T> paramFirstTimeoutStub, TimeoutStub<T> paramTimeoutStub, Observable<? extends T> paramObservable, Scheduler paramScheduler)
  {
    this.firstTimeoutStub = paramFirstTimeoutStub;
    this.timeoutStub = paramTimeoutStub;
    this.other = paramObservable;
    this.scheduler = paramScheduler;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    Scheduler.Worker localWorker = this.scheduler.createWorker();
    paramSubscriber.add(localWorker);
    SerializedSubscriber localSerializedSubscriber = new SerializedSubscriber(paramSubscriber);
    SerialSubscription localSerialSubscription = new SerialSubscription();
    localSerializedSubscriber.add(localSerialSubscription);
    TimeoutSubscriber localTimeoutSubscriber = new TimeoutSubscriber(localSerializedSubscriber, this.timeoutStub, localSerialSubscription, this.other, localWorker);
    localSerializedSubscriber.add(localTimeoutSubscriber);
    localSerializedSubscriber.setProducer(localTimeoutSubscriber.arbiter);
    localSerialSubscription.set((Subscription)this.firstTimeoutStub.call(localTimeoutSubscriber, Long.valueOf(0L), localWorker));
    return localTimeoutSubscriber;
  }

  static abstract interface FirstTimeoutStub<T> extends Func3<OperatorTimeoutBase.TimeoutSubscriber<T>, Long, Scheduler.Worker, Subscription>
  {
  }

  static abstract interface TimeoutStub<T> extends Func4<OperatorTimeoutBase.TimeoutSubscriber<T>, Long, T, Scheduler.Worker, Subscription>
  {
  }

  static final class TimeoutSubscriber<T> extends Subscriber<T>
  {
    long actual;
    final ProducerArbiter arbiter;
    final Scheduler.Worker inner;
    final Observable<? extends T> other;
    final SerialSubscription serial;
    final SerializedSubscriber<T> serializedSubscriber;
    boolean terminated;
    final OperatorTimeoutBase.TimeoutStub<T> timeoutStub;

    TimeoutSubscriber(SerializedSubscriber<T> paramSerializedSubscriber, OperatorTimeoutBase.TimeoutStub<T> paramTimeoutStub, SerialSubscription paramSerialSubscription, Observable<? extends T> paramObservable, Scheduler.Worker paramWorker)
    {
      this.serializedSubscriber = paramSerializedSubscriber;
      this.timeoutStub = paramTimeoutStub;
      this.serial = paramSerialSubscription;
      this.other = paramObservable;
      this.inner = paramWorker;
      this.arbiter = new ProducerArbiter();
    }

    public void onCompleted()
    {
      monitorenter;
      try
      {
        boolean bool = this.terminated;
        int i = 0;
        if (!bool)
        {
          this.terminated = true;
          i = 1;
        }
        monitorexit;
        if (i != 0)
        {
          this.serial.unsubscribe();
          this.serializedSubscriber.onCompleted();
        }
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void onError(Throwable paramThrowable)
    {
      monitorenter;
      try
      {
        boolean bool = this.terminated;
        int i = 0;
        if (!bool)
        {
          this.terminated = true;
          i = 1;
        }
        monitorexit;
        if (i != 0)
        {
          this.serial.unsubscribe();
          this.serializedSubscriber.onError(paramThrowable);
        }
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void onNext(T paramT)
    {
      monitorenter;
      try
      {
        long l;
        if (!this.terminated)
        {
          l = 1L + this.actual;
          this.actual = l;
        }
        for (int i = 1; ; i = 0)
        {
          monitorexit;
          if (i != 0)
          {
            this.serializedSubscriber.onNext(paramT);
            this.serial.set((Subscription)this.timeoutStub.call(this, Long.valueOf(l), paramT, this.inner));
          }
          return;
          l = this.actual;
        }
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void onTimeout(long paramLong)
    {
      monitorenter;
      try
      {
        boolean bool1 = paramLong < this.actual;
        int i = 0;
        if (!bool1)
        {
          boolean bool2 = this.terminated;
          i = 0;
          if (!bool2)
          {
            this.terminated = true;
            i = 1;
          }
        }
        monitorexit;
        if (i != 0)
        {
          if (this.other == null)
            this.serializedSubscriber.onError(new TimeoutException());
        }
        else
          return;
      }
      finally
      {
        monitorexit;
      }
      1 local1 = new Subscriber()
      {
        public void onCompleted()
        {
          OperatorTimeoutBase.TimeoutSubscriber.this.serializedSubscriber.onCompleted();
        }

        public void onError(Throwable paramThrowable)
        {
          OperatorTimeoutBase.TimeoutSubscriber.this.serializedSubscriber.onError(paramThrowable);
        }

        public void onNext(T paramT)
        {
          OperatorTimeoutBase.TimeoutSubscriber.this.serializedSubscriber.onNext(paramT);
        }

        public void setProducer(Producer paramProducer)
        {
          OperatorTimeoutBase.TimeoutSubscriber.this.arbiter.setProducer(paramProducer);
        }
      };
      this.other.unsafeSubscribe(local1);
      this.serial.set(local1);
    }

    public void setProducer(Producer paramProducer)
    {
      this.arbiter.setProducer(paramProducer);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorTimeoutBase
 * JD-Core Version:    0.6.0
 */