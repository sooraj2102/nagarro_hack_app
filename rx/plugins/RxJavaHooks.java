package rx.plugins;

import java.io.PrintStream;
import java.util.concurrent.ScheduledExecutorService;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.Completable.Operator;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Scheduler;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.operators.OnSubscribeOnAssembly;
import rx.internal.operators.OnSubscribeOnAssemblyCompletable;
import rx.internal.operators.OnSubscribeOnAssemblySingle;

@Experimental
public final class RxJavaHooks
{
  static volatile boolean lockdown;
  static volatile Func1<Completable.OnSubscribe, Completable.OnSubscribe> onCompletableCreate;
  static volatile Func1<Completable.Operator, Completable.Operator> onCompletableLift;
  static volatile Func2<Completable, Completable.OnSubscribe, Completable.OnSubscribe> onCompletableStart;
  static volatile Func1<Throwable, Throwable> onCompletableSubscribeError;
  static volatile Func1<Scheduler, Scheduler> onComputationScheduler;
  static volatile Action1<Throwable> onError;
  static volatile Func0<? extends ScheduledExecutorService> onGenericScheduledExecutorService;
  static volatile Func1<Scheduler, Scheduler> onIOScheduler;
  static volatile Func1<Scheduler, Scheduler> onNewThreadScheduler;
  static volatile Func1<Observable.OnSubscribe, Observable.OnSubscribe> onObservableCreate;
  static volatile Func1<Observable.Operator, Observable.Operator> onObservableLift;
  static volatile Func1<Subscription, Subscription> onObservableReturn;
  static volatile Func2<Observable, Observable.OnSubscribe, Observable.OnSubscribe> onObservableStart;
  static volatile Func1<Throwable, Throwable> onObservableSubscribeError;
  static volatile Func1<Action0, Action0> onScheduleAction;
  static volatile Func1<Single.OnSubscribe, Single.OnSubscribe> onSingleCreate;
  static volatile Func1<Observable.Operator, Observable.Operator> onSingleLift;
  static volatile Func1<Subscription, Subscription> onSingleReturn;
  static volatile Func2<Single, Observable.OnSubscribe, Observable.OnSubscribe> onSingleStart;
  static volatile Func1<Throwable, Throwable> onSingleSubscribeError;

  static
  {
    init();
  }

  private RxJavaHooks()
  {
    throw new IllegalStateException("No instances!");
  }

  public static void clear()
  {
    if (lockdown)
      return;
    onError = null;
    onObservableCreate = null;
    onObservableStart = null;
    onObservableReturn = null;
    onObservableSubscribeError = null;
    onObservableLift = null;
    onSingleCreate = null;
    onSingleStart = null;
    onSingleReturn = null;
    onSingleSubscribeError = null;
    onSingleLift = null;
    onCompletableCreate = null;
    onCompletableStart = null;
    onCompletableSubscribeError = null;
    onCompletableLift = null;
    onComputationScheduler = null;
    onIOScheduler = null;
    onNewThreadScheduler = null;
    onScheduleAction = null;
    onGenericScheduledExecutorService = null;
  }

  public static void clearAssemblyTracking()
  {
    if (lockdown)
      return;
    onObservableCreate = null;
    onSingleCreate = null;
    onCompletableCreate = null;
  }

  public static void enableAssemblyTracking()
  {
    if (lockdown)
      return;
    onObservableCreate = new Func1()
    {
      public Observable.OnSubscribe call(Observable.OnSubscribe paramOnSubscribe)
      {
        return new OnSubscribeOnAssembly(paramOnSubscribe);
      }
    };
    onSingleCreate = new Func1()
    {
      public Single.OnSubscribe call(Single.OnSubscribe paramOnSubscribe)
      {
        return new OnSubscribeOnAssemblySingle(paramOnSubscribe);
      }
    };
    onCompletableCreate = new Func1()
    {
      public Completable.OnSubscribe call(Completable.OnSubscribe paramOnSubscribe)
      {
        return new OnSubscribeOnAssemblyCompletable(paramOnSubscribe);
      }
    };
  }

  public static Func1<Completable.OnSubscribe, Completable.OnSubscribe> getOnCompletableCreate()
  {
    return onCompletableCreate;
  }

