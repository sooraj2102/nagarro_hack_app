package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import rx.Observable;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func0;
import rx.observers.SerializedSubscriber;
import rx.subjects.UnicastSubject;
import rx.subscriptions.SerialSubscription;

public final class OperatorWindowWithObservableFactory<T, U>
  implements Observable.Operator<Observable<T>, T>
{
  static final Object NEXT_SUBJECT = new Object();
  static final NotificationLite<Object> NL = NotificationLite.instance();
  final Func0<? extends Observable<? extends U>> otherFactory;

  public OperatorWindowWithObservableFactory(Func0<? extends Observable<? extends U>> paramFunc0)
  {
    this.otherFactory = paramFunc0;
  }

  public Subscriber<? super T> call(Subscriber<? super Observable<T>> paramSubscriber)
  {
    SourceSubscriber localSourceSubscriber = new SourceSubscriber(paramSubscriber, this.otherFactory);
    paramSubscriber.add(localSourceSubscriber);
    localSourceSubscriber.replaceWindow();
    return localSourceSubscriber;
  }

  static final class BoundarySubscriber<T, U> extends Subscriber<U>
  {
    boolean done;
    final OperatorWindowWithObservableFactory.SourceSubscriber<T, U> sub;

    public BoundarySubscriber(OperatorWindowWithObservableFactory.SourceSubscriber<T, U> paramSourceSubscriber)
    {
      this.sub = paramSourceSubscriber;
    }

    public void onCompleted()
    {
      if (!this.done)
      {
        this.done = true;
        this.sub.onCompleted();
      }
    }

    public void onError(Throwable paramThrowable)
    {
      this.sub.onError(paramThrowable);
    }

    public void onNext(U paramU)
    {
      if (!this.done)
      {
        this.done = true;
        this.sub.replaceWindow();
      }
    }

    public void onStart()
    {
      request(9223372036854775807L);
    }
  }

  static final class SourceSubscriber<T, U> extends Subscriber<T>
  {
    final Subscriber<? super Observable<T>> child;
    Observer<T> consumer;
    boolean emitting;
    final Object guard;
    final Func0<? extends Observable<? extends U>> otherFactory;
    Observable<T> producer;
    List<Object> queue;
    final SerialSubscription serial;

    public SourceSubscriber(Subscriber<? super Observable<T>> paramSubscriber, Func0<? extends Observable<? extends U>> paramFunc0)
    {
      this.child = new SerializedSubscriber(paramSubscriber);
      this.guard = new Object();
      this.serial = new SerialSubscription();
      this.otherFactory = paramFunc0;
      add(this.serial);
    }

    void complete()
    {
      Observer localObserver = this.consumer;
      this.consumer = null;
      this.producer = null;
      if (localObserver != null)
        localObserver.onCompleted();
      this.child.onCompleted();
      unsubscribe();
    }

    void createNewWindow()
    {
      UnicastSubject localUnicastSubject = UnicastSubject.create();
      this.consumer = localUnicastSubject;
      this.producer = localUnicastSubject;
      try
      {
        Observable localObservable = (Observable)this.otherFactory.call();
        OperatorWindowWithObservableFactory.BoundarySubscriber localBoundarySubscriber = new OperatorWindowWithObservableFactory.BoundarySubscriber(this);
        this.serial.set(localBoundarySubscriber);
        localObservable.unsafeSubscribe(localBoundarySubscriber);
        return;
      }
      catch (Throwable localThrowable)
      {
        this.child.onError(localThrowable);
        unsubscribe();
      }
    }

    void drain(List<Object> paramList)
    {
      if (paramList == null);
      while (true)
      {
        return;
        Iterator localIterator = paramList.iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          if (localObject == OperatorWindowWithObservableFactory.NEXT_SUBJECT)
          {
            replaceSubject();
            continue;
          }
          if (OperatorWindowWithObservableFactory.NL.isError(localObject))
          {
            error(OperatorWindowWithObservableFactory.NL.getError(localObject));
            return;
          }
          if (OperatorWindowWithObservableFactory.NL.isCompleted(localObject))
          {
            complete();
            return;
          }
          emitValue(localObject);
        }
      }
    }

    void emitValue(T paramT)
    {
      Observer localObserver = this.consumer;
      if (localObserver != null)
        localObserver.onNext(paramT);
    }

    void error(Throwable paramThrowable)
    {
      Observer localObserver = this.consumer;
      this.consumer = null;
      this.producer = null;
      if (localObserver != null)
        localObserver.onError(paramThrowable);
      this.child.onError(paramThrowable);
      unsubscribe();
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
          this.queue.add(OperatorWindowWithObservableFactory.NL.completed());
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
          this.queue = Collections.singletonList(OperatorWindowWithObservableFactory.NL.error(paramThrowable));
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
      //   1: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   4: astore_2
      //   5: aload_2
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   11: ifeq +35 -> 46
      //   14: aload_0
      //   15: getfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   18: ifnonnull +14 -> 32
      //   21: aload_0
      //   22: new 162	java/util/ArrayList
      //   25: dup
      //   26: invokespecial 163	java/util/ArrayList:<init>	()V
      //   29: putfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   32: aload_0
      //   33: getfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   36: aload_1
      //   37: invokeinterface 168 2 0
      //   42: pop
      //   43: aload_2
      //   44: monitorexit
      //   45: return
      //   46: aload_0
      //   47: getfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   50: astore 4
      //   52: aload_0
      //   53: aconst_null
      //   54: putfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   57: aload_0
      //   58: iconst_1
      //   59: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   62: aload_2
      //   63: monitorexit
      //   64: iconst_1
      //   65: istore 5
      //   67: iconst_0
      //   68: istore 6
      //   70: aload_0
      //   71: aload 4
      //   73: invokevirtual 170	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:drain	(Ljava/util/List;)V
      //   76: iload 5
      //   78: ifeq +11 -> 89
      //   81: iconst_0
      //   82: istore 5
      //   84: aload_0
      //   85: aload_1
      //   86: invokevirtual 152	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitValue	(Ljava/lang/Object;)V
      //   89: aload_0
      //   90: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   93: astore 10
      //   95: aload 10
      //   97: monitorenter
      //   98: aload_0
      //   99: getfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   102: astore 4
      //   104: aload_0
      //   105: aconst_null
      //   106: putfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   109: aload 4
      //   111: ifnonnull +50 -> 161
      //   114: aload_0
      //   115: iconst_0
      //   116: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   119: iconst_1
      //   120: istore 6
      //   122: aload 10
      //   124: monitorexit
      //   125: iload 6
      //   127: ifne +124 -> 251
      //   130: aload_0
      //   131: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   134: astore 12
      //   136: aload 12
      //   138: monitorenter
      //   139: aload_0
      //   140: iconst_0
      //   141: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   144: aload 12
      //   146: monitorexit
      //   147: return
      //   148: astore 13
      //   150: aload 12
      //   152: monitorexit
      //   153: aload 13
      //   155: athrow
      //   156: astore_3
      //   157: aload_2
      //   158: monitorexit
      //   159: aload_3
      //   160: athrow
      //   161: aload 10
      //   163: monitorexit
      //   164: aload_0
      //   165: getfield 38	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:child	Lrx/Subscriber;
      //   168: invokevirtual 182	rx/Subscriber:isUnsubscribed	()Z
      //   171: istore 14
      //   173: iload 14
      //   175: ifeq -105 -> 70
      //   178: iconst_0
      //   179: ifne +72 -> 251
      //   182: aload_0
      //   183: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   186: astore 15
      //   188: aload 15
      //   190: monitorenter
      //   191: aload_0
      //   192: iconst_0
      //   193: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   196: aload 15
      //   198: monitorexit
      //   199: return
      //   200: astore 16
      //   202: aload 15
      //   204: monitorexit
      //   205: aload 16
      //   207: athrow
      //   208: astore 11
      //   210: aload 10
      //   212: monitorexit
      //   213: aload 11
      //   215: athrow
      //   216: astore 7
      //   218: iload 6
      //   220: ifne +20 -> 240
      //   223: aload_0
      //   224: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   227: astore 8
      //   229: aload 8
      //   231: monitorenter
      //   232: aload_0
      //   233: iconst_0
      //   234: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   237: aload 8
      //   239: monitorexit
      //   240: aload 7
      //   242: athrow
      //   243: astore 9
      //   245: aload 8
      //   247: monitorexit
      //   248: aload 9
      //   250: athrow
      //   251: return
      //
      // Exception table:
      //   from	to	target	type
      //   139	147	148	finally
      //   150	153	148	finally
      //   7	32	156	finally
      //   32	45	156	finally
      //   46	64	156	finally
      //   157	159	156	finally
      //   191	199	200	finally
      //   202	205	200	finally
      //   98	109	208	finally
      //   114	119	208	finally
      //   122	125	208	finally
      //   161	164	208	finally
      //   210	213	208	finally
      //   70	76	216	finally
      //   84	89	216	finally
      //   89	98	216	finally
      //   164	173	216	finally
      //   213	216	216	finally
      //   232	240	243	finally
      //   245	248	243	finally
    }

    public void onStart()
    {
      request(9223372036854775807L);
    }

    void replaceSubject()
    {
      Observer localObserver = this.consumer;
      if (localObserver != null)
        localObserver.onCompleted();
      createNewWindow();
      this.child.onNext(this.producer);
    }

    // ERROR //
    void replaceWindow()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   4: astore_1
      //   5: aload_1
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   11: ifeq +37 -> 48
      //   14: aload_0
      //   15: getfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   18: ifnonnull +14 -> 32
      //   21: aload_0
      //   22: new 162	java/util/ArrayList
      //   25: dup
      //   26: invokespecial 163	java/util/ArrayList:<init>	()V
      //   29: putfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   32: aload_0
      //   33: getfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   36: getstatic 123	rx/internal/operators/OperatorWindowWithObservableFactory:NEXT_SUBJECT	Ljava/lang/Object;
      //   39: invokeinterface 168 2 0
      //   44: pop
      //   45: aload_1
      //   46: monitorexit
      //   47: return
      //   48: aload_0
      //   49: getfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   52: astore_3
      //   53: aload_0
      //   54: aconst_null
      //   55: putfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   58: aload_0
      //   59: iconst_1
      //   60: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   63: aload_1
      //   64: monitorexit
      //   65: iconst_1
      //   66: istore 4
      //   68: iconst_0
      //   69: istore 5
      //   71: aload_0
      //   72: aload_3
      //   73: invokevirtual 170	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:drain	(Ljava/util/List;)V
      //   76: iload 4
      //   78: ifeq +10 -> 88
      //   81: iconst_0
      //   82: istore 4
      //   84: aload_0
      //   85: invokevirtual 126	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:replaceSubject	()V
      //   88: aload_0
      //   89: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   92: astore 9
      //   94: aload 9
      //   96: monitorenter
      //   97: aload_0
      //   98: getfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   101: astore_3
      //   102: aload_0
      //   103: aconst_null
      //   104: putfield 160	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:queue	Ljava/util/List;
      //   107: aload_3
      //   108: ifnonnull +50 -> 158
      //   111: aload_0
      //   112: iconst_0
      //   113: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   116: iconst_1
      //   117: istore 5
      //   119: aload 9
      //   121: monitorexit
      //   122: iload 5
      //   124: ifne +124 -> 248
      //   127: aload_0
      //   128: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   131: astore 11
      //   133: aload 11
      //   135: monitorenter
      //   136: aload_0
      //   137: iconst_0
      //   138: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   141: aload 11
      //   143: monitorexit
      //   144: return
      //   145: astore 12
      //   147: aload 11
      //   149: monitorexit
      //   150: aload 12
      //   152: athrow
      //   153: astore_2
      //   154: aload_1
      //   155: monitorexit
      //   156: aload_2
      //   157: athrow
      //   158: aload 9
      //   160: monitorexit
      //   161: aload_0
      //   162: getfield 38	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:child	Lrx/Subscriber;
      //   165: invokevirtual 182	rx/Subscriber:isUnsubscribed	()Z
      //   168: istore 13
      //   170: iload 13
      //   172: ifeq -101 -> 71
      //   175: iconst_0
      //   176: ifne +72 -> 248
      //   179: aload_0
      //   180: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   183: astore 14
      //   185: aload 14
      //   187: monitorenter
      //   188: aload_0
      //   189: iconst_0
      //   190: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   193: aload 14
      //   195: monitorexit
      //   196: return
      //   197: astore 15
      //   199: aload 14
      //   201: monitorexit
      //   202: aload 15
      //   204: athrow
      //   205: astore 10
      //   207: aload 9
      //   209: monitorexit
      //   210: aload 10
      //   212: athrow
      //   213: astore 6
      //   215: iload 5
      //   217: ifne +20 -> 237
      //   220: aload_0
      //   221: getfield 43	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:guard	Ljava/lang/Object;
      //   224: astore 7
      //   226: aload 7
      //   228: monitorenter
      //   229: aload_0
      //   230: iconst_0
      //   231: putfield 158	rx/internal/operators/OperatorWindowWithObservableFactory$SourceSubscriber:emitting	Z
      //   234: aload 7
      //   236: monitorexit
      //   237: aload 6
      //   239: athrow
      //   240: astore 8
      //   242: aload 7
      //   244: monitorexit
      //   245: aload 8
      //   247: athrow
      //   248: return
      //
      // Exception table:
      //   from	to	target	type
      //   136	144	145	finally
      //   147	150	145	finally
      //   7	32	153	finally
      //   32	47	153	finally
      //   48	65	153	finally
      //   154	156	153	finally
      //   188	196	197	finally
      //   199	202	197	finally
      //   97	107	205	finally
      //   111	116	205	finally
      //   119	122	205	finally
      //   158	161	205	finally
      //   207	210	205	finally
      //   71	76	213	finally
      //   84	88	213	finally
      //   88	97	213	finally
      //   161	170	213	finally
      //   210	213	213	finally
      //   229	237	240	finally
      //   242	245	240	finally
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorWindowWithObservableFactory
 * JD-Core Version:    0.6.0
 */