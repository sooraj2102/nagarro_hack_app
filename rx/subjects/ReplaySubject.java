package rx.subjects;

import TT;;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.internal.operators.BackpressureUtils;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

public final class ReplaySubject<T> extends Subject<T, T>
{
  private static final Object[] EMPTY_ARRAY = new Object[0];
  final ReplayState<T> state;

  ReplaySubject(ReplayState<T> paramReplayState)
  {
    super(paramReplayState);
    this.state = paramReplayState;
  }

  public static <T> ReplaySubject<T> create()
  {
    return create(16);
  }

  public static <T> ReplaySubject<T> create(int paramInt)
  {
    if (paramInt <= 0)
      throw new IllegalArgumentException("capacity > 0 required but it was " + paramInt);
    return new ReplaySubject(new ReplayState(new ReplayUnboundedBuffer(paramInt)));
  }

  static <T> ReplaySubject<T> createUnbounded()
  {
    return new ReplaySubject(new ReplayState(new ReplaySizeBoundBuffer(2147483647)));
  }

  static <T> ReplaySubject<T> createUnboundedTime()
  {
    return new ReplaySubject(new ReplayState(new ReplaySizeAndTimeBoundBuffer(2147483647, 9223372036854775807L, Schedulers.immediate())));
  }

  public static <T> ReplaySubject<T> createWithSize(int paramInt)
  {
    return new ReplaySubject(new ReplayState(new ReplaySizeBoundBuffer(paramInt)));
  }

  public static <T> ReplaySubject<T> createWithTime(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    return createWithTimeAndSize(paramLong, paramTimeUnit, 2147483647, paramScheduler);
  }

  public static <T> ReplaySubject<T> createWithTimeAndSize(long paramLong, TimeUnit paramTimeUnit, int paramInt, Scheduler paramScheduler)
  {
    return new ReplaySubject(new ReplayState(new ReplaySizeAndTimeBoundBuffer(paramInt, paramTimeUnit.toMillis(paramLong), paramScheduler)));
  }

  public Throwable getThrowable()
  {
    if (this.state.isTerminated())
      return this.state.buffer.error();
    return null;
  }

  public T getValue()
  {
    return this.state.buffer.last();
  }

  public Object[] getValues()
  {
    Object[] arrayOfObject = getValues((Object[])EMPTY_ARRAY);
    if (arrayOfObject == EMPTY_ARRAY)
      arrayOfObject = new Object[0];
    return arrayOfObject;
  }

  public T[] getValues(T[] paramArrayOfT)
  {
    return this.state.buffer.toArray(paramArrayOfT);
  }

  public boolean hasAnyValue()
  {
    return !this.state.buffer.isEmpty();
  }

  public boolean hasCompleted()
  {
    return (this.state.isTerminated()) && (this.state.buffer.error() == null);
  }

  public boolean hasObservers()
  {
    return ((ReplayProducer[])this.state.get()).length != 0;
  }

  public boolean hasThrowable()
  {
    return (this.state.isTerminated()) && (this.state.buffer.error() != null);
  }

