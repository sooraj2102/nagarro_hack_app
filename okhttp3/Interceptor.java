package okhttp3;

import java.io.IOException;

public abstract interface Interceptor
{
  public abstract Response intercept(Chain paramChain)
    throws IOException;

  public static abstract interface Chain
  {
    public abstract Connection connection();

    public abstract Response proceed(Request paramRequest)
      throws IOException;

    public abstract Request request();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Interceptor
 * JD-Core Version:    0.6.0
 */