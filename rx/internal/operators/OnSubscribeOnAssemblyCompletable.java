package rx.internal.operators;

import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Subscription;
import rx.exceptions.AssemblyStackTraceException;

public final class OnSubscribeOnAssemblyCompletable<T>
  implements Completable.OnSubscribe
{
  public static volatile boolean fullStackTrace;
  final Completable.OnSubscribe source;
  final String stacktrace;

  public OnSubscribeOnAssemblyCompletable(Completable.OnSubscribe paramOnSubscribe)
  {
    this.source = paramOnSubscribe;
    this.stacktrace = OnSubscribeOnAssembly.createStacktrace();
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    this.source.call(new OnAssemblyCompletableSubscriber(paramCompletableSubscriber, this.stacktrace));
  }

  static final class OnAssemblyCompletableSubscriber
    implements CompletableSubscriber
  {
    final CompletableSubscriber actual;
    final String stacktrace;

    public OnAssemblyCompletableSubscriber(CompletableSubscriber paramCompletableSubscriber, String paramString)
    {
      this.actual = paramCompletableSubscriber;
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

    public void onSubscribe(Subscription paramSubscription)
    {
      this.actual.onSubscribe(paramSubscription);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeOnAssemblyCompletable
 * JD-Core Version:    0.6.0
 */