  public boolean hasValue()
  {
    return hasAnyValue();
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

  public int size()
  {
    return this.state.buffer.size();
  }

  int subscriberCount()
  {
    return ((ReplayProducer[])this.state.get()).length;
  }

  static abstract interface ReplayBuffer<T>
  {
    public abstract void complete();

    public abstract void drain(ReplaySubject.ReplayProducer<T> paramReplayProducer);

    public abstract Throwable error();

    public abstract void error(Throwable paramThrowable);

    public abstract boolean isComplete();

    public abstract boolean isEmpty();

    public abstract T last();

    public abstract void next(T paramT);

    public abstract int size();

    public abstract T[] toArray(T[] paramArrayOfT);
  }

  static final class ReplayProducer<T> extends AtomicInteger
    implements Producer, Subscription
  {
    private static final long serialVersionUID = -5006209596735204567L;
    final Subscriber<? super T> actual;
    int index;
    Object node;
    final AtomicLong requested;
    final ReplaySubject.ReplayState<T> state;
    int tailIndex;

    public ReplayProducer(Subscriber<? super T> paramSubscriber, ReplaySubject.ReplayState<T> paramReplayState)
    {
      this.actual = paramSubscriber;
      this.requested = new AtomicLong();
      this.state = paramReplayState;
    }

    public boolean isUnsubscribed()
    {
      return this.actual.isUnsubscribed();
    }

    public void request(long paramLong)
    {
      if (paramLong > 0L)
      {
        BackpressureUtils.getAndAddRequest(this.requested, paramLong);
        this.state.buffer.drain(this);
      }
      do
        return;
      while (paramLong >= 0L);
      throw new IllegalArgumentException("n >= required but it was " + paramLong);
    }

    public void unsubscribe()
    {
      this.state.remove(this);
    }
  }

  static final class ReplaySizeAndTimeBoundBuffer<T>
    implements ReplaySubject.ReplayBuffer<T>
  {
    volatile boolean done;
    Throwable error;
    volatile TimedNode<T> head;
    final int limit;
    final long maxAgeMillis;
    final Scheduler scheduler;
    int size;
    TimedNode<T> tail;

    public ReplaySizeAndTimeBoundBuffer(int paramInt, long paramLong, Scheduler paramScheduler)
    {
      this.limit = paramInt;
      TimedNode localTimedNode = new TimedNode(null, 0L);
      this.tail = localTimedNode;
      this.head = localTimedNode;
      this.maxAgeMillis = paramLong;
      this.scheduler = paramScheduler;
    }

    public void complete()
    {
      evictFinal();
      this.done = true;
    }

    public void drain(ReplaySubject.ReplayProducer<T> paramReplayProducer)
    {
      if (paramReplayProducer.getAndIncrement() != 0)
        return;
      Subscriber localSubscriber = paramReplayProducer.actual;
      int i = 1;
      label184: 
      do
      {
        long l1 = paramReplayProducer.requested.get();
        long l2 = 0L;
        Object localObject = (TimedNode)paramReplayProducer.node;
        if (localObject == null)
          localObject = latestHead();
        while (true)
        {
          TimedNode localTimedNode;
          if (l2 != l1)
          {
            if (localSubscriber.isUnsubscribed())
            {
              paramReplayProducer.node = null;
              return;
            }
            boolean bool2 = this.done;
            localTimedNode = (TimedNode)((TimedNode)localObject).get();
            int k;
            if (localTimedNode == null)
              k = 1;
            while ((bool2) && (k != 0))
            {
              paramReplayProducer.node = null;
              Throwable localThrowable2 = this.error;
              if (localThrowable2 != null)
              {
                localSubscriber.onError(localThrowable2);
                return;
                k = 0;
                continue;
              }
              localSubscriber.onCompleted();
              return;
            }
            if (k == 0);
          }
          else
          {
            if (l2 != l1)
              break;
            if (!localSubscriber.isUnsubscribed())
              break label184;
            paramReplayProducer.node = null;
            return;
          }
          localSubscriber.onNext(localTimedNode.value);
          l2 += 1L;
          localObject = localTimedNode;
          continue;
          boolean bool1 = this.done;
          int j;
          if (((TimedNode)localObject).get() == null)
            j = 1;
          while ((bool1) && (j != 0))
          {
            paramReplayProducer.node = null;
            Throwable localThrowable1 = this.error;
            if (localThrowable1 != null)
            {
              localSubscriber.onError(localThrowable1);
              return;
              j = 0;
              continue;
            }
            localSubscriber.onCompleted();
            return;
          }
        }
        if ((l2 != 0L) && (l1 != 9223372036854775807L))
          BackpressureUtils.produced(paramReplayProducer.requested, l2);
        paramReplayProducer.node = localObject;
        i = paramReplayProducer.addAndGet(-i);
      }
      while (i != 0);
    }

    public Throwable error()
    {
      return this.error;
    }

    public void error(Throwable paramThrowable)
    {
      evictFinal();
      this.error = paramThrowable;
      this.done = true;
    }

    void evictFinal()
    {
      long l = this.scheduler.now() - this.maxAgeMillis;
      TimedNode localTimedNode1 = this.head;
      TimedNode localTimedNode2;
      for (Object localObject = localTimedNode1; ; localObject = localTimedNode2)
      {
        localTimedNode2 = (TimedNode)((TimedNode)localObject).get();
        if ((localTimedNode2 != null) && (localTimedNode2.timestamp <= l))
          continue;
        if (localTimedNode1 != localObject)
          this.head = ((TimedNode)localObject);
        return;
      }
    }

    public boolean isComplete()
    {
      return this.done;
    }

    public boolean isEmpty()
    {
      return latestHead().get() == null;
    }

    public T last()
    {
      TimedNode localTimedNode;
      for (Object localObject = latestHead(); ; localObject = localTimedNode)
      {
        localTimedNode = (TimedNode)((TimedNode)localObject).get();
        if (localTimedNode == null)
          break;
      }
      return (TT)((TimedNode)localObject).value;
    }

    TimedNode<T> latestHead()
    {
      long l = this.scheduler.now() - this.maxAgeMillis;
      TimedNode localTimedNode;
      for (Object localObject = this.head; ; localObject = localTimedNode)
      {
        localTimedNode = (TimedNode)((TimedNode)localObject).get();
        if ((localTimedNode == null) || (localTimedNode.timestamp > l))
          return localObject;
      }
    }

    public void next(T paramT)
    {
      long l1 = this.scheduler.now();
      TimedNode localTimedNode1 = new TimedNode(paramT, l1);
      this.tail.set(localTimedNode1);
      this.tail = localTimedNode1;
      long l2 = l1 - this.maxAgeMillis;
      int i = this.size;
      TimedNode localTimedNode2 = this.head;
      Object localObject = localTimedNode2;
      if (i == this.limit)
        localObject = (TimedNode)((TimedNode)localObject).get();
      while (true)
      {
        TimedNode localTimedNode3 = (TimedNode)((TimedNode)localObject).get();
        if ((localTimedNode3 == null) || (localTimedNode3.timestamp > l2))
        {
          this.size = i;
          if (localObject != localTimedNode2)
            this.head = ((TimedNode)localObject);
          return;
          i++;
          continue;
        }
        localObject = localTimedNode3;
        i--;
      }
    }

    public int size()
    {
      int i = 0;
      TimedNode localTimedNode = (TimedNode)latestHead().get();
      while ((localTimedNode != null) && (i != 2147483647))
      {
        localTimedNode = (TimedNode)localTimedNode.get();
        i++;
      }
      return i;
    }

    public T[] toArray(T[] paramArrayOfT)
    {
      ArrayList localArrayList = new ArrayList();
      for (TimedNode localTimedNode = (TimedNode)latestHead().get(); localTimedNode != null; localTimedNode = (TimedNode)localTimedNode.get())
        localArrayList.add(localTimedNode.value);
      return localArrayList.toArray(paramArrayOfT);
    }

    static final class TimedNode<T> extends AtomicReference<TimedNode<T>>
    {
      private static final long serialVersionUID = 3713592843205853725L;
      final long timestamp;
      final T value;

      public TimedNode(T paramT, long paramLong)
      {
        this.value = paramT;
        this.timestamp = paramLong;
      }
    }
  }

  static final class ReplaySizeBoundBuffer<T>
    implements ReplaySubject.ReplayBuffer<T>
  {
    volatile boolean done;
    Throwable error;
    volatile Node<T> head;
    final int limit;
    int size;
    Node<T> tail;

    public ReplaySizeBoundBuffer(int paramInt)
    {
      this.limit = paramInt;
      Node localNode = new Node(null);
      this.tail = localNode;
      this.head = localNode;
    }

    public void complete()
    {
      this.done = true;
    }

    public void drain(ReplaySubject.ReplayProducer<T> paramReplayProducer)
    {
      if (paramReplayProducer.getAndIncrement() != 0)
        return;
      Subscriber localSubscriber = paramReplayProducer.actual;
      int i = 1;
      label184: 
      do
      {
        long l1 = paramReplayProducer.requested.get();
        long l2 = 0L;
        Object localObject = (Node)paramReplayProducer.node;
        if (localObject == null)
          localObject = this.head;
        while (true)
        {
          Node localNode;
          if (l2 != l1)
          {
            if (localSubscriber.isUnsubscribed())
            {
              paramReplayProducer.node = null;
              return;
            }
            boolean bool2 = this.done;
            localNode = (Node)((Node)localObject).get();
            int k;
            if (localNode == null)
              k = 1;
            while ((bool2) && (k != 0))
            {
              paramReplayProducer.node = null;
              Throwable localThrowable2 = this.error;
              if (localThrowable2 != null)
              {
                localSubscriber.onError(localThrowable2);
                return;
                k = 0;
                continue;
              }
              localSubscriber.onCompleted();
              return;
            }
            if (k == 0);
          }
          else
          {
            if (l2 != l1)
              break;
            if (!localSubscriber.isUnsubscribed())
              break label184;
            paramReplayProducer.node = null;
            return;
          }
          localSubscriber.onNext(localNode.value);
          l2 += 1L;
          localObject = localNode;
          continue;
          boolean bool1 = this.done;
          int j;
          if (((Node)localObject).get() == null)
            j = 1;
          while ((bool1) && (j != 0))
          {
            paramReplayProducer.node = null;
            Throwable localThrowable1 = this.error;
            if (localThrowable1 != null)
            {
              localSubscriber.onError(localThrowable1);
              return;
              j = 0;
              continue;
            }
            localSubscriber.onCompleted();
            return;
          }
        }
        if ((l2 != 0L) && (l1 != 9223372036854775807L))
          BackpressureUtils.produced(paramReplayProducer.requested, l2);
        paramReplayProducer.node = localObject;
        i = paramReplayProducer.addAndGet(-i);
      }
      while (i != 0);
    }

    public Throwable error()
    {
      return this.error;
    }

    public void error(Throwable paramThrowable)
    {
      this.error = paramThrowable;
      this.done = true;
    }

    public boolean isComplete()
    {
      return this.done;
    }

    public boolean isEmpty()
    {
      return this.head.get() == null;
    }

    public T last()
    {
      Node localNode;
      for (Object localObject = this.head; ; localObject = localNode)
      {
        localNode = (Node)((Node)localObject).get();
        if (localNode == null)
          break;
      }
      return (TT)((Node)localObject).value;
    }

    public void next(T paramT)
    {
      Node localNode = new Node(paramT);
      this.tail.set(localNode);
      this.tail = localNode;
      int i = this.size;
      if (i == this.limit)
      {
        this.head = ((Node)this.head.get());
        return;
      }
      this.size = (i + 1);
    }

    public int size()
    {
      int i = 0;
      Node localNode = (Node)this.head.get();
      while ((localNode != null) && (i != 2147483647))
      {
        localNode = (Node)localNode.get();
        i++;
      }
      return i;
    }

    public T[] toArray(T[] paramArrayOfT)
    {
      ArrayList localArrayList = new ArrayList();
      for (Node localNode = (Node)this.head.get(); localNode != null; localNode = (Node)localNode.get())
        localArrayList.add(localNode.value);
      return localArrayList.toArray(paramArrayOfT);
    }

    static final class Node<T> extends AtomicReference<Node<T>>
    {
      private static final long serialVersionUID = 3713592843205853725L;
      final T value;

      public Node(T paramT)
      {
        this.value = paramT;
      }
    }
  }

  static final class ReplayState<T> extends AtomicReference<ReplaySubject.ReplayProducer<T>[]>
    implements Observable.OnSubscribe<T>, Observer<T>
  {
    static final ReplaySubject.ReplayProducer[] EMPTY = new ReplaySubject.ReplayProducer[0];
    static final ReplaySubject.ReplayProducer[] TERMINATED = new ReplaySubject.ReplayProducer[0];
    private static final long serialVersionUID = 5952362471246910544L;
    final ReplaySubject.ReplayBuffer<T> buffer;

    public ReplayState(ReplaySubject.ReplayBuffer<T> paramReplayBuffer)
    {
      this.buffer = paramReplayBuffer;
      lazySet(EMPTY);
    }

    boolean add(ReplaySubject.ReplayProducer<T> paramReplayProducer)
    {
      ReplaySubject.ReplayProducer[] arrayOfReplayProducer1;
      ReplaySubject.ReplayProducer[] arrayOfReplayProducer2;
      do
      {
        arrayOfReplayProducer1 = (ReplaySubject.ReplayProducer[])get();
        if (arrayOfReplayProducer1 == TERMINATED)
          return false;
        int i = arrayOfReplayProducer1.length;
        arrayOfReplayProducer2 = new ReplaySubject.ReplayProducer[i + 1];
        System.arraycopy(arrayOfReplayProducer1, 0, arrayOfReplayProducer2, 0, i);
        arrayOfReplayProducer2[i] = paramReplayProducer;
      }
      while (!compareAndSet(arrayOfReplayProducer1, arrayOfReplayProducer2));
      return true;
    }

    public void call(Subscriber<? super T> paramSubscriber)
    {
      ReplaySubject.ReplayProducer localReplayProducer = new ReplaySubject.ReplayProducer(paramSubscriber, this);
      paramSubscriber.add(localReplayProducer);
      paramSubscriber.setProducer(localReplayProducer);
      if ((add(localReplayProducer)) && (localReplayProducer.isUnsubscribed()))
      {
        remove(localReplayProducer);
        return;
      }
      this.buffer.drain(localReplayProducer);
    }

    boolean isTerminated()
    {
      return get() == TERMINATED;
    }

    public void onCompleted()
    {
      ReplaySubject.ReplayBuffer localReplayBuffer = this.buffer;
      localReplayBuffer.complete();
      ReplaySubject.ReplayProducer[] arrayOfReplayProducer = (ReplaySubject.ReplayProducer[])getAndSet(TERMINATED);
      int i = arrayOfReplayProducer.length;
      for (int j = 0; j < i; j++)
        localReplayBuffer.drain(arrayOfReplayProducer[j]);
    }

    public void onError(Throwable paramThrowable)
    {
      ReplaySubject.ReplayBuffer localReplayBuffer = this.buffer;
      localReplayBuffer.error(paramThrowable);
      ArrayList localArrayList = null;
      ReplaySubject.ReplayProducer[] arrayOfReplayProducer = (ReplaySubject.ReplayProducer[])getAndSet(TERMINATED);
      int i = arrayOfReplayProducer.length;
      int j = 0;
      while (true)
        if (j < i)
        {
          ReplaySubject.ReplayProducer localReplayProducer = arrayOfReplayProducer[j];
          try
          {
            localReplayBuffer.drain(localReplayProducer);
            j++;
          }
          catch (Throwable localThrowable)
          {
            while (true)
            {
              if (localArrayList == null)
                localArrayList = new ArrayList();
              localArrayList.add(localThrowable);
            }
          }
        }
      Exceptions.throwIfAny(localArrayList);
    }

    public void onNext(T paramT)
    {
      ReplaySubject.ReplayBuffer localReplayBuffer = this.buffer;
      localReplayBuffer.next(paramT);
      ReplaySubject.ReplayProducer[] arrayOfReplayProducer = (ReplaySubject.ReplayProducer[])get();
      int i = arrayOfReplayProducer.length;
      for (int j = 0; j < i; j++)
        localReplayBuffer.drain(arrayOfReplayProducer[j]);
    }

    void remove(ReplaySubject.ReplayProducer<T> paramReplayProducer)
    {
      ReplaySubject.ReplayProducer[] arrayOfReplayProducer1 = (ReplaySubject.ReplayProducer[])get();
      if ((arrayOfReplayProducer1 == TERMINATED) || (arrayOfReplayProducer1 == EMPTY));
      int i;
      int j;
      int k;
      label32: ReplaySubject.ReplayProducer[] arrayOfReplayProducer2;
      while (true)
      {
        return;
        i = arrayOfReplayProducer1.length;
        j = -1;
        k = 0;
        if (k < i)
        {
          if (arrayOfReplayProducer1[k] != paramReplayProducer)
            break;
          j = k;
        }
        else
        {
          if (j < 0)
            continue;
          if (i != 1)
            break label82;
          arrayOfReplayProducer2 = EMPTY;
        }
      }
      while (compareAndSet(arrayOfReplayProducer1, arrayOfReplayProducer2))
      {
        return;
        k++;
        break label32;
        label82: arrayOfReplayProducer2 = new ReplaySubject.ReplayProducer[i - 1];
        System.arraycopy(arrayOfReplayProducer1, 0, arrayOfReplayProducer2, 0, j);
        System.arraycopy(arrayOfReplayProducer1, j + 1, arrayOfReplayProducer2, j, -1 + (i - j));
      }
    }
  }

  static final class ReplayUnboundedBuffer<T>
    implements ReplaySubject.ReplayBuffer<T>
  {
    final int capacity;
    volatile boolean done;
    Throwable error;
    final Object[] head;
    volatile int size;
    Object[] tail;
    int tailIndex;

    public ReplayUnboundedBuffer(int paramInt)
    {
      this.capacity = paramInt;
      Object[] arrayOfObject = new Object[paramInt + 1];
      this.head = arrayOfObject;
      this.tail = arrayOfObject;
    }

    public void complete()
    {
      this.done = true;
    }

    public void drain(ReplaySubject.ReplayProducer<T> paramReplayProducer)
    {
      if (paramReplayProducer.getAndIncrement() != 0)
        return;
      int i = 1;
      Subscriber localSubscriber = paramReplayProducer.actual;
      int j = this.capacity;
      label224: 
      do
      {
        long l1 = paramReplayProducer.requested.get();
        long l2 = 0L;
        Object[] arrayOfObject = (Object[])(Object[])paramReplayProducer.node;
        if (arrayOfObject == null)
          arrayOfObject = this.head;
        int k = paramReplayProducer.tailIndex;
        int m = paramReplayProducer.index;
        while (true)
        {
          if (l2 != l1)
          {
            if (localSubscriber.isUnsubscribed())
            {
              paramReplayProducer.node = null;
              return;
            }
            boolean bool2 = this.done;
            int i1;
            if (m == this.size)
              i1 = 1;
            while ((bool2) && (i1 != 0))
            {
              paramReplayProducer.node = null;
              Throwable localThrowable2 = this.error;
              if (localThrowable2 != null)
              {
                localSubscriber.onError(localThrowable2);
                return;
                i1 = 0;
                continue;
              }
              localSubscriber.onCompleted();
              return;
            }
            if (i1 == 0);
          }
          else
          {
            if (l2 != l1)
              break;
            if (!localSubscriber.isUnsubscribed())
              break label224;
            paramReplayProducer.node = null;
            return;
          }
          if (k == j)
          {
            arrayOfObject = (Object[])(Object[])arrayOfObject[k];
            k = 0;
          }
          localSubscriber.onNext(arrayOfObject[k]);
          l2 += 1L;
          k++;
          m++;
          continue;
          boolean bool1 = this.done;
          int n;
          if (m == this.size)
            n = 1;
          while ((bool1) && (n != 0))
          {
            paramReplayProducer.node = null;
            Throwable localThrowable1 = this.error;
            if (localThrowable1 != null)
            {
              localSubscriber.onError(localThrowable1);
              return;
              n = 0;
              continue;
            }
            localSubscriber.onCompleted();
            return;
          }
        }
        if ((l2 != 0L) && (l1 != 9223372036854775807L))
          BackpressureUtils.produced(paramReplayProducer.requested, l2);
        paramReplayProducer.index = m;
        paramReplayProducer.tailIndex = k;
        paramReplayProducer.node = arrayOfObject;
        i = paramReplayProducer.addAndGet(-i);
      }
      while (i != 0);
    }

    public Throwable error()
    {
      return this.error;
    }

    public void error(Throwable paramThrowable)
    {
      if (this.done)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.error = paramThrowable;
      this.done = true;
    }

    public boolean isComplete()
    {
      return this.done;
    }

    public boolean isEmpty()
    {
      return this.size == 0;
    }

    public T last()
    {
      int i = this.size;
      if (i == 0)
        return null;
      Object[] arrayOfObject = this.head;
      int j = this.capacity;
      while (i >= j)
      {
        arrayOfObject = (Object[])(Object[])arrayOfObject[j];
        i -= j;
      }
      return arrayOfObject[(i - 1)];
    }

    public void next(T paramT)
    {
      if (this.done)
        return;
      int i = this.tailIndex;
      Object[] arrayOfObject1 = this.tail;
      if (i == -1 + arrayOfObject1.length)
      {
        Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
        arrayOfObject2[0] = paramT;
        this.tailIndex = 1;
        arrayOfObject1[i] = arrayOfObject2;
        this.tail = arrayOfObject2;
      }
      while (true)
      {
        this.size = (1 + this.size);
        return;
        arrayOfObject1[i] = paramT;
        this.tailIndex = (i + 1);
      }
    }

    public int size()
    {
      return this.size;
    }

    public T[] toArray(T[] paramArrayOfT)
    {
      int i = this.size;
      if (paramArrayOfT.length < i)
        paramArrayOfT = (Object[])(Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
      Object[] arrayOfObject = this.head;
      int j = this.capacity;
      int k = 0;
      while (k + j < i)
      {
        System.arraycopy(arrayOfObject, 0, paramArrayOfT, k, j);
        k += j;
        arrayOfObject = (Object[])(Object[])arrayOfObject[j];
      }
      System.arraycopy(arrayOfObject, 0, paramArrayOfT, k, i - k);
      if (paramArrayOfT.length > i)
        paramArrayOfT[i] = null;
      return paramArrayOfT;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.subjects.ReplaySubject
 * JD-Core Version:    0.6.0
 */