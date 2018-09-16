package rx.internal.operators;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observers.SerializedSubscriber;
import rx.subscriptions.CompositeSubscription;

public final class OperatorBufferWithStartEndObservable<T, TOpening, TClosing>
  implements Observable.Operator<List<T>, T>
{
  final Func1<? super TOpening, ? extends Observable<? extends TClosing>> bufferClosing;
  final Observable<? extends TOpening> bufferOpening;

  public OperatorBufferWithStartEndObservable(Observable<? extends TOpening> paramObservable, Func1<? super TOpening, ? extends Observable<? extends TClosing>> paramFunc1)
  {
    this.bufferOpening = paramObservable;
    this.bufferClosing = paramFunc1;
  }

  public Subscriber<? super T> call(Subscriber<? super List<T>> paramSubscriber)
  {
    BufferingSubscriber localBufferingSubscriber = new BufferingSubscriber(new SerializedSubscriber(paramSubscriber));
    1 local1 = new Subscriber(localBufferingSubscriber)
    {
      public void onCompleted()
      {
        this.val$s.onCompleted();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$s.onError(paramThrowable);
      }

      public void onNext(TOpening paramTOpening)
      {
        this.val$s.startBuffer(paramTOpening);
      }
    };
    paramSubscriber.add(local1);
    paramSubscriber.add(localBufferingSubscriber);
    this.bufferOpening.unsafeSubscribe(local1);
    return localBufferingSubscriber;
  }

  final class BufferingSubscriber extends Subscriber<T>
  {
    final Subscriber<? super List<T>> child;
    final List<List<T>> chunks;
    final CompositeSubscription closingSubscriptions;
    boolean done;

    public BufferingSubscriber()
    {
      Object localObject;
      this.child = localObject;
      this.chunks = new LinkedList();
      this.closingSubscriptions = new CompositeSubscription();
      add(this.closingSubscriptions);
    }

    void endBuffer(List<T> paramList)
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
          i = 1;
          localIterator.remove();
        }
        monitorexit;
        if (i != 0)
        {
          this.child.onNext(paramList);
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
      //   3: getfield 44	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:done	Z
      //   6: ifeq +6 -> 12
      //   9: aload_0
      //   10: monitorexit
      //   11: return
      //   12: aload_0
      //   13: iconst_1
      //   14: putfield 44	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:done	Z
      //   17: new 28	java/util/LinkedList
      //   20: dup
      //   21: aload_0
      //   22: getfield 31	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:chunks	Ljava/util/List;
      //   25: invokespecial 73	java/util/LinkedList:<init>	(Ljava/util/Collection;)V
      //   28: astore_3
      //   29: aload_0
      //   30: getfield 31	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:chunks	Ljava/util/List;
      //   33: invokeinterface 76 1 0
      //   38: aload_0
      //   39: monitorexit
      //   40: aload_3
      //   41: invokeinterface 50 1 0
      //   46: astore 4
      //   48: aload 4
      //   50: invokeinterface 56 1 0
      //   55: ifeq +42 -> 97
      //   58: aload 4
      //   60: invokeinterface 60 1 0
      //   65: checkcast 46	java/util/List
      //   68: astore 5
      //   70: aload_0
      //   71: getfield 26	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:child	Lrx/Subscriber;
      //   74: aload 5
      //   76: invokevirtual 67	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   79: goto -31 -> 48
      //   82: astore_1
      //   83: aload_1
      //   84: aload_0
      //   85: getfield 26	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:child	Lrx/Subscriber;
      //   88: invokestatic 82	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
      //   91: return
      //   92: astore_2
      //   93: aload_0
      //   94: monitorexit
      //   95: aload_2
      //   96: athrow
      //   97: aload_0
      //   98: getfield 26	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:child	Lrx/Subscriber;
      //   101: invokevirtual 84	rx/Subscriber:onCompleted	()V
      //   104: aload_0
      //   105: invokevirtual 87	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:unsubscribe	()V
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

    public void onNext(T paramT)
    {
      monitorenter;
      try
      {
        Iterator localIterator = this.chunks.iterator();
        while (localIterator.hasNext())
          ((List)localIterator.next()).add(paramT);
      }
      finally
      {
        monitorexit;
      }
      monitorexit;
    }

    // ERROR //
    void startBuffer(TOpening paramTOpening)
    {
      // Byte code:
      //   0: new 97	java/util/ArrayList
      //   3: dup
      //   4: invokespecial 98	java/util/ArrayList:<init>	()V
      //   7: astore_2
      //   8: aload_0
      //   9: monitorenter
      //   10: aload_0
      //   11: getfield 44	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:done	Z
      //   14: ifeq +6 -> 20
      //   17: aload_0
      //   18: monitorexit
      //   19: return
      //   20: aload_0
      //   21: getfield 31	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:chunks	Ljava/util/List;
      //   24: aload_2
      //   25: invokeinterface 94 2 0
      //   30: pop
      //   31: aload_0
      //   32: monitorexit
      //   33: aload_0
      //   34: getfield 21	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:this$0	Lrx/internal/operators/OperatorBufferWithStartEndObservable;
      //   37: getfield 104	rx/internal/operators/OperatorBufferWithStartEndObservable:bufferClosing	Lrx/functions/Func1;
      //   40: aload_1
      //   41: invokeinterface 110 2 0
      //   46: checkcast 112	rx/Observable
      //   49: astore 6
      //   51: new 114	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber$1
      //   54: dup
      //   55: aload_0
      //   56: aload_2
      //   57: invokespecial 117	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber$1:<init>	(Lrx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber;Ljava/util/List;)V
      //   60: astore 7
      //   62: aload_0
      //   63: getfield 36	rx/internal/operators/OperatorBufferWithStartEndObservable$BufferingSubscriber:closingSubscriptions	Lrx/subscriptions/CompositeSubscription;
      //   66: aload 7
      //   68: invokevirtual 118	rx/subscriptions/CompositeSubscription:add	(Lrx/Subscription;)V
      //   71: aload 6
      //   73: aload 7
      //   75: invokevirtual 122	rx/Observable:unsafeSubscribe	(Lrx/Subscriber;)Lrx/Subscription;
      //   78: pop
      //   79: return
      //   80: astore_3
      //   81: aload_0
      //   82: monitorexit
      //   83: aload_3
      //   84: athrow
      //   85: astore 5
      //   87: aload 5
      //   89: aload_0
      //   90: invokestatic 82	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
      //   93: return
      //
      // Exception table:
      //   from	to	target	type
      //   10	19	80	finally
      //   20	33	80	finally
      //   81	83	80	finally
      //   33	51	85	java/lang/Throwable
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorBufferWithStartEndObservable
 * JD-Core Version:    0.6.0
 */