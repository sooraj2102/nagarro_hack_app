package rx.internal.operators;

import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

public final class SingleOperatorOnErrorResumeNext<T>
  implements Single.OnSubscribe<T>
{
  private final Single<? extends T> originalSingle;
  final Func1<Throwable, ? extends Single<? extends T>> resumeFunctionInCaseOfError;

  private SingleOperatorOnErrorResumeNext(Single<? extends T> paramSingle, Func1<Throwable, ? extends Single<? extends T>> paramFunc1)
  {
    if (paramSingle == null)
      throw new NullPointerException("originalSingle must not be null");
    if (paramFunc1 == null)
      throw new NullPointerException("resumeFunctionInCaseOfError must not be null");
    this.originalSingle = paramSingle;
    this.resumeFunctionInCaseOfError = paramFunc1;
  }

  public static <T> SingleOperatorOnErrorResumeNext<T> withFunction(Single<? extends T> paramSingle, Func1<Throwable, ? extends Single<? extends T>> paramFunc1)
  {
    return new SingleOperatorOnErrorResumeNext(paramSingle, paramFunc1);
  }

  public static <T> SingleOperatorOnErrorResumeNext<T> withOther(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2)
  {
    if (paramSingle2 == null)
      throw new NullPointerException("resumeSingleInCaseOfError must not be null");
    return new SingleOperatorOnErrorResumeNext(paramSingle1, new Func1(paramSingle2)
    {
      public Single<? extends T> call(Throwable paramThrowable)
      {
        return this.val$resumeSingleInCaseOfError;
      }
    });
  }

  public void call(SingleSubscriber<? super T> paramSingleSubscriber)
  {
    2 local2 = new SingleSubscriber(paramSingleSubscriber)
    {
      public void onError(Throwable paramThrowable)
      {
        try
        {
          ((Single)SingleOperatorOnErrorResumeNext.this.resumeFunctionInCaseOfError.call(paramThrowable)).subscribe(this.val$child);
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwOrReport(localThrowable, this.val$child);
        }
      }

      public void onSuccess(T paramT)
      {
        this.val$child.onSuccess(paramT);
      }
    };
    paramSingleSubscriber.add(local2);
    this.originalSingle.subscribe(local2);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.SingleOperatorOnErrorResumeNext
 * JD-Core Version:    0.6.0
 */