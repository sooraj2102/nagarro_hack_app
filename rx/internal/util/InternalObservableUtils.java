package rx.internal.util;

import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Notification;
import rx.Observable;
import rx.Observable.Operator;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.operators.OperatorAny;
import rx.observables.ConnectableObservable;

public enum InternalObservableUtils
{
  public static final PlusOneFunc2 COUNTER;
  static final NotificationErrorExtractor ERROR_EXTRACTOR;
  public static final Action1<Throwable> ERROR_NOT_IMPLEMENTED;
  public static final Observable.Operator<Boolean, Object> IS_EMPTY;
  public static final PlusOneLongFunc2 LONG_COUNTER = new PlusOneLongFunc2();
  public static final ObjectEqualsFunc2 OBJECT_EQUALS = new ObjectEqualsFunc2();
  static final ReturnsVoidFunc1 RETURNS_VOID;
  public static final ToArrayFunc1 TO_ARRAY = new ToArrayFunc1();

  static
  {
    RETURNS_VOID = new ReturnsVoidFunc1();
    COUNTER = new PlusOneFunc2();
    ERROR_EXTRACTOR = new NotificationErrorExtractor();
    ERROR_NOT_IMPLEMENTED = new ErrorNotImplementedAction();
    IS_EMPTY = new OperatorAny(UtilityFunctions.alwaysTrue(), true);
  }

  public static <T, R> Func2<R, T, R> createCollectorCaller(Action2<R, ? super T> paramAction2)
  {
    return new CollectorCaller(paramAction2);
  }

  public static Func1<Observable<? extends Notification<?>>, Observable<?>> createRepeatDematerializer(Func1<? super Observable<? extends Void>, ? extends Observable<?>> paramFunc1)
  {
    return new RepeatNotificationDematerializer(paramFunc1);
  }

  public static <T, R> Func1<Observable<T>, Observable<R>> createReplaySelectorAndObserveOn(Func1<? super Observable<T>, ? extends Observable<R>> paramFunc1, Scheduler paramScheduler)
  {
    return new SelectorAndObserveOn(paramFunc1, paramScheduler);
  }

  public static <T> Func0<ConnectableObservable<T>> createReplaySupplier(Observable<T> paramObservable)
  {
    return new ReplaySupplierNoParams(paramObservable);
  }

  public static <T> Func0<ConnectableObservable<T>> createReplaySupplier(Observable<T> paramObservable, int paramInt)
  {
    return new ReplaySupplierBuffer(paramObservable, paramInt);
  }

  public static <T> Func0<ConnectableObservable<T>> createReplaySupplier(Observable<T> paramObservable, int paramInt, long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    return new ReplaySupplierTime(paramObservable, paramInt, paramLong, paramTimeUnit, paramScheduler);
  }

  public static <T> Func0<ConnectableObservable<T>> createReplaySupplier(Observable<T> paramObservable, long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    return new ReplaySupplierBufferTime(paramObservable, paramLong, paramTimeUnit, paramScheduler);
  }

  public static Func1<Observable<? extends Notification<?>>, Observable<?>> createRetryDematerializer(Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> paramFunc1)
  {
    return new RetryNotificationDematerializer(paramFunc1);
  }

  public static Func1<Object, Boolean> equalsWith(Object paramObject)
  {
    return new EqualsWithFunc1(paramObject);
  }

  public static Func1<Object, Boolean> isInstanceOf(Class<?> paramClass)
  {
    return new IsInstanceOfFunc1(paramClass);
  }

  static final class CollectorCaller<T, R>
    implements Func2<R, T, R>
  {
    final Action2<R, ? super T> collector;

    public CollectorCaller(Action2<R, ? super T> paramAction2)
    {
      this.collector = paramAction2;
    }

    public R call(R paramR, T paramT)
    {
      this.collector.call(paramR, paramT);
      return paramR;
    }
  }

  static final class EqualsWithFunc1
    implements Func1<Object, Boolean>
  {
    final Object other;

    public EqualsWithFunc1(Object paramObject)
    {
      this.other = paramObject;
    }

    public Boolean call(Object paramObject)
    {
      if ((paramObject == this.other) || ((paramObject != null) && (paramObject.equals(this.other))));
      for (boolean bool = true; ; bool = false)
        return Boolean.valueOf(bool);
    }
  }

  static final class ErrorNotImplementedAction
    implements Action1<Throwable>
  {
    public void call(Throwable paramThrowable)
    {
      throw new OnErrorNotImplementedException(paramThrowable);
    }
  }

  static final class IsInstanceOfFunc1
    implements Func1<Object, Boolean>
  {
    final Class<?> clazz;

    public IsInstanceOfFunc1(Class<?> paramClass)
    {
      this.clazz = paramClass;
    }

    public Boolean call(Object paramObject)
    {
      return Boolean.valueOf(this.clazz.isInstance(paramObject));
    }
  }

  static final class NotificationErrorExtractor
    implements Func1<Notification<?>, Throwable>
  {
    public Throwable call(Notification<?> paramNotification)
    {
      return paramNotification.getThrowable();
    }
  }

