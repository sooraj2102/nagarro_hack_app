package rx.observers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import rx.Notification;
import rx.Observer;
import rx.Subscriber;
import rx.annotations.Experimental;
import rx.exceptions.CompositeException;

public class TestSubscriber<T> extends Subscriber<T>
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
  private int completions;
  private final Observer<T> delegate;
  private final List<Throwable> errors;
  private volatile Thread lastSeenThread;
  private final CountDownLatch latch = new CountDownLatch(1);
  private volatile int valueCount;
  private final List<T> values;

  public TestSubscriber()
  {
    this(-1L);
  }

  public TestSubscriber(long paramLong)
  {
    this(INERT, paramLong);
  }

  public TestSubscriber(Observer<T> paramObserver)
  {
    this(paramObserver, -1L);
  }

  public TestSubscriber(Observer<T> paramObserver, long paramLong)
  {
    if (paramObserver == null)
      throw new NullPointerException();
    this.delegate = paramObserver;
    if (paramLong >= 0L)
      request(paramLong);
    this.values = new ArrayList();
    this.errors = new ArrayList();
  }

  public TestSubscriber(Subscriber<T> paramSubscriber)
  {
    this(paramSubscriber, -1L);
  }

  private void assertItem(T paramT, int paramInt)
  {
    Object localObject = this.values.get(paramInt);
    if (paramT == null)
      if (localObject != null)
        assertionError("Value at index: " + paramInt + " expected to be [null] but was: [" + localObject + "]\n");
    do
      return;
    while (paramT.equals(localObject));
    StringBuilder localStringBuilder = new StringBuilder().append("Value at index: ").append(paramInt).append(" expected to be [").append(paramT).append("] (").append(paramT.getClass().getSimpleName()).append(") but was: [").append(localObject).append("] (");
    if (localObject != null);
    for (String str = localObject.getClass().getSimpleName(); ; str = "null")
    {
      assertionError(str + ")\n");
      return;
    }
  }

  public static <T> TestSubscriber<T> create()
  {
    return new TestSubscriber();
  }

  public static <T> TestSubscriber<T> create(long paramLong)
  {
    return new TestSubscriber(paramLong);
  }

  public static <T> TestSubscriber<T> create(Observer<T> paramObserver)
  {
    return new TestSubscriber(paramObserver);
  }

  public static <T> TestSubscriber<T> create(Observer<T> paramObserver, long paramLong)
  {
    return new TestSubscriber(paramObserver, paramLong);
  }

  public static <T> TestSubscriber<T> create(Subscriber<T> paramSubscriber)
  {
    return new TestSubscriber(paramSubscriber);
  }

  public void assertCompleted()
  {
    int i = this.completions;
    if (i == 0)
      assertionError("Not completed!");
    do
      return;
    while (i <= 1);
    assertionError("Completed multiple times: " + i);
  }

  public void assertError(Class<? extends Throwable> paramClass)
  {
    List localList = this.errors;
    if (localList.isEmpty())
      assertionError("No errors");
    do
    {
      return;
      if (localList.size() <= 1)
        continue;
      AssertionError localAssertionError1 = new AssertionError("Multiple errors: " + localList.size());
      localAssertionError1.initCause(new CompositeException(localList));
      throw localAssertionError1;
    }
    while (paramClass.isInstance(localList.get(0)));
    AssertionError localAssertionError2 = new AssertionError("Exceptions differ; expected: " + paramClass + ", actual: " + localList.get(0));
    localAssertionError2.initCause((Throwable)localList.get(0));
    throw localAssertionError2;
  }

  public void assertError(Throwable paramThrowable)
  {
    List localList = this.errors;
    if (localList.isEmpty())
      assertionError("No errors");
    do
    {
      return;
      if (localList.size() <= 1)
        continue;
      assertionError("Multiple errors");
      return;
    }
    while (paramThrowable.equals(localList.get(0)));
    assertionError("Exceptions differ; expected: " + paramThrowable + ", actual: " + localList.get(0));
  }

  public void assertNoErrors()
  {
    if (!getOnErrorEvents().isEmpty())
      assertionError("Unexpected onError events");
  }

  public void assertNoTerminalEvent()
  {
    List localList = this.errors;
    int i = this.completions;
    if ((!localList.isEmpty()) || (i > 0))
    {
      if (localList.isEmpty())
        assertionError("Found " + localList.size() + " errors and " + i + " completion events instead of none");
    }
    else
      return;
    if (localList.size() == 1)
    {
      assertionError("Found " + localList.size() + " errors and " + i + " completion events instead of none");
      return;
    }
    assertionError("Found " + localList.size() + " errors and " + i + " completion events instead of none");
  }

  public void assertNoValues()
  {
    int i = this.values.size();
    if (i != 0)
      assertionError("No onNext events expected yet some received: " + i);
  }

  public void assertNotCompleted()
  {
    int i = this.completions;
    if (i == 1)
      assertionError("Completed!");
    do
      return;
    while (i <= 1);
    assertionError("Completed multiple times: " + i);
  }

  public void assertReceivedOnNext(List<T> paramList)
  {
    if (this.values.size() != paramList.size())
      assertionError("Number of items does not match. Provided: " + paramList.size() + "  Actual: " + this.values.size() + ".\n" + "Provided values: " + paramList + "\n" + "Actual values: " + this.values + "\n");
    for (int i = 0; i < paramList.size(); i++)
      assertItem(paramList.get(i), i);
  }

  public void assertTerminalEvent()
  {
    if (this.errors.size() > 1)
      assertionError("Too many onError events: " + this.errors.size());
    if (this.completions > 1)
      assertionError("Too many onCompleted events: " + this.completions);
    if ((this.completions == 1) && (this.errors.size() == 1))
      assertionError("Received both an onError and onCompleted. Should be one or the other.");
    if ((this.completions == 0) && (this.errors.isEmpty()))
      assertionError("No terminal events received.");
  }

  public void assertUnsubscribed()
  {
    if (!isUnsubscribed())
      assertionError("Not unsubscribed.");
  }

  public void assertValue(T paramT)
  {
    assertReceivedOnNext(Collections.singletonList(paramT));
  }

  public void assertValueCount(int paramInt)
  {
    int i = this.values.size();
    if (i != paramInt)
      assertionError("Number of onNext events differ; expected: " + paramInt + ", actual: " + i);
  }

  public void assertValues(T[] paramArrayOfT)
  {
    assertReceivedOnNext(Arrays.asList(paramArrayOfT));
  }

  @Experimental
  public final void assertValuesAndClear(T paramT, T[] paramArrayOfT)
  {
    assertValueCount(1 + paramArrayOfT.length);
    assertItem(paramT, 0);
    for (int i = 0; i < paramArrayOfT.length; i++)
      assertItem(paramArrayOfT[i], i + 1);
    this.values.clear();
  }

  final void assertionError(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder(32 + paramString.length());
    localStringBuilder.append(paramString).append(" (");
    int i = this.completions;
    localStringBuilder.append(i).append(" completion");
    if (i != 1)
      localStringBuilder.append('s');
    localStringBuilder.append(')');
    if (!this.errors.isEmpty())
    {
      int j = this.errors.size();
      localStringBuilder.append(" (+").append(j).append(" error");
      if (j != 1)
        localStringBuilder.append('s');
      localStringBuilder.append(')');
    }
    AssertionError localAssertionError = new AssertionError(localStringBuilder.toString());
    if (!this.errors.isEmpty())
    {
      if (this.errors.size() != 1)
        break label188;
      localAssertionError.initCause((Throwable)this.errors.get(0));
    }
    while (true)
    {
      throw localAssertionError;
      label188: localAssertionError.initCause(new CompositeException(this.errors));
    }
  }

  public void awaitTerminalEvent()
  {
    try
    {
      this.latch.await();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    throw new IllegalStateException("Interrupted", localInterruptedException);
  }

  public void awaitTerminalEvent(long paramLong, TimeUnit paramTimeUnit)
  {
    try
    {
      this.latch.await(paramLong, paramTimeUnit);
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    throw new IllegalStateException("Interrupted", localInterruptedException);
  }

  public void awaitTerminalEventAndUnsubscribeOnTimeout(long paramLong, TimeUnit paramTimeUnit)
  {
    try
    {
      if (!this.latch.await(paramLong, paramTimeUnit))
        unsubscribe();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      unsubscribe();
    }
  }

  @Experimental
  public final boolean awaitValueCount(int paramInt, long paramLong, TimeUnit paramTimeUnit)
  {
    while ((paramLong != 0L) && (this.valueCount < paramInt))
      try
      {
        paramTimeUnit.sleep(1L);
        paramLong -= 1L;
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new IllegalStateException("Interrupted", localInterruptedException);
      }
    return this.valueCount >= paramInt;
  }

  @Experimental
  public final int getCompletions()
  {
    return this.completions;
  }

  public Thread getLastSeenThread()
  {
    return this.lastSeenThread;
  }

  @Deprecated
  public List<Notification<T>> getOnCompletedEvents()
  {
    int i = this.completions;
    if (i != 0);
    ArrayList localArrayList;
    for (int j = i; ; j = 1)
    {
      localArrayList = new ArrayList(j);
      for (int k = 0; k < i; k++)
        localArrayList.add(Notification.createOnCompleted());
    }
    return localArrayList;
  }

  public List<Throwable> getOnErrorEvents()
  {
    return this.errors;
  }

  public List<T> getOnNextEvents()
  {
    return this.values;
  }

  public final int getValueCount()
  {
    return this.valueCount;
  }

  public void onCompleted()
  {
    try
    {
      this.completions = (1 + this.completions);
      this.lastSeenThread = Thread.currentThread();
      this.delegate.onCompleted();
      return;
    }
    finally
    {
      this.latch.countDown();
    }
    throw localObject;
  }

  public void onError(Throwable paramThrowable)
  {
    try
    {
      this.lastSeenThread = Thread.currentThread();
      this.errors.add(paramThrowable);
      this.delegate.onError(paramThrowable);
      return;
    }
    finally
    {
      this.latch.countDown();
    }
    throw localObject;
  }

  public void onNext(T paramT)
  {
    this.lastSeenThread = Thread.currentThread();
    this.values.add(paramT);
    this.valueCount = this.values.size();
    this.delegate.onNext(paramT);
  }

  public void requestMore(long paramLong)
  {
    request(paramLong);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.observers.TestSubscriber
 * JD-Core Version:    0.6.0
 */