package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable.Operator;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;

public final class OperatorOnBackpressureLatest<T>
  implements Observable.Operator<T, T>
{
  public static <T> OperatorOnBackpressureLatest<T> instance()
  {
    return Holder.INSTANCE;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    LatestEmitter localLatestEmitter = new LatestEmitter(paramSubscriber);
    LatestSubscriber localLatestSubscriber = new LatestSubscriber(localLatestEmitter);
    localLatestEmitter.parent = localLatestSubscriber;
    paramSubscriber.add(localLatestSubscriber);
    paramSubscriber.add(localLatestEmitter);
    paramSubscriber.setProducer(localLatestEmitter);
    return localLatestSubscriber;
  }

  static final class Holder
  {
    static final OperatorOnBackpressureLatest<Object> INSTANCE = new OperatorOnBackpressureLatest();
  }

  static final class LatestEmitter<T> extends AtomicLong
    implements Producer, Subscription, Observer<T>
  {
    static final Object EMPTY = new Object();
    static final long NOT_REQUESTED = -4611686018427387904L;
    private static final long serialVersionUID = -1364393685005146274L;
    final Subscriber<? super T> child;
    volatile boolean done;
    boolean emitting;
    boolean missed;
    OperatorOnBackpressureLatest.LatestSubscriber<? super T> parent;
    Throwable terminal;
    final AtomicReference<Object> value;

    public LatestEmitter(Subscriber<? super T> paramSubscriber)
    {
      this.child = paramSubscriber;
      this.value = new AtomicReference(EMPTY);
      lazySet(-4611686018427387904L);
    }

    // ERROR //
    void emit()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 62	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:emitting	Z
      //   6: ifeq +11 -> 17
      //   9: aload_0
      //   10: iconst_1
      //   11: putfield 64	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:missed	Z
      //   14: aload_0
      //   15: monitorexit
      //   16: return
      //   17: aload_0
      //   18: iconst_1
      //   19: putfield 62	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:emitting	Z
      //   22: aload_0
      //   23: iconst_0
      //   24: putfield 64	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:missed	Z
      //   27: aload_0
      //   28: monitorexit
      //   29: iconst_0
      //   30: istore_2
      //   31: aload_0
      //   32: invokevirtual 68	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:get	()J
      //   35: lstore 5
      //   37: lload 5
      //   39: ldc2_w 69
      //   42: lcmp
      //   43: ifne +31 -> 74
      //   46: iconst_1
      //   47: istore_2
      //   48: iload_2
      //   49: ifne +212 -> 261
      //   52: aload_0
      //   53: monitorenter
      //   54: aload_0
      //   55: iconst_0
      //   56: putfield 62	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:emitting	Z
      //   59: aload_0
      //   60: monitorexit
      //   61: return
      //   62: astore 17
      //   64: aload_0
      //   65: monitorexit
      //   66: aload 17
      //   68: athrow
      //   69: astore_1
      //   70: aload_0
      //   71: monitorexit
      //   72: aload_1
      //   73: athrow
      //   74: aload_0
      //   75: getfield 55	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:value	Ljava/util/concurrent/atomic/AtomicReference;
      //   78: invokevirtual 73	java/util/concurrent/atomic/AtomicReference:get	()Ljava/lang/Object;
      //   81: astore 7
      //   83: lload 5
      //   85: lconst_0
      //   86: lcmp
      //   87: istore 8
      //   89: iconst_0
      //   90: istore_2
      //   91: iload 8
      //   93: ifle +48 -> 141
      //   96: aload 7
      //   98: getstatic 44	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:EMPTY	Ljava/lang/Object;
      //   101: if_acmpeq +40 -> 141
      //   104: aload 7
      //   106: astore 9
      //   108: aload_0
      //   109: getfield 48	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:child	Lrx/Subscriber;
      //   112: aload 9
      //   114: invokevirtual 78	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   117: aload_0
      //   118: getfield 55	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:value	Ljava/util/concurrent/atomic/AtomicReference;
      //   121: aload 7
      //   123: getstatic 44	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:EMPTY	Ljava/lang/Object;
      //   126: invokevirtual 82	java/util/concurrent/atomic/AtomicReference:compareAndSet	(Ljava/lang/Object;Ljava/lang/Object;)Z
      //   129: pop
      //   130: aload_0
      //   131: lconst_1
      //   132: invokevirtual 86	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:produced	(J)J
      //   135: pop2
      //   136: getstatic 44	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:EMPTY	Ljava/lang/Object;
      //   139: astore 7
      //   141: getstatic 44	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:EMPTY	Ljava/lang/Object;
      //   144: astore 13
      //   146: iconst_0
      //   147: istore_2
      //   148: aload 7
      //   150: aload 13
      //   152: if_acmpne +38 -> 190
      //   155: aload_0
      //   156: getfield 88	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:done	Z
      //   159: istore 14
      //   161: iconst_0
      //   162: istore_2
      //   163: iload 14
      //   165: ifeq +25 -> 190
      //   168: aload_0
      //   169: getfield 90	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:terminal	Ljava/lang/Throwable;
      //   172: astore 15
      //   174: iconst_0
      //   175: istore_2
      //   176: aload 15
      //   178: ifnull +56 -> 234
      //   181: aload_0
      //   182: getfield 48	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:child	Lrx/Subscriber;
      //   185: aload 15
      //   187: invokevirtual 94	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
      //   190: aload_0
      //   191: monitorenter
      //   192: aload_0
      //   193: getfield 64	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:missed	Z
      //   196: ifne +48 -> 244
      //   199: aload_0
      //   200: iconst_0
      //   201: putfield 62	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:emitting	Z
      //   204: iconst_1
      //   205: istore_2
      //   206: aload_0
      //   207: monitorexit
      //   208: goto -160 -> 48
      //   211: astore 16
      //   213: aload_0
      //   214: monitorexit
      //   215: aload 16
      //   217: athrow
      //   218: astore_3
      //   219: iload_2
      //   220: ifne +12 -> 232
      //   223: aload_0
      //   224: monitorenter
      //   225: aload_0
      //   226: iconst_0
      //   227: putfield 62	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:emitting	Z
      //   230: aload_0
      //   231: monitorexit
      //   232: aload_3
      //   233: athrow
      //   234: aload_0
      //   235: getfield 48	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:child	Lrx/Subscriber;
      //   238: invokevirtual 97	rx/Subscriber:onCompleted	()V
      //   241: goto -51 -> 190
      //   244: aload_0
      //   245: iconst_0
      //   246: putfield 64	rx/internal/operators/OperatorOnBackpressureLatest$LatestEmitter:missed	Z
      //   249: aload_0
      //   250: monitorexit
      //   251: goto -220 -> 31
      //   254: astore 4
      //   256: aload_0
      //   257: monitorexit
      //   258: aload 4
      //   260: athrow
      //   261: return
      //
      // Exception table:
      //   from	to	target	type
      //   54	61	62	finally
      //   64	66	62	finally
      //   2	16	69	finally
      //   17	29	69	finally
      //   70	72	69	finally
      //   192	204	211	finally
      //   206	208	211	finally
      //   213	215	211	finally
      //   244	251	211	finally
      //   31	37	218	finally
      //   74	83	218	finally
      //   96	104	218	finally
      //   108	141	218	finally
      //   141	146	218	finally
      //   155	161	218	finally
      //   168	174	218	finally
      //   181	190	218	finally
      //   190	192	218	finally
      //   215	218	218	finally
      //   234	241	218	finally
      //   225	232	254	finally
      //   256	258	254	finally
    }

    public boolean isUnsubscribed()
    {
      return get() == -9223372036854775808L;
    }

    public void onCompleted()
    {
      this.done = true;
      emit();
    }

    public void onError(Throwable paramThrowable)
    {
      this.terminal = paramThrowable;
      this.done = true;
      emit();
    }

    public void onNext(T paramT)
    {
      this.value.lazySet(paramT);
      emit();
    }

    long produced(long paramLong)
    {
      long l1;
      long l2;
      do
      {
        l1 = get();
        if (l1 < 0L)
          return l1;
        l2 = l1 - paramLong;
      }
      while (!compareAndSet(l1, l2));
      return l2;
    }

    public void request(long paramLong)
    {
      if (paramLong >= 0L);
      while (true)
      {
        long l1 = get();
        if (l1 == -9223372036854775808L)
          return;
        long l2;
        if (l1 == -4611686018427387904L)
          l2 = paramLong;
        while (compareAndSet(l1, l2))
        {
          if (l1 == -4611686018427387904L)
            this.parent.requestMore(9223372036854775807L);
          emit();
          return;
          l2 = l1 + paramLong;
          if (l2 >= 0L)
            continue;
          l2 = 9223372036854775807L;
        }
      }
    }

    public void unsubscribe()
    {
      if (get() >= 0L)
        getAndSet(-9223372036854775808L);
    }
  }

  static final class LatestSubscriber<T> extends Subscriber<T>
  {
    private final OperatorOnBackpressureLatest.LatestEmitter<T> producer;

    LatestSubscriber(OperatorOnBackpressureLatest.LatestEmitter<T> paramLatestEmitter)
    {
      this.producer = paramLatestEmitter;
    }

    public void onCompleted()
    {
      this.producer.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      this.producer.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      this.producer.onNext(paramT);
    }

    public void onStart()
    {
      request(0L);
    }

    void requestMore(long paramLong)
    {
      request(paramLong);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorOnBackpressureLatest
 * JD-Core Version:    0.6.0
 */