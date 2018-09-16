package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.plugins.RxJavaHooks;

public final class OnSubscribeDetach<T>
  implements Observable.OnSubscribe<T>
{
  final Observable<T> source;

  public OnSubscribeDetach(Observable<T> paramObservable)
  {
    this.source = paramObservable;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    DetachSubscriber localDetachSubscriber = new DetachSubscriber(paramSubscriber);
    DetachProducer localDetachProducer = new DetachProducer(localDetachSubscriber);
    paramSubscriber.add(localDetachProducer);
    paramSubscriber.setProducer(localDetachProducer);
    this.source.unsafeSubscribe(localDetachSubscriber);
  }

  static final class DetachProducer<T>
    implements Producer, Subscription
  {
    final OnSubscribeDetach.DetachSubscriber<T> parent;

    public DetachProducer(OnSubscribeDetach.DetachSubscriber<T> paramDetachSubscriber)
    {
      this.parent = paramDetachSubscriber;
    }

    public boolean isUnsubscribed()
    {
      return this.parent.isUnsubscribed();
    }

    public void request(long paramLong)
    {
      this.parent.innerRequest(paramLong);
    }

    public void unsubscribe()
    {
      this.parent.innerUnsubscribe();
    }
  }

  static final class DetachSubscriber<T> extends Subscriber<T>
  {
    final AtomicReference<Subscriber<? super T>> actual;
    final AtomicReference<Producer> producer;
    final AtomicLong requested;

    public DetachSubscriber(Subscriber<? super T> paramSubscriber)
    {
      this.actual = new AtomicReference(paramSubscriber);
      this.producer = new AtomicReference();
      this.requested = new AtomicLong();
    }

    void innerRequest(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
      Producer localProducer1 = (Producer)this.producer.get();
      if (localProducer1 != null)
        localProducer1.request(paramLong);
      Producer localProducer2;
      do
      {
        return;
        BackpressureUtils.getAndAddRequest(this.requested, paramLong);
        localProducer2 = (Producer)this.producer.get();
      }
      while ((localProducer2 == null) || (localProducer2 == OnSubscribeDetach.TerminatedProducer.INSTANCE));
      localProducer2.request(this.requested.getAndSet(0L));
    }

    void innerUnsubscribe()
    {
      this.producer.lazySet(OnSubscribeDetach.TerminatedProducer.INSTANCE);
      this.actual.lazySet(null);
      unsubscribe();
    }

    public void onCompleted()
    {
      this.producer.lazySet(OnSubscribeDetach.TerminatedProducer.INSTANCE);
      Subscriber localSubscriber = (Subscriber)this.actual.getAndSet(null);
      if (localSubscriber != null)
        localSubscriber.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      this.producer.lazySet(OnSubscribeDetach.TerminatedProducer.INSTANCE);
      Subscriber localSubscriber = (Subscriber)this.actual.getAndSet(null);
      if (localSubscriber != null)
      {
        localSubscriber.onError(paramThrowable);
        return;
      }
      RxJavaHooks.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      Subscriber localSubscriber = (Subscriber)this.actual.get();
      if (localSubscriber != null)
        localSubscriber.onNext(paramT);
    }

    public void setProducer(Producer paramProducer)
    {
      if (this.producer.compareAndSet(null, paramProducer))
        paramProducer.request(this.requested.getAndSet(0L));
      do
        return;
      while (this.producer.get() == OnSubscribeDetach.TerminatedProducer.INSTANCE);
      throw new IllegalStateException("Producer already set!");
    }
  }

  static enum TerminatedProducer
    implements Producer
  {
    static
    {
      TerminatedProducer[] arrayOfTerminatedProducer = new TerminatedProducer[1];
      arrayOfTerminatedProducer[0] = INSTANCE;
      $VALUES = arrayOfTerminatedProducer;
    }

    public void request(long paramLong)
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeDetach
 * JD-Core Version:    0.6.0
 */