package rx.plugins;

import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.Completable.Operator;
import rx.annotations.Experimental;

@Experimental
public abstract class RxJavaCompletableExecutionHook
{
  @Deprecated
  public Completable.OnSubscribe onCreate(Completable.OnSubscribe paramOnSubscribe)
  {
    return paramOnSubscribe;
  }

  @Deprecated
  public Completable.Operator onLift(Completable.Operator paramOperator)
  {
    return paramOperator;
  }

  @Deprecated
  public Throwable onSubscribeError(Throwable paramThrowable)
  {
    return paramThrowable;
  }

  @Deprecated
  public Completable.OnSubscribe onSubscribeStart(Completable paramCompletable, Completable.OnSubscribe paramOnSubscribe)
  {
    return paramOnSubscribe;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.plugins.RxJavaCompletableExecutionHook
 * JD-Core Version:    0.6.0
 */