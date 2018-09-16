package rx.internal.util;

import rx.Notification;
import rx.Observer;
import rx.functions.Action1;

public final class ActionNotificationObserver<T>
  implements Observer<T>
{
  final Action1<Notification<? super T>> onNotification;

  public ActionNotificationObserver(Action1<Notification<? super T>> paramAction1)
  {
    this.onNotification = paramAction1;
  }

  public void onCompleted()
  {
    this.onNotification.call(Notification.createOnCompleted());
  }

  public void onError(Throwable paramThrowable)
  {
    this.onNotification.call(Notification.createOnError(paramThrowable));
  }

  public void onNext(T paramT)
  {
    this.onNotification.call(Notification.createOnNext(paramT));
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.ActionNotificationObserver
 * JD-Core Version:    0.6.0
 */