  public static Func1<Completable.Operator, Completable.Operator> getOnCompletableLift()
  {
    return onCompletableLift;
  }

  public static Func2<Completable, Completable.OnSubscribe, Completable.OnSubscribe> getOnCompletableStart()
  {
    return onCompletableStart;
  }

  public static Func1<Throwable, Throwable> getOnCompletableSubscribeError()
  {
    return onCompletableSubscribeError;
  }

  public static Func1<Scheduler, Scheduler> getOnComputationScheduler()
  {
    return onComputationScheduler;
  }

  public static Action1<Throwable> getOnError()
  {
    return onError;
  }

  public static Func0<? extends ScheduledExecutorService> getOnGenericScheduledExecutorService()
  {
    return onGenericScheduledExecutorService;
  }

  public static Func1<Scheduler, Scheduler> getOnIOScheduler()
  {
    return onIOScheduler;
  }

  public static Func1<Scheduler, Scheduler> getOnNewThreadScheduler()
  {
    return onNewThreadScheduler;
  }

  public static Func1<Observable.OnSubscribe, Observable.OnSubscribe> getOnObservableCreate()
  {
    return onObservableCreate;
  }

  public static Func1<Observable.Operator, Observable.Operator> getOnObservableLift()
  {
    return onObservableLift;
  }

  public static Func1<Subscription, Subscription> getOnObservableReturn()
  {
    return onObservableReturn;
  }

  public static Func2<Observable, Observable.OnSubscribe, Observable.OnSubscribe> getOnObservableStart()
  {
    return onObservableStart;
  }

  public static Func1<Throwable, Throwable> getOnObservableSubscribeError()
  {
    return onObservableSubscribeError;
  }

  public static Func1<Action0, Action0> getOnScheduleAction()
  {
    return onScheduleAction;
  }

  public static Func1<Single.OnSubscribe, Single.OnSubscribe> getOnSingleCreate()
  {
    return onSingleCreate;
  }

  public static Func1<Observable.Operator, Observable.Operator> getOnSingleLift()
  {
    return onSingleLift;
  }

  public static Func1<Subscription, Subscription> getOnSingleReturn()
  {
    return onSingleReturn;
  }

  public static Func2<Single, Observable.OnSubscribe, Observable.OnSubscribe> getOnSingleStart()
  {
    return onSingleStart;
  }

  public static Func1<Throwable, Throwable> getOnSingleSubscribeError()
  {
    return onSingleSubscribeError;
  }

  static void init()
  {
    onError = new Action1()
    {
      public void call(Throwable paramThrowable)
      {
        RxJavaPlugins.getInstance().getErrorHandler().handleError(paramThrowable);
      }
    };
    onObservableStart = new Func2()
    {
      public Observable.OnSubscribe call(Observable paramObservable, Observable.OnSubscribe paramOnSubscribe)
      {
        return RxJavaPlugins.getInstance().getObservableExecutionHook().onSubscribeStart(paramObservable, paramOnSubscribe);
      }
    };
    onObservableReturn = new Func1()
    {
      public Subscription call(Subscription paramSubscription)
      {
        return RxJavaPlugins.getInstance().getObservableExecutionHook().onSubscribeReturn(paramSubscription);
      }
    };
    onSingleStart = new Func2()
    {
      public Observable.OnSubscribe call(Single paramSingle, Observable.OnSubscribe paramOnSubscribe)
      {
        return RxJavaPlugins.getInstance().getSingleExecutionHook().onSubscribeStart(paramSingle, paramOnSubscribe);
      }
    };
    onSingleReturn = new Func1()
    {
      public Subscription call(Subscription paramSubscription)
      {
        return RxJavaPlugins.getInstance().getSingleExecutionHook().onSubscribeReturn(paramSubscription);
      }
    };
    onCompletableStart = new Func2()
    {
      public Completable.OnSubscribe call(Completable paramCompletable, Completable.OnSubscribe paramOnSubscribe)
      {
        return RxJavaPlugins.getInstance().getCompletableExecutionHook().onSubscribeStart(paramCompletable, paramOnSubscribe);
      }
    };
    onScheduleAction = new Func1()
    {
      public Action0 call(Action0 paramAction0)
      {
        return RxJavaPlugins.getInstance().getSchedulersHook().onSchedule(paramAction0);
      }
    };
    onObservableSubscribeError = new Func1()
    {
      public Throwable call(Throwable paramThrowable)
      {
        return RxJavaPlugins.getInstance().getObservableExecutionHook().onSubscribeError(paramThrowable);
      }
    };
    onObservableLift = new Func1()
    {
      public Observable.Operator call(Observable.Operator paramOperator)
      {
        return RxJavaPlugins.getInstance().getObservableExecutionHook().onLift(paramOperator);
      }
    };
    onSingleSubscribeError = new Func1()
    {
      public Throwable call(Throwable paramThrowable)
      {
        return RxJavaPlugins.getInstance().getSingleExecutionHook().onSubscribeError(paramThrowable);
      }
    };
    onSingleLift = new Func1()
    {
      public Observable.Operator call(Observable.Operator paramOperator)
      {
        return RxJavaPlugins.getInstance().getSingleExecutionHook().onLift(paramOperator);
      }
    };
    onCompletableSubscribeError = new Func1()
    {
      public Throwable call(Throwable paramThrowable)
      {
        return RxJavaPlugins.getInstance().getCompletableExecutionHook().onSubscribeError(paramThrowable);
      }
    };
    onCompletableLift = new Func1()
    {
      public Completable.Operator call(Completable.Operator paramOperator)
      {
        return RxJavaPlugins.getInstance().getCompletableExecutionHook().onLift(paramOperator);
      }
    };
    initCreate();
  }

