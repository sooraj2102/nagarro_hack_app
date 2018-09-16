package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

public class AsyncTimeout extends Timeout
{
  private static final long IDLE_TIMEOUT_MILLIS = 0L;
  private static final long IDLE_TIMEOUT_NANOS = 0L;
  private static final int TIMEOUT_WRITE_SIZE = 65536;
  private static AsyncTimeout head;
  private boolean inQueue;
  private AsyncTimeout next;
  private long timeoutAt;

  static AsyncTimeout awaitTimeout()
    throws InterruptedException
  {
    AsyncTimeout localAsyncTimeout1 = head.next;
    if (localAsyncTimeout1 == null)
    {
      long l3 = System.nanoTime();
      AsyncTimeout.class.wait(IDLE_TIMEOUT_MILLIS);
      AsyncTimeout localAsyncTimeout2 = head.next;
      AsyncTimeout localAsyncTimeout3 = null;
      if (localAsyncTimeout2 == null)
      {
        boolean bool = System.nanoTime() - l3 < IDLE_TIMEOUT_NANOS;
        localAsyncTimeout3 = null;
        if (!bool)
          localAsyncTimeout3 = head;
      }
      return localAsyncTimeout3;
    }
    long l1 = localAsyncTimeout1.remainingNanos(System.nanoTime());
    if (l1 > 0L)
    {
      long l2 = l1 / 1000000L;
      AsyncTimeout.class.wait(l2, (int)(l1 - l2 * 1000000L));
      return null;
    }
    head.next = localAsyncTimeout1.next;
    localAsyncTimeout1.next = null;
    return localAsyncTimeout1;
  }

