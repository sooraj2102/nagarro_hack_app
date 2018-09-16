package rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import rx.AsyncEmitter.Cancellable;
import rx.Completable.OnSubscribe;
import rx.CompletableEmitter;
import rx.CompletableSubscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.internal.subscriptions.SequentialSubscription;
import rx.plugins.RxJavaHooks;

public final class CompletableFromEmitter
  implements Completable.OnSubscribe
{
  final Action1<CompletableEmitter> producer;

  public CompletableFromEmitter(Action1<CompletableEmitter> paramAction1)
  {
    this.producer = paramAction1;
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    FromEmitter localFromEmitter = new FromEmitter(paramCompletableSubscriber);
    paramCompletableSubscriber.onSubscribe(localFromEmitter);
    try
    {
      this.producer.call(localFromEmitter);
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwIfFatal(localThrowable);
      localFromEmitter.onError(localThrowable);
    }
  }

  static final class FromEmitter extends AtomicBoolean
    implements CompletableEmitter, Subscription
  {
    private static final long serialVersionUID = 5539301318568668881L;
    final CompletableSubscriber actual;
    final SequentialSubscription resource;

    public FromEmitter(CompletableSubscriber paramCompletableSubscriber)
    {
      this.actual = paramCompletableSubscriber;
      this.resource = new SequentialSubscription();
    }

    public boolean isUnsubscribed()
    {
      return get();
    }

    public void onCompleted()
    {
      if (compareAndSet(false, true));
      try
      {
        this.actual.onCompleted();
        return;
      }
      finally
      {
        this.resource.unsubscribe();
      }
      throw localObject;
    }

    public void onError(Throwable paramThrowable)
    {
      if (compareAndSet(false, true))
        try
        {
          this.actual.onError(paramThrowable);
          return;
        }
        finally
        {
          this.resource.unsubscribe();
        }
      RxJavaHooks.onError(paramThrowable);
    }

    public void setCancellation(AsyncEmitter.Cancellable paramCancellable)
    {
      setSubscription(new OnSubscribeFromEmitter.CancellableSubscription(paramCancellable));
    }

    public void setSubscription(Subscription paramSubscription)
    {
      this.resource.update(paramSubscription);
    }

    public void unsubscribe()
    {
      if (compareAndSet(false, true))
        this.resource.unsubscribe();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableFromEmitter
 * JD-Core Version:    0.6.0
 */