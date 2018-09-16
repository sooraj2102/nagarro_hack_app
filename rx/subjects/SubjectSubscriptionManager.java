package rx.subjects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.internal.operators.NotificationLite;
import rx.subscriptions.Subscriptions;

final class SubjectSubscriptionManager<T> extends AtomicReference<State<T>>
  implements Observable.OnSubscribe<T>
{
  private static final long serialVersionUID = 6035251036011671568L;
  boolean active = true;
  volatile Object latest;
  public final NotificationLite<T> nl = NotificationLite.instance();
  Action1<SubjectObserver<T>> onAdded = Actions.empty();
  Action1<SubjectObserver<T>> onStart = Actions.empty();
  Action1<SubjectObserver<T>> onTerminated = Actions.empty();

  public SubjectSubscriptionManager()
  {
    super(State.EMPTY);
  }

  boolean add(SubjectObserver<T> paramSubjectObserver)
  {
    State localState;
    do
    {
      localState = (State)get();
      if (!localState.terminated)
        continue;
      this.onTerminated.call(paramSubjectObserver);
      return false;
    }
    while (!compareAndSet(localState, localState.add(paramSubjectObserver)));
    this.onAdded.call(paramSubjectObserver);
    return true;
  }

  void addUnsubscriber(Subscriber<? super T> paramSubscriber, SubjectObserver<T> paramSubjectObserver)
  {
    paramSubscriber.add(Subscriptions.create(new Action0(paramSubjectObserver)
    {
      public void call()
      {
        SubjectSubscriptionManager.this.remove(this.val$bo);
      }
    }));
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    SubjectObserver localSubjectObserver = new SubjectObserver(paramSubscriber);
    addUnsubscriber(paramSubscriber, localSubjectObserver);
    this.onStart.call(localSubjectObserver);
    if ((!paramSubscriber.isUnsubscribed()) && (add(localSubjectObserver)) && (paramSubscriber.isUnsubscribed()))
      remove(localSubjectObserver);
  }

  Object getLatest()
  {
    return this.latest;
  }

  SubjectObserver<T>[] next(Object paramObject)
  {
    setLatest(paramObject);
    return ((State)get()).observers;
  }

  SubjectObserver<T>[] observers()
  {
    return ((State)get()).observers;
  }

  void remove(SubjectObserver<T> paramSubjectObserver)
  {
    State localState1;
    State localState2;
    do
    {
      localState1 = (State)get();
      if (localState1.terminated);
      do
      {
        return;
        localState2 = localState1.remove(paramSubjectObserver);
      }
      while (localState2 == localState1);
    }
    while (!compareAndSet(localState1, localState2));
  }

  void setLatest(Object paramObject)
  {
    this.latest = paramObject;
  }

  SubjectObserver<T>[] terminate(Object paramObject)
  {
    setLatest(paramObject);
    this.active = false;
    if (((State)get()).terminated)
      return State.NO_OBSERVERS;
    return ((State)getAndSet(State.TERMINATED)).observers;
  }

  protected static final class State<T>
  {
    static final State EMPTY;
    static final SubjectSubscriptionManager.SubjectObserver[] NO_OBSERVERS = new SubjectSubscriptionManager.SubjectObserver[0];
    static final State TERMINATED = new State(true, NO_OBSERVERS);
    final SubjectSubscriptionManager.SubjectObserver[] observers;
    final boolean terminated;

    static
    {
      EMPTY = new State(false, NO_OBSERVERS);
    }

    public State(boolean paramBoolean, SubjectSubscriptionManager.SubjectObserver[] paramArrayOfSubjectObserver)
    {
      this.terminated = paramBoolean;
      this.observers = paramArrayOfSubjectObserver;
    }

    public State add(SubjectSubscriptionManager.SubjectObserver paramSubjectObserver)
    {
      int i = this.observers.length;
      SubjectSubscriptionManager.SubjectObserver[] arrayOfSubjectObserver = new SubjectSubscriptionManager.SubjectObserver[i + 1];
      System.arraycopy(this.observers, 0, arrayOfSubjectObserver, 0, i);
      arrayOfSubjectObserver[i] = paramSubjectObserver;
      return new State(this.terminated, arrayOfSubjectObserver);
    }

    public State remove(SubjectSubscriptionManager.SubjectObserver paramSubjectObserver)
    {
      SubjectSubscriptionManager.SubjectObserver[] arrayOfSubjectObserver1 = this.observers;
      int i = arrayOfSubjectObserver1.length;
      if ((i == 1) && (arrayOfSubjectObserver1[0] == paramSubjectObserver))
        this = EMPTY;
      Object localObject;
      int j;
      int k;
      int m;
      while (true)
      {
        return this;
        if (i == 0)
          continue;
        localObject = new SubjectSubscriptionManager.SubjectObserver[i - 1];
        j = 0;
        k = 0;
        if (j >= i)
          break;
        SubjectSubscriptionManager.SubjectObserver localSubjectObserver = arrayOfSubjectObserver1[j];
        if (localSubjectObserver == paramSubjectObserver)
          break label146;
        if (k == i - 1)
          continue;
        m = k + 1;
        localObject[k] = localSubjectObserver;
      }
      while (true)
      {
        j++;
        k = m;
        break;
        if (k == 0)
          return EMPTY;
        if (k < i - 1)
        {
          SubjectSubscriptionManager.SubjectObserver[] arrayOfSubjectObserver2 = new SubjectSubscriptionManager.SubjectObserver[k];
          System.arraycopy(localObject, 0, arrayOfSubjectObserver2, 0, k);
          localObject = arrayOfSubjectObserver2;
        }
        return new State(this.terminated, localObject);
        label146: m = k;
      }
    }
  }

  protected static final class SubjectObserver<T>
    implements Observer<T>
  {
    final Subscriber<? super T> actual;
    volatile boolean caughtUp;
    boolean emitting;
    boolean fastPath;
    boolean first = true;
    private volatile Object index;
    List<Object> queue;

    public SubjectObserver(Subscriber<? super T> paramSubscriber)
    {
      this.actual = paramSubscriber;
    }

    void accept(Object paramObject, NotificationLite<T> paramNotificationLite)
    {
      if (paramObject != null)
        paramNotificationLite.accept(this.actual, paramObject);
    }

    void emitFirst(Object paramObject, NotificationLite<T> paramNotificationLite)
    {
      monitorenter;
      try
      {
        if ((!this.first) || (this.emitting))
          return;
        this.first = false;
        boolean bool = false;
        if (paramObject != null)
          bool = true;
        this.emitting = bool;
        monitorexit;
        if (paramObject != null)
        {
          emitLoop(null, paramObject, paramNotificationLite);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
    }

    // ERROR //
    void emitLoop(List<Object> paramList, Object paramObject, NotificationLite<T> paramNotificationLite)
    {
      // Byte code:
      //   0: iconst_1
      //   1: istore 4
      //   3: iconst_0
      //   4: istore 5
      //   6: aload_1
      //   7: ifnull +62 -> 69
      //   10: aload_1
      //   11: invokeinterface 49 1 0
      //   16: astore 10
      //   18: aload 10
      //   20: invokeinterface 55 1 0
      //   25: istore 11
      //   27: iconst_0
      //   28: istore 5
      //   30: iload 11
      //   32: ifeq +37 -> 69
      //   35: aload_0
      //   36: aload 10
      //   38: invokeinterface 59 1 0
      //   43: aload_3
      //   44: invokevirtual 61	rx/subjects/SubjectSubscriptionManager$SubjectObserver:accept	(Ljava/lang/Object;Lrx/internal/operators/NotificationLite;)V
      //   47: goto -29 -> 18
      //   50: astore 7
      //   52: iload 5
      //   54: ifne +12 -> 66
      //   57: aload_0
      //   58: monitorenter
      //   59: aload_0
      //   60: iconst_0
      //   61: putfield 39	rx/subjects/SubjectSubscriptionManager$SubjectObserver:emitting	Z
      //   64: aload_0
      //   65: monitorexit
      //   66: aload 7
      //   68: athrow
      //   69: iload 4
      //   71: ifeq +12 -> 83
      //   74: iconst_0
      //   75: istore 4
      //   77: aload_0
      //   78: aload_2
      //   79: aload_3
      //   80: invokevirtual 61	rx/subjects/SubjectSubscriptionManager$SubjectObserver:accept	(Ljava/lang/Object;Lrx/internal/operators/NotificationLite;)V
      //   83: aload_0
      //   84: monitorenter
      //   85: aload_0
      //   86: getfield 63	rx/subjects/SubjectSubscriptionManager$SubjectObserver:queue	Ljava/util/List;
      //   89: astore_1
      //   90: aload_0
      //   91: aconst_null
      //   92: putfield 63	rx/subjects/SubjectSubscriptionManager$SubjectObserver:queue	Ljava/util/List;
      //   95: aload_1
      //   96: ifnonnull +28 -> 124
      //   99: aload_0
      //   100: iconst_0
      //   101: putfield 39	rx/subjects/SubjectSubscriptionManager$SubjectObserver:emitting	Z
      //   104: iconst_1
      //   105: istore 5
      //   107: aload_0
      //   108: monitorexit
      //   109: iload 5
      //   111: ifne +12 -> 123
      //   114: aload_0
      //   115: monitorenter
      //   116: aload_0
      //   117: iconst_0
      //   118: putfield 39	rx/subjects/SubjectSubscriptionManager$SubjectObserver:emitting	Z
      //   121: aload_0
      //   122: monitorexit
      //   123: return
      //   124: aload_0
      //   125: monitorexit
      //   126: goto -120 -> 6
      //   129: astore 6
      //   131: aload_0
      //   132: monitorexit
      //   133: aload 6
      //   135: athrow
      //   136: astore 9
      //   138: aload_0
      //   139: monitorexit
      //   140: aload 9
      //   142: athrow
      //   143: astore 8
      //   145: aload_0
      //   146: monitorexit
      //   147: aload 8
      //   149: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   10	18	50	finally
      //   18	27	50	finally
      //   35	47	50	finally
      //   77	83	50	finally
      //   83	85	50	finally
      //   133	136	50	finally
      //   85	95	129	finally
      //   99	104	129	finally
      //   107	109	129	finally
      //   124	126	129	finally
      //   131	133	129	finally
      //   116	123	136	finally
      //   138	140	136	finally
      //   59	66	143	finally
      //   145	147	143	finally
    }

    void emitNext(Object paramObject, NotificationLite<T> paramNotificationLite)
    {
      if (!this.fastPath)
        monitorenter;
      try
      {
        this.first = false;
        if (this.emitting)
        {
          if (this.queue == null)
            this.queue = new ArrayList();
          this.queue.add(paramObject);
          return;
        }
        monitorexit;
        this.fastPath = true;
        paramNotificationLite.accept(this.actual, paramObject);
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    Observer<? super T> getActual()
    {
      return this.actual;
    }

    public <I> I index()
    {
      return this.index;
    }

    public void index(Object paramObject)
    {
      this.index = paramObject;
    }

    public void onCompleted()
    {
      this.actual.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      this.actual.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      this.actual.onNext(paramT);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.subjects.SubjectSubscriptionManager
 * JD-Core Version:    0.6.0
 */