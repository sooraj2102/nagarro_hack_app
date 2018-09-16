package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Observable.Operator;
import rx.Observer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.functions.Action0;
import rx.observers.SerializedObserver;
import rx.observers.SerializedSubscriber;
import rx.subjects.UnicastSubject;
import rx.subscriptions.Subscriptions;

public final class OperatorWindowWithTime<T>
  implements Observable.Operator<Observable<T>, T>
{
  static final Object NEXT_SUBJECT = new Object();
  static final NotificationLite<Object> NL = NotificationLite.instance();
  final Scheduler scheduler;
  final int size;
  final long timeshift;
  final long timespan;
  final TimeUnit unit;

  public OperatorWindowWithTime(long paramLong1, long paramLong2, TimeUnit paramTimeUnit, int paramInt, Scheduler paramScheduler)
  {
    this.timespan = paramLong1;
    this.timeshift = paramLong2;
    this.unit = paramTimeUnit;
    this.size = paramInt;
    this.scheduler = paramScheduler;
  }

  public Subscriber<? super T> call(Subscriber<? super Observable<T>> paramSubscriber)
  {
    Scheduler.Worker localWorker = this.scheduler.createWorker();
    if (this.timespan == this.timeshift)
    {
      ExactSubscriber localExactSubscriber = new ExactSubscriber(paramSubscriber, localWorker);
      localExactSubscriber.add(localWorker);
      localExactSubscriber.scheduleExact();
      return localExactSubscriber;
    }
    InexactSubscriber localInexactSubscriber = new InexactSubscriber(paramSubscriber, localWorker);
    localInexactSubscriber.add(localWorker);
    localInexactSubscriber.startNewChunk();
    localInexactSubscriber.scheduleChunk();
    return localInexactSubscriber;
  }

  static final class CountedSerializedSubject<T>
  {
    final Observer<T> consumer;
    int count;
    final Observable<T> producer;

    public CountedSerializedSubject(Observer<T> paramObserver, Observable<T> paramObservable)
    {
      this.consumer = new SerializedObserver(paramObserver);
      this.producer = paramObservable;
    }
  }

  final class ExactSubscriber extends Subscriber<T>
  {
    final Subscriber<? super Observable<T>> child;
    boolean emitting;
    final Object guard;
    List<Object> queue;
    volatile OperatorWindowWithTime.State<T> state;
    final Scheduler.Worker worker;

    public ExactSubscriber(Scheduler.Worker arg2)
    {
      Subscriber localSubscriber;
      this.child = new SerializedSubscriber(localSubscriber);
      Object localObject;
      this.worker = localObject;
      this.guard = new Object();
      this.state = OperatorWindowWithTime.State.empty();
      localSubscriber.add(Subscriptions.create(new Action0(OperatorWindowWithTime.this)
      {
        public void call()
        {
          if (OperatorWindowWithTime.ExactSubscriber.this.state.consumer == null)
            OperatorWindowWithTime.ExactSubscriber.this.unsubscribe();
        }
      }));
    }

    void complete()
    {
      Observer localObserver = this.state.consumer;
      this.state = this.state.clear();
      if (localObserver != null)
        localObserver.onCompleted();
      this.child.onCompleted();
      unsubscribe();
    }

    boolean drain(List<Object> paramList)
    {
      if (paramList == null);
      Object localObject;
      do
      {
        while (true)
        {
          Iterator localIterator;
          while (!localIterator.hasNext())
          {
            return true;
            localIterator = paramList.iterator();
          }
          localObject = localIterator.next();
          if (localObject != OperatorWindowWithTime.NEXT_SUBJECT)
            break;
          if (!replaceSubject())
            return false;
        }
        if (OperatorWindowWithTime.NL.isError(localObject))
        {
          error(OperatorWindowWithTime.NL.getError(localObject));
          return true;
        }
        if (!OperatorWindowWithTime.NL.isCompleted(localObject))
          continue;
        complete();
        return true;
      }
      while (emitValue(localObject));
      return false;
    }

    boolean emitValue(T paramT)
    {
      OperatorWindowWithTime.State localState1 = this.state;
      if (localState1.consumer == null)
      {
        if (!replaceSubject())
          return false;
        localState1 = this.state;
      }
      localState1.consumer.onNext(paramT);
      if (localState1.count == -1 + OperatorWindowWithTime.this.size)
        localState1.consumer.onCompleted();
      for (OperatorWindowWithTime.State localState2 = localState1.clear(); ; localState2 = localState1.next())
      {
        this.state = localState2;
        return true;
      }
    }

    void error(Throwable paramThrowable)
    {
      Observer localObserver = this.state.consumer;
      this.state = this.state.clear();
      if (localObserver != null)
        localObserver.onError(paramThrowable);
      this.child.onError(paramThrowable);
      unsubscribe();
    }

    // ERROR //
    void nextWindow()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   4: astore_1
      //   5: aload_1
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   11: ifeq +37 -> 48
      //   14: aload_0
      //   15: getfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   18: ifnonnull +14 -> 32
      //   21: aload_0
      //   22: new 159	java/util/ArrayList
      //   25: dup
      //   26: invokespecial 160	java/util/ArrayList:<init>	()V
      //   29: putfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   32: aload_0
      //   33: getfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   36: getstatic 106	rx/internal/operators/OperatorWindowWithTime:NEXT_SUBJECT	Ljava/lang/Object;
      //   39: invokeinterface 162 2 0
      //   44: pop
      //   45: aload_1
      //   46: monitorexit
      //   47: return
      //   48: aload_0
      //   49: iconst_1
      //   50: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   53: aload_1
      //   54: monitorexit
      //   55: iconst_0
      //   56: istore_3
      //   57: aload_0
      //   58: invokevirtual 109	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:replaceSubject	()Z
      //   61: istore 7
      //   63: iload 7
      //   65: ifne +38 -> 103
      //   68: iconst_0
      //   69: ifne +187 -> 256
      //   72: aload_0
      //   73: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   76: astore 16
      //   78: aload 16
      //   80: monitorenter
      //   81: aload_0
      //   82: iconst_0
      //   83: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   86: aload 16
      //   88: monitorexit
      //   89: return
      //   90: astore 17
      //   92: aload 16
      //   94: monitorexit
      //   95: aload 17
      //   97: athrow
      //   98: astore_2
      //   99: aload_1
      //   100: monitorexit
      //   101: aload_2
      //   102: athrow
      //   103: aload_0
      //   104: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   107: astore 8
      //   109: aload 8
      //   111: monitorenter
      //   112: aload_0
      //   113: getfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   116: astore 10
      //   118: aload 10
      //   120: ifnonnull +43 -> 163
      //   123: aload_0
      //   124: iconst_0
      //   125: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   128: iconst_1
      //   129: istore_3
      //   130: aload 8
      //   132: monitorexit
      //   133: iload_3
      //   134: ifne +122 -> 256
      //   137: aload_0
      //   138: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   141: astore 11
      //   143: aload 11
      //   145: monitorenter
      //   146: aload_0
      //   147: iconst_0
      //   148: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   151: aload 11
      //   153: monitorexit
      //   154: return
      //   155: astore 12
      //   157: aload 11
      //   159: monitorexit
      //   160: aload 12
      //   162: athrow
      //   163: aload_0
      //   164: aconst_null
      //   165: putfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   168: aload 8
      //   170: monitorexit
      //   171: aload_0
      //   172: aload 10
      //   174: invokevirtual 164	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:drain	(Ljava/util/List;)Z
      //   177: istore 13
      //   179: iload 13
      //   181: ifne -78 -> 103
      //   184: iconst_0
      //   185: ifne +71 -> 256
      //   188: aload_0
      //   189: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   192: astore 14
      //   194: aload 14
      //   196: monitorenter
      //   197: aload_0
      //   198: iconst_0
      //   199: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   202: aload 14
      //   204: monitorexit
      //   205: return
      //   206: astore 15
      //   208: aload 14
      //   210: monitorexit
      //   211: aload 15
      //   213: athrow
      //   214: astore 9
      //   216: aload 8
      //   218: monitorexit
      //   219: aload 9
      //   221: athrow
      //   222: astore 4
      //   224: iload_3
      //   225: ifne +20 -> 245
      //   228: aload_0
      //   229: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   232: astore 5
      //   234: aload 5
      //   236: monitorenter
      //   237: aload_0
      //   238: iconst_0
      //   239: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   242: aload 5
      //   244: monitorexit
      //   245: aload 4
      //   247: athrow
      //   248: astore 6
      //   250: aload 5
      //   252: monitorexit
      //   253: aload 6
      //   255: athrow
      //   256: return
      //
      // Exception table:
      //   from	to	target	type
      //   81	89	90	finally
      //   92	95	90	finally
      //   7	32	98	finally
      //   32	47	98	finally
      //   48	55	98	finally
      //   99	101	98	finally
      //   146	154	155	finally
      //   157	160	155	finally
      //   197	205	206	finally
      //   208	211	206	finally
      //   112	118	214	finally
      //   123	128	214	finally
      //   130	133	214	finally
      //   163	171	214	finally
      //   216	219	214	finally
      //   57	63	222	finally
      //   103	112	222	finally
      //   171	179	222	finally
      //   219	222	222	finally
      //   237	245	248	finally
      //   250	253	248	finally
    }

    public void onCompleted()
    {
      List localList;
      synchronized (this.guard)
      {
        if (this.emitting)
        {
          if (this.queue == null)
            this.queue = new ArrayList();
          this.queue.add(OperatorWindowWithTime.NL.completed());
          return;
        }
        localList = this.queue;
        this.queue = null;
        this.emitting = true;
      }
      try
      {
        drain(localList);
        complete();
        return;
        localObject2 = finally;
        monitorexit;
        throw localObject2;
      }
      catch (Throwable localThrowable)
      {
        error(localThrowable);
      }
    }

    public void onError(Throwable paramThrowable)
    {
      synchronized (this.guard)
      {
        if (this.emitting)
        {
          this.queue = Collections.singletonList(OperatorWindowWithTime.NL.error(paramThrowable));
          return;
        }
        this.queue = null;
        this.emitting = true;
        error(paramThrowable);
        return;
      }
    }

    // ERROR //
    public void onNext(T paramT)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   4: astore_2
      //   5: aload_2
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   11: ifeq +35 -> 46
      //   14: aload_0
      //   15: getfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   18: ifnonnull +14 -> 32
      //   21: aload_0
      //   22: new 159	java/util/ArrayList
      //   25: dup
      //   26: invokespecial 160	java/util/ArrayList:<init>	()V
      //   29: putfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   32: aload_0
      //   33: getfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   36: aload_1
      //   37: invokeinterface 162 2 0
      //   42: pop
      //   43: aload_2
      //   44: monitorexit
      //   45: return
      //   46: aload_0
      //   47: iconst_1
      //   48: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   51: aload_2
      //   52: monitorexit
      //   53: iconst_0
      //   54: istore 4
      //   56: aload_0
      //   57: aload_1
      //   58: invokevirtual 135	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitValue	(Ljava/lang/Object;)Z
      //   61: istore 8
      //   63: iload 8
      //   65: ifne +38 -> 103
      //   68: iconst_0
      //   69: ifne +190 -> 259
      //   72: aload_0
      //   73: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   76: astore 17
      //   78: aload 17
      //   80: monitorenter
      //   81: aload_0
      //   82: iconst_0
      //   83: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   86: aload 17
      //   88: monitorexit
      //   89: return
      //   90: astore 18
      //   92: aload 17
      //   94: monitorexit
      //   95: aload 18
      //   97: athrow
      //   98: astore_3
      //   99: aload_2
      //   100: monitorexit
      //   101: aload_3
      //   102: athrow
      //   103: aload_0
      //   104: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   107: astore 9
      //   109: aload 9
      //   111: monitorenter
      //   112: aload_0
      //   113: getfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   116: astore 11
      //   118: aload 11
      //   120: ifnonnull +45 -> 165
      //   123: aload_0
      //   124: iconst_0
      //   125: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   128: iconst_1
      //   129: istore 4
      //   131: aload 9
      //   133: monitorexit
      //   134: iload 4
      //   136: ifne +123 -> 259
      //   139: aload_0
      //   140: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   143: astore 12
      //   145: aload 12
      //   147: monitorenter
      //   148: aload_0
      //   149: iconst_0
      //   150: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   153: aload 12
      //   155: monitorexit
      //   156: return
      //   157: astore 13
      //   159: aload 12
      //   161: monitorexit
      //   162: aload 13
      //   164: athrow
      //   165: aload_0
      //   166: aconst_null
      //   167: putfield 157	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:queue	Ljava/util/List;
      //   170: aload 9
      //   172: monitorexit
      //   173: aload_0
      //   174: aload 11
      //   176: invokevirtual 164	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:drain	(Ljava/util/List;)Z
      //   179: istore 14
      //   181: iload 14
      //   183: ifne -80 -> 103
      //   186: iconst_0
      //   187: ifne +72 -> 259
      //   190: aload_0
      //   191: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   194: astore 15
      //   196: aload 15
      //   198: monitorenter
      //   199: aload_0
      //   200: iconst_0
      //   201: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   204: aload 15
      //   206: monitorexit
      //   207: return
      //   208: astore 16
      //   210: aload 15
      //   212: monitorexit
      //   213: aload 16
      //   215: athrow
      //   216: astore 10
      //   218: aload 9
      //   220: monitorexit
      //   221: aload 10
      //   223: athrow
      //   224: astore 5
      //   226: iload 4
      //   228: ifne +20 -> 248
      //   231: aload_0
      //   232: getfield 43	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:guard	Ljava/lang/Object;
      //   235: astore 6
      //   237: aload 6
      //   239: monitorenter
      //   240: aload_0
      //   241: iconst_0
      //   242: putfield 155	rx/internal/operators/OperatorWindowWithTime$ExactSubscriber:emitting	Z
      //   245: aload 6
      //   247: monitorexit
      //   248: aload 5
      //   250: athrow
      //   251: astore 7
      //   253: aload 6
      //   255: monitorexit
      //   256: aload 7
      //   258: athrow
      //   259: return
      //
      // Exception table:
      //   from	to	target	type
      //   81	89	90	finally
      //   92	95	90	finally
      //   7	32	98	finally
      //   32	45	98	finally
      //   46	53	98	finally
      //   99	101	98	finally
      //   148	156	157	finally
      //   159	162	157	finally
      //   199	207	208	finally
      //   210	213	208	finally
      //   112	118	216	finally
      //   123	128	216	finally
      //   131	134	216	finally
      //   165	173	216	finally
      //   218	221	216	finally
      //   56	63	224	finally
      //   103	112	224	finally
      //   173	181	224	finally
      //   221	224	224	finally
      //   240	248	251	finally
      //   253	256	251	finally
    }

    public void onStart()
    {
      request(9223372036854775807L);
    }

    boolean replaceSubject()
    {
      Observer localObserver = this.state.consumer;
      if (localObserver != null)
        localObserver.onCompleted();
      if (this.child.isUnsubscribed())
      {
        this.state = this.state.clear();
        unsubscribe();
        return false;
      }
      UnicastSubject localUnicastSubject = UnicastSubject.create();
      this.state = this.state.create(localUnicastSubject, localUnicastSubject);
      this.child.onNext(localUnicastSubject);
      return true;
    }

    void scheduleExact()
    {
      this.worker.schedulePeriodically(new Action0()
      {
        public void call()
        {
          OperatorWindowWithTime.ExactSubscriber.this.nextWindow();
        }
      }
      , 0L, OperatorWindowWithTime.this.timespan, OperatorWindowWithTime.this.unit);
    }
  }

  final class InexactSubscriber extends Subscriber<T>
  {
    final Subscriber<? super Observable<T>> child;
    final List<OperatorWindowWithTime.CountedSerializedSubject<T>> chunks;
    boolean done;
    final Object guard;
    final Scheduler.Worker worker;

    public InexactSubscriber(Scheduler.Worker arg2)
    {
      super();
      this.child = localSubscriber;
      Object localObject;
      this.worker = localObject;
      this.guard = new Object();
      this.chunks = new LinkedList();
    }

    OperatorWindowWithTime.CountedSerializedSubject<T> createCountedSerializedSubject()
    {
      UnicastSubject localUnicastSubject = UnicastSubject.create();
      return new OperatorWindowWithTime.CountedSerializedSubject(localUnicastSubject, localUnicastSubject);
    }

    public void onCompleted()
    {
      synchronized (this.guard)
      {
        if (this.done)
          return;
        this.done = true;
        ArrayList localArrayList = new ArrayList(this.chunks);
        this.chunks.clear();
        Iterator localIterator = localArrayList.iterator();
        if (localIterator.hasNext())
          ((OperatorWindowWithTime.CountedSerializedSubject)localIterator.next()).consumer.onCompleted();
      }
      this.child.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      synchronized (this.guard)
      {
        if (this.done)
          return;
        this.done = true;
        ArrayList localArrayList = new ArrayList(this.chunks);
        this.chunks.clear();
        Iterator localIterator = localArrayList.iterator();
        if (localIterator.hasNext())
          ((OperatorWindowWithTime.CountedSerializedSubject)localIterator.next()).consumer.onError(paramThrowable);
      }
      this.child.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      ArrayList localArrayList;
      synchronized (this.guard)
      {
        if (this.done)
          return;
        localArrayList = new ArrayList(this.chunks);
        Iterator localIterator1 = this.chunks.iterator();
        while (localIterator1.hasNext())
        {
          OperatorWindowWithTime.CountedSerializedSubject localCountedSerializedSubject2 = (OperatorWindowWithTime.CountedSerializedSubject)localIterator1.next();
          int i = 1 + localCountedSerializedSubject2.count;
          localCountedSerializedSubject2.count = i;
          if (i != OperatorWindowWithTime.this.size)
            continue;
          localIterator1.remove();
        }
      }
      monitorexit;
      Iterator localIterator2 = localArrayList.iterator();
      while (localIterator2.hasNext())
      {
        OperatorWindowWithTime.CountedSerializedSubject localCountedSerializedSubject1 = (OperatorWindowWithTime.CountedSerializedSubject)localIterator2.next();
        localCountedSerializedSubject1.consumer.onNext(paramT);
        if (localCountedSerializedSubject1.count != OperatorWindowWithTime.this.size)
          continue;
        localCountedSerializedSubject1.consumer.onCompleted();
      }
    }

    public void onStart()
    {
      request(9223372036854775807L);
    }

    void scheduleChunk()
    {
      this.worker.schedulePeriodically(new Action0()
      {
        public void call()
        {
          OperatorWindowWithTime.InexactSubscriber.this.startNewChunk();
        }
      }
      , OperatorWindowWithTime.this.timeshift, OperatorWindowWithTime.this.timeshift, OperatorWindowWithTime.this.unit);
    }

    void startNewChunk()
    {
      OperatorWindowWithTime.CountedSerializedSubject localCountedSerializedSubject = createCountedSerializedSubject();
      synchronized (this.guard)
      {
        if (this.done)
          return;
        this.chunks.add(localCountedSerializedSubject);
      }
      try
      {
        this.child.onNext(localCountedSerializedSubject.producer);
        this.worker.schedule(new Action0(localCountedSerializedSubject)
        {
          public void call()
          {
            OperatorWindowWithTime.InexactSubscriber.this.terminateChunk(this.val$chunk);
          }
        }
        , OperatorWindowWithTime.this.timespan, OperatorWindowWithTime.this.unit);
        return;
        localObject2 = finally;
        monitorexit;
        throw localObject2;
      }
      catch (Throwable localThrowable)
      {
        onError(localThrowable);
      }
    }

    void terminateChunk(OperatorWindowWithTime.CountedSerializedSubject<T> paramCountedSerializedSubject)
    {
      synchronized (this.guard)
      {
        if (this.done)
          return;
        Iterator localIterator = this.chunks.iterator();
        int i;
        while (true)
        {
          boolean bool = localIterator.hasNext();
          i = 0;
          if (!bool)
            break;
          if ((OperatorWindowWithTime.CountedSerializedSubject)localIterator.next() != paramCountedSerializedSubject)
            continue;
          i = 1;
          localIterator.remove();
        }
        if (i != 0)
        {
          paramCountedSerializedSubject.consumer.onCompleted();
          return;
        }
      }
    }
  }

  static final class State<T>
  {
    static final State<Object> EMPTY = new State(null, null, 0);
    final Observer<T> consumer;
    final int count;
    final Observable<T> producer;

    public State(Observer<T> paramObserver, Observable<T> paramObservable, int paramInt)
    {
      this.consumer = paramObserver;
      this.producer = paramObservable;
      this.count = paramInt;
    }

    public static <T> State<T> empty()
    {
      return EMPTY;
    }

    public State<T> clear()
    {
      return empty();
    }

    public State<T> create(Observer<T> paramObserver, Observable<T> paramObservable)
    {
      return new State(paramObserver, paramObservable, 0);
    }

    public State<T> next()
    {
      return new State(this.consumer, this.producer, 1 + this.count);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorWindowWithTime
 * JD-Core Version:    0.6.0
 */