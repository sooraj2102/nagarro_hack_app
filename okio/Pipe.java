package okio;

import java.io.IOException;

public final class Pipe
{
  final Buffer buffer = new Buffer();
  final long maxBufferSize;
  private final Sink sink = new PipeSink();
  boolean sinkClosed;
  private final Source source = new PipeSource();
  boolean sourceClosed;

  public Pipe(long paramLong)
  {
    if (paramLong < 1L)
      throw new IllegalArgumentException("maxBufferSize < 1: " + paramLong);
    this.maxBufferSize = paramLong;
  }

  public Sink sink()
  {
    return this.sink;
  }

  public Source source()
  {
    return this.source;
  }

  final class PipeSink
    implements Sink
  {
    final Timeout timeout = new Timeout();

    PipeSink()
    {
    }

    public void close()
      throws IOException
    {
      synchronized (Pipe.this.buffer)
      {
        if (Pipe.this.sinkClosed)
          return;
      }
      try
      {
        flush();
        Pipe.this.sinkClosed = true;
        Pipe.this.buffer.notifyAll();
        monitorexit;
        return;
        localObject1 = finally;
        monitorexit;
        throw localObject1;
      }
      finally
      {
        Pipe.this.sinkClosed = true;
      }
      throw localObject2;
    }

    public void flush()
      throws IOException
    {
      synchronized (Pipe.this.buffer)
      {
        if (Pipe.this.sinkClosed)
          throw new IllegalStateException("closed");
      }
      while (true)
      {
        this.timeout.waitUntilNotified(Pipe.this.buffer);
        if (Pipe.this.buffer.size() <= 0L)
          break;
        if (!Pipe.this.sourceClosed)
          continue;
        throw new IOException("source is closed");
      }
      monitorexit;
    }

    public Timeout timeout()
    {
      return this.timeout;
    }

    public void write(Buffer paramBuffer, long paramLong)
      throws IOException
    {
      synchronized (Pipe.this.buffer)
      {
        if (!Pipe.this.sinkClosed)
          break label81;
        throw new IllegalStateException("closed");
      }
      long l1 = Pipe.this.maxBufferSize - Pipe.this.buffer.size();
      if (l1 == 0L)
        this.timeout.waitUntilNotified(Pipe.this.buffer);
      while (true)
      {
        label81: if (paramLong <= 0L)
          break label146;
        if (!Pipe.this.sourceClosed)
          break;
        throw new IOException("source is closed");
        long l2 = Math.min(l1, paramLong);
        Pipe.this.buffer.write(paramBuffer, l2);
        paramLong -= l2;
        Pipe.this.buffer.notifyAll();
      }
      label146: monitorexit;
    }
  }

  final class PipeSource
    implements Source
  {
    final Timeout timeout = new Timeout();

    PipeSource()
    {
    }

    public void close()
      throws IOException
    {
      synchronized (Pipe.this.buffer)
      {
        Pipe.this.sourceClosed = true;
        Pipe.this.buffer.notifyAll();
        return;
      }
    }

    public long read(Buffer paramBuffer, long paramLong)
      throws IOException
    {
      synchronized (Pipe.this.buffer)
      {
        if (Pipe.this.sourceClosed)
          throw new IllegalStateException("closed");
      }
      while (true)
      {
        this.timeout.waitUntilNotified(Pipe.this.buffer);
        if (Pipe.this.buffer.size() != 0L)
          break;
        if (!Pipe.this.sinkClosed)
          continue;
        monitorexit;
        return -1L;
      }
      long l = Pipe.this.buffer.read(paramBuffer, paramLong);
      Pipe.this.buffer.notifyAll();
      monitorexit;
      return l;
    }

    public Timeout timeout()
    {
      return this.timeout;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okio.Pipe
 * JD-Core Version:    0.6.0
 */