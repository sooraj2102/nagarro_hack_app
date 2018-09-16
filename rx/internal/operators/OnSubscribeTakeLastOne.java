package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public final class OnSubscribeTakeLastOne<T>
  implements Observable.OnSubscribe<T>
{
  final Observable<T> source;

  public OnSubscribeTakeLastOne(Observable<T> paramObservable)
  {
    this.source = paramObservable;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    new TakeLastOneSubscriber(paramSubscriber).subscribeTo(this.source);
  }

  static final class TakeLastOneSubscriber<T> extends DeferredScalarSubscriber<T, T>
  {
    static final Object EMPTY = new Object();

    public TakeLastOneSubscriber(Subscriber<? super T> paramSubscriber)
    {
      super();
      this.value = EMPTY;
    }

    public void onCompleted()
    {
      Object localObject = this.value;
      if (localObject == EMPTY)
      {
        complete();
        return;
      }
      complete(localObject);
    }

    public void onNext(T paramT)
    {
      this.value = paramT;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeTakeLastOne
 * JD-Core Version:    0.6.0
 */