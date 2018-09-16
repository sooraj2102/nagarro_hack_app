package okhttp3;

import java.io.IOException;

public abstract interface Callback
{
  public abstract void onFailure(Call paramCall, IOException paramIOException);

  public abstract void onResponse(Call paramCall, Response paramResponse)
    throws IOException;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Callback
 * JD-Core Version:    0.6.0
 */