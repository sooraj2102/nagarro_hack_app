package rx.observables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Action3;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.internal.operators.BufferUntilSubscriber;
import rx.observers.SerializedObserver;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.CompositeSubscription;

@Experimental
public abstract class AsyncOnSubscribe<S, T>
  implements Observable.OnSubscribe<T>
{
  @Experimental
  public static <S, T> AsyncOnSubscribe<S, T> createSingleState(Func0<? extends S> paramFunc0, Action3<? super S, Long, ? super Observer<Observable<? extends T>>> paramAction3)
  {
    return new AsyncOnSubscribeImpl(paramFunc0, new Func3(paramAction3)
    {
      public S call(S paramS, Long paramLong, Observer<Observable<? extends T>> paramObserver)
      {
        this.val$next.call(paramS, paramLong, paramObserver);
        return paramS;
      }
    });
  }

  @Experimental
  public static <S, T> AsyncOnSubscribe<S, T> createSingleState(Func0<? extends S> paramFunc0, Action3<? super S, Long, ? super Observer<Observable<? extends T>>> paramAction3, Action1<? super S> paramAction1)
  {
    return new AsyncOnSubscribeImpl(paramFunc0, new Func3(paramAction3)
    {
      public S call(S paramS, Long paramLong, Observer<Observable<? extends T>> paramObserver)
      {
        this.val$next.call(paramS, paramLong, paramObserver);
        return paramS;
      }
    }
    , paramAction1);
  }

  @Experimental
  public static <S, T> AsyncOnSubscribe<S, T> createStateful(Func0<? extends S> paramFunc0, Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> paramFunc3)
  {
    return new AsyncOnSubscribeImpl(paramFunc0, paramFunc3);
  }

  @Experimental
  public static <S, T> AsyncOnSubscribe<S, T> createStateful(Func0<? extends S> paramFunc0, Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> paramFunc3, Action1<? super S> paramAction1)
  {
    return new AsyncOnSubscribeImpl(paramFunc0, paramFunc3, paramAction1);
  }

  @Experimental
  public static <T> AsyncOnSubscribe<Void, T> createStateless(Action2<Long, ? super Observer<Observable<? extends T>>> paramAction2)
  {
    return new AsyncOnSubscribeImpl(new Func3(paramAction2)
    {
      public Void call(Void paramVoid, Long paramLong, Observer<Observable<? extends T>> paramObserver)
      {
        this.val$next.call(paramLong, paramObserver);
        return paramVoid;
      }
    });
  }

  @Experimental
  public static <T> AsyncOnSubscribe<Void, T> createStateless(Action2<Long, ? super Observer<Observable<? extends T>>> paramAction2, Action0 paramAction0)
  {
    return new AsyncOnSubscribeImpl(new Func3(paramAction2)
    {
      public Void call(Void paramVoid, Long paramLong, Observer<Observable<? extends T>> paramObserver)
      {
        this.val$next.call(paramLong, paramObserver);
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
      UnicastSubject localUnicastSubject = UnicastSubject.create();
      AsyncOuterManager localAsyncOuterManager = new AsyncOuterManager(this, localObject, localUnicastSubject);
      6 local6 = new Subscriber(paramSubscriber, localAsyncOuterManager)
      {
        public void onCompleted()
        {
          this.val$actualSubscriber.onCompleted();
        }

        public void onError(Throwable paramThrowable)
        {
          this.val$actualSubscriber.onError(paramThrowable);
        }

        public void onNext(T paramT)
        {
          this.val$actualSubscriber.onNext(paramT);
        }

        public void setProducer(Producer paramProducer)
        {
          this.val$outerProducer.setConcatProducer(paramProducer);
        }
      };
      localUnicastSubject.onBackpressureBuffer().concatMap(new Func1()
      {
        public Observable<T> call(Observable<T> paramObservable)
        {
          return paramObservable.onBackpressureBuffer();
        }
      }).unsafeSubscribe(local6);
      paramSubscriber.add(local6);
      paramSubscriber.add(localAsyncOuterManager);
      paramSubscriber.setProducer(localAsyncOuterManager);
      return;
    }
    catch (Throwable localThrowable)
    {
      paramSubscriber.onError(localThrowable);
    }
  }

  protected abstract S generateState();

  protected abstract S next(S paramS, long paramLong, Observer<Observable<? extends T>> paramObserver);

  protected void onUnsubscribe(S paramS)
  {
  }

  static final class AsyncOnSubscribeImpl<S, T> extends AsyncOnSubscribe<S, T>
  {
    private final Func0<? extends S> generator;
    private final Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> next;
    private final Action1<? super S> onUnsubscribe;

    public AsyncOnSubscribeImpl(Func0<? extends S> paramFunc0, Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> paramFunc3)
    {
      this(paramFunc0, paramFunc3, null);
    }

    AsyncOnSubscribeImpl(Func0<? extends S> paramFunc0, Func3<? super S, Long, ? super Observer<Observable<? extends T>>, ? extends S> paramFunc3, Action1<? super S> paramAction1)
    {
      this.generator = paramFunc0;
      this.next = paramFunc3;
      this.onUnsubscribe = paramAction1;
    }

    public AsyncOnSubscribeImpl(Func3<S, Long, Observer<Observable<? extends T>>, S> paramFunc3)
    {
      this(null, paramFunc3, null);
    }

    public AsyncOnSubscribeImpl(Func3<S, Long, Observer<Observable<? extends T>>, S> paramFunc3, Action1<? super S> paramAction1)
    {
      this(null, paramFunc3, paramAction1);
    }

    protected S generateState()
    {
      if (this.generator == null)
        return null;
      return this.generator.call();
    }

    protected S next(S paramS, long paramLong, Observer<Observable<? extends T>> paramObserver)
    {
      return this.next.call(paramS, Long.valueOf(paramLong), paramObserver);
    }

    protected void onUnsubscribe(S paramS)
    {
      if (this.onUnsubscribe != null)
        this.onUnsubscribe.call(paramS);
    }
  }

  static final class AsyncOuterManager<S, T>
    implements Producer, Subscription, Observer<Observable<? extends T>>
  {
    Producer concatProducer;
    boolean emitting;
    long expectedDelivery;
    private boolean hasTerminated;
    final AtomicBoolean isUnsubscribed;
    private final AsyncOnSubscribe.UnicastSubject<Observable<T>> merger;
    private boolean onNextCalled;
    private final AsyncOnSubscribe<S, T> parent;
    List<Long> requests;
    private final SerializedObserver<Observable<? extends T>> serializedSubscriber;
    private S state;
    final CompositeSubscription subscriptions = new CompositeSubscription();

    public AsyncOuterManager(AsyncOnSubscribe<S, T> paramAsyncOnSubscribe, S paramS, AsyncOnSubscribe.UnicastSubject<Observable<T>> paramUnicastSubject)
    {
      this.parent = paramAsyncOnSubscribe;
      this.serializedSubscriber = new SerializedObserver(this);
      this.state = paramS;
      this.merger = paramUnicastSubject;
      this.isUnsubscribed = new AtomicBoolean();
    }

    private void handleThrownError(Throwable paramThrowable)
    {
      if (this.hasTerminated)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.hasTerminated = true;
      this.merger.onError(paramThrowable);
      cleanup();
    }

    private void subscribeBufferToObservable(Observable<? extends T> paramObservable)
    {
      BufferUntilSubscriber localBufferUntilSubscriber = BufferUntilSubscriber.create();
      1 local1 = new Subscriber(this.expectedDelivery, localBufferUntilSubscriber)
      {
        long remaining = this.val$expected;

        public void onCompleted()
        {
          this.val$buffer.onCompleted();
          long l = this.remaining;
          if (l > 0L)
            AsyncOnSubscribe.AsyncOuterManager.this.requestRemaining(l);
        }

        public void onError(Throwable paramThrowable)
        {
          this.val$buffer.onError(paramThrowable);
        }

        public void onNext(T paramT)
        {
          this.remaining -= 1L;
          this.val$buffer.onNext(paramT);
        }
      };
      this.subscriptions.add(local1);
      paramObservable.doOnTerminate(new Action0(local1)
      {
        public void call()
        {
          AsyncOnSubscribe.AsyncOuterManager.this.subscriptions.remove(this.val$s);
        }
      }).subscribe(local1);
      this.merger.onNext(localBufferUntilSubscriber);
    }

    void cleanup()
    {
      this.subscriptions.unsubscribe();
      try
      {
        this.parent.onUnsubscribe(this.state);
        return;
      }
      catch (Throwable localThrowable)
      {
        handleThrownError(localThrowable);
      }
    }

    public boolean isUnsubscribed()
    {
      return this.isUnsubscribed.get();
    }

    public void nextIteration(long paramLong)
    {
      this.state = this.parent.next(this.state, paramLong, this.serializedSubscriber);
    }

    public void onCompleted()
    {
      if (this.hasTerminated)
        throw new IllegalStateException("Terminal event already emitted.");
      this.hasTerminated = true;
      this.merger.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.hasTerminated)
        throw new IllegalStateException("Terminal event already emitted.");
      this.hasTerminated = true;
      this.merger.onError(paramThrowable);
    }

    public void onNext(Observable<? extends T> paramObservable)
    {
      if (this.onNextCalled)
        throw new IllegalStateException("onNext called multiple times!");
      this.onNextCalled = true;
      if (this.hasTerminated)
        return;
      subscribeBufferToObservable(paramObservable);
    }

    public void request(long paramLong)
    {
      if (paramLong == 0L)
        return;
      if (paramLong < 0L)
        throw new IllegalStateException("Request can't be negative! " + paramLong);
      monitorenter;
      label90: Iterator localIterator;
      label159: 
      do
      {
        int i;
        while (!localIterator.hasNext())
        {
          List localList;
          try
          {
            if (this.emitting)
            {
              Object localObject3 = this.requests;
              if (localObject3 == null)
              {
                localObject3 = new ArrayList();
                this.requests = ((List)localObject3);
              }
              ((List)localObject3).add(Long.valueOf(paramLong));
              i = 1;
              monitorexit;
              this.concatProducer.request(paramLong);
              if ((i != 0) || (tryEmit(paramLong)))
                break;
              monitorenter;
              try
              {
                localList = this.requests;
                if (localList != null)
                  break label159;
                this.emitting = false;
                return;
              }
              finally
              {
                monitorexit;
              }
            }
            else
            {
              this.emitting = true;
              i = 0;
              break label90;
            }
          }
          finally
          {
            monitorexit;
          }
          this.requests = null;
          monitorexit;
          localIterator = localList.iterator();
        }
      }
      while (!tryEmit(((Long)localIterator.next()).longValue()));
    }

    public void requestRemaining(long paramLong)
    {
      if (paramLong == 0L);
      do
      {
        return;
        if (paramLong < 0L)
          throw new IllegalStateException("Request can't be negative! " + paramLong);
        monitorenter;
        try
        {
          if (this.emitting)
          {
            Object localObject3 = this.requests;
            if (localObject3 == null)
            {
              localObject3 = new ArrayList();
              this.requests = ((List)localObject3);
            }
            ((List)localObject3).add(Long.valueOf(paramLong));
            return;
          }
        }
        finally
        {
          monitorexit;
        }
        this.emitting = true;
        monitorexit;
      }
      while (tryEmit(paramLong));
      Iterator localIterator;
      do
        while (!localIterator.hasNext())
        {
          monitorenter;
          List localList;
          try
          {
            localList = this.requests;
            if (localList == null)
            {
              this.emitting = false;
              return;
            }
          }
          finally
          {
            monitorexit;
          }
          this.requests = null;
          monitorexit;
          localIterator = localList.iterator();
        }
      while (!tryEmit(((Long)localIterator.next()).longValue()));
    }

    void setConcatProducer(Producer paramProducer)
    {
      if (this.concatProducer != null)
        throw new IllegalStateException("setConcatProducer may be called at most once!");
      this.concatProducer = paramProducer;
    }

    boolean tryEmit(long paramLong)
    {
      if (isUnsubscribed())
      {
        cleanup();
        return true;
      }
      try
      {
        this.onNextCalled = false;
        this.expectedDelivery = paramLong;
        nextIteration(paramLong);
        if ((this.hasTerminated) || (isUnsubscribed()))
        {
          cleanup();
          return true;
        }
      }
      catch (Throwable localThrowable)
      {
        handleThrownError(localThrowable);
        return true;
      }
      if (!this.onNextCalled)
      {
        handleThrownError(new IllegalStateException("No events emitted!"));
        return true;
      }
      return false;
    }

    public void unsubscribe()
    {
      if (this.isUnsubscribed.compareAndSet(false, true))
      {
        monitorenter;
        try
        {
          if (this.emitting)
          {
            this.requests = new ArrayList();
            this.requests.add(Long.valueOf(0L));
            return;
          }
          this.emitting = true;
          monitorexit;
          cleanup();
          return;
        }
        finally
        {
          monitorexit;
        }
      }
    }
  }

  static final class UnicastSubject<T> extends Observable<T>
    implements Observer<T>
  {
    private final State<T> state;

    protected UnicastSubject(State<T> paramState)
    {
      super();
      this.state = paramState;
    }

    public static <T> UnicastSubject<T> create()
    {
      return new UnicastSubject(new State());
    }

    public void onCompleted()
    {
      this.state.subscriber.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      this.state.subscriber.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      this.state.subscriber.onNext(paramT);
    }

    static final class State<T>
      implements Observable.OnSubscribe<T>
    {
      Subscriber<? super T> subscriber;

      public void call(Subscriber<? super T> paramSubscriber)
      {
        monitorenter;
        try
        {
          if (this.subscriber == null)
          {
            this.subscriber = paramSubscriber;
            return;
          }
          monitorexit;
          paramSubscriber.onError(new IllegalStateException("There can be only one subscriber"));
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
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.observables.AsyncOnSubscribe
 * JD-Core Version:    0.6.0
 */