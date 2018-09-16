package rx;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.annotations.Beta;
import rx.annotations.Experimental;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.functions.Func5;
import rx.functions.Func6;
import rx.functions.Func7;
import rx.functions.Func8;
import rx.functions.Func9;
import rx.functions.FuncN;
import rx.internal.operators.CompletableFlatMapSingleToCompletable;
import rx.internal.operators.OnSubscribeToObservableFuture;
import rx.internal.operators.OperatorDelay;
import rx.internal.operators.OperatorDoOnSubscribe;
import rx.internal.operators.OperatorDoOnUnsubscribe;
import rx.internal.operators.OperatorObserveOn;
import rx.internal.operators.OperatorOnErrorResumeNextViaFunction;
import rx.internal.operators.OperatorTimeout;
import rx.internal.operators.SingleDoAfterTerminate;
import rx.internal.operators.SingleDoOnEvent;
import rx.internal.operators.SingleOnSubscribeDelaySubscriptionOther;
import rx.internal.operators.SingleOnSubscribeMap;
import rx.internal.operators.SingleOnSubscribeUsing;
import rx.internal.operators.SingleOperatorOnErrorResumeNext;
import rx.internal.operators.SingleOperatorZip;
import rx.internal.producers.SingleDelayedProducer;
import rx.internal.util.ScalarSynchronousSingle;
import rx.internal.util.UtilityFunctions;
import rx.observers.SafeSubscriber;
import rx.observers.SerializedSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.singles.BlockingSingle;
import rx.subscriptions.Subscriptions;

public class Single<T>
{
  final Observable.OnSubscribe<T> onSubscribe;

  private Single(Observable.OnSubscribe<T> paramOnSubscribe)
  {
    this.onSubscribe = RxJavaHooks.onCreate(paramOnSubscribe);
  }

  protected Single(OnSubscribe<T> paramOnSubscribe)
  {
    this.onSubscribe = new Observable.OnSubscribe(RxJavaHooks.onCreate(paramOnSubscribe))
    {
      public void call(Subscriber<? super T> paramSubscriber)
      {
        SingleDelayedProducer localSingleDelayedProducer = new SingleDelayedProducer(paramSubscriber);
        paramSubscriber.setProducer(localSingleDelayedProducer);
        1 local1 = new SingleSubscriber(localSingleDelayedProducer, paramSubscriber)
        {
          public void onError(Throwable paramThrowable)
          {
            this.val$child.onError(paramThrowable);
          }

          public void onSuccess(T paramT)
          {
            this.val$producer.setValue(paramT);
          }
        };
        paramSubscriber.add(local1);
        this.val$g.call(local1);
      }
    };
  }

  private static <T> Observable<T> asObservable(Single<T> paramSingle)
  {
    return Observable.create(paramSingle.onSubscribe);
  }

  public static <T> Observable<T> concat(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2)
  {
    return Observable.concat(asObservable(paramSingle1), asObservable(paramSingle2));
  }

