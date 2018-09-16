package rx.internal.operators;

import rx.Observable.Operator;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.util.UtilityFunctions;

public final class OperatorDistinctUntilChanged<T, U>
  implements Observable.Operator<T, T>, Func2<U, U, Boolean>
{
  final Func2<? super U, ? super U, Boolean> comparator;
  final Func1<? super T, ? extends U> keySelector;

  public OperatorDistinctUntilChanged(Func1<? super T, ? extends U> paramFunc1)
  {
    this.keySelector = paramFunc1;
    this.comparator = this;
  }

  public OperatorDistinctUntilChanged(Func2<? super U, ? super U, Boolean> paramFunc2)
  {
    this.keySelector = UtilityFunctions.identity();
    this.comparator = paramFunc2;
  }

  public static <T> OperatorDistinctUntilChanged<T, T> instance()
  {
    return Holder.INSTANCE;
  }

  public Boolean call(U paramU1, U paramU2)
  {
    if ((paramU1 == paramU2) || ((paramU1 != null) && (paramU1.equals(paramU2))));
    for (boolean bool = true; ; bool = false)
      return Boolean.valueOf(bool);
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    return new Subscriber(paramSubscriber, paramSubscriber)
    {
      boolean hasPrevious;
      U previousKey;

      public void onCompleted()
      {
        this.val$child.onCompleted();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$child.onError(paramThrowable);
      }

      // ERROR //
      public void onNext(T paramT)
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 23	rx/internal/operators/OperatorDistinctUntilChanged$1:this$0	Lrx/internal/operators/OperatorDistinctUntilChanged;
        //   4: getfield 44	rx/internal/operators/OperatorDistinctUntilChanged:keySelector	Lrx/functions/Func1;
        //   7: aload_1
        //   8: invokeinterface 49 2 0
        //   13: astore_3
        //   14: aload_0
        //   15: getfield 51	rx/internal/operators/OperatorDistinctUntilChanged$1:previousKey	Ljava/lang/Object;
        //   18: astore 4
        //   20: aload_0
        //   21: aload_3
        //   22: putfield 51	rx/internal/operators/OperatorDistinctUntilChanged$1:previousKey	Ljava/lang/Object;
        //   25: aload_0
        //   26: getfield 53	rx/internal/operators/OperatorDistinctUntilChanged$1:hasPrevious	Z
        //   29: ifeq +70 -> 99
        //   32: aload_0
        //   33: getfield 23	rx/internal/operators/OperatorDistinctUntilChanged$1:this$0	Lrx/internal/operators/OperatorDistinctUntilChanged;
        //   36: getfield 57	rx/internal/operators/OperatorDistinctUntilChanged:comparator	Lrx/functions/Func2;
        //   39: aload 4
        //   41: aload_3
        //   42: invokeinterface 62 3 0
        //   47: checkcast 64	java/lang/Boolean
        //   50: invokevirtual 68	java/lang/Boolean:booleanValue	()Z
        //   53: istore 6
        //   55: iload 6
        //   57: ifne +36 -> 93
        //   60: aload_0
        //   61: getfield 25	rx/internal/operators/OperatorDistinctUntilChanged$1:val$child	Lrx/Subscriber;
        //   64: aload_1
        //   65: invokevirtual 70	rx/Subscriber:onNext	(Ljava/lang/Object;)V
        //   68: return
        //   69: astore_2
        //   70: aload_2
        //   71: aload_0
        //   72: getfield 25	rx/internal/operators/OperatorDistinctUntilChanged$1:val$child	Lrx/Subscriber;
        //   75: aload_1
        //   76: invokestatic 76	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;Ljava/lang/Object;)V
        //   79: return
        //   80: astore 5
        //   82: aload 5
        //   84: aload_0
        //   85: getfield 25	rx/internal/operators/OperatorDistinctUntilChanged$1:val$child	Lrx/Subscriber;
        //   88: aload_3
        //   89: invokestatic 76	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;Ljava/lang/Object;)V
        //   92: return
        //   93: aload_0
        //   94: lconst_1
        //   95: invokevirtual 80	rx/internal/operators/OperatorDistinctUntilChanged$1:request	(J)V
        //   98: return
        //   99: aload_0
        //   100: iconst_1
        //   101: putfield 53	rx/internal/operators/OperatorDistinctUntilChanged$1:hasPrevious	Z
        //   104: aload_0
        //   105: getfield 25	rx/internal/operators/OperatorDistinctUntilChanged$1:val$child	Lrx/Subscriber;
        //   108: aload_1
        //   109: invokevirtual 70	rx/Subscriber:onNext	(Ljava/lang/Object;)V
        //   112: return
        //
        // Exception table:
        //   from	to	target	type
        //   0	14	69	java/lang/Throwable
        //   32	55	80	java/lang/Throwable
      }
    };
  }

  static final class Holder
  {
    static final OperatorDistinctUntilChanged<?, ?> INSTANCE = new OperatorDistinctUntilChanged(UtilityFunctions.identity());
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorDistinctUntilChanged
 * JD-Core Version:    0.6.0
 */