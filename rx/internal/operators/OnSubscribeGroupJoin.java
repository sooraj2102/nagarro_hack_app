package rx.internal.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.SerializedSubscriber;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.RefCountSubscription;

public final class OnSubscribeGroupJoin<T1, T2, D1, D2, R>
  implements Observable.OnSubscribe<R>
{
  final Observable<T1> left;
  final Func1<? super T1, ? extends Observable<D1>> leftDuration;
  final Func2<? super T1, ? super Observable<T2>, ? extends R> resultSelector;
  final Observable<T2> right;
  final Func1<? super T2, ? extends Observable<D2>> rightDuration;

  public OnSubscribeGroupJoin(Observable<T1> paramObservable, Observable<T2> paramObservable1, Func1<? super T1, ? extends Observable<D1>> paramFunc1, Func1<? super T2, ? extends Observable<D2>> paramFunc11, Func2<? super T1, ? super Observable<T2>, ? extends R> paramFunc2)
  {
    this.left = paramObservable;
    this.right = paramObservable1;
    this.leftDuration = paramFunc1;
    this.rightDuration = paramFunc11;
    this.resultSelector = paramFunc2;
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    ResultManager localResultManager = new ResultManager(new SerializedSubscriber(paramSubscriber));
    paramSubscriber.add(localResultManager);
    localResultManager.init();
  }

  final class ResultManager extends HashMap<Integer, Observer<T2>>
    implements Subscription
  {
    private static final long serialVersionUID = -3035156013812425335L;
    final RefCountSubscription cancel;
    final CompositeSubscription group;
    boolean leftDone;
    int leftIds;
    boolean rightDone;
    int rightIds;
    final Map<Integer, T2> rightMap = new HashMap();
    final Subscriber<? super R> subscriber;

    public ResultManager()
    {
      Object localObject;
      this.subscriber = localObject;
      this.group = new CompositeSubscription();
      this.cancel = new RefCountSubscription(this.group);
    }

    void complete(List<Observer<T2>> paramList)
    {
      if (paramList != null)
      {
        Iterator localIterator = paramList.iterator();
        while (localIterator.hasNext())
          ((Observer)localIterator.next()).onCompleted();
        this.subscriber.onCompleted();
        this.cancel.unsubscribe();
      }
    }

    void errorAll(Throwable paramThrowable)
    {
      monitorenter;
      try
      {
        ArrayList localArrayList = new ArrayList(leftMap().values());
        leftMap().clear();
        this.rightMap.clear();
        monitorexit;
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
          ((Observer)localIterator.next()).onError(paramThrowable);
      }
      finally
      {
        monitorexit;
      }
      this.subscriber.onError(paramThrowable);
      this.cancel.unsubscribe();
    }

    void errorMain(Throwable paramThrowable)
    {
      monitorenter;
      try
      {
        leftMap().clear();
        this.rightMap.clear();
        monitorexit;
        this.subscriber.onError(paramThrowable);
        this.cancel.unsubscribe();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void init()
    {
      LeftObserver localLeftObserver = new LeftObserver();
      RightObserver localRightObserver = new RightObserver();
      this.group.add(localLeftObserver);
      this.group.add(localRightObserver);
      OnSubscribeGroupJoin.this.left.unsafeSubscribe(localLeftObserver);
      OnSubscribeGroupJoin.this.right.unsafeSubscribe(localRightObserver);
    }

    public boolean isUnsubscribed()
    {
      return this.cancel.isUnsubscribed();
    }

    Map<Integer, Observer<T2>> leftMap()
    {
      return this;
    }

    public void unsubscribe()
    {
      this.cancel.unsubscribe();
    }

    final class LeftDurationObserver extends Subscriber<D1>
    {
      final int id;
      boolean once = true;

      public LeftDurationObserver(int arg2)
      {
        int i;
        this.id = i;
      }

      public void onCompleted()
      {
        if (this.once)
          this.once = false;
        synchronized (OnSubscribeGroupJoin.ResultManager.this)
        {
          Observer localObserver = (Observer)OnSubscribeGroupJoin.ResultManager.this.leftMap().remove(Integer.valueOf(this.id));
          if (localObserver != null)
            localObserver.onCompleted();
          OnSubscribeGroupJoin.ResultManager.this.group.remove(this);
          return;
        }
      }

      public void onError(Throwable paramThrowable)
      {
        OnSubscribeGroupJoin.ResultManager.this.errorMain(paramThrowable);
      }

      public void onNext(D1 paramD1)
      {
        onCompleted();
      }
    }

    final class LeftObserver extends Subscriber<T1>
    {
      LeftObserver()
      {
      }

      public void onCompleted()
      {
        Object localObject3;
        ArrayList localArrayList;
        synchronized (OnSubscribeGroupJoin.ResultManager.this)
        {
          OnSubscribeGroupJoin.ResultManager.this.leftDone = true;
          boolean bool = OnSubscribeGroupJoin.ResultManager.this.rightDone;
          localObject3 = null;
          if (bool)
            localArrayList = new ArrayList(OnSubscribeGroupJoin.ResultManager.this.leftMap().values());
        }
        try
        {
          OnSubscribeGroupJoin.ResultManager.this.leftMap().clear();
          OnSubscribeGroupJoin.ResultManager.this.rightMap.clear();
          localObject3 = localArrayList;
          monitorexit;
          OnSubscribeGroupJoin.ResultManager.this.complete(localObject3);
          return;
          localObject1 = finally;
          monitorexit;
          throw localObject1;
        }
        finally
        {
        }
      }

      public void onError(Throwable paramThrowable)
      {
        OnSubscribeGroupJoin.ResultManager.this.errorAll(paramThrowable);
      }

      // ERROR //
      public void onNext(T1 paramT1)
      {
        // Byte code:
        //   0: invokestatic 65	rx/subjects/PublishSubject:create	()Lrx/subjects/PublishSubject;
        //   3: astore_3
        //   4: new 67	rx/observers/SerializedObserver
        //   7: dup
        //   8: aload_3
        //   9: invokespecial 70	rx/observers/SerializedObserver:<init>	(Lrx/Observer;)V
        //   12: astore 4
        //   14: aload_0
        //   15: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   18: astore 5
        //   20: aload 5
        //   22: monitorenter
        //   23: aload_0
        //   24: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   27: astore 7
        //   29: aload 7
        //   31: getfield 74	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:leftIds	I
        //   34: istore 8
        //   36: aload 7
        //   38: iload 8
        //   40: iconst_1
        //   41: iadd
        //   42: putfield 74	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:leftIds	I
        //   45: aload_0
        //   46: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   49: invokevirtual 30	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:leftMap	()Ljava/util/Map;
        //   52: iload 8
        //   54: invokestatic 80	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   57: aload 4
        //   59: invokeinterface 84 3 0
        //   64: pop
        //   65: aload 5
        //   67: monitorexit
        //   68: new 86	rx/internal/operators/OnSubscribeGroupJoin$WindowObservableFunc
        //   71: dup
        //   72: aload_3
        //   73: aload_0
        //   74: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   77: getfield 90	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:cancel	Lrx/subscriptions/RefCountSubscription;
        //   80: invokespecial 93	rx/internal/operators/OnSubscribeGroupJoin$WindowObservableFunc:<init>	(Lrx/Observable;Lrx/subscriptions/RefCountSubscription;)V
        //   83: invokestatic 98	rx/Observable:create	(Lrx/Observable$OnSubscribe;)Lrx/Observable;
        //   86: astore 10
        //   88: aload_0
        //   89: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   92: getfield 102	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:this$0	Lrx/internal/operators/OnSubscribeGroupJoin;
        //   95: getfield 108	rx/internal/operators/OnSubscribeGroupJoin:leftDuration	Lrx/functions/Func1;
        //   98: aload_1
        //   99: invokeinterface 114 2 0
        //   104: checkcast 95	rx/Observable
        //   107: astore 11
        //   109: new 116	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftDurationObserver
        //   112: dup
        //   113: aload_0
        //   114: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   117: iload 8
        //   119: invokespecial 119	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftDurationObserver:<init>	(Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;I)V
        //   122: astore 12
        //   124: aload_0
        //   125: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   128: getfield 123	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:group	Lrx/subscriptions/CompositeSubscription;
        //   131: aload 12
        //   133: invokevirtual 129	rx/subscriptions/CompositeSubscription:add	(Lrx/Subscription;)V
        //   136: aload 11
        //   138: aload 12
        //   140: invokevirtual 133	rx/Observable:unsafeSubscribe	(Lrx/Subscriber;)Lrx/Subscription;
        //   143: pop
        //   144: aload_0
        //   145: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   148: getfield 102	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:this$0	Lrx/internal/operators/OnSubscribeGroupJoin;
        //   151: getfield 137	rx/internal/operators/OnSubscribeGroupJoin:resultSelector	Lrx/functions/Func2;
        //   154: aload_1
        //   155: aload 10
        //   157: invokeinterface 141 3 0
        //   162: astore 14
        //   164: aload_0
        //   165: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   168: astore 15
        //   170: aload 15
        //   172: monitorenter
        //   173: new 26	java/util/ArrayList
        //   176: dup
        //   177: aload_0
        //   178: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   181: getfield 46	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:rightMap	Ljava/util/Map;
        //   184: invokeinterface 36 1 0
        //   189: invokespecial 39	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
        //   192: astore 16
        //   194: aload 15
        //   196: monitorexit
        //   197: aload_0
        //   198: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$LeftObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   201: getfield 145	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:subscriber	Lrx/Subscriber;
        //   204: aload 14
        //   206: invokevirtual 147	rx/Subscriber:onNext	(Ljava/lang/Object;)V
        //   209: aload 16
        //   211: invokeinterface 153 1 0
        //   216: astore 18
        //   218: aload 18
        //   220: invokeinterface 159 1 0
        //   225: ifeq +26 -> 251
        //   228: aload 4
        //   230: aload 18
        //   232: invokeinterface 163 1 0
        //   237: invokeinterface 166 2 0
        //   242: goto -24 -> 218
        //   245: astore_2
        //   246: aload_2
        //   247: aload_0
        //   248: invokestatic 172	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
        //   251: return
        //   252: astore 6
        //   254: aload 5
        //   256: monitorexit
        //   257: aload 6
        //   259: athrow
        //   260: astore 17
        //   262: aload 15
        //   264: monitorexit
        //   265: aload 17
        //   267: athrow
        //
        // Exception table:
        //   from	to	target	type
        //   0	23	245	java/lang/Throwable
        //   68	173	245	java/lang/Throwable
        //   197	218	245	java/lang/Throwable
        //   218	242	245	java/lang/Throwable
        //   257	260	245	java/lang/Throwable
        //   265	268	245	java/lang/Throwable
        //   23	68	252	finally
        //   254	257	252	finally
        //   173	197	260	finally
        //   262	265	260	finally
      }
    }

    final class RightDurationObserver extends Subscriber<D2>
    {
      final int id;
      boolean once = true;

      public RightDurationObserver(int arg2)
      {
        int i;
        this.id = i;
      }

      public void onCompleted()
      {
        if (this.once)
          this.once = false;
        synchronized (OnSubscribeGroupJoin.ResultManager.this)
        {
          OnSubscribeGroupJoin.ResultManager.this.rightMap.remove(Integer.valueOf(this.id));
          OnSubscribeGroupJoin.ResultManager.this.group.remove(this);
          return;
        }
      }

      public void onError(Throwable paramThrowable)
      {
        OnSubscribeGroupJoin.ResultManager.this.errorMain(paramThrowable);
      }

      public void onNext(D2 paramD2)
      {
        onCompleted();
      }
    }

    final class RightObserver extends Subscriber<T2>
    {
      RightObserver()
      {
      }

      public void onCompleted()
      {
        Object localObject3;
        ArrayList localArrayList;
        synchronized (OnSubscribeGroupJoin.ResultManager.this)
        {
          OnSubscribeGroupJoin.ResultManager.this.rightDone = true;
          boolean bool = OnSubscribeGroupJoin.ResultManager.this.leftDone;
          localObject3 = null;
          if (bool)
            localArrayList = new ArrayList(OnSubscribeGroupJoin.ResultManager.this.leftMap().values());
        }
        try
        {
          OnSubscribeGroupJoin.ResultManager.this.leftMap().clear();
          OnSubscribeGroupJoin.ResultManager.this.rightMap.clear();
          localObject3 = localArrayList;
          monitorexit;
          OnSubscribeGroupJoin.ResultManager.this.complete(localObject3);
          return;
          localObject1 = finally;
          monitorexit;
          throw localObject1;
        }
        finally
        {
        }
      }

      public void onError(Throwable paramThrowable)
      {
        OnSubscribeGroupJoin.ResultManager.this.errorAll(paramThrowable);
      }

      // ERROR //
      public void onNext(T2 paramT2)
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   4: astore_3
        //   5: aload_3
        //   6: monitorenter
        //   7: aload_0
        //   8: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   11: astore 5
        //   13: aload 5
        //   15: getfield 63	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:rightIds	I
        //   18: istore 6
        //   20: aload 5
        //   22: iload 6
        //   24: iconst_1
        //   25: iadd
        //   26: putfield 63	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:rightIds	I
        //   29: aload_0
        //   30: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   33: getfield 46	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:rightMap	Ljava/util/Map;
        //   36: iload 6
        //   38: invokestatic 69	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   41: aload_1
        //   42: invokeinterface 73 3 0
        //   47: pop
        //   48: aload_3
        //   49: monitorexit
        //   50: aload_0
        //   51: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   54: getfield 77	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:this$0	Lrx/internal/operators/OnSubscribeGroupJoin;
        //   57: getfield 83	rx/internal/operators/OnSubscribeGroupJoin:rightDuration	Lrx/functions/Func1;
        //   60: aload_1
        //   61: invokeinterface 89 2 0
        //   66: checkcast 91	rx/Observable
        //   69: astore 8
        //   71: new 93	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightDurationObserver
        //   74: dup
        //   75: aload_0
        //   76: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   79: iload 6
        //   81: invokespecial 96	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightDurationObserver:<init>	(Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;I)V
        //   84: astore 9
        //   86: aload_0
        //   87: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   90: getfield 100	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:group	Lrx/subscriptions/CompositeSubscription;
        //   93: aload 9
        //   95: invokevirtual 106	rx/subscriptions/CompositeSubscription:add	(Lrx/Subscription;)V
        //   98: aload 8
        //   100: aload 9
        //   102: invokevirtual 110	rx/Observable:unsafeSubscribe	(Lrx/Subscriber;)Lrx/Subscription;
        //   105: pop
        //   106: aload_0
        //   107: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   110: astore 11
        //   112: aload 11
        //   114: monitorenter
        //   115: new 26	java/util/ArrayList
        //   118: dup
        //   119: aload_0
        //   120: getfield 11	rx/internal/operators/OnSubscribeGroupJoin$ResultManager$RightObserver:this$1	Lrx/internal/operators/OnSubscribeGroupJoin$ResultManager;
        //   123: invokevirtual 30	rx/internal/operators/OnSubscribeGroupJoin$ResultManager:leftMap	()Ljava/util/Map;
        //   126: invokeinterface 36 1 0
        //   131: invokespecial 39	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
        //   134: astore 12
        //   136: aload 11
        //   138: monitorexit
        //   139: aload 12
        //   141: invokeinterface 116 1 0
        //   146: astore 14
        //   148: aload 14
        //   150: invokeinterface 122 1 0
        //   155: ifeq +28 -> 183
        //   158: aload 14
        //   160: invokeinterface 126 1 0
        //   165: checkcast 128	rx/Observer
        //   168: aload_1
        //   169: invokeinterface 130 2 0
        //   174: goto -26 -> 148
        //   177: astore_2
        //   178: aload_2
        //   179: aload_0
        //   180: invokestatic 136	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
        //   183: return
        //   184: astore 4
        //   186: aload_3
        //   187: monitorexit
        //   188: aload 4
        //   190: athrow
        //   191: astore 13
        //   193: aload 11
        //   195: monitorexit
        //   196: aload 13
        //   198: athrow
        //
        // Exception table:
        //   from	to	target	type
        //   0	7	177	java/lang/Throwable
        //   50	115	177	java/lang/Throwable
        //   139	148	177	java/lang/Throwable
        //   148	174	177	java/lang/Throwable
        //   188	191	177	java/lang/Throwable
        //   196	199	177	java/lang/Throwable
        //   7	50	184	finally
        //   186	188	184	finally
        //   115	139	191	finally
        //   193	196	191	finally
      }
    }
  }

  static final class WindowObservableFunc<T>
    implements Observable.OnSubscribe<T>
  {
    final RefCountSubscription refCount;
    final Observable<T> underlying;

    public WindowObservableFunc(Observable<T> paramObservable, RefCountSubscription paramRefCountSubscription)
    {
      this.refCount = paramRefCountSubscription;
      this.underlying = paramObservable;
    }

    public void call(Subscriber<? super T> paramSubscriber)
    {
      Subscription localSubscription = this.refCount.get();
      WindowSubscriber localWindowSubscriber = new WindowSubscriber(paramSubscriber, localSubscription);
      localWindowSubscriber.add(localSubscription);
      this.underlying.unsafeSubscribe(localWindowSubscriber);
    }

    final class WindowSubscriber extends Subscriber<T>
    {
      private final Subscription ref;
      final Subscriber<? super T> subscriber;

      public WindowSubscriber(Subscription arg2)
      {
        super();
        this.subscriber = localSubscriber;
        Object localObject;
        this.ref = localObject;
      }

      public void onCompleted()
      {
        this.subscriber.onCompleted();
        this.ref.unsubscribe();
      }

      public void onError(Throwable paramThrowable)
      {
        this.subscriber.onError(paramThrowable);
        this.ref.unsubscribe();
      }

      public void onNext(T paramT)
      {
        this.subscriber.onNext(paramT);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeGroupJoin
 * JD-Core Version:    0.6.0
 */