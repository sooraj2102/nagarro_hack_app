package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.plugins.RxJavaHooks;

public class OperatorOnBackpressureDrop<T>
  implements Observable.Operator<T, T>
{
  final Action1<? super T> onDrop;

  OperatorOnBackpressureDrop()
  {
    this(null);
  }

  public OperatorOnBackpressureDrop(Action1<? super T> paramAction1)
  {
    this.onDrop = paramAction1;
  }

  public static <T> OperatorOnBackpressureDrop<T> instance()
  {
    return Holder.INSTANCE;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    AtomicLong localAtomicLong = new AtomicLong();
    paramSubscriber.setProducer(new Producer(localAtomicLong)
    {
      public void request(long paramLong)
      {
        BackpressureUtils.getAndAddRequest(this.val$requested, paramLong);
      }
    });
    return new Subscriber(paramSubscriber, paramSubscriber, localAtomicLong)
    {
      boolean done;

      public void onCompleted()
      {
        if (!this.done)
        {
          this.done = true;
          this.val$child.onCompleted();
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
        do
        {
          return;
          if (this.val$requested.get() <= 0L)
            continue;
          this.val$child.onNext(paramT);
          this.val$requested.decrementAndGet();
          return;
        }
        while (OperatorOnBackpressureDrop.this.onDrop == null);
        try
        {
          OperatorOnBackpressureDrop.this.onDrop.call(paramT);
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwOrReport(localThrowable, this, paramT);
        }
      }

      public void onStart()
      {
        request(9223372036854775807L);
      }
    };
  }

  static final class Holder
  {
    static final OperatorOnBackpressureDrop<Object> INSTANCE = new OperatorOnBackpressureDrop();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorOnBackpressureDrop
 * JD-Core Version:    0.6.0
 */