package rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.internal.util.LinkedArrayList;
import rx.subscriptions.SerialSubscription;

public final class CachedObservable<T> extends Observable<T>
{
  private final CacheState<T> state;

  private CachedObservable(Observable.OnSubscribe<T> paramOnSubscribe, CacheState<T> paramCacheState)
  {
    super(paramOnSubscribe);
    this.state = paramCacheState;
  }

  public static <T> CachedObservable<T> from(Observable<? extends T> paramObservable)
  {
    return from(paramObservable, 16);
  }

  public static <T> CachedObservable<T> from(Observable<? extends T> paramObservable, int paramInt)
  {
    if (paramInt < 1)
      throw new IllegalArgumentException("capacityHint > 0 required");
    CacheState localCacheState = new CacheState(paramObservable, paramInt);
    return new CachedObservable(new CachedSubscribe(localCacheState), localCacheState);
  }

  boolean hasObservers()
  {
    return this.state.producers.length != 0;
  }

  boolean isConnected()
  {
    return this.state.isConnected;
  }

  static final class CacheState<T> extends LinkedArrayList
    implements Observer<T>
  {
    static final CachedObservable.ReplayProducer<?>[] EMPTY = new CachedObservable.ReplayProducer[0];
    final SerialSubscription connection;
    volatile boolean isConnected;
    final NotificationLite<T> nl;
    volatile CachedObservable.ReplayProducer<?>[] producers;
    final Observable<? extends T> source;
    boolean sourceDone;

    public CacheState(Observable<? extends T> paramObservable, int paramInt)
    {
      super();
      this.source = paramObservable;
      this.producers = EMPTY;
      this.nl = NotificationLite.instance();
      this.connection = new SerialSubscription();
    }

    public void addProducer(CachedObservable.ReplayProducer<T> paramReplayProducer)
    {
      synchronized (this.connection)
      {
        CachedObservable.ReplayProducer[] arrayOfReplayProducer1 = this.producers;
        int i = arrayOfReplayProducer1.length;
        CachedObservable.ReplayProducer[] arrayOfReplayProducer2 = new CachedObservable.ReplayProducer[i + 1];
        System.arraycopy(arrayOfReplayProducer1, 0, arrayOfReplayProducer2, 0, i);
        arrayOfReplayProducer2[i] = paramReplayProducer;
        this.producers = arrayOfReplayProducer2;
        return;
      }
    }

    public void connect()
    {
      1 local1 = new Subscriber()
      {
        public void onCompleted()
        {
          CachedObservable.CacheState.this.onCompleted();
        }

        public void onError(Throwable paramThrowable)
        {
          CachedObservable.CacheState.this.onError(paramThrowable);
        }

        public void onNext(T paramT)
        {
          CachedObservable.CacheState.this.onNext(paramT);
        }
      };
      this.connection.set(local1);
      this.source.unsafeSubscribe(local1);
      this.isConnected = true;
    }

    void dispatch()
    {
      CachedObservable.ReplayProducer[] arrayOfReplayProducer = this.producers;
      int i = arrayOfReplayProducer.length;
      for (int j = 0; j < i; j++)
        arrayOfReplayProducer[j].replay();
    }

    public void onCompleted()
    {
      if (!this.sourceDone)
      {
        this.sourceDone = true;
        add(this.nl.completed());
        this.connection.unsubscribe();
        dispatch();
      }
    }

    public void onError(Throwable paramThrowable)
    {
      if (!this.sourceDone)
      {
        this.sourceDone = true;
        add(this.nl.error(paramThrowable));
        this.connection.unsubscribe();
        dispatch();
      }
    }

    public void onNext(T paramT)
    {
      if (!this.sourceDone)
      {
        add(this.nl.next(paramT));
        dispatch();
      }
    }

    public void removeProducer(CachedObservable.ReplayProducer<T> paramReplayProducer)
    {
      while (true)
      {
        CachedObservable.ReplayProducer[] arrayOfReplayProducer1;
        int i;
        int j;
        int k;
        synchronized (this.connection)
        {
          arrayOfReplayProducer1 = this.producers;
          i = arrayOfReplayProducer1.length;
          j = -1;
          k = 0;
          if (k >= i)
            continue;
          if (!arrayOfReplayProducer1[k].equals(paramReplayProducer))
            break label129;
          j = k;
          if (j < 0)
            return;
          if (i == 1)
          {
            this.producers = EMPTY;
            return;
          }
        }
        int m = i - 1;
        CachedObservable.ReplayProducer[] arrayOfReplayProducer2 = new CachedObservable.ReplayProducer[m];
        System.arraycopy(arrayOfReplayProducer1, 0, arrayOfReplayProducer2, 0, j);
        System.arraycopy(arrayOfReplayProducer1, j + 1, arrayOfReplayProducer2, j, -1 + (i - j));
        this.producers = arrayOfReplayProducer2;
        monitorexit;
        return;
        label129: k++;
      }
    }
  }

