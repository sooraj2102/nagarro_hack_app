package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import rx.Subscriber;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;

public final class BackpressureUtils
{
  static final long COMPLETED_MASK = -9223372036854775808L;
  static final long REQUESTED_MASK = 9223372036854775807L;

  private BackpressureUtils()
  {
    throw new IllegalStateException("No instances!");
  }

  public static long addCap(long paramLong1, long paramLong2)
  {
    long l = paramLong1 + paramLong2;
    if (l < 0L)
      l = 9223372036854775807L;
    return l;
  }

  public static long getAndAddRequest(AtomicLong paramAtomicLong, long paramLong)
  {
    long l;
    do
      l = paramAtomicLong.get();
    while (!paramAtomicLong.compareAndSet(l, addCap(l, paramLong)));
    return l;
  }

  public static long multiplyCap(long paramLong1, long paramLong2)
  {
    long l = paramLong1 * paramLong2;
    if (((paramLong1 | paramLong2) >>> 31 != 0L) && (paramLong2 != 0L) && (l / paramLong2 != paramLong1))
      l = 9223372036854775807L;
    return l;
  }

  public static <T> void postCompleteDone(AtomicLong paramAtomicLong, Queue<T> paramQueue, Subscriber<? super T> paramSubscriber)
  {
    postCompleteDone(paramAtomicLong, paramQueue, paramSubscriber, UtilityFunctions.identity());
  }

  public static <T, R> void postCompleteDone(AtomicLong paramAtomicLong, Queue<T> paramQueue, Subscriber<? super R> paramSubscriber, Func1<? super T, ? extends R> paramFunc1)
  {
    long l = paramAtomicLong.get();
    if ((l & 0x0) != 0L);
    do
    {
      return;
      if (!paramAtomicLong.compareAndSet(l, l | 0x0))
        break;
    }
    while (l == 0L);
    postCompleteDrain(paramAtomicLong, paramQueue, paramSubscriber, paramFunc1);
  }

  static <T, R> void postCompleteDrain(AtomicLong paramAtomicLong, Queue<T> paramQueue, Subscriber<? super R> paramSubscriber, Func1<? super T, ? extends R> paramFunc1)
  {
    long l1 = paramAtomicLong.get();
    if (l1 == 9223372036854775807L)
      while (true)
      {
        if (paramSubscriber.isUnsubscribed())
          return;
        Object localObject2 = paramQueue.poll();
        if (localObject2 == null)
        {
          paramSubscriber.onCompleted();
          return;
        }
        paramSubscriber.onNext(paramFunc1.call(localObject2));
      }
    long l2 = -9223372036854775808L;
    while (true)
    {
      if (l2 != l1)
      {
        if (paramSubscriber.isUnsubscribed())
          break;
        Object localObject1 = paramQueue.poll();
        if (localObject1 == null)
        {
          paramSubscriber.onCompleted();
          return;
        }
        paramSubscriber.onNext(paramFunc1.call(localObject1));
        l2 += 1L;
        continue;
      }
      if (l2 == l1)
      {
        if (paramSubscriber.isUnsubscribed())
          break;
        if (paramQueue.isEmpty())
        {
          paramSubscriber.onCompleted();
          return;
        }
      }
      l1 = paramAtomicLong.get();
      if (l1 != l2)
        continue;
      l1 = paramAtomicLong.addAndGet(-(l2 & 0xFFFFFFFF));
      if (l1 == -9223372036854775808L)
        break;
      l2 = -9223372036854775808L;
    }
  }

  public static <T> boolean postCompleteRequest(AtomicLong paramAtomicLong, long paramLong, Queue<T> paramQueue, Subscriber<? super T> paramSubscriber)
  {
    return postCompleteRequest(paramAtomicLong, paramLong, paramQueue, paramSubscriber, UtilityFunctions.identity());
  }

  public static <T, R> boolean postCompleteRequest(AtomicLong paramAtomicLong, long paramLong, Queue<T> paramQueue, Subscriber<? super R> paramSubscriber, Func1<? super T, ? extends R> paramFunc1)
  {
    if (paramLong < 0L)
      throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
    if (paramLong == 0L)
      return (0x0 & paramAtomicLong.get()) == 0L;
    long l1;
    long l2;
    do
    {
      l1 = paramAtomicLong.get();
      l2 = l1 & 0x0;
    }
    while (!paramAtomicLong.compareAndSet(l1, l2 | addCap(l1 & 0xFFFFFFFF, paramLong)));
    if (l1 == -9223372036854775808L)
    {
      postCompleteDrain(paramAtomicLong, paramQueue, paramSubscriber, paramFunc1);
      return false;
    }
    return l2 == 0L;
  }

  public static long produced(AtomicLong paramAtomicLong, long paramLong)
  {
    long l1;
    long l2;
    do
    {
      l1 = paramAtomicLong.get();
      if (l1 == 9223372036854775807L)
        return 9223372036854775807L;
      l2 = l1 - paramLong;
      if (l2 >= 0L)
        continue;
      throw new IllegalStateException("More produced than requested: " + l2);
    }
    while (!paramAtomicLong.compareAndSet(l1, l2));
    return l2;
  }

  public static boolean validate(long paramLong)
  {
    if (paramLong < 0L)
      throw new IllegalArgumentException("n >= 0 required but it was " + paramLong);
    return paramLong != 0L;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.BackpressureUtils
 * JD-Core Version:    0.6.0
 */