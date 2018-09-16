package rx.internal.operators;

import java.util.concurrent.TimeUnit;
import rx.Observable.Operator;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.functions.Action0;
import rx.observers.SerializedSubscriber;
import rx.subscriptions.SerialSubscription;

public final class OperatorDebounceWithTime<T>
  implements Observable.Operator<T, T>
{
  final Scheduler scheduler;
  final long timeout;
  final TimeUnit unit;

  public OperatorDebounceWithTime(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    this.timeout = paramLong;
    this.unit = paramTimeUnit;
    this.scheduler = paramScheduler;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    Scheduler.Worker localWorker = this.scheduler.createWorker();
    SerializedSubscriber localSerializedSubscriber = new SerializedSubscriber(paramSubscriber);
    SerialSubscription localSerialSubscription = new SerialSubscription();
    localSerializedSubscriber.add(localWorker);
    localSerializedSubscriber.add(localSerialSubscription);
    return new Subscriber(paramSubscriber, localSerialSubscription, localWorker, localSerializedSubscriber)
    {
      final Subscriber<?> self = this;
      final OperatorDebounceWithTime.DebounceState<T> state = new OperatorDebounceWithTime.DebounceState();

      public void onCompleted()
      {
        this.state.emitAndComplete(this.val$s, this);
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$s.onError(paramThrowable);
        unsubscribe();
        this.state.clear();
      }

      public void onNext(T paramT)
      {
        int i = this.state.next(paramT);
        this.val$serial.set(this.val$worker.schedule(new Action0(i)
        {
          public void call()
          {
            OperatorDebounceWithTime.1.this.state.emit(this.val$index, OperatorDebounceWithTime.1.this.val$s, OperatorDebounceWithTime.1.this.self);
          }
        }
        , OperatorDebounceWithTime.this.timeout, OperatorDebounceWithTime.this.unit));
      }

      public void onStart()
      {
        request(9223372036854775807L);
      }
    };
  }

  static final class DebounceState<T>
  {
    boolean emitting;
    boolean hasValue;
    int index;
    boolean terminate;
    T value;

    public void clear()
    {
      monitorenter;
      try
      {
        this.index = (1 + this.index);
        this.value = null;
        this.hasValue = false;
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
      }
      throw localObject;
    }

    // ERROR //
    public void emit(int paramInt, Subscriber<T> paramSubscriber, Subscriber<?> paramSubscriber1)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 31	rx/internal/operators/OperatorDebounceWithTime$DebounceState:emitting	Z
      //   6: ifne +18 -> 24
      //   9: aload_0
      //   10: getfield 25	rx/internal/operators/OperatorDebounceWithTime$DebounceState:hasValue	Z
      //   13: ifeq +11 -> 24
      //   16: iload_1
      //   17: aload_0
      //   18: getfield 21	rx/internal/operators/OperatorDebounceWithTime$DebounceState:index	I
      //   21: if_icmpeq +6 -> 27
      //   24: aload_0
      //   25: monitorexit
      //   26: return
      //   27: aload_0
      //   28: getfield 23	rx/internal/operators/OperatorDebounceWithTime$DebounceState:value	Ljava/lang/Object;
      //   31: astore 5
      //   33: aload_0
      //   34: aconst_null
      //   35: putfield 23	rx/internal/operators/OperatorDebounceWithTime$DebounceState:value	Ljava/lang/Object;
      //   38: aload_0
      //   39: iconst_0
      //   40: putfield 25	rx/internal/operators/OperatorDebounceWithTime$DebounceState:hasValue	Z
      //   43: aload_0
      //   44: iconst_1
      //   45: putfield 31	rx/internal/operators/OperatorDebounceWithTime$DebounceState:emitting	Z
      //   48: aload_0
      //   49: monitorexit
      //   50: aload_2
      //   51: aload 5
      //   53: invokevirtual 37	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   56: aload_0
      //   57: monitorenter
      //   58: aload_0
      //   59: getfield 39	rx/internal/operators/OperatorDebounceWithTime$DebounceState:terminate	Z
      //   62: ifne +36 -> 98
      //   65: aload_0
      //   66: iconst_0
      //   67: putfield 31	rx/internal/operators/OperatorDebounceWithTime$DebounceState:emitting	Z
      //   70: aload_0
      //   71: monitorexit
      //   72: return
      //   73: astore 7
      //   75: aload_0
      //   76: monitorexit
      //   77: aload 7
      //   79: athrow
      //   80: astore 4
      //   82: aload_0
      //   83: monitorexit
      //   84: aload 4
      //   86: athrow
      //   87: astore 6
      //   89: aload 6
      //   91: aload_3
      //   92: aload 5
      //   94: invokestatic 45	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;Ljava/lang/Object;)V
      //   97: return
      //   98: aload_0
      //   99: monitorexit
      //   100: aload_2
      //   101: invokevirtual 48	rx/Subscriber:onCompleted	()V
      //   104: return
      //
      // Exception table:
      //   from	to	target	type
      //   58	72	73	finally
      //   75	77	73	finally
      //   98	100	73	finally
      //   2	24	80	finally
      //   24	26	80	finally
      //   27	50	80	finally
      //   82	84	80	finally
      //   50	56	87	java/lang/Throwable
    }

    // ERROR //
    public void emitAndComplete(Subscriber<T> paramSubscriber, Subscriber<?> paramSubscriber1)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 31	rx/internal/operators/OperatorDebounceWithTime$DebounceState:emitting	Z
      //   6: ifeq +11 -> 17
      //   9: aload_0
      //   10: iconst_1
      //   11: putfield 39	rx/internal/operators/OperatorDebounceWithTime$DebounceState:terminate	Z
      //   14: aload_0
      //   15: monitorexit
      //   16: return
      //   17: aload_0
      //   18: getfield 23	rx/internal/operators/OperatorDebounceWithTime$DebounceState:value	Ljava/lang/Object;
      //   21: astore 4
      //   23: aload_0
      //   24: getfield 25	rx/internal/operators/OperatorDebounceWithTime$DebounceState:hasValue	Z
      //   27: istore 5
      //   29: aload_0
      //   30: aconst_null
      //   31: putfield 23	rx/internal/operators/OperatorDebounceWithTime$DebounceState:value	Ljava/lang/Object;
      //   34: aload_0
      //   35: iconst_0
      //   36: putfield 25	rx/internal/operators/OperatorDebounceWithTime$DebounceState:hasValue	Z
      //   39: aload_0
      //   40: iconst_1
      //   41: putfield 31	rx/internal/operators/OperatorDebounceWithTime$DebounceState:emitting	Z
      //   44: aload_0
      //   45: monitorexit
      //   46: iload 5
      //   48: ifeq +9 -> 57
      //   51: aload_1
      //   52: aload 4
      //   54: invokevirtual 37	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   57: aload_1
      //   58: invokevirtual 48	rx/Subscriber:onCompleted	()V
      //   61: return
      //   62: astore_3
      //   63: aload_0
      //   64: monitorexit
      //   65: aload_3
      //   66: athrow
      //   67: astore 6
      //   69: aload 6
      //   71: aload_2
      //   72: aload 4
      //   74: invokestatic 45	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;Ljava/lang/Object;)V
      //   77: return
      //
      // Exception table:
      //   from	to	target	type
      //   2	16	62	finally
      //   17	46	62	finally
      //   63	65	62	finally
      //   51	57	67	java/lang/Throwable
    }

    public int next(T paramT)
    {
      monitorenter;
      try
      {
        this.value = paramT;
        this.hasValue = true;
        int i = 1 + this.index;
        this.index = i;
        monitorexit;
        return i;
      }
      finally
      {
        localObject = finally;
        monitorexit;
      }
      throw localObject;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorDebounceWithTime
 * JD-Core Version:    0.6.0
 */