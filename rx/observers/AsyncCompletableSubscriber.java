package rx.observers;

import java.util.concurrent.atomic.AtomicReference;
import rx.CompletableSubscriber;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.plugins.RxJavaHooks;

@Experimental
public abstract class AsyncCompletableSubscriber
  implements CompletableSubscriber, Subscription
{
  static final Unsubscribed UNSUBSCRIBED = new Unsubscribed();
  private final AtomicReference<Subscription> upstream = new AtomicReference();

  protected final void clear()
  {
    this.upstream.set(UNSUBSCRIBED);
  }

  public final boolean isUnsubscribed()
  {
    return this.upstream.get() == UNSUBSCRIBED;
  }

  protected void onStart()
  {
  }

  public final void onSubscribe(Subscription paramSubscription)
  {
    if (!this.upstream.compareAndSet(null, paramSubscription))
    {
      paramSubscription.unsubscribe();
      if (this.upstream.get() != UNSUBSCRIBED)
        RxJavaHooks.onError(new IllegalStateException("Subscription already set!"));
      return;
    }
    onStart();
  }

  public final void unsubscribe()
  {
    if ((Subscription)this.upstream.get() != UNSUBSCRIBED)
    {
      Subscription localSubscription = (Subscription)this.upstream.getAndSet(UNSUBSCRIBED);
      if ((localSubscription != null) && (localSubscription != UNSUBSCRIBED))
        localSubscription.unsubscribe();
    }
  }

  static final class Unsubscribed
    implements Subscription
  {
    public boolean isUnsubscribed()
    {
      return true;
    }

    public void unsubscribe()
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.observers.AsyncCompletableSubscriber
 * JD-Core Version:    0.6.0
 */