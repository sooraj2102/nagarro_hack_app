package rx.internal.operators;

import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.Subscription;
import rx.observers.SerializedSubscriber;

public final class OperatorSampleWithObservable<T, U>
  implements Observable.Operator<T, T>
{
  static final Object EMPTY_TOKEN = new Object();
  final Observable<U> sampler;

  public OperatorSampleWithObservable(Observable<U> paramObservable)
  {
    this.sampler = paramObservable;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    SerializedSubscriber localSerializedSubscriber = new SerializedSubscriber(paramSubscriber);
    AtomicReference localAtomicReference1 = new AtomicReference(EMPTY_TOKEN);
    AtomicReference localAtomicReference2 = new AtomicReference();
    1 local1 = new Subscriber(localAtomicReference1, localSerializedSubscriber, localAtomicReference2)
    {
      public void onCompleted()
      {
        onNext(null);
        this.val$s.onCompleted();
        ((Subscription)this.val$main.get()).unsubscribe();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$s.onError(paramThrowable);
        ((Subscription)this.val$main.get()).unsubscribe();
      }

      public void onNext(U paramU)
      {
        Object localObject = this.val$value.getAndSet(OperatorSampleWithObservable.EMPTY_TOKEN);
        if (localObject != OperatorSampleWithObservable.EMPTY_TOKEN)
          this.val$s.onNext(localObject);
      }
    };
    2 local2 = new Subscriber(localAtomicReference1, localSerializedSubscriber, local1)
    {
      public void onCompleted()
      {
        this.val$samplerSub.onNext(null);
        this.val$s.onCompleted();
        this.val$samplerSub.unsubscribe();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$s.onError(paramThrowable);
        this.val$samplerSub.unsubscribe();
      }

      public void onNext(T paramT)
      {
        this.val$value.set(paramT);
      }
    };
    localAtomicReference2.lazySet(local2);
    paramSubscriber.add(local2);
    paramSubscriber.add(local1);
    this.sampler.unsafeSubscribe(local1);
    return local2;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorSampleWithObservable
 * JD-Core Version:    0.6.0
 */