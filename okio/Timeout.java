package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

public class Timeout
{
  public static final Timeout NONE = new Timeout()
  {
    public Timeout deadlineNanoTime(long paramLong)
    {
      return this;
    }

    public void throwIfReached()
      throws IOException
    {
    }

    public Timeout timeout(long paramLong, TimeUnit paramTimeUnit)
    {
      return this;
    }
  };
  private long deadlineNanoTime;
  private boolean hasDeadline;
  private long timeoutNanos;

  public Timeout clearDeadline()
  {
    this.hasDeadline = false;
    return this;
  }

  public Timeout clearTimeout()
  {
    this.timeoutNanos = 0L;
    return this;
  }

  public final Timeout deadline(long paramLong, TimeUnit paramTimeUnit)
  {
    if (paramLong <= 0L)
      throw new IllegalArgumentException("duration <= 0: " + paramLong);
    if (paramTimeUnit == null)
      throw new IllegalArgumentException("unit == null");
    return deadlineNanoTime(System.nanoTime() + paramTimeUnit.toNanos(paramLong));
  }

  public long deadlineNanoTime()
  {
    if (!this.hasDeadline)
      throw new IllegalStateException("No deadline");
    return this.deadlineNanoTime;
  }

  public Timeout deadlineNanoTime(long paramLong)
  {
    this.hasDeadline = true;
    this.deadlineNanoTime = paramLong;
    return this;
  }

  public boolean hasDeadline()
  {
    return this.hasDeadline;
  }

  public void throwIfReached()
    throws IOException
  {
    if (Thread.interrupted())
      throw new InterruptedIOException("thread interrupted");
    if ((this.hasDeadline) && (this.deadlineNanoTime - System.nanoTime() <= 0L))
      throw new InterruptedIOException("deadline reached");
  }

  public Timeout timeout(long paramLong, TimeUnit paramTimeUnit)
  {
    if (paramLong < 0L)
      throw new IllegalArgumentException("timeout < 0: " + paramLong);
    if (paramTimeUnit == null)
      throw new IllegalArgumentException("unit == null");
    this.timeoutNanos = paramTimeUnit.toNanos(paramLong);
    return this;
  }

  public long timeoutNanos()
  {
    return this.timeoutNanos;
  }

  public final void waitUntilNotified(Object paramObject)
    throws InterruptedIOException
  {
    while (true)
    {
      boolean bool;
      long l1;
      long l2;
      try
      {
        bool = hasDeadline();
        l1 = timeoutNanos();
        if ((bool) || (l1 != 0L))
          continue;
        paramObject.wait();
        return;
        l2 = System.nanoTime();
        if ((bool) && (l1 != 0L))
        {
          l3 = Math.min(l1, deadlineNanoTime() - l2);
          long l4 = 0L;
          if (l3 <= 0L)
            continue;
          long l5 = l3 / 1000000L;
          paramObject.wait(l5, (int)(l3 - 1000000L * l5));
          l4 = System.nanoTime() - l2;
          if (l4 < l3)
            break;
          throw new InterruptedIOException("timeout");
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new InterruptedIOException("interrupted");
      }
      if (bool)
      {
        long l6 = deadlineNanoTime();
        l3 = l6 - l2;
        continue;
      }
      long l3 = l1;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okio.Timeout
 * JD-Core Version:    0.6.0
 */