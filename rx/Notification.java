package rx;

public final class Notification<T>
{
  private static final Notification<Void> ON_COMPLETED = new Notification(Kind.OnCompleted, null, null);
  private final Kind kind;
  private final Throwable throwable;
  private final T value;

  private Notification(Kind paramKind, T paramT, Throwable paramThrowable)
  {
    this.value = paramT;
    this.throwable = paramThrowable;
    this.kind = paramKind;
  }

  public static <T> Notification<T> createOnCompleted()
  {
    return ON_COMPLETED;
  }

  @Deprecated
  public static <T> Notification<T> createOnCompleted(Class<T> paramClass)
  {
    return ON_COMPLETED;
  }

  public static <T> Notification<T> createOnError(Throwable paramThrowable)
  {
    return new Notification(Kind.OnError, null, paramThrowable);
  }

  public static <T> Notification<T> createOnNext(T paramT)
  {
    return new Notification(Kind.OnNext, paramT, null);
  }

  public void accept(Observer<? super T> paramObserver)
  {
    if (this.kind == Kind.OnNext)
    {
      paramObserver.onNext(getValue());
      return;
    }
    if (this.kind == Kind.OnCompleted)
    {
      paramObserver.onCompleted();
      return;
    }
    paramObserver.onError(getThrowable());
  }

  public boolean equals(Object paramObject)
  {
    int i = 1;
    if (paramObject == null);
    do
    {
      return false;
      if (this == paramObject)
        return i;
    }
    while (paramObject.getClass() != getClass());
    Notification localNotification = (Notification)paramObject;
    if ((localNotification.getKind() == getKind()) && ((this.value == localNotification.value) || ((this.value != null) && (this.value.equals(localNotification.value)))) && ((this.throwable == localNotification.throwable) || ((this.throwable != null) && (this.throwable.equals(localNotification.throwable)))));
    while (true)
    {
      return i;
      i = 0;
    }
  }

  public Kind getKind()
  {
    return this.kind;
  }

  public Throwable getThrowable()
  {
    return this.throwable;
  }

  public T getValue()
  {
    return this.value;
  }

  public boolean hasThrowable()
  {
    return (isOnError()) && (this.throwable != null);
  }

  public boolean hasValue()
  {
    return (isOnNext()) && (this.value != null);
  }

  public int hashCode()
  {
    int i = getKind().hashCode();
    if (hasValue())
      i = i * 31 + getValue().hashCode();
    if (hasThrowable())
      i = i * 31 + getThrowable().hashCode();
    return i;
  }

  public boolean isOnCompleted()
  {
    return getKind() == Kind.OnCompleted;
  }

  public boolean isOnError()
  {
    return getKind() == Kind.OnError;
  }

  public boolean isOnNext()
  {
    return getKind() == Kind.OnNext;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(64).append('[').append(super.toString()).append(' ').append(getKind());
    if (hasValue())
      localStringBuilder.append(' ').append(getValue());
    if (hasThrowable())
      localStringBuilder.append(' ').append(getThrowable().getMessage());
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }

  public static enum Kind
  {
    static
    {
      OnError = new Kind("OnError", 1);
      OnCompleted = new Kind("OnCompleted", 2);
      Kind[] arrayOfKind = new Kind[3];
      arrayOfKind[0] = OnNext;
      arrayOfKind[1] = OnError;
      arrayOfKind[2] = OnCompleted;
      $VALUES = arrayOfKind;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.Notification
 * JD-Core Version:    0.6.0
 */