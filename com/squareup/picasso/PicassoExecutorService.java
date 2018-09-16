package com.squareup.picasso;

import android.net.NetworkInfo;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class PicassoExecutorService extends ThreadPoolExecutor
{
  private static final int DEFAULT_THREAD_COUNT = 3;

  PicassoExecutorService()
  {
    super(3, 3, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue(), new Utils.PicassoThreadFactory());
  }

  private void setThreadCount(int paramInt)
  {
    setCorePoolSize(paramInt);
    setMaximumPoolSize(paramInt);
  }

  void adjustThreadCount(NetworkInfo paramNetworkInfo)
  {
    if ((paramNetworkInfo == null) || (!paramNetworkInfo.isConnectedOrConnecting()))
    {
      setThreadCount(3);
      return;
    }
    switch (paramNetworkInfo.getType())
    {
    default:
      setThreadCount(3);
      return;
    case 1:
    case 6:
    case 9:
      setThreadCount(4);
      return;
    case 0:
    }
    switch (paramNetworkInfo.getSubtype())
    {
    case 7:
    case 8:
    case 9:
    case 10:
    case 11:
    default:
      setThreadCount(3);
      return;
    case 13:
    case 14:
    case 15:
      setThreadCount(3);
      return;
    case 3:
    case 4:
    case 5:
    case 6:
    case 12:
      setThreadCount(2);
      return;
    case 1:
    case 2:
    }
    setThreadCount(1);
  }

  public Future<?> submit(Runnable paramRunnable)
  {
    PicassoFutureTask localPicassoFutureTask = new PicassoFutureTask((BitmapHunter)paramRunnable);
    execute(localPicassoFutureTask);
    return localPicassoFutureTask;
  }

  private static final class PicassoFutureTask extends FutureTask<BitmapHunter>
    implements Comparable<PicassoFutureTask>
  {
    private final BitmapHunter hunter;

    public PicassoFutureTask(BitmapHunter paramBitmapHunter)
    {
      super(null);
      this.hunter = paramBitmapHunter;
    }

    public int compareTo(PicassoFutureTask paramPicassoFutureTask)
    {
      Picasso.Priority localPriority1 = this.hunter.getPriority();
      Picasso.Priority localPriority2 = paramPicassoFutureTask.hunter.getPriority();
      if (localPriority1 == localPriority2)
        return this.hunter.sequence - paramPicassoFutureTask.hunter.sequence;
      return localPriority2.ordinal() - localPriority1.ordinal();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.PicassoExecutorService
 * JD-Core Version:    0.6.0
 */