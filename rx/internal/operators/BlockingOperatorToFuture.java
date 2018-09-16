package rx.internal.operators;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public final class BlockingOperatorToFuture
{
  private BlockingOperatorToFuture()
  {
    throw new IllegalStateException("No instances!");
  }

  public static <T> Future<T> toFuture(Observable<? extends T> paramObservable)
  {
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    AtomicReference localAtomicReference1 = new AtomicReference();
    AtomicReference localAtomicReference2 = new AtomicReference();
    return new Future(localCountDownLatch, paramObservable.single().subscribe(new Subscriber(localCountDownLatch, localAtomicReference2, localAtomicReference1)
    {
      public void onCompleted()
      {
        this.val$finished.countDown();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$error.compareAndSet(null, paramThrowable);
        this.val$finished.countDown();
      }

      public void onNext(T paramT)
      {
        this.val$value.set(paramT);
      }
    }), localAtomicReference2, localAtomicReference1)
    {
      private volatile boolean cancelled;

      private T getValue()
        throws ExecutionException
      {
        Throwable localThrowable = (Throwable)this.val$error.get();
        if (localThrowable != null)
          throw new ExecutionException("Observable onError", localThrowable);
        if (this.cancelled)
          throw new CancellationException("Subscription unsubscribed");
        return this.val$value.get();
      }

      public boolean cancel(boolean paramBoolean)
      {
        if (this.val$finished.getCount() > 0L)
        {
          this.cancelled = true;
          this.val$s.unsubscribe();
          this.val$finished.countDown();
          return true;
        }
        return false;
      }

      public T get()
        throws InterruptedException, ExecutionException
      {
        this.val$finished.await();
        return getValue();
      }

      public T get(long paramLong, TimeUnit paramTimeUnit)
        throws InterruptedException, ExecutionException, TimeoutException
      {
        if (this.val$finished.await(paramLong, paramTimeUnit))
          return getValue();
        throw new TimeoutException("Timed out after " + paramTimeUnit.toMillis(paramLong) + "ms waiting for underlying Observable.");
      }

      public boolean isCancelled()
      {
        return this.cancelled;
      }

      public boolean isDone()
      {
        return this.val$finished.getCount() == 0L;
      }
    };
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.BlockingOperatorToFuture
 * JD-Core Version:    0.6.0
 */