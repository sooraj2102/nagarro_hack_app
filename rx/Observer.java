package rx;

public abstract interface Observer<T>
{
  public abstract void onCompleted();

  public abstract void onError(Throwable paramThrowable);

  public abstract void onNext(T paramT);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.Observer
 * JD-Core Version:    0.6.0
 */