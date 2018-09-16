package rx.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import rx.annotations.Beta;

public final class CompositeException extends RuntimeException
{
  private static final long serialVersionUID = 3026362227162912146L;
  private Throwable cause;
  private final List<Throwable> exceptions;
  private final String message;

  @Deprecated
  public CompositeException(String paramString, Collection<? extends Throwable> paramCollection)
  {
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    ArrayList localArrayList = new ArrayList();
    if (paramCollection != null)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Throwable localThrowable = (Throwable)localIterator.next();
        if ((localThrowable instanceof CompositeException))
        {
          localLinkedHashSet.addAll(((CompositeException)localThrowable).getExceptions());
          continue;
        }
        if (localThrowable != null)
        {
          localLinkedHashSet.add(localThrowable);
          continue;
        }
        localLinkedHashSet.add(new NullPointerException());
      }
    }
    localLinkedHashSet.add(new NullPointerException());
    localArrayList.addAll(localLinkedHashSet);
    this.exceptions = Collections.unmodifiableList(localArrayList);
    this.message = (this.exceptions.size() + " exceptions occurred. ");
  }

  public CompositeException(Collection<? extends Throwable> paramCollection)
  {
    this(null, paramCollection);
  }

  @Beta
  public CompositeException(Throwable[] paramArrayOfThrowable)
  {
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    ArrayList localArrayList = new ArrayList();
    if (paramArrayOfThrowable != null)
    {
      int i = paramArrayOfThrowable.length;
      int j = 0;
      if (j < i)
      {
        Throwable localThrowable = paramArrayOfThrowable[j];
        if ((localThrowable instanceof CompositeException))
          localLinkedHashSet.addAll(((CompositeException)localThrowable).getExceptions());
        while (true)
        {
          j++;
          break;
          if (localThrowable != null)
          {
            localLinkedHashSet.add(localThrowable);
            continue;
          }
          localLinkedHashSet.add(new NullPointerException());
        }
      }
    }
    else
    {
      localLinkedHashSet.add(new NullPointerException());
    }
    localArrayList.addAll(localLinkedHashSet);
    this.exceptions = Collections.unmodifiableList(localArrayList);
    this.message = (this.exceptions.size() + " exceptions occurred. ");
  }

  private void appendStackTrace(StringBuilder paramStringBuilder, Throwable paramThrowable, String paramString)
  {
    paramStringBuilder.append(paramString).append(paramThrowable).append('\n');
    for (StackTraceElement localStackTraceElement : paramThrowable.getStackTrace())
      paramStringBuilder.append("\t\tat ").append(localStackTraceElement).append('\n');
    if (paramThrowable.getCause() != null)
    {
      paramStringBuilder.append("\tCaused by: ");
      appendStackTrace(paramStringBuilder, paramThrowable.getCause(), "");
    }
  }

  private List<Throwable> getListOfCauses(Throwable paramThrowable)
  {
    ArrayList localArrayList = new ArrayList();
    Throwable localThrowable1 = paramThrowable.getCause();
    if ((localThrowable1 == null) || (localThrowable1 == paramThrowable))
      return localArrayList;
    Throwable localThrowable2;
    do
    {
      localThrowable1 = localThrowable1.getCause();
      localArrayList.add(localThrowable1);
      localThrowable2 = localThrowable1.getCause();
      if (localThrowable2 == null)
        break;
    }
    while (localThrowable2 != localThrowable1);
    return localArrayList;
  }

  private Throwable getRootCause(Throwable paramThrowable)
  {
    Throwable localThrowable1 = paramThrowable.getCause();
    if ((localThrowable1 == null) || (localThrowable1 == paramThrowable))
      return paramThrowable;
    Throwable localThrowable2;
    do
    {
      localThrowable1 = localThrowable1.getCause();
      localThrowable2 = localThrowable1.getCause();
    }
    while ((localThrowable2 != null) && (localThrowable2 != localThrowable1));
    return localThrowable1;
  }

  private void printStackTrace(PrintStreamOrWriter paramPrintStreamOrWriter)
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append(this).append('\n');
    for (StackTraceElement localStackTraceElement : getStackTrace())
      localStringBuilder.append("\tat ").append(localStackTraceElement).append('\n');
    int k = 1;
    Iterator localIterator = this.exceptions.iterator();
    while (localIterator.hasNext())
    {
      Throwable localThrowable = (Throwable)localIterator.next();
      localStringBuilder.append("  ComposedException ").append(k).append(" :\n");
      appendStackTrace(localStringBuilder, localThrowable, "\t");
      k++;
    }
    synchronized (paramPrintStreamOrWriter.lock())
    {
      paramPrintStreamOrWriter.println(localStringBuilder.toString());
      return;
    }
  }

  public Throwable getCause()
  {
    monitorenter;
    while (true)
    {
      CompositeExceptionCausalChain localCompositeExceptionCausalChain;
      Object localObject2;
      Object localObject3;
      try
      {
        if (this.cause == null)
        {
          localCompositeExceptionCausalChain = new CompositeExceptionCausalChain();
          localHashSet = new HashSet();
          localObject2 = localCompositeExceptionCausalChain;
          Iterator localIterator1 = this.exceptions.iterator();
          if (localIterator1.hasNext())
          {
            localObject3 = (Throwable)localIterator1.next();
            if (localHashSet.contains(localObject3))
              continue;
            localHashSet.add(localObject3);
            Iterator localIterator2 = getListOfCauses((Throwable)localObject3).iterator();
            if (localIterator2.hasNext())
            {
              Throwable localThrowable3 = (Throwable)localIterator2.next();
              if (!localHashSet.contains(localThrowable3))
                continue;
              localObject3 = new RuntimeException("Duplicate found in causal chain so cropping to prevent loop ...");
              continue;
              localHashSet.add(localThrowable3);
              continue;
            }
          }
        }
      }
      finally
      {
        HashSet localHashSet;
        monitorexit;
      }
      try
      {
        ((Throwable)localObject2).initCause((Throwable)localObject3);
        label166: localObject2 = getRootCause((Throwable)localObject2);
        continue;
        this.cause = localCompositeExceptionCausalChain;
        Throwable localThrowable1 = this.cause;
        monitorexit;
        return localThrowable1;
      }
      catch (Throwable localThrowable2)
      {
        break label166;
      }
    }
  }

  public List<Throwable> getExceptions()
  {
    return this.exceptions;
  }

  public String getMessage()
  {
    return this.message;
  }

  public void printStackTrace()
  {
    printStackTrace(System.err);
  }

  public void printStackTrace(PrintStream paramPrintStream)
  {
    printStackTrace(new WrappedPrintStream(paramPrintStream));
  }

  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    printStackTrace(new WrappedPrintWriter(paramPrintWriter));
  }

  static final class CompositeExceptionCausalChain extends RuntimeException
  {
    static final String MESSAGE = "Chain of Causes for CompositeException In Order Received =>";
    private static final long serialVersionUID = 3875212506787802066L;

    public String getMessage()
    {
      return "Chain of Causes for CompositeException In Order Received =>";
    }
  }

  static abstract class PrintStreamOrWriter
  {
    abstract Object lock();

    abstract void println(Object paramObject);
  }

  static final class WrappedPrintStream extends CompositeException.PrintStreamOrWriter
  {
    private final PrintStream printStream;

    WrappedPrintStream(PrintStream paramPrintStream)
    {
      this.printStream = paramPrintStream;
    }

    Object lock()
    {
      return this.printStream;
    }

    void println(Object paramObject)
    {
      this.printStream.println(paramObject);
    }
  }

  static final class WrappedPrintWriter extends CompositeException.PrintStreamOrWriter
  {
    private final PrintWriter printWriter;

    WrappedPrintWriter(PrintWriter paramPrintWriter)
    {
      this.printWriter = paramPrintWriter;
    }

    Object lock()
    {
      return this.printWriter;
    }

    void println(Object paramObject)
    {
      this.printWriter.println(paramObject);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.exceptions.CompositeException
 * JD-Core Version:    0.6.0
 */