  static void initCreate()
  {
    onObservableCreate = new Func1()
    {
      public Observable.OnSubscribe call(Observable.OnSubscribe paramOnSubscribe)
      {
        return RxJavaPlugins.getInstance().getObservableExecutionHook().onCreate(paramOnSubscribe);
      }
    };
    onSingleCreate = new Func1()
    {
      public Single.OnSubscribe call(Single.OnSubscribe paramOnSubscribe)
      {
        return RxJavaPlugins.getInstance().getSingleExecutionHook().onCreate(paramOnSubscribe);
      }
    };
    onCompletableCreate = new Func1()
    {
      public Completable.OnSubscribe call(Completable.OnSubscribe paramOnSubscribe)
      {
        return RxJavaPlugins.getInstance().getCompletableExecutionHook().onCreate(paramOnSubscribe);
      }
    };
  }

  public static boolean isLockdown()
  {
    return lockdown;
  }

  public static void lockdown()
  {
    lockdown = true;
  }

  public static Throwable onCompletableError(Throwable paramThrowable)
  {
    Func1 localFunc1 = onCompletableSubscribeError;
    if (localFunc1 != null)
      return (Throwable)localFunc1.call(paramThrowable);
    return paramThrowable;
  }

  public static <T, R> Completable.Operator onCompletableLift(Completable.Operator paramOperator)
  {
    Func1 localFunc1 = onCompletableLift;
    if (localFunc1 != null)
      return (Completable.Operator)localFunc1.call(paramOperator);
    return paramOperator;
  }

  public static <T> Completable.OnSubscribe onCompletableStart(Completable paramCompletable, Completable.OnSubscribe paramOnSubscribe)
  {
    Func2 localFunc2 = onCompletableStart;
    if (localFunc2 != null)
      return (Completable.OnSubscribe)localFunc2.call(paramCompletable, paramOnSubscribe);
    return paramOnSubscribe;
  }

  public static Scheduler onComputationScheduler(Scheduler paramScheduler)
  {
    Func1 localFunc1 = onComputationScheduler;
    if (localFunc1 != null)
      return (Scheduler)localFunc1.call(paramScheduler);
    return paramScheduler;
  }

  public static Completable.OnSubscribe onCreate(Completable.OnSubscribe paramOnSubscribe)
  {
    Func1 localFunc1 = onCompletableCreate;
    if (localFunc1 != null)
      return (Completable.OnSubscribe)localFunc1.call(paramOnSubscribe);
    return paramOnSubscribe;
  }

  public static <T> Observable.OnSubscribe<T> onCreate(Observable.OnSubscribe<T> paramOnSubscribe)
  {
    Func1 localFunc1 = onObservableCreate;
    if (localFunc1 != null)
      return (Observable.OnSubscribe)localFunc1.call(paramOnSubscribe);
    return paramOnSubscribe;
  }

