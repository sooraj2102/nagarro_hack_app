package rx.observers;

import rx.Observer;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.internal.operators.NotificationLite;

public class SerializedObserver<T>
  implements Observer<T>
{
  private final Observer<? super T> actual;
  private boolean emitting;
  private final NotificationLite<T> nl = NotificationLite.instance();
  private FastList queue;
  private volatile boolean terminated;

  public SerializedObserver(Observer<? super T> paramObserver)
  {
    this.actual = paramObserver;
  }

  public void onCompleted()
  {
    if (this.terminated)
      return;
    monitorenter;
    try
    {
      if (this.terminated)
        return;
    }
    finally
    {
      monitorexit;
    }
    this.terminated = true;
    if (this.emitting)
    {
      FastList localFastList = this.queue;
      if (localFastList == null)
      {
        localFastList = new FastList();
        this.queue = localFastList;
      }
      localFastList.add(this.nl.completed());
      monitorexit;
      return;
    }
    this.emitting = true;
    monitorexit;
    this.actual.onCompleted();
  }

  public void onError(Throwable paramThrowable)
  {
    Exceptions.throwIfFatal(paramThrowable);
    if (this.terminated)
      return;
    monitorenter;
    try
    {
      if (this.terminated)
        return;
    }
    finally
    {
      monitorexit;
    }
    this.terminated = true;
    if (this.emitting)
    {
      FastList localFastList = this.queue;
      if (localFastList == null)
      {
        localFastList = new FastList();
        this.queue = localFastList;
      }
      localFastList.add(this.nl.error(paramThrowable));
      monitorexit;
      return;
    }
    this.emitting = true;
    monitorexit;
    this.actual.onError(paramThrowable);
  }

  public void onNext(T paramT)
  {
    if (this.terminated)
      return;
    monitorenter;
    try
    {
      if (this.terminated)
        return;
    }
    finally
    {
      monitorexit;
    }
    if (this.emitting)
    {
      FastList localFastList2 = this.queue;
      if (localFastList2 == null)
      {
        localFastList2 = new FastList();
        this.queue = localFastList2;
      }
      localFastList2.add(this.nl.next(paramT));
      monitorexit;
      return;
    }
    this.emitting = true;
    monitorexit;
    label230: 
    while (true)
    {
      FastList localFastList1;
      try
      {
        this.actual.onNext(paramT);
        monitorenter;
        try
        {
          localFastList1 = this.queue;
          if (localFastList1 == null)
          {
            this.emitting = false;
            return;
          }
        }
        finally
        {
          monitorexit;
        }
      }
      catch (Throwable localThrowable1)
      {
        this.terminated = true;
        Exceptions.throwOrReport(localThrowable1, this.actual, paramT);
        return;
      }
      this.queue = null;
      monitorexit;
      Object[] arrayOfObject = localFastList1.array;
      int i = arrayOfObject.length;
      for (int j = 0; ; j++)
      {
        if (j >= i)
          break label230;
        Object localObject3 = arrayOfObject[j];
        if (localObject3 == null)
          break;
        try
        {
          if (!this.nl.accept(this.actual, localObject3))
            continue;
          this.terminated = true;
          return;
        }
        catch (Throwable localThrowable2)
        {
          this.terminated = true;
          Exceptions.throwIfFatal(localThrowable2);
          this.actual.onError(OnErrorThrowable.addValueAsLastCause(localThrowable2, paramT));
          return;
        }
      }
    }
  }

  static final class FastList
  {
    Object[] array;
    int size;

    public void add(Object paramObject)
    {
      int i = this.size;
      Object localObject = this.array;
      if (localObject == null)
      {
        localObject = new Object[16];
        this.array = ((Object)localObject);
      }
      while (true)
      {
        localObject[i] = paramObject;
        this.size = (i + 1);
        return;
        if (i != localObject.length)
          continue;
        Object[] arrayOfObject = new Object[i + (i >> 2)];
        System.arraycopy(localObject, 0, arrayOfObject, 0, i);
        localObject = arrayOfObject;
        this.array = ((Object)localObject);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.observers.SerializedObserver
 * JD-Core Version:    0.6.0
 */