package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.producers.ProducerArbiter;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.UtilityFunctions;
import rx.observables.GroupedObservable;
import rx.observers.Subscribers;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.Subscriptions;

public final class OperatorGroupBy<T, K, V>
  implements Observable.Operator<GroupedObservable<K, V>, T>
{
  final int bufferSize;
  final boolean delayError;
  final Func1<? super T, ? extends K> keySelector;
  final Func1<Action1<K>, Map<K, Object>> mapFactory;
  final Func1<? super T, ? extends V> valueSelector;

  public OperatorGroupBy(Func1<? super T, ? extends K> paramFunc1)
  {
    this(paramFunc1, UtilityFunctions.identity(), RxRingBuffer.SIZE, false, null);
  }

  public OperatorGroupBy(Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11)
  {
    this(paramFunc1, paramFunc11, RxRingBuffer.SIZE, false, null);
  }

  public OperatorGroupBy(Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11, int paramInt, boolean paramBoolean, Func1<Action1<K>, Map<K, Object>> paramFunc12)
  {
    this.keySelector = paramFunc1;
    this.valueSelector = paramFunc11;
    this.bufferSize = paramInt;
    this.delayError = paramBoolean;
    this.mapFactory = paramFunc12;
  }

  public OperatorGroupBy(Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11, Func1<Action1<K>, Map<K, Object>> paramFunc12)
  {
    this(paramFunc1, paramFunc11, RxRingBuffer.SIZE, false, paramFunc12);
  }

  public Subscriber<? super T> call(Subscriber<? super GroupedObservable<K, V>> paramSubscriber)
  {
    Subscriber localSubscriber;
    try
    {
      GroupBySubscriber localGroupBySubscriber = new GroupBySubscriber(paramSubscriber, this.keySelector, this.valueSelector, this.bufferSize, this.delayError, this.mapFactory);
      paramSubscriber.add(Subscriptions.create(new Action0(localGroupBySubscriber)
      {
        public void call()
        {
          this.val$parent.cancel();
        }
      }));
      paramSubscriber.setProducer(localGroupBySubscriber.producer);
      return localGroupBySubscriber;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwOrReport(localThrowable, paramSubscriber);
      localSubscriber = Subscribers.empty();
      localSubscriber.unsubscribe();
    }
    return localSubscriber;
  }

  public static final class GroupByProducer
    implements Producer
  {
    final OperatorGroupBy.GroupBySubscriber<?, ?, ?> parent;

    public GroupByProducer(OperatorGroupBy.GroupBySubscriber<?, ?, ?> paramGroupBySubscriber)
    {
      this.parent = paramGroupBySubscriber;
    }

    public void request(long paramLong)
    {
      this.parent.requestMore(paramLong);
    }
  }

  public static final class GroupBySubscriber<T, K, V> extends Subscriber<T>
  {
    static final Object NULL_KEY = new Object();
    final Subscriber<? super GroupedObservable<K, V>> actual;
    final int bufferSize;
    final AtomicBoolean cancelled;
    final boolean delayError;
    volatile boolean done;
    Throwable error;
    final Queue<K> evictedKeys;
    final AtomicInteger groupCount;
    final Map<Object, OperatorGroupBy.GroupedUnicast<K, V>> groups;
    final Func1<? super T, ? extends K> keySelector;
    final OperatorGroupBy.GroupByProducer producer;
    final Queue<GroupedObservable<K, V>> queue;
    final AtomicLong requested;
    final ProducerArbiter s;
    final Func1<? super T, ? extends V> valueSelector;
    final AtomicInteger wip;

    public GroupBySubscriber(Subscriber<? super GroupedObservable<K, V>> paramSubscriber, Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11, int paramInt, boolean paramBoolean, Func1<Action1<K>, Map<K, Object>> paramFunc12)
    {
      this.actual = paramSubscriber;
      this.keySelector = paramFunc1;
      this.valueSelector = paramFunc11;
      this.bufferSize = paramInt;
      this.delayError = paramBoolean;
      this.queue = new ConcurrentLinkedQueue();
      this.s = new ProducerArbiter();
      this.s.request(paramInt);
      this.producer = new OperatorGroupBy.GroupByProducer(this);
      this.cancelled = new AtomicBoolean();
      this.requested = new AtomicLong();
      this.groupCount = new AtomicInteger(1);
      this.wip = new AtomicInteger();
      if (paramFunc12 == null)
      {
        this.groups = new ConcurrentHashMap();
        this.evictedKeys = null;
        return;
      }
      this.evictedKeys = new ConcurrentLinkedQueue();
      this.groups = createMap(paramFunc12, new EvictionAction(this.evictedKeys));
    }

    private Map<Object, OperatorGroupBy.GroupedUnicast<K, V>> createMap(Func1<Action1<K>, Map<K, Object>> paramFunc1, Action1<K> paramAction1)
    {
      return (Map)paramFunc1.call(paramAction1);
    }

    public void cancel()
    {
      if ((this.cancelled.compareAndSet(false, true)) && (this.groupCount.decrementAndGet() == 0))
        unsubscribe();
    }

    public void cancel(K paramK)
    {
      if (paramK != null);
      for (Object localObject = paramK; ; localObject = NULL_KEY)
      {
        if ((this.groups.remove(localObject) != null) && (this.groupCount.decrementAndGet() == 0))
          unsubscribe();
        return;
      }
    }

    boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2, Subscriber<? super GroupedObservable<K, V>> paramSubscriber, Queue<?> paramQueue)
    {
      if (paramBoolean1)
      {
        Throwable localThrowable = this.error;
        if (localThrowable != null)
        {
          errorAll(paramSubscriber, paramQueue, localThrowable);
          return true;
        }
        if (paramBoolean2)
        {
          this.actual.onCompleted();
          return true;
        }
      }
      return false;
    }

    void drain()
    {
      if (this.wip.getAndIncrement() != 0)
        label10: return;
      int i;
      Queue localQueue;
      Subscriber localSubscriber;
      while (checkTerminated(this.done, localQueue.isEmpty(), localSubscriber, localQueue))
      {
        i = 1;
        localQueue = this.queue;
        localSubscriber = this.actual;
      }
      long l1 = this.requested.get();
      long l2 = 0L;
      while (true)
      {
        boolean bool1;
        GroupedObservable localGroupedObservable;
        if (l2 != l1)
        {
          bool1 = this.done;
          localGroupedObservable = (GroupedObservable)localQueue.poll();
          if (localGroupedObservable != null)
            break label155;
        }
        label155: for (boolean bool2 = true; ; bool2 = false)
        {
          if (checkTerminated(bool1, bool2, localSubscriber, localQueue))
            break label159;
          if (!bool2)
            break label161;
          if (l2 != 0L)
          {
            if (l1 != 9223372036854775807L)
              BackpressureUtils.produced(this.requested, l2);
            this.s.request(l2);
          }
          i = this.wip.addAndGet(-i);
          if (i != 0)
            break;
          return;
        }
        label159: break label10;
        label161: localSubscriber.onNext(localGroupedObservable);
        l2 += 1L;
      }
    }

    void errorAll(Subscriber<? super GroupedObservable<K, V>> paramSubscriber, Queue<?> paramQueue, Throwable paramThrowable)
    {
      paramQueue.clear();
      ArrayList localArrayList = new ArrayList(this.groups.values());
      this.groups.clear();
      if (this.evictedKeys != null)
        this.evictedKeys.clear();
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
        ((OperatorGroupBy.GroupedUnicast)localIterator.next()).onError(paramThrowable);
      paramSubscriber.onError(paramThrowable);
    }

    public void onCompleted()
    {
      if (this.done)
        return;
      Iterator localIterator = this.groups.values().iterator();
      while (localIterator.hasNext())
        ((OperatorGroupBy.GroupedUnicast)localIterator.next()).onComplete();
      this.groups.clear();
      if (this.evictedKeys != null)
        this.evictedKeys.clear();
      this.done = true;
      this.groupCount.decrementAndGet();
      drain();
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.done)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.error = paramThrowable;
      this.done = true;
      this.groupCount.decrementAndGet();
      drain();
    }

    // ERROR //
    public void onNext(T paramT)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 160	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:done	Z
      //   4: ifeq +4 -> 8
      //   7: return
      //   8: aload_0
      //   9: getfield 67	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:queue	Ljava/util/Queue;
      //   12: astore_2
      //   13: aload_0
      //   14: getfield 54	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:actual	Lrx/Subscriber;
      //   17: astore_3
      //   18: aload_0
      //   19: getfield 56	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:keySelector	Lrx/functions/Func1;
      //   22: aload_1
      //   23: invokeinterface 125 2 0
      //   28: astore 5
      //   30: iconst_1
      //   31: istore 6
      //   33: aload 5
      //   35: ifnull +178 -> 213
      //   38: aload 5
      //   40: astore 7
      //   42: aload_0
      //   43: getfield 108	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:groups	Ljava/util/Map;
      //   46: aload 7
      //   48: invokeinterface 242 2 0
      //   53: checkcast 222	rx/internal/operators/OperatorGroupBy$GroupedUnicast
      //   56: astore 8
      //   58: aload 8
      //   60: ifnonnull +67 -> 127
      //   63: aload_0
      //   64: getfield 88	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:cancelled	Ljava/util/concurrent/atomic/AtomicBoolean;
      //   67: invokevirtual 244	java/util/concurrent/atomic/AtomicBoolean:get	()Z
      //   70: ifne -63 -> 7
      //   73: aload 5
      //   75: aload_0
      //   76: getfield 60	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:bufferSize	I
      //   79: aload_0
      //   80: aload_0
      //   81: getfield 62	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:delayError	Z
      //   84: invokestatic 248	rx/internal/operators/OperatorGroupBy$GroupedUnicast:createWith	(Ljava/lang/Object;ILrx/internal/operators/OperatorGroupBy$GroupBySubscriber;Z)Lrx/internal/operators/OperatorGroupBy$GroupedUnicast;
      //   87: astore 8
      //   89: aload_0
      //   90: getfield 108	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:groups	Ljava/util/Map;
      //   93: aload 7
      //   95: aload 8
      //   97: invokeinterface 252 3 0
      //   102: pop
      //   103: aload_0
      //   104: getfield 100	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:groupCount	Ljava/util/concurrent/atomic/AtomicInteger;
      //   107: invokevirtual 158	java/util/concurrent/atomic/AtomicInteger:getAndIncrement	()I
      //   110: pop
      //   111: iconst_0
      //   112: istore 6
      //   114: aload_2
      //   115: aload 8
      //   117: invokeinterface 256 2 0
      //   122: pop
      //   123: aload_0
      //   124: invokevirtual 235	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:drain	()V
      //   127: aload_0
      //   128: getfield 58	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:valueSelector	Lrx/functions/Func1;
      //   131: aload_1
      //   132: invokeinterface 125 2 0
      //   137: astore 10
      //   139: aload 8
      //   141: aload 10
      //   143: invokevirtual 257	rx/internal/operators/OperatorGroupBy$GroupedUnicast:onNext	(Ljava/lang/Object;)V
      //   146: aload_0
      //   147: getfield 110	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:evictedKeys	Ljava/util/Queue;
      //   150: ifnull +86 -> 236
      //   153: aload_0
      //   154: getfield 110	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:evictedKeys	Ljava/util/Queue;
      //   157: invokeinterface 176 1 0
      //   162: astore 11
      //   164: aload 11
      //   166: ifnull +70 -> 236
      //   169: aload_0
      //   170: getfield 108	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:groups	Ljava/util/Map;
      //   173: aload 11
      //   175: invokeinterface 242 2 0
      //   180: checkcast 222	rx/internal/operators/OperatorGroupBy$GroupedUnicast
      //   183: astore 12
      //   185: aload 12
      //   187: ifnull -34 -> 153
      //   190: aload 12
      //   192: invokevirtual 233	rx/internal/operators/OperatorGroupBy$GroupedUnicast:onComplete	()V
      //   195: goto -42 -> 153
      //   198: astore 4
      //   200: aload_0
      //   201: invokevirtual 139	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:unsubscribe	()V
      //   204: aload_0
      //   205: aload_3
      //   206: aload_2
      //   207: aload 4
      //   209: invokevirtual 151	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:errorAll	(Lrx/Subscriber;Ljava/util/Queue;Ljava/lang/Throwable;)V
      //   212: return
      //   213: getstatic 50	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:NULL_KEY	Ljava/lang/Object;
      //   216: astore 7
      //   218: goto -176 -> 42
      //   221: astore 9
      //   223: aload_0
      //   224: invokevirtual 139	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:unsubscribe	()V
      //   227: aload_0
      //   228: aload_3
      //   229: aload_2
      //   230: aload 9
      //   232: invokevirtual 151	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:errorAll	(Lrx/Subscriber;Ljava/util/Queue;Ljava/lang/Throwable;)V
      //   235: return
      //   236: iload 6
      //   238: ifeq -231 -> 7
      //   241: aload_0
      //   242: getfield 72	rx/internal/operators/OperatorGroupBy$GroupBySubscriber:s	Lrx/internal/producers/ProducerArbiter;
      //   245: lconst_1
      //   246: invokevirtual 76	rx/internal/producers/ProducerArbiter:request	(J)V
      //   249: return
      //
      // Exception table:
      //   from	to	target	type
      //   18	30	198	java/lang/Throwable
      //   127	139	221	java/lang/Throwable
    }

    public void requestMore(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
      BackpressureUtils.getAndAddRequest(this.requested, paramLong);
      drain();
    }

    public void setProducer(Producer paramProducer)
    {
      this.s.setProducer(paramProducer);
    }

    static class EvictionAction<K>
      implements Action1<K>
    {
      final Queue<K> evictedKeys;

      EvictionAction(Queue<K> paramQueue)
      {
        this.evictedKeys = paramQueue;
      }

      public void call(K paramK)
      {
        this.evictedKeys.offer(paramK);
      }
    }
  }

  static final class GroupedUnicast<K, T> extends GroupedObservable<K, T>
  {
    final OperatorGroupBy.State<T, K> state;

    protected GroupedUnicast(K paramK, OperatorGroupBy.State<T, K> paramState)
    {
      super(paramState);
      this.state = paramState;
    }

    public static <T, K> GroupedUnicast<K, T> createWith(K paramK, int paramInt, OperatorGroupBy.GroupBySubscriber<?, K, T> paramGroupBySubscriber, boolean paramBoolean)
    {
      return new GroupedUnicast(paramK, new OperatorGroupBy.State(paramInt, paramGroupBySubscriber, paramK, paramBoolean));
    }

    public void onComplete()
    {
      this.state.onComplete();
    }

    public void onError(Throwable paramThrowable)
    {
      this.state.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      this.state.onNext(paramT);
    }
  }

  static final class State<T, K> extends AtomicInteger
    implements Producer, Subscription, Observable.OnSubscribe<T>
  {
    private static final long serialVersionUID = -3852313036005250360L;
    final AtomicReference<Subscriber<? super T>> actual;
    final AtomicBoolean cancelled;
    final boolean delayError;
    volatile boolean done;
    Throwable error;
    final K key;
    final AtomicBoolean once;
    final OperatorGroupBy.GroupBySubscriber<?, K, T> parent;
    final Queue<Object> queue = new ConcurrentLinkedQueue();
    final AtomicLong requested;

    public State(int paramInt, OperatorGroupBy.GroupBySubscriber<?, K, T> paramGroupBySubscriber, K paramK, boolean paramBoolean)
    {
      this.parent = paramGroupBySubscriber;
      this.key = paramK;
      this.delayError = paramBoolean;
      this.cancelled = new AtomicBoolean();
      this.actual = new AtomicReference();
      this.once = new AtomicBoolean();
      this.requested = new AtomicLong();
    }

    public void call(Subscriber<? super T> paramSubscriber)
    {
      if (this.once.compareAndSet(false, true))
      {
        paramSubscriber.add(this);
        paramSubscriber.setProducer(this);
        this.actual.lazySet(paramSubscriber);
        drain();
        return;
      }
      paramSubscriber.onError(new IllegalStateException("Only one Subscriber allowed!"));
    }

    boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2, Subscriber<? super T> paramSubscriber, boolean paramBoolean3)
    {
      if (this.cancelled.get())
      {
        this.queue.clear();
        this.parent.cancel(this.key);
        return true;
      }
      if (paramBoolean1)
        if (paramBoolean3)
        {
          if (paramBoolean2)
          {
            Throwable localThrowable2 = this.error;
            if (localThrowable2 != null)
            {
              paramSubscriber.onError(localThrowable2);
              return true;
            }
            paramSubscriber.onCompleted();
            return true;
          }
        }
        else
        {
          Throwable localThrowable1 = this.error;
          if (localThrowable1 != null)
          {
            this.queue.clear();
            paramSubscriber.onError(localThrowable1);
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

    void drain()
    {
      if (getAndIncrement() != 0);
      int i;
      Queue localQueue;
      boolean bool1;
      Subscriber localSubscriber;
      NotificationLite localNotificationLite;
      label37: long l1;
      long l2;
      while (true)
      {
        return;
        i = 1;
        localQueue = this.queue;
        bool1 = this.delayError;
        localSubscriber = (Subscriber)this.actual.get();
        localNotificationLite = NotificationLite.instance();
        if (localSubscriber == null)
          break;
        if (checkTerminated(this.done, localQueue.isEmpty(), localSubscriber, bool1))
          continue;
        l1 = this.requested.get();
        l2 = 0L;
      }
      while (true)
      {
        boolean bool2;
        Object localObject;
        if (l2 != l1)
        {
          bool2 = this.done;
          localObject = localQueue.poll();
          if (localObject != null)
            break label192;
        }
        label192: for (boolean bool3 = true; ; bool3 = false)
        {
          if (checkTerminated(bool2, bool3, localSubscriber, bool1))
            break label196;
          if (!bool3)
            break label198;
          if (l2 != 0L)
          {
            if (l1 != 9223372036854775807L)
              BackpressureUtils.produced(this.requested, l2);
            this.parent.s.request(l2);
          }
          i = addAndGet(-i);
          if (i == 0)
            break;
          if (localSubscriber != null)
            break label37;
          localSubscriber = (Subscriber)this.actual.get();
          break label37;
        }
        label196: break;
        label198: localSubscriber.onNext(localNotificationLite.getValue(localObject));
        l2 += 1L;
      }
    }

    public boolean isUnsubscribed()
    {
      return this.cancelled.get();
    }

    public void onComplete()
    {
      this.done = true;
      drain();
    }

    public void onError(Throwable paramThrowable)
    {
      this.error = paramThrowable;
      this.done = true;
      drain();
    }

    public void onNext(T paramT)
    {
      if (paramT == null)
      {
        this.error = new NullPointerException();
        this.done = true;
      }
      while (true)
      {
        drain();
        return;
        this.queue.offer(NotificationLite.instance().next(paramT));
      }
    }

    public void request(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= required but it was " + paramLong);
      if (paramLong != 0L)
      {
        BackpressureUtils.getAndAddRequest(this.requested, paramLong);
        drain();
      }
    }

    public void unsubscribe()
    {
      if ((this.cancelled.compareAndSet(false, true)) && (getAndIncrement() == 0))
        this.parent.cancel(this.key);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorGroupBy
 * JD-Core Version:    0.6.0
 */