package okhttp3;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Source;

public abstract class RequestBody
{
  public static RequestBody create(MediaType paramMediaType, File paramFile)
  {
    if (paramFile == null)
      throw new NullPointerException("content == null");
    return new RequestBody(paramMediaType, paramFile)
    {
      public long contentLength()
      {
        return this.val$file.length();
      }

      public MediaType contentType()
      {
        return this.val$contentType;
      }

      public void writeTo(BufferedSink paramBufferedSink)
        throws IOException
      {
        Source localSource = null;
        try
        {
          localSource = Okio.source(this.val$file);
          paramBufferedSink.writeAll(localSource);
          return;
        }
        finally
        {
          Util.closeQuietly(localSource);
        }
        throw localObject;
      }
    };
  }

  public static RequestBody create(MediaType paramMediaType, String paramString)
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
    return create(paramMediaType, paramString.getBytes(localCharset));
  }

  public static RequestBody create(MediaType paramMediaType, ByteString paramByteString)
  {
    return new RequestBody(paramMediaType, paramByteString)
    {
      public long contentLength()
        throws IOException
      {
        return this.val$content.size();
      }

      public MediaType contentType()
      {
        return this.val$contentType;
      }

      public void writeTo(BufferedSink paramBufferedSink)
        throws IOException
      {
        paramBufferedSink.write(this.val$content);
      }
    };
  }

  public static RequestBody create(MediaType paramMediaType, byte[] paramArrayOfByte)
  {
    return create(paramMediaType, paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public static RequestBody create(MediaType paramMediaType, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null)
      throw new NullPointerException("content == null");
    Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
    return new RequestBody(paramMediaType, paramInt2, paramArrayOfByte, paramInt1)
    {
      public long contentLength()
      {
        return this.val$byteCount;
      }

      public MediaType contentType()
      {
        return this.val$contentType;
      }

      public void writeTo(BufferedSink paramBufferedSink)
        throws IOException
      {
        paramBufferedSink.write(this.val$content, this.val$offset, this.val$byteCount);
      }
    };
  }

  public long contentLength()
    throws IOException
  {
    return -1L;
  }

  public abstract MediaType contentType();

  public abstract void writeTo(BufferedSink paramBufferedSink)
    throws IOException;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.RequestBody
 * JD-Core Version:    0.6.0
 */