  static final class CachedSubscribe<T> extends AtomicBoolean
    implements Observable.OnSubscribe<T>
  {
    private static final long serialVersionUID = -2817751667698696782L;
    final CachedObservable.CacheState<T> state;

    public CachedSubscribe(CachedObservable.CacheState<T> paramCacheState)
    {
      this.state = paramCacheState;
    }

    public void call(Subscriber<? super T> paramSubscriber)
    {
      CachedObservable.ReplayProducer localReplayProducer = new CachedObservable.ReplayProducer(paramSubscriber, this.state);
      this.state.addProducer(localReplayProducer);
      paramSubscriber.add(localReplayProducer);
      paramSubscriber.setProducer(localReplayProducer);
      if ((!get()) && (compareAndSet(false, true)))
        this.state.connect();
    }
  }

  static final class ReplayProducer<T> extends AtomicLong
    implements Producer, Subscription
  {
    private static final long serialVersionUID = -2557562030197141021L;
    final Subscriber<? super T> child;
    Object[] currentBuffer;
    int currentIndexInBuffer;
    boolean emitting;
    int index;
    boolean missed;
    final CachedObservable.CacheState<T> state;

    public ReplayProducer(Subscriber<? super T> paramSubscriber, CachedObservable.CacheState<T> paramCacheState)
    {
      this.child = paramSubscriber;
      this.state = paramCacheState;
    }

    public boolean isUnsubscribed()
    {
      return get() < 0L;
    }

    public long produced(long paramLong)
    {
      return addAndGet(-paramLong);
    }

    // ERROR //
    public void replay()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   6: ifeq +11 -> 17
      //   9: aload_0
      //   10: iconst_1
      //   11: putfield 54	rx/internal/operators/CachedObservable$ReplayProducer:missed	Z
      //   14: aload_0
      //   15: monitorexit
      //   16: return
      //   17: aload_0
      //   18: iconst_1
      //   19: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   22: aload_0
      //   23: monitorexit
      //   24: iconst_0
      //   25: istore_2
      //   26: aload_0
      //   27: getfield 36	rx/internal/operators/CachedObservable$ReplayProducer:state	Lrx/internal/operators/CachedObservable$CacheState;
      //   30: getfield 60	rx/internal/operators/CachedObservable$CacheState:nl	Lrx/internal/operators/NotificationLite;
      //   33: astore 5
      //   35: aload_0
      //   36: getfield 34	rx/internal/operators/CachedObservable$ReplayProducer:child	Lrx/Subscriber;
      //   39: astore 6
      //   41: aload_0
      //   42: invokevirtual 42	rx/internal/operators/CachedObservable$ReplayProducer:get	()J
      //   45: lstore 7
      //   47: lload 7
      //   49: lstore 9
      //   51: lload 9
      //   53: lconst_0
      //   54: lcmp
      //   55: ifge +29 -> 84
      //   58: iconst_1
      //   59: ifne +560 -> 619
      //   62: aload_0
      //   63: monitorenter
      //   64: aload_0
      //   65: iconst_0
      //   66: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   69: aload_0
      //   70: monitorexit
      //   71: return
      //   72: astore 35
      //   74: aload_0
      //   75: monitorexit
      //   76: aload 35
      //   78: athrow
      //   79: astore_1
      //   80: aload_0
      //   81: monitorexit
      //   82: aload_1
      //   83: athrow
      //   84: aload_0
      //   85: getfield 36	rx/internal/operators/CachedObservable$ReplayProducer:state	Lrx/internal/operators/CachedObservable$CacheState;
      //   88: invokevirtual 64	rx/internal/operators/CachedObservable$CacheState:size	()I
      //   91: istore 11
      //   93: iload 11
      //   95: ifeq +445 -> 540
      //   98: aload_0
      //   99: getfield 66	rx/internal/operators/CachedObservable$ReplayProducer:currentBuffer	[Ljava/lang/Object;
      //   102: astore 12
      //   104: aload 12
      //   106: ifnonnull +18 -> 124
      //   109: aload_0
      //   110: getfield 36	rx/internal/operators/CachedObservable$ReplayProducer:state	Lrx/internal/operators/CachedObservable$CacheState;
      //   113: invokevirtual 70	rx/internal/operators/CachedObservable$CacheState:head	()[Ljava/lang/Object;
      //   116: astore 12
      //   118: aload_0
      //   119: aload 12
      //   121: putfield 66	rx/internal/operators/CachedObservable$ReplayProducer:currentBuffer	[Ljava/lang/Object;
      //   124: iconst_m1
      //   125: aload 12
      //   127: arraylength
      //   128: iadd
      //   129: istore 13
      //   131: aload_0
      //   132: getfield 72	rx/internal/operators/CachedObservable$ReplayProducer:index	I
      //   135: istore 14
      //   137: aload_0
      //   138: getfield 74	rx/internal/operators/CachedObservable$ReplayProducer:currentIndexInBuffer	I
      //   141: istore 15
      //   143: lload 9
      //   145: lconst_0
      //   146: lcmp
      //   147: istore 16
      //   149: iconst_0
      //   150: istore_2
      //   151: iload 16
      //   153: ifne +113 -> 266
      //   156: aload 12
      //   158: iload 15
      //   160: aaload
      //   161: astore 17
      //   163: aload 5
      //   165: aload 17
      //   167: invokevirtual 80	rx/internal/operators/NotificationLite:isCompleted	(Ljava/lang/Object;)Z
      //   170: istore 18
      //   172: iconst_0
      //   173: istore_2
      //   174: iload 18
      //   176: ifeq +35 -> 211
      //   179: aload 6
      //   181: invokevirtual 85	rx/Subscriber:onCompleted	()V
      //   184: iconst_1
      //   185: istore_2
      //   186: aload_0
      //   187: invokevirtual 88	rx/internal/operators/CachedObservable$ReplayProducer:unsubscribe	()V
      //   190: iload_2
      //   191: ifne +428 -> 619
      //   194: aload_0
      //   195: monitorenter
      //   196: aload_0
      //   197: iconst_0
      //   198: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   201: aload_0
      //   202: monitorexit
      //   203: return
      //   204: astore 19
      //   206: aload_0
      //   207: monitorexit
      //   208: aload 19
      //   210: athrow
      //   211: aload 5
      //   213: aload 17
      //   215: invokevirtual 91	rx/internal/operators/NotificationLite:isError	(Ljava/lang/Object;)Z
      //   218: istore 20
      //   220: iconst_0
      //   221: istore_2
      //   222: iload 20
      //   224: ifeq +316 -> 540
      //   227: aload 6
      //   229: aload 5
      //   231: aload 17
      //   233: invokevirtual 95	rx/internal/operators/NotificationLite:getError	(Ljava/lang/Object;)Ljava/lang/Throwable;
      //   236: invokevirtual 99	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
      //   239: iconst_1
      //   240: istore_2
      //   241: aload_0
      //   242: invokevirtual 88	rx/internal/operators/CachedObservable$ReplayProducer:unsubscribe	()V
      //   245: iload_2
      //   246: ifne +373 -> 619
      //   249: aload_0
      //   250: monitorenter
      //   251: aload_0
      //   252: iconst_0
      //   253: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   256: aload_0
      //   257: monitorexit
      //   258: return
      //   259: astore 21
      //   261: aload_0
      //   262: monitorexit
      //   263: aload 21
      //   265: athrow
      //   266: lload 9
      //   268: lconst_0
      //   269: lcmp
      //   270: ifle +270 -> 540
      //   273: iconst_0
      //   274: istore 24
      //   276: iload 14
      //   278: iload 11
      //   280: if_icmpge +201 -> 481
      //   283: lload 9
      //   285: lconst_0
      //   286: lcmp
      //   287: ifle +194 -> 481
      //   290: aload 6
      //   292: invokevirtual 101	rx/Subscriber:isUnsubscribed	()Z
      //   295: istore 29
      //   297: iload 29
      //   299: ifeq +24 -> 323
      //   302: iconst_1
      //   303: ifne +316 -> 619
      //   306: aload_0
      //   307: monitorenter
      //   308: aload_0
      //   309: iconst_0
      //   310: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   313: aload_0
      //   314: monitorexit
      //   315: return
      //   316: astore 34
      //   318: aload_0
      //   319: monitorexit
      //   320: aload 34
      //   322: athrow
      //   323: iload 15
      //   325: iload 13
      //   327: if_icmpne +19 -> 346
      //   330: aload 12
      //   332: iload 13
      //   334: aaload
      //   335: checkcast 102	[Ljava/lang/Object;
      //   338: checkcast 102	[Ljava/lang/Object;
      //   341: astore 12
      //   343: iconst_0
      //   344: istore 15
      //   346: aload 12
      //   348: iload 15
      //   350: aaload
      //   351: astore 30
      //   353: aload 5
      //   355: aload 6
      //   357: aload 30
      //   359: invokevirtual 106	rx/internal/operators/NotificationLite:accept	(Lrx/Observer;Ljava/lang/Object;)Z
      //   362: ifeq +101 -> 463
      //   365: iconst_1
      //   366: istore_2
      //   367: aload_0
      //   368: invokevirtual 88	rx/internal/operators/CachedObservable$ReplayProducer:unsubscribe	()V
      //   371: iload_2
      //   372: ifne +247 -> 619
      //   375: aload_0
      //   376: monitorenter
      //   377: aload_0
      //   378: iconst_0
      //   379: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   382: aload_0
      //   383: monitorexit
      //   384: return
      //   385: astore 33
      //   387: aload_0
      //   388: monitorexit
      //   389: aload 33
      //   391: athrow
      //   392: astore 31
      //   394: aload 31
      //   396: invokestatic 111	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
      //   399: iconst_1
      //   400: istore_2
      //   401: aload_0
      //   402: invokevirtual 88	rx/internal/operators/CachedObservable$ReplayProducer:unsubscribe	()V
      //   405: aload 5
      //   407: aload 30
      //   409: invokevirtual 91	rx/internal/operators/NotificationLite:isError	(Ljava/lang/Object;)Z
      //   412: ifne +30 -> 442
      //   415: aload 5
      //   417: aload 30
      //   419: invokevirtual 80	rx/internal/operators/NotificationLite:isCompleted	(Ljava/lang/Object;)Z
      //   422: ifne +20 -> 442
      //   425: aload 6
      //   427: aload 31
      //   429: aload 5
      //   431: aload 30
      //   433: invokevirtual 115	rx/internal/operators/NotificationLite:getValue	(Ljava/lang/Object;)Ljava/lang/Object;
      //   436: invokestatic 121	rx/exceptions/OnErrorThrowable:addValueAsLastCause	(Ljava/lang/Throwable;Ljava/lang/Object;)Ljava/lang/Throwable;
      //   439: invokevirtual 99	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
      //   442: iload_2
      //   443: ifne +176 -> 619
      //   446: aload_0
      //   447: monitorenter
      //   448: aload_0
      //   449: iconst_0
      //   450: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   453: aload_0
      //   454: monitorexit
      //   455: return
      //   456: astore 32
      //   458: aload_0
      //   459: monitorexit
      //   460: aload 32
      //   462: athrow
      //   463: iinc 15 1
      //   466: iinc 14 1
      //   469: lload 9
      //   471: lconst_1
      //   472: lsub
      //   473: lstore 9
      //   475: iinc 24 1
      //   478: goto -202 -> 276
      //   481: aload 6
      //   483: invokevirtual 101	rx/Subscriber:isUnsubscribed	()Z
      //   486: istore 25
      //   488: iload 25
      //   490: ifeq +24 -> 514
      //   493: iconst_1
      //   494: ifne +125 -> 619
      //   497: aload_0
      //   498: monitorenter
      //   499: aload_0
      //   500: iconst_0
      //   501: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   504: aload_0
      //   505: monitorexit
      //   506: return
      //   507: astore 28
      //   509: aload_0
      //   510: monitorexit
      //   511: aload 28
      //   513: athrow
      //   514: aload_0
      //   515: iload 14
      //   517: putfield 72	rx/internal/operators/CachedObservable$ReplayProducer:index	I
      //   520: aload_0
      //   521: iload 15
      //   523: putfield 74	rx/internal/operators/CachedObservable$ReplayProducer:currentIndexInBuffer	I
      //   526: aload_0
      //   527: aload 12
      //   529: putfield 66	rx/internal/operators/CachedObservable$ReplayProducer:currentBuffer	[Ljava/lang/Object;
      //   532: aload_0
      //   533: iload 24
      //   535: i2l
      //   536: invokevirtual 123	rx/internal/operators/CachedObservable$ReplayProducer:produced	(J)J
      //   539: pop2
      //   540: aload_0
      //   541: monitorenter
      //   542: aload_0
      //   543: getfield 54	rx/internal/operators/CachedObservable$ReplayProducer:missed	Z
      //   546: ifne +33 -> 579
      //   549: aload_0
      //   550: iconst_0
      //   551: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   554: iconst_1
      //   555: istore_2
      //   556: aload_0
      //   557: monitorexit
      //   558: iload_2
      //   559: ifne +60 -> 619
      //   562: aload_0
      //   563: monitorenter
      //   564: aload_0
      //   565: iconst_0
      //   566: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   569: aload_0
      //   570: monitorexit
      //   571: return
      //   572: astore 23
      //   574: aload_0
      //   575: monitorexit
      //   576: aload 23
      //   578: athrow
      //   579: aload_0
      //   580: iconst_0
      //   581: putfield 54	rx/internal/operators/CachedObservable$ReplayProducer:missed	Z
      //   584: aload_0
      //   585: monitorexit
      //   586: goto -545 -> 41
      //   589: astore 22
      //   591: aload_0
      //   592: monitorexit
      //   593: aload 22
      //   595: athrow
      //   596: astore_3
      //   597: iload_2
      //   598: ifne +12 -> 610
      //   601: aload_0
      //   602: monitorenter
      //   603: aload_0
      //   604: iconst_0
      //   605: putfield 52	rx/internal/operators/CachedObservable$ReplayProducer:emitting	Z
      //   608: aload_0
      //   609: monitorexit
      //   610: aload_3
      //   611: athrow
      //   612: astore 4
      //   614: aload_0
      //   615: monitorexit
      //   616: aload 4
      //   618: athrow
      //   619: return
      //
      // Exception table:
      //   from	to	target	type
      //   64	71	72	finally
      //   74	76	72	finally
      //   2	16	79	finally
      //   17	24	79	finally
      //   80	82	79	finally
      //   196	203	204	finally
      //   206	208	204	finally
      //   251	258	259	finally
      //   261	263	259	finally
      //   308	315	316	finally
      //   318	320	316	finally
      //   377	384	385	finally
      //   387	389	385	finally
      //   353	365	392	java/lang/Throwable
      //   367	371	392	java/lang/Throwable
      //   448	455	456	finally
      //   458	460	456	finally
      //   499	506	507	finally
      //   509	511	507	finally
      //   564	571	572	finally
      //   574	576	572	finally
      //   542	554	589	finally
      //   556	558	589	finally
      //   579	586	589	finally
      //   591	593	589	finally
      //   26	41	596	finally
      //   41	47	596	finally
      //   84	93	596	finally
      //   98	104	596	finally
      //   109	124	596	finally
      //   124	143	596	finally
      //   156	172	596	finally
      //   179	184	596	finally
      //   186	190	596	finally
      //   211	220	596	finally
      //   227	239	596	finally
      //   241	245	596	finally
      //   290	297	596	finally
      //   330	343	596	finally
      //   346	353	596	finally
      //   353	365	596	finally
      //   367	371	596	finally
      //   394	399	596	finally
      //   401	442	596	finally
      //   481	488	596	finally
      //   514	540	596	finally
      //   540	542	596	finally
      //   593	596	596	finally
      //   603	610	612	finally
      //   614	616	612	finally
    }

    public void request(long paramLong)
    {
      long l1;
      long l2;
      do
      {
        l1 = get();
        if (l1 < 0L)
          return;
        l2 = l1 + paramLong;
        if (l2 >= 0L)
          continue;
        l2 = 9223372036854775807L;
      }
      while (!compareAndSet(l1, l2));
      replay();
    }

    public void unsubscribe()
    {
      if ((get() >= 0L) && (getAndSet(-1L) >= 0L))
        this.state.removeProducer(this);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CachedObservable
 * JD-Core Version:    0.6.0
 */