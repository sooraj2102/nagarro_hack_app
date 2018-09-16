package rx.internal.operators;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.Exceptions;
import rx.functions.FuncN;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.CompositeSubscription;

public final class SingleOperatorZip
{
  private SingleOperatorZip()
  {
    throw new IllegalStateException("No instances!");
  }

  public static <T, R> Single<R> zip(Single<? extends T>[] paramArrayOfSingle, FuncN<? extends R> paramFuncN)
  {
    return Single.create(new Single.OnSubscribe(paramArrayOfSingle, paramFuncN)
    {
      public void call(SingleSubscriber<? super R> paramSingleSubscriber)
      {
        if (this.val$singles.length == 0)
          paramSingleSubscriber.onError(new NoSuchElementException("Can't zip 0 Singles."));
        label152: 
        while (true)
        {
          return;
          AtomicInteger localAtomicInteger = new AtomicInteger(this.val$singles.length);
          AtomicBoolean localAtomicBoolean = new AtomicBoolean();
          Object[] arrayOfObject = new Object[this.val$singles.length];
          CompositeSubscription localCompositeSubscription = new CompositeSubscription();
          paramSingleSubscriber.add(localCompositeSubscription);
          for (int i = 0; ; i++)
          {
            if ((i >= this.val$singles.length) || (localCompositeSubscription.isUnsubscribed()) || (localAtomicBoolean.get()))
              break label152;
            1 local1 = new SingleSubscriber(arrayOfObject, i, localAtomicInteger, paramSingleSubscriber, localAtomicBoolean)
            {
              public void onError(Throwable paramThrowable)
              {
                if (this.val$once.compareAndSet(false, true))
                {
                  this.val$subscriber.onError(paramThrowable);
                  return;
                }
                RxJavaHooks.onError(paramThrowable);
              }

              public void onSuccess(T paramT)
              {
                this.val$values[this.val$j] = paramT;
                if (this.val$wip.decrementAndGet() == 0);
                try
                {
                  Object localObject = SingleOperatorZip.1.this.val$zipper.call(this.val$values);
                  this.val$subscriber.onSuccess(localObject);
                  return;
                }
                catch (Throwable localThrowable)
                {
                  Exceptions.throwIfFatal(localThrowable);
                  onError(localThrowable);
                }
              }
            };
            localCompositeSubscription.add(local1);
            if ((localCompositeSubscription.isUnsubscribed()) || (localAtomicBoolean.get()))
              break;
            this.val$singles[i].subscribe(local1);
          }
        }
      }
    });
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.SingleOperatorZip
 * JD-Core Version:    0.6.0
 */