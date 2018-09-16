package rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public final class OnSubscribeUsing<T, Resource>
  implements Observable.OnSubscribe<T>
{
  private final Action1<? super Resource> dispose;
  private final boolean disposeEagerly;
  private final Func1<? super Resource, ? extends Observable<? extends T>> observableFactory;
  private final Func0<Resource> resourceFactory;

  public OnSubscribeUsing(Func0<Resource> paramFunc0, Func1<? super Resource, ? extends Observable<? extends T>> paramFunc1, Action1<? super Resource> paramAction1, boolean paramBoolean)
  {
    this.resourceFactory = paramFunc0;
    this.observableFactory = paramFunc1;
    this.dispose = paramAction1;
    this.disposeEagerly = paramBoolean;
  }

  private Throwable dispose(Action0 paramAction0)
  {
    try
    {
      paramAction0.call();
      return null;
    }
    catch (Throwable localThrowable)
    {
    }
    return localThrowable;
  }

  // ERROR //
  public void call(rx.Subscriber<? super T> paramSubscriber)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 25	rx/internal/operators/OnSubscribeUsing:resourceFactory	Lrx/functions/Func0;
    //   4: invokeinterface 50 1 0
    //   9: astore_3
    //   10: new 52	rx/internal/operators/OnSubscribeUsing$DisposeAction
    //   13: dup
    //   14: aload_0
    //   15: getfield 29	rx/internal/operators/OnSubscribeUsing:dispose	Lrx/functions/Action1;
    //   18: aload_3
    //   19: invokespecial 55	rx/internal/operators/OnSubscribeUsing$DisposeAction:<init>	(Lrx/functions/Action1;Ljava/lang/Object;)V
    //   22: astore 4
    //   24: aload_1
    //   25: aload 4
    //   27: invokevirtual 59	rx/Subscriber:add	(Lrx/Subscription;)V
    //   30: aload_0
    //   31: getfield 27	rx/internal/operators/OnSubscribeUsing:observableFactory	Lrx/functions/Func1;
    //   34: aload_3
    //   35: invokeinterface 64 2 0
    //   40: checkcast 66	rx/Observable
    //   43: astore 7
    //   45: aload_0
    //   46: getfield 31	rx/internal/operators/OnSubscribeUsing:disposeEagerly	Z
    //   49: ifeq +92 -> 141
    //   52: aload 7
    //   54: aload 4
    //   56: invokevirtual 70	rx/Observable:doOnTerminate	(Lrx/functions/Action0;)Lrx/Observable;
    //   59: astore 12
    //   61: aload 12
    //   63: astore 8
    //   65: aload 8
    //   67: aload_1
    //   68: invokestatic 76	rx/observers/Subscribers:wrap	(Lrx/Subscriber;)Lrx/Subscriber;
    //   71: invokevirtual 80	rx/Observable:unsafeSubscribe	(Lrx/Subscriber;)Lrx/Subscription;
    //   74: pop
    //   75: return
    //   76: astore 5
    //   78: aload_0
    //   79: aload 4
    //   81: invokespecial 82	rx/internal/operators/OnSubscribeUsing:dispose	(Lrx/functions/Action0;)Ljava/lang/Throwable;
    //   84: astore 6
    //   86: aload 5
    //   88: invokestatic 88	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
    //   91: aload 6
    //   93: invokestatic 88	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
    //   96: aload 6
    //   98: ifnull +36 -> 134
    //   101: aload_1
    //   102: new 90	rx/exceptions/CompositeException
    //   105: dup
    //   106: iconst_2
    //   107: anewarray 34	java/lang/Throwable
    //   110: dup
    //   111: iconst_0
    //   112: aload 5
    //   114: aastore
    //   115: dup
    //   116: iconst_1
    //   117: aload 6
    //   119: aastore
    //   120: invokespecial 93	rx/exceptions/CompositeException:<init>	([Ljava/lang/Throwable;)V
    //   123: invokevirtual 96	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
    //   126: return
    //   127: astore_2
    //   128: aload_2
    //   129: aload_1
    //   130: invokestatic 100	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
    //   133: return
    //   134: aload_1
    //   135: aload 5
    //   137: invokevirtual 96	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
    //   140: return
    //   141: aload 7
    //   143: aload 4
    //   145: invokevirtual 103	rx/Observable:doAfterTerminate	(Lrx/functions/Action0;)Lrx/Observable;
    //   148: astore 8
    //   150: goto -85 -> 65
    //   153: astore 9
    //   155: aload_0
    //   156: aload 4
    //   158: invokespecial 82	rx/internal/operators/OnSubscribeUsing:dispose	(Lrx/functions/Action0;)Ljava/lang/Throwable;
    //   161: astore 10
    //   163: aload 9
    //   165: invokestatic 88	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
    //   168: aload 10
    //   170: invokestatic 88	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
    //   173: aload 10
    //   175: ifnull +29 -> 204
    //   178: aload_1
    //   179: new 90	rx/exceptions/CompositeException
    //   182: dup
    //   183: iconst_2
    //   184: anewarray 34	java/lang/Throwable
    //   187: dup
    //   188: iconst_0
    //   189: aload 9
    //   191: aastore
    //   192: dup
    //   193: iconst_1
    //   194: aload 10
    //   196: aastore
    //   197: invokespecial 93	rx/exceptions/CompositeException:<init>	([Ljava/lang/Throwable;)V
    //   200: invokevirtual 96	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
    //   203: return
    //   204: aload_1
    //   205: aload 9
    //   207: invokevirtual 96	rx/Subscriber:onError	(Ljava/lang/Throwable;)V
    //   210: return
    //
    // Exception table:
    //   from	to	target	type
    //   30	45	76	java/lang/Throwable
    //   0	30	127	java/lang/Throwable
    //   45	61	127	java/lang/Throwable
    //   78	96	127	java/lang/Throwable
    //   101	126	127	java/lang/Throwable
    //   134	140	127	java/lang/Throwable
    //   141	150	127	java/lang/Throwable
    //   155	173	127	java/lang/Throwable
    //   178	203	127	java/lang/Throwable
    //   204	210	127	java/lang/Throwable
    //   65	75	153	java/lang/Throwable
  }

  static final class DisposeAction<Resource> extends AtomicBoolean
    implements Action0, Subscription
  {
    private static final long serialVersionUID = 4262875056400218316L;
    private Action1<? super Resource> dispose;
    private Resource resource;

    DisposeAction(Action1<? super Resource> paramAction1, Resource paramResource)
    {
      this.dispose = paramAction1;
      this.resource = paramResource;
      lazySet(false);
    }

    public void call()
    {
      if (compareAndSet(false, true));
      try
      {
        this.dispose.call(this.resource);
        return;
      }
      finally
      {
        this.resource = null;
        this.dispose = null;
      }
      throw localObject;
    }

    public boolean isUnsubscribed()
    {
      return get();
    }

    public void unsubscribe()
    {
      call();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeUsing
 * JD-Core Version:    0.6.0
 */