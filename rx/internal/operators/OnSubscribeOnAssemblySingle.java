package rx.internal.operators;

import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.AssemblyStackTraceException;

public final class OnSubscribeOnAssemblySingle<T>
  implements Single.OnSubscribe<T>
{
  public static volatile boolean fullStackTrace;
  final Single.OnSubscribe<T> source;
  final String stacktrace;

  public OnSubscribeOnAssemblySingle(Single.OnSubscribe<T> paramOnSubscribe)
  {
    this.source = paramOnSubscribe;
    this.stacktrace = OnSubscribeOnAssembly.createStacktrace();
  }

  public void call(SingleSubscriber<? super T> paramSingleSubscriber)
  {
    this.source.call(new OnAssemblySingleSubscriber(paramSingleSubscriber, this.stacktrace));
  }

  static final class OnAssemblySingleSubscriber<T> extends SingleSubscriber<T>
  {
    final SingleSubscriber<? super T> actual;
    final String stacktrace;

    public OnAssemblySingleSubscriber(SingleSubscriber<? super T> paramSingleSubscriber, String paramString)
    {
      this.actual = paramSingleSubscriber;
      this.stacktrace = paramString;
      paramSingleSubscriber.add(this);
    }

    public void onError(Throwable paramThrowable)
    {
      new AssemblyStackTraceException(this.stacktrace).attachTo(paramThrowable);
      this.actual.onError(paramThrowable);
    }

    public void onSuccess(T paramT)
    {
      this.actual.onSuccess(paramT);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeOnAssemblySingle
 * JD-Core Version:    0.6.0
 */