package rx.internal.operators;

import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.internal.producers.SingleDelayedProducer;
import rx.plugins.RxJavaHooks;

public final class OperatorAll<T>
  implements Observable.Operator<Boolean, T>
{
  final Func1<? super T, Boolean> predicate;

  public OperatorAll(Func1<? super T, Boolean> paramFunc1)
  {
    this.predicate = paramFunc1;
  }

  public Subscriber<? super T> call(Subscriber<? super Boolean> paramSubscriber)
  {
    SingleDelayedProducer localSingleDelayedProducer = new SingleDelayedProducer(paramSubscriber);
    1 local1 = new Subscriber(localSingleDelayedProducer, paramSubscriber)
    {
      boolean done;

      public void onCompleted()
      {
        if (!this.done)
        {
          this.done = true;
          this.val$producer.setValue(Boolean.valueOf(true));
        }
      }

      public void onError(Throwable paramThrowable)
      {
        if (!this.done)
        {
          this.done = true;
          this.val$child.onError(paramThrowable);
          return;
        }
        RxJavaHooks.onError(paramThrowable);
      }

      public void onNext(T paramT)
      {
        if (this.done);
        while (true)
        {
          return;
          try
          {
            Boolean localBoolean = (Boolean)OperatorAll.this.predicate.call(paramT);
            if (localBoolean.booleanValue())
              continue;
            this.done = true;
            this.val$producer.setValue(Boolean.valueOf(false));
            unsubscribe();
            return;
          }
          catch (Throwable localThrowable)
          {
            Exceptions.throwOrReport(localThrowable, this, paramT);
          }
        }
      }
    };
    paramSubscriber.add(local1);
    paramSubscriber.setProducer(localSingleDelayedProducer);
    return local1;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorAll
 * JD-Core Version:    0.6.0
 */