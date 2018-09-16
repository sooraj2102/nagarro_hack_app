package rx.observers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Notification;
import rx.Observer;
import rx.exceptions.CompositeException;

@Deprecated
public class TestObserver<T>
  implements Observer<T>
{
  private static final Observer<Object> INERT = new Observer()
  {
    public void onCompleted()
    {
    }

    public void onError(Throwable paramThrowable)
    {
    }

    public void onNext(Object paramObject)
    {
    }
  };
  private final Observer<T> delegate;
  private final List<Notification<T>> onCompletedEvents = new ArrayList();
  private final List<Throwable> onErrorEvents = new ArrayList();
  private final List<T> onNextEvents = new ArrayList();

  public TestObserver()
  {
    this.delegate = INERT;
  }

  public TestObserver(Observer<T> paramObserver)
  {
    this.delegate = paramObserver;
  }

  public void assertReceivedOnNext(List<T> paramList)
  {
    if (this.onNextEvents.size() != paramList.size())
      assertionError("Number of items does not match. Provided: " + paramList.size() + "  Actual: " + this.onNextEvents.size() + ".\n" + "Provided values: " + paramList + "\n" + "Actual values: " + this.onNextEvents + "\n");
    int i = 0;
    if (i < paramList.size())
    {
      Object localObject1 = paramList.get(i);
      Object localObject2 = this.onNextEvents.get(i);
      if (localObject1 == null)
        if (localObject2 != null)
          assertionError("Value at index: " + i + " expected to be [null] but was: [" + localObject2 + "]\n");
      do
      {
        i++;
        break;
      }
      while (localObject1.equals(localObject2));
      StringBuilder localStringBuilder = new StringBuilder().append("Value at index: ").append(i).append(" expected to be [").append(localObject1).append("] (").append(localObject1.getClass().getSimpleName()).append(") but was: [").append(localObject2).append("] (");
      if (localObject2 != null);
      for (String str = localObject2.getClass().getSimpleName(); ; str = "null")
      {
        assertionError(str + ")\n");
        break;
      }
    }
  }

  public void assertTerminalEvent()
  {
    if (this.onErrorEvents.size() > 1)
      assertionError("Too many onError events: " + this.onErrorEvents.size());
    if (this.onCompletedEvents.size() > 1)
      assertionError("Too many onCompleted events: " + this.onCompletedEvents.size());
    if ((this.onCompletedEvents.size() == 1) && (this.onErrorEvents.size() == 1))
      assertionError("Received both an onError and onCompleted. Should be one or the other.");
    if ((this.onCompletedEvents.isEmpty()) && (this.onErrorEvents.isEmpty()))
      assertionError("No terminal events received.");
  }

  final void assertionError(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder(32 + paramString.length());
    localStringBuilder.append(paramString).append(" (");
    int i = this.onCompletedEvents.size();
    localStringBuilder.append(i).append(" completion");
    if (i != 1)
      localStringBuilder.append('s');
    localStringBuilder.append(')');
    if (!this.onErrorEvents.isEmpty())
    {
      int j = this.onErrorEvents.size();
      localStringBuilder.append(" (+").append(j).append(" error");
      if (j != 1)
        localStringBuilder.append('s');
      localStringBuilder.append(')');
    }
    AssertionError localAssertionError = new AssertionError(localStringBuilder.toString());
    if (!this.onErrorEvents.isEmpty())
    {
      if (this.onErrorEvents.size() != 1)
        break label189;
      localAssertionError.initCause((Throwable)this.onErrorEvents.get(0));
    }
    while (true)
    {
      throw localAssertionError;
      label189: localAssertionError.initCause(new CompositeException(this.onErrorEvents));
    }
  }

  public List<Object> getEvents()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(this.onNextEvents);
    localArrayList.add(this.onErrorEvents);
    localArrayList.add(this.onCompletedEvents);
    return Collections.unmodifiableList(localArrayList);
  }

  public List<Notification<T>> getOnCompletedEvents()
  {
    return Collections.unmodifiableList(this.onCompletedEvents);
  }

  public List<Throwable> getOnErrorEvents()
  {
    return Collections.unmodifiableList(this.onErrorEvents);
  }

  public List<T> getOnNextEvents()
  {
    return Collections.unmodifiableList(this.onNextEvents);
  }

  public void onCompleted()
  {
    this.onCompletedEvents.add(Notification.createOnCompleted());
    this.delegate.onCompleted();
  }

  public void onError(Throwable paramThrowable)
  {
    this.onErrorEvents.add(paramThrowable);
    this.delegate.onError(paramThrowable);
  }

  public void onNext(T paramT)
  {
    this.onNextEvents.add(paramT);
    this.delegate.onNext(paramT);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.observers.TestObserver
 * JD-Core Version:    0.6.0
 */