  public static <T> Observable<T> concat(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3)
  {
    return Observable.concat(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3));
  }

  public static <T> Observable<T> concat(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4)
  {
    return Observable.concat(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4));
  }

  public static <T> Observable<T> concat(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5)
  {
    return Observable.concat(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5));
  }

  public static <T> Observable<T> concat(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5, Single<? extends T> paramSingle6)
  {
    return Observable.concat(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5), asObservable(paramSingle6));
  }

  public static <T> Observable<T> concat(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5, Single<? extends T> paramSingle6, Single<? extends T> paramSingle7)
  {
    return Observable.concat(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5), asObservable(paramSingle6), asObservable(paramSingle7));
  }

  public static <T> Observable<T> concat(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5, Single<? extends T> paramSingle6, Single<? extends T> paramSingle7, Single<? extends T> paramSingle8)
  {
    return Observable.concat(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5), asObservable(paramSingle6), asObservable(paramSingle7), asObservable(paramSingle8));
  }

  public static <T> Observable<T> concat(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5, Single<? extends T> paramSingle6, Single<? extends T> paramSingle7, Single<? extends T> paramSingle8, Single<? extends T> paramSingle9)
  {
    return Observable.concat(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5), asObservable(paramSingle6), asObservable(paramSingle7), asObservable(paramSingle8), asObservable(paramSingle9));
  }

  public static <T> Single<T> create(OnSubscribe<T> paramOnSubscribe)
  {
    return new Single(paramOnSubscribe);
  }

  @Beta
  public static <T> Single<T> defer(Callable<Single<T>> paramCallable)
  {
    return create(new OnSubscribe(paramCallable)
    {
      public void call(SingleSubscriber<? super T> paramSingleSubscriber)
      {
        try
        {
          Single localSingle = (Single)this.val$singleFactory.call();
          localSingle.subscribe(paramSingleSubscriber);
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwIfFatal(localThrowable);
          paramSingleSubscriber.onError(localThrowable);
        }
      }
    });
  }

  public static <T> Single<T> error(Throwable paramThrowable)
  {
    return create(new OnSubscribe(paramThrowable)
    {
      public void call(SingleSubscriber<? super T> paramSingleSubscriber)
      {
        paramSingleSubscriber.onError(this.val$exception);
      }
    });
  }

  public static <T> Single<T> from(Future<? extends T> paramFuture)
  {
    return new Single(OnSubscribeToObservableFuture.toObservableFuture(paramFuture));
  }

  public static <T> Single<T> from(Future<? extends T> paramFuture, long paramLong, TimeUnit paramTimeUnit)
  {
    return new Single(OnSubscribeToObservableFuture.toObservableFuture(paramFuture, paramLong, paramTimeUnit));
  }

  public static <T> Single<T> from(Future<? extends T> paramFuture, Scheduler paramScheduler)
  {
    return new Single(OnSubscribeToObservableFuture.toObservableFuture(paramFuture)).subscribeOn(paramScheduler);
  }

  public static <T> Single<T> fromCallable(Callable<? extends T> paramCallable)
  {
    return create(new OnSubscribe(paramCallable)
    {
      public void call(SingleSubscriber<? super T> paramSingleSubscriber)
      {
        try
        {
          Object localObject = this.val$func.call();
          paramSingleSubscriber.onSuccess(localObject);
          return;
        }
        catch (Throwable localThrowable)
        {
          Exceptions.throwIfFatal(localThrowable);
          paramSingleSubscriber.onError(localThrowable);
        }
      }
    });
  }

  static <T> Single<? extends T>[] iterableToArray(Iterable<? extends Single<? extends T>> paramIterable)
  {
    if ((paramIterable instanceof Collection))
    {
      Collection localCollection = (Collection)paramIterable;
      return (Single[])localCollection.toArray(new Single[localCollection.size()]);
    }
    Object localObject = new Single[8];
    int i = 0;
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Single localSingle = (Single)localIterator.next();
      if (i == localObject.length)
      {
        Single[] arrayOfSingle2 = new Single[i + (i >> 2)];
        System.arraycopy(localObject, 0, arrayOfSingle2, 0, i);
        localObject = arrayOfSingle2;
      }
      localObject[i] = localSingle;
      i++;
    }
    if (localObject.length == i)
      return localObject;
    Single[] arrayOfSingle1 = new Single[i];
    System.arraycopy(localObject, 0, arrayOfSingle1, 0, i);
    return (Single<? extends T>)arrayOfSingle1;
  }

  public static <T> Single<T> just(T paramT)
  {
    return ScalarSynchronousSingle.create(paramT);
  }

  public static <T> Observable<T> merge(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2)
  {
    return Observable.merge(asObservable(paramSingle1), asObservable(paramSingle2));
  }

  public static <T> Observable<T> merge(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3)
  {
    return Observable.merge(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3));
  }

  public static <T> Observable<T> merge(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4)
  {
    return Observable.merge(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4));
  }

  public static <T> Observable<T> merge(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5)
  {
    return Observable.merge(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5));
  }

  public static <T> Observable<T> merge(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5, Single<? extends T> paramSingle6)
  {
    return Observable.merge(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5), asObservable(paramSingle6));
  }

  public static <T> Observable<T> merge(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5, Single<? extends T> paramSingle6, Single<? extends T> paramSingle7)
  {
    return Observable.merge(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5), asObservable(paramSingle6), asObservable(paramSingle7));
  }

  public static <T> Observable<T> merge(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5, Single<? extends T> paramSingle6, Single<? extends T> paramSingle7, Single<? extends T> paramSingle8)
  {
    return Observable.merge(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5), asObservable(paramSingle6), asObservable(paramSingle7), asObservable(paramSingle8));
  }

  public static <T> Observable<T> merge(Single<? extends T> paramSingle1, Single<? extends T> paramSingle2, Single<? extends T> paramSingle3, Single<? extends T> paramSingle4, Single<? extends T> paramSingle5, Single<? extends T> paramSingle6, Single<? extends T> paramSingle7, Single<? extends T> paramSingle8, Single<? extends T> paramSingle9)
  {
    return Observable.merge(asObservable(paramSingle1), asObservable(paramSingle2), asObservable(paramSingle3), asObservable(paramSingle4), asObservable(paramSingle5), asObservable(paramSingle6), asObservable(paramSingle7), asObservable(paramSingle8), asObservable(paramSingle9));
  }

  public static <T> Single<T> merge(Single<? extends Single<? extends T>> paramSingle)
  {
    if ((paramSingle instanceof ScalarSynchronousSingle))
      return ((ScalarSynchronousSingle)paramSingle).scalarFlatMap(UtilityFunctions.identity());
    return create(new OnSubscribe(paramSingle)
    {
      public void call(SingleSubscriber<? super T> paramSingleSubscriber)
      {
        1 local1 = new SingleSubscriber(paramSingleSubscriber)
        {
          public void onError(Throwable paramThrowable)
          {
            this.val$child.onError(paramThrowable);
          }

          public void onSuccess(Single<? extends T> paramSingle)
          {
            paramSingle.subscribe(this.val$child);
          }
        };
        paramSingleSubscriber.add(local1);
        this.val$source.subscribe(local1);
      }
    });
  }

  @Beta
  public static <T, Resource> Single<T> using(Func0<Resource> paramFunc0, Func1<? super Resource, ? extends Single<? extends T>> paramFunc1, Action1<? super Resource> paramAction1)
  {
    return using(paramFunc0, paramFunc1, paramAction1, false);
  }

  @Beta
  public static <T, Resource> Single<T> using(Func0<Resource> paramFunc0, Func1<? super Resource, ? extends Single<? extends T>> paramFunc1, Action1<? super Resource> paramAction1, boolean paramBoolean)
  {
    if (paramFunc0 == null)
      throw new NullPointerException("resourceFactory is null");
    if (paramFunc1 == null)
      throw new NullPointerException("singleFactory is null");
    if (paramAction1 == null)
      throw new NullPointerException("disposeAction is null");
    return create(new SingleOnSubscribeUsing(paramFunc0, paramFunc1, paramAction1, paramBoolean));
  }

  public static <R> Single<R> zip(Iterable<? extends Single<?>> paramIterable, FuncN<? extends R> paramFuncN)
  {
    return SingleOperatorZip.zip(iterableToArray(paramIterable), paramFuncN);
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Single<R> zip(Single<? extends T1> paramSingle, Single<? extends T2> paramSingle1, Single<? extends T3> paramSingle2, Single<? extends T4> paramSingle3, Single<? extends T5> paramSingle4, Single<? extends T6> paramSingle5, Single<? extends T7> paramSingle6, Single<? extends T8> paramSingle7, Single<? extends T9> paramSingle8, Func9<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends R> paramFunc9)
  {
    return SingleOperatorZip.zip(new Single[] { paramSingle, paramSingle1, paramSingle2, paramSingle3, paramSingle4, paramSingle5, paramSingle6, paramSingle7, paramSingle8 }, new FuncN(paramFunc9)
    {
      public R call(Object[] paramArrayOfObject)
      {
        return this.val$zipFunction.call(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3], paramArrayOfObject[4], paramArrayOfObject[5], paramArrayOfObject[6], paramArrayOfObject[7], paramArrayOfObject[8]);
      }
    });
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Single<R> zip(Single<? extends T1> paramSingle, Single<? extends T2> paramSingle1, Single<? extends T3> paramSingle2, Single<? extends T4> paramSingle3, Single<? extends T5> paramSingle4, Single<? extends T6> paramSingle5, Single<? extends T7> paramSingle6, Single<? extends T8> paramSingle7, Func8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R> paramFunc8)
  {
    return SingleOperatorZip.zip(new Single[] { paramSingle, paramSingle1, paramSingle2, paramSingle3, paramSingle4, paramSingle5, paramSingle6, paramSingle7 }, new FuncN(paramFunc8)
    {
      public R call(Object[] paramArrayOfObject)
      {
        return this.val$zipFunction.call(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3], paramArrayOfObject[4], paramArrayOfObject[5], paramArrayOfObject[6], paramArrayOfObject[7]);
      }
    });
  }

  public static <T1, T2, T3, T4, T5, T6, T7, R> Single<R> zip(Single<? extends T1> paramSingle, Single<? extends T2> paramSingle1, Single<? extends T3> paramSingle2, Single<? extends T4> paramSingle3, Single<? extends T5> paramSingle4, Single<? extends T6> paramSingle5, Single<? extends T7> paramSingle6, Func7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R> paramFunc7)
  {
    return SingleOperatorZip.zip(new Single[] { paramSingle, paramSingle1, paramSingle2, paramSingle3, paramSingle4, paramSingle5, paramSingle6 }, new FuncN(paramFunc7)
    {
      public R call(Object[] paramArrayOfObject)
      {
        return this.val$zipFunction.call(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3], paramArrayOfObject[4], paramArrayOfObject[5], paramArrayOfObject[6]);
      }
    });
  }

  public static <T1, T2, T3, T4, T5, T6, R> Single<R> zip(Single<? extends T1> paramSingle, Single<? extends T2> paramSingle1, Single<? extends T3> paramSingle2, Single<? extends T4> paramSingle3, Single<? extends T5> paramSingle4, Single<? extends T6> paramSingle5, Func6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> paramFunc6)
  {
    return SingleOperatorZip.zip(new Single[] { paramSingle, paramSingle1, paramSingle2, paramSingle3, paramSingle4, paramSingle5 }, new FuncN(paramFunc6)
    {
      public R call(Object[] paramArrayOfObject)
      {
        return this.val$zipFunction.call(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3], paramArrayOfObject[4], paramArrayOfObject[5]);
      }
    });
  }

  public static <T1, T2, T3, T4, T5, R> Single<R> zip(Single<? extends T1> paramSingle, Single<? extends T2> paramSingle1, Single<? extends T3> paramSingle2, Single<? extends T4> paramSingle3, Single<? extends T5> paramSingle4, Func5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R> paramFunc5)
  {
    return SingleOperatorZip.zip(new Single[] { paramSingle, paramSingle1, paramSingle2, paramSingle3, paramSingle4 }, new FuncN(paramFunc5)
    {
      public R call(Object[] paramArrayOfObject)
      {
        return this.val$zipFunction.call(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3], paramArrayOfObject[4]);
      }
    });
  }

  public static <T1, T2, T3, T4, R> Single<R> zip(Single<? extends T1> paramSingle, Single<? extends T2> paramSingle1, Single<? extends T3> paramSingle2, Single<? extends T4> paramSingle3, Func4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> paramFunc4)
  {
    return SingleOperatorZip.zip(new Single[] { paramSingle, paramSingle1, paramSingle2, paramSingle3 }, new FuncN(paramFunc4)
    {
      public R call(Object[] paramArrayOfObject)
      {
        return this.val$zipFunction.call(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3]);
      }
    });
  }

  public static <T1, T2, T3, R> Single<R> zip(Single<? extends T1> paramSingle, Single<? extends T2> paramSingle1, Single<? extends T3> paramSingle2, Func3<? super T1, ? super T2, ? super T3, ? extends R> paramFunc3)
  {
    return SingleOperatorZip.zip(new Single[] { paramSingle, paramSingle1, paramSingle2 }, new FuncN(paramFunc3)
    {
      public R call(Object[] paramArrayOfObject)
      {
        return this.val$zipFunction.call(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2]);
      }
    });
  }

  public static <T1, T2, R> Single<R> zip(Single<? extends T1> paramSingle, Single<? extends T2> paramSingle1, Func2<? super T1, ? super T2, ? extends R> paramFunc2)
  {
    return SingleOperatorZip.zip(new Single[] { paramSingle, paramSingle1 }, new FuncN(paramFunc2)
    {
      public R call(Object[] paramArrayOfObject)
      {
        return this.val$zipFunction.call(paramArrayOfObject[0], paramArrayOfObject[1]);
      }
    });
  }

  public <R> Single<R> compose(Transformer<? super T, ? extends R> paramTransformer)
  {
    return (Single)paramTransformer.call(this);
  }

  public final Observable<T> concatWith(Single<? extends T> paramSingle)
  {
    return concat(this, paramSingle);
  }

  @Beta
  public final Single<T> delay(long paramLong, TimeUnit paramTimeUnit)
  {
    return delay(paramLong, paramTimeUnit, Schedulers.computation());
  }

  @Beta
  public final Single<T> delay(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    return lift(new OperatorDelay(paramLong, paramTimeUnit, paramScheduler));
  }

  @Beta
  public final Single<T> delaySubscription(Observable<?> paramObservable)
  {
    if (paramObservable == null)
      throw new NullPointerException();
    return create(new SingleOnSubscribeDelaySubscriptionOther(this, paramObservable));
  }

  @Beta
  public final Single<T> doAfterTerminate(Action0 paramAction0)
  {
    return create(new SingleDoAfterTerminate(this, paramAction0));
  }

  @Experimental
  public final Single<T> doOnEach(Action1<Notification<? extends T>> paramAction1)
  {
    if (paramAction1 == null)
      throw new IllegalArgumentException("onNotification is null");
    return create(new SingleDoOnEvent(this, new Action1(paramAction1)
    {
      public void call(T paramT)
      {
        this.val$onNotification.call(Notification.createOnNext(paramT));
      }
    }
    , new Action1(paramAction1)
    {
      public void call(Throwable paramThrowable)
      {
        this.val$onNotification.call(Notification.createOnError(paramThrowable));
      }
    }));
  }

  @Beta
  public final Single<T> doOnError(Action1<Throwable> paramAction1)
  {
    if (paramAction1 == null)
      throw new IllegalArgumentException("onError is null");
    return create(new SingleDoOnEvent(this, Actions.empty(), new Action1(paramAction1)
    {
      public void call(Throwable paramThrowable)
      {
        this.val$onError.call(paramThrowable);
      }
    }));
  }

  @Beta
  public final Single<T> doOnSubscribe(Action0 paramAction0)
  {
    return lift(new OperatorDoOnSubscribe(paramAction0));
  }

  @Experimental
  public final Single<T> doOnSuccess(Action1<? super T> paramAction1)
  {
    if (paramAction1 == null)
      throw new IllegalArgumentException("onSuccess is null");
    return create(new SingleDoOnEvent(this, new Action1(paramAction1)
    {
      public void call(T paramT)
      {
        this.val$onSuccess.call(paramT);
      }
    }
    , new Action1()
    {
      public void call(Throwable paramThrowable)
      {
      }
    }));
  }

  @Beta
  public final Single<T> doOnUnsubscribe(Action0 paramAction0)
  {
    return lift(new OperatorDoOnUnsubscribe(paramAction0));
  }

  public final <R> Single<R> flatMap(Func1<? super T, ? extends Single<? extends R>> paramFunc1)
  {
    if ((this instanceof ScalarSynchronousSingle))
      return ((ScalarSynchronousSingle)this).scalarFlatMap(paramFunc1);
    return merge(map(paramFunc1));
  }

  @Beta
  public final Completable flatMapCompletable(Func1<? super T, ? extends Completable> paramFunc1)
  {
    return Completable.create(new CompletableFlatMapSingleToCompletable(this, paramFunc1));
  }

  public final <R> Observable<R> flatMapObservable(Func1<? super T, ? extends Observable<? extends R>> paramFunc1)
  {
    return Observable.merge(asObservable(map(paramFunc1)));
  }

  @Beta
  public final <R> Single<R> lift(Observable.Operator<? extends R, ? super T> paramOperator)
  {
    return new Single(new Observable.OnSubscribe(paramOperator)
    {
      public void call(Subscriber<? super R> paramSubscriber)
      {
        try
        {
          Subscriber localSubscriber = (Subscriber)RxJavaHooks.onSingleLift(this.val$lift).call(paramSubscriber);
          try
          {
            localSubscriber.onStart();
            Single.this.onSubscribe.call(localSubscriber);
            return;
          }
          catch (Throwable localThrowable2)
          {
            Exceptions.throwOrReport(localThrowable2, localSubscriber);
            return;
          }
        }
        catch (Throwable localThrowable1)
        {
          Exceptions.throwOrReport(localThrowable1, paramSubscriber);
        }
      }
    });
  }

  public final <R> Single<R> map(Func1<? super T, ? extends R> paramFunc1)
  {
    return create(new SingleOnSubscribeMap(this, paramFunc1));
  }

  public final Observable<T> mergeWith(Single<? extends T> paramSingle)
  {
    return merge(this, paramSingle);
  }

  public final Single<T> observeOn(Scheduler paramScheduler)
  {
    if ((this instanceof ScalarSynchronousSingle))
      return ((ScalarSynchronousSingle)this).scalarScheduleOn(paramScheduler);
    return lift(new OperatorObserveOn(paramScheduler, false));
  }

  @Beta
  public final Single<T> onErrorResumeNext(Single<? extends T> paramSingle)
  {
    return new Single(SingleOperatorOnErrorResumeNext.withOther(this, paramSingle));
  }

  @Beta
  public final Single<T> onErrorResumeNext(Func1<Throwable, ? extends Single<? extends T>> paramFunc1)
  {
    return new Single(SingleOperatorOnErrorResumeNext.withFunction(this, paramFunc1));
  }

  public final Single<T> onErrorReturn(Func1<Throwable, ? extends T> paramFunc1)
  {
    return lift(OperatorOnErrorResumeNextViaFunction.withSingle(paramFunc1));
  }

  public final Single<T> retry()
  {
    return toObservable().retry().toSingle();
  }

  public final Single<T> retry(long paramLong)
  {
    return toObservable().retry(paramLong).toSingle();
  }

  public final Single<T> retry(Func2<Integer, Throwable, Boolean> paramFunc2)
  {
    return toObservable().retry(paramFunc2).toSingle();
  }

  public final Single<T> retryWhen(Func1<Observable<? extends Throwable>, ? extends Observable<?>> paramFunc1)
  {
    return toObservable().retryWhen(paramFunc1).toSingle();
  }

  public final Subscription subscribe()
  {
    return subscribe(new Subscriber()
    {
      public final void onCompleted()
      {
      }

      public final void onError(Throwable paramThrowable)
      {
        throw new OnErrorNotImplementedException(paramThrowable);
      }

      public final void onNext(T paramT)
      {
      }
    });
  }

  public final Subscription subscribe(Observer<? super T> paramObserver)
  {
    if (paramObserver == null)
      throw new NullPointerException("observer is null");
    return subscribe(new SingleSubscriber(paramObserver)
    {
      public void onError(Throwable paramThrowable)
      {
        this.val$observer.onError(paramThrowable);
      }

      public void onSuccess(T paramT)
      {
        this.val$observer.onNext(paramT);
        this.val$observer.onCompleted();
      }
    });
  }

  public final Subscription subscribe(SingleSubscriber<? super T> paramSingleSubscriber)
  {
    18 local18 = new Subscriber(paramSingleSubscriber)
    {
      public void onCompleted()
      {
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$te.onError(paramThrowable);
      }

      public void onNext(T paramT)
      {
        this.val$te.onSuccess(paramT);
      }
    };
    paramSingleSubscriber.add(local18);
    subscribe(local18);
    return local18;
  }

  public final Subscription subscribe(Subscriber<? super T> paramSubscriber)
  {
    if (paramSubscriber == null)
      throw new IllegalArgumentException("observer can not be null");
    if (this.onSubscribe == null)
      throw new IllegalStateException("onSubscribe function can not be null.");
    paramSubscriber.onStart();
    if (!(paramSubscriber instanceof SafeSubscriber))
      paramSubscriber = new SafeSubscriber(paramSubscriber);
    RuntimeException localRuntimeException;
    try
    {
      RxJavaHooks.onSingleStart(this, this.onSubscribe).call(paramSubscriber);
      Subscription localSubscription = RxJavaHooks.onSingleReturn(paramSubscriber);
      return localSubscription;
    }
    catch (Throwable localThrowable1)
    {
      Exceptions.throwIfFatal(localThrowable1);
      try
      {
        paramSubscriber.onError(RxJavaHooks.onSingleError(localThrowable1));
        return Subscriptions.empty();
      }
      catch (Throwable localThrowable2)
      {
        Exceptions.throwIfFatal(localThrowable2);
        localRuntimeException = new RuntimeException("Error occurred attempting to subscribe [" + localThrowable1.getMessage() + "] and then again while trying to pass to onError.", localThrowable2);
        RxJavaHooks.onSingleError(localRuntimeException);
      }
    }
    throw localRuntimeException;
  }

  public final Subscription subscribe(Action1<? super T> paramAction1)
  {
    if (paramAction1 == null)
      throw new IllegalArgumentException("onSuccess can not be null");
    return subscribe(new Subscriber(paramAction1)
    {
      public final void onCompleted()
      {
      }

      public final void onError(Throwable paramThrowable)
      {
        throw new OnErrorNotImplementedException(paramThrowable);
      }

      public final void onNext(T paramT)
      {
        this.val$onSuccess.call(paramT);
      }
    });
  }

  public final Subscription subscribe(Action1<? super T> paramAction1, Action1<Throwable> paramAction11)
  {
    if (paramAction1 == null)
      throw new IllegalArgumentException("onSuccess can not be null");
    if (paramAction11 == null)
      throw new IllegalArgumentException("onError can not be null");
    return subscribe(new Subscriber(paramAction11, paramAction1)
    {
      public final void onCompleted()
      {
      }

      public final void onError(Throwable paramThrowable)
      {
        this.val$onError.call(paramThrowable);
      }

      public final void onNext(T paramT)
      {
        this.val$onSuccess.call(paramT);
      }
    });
  }

  public final Single<T> subscribeOn(Scheduler paramScheduler)
  {
    if ((this instanceof ScalarSynchronousSingle))
      return ((ScalarSynchronousSingle)this).scalarScheduleOn(paramScheduler);
    return create(new OnSubscribe(paramScheduler)
    {
      public void call(SingleSubscriber<? super T> paramSingleSubscriber)
      {
        Scheduler.Worker localWorker = this.val$scheduler.createWorker();
        paramSingleSubscriber.add(localWorker);
        localWorker.schedule(new Action0(paramSingleSubscriber, localWorker)
        {
          public void call()
          {
            1 local1 = new SingleSubscriber()
            {
              public void onError(Throwable paramThrowable)
              {
                try
                {
                  Single.19.1.this.val$t.onError(paramThrowable);
                  return;
                }
                finally
                {
                  Single.19.1.this.val$w.unsubscribe();
                }
                throw localObject;
              }

              public void onSuccess(T paramT)
              {
                try
                {
                  Single.19.1.this.val$t.onSuccess(paramT);
                  return;
                }
                finally
                {
                  Single.19.1.this.val$w.unsubscribe();
                }
                throw localObject;
              }
            };
            this.val$t.add(local1);
            Single.this.subscribe(local1);
          }
        });
      }
    });
  }

  public final Single<T> takeUntil(Completable paramCompletable)
  {
    return lift(new Observable.Operator(paramCompletable)
    {
      public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
      {
        SerializedSubscriber localSerializedSubscriber = new SerializedSubscriber(paramSubscriber, false);
        1 local1 = new Subscriber(localSerializedSubscriber, false, localSerializedSubscriber)
        {
          public void onCompleted()
          {
            try
            {
              this.val$serial.onCompleted();
              return;
            }
            finally
            {
              this.val$serial.unsubscribe();
            }
            throw localObject;
          }

          public void onError(Throwable paramThrowable)
          {
            try
            {
              this.val$serial.onError(paramThrowable);
              return;
            }
            finally
            {
              this.val$serial.unsubscribe();
            }
            throw localObject;
          }

          public void onNext(T paramT)
          {
            this.val$serial.onNext(paramT);
          }
        };
        2 local2 = new CompletableSubscriber(local1, localSerializedSubscriber)
        {
          public void onCompleted()
          {
            onError(new CancellationException("Stream was canceled before emitting a terminal event."));
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$main.onError(paramThrowable);
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            this.val$serial.add(paramSubscription);
          }
        };
        localSerializedSubscriber.add(local1);
        paramSubscriber.add(localSerializedSubscriber);
        this.val$other.unsafeSubscribe(local2);
        return local1;
      }
    });
  }

  public final <E> Single<T> takeUntil(Observable<? extends E> paramObservable)
  {
    return lift(new Observable.Operator(paramObservable)
    {
      public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
      {
        SerializedSubscriber localSerializedSubscriber = new SerializedSubscriber(paramSubscriber, false);
        1 local1 = new Subscriber(localSerializedSubscriber, false, localSerializedSubscriber)
        {
          public void onCompleted()
          {
            try
            {
              this.val$serial.onCompleted();
              return;
            }
            finally
            {
              this.val$serial.unsubscribe();
            }
            throw localObject;
          }

          public void onError(Throwable paramThrowable)
          {
            try
            {
              this.val$serial.onError(paramThrowable);
              return;
            }
            finally
            {
              this.val$serial.unsubscribe();
            }
            throw localObject;
          }

          public void onNext(T paramT)
          {
            this.val$serial.onNext(paramT);
          }
        };
        2 local2 = new Subscriber(local1)
        {
          public void onCompleted()
          {
            onError(new CancellationException("Stream was canceled before emitting a terminal event."));
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$main.onError(paramThrowable);
          }

          public void onNext(E paramE)
          {
            onError(new CancellationException("Stream was canceled before emitting a terminal event."));
          }
        };
        localSerializedSubscriber.add(local1);
        localSerializedSubscriber.add(local2);
        paramSubscriber.add(localSerializedSubscriber);
        this.val$other.unsafeSubscribe(local2);
        return local1;
      }
    });
  }

  public final <E> Single<T> takeUntil(Single<? extends E> paramSingle)
  {
    return lift(new Observable.Operator(paramSingle)
    {
      public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
      {
        SerializedSubscriber localSerializedSubscriber = new SerializedSubscriber(paramSubscriber, false);
        1 local1 = new Subscriber(localSerializedSubscriber, false, localSerializedSubscriber)
        {
          public void onCompleted()
          {
            try
            {
              this.val$serial.onCompleted();
              return;
            }
            finally
            {
              this.val$serial.unsubscribe();
            }
            throw localObject;
          }

          public void onError(Throwable paramThrowable)
          {
            try
            {
              this.val$serial.onError(paramThrowable);
              return;
            }
            finally
            {
              this.val$serial.unsubscribe();
            }
            throw localObject;
          }

          public void onNext(T paramT)
          {
            this.val$serial.onNext(paramT);
          }
        };
        2 local2 = new SingleSubscriber(local1)
        {
          public void onError(Throwable paramThrowable)
          {
            this.val$main.onError(paramThrowable);
          }

          public void onSuccess(E paramE)
          {
            onError(new CancellationException("Stream was canceled before emitting a terminal event."));
          }
        };
        localSerializedSubscriber.add(local1);
        localSerializedSubscriber.add(local2);
        paramSubscriber.add(localSerializedSubscriber);
        this.val$other.subscribe(local2);
        return local1;
      }
    });
  }

  public final Single<T> timeout(long paramLong, TimeUnit paramTimeUnit)
  {
    return timeout(paramLong, paramTimeUnit, null, Schedulers.computation());
  }

  public final Single<T> timeout(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    return timeout(paramLong, paramTimeUnit, null, paramScheduler);
  }

  public final Single<T> timeout(long paramLong, TimeUnit paramTimeUnit, Single<? extends T> paramSingle)
  {
    return timeout(paramLong, paramTimeUnit, paramSingle, Schedulers.computation());
  }

  public final Single<T> timeout(long paramLong, TimeUnit paramTimeUnit, Single<? extends T> paramSingle, Scheduler paramScheduler)
  {
    if (paramSingle == null)
      paramSingle = error(new TimeoutException());
    return lift(new OperatorTimeout(paramLong, paramTimeUnit, asObservable(paramSingle), paramScheduler));
  }

  @Experimental
  public final <R> R to(Func1<? super Single<T>, R> paramFunc1)
  {
    return paramFunc1.call(this);
  }

  @Beta
  public final BlockingSingle<T> toBlocking()
  {
    return BlockingSingle.from(this);
  }

  @Beta
  public final Completable toCompletable()
  {
    return Completable.fromSingle(this);
  }

  public final Observable<T> toObservable()
  {
    return asObservable(this);
  }

  public final Subscription unsafeSubscribe(Subscriber<? super T> paramSubscriber)
  {
    RuntimeException localRuntimeException;
    try
    {
      paramSubscriber.onStart();
      RxJavaHooks.onSingleStart(this, this.onSubscribe).call(paramSubscriber);
      Subscription localSubscription = RxJavaHooks.onSingleReturn(paramSubscriber);
      return localSubscription;
    }
    catch (Throwable localThrowable1)
    {
      Exceptions.throwIfFatal(localThrowable1);
      try
      {
        paramSubscriber.onError(RxJavaHooks.onSingleError(localThrowable1));
        return Subscriptions.unsubscribed();
      }
      catch (Throwable localThrowable2)
      {
        Exceptions.throwIfFatal(localThrowable2);
        localRuntimeException = new RuntimeException("Error occurred attempting to subscribe [" + localThrowable1.getMessage() + "] and then again while trying to pass to onError.", localThrowable2);
        RxJavaHooks.onSingleError(localRuntimeException);
      }
    }
    throw localRuntimeException;
  }

  public final <T2, R> Single<R> zipWith(Single<? extends T2> paramSingle, Func2<? super T, ? super T2, ? extends R> paramFunc2)
  {
    return zip(this, paramSingle, paramFunc2);
  }

  public static abstract interface OnSubscribe<T> extends Action1<SingleSubscriber<? super T>>
  {
  }

  public static abstract interface Transformer<T, R> extends Func1<Single<T>, Single<R>>
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.Single
 * JD-Core Version:    0.6.0
 */