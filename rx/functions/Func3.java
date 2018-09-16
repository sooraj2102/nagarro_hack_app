package rx.functions;

public abstract interface Func3<T1, T2, T3, R> extends Function
{
  public abstract R call(T1 paramT1, T2 paramT2, T3 paramT3);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.functions.Func3
 * JD-Core Version:    0.6.0
 */