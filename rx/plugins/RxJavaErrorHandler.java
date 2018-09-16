package rx.plugins;

import rx.annotations.Beta;
import rx.exceptions.Exceptions;

public abstract class RxJavaErrorHandler
{
  protected static final String ERROR_IN_RENDERING_SUFFIX = ".errorRendering";

  @Deprecated
  public void handleError(Throwable paramThrowable)
  {
  }

  @Beta
  public final String handleOnNextValueRendering(Object paramObject)
  {
    try
    {
      String str = render(paramObject);
      return str;
    }
    catch (InterruptedException localInterruptedException)
    {
      Thread.currentThread().interrupt();
      return paramObject.getClass().getName() + ".errorRendering";
    }
    catch (Throwable localThrowable)
    {
      while (true)
        Exceptions.throwIfFatal(localThrowable);
    }
  }

  @Beta
  protected String render(Object paramObject)
    throws InterruptedException
  {
    return null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.plugins.RxJavaErrorHandler
 * JD-Core Version:    0.6.0
 */