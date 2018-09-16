package rx.internal.operators;

import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.functions.Action1;

public class OperatorDoOnRequest<T>
  implements Observable.Operator<T, T>
{
  final Action1<Long> request;

  public OperatorDoOnRequest(Action1<Long> paramAction1)
  {
    this.request = paramAction1;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    ParentSubscriber localParentSubscriber = new ParentSubscriber(paramSubscriber);
    paramSubscriber.setProducer(new Producer(localParentSubscriber)
    {
      public void request(long paramLong)
      {
        OperatorDoOnRequest.this.request.call(Long.valueOf(paramLong));
        OperatorDoOnRequest.ParentSubscriber.access$000(this.val$parent, paramLong);
      }
    });
    paramSubscriber.add(localParentSubscriber);
    return localParentSubscriber;
  }

  static final class ParentSubscriber<T> extends Subscriber<T>
  {
    private final Subscriber<? super T> child;

    ParentSubscriber(Subscriber<? super T> paramSubscriber)
    {
      this.child = paramSubscriber;
      request(0L);
    }

    private void requestMore(long paramLong)
    {
      request(paramLong);
    }

    public void onCompleted()
    {
      this.child.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      this.child.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      this.child.onNext(paramT);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorDoOnRequest
 * JD-Core Version:    0.6.0
 */