package rx.internal.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable.Operator;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.observers.SerializedSubscriber;

public final class OperatorBufferWithTime<T>
  implements Observable.Operator<List<T>, T>
{
  final int count;
  final Scheduler scheduler;
  final long timeshift;
  final long timespan;
  final TimeUnit unit;

  public OperatorBufferWithTime(long paramLong1, long paramLong2, TimeUnit paramTimeUnit, int paramInt, Scheduler paramScheduler)
  {
    this.timespan = paramLong1;
    this.timeshift = paramLong2;
    this.unit = paramTimeUnit;
    this.count = paramInt;
    this.scheduler = paramScheduler;
  }

  public Subscriber<? super T> call(Subscriber<? super List<T>> paramSubscriber)
  {
    Scheduler.Worker localWorker = this.scheduler.createWorker();
    SerializedSubscriber localSerializedSubscriber = new SerializedSubscriber(paramSubscriber);
    if (this.timespan == this.timeshift)
    {
      ExactSubscriber localExactSubscriber = new ExactSubscriber(localSerializedSubscriber, localWorker);
      localExactSubscriber.add(localWorker);
      paramSubscriber.add(localExactSubscriber);
      localExactSubscriber.scheduleExact();
      return localExactSubscriber;
    }
    InexactSubscriber localInexactSubscriber = new InexactSubscriber(localSerializedSubscriber, localWorker);
    localInexactSubscriber.add(localWorker);
    paramSubscriber.add(localInexactSubscriber);
    localInexactSubscriber.startNewChunk();
    localInexactSubscriber.scheduleChunk();
    return localInexactSubscriber;
  }

  final class ExactSubscriber extends Subscriber<T>
  {
    final Subscriber<? super List<T>> child;
    List<T> chunk;
    boolean done;
    final Scheduler.Worker inner;

    public ExactSubscriber(Scheduler.Worker arg2)
    {
      Object localObject1;
      this.child = localObject1;
      Object localObject2;
      this.inner = localObject2;
      this.chunk = new ArrayList();
    }

    void emit()
    {
      monitorenter;
      try
      {
        if (this.done)
          return;
        List localList = this.chunk;
        this.chunk = new ArrayList();
        monitorexit;
        try
        {
          this.child.onNext(localList);
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwOrReport(localThrowable, this);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void onCompleted()
    {
      try
      {
        this.inner.unsubscribe();
        monitorenter;
        try
        {
          if (this.done)
            return;
          this.done = true;
          List localList = this.chunk;
          this.chunk = null;
          monitorexit;
          this.child.onNext(localList);
          this.child.onCompleted();
          unsubscribe();
          return;
        }
        finally
        {
          monitorexit;
        }
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwOrReport(localThrowable, this.child);
      }
    }

    public void onError(Throwable paramThrowable)
    {
      monitorenter;
      try
      {
        if (this.done)
          return;
        this.done = true;
        this.chunk = null;
        monitorexit;
        this.child.onError(paramThrowable);
        unsubscribe();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void onNext(T paramT)
    {
      monitorenter;
      try
      {
        if (this.done)
          return;
        this.chunk.add(paramT);
        int i = this.chunk.size();
        int j = OperatorBufferWithTime.this.count;
        List localList = null;
        if (i == j)
        {
          localList = this.chunk;
          this.chunk = new ArrayList();
        }
        monitorexit;
        if (localList != null)
        {
          this.child.onNext(localList);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
    }

    void scheduleExact()
    {
      this.inner.schedulePeriodically(new Action0()
      {
        public void call()
        {
          OperatorBufferWithTime.ExactSubscriber.this.emit();
        }
      }
      , OperatorBufferWithTime.this.timespan, OperatorBufferWithTime.this.timespan, OperatorBufferWithTime.this.unit);
    }
  }

  final class InexactSubscriber extends Subscriber<T>
  {
    final Subscriber<? super List<T>> child;
    final List<List<T>> chunks;
    boolean done;
    final Scheduler.Worker inner;

    public InexactSubscriber(Scheduler.Worker arg2)
    {
      Object localObject1;
      this.child = localObject1;
      Object localObject2;
      this.inner = localObject2;
      this.chunks = new LinkedList();
    }

    void emitChunk(List<T> paramList)
    {
      monitorenter;
      try
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
          if ((List)localIterator.next() != paramList)
            continue;
          localIterator.remove();
          i = 1;
        }
        monitorexit;
        if (i != 0)
          try
          {
            this.child.onNext(paramList);
            return;
          }
          catch (Throwable localThrowable)
          {
            Exceptions.throwOrReport(localThrowable, this);
            return;
          }
      }
      finally
      {
        monitorexit;
      }
    }

    // ERROR //
    public void onCompleted()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 39	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:done	Z
      //   6: ifeq +6 -> 12
      //   9: aload_0
      //   10: monitorexit
      //   11: return
      //   12: aload_0
      //   13: iconst_1
      //   14: putfield 39	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:done	Z
      //   17: new 30	java/util/LinkedList
      //   20: dup
      //   21: aload_0
      //   22: getfield 33	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:chunks	Ljava/util/List;
      //   25: invokespecial 72	java/util/LinkedList:<init>	(Ljava/util/Collection;)V
      //   28: astore_3
      //   29: aload_0
      //   30: getfield 33	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:chunks	Ljava/util/List;
      //   33: invokeinterface 75 1 0
      //   38: aload_0
      //   39: monitorexit
      //   40: aload_3
      //   41: invokeinterface 45 1 0
      //   46: astore 4
      //   48: aload 4
      //   50: invokeinterface 51 1 0
      //   55: ifeq +42 -> 97
      //   58: aload 4
      //   60: invokeinterface 55 1 0
      //   65: checkcast 41	java/util/List
      //   68: astore 5
      //   70: aload_0
      //   71: getfield 26	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:child	Lrx/Subscriber;
      //   74: aload 5
      //   76: invokevirtual 62	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   79: goto -31 -> 48
      //   82: astore_1
      //   83: aload_1
      //   84: aload_0
      //   85: getfield 26	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:child	Lrx/Subscriber;
      //   88: invokestatic 68	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
      //   91: return
      //   92: astore_2
      //   93: aload_0
      //   94: monitorexit
      //   95: aload_2
      //   96: athrow
      //   97: aload_0
      //   98: getfield 26	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:child	Lrx/Subscriber;
      //   101: invokevirtual 77	rx/Subscriber:onCompleted	()V
      //   104: aload_0
      //   105: invokevirtual 80	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:unsubscribe	()V
      //   108: return
      //
      // Exception table:
      //   from	to	target	type
      //   0	2	82	java/lang/Throwable
      //   40	48	82	java/lang/Throwable
      //   48	79	82	java/lang/Throwable
      //   95	97	82	java/lang/Throwable
      //   2	11	92	finally
      //   12	40	92	finally
      //   93	95	92	finally
    }

    public void onError(Throwable paramThrowable)
    {
      monitorenter;
      try
      {
        if (this.done)
          return;
        this.done = true;
        this.chunks.clear();
        monitorexit;
        this.child.onError(paramThrowable);
        unsubscribe();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    // ERROR //
    public void onNext(T paramT)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 39	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:done	Z
      //   6: ifeq +6 -> 12
      //   9: aload_0
      //   10: monitorexit
      //   11: return
      //   12: aload_0
      //   13: getfield 33	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:chunks	Ljava/util/List;
      //   16: invokeinterface 45 1 0
      //   21: astore_3
      //   22: aconst_null
      //   23: astore 4
      //   25: aload_3
      //   26: invokeinterface 51 1 0
      //   31: ifeq +77 -> 108
      //   34: aload_3
      //   35: invokeinterface 55 1 0
      //   40: checkcast 41	java/util/List
      //   43: astore 9
      //   45: aload 9
      //   47: aload_1
      //   48: invokeinterface 88 2 0
      //   53: pop
      //   54: aload 9
      //   56: invokeinterface 92 1 0
      //   61: aload_0
      //   62: getfield 21	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:this$0	Lrx/internal/operators/OperatorBufferWithTime;
      //   65: getfield 98	rx/internal/operators/OperatorBufferWithTime:count	I
      //   68: if_icmpne +113 -> 181
      //   71: aload_3
      //   72: invokeinterface 58 1 0
      //   77: aload 4
      //   79: ifnonnull +95 -> 174
      //   82: new 30	java/util/LinkedList
      //   85: dup
      //   86: invokespecial 31	java/util/LinkedList:<init>	()V
      //   89: astore 11
      //   91: aload 11
      //   93: aload 9
      //   95: invokeinterface 88 2 0
      //   100: pop
      //   101: aload 11
      //   103: astore 4
      //   105: goto -80 -> 25
      //   108: aload_0
      //   109: monitorexit
      //   110: aload 4
      //   112: ifnull +51 -> 163
      //   115: aload 4
      //   117: invokeinterface 45 1 0
      //   122: astore 7
      //   124: aload 7
      //   126: invokeinterface 51 1 0
      //   131: ifeq +32 -> 163
      //   134: aload 7
      //   136: invokeinterface 55 1 0
      //   141: checkcast 41	java/util/List
      //   144: astore 8
      //   146: aload_0
      //   147: getfield 26	rx/internal/operators/OperatorBufferWithTime$InexactSubscriber:child	Lrx/Subscriber;
      //   150: aload 8
      //   152: invokevirtual 62	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   155: goto -31 -> 124
      //   158: astore_2
      //   159: aload_0
      //   160: monitorexit
      //   161: aload_2
      //   162: athrow
      //   163: aload 4
      //   165: pop
      //   166: return
      //   167: astore_2
      //   168: aload 4
      //   170: pop
      //   171: goto -12 -> 159
      //   174: aload 4
      //   176: astore 11
      //   178: goto -87 -> 91
      //   181: aload 4
      //   183: astore 11
      //   185: goto -84 -> 101
      //
      // Exception table:
      //   from	to	target	type
      //   2	11	158	finally
      //   12	22	158	finally
      //   91	101	158	finally
      //   159	161	158	finally
      //   25	77	167	finally
      //   82	91	167	finally
      //   108	110	167	finally
    }

    void scheduleChunk()
    {
      this.inner.schedulePeriodically(new Action0()
      {
        public void call()
        {
          OperatorBufferWithTime.InexactSubscriber.this.startNewChunk();
        }
      }
      , OperatorBufferWithTime.this.timeshift, OperatorBufferWithTime.this.timeshift, OperatorBufferWithTime.this.unit);
    }

    void startNewChunk()
    {
      ArrayList localArrayList = new ArrayList();
      monitorenter;
      try
      {
        if (this.done)
          return;
        this.chunks.add(localArrayList);
        monitorexit;
        this.inner.schedule(new Action0(localArrayList)
        {
          public void call()
          {
            OperatorBufferWithTime.InexactSubscriber.this.emitChunk(this.val$chunk);
          }
        }
        , OperatorBufferWithTime.this.timespan, OperatorBufferWithTime.this.unit);
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorBufferWithTime
 * JD-Core Version:    0.6.0
 */