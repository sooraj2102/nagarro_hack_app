package rx.internal.util;

import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

public final class ActionSubscriber<T> extends Subscriber<T>
{
  final Action0 onCompleted;
  final Action1<Throwable> onError;
  final Action1<? super T> onNext;

  public ActionSubscriber(Action1<? super T> paramAction1, Action1<Throwable> paramAction11, Action0 paramAction0)
  {
    this.onNext = paramAction1;
    this.onError = paramAction11;
    this.onCompleted = paramAction0;
  }

  public void onCompleted()
  {
    this.onCompleted.call();
  }

  public void onError(Throwable paramThrowable)
  {
    this.onError.call(paramThrowable);
  }

  public void onNext(T paramT)
  {
    this.onNext.call(paramT);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.ActionSubscriber
 * JD-Core Version:    0.6.0
 */