package rx.internal.operators;

import java.io.Serializable;
import rx.Notification.Kind;
import rx.Observer;

public final class NotificationLite<T>
{
  private static final NotificationLite INSTANCE = new NotificationLite();
  private static final Object ON_COMPLETED_SENTINEL = new Serializable()
  {
    private static final long serialVersionUID = 1L;

    public String toString()
    {
      return "Notification=>Completed";
    }
  };
  private static final Object ON_NEXT_NULL_SENTINEL = new Serializable()
  {
    private static final long serialVersionUID = 2L;

    public String toString()
    {
      return "Notification=>NULL";
    }
  };

  public static <T> NotificationLite<T> instance()
  {
    return INSTANCE;
  }

  public boolean accept(Observer<? super T> paramObserver, Object paramObject)
  {
    if (paramObject == ON_COMPLETED_SENTINEL)
    {
      paramObserver.onCompleted();
      return true;
    }
    if (paramObject == ON_NEXT_NULL_SENTINEL)
    {
      paramObserver.onNext(null);
      return false;
    }
    if (paramObject != null)
    {
      if (paramObject.getClass() == OnErrorSentinel.class)
      {
        paramObserver.onError(((OnErrorSentinel)paramObject).e);
        return true;
      }
      paramObserver.onNext(paramObject);
      return false;
    }
    throw new IllegalArgumentException("The lite notification can not be null");
  }

  public Object completed()
  {
    return ON_COMPLETED_SENTINEL;
  }

  public Object error(Throwable paramThrowable)
  {
    return new OnErrorSentinel(paramThrowable);
  }

  public Throwable getError(Object paramObject)
  {
    return ((OnErrorSentinel)paramObject).e;
  }

  public T getValue(Object paramObject)
  {
    if (paramObject == ON_NEXT_NULL_SENTINEL)
      paramObject = null;
    return paramObject;
  }

  public boolean isCompleted(Object paramObject)
  {
    return paramObject == ON_COMPLETED_SENTINEL;
  }

  public boolean isError(Object paramObject)
  {
    return paramObject instanceof OnErrorSentinel;
  }

  public boolean isNext(Object paramObject)
  {
    return (paramObject != null) && (!isError(paramObject)) && (!isCompleted(paramObject));
  }

  public boolean isNull(Object paramObject)
  {
    return paramObject == ON_NEXT_NULL_SENTINEL;
  }

  public Notification.Kind kind(Object paramObject)
  {
    if (paramObject == null)
      throw new IllegalArgumentException("The lite notification can not be null");
    if (paramObject == ON_COMPLETED_SENTINEL)
      return Notification.Kind.OnCompleted;
    if ((paramObject instanceof OnErrorSentinel))
      return Notification.Kind.OnError;
    return Notification.Kind.OnNext;
  }

  public Object next(T paramT)
  {
    if (paramT == null)
      paramT = ON_NEXT_NULL_SENTINEL;
    return paramT;
  }

  static final class OnErrorSentinel
    implements Serializable
  {
    private static final long serialVersionUID = 3L;
    final Throwable e;

    public OnErrorSentinel(Throwable paramThrowable)
    {
      this.e = paramThrowable;
    }

    public String toString()
    {
      return "Notification=>Error:" + this.e;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.NotificationLite
 * JD-Core Version:    0.6.0
 */