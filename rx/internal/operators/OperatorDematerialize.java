package rx.internal.operators;

import rx.Notification;
import rx.Observable.Operator;
import rx.Subscriber;

public final class OperatorDematerialize<T>
  implements Observable.Operator<T, Notification<T>>
{
  public static OperatorDematerialize instance()
  {
    return Holder.INSTANCE;
  }

  public Subscriber<? super Notification<T>> call(Subscriber<? super T> paramSubscriber)
  {
    return new Subscriber(paramSubscriber, paramSubscriber)
    {
      boolean terminated;

      public void onCompleted()
      {
        if (!this.terminated)
        {
          this.terminated = true;
          this.val$child.onCompleted();
        }
      }

      public void onError(Throwable paramThrowable)
      {
        if (!this.terminated)
        {
          this.terminated = true;
          this.val$child.onError(paramThrowable);
        }
      }

      public void onNext(Notification<T> paramNotification)
      {
        switch (OperatorDematerialize.2.$SwitchMap$rx$Notification$Kind[paramNotification.getKind().ordinal()])
        {
        default:
          onError(new IllegalArgumentException("Unsupported notification type: " + paramNotification));
        case 1:
          do
            return;
          while (this.terminated);
          this.val$child.onNext(paramNotification.getValue());
          return;
        case 2:
          onError(paramNotification.getThrowable());
          return;
        case 3:
        }
        onCompleted();
      }
    };
  }

  static final class Holder
  {
    static final OperatorDematerialize<Object> INSTANCE = new OperatorDematerialize();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorDematerialize
 * JD-Core Version:    0.6.0
 */