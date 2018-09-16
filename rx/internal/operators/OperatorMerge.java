package rx.internal.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.MissingBackpressureException;
import rx.exceptions.OnErrorThrowable;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.ScalarSynchronousObservable;
import rx.internal.util.atomic.SpscAtomicArrayQueue;
import rx.internal.util.atomic.SpscExactAtomicArrayQueue;
import rx.internal.util.atomic.SpscUnboundedAtomicArrayQueue;
import rx.internal.util.unsafe.Pow2;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.subscriptions.CompositeSubscription;

public final class OperatorMerge<T>
  implements Observable.Operator<T, Observable<? extends T>>
{
  final boolean delayErrors;
  final int maxConcurrent;

  OperatorMerge(boolean paramBoolean, int paramInt)
  {
    this.delayErrors = paramBoolean;
    this.maxConcurrent = paramInt;
  }

  public static <T> OperatorMerge<T> instance(boolean paramBoolean)
  {
    if (paramBoolean)
      return HolderDelayErrors.INSTANCE;
    return HolderNoDelay.INSTANCE;
  }

  public static <T> OperatorMerge<T> instance(boolean paramBoolean, int paramInt)
  {
    if (paramInt <= 0)
      throw new IllegalArgumentException("maxConcurrent > 0 required but it was " + paramInt);
    if (paramInt == 2147483647)
      return instance(paramBoolean);
    return new OperatorMerge(paramBoolean, paramInt);
  }

  public Subscriber<Observable<? extends T>> call(Subscriber<? super T> paramSubscriber)
  {
    MergeSubscriber localMergeSubscriber = new MergeSubscriber(paramSubscriber, this.delayErrors, this.maxConcurrent);
    MergeProducer localMergeProducer = new MergeProducer(localMergeSubscriber);
    localMergeSubscriber.producer = localMergeProducer;
    paramSubscriber.add(localMergeSubscriber);
    paramSubscriber.setProducer(localMergeProducer);
    return localMergeSubscriber;
  }

  static final class HolderDelayErrors
  {
    static final OperatorMerge<Object> INSTANCE = new OperatorMerge(true, 2147483647);
  }

  static final class HolderNoDelay
  {
    static final OperatorMerge<Object> INSTANCE = new OperatorMerge(false, 2147483647);
  }

  static final class InnerSubscriber<T> extends Subscriber<T>
  {
    static final int LIMIT = RxRingBuffer.SIZE / 4;
    volatile boolean done;
    final long id;
    int outstanding;
    final OperatorMerge.MergeSubscriber<T> parent;
    volatile RxRingBuffer queue;

    public InnerSubscriber(OperatorMerge.MergeSubscriber<T> paramMergeSubscriber, long paramLong)
    {
      this.parent = paramMergeSubscriber;
      this.id = paramLong;
    }

    public void onCompleted()
    {
      this.done = true;
      this.parent.emit();
    }

    public void onError(Throwable paramThrowable)
    {
      this.done = true;
      this.parent.getOrCreateErrorQueue().offer(paramThrowable);
      this.parent.emit();
    }

    public void onNext(T paramT)
    {
      this.parent.tryEmit(this, paramT);
    }

    public void onStart()
    {
      this.outstanding = RxRingBuffer.SIZE;
      request(RxRingBuffer.SIZE);
    }

    public void requestMore(long paramLong)
    {
      int i = this.outstanding - (int)paramLong;
      if (i > LIMIT)
        this.outstanding = i;
      int j;
      do
      {
        return;
        this.outstanding = RxRingBuffer.SIZE;
        j = RxRingBuffer.SIZE - i;
      }
      while (j <= 0);
      request(j);
    }
  }

  static final class MergeProducer<T> extends AtomicLong
    implements Producer
  {
    private static final long serialVersionUID = -1214379189873595503L;
    final OperatorMerge.MergeSubscriber<T> subscriber;

    public MergeProducer(OperatorMerge.MergeSubscriber<T> paramMergeSubscriber)
    {
      this.subscriber = paramMergeSubscriber;
    }

    public long produced(int paramInt)
    {
      return addAndGet(-paramInt);
    }

    public void request(long paramLong)
    {
      if (paramLong > 0L)
        if (get() != 9223372036854775807L);
      do
      {
        return;
        BackpressureUtils.getAndAddRequest(this, paramLong);
        this.subscriber.emit();
        return;
      }
      while (paramLong >= 0L);
      throw new IllegalArgumentException("n >= 0 required");
    }
  }

  static final class MergeSubscriber<T> extends Subscriber<Observable<? extends T>>
  {
    static final OperatorMerge.InnerSubscriber<?>[] EMPTY = new OperatorMerge.InnerSubscriber[0];
    final Subscriber<? super T> child;
    final boolean delayErrors;
    volatile boolean done;
    boolean emitting;
    volatile ConcurrentLinkedQueue<Throwable> errors;
    final Object innerGuard;
    volatile OperatorMerge.InnerSubscriber<?>[] innerSubscribers;
    long lastId;
    int lastIndex;
    final int maxConcurrent;
    boolean missed;
    final NotificationLite<T> nl;
    OperatorMerge.MergeProducer<T> producer;
    volatile Queue<Object> queue;
    int scalarEmissionCount;
    final int scalarEmissionLimit;
    volatile CompositeSubscription subscriptions;
    long uniqueId;

    public MergeSubscriber(Subscriber<? super T> paramSubscriber, boolean paramBoolean, int paramInt)
    {
      this.child = paramSubscriber;
      this.delayErrors = paramBoolean;
      this.maxConcurrent = paramInt;
      this.nl = NotificationLite.instance();
      this.innerGuard = new Object();
      this.innerSubscribers = EMPTY;
      if (paramInt == 2147483647)
      {
        this.scalarEmissionLimit = 2147483647;
        request(9223372036854775807L);
        return;
      }
      this.scalarEmissionLimit = Math.max(1, paramInt >> 1);
      request(paramInt);
    }

    private void reportError()
    {
      ArrayList localArrayList = new ArrayList(this.errors);
      if (localArrayList.size() == 1)
      {
        this.child.onError((Throwable)localArrayList.get(0));
        return;
      }
      this.child.onError(new CompositeException(localArrayList));
    }

    void addInner(OperatorMerge.InnerSubscriber<T> paramInnerSubscriber)
    {
      getOrCreateComposite().add(paramInnerSubscriber);
      synchronized (this.innerGuard)
      {
        OperatorMerge.InnerSubscriber[] arrayOfInnerSubscriber1 = this.innerSubscribers;
        int i = arrayOfInnerSubscriber1.length;
        OperatorMerge.InnerSubscriber[] arrayOfInnerSubscriber2 = new OperatorMerge.InnerSubscriber[i + 1];
        System.arraycopy(arrayOfInnerSubscriber1, 0, arrayOfInnerSubscriber2, 0, i);
        arrayOfInnerSubscriber2[i] = paramInnerSubscriber;
        this.innerSubscribers = arrayOfInnerSubscriber2;
        return;
      }
    }

    boolean checkTerminate()
    {
      if (this.child.isUnsubscribed())
        return true;
      ConcurrentLinkedQueue localConcurrentLinkedQueue = this.errors;
      if ((!this.delayErrors) && (localConcurrentLinkedQueue != null) && (!localConcurrentLinkedQueue.isEmpty()))
        try
        {
          reportError();
          return true;
        }
        finally
        {
          unsubscribe();
        }
      return false;
    }

    void emit()
    {
      monitorenter;
      try
      {
        if (this.emitting)
        {
          this.missed = true;
          return;
        }
        this.emitting = true;
        monitorexit;
        emitLoop();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    void emitEmpty()
    {
      int i = 1 + this.scalarEmissionCount;
      if (i == this.scalarEmissionLimit)
      {
        this.scalarEmissionCount = 0;
        requestMore(i);
        return;
      }
      this.scalarEmissionCount = i;
    }

    // ERROR //
    void emitLoop()
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_1
      //   2: aload_0
      //   3: getfield 53	rx/internal/operators/OperatorMerge$MergeSubscriber:child	Lrx/Subscriber;
      //   6: astore 4
      //   8: aload_0
      //   9: invokevirtual 163	rx/internal/operators/OperatorMerge$MergeSubscriber:checkTerminate	()Z
      //   12: istore 5
      //   14: iload 5
      //   16: ifeq +24 -> 40
      //   19: iconst_1
      //   20: ifne +917 -> 937
      //   23: aload_0
      //   24: monitorenter
      //   25: aload_0
      //   26: iconst_0
      //   27: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   30: aload_0
      //   31: monitorexit
      //   32: return
      //   33: astore 54
      //   35: aload_0
      //   36: monitorexit
      //   37: aload 54
      //   39: athrow
      //   40: aload_0
      //   41: getfield 165	rx/internal/operators/OperatorMerge$MergeSubscriber:queue	Ljava/util/Queue;
      //   44: astore 6
      //   46: aload_0
      //   47: getfield 167	rx/internal/operators/OperatorMerge$MergeSubscriber:producer	Lrx/internal/operators/OperatorMerge$MergeProducer;
      //   50: invokevirtual 172	rx/internal/operators/OperatorMerge$MergeProducer:get	()J
      //   53: lstore 7
      //   55: lload 7
      //   57: ldc2_w 76
      //   60: lcmp
      //   61: ifne +63 -> 124
      //   64: iconst_1
      //   65: istore 9
      //   67: goto +871 -> 938
      //   70: lload 7
      //   72: lconst_0
      //   73: lcmp
      //   74: istore 46
      //   76: iconst_0
      //   77: istore_1
      //   78: iload 46
      //   80: ifle +55 -> 135
      //   83: aload 6
      //   85: invokeinterface 176 1 0
      //   90: astore 44
      //   92: aload_0
      //   93: invokevirtual 163	rx/internal/operators/OperatorMerge$MergeSubscriber:checkTerminate	()Z
      //   96: istore 47
      //   98: iload 47
      //   100: ifeq +30 -> 130
      //   103: iconst_1
      //   104: ifne +833 -> 937
      //   107: aload_0
      //   108: monitorenter
      //   109: aload_0
      //   110: iconst_0
      //   111: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   114: aload_0
      //   115: monitorexit
      //   116: return
      //   117: astore 53
      //   119: aload_0
      //   120: monitorexit
      //   121: aload 53
      //   123: athrow
      //   124: iconst_0
      //   125: istore 9
      //   127: goto +811 -> 938
      //   130: aload 44
      //   132: ifnonnull +129 -> 261
      //   135: iload 45
      //   137: ifle +13 -> 150
      //   140: iload 9
      //   142: ifeq +237 -> 379
      //   145: ldc2_w 76
      //   148: lstore 7
      //   150: lload 7
      //   152: lconst_0
      //   153: lcmp
      //   154: ifeq +8 -> 162
      //   157: aload 44
      //   159: ifnonnull +787 -> 946
      //   162: aload_0
      //   163: getfield 178	rx/internal/operators/OperatorMerge$MergeSubscriber:done	Z
      //   166: istore 11
      //   168: aload_0
      //   169: getfield 165	rx/internal/operators/OperatorMerge$MergeSubscriber:queue	Ljava/util/Queue;
      //   172: astore 12
      //   174: aload_0
      //   175: getfield 72	rx/internal/operators/OperatorMerge$MergeSubscriber:innerSubscribers	[Lrx/internal/operators/OperatorMerge$InnerSubscriber;
      //   178: astore 13
      //   180: aload 13
      //   182: arraylength
      //   183: istore 14
      //   185: iload 11
      //   187: ifeq +213 -> 400
      //   190: iconst_0
      //   191: istore_1
      //   192: aload 12
      //   194: ifnull +13 -> 207
      //   197: aload 12
      //   199: invokeinterface 142 1 0
      //   204: ifeq +196 -> 400
      //   207: iload 14
      //   209: ifne +191 -> 400
      //   212: aload_0
      //   213: getfield 92	rx/internal/operators/OperatorMerge$MergeSubscriber:errors	Ljava/util/concurrent/ConcurrentLinkedQueue;
      //   216: astore 15
      //   218: iconst_0
      //   219: istore_1
      //   220: aload 15
      //   222: ifnull +13 -> 235
      //   225: aload 15
      //   227: invokeinterface 142 1 0
      //   232: ifeq +161 -> 393
      //   235: aload 4
      //   237: invokevirtual 181	rx/Subscriber:onCompleted	()V
      //   240: iconst_1
      //   241: ifne +696 -> 937
      //   244: aload_0
      //   245: monitorenter
      //   246: aload_0
      //   247: iconst_0
      //   248: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   251: aload_0
      //   252: monitorexit
      //   253: return
      //   254: astore 16
      //   256: aload_0
      //   257: monitorexit
      //   258: aload 16
      //   260: athrow
      //   261: aload_0
      //   262: getfield 65	rx/internal/operators/OperatorMerge$MergeSubscriber:nl	Lrx/internal/operators/NotificationLite;
      //   265: aload 44
      //   267: invokevirtual 185	rx/internal/operators/NotificationLite:getValue	(Ljava/lang/Object;)Ljava/lang/Object;
      //   270: astore 48
      //   272: aload 4
      //   274: aload 48
      //   276: invokevirtual 189	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   279: iinc 10 1
      //   282: iinc 45 1
      //   285: lload 7
      //   287: lconst_1
      //   288: lsub
      //   289: lstore 7
      //   291: goto -221 -> 70
      //   294: astore 49
      //   296: aload_0
      //   297: getfield 55	rx/internal/operators/OperatorMerge$MergeSubscriber:delayErrors	Z
      //   300: istore 50
      //   302: iconst_0
      //   303: istore_1
      //   304: iload 50
      //   306: ifne +42 -> 348
      //   309: aload 49
      //   311: invokestatic 194	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
      //   314: iconst_1
      //   315: istore_1
      //   316: aload_0
      //   317: invokevirtual 147	rx/internal/operators/OperatorMerge$MergeSubscriber:unsubscribe	()V
      //   320: aload 4
      //   322: aload 49
      //   324: invokevirtual 111	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
      //   327: iload_1
      //   328: ifne +609 -> 937
      //   331: aload_0
      //   332: monitorenter
      //   333: aload_0
      //   334: iconst_0
      //   335: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   338: aload_0
      //   339: monitorexit
      //   340: return
      //   341: astore 51
      //   343: aload_0
      //   344: monitorexit
      //   345: aload 51
      //   347: athrow
      //   348: aload_0
      //   349: invokevirtual 198	rx/internal/operators/OperatorMerge$MergeSubscriber:getOrCreateErrorQueue	()Ljava/util/Queue;
      //   352: aload 49
      //   354: invokeinterface 202 2 0
      //   359: pop
      //   360: goto -81 -> 279
      //   363: astore_2
      //   364: iload_1
      //   365: ifne +12 -> 377
      //   368: aload_0
      //   369: monitorenter
      //   370: aload_0
      //   371: iconst_0
      //   372: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   375: aload_0
      //   376: monitorexit
      //   377: aload_2
      //   378: athrow
      //   379: aload_0
      //   380: getfield 167	rx/internal/operators/OperatorMerge$MergeSubscriber:producer	Lrx/internal/operators/OperatorMerge$MergeProducer;
      //   383: iload 45
      //   385: invokevirtual 206	rx/internal/operators/OperatorMerge$MergeProducer:produced	(I)J
      //   388: lstore 7
      //   390: goto -240 -> 150
      //   393: aload_0
      //   394: invokespecial 144	rx/internal/operators/OperatorMerge$MergeSubscriber:reportError	()V
      //   397: goto -157 -> 240
      //   400: iconst_0
      //   401: istore 17
      //   403: iload 14
      //   405: ifle +433 -> 838
      //   408: aload_0
      //   409: getfield 208	rx/internal/operators/OperatorMerge$MergeSubscriber:lastId	J
      //   412: lstore 18
      //   414: aload_0
      //   415: getfield 210	rx/internal/operators/OperatorMerge$MergeSubscriber:lastIndex	I
      //   418: istore 20
      //   420: iconst_0
      //   421: istore_1
      //   422: iload 14
      //   424: iload 20
      //   426: if_icmple +529 -> 955
      //   429: aload 13
      //   431: iload 20
      //   433: aaload
      //   434: getfield 213	rx/internal/operators/OperatorMerge$InnerSubscriber:id	J
      //   437: lload 18
      //   439: lcmp
      //   440: ifeq +535 -> 975
      //   443: goto +512 -> 955
      //   446: iconst_0
      //   447: istore_1
      //   448: iload 22
      //   450: iload 14
      //   452: if_icmpge +17 -> 469
      //   455: aload 13
      //   457: iload 21
      //   459: aaload
      //   460: getfield 213	rx/internal/operators/OperatorMerge$InnerSubscriber:id	J
      //   463: lload 18
      //   465: lcmp
      //   466: ifne +69 -> 535
      //   469: iload 21
      //   471: istore 20
      //   473: aload_0
      //   474: iload 21
      //   476: putfield 210	rx/internal/operators/OperatorMerge$MergeSubscriber:lastIndex	I
      //   479: aload_0
      //   480: aload 13
      //   482: iload 21
      //   484: aaload
      //   485: getfield 213	rx/internal/operators/OperatorMerge$InnerSubscriber:id	J
      //   488: putfield 208	rx/internal/operators/OperatorMerge$MergeSubscriber:lastId	J
      //   491: goto +484 -> 975
      //   494: iconst_0
      //   495: istore_1
      //   496: iload 24
      //   498: iload 14
      //   500: if_icmpge +320 -> 820
      //   503: aload_0
      //   504: invokevirtual 163	rx/internal/operators/OperatorMerge$MergeSubscriber:checkTerminate	()Z
      //   507: istore 25
      //   509: iload 25
      //   511: ifeq +43 -> 554
      //   514: iconst_1
      //   515: ifne +422 -> 937
      //   518: aload_0
      //   519: monitorenter
      //   520: aload_0
      //   521: iconst_0
      //   522: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   525: aload_0
      //   526: monitorexit
      //   527: return
      //   528: astore 43
      //   530: aload_0
      //   531: monitorexit
      //   532: aload 43
      //   534: athrow
      //   535: iinc 21 1
      //   538: iload 21
      //   540: iload 14
      //   542: if_icmpne +6 -> 548
      //   545: iconst_0
      //   546: istore 21
      //   548: iinc 22 1
      //   551: goto -105 -> 446
      //   554: aload 13
      //   556: iload 23
      //   558: aaload
      //   559: astore 26
      //   561: aconst_null
      //   562: astore 27
      //   564: goto +424 -> 988
      //   567: lload 7
      //   569: lconst_0
      //   570: lcmp
      //   571: istore 29
      //   573: iconst_0
      //   574: istore_1
      //   575: iload 29
      //   577: ifle +47 -> 624
      //   580: aload_0
      //   581: invokevirtual 163	rx/internal/operators/OperatorMerge$MergeSubscriber:checkTerminate	()Z
      //   584: istore 30
      //   586: iload 30
      //   588: ifeq +24 -> 612
      //   591: iconst_1
      //   592: ifne +345 -> 937
      //   595: aload_0
      //   596: monitorenter
      //   597: aload_0
      //   598: iconst_0
      //   599: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   602: aload_0
      //   603: monitorexit
      //   604: return
      //   605: astore 42
      //   607: aload_0
      //   608: monitorexit
      //   609: aload 42
      //   611: athrow
      //   612: aload 26
      //   614: getfield 216	rx/internal/operators/OperatorMerge$InnerSubscriber:queue	Lrx/internal/util/RxRingBuffer;
      //   617: astore 31
      //   619: aload 31
      //   621: ifnonnull +107 -> 728
      //   624: iload 28
      //   626: ifle +368 -> 994
      //   629: iload 9
      //   631: ifne +378 -> 1009
      //   634: aload_0
      //   635: getfield 167	rx/internal/operators/OperatorMerge$MergeSubscriber:producer	Lrx/internal/operators/OperatorMerge$MergeProducer;
      //   638: iload 28
      //   640: invokevirtual 206	rx/internal/operators/OperatorMerge$MergeProducer:produced	(I)J
      //   643: lstore 7
      //   645: aload 26
      //   647: iload 28
      //   649: i2l
      //   650: invokevirtual 217	rx/internal/operators/OperatorMerge$InnerSubscriber:requestMore	(J)V
      //   653: goto +341 -> 994
      //   656: aload 26
      //   658: getfield 218	rx/internal/operators/OperatorMerge$InnerSubscriber:done	Z
      //   661: istore 36
      //   663: aload 26
      //   665: getfield 216	rx/internal/operators/OperatorMerge$InnerSubscriber:queue	Lrx/internal/util/RxRingBuffer;
      //   668: astore 37
      //   670: iload 36
      //   672: ifeq +351 -> 1023
      //   675: iconst_0
      //   676: istore_1
      //   677: aload 37
      //   679: ifnull +11 -> 690
      //   682: aload 37
      //   684: invokevirtual 221	rx/internal/util/RxRingBuffer:isEmpty	()Z
      //   687: ifeq +336 -> 1023
      //   690: aload_0
      //   691: aload 26
      //   693: invokevirtual 224	rx/internal/operators/OperatorMerge$MergeSubscriber:removeInner	(Lrx/internal/operators/OperatorMerge$InnerSubscriber;)V
      //   696: aload_0
      //   697: invokevirtual 163	rx/internal/operators/OperatorMerge$MergeSubscriber:checkTerminate	()Z
      //   700: istore 38
      //   702: iload 38
      //   704: ifeq +313 -> 1017
      //   707: iconst_1
      //   708: ifne +229 -> 937
      //   711: aload_0
      //   712: monitorenter
      //   713: aload_0
      //   714: iconst_0
      //   715: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   718: aload_0
      //   719: monitorexit
      //   720: return
      //   721: astore 41
      //   723: aload_0
      //   724: monitorexit
      //   725: aload 41
      //   727: athrow
      //   728: aload 31
      //   730: invokevirtual 225	rx/internal/util/RxRingBuffer:poll	()Ljava/lang/Object;
      //   733: astore 27
      //   735: aload 27
      //   737: ifnull -113 -> 624
      //   740: aload_0
      //   741: getfield 65	rx/internal/operators/OperatorMerge$MergeSubscriber:nl	Lrx/internal/operators/NotificationLite;
      //   744: aload 27
      //   746: invokevirtual 185	rx/internal/operators/NotificationLite:getValue	(Ljava/lang/Object;)Ljava/lang/Object;
      //   749: astore 32
      //   751: aload 4
      //   753: aload 32
      //   755: invokevirtual 189	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   758: lload 7
      //   760: lconst_1
      //   761: lsub
      //   762: lstore 7
      //   764: iinc 28 1
      //   767: goto -200 -> 567
      //   770: astore 33
      //   772: iconst_1
      //   773: istore_1
      //   774: aload 33
      //   776: invokestatic 194	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
      //   779: aload 4
      //   781: aload 33
      //   783: invokevirtual 111	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
      //   786: aload_0
      //   787: invokevirtual 147	rx/internal/operators/OperatorMerge$MergeSubscriber:unsubscribe	()V
      //   790: iload_1
      //   791: ifne +146 -> 937
      //   794: aload_0
      //   795: monitorenter
      //   796: aload_0
      //   797: iconst_0
      //   798: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   801: aload_0
      //   802: monitorexit
      //   803: return
      //   804: astore 35
      //   806: aload_0
      //   807: monitorexit
      //   808: aload 35
      //   810: athrow
      //   811: astore 34
      //   813: aload_0
      //   814: invokevirtual 147	rx/internal/operators/OperatorMerge$MergeSubscriber:unsubscribe	()V
      //   817: aload 34
      //   819: athrow
      //   820: aload_0
      //   821: iload 23
      //   823: putfield 210	rx/internal/operators/OperatorMerge$MergeSubscriber:lastIndex	I
      //   826: aload_0
      //   827: aload 13
      //   829: iload 23
      //   831: aaload
      //   832: getfield 213	rx/internal/operators/OperatorMerge$InnerSubscriber:id	J
      //   835: putfield 208	rx/internal/operators/OperatorMerge$MergeSubscriber:lastId	J
      //   838: iload 10
      //   840: ifle +10 -> 850
      //   843: aload_0
      //   844: iload 10
      //   846: i2l
      //   847: invokevirtual 81	rx/internal/operators/OperatorMerge$MergeSubscriber:request	(J)V
      //   850: iconst_0
      //   851: istore_1
      //   852: iload 17
      //   854: ifne -846 -> 8
      //   857: aload_0
      //   858: monitorenter
      //   859: aload_0
      //   860: getfield 152	rx/internal/operators/OperatorMerge$MergeSubscriber:missed	Z
      //   863: ifne +52 -> 915
      //   866: iconst_1
      //   867: istore_1
      //   868: aload_0
      //   869: iconst_0
      //   870: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   873: aload_0
      //   874: monitorexit
      //   875: iload_1
      //   876: ifne +61 -> 937
      //   879: aload_0
      //   880: monitorenter
      //   881: aload_0
      //   882: iconst_0
      //   883: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   886: aload_0
      //   887: monitorexit
      //   888: return
      //   889: astore 40
      //   891: aload_0
      //   892: monitorexit
      //   893: aload 40
      //   895: athrow
      //   896: iinc 23 1
      //   899: iload 23
      //   901: iload 14
      //   903: if_icmpne +6 -> 909
      //   906: iconst_0
      //   907: istore 23
      //   909: iinc 24 1
      //   912: goto -418 -> 494
      //   915: aload_0
      //   916: iconst_0
      //   917: putfield 152	rx/internal/operators/OperatorMerge$MergeSubscriber:missed	Z
      //   920: aload_0
      //   921: monitorexit
      //   922: goto -914 -> 8
      //   925: astore 39
      //   927: aload_0
      //   928: monitorexit
      //   929: aload 39
      //   931: athrow
      //   932: astore_3
      //   933: aload_0
      //   934: monitorexit
      //   935: aload_3
      //   936: athrow
      //   937: return
      //   938: iconst_0
      //   939: istore 10
      //   941: aload 6
      //   943: ifnull -781 -> 162
      //   946: aconst_null
      //   947: astore 44
      //   949: iconst_0
      //   950: istore 45
      //   952: goto -882 -> 70
      //   955: iload 14
      //   957: iload 20
      //   959: if_icmpgt +6 -> 965
      //   962: iconst_0
      //   963: istore 20
      //   965: iload 20
      //   967: istore 21
      //   969: iconst_0
      //   970: istore 22
      //   972: goto -526 -> 446
      //   975: iload 20
      //   977: istore 23
      //   979: iconst_0
      //   980: istore 24
      //   982: iconst_0
      //   983: istore 17
      //   985: goto -491 -> 494
      //   988: iconst_0
      //   989: istore 28
      //   991: goto -424 -> 567
      //   994: lload 7
      //   996: lconst_0
      //   997: lcmp
      //   998: ifeq -342 -> 656
      //   1001: aload 27
      //   1003: ifnonnull -15 -> 988
      //   1006: goto -350 -> 656
      //   1009: ldc2_w 76
      //   1012: lstore 7
      //   1014: goto -369 -> 645
      //   1017: iinc 10 1
      //   1020: iconst_1
      //   1021: istore 17
      //   1023: lload 7
      //   1025: lconst_0
      //   1026: lcmp
      //   1027: ifne -131 -> 896
      //   1030: goto -210 -> 820
      //
      // Exception table:
      //   from	to	target	type
      //   25	32	33	finally
      //   35	37	33	finally
      //   109	116	117	finally
      //   119	121	117	finally
      //   246	253	254	finally
      //   256	258	254	finally
      //   272	279	294	java/lang/Throwable
      //   333	340	341	finally
      //   343	345	341	finally
      //   2	8	363	finally
      //   8	14	363	finally
      //   40	55	363	finally
      //   83	98	363	finally
      //   162	185	363	finally
      //   197	207	363	finally
      //   212	218	363	finally
      //   225	235	363	finally
      //   235	240	363	finally
      //   261	272	363	finally
      //   272	279	363	finally
      //   296	302	363	finally
      //   309	314	363	finally
      //   316	327	363	finally
      //   348	360	363	finally
      //   379	390	363	finally
      //   393	397	363	finally
      //   408	420	363	finally
      //   429	443	363	finally
      //   455	469	363	finally
      //   473	491	363	finally
      //   503	509	363	finally
      //   554	561	363	finally
      //   580	586	363	finally
      //   612	619	363	finally
      //   634	645	363	finally
      //   645	653	363	finally
      //   656	670	363	finally
      //   682	690	363	finally
      //   690	702	363	finally
      //   728	735	363	finally
      //   740	751	363	finally
      //   751	758	363	finally
      //   774	779	363	finally
      //   786	790	363	finally
      //   813	820	363	finally
      //   820	838	363	finally
      //   843	850	363	finally
      //   857	859	363	finally
      //   929	932	363	finally
      //   520	527	528	finally
      //   530	532	528	finally
      //   597	604	605	finally
      //   607	609	605	finally
      //   713	720	721	finally
      //   723	725	721	finally
      //   751	758	770	java/lang/Throwable
      //   796	803	804	finally
      //   806	808	804	finally
      //   779	786	811	finally
      //   881	888	889	finally
      //   891	893	889	finally
      //   859	866	925	finally
      //   868	875	925	finally
      //   915	922	925	finally
      //   927	929	925	finally
      //   370	377	932	finally
      //   933	935	932	finally
    }

    // ERROR //
    protected void emitScalar(T paramT, long paramLong)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore 4
      //   3: aload_0
      //   4: getfield 53	rx/internal/operators/OperatorMerge$MergeSubscriber:child	Lrx/Subscriber;
      //   7: aload_1
      //   8: invokevirtual 189	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   11: lload_2
      //   12: ldc2_w 76
      //   15: lcmp
      //   16: ifeq +12 -> 28
      //   19: aload_0
      //   20: getfield 167	rx/internal/operators/OperatorMerge$MergeSubscriber:producer	Lrx/internal/operators/OperatorMerge$MergeProducer;
      //   23: iconst_1
      //   24: invokevirtual 206	rx/internal/operators/OperatorMerge$MergeProducer:produced	(I)J
      //   27: pop2
      //   28: iconst_1
      //   29: aload_0
      //   30: getfield 158	rx/internal/operators/OperatorMerge$MergeSubscriber:scalarEmissionCount	I
      //   33: iadd
      //   34: istore 11
      //   36: iload 11
      //   38: aload_0
      //   39: getfield 75	rx/internal/operators/OperatorMerge$MergeSubscriber:scalarEmissionLimit	I
      //   42: if_icmpne +139 -> 181
      //   45: aload_0
      //   46: iconst_0
      //   47: putfield 158	rx/internal/operators/OperatorMerge$MergeSubscriber:scalarEmissionCount	I
      //   50: aload_0
      //   51: iload 11
      //   53: i2l
      //   54: invokevirtual 161	rx/internal/operators/OperatorMerge$MergeSubscriber:requestMore	(J)V
      //   57: aload_0
      //   58: monitorenter
      //   59: iconst_1
      //   60: istore 4
      //   62: aload_0
      //   63: getfield 152	rx/internal/operators/OperatorMerge$MergeSubscriber:missed	Z
      //   66: ifne +131 -> 197
      //   69: aload_0
      //   70: iconst_0
      //   71: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   74: aload_0
      //   75: monitorexit
      //   76: iload 4
      //   78: ifne +12 -> 90
      //   81: aload_0
      //   82: monitorenter
      //   83: aload_0
      //   84: iconst_0
      //   85: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   88: aload_0
      //   89: monitorexit
      //   90: return
      //   91: astore 7
      //   93: aload_0
      //   94: getfield 55	rx/internal/operators/OperatorMerge$MergeSubscriber:delayErrors	Z
      //   97: istore 8
      //   99: iconst_0
      //   100: istore 4
      //   102: iload 8
      //   104: ifne +43 -> 147
      //   107: aload 7
      //   109: invokestatic 194	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
      //   112: iconst_1
      //   113: istore 4
      //   115: aload_0
      //   116: invokevirtual 147	rx/internal/operators/OperatorMerge$MergeSubscriber:unsubscribe	()V
      //   119: aload_0
      //   120: aload 7
      //   122: invokevirtual 228	rx/internal/operators/OperatorMerge$MergeSubscriber:onError	(Ljava/lang/Throwable;)V
      //   125: iload 4
      //   127: ifne -37 -> 90
      //   130: aload_0
      //   131: monitorenter
      //   132: aload_0
      //   133: iconst_0
      //   134: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   137: aload_0
      //   138: monitorexit
      //   139: return
      //   140: astore 9
      //   142: aload_0
      //   143: monitorexit
      //   144: aload 9
      //   146: athrow
      //   147: aload_0
      //   148: invokevirtual 198	rx/internal/operators/OperatorMerge$MergeSubscriber:getOrCreateErrorQueue	()Ljava/util/Queue;
      //   151: aload 7
      //   153: invokeinterface 202 2 0
      //   158: pop
      //   159: goto -148 -> 11
      //   162: astore 5
      //   164: iload 4
      //   166: ifne +12 -> 178
      //   169: aload_0
      //   170: monitorenter
      //   171: aload_0
      //   172: iconst_0
      //   173: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   176: aload_0
      //   177: monitorexit
      //   178: aload 5
      //   180: athrow
      //   181: aload_0
      //   182: iload 11
      //   184: putfield 158	rx/internal/operators/OperatorMerge$MergeSubscriber:scalarEmissionCount	I
      //   187: goto -130 -> 57
      //   190: astore 14
      //   192: aload_0
      //   193: monitorexit
      //   194: aload 14
      //   196: athrow
      //   197: aload_0
      //   198: iconst_0
      //   199: putfield 152	rx/internal/operators/OperatorMerge$MergeSubscriber:missed	Z
      //   202: aload_0
      //   203: monitorexit
      //   204: iload 4
      //   206: ifne +12 -> 218
      //   209: aload_0
      //   210: monitorenter
      //   211: aload_0
      //   212: iconst_0
      //   213: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   216: aload_0
      //   217: monitorexit
      //   218: aload_0
      //   219: invokevirtual 155	rx/internal/operators/OperatorMerge$MergeSubscriber:emitLoop	()V
      //   222: return
      //   223: astore 12
      //   225: aload_0
      //   226: monitorexit
      //   227: aload 12
      //   229: athrow
      //   230: astore 13
      //   232: aload_0
      //   233: monitorexit
      //   234: aload 13
      //   236: athrow
      //   237: astore 6
      //   239: aload_0
      //   240: monitorexit
      //   241: aload 6
      //   243: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   3	11	91	java/lang/Throwable
      //   132	139	140	finally
      //   142	144	140	finally
      //   3	11	162	finally
      //   19	28	162	finally
      //   28	57	162	finally
      //   57	59	162	finally
      //   93	99	162	finally
      //   107	112	162	finally
      //   115	125	162	finally
      //   147	159	162	finally
      //   181	187	162	finally
      //   227	230	162	finally
      //   83	90	190	finally
      //   192	194	190	finally
      //   62	76	223	finally
      //   197	204	223	finally
      //   225	227	223	finally
      //   211	218	230	finally
      //   232	234	230	finally
      //   171	178	237	finally
      //   239	241	237	finally
    }

    // ERROR //
    protected void emitScalar(OperatorMerge.InnerSubscriber<T> paramInnerSubscriber, T paramT, long paramLong)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore 5
      //   3: aload_0
      //   4: getfield 53	rx/internal/operators/OperatorMerge$MergeSubscriber:child	Lrx/Subscriber;
      //   7: aload_2
      //   8: invokevirtual 189	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   11: lload_3
      //   12: ldc2_w 76
      //   15: lcmp
      //   16: ifeq +12 -> 28
      //   19: aload_0
      //   20: getfield 167	rx/internal/operators/OperatorMerge$MergeSubscriber:producer	Lrx/internal/operators/OperatorMerge$MergeProducer;
      //   23: iconst_1
      //   24: invokevirtual 206	rx/internal/operators/OperatorMerge$MergeProducer:produced	(I)J
      //   27: pop2
      //   28: aload_1
      //   29: lconst_1
      //   30: invokevirtual 217	rx/internal/operators/OperatorMerge$InnerSubscriber:requestMore	(J)V
      //   33: aload_0
      //   34: monitorenter
      //   35: iconst_1
      //   36: istore 5
      //   38: aload_0
      //   39: getfield 152	rx/internal/operators/OperatorMerge$MergeSubscriber:missed	Z
      //   42: ifne +122 -> 164
      //   45: aload_0
      //   46: iconst_0
      //   47: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   50: aload_0
      //   51: monitorexit
      //   52: iload 5
      //   54: ifne +12 -> 66
      //   57: aload_0
      //   58: monitorenter
      //   59: aload_0
      //   60: iconst_0
      //   61: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   64: aload_0
      //   65: monitorexit
      //   66: return
      //   67: astore 8
      //   69: aload_0
      //   70: getfield 55	rx/internal/operators/OperatorMerge$MergeSubscriber:delayErrors	Z
      //   73: istore 9
      //   75: iconst_0
      //   76: istore 5
      //   78: iload 9
      //   80: ifne +43 -> 123
      //   83: aload 8
      //   85: invokestatic 194	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
      //   88: iconst_1
      //   89: istore 5
      //   91: aload_1
      //   92: invokevirtual 230	rx/internal/operators/OperatorMerge$InnerSubscriber:unsubscribe	()V
      //   95: aload_1
      //   96: aload 8
      //   98: invokevirtual 231	rx/internal/operators/OperatorMerge$InnerSubscriber:onError	(Ljava/lang/Throwable;)V
      //   101: iload 5
      //   103: ifne -37 -> 66
      //   106: aload_0
      //   107: monitorenter
      //   108: aload_0
      //   109: iconst_0
      //   110: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   113: aload_0
      //   114: monitorexit
      //   115: return
      //   116: astore 10
      //   118: aload_0
      //   119: monitorexit
      //   120: aload 10
      //   122: athrow
      //   123: aload_0
      //   124: invokevirtual 198	rx/internal/operators/OperatorMerge$MergeSubscriber:getOrCreateErrorQueue	()Ljava/util/Queue;
      //   127: aload 8
      //   129: invokeinterface 202 2 0
      //   134: pop
      //   135: goto -124 -> 11
      //   138: astore 6
      //   140: iload 5
      //   142: ifne +12 -> 154
      //   145: aload_0
      //   146: monitorenter
      //   147: aload_0
      //   148: iconst_0
      //   149: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   152: aload_0
      //   153: monitorexit
      //   154: aload 6
      //   156: athrow
      //   157: astore 14
      //   159: aload_0
      //   160: monitorexit
      //   161: aload 14
      //   163: athrow
      //   164: aload_0
      //   165: iconst_0
      //   166: putfield 152	rx/internal/operators/OperatorMerge$MergeSubscriber:missed	Z
      //   169: aload_0
      //   170: monitorexit
      //   171: iload 5
      //   173: ifne +12 -> 185
      //   176: aload_0
      //   177: monitorenter
      //   178: aload_0
      //   179: iconst_0
      //   180: putfield 150	rx/internal/operators/OperatorMerge$MergeSubscriber:emitting	Z
      //   183: aload_0
      //   184: monitorexit
      //   185: aload_0
      //   186: invokevirtual 155	rx/internal/operators/OperatorMerge$MergeSubscriber:emitLoop	()V
      //   189: return
      //   190: astore 12
      //   192: aload_0
      //   193: monitorexit
      //   194: aload 12
      //   196: athrow
      //   197: astore 13
      //   199: aload_0
      //   200: monitorexit
      //   201: aload 13
      //   203: athrow
      //   204: astore 7
      //   206: aload_0
      //   207: monitorexit
      //   208: aload 7
      //   210: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   3	11	67	java/lang/Throwable
      //   108	115	116	finally
      //   118	120	116	finally
      //   3	11	138	finally
      //   19	28	138	finally
      //   28	35	138	finally
      //   69	75	138	finally
      //   83	88	138	finally
      //   91	101	138	finally
      //   123	135	138	finally
      //   194	197	138	finally
      //   59	66	157	finally
      //   159	161	157	finally
      //   38	52	190	finally
      //   164	171	190	finally
      //   192	194	190	finally
      //   178	185	197	finally
      //   199	201	197	finally
      //   147	154	204	finally
      //   206	208	204	finally
    }

    // ERROR //
    CompositeSubscription getOrCreateComposite()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 233	rx/internal/operators/OperatorMerge$MergeSubscriber:subscriptions	Lrx/subscriptions/CompositeSubscription;
      //   4: astore_1
      //   5: aload_1
      //   6: ifnonnull +47 -> 53
      //   9: aload_0
      //   10: monitorenter
      //   11: aload_0
      //   12: getfield 233	rx/internal/operators/OperatorMerge$MergeSubscriber:subscriptions	Lrx/subscriptions/CompositeSubscription;
      //   15: astore_1
      //   16: iconst_0
      //   17: istore_3
      //   18: aload_1
      //   19: ifnonnull +23 -> 42
      //   22: new 122	rx/subscriptions/CompositeSubscription
      //   25: dup
      //   26: invokespecial 234	rx/subscriptions/CompositeSubscription:<init>	()V
      //   29: astore 4
      //   31: aload_0
      //   32: aload 4
      //   34: putfield 233	rx/internal/operators/OperatorMerge$MergeSubscriber:subscriptions	Lrx/subscriptions/CompositeSubscription;
      //   37: iconst_1
      //   38: istore_3
      //   39: aload 4
      //   41: astore_1
      //   42: aload_0
      //   43: monitorexit
      //   44: iload_3
      //   45: ifeq +8 -> 53
      //   48: aload_0
      //   49: aload_1
      //   50: invokevirtual 235	rx/internal/operators/OperatorMerge$MergeSubscriber:add	(Lrx/Subscription;)V
      //   53: aload_1
      //   54: areturn
      //   55: astore_2
      //   56: aload_0
      //   57: monitorexit
      //   58: aload_2
      //   59: athrow
      //   60: astore_2
      //   61: goto -5 -> 56
      //
      // Exception table:
      //   from	to	target	type
      //   11	16	55	finally
      //   22	31	55	finally
      //   42	44	55	finally
      //   56	58	55	finally
      //   31	37	60	finally
    }

    Queue<Throwable> getOrCreateErrorQueue()
    {
      ConcurrentLinkedQueue localConcurrentLinkedQueue1 = this.errors;
      if (localConcurrentLinkedQueue1 == null)
        monitorenter;
      try
      {
        Object localObject4 = this.errors;
        ConcurrentLinkedQueue localConcurrentLinkedQueue2;
        if (localObject4 == null)
          localConcurrentLinkedQueue2 = new ConcurrentLinkedQueue();
        try
        {
          this.errors = localConcurrentLinkedQueue2;
          localObject4 = localConcurrentLinkedQueue2;
          monitorexit;
          return localObject4;
          label42: monitorexit;
          Object localObject1;
          throw localObject1;
        }
        finally
        {
        }
        return localConcurrentLinkedQueue1;
      }
      finally
      {
        break label42;
      }
    }

    public void onCompleted()
    {
      this.done = true;
      emit();
    }

    public void onError(Throwable paramThrowable)
    {
      getOrCreateErrorQueue().offer(paramThrowable);
      this.done = true;
      emit();
    }

    public void onNext(Observable<? extends T> paramObservable)
    {
      if (paramObservable == null)
        return;
      if (paramObservable == Observable.empty())
      {
        emitEmpty();
        return;
      }
      if ((paramObservable instanceof ScalarSynchronousObservable))
      {
        tryEmit(((ScalarSynchronousObservable)paramObservable).get());
        return;
      }
      long l = this.uniqueId;
      this.uniqueId = (1L + l);
      OperatorMerge.InnerSubscriber localInnerSubscriber = new OperatorMerge.InnerSubscriber(this, l);
      addInner(localInnerSubscriber);
      paramObservable.unsafeSubscribe(localInnerSubscriber);
      emit();
    }

    protected void queueScalar(T paramT)
    {
      Object localObject = this.queue;
      int i;
      if (localObject == null)
      {
        i = this.maxConcurrent;
        if (i != 2147483647)
          break label73;
        localObject = new SpscUnboundedAtomicArrayQueue(RxRingBuffer.SIZE);
      }
      while (true)
      {
        this.queue = ((Queue)localObject);
        if (!((Queue)localObject).offer(this.nl.next(paramT)))
        {
          unsubscribe();
          onError(OnErrorThrowable.addValueAsLastCause(new MissingBackpressureException(), paramT));
        }
        return;
        label73: if (Pow2.isPowerOfTwo(i))
        {
          if (UnsafeAccess.isUnsafeAvailable())
          {
            localObject = new SpscArrayQueue(i);
            continue;
          }
          localObject = new SpscAtomicArrayQueue(i);
          continue;
        }
        localObject = new SpscExactAtomicArrayQueue(i);
      }
    }

    protected void queueScalar(OperatorMerge.InnerSubscriber<T> paramInnerSubscriber, T paramT)
    {
      RxRingBuffer localRxRingBuffer = paramInnerSubscriber.queue;
      if (localRxRingBuffer == null)
      {
        localRxRingBuffer = RxRingBuffer.getSpscInstance();
        paramInnerSubscriber.add(localRxRingBuffer);
        paramInnerSubscriber.queue = localRxRingBuffer;
      }
      try
      {
        localRxRingBuffer.onNext(this.nl.next(paramT));
        return;
      }
      catch (MissingBackpressureException localMissingBackpressureException)
      {
        paramInnerSubscriber.unsubscribe();
        paramInnerSubscriber.onError(localMissingBackpressureException);
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        while (paramInnerSubscriber.isUnsubscribed());
        paramInnerSubscriber.unsubscribe();
        paramInnerSubscriber.onError(localIllegalStateException);
      }
    }

    void removeInner(OperatorMerge.InnerSubscriber<T> paramInnerSubscriber)
    {
      RxRingBuffer localRxRingBuffer = paramInnerSubscriber.queue;
      if (localRxRingBuffer != null)
        localRxRingBuffer.release();
      this.subscriptions.remove(paramInnerSubscriber);
      while (true)
      {
        OperatorMerge.InnerSubscriber[] arrayOfInnerSubscriber1;
        int i;
        int j;
        int k;
        synchronized (this.innerGuard)
        {
          arrayOfInnerSubscriber1 = this.innerSubscribers;
          i = arrayOfInnerSubscriber1.length;
          j = -1;
          k = 0;
          if (k >= i)
            continue;
          if (!paramInnerSubscriber.equals(arrayOfInnerSubscriber1[k]))
            break label152;
          j = k;
          if (j < 0)
            return;
          if (i == 1)
          {
            this.innerSubscribers = EMPTY;
            return;
          }
        }
        int m = i - 1;
        OperatorMerge.InnerSubscriber[] arrayOfInnerSubscriber2 = new OperatorMerge.InnerSubscriber[m];
        System.arraycopy(arrayOfInnerSubscriber1, 0, arrayOfInnerSubscriber2, 0, j);
        System.arraycopy(arrayOfInnerSubscriber1, j + 1, arrayOfInnerSubscriber2, j, -1 + (i - j));
        this.innerSubscribers = arrayOfInnerSubscriber2;
        monitorexit;
        return;
        label152: k++;
      }
    }

    public void requestMore(long paramLong)
    {
      request(paramLong);
    }

    void tryEmit(T paramT)
    {
      long l = this.producer.get();
      boolean bool1 = l < 0L;
      int i = 0;
      if (bool1)
        monitorenter;
      try
      {
        l = this.producer.get();
        boolean bool2 = this.emitting;
        i = 0;
        if (!bool2)
        {
          boolean bool3 = l < 0L;
          i = 0;
          if (bool3)
          {
            this.emitting = true;
            i = 1;
          }
        }
        monitorexit;
        if (i == 0)
          break label118;
        Queue localQueue = this.queue;
        if ((localQueue == null) || (localQueue.isEmpty()))
        {
          emitScalar(paramT, l);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      queueScalar(paramT);
      emitLoop();
      return;
      label118: queueScalar(paramT);
      emit();
    }

    void tryEmit(OperatorMerge.InnerSubscriber<T> paramInnerSubscriber, T paramT)
    {
      long l = this.producer.get();
      boolean bool1 = l < 0L;
      int i = 0;
      if (bool1)
        monitorenter;
      try
      {
        l = this.producer.get();
        boolean bool2 = this.emitting;
        i = 0;
        if (!bool2)
        {
          boolean bool3 = l < 0L;
          i = 0;
          if (bool3)
          {
            this.emitting = true;
            i = 1;
          }
        }
        monitorexit;
        if (i == 0)
          break label118;
        RxRingBuffer localRxRingBuffer = paramInnerSubscriber.queue;
        if ((localRxRingBuffer == null) || (localRxRingBuffer.isEmpty()))
        {
          emitScalar(paramInnerSubscriber, paramT, l);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      queueScalar(paramInnerSubscriber, paramT);
      emitLoop();
      return;
      label118: queueScalar(paramInnerSubscriber, paramT);
      emit();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorMerge
 * JD-Core Version:    0.6.0
 */