  private static boolean cancelScheduledTimeout(AsyncTimeout paramAsyncTimeout)
  {
    monitorenter;
    try
    {
      AsyncTimeout localAsyncTimeout = head;
      if (localAsyncTimeout != null)
        if (localAsyncTimeout.next == paramAsyncTimeout)
        {
          localAsyncTimeout.next = paramAsyncTimeout.next;
          paramAsyncTimeout.next = null;
        }
      for (int i = 0; ; i = 1)
      {
        return i;
        localAsyncTimeout = localAsyncTimeout.next;
        break;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  private long remainingNanos(long paramLong)
  {
    return this.timeoutAt - paramLong;
  }

  private static void scheduleTimeout(AsyncTimeout paramAsyncTimeout, long paramLong, boolean paramBoolean)
  {
    monitorenter;
    while (true)
    {
      try
      {
        if (head != null)
          continue;
        head = new AsyncTimeout();
        new Watchdog().start();
        long l1 = System.nanoTime();
        if ((paramLong == 0L) || (!paramBoolean))
          continue;
        paramAsyncTimeout.timeoutAt = (l1 + Math.min(paramLong, paramAsyncTimeout.deadlineNanoTime() - l1));
        long l2 = paramAsyncTimeout.remainingNanos(l1);
        localAsyncTimeout = head;
        if ((localAsyncTimeout.next != null) && (l2 >= localAsyncTimeout.next.remainingNanos(l1)))
          break label183;
        paramAsyncTimeout.next = localAsyncTimeout.next;
        localAsyncTimeout.next = paramAsyncTimeout;
        if (localAsyncTimeout != head)
          continue;
        AsyncTimeout.class.notify();
        return;
        if (paramLong != 0L)
        {
          long l3 = l1 + paramLong;
          paramAsyncTimeout.timeoutAt = l3;
          continue;
        }
      }
      finally
      {
        monitorexit;
      }
      if (paramBoolean)
      {
        paramAsyncTimeout.timeoutAt = paramAsyncTimeout.deadlineNanoTime();
        continue;
      }
      throw new AssertionError();
      label183: AsyncTimeout localAsyncTimeout = localAsyncTimeout.next;
    }
  }

  public final void enter()
  {
    if (this.inQueue)
      throw new IllegalStateException("Unbalanced enter/exit");
    long l = timeoutNanos();
    boolean bool = hasDeadline();
    if ((l == 0L) && (!bool))
      return;
    this.inQueue = true;
    scheduleTimeout(this, l, bool);
  }

  final IOException exit(IOException paramIOException)
    throws IOException
  {
    if (!exit())
      return paramIOException;
    return newTimeoutException(paramIOException);
  }

  final void exit(boolean paramBoolean)
    throws IOException
  {
    if ((exit()) && (paramBoolean))
      throw newTimeoutException(null);
  }

  public final boolean exit()
  {
    if (!this.inQueue)
      return false;
    this.inQueue = false;
    return cancelScheduledTimeout(this);
  }

  protected IOException newTimeoutException(IOException paramIOException)
  {
    InterruptedIOException localInterruptedIOException = new InterruptedIOException("timeout");
    if (paramIOException != null)
      localInterruptedIOException.initCause(paramIOException);
    return localInterruptedIOException;
  }

  public final Sink sink(Sink paramSink)
  {
    return new Sink(paramSink)
    {
      public void close()
        throws IOException
      {
        AsyncTimeout.this.enter();
        try
        {
          this.val$sink.close();
          AsyncTimeout.this.exit(true);
          return;
        }
        catch (IOException localIOException)
        {
          throw AsyncTimeout.this.exit(localIOException);
        }
        finally
        {
          AsyncTimeout.this.exit(false);
        }
        throw localObject;
      }

      public void flush()
        throws IOException
      {
        AsyncTimeout.this.enter();
        try
        {
          this.val$sink.flush();
          AsyncTimeout.this.exit(true);
          return;
        }
        catch (IOException localIOException)
        {
          throw AsyncTimeout.this.exit(localIOException);
        }
        finally
        {
          AsyncTimeout.this.exit(false);
        }
        throw localObject;
      }

      public Timeout timeout()
      {
        return AsyncTimeout.this;
      }

      public String toString()
      {
        return "AsyncTimeout.sink(" + this.val$sink + ")";
      }

      public void write(Buffer paramBuffer, long paramLong)
        throws IOException
      {
        Util.checkOffsetAndCount(paramBuffer.size, 0L, paramLong);
        while (paramLong > 0L)
        {
          long l = 0L;
          Segment localSegment = paramBuffer.head;
          label24: if (l < 65536L)
          {
            l += paramBuffer.head.limit - paramBuffer.head.pos;
            if (l >= paramLong)
              l = paramLong;
          }
          else
          {
            AsyncTimeout.this.enter();
          }
          try
          {
            this.val$sink.write(paramBuffer, l);
            paramLong -= l;
            AsyncTimeout.this.exit(true);
            continue;
            localSegment = localSegment.next;
            break label24;
          }
          catch (IOException localIOException)
          {
            throw AsyncTimeout.this.exit(localIOException);
          }
          finally
          {
            AsyncTimeout.this.exit(false);
          }
        }
      }
    };
  }

  public final Source source(Source paramSource)
  {
    return new Source(paramSource)
    {
      public void close()
        throws IOException
      {
        try
        {
          this.val$source.close();
          AsyncTimeout.this.exit(true);
          return;
        }
        catch (IOException localIOException)
        {
          throw AsyncTimeout.this.exit(localIOException);
        }
        finally
        {
          AsyncTimeout.this.exit(false);
        }
        throw localObject;
      }

      public long read(Buffer paramBuffer, long paramLong)
        throws IOException
      {
        AsyncTimeout.this.enter();
        try
        {
          long l = this.val$source.read(paramBuffer, paramLong);
          AsyncTimeout.this.exit(true);
          return l;
        }
        catch (IOException localIOException)
        {
          throw AsyncTimeout.this.exit(localIOException);
        }
        finally
        {
          AsyncTimeout.this.exit(false);
        }
        throw localObject;
      }

      public Timeout timeout()
      {
        return AsyncTimeout.this;
      }

      public String toString()
      {
        return "AsyncTimeout.source(" + this.val$source + ")";
      }
    };
  }

  protected void timedOut()
  {
  }

  private static final class Watchdog extends Thread
  {
    public Watchdog()
    {
      super();
      setDaemon(true);
    }

    public void run()
    {
      while (true)
      {
        AsyncTimeout localAsyncTimeout;
        try
        {
          monitorenter;
          try
          {
            localAsyncTimeout = AsyncTimeout.awaitTimeout();
            if (localAsyncTimeout != null)
              break label27;
            monitorexit;
            continue;
          }
          finally
          {
            monitorexit;
          }
        }
        catch (InterruptedException localInterruptedException)
        {
        }
        continue;
        label27: if (localAsyncTimeout == AsyncTimeout.head)
        {
          AsyncTimeout.access$002(null);
          monitorexit;
          return;
        }
        monitorexit;
        localAsyncTimeout.timedOut();
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okio.AsyncTimeout
 * JD-Core Version:    0.6.0
 */