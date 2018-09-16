package rx.observers;

import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.UnsubscribeFailedException;
import rx.plugins.RxJavaHooks;

public class SafeSubscriber<T> extends Subscriber<T>
{
  private final Subscriber<? super T> actual;
  boolean done;

  public SafeSubscriber(Subscriber<? super T> paramSubscriber)
  {
    super(paramSubscriber);
    this.actual = paramSubscriber;
  }

  // ERROR //
  protected void _onError(Throwable paramThrowable)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 27	rx/plugins/RxJavaHooks:onError	(Ljava/lang/Throwable;)V
    //   4: aload_0
    //   5: getfield 16	rx/observers/SafeSubscriber:actual	Lrx/Subscriber;
    //   8: aload_1
    //   9: invokevirtual 28	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
    //   12: aload_0
    //   13: invokevirtual 32	rx/observers/SafeSubscriber:unsubscribe	()V
    //   16: return
    //   17: astore 4
    //   19: aload_0
    //   20: invokevirtual 32	rx/observers/SafeSubscriber:unsubscribe	()V
    //   23: aload 4
    //   25: athrow
    //   26: astore 5
    //   28: aload 5
    //   30: invokestatic 27	rx/plugins/RxJavaHooks:onError	(Ljava/lang/Throwable;)V
    //   33: new 20	rx/exceptions/OnErrorNotImplementedException
    //   36: dup
    //   37: ldc 34
    //   39: new 36	rx/exceptions/CompositeException
    //   42: dup
    //   43: iconst_2
    //   44: anewarray 22	java/lang/Throwable
    //   47: dup
    //   48: iconst_0
    //   49: aload_1
    //   50: aastore
    //   51: dup
    //   52: iconst_1
    //   53: aload 5
    //   55: aastore
    //   56: invokestatic 42	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
    //   59: invokespecial 45	rx/exceptions/CompositeException:<init>	(Ljava/util/Collection;)V
    //   62: invokespecial 48	rx/exceptions/OnErrorNotImplementedException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   65: athrow
    //   66: astore_2
    //   67: aload_2
    //   68: invokestatic 27	rx/plugins/RxJavaHooks:onError	(Ljava/lang/Throwable;)V
    //   71: aload_0
    //   72: invokevirtual 32	rx/observers/SafeSubscriber:unsubscribe	()V
    //   75: new 50	rx/exceptions/OnErrorFailedException
    //   78: dup
    //   79: ldc 52
    //   81: new 36	rx/exceptions/CompositeException
    //   84: dup
    //   85: iconst_2
    //   86: anewarray 22	java/lang/Throwable
    //   89: dup
    //   90: iconst_0
    //   91: aload_1
    //   92: aastore
    //   93: dup
    //   94: iconst_1
    //   95: aload_2
    //   96: aastore
    //   97: invokestatic 42	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
    //   100: invokespecial 45	rx/exceptions/CompositeException:<init>	(Ljava/util/Collection;)V
    //   103: invokespecial 53	rx/exceptions/OnErrorFailedException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   106: athrow
    //   107: astore_3
    //   108: aload_3
    //   109: invokestatic 27	rx/plugins/RxJavaHooks:onError	(Ljava/lang/Throwable;)V
    //   112: new 50	rx/exceptions/OnErrorFailedException
    //   115: dup
    //   116: ldc 55
    //   118: new 36	rx/exceptions/CompositeException
    //   121: dup
    //   122: iconst_3
    //   123: anewarray 22	java/lang/Throwable
    //   126: dup
    //   127: iconst_0
    //   128: aload_1
    //   129: aastore
    //   130: dup
    //   131: iconst_1
    //   132: aload_2
    //   133: aastore
    //   134: dup
    //   135: iconst_2
    //   136: aload_3
    //   137: aastore
    //   138: invokestatic 42	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
    //   141: invokespecial 45	rx/exceptions/CompositeException:<init>	(Ljava/util/Collection;)V
    //   144: invokespecial 53	rx/exceptions/OnErrorFailedException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   147: athrow
    //   148: astore 6
    //   150: aload 6
    //   152: invokestatic 27	rx/plugins/RxJavaHooks:onError	(Ljava/lang/Throwable;)V
    //   155: new 50	rx/exceptions/OnErrorFailedException
    //   158: dup
    //   159: aload 6
    //   161: invokespecial 57	rx/exceptions/OnErrorFailedException:<init>	(Ljava/lang/Throwable;)V
    //   164: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   4	12	17	rx/exceptions/OnErrorNotImplementedException
    //   19	23	26	java/lang/Throwable
    //   4	12	66	java/lang/Throwable
    //   71	75	107	java/lang/Throwable
    //   12	16	148	java/lang/Throwable
  }

  public Subscriber<? super T> getActual()
  {
    return this.actual;
  }

  public void onCompleted()
  {
    if (!this.done)
      this.done = true;
    try
    {
      this.actual.onCompleted();
      try
      {
        unsubscribe();
        return;
      }
      catch (Throwable localThrowable3)
      {
        RxJavaHooks.onError(localThrowable3);
        throw new UnsubscribeFailedException(localThrowable3.getMessage(), localThrowable3);
      }
    }
    catch (Throwable localThrowable2)
    {
      Exceptions.throwIfFatal(localThrowable2);
      RxJavaHooks.onError(localThrowable2);
      throw new OnCompletedFailedException(localThrowable2.getMessage(), localThrowable2);
    }
    finally
    {
      try
      {
        unsubscribe();
        throw localObject;
      }
      catch (Throwable localThrowable1)
      {
        RxJavaHooks.onError(localThrowable1);
      }
    }
    throw new UnsubscribeFailedException(localThrowable1.getMessage(), localThrowable1);
  }

  public void onError(Throwable paramThrowable)
  {
    Exceptions.throwIfFatal(paramThrowable);
    if (!this.done)
    {
      this.done = true;
      _onError(paramThrowable);
    }
  }

  public void onNext(T paramT)
  {
    try
    {
      if (!this.done)
        this.actual.onNext(paramT);
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwOrReport(localThrowable, this);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.observers.SafeSubscriber
 * JD-Core Version:    0.6.0
 */