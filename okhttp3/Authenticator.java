package okhttp3;

import java.io.IOException;

public abstract interface Authenticator
{
  public static final Authenticator NONE = new Authenticator()
  {
    public Request authenticate(Route paramRoute, Response paramResponse)
    {
      return null;
    }
  };

  public abstract Request authenticate(Route paramRoute, Response paramResponse)
    throws IOException;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Authenticator
 * JD-Core Version:    0.6.0
 */