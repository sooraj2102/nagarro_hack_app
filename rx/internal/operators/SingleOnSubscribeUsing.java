package rx.internal.operators;

import java.util.Arrays;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;

public final class SingleOnSubscribeUsing<T, Resource>
  implements Single.OnSubscribe<T>
{
  final Action1<? super Resource> disposeAction;
  final boolean disposeEagerly;
  final Func0<Resource> resourceFactory;
  final Func1<? super Resource, ? extends Single<? extends T>> singleFactory;

  public SingleOnSubscribeUsing(Func0<Resource> paramFunc0, Func1<? super Resource, ? extends Single<? extends T>> paramFunc1, Action1<? super Resource> paramAction1, boolean paramBoolean)
  {
    this.resourceFactory = paramFunc0;
    this.singleFactory = paramFunc1;
    this.disposeAction = paramAction1;
    this.disposeEagerly = paramBoolean;
  }

  // ERROR //
  public void call(SingleSubscriber<? super T> paramSingleSubscriber)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 25	rx/internal/operators/SingleOnSubscribeUsing:resourceFactory	Lrx/functions/Func0;
    //   4: invokeinterface 45 1 0
    //   9: astore_3
    //   10: aload_0
    //   11: getfield 27	rx/internal/operators/SingleOnSubscribeUsing:singleFactory	Lrx/functions/Func1;
    //   14: aload_3
    //   15: invokeinterface 50 2 0
    //   20: checkcast 52	rx/Single
    //   23: astore 5
    //   25: aload 5
    //   27: ifnonnull +41 -> 68
    //   30: aload_0
    //   31: aload_1
    //   32: aload_3
    //   33: new 54	java/lang/NullPointerException
    //   36: dup
    //   37: ldc 56
    //   39: invokespecial 59	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   42: invokevirtual 63	rx/internal/operators/SingleOnSubscribeUsing:handleSubscriptionTimeError	(Lrx/SingleSubscriber;Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   45: return
    //   46: astore_2
    //   47: aload_2
    //   48: invokestatic 69	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
    //   51: aload_1
    //   52: aload_2
    //   53: invokevirtual 72	rx/SingleSubscriber:onError	(Ljava/lang/Throwable;)V
    //   56: return
    //   57: astore 4
    //   59: aload_0
    //   60: aload_1
    //   61: aload_3
    //   62: aload 4
    //   64: invokevirtual 63	rx/internal/operators/SingleOnSubscribeUsing:handleSubscriptionTimeError	(Lrx/SingleSubscriber;Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   67: return
    //   68: new 74	rx/internal/operators/SingleOnSubscribeUsing$1
    //   71: dup
    //   72: aload_0
    //   73: aload_3
    //   74: aload_1
    //   75: invokespecial 77	rx/internal/operators/SingleOnSubscribeUsing$1:<init>	(Lrx/internal/operators/SingleOnSubscribeUsing;Ljava/lang/Object;Lrx/SingleSubscriber;)V
    //   78: astore 6
    //   80: aload_1
    //   81: aload 6
    //   83: invokevirtual 81	rx/SingleSubscriber:add	(Lrx/Subscription;)V
    //   86: aload 5
    //   88: aload 6
    //   90: invokevirtual 85	rx/Single:subscribe	(Lrx/SingleSubscriber;)Lrx/Subscription;
    //   93: pop
    //   94: return
    //
    // Exception table:
    //   from	to	target	type
    //   0	10	46	java/lang/Throwable
    //   10	25	57	java/lang/Throwable
  }

  void handleSubscriptionTimeError(SingleSubscriber<? super T> paramSingleSubscriber, Resource paramResource, Throwable paramThrowable)
  {
    Exceptions.throwIfFatal(paramThrowable);
    if (this.disposeEagerly);
    try
    {
      this.disposeAction.call(paramResource);
      paramSingleSubscriber.onError(paramThrowable);
      if (this.disposeEagerly);
    }
    catch (Throwable localThrowable2)
    {
      try
      {
        this.disposeAction.call(paramResource);
        return;
        localThrowable2 = localThrowable2;
        Exceptions.throwIfFatal(localThrowable2);
        paramThrowable = new CompositeException(Arrays.asList(new Throwable[] { paramThrowable, localThrowable2 }));
      }
      catch (Throwable localThrowable1)
      {
        Exceptions.throwIfFatal(localThrowable1);
        RxJavaHooks.onError(localThrowable1);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.SingleOnSubscribeUsing
 * JD-Core Version:    0.6.0
 */