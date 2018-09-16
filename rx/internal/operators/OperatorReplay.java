package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.util.OpenHashSet;
import rx.observables.ConnectableObservable;
import rx.schedulers.Timestamped;
import rx.subscriptions.Subscriptions;

public final class OperatorReplay<T> extends ConnectableObservable<T>
{
  static final Func0 DEFAULT_UNBOUNDED_FACTORY = new Func0()
  {
    public Object call()
    {
      return new OperatorReplay.UnboundedReplayBuffer(16);
    }
  };
  final Func0<? extends ReplayBuffer<T>> bufferFactory;
  final AtomicReference<ReplaySubscriber<T>> current;
  final Observable<? extends T> source;

  private OperatorReplay(Observable.OnSubscribe<T> paramOnSubscribe, Observable<? extends T> paramObservable, AtomicReference<ReplaySubscriber<T>> paramAtomicReference, Func0<? extends ReplayBuffer<T>> paramFunc0)
  {
    super(paramOnSubscribe);
    this.source = paramObservable;
    this.current = paramAtomicReference;
    this.bufferFactory = paramFunc0;
  }

  public static <T> ConnectableObservable<T> create(Observable<? extends T> paramObservable)
  {
    return create(paramObservable, DEFAULT_UNBOUNDED_FACTORY);
  }

  public static <T> ConnectableObservable<T> create(Observable<? extends T> paramObservable, int paramInt)
  {
    if (paramInt == 2147483647)
      return create(paramObservable);
    return create(paramObservable, new Func0(paramInt)
    {
      public OperatorReplay.ReplayBuffer<T> call()
      {
        return new OperatorReplay.SizeBoundReplayBuffer(this.val$bufferSize);
      }
    });
  }

  public static <T> ConnectableObservable<T> create(Observable<? extends T> paramObservable, long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    return create(paramObservable, paramLong, paramTimeUnit, paramScheduler, 2147483647);
  }

  public static <T> ConnectableObservable<T> create(Observable<? extends T> paramObservable, long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler, int paramInt)
  {
    return create(paramObservable, new Func0(paramInt, paramTimeUnit.toMillis(paramLong), paramScheduler)
    {
      public OperatorReplay.ReplayBuffer<T> call()
      {
        return new OperatorReplay.SizeAndTimeBoundReplayBuffer(this.val$bufferSize, this.val$maxAgeInMillis, this.val$scheduler);
      }
    });
  }

  static <T> ConnectableObservable<T> create(Observable<? extends T> paramObservable, Func0<? extends ReplayBuffer<T>> paramFunc0)
  {
    AtomicReference localAtomicReference = new AtomicReference();
    return new OperatorReplay(new Observable.OnSubscribe(localAtomicReference, paramFunc0)
    {
      public void call(Subscriber<? super T> paramSubscriber)
      {
        Object localObject;
        while (true)
        {
          localObject = (OperatorReplay.ReplaySubscriber)this.val$curr.get();
          if (localObject != null)
            break;
          OperatorReplay.ReplaySubscriber localReplaySubscriber = new OperatorReplay.ReplaySubscriber((OperatorReplay.ReplayBuffer)this.val$bufferFactory.call());
          localReplaySubscriber.init();
          if (!this.val$curr.compareAndSet(localObject, localReplaySubscriber))
            continue;
          localObject = localReplaySubscriber;
        }
        OperatorReplay.InnerProducer localInnerProducer = new OperatorReplay.InnerProducer((OperatorReplay.ReplaySubscriber)localObject, paramSubscriber);
        ((OperatorReplay.ReplaySubscriber)localObject).add(localInnerProducer);
        paramSubscriber.add(localInnerProducer);
        ((OperatorReplay.ReplaySubscriber)localObject).buffer.replay(localInnerProducer);
        paramSubscriber.setProducer(localInnerProducer);
      }
    }
    , paramObservable, localAtomicReference, paramFunc0);
  }

  public static <T, U, R> Observable<R> multicastSelector(Func0<? extends ConnectableObservable<U>> paramFunc0, Func1<? super Observable<U>, ? extends Observable<R>> paramFunc1)
  {
    return Observable.create(new Observable.OnSubscribe(paramFunc0, paramFunc1)
    {
      public void call(Subscriber<? super R> paramSubscriber)
      {
        try
        {
          ConnectableObservable localConnectableObservable = (ConnectableObservable)this.val$connectableFactory.call();
          Observable localObservable = (Observable)this.val$selector.call(localConnectableObservable);
          localObservable.subscribe(paramSubscriber);
          localConnectableObservable.connect(new Action1(paramSubscriber)
          {
            public void call(Subscription paramSubscription)
            {
              this.val$child.add(paramSubscription);
            }
          });
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwOrReport(localThrowable, paramSubscriber);
        }
      }
    });
  }

