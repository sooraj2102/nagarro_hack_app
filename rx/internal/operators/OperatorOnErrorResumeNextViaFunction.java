package rx.internal.operators;

import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.internal.producers.ProducerArbiter;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.SerialSubscription;

public final class OperatorOnErrorResumeNextViaFunction<T>
  implements Observable.Operator<T, T>
{
  final Func1<? super Throwable, ? extends Observable<? extends T>> resumeFunction;

  public OperatorOnErrorResumeNextViaFunction(Func1<? super Throwable, ? extends Observable<? extends T>> paramFunc1)
  {
    this.resumeFunction = paramFunc1;
  }

  public static <T> OperatorOnErrorResumeNextViaFunction<T> withException(Observable<? extends T> paramObservable)
  {
    return new OperatorOnErrorResumeNextViaFunction(new Func1(paramObservable)
    {
      public Observable<? extends T> call(Throwable paramThrowable)
      {
        if ((paramThrowable instanceof Exception))
          return this.val$other;
        return Observable.error(paramThrowable);
      }
    });
  }

  public static <T> OperatorOnErrorResumeNextViaFunction<T> withOther(Observable<? extends T> paramObservable)
  {
    return new OperatorOnErrorResumeNextViaFunction(new Func1(paramObservable)
    {
      public Observable<? extends T> call(Throwable paramThrowable)
      {
        return this.val$other;
      }
    });
  }

  public static <T> OperatorOnErrorResumeNextViaFunction<T> withSingle(Func1<? super Throwable, ? extends T> paramFunc1)
  {
    return new OperatorOnErrorResumeNextViaFunction(new Func1(paramFunc1)
    {
      public Observable<? extends T> call(Throwable paramThrowable)
      {
        return Observable.just(this.val$resumeFunction.call(paramThrowable));
      }
    });
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    ProducerArbiter localProducerArbiter = new ProducerArbiter();
    SerialSubscription localSerialSubscription = new SerialSubscription();
    4 local4 = new Subscriber(paramSubscriber, localProducerArbiter, localSerialSubscription)
    {
      private boolean done;
      long produced;

      public void onCompleted()
      {
        if (this.done)
          return;
        this.done = true;
        this.val$child.onCompleted();
      }

      public void onError(Throwable paramThrowable)
      {
        if (this.done)
        {
          Exceptions.throwIfFatal(paramThrowable);
          RxJavaHooks.onError(paramThrowable);
          return;
        }
        this.done = true;
        try
        {
          unsubscribe();
          1 local1 = new Subscriber()
          {
            public void onCompleted()
            {
              OperatorOnErrorResumeNextViaFunction.4.this.val$child.onCompleted();
            }

            public void onError(Throwable paramThrowable)
            {
              OperatorOnErrorResumeNextViaFunction.4.this.val$child.onError(paramThrowable);
            }

            public void onNext(T paramT)
            {
              OperatorOnErrorResumeNextViaFunction.4.this.val$child.onNext(paramT);
            }

            public void setProducer(Producer paramProducer)
            {
              OperatorOnErrorResumeNextViaFunction.4.this.val$pa.setProducer(paramProducer);
            }
          };
          this.val$serial.set(local1);
          long l = this.produced;
          if (l != 0L)
            this.val$pa.produced(l);
          ((Observable)OperatorOnErrorResumeNextViaFunction.this.resumeFunction.call(paramThrowable)).unsafeSubscribe(local1);
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwOrReport(localThrowable, this.val$child);
        }
      }

      public void onNext(T paramT)
      {
        if (this.done)
          return;
        this.produced = (1L + this.produced);
        this.val$child.onNext(paramT);
      }

      public void setProducer(Producer paramProducer)
      {
        this.val$pa.setProducer(paramProducer);
      }
    };
    localSerialSubscription.set(local4);
    paramSubscriber.add(localSerialSubscription);
    paramSubscriber.setProducer(localProducerArbiter);
    return local4;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorOnErrorResumeNextViaFunction
 * JD-Core Version:    0.6.0
 */