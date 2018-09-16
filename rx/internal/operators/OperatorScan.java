package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.Operator;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.internal.util.atomic.SpscLinkedAtomicQueue;
import rx.internal.util.unsafe.SpscLinkedQueue;
import rx.internal.util.unsafe.UnsafeAccess;

public final class OperatorScan<R, T>
  implements Observable.Operator<R, T>
{
  private static final Object NO_INITIAL_VALUE = new Object();
  final Func2<R, ? super T, R> accumulator;
  private final Func0<R> initialValueFactory;

  public OperatorScan(R paramR, Func2<R, ? super T, R> paramFunc2)
  {
    this(new Func0()
    {
      public R call()
      {
        return OperatorScan.this;
      }
    }
    , paramFunc2);
  }

  public OperatorScan(Func0<R> paramFunc0, Func2<R, ? super T, R> paramFunc2)
  {
    this.initialValueFactory = paramFunc0;
    this.accumulator = paramFunc2;
  }

  public OperatorScan(Func2<R, ? super T, R> paramFunc2)
  {
    this(NO_INITIAL_VALUE, paramFunc2);
  }

  public Subscriber<? super T> call(Subscriber<? super R> paramSubscriber)
  {
    Object localObject = this.initialValueFactory.call();
    if (localObject == NO_INITIAL_VALUE)
      return new Subscriber(paramSubscriber, paramSubscriber)
      {
        boolean once;
        R value;

        public void onCompleted()
        {
          this.val$child.onCompleted();
        }

        public void onError(Throwable paramThrowable)
        {
          this.val$child.onError(paramThrowable);
        }

        public void onNext(T paramT)
        {
          Object localObject3;
          if (!this.once)
          {
            this.once = true;
            localObject3 = paramT;
          }
          while (true)
          {
            this.value = localObject3;
            this.val$child.onNext(localObject3);
            return;
            Object localObject1 = this.value;
            try
            {
              Object localObject2 = OperatorScan.this.accumulator.call(localObject1, paramT);
              localObject3 = localObject2;
            }
            catch (Throwable localThrowable)
            {
              Exceptions.throwOrReport(localThrowable, this.val$child, paramT);
            }
          }
        }
      };
    InitialProducer localInitialProducer = new InitialProducer(localObject, paramSubscriber);
    3 local3 = new Subscriber(localObject, localInitialProducer)
    {
      private R value = this.val$initialValue;

      public void onCompleted()
      {
        this.val$ip.onCompleted();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$ip.onError(paramThrowable);
      }

      public void onNext(T paramT)
      {
        Object localObject1 = this.value;
        try
        {
          Object localObject2 = OperatorScan.this.accumulator.call(localObject1, paramT);
          this.value = localObject2;
          this.val$ip.onNext(localObject2);
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwOrReport(localThrowable, this, paramT);
        }
      }

      public void setProducer(Producer paramProducer)
      {
        this.val$ip.setProducer(paramProducer);
      }
    };
    paramSubscriber.add(local3);
    paramSubscriber.setProducer(localInitialProducer);
    return local3;
  }

  static final class InitialProducer<R>
    implements Producer, Observer<R>
  {
    final Subscriber<? super R> child;
    volatile boolean done;
    boolean emitting;
    Throwable error;
    boolean missed;
    long missedRequested;
    volatile Producer producer;
    final Queue<Object> queue;
    final AtomicLong requested;

    public InitialProducer(R paramR, Subscriber<? super R> paramSubscriber)
    {
      this.child = paramSubscriber;
      if (UnsafeAccess.isUnsafeAvailable());
      for (Object localObject = new SpscLinkedQueue(); ; localObject = new SpscLinkedAtomicQueue())
      {
        this.queue = ((Queue)localObject);
        ((Queue)localObject).offer(NotificationLite.instance().next(paramR));
        this.requested = new AtomicLong();
        return;
      }
    }

    boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2, Subscriber<? super R> paramSubscriber)
    {
      if (paramSubscriber.isUnsubscribed())
        return true;
      if (paramBoolean1)
      {
        Throwable localThrowable = this.error;
        if (localThrowable != null)
        {
          paramSubscriber.onError(localThrowable);
          return true;
        }
        if (paramBoolean2)
        {
          paramSubscriber.onCompleted();
          return true;
        }
      }
      return false;
    }

    void emit()
    {
      monitorenter;
      try
      {
        if (this.emitting)
        {
          this.missed = true;
          return;
        }
        this.emitting = true;
        monitorexit;
        emitLoop();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    void emitLoop()
    {
      Subscriber localSubscriber = this.child;
      Queue localQueue = this.queue;
      NotificationLite localNotificationLite = NotificationLite.instance();
      AtomicLong localAtomicLong = this.requested;
      long l1 = localAtomicLong.get();
      while (true)
      {
        if (checkTerminated(this.done, localQueue.isEmpty(), localSubscriber))
          return;
        long l2 = 0L;
        while (true)
        {
          boolean bool1;
          Object localObject2;
          boolean bool2;
          if (l2 != l1)
          {
            bool1 = this.done;
            localObject2 = localQueue.poll();
            if (localObject2 != null)
              break label145;
            bool2 = true;
          }
          while (true)
            if (!checkTerminated(bool1, bool2, localSubscriber))
            {
              if (bool2)
              {
                if ((l2 != 0L) && (l1 != 9223372036854775807L))
                  l1 = BackpressureUtils.produced(localAtomicLong, l2);
                monitorenter;
                try
                {
                  if (this.missed)
                    break label185;
                  this.emitting = false;
                  return;
                }
                finally
                {
                  monitorexit;
                }
                label145: bool2 = false;
                continue;
              }
            }
            else
              break;
          Object localObject3 = localNotificationLite.getValue(localObject2);
          try
          {
            localSubscriber.onNext(localObject3);
            l2 += 1L;
          }
          catch (Throwable localThrowable)
          {
            Exceptions.throwOrReport(localThrowable, localSubscriber, localObject3);
            return;
          }
        }
        label185: this.missed = false;
        monitorexit;
      }
    }

    public void onCompleted()
    {
      this.done = true;
      emit();
    }

    public void onError(Throwable paramThrowable)
    {
      this.error = paramThrowable;
      this.done = true;
      emit();
    }

    public void onNext(R paramR)
    {
      this.queue.offer(NotificationLite.instance().next(paramR));
      emit();
    }

    public void request(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= required but it was " + paramLong);
      Producer localProducer;
      if (paramLong != 0L)
      {
        BackpressureUtils.getAndAddRequest(this.requested, paramLong);
        localProducer = this.producer;
        if (localProducer != null);
      }
      synchronized (this.requested)
      {
        localProducer = this.producer;
        if (localProducer == null)
          this.missedRequested = BackpressureUtils.addCap(this.missedRequested, paramLong);
        if (localProducer != null)
          localProducer.request(paramLong);
        emit();
        return;
      }
    }

    public void setProducer(Producer paramProducer)
    {
      if (paramProducer == null)
        throw new NullPointerException();
      synchronized (this.requested)
      {
        if (this.producer != null)
          throw new IllegalStateException("Can't set more than one Producer!");
      }
      long l = this.missedRequested;
      if (l != 9223372036854775807L)
        l -= 1L;
      this.missedRequested = 0L;
      this.producer = paramProducer;
      monitorexit;
      if (l > 0L)
        paramProducer.request(l);
      emit();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorScan
 * JD-Core Version:    0.6.0
 */