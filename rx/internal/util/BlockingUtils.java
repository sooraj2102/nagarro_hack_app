package rx.internal.util;

import java.util.concurrent.CountDownLatch;
import rx.Subscription;
import rx.annotations.Experimental;

@Experimental
public final class BlockingUtils
{
  @Experimental
  public static void awaitForComplete(CountDownLatch paramCountDownLatch, Subscription paramSubscription)
  {
    if (paramCountDownLatch.getCount() == 0L)
      return;
    try
    {
      paramCountDownLatch.await();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      paramSubscription.unsubscribe();
      Thread.currentThread().interrupt();
    }
    throw new IllegalStateException("Interrupted while waiting for subscription to complete.", localInterruptedException);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.BlockingUtils
 * JD-Core Version:    0.6.0
 */