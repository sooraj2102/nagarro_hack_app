package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;

public final class OnSubscribeFromArray<T>
  implements Observable.OnSubscribe<T>
{
  final T[] array;

  public OnSubscribeFromArray(T[] paramArrayOfT)
  {
    this.array = paramArrayOfT;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    paramSubscriber.setProducer(new FromArrayProducer(paramSubscriber, this.array));
  }

  static final class FromArrayProducer<T> extends AtomicLong
    implements Producer
  {
    private static final long serialVersionUID = 3534218984725836979L;
    final T[] array;
    final Subscriber<? super T> child;
    int index;

    public FromArrayProducer(Subscriber<? super T> paramSubscriber, T[] paramArrayOfT)
    {
      this.child = paramSubscriber;
      this.array = paramArrayOfT;
    }

    void fastPath()
    {
      Subscriber localSubscriber = this.child;
      Object[] arrayOfObject = this.array;
      int i = arrayOfObject.length;
      int j = 0;
      Object localObject;
      if (j < i)
      {
        localObject = arrayOfObject[j];
        if (!localSubscriber.isUnsubscribed());
      }
      do
      {
        return;
        localSubscriber.onNext(localObject);
        j++;
        break;
      }
      while (localSubscriber.isUnsubscribed());
      localSubscriber.onCompleted();
    }

    public void request(long paramLong)
    {
      if (paramLong < 0L)
        throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
      if (paramLong == 9223372036854775807L)
        if (BackpressureUtils.getAndAddRequest(this, paramLong) == 0L)
          fastPath();
      do
        return;
      while ((paramLong == 0L) || (BackpressureUtils.getAndAddRequest(this, paramLong) != 0L));
      slowPath(paramLong);
    }

    void slowPath(long paramLong)
    {
      Subscriber localSubscriber = this.child;
      Object[] arrayOfObject = this.array;
      int i = arrayOfObject.length;
      long l = 0L;
      int j = this.index;
      while (true)
      {
        if ((paramLong != 0L) && (j != i))
          if (!localSubscriber.isUnsubscribed());
        do
        {
          while (true)
          {
            return;
            localSubscriber.onNext(arrayOfObject[j]);
            j++;
            if (j != i)
              break;
            if (localSubscriber.isUnsubscribed())
              continue;
            localSubscriber.onCompleted();
            return;
          }
          paramLong -= 1L;
          l -= 1L;
          break;
          paramLong = l + get();
          if (paramLong != 0L)
            break;
          this.index = j;
          paramLong = addAndGet(l);
        }
        while (paramLong == 0L);
        l = 0L;
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeFromArray
 * JD-Core Version:    0.6.0
 */