  public static <T> Single.OnSubscribe<T> onCreate(Single.OnSubscribe<T> paramOnSubscribe)
  {
    Func1 localFunc1 = onSingleCreate;
    if (localFunc1 != null)
      return (Single.OnSubscribe)localFunc1.call(paramOnSubscribe);
    return paramOnSubscribe;
  }

  public static void onError(Throwable paramThrowable)
  {
    Action1 localAction1 = onError;
    if (localAction1 != null)
      try
      {
        localAction1.call(paramThrowable);
        return;
      }
      catch (Throwable localThrowable)
      {
        System.err.println("The onError handler threw an Exception. It shouldn't. => " + localThrowable.getMessage());
        localThrowable.printStackTrace();
        signalUncaught(localThrowable);
      }
    signalUncaught(paramThrowable);
  }

  public static Scheduler onIOScheduler(Scheduler paramScheduler)
  {
    Func1 localFunc1 = onIOScheduler;
    if (localFunc1 != null)
      return (Scheduler)localFunc1.call(paramScheduler);
    return paramScheduler;
  }

  public static Scheduler onNewThreadScheduler(Scheduler paramScheduler)
  {
    Func1 localFunc1 = onNewThreadScheduler;
    if (localFunc1 != null)
      return (Scheduler)localFunc1.call(paramScheduler);
    return paramScheduler;
  }

  public static Throwable onObservableError(Throwable paramThrowable)
  {
    Func1 localFunc1 = onObservableSubscribeError;
    if (localFunc1 != null)
      return (Throwable)localFunc1.call(paramThrowable);
    return paramThrowable;
  }

  public static <T, R> Observable.Operator<R, T> onObservableLift(Observable.Operator<R, T> paramOperator)
  {
    Func1 localFunc1 = onObservableLift;
    if (localFunc1 != null)
      return (Observable.Operator)localFunc1.call(paramOperator);
    return paramOperator;
  }

  public static Subscription onObservableReturn(Subscription paramSubscription)
  {
    Func1 localFunc1 = onObservableReturn;
    if (localFunc1 != null)
      return (Subscription)localFunc1.call(paramSubscription);
    return paramSubscription;
  }

  public static <T> Observable.OnSubscribe<T> onObservableStart(Observable<T> paramObservable, Observable.OnSubscribe<T> paramOnSubscribe)
  {
    Func2 localFunc2 = onObservableStart;
    if (localFunc2 != null)
      return (Observable.OnSubscribe)localFunc2.call(paramObservable, paramOnSubscribe);
    return paramOnSubscribe;
  }

  public static Action0 onScheduledAction(Action0 paramAction0)
  {
    Func1 localFunc1 = onScheduleAction;
    if (localFunc1 != null)
      return (Action0)localFunc1.call(paramAction0);
    return paramAction0;
  }

  public static Throwable onSingleError(Throwable paramThrowable)
  {
    Func1 localFunc1 = onSingleSubscribeError;
    if (localFunc1 != null)
      return (Throwable)localFunc1.call(paramThrowable);
    return paramThrowable;
  }

  public static <T, R> Observable.Operator<R, T> onSingleLift(Observable.Operator<R, T> paramOperator)
  {
    Func1 localFunc1 = onSingleLift;
    if (localFunc1 != null)
      return (Observable.Operator)localFunc1.call(paramOperator);
    return paramOperator;
  }

  public static Subscription onSingleReturn(Subscription paramSubscription)
  {
    Func1 localFunc1 = onSingleReturn;
    if (localFunc1 != null)
      return (Subscription)localFunc1.call(paramSubscription);
    return paramSubscription;
  }

  public static <T> Observable.OnSubscribe<T> onSingleStart(Single<T> paramSingle, Observable.OnSubscribe<T> paramOnSubscribe)
  {
    Func2 localFunc2 = onSingleStart;
    if (localFunc2 != null)
      return (Observable.OnSubscribe)localFunc2.call(paramSingle, paramOnSubscribe);
    return paramOnSubscribe;
  }

  public static void reset()
  {
    if (lockdown)
      return;
    init();
    onComputationScheduler = null;
    onIOScheduler = null;
    onNewThreadScheduler = null;
    onGenericScheduledExecutorService = null;
  }

  public static void resetAssemblyTracking()
  {
    if (lockdown)
      return;
    initCreate();
  }

