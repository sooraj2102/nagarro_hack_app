package rx;

import rx.annotations.Beta;
import rx.exceptions.MissingBackpressureException;

@Beta
public final class BackpressureOverflow
{
  public static final Strategy ON_OVERFLOW_DEFAULT;
  public static final Strategy ON_OVERFLOW_DROP_LATEST;
  public static final Strategy ON_OVERFLOW_DROP_OLDEST;
  public static final Strategy ON_OVERFLOW_ERROR = Error.INSTANCE;

  static
  {
    ON_OVERFLOW_DEFAULT = ON_OVERFLOW_ERROR;
    ON_OVERFLOW_DROP_OLDEST = DropOldest.INSTANCE;
    ON_OVERFLOW_DROP_LATEST = DropLatest.INSTANCE;
  }

  static class DropLatest
    implements BackpressureOverflow.Strategy
  {
    static final DropLatest INSTANCE = new DropLatest();

    public boolean mayAttemptDrop()
    {
      return false;
    }
  }

  static class DropOldest
    implements BackpressureOverflow.Strategy
  {
    static final DropOldest INSTANCE = new DropOldest();

    public boolean mayAttemptDrop()
    {
      return true;
    }
  }

  static class Error
    implements BackpressureOverflow.Strategy
  {
    static final Error INSTANCE = new Error();

    public boolean mayAttemptDrop()
      throws MissingBackpressureException
    {
      throw new MissingBackpressureException("Overflowed buffer");
    }
  }

  public static abstract interface Strategy
  {
    public abstract boolean mayAttemptDrop()
      throws MissingBackpressureException;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.BackpressureOverflow
 * JD-Core Version:    0.6.0
 */