  static final class ObjectEqualsFunc2
    implements Func2<Object, Object, Boolean>
  {
    public Boolean call(Object paramObject1, Object paramObject2)
    {
      if ((paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2))));
      for (boolean bool = true; ; bool = false)
        return Boolean.valueOf(bool);
    }
  }

  static final class PlusOneFunc2
    implements Func2<Integer, Object, Integer>
  {
    public Integer call(Integer paramInteger, Object paramObject)
    {
      return Integer.valueOf(1 + paramInteger.intValue());
    }
  }

  static final class PlusOneLongFunc2
    implements Func2<Long, Object, Long>
  {
    public Long call(Long paramLong, Object paramObject)
    {
      return Long.valueOf(1L + paramLong.longValue());
    }
  }

  static final class RepeatNotificationDematerializer
    implements Func1<Observable<? extends Notification<?>>, Observable<?>>
  {
    final Func1<? super Observable<? extends Void>, ? extends Observable<?>> notificationHandler;

    public RepeatNotificationDematerializer(Func1<? super Observable<? extends Void>, ? extends Observable<?>> paramFunc1)
    {
      this.notificationHandler = paramFunc1;
    }

    public Observable<?> call(Observable<? extends Notification<?>> paramObservable)
    {
      return (Observable)this.notificationHandler.call(paramObservable.map(InternalObservableUtils.RETURNS_VOID));
    }
  }

  static final class ReplaySupplierBuffer<T>
    implements Func0<ConnectableObservable<T>>
  {
    private final int bufferSize;
    private final Observable<T> source;

    ReplaySupplierBuffer(Observable<T> paramObservable, int paramInt)
    {
      this.source = paramObservable;
      this.bufferSize = paramInt;
    }

    public ConnectableObservable<T> call()
    {
      return this.source.replay(this.bufferSize);
    }
  }

  static final class ReplaySupplierBufferTime<T>
    implements Func0<ConnectableObservable<T>>
  {
    private final Scheduler scheduler;
    private final Observable<T> source;
    private final long time;
    private final TimeUnit unit;

    ReplaySupplierBufferTime(Observable<T> paramObservable, long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
    {
      this.unit = paramTimeUnit;
      this.source = paramObservable;
      this.time = paramLong;
      this.scheduler = paramScheduler;
    }

    public ConnectableObservable<T> call()
    {
      return this.source.replay(this.time, this.unit, this.scheduler);
    }
  }

  static final class ReplaySupplierNoParams<T>
    implements Func0<ConnectableObservable<T>>
  {
    private final Observable<T> source;

    ReplaySupplierNoParams(Observable<T> paramObservable)
    {
      this.source = paramObservable;
    }

    public ConnectableObservable<T> call()
    {
      return this.source.replay();
    }
  }

  static final class ReplaySupplierTime<T>
    implements Func0<ConnectableObservable<T>>
  {
    private final int bufferSize;
    private final Scheduler scheduler;
    private final Observable<T> source;
    private final long time;
    private final TimeUnit unit;

    ReplaySupplierTime(Observable<T> paramObservable, int paramInt, long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
    {
      this.time = paramLong;
      this.unit = paramTimeUnit;
      this.scheduler = paramScheduler;
      this.bufferSize = paramInt;
      this.source = paramObservable;
    }

    public ConnectableObservable<T> call()
    {
      return this.source.replay(this.bufferSize, this.time, this.unit, this.scheduler);
    }
  }

  static final class RetryNotificationDematerializer
    implements Func1<Observable<? extends Notification<?>>, Observable<?>>
  {
    final Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> notificationHandler;

    public RetryNotificationDematerializer(Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> paramFunc1)
    {
      this.notificationHandler = paramFunc1;
    }

    public Observable<?> call(Observable<? extends Notification<?>> paramObservable)
    {
      return (Observable)this.notificationHandler.call(paramObservable.map(InternalObservableUtils.ERROR_EXTRACTOR));
    }
  }

  static final class ReturnsVoidFunc1
    implements Func1<Object, Void>
  {
    public Void call(Object paramObject)
    {
      return null;
    }
  }

  static final class SelectorAndObserveOn<T, R>
    implements Func1<Observable<T>, Observable<R>>
  {
    final Scheduler scheduler;
    final Func1<? super Observable<T>, ? extends Observable<R>> selector;

    public SelectorAndObserveOn(Func1<? super Observable<T>, ? extends Observable<R>> paramFunc1, Scheduler paramScheduler)
    {
      this.selector = paramFunc1;
      this.scheduler = paramScheduler;
    }

    public Observable<R> call(Observable<T> paramObservable)
    {
      return ((Observable)this.selector.call(paramObservable)).observeOn(this.scheduler);
    }
  }

  static final class ToArrayFunc1
    implements Func1<List<? extends Observable<?>>, Observable<?>[]>
  {
    public Observable<?>[] call(List<? extends Observable<?>> paramList)
    {
      return (Observable[])paramList.toArray(new Observable[paramList.size()]);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.InternalObservableUtils
 * JD-Core Version:    0.6.0
 */