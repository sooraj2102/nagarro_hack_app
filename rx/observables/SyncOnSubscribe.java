package rx.observables;

import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.annotations.Beta;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.internal.operators.BackpressureUtils;
import rx.plugins.RxJavaHooks;

public abstract class SyncOnSubscribe<S, T>
  implements Observable.OnSubscribe<T>
{
  @Beta
  public static <S, T> SyncOnSubscribe<S, T> createSingleState(Func0<? extends S> paramFunc0, Action2<? super S, ? super Observer<? super T>> paramAction2)
  {
    return new SyncOnSubscribeImpl(paramFunc0, new Func2(paramAction2)
    {
      public S call(S paramS, Observer<? super T> paramObserver)
      {
        this.val$next.call(paramS, paramObserver);
        return paramS;
      }
    });
  }

  @Beta
  public static <S, T> SyncOnSubscribe<S, T> createSingleState(Func0<? extends S> paramFunc0, Action2<? super S, ? super Observer<? super T>> paramAction2, Action1<? super S> paramAction1)
  {
    return new SyncOnSubscribeImpl(paramFunc0, new Func2(paramAction2)
    {
      public S call(S paramS, Observer<? super T> paramObserver)
      {
        this.val$next.call(paramS, paramObserver);
        return paramS;
      }
    }
    , paramAction1);
  }

  @Beta
  public static <S, T> SyncOnSubscribe<S, T> createStateful(Func0<? extends S> paramFunc0, Func2<? super S, ? super Observer<? super T>, ? extends S> paramFunc2)
  {
    return new SyncOnSubscribeImpl(paramFunc0, paramFunc2);
  }

  @Beta
  public static <S, T> SyncOnSubscribe<S, T> createStateful(Func0<? extends S> paramFunc0, Func2<? super S, ? super Observer<? super T>, ? extends S> paramFunc2, Action1<? super S> paramAction1)
  {
    return new SyncOnSubscribeImpl(paramFunc0, paramFunc2, paramAction1);
  }

  @Beta
  public static <T> SyncOnSubscribe<Void, T> createStateless(Action1<? super Observer<? super T>> paramAction1)
  {
    return new SyncOnSubscribeImpl(new Func2(paramAction1)
    {
      public Void call(Void paramVoid, Observer<? super T> paramObserver)
      {
        this.val$next.call(paramObserver);
        return paramVoid;
      }
    });
  }

  @Beta
  public static <T> SyncOnSubscribe<Void, T> createStateless(Action1<? super Observer<? super T>> paramAction1, Action0 paramAction0)
  {
    return new SyncOnSubscribeImpl(new Func2(paramAction1)
    {
      public Void call(Void paramVoid, Observer<? super T> paramObserver)
      {
        this.val$next.call(paramObserver);
        return null;
      }
    }
    , new Action1(paramAction0)
    {
      public void call(Void paramVoid)
      {
        this.val$onUnsubscribe.call();
      }
    });
  }

  public final void call(Subscriber<? super T> paramSubscriber)
  {
    try
    {
      Object localObject = generateState();
      SubscriptionProducer localSubscriptionProducer = new SubscriptionProducer(paramSubscriber, this, localObject);
      paramSubscriber.add(localSubscriptionProducer);
      paramSubscriber.setProducer(localSubscriptionProducer);
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwIfFatal(localThrowable);
      paramSubscriber.onError(localThrowable);
    }
  }

  protected abstract S generateState();

  protected abstract S next(S paramS, Observer<? super T> paramObserver);

  protected void onUnsubscribe(S paramS)
  {
  }

  static final class SubscriptionProducer<S, T> extends AtomicLong
    implements Producer, Subscription, Observer<T>
  {
    private static final long serialVersionUID = -3736864024352728072L;
    private final Subscriber<? super T> actualSubscriber;
    private boolean hasTerminated;
    private boolean onNextCalled;
    private final SyncOnSubscribe<S, T> parent;
    private S state;

    SubscriptionProducer(Subscriber<? super T> paramSubscriber, SyncOnSubscribe<S, T> paramSyncOnSubscribe, S paramS)
    {
      this.actualSubscriber = paramSubscriber;
      this.parent = paramSyncOnSubscribe;
      this.state = paramS;
    }

    private void doUnsubscribe()
    {
      try
      {
        this.parent.onUnsubscribe(this.state);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        RxJavaHooks.onError(localThrowable);
      }
    }

    private void fastPath()
    {
      SyncOnSubscribe localSyncOnSubscribe = this.parent;
      Subscriber localSubscriber = this.actualSubscriber;
      try
      {
        do
        {
          this.onNextCalled = false;
          nextIteration(localSyncOnSubscribe);
        }
        while (!tryUnsubscribe());
        return;
      }
      catch (Throwable localThrowable)
      {
        handleThrownError(localSubscriber, localThrowable);
      }
    }

    private void handleThrownError(Subscriber<? super T> paramSubscriber, Throwable paramThrowable)
    {
      if (this.hasTerminated)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.hasTerminated = true;
      paramSubscriber.onError(paramThrowable);
      unsubscribe();
    }

    private void nextIteration(SyncOnSubscribe<S, T> paramSyncOnSubscribe)
    {
      this.state = paramSyncOnSubscribe.next(this.state, this);
    }

    private void slowPath(long paramLong)
    {
      SyncOnSubscribe localSyncOnSubscribe = this.parent;
      Subscriber localSubscriber = this.actualSubscriber;
      long l1 = paramLong;
      do
      {
        long l2 = l1;
        do
        {
          try
          {
            this.onNextCalled = false;
            nextIteration(localSyncOnSubscribe);
            if (tryUnsubscribe())
              return;
          }
          catch (Throwable localThrowable)
          {
            handleThrownError(localSubscriber, localThrowable);
            return;
          }
          if (!this.onNextCalled)
            continue;
          l2 -= 1L;
        }
        while (l2 != 0L);
        l1 = addAndGet(-l1);
      }
      while (l1 > 0L);
      tryUnsubscribe();
    }

    private boolean tryUnsubscribe()
    {
      if ((this.hasTerminated) || (get() < -1L))
      {
        set(-1L);
        doUnsubscribe();
        return true;
      }
      return false;
    }

    public boolean isUnsubscribed()
    {
      return get() < 0L;
    }

    public void onCompleted()
    {
      if (this.hasTerminated)
        throw new IllegalStateException("Terminal event already emitted.");
      this.hasTerminated = true;
      if (!this.actualSubscriber.isUnsubscribed())
        this.actualSubscriber.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.hasTerminated)
        throw new IllegalStateException("Terminal event already emitted.");
      this.hasTerminated = true;
      if (!this.actualSubscriber.isUnsubscribed())
        this.actualSubscriber.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      if (this.onNextCalled)
        throw new IllegalStateException("onNext called multiple times!");
      this.onNextCalled = true;
      this.actualSubscriber.onNext(paramT);
    }

    public void request(long paramLong)
    {
      if ((paramLong > 0L) && (BackpressureUtils.getAndAddRequest(this, paramLong) == 0L))
      {
        if (paramLong == 9223372036854775807L)
          fastPath();
      }
      else
        return;
      slowPath(paramLong);
    }

    public void unsubscribe()
    {
      long l;
      do
      {
        l = get();
        if (!compareAndSet(0L, -1L))
          continue;
        doUnsubscribe();
        return;
      }
      while (!compareAndSet(l, -2L));
    }
  }

  static final class SyncOnSubscribeImpl<S, T> extends SyncOnSubscribe<S, T>
  {
    private final Func0<? extends S> generator;
    private final Func2<? super S, ? super Observer<? super T>, ? extends S> next;
    private final Action1<? super S> onUnsubscribe;

    public SyncOnSubscribeImpl(Func0<? extends S> paramFunc0, Func2<? super S, ? super Observer<? super T>, ? extends S> paramFunc2)
    {
      this(paramFunc0, paramFunc2, null);
    }

    SyncOnSubscribeImpl(Func0<? extends S> paramFunc0, Func2<? super S, ? super Observer<? super T>, ? extends S> paramFunc2, Action1<? super S> paramAction1)
    {
      this.generator = paramFunc0;
      this.next = paramFunc2;
      this.onUnsubscribe = paramAction1;
    }

    public SyncOnSubscribeImpl(Func2<S, Observer<? super T>, S> paramFunc2)
    {
      this(null, paramFunc2, null);
    }

    public SyncOnSubscribeImpl(Func2<S, Observer<? super T>, S> paramFunc2, Action1<? super S> paramAction1)
    {
      this(null, paramFunc2, paramAction1);
    }

    protected S generateState()
    {
      if (this.generator == null)
        return null;
      return this.generator.call();
    }

    protected S next(S paramS, Observer<? super T> paramObserver)
    {
      return this.next.call(paramS, paramObserver);
    }

    protected void onUnsubscribe(S paramS)
    {
      if (this.onUnsubscribe != null)
        this.onUnsubscribe.call(paramS);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.observables.SyncOnSubscribe
 * JD-Core Version:    0.6.0
 */