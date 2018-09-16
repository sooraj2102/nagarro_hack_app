package rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.SerialSubscription;

public final class CompletableOnSubscribeConcat
  implements Completable.OnSubscribe
{
  final int prefetch;
  final Observable<Completable> sources;

  public CompletableOnSubscribeConcat(Observable<? extends Completable> paramObservable, int paramInt)
  {
    this.sources = paramObservable;
    this.prefetch = paramInt;
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    CompletableConcatSubscriber localCompletableConcatSubscriber = new CompletableConcatSubscriber(paramCompletableSubscriber, this.prefetch);
    paramCompletableSubscriber.onSubscribe(localCompletableConcatSubscriber);
    this.sources.subscribe(localCompletableConcatSubscriber);
  }

  static final class CompletableConcatSubscriber extends Subscriber<Completable>
  {
    final CompletableSubscriber actual;
    volatile boolean done;
    final ConcatInnerSubscriber inner;
    final AtomicBoolean once;
    final SpscArrayQueue<Completable> queue;
    final SerialSubscription sr;
    final AtomicInteger wip;

    public CompletableConcatSubscriber(CompletableSubscriber paramCompletableSubscriber, int paramInt)
    {
      this.actual = paramCompletableSubscriber;
      this.queue = new SpscArrayQueue(paramInt);
      this.sr = new SerialSubscription();
      this.inner = new ConcatInnerSubscriber();
      this.wip = new AtomicInteger();
      this.once = new AtomicBoolean();
      add(this.sr);
      request(paramInt);
    }

    void innerComplete()
    {
      if (this.wip.decrementAndGet() != 0)
        next();
      if (!this.done)
        request(1L);
    }

    void innerError(Throwable paramThrowable)
    {
      unsubscribe();
      onError(paramThrowable);
    }

    void next()
    {
      boolean bool = this.done;
      Completable localCompletable = (Completable)this.queue.poll();
      if (localCompletable == null)
      {
        if (bool)
        {
          if (this.once.compareAndSet(false, true))
            this.actual.onCompleted();
          return;
        }
        RxJavaHooks.onError(new IllegalStateException("Queue is empty?!"));
        return;
      }
      localCompletable.unsafeSubscribe(this.inner);
    }

    public void onCompleted()
    {
      if (this.done);
      do
      {
        return;
        this.done = true;
      }
      while (this.wip.getAndIncrement() != 0);
      next();
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.once.compareAndSet(false, true))
      {
        this.actual.onError(paramThrowable);
        return;
      }
      RxJavaHooks.onError(paramThrowable);
    }

    public void onNext(Completable paramCompletable)
    {
      if (!this.queue.offer(paramCompletable))
        onError(new MissingBackpressureException());
      do
        return;
      while (this.wip.getAndIncrement() != 0);
      next();
    }

    final class ConcatInnerSubscriber
      implements CompletableSubscriber
    {
      ConcatInnerSubscriber()
      {
      }

      public void onCompleted()
      {
        CompletableOnSubscribeConcat.CompletableConcatSubscriber.this.innerComplete();
      }

      public void onError(Throwable paramThrowable)
      {
        CompletableOnSubscribeConcat.CompletableConcatSubscriber.this.innerError(paramThrowable);
      }

      public void onSubscribe(Subscription paramSubscription)
      {
        CompletableOnSubscribeConcat.CompletableConcatSubscriber.this.sr.set(paramSubscription);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableOnSubscribeConcat
 * JD-Core Version:    0.6.0
 */