  public static void setOnCompletableCreate(Func1<Completable.OnSubscribe, Completable.OnSubscribe> paramFunc1)
  {
    if (lockdown)
      return;
    onCompletableCreate = paramFunc1;
  }

  public static void setOnCompletableLift(Func1<Completable.Operator, Completable.Operator> paramFunc1)
  {
    if (lockdown)
      return;
    onCompletableLift = paramFunc1;
  }

  public static void setOnCompletableStart(Func2<Completable, Completable.OnSubscribe, Completable.OnSubscribe> paramFunc2)
  {
    if (lockdown)
      return;
    onCompletableStart = paramFunc2;
  }

  public static void setOnCompletableSubscribeError(Func1<Throwable, Throwable> paramFunc1)
  {
    if (lockdown)
      return;
    onCompletableSubscribeError = paramFunc1;
  }

  public static void setOnComputationScheduler(Func1<Scheduler, Scheduler> paramFunc1)
  {
    if (lockdown)
      return;
    onComputationScheduler = paramFunc1;
  }

  public static void setOnError(Action1<Throwable> paramAction1)
  {
    if (lockdown)
      return;
    onError = paramAction1;
  }

  public static void setOnGenericScheduledExecutorService(Func0<? extends ScheduledExecutorService> paramFunc0)
  {
    if (lockdown)
      return;
    onGenericScheduledExecutorService = paramFunc0;
  }

  public static void setOnIOScheduler(Func1<Scheduler, Scheduler> paramFunc1)
  {
    if (lockdown)
      return;
    onIOScheduler = paramFunc1;
  }

  public static void setOnNewThreadScheduler(Func1<Scheduler, Scheduler> paramFunc1)
  {
    if (lockdown)
      return;
    onNewThreadScheduler = paramFunc1;
  }

  public static void setOnObservableCreate(Func1<Observable.OnSubscribe, Observable.OnSubscribe> paramFunc1)
  {
    if (lockdown)
      return;
    onObservableCreate = paramFunc1;
  }

  public static void setOnObservableLift(Func1<Observable.Operator, Observable.Operator> paramFunc1)
  {
    if (lockdown)
      return;
    onObservableLift = paramFunc1;
  }

  public static void setOnObservableReturn(Func1<Subscription, Subscription> paramFunc1)
  {
    if (lockdown)
      return;
    onObservableReturn = paramFunc1;
  }

  public static void setOnObservableStart(Func2<Observable, Observable.OnSubscribe, Observable.OnSubscribe> paramFunc2)
  {
    if (lockdown)
      return;
    onObservableStart = paramFunc2;
  }

  public static void setOnObservableSubscribeError(Func1<Throwable, Throwable> paramFunc1)
  {
    if (lockdown)
      return;
    onObservableSubscribeError = paramFunc1;
  }

  public static void setOnScheduleAction(Func1<Action0, Action0> paramFunc1)
  {
    if (lockdown)
      return;
    onScheduleAction = paramFunc1;
  }

  public static void setOnSingleCreate(Func1<Single.OnSubscribe, Single.OnSubscribe> paramFunc1)
  {
    if (lockdown)
      return;
    onSingleCreate = paramFunc1;
  }

  public static void setOnSingleLift(Func1<Observable.Operator, Observable.Operator> paramFunc1)
  {
    if (lockdown)
      return;
    onSingleLift = paramFunc1;
  }

  public static void setOnSingleReturn(Func1<Subscription, Subscription> paramFunc1)
  {
    if (lockdown)
      return;
    onSingleReturn = paramFunc1;
  }

  public static void setOnSingleStart(Func2<Single, Observable.OnSubscribe, Observable.OnSubscribe> paramFunc2)
  {
    if (lockdown)
      return;
    onSingleStart = paramFunc2;
  }

  public static void setOnSingleSubscribeError(Func1<Throwable, Throwable> paramFunc1)
  {
    if (lockdown)
      return;
    onSingleSubscribeError = paramFunc1;
  }

  static void signalUncaught(Throwable paramThrowable)
  {
    Thread localThread = Thread.currentThread();
    localThread.getUncaughtExceptionHandler().uncaughtException(localThread, paramThrowable);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.plugins.RxJavaHooks
 * JD-Core Version:    0.6.0
 */