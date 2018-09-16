package okhttp3;

import java.util.Collections;
import java.util.List;

public abstract interface CookieJar
{
  public static final CookieJar NO_COOKIES = new CookieJar()
  {
    public List<Cookie> loadForRequest(HttpUrl paramHttpUrl)
    {
      return Collections.emptyList();
    }

    public void saveFromResponse(HttpUrl paramHttpUrl, List<Cookie> paramList)
    {
    }
  };

  public abstract List<Cookie> loadForRequest(HttpUrl paramHttpUrl);

  public abstract void saveFromResponse(HttpUrl paramHttpUrl, List<Cookie> paramList);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.CookieJar
 * JD-Core Version:    0.6.0
 */