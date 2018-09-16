package rx.plugins;

import java.util.concurrent.ThreadFactory;
import rx.Scheduler;
import rx.annotations.Experimental;
import rx.functions.Action0;
import rx.internal.schedulers.CachedThreadScheduler;
import rx.internal.schedulers.EventLoopsScheduler;
import rx.internal.schedulers.NewThreadScheduler;
import rx.internal.util.RxThreadFactory;

public class RxJavaSchedulersHook
{
  private static final RxJavaSchedulersHook DEFAULT_INSTANCE = new RxJavaSchedulersHook();

  @Experimental
  public static Scheduler createComputationScheduler()
  {
    return createComputationScheduler(new RxThreadFactory("RxComputationScheduler-"));
  }

  @Experimental
  public static Scheduler createComputationScheduler(ThreadFactory paramThreadFactory)
  {
    if (paramThreadFactory == null)
      throw new NullPointerException("threadFactory == null");
    return new EventLoopsScheduler(paramThreadFactory);
  }

  @Experimental
  public static Scheduler createIoScheduler()
  {
    return createIoScheduler(new RxThreadFactory("RxIoScheduler-"));
  }

  @Experimental
  public static Scheduler createIoScheduler(ThreadFactory paramThreadFactory)
  {
    if (paramThreadFactory == null)
      throw new NullPointerException("threadFactory == null");
    return new CachedThreadScheduler(paramThreadFactory);
  }

  @Experimental
  public static Scheduler createNewThreadScheduler()
  {
    return createNewThreadScheduler(new RxThreadFactory("RxNewThreadScheduler-"));
  }

  @Experimental
  public static Scheduler createNewThreadScheduler(ThreadFactory paramThreadFactory)
  {
    if (paramThreadFactory == null)
      throw new NullPointerException("threadFactory == null");
    return new NewThreadScheduler(paramThreadFactory);
  }

  public static RxJavaSchedulersHook getDefaultInstance()
  {
    return DEFAULT_INSTANCE;
  }

  public Scheduler getComputationScheduler()
  {
    return null;
  }

  public Scheduler getIOScheduler()
  {
    return null;
  }

  public Scheduler getNewThreadScheduler()
  {
    return null;
  }

  @Deprecated
  public Action0 onSchedule(Action0 paramAction0)
  {
    return paramAction0;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.plugins.RxJavaSchedulersHook
 * JD-Core Version:    0.6.0
 */