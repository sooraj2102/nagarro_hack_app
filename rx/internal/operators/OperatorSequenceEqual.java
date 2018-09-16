package rx.internal.operators;

import rx.Observable;
import rx.functions.Func2;
import rx.internal.util.UtilityFunctions;

public final class OperatorSequenceEqual
{
  static final Object LOCAL_ON_COMPLETED = new Object();

  private OperatorSequenceEqual()
  {
    throw new IllegalStateException("No instances!");
  }

  static <T> Observable<Object> materializeLite(Observable<T> paramObservable)
  {
    return Observable.concat(paramObservable, Observable.just(LOCAL_ON_COMPLETED));
  }

  public static <T> Observable<Boolean> sequenceEqual(Observable<? extends T> paramObservable1, Observable<? extends T> paramObservable2, Func2<? super T, ? super T, Boolean> paramFunc2)
  {
    return Observable.zip(materializeLite(paramObservable1), materializeLite(paramObservable2), new Func2(paramFunc2)
    {
      public Boolean call(Object paramObject1, Object paramObject2)
      {
        int i;
        if (paramObject1 == OperatorSequenceEqual.LOCAL_ON_COMPLETED)
        {
          i = 1;
          if (paramObject2 != OperatorSequenceEqual.LOCAL_ON_COMPLETED)
            break label38;
        }
        label38: for (int j = 1; ; j = 0)
        {
          if ((i == 0) || (j == 0))
            break label44;
          return Boolean.valueOf(true);
          i = 0;
          break;
        }
        label44: if ((i != 0) || (j != 0))
          return Boolean.valueOf(false);
        return (Boolean)this.val$equality.call(paramObject1, paramObject2);
      }
    }).all(UtilityFunctions.identity());
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorSequenceEqual
 * JD-Core Version:    0.6.0
 */