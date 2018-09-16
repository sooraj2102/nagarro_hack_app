package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;

public final class OnSubscribeRange
  implements Observable.OnSubscribe<Integer>
{
  private final int endIndex;
  private final int startIndex;

  public OnSubscribeRange(int paramInt1, int paramInt2)
  {
    this.startIndex = paramInt1;
    this.endIndex = paramInt2;
  }

  public void call(Subscriber<? super Integer> paramSubscriber)
  {
    paramSubscriber.setProducer(new RangeProducer(paramSubscriber, this.startIndex, this.endIndex));
  }

  static final class RangeProducer extends AtomicLong
    implements Producer
  {
    private static final long serialVersionUID = 4114392207069098388L;
    private final Subscriber<? super Integer> childSubscriber;
    private long currentIndex;
    private final int endOfRange;

    RangeProducer(Subscriber<? super Integer> paramSubscriber, int paramInt1, int paramInt2)
    {
      this.childSubscriber = paramSubscriber;
      this.currentIndex = paramInt1;
      this.endOfRange = paramInt2;
    }

    void fastPath()
    {
      long l1 = 1L + this.endOfRange;
      Subscriber localSubscriber = this.childSubscriber;
      long l2 = this.currentIndex;
      if (l2 != l1)
        if (!localSubscriber.isUnsubscribed());
      do
      {
        return;
        localSubscriber.onNext(Integer.valueOf((int)l2));
        l2 += 1L;
        break;
      }
      while (localSubscriber.isUnsubscribed());
      localSubscriber.onCompleted();
    }

    public void request(long paramLong)
    {
      if (get() == 9223372036854775807L);
      do
      {
        return;
        if ((paramLong != 9223372036854775807L) || (!compareAndSet(0L, 9223372036854775807L)))
          continue;
        fastPath();
        return;
      }
      while ((paramLong <= 0L) || (BackpressureUtils.getAndAddRequest(this, paramLong) != 0L));
      slowPath(paramLong);
    }

    void slowPath(long paramLong)
    {
      long l1 = 0L;
      long l2 = 1L + this.endOfRange;
      long l3 = this.currentIndex;
      Subscriber localSubscriber = this.childSubscriber;
      while (true)
      {
        if ((l1 != paramLong) && (l3 != l2))
          if (!localSubscriber.isUnsubscribed());
        do
        {
          do
          {
            return;
            localSubscriber.onNext(Integer.valueOf((int)l3));
            l3 += 1L;
            l1 += 1L;
            break;
          }
          while (localSubscriber.isUnsubscribed());
          if (l3 == l2)
          {
            localSubscriber.onCompleted();
            return;
          }
          paramLong = get();
          if (paramLong != l1)
            break;
          this.currentIndex = l3;
          paramLong = addAndGet(-l1);
        }
        while (paramLong == 0L);
        l1 = 0L;
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeRange
 * JD-Core Version:    0.6.0
 */