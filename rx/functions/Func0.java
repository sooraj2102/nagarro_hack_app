package rx.functions;

import java.util.concurrent.Callable;

public abstract interface Func0<R> extends Function, Callable<R>
{
  public abstract R call();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.functions.Func0
 * JD-Core Version:    0.6.0
 */