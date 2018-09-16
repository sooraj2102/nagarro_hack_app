package okhttp3;

import java.io.IOException;

public abstract interface Call extends Cloneable
{
  public abstract void cancel();

  public abstract Call clone();

  public abstract void enqueue(Callback paramCallback);

  public abstract Response execute()
    throws IOException;

  public abstract boolean isCanceled();

  public abstract boolean isExecuted();

  public abstract Request request();

  public static abstract interface Factory
  {
    public abstract Call newCall(Request paramRequest);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Call
 * JD-Core Version:    0.6.0
 */