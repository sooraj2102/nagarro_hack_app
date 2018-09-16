package rx.subjects;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Action0;
import rx.internal.operators.BackpressureUtils;
import rx.internal.operators.NotificationLite;
import rx.internal.util.atomic.SpscLinkedAtomicQueue;
import rx.internal.util.atomic.SpscUnboundedAtomicArrayQueue;
import rx.internal.util.unsafe.SpscLinkedQueue;
import rx.internal.util.unsafe.SpscUnboundedArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;

@Experimental
public final class UnicastSubject<T> extends Subject<T, T>
{
  final State<T> state;

  private UnicastSubject(State<T> paramState)
  {
    super(paramState);
    this.state = paramState;
  }

  public static <T> UnicastSubject<T> create()
  {
    return create(16);
  }

  public static <T> UnicastSubject<T> create(int paramInt)
  {
    return new UnicastSubject(new State(paramInt, null));
  }

  public static <T> UnicastSubject<T> create(int paramInt, Action0 paramAction0)
  {
    return new UnicastSubject(new State(paramInt, paramAction0));
  }

  public boolean hasObservers()
  {
    return this.state.subscriber.get() != null;
  }

  public void onCompleted()
  {
    this.state.onCompleted();
  }

  public void onError(Throwable paramThrowable)
  {
    this.state.onError(paramThrowable);
  }

  public void onNext(T paramT)
  {
    this.state.onNext(paramT);
  }

