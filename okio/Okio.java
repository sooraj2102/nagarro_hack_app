package okio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

public final class Okio
{
  static final Logger logger = Logger.getLogger(Okio.class.getName());

  public static Sink appendingSink(File paramFile)
    throws FileNotFoundException
  {
    if (paramFile == null)
      throw new IllegalArgumentException("file == null");
    return sink(new FileOutputStream(paramFile, true));
  }

  public static Sink blackhole()
  {
    return new Sink()
    {
      public void close()
        throws IOException
      {
      }

      public void flush()
        throws IOException
      {
      }

      public Timeout timeout()
      {
        return Timeout.NONE;
      }

      public void write(Buffer paramBuffer, long paramLong)
        throws IOException
      {
        paramBuffer.skip(paramLong);
      }
    };
  }

  public static BufferedSink buffer(Sink paramSink)
  {
    return new RealBufferedSink(paramSink);
  }

  public static BufferedSource buffer(Source paramSource)
  {
    return new RealBufferedSource(paramSource);
  }

  static boolean isAndroidGetsocknameError(AssertionError paramAssertionError)
  {
    return (paramAssertionError.getCause() != null) && (paramAssertionError.getMessage() != null) && (paramAssertionError.getMessage().contains("getsockname failed"));
  }

  public static Sink sink(File paramFile)
    throws FileNotFoundException
  {
    if (paramFile == null)
      throw new IllegalArgumentException("file == null");
    return sink(new FileOutputStream(paramFile));
  }

  public static Sink sink(OutputStream paramOutputStream)
  {
    return sink(paramOutputStream, new Timeout());
  }

  private static Sink sink(OutputStream paramOutputStream, Timeout paramTimeout)
  {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("out == null");
    if (paramTimeout == null)
      throw new IllegalArgumentException("timeout == null");
    return new Sink(paramTimeout, paramOutputStream)
    {
      public void close()
        throws IOException
      {
        this.val$out.close();
      }

      public void flush()
        throws IOException
      {
        this.val$out.flush();
      }

      public Timeout timeout()
      {
        return this.val$timeout;
      }

      public String toString()
      {
        return "sink(" + this.val$out + ")";
      }

      public void write(Buffer paramBuffer, long paramLong)
        throws IOException
      {
        Util.checkOffsetAndCount(paramBuffer.size, 0L, paramLong);
        while (paramLong > 0L)
        {
          this.val$timeout.throwIfReached();
          Segment localSegment = paramBuffer.head;
          int i = (int)Math.min(paramLong, localSegment.limit - localSegment.pos);
          this.val$out.write(localSegment.data, localSegment.pos, i);
          localSegment.pos = (i + localSegment.pos);
          paramLong -= i;
          paramBuffer.size -= i;
          if (localSegment.pos != localSegment.limit)
            continue;
          paramBuffer.head = localSegment.pop();
          SegmentPool.recycle(localSegment);
        }
      }
    };
  }

  public static Sink sink(Socket paramSocket)
    throws IOException
  {
    if (paramSocket == null)
      throw new IllegalArgumentException("socket == null");
    AsyncTimeout localAsyncTimeout = timeout(paramSocket);
    return localAsyncTimeout.sink(sink(paramSocket.getOutputStream(), localAsyncTimeout));
  }

  @IgnoreJRERequirement
  public static Sink sink(Path paramPath, OpenOption[] paramArrayOfOpenOption)
    throws IOException
  {
    if (paramPath == null)
      throw new IllegalArgumentException("path == null");
    return sink(Files.newOutputStream(paramPath, paramArrayOfOpenOption));
  }

  public static Source source(File paramFile)
    throws FileNotFoundException
  {
    if (paramFile == null)
      throw new IllegalArgumentException("file == null");
    return source(new FileInputStream(paramFile));
  }

  public static Source source(InputStream paramInputStream)
  {
    return source(paramInputStream, new Timeout());
  }

  private static Source source(InputStream paramInputStream, Timeout paramTimeout)
  {
    if (paramInputStream == null)
      throw new IllegalArgumentException("in == null");
    if (paramTimeout == null)
      throw new IllegalArgumentException("timeout == null");
    return new Source(paramTimeout, paramInputStream)
    {
      public void close()
        throws IOException
      {
        this.val$in.close();
      }

      public long read(Buffer paramBuffer, long paramLong)
        throws IOException
      {
        if (paramLong < 0L)
          throw new IllegalArgumentException("byteCount < 0: " + paramLong);
        if (paramLong == 0L)
          return 0L;
        try
        {
          this.val$timeout.throwIfReached();
          Segment localSegment = paramBuffer.writableSegment(1);
          int i = (int)Math.min(paramLong, 8192 - localSegment.limit);
          int j = this.val$in.read(localSegment.data, localSegment.limit, i);
          if (j == -1)
            return -1L;
          localSegment.limit = (j + localSegment.limit);
          paramBuffer.size += j;
          return j;
        }
        catch (AssertionError localAssertionError)
        {
          if (Okio.isAndroidGetsocknameError(localAssertionError))
            throw new IOException(localAssertionError);
        }
        throw localAssertionError;
      }

      public Timeout timeout()
      {
        return this.val$timeout;
      }

      public String toString()
      {
        return "source(" + this.val$in + ")";
      }
    };
  }

  public static Source source(Socket paramSocket)
    throws IOException
  {
    if (paramSocket == null)
      throw new IllegalArgumentException("socket == null");
    AsyncTimeout localAsyncTimeout = timeout(paramSocket);
    return localAsyncTimeout.source(source(paramSocket.getInputStream(), localAsyncTimeout));
  }

  @IgnoreJRERequirement
  public static Source source(Path paramPath, OpenOption[] paramArrayOfOpenOption)
    throws IOException
  {
    if (paramPath == null)
      throw new IllegalArgumentException("path == null");
    return source(Files.newInputStream(paramPath, paramArrayOfOpenOption));
  }

  private static AsyncTimeout timeout(Socket paramSocket)
  {
    return new AsyncTimeout(paramSocket)
    {
      protected IOException newTimeoutException(IOException paramIOException)
      {
        SocketTimeoutException localSocketTimeoutException = new SocketTimeoutException("timeout");
        if (paramIOException != null)
          localSocketTimeoutException.initCause(paramIOException);
        return localSocketTimeoutException;
      }

      protected void timedOut()
      {
        try
        {
          this.val$socket.close();
          return;
        }
        catch (Exception localException)
        {
          Okio.logger.log(Level.WARNING, "Failed to close timed out socket " + this.val$socket, localException);
          return;
        }
        catch (AssertionError localAssertionError)
        {
          if (Okio.isAndroidGetsocknameError(localAssertionError))
          {
            Okio.logger.log(Level.WARNING, "Failed to close timed out socket " + this.val$socket, localAssertionError);
            return;
          }
        }
        throw localAssertionError;
      }
    };
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okio.Okio
 * JD-Core Version:    0.6.0
 */