  public static <T> ConnectableObservable<T> observeOn(ConnectableObservable<T> paramConnectableObservable, Scheduler paramScheduler)
  {
    return new ConnectableObservable(new Observable.OnSubscribe(paramConnectableObservable.observeOn(paramScheduler))
    {
      public void call(Subscriber<? super T> paramSubscriber)
      {
        this.val$observable.unsafeSubscribe(new Subscriber(paramSubscriber, paramSubscriber)
        {
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
            this.val$child.onNext(paramT);
          }
        });
      }
    }
    , paramConnectableObservable)
    {
      public void connect(Action1<? super Subscription> paramAction1)
      {
        this.val$co.connect(paramAction1);
      }
    };
  }

  public void connect(Action1<? super Subscription> paramAction1)
  {
    boolean bool = true;
    Object localObject;
    while (true)
    {
      localObject = (ReplaySubscriber)this.current.get();
      if ((localObject != null) && (!((ReplaySubscriber)localObject).isUnsubscribed()))
        break;
      ReplaySubscriber localReplaySubscriber = new ReplaySubscriber((ReplayBuffer)this.bufferFactory.call());
      localReplaySubscriber.init();
      if (!this.current.compareAndSet(localObject, localReplaySubscriber))
        continue;
      localObject = localReplaySubscriber;
    }
    if ((!((ReplaySubscriber)localObject).shouldConnect.get()) && (((ReplaySubscriber)localObject).shouldConnect.compareAndSet(false, bool)));
    while (true)
    {
      paramAction1.call(localObject);
      if (bool)
        this.source.unsafeSubscribe((Subscriber)localObject);
      return;
      bool = false;
    }
  }

  static class BoundedReplayBuffer<T> extends AtomicReference<OperatorReplay.Node>
    implements OperatorReplay.ReplayBuffer<T>
  {
    private static final long serialVersionUID = 2346567790059478686L;
    long index;
    final NotificationLite<T> nl = NotificationLite.instance();
    int size;
    OperatorReplay.Node tail;

    public BoundedReplayBuffer()
    {
      OperatorReplay.Node localNode = new OperatorReplay.Node(null, 0L);
      this.tail = localNode;
      set(localNode);
    }

    final void addLast(OperatorReplay.Node paramNode)
    {
      this.tail.set(paramNode);
      this.tail = paramNode;
      this.size = (1 + this.size);
    }

    final void collect(Collection<? super T> paramCollection)
    {
      OperatorReplay.Node localNode;
      for (Object localObject1 = getInitialHead(); ; localObject1 = localNode)
      {
        localNode = (OperatorReplay.Node)((OperatorReplay.Node)localObject1).get();
        Object localObject2;
        if (localNode != null)
        {
          localObject2 = leaveTransform(localNode.value);
          if ((!this.nl.isCompleted(localObject2)) && (!this.nl.isError(localObject2)));
        }
        else
        {
          return;
        }
        paramCollection.add(this.nl.getValue(localObject2));
      }
    }

    public final void complete()
    {
      Object localObject = enterTransform(this.nl.completed());
      long l = 1L + this.index;
      this.index = l;
      addLast(new OperatorReplay.Node(localObject, l));
      truncateFinal();
    }

    Object enterTransform(Object paramObject)
    {
      return paramObject;
    }

    public final void error(Throwable paramThrowable)
    {
      Object localObject = enterTransform(this.nl.error(paramThrowable));
      long l = 1L + this.index;
      this.index = l;
      addLast(new OperatorReplay.Node(localObject, l));
      truncateFinal();
    }

    OperatorReplay.Node getInitialHead()
    {
      return (OperatorReplay.Node)get();
    }

    boolean hasCompleted()
    {
      return (this.tail.value != null) && (this.nl.isCompleted(leaveTransform(this.tail.value)));
    }

    boolean hasError()
    {
      return (this.tail.value != null) && (this.nl.isError(leaveTransform(this.tail.value)));
    }

    Object leaveTransform(Object paramObject)
    {
      return paramObject;
    }

    public final void next(T paramT)
    {
      Object localObject = enterTransform(this.nl.next(paramT));
      long l = 1L + this.index;
      this.index = l;
      addLast(new OperatorReplay.Node(localObject, l));
      truncate();
    }

    final void removeFirst()
    {
      OperatorReplay.Node localNode = (OperatorReplay.Node)((OperatorReplay.Node)get()).get();
      if (localNode == null)
        throw new IllegalStateException("Empty list!");
      this.size = (-1 + this.size);
      setFirst(localNode);
    }

    final void removeSome(int paramInt)
    {
      OperatorReplay.Node localNode = (OperatorReplay.Node)get();
      while (paramInt > 0)
      {
        localNode = (OperatorReplay.Node)localNode.get();
        paramInt--;
        this.size = (-1 + this.size);
      }
      setFirst(localNode);
    }

    public final void replay(OperatorReplay.InnerProducer<T> paramInnerProducer)
    {
      monitorenter;
      while (true)
      {
        long l1;
        long l2;
        OperatorReplay.Node localNode;
        try
        {
          if (!paramInnerProducer.emitting)
            continue;
          paramInnerProducer.missed = true;
          return;
          paramInnerProducer.emitting = true;
          monitorexit;
          if (paramInnerProducer.isUnsubscribed())
            break;
          localObject2 = (OperatorReplay.Node)paramInnerProducer.index();
          if (localObject2 != null)
            continue;
          localObject2 = getInitialHead();
          paramInnerProducer.index = localObject2;
          paramInnerProducer.addTotalRequested(((OperatorReplay.Node)localObject2).index);
          if (paramInnerProducer.isUnsubscribed())
            break;
          Subscriber localSubscriber = paramInnerProducer.child;
          if (localSubscriber == null)
            break;
          l1 = paramInnerProducer.get();
          l2 = 0L;
          if (l2 == l1)
            break label223;
          localNode = (OperatorReplay.Node)((OperatorReplay.Node)localObject2).get();
          if (localNode == null)
            break label223;
          Object localObject4 = leaveTransform(localNode.value);
          try
          {
            if (!this.nl.accept(localSubscriber, localObject4))
              break label206;
            paramInnerProducer.index = null;
            return;
          }
          catch (Throwable localThrowable)
          {
            paramInnerProducer.index = null;
            Exceptions.throwIfFatal(localThrowable);
            paramInnerProducer.unsubscribe();
            if (this.nl.isError(localObject4))
              break;
          }
          if (this.nl.isCompleted(localObject4))
            break;
          localSubscriber.onError(OnErrorThrowable.addValueAsLastCause(localThrowable, this.nl.getValue(localObject4)));
          return;
        }
        finally
        {
          monitorexit;
        }
        label206: l2 += 1L;
        Object localObject2 = localNode;
        if (paramInnerProducer.isUnsubscribed())
        {
          return;
          label223: if (l2 != 0L)
          {
            paramInnerProducer.index = localObject2;
            if (l1 != 9223372036854775807L)
              paramInnerProducer.produced(l2);
          }
          monitorenter;
          try
          {
            if (!paramInnerProducer.missed)
            {
              paramInnerProducer.emitting = false;
              return;
            }
          }
          finally
          {
            monitorexit;
          }
          paramInnerProducer.missed = false;
          monitorexit;
        }
      }
    }

    final void setFirst(OperatorReplay.Node paramNode)
    {
      set(paramNode);
    }

    void truncate()
    {
    }

    void truncateFinal()
    {
    }
  }

  static final class InnerProducer<T> extends AtomicLong
    implements Producer, Subscription
  {
    static final long UNSUBSCRIBED = -9223372036854775808L;
    private static final long serialVersionUID = -4453897557930727610L;
    Subscriber<? super T> child;
    boolean emitting;
    Object index;
    boolean missed;
    final OperatorReplay.ReplaySubscriber<T> parent;
    final AtomicLong totalRequested;

    public InnerProducer(OperatorReplay.ReplaySubscriber<T> paramReplaySubscriber, Subscriber<? super T> paramSubscriber)
    {
      this.parent = paramReplaySubscriber;
      this.child = paramSubscriber;
      this.totalRequested = new AtomicLong();
    }

    void addTotalRequested(long paramLong)
    {
      long l1;
      long l2;
      do
      {
        l1 = this.totalRequested.get();
        l2 = l1 + paramLong;
        if (l2 >= 0L)
          continue;
        l2 = 9223372036854775807L;
      }
      while (!this.totalRequested.compareAndSet(l1, l2));
    }

    <U> U index()
    {
      return this.index;
    }

    public boolean isUnsubscribed()
    {
      return get() == -9223372036854775808L;
    }

    public long produced(long paramLong)
    {
      if (paramLong <= 0L)
        throw new IllegalArgumentException("Cant produce zero or less");
      long l1;
      long l2;
      do
      {
        l1 = get();
        if (l1 == -9223372036854775808L)
          return -9223372036854775808L;
        l2 = l1 - paramLong;
        if (l2 >= 0L)
          continue;
        throw new IllegalStateException("More produced (" + paramLong + ") than requested (" + l1 + ")");
      }
      while (!compareAndSet(l1, l2));
      return l2;
    }

    public void request(long paramLong)
    {
      if (paramLong < 0L)
        return;
      long l1;
      long l2;
      do
      {
        l1 = get();
        if ((l1 == -9223372036854775808L) || ((l1 >= 0L) && (paramLong == 0L)))
          break;
        l2 = l1 + paramLong;
        if (l2 >= 0L)
          continue;
        l2 = 9223372036854775807L;
      }
      while (!compareAndSet(l1, l2));
      addTotalRequested(paramLong);
      this.parent.manageRequests(this);
      this.parent.buffer.replay(this);
    }

    public void unsubscribe()
    {
      if ((get() != -9223372036854775808L) && (getAndSet(-9223372036854775808L) != -9223372036854775808L))
      {
        this.parent.remove(this);
        this.parent.manageRequests(this);
        this.child = null;
      }
    }
  }

  static final class Node extends AtomicReference<Node>
  {
    private static final long serialVersionUID = 245354315435971818L;
    final long index;
    final Object value;

    public Node(Object paramObject, long paramLong)
    {
      this.value = paramObject;
      this.index = paramLong;
    }
  }

  static abstract interface ReplayBuffer<T>
  {
    public abstract void complete();

    public abstract void error(Throwable paramThrowable);

    public abstract void next(T paramT);

    public abstract void replay(OperatorReplay.InnerProducer<T> paramInnerProducer);
  }

  static final class ReplaySubscriber<T> extends Subscriber<T>
    implements Subscription
  {
    static final OperatorReplay.InnerProducer[] EMPTY = new OperatorReplay.InnerProducer[0];
    static final OperatorReplay.InnerProducer[] TERMINATED = new OperatorReplay.InnerProducer[0];
    final OperatorReplay.ReplayBuffer<T> buffer;
    boolean coordinateAll;
    List<OperatorReplay.InnerProducer<T>> coordinationQueue;
    boolean done;
    boolean emitting;
    long maxChildRequested;
    long maxUpstreamRequested;
    boolean missed;
    final NotificationLite<T> nl;
    volatile Producer producer;
    final OpenHashSet<OperatorReplay.InnerProducer<T>> producers;
    OperatorReplay.InnerProducer<T>[] producersCache;
    long producersCacheVersion;
    volatile long producersVersion;
    final AtomicBoolean shouldConnect;
    volatile boolean terminated;

    public ReplaySubscriber(OperatorReplay.ReplayBuffer<T> paramReplayBuffer)
    {
      this.buffer = paramReplayBuffer;
      this.nl = NotificationLite.instance();
      this.producers = new OpenHashSet();
      this.producersCache = EMPTY;
      this.shouldConnect = new AtomicBoolean();
      request(0L);
    }

    boolean add(OperatorReplay.InnerProducer<T> paramInnerProducer)
    {
      if (paramInnerProducer == null)
        throw new NullPointerException();
      if (this.terminated)
        return false;
      synchronized (this.producers)
      {
        if (this.terminated)
          return false;
      }
      this.producers.add(paramInnerProducer);
      this.producersVersion = (1L + this.producersVersion);
      monitorexit;
      return true;
    }

    OperatorReplay.InnerProducer<T>[] copyProducers()
    {
      synchronized (this.producers)
      {
        Object[] arrayOfObject = this.producers.values();
        int i = arrayOfObject.length;
        OperatorReplay.InnerProducer[] arrayOfInnerProducer = new OperatorReplay.InnerProducer[i];
        System.arraycopy(arrayOfObject, 0, arrayOfInnerProducer, 0, i);
        return arrayOfInnerProducer;
      }
    }

    void init()
    {
      add(Subscriptions.create(new Action0()
      {
        public void call()
        {
          if (!OperatorReplay.ReplaySubscriber.this.terminated)
            synchronized (OperatorReplay.ReplaySubscriber.this.producers)
            {
              if (!OperatorReplay.ReplaySubscriber.this.terminated)
              {
                OperatorReplay.ReplaySubscriber.this.producers.terminate();
                OperatorReplay.ReplaySubscriber localReplaySubscriber = OperatorReplay.ReplaySubscriber.this;
                localReplaySubscriber.producersVersion = (1L + localReplaySubscriber.producersVersion);
                OperatorReplay.ReplaySubscriber.this.terminated = true;
              }
              return;
            }
        }
      }));
    }

    void makeRequest(long paramLong1, long paramLong2)
    {
      long l1 = this.maxUpstreamRequested;
      Producer localProducer = this.producer;
      long l2 = paramLong1 - paramLong2;
      if (l2 != 0L)
      {
        this.maxChildRequested = paramLong1;
        if (localProducer != null)
          if (l1 != 0L)
          {
            this.maxUpstreamRequested = 0L;
            localProducer.request(l1 + l2);
          }
      }
      do
      {
        return;
        localProducer.request(l2);
        return;
        long l3 = l1 + l2;
        if (l3 < 0L)
          l3 = 9223372036854775807L;
        this.maxUpstreamRequested = l3;
        return;
      }
      while ((l1 == 0L) || (localProducer == null));
      this.maxUpstreamRequested = 0L;
      localProducer.request(l1);
    }

    void manageRequests(OperatorReplay.InnerProducer<T> paramInnerProducer)
    {
      if (isUnsubscribed());
      label200: label357: 
      while (true)
      {
        return;
        monitorenter;
        while (true)
        {
          try
          {
            if (!this.emitting)
              break;
            if (paramInnerProducer != null)
            {
              Object localObject2 = this.coordinationQueue;
              if (localObject2 != null)
                continue;
              localObject2 = new ArrayList();
              this.coordinationQueue = ((List)localObject2);
              ((List)localObject2).add(paramInnerProducer);
              this.missed = true;
              return;
            }
          }
          finally
          {
            monitorexit;
          }
          this.coordinateAll = true;
        }
        this.emitting = true;
        monitorexit;
        long l1 = this.maxChildRequested;
        long l2;
        if (paramInnerProducer != null)
        {
          l2 = Math.max(l1, paramInnerProducer.totalRequested.get());
          makeRequest(l2, l1);
        }
        while (true)
        {
          if (isUnsubscribed())
            break label357;
          monitorenter;
          try
          {
            if (this.missed)
              break label200;
            this.emitting = false;
            return;
          }
          finally
          {
            monitorexit;
          }
          l2 = l1;
          for (OperatorReplay.InnerProducer localInnerProducer1 : copyProducers())
          {
            if (localInnerProducer1 == null)
              continue;
            l2 = Math.max(l2, localInnerProducer1.totalRequested.get());
          }
          break;
          this.missed = false;
          List localList = this.coordinationQueue;
          this.coordinationQueue = null;
          boolean bool = this.coordinateAll;
          this.coordinateAll = false;
          monitorexit;
          long l3 = this.maxChildRequested;
          long l4 = l3;
          if (localList != null)
          {
            Iterator localIterator = localList.iterator();
            while (localIterator.hasNext())
              l4 = Math.max(l4, ((OperatorReplay.InnerProducer)localIterator.next()).totalRequested.get());
          }
          if (bool)
            for (OperatorReplay.InnerProducer localInnerProducer2 : copyProducers())
            {
              if (localInnerProducer2 == null)
                continue;
              l4 = Math.max(l4, localInnerProducer2.totalRequested.get());
            }
          makeRequest(l4, l3);
        }
      }
    }

    public void onCompleted()
    {
      if (!this.done)
        this.done = true;
      try
      {
        this.buffer.complete();
        replay();
        return;
      }
      finally
      {
        unsubscribe();
      }
      throw localObject;
    }

    public void onError(Throwable paramThrowable)
    {
      if (!this.done)
        this.done = true;
      try
      {
        this.buffer.error(paramThrowable);
        replay();
        return;
      }
      finally
      {
        unsubscribe();
      }
      throw localObject;
    }

    public void onNext(T paramT)
    {
      if (!this.done)
      {
        this.buffer.next(paramT);
        replay();
      }
    }

    void remove(OperatorReplay.InnerProducer<T> paramInnerProducer)
    {
      if (this.terminated)
        return;
      synchronized (this.producers)
      {
        if (this.terminated)
          return;
      }
      this.producers.remove(paramInnerProducer);
      if (this.producers.isEmpty())
        this.producersCache = EMPTY;
      this.producersVersion = (1L + this.producersVersion);
      monitorexit;
    }

    void replay()
    {
      OperatorReplay.InnerProducer[] arrayOfInnerProducer1 = this.producersCache;
      if (this.producersCacheVersion != this.producersVersion);
      synchronized (this.producers)
      {
        arrayOfInnerProducer1 = this.producersCache;
        Object[] arrayOfObject = this.producers.values();
        int k = arrayOfObject.length;
        if (arrayOfInnerProducer1.length != k)
        {
          arrayOfInnerProducer1 = new OperatorReplay.InnerProducer[k];
          this.producersCache = arrayOfInnerProducer1;
        }
        System.arraycopy(arrayOfObject, 0, arrayOfInnerProducer1, 0, k);
        this.producersCacheVersion = this.producersVersion;
        OperatorReplay.ReplayBuffer localReplayBuffer = this.buffer;
        OperatorReplay.InnerProducer[] arrayOfInnerProducer2 = arrayOfInnerProducer1;
        int i = arrayOfInnerProducer2.length;
        int j = 0;
        if (j < i)
        {
          OperatorReplay.InnerProducer localInnerProducer = arrayOfInnerProducer2[j];
          if (localInnerProducer != null)
            localReplayBuffer.replay(localInnerProducer);
          j++;
        }
      }
    }

    public void setProducer(Producer paramProducer)
    {
      if (this.producer != null)
        throw new IllegalStateException("Only a single producer can be set on a Subscriber.");
      this.producer = paramProducer;
      manageRequests(null);
      replay();
    }
  }

  static final class SizeAndTimeBoundReplayBuffer<T> extends OperatorReplay.BoundedReplayBuffer<T>
  {
    private static final long serialVersionUID = 3457957419649567404L;
    final int limit;
    final long maxAgeInMillis;
    final Scheduler scheduler;

    public SizeAndTimeBoundReplayBuffer(int paramInt, long paramLong, Scheduler paramScheduler)
    {
      this.scheduler = paramScheduler;
      this.limit = paramInt;
      this.maxAgeInMillis = paramLong;
    }

    Object enterTransform(Object paramObject)
    {
      return new Timestamped(this.scheduler.now(), paramObject);
    }

    OperatorReplay.Node getInitialHead()
    {
      long l = this.scheduler.now() - this.maxAgeInMillis;
      Object localObject = (OperatorReplay.Node)get();
      for (OperatorReplay.Node localNode = (OperatorReplay.Node)((OperatorReplay.Node)localObject).get(); (localNode != null) && (((Timestamped)localNode.value).getTimestampMillis() <= l); localNode = (OperatorReplay.Node)localNode.get())
        localObject = localNode;
      return (OperatorReplay.Node)localObject;
    }

    Object leaveTransform(Object paramObject)
    {
      return ((Timestamped)paramObject).getValue();
    }

    void truncate()
    {
      long l = this.scheduler.now() - this.maxAgeInMillis;
      Object localObject = (OperatorReplay.Node)get();
      OperatorReplay.Node localNode = (OperatorReplay.Node)((OperatorReplay.Node)localObject).get();
      int i = 0;
      while (localNode != null)
      {
        if (this.size > this.limit)
        {
          i++;
          this.size = (-1 + this.size);
          localObject = localNode;
          localNode = (OperatorReplay.Node)localNode.get();
          continue;
        }
        if (((Timestamped)localNode.value).getTimestampMillis() > l)
          break;
        i++;
        this.size = (-1 + this.size);
        localObject = localNode;
        localNode = (OperatorReplay.Node)localNode.get();
      }
      if (i != 0)
        setFirst((OperatorReplay.Node)localObject);
    }

    void truncateFinal()
    {
      long l = this.scheduler.now() - this.maxAgeInMillis;
      Object localObject = (OperatorReplay.Node)get();
      OperatorReplay.Node localNode = (OperatorReplay.Node)((OperatorReplay.Node)localObject).get();
      int i = 0;
      while ((localNode != null) && (this.size > 1) && (((Timestamped)localNode.value).getTimestampMillis() <= l))
      {
        i++;
        this.size = (-1 + this.size);
        localObject = localNode;
        localNode = (OperatorReplay.Node)localNode.get();
      }
      if (i != 0)
        setFirst((OperatorReplay.Node)localObject);
    }
  }

  static final class SizeBoundReplayBuffer<T> extends OperatorReplay.BoundedReplayBuffer<T>
  {
    private static final long serialVersionUID = -5898283885385201806L;
    final int limit;

    public SizeBoundReplayBuffer(int paramInt)
    {
      this.limit = paramInt;
    }

    void truncate()
    {
      if (this.size > this.limit)
        removeFirst();
    }
  }

  static final class UnboundedReplayBuffer<T> extends ArrayList<Object>
    implements OperatorReplay.ReplayBuffer<T>
  {
    private static final long serialVersionUID = 7063189396499112664L;
    final NotificationLite<T> nl = NotificationLite.instance();
    volatile int size;

    public UnboundedReplayBuffer(int paramInt)
    {
      super();
    }

    public void complete()
    {
      add(this.nl.completed());
      this.size = (1 + this.size);
    }

    public void error(Throwable paramThrowable)
    {
      add(this.nl.error(paramThrowable));
      this.size = (1 + this.size);
    }

    public void next(T paramT)
    {
      add(this.nl.next(paramT));
      this.size = (1 + this.size);
    }

    // ERROR //
    public void replay(OperatorReplay.InnerProducer<T> paramInnerProducer)
    {
      // Byte code:
      //   0: aload_1
      //   1: monitorenter
      //   2: aload_1
      //   3: getfield 60	rx/internal/operators/OperatorReplay$InnerProducer:emitting	Z
      //   6: ifeq +11 -> 17
      //   9: aload_1
      //   10: iconst_1
      //   11: putfield 63	rx/internal/operators/OperatorReplay$InnerProducer:missed	Z
      //   14: aload_1
      //   15: monitorexit
      //   16: return
      //   17: aload_1
      //   18: iconst_1
      //   19: putfield 60	rx/internal/operators/OperatorReplay$InnerProducer:emitting	Z
      //   22: aload_1
      //   23: monitorexit
      //   24: aload_1
      //   25: invokevirtual 67	rx/internal/operators/OperatorReplay$InnerProducer:isUnsubscribed	()Z
      //   28: ifne +240 -> 268
      //   31: aload_0
      //   32: getfield 40	rx/internal/operators/OperatorReplay$UnboundedReplayBuffer:size	I
      //   35: istore_3
      //   36: aload_1
      //   37: invokevirtual 70	rx/internal/operators/OperatorReplay$InnerProducer:index	()Ljava/lang/Object;
      //   40: checkcast 72	java/lang/Integer
      //   43: astore 4
      //   45: aload 4
      //   47: ifnull +94 -> 141
      //   50: aload 4
      //   52: invokevirtual 76	java/lang/Integer:intValue	()I
      //   55: istore 5
      //   57: aload_1
      //   58: getfield 80	rx/internal/operators/OperatorReplay$InnerProducer:child	Lrx/Subscriber;
      //   61: astore 6
      //   63: aload 6
      //   65: ifnull +203 -> 268
      //   68: aload_1
      //   69: invokevirtual 84	rx/internal/operators/OperatorReplay$InnerProducer:get	()J
      //   72: lstore 7
      //   74: lconst_0
      //   75: lstore 9
      //   77: lload 9
      //   79: lload 7
      //   81: lcmp
      //   82: ifeq +120 -> 202
      //   85: iload 5
      //   87: iload_3
      //   88: if_icmpge +114 -> 202
      //   91: aload_0
      //   92: iload 5
      //   94: invokevirtual 87	rx/internal/operators/OperatorReplay$UnboundedReplayBuffer:get	(I)Ljava/lang/Object;
      //   97: astore 14
      //   99: aload_0
      //   100: getfield 28	rx/internal/operators/OperatorReplay$UnboundedReplayBuffer:nl	Lrx/internal/operators/NotificationLite;
      //   103: aload 6
      //   105: aload 14
      //   107: invokevirtual 91	rx/internal/operators/NotificationLite:accept	(Lrx/Observer;Ljava/lang/Object;)Z
      //   110: istore 16
      //   112: iload 16
      //   114: ifne +154 -> 268
      //   117: aload_1
      //   118: invokevirtual 67	rx/internal/operators/OperatorReplay$InnerProducer:isUnsubscribed	()Z
      //   121: ifne +147 -> 268
      //   124: iinc 5 1
      //   127: lload 9
      //   129: lconst_1
      //   130: ladd
      //   131: lstore 9
      //   133: goto -56 -> 77
      //   136: astore_2
      //   137: aload_1
      //   138: monitorexit
      //   139: aload_2
      //   140: athrow
      //   141: iconst_0
      //   142: istore 5
      //   144: goto -87 -> 57
      //   147: astore 15
      //   149: aload 15
      //   151: invokestatic 96	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
      //   154: aload_1
      //   155: invokevirtual 99	rx/internal/operators/OperatorReplay$InnerProducer:unsubscribe	()V
      //   158: aload_0
      //   159: getfield 28	rx/internal/operators/OperatorReplay$UnboundedReplayBuffer:nl	Lrx/internal/operators/NotificationLite;
      //   162: aload 14
      //   164: invokevirtual 102	rx/internal/operators/NotificationLite:isError	(Ljava/lang/Object;)Z
      //   167: ifne +101 -> 268
      //   170: aload_0
      //   171: getfield 28	rx/internal/operators/OperatorReplay$UnboundedReplayBuffer:nl	Lrx/internal/operators/NotificationLite;
      //   174: aload 14
      //   176: invokevirtual 105	rx/internal/operators/NotificationLite:isCompleted	(Ljava/lang/Object;)Z
      //   179: ifne +89 -> 268
      //   182: aload 6
      //   184: aload 15
      //   186: aload_0
      //   187: getfield 28	rx/internal/operators/OperatorReplay$UnboundedReplayBuffer:nl	Lrx/internal/operators/NotificationLite;
      //   190: aload 14
      //   192: invokevirtual 108	rx/internal/operators/NotificationLite:getValue	(Ljava/lang/Object;)Ljava/lang/Object;
      //   195: invokestatic 114	rx/exceptions/OnErrorThrowable:addValueAsLastCause	(Ljava/lang/Throwable;Ljava/lang/Object;)Ljava/lang/Throwable;
      //   198: invokevirtual 119	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
      //   201: return
      //   202: lload 9
      //   204: lconst_0
      //   205: lcmp
      //   206: ifeq +28 -> 234
      //   209: aload_1
      //   210: iload 5
      //   212: invokestatic 123	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   215: putfield 126	rx/internal/operators/OperatorReplay$InnerProducer:index	Ljava/lang/Object;
      //   218: lload 7
      //   220: ldc2_w 127
      //   223: lcmp
      //   224: ifeq +10 -> 234
      //   227: aload_1
      //   228: lload 9
      //   230: invokevirtual 132	rx/internal/operators/OperatorReplay$InnerProducer:produced	(J)J
      //   233: pop2
      //   234: aload_1
      //   235: monitorenter
      //   236: aload_1
      //   237: getfield 63	rx/internal/operators/OperatorReplay$InnerProducer:missed	Z
      //   240: ifne +18 -> 258
      //   243: aload_1
      //   244: iconst_0
      //   245: putfield 60	rx/internal/operators/OperatorReplay$InnerProducer:emitting	Z
      //   248: aload_1
      //   249: monitorexit
      //   250: return
      //   251: astore 11
      //   253: aload_1
      //   254: monitorexit
      //   255: aload 11
      //   257: athrow
      //   258: aload_1
      //   259: iconst_0
      //   260: putfield 63	rx/internal/operators/OperatorReplay$InnerProducer:missed	Z
      //   263: aload_1
      //   264: monitorexit
      //   265: goto -241 -> 24
      //   268: return
      //
      // Exception table:
      //   from	to	target	type
      //   2	16	136	finally
      //   17	24	136	finally
      //   137	139	136	finally
      //   99	112	147	java/lang/Throwable
      //   236	250	251	finally
      //   253	255	251	finally
      //   258	265	251	finally
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorReplay
 * JD-Core Version:    0.6.0
 */