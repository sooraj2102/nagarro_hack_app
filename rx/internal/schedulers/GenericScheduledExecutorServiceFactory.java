package rx.internal.schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import rx.functions.Func0;
import rx.internal.util.RxThreadFactory;
import rx.plugins.RxJavaHooks;

 enum GenericScheduledExecutorServiceFactory
{
  static final RxThreadFactory THREAD_FACTORY = new RxThreadFactory("RxScheduledExecutorPool-");
  static final String THREAD_NAME_PREFIX = "RxScheduledExecutorPool-";

  public static ScheduledExecutorService create()
  {
    Func0 localFunc0 = RxJavaHooks.getOnGenericScheduledExecutorService();
    if (localFunc0 == null)
      return createDefault();
    return (ScheduledExecutorService)localFunc0.call();
  }

  static ScheduledExecutorService createDefault()
  {
    return Executors.newScheduledThreadPool(1, threadFactory());
  }

  static ThreadFactory threadFactory()
  {
    return THREAD_FACTORY;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.schedulers.GenericScheduledExecutorServiceFactory
 * JD-Core Version:    0.6.0
 */