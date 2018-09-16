package rx.internal.operators;

import java.util.Iterator;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func2;
import rx.observers.Subscribers;

public final class OperatorZipIterable<T1, T2, R>
  implements Observable.Operator<R, T1>
{
  final Iterable<? extends T2> iterable;
  final Func2<? super T1, ? super T2, ? extends R> zipFunction;

  public OperatorZipIterable(Iterable<? extends T2> paramIterable, Func2<? super T1, ? super T2, ? extends R> paramFunc2)
  {
    this.iterable = paramIterable;
    this.zipFunction = paramFunc2;
  }

  public Subscriber<? super T1> call(Subscriber<? super R> paramSubscriber)
  {
    Iterator localIterator = this.iterable.iterator();
    try
    {
      if (!localIterator.hasNext())
      {
        paramSubscriber.onCompleted();
        Subscriber localSubscriber = Subscribers.empty();
        return localSubscriber;
      }
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwOrReport(localThrowable, paramSubscriber);
      return Subscribers.empty();
    }
    return new Subscriber(paramSubscriber, paramSubscriber, localIterator)
    {
      boolean done;

      public void onCompleted()
      {
        if (this.done)
          return;
        this.done = true;
        this.val$subscriber.onCompleted();
      }

      public void onError(Throwable paramThrowable)
      {
        if (this.done)
        {
          Exceptions.throwIfFatal(paramThrowable);
          return;
        }
        this.done = true;
        this.val$subscriber.onError(paramThrowable);
      }

      public void onNext(T1 paramT1)
      {
        if (this.done);
        while (true)
        {
          return;
          try
          {
            this.val$subscriber.onNext(OperatorZipIterable.this.zipFunction.call(paramT1, this.val$iterator.next()));
            if (this.val$iterator.hasNext())
              continue;
            onCompleted();
            return;
          }
          catch (Throwable localThrowable)
          {
            Exceptions.throwOrReport(localThrowable, this);
          }
        }
      }
    };
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorZipIterable
 * JD-Core Version:    0.6.0
 */