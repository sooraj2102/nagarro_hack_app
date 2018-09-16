package rx.internal.operators;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.AssemblyStackTraceException;

public final class OnSubscribeOnAssembly<T>
  implements Observable.OnSubscribe<T>
{
  public static volatile boolean fullStackTrace;
  final Observable.OnSubscribe<T> source;
  final String stacktrace;

  public OnSubscribeOnAssembly(Observable.OnSubscribe<T> paramOnSubscribe)
  {
    this.source = paramOnSubscribe;
    this.stacktrace = createStacktrace();
  }

  static String createStacktrace()
  {
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    StringBuilder localStringBuilder = new StringBuilder("Assembly trace:");
    int i = arrayOfStackTraceElement.length;
    int j = 0;
    if (j < i)
    {
      StackTraceElement localStackTraceElement = arrayOfStackTraceElement[j];
      String str = localStackTraceElement.toString();
      if (!fullStackTrace)
        if (localStackTraceElement.getLineNumber() > 1);
      while (true)
      {
        j++;
        break;
        if ((str.contains("RxJavaHooks.")) || (str.contains("OnSubscribeOnAssembly")) || (str.contains(".junit.runner")) || (str.contains(".junit4.runner")) || (str.contains(".junit.internal")) || (str.contains("sun.reflect")) || (str.contains("java.lang.Thread.")) || (str.contains("ThreadPoolExecutor")) || (str.contains("org.apache.catalina.")) || (str.contains("org.apache.tomcat.")))
          continue;
        localStringBuilder.append("\n at ").append(str);
      }
    }
    return "\nOriginal exception:";
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    this.source.call(new OnAssemblySubscriber(paramSubscriber, this.stacktrace));
  }

  static final class OnAssemblySubscriber<T> extends Subscriber<T>
  {
    final Subscriber<? super T> actual;
    final String stacktrace;

    public OnAssemblySubscriber(Subscriber<? super T> paramSubscriber, String paramString)
    {
      super();
      this.actual = paramSubscriber;
      this.stacktrace = paramString;
    }

    public void onCompleted()
    {
      this.actual.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      new AssemblyStackTraceException(this.stacktrace).attachTo(paramThrowable);
      this.actual.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      this.actual.onNext(paramT);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeOnAssembly
 * JD-Core Version:    0.6.0
 */