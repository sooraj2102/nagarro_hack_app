package rx.internal.operators;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;

public final class OnSubscribeFromIterable<T>
  implements Observable.OnSubscribe<T>
{
  final Iterable<? extends T> is;

  public OnSubscribeFromIterable(Iterable<? extends T> paramIterable)
  {
    if (paramIterable == null)
      throw new NullPointerException("iterable must not be null");
    this.is = paramIterable;
  }

  public void call(Subscriber<? super T> paramSubscriber)
  {
    Iterator localIterator;
    try
    {
      localIterator = this.is.iterator();
      boolean bool = localIterator.hasNext();
      if (!paramSubscriber.isUnsubscribed())
      {
        if (!bool)
          paramSubscriber.onCompleted();
      }
      else
        return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwOrReport(localThrowable, paramSubscriber);
      return;
    }
    paramSubscriber.setProducer(new IterableProducer(paramSubscriber, localIterator));
  }

  static final class IterableProducer<T> extends AtomicLong
    implements Producer
  {
    private static final long serialVersionUID = -8730475647105475802L;
    private final Iterator<? extends T> it;
    private final Subscriber<? super T> o;

    IterableProducer(Subscriber<? super T> paramSubscriber, Iterator<? extends T> paramIterator)
    {
      this.o = paramSubscriber;
      this.it = paramIterator;
    }

    // ERROR //
    void fastPath()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 24	rx/internal/operators/OnSubscribeFromIterable$IterableProducer:o	Lrx/Subscriber;
      //   4: astore_1
      //   5: aload_0
      //   6: getfield 26	rx/internal/operators/OnSubscribeFromIterable$IterableProducer:it	Ljava/util/Iterator;
      //   9: astore_2
      //   10: aload_1
      //   11: invokevirtual 35	rx/Subscriber:isUnsubscribed	()Z
      //   14: ifeq +4 -> 18
      //   17: return
      //   18: aload_2
      //   19: invokeinterface 41 1 0
      //   24: astore 4
      //   26: aload_1
      //   27: aload 4
      //   29: invokevirtual 45	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   32: aload_1
      //   33: invokevirtual 35	rx/Subscriber:isUnsubscribed	()Z
      //   36: ifne -19 -> 17
      //   39: aload_2
      //   40: invokeinterface 48 1 0
      //   45: istore 6
      //   47: iload 6
      //   49: ifne -39 -> 10
      //   52: aload_1
      //   53: invokevirtual 35	rx/Subscriber:isUnsubscribed	()Z
      //   56: ifne -39 -> 17
      //   59: aload_1
      //   60: invokevirtual 51	rx/Subscriber:onCompleted	()V
      //   63: return
      //   64: astore_3
      //   65: aload_3
      //   66: aload_1
      //   67: invokestatic 57	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
      //   70: return
      //   71: astore 5
      //   73: aload 5
      //   75: aload_1
      //   76: invokestatic 57	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
      //   79: return
      //
      // Exception table:
      //   from	to	target	type
      //   18	26	64	java/lang/Throwable
      //   39	47	71	java/lang/Throwable
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

    // ERROR //
    void slowPath(long paramLong)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 24	rx/internal/operators/OnSubscribeFromIterable$IterableProducer:o	Lrx/Subscriber;
      //   4: astore_3
      //   5: aload_0
      //   6: getfield 26	rx/internal/operators/OnSubscribeFromIterable$IterableProducer:it	Ljava/util/Iterator;
      //   9: astore 4
      //   11: lload_1
      //   12: lstore 5
      //   14: lconst_0
      //   15: lstore 7
      //   17: lload 7
      //   19: lload 5
      //   21: lcmp
      //   22: ifeq +86 -> 108
      //   25: aload_3
      //   26: invokevirtual 35	rx/Subscriber:isUnsubscribed	()Z
      //   29: ifeq +4 -> 33
      //   32: return
      //   33: aload 4
      //   35: invokeinterface 41 1 0
      //   40: astore 10
      //   42: aload_3
      //   43: aload 10
      //   45: invokevirtual 45	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   48: aload_3
      //   49: invokevirtual 35	rx/Subscriber:isUnsubscribed	()Z
      //   52: ifne -20 -> 32
      //   55: aload 4
      //   57: invokeinterface 48 1 0
      //   62: istore 12
      //   64: iload 12
      //   66: ifne +33 -> 99
      //   69: aload_3
      //   70: invokevirtual 35	rx/Subscriber:isUnsubscribed	()Z
      //   73: ifne -41 -> 32
      //   76: aload_3
      //   77: invokevirtual 51	rx/Subscriber:onCompleted	()V
      //   80: return
      //   81: astore 9
      //   83: aload 9
      //   85: aload_3
      //   86: invokestatic 57	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
      //   89: return
      //   90: astore 11
      //   92: aload 11
      //   94: aload_3
      //   95: invokestatic 57	rx/exceptions/Exceptions:throwOrReport	(Ljava/lang/Throwable;Lrx/Observer;)V
      //   98: return
      //   99: lload 7
      //   101: lconst_1
      //   102: ladd
      //   103: lstore 7
      //   105: goto -88 -> 17
      //   108: aload_0
      //   109: invokevirtual 63	rx/internal/operators/OnSubscribeFromIterable$IterableProducer:get	()J
      //   112: lstore 5
      //   114: lload 7
      //   116: lload 5
      //   118: lcmp
      //   119: ifne -102 -> 17
      //   122: aload_0
      //   123: lload 7
      //   125: invokestatic 83	rx/internal/operators/BackpressureUtils:produced	(Ljava/util/concurrent/atomic/AtomicLong;J)J
      //   128: lstore 5
      //   130: lload 5
      //   132: lconst_0
      //   133: lcmp
      //   134: ifeq -102 -> 32
      //   137: lconst_0
      //   138: lstore 7
      //   140: goto -123 -> 17
      //
      // Exception table:
      //   from	to	target	type
      //   33	42	81	java/lang/Throwable
      //   55	64	90	java/lang/Throwable
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeFromIterable
 * JD-Core Version:    0.6.0
 */