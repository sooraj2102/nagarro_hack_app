package rx.internal.operators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.FuncN;
import rx.observers.SerializedSubscriber;
import rx.plugins.RxJavaHooks;

public final class OperatorWithLatestFromMany<T, R>
  implements Observable.OnSubscribe<R>
{
  final FuncN<R> combiner;
  final Observable<T> main;
  final Observable<?>[] others;
  final Iterable<Observable<?>> othersIterable;

  public OperatorWithLatestFromMany(Observable<T> paramObservable, Observable<?>[] paramArrayOfObservable, Iterable<Observable<?>> paramIterable, FuncN<R> paramFuncN)
  {
    this.main = paramObservable;
    this.others = paramArrayOfObservable;
    this.othersIterable = paramIterable;
    this.combiner = paramFuncN;
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    SerializedSubscriber localSerializedSubscriber = new SerializedSubscriber(paramSubscriber);
    int i = 0;
    Observable[] arrayOfObservable;
    WithLatestMainSubscriber localWithLatestMainSubscriber;
    if (this.others != null)
    {
      arrayOfObservable = this.others;
      i = arrayOfObservable.length;
      localWithLatestMainSubscriber = new WithLatestMainSubscriber(paramSubscriber, this.combiner, i);
      localSerializedSubscriber.add(localWithLatestMainSubscriber);
    }
    for (int k = 0; ; k++)
    {
      if (k >= i)
        break label184;
      if (localSerializedSubscriber.isUnsubscribed())
      {
        return;
        arrayOfObservable = new Observable[8];
        Iterator localIterator = this.othersIterable.iterator();
        while (localIterator.hasNext())
        {
          Observable localObservable = (Observable)localIterator.next();
          if (i == arrayOfObservable.length)
            arrayOfObservable = (Observable[])Arrays.copyOf(arrayOfObservable, i + (i >> 2));
          int j = i + 1;
          arrayOfObservable[i] = localObservable;
          i = j;
        }
        break;
      }
      WithLatestOtherSubscriber localWithLatestOtherSubscriber = new WithLatestOtherSubscriber(localWithLatestMainSubscriber, k + 1);
      localWithLatestMainSubscriber.add(localWithLatestOtherSubscriber);
      arrayOfObservable[k].unsafeSubscribe(localWithLatestOtherSubscriber);
    }
    label184: this.main.unsafeSubscribe(localWithLatestMainSubscriber);
  }

  static final class WithLatestMainSubscriber<T, R> extends Subscriber<T>
  {
    static final Object EMPTY = new Object();
    final Subscriber<? super R> actual;
    final FuncN<R> combiner;
    final AtomicReferenceArray<Object> current;
    boolean done;
    final AtomicInteger ready;

    public WithLatestMainSubscriber(Subscriber<? super R> paramSubscriber, FuncN<R> paramFuncN, int paramInt)
    {
      this.actual = paramSubscriber;
      this.combiner = paramFuncN;
      AtomicReferenceArray localAtomicReferenceArray = new AtomicReferenceArray(paramInt + 1);
      for (int i = 0; i <= paramInt; i++)
        localAtomicReferenceArray.lazySet(i, EMPTY);
      this.current = localAtomicReferenceArray;
      this.ready = new AtomicInteger(paramInt);
      request(0L);
    }

    void innerComplete(int paramInt)
    {
      if (this.current.get(paramInt) == EMPTY)
        onCompleted();
    }

    void innerError(int paramInt, Throwable paramThrowable)
    {
      onError(paramThrowable);
    }

    void innerNext(int paramInt, Object paramObject)
    {
      if (this.current.getAndSet(paramInt, paramObject) == EMPTY)
        this.ready.decrementAndGet();
    }

    public void onCompleted()
    {
      if (this.done)
        return;
      this.done = true;
      unsubscribe();
      this.actual.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.done)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.done = true;
      unsubscribe();
      this.actual.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      if (this.done)
        return;
      if (this.ready.get() == 0)
      {
        AtomicReferenceArray localAtomicReferenceArray = this.current;
        int i = localAtomicReferenceArray.length();
        localAtomicReferenceArray.lazySet(0, paramT);
        Object[] arrayOfObject = new Object[localAtomicReferenceArray.length()];
        for (int j = 0; j < i; j++)
          arrayOfObject[j] = localAtomicReferenceArray.get(j);
        try
        {
          Object localObject = this.combiner.call(arrayOfObject);
          this.actual.onNext(localObject);
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwIfFatal(localThrowable);
          onError(localThrowable);
          return;
        }
      }
      request(1L);
    }

    public void setProducer(Producer paramProducer)
    {
      super.setProducer(paramProducer);
      this.actual.setProducer(paramProducer);
    }
  }

  static final class WithLatestOtherSubscriber extends Subscriber<Object>
  {
    final int index;
    final OperatorWithLatestFromMany.WithLatestMainSubscriber<?, ?> parent;

    public WithLatestOtherSubscriber(OperatorWithLatestFromMany.WithLatestMainSubscriber<?, ?> paramWithLatestMainSubscriber, int paramInt)
    {
      this.parent = paramWithLatestMainSubscriber;
      this.index = paramInt;
    }

    public void onCompleted()
    {
      this.parent.innerComplete(this.index);
    }

    public void onError(Throwable paramThrowable)
    {
      this.parent.innerError(this.index, paramThrowable);
    }

    public void onNext(Object paramObject)
    {
      this.parent.innerNext(this.index, paramObject);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorWithLatestFromMany
 * JD-Core Version:    0.6.0
 */