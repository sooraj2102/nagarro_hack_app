package okhttp3.internal.http2;

public enum ErrorCode
{
  public final int httpCode;

  static
  {
    INTERNAL_ERROR = new ErrorCode("INTERNAL_ERROR", 2, 2);
    FLOW_CONTROL_ERROR = new ErrorCode("FLOW_CONTROL_ERROR", 3, 3);
    REFUSED_STREAM = new ErrorCode("REFUSED_STREAM", 4, 7);
    CANCEL = new ErrorCode("CANCEL", 5, 8);
    ErrorCode[] arrayOfErrorCode = new ErrorCode[6];
    arrayOfErrorCode[0] = NO_ERROR;
    arrayOfErrorCode[1] = PROTOCOL_ERROR;
    arrayOfErrorCode[2] = INTERNAL_ERROR;
    arrayOfErrorCode[3] = FLOW_CONTROL_ERROR;
    arrayOfErrorCode[4] = REFUSED_STREAM;
    arrayOfErrorCode[5] = CANCEL;
    $VALUES = arrayOfErrorCode;
  }

  private ErrorCode(int paramInt)
  {
    this.httpCode = paramInt;
  }

  public static ErrorCode fromHttp2(int paramInt)
  {
    for (ErrorCode localErrorCode : values())
      if (localErrorCode.httpCode == paramInt)
        return localErrorCode;
    return null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.http2.ErrorCode
 * JD-Core Version:    0.6.0
 */