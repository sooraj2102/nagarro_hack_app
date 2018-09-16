package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.SynchronizedQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.observables.ConnectableObservable;
import rx.subscriptions.Subscriptions;

public final class OperatorPublish<T> extends ConnectableObservable<T>
{
  final AtomicReference<PublishSubscriber<T>> current;
  final Observable<? extends T> source;

  private OperatorPublish(Observable.OnSubscribe<T> paramOnSubscribe, Observable<? extends T> paramObservable, AtomicReference<PublishSubscriber<T>> paramAtomicReference)
  {
    super(paramOnSubscribe);
    this.source = paramObservable;
    this.current = paramAtomicReference;
  }

  public static <T, R> Observable<R> create(Observable<? extends T> paramObservable, Func1<? super Observable<T>, ? extends Observable<R>> paramFunc1)
  {
    return create(paramObservable, paramFunc1, false);
  }

  public static <T, R> Observable<R> create(Observable<? extends T> paramObservable, Func1<? super Observable<T>, ? extends Observable<R>> paramFunc1, boolean paramBoolean)
  {
    return create(new Observable.OnSubscribe(paramBoolean, paramFunc1, paramObservable)
    {
      public void call(Subscriber<? super R> paramSubscriber)
      {
        OnSubscribePublishMulticast localOnSubscribePublishMulticast = new OnSubscribePublishMulticast(RxRingBuffer.SIZE, this.val$delayError);
        1 local1 = new Subscriber(paramSubscriber, localOnSubscribePublishMulticast)
        {
          public void onCompleted()
          {
            this.val$op.unsubscribe();
            this.val$child.onCompleted();
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$op.unsubscribe();
            this.val$child.onError(paramThrowable);
          }

          public void onNext(R paramR)
          {
            this.val$child.onNext(paramR);
          }

          public void setProducer(Producer paramProducer)
          {
            this.val$child.setProducer(paramProducer);
          }
        };
        paramSubscriber.add(localOnSubscribePublishMulticast);
        paramSubscriber.add(local1);
        ((Observable)this.val$selector.call(Observable.create(localOnSubscribePublishMulticast))).unsafeSubscribe(local1);
        this.val$source.unsafeSubscribe(localOnSubscribePublishMulticast.subscriber());
      }
    });
  }

  public static <T> ConnectableObservable<T> create(Observable<? extends T> paramObservable)
  {
    AtomicReference localAtomicReference = new AtomicReference();
    return new OperatorPublish(new Observable.OnSubscribe(localAtomicReference)
    {
      public void call(Subscriber<? super T> paramSubscriber)
      {
        Object localObject;
        OperatorPublish.InnerProducer localInnerProducer;
        do
        {
          while (true)
          {
            localObject = (OperatorPublish.PublishSubscriber)this.val$curr.get();
            if ((localObject != null) && (!((OperatorPublish.PublishSubscriber)localObject).isUnsubscribed()))
              break;
            OperatorPublish.PublishSubscriber localPublishSubscriber = new OperatorPublish.PublishSubscriber(this.val$curr);
            localPublishSubscriber.init();
            if (!this.val$curr.compareAndSet(localObject, localPublishSubscriber))
              continue;
            localObject = localPublishSubscriber;
          }
          localInnerProducer = new OperatorPublish.InnerProducer((OperatorPublish.PublishSubscriber)localObject, paramSubscriber);
        }
        while (!((OperatorPublish.PublishSubscriber)localObject).add(localInnerProducer));
        paramSubscriber.add(localInnerProducer);
        paramSubscriber.setProducer(localInnerProducer);
      }
    }
    , paramObservable, localAtomicReference);
  }

  public void connect(Action1<? super Subscription> paramAction1)
  {
    boolean bool = true;
    Object localObject;
    while (true)
    {
      localObject = (PublishSubscriber)this.current.get();
      if ((localObject != null) && (!((PublishSubscriber)localObject).isUnsubscribed()))
        break;
      PublishSubscriber localPublishSubscriber = new PublishSubscriber(this.current);
      localPublishSubscriber.init();
      if (!this.current.compareAndSet(localObject, localPublishSubscriber))
        continue;
      localObject = localPublishSubscriber;
    }
    if ((!((PublishSubscriber)localObject).shouldConnect.get()) && (((PublishSubscriber)localObject).shouldConnect.compareAndSet(false, bool)));
    while (true)
    {
      paramAction1.call(localObject);
      if (bool)
        this.source.unsafeSubscribe((Subscriber)localObject);
      return;
      bool = false;
    }
  }

