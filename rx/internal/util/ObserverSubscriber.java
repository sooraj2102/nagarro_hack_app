package rx.internal.util;

import rx.Observer;
import rx.Subscriber;

public final class ObserverSubscriber<T> extends Subscriber<T>
{
  final Observer<? super T> observer;

  public ObserverSubscriber(Observer<? super T> paramObserver)
  {
    this.observer = paramObserver;
  }

  public void onCompleted()
  {
    this.observer.onCompleted();
  }

  public void onError(Throwable paramThrowable)
  {
    this.observer.onError(paramThrowable);
  }

  public void onNext(T paramT)
  {
    this.observer.onNext(paramT);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.ObserverSubscriber
 * JD-Core Version:    0.6.0
 */