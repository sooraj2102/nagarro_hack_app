package okhttp3;

import java.io.IOException;

public enum Protocol
{
  private final String protocol;

  static
  {
    HTTP_2 = new Protocol("HTTP_2", 3, "h2");
    Protocol[] arrayOfProtocol = new Protocol[4];
    arrayOfProtocol[0] = HTTP_1_0;
    arrayOfProtocol[1] = HTTP_1_1;
    arrayOfProtocol[2] = SPDY_3;
    arrayOfProtocol[3] = HTTP_2;
    $VALUES = arrayOfProtocol;
  }

  private Protocol(String paramString)
  {
    this.protocol = paramString;
  }

  public static Protocol get(String paramString)
    throws IOException
  {
    if (paramString.equals(HTTP_1_0.protocol))
      return HTTP_1_0;
    if (paramString.equals(HTTP_1_1.protocol))
      return HTTP_1_1;
    if (paramString.equals(HTTP_2.protocol))
      return HTTP_2;
    if (paramString.equals(SPDY_3.protocol))
      return SPDY_3;
    throw new IOException("Unexpected protocol: " + paramString);
  }

  public String toString()
  {
    return this.protocol;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Protocol
 * JD-Core Version:    0.6.0
 */