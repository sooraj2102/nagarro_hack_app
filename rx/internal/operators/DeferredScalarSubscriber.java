package rx.internal.operators;

import java.util.concurrent.atomic.AtomicInteger;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;

public abstract class DeferredScalarSubscriber<T, R> extends Subscriber<T>
{
  static final int HAS_REQUEST_HAS_VALUE = 3;
  static final int HAS_REQUEST_NO_VALUE = 1;
  static final int NO_REQUEST_HAS_VALUE = 2;
  static final int NO_REQUEST_NO_VALUE;
  protected final Subscriber<? super R> actual;
  protected boolean hasValue;
  final AtomicInteger state;
  protected R value;

  public DeferredScalarSubscriber(Subscriber<? super R> paramSubscriber)
  {
    this.actual = paramSubscriber;
    this.state = new AtomicInteger();
  }

  protected final void complete()
  {
    this.actual.onCompleted();
  }

  protected final void complete(R paramR)
  {
    Subscriber localSubscriber = this.actual;
    do
    {
      int i = this.state.get();
      if ((i == 2) || (i == 3) || (localSubscriber.isUnsubscribed()))
        return;
      if (i == 1)
      {
        localSubscriber.onNext(paramR);
        if (!localSubscriber.isUnsubscribed())
          localSubscriber.onCompleted();
        this.state.lazySet(3);
        return;
      }
      this.value = paramR;
    }
    while (!this.state.compareAndSet(0, 2));
  }

  final void downstreamRequest(long paramLong)
  {
    if (paramLong < 0L)
      throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
    Subscriber localSubscriber;
    if (paramLong != 0L)
      localSubscriber = this.actual;
    do
    {
      int i = this.state.get();
      if ((i == 1) || (i == 3) || (localSubscriber.isUnsubscribed()));
      while (true)
      {
        return;
        if (i != 2)
          break;
        if (!this.state.compareAndSet(2, 3))
          continue;
        localSubscriber.onNext(this.value);
        if (localSubscriber.isUnsubscribed())
          continue;
        localSubscriber.onCompleted();
        return;
      }
    }
    while (!this.state.compareAndSet(0, 1));
  }

  public void onCompleted()
  {
    if (this.hasValue)
    {
      complete(this.value);
      return;
    }
    complete();
  }

  public void onError(Throwable paramThrowable)
  {
    this.value = null;
    this.actual.onError(paramThrowable);
  }

  public final void setProducer(Producer paramProducer)
  {
    paramProducer.request(9223372036854775807L);
  }

  final void setupDownstream()
  {
    Subscriber localSubscriber = this.actual;
    localSubscriber.add(this);
    localSubscriber.setProducer(new InnerProducer(this));
  }

  public final void subscribeTo(Observable<? extends T> paramObservable)
  {
    setupDownstream();
    paramObservable.unsafeSubscribe(this);
  }

  static final class InnerProducer
    implements Producer
  {
    final DeferredScalarSubscriber<?, ?> parent;

    public InnerProducer(DeferredScalarSubscriber<?, ?> paramDeferredScalarSubscriber)
    {
      this.parent = paramDeferredScalarSubscriber;
    }

    public void request(long paramLong)
    {
      this.parent.downstreamRequest(paramLong);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.DeferredScalarSubscriber
 * JD-Core Version:    0.6.0
 */