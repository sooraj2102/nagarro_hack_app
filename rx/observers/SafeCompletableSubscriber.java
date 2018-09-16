package rx.observers;

import rx.CompletableSubscriber;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.OnErrorFailedException;
import rx.plugins.RxJavaHooks;

@Experimental
public final class SafeCompletableSubscriber
  implements CompletableSubscriber, Subscription
{
  final CompletableSubscriber actual;
  boolean done;
  Subscription s;

  public SafeCompletableSubscriber(CompletableSubscriber paramCompletableSubscriber)
  {
    this.actual = paramCompletableSubscriber;
  }

  public boolean isUnsubscribed()
  {
    return (this.done) || (this.s.isUnsubscribed());
  }

  public void onCompleted()
  {
    if (this.done)
      return;
    this.done = true;
    try
    {
      this.actual.onCompleted();
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwIfFatal(localThrowable);
    }
    throw new OnCompletedFailedException(localThrowable);
  }

  public void onError(Throwable paramThrowable)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 50	rx/plugins/RxJavaHooks:onError	(Ljava/lang/Throwable;)V
    //   4: aload_0
    //   5: getfield 26	rx/observers/SafeCompletableSubscriber:done	Z
    //   8: ifeq +4 -> 12
    //   11: return
    //   12: aload_0
    //   13: iconst_1
    //   14: putfield 26	rx/observers/SafeCompletableSubscriber:done	Z
    //   17: aload_0
    //   18: getfield 22	rx/observers/SafeCompletableSubscriber:actual	Lrx/CompletableSubscriber;
    //   21: aload_1
    //   22: invokeinterface 51 2 0
    //   27: return
    //   28: astore_2
    //   29: aload_2
    //   30: invokestatic 41	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
    //   33: new 53	rx/exceptions/OnErrorFailedException
    //   36: dup
    //   37: new 55	rx/exceptions/CompositeException
    //   40: dup
    //   41: iconst_2
    //   42: anewarray 33	java/lang/Throwable
    //   45: dup
    //   46: iconst_0
    //   47: aload_1
    //   48: aastore
    //   49: dup
    //   50: iconst_1
    //   51: aload_2
    //   52: aastore
    //   53: invokespecial 58	rx/exceptions/CompositeException:<init>	([Ljava/lang/Throwable;)V
    //   56: invokespecial 59	rx/exceptions/OnErrorFailedException:<init>	(Ljava/lang/Throwable;)V
    //   59: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   17	27	28	java/lang/Throwable
  }

  public void onSubscribe(Subscription paramSubscription)
  {
    this.s = paramSubscription;
    try
    {
      this.actual.onSubscribe(this);
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwIfFatal(localThrowable);
      paramSubscription.unsubscribe();
      onError(localThrowable);
    }
  }

  public void unsubscribe()
  {
    this.s.unsubscribe();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.observers.SafeCompletableSubscriber
 * JD-Core Version:    0.6.0
 */