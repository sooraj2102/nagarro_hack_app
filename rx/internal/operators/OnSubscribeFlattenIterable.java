package rx.internal.operators;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.MissingBackpressureException;
import rx.functions.Func1;
import rx.internal.util.ExceptionsUtils;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.ScalarSynchronousObservable;
import rx.internal.util.atomic.SpscAtomicArrayQueue;
import rx.internal.util.atomic.SpscLinkedArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.plugins.RxJavaHooks;

public final class OnSubscribeFlattenIterable<T, R>
  implements Observable.OnSubscribe<R>
{
  final Func1<? super T, ? extends Iterable<? extends R>> mapper;
  final int prefetch;
  final Observable<? extends T> source;

  protected OnSubscribeFlattenIterable(Observable<? extends T> paramObservable, Func1<? super T, ? extends Iterable<? extends R>> paramFunc1, int paramInt)
  {
    this.source = paramObservable;
    this.mapper = paramFunc1;
    this.prefetch = paramInt;
  }

  public static <T, R> Observable<R> createFrom(Observable<? extends T> paramObservable, Func1<? super T, ? extends Iterable<? extends R>> paramFunc1, int paramInt)
  {
    if ((paramObservable instanceof ScalarSynchronousObservable))
      return Observable.create(new OnSubscribeScalarFlattenIterable(((ScalarSynchronousObservable)paramObservable).get(), paramFunc1));
    return Observable.create(new OnSubscribeFlattenIterable(paramObservable, paramFunc1, paramInt));
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    FlattenIterableSubscriber localFlattenIterableSubscriber = new FlattenIterableSubscriber(paramSubscriber, this.mapper, this.prefetch);
    paramSubscriber.add(localFlattenIterableSubscriber);
    paramSubscriber.setProducer(new Producer(localFlattenIterableSubscriber)
    {
      public void request(long paramLong)
      {
        this.val$parent.requestMore(paramLong);
      }
    });
    this.source.unsafeSubscribe(localFlattenIterableSubscriber);
  }

  static final class FlattenIterableSubscriber<T, R> extends Subscriber<T>
  {
    Iterator<? extends R> active;
    final Subscriber<? super R> actual;
    volatile boolean done;
    final AtomicReference<Throwable> error;
    final long limit;
    final Func1<? super T, ? extends Iterable<? extends R>> mapper;
    final NotificationLite<T> nl;
    long produced;
    final Queue<Object> queue;
    final AtomicLong requested;
    final AtomicInteger wip;

    public FlattenIterableSubscriber(Subscriber<? super R> paramSubscriber, Func1<? super T, ? extends Iterable<? extends R>> paramFunc1, int paramInt)
    {
      this.actual = paramSubscriber;
      this.mapper = paramFunc1;
      this.error = new AtomicReference();
      this.wip = new AtomicInteger();
      this.requested = new AtomicLong();
      this.nl = NotificationLite.instance();
      if (paramInt == 2147483647)
      {
        this.limit = 9223372036854775807L;
        this.queue = new SpscLinkedArrayQueue(RxRingBuffer.SIZE);
      }
      while (true)
      {
        request(paramInt);
        return;
        this.limit = (paramInt - (paramInt >> 2));
        if (UnsafeAccess.isUnsafeAvailable())
        {
          this.queue = new SpscArrayQueue(paramInt);
          continue;
        }
        this.queue = new SpscAtomicArrayQueue(paramInt);
      }
    }

    boolean checkTerminated(boolean paramBoolean1, boolean paramBoolean2, Subscriber<?> paramSubscriber, Queue<?> paramQueue)
    {
      if (paramSubscriber.isUnsubscribed())
      {
        paramQueue.clear();
        this.active = null;
        return true;
      }
      if (paramBoolean1)
      {
        if ((Throwable)this.error.get() != null)
        {
          Throwable localThrowable = ExceptionsUtils.terminate(this.error);
          unsubscribe();
          paramQueue.clear();
          this.active = null;
          paramSubscriber.onError(localThrowable);
          return true;
        }
        if (paramBoolean2)
        {
          paramSubscriber.onCompleted();
          return true;
        }
      }
      return false;
    }

    // ERROR //
    void drain()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 51	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:wip	Ljava/util/concurrent/atomic/AtomicInteger;
      //   4: invokevirtual 137	java/util/concurrent/atomic/AtomicInteger:getAndIncrement	()I
      //   7: ifeq +4 -> 11
      //   10: return
      //   11: aload_0
      //   12: getfield 39	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:actual	Lrx/Subscriber;
      //   15: astore_1
      //   16: aload_0
      //   17: getfield 82	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:queue	Ljava/util/Queue;
      //   20: astore_2
      //   21: iconst_1
      //   22: istore_3
      //   23: aload_0
      //   24: getfield 110	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:active	Ljava/util/Iterator;
      //   27: astore 4
      //   29: aload 4
      //   31: ifnonnull +120 -> 151
      //   34: aload_0
      //   35: getfield 139	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:done	Z
      //   38: istore 17
      //   40: aload_2
      //   41: invokeinterface 142 1 0
      //   46: astore 18
      //   48: aload 18
      //   50: ifnonnull +278 -> 328
      //   53: iconst_1
      //   54: istore 19
      //   56: aload_0
      //   57: iload 17
      //   59: iload 19
      //   61: aload_1
      //   62: aload_2
      //   63: invokevirtual 144	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:checkTerminated	(ZZLrx/Subscriber;Ljava/util/Queue;)Z
      //   66: ifne -56 -> 10
      //   69: iload 19
      //   71: ifne +80 -> 151
      //   74: lconst_1
      //   75: aload_0
      //   76: getfield 146	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:produced	J
      //   79: ladd
      //   80: lstore 20
      //   82: lload 20
      //   84: aload_0
      //   85: getfield 69	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:limit	J
      //   88: lcmp
      //   89: ifne +245 -> 334
      //   92: aload_0
      //   93: lconst_0
      //   94: putfield 146	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:produced	J
      //   97: aload_0
      //   98: lload 20
      //   100: invokevirtual 86	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:request	(J)V
      //   103: aload_0
      //   104: getfield 41	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:mapper	Lrx/functions/Func1;
      //   107: aload_0
      //   108: getfield 64	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:nl	Lrx/internal/operators/NotificationLite;
      //   111: aload 18
      //   113: invokevirtual 150	rx/internal/operators/NotificationLite:getValue	(Ljava/lang/Object;)Ljava/lang/Object;
      //   116: invokeinterface 155 2 0
      //   121: checkcast 157	java/lang/Iterable
      //   124: invokeinterface 161 1 0
      //   129: astore 4
      //   131: aload 4
      //   133: invokeinterface 166 1 0
      //   138: istore 23
      //   140: iload 23
      //   142: ifeq -119 -> 23
      //   145: aload_0
      //   146: aload 4
      //   148: putfield 110	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:active	Ljava/util/Iterator;
      //   151: aload 4
      //   153: ifnull +160 -> 313
      //   156: aload_0
      //   157: getfield 56	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:requested	Ljava/util/concurrent/atomic/AtomicLong;
      //   160: invokevirtual 169	java/util/concurrent/atomic/AtomicLong:get	()J
      //   163: lstore 5
      //   165: lconst_0
      //   166: lstore 7
      //   168: lload 7
      //   170: lload 5
      //   172: lcmp
      //   173: ifeq +74 -> 247
      //   176: aload_0
      //   177: aload_0
      //   178: getfield 139	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:done	Z
      //   181: iconst_0
      //   182: aload_1
      //   183: aload_2
      //   184: invokevirtual 144	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:checkTerminated	(ZZLrx/Subscriber;Ljava/util/Queue;)Z
      //   187: ifne -177 -> 10
      //   190: aload 4
      //   192: invokeinterface 172 1 0
      //   197: astore 14
      //   199: aload_1
      //   200: aload 14
      //   202: invokevirtual 176	rx/Subscriber:onNext	(Ljava/lang/Object;)V
      //   205: aload_0
      //   206: aload_0
      //   207: getfield 139	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:done	Z
      //   210: iconst_0
      //   211: aload_1
      //   212: aload_2
      //   213: invokevirtual 144	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:checkTerminated	(ZZLrx/Subscriber;Ljava/util/Queue;)Z
      //   216: ifne -206 -> 10
      //   219: lload 7
      //   221: lconst_1
      //   222: ladd
      //   223: lstore 7
      //   225: aload 4
      //   227: invokeinterface 166 1 0
      //   232: istore 16
      //   234: iload 16
      //   236: ifne -68 -> 168
      //   239: aconst_null
      //   240: astore 4
      //   242: aload_0
      //   243: aconst_null
      //   244: putfield 110	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:active	Ljava/util/Iterator;
      //   247: lload 7
      //   249: lload 5
      //   251: lcmp
      //   252: ifne +39 -> 291
      //   255: aload_0
      //   256: getfield 139	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:done	Z
      //   259: istore 11
      //   261: aload_2
      //   262: invokeinterface 179 1 0
      //   267: ifeq +140 -> 407
      //   270: aload 4
      //   272: ifnonnull +135 -> 407
      //   275: iconst_1
      //   276: istore 12
      //   278: aload_0
      //   279: iload 11
      //   281: iload 12
      //   283: aload_1
      //   284: aload_2
      //   285: invokevirtual 144	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:checkTerminated	(ZZLrx/Subscriber;Ljava/util/Queue;)Z
      //   288: ifne -278 -> 10
      //   291: lload 7
      //   293: lconst_0
      //   294: lcmp
      //   295: ifeq +13 -> 308
      //   298: aload_0
      //   299: getfield 56	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:requested	Ljava/util/concurrent/atomic/AtomicLong;
      //   302: lload 7
      //   304: invokestatic 184	rx/internal/operators/BackpressureUtils:produced	(Ljava/util/concurrent/atomic/AtomicLong;J)J
      //   307: pop2
      //   308: aload 4
      //   310: ifnull -287 -> 23
      //   313: aload_0
      //   314: getfield 51	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:wip	Ljava/util/concurrent/atomic/AtomicInteger;
      //   317: iload_3
      //   318: ineg
      //   319: invokevirtual 188	java/util/concurrent/atomic/AtomicInteger:addAndGet	(I)I
      //   322: istore_3
      //   323: iload_3
      //   324: ifne -301 -> 23
      //   327: return
      //   328: iconst_0
      //   329: istore 19
      //   331: goto -275 -> 56
      //   334: aload_0
      //   335: lload 20
      //   337: putfield 146	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:produced	J
      //   340: goto -237 -> 103
      //   343: astore 22
      //   345: aload 22
      //   347: invokestatic 193	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
      //   350: aload_0
      //   351: aload 22
      //   353: invokevirtual 194	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:onError	(Ljava/lang/Throwable;)V
      //   356: goto -333 -> 23
      //   359: astore 13
      //   361: aload 13
      //   363: invokestatic 193	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
      //   366: aload_0
      //   367: aconst_null
      //   368: putfield 110	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:active	Ljava/util/Iterator;
      //   371: aload_0
      //   372: aload 13
      //   374: invokevirtual 194	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:onError	(Ljava/lang/Throwable;)V
      //   377: aconst_null
      //   378: astore 4
      //   380: goto -133 -> 247
      //   383: astore 15
      //   385: aload 15
      //   387: invokestatic 193	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
      //   390: aload_0
      //   391: aconst_null
      //   392: putfield 110	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:active	Ljava/util/Iterator;
      //   395: aload_0
      //   396: aload 15
      //   398: invokevirtual 194	rx/internal/operators/OnSubscribeFlattenIterable$FlattenIterableSubscriber:onError	(Ljava/lang/Throwable;)V
      //   401: aconst_null
      //   402: astore 4
      //   404: goto -157 -> 247
      //   407: iconst_0
      //   408: istore 12
      //   410: goto -132 -> 278
      //
      // Exception table:
      //   from	to	target	type
      //   103	140	343	java/lang/Throwable
      //   190	199	359	java/lang/Throwable
      //   225	234	383	java/lang/Throwable
    }

    public void onCompleted()
    {
      this.done = true;
      drain();
    }

    public void onError(Throwable paramThrowable)
    {
      if (ExceptionsUtils.addThrowable(this.error, paramThrowable))
      {
        this.done = true;
        drain();
        return;
      }
      RxJavaHooks.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      if (!this.queue.offer(this.nl.next(paramT)))
      {
        unsubscribe();
        onError(new MissingBackpressureException());
        return;
      }
      drain();
    }

    void requestMore(long paramLong)
    {
      if (paramLong > 0L)
      {
        BackpressureUtils.getAndAddRequest(this.requested, paramLong);
        drain();
      }
      do
        return;
      while (paramLong >= 0L);
      throw new IllegalStateException("n >= 0 required but it was " + paramLong);
    }
  }

  static final class OnSubscribeScalarFlattenIterable<T, R>
    implements Observable.OnSubscribe<R>
  {
    final Func1<? super T, ? extends Iterable<? extends R>> mapper;
    final T value;

    public OnSubscribeScalarFlattenIterable(T paramT, Func1<? super T, ? extends Iterable<? extends R>> paramFunc1)
    {
      this.value = paramT;
      this.mapper = paramFunc1;
    }

    public void call(Subscriber<? super R> paramSubscriber)
    {
      Iterator localIterator;
      try
      {
        localIterator = ((Iterable)this.mapper.call(this.value)).iterator();
        boolean bool = localIterator.hasNext();
        if (!bool)
        {
          paramSubscriber.onCompleted();
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwOrReport(localThrowable, paramSubscriber, this.value);
        return;
      }
      paramSubscriber.setProducer(new OnSubscribeFromIterable.IterableProducer(paramSubscriber, localIterator));
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeFlattenIterable
 * JD-Core Version:    0.6.0
 */