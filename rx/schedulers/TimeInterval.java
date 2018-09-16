package rx.schedulers;

public class TimeInterval<T>
{
  private final long intervalInMilliseconds;
  private final T value;

  public TimeInterval(long paramLong, T paramT)
  {
    this.value = paramT;
    this.intervalInMilliseconds = paramLong;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    TimeInterval localTimeInterval;
    do
      while (true)
      {
        return true;
        if (paramObject == null)
          return false;
        if (getClass() != paramObject.getClass())
          return false;
        localTimeInterval = (TimeInterval)paramObject;
        if (this.intervalInMilliseconds != localTimeInterval.intervalInMilliseconds)
          return false;
        if (this.value != null)
          break;
        if (localTimeInterval.value != null)
          return false;
      }
    while (this.value.equals(localTimeInterval.value));
    return false;
  }

  public long getIntervalInMilliseconds()
  {
    return this.intervalInMilliseconds;
  }

  public T getValue()
  {
    return this.value;
  }

  public int hashCode()
  {
    int i = 31 * (31 + (int)(this.intervalInMilliseconds ^ this.intervalInMilliseconds >>> 32));
    if (this.value == null);
    for (int j = 0; ; j = this.value.hashCode())
      return i + j;
  }

  public String toString()
  {
    return "TimeInterval [intervalInMilliseconds=" + this.intervalInMilliseconds + ", value=" + this.value + "]";
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.schedulers.TimeInterval
 * JD-Core Version:    0.6.0
 */