  static final class State<T> extends AtomicLong
    implements Producer, Observer<T>, Observable.OnSubscribe<T>, Subscription
  {
    private static final long serialVersionUID = -9044104859202255786L;
    volatile boolean caughtUp;
    volatile boolean done;
    boolean emitting;
    Throwable error;
    boolean missed;
    final NotificationLite<T> nl = NotificationLite.instance();
    final Queue<Object> queue;
    final AtomicReference<Subscriber<? super T>> subscriber = new AtomicReference();
    final AtomicReference<Action0> terminateOnce;

    public State(int paramInt, Action0 paramAction0)
    {
      AtomicReference localAtomicReference;
      if (paramAction0 != null)
      {
        localAtomicReference = new AtomicReference(paramAction0);
        this.terminateOnce = localAtomicReference;
        if (paramInt <= 1)
          break label86;
        if (!UnsafeAccess.isUnsafeAvailable())
          break label73;
      }
      label73: for (Object localObject = new SpscUnboundedArrayQueue(paramInt); ; localObject = new SpscUnboundedAtomicArrayQueue(paramInt))
      {
        this.queue = ((Queue)localObject);
        return;
        localAtomicReference = null;
        break;
      }
      label86: if (UnsafeAccess.isUnsafeAvailable());
      for (localObject = new SpscLinkedQueue(); ; localObject = new SpscLinkedAtomicQueue())
        break;
    }

    public void call(Subscriber<? super T> paramSubscriber)
    {
      if (this.subscriber.compareAndSet(null, paramSubscriber))
      {
        paramSubscriber.add(this);
        paramSubscriber.setProducer(this);
        return;
      }
      paramSubscriber.onError(new IllegalStateException("Only a single subscriber is allowed"));
    }

    boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2, Subscriber<? super T> paramSubscriber)
    {
      if (paramSubscriber.isUnsubscribed())
      {
        this.queue.clear();
        return true;
      }
      if (paramBoolean1)
      {
        Throwable localThrowable = this.error;
        if (localThrowable != null)
        {
          this.queue.clear();
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

    void doTerminate()
    {
      AtomicReference localAtomicReference = this.terminateOnce;
      if (localAtomicReference != null)
      {
        Action0 localAction0 = (Action0)localAtomicReference.get();
        if ((localAction0 != null) && (localAtomicReference.compareAndSet(localAction0, null)))
          localAction0.call();
      }
    }

    public boolean isUnsubscribed()
    {
      return this.done;
    }

    public void onCompleted()
    {
      boolean bool = true;
      if (!this.done)
      {
        doTerminate();
        this.done = bool;
        if (!this.caughtUp)
          monitorenter;
      }
      else
      {
        try
        {
          if (!this.caughtUp);
          while (true)
          {
            monitorexit;
            if (!bool)
              break;
            replay();
            return;
            bool = false;
          }
        }
        finally
        {
          monitorexit;
        }
      }
      ((Subscriber)this.subscriber.get()).onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      boolean bool = true;
      if (!this.done)
      {
        doTerminate();
        this.error = paramThrowable;
        this.done = bool;
        if (!this.caughtUp)
          monitorenter;
      }
      else
      {
        try
        {
          if (!this.caughtUp);
          while (true)
          {
            monitorexit;
            if (!bool)
              break;
            replay();
            return;
            bool = false;
          }
        }
        finally
        {
          monitorexit;
        }
      }
      ((Subscriber)this.subscriber.get()).onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      if (!this.done)
      {
        if (!this.caughtUp)
          monitorenter;
      }
      else
        try
        {
          boolean bool = this.caughtUp;
          int i = 0;
          if (!bool)
          {
            this.queue.offer(this.nl.next(paramT));
            i = 1;
          }
          monitorexit;
          if (i != 0)
          {
            replay();
            return;
          }
        }
        finally
        {
          monitorexit;
        }
      Subscriber localSubscriber = (Subscriber)this.subscriber.get();
      try
      {
        localSubscriber.onNext(paramT);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwOrReport(localThrowable, localSubscriber, paramT);
      }
    }

    void replay()
    {
      monitorenter;
      while (true)
      {
        Queue localQueue;
        Subscriber localSubscriber;
        long l1;
        long l2;
        Object localObject3;
        try
        {
          if (!this.emitting)
            continue;
          this.missed = true;
          return;
          this.emitting = true;
          monitorexit;
          localQueue = this.queue;
          localSubscriber = (Subscriber)this.subscriber.get();
          i = 0;
          if (localSubscriber == null)
            continue;
          if (checkTerminated(this.done, localQueue.isEmpty(), localSubscriber))
            break;
          l1 = get();
          if (l1 == 9223372036854775807L)
          {
            i = 1;
            l2 = 0L;
            if (l1 == 0L)
              continue;
            boolean bool1 = this.done;
            localObject3 = localQueue.poll();
            if (localObject3 != null)
              break label206;
            bool2 = true;
            if (checkTerminated(bool1, bool2, localSubscriber))
              break;
            if (!bool2)
              break label212;
            if ((i != 0) || (l2 == 0L))
              continue;
            addAndGet(-l2);
            monitorenter;
            try
            {
              if (this.missed)
                break label269;
              if ((i == 0) || (!localQueue.isEmpty()))
                continue;
              this.caughtUp = true;
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
        int i = 0;
        continue;
        label206: boolean bool2 = false;
        continue;
        label212: Object localObject4 = this.nl.getValue(localObject3);
        try
        {
          localSubscriber.onNext(localObject4);
          l1 -= 1L;
          l2 += 1L;
        }
        catch (Throwable localThrowable)
        {
          localQueue.clear();
          Exceptions.throwIfFatal(localThrowable);
          localSubscriber.onError(OnErrorThrowable.addValueAsLastCause(localThrowable, localObject4));
          return;
        }
        label269: this.missed = false;
        monitorexit;
      }
    }

    public void request(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= 0 required");
      if (paramLong > 0L)
      {
        BackpressureUtils.getAndAddRequest(this, paramLong);
        replay();
      }
      do
        return;
      while (!this.done);
      replay();
    }

    public void unsubscribe()
    {
      doTerminate();
      this.done = true;
      monitorenter;
      try
      {
        if (this.emitting)
          return;
        this.emitting = true;
        monitorexit;
        this.queue.clear();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.subjects.UnicastSubject
 * JD-Core Version:    0.6.0
 */