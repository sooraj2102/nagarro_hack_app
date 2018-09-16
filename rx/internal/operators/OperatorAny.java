package rx.internal.operators;

import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.internal.producers.SingleDelayedProducer;
import rx.plugins.RxJavaHooks;

public final class OperatorAny<T>
  implements Observable.Operator<Boolean, T>
{
  final Func1<? super T, Boolean> predicate;
  final boolean returnOnEmpty;

  public OperatorAny(Func1<? super T, Boolean> paramFunc1, boolean paramBoolean)
  {
    this.predicate = paramFunc1;
    this.returnOnEmpty = paramBoolean;
  }

  public Subscriber<? super T> call(Subscriber<? super Boolean> paramSubscriber)
  {
    SingleDelayedProducer localSingleDelayedProducer = new SingleDelayedProducer(paramSubscriber);
    1 local1 = new Subscriber(localSingleDelayedProducer, paramSubscriber)
    {
      boolean done;
      boolean hasElements;

      public void onCompleted()
      {
        if (!this.done)
        {
          this.done = true;
          if (this.hasElements)
            this.val$producer.setValue(Boolean.valueOf(false));
        }
        else
        {
          return;
        }
        this.val$producer.setValue(Boolean.valueOf(OperatorAny.this.returnOnEmpty));
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
        if (this.done)
          return;
        this.hasElements = true;
        while (true)
        {
          try
          {
            boolean bool1 = ((Boolean)OperatorAny.this.predicate.call(paramT)).booleanValue();
            if (!bool1)
              break;
            this.done = true;
            SingleDelayedProducer localSingleDelayedProducer = this.val$producer;
            if (!OperatorAny.this.returnOnEmpty)
            {
              bool2 = true;
              localSingleDelayedProducer.setValue(Boolean.valueOf(bool2));
              unsubscribe();
              return;
            }
          }
          catch (Throwable localThrowable)
          {
            Exceptions.throwOrReport(localThrowable, this, paramT);
            return;
          }
          boolean bool2 = false;
        }
      }
    };
    paramSubscriber.add(local1);
    paramSubscriber.setProducer(localSingleDelayedProducer);
    return local1;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorAny
 * JD-Core Version:    0.6.0
 */