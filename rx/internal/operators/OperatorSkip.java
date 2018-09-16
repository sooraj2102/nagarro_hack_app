package rx.internal.operators;

import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;

public final class OperatorSkip<T>
  implements Observable.Operator<T, T>
{
  final int toSkip;

  public OperatorSkip(int paramInt)
  {
    if (paramInt < 0)
      throw new IllegalArgumentException("n >= 0 required but it was " + paramInt);
    this.toSkip = paramInt;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    return new Subscriber(paramSubscriber, paramSubscriber)
    {
      int skipped;

      public void onCompleted()
      {
        this.val$child.onCompleted();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$child.onError(paramThrowable);
      }

      public void onNext(T paramT)
      {
        if (this.skipped >= OperatorSkip.this.toSkip)
        {
          this.val$child.onNext(paramT);
          return;
        }
        this.skipped = (1 + this.skipped);
      }

      public void setProducer(Producer paramProducer)
      {
        this.val$child.setProducer(paramProducer);
        paramProducer.request(OperatorSkip.this.toSkip);
      }
    };
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorSkip
 * JD-Core Version:    0.6.0
 */