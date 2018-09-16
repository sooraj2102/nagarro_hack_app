package okhttp3.internal.http2;

import java.io.IOException;

public final class StreamResetException extends IOException
{
  public final ErrorCode errorCode;

  public StreamResetException(ErrorCode paramErrorCode)
  {
    super("stream was reset: " + paramErrorCode);
    this.errorCode = paramErrorCode;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.http2.StreamResetException
 * JD-Core Version:    0.6.0
 */