package rx.internal.schedulers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.internal.util.PlatformDependent;
import rx.internal.util.RxThreadFactory;
import rx.internal.util.SubscriptionList;
import rx.internal.util.SuppressAnimalSniffer;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class NewThreadWorker extends Scheduler.Worker
  implements Subscription
{
  private static final ConcurrentHashMap<ScheduledThreadPoolExecutor, ScheduledThreadPoolExecutor> EXECUTORS;
  private static final String FREQUENCY_KEY = "rx.scheduler.jdk6.purge-frequency-millis";
  private static final AtomicReference<ScheduledExecutorService> PURGE;
  private static final String PURGE_FORCE_KEY = "rx.scheduler.jdk6.purge-force";
  public static final int PURGE_FREQUENCY = 0;
  private static final String PURGE_THREAD_PREFIX = "RxSchedulerPurge-";
  private static final Object SET_REMOVE_ON_CANCEL_POLICY_METHOD_NOT_SUPPORTED = new Object();
  private static final boolean SHOULD_TRY_ENABLE_CANCEL_POLICY;
  private static volatile Object cachedSetRemoveOnCancelPolicyMethod;
  private final ScheduledExecutorService executor;
  volatile boolean isUnsubscribed;

  static
  {
    EXECUTORS = new ConcurrentHashMap();
    PURGE = new AtomicReference();
    PURGE_FREQUENCY = Integer.getInteger("rx.scheduler.jdk6.purge-frequency-millis", 1000).intValue();
    boolean bool1 = Boolean.getBoolean("rx.scheduler.jdk6.purge-force");
    int i = PlatformDependent.getAndroidApiVersion();
    if ((!bool1) && ((i == 0) || (i >= 21)));
    for (boolean bool2 = true; ; bool2 = false)
    {
      SHOULD_TRY_ENABLE_CANCEL_POLICY = bool2;
      return;
    }
  }

  public NewThreadWorker(ThreadFactory paramThreadFactory)
  {
    ScheduledExecutorService localScheduledExecutorService = Executors.newScheduledThreadPool(1, paramThreadFactory);
    if ((!tryEnableCancelPolicy(localScheduledExecutorService)) && ((localScheduledExecutorService instanceof ScheduledThreadPoolExecutor)))
      registerExecutor((ScheduledThreadPoolExecutor)localScheduledExecutorService);
    this.executor = localScheduledExecutorService;
  }

  public static void deregisterExecutor(ScheduledExecutorService paramScheduledExecutorService)
  {
    EXECUTORS.remove(paramScheduledExecutorService);
  }

  static Method findSetRemoveOnCancelPolicyMethod(ScheduledExecutorService paramScheduledExecutorService)
  {
    for (Method localMethod : paramScheduledExecutorService.getClass().getMethods())
    {
      if (!localMethod.getName().equals("setRemoveOnCancelPolicy"))
        continue;
      Class[] arrayOfClass = localMethod.getParameterTypes();
      if ((arrayOfClass.length == 1) && (arrayOfClass[0] == Boolean.TYPE))
        return localMethod;
    }
    return null;
  }

  @SuppressAnimalSniffer
  static void purgeExecutors()
  {
    while (true)
    {
      Iterator localIterator;
      try
      {
        localIterator = EXECUTORS.keySet().iterator();
        if (localIterator.hasNext())
        {
          ScheduledThreadPoolExecutor localScheduledThreadPoolExecutor = (ScheduledThreadPoolExecutor)localIterator.next();
          if (localScheduledThreadPoolExecutor.isShutdown())
            break label55;
          localScheduledThreadPoolExecutor.purge();
          continue;
        }
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        RxJavaHooks.onError(localThrowable);
      }
      return;
      label55: localIterator.remove();
    }
  }

  public static void registerExecutor(ScheduledThreadPoolExecutor paramScheduledThreadPoolExecutor)
  {
    while (true)
    {
      if ((ScheduledExecutorService)PURGE.get() != null);
      ScheduledExecutorService localScheduledExecutorService;
      while (true)
      {
        EXECUTORS.putIfAbsent(paramScheduledThreadPoolExecutor, paramScheduledThreadPoolExecutor);
        return;
        localScheduledExecutorService = Executors.newScheduledThreadPool(1, new RxThreadFactory("RxSchedulerPurge-"));
        if (!PURGE.compareAndSet(null, localScheduledExecutorService))
          break;
        localScheduledExecutorService.scheduleAtFixedRate(new Runnable()
        {
          public void run()
          {
            NewThreadWorker.purgeExecutors();
          }
        }
        , PURGE_FREQUENCY, PURGE_FREQUENCY, TimeUnit.MILLISECONDS);
      }
      localScheduledExecutorService.shutdownNow();
    }
  }

  public static boolean tryEnableCancelPolicy(ScheduledExecutorService paramScheduledExecutorService)
  {
    Object localObject1;
    Method localMethod2;
    Object localObject2;
    if (SHOULD_TRY_ENABLE_CANCEL_POLICY)
      if ((paramScheduledExecutorService instanceof ScheduledThreadPoolExecutor))
      {
        localObject1 = cachedSetRemoveOnCancelPolicyMethod;
        if (localObject1 == SET_REMOVE_ON_CANCEL_POLICY_METHOD_NOT_SUPPORTED)
          return false;
        if (localObject1 == null)
        {
          localMethod2 = findSetRemoveOnCancelPolicyMethod(paramScheduledExecutorService);
          if (localMethod2 != null)
            localObject2 = localMethod2;
        }
      }
    while (true)
    {
      cachedSetRemoveOnCancelPolicyMethod = localObject2;
      Method localMethod1 = localMethod2;
      label56: if (localMethod1 != null);
      try
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Boolean.valueOf(true);
        localMethod1.invoke(paramScheduledExecutorService, arrayOfObject);
        return true;
        localObject2 = SET_REMOVE_ON_CANCEL_POLICY_METHOD_NOT_SUPPORTED;
        continue;
        localMethod1 = (Method)localObject1;
        break label56;
        localMethod1 = findSetRemoveOnCancelPolicyMethod(paramScheduledExecutorService);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        RxJavaHooks.onError(localInvocationTargetException);
        return false;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        while (true)
          RxJavaHooks.onError(localIllegalAccessException);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        while (true)
          RxJavaHooks.onError(localIllegalArgumentException);
      }
    }
  }

  public boolean isUnsubscribed()
  {
    return this.isUnsubscribed;
  }

  public Subscription schedule(Action0 paramAction0)
  {
    return schedule(paramAction0, 0L, null);
  }

  public Subscription schedule(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit)
  {
    if (this.isUnsubscribed)
      return Subscriptions.unsubscribed();
    return scheduleActual(paramAction0, paramLong, paramTimeUnit);
  }

  public ScheduledAction scheduleActual(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit)
  {
    ScheduledAction localScheduledAction = new ScheduledAction(RxJavaHooks.onScheduledAction(paramAction0));
    if (paramLong <= 0L);
    for (Object localObject = this.executor.submit(localScheduledAction); ; localObject = this.executor.schedule(localScheduledAction, paramLong, paramTimeUnit))
    {
      localScheduledAction.add((Future)localObject);
      return localScheduledAction;
    }
  }

  public ScheduledAction scheduleActual(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit, SubscriptionList paramSubscriptionList)
  {
    ScheduledAction localScheduledAction = new ScheduledAction(RxJavaHooks.onScheduledAction(paramAction0), paramSubscriptionList);
    paramSubscriptionList.add(localScheduledAction);
    if (paramLong <= 0L);
    for (Object localObject = this.executor.submit(localScheduledAction); ; localObject = this.executor.schedule(localScheduledAction, paramLong, paramTimeUnit))
    {
      localScheduledAction.add((Future)localObject);
      return localScheduledAction;
    }
  }

  public ScheduledAction scheduleActual(Action0 paramAction0, long paramLong, TimeUnit paramTimeUnit, CompositeSubscription paramCompositeSubscription)
  {
    ScheduledAction localScheduledAction = new ScheduledAction(RxJavaHooks.onScheduledAction(paramAction0), paramCompositeSubscription);
    paramCompositeSubscription.add(localScheduledAction);
    if (paramLong <= 0L);
    for (Object localObject = this.executor.submit(localScheduledAction); ; localObject = this.executor.schedule(localScheduledAction, paramLong, paramTimeUnit))
    {
      localScheduledAction.add((Future)localObject);
      return localScheduledAction;
    }
  }

  public void unsubscribe()
  {
    this.isUnsubscribed = true;
    this.executor.shutdownNow();
    deregisterExecutor(this.executor);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.NewThreadWorker
 * JD-Core Version:    0.6.0
 */