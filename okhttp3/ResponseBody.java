package okhttp3;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;

public abstract class ResponseBody
  implements Closeable
{
  private Reader reader;

  private Charset charset()
  {
    MediaType localMediaType = contentType();
    if (localMediaType != null)
      return localMediaType.charset(Util.UTF_8);
    return Util.UTF_8;
  }

  public static ResponseBody create(MediaType paramMediaType, long paramLong, BufferedSource paramBufferedSource)
  {
    if (paramBufferedSource == null)
      throw new NullPointerException("source == null");
    return new ResponseBody(paramMediaType, paramLong, paramBufferedSource)
    {
      public long contentLength()
      {
        return this.val$contentLength;
      }

      public MediaType contentType()
      {
        return this.val$contentType;
      }

      public BufferedSource source()
      {
        return this.val$content;
      }
    };
  }

  public static ResponseBody create(MediaType paramMediaType, String paramString)
  {
    Charset localCharset = Util.UTF_8;
    if (paramMediaType != null)
    {
      localCharset = paramMediaType.charset();
      if (localCharset == null)
      {
        localCharset = Util.UTF_8;
        paramMediaType = MediaType.parse(paramMediaType + "; charset=utf-8");
      }
    }
    Buffer localBuffer = new Buffer().writeString(paramString, localCharset);
    return create(paramMediaType, localBuffer.size(), localBuffer);
  }

  public static ResponseBody create(MediaType paramMediaType, byte[] paramArrayOfByte)
  {
    Buffer localBuffer = new Buffer().write(paramArrayOfByte);
    return create(paramMediaType, paramArrayOfByte.length, localBuffer);
  }

  public final InputStream byteStream()
  {
    return source().inputStream();
  }

  public final byte[] bytes()
    throws IOException
  {
    long l = contentLength();
    if (l > 2147483647L)
      throw new IOException("Cannot buffer entire body for content length: " + l);
    BufferedSource localBufferedSource = source();
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = localBufferedSource.readByteArray();
      Util.closeQuietly(localBufferedSource);
      if ((l != -1L) && (l != arrayOfByte.length))
        throw new IOException("Content-Length (" + l + ") and stream length (" + arrayOfByte.length + ") disagree");
    }
    finally
    {
      Util.closeQuietly(localBufferedSource);
    }
    return arrayOfByte;
  }

  public final Reader charStream()
  {
    Reader localReader = this.reader;
    if (localReader != null)
      return localReader;
    BomAwareReader localBomAwareReader = new BomAwareReader(source(), charset());
    this.reader = localBomAwareReader;
    return localBomAwareReader;
  }

  public void close()
  {
    Util.closeQuietly(source());
  }

  public abstract long contentLength();

  public abstract MediaType contentType();

  public abstract BufferedSource source();

  public final String string()
    throws IOException
  {
    BufferedSource localBufferedSource = source();
    try
    {
      String str = localBufferedSource.readString(Util.bomAwareCharset(localBufferedSource, charset()));
      return str;
    }
    finally
    {
      Util.closeQuietly(localBufferedSource);
    }
    throw localObject;
  }

  static final class BomAwareReader extends Reader
  {
    private final Charset charset;
    private boolean closed;
    private Reader delegate;
    private final BufferedSource source;

    BomAwareReader(BufferedSource paramBufferedSource, Charset paramCharset)
    {
      this.source = paramBufferedSource;
      this.charset = paramCharset;
    }

    public void close()
      throws IOException
    {
      this.closed = true;
      if (this.delegate != null)
      {
        this.delegate.close();
        return;
      }
      this.source.close();
    }

    public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      if (this.closed)
        throw new IOException("Stream closed");
      Object localObject = this.delegate;
      if (localObject == null)
      {
        Charset localCharset = Util.bomAwareCharset(this.source, this.charset);
        localObject = new InputStreamReader(this.source.inputStream(), localCharset);
        this.delegate = ((Reader)localObject);
      }
      return ((Reader)localObject).read(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.ResponseBody
 * JD-Core Version:    0.6.0
 */