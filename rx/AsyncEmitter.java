package rx;

import rx.annotations.Experimental;

@Experimental
public abstract interface AsyncEmitter<T> extends Observer<T>
{
  public abstract long requested();

  public abstract void setCancellation(Cancellable paramCancellable);

  public abstract void setSubscription(Subscription paramSubscription);

  public static enum BackpressureMode
  {
    static
    {
      ERROR = new BackpressureMode("ERROR", 1);
      BUFFER = new BackpressureMode("BUFFER", 2);
      DROP = new BackpressureMode("DROP", 3);
      LATEST = new BackpressureMode("LATEST", 4);
      BackpressureMode[] arrayOfBackpressureMode = new BackpressureMode[5];
      arrayOfBackpressureMode[0] = NONE;
      arrayOfBackpressureMode[1] = ERROR;
      arrayOfBackpressureMode[2] = BUFFER;
      arrayOfBackpressureMode[3] = DROP;
      arrayOfBackpressureMode[4] = LATEST;
      $VALUES = arrayOfBackpressureMode;
    }
  }

  public static abstract interface Cancellable
  {
    public abstract void cancel()
      throws Exception;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.AsyncEmitter
 * JD-Core Version:    0.6.0
 */