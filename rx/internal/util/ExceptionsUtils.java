package rx.internal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import rx.exceptions.CompositeException;

public enum ExceptionsUtils
{
  private static final Throwable TERMINATED = new Throwable("Terminated");

  public static boolean addThrowable(AtomicReference<Throwable> paramAtomicReference, Throwable paramThrowable)
  {
    Throwable localThrowable = (Throwable)paramAtomicReference.get();
    if (localThrowable == TERMINATED)
      return false;
    Object localObject;
    if (localThrowable == null)
      localObject = paramThrowable;
    while (paramAtomicReference.compareAndSet(localThrowable, localObject))
    {
      return true;
      if ((localThrowable instanceof CompositeException))
      {
        ArrayList localArrayList = new ArrayList(((CompositeException)localThrowable).getExceptions());
        localArrayList.add(paramThrowable);
        localObject = new CompositeException(localArrayList);
        continue;
      }
      localObject = new CompositeException(new Throwable[] { localThrowable, paramThrowable });
    }
  }

  public static boolean isTerminated(Throwable paramThrowable)
  {
    return paramThrowable == TERMINATED;
  }

  public static boolean isTerminated(AtomicReference<Throwable> paramAtomicReference)
  {
    return isTerminated((Throwable)paramAtomicReference.get());
  }

  public static Throwable terminate(AtomicReference<Throwable> paramAtomicReference)
  {
    Throwable localThrowable = (Throwable)paramAtomicReference.get();
    if (localThrowable != TERMINATED)
      localThrowable = (Throwable)paramAtomicReference.getAndSet(TERMINATED);
    return localThrowable;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.ExceptionsUtils
 * JD-Core Version:    0.6.0
 */