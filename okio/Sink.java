package okio;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public abstract interface Sink extends Closeable, Flushable
{
  public abstract void close()
    throws IOException;

  public abstract void flush()
    throws IOException;

  public abstract Timeout timeout();

  public abstract void write(Buffer paramBuffer, long paramLong)
    throws IOException;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okio.Sink
 * JD-Core Version:    0.6.0
 */