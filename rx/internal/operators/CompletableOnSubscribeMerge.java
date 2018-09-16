package rx.internal.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import rx.Completable;
import rx.Completable.OnSubscribe;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeMerge
  implements Completable.OnSubscribe
{
  final boolean delayErrors;
  final int maxConcurrency;
  final Observable<Completable> source;

  public CompletableOnSubscribeMerge(Observable<? extends Completable> paramObservable, int paramInt, boolean paramBoolean)
  {
    this.source = paramObservable;
    this.maxConcurrency = paramInt;
    this.delayErrors = paramBoolean;
  }

  public static Throwable collectErrors(Queue<Throwable> paramQueue)
  {
    ArrayList localArrayList = new ArrayList();
    while (true)
    {
      Throwable localThrowable = (Throwable)paramQueue.poll();
      if (localThrowable == null)
        break;
      localArrayList.add(localThrowable);
    }
    if (localArrayList.isEmpty())
      return null;
    if (localArrayList.size() == 1)
      return (Throwable)localArrayList.get(0);
    return new CompositeException(localArrayList);
  }

  public void call(CompletableSubscriber paramCompletableSubscriber)
  {
    CompletableMergeSubscriber localCompletableMergeSubscriber = new CompletableMergeSubscriber(paramCompletableSubscriber, this.maxConcurrency, this.delayErrors);
    paramCompletableSubscriber.onSubscribe(localCompletableMergeSubscriber);
    this.source.subscribe(localCompletableMergeSubscriber);
  }

  static final class CompletableMergeSubscriber extends Subscriber<Completable>
  {
    final CompletableSubscriber actual;
    final boolean delayErrors;
    volatile boolean done;
    final AtomicReference<Queue<Throwable>> errors;
    final AtomicBoolean once;
    final CompositeSubscription set;
    final AtomicInteger wip;

    public CompletableMergeSubscriber(CompletableSubscriber paramCompletableSubscriber, int paramInt, boolean paramBoolean)
    {
      this.actual = paramCompletableSubscriber;
      this.delayErrors = paramBoolean;
      this.set = new CompositeSubscription();
      this.wip = new AtomicInteger(1);
      this.once = new AtomicBoolean();
      this.errors = new AtomicReference();
      if (paramInt == 2147483647)
      {
        request(9223372036854775807L);
        return;
      }
      request(paramInt);
    }

    Queue<Throwable> getOrCreateErrors()
    {
      Queue localQueue = (Queue)this.errors.get();
      if (localQueue != null)
        return localQueue;
      ConcurrentLinkedQueue localConcurrentLinkedQueue = new ConcurrentLinkedQueue();
      if (this.errors.compareAndSet(null, localConcurrentLinkedQueue))
        return localConcurrentLinkedQueue;
      return (Queue)this.errors.get();
    }

    public void onCompleted()
    {
      if (this.done)
        return;
      this.done = true;
      terminate();
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.done)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      getOrCreateErrors().offer(paramThrowable);
      this.done = true;
      terminate();
    }

    public void onNext(Completable paramCompletable)
    {
      if (this.done)
        return;
      this.wip.getAndIncrement();
      paramCompletable.unsafeSubscribe(new CompletableSubscriber()
      {
        Subscription d;
        boolean innerDone;

        public void onCompleted()
        {
          if (this.innerDone);
          do
          {
            return;
            this.innerDone = true;
            CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.set.remove(this.d);
            CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.terminate();
          }
          while (CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.done);
          CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.request(1L);
        }

        public void onError(Throwable paramThrowable)
        {
          if (this.innerDone)
            RxJavaHooks.onError(paramThrowable);
          do
          {
            return;
            this.innerDone = true;
            CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.set.remove(this.d);
            CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.getOrCreateErrors().offer(paramThrowable);
            CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.terminate();
          }
          while ((!CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.delayErrors) || (CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.done));
          CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.request(1L);
        }

        public void onSubscribe(Subscription paramSubscription)
        {
          this.d = paramSubscription;
          CompletableOnSubscribeMerge.CompletableMergeSubscriber.this.set.add(paramSubscription);
        }
      });
    }

    void terminate()
    {
      Queue localQueue2;
      if (this.wip.decrementAndGet() == 0)
      {
        localQueue2 = (Queue)this.errors.get();
        if ((localQueue2 == null) || (localQueue2.isEmpty()))
          this.actual.onCompleted();
      }
      Queue localQueue1;
      do
      {
        do
        {
          return;
          Throwable localThrowable2 = CompletableOnSubscribeMerge.collectErrors(localQueue2);
          if (this.once.compareAndSet(false, true))
          {
            this.actual.onError(localThrowable2);
            return;
          }
          RxJavaHooks.onError(localThrowable2);
          return;
        }
        while (this.delayErrors);
        localQueue1 = (Queue)this.errors.get();
      }
      while ((localQueue1 == null) || (localQueue1.isEmpty()));
      Throwable localThrowable1 = CompletableOnSubscribeMerge.collectErrors(localQueue1);
      if (this.once.compareAndSet(false, true))
      {
        this.actual.onError(localThrowable1);
        return;
      }
      RxJavaHooks.onError(localThrowable1);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.CompletableOnSubscribeMerge
 * JD-Core Version:    0.6.0
 */