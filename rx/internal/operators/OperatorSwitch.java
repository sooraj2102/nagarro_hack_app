package rx.internal.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.functions.Action0;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.atomic.SpscLinkedArrayQueue;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public final class OperatorSwitch<T>
  implements Observable.Operator<T, Observable<? extends T>>
{
  final boolean delayError;

  OperatorSwitch(boolean paramBoolean)
  {
    this.delayError = paramBoolean;
  }

  public static <T> OperatorSwitch<T> instance(boolean paramBoolean)
  {
    if (paramBoolean)
      return HolderDelayError.INSTANCE;
    return Holder.INSTANCE;
  }

  public Subscriber<? super Observable<? extends T>> call(Subscriber<? super T> paramSubscriber)
  {
    SwitchSubscriber localSwitchSubscriber = new SwitchSubscriber(paramSubscriber, this.delayError);
    paramSubscriber.add(localSwitchSubscriber);
    localSwitchSubscriber.init();
    return localSwitchSubscriber;
  }

  static final class Holder
  {
    static final OperatorSwitch<Object> INSTANCE = new OperatorSwitch(false);
  }

  static final class HolderDelayError
  {
    static final OperatorSwitch<Object> INSTANCE = new OperatorSwitch(true);
  }

  static final class InnerSubscriber<T> extends Subscriber<T>
  {
    private final long id;
    private final OperatorSwitch.SwitchSubscriber<T> parent;

    InnerSubscriber(long paramLong, OperatorSwitch.SwitchSubscriber<T> paramSwitchSubscriber)
    {
      this.id = paramLong;
      this.parent = paramSwitchSubscriber;
    }

    public void onCompleted()
    {
      this.parent.complete(this.id);
    }

    public void onError(Throwable paramThrowable)
    {
      this.parent.error(paramThrowable, this.id);
    }

    public void onNext(T paramT)
    {
      this.parent.emit(paramT, this);
    }

    public void setProducer(Producer paramProducer)
    {
      this.parent.innerProducer(paramProducer, this.id);
    }
  }

  static final class SwitchSubscriber<T> extends Subscriber<Observable<? extends T>>
  {
    static final Throwable TERMINAL_ERROR = new Throwable("Terminal error");
    final Subscriber<? super T> child;
    final boolean delayError;
    boolean emitting;
    Throwable error;
    final AtomicLong index;
    boolean innerActive;
    volatile boolean mainDone;
    boolean missed;
    final NotificationLite<T> nl;
    Producer producer;
    final SpscLinkedArrayQueue<Object> queue;
    long requested;
    final SerialSubscription serial;

    SwitchSubscriber(Subscriber<? super T> paramSubscriber, boolean paramBoolean)
    {
      this.child = paramSubscriber;
      this.serial = new SerialSubscription();
      this.delayError = paramBoolean;
      this.index = new AtomicLong();
      this.queue = new SpscLinkedArrayQueue(RxRingBuffer.SIZE);
      this.nl = NotificationLite.instance();
    }

    protected boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2, Throwable paramThrowable, SpscLinkedArrayQueue<Object> paramSpscLinkedArrayQueue, Subscriber<? super T> paramSubscriber, boolean paramBoolean3)
    {
      if (this.delayError)
      {
        if ((paramBoolean1) && (!paramBoolean2) && (paramBoolean3))
        {
          if (paramThrowable != null)
          {
            paramSubscriber.onError(paramThrowable);
            return true;
          }
          paramSubscriber.onCompleted();
          return true;
        }
      }
      else
      {
        if (paramThrowable != null)
        {
          paramSpscLinkedArrayQueue.clear();
          paramSubscriber.onError(paramThrowable);
          return true;
        }
        if ((paramBoolean1) && (!paramBoolean2) && (paramBoolean3))
        {
          paramSubscriber.onCompleted();
          return true;
        }
      }
      return false;
    }

    void childRequested(long paramLong)
    {
      monitorenter;
      try
      {
        Producer localProducer = this.producer;
        this.requested = BackpressureUtils.addCap(this.requested, paramLong);
        monitorexit;
        if (localProducer != null)
          localProducer.request(paramLong);
        drain();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    void clearProducer()
    {
      monitorenter;
      try
      {
        this.producer = null;
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    void complete(long paramLong)
    {
      monitorenter;
      try
      {
        if (this.index.get() != paramLong)
          return;
        this.innerActive = false;
        this.producer = null;
        monitorexit;
        drain();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    void drain()
    {
      monitorenter;
      while (true)
      {
        SpscLinkedArrayQueue localSpscLinkedArrayQueue;
        AtomicLong localAtomicLong;
        Subscriber localSubscriber;
        long l2;
        try
        {
          if (!this.emitting)
            continue;
          this.missed = true;
          return;
          this.emitting = true;
          bool1 = this.innerActive;
          long l1 = this.requested;
          localThrowable = this.error;
          if ((localThrowable == null) || (localThrowable == TERMINAL_ERROR) || (this.delayError))
            continue;
          this.error = TERMINAL_ERROR;
          monitorexit;
          localSpscLinkedArrayQueue = this.queue;
          localAtomicLong = this.index;
          localSubscriber = this.child;
          bool2 = this.mainDone;
          l2 = 0L;
          if (l2 == l1)
            continue;
          if (localSubscriber.isUnsubscribed())
            break;
          boolean bool5 = localSpscLinkedArrayQueue.isEmpty();
          if (checkTerminated(bool2, bool1, localThrowable, localSpscLinkedArrayQueue, localSubscriber, bool5))
            break;
          if (bool5)
          {
            if (l2 != l1)
              continue;
            if (localSubscriber.isUnsubscribed())
              break;
            boolean bool3 = this.mainDone;
            boolean bool4 = localSpscLinkedArrayQueue.isEmpty();
            if (checkTerminated(bool3, bool1, localThrowable, localSpscLinkedArrayQueue, localSubscriber, bool4))
              break;
            monitorenter;
            try
            {
              l1 = this.requested;
              if (l1 == 9223372036854775807L)
                continue;
              l1 -= l2;
              this.requested = l1;
              if (this.missed)
                break label291;
              this.emitting = false;
              return;
            }
            finally
            {
              monitorexit;
            }
          }
        }
        finally
        {
          monitorexit;
        }
        OperatorSwitch.InnerSubscriber localInnerSubscriber = (OperatorSwitch.InnerSubscriber)localSpscLinkedArrayQueue.poll();
        Object localObject3 = this.nl.getValue(localSpscLinkedArrayQueue.poll());
        if (localAtomicLong.get() != localInnerSubscriber.id)
          continue;
        localSubscriber.onNext(localObject3);
        l2 += 1L;
        continue;
        label291: this.missed = false;
        boolean bool2 = this.mainDone;
        boolean bool1 = this.innerActive;
        Throwable localThrowable = this.error;
        if ((localThrowable != null) && (localThrowable != TERMINAL_ERROR) && (!this.delayError))
          this.error = TERMINAL_ERROR;
        monitorexit;
      }
    }

    void emit(T paramT, OperatorSwitch.InnerSubscriber<T> paramInnerSubscriber)
    {
      monitorenter;
      try
      {
        if (this.index.get() != paramInnerSubscriber.id)
          return;
        this.queue.offer(paramInnerSubscriber, this.nl.next(paramT));
        monitorexit;
        drain();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    void error(Throwable paramThrowable, long paramLong)
    {
      monitorenter;
      try
      {
        boolean bool;
        if (this.index.get() == paramLong)
        {
          bool = updateError(paramThrowable);
          this.innerActive = false;
          this.producer = null;
        }
        while (true)
        {
          monitorexit;
          if (!bool)
            break;
          drain();
          return;
          bool = true;
        }
      }
      finally
      {
        monitorexit;
      }
      pluginError(paramThrowable);
    }

    void init()
    {
      this.child.add(this.serial);
      this.child.add(Subscriptions.create(new Action0()
      {
        public void call()
        {
          OperatorSwitch.SwitchSubscriber.this.clearProducer();
        }
      }));
      this.child.setProducer(new Producer()
      {
        public void request(long paramLong)
        {
          if (paramLong > 0L)
            OperatorSwitch.SwitchSubscriber.this.childRequested(paramLong);
          do
            return;
          while (paramLong >= 0L);
          throw new IllegalArgumentException("n >= 0 expected but it was " + paramLong);
        }
      });
    }

    void innerProducer(Producer paramProducer, long paramLong)
    {
      monitorenter;
      try
      {
        if (this.index.get() != paramLong)
          return;
        long l = this.requested;
        this.producer = paramProducer;
        monitorexit;
        paramProducer.request(l);
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void onCompleted()
    {
      this.mainDone = true;
      drain();
    }

    public void onError(Throwable paramThrowable)
    {
      monitorenter;
      try
      {
        boolean bool = updateError(paramThrowable);
        monitorexit;
        if (bool)
        {
          this.mainDone = true;
          drain();
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      pluginError(paramThrowable);
    }

    public void onNext(Observable<? extends T> paramObservable)
    {
      long l = this.index.incrementAndGet();
      Subscription localSubscription = this.serial.get();
      if (localSubscription != null)
        localSubscription.unsubscribe();
      monitorenter;
      try
      {
        OperatorSwitch.InnerSubscriber localInnerSubscriber = new OperatorSwitch.InnerSubscriber(l, this);
        this.innerActive = true;
        this.producer = null;
        monitorexit;
        this.serial.set(localInnerSubscriber);
        paramObservable.unsafeSubscribe(localInnerSubscriber);
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    void pluginError(Throwable paramThrowable)
    {
      RxJavaHooks.onError(paramThrowable);
    }

    boolean updateError(Throwable paramThrowable)
    {
      Throwable localThrowable = this.error;
      if (localThrowable == TERMINAL_ERROR)
        return false;
      if (localThrowable == null)
        this.error = paramThrowable;
      while (true)
      {
        return true;
        if ((localThrowable instanceof CompositeException))
        {
          ArrayList localArrayList = new ArrayList(((CompositeException)localThrowable).getExceptions());
          localArrayList.add(paramThrowable);
          this.error = new CompositeException(localArrayList);
          continue;
        }
        this.error = new CompositeException(new Throwable[] { localThrowable, paramThrowable });
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorSwitch
 * JD-Core Version:    0.6.0
 */