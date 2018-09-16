package rx.internal.operators;

import java.util.Arrays;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.plugins.RxJavaHooks;

public class OnSubscribeDoOnEach<T>
  implements Observable.OnSubscribe<T>
{
  private final Observer<? super T> doOnEachObserver;
  private final Observable<T> source;

  public OnSubscribeDoOnEach(Observable<T> paramObservable, Observer<? super T> paramObserver)
  {
    this.source = paramObservable;
    this.doOnEachObserver = paramObserver;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    this.source.unsafeSubscribe(new DoOnEachSubscriber(paramSubscriber, this.doOnEachObserver));
  }

  private static final class DoOnEachSubscriber<T> extends Subscriber<T>
  {
    private final Observer<? super T> doOnEachObserver;
    private boolean done;
    private final Subscriber<? super T> subscriber;

    DoOnEachSubscriber(Subscriber<? super T> paramSubscriber, Observer<? super T> paramObserver)
    {
      super();
      this.subscriber = paramSubscriber;
      this.doOnEachObserver = paramObserver;
    }

    public void onCompleted()
    {
      if (this.done)
        return;
      try
      {
        this.doOnEachObserver.onCompleted();
        this.done = true;
        this.subscriber.onCompleted();
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwOrReport(localThrowable, this);
      }
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.done)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.done = true;
      try
      {
        this.doOnEachObserver.onError(paramThrowable);
        this.subscriber.onError(paramThrowable);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        this.subscriber.onError(new CompositeException(Arrays.asList(new Throwable[] { paramThrowable, localThrowable })));
      }
    }

    public void onNext(T paramT)
    {
      if (this.done)
        return;
      try
      {
        this.doOnEachObserver.onNext(paramT);
        this.subscriber.onNext(paramT);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwOrReport(localThrowable, this, paramT);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeDoOnEach
 * JD-Core Version:    0.6.0
 */