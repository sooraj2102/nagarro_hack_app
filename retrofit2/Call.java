package retrofit2;

import java.io.IOException;
import okhttp3.Request;

public abstract interface Call<T> extends Cloneable
{
  public abstract void cancel();

  public abstract Call<T> clone();

  public abstract void enqueue(Callback<T> paramCallback);

  public abstract Response<T> execute()
    throws IOException;

  public abstract boolean isCanceled();

  public abstract boolean isExecuted();

  public abstract Request request();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.Call
 * JD-Core Version:    0.6.0
 */