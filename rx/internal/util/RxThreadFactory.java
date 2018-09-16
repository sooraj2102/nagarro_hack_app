package rx.internal.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class RxThreadFactory extends AtomicLong
  implements ThreadFactory
{
  public static final ThreadFactory NONE = new ThreadFactory()
  {
    public Thread newThread(Runnable paramRunnable)
    {
      throw new AssertionError("No threads allowed.");
    }
  };
  private static final long serialVersionUID = -8841098858898482335L;
  final String prefix;

  public RxThreadFactory(String paramString)
  {
    this.prefix = paramString;
  }

  public Thread newThread(Runnable paramRunnable)
  {
    Thread localThread = new Thread(paramRunnable, this.prefix + incrementAndGet());
    localThread.setDaemon(true);
    return localThread;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.RxThreadFactory
 * JD-Core Version:    0.6.0
 */