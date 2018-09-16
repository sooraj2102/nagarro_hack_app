package rx.exceptions;

import java.util.HashSet;
import java.util.Set;
import rx.annotations.Experimental;
import rx.plugins.RxJavaHooks;

@Experimental
public final class AssemblyStackTraceException extends RuntimeException
{
  private static final long serialVersionUID = 2038859767182585852L;

  public AssemblyStackTraceException(String paramString)
  {
    super(paramString);
  }

  public static AssemblyStackTraceException find(Throwable paramThrowable)
  {
    HashSet localHashSet = new HashSet();
    do
    {
      if ((paramThrowable instanceof AssemblyStackTraceException))
        return (AssemblyStackTraceException)paramThrowable;
      if ((paramThrowable == null) || (paramThrowable.getCause() == null))
        return null;
      paramThrowable = paramThrowable.getCause();
    }
    while (localHashSet.add(paramThrowable));
    return null;
  }

  public void attachTo(Throwable paramThrowable)
  {
    HashSet localHashSet = new HashSet();
    do
    {
      if (paramThrowable.getCause() == null)
      {
        paramThrowable.initCause(this);
        return;
      }
      paramThrowable = paramThrowable.getCause();
    }
    while (localHashSet.add(paramThrowable));
    RxJavaHooks.onError(this);
  }

  public Throwable fillInStackTrace()
  {
    monitorenter;
    monitorexit;
    return this;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.exceptions.AssemblyStackTraceException
 * JD-Core Version:    0.6.0
 */