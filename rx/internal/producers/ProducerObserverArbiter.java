package rx.internal.producers;

import java.util.Iterator;
import java.util.List;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.internal.operators.BackpressureUtils;

public final class ProducerObserverArbiter<T>
  implements Producer, Observer<T>
{
  static final Producer NULL_PRODUCER = new Producer()
  {
    public void request(long paramLong)
    {
    }
  };
  final Subscriber<? super T> child;
  Producer currentProducer;
  boolean emitting;
  volatile boolean hasError;
  Producer missedProducer;
  long missedRequested;
  Object missedTerminal;
  List<T> queue;
  long requested;

  public ProducerObserverArbiter(Subscriber<? super T> paramSubscriber)
  {
    this.child = paramSubscriber;
  }

  void emitLoop()
  {
    Subscriber localSubscriber = this.child;
    long l1 = 0L;
    Object localObject1 = null;
    while (true)
    {
      monitorenter;
      long l2;
      Producer localProducer1;
      Object localObject3;
      List localList;
      try
      {
        l2 = this.missedRequested;
        localProducer1 = this.missedProducer;
        localObject3 = this.missedTerminal;
        localList = this.queue;
        if ((l2 == 0L) && (localProducer1 == null) && (localList == null) && (localObject3 == null))
          this.emitting = false;
        for (int i = 1; ; i = 0)
        {
          monitorexit;
          if (i == 0)
            break;
          if ((l1 != 0L) && (localObject1 != null))
            localObject1.request(l1);
          label92: return;
          this.missedRequested = 0L;
          this.missedProducer = null;
          this.queue = null;
          this.missedTerminal = null;
        }
      }
      finally
      {
        monitorexit;
      }
      int j;
      if ((localList == null) || (localList.isEmpty()))
        j = 1;
      while (localObject3 != null)
      {
        if (localObject3 != Boolean.TRUE)
        {
          localSubscriber.onError((Throwable)localObject3);
          return;
          j = 0;
          continue;
        }
        if (j == 0)
          break;
        localSubscriber.onCompleted();
        return;
      }
      long l3 = 0L;
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (true)
          if (localIterator.hasNext())
          {
            Object localObject4 = localIterator.next();
            if (localSubscriber.isUnsubscribed())
              break label92;
            if (this.hasError)
              break;
            try
            {
              localSubscriber.onNext(localObject4);
            }
            catch (Throwable localThrowable)
            {
              Exceptions.throwOrReport(localThrowable, localSubscriber, localObject4);
              return;
            }
          }
        l3 += localList.size();
      }
      long l4 = this.requested;
      if (l4 != 9223372036854775807L)
      {
        if (l2 != 0L)
        {
          long l6 = l4 + l2;
          if (l6 < 0L)
            l6 = 9223372036854775807L;
          l4 = l6;
        }
        if ((l3 != 0L) && (l4 != 9223372036854775807L))
        {
          long l5 = l4 - l3;
          if (l5 < 0L)
            throw new IllegalStateException("More produced than requested");
          l4 = l5;
        }
        this.requested = l4;
      }
      if (localProducer1 != null)
      {
        if (localProducer1 == NULL_PRODUCER)
        {
          this.currentProducer = null;
          continue;
        }
        this.currentProducer = localProducer1;
        if (l4 == 0L)
          continue;
        l1 = BackpressureUtils.addCap(l1, l4);
        localObject1 = localProducer1;
        continue;
      }
      Producer localProducer2 = this.currentProducer;
      if ((localProducer2 == null) || (l2 == 0L))
        continue;
      l1 = BackpressureUtils.addCap(l1, l2);
      localObject1 = localProducer2;
    }
  }

  public void onCompleted()
  {
    monitorenter;
    try
    {
      if (this.emitting)
      {
        this.missedTerminal = Boolean.valueOf(true);
        return;
      }
      this.emitting = true;
      monitorexit;
      this.child.onCompleted();
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public void onError(Throwable paramThrowable)
  {
    monitorenter;
    try
    {
      if (this.emitting)
        this.missedTerminal = paramThrowable;
      for (int i = 0; ; i = 1)
      {
        monitorexit;
        if (i == 0)
          break;
        this.child.onError(paramThrowable);
        return;
        this.emitting = true;
      }
    }
    finally
    {
      monitorexit;
    }
    this.hasError = true;
  }

  // ERROR //
  public void onNext(T paramT)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   6: ifeq +42 -> 48
    //   9: aload_0
    //   10: getfield 51	rx/internal/producers/ProducerObserverArbiter:queue	Ljava/util/List;
    //   13: astore 8
    //   15: aload 8
    //   17: ifnonnull +19 -> 36
    //   20: new 135	java/util/ArrayList
    //   23: dup
    //   24: iconst_4
    //   25: invokespecial 138	java/util/ArrayList:<init>	(I)V
    //   28: astore 8
    //   30: aload_0
    //   31: aload 8
    //   33: putfield 51	rx/internal/producers/ProducerObserverArbiter:queue	Ljava/util/List;
    //   36: aload 8
    //   38: aload_1
    //   39: invokeinterface 142 2 0
    //   44: pop
    //   45: aload_0
    //   46: monitorexit
    //   47: return
    //   48: aload_0
    //   49: iconst_1
    //   50: putfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   53: aload_0
    //   54: monitorexit
    //   55: aload_0
    //   56: getfield 40	rx/internal/producers/ProducerObserverArbiter:child	Lrx/Subscriber;
    //   59: aload_1
    //   60: invokevirtual 100	rx/Subscriber:onNext	(Ljava/lang/Object;)V
    //   63: aload_0
    //   64: getfield 112	rx/internal/producers/ProducerObserverArbiter:requested	J
    //   67: lstore 5
    //   69: lload 5
    //   71: ldc2_w 113
    //   74: lcmp
    //   75: ifeq +11 -> 86
    //   78: aload_0
    //   79: lload 5
    //   81: lconst_1
    //   82: lsub
    //   83: putfield 112	rx/internal/producers/ProducerObserverArbiter:requested	J
    //   86: aload_0
    //   87: invokevirtual 144	rx/internal/producers/ProducerObserverArbiter:emitLoop	()V
    //   90: iconst_1
    //   91: ifne +48 -> 139
    //   94: aload_0
    //   95: monitorenter
    //   96: aload_0
    //   97: iconst_0
    //   98: putfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   101: aload_0
    //   102: monitorexit
    //   103: return
    //   104: astore 7
    //   106: aload_0
    //   107: monitorexit
    //   108: aload 7
    //   110: athrow
    //   111: astore_2
    //   112: aload_0
    //   113: monitorexit
    //   114: aload_2
    //   115: athrow
    //   116: astore_3
    //   117: iconst_0
    //   118: ifne +12 -> 130
    //   121: aload_0
    //   122: monitorenter
    //   123: aload_0
    //   124: iconst_0
    //   125: putfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   128: aload_0
    //   129: monitorexit
    //   130: aload_3
    //   131: athrow
    //   132: astore 4
    //   134: aload_0
    //   135: monitorexit
    //   136: aload 4
    //   138: athrow
    //   139: return
    //
    // Exception table:
    //   from	to	target	type
    //   96	103	104	finally
    //   106	108	104	finally
    //   2	15	111	finally
    //   20	36	111	finally
    //   36	47	111	finally
    //   48	55	111	finally
    //   112	114	111	finally
    //   55	69	116	finally
    //   78	86	116	finally
    //   86	90	116	finally
    //   123	130	132	finally
    //   134	136	132	finally
  }

  // ERROR //
  public void request(long paramLong)
  {
    // Byte code:
    //   0: lload_1
    //   1: lconst_0
    //   2: lcmp
    //   3: ifge +13 -> 16
    //   6: new 146	java/lang/IllegalArgumentException
    //   9: dup
    //   10: ldc 148
    //   12: invokespecial 149	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   15: athrow
    //   16: lload_1
    //   17: lconst_0
    //   18: lcmp
    //   19: ifne +4 -> 23
    //   22: return
    //   23: aload_0
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   29: ifeq +21 -> 50
    //   32: aload_0
    //   33: lload_1
    //   34: aload_0
    //   35: getfield 45	rx/internal/producers/ProducerObserverArbiter:missedRequested	J
    //   38: ladd
    //   39: putfield 45	rx/internal/producers/ProducerObserverArbiter:missedRequested	J
    //   42: aload_0
    //   43: monitorexit
    //   44: return
    //   45: astore_3
    //   46: aload_0
    //   47: monitorexit
    //   48: aload_3
    //   49: athrow
    //   50: aload_0
    //   51: iconst_1
    //   52: putfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   55: aload_0
    //   56: monitorexit
    //   57: aload_0
    //   58: getfield 123	rx/internal/producers/ProducerObserverArbiter:currentProducer	Lrx/Producer;
    //   61: astore 4
    //   63: lload_1
    //   64: aload_0
    //   65: getfield 112	rx/internal/producers/ProducerObserverArbiter:requested	J
    //   68: ladd
    //   69: lstore 7
    //   71: lload 7
    //   73: lconst_0
    //   74: lcmp
    //   75: ifge +8 -> 83
    //   78: ldc2_w 113
    //   81: lstore 7
    //   83: aload_0
    //   84: lload 7
    //   86: putfield 112	rx/internal/producers/ProducerObserverArbiter:requested	J
    //   89: aload_0
    //   90: invokevirtual 144	rx/internal/producers/ProducerObserverArbiter:emitLoop	()V
    //   93: iconst_1
    //   94: ifne +12 -> 106
    //   97: aload_0
    //   98: monitorenter
    //   99: aload_0
    //   100: iconst_0
    //   101: putfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   104: aload_0
    //   105: monitorexit
    //   106: aload 4
    //   108: ifnull -86 -> 22
    //   111: aload 4
    //   113: lload_1
    //   114: invokeinterface 57 3 0
    //   119: return
    //   120: astore 9
    //   122: aload_0
    //   123: monitorexit
    //   124: aload 9
    //   126: athrow
    //   127: astore 5
    //   129: iconst_0
    //   130: ifne +12 -> 142
    //   133: aload_0
    //   134: monitorenter
    //   135: aload_0
    //   136: iconst_0
    //   137: putfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   140: aload_0
    //   141: monitorexit
    //   142: aload 5
    //   144: athrow
    //   145: astore 6
    //   147: aload_0
    //   148: monitorexit
    //   149: aload 6
    //   151: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   25	44	45	finally
    //   46	48	45	finally
    //   50	57	45	finally
    //   99	106	120	finally
    //   122	124	120	finally
    //   63	71	127	finally
    //   83	93	127	finally
    //   135	142	145	finally
    //   147	149	145	finally
  }

  // ERROR //
  public void setProducer(Producer paramProducer)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   6: ifeq +22 -> 28
    //   9: aload_1
    //   10: ifnull +11 -> 21
    //   13: aload_0
    //   14: aload_1
    //   15: putfield 47	rx/internal/producers/ProducerObserverArbiter:missedProducer	Lrx/Producer;
    //   18: aload_0
    //   19: monitorexit
    //   20: return
    //   21: getstatic 36	rx/internal/producers/ProducerObserverArbiter:NULL_PRODUCER	Lrx/Producer;
    //   24: astore_1
    //   25: goto -12 -> 13
    //   28: aload_0
    //   29: iconst_1
    //   30: putfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   33: aload_0
    //   34: monitorexit
    //   35: aload_0
    //   36: aload_1
    //   37: putfield 123	rx/internal/producers/ProducerObserverArbiter:currentProducer	Lrx/Producer;
    //   40: aload_0
    //   41: getfield 112	rx/internal/producers/ProducerObserverArbiter:requested	J
    //   44: lstore_3
    //   45: aload_0
    //   46: invokevirtual 144	rx/internal/producers/ProducerObserverArbiter:emitLoop	()V
    //   49: iconst_1
    //   50: ifne +12 -> 62
    //   53: aload_0
    //   54: monitorenter
    //   55: aload_0
    //   56: iconst_0
    //   57: putfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   60: aload_0
    //   61: monitorexit
    //   62: aload_1
    //   63: ifnull +54 -> 117
    //   66: lload_3
    //   67: lconst_0
    //   68: lcmp
    //   69: ifeq +48 -> 117
    //   72: aload_1
    //   73: lload_3
    //   74: invokeinterface 57 3 0
    //   79: return
    //   80: astore_2
    //   81: aload_0
    //   82: monitorexit
    //   83: aload_2
    //   84: athrow
    //   85: astore 7
    //   87: aload_0
    //   88: monitorexit
    //   89: aload 7
    //   91: athrow
    //   92: astore 5
    //   94: iconst_0
    //   95: ifne +12 -> 107
    //   98: aload_0
    //   99: monitorenter
    //   100: aload_0
    //   101: iconst_0
    //   102: putfield 53	rx/internal/producers/ProducerObserverArbiter:emitting	Z
    //   105: aload_0
    //   106: monitorexit
    //   107: aload 5
    //   109: athrow
    //   110: astore 6
    //   112: aload_0
    //   113: monitorexit
    //   114: aload 6
    //   116: athrow
    //   117: return
    //
    // Exception table:
    //   from	to	target	type
    //   2	9	80	finally
    //   13	20	80	finally
    //   21	25	80	finally
    //   28	35	80	finally
    //   81	83	80	finally
    //   55	62	85	finally
    //   87	89	85	finally
    //   45	49	92	finally
    //   100	107	110	finally
    //   112	114	110	finally
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.producers.ProducerObserverArbiter
 * JD-Core Version:    0.6.0
 */