  static final class InnerProducer<T> extends AtomicLong
    implements Producer, Subscription
  {
    static final long NOT_REQUESTED = -4611686018427387904L;
    static final long UNSUBSCRIBED = -9223372036854775808L;
    private static final long serialVersionUID = -4453897557930727610L;
    final Subscriber<? super T> child;
    final OperatorPublish.PublishSubscriber<T> parent;

    public InnerProducer(OperatorPublish.PublishSubscriber<T> paramPublishSubscriber, Subscriber<? super T> paramSubscriber)
    {
      this.parent = paramPublishSubscriber;
      this.child = paramSubscriber;
      lazySet(-4611686018427387904L);
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
        if (l1 == -4611686018427387904L)
          throw new IllegalStateException("Produced without request");
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
      while (true)
      {
        long l1 = get();
        if ((l1 == -9223372036854775808L) || ((l1 >= 0L) && (paramLong == 0L)))
          break;
        long l2;
        if (l1 == -4611686018427387904L)
          l2 = paramLong;
        while (compareAndSet(l1, l2))
        {
          this.parent.dispatch();
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
      if ((get() != -9223372036854775808L) && (getAndSet(-9223372036854775808L) != -9223372036854775808L))
      {
        this.parent.remove(this);
        this.parent.dispatch();
      }
    }
  }

  static final class PublishSubscriber<T> extends Subscriber<T>
    implements Subscription
  {
    static final OperatorPublish.InnerProducer[] EMPTY = new OperatorPublish.InnerProducer[0];
    static final OperatorPublish.InnerProducer[] TERMINATED = new OperatorPublish.InnerProducer[0];
    final AtomicReference<PublishSubscriber<T>> current;
    boolean emitting;
    boolean missed;
    final NotificationLite<T> nl;
    final AtomicReference<OperatorPublish.InnerProducer[]> producers;
    final Queue<Object> queue;
    final AtomicBoolean shouldConnect;
    volatile Object terminalEvent;

    public PublishSubscriber(AtomicReference<PublishSubscriber<T>> paramAtomicReference)
    {
      if (UnsafeAccess.isUnsafeAvailable());
      for (Object localObject = new SpscArrayQueue(RxRingBuffer.SIZE); ; localObject = new SynchronizedQueue(RxRingBuffer.SIZE))
      {
        this.queue = ((Queue)localObject);
        this.nl = NotificationLite.instance();
        this.producers = new AtomicReference(EMPTY);
        this.current = paramAtomicReference;
        this.shouldConnect = new AtomicBoolean();
        return;
      }
    }

    boolean add(OperatorPublish.InnerProducer<T> paramInnerProducer)
    {
      if (paramInnerProducer == null)
        throw new NullPointerException();
      OperatorPublish.InnerProducer[] arrayOfInnerProducer1;
      OperatorPublish.InnerProducer[] arrayOfInnerProducer2;
      do
      {
        arrayOfInnerProducer1 = (OperatorPublish.InnerProducer[])this.producers.get();
        if (arrayOfInnerProducer1 == TERMINATED)
          return false;
        int i = arrayOfInnerProducer1.length;
        arrayOfInnerProducer2 = new OperatorPublish.InnerProducer[i + 1];
        System.arraycopy(arrayOfInnerProducer1, 0, arrayOfInnerProducer2, 0, i);
        arrayOfInnerProducer2[i] = paramInnerProducer;
      }
      while (!this.producers.compareAndSet(arrayOfInnerProducer1, arrayOfInnerProducer2));
      return true;
    }

    boolean checkTerminated(Object paramObject, boolean paramBoolean)
    {
      if (paramObject != null)
      {
        if (this.nl.isCompleted(paramObject))
        {
          if (!paramBoolean)
            break label173;
          this.current.compareAndSet(this, null);
          try
          {
            OperatorPublish.InnerProducer[] arrayOfInnerProducer2 = (OperatorPublish.InnerProducer[])this.producers.getAndSet(TERMINATED);
            int k = arrayOfInnerProducer2.length;
            for (int m = 0; m < k; m++)
              arrayOfInnerProducer2[m].child.onCompleted();
            return true;
          }
          finally
          {
            unsubscribe();
          }
        }
        Throwable localThrowable = this.nl.getError(paramObject);
        this.current.compareAndSet(this, null);
        try
        {
          OperatorPublish.InnerProducer[] arrayOfInnerProducer1 = (OperatorPublish.InnerProducer[])this.producers.getAndSet(TERMINATED);
          int i = arrayOfInnerProducer1.length;
          for (int j = 0; j < i; j++)
            arrayOfInnerProducer1[j].child.onError(localThrowable);
          return true;
        }
        finally
        {
          unsubscribe();
        }
      }
      label173: return false;
    }

    // ERROR //
    void dispatch()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 137	rx/internal/operators/OperatorPublish$PublishSubscriber:emitting	Z
      //   6: ifeq +11 -> 17
      //   9: aload_0
      //   10: iconst_1
      //   11: putfield 139	rx/internal/operators/OperatorPublish$PublishSubscriber:missed	Z
      //   14: aload_0
      //   15: monitorexit
      //   16: return
      //   17: aload_0
      //   18: iconst_1
      //   19: putfield 137	rx/internal/operators/OperatorPublish$PublishSubscriber:emitting	Z
      //   22: aload_0
      //   23: iconst_0
      //   24: putfield 139	rx/internal/operators/OperatorPublish$PublishSubscriber:missed	Z
      //   27: aload_0
      //   28: monitorexit
      //   29: iconst_0
      //   30: istore_2
      //   31: aload_0
      //   32: getfield 141	rx/internal/operators/OperatorPublish$PublishSubscriber:terminalEvent	Ljava/lang/Object;
      //   35: astore 5
      //   37: aload_0
      //   38: getfield 59	rx/internal/operators/OperatorPublish$PublishSubscriber:queue	Ljava/util/Queue;
      //   41: invokeinterface 146 1 0
      //   46: istore 6
      //   48: aload_0
      //   49: aload 5
      //   51: iload 6
      //   53: invokevirtual 148	rx/internal/operators/OperatorPublish$PublishSubscriber:checkTerminated	(Ljava/lang/Object;Z)Z
      //   56: istore 7
      //   58: iload 7
      //   60: ifeq +29 -> 89
      //   63: iconst_1
      //   64: ifne +464 -> 528
      //   67: aload_0
      //   68: monitorenter
      //   69: aload_0
      //   70: iconst_0
      //   71: putfield 137	rx/internal/operators/OperatorPublish$PublishSubscriber:emitting	Z
      //   74: aload_0
      //   75: monitorexit
      //   76: return
      //   77: astore 40
      //   79: aload_0
      //   80: monitorexit
      //   81: aload 40
      //   83: athrow
      //   84: astore_1
      //   85: aload_0
      //   86: monitorexit
      //   87: aload_1
      //   88: athrow
      //   89: iload 6
      //   91: ifne +277 -> 368
      //   94: aload_0
      //   95: getfield 74	rx/internal/operators/OperatorPublish$PublishSubscriber:producers	Ljava/util/concurrent/atomic/AtomicReference;
      //   98: invokevirtual 93	java/util/concurrent/atomic/AtomicReference:get	()Ljava/lang/Object;
      //   101: checkcast 94	[Lrx/internal/operators/OperatorPublish$InnerProducer;
      //   104: astore 10
      //   106: aload 10
      //   108: arraylength
      //   109: istore 11
      //   111: ldc2_w 149
      //   114: lstore 12
      //   116: iconst_0
      //   117: istore 14
      //   119: aload 10
      //   121: arraylength
      //   122: istore 15
      //   124: iconst_0
      //   125: istore 16
      //   127: iconst_0
      //   128: istore_2
      //   129: iload 16
      //   131: iload 15
      //   133: if_icmpge +38 -> 171
      //   136: aload 10
      //   138: iload 16
      //   140: aaload
      //   141: invokevirtual 153	rx/internal/operators/OperatorPublish$InnerProducer:get	()J
      //   144: lstore 17
      //   146: lload 17
      //   148: lconst_0
      //   149: lcmp
      //   150: istore 19
      //   152: iconst_0
      //   153: istore_2
      //   154: iload 19
      //   156: iflt +379 -> 535
      //   159: lload 12
      //   161: lload 17
      //   163: invokestatic 159	java/lang/Math:min	(JJ)J
      //   166: lstore 12
      //   168: goto +361 -> 529
      //   171: iload 11
      //   173: iload 14
      //   175: if_icmpne +90 -> 265
      //   178: aload_0
      //   179: getfield 141	rx/internal/operators/OperatorPublish$PublishSubscriber:terminalEvent	Ljava/lang/Object;
      //   182: astore 20
      //   184: aload_0
      //   185: getfield 59	rx/internal/operators/OperatorPublish$PublishSubscriber:queue	Ljava/util/Queue;
      //   188: invokeinterface 162 1 0
      //   193: ifnonnull +42 -> 235
      //   196: iconst_1
      //   197: istore 21
      //   199: aload_0
      //   200: aload 20
      //   202: iload 21
      //   204: invokevirtual 148	rx/internal/operators/OperatorPublish$PublishSubscriber:checkTerminated	(Ljava/lang/Object;Z)Z
      //   207: istore 22
      //   209: iload 22
      //   211: ifeq +30 -> 241
      //   214: iconst_1
      //   215: ifne +313 -> 528
      //   218: aload_0
      //   219: monitorenter
      //   220: aload_0
      //   221: iconst_0
      //   222: putfield 137	rx/internal/operators/OperatorPublish$PublishSubscriber:emitting	Z
      //   225: aload_0
      //   226: monitorexit
      //   227: return
      //   228: astore 23
      //   230: aload_0
      //   231: monitorexit
      //   232: aload 23
      //   234: athrow
      //   235: iconst_0
      //   236: istore 21
      //   238: goto -39 -> 199
      //   241: aload_0
      //   242: lconst_1
      //   243: invokevirtual 166	rx/internal/operators/OperatorPublish$PublishSubscriber:request	(J)V
      //   246: goto -215 -> 31
      //   249: astore_3
      //   250: iload_2
      //   251: ifne +12 -> 263
      //   254: aload_0
      //   255: monitorenter
      //   256: aload_0
      //   257: iconst_0
      //   258: putfield 137	rx/internal/operators/OperatorPublish$PublishSubscriber:emitting	Z
      //   261: aload_0
      //   262: monitorexit
      //   263: aload_3
      //   264: athrow
      //   265: iconst_0
      //   266: istore 24
      //   268: iload 24
      //   270: i2l
      //   271: lload 12
      //   273: lcmp
      //   274: ifge +75 -> 349
      //   277: aload_0
      //   278: getfield 141	rx/internal/operators/OperatorPublish$PublishSubscriber:terminalEvent	Ljava/lang/Object;
      //   281: astore 27
      //   283: aload_0
      //   284: getfield 59	rx/internal/operators/OperatorPublish$PublishSubscriber:queue	Ljava/util/Queue;
      //   287: invokeinterface 162 1 0
      //   292: astore 28
      //   294: aload 28
      //   296: ifnonnull +42 -> 338
      //   299: iconst_1
      //   300: istore 6
      //   302: aload_0
      //   303: aload 27
      //   305: iload 6
      //   307: invokevirtual 148	rx/internal/operators/OperatorPublish$PublishSubscriber:checkTerminated	(Ljava/lang/Object;Z)Z
      //   310: istore 29
      //   312: iload 29
      //   314: ifeq +30 -> 344
      //   317: iconst_1
      //   318: ifne +210 -> 528
      //   321: aload_0
      //   322: monitorenter
      //   323: aload_0
      //   324: iconst_0
      //   325: putfield 137	rx/internal/operators/OperatorPublish$PublishSubscriber:emitting	Z
      //   328: aload_0
      //   329: monitorexit
      //   330: return
      //   331: astore 39
      //   333: aload_0
      //   334: monitorexit
      //   335: aload 39
      //   337: athrow
      //   338: iconst_0
      //   339: istore 6
      //   341: goto -39 -> 302
      //   344: iload 6
      //   346: ifeq +61 -> 407
      //   349: iload 24
      //   351: ifle +199 -> 550
      //   354: iload 24
      //   356: i2l
      //   357: lstore 25
      //   359: aload_0
      //   360: lload 25
      //   362: invokevirtual 166	rx/internal/operators/OperatorPublish$PublishSubscriber:request	(J)V
      //   365: goto +185 -> 550
      //   368: aload_0
      //   369: monitorenter
      //   370: aload_0
      //   371: getfield 139	rx/internal/operators/OperatorPublish$PublishSubscriber:missed	Z
      //   374: ifne +130 -> 504
      //   377: aload_0
      //   378: iconst_0
      //   379: putfield 137	rx/internal/operators/OperatorPublish$PublishSubscriber:emitting	Z
      //   382: iconst_1
      //   383: istore_2
      //   384: aload_0
      //   385: monitorexit
      //   386: iload_2
      //   387: ifne +141 -> 528
      //   390: aload_0
      //   391: monitorenter
      //   392: aload_0
      //   393: iconst_0
      //   394: putfield 137	rx/internal/operators/OperatorPublish$PublishSubscriber:emitting	Z
      //   397: aload_0
      //   398: monitorexit
      //   399: return
      //   400: astore 9
      //   402: aload_0
      //   403: monitorexit
      //   404: aload 9
      //   406: athrow
      //   407: aload_0
      //   408: getfield 67	rx/internal/operators/OperatorPublish$PublishSubscriber:nl	Lrx/internal/operators/NotificationLite;
      //   411: aload 28
      //   413: invokevirtual 169	rx/internal/operators/NotificationLite:getValue	(Ljava/lang/Object;)Ljava/lang/Object;
      //   416: astore 30
      //   418: aload 10
      //   420: arraylength
      //   421: istore 31
      //   423: iconst_0
      //   424: istore 32
      //   426: iconst_0
      //   427: istore_2
      //   428: iload 32
      //   430: iload 31
      //   432: if_icmpge +66 -> 498
      //   435: aload 10
      //   437: iload 32
      //   439: aaload
      //   440: astore 33
      //   442: aload 33
      //   444: invokevirtual 153	rx/internal/operators/OperatorPublish$InnerProducer:get	()J
      //   447: lstore 34
      //   449: lload 34
      //   451: lconst_0
      //   452: lcmp
      //   453: ifle +112 -> 565
      //   456: aload 33
      //   458: getfield 118	rx/internal/operators/OperatorPublish$InnerProducer:child	Lrx/Subscriber;
      //   461: aload 30
      //   463: invokevirtual 172	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   466: aload 33
      //   468: lconst_1
      //   469: invokevirtual 176	rx/internal/operators/OperatorPublish$InnerProducer:produced	(J)J
      //   472: pop2
      //   473: goto +92 -> 565
      //   476: astore 36
      //   478: aload 33
      //   480: invokevirtual 177	rx/internal/operators/OperatorPublish$InnerProducer:unsubscribe	()V
      //   483: aload 36
      //   485: aload 33
      //   487: getfield 118	rx/internal/operators/OperatorPublish$InnerProducer:child	Lrx/Subscriber;
      //   490: aload 30
      //   492: invokestatic 183	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;Ljava/lang/Object;)V
      //   495: goto +70 -> 565
      //   498: iinc 24 1
      //   501: goto -233 -> 268
      //   504: aload_0
      //   505: iconst_0
      //   506: putfield 139	rx/internal/operators/OperatorPublish$PublishSubscriber:missed	Z
      //   509: aload_0
      //   510: monitorexit
      //   511: goto -480 -> 31
      //   514: astore 8
      //   516: aload_0
      //   517: monitorexit
      //   518: aload 8
      //   520: athrow
      //   521: astore 4
      //   523: aload_0
      //   524: monitorexit
      //   525: aload 4
      //   527: athrow
      //   528: return
      //   529: iinc 16 1
      //   532: goto -405 -> 127
      //   535: lload 17
      //   537: ldc2_w 184
      //   540: lcmp
      //   541: ifne -12 -> 529
      //   544: iinc 14 1
      //   547: goto -18 -> 529
      //   550: lload 12
      //   552: lconst_0
      //   553: lcmp
      //   554: ifeq -186 -> 368
      //   557: iload 6
      //   559: ifeq -528 -> 31
      //   562: goto -194 -> 368
      //   565: iinc 32 1
      //   568: goto -142 -> 426
      //
      // Exception table:
      //   from	to	target	type
      //   69	76	77	finally
      //   79	81	77	finally
      //   2	16	84	finally
      //   17	29	84	finally
      //   85	87	84	finally
      //   220	227	228	finally
      //   230	232	228	finally
      //   31	58	249	finally
      //   94	111	249	finally
      //   119	124	249	finally
      //   136	146	249	finally
      //   159	168	249	finally
      //   178	196	249	finally
      //   199	209	249	finally
      //   241	246	249	finally
      //   277	294	249	finally
      //   302	312	249	finally
      //   359	365	249	finally
      //   368	370	249	finally
      //   407	423	249	finally
      //   435	449	249	finally
      //   456	466	249	finally
      //   466	473	249	finally
      //   478	495	249	finally
      //   518	521	249	finally
      //   323	330	331	finally
      //   333	335	331	finally
      //   392	399	400	finally
      //   402	404	400	finally
      //   456	466	476	java/lang/Throwable
      //   370	382	514	finally
      //   384	386	514	finally
      //   504	511	514	finally
      //   516	518	514	finally
      //   256	263	521	finally
      //   523	525	521	finally
    }

    void init()
    {
      add(Subscriptions.create(new Action0()
      {
        public void call()
        {
          OperatorPublish.PublishSubscriber.this.producers.getAndSet(OperatorPublish.PublishSubscriber.TERMINATED);
          OperatorPublish.PublishSubscriber.this.current.compareAndSet(OperatorPublish.PublishSubscriber.this, null);
        }
      }));
    }

    public void onCompleted()
    {
      if (this.terminalEvent == null)
      {
        this.terminalEvent = this.nl.completed();
        dispatch();
      }
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.terminalEvent == null)
      {
        this.terminalEvent = this.nl.error(paramThrowable);
        dispatch();
      }
    }

    public void onNext(T paramT)
    {
      if (!this.queue.offer(this.nl.next(paramT)))
      {
        onError(new MissingBackpressureException());
        return;
      }
      dispatch();
    }

    public void onStart()
    {
      request(RxRingBuffer.SIZE);
    }

    void remove(OperatorPublish.InnerProducer<T> paramInnerProducer)
    {
      OperatorPublish.InnerProducer[] arrayOfInnerProducer1 = (OperatorPublish.InnerProducer[])this.producers.get();
      if ((arrayOfInnerProducer1 == EMPTY) || (arrayOfInnerProducer1 == TERMINATED));
      int i;
      int j;
      int k;
      label35: OperatorPublish.InnerProducer[] arrayOfInnerProducer2;
      while (true)
      {
        return;
        i = -1;
        j = arrayOfInnerProducer1.length;
        k = 0;
        if (k < j)
        {
          if (!arrayOfInnerProducer1[k].equals(paramInnerProducer))
            break;
          i = k;
        }
        else
        {
          if (i < 0)
            continue;
          if (j != 1)
            break label91;
          arrayOfInnerProducer2 = EMPTY;
        }
      }
      while (this.producers.compareAndSet(arrayOfInnerProducer1, arrayOfInnerProducer2))
      {
        return;
        k++;
        break label35;
        label91: arrayOfInnerProducer2 = new OperatorPublish.InnerProducer[j - 1];
        System.arraycopy(arrayOfInnerProducer1, 0, arrayOfInnerProducer2, 0, i);
        System.arraycopy(arrayOfInnerProducer1, i + 1, arrayOfInnerProducer2, i, -1 + (j - i));
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorPublish
 * JD-Core Version:    0.6.0
 */