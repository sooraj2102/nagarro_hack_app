package rx;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.annotations.Beta;
import rx.annotations.Experimental;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.operators.CompletableFromEmitter;
import rx.internal.operators.CompletableOnSubscribeConcat;
import rx.internal.operators.CompletableOnSubscribeConcatArray;
import rx.internal.operators.CompletableOnSubscribeConcatIterable;
import rx.internal.operators.CompletableOnSubscribeMerge;
import rx.internal.operators.CompletableOnSubscribeMergeArray;
import rx.internal.operators.CompletableOnSubscribeMergeDelayErrorArray;
import rx.internal.operators.CompletableOnSubscribeMergeDelayErrorIterable;
import rx.internal.operators.CompletableOnSubscribeMergeIterable;
import rx.internal.operators.CompletableOnSubscribeTimeout;
import rx.internal.util.SubscriptionList;
import rx.internal.util.UtilityFunctions;
import rx.observers.SafeCompletableSubscriber;
import rx.observers.SafeSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.MultipleAssignmentSubscription;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

@Beta
public class Completable
{
  static final Completable COMPLETE = new Completable(new OnSubscribe()
  {
    public void call(CompletableSubscriber paramCompletableSubscriber)
    {
      paramCompletableSubscriber.onSubscribe(Subscriptions.unsubscribed());
      paramCompletableSubscriber.onCompleted();
    }
  }
  , false);
  static final Completable NEVER = new Completable(new OnSubscribe()
  {
    public void call(CompletableSubscriber paramCompletableSubscriber)
    {
      paramCompletableSubscriber.onSubscribe(Subscriptions.unsubscribed());
    }
  }
  , false);
  private final OnSubscribe onSubscribe;

  protected Completable(OnSubscribe paramOnSubscribe)
  {
    this.onSubscribe = RxJavaHooks.onCreate(paramOnSubscribe);
  }

  protected Completable(OnSubscribe paramOnSubscribe, boolean paramBoolean)
  {
    if (paramBoolean)
      paramOnSubscribe = RxJavaHooks.onCreate(paramOnSubscribe);
    this.onSubscribe = paramOnSubscribe;
  }

  public static Completable amb(Iterable<? extends Completable> paramIterable)
  {
    requireNonNull(paramIterable);
    return create(new OnSubscribe(paramIterable)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        CompositeSubscription localCompositeSubscription = new CompositeSubscription();
        paramCompletableSubscriber.onSubscribe(localCompositeSubscription);
        label257: label283: 
        while (true)
        {
          Iterator localIterator;
          try
          {
            localIterator = this.val$sources.iterator();
            if (localIterator == null)
            {
              paramCompletableSubscriber.onError(new NullPointerException("The iterator returned is null"));
              return;
            }
          }
          catch (Throwable localThrowable1)
          {
            paramCompletableSubscriber.onError(localThrowable1);
            return;
          }
          int i = 1;
          AtomicBoolean localAtomicBoolean = new AtomicBoolean();
          1 local1 = new CompletableSubscriber(localAtomicBoolean, localCompositeSubscription, paramCompletableSubscriber)
          {
            public void onCompleted()
            {
              if (this.val$once.compareAndSet(false, true))
              {
                this.val$set.unsubscribe();
                this.val$s.onCompleted();
              }
            }

            public void onError(Throwable paramThrowable)
            {
              if (this.val$once.compareAndSet(false, true))
              {
                this.val$set.unsubscribe();
                this.val$s.onError(paramThrowable);
                return;
              }
              RxJavaHooks.onError(paramThrowable);
            }

            public void onSubscribe(Subscription paramSubscription)
            {
              this.val$set.add(paramSubscription);
            }
          };
          while (true)
          {
            while (true)
            {
              if ((localAtomicBoolean.get()) || (localCompositeSubscription.isUnsubscribed()))
                break label283;
              try
              {
                boolean bool = localIterator.hasNext();
                if (!bool)
                {
                  if (i == 0)
                    break;
                  paramCompletableSubscriber.onCompleted();
                  return;
                }
              }
              catch (Throwable localThrowable2)
              {
                if (localAtomicBoolean.compareAndSet(false, true))
                {
                  localCompositeSubscription.unsubscribe();
                  paramCompletableSubscriber.onError(localThrowable2);
                  return;
                }
                RxJavaHooks.onError(localThrowable2);
                return;
              }
            }
            if ((localAtomicBoolean.get()) || (localCompositeSubscription.isUnsubscribed()))
              break;
            Completable localCompletable;
            NullPointerException localNullPointerException;
            try
            {
              localCompletable = (Completable)localIterator.next();
              if (localCompletable != null)
                break label257;
              localNullPointerException = new NullPointerException("One of the sources is null");
              if (localAtomicBoolean.compareAndSet(false, true))
              {
                localCompositeSubscription.unsubscribe();
                paramCompletableSubscriber.onError(localNullPointerException);
                return;
              }
            }
            catch (Throwable localThrowable3)
            {
              if (localAtomicBoolean.compareAndSet(false, true))
              {
                localCompositeSubscription.unsubscribe();
                paramCompletableSubscriber.onError(localThrowable3);
                return;
              }
              RxJavaHooks.onError(localThrowable3);
              return;
            }
            RxJavaHooks.onError(localNullPointerException);
            return;
            if ((localAtomicBoolean.get()) || (localCompositeSubscription.isUnsubscribed()))
              break;
            localCompletable.unsafeSubscribe(local1);
            i = 0;
          }
        }
      }
    });
  }

  public static Completable amb(Completable[] paramArrayOfCompletable)
  {
    requireNonNull(paramArrayOfCompletable);
    if (paramArrayOfCompletable.length == 0)
      return complete();
    if (paramArrayOfCompletable.length == 1)
      return paramArrayOfCompletable[0];
    return create(new OnSubscribe(paramArrayOfCompletable)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        CompositeSubscription localCompositeSubscription = new CompositeSubscription();
        paramCompletableSubscriber.onSubscribe(localCompositeSubscription);
        AtomicBoolean localAtomicBoolean = new AtomicBoolean();
        1 local1 = new CompletableSubscriber(localAtomicBoolean, localCompositeSubscription, paramCompletableSubscriber)
        {
          public void onCompleted()
          {
            if (this.val$once.compareAndSet(false, true))
            {
              this.val$set.unsubscribe();
              this.val$s.onCompleted();
            }
          }

          public void onError(Throwable paramThrowable)
          {
            if (this.val$once.compareAndSet(false, true))
            {
              this.val$set.unsubscribe();
              this.val$s.onError(paramThrowable);
              return;
            }
            RxJavaHooks.onError(paramThrowable);
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            this.val$set.add(paramSubscription);
          }
        };
        Completable[] arrayOfCompletable = this.val$sources;
        int i = arrayOfCompletable.length;
        for (int j = 0; ; j++)
        {
          Completable localCompletable;
          if (j < i)
          {
            localCompletable = arrayOfCompletable[j];
            if (!localCompositeSubscription.isUnsubscribed())
              break label72;
          }
          label72: 
          do
          {
            return;
            if (localCompletable != null)
              continue;
            NullPointerException localNullPointerException = new NullPointerException("One of the sources is null");
            if (localAtomicBoolean.compareAndSet(false, true))
            {
              localCompositeSubscription.unsubscribe();
              paramCompletableSubscriber.onError(localNullPointerException);
              return;
            }
            RxJavaHooks.onError(localNullPointerException);
            return;
          }
          while ((localAtomicBoolean.get()) || (localCompositeSubscription.isUnsubscribed()));
          localCompletable.unsafeSubscribe(local1);
        }
      }
    });
  }

  public static Completable complete()
  {
    OnSubscribe localOnSubscribe = RxJavaHooks.onCreate(COMPLETE.onSubscribe);
    if (localOnSubscribe == COMPLETE.onSubscribe)
      return COMPLETE;
    return new Completable(localOnSubscribe, false);
  }

  public static Completable concat(Iterable<? extends Completable> paramIterable)
  {
    requireNonNull(paramIterable);
    return create(new CompletableOnSubscribeConcatIterable(paramIterable));
  }

  public static Completable concat(Observable<? extends Completable> paramObservable)
  {
    return concat(paramObservable, 2);
  }

  public static Completable concat(Observable<? extends Completable> paramObservable, int paramInt)
  {
    requireNonNull(paramObservable);
    if (paramInt < 1)
      throw new IllegalArgumentException("prefetch > 0 required but it was " + paramInt);
    return create(new CompletableOnSubscribeConcat(paramObservable, paramInt));
  }

  public static Completable concat(Completable[] paramArrayOfCompletable)
  {
    requireNonNull(paramArrayOfCompletable);
    if (paramArrayOfCompletable.length == 0)
      return complete();
    if (paramArrayOfCompletable.length == 1)
      return paramArrayOfCompletable[0];
    return create(new CompletableOnSubscribeConcatArray(paramArrayOfCompletable));
  }

  public static Completable create(OnSubscribe paramOnSubscribe)
  {
    requireNonNull(paramOnSubscribe);
    try
    {
      Completable localCompletable = new Completable(paramOnSubscribe);
      return localCompletable;
    }
    catch (NullPointerException localNullPointerException)
    {
      throw localNullPointerException;
    }
    catch (Throwable localThrowable)
    {
      RxJavaHooks.onError(localThrowable);
    }
    throw toNpe(localThrowable);
  }

  public static Completable defer(Func0<? extends Completable> paramFunc0)
  {
    requireNonNull(paramFunc0);
    return create(new OnSubscribe(paramFunc0)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        Completable localCompletable;
        try
        {
          localCompletable = (Completable)this.val$completableFunc0.call();
          if (localCompletable == null)
          {
            paramCompletableSubscriber.onSubscribe(Subscriptions.unsubscribed());
            paramCompletableSubscriber.onError(new NullPointerException("The completable returned is null"));
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          paramCompletableSubscriber.onSubscribe(Subscriptions.unsubscribed());
          paramCompletableSubscriber.onError(localThrowable);
          return;
        }
        localCompletable.unsafeSubscribe(paramCompletableSubscriber);
      }
    });
  }

  static void deliverUncaughtException(Throwable paramThrowable)
  {
    Thread localThread = Thread.currentThread();
    localThread.getUncaughtExceptionHandler().uncaughtException(localThread, paramThrowable);
  }

  public static Completable error(Throwable paramThrowable)
  {
    requireNonNull(paramThrowable);
    return create(new OnSubscribe(paramThrowable)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        paramCompletableSubscriber.onSubscribe(Subscriptions.unsubscribed());
        paramCompletableSubscriber.onError(this.val$error);
      }
    });
  }

  public static Completable error(Func0<? extends Throwable> paramFunc0)
  {
    requireNonNull(paramFunc0);
    return create(new OnSubscribe(paramFunc0)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        paramCompletableSubscriber.onSubscribe(Subscriptions.unsubscribed());
        try
        {
          localObject = (Throwable)this.val$errorFunc0.call();
          if (localObject == null)
            localObject = new NullPointerException("The error supplied is null");
          paramCompletableSubscriber.onError((Throwable)localObject);
          return;
        }
        catch (Throwable localThrowable)
        {
          while (true)
            Object localObject = localThrowable;
        }
      }
    });
  }

  public static Completable fromAction(Action0 paramAction0)
  {
    requireNonNull(paramAction0);
    return create(new OnSubscribe(paramAction0)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        BooleanSubscription localBooleanSubscription = new BooleanSubscription();
        paramCompletableSubscriber.onSubscribe(localBooleanSubscription);
        try
        {
          this.val$action.call();
          if (!localBooleanSubscription.isUnsubscribed())
            paramCompletableSubscriber.onCompleted();
          return;
        }
        catch (Throwable localThrowable)
        {
          while (localBooleanSubscription.isUnsubscribed());
          paramCompletableSubscriber.onError(localThrowable);
        }
      }
    });
  }

  public static Completable fromCallable(Callable<?> paramCallable)
  {
    requireNonNull(paramCallable);
    return create(new OnSubscribe(paramCallable)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        BooleanSubscription localBooleanSubscription = new BooleanSubscription();
        paramCompletableSubscriber.onSubscribe(localBooleanSubscription);
        try
        {
          this.val$callable.call();
          if (!localBooleanSubscription.isUnsubscribed())
            paramCompletableSubscriber.onCompleted();
          return;
        }
        catch (Throwable localThrowable)
        {
          while (localBooleanSubscription.isUnsubscribed());
          paramCompletableSubscriber.onError(localThrowable);
        }
      }
    });
  }

  @Experimental
  public static Completable fromEmitter(Action1<CompletableEmitter> paramAction1)
  {
    return create(new CompletableFromEmitter(paramAction1));
  }

  public static Completable fromFuture(Future<?> paramFuture)
  {
    requireNonNull(paramFuture);
    return fromObservable(Observable.from(paramFuture));
  }

  public static Completable fromObservable(Observable<?> paramObservable)
  {
    requireNonNull(paramObservable);
    return create(new OnSubscribe(paramObservable)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        1 local1 = new Subscriber(paramCompletableSubscriber)
        {
          public void onCompleted()
          {
            this.val$cs.onCompleted();
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$cs.onError(paramThrowable);
          }

          public void onNext(Object paramObject)
          {
          }
        };
        paramCompletableSubscriber.onSubscribe(local1);
        this.val$flowable.unsafeSubscribe(local1);
      }
    });
  }

  public static Completable fromSingle(Single<?> paramSingle)
  {
    requireNonNull(paramSingle);
    return create(new OnSubscribe(paramSingle)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        1 local1 = new SingleSubscriber(paramCompletableSubscriber)
        {
          public void onError(Throwable paramThrowable)
          {
            this.val$s.onError(paramThrowable);
          }

          public void onSuccess(Object paramObject)
          {
            this.val$s.onCompleted();
          }
        };
        paramCompletableSubscriber.onSubscribe(local1);
        this.val$single.subscribe(local1);
      }
    });
  }

  public static Completable merge(Iterable<? extends Completable> paramIterable)
  {
    requireNonNull(paramIterable);
    return create(new CompletableOnSubscribeMergeIterable(paramIterable));
  }

  public static Completable merge(Observable<? extends Completable> paramObservable)
  {
    return merge0(paramObservable, 2147483647, false);
  }

  public static Completable merge(Observable<? extends Completable> paramObservable, int paramInt)
  {
    return merge0(paramObservable, paramInt, false);
  }

  public static Completable merge(Completable[] paramArrayOfCompletable)
  {
    requireNonNull(paramArrayOfCompletable);
    if (paramArrayOfCompletable.length == 0)
      return complete();
    if (paramArrayOfCompletable.length == 1)
      return paramArrayOfCompletable[0];
    return create(new CompletableOnSubscribeMergeArray(paramArrayOfCompletable));
  }

  protected static Completable merge0(Observable<? extends Completable> paramObservable, int paramInt, boolean paramBoolean)
  {
    requireNonNull(paramObservable);
    if (paramInt < 1)
      throw new IllegalArgumentException("maxConcurrency > 0 required but it was " + paramInt);
    return create(new CompletableOnSubscribeMerge(paramObservable, paramInt, paramBoolean));
  }

  public static Completable mergeDelayError(Iterable<? extends Completable> paramIterable)
  {
    requireNonNull(paramIterable);
    return create(new CompletableOnSubscribeMergeDelayErrorIterable(paramIterable));
  }

  public static Completable mergeDelayError(Observable<? extends Completable> paramObservable)
  {
    return merge0(paramObservable, 2147483647, true);
  }

  public static Completable mergeDelayError(Observable<? extends Completable> paramObservable, int paramInt)
  {
    return merge0(paramObservable, paramInt, true);
  }

  public static Completable mergeDelayError(Completable[] paramArrayOfCompletable)
  {
    requireNonNull(paramArrayOfCompletable);
    return create(new CompletableOnSubscribeMergeDelayErrorArray(paramArrayOfCompletable));
  }

  public static Completable never()
  {
    OnSubscribe localOnSubscribe = RxJavaHooks.onCreate(NEVER.onSubscribe);
    if (localOnSubscribe == NEVER.onSubscribe)
      return NEVER;
    return new Completable(localOnSubscribe, false);
  }

  static <T> T requireNonNull(T paramT)
  {
    if (paramT == null)
      throw new NullPointerException();
    return paramT;
  }

  public static Completable timer(long paramLong, TimeUnit paramTimeUnit)
  {
    return timer(paramLong, paramTimeUnit, Schedulers.computation());
  }

  public static Completable timer(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    requireNonNull(paramTimeUnit);
    requireNonNull(paramScheduler);
    return create(new OnSubscribe(paramScheduler, paramLong, paramTimeUnit)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        MultipleAssignmentSubscription localMultipleAssignmentSubscription = new MultipleAssignmentSubscription();
        paramCompletableSubscriber.onSubscribe(localMultipleAssignmentSubscription);
        if (!localMultipleAssignmentSubscription.isUnsubscribed())
        {
          Scheduler.Worker localWorker = this.val$scheduler.createWorker();
          localMultipleAssignmentSubscription.set(localWorker);
          localWorker.schedule(new Action0(paramCompletableSubscriber, localWorker)
          {
            public void call()
            {
              try
              {
                this.val$s.onCompleted();
                return;
              }
              finally
              {
                this.val$w.unsubscribe();
              }
              throw localObject;
            }
          }
          , this.val$delay, this.val$unit);
        }
      }
    });
  }

  static NullPointerException toNpe(Throwable paramThrowable)
  {
    NullPointerException localNullPointerException = new NullPointerException("Actually not, but can't pass out an exception otherwise...");
    localNullPointerException.initCause(paramThrowable);
    return localNullPointerException;
  }

  private <T> void unsafeSubscribe(Subscriber<T> paramSubscriber, boolean paramBoolean)
  {
    requireNonNull(paramSubscriber);
    if (paramBoolean);
    Throwable localThrowable2;
    try
    {
      paramSubscriber.onStart();
      unsafeSubscribe(new CompletableSubscriber(paramSubscriber)
      {
        public void onCompleted()
        {
          this.val$s.onCompleted();
        }

        public void onError(Throwable paramThrowable)
        {
          this.val$s.onError(paramThrowable);
        }

        public void onSubscribe(Subscription paramSubscription)
        {
          this.val$s.add(paramSubscription);
        }
      });
      RxJavaHooks.onObservableReturn(paramSubscriber);
      return;
    }
    catch (NullPointerException localNullPointerException)
    {
      throw localNullPointerException;
    }
    catch (Throwable localThrowable1)
    {
      Exceptions.throwIfFatal(localThrowable1);
      localThrowable2 = RxJavaHooks.onObservableError(localThrowable1);
      RxJavaHooks.onError(localThrowable2);
    }
    throw toNpe(localThrowable2);
  }

  public static <R> Completable using(Func0<R> paramFunc0, Func1<? super R, ? extends Completable> paramFunc1, Action1<? super R> paramAction1)
  {
    return using(paramFunc0, paramFunc1, paramAction1, true);
  }

  public static <R> Completable using(Func0<R> paramFunc0, Func1<? super R, ? extends Completable> paramFunc1, Action1<? super R> paramAction1, boolean paramBoolean)
  {
    requireNonNull(paramFunc0);
    requireNonNull(paramFunc1);
    requireNonNull(paramAction1);
    return create(new OnSubscribe(paramFunc0, paramFunc1, paramAction1, paramBoolean)
    {
      // ERROR //
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 23	rx/Completable$13:val$resourceFunc0	Lrx/functions/Func0;
        //   4: invokeinterface 46 1 0
        //   9: astore_3
        //   10: aload_0
        //   11: getfield 25	rx/Completable$13:val$completableFunc1	Lrx/functions/Func1;
        //   14: aload_3
        //   15: invokeinterface 51 2 0
        //   20: checkcast 8	rx/Completable
        //   23: astore 6
        //   25: aload 6
        //   27: ifnonnull +203 -> 230
        //   30: aload_0
        //   31: getfield 27	rx/Completable$13:val$disposer	Lrx/functions/Action1;
        //   34: aload_3
        //   35: invokeinterface 55 2 0
        //   40: aload_1
        //   41: invokestatic 61	rx/subscriptions/Subscriptions:unsubscribed	()Lrx/Subscription;
        //   44: invokeinterface 65 2 0
        //   49: aload_1
        //   50: new 67	java/lang/NullPointerException
        //   53: dup
        //   54: ldc 69
        //   56: invokespecial 72	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
        //   59: invokeinterface 76 2 0
        //   64: return
        //   65: astore_2
        //   66: aload_1
        //   67: invokestatic 61	rx/subscriptions/Subscriptions:unsubscribed	()Lrx/Subscription;
        //   70: invokeinterface 65 2 0
        //   75: aload_1
        //   76: aload_2
        //   77: invokeinterface 76 2 0
        //   82: return
        //   83: astore 4
        //   85: aload_0
        //   86: getfield 27	rx/Completable$13:val$disposer	Lrx/functions/Action1;
        //   89: aload_3
        //   90: invokeinterface 55 2 0
        //   95: aload 4
        //   97: invokestatic 81	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
        //   100: aload_1
        //   101: invokestatic 61	rx/subscriptions/Subscriptions:unsubscribed	()Lrx/Subscription;
        //   104: invokeinterface 65 2 0
        //   109: aload_1
        //   110: aload 4
        //   112: invokeinterface 76 2 0
        //   117: return
        //   118: astore 5
        //   120: aload 4
        //   122: invokestatic 81	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
        //   125: aload 5
        //   127: invokestatic 81	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
        //   130: aload_1
        //   131: invokestatic 61	rx/subscriptions/Subscriptions:unsubscribed	()Lrx/Subscription;
        //   134: invokeinterface 65 2 0
        //   139: aload_1
        //   140: new 83	rx/exceptions/CompositeException
        //   143: dup
        //   144: iconst_2
        //   145: anewarray 41	java/lang/Throwable
        //   148: dup
        //   149: iconst_0
        //   150: aload 4
        //   152: aastore
        //   153: dup
        //   154: iconst_1
        //   155: aload 5
        //   157: aastore
        //   158: invokestatic 89	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
        //   161: invokespecial 92	rx/exceptions/CompositeException:<init>	(Ljava/util/Collection;)V
        //   164: invokeinterface 76 2 0
        //   169: return
        //   170: astore 7
        //   172: aload 7
        //   174: invokestatic 81	rx/exceptions/Exceptions:throwIfFatal	(Ljava/lang/Throwable;)V
        //   177: aload_1
        //   178: invokestatic 61	rx/subscriptions/Subscriptions:unsubscribed	()Lrx/Subscription;
        //   181: invokeinterface 65 2 0
        //   186: iconst_2
        //   187: anewarray 41	java/lang/Throwable
        //   190: astore 8
        //   192: aload 8
        //   194: iconst_0
        //   195: new 67	java/lang/NullPointerException
        //   198: dup
        //   199: ldc 69
        //   201: invokespecial 72	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
        //   204: aastore
        //   205: aload 8
        //   207: iconst_1
        //   208: aload 7
        //   210: aastore
        //   211: aload_1
        //   212: new 83	rx/exceptions/CompositeException
        //   215: dup
        //   216: aload 8
        //   218: invokestatic 89	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
        //   221: invokespecial 92	rx/exceptions/CompositeException:<init>	(Ljava/util/Collection;)V
        //   224: invokeinterface 76 2 0
        //   229: return
        //   230: aload 6
        //   232: new 94	rx/Completable$13$1
        //   235: dup
        //   236: aload_0
        //   237: new 96	java/util/concurrent/atomic/AtomicBoolean
        //   240: dup
        //   241: invokespecial 97	java/util/concurrent/atomic/AtomicBoolean:<init>	()V
        //   244: aload_3
        //   245: aload_1
        //   246: invokespecial 100	rx/Completable$13$1:<init>	(Lrx/Completable$13;Ljava/util/concurrent/atomic/AtomicBoolean;Ljava/lang/Object;Lrx/CompletableSubscriber;)V
        //   249: invokevirtual 103	rx/Completable:unsafeSubscribe	(Lrx/CompletableSubscriber;)V
        //   252: return
        //
        // Exception table:
        //   from	to	target	type
        //   0	10	65	java/lang/Throwable
        //   10	25	83	java/lang/Throwable
        //   85	95	118	java/lang/Throwable
        //   30	40	170	java/lang/Throwable
      }
    });
  }

  public final Completable ambWith(Completable paramCompletable)
  {
    requireNonNull(paramCompletable);
    return amb(new Completable[] { this, paramCompletable });
  }

  public final Completable andThen(Completable paramCompletable)
  {
    return concatWith(paramCompletable);
  }

  public final <T> Observable<T> andThen(Observable<T> paramObservable)
  {
    requireNonNull(paramObservable);
    return paramObservable.delaySubscription(toObservable());
  }

  public final <T> Single<T> andThen(Single<T> paramSingle)
  {
    requireNonNull(paramSingle);
    return paramSingle.delaySubscription(toObservable());
  }

  public final void await()
  {
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    Throwable[] arrayOfThrowable = new Throwable[1];
    unsafeSubscribe(new CompletableSubscriber(localCountDownLatch, arrayOfThrowable)
    {
      public void onCompleted()
      {
        this.val$cdl.countDown();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$err[0] = paramThrowable;
        this.val$cdl.countDown();
      }

      public void onSubscribe(Subscription paramSubscription)
      {
      }
    });
    if (localCountDownLatch.getCount() == 0L)
      if (arrayOfThrowable[0] != null)
        Exceptions.propagate(arrayOfThrowable[0]);
    while (true)
    {
      return;
      try
      {
        localCountDownLatch.await();
        if (arrayOfThrowable[0] == null)
          continue;
        Exceptions.propagate(arrayOfThrowable[0]);
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
    throw Exceptions.propagate(localInterruptedException);
  }

  public final boolean await(long paramLong, TimeUnit paramTimeUnit)
  {
    int i = 1;
    requireNonNull(paramTimeUnit);
    CountDownLatch localCountDownLatch = new CountDownLatch(i);
    Throwable[] arrayOfThrowable = new Throwable[i];
    unsafeSubscribe(new CompletableSubscriber(localCountDownLatch, arrayOfThrowable)
    {
      public void onCompleted()
      {
        this.val$cdl.countDown();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$err[0] = paramThrowable;
        this.val$cdl.countDown();
      }

      public void onSubscribe(Subscription paramSubscription)
      {
      }
    });
    if (localCountDownLatch.getCount() == 0L)
      if (arrayOfThrowable[0] != null)
        Exceptions.propagate(arrayOfThrowable[0]);
    while (true)
    {
      return i;
      try
      {
        int j = localCountDownLatch.await(paramLong, paramTimeUnit);
        i = j;
        if ((i == 0) || (arrayOfThrowable[0] == null))
          continue;
        Exceptions.propagate(arrayOfThrowable[0]);
        return i;
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
    throw Exceptions.propagate(localInterruptedException);
  }

  public final Completable compose(Transformer paramTransformer)
  {
    return (Completable)to(paramTransformer);
  }

  public final Completable concatWith(Completable paramCompletable)
  {
    requireNonNull(paramCompletable);
    return concat(new Completable[] { this, paramCompletable });
  }

  public final Completable delay(long paramLong, TimeUnit paramTimeUnit)
  {
    return delay(paramLong, paramTimeUnit, Schedulers.computation(), false);
  }

  public final Completable delay(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    return delay(paramLong, paramTimeUnit, paramScheduler, false);
  }

  public final Completable delay(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler, boolean paramBoolean)
  {
    requireNonNull(paramTimeUnit);
    requireNonNull(paramScheduler);
    return create(new OnSubscribe(paramScheduler, paramLong, paramTimeUnit, paramBoolean)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        CompositeSubscription localCompositeSubscription = new CompositeSubscription();
        Scheduler.Worker localWorker = this.val$scheduler.createWorker();
        localCompositeSubscription.add(localWorker);
        Completable.this.unsafeSubscribe(new CompletableSubscriber(localCompositeSubscription, localWorker, paramCompletableSubscriber)
        {
          public void onCompleted()
          {
            this.val$set.add(this.val$w.schedule(new Action0()
            {
              public void call()
              {
                try
                {
                  Completable.16.1.this.val$s.onCompleted();
                  return;
                }
                finally
                {
                  Completable.16.1.this.val$w.unsubscribe();
                }
                throw localObject;
              }
            }
            , Completable.16.this.val$delay, Completable.16.this.val$unit));
          }

          public void onError(Throwable paramThrowable)
          {
            if (Completable.16.this.val$delayError)
            {
              this.val$set.add(this.val$w.schedule(new Action0(paramThrowable)
              {
                public void call()
                {
                  try
                  {
                    Completable.16.1.this.val$s.onError(this.val$e);
                    return;
                  }
                  finally
                  {
                    Completable.16.1.this.val$w.unsubscribe();
                  }
                  throw localObject;
                }
              }
              , Completable.16.this.val$delay, Completable.16.this.val$unit));
              return;
            }
            this.val$s.onError(paramThrowable);
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            this.val$set.add(paramSubscription);
            this.val$s.onSubscribe(this.val$set);
          }
        });
      }
    });
  }

  public final Completable doAfterTerminate(Action0 paramAction0)
  {
    return doOnLifecycle(Actions.empty(), Actions.empty(), Actions.empty(), paramAction0, Actions.empty());
  }

  public final Completable doOnCompleted(Action0 paramAction0)
  {
    return doOnLifecycle(Actions.empty(), Actions.empty(), paramAction0, Actions.empty(), Actions.empty());
  }

  public final Completable doOnEach(Action1<Notification<Object>> paramAction1)
  {
    if (paramAction1 == null)
      throw new IllegalArgumentException("onNotification is null");
    return doOnLifecycle(Actions.empty(), new Action1(paramAction1)
    {
      public void call(Throwable paramThrowable)
      {
        this.val$onNotification.call(Notification.createOnError(paramThrowable));
      }
    }
    , new Action0(paramAction1)
    {
      public void call()
      {
        this.val$onNotification.call(Notification.createOnCompleted());
      }
    }
    , Actions.empty(), Actions.empty());
  }

  public final Completable doOnError(Action1<? super Throwable> paramAction1)
  {
    return doOnLifecycle(Actions.empty(), paramAction1, Actions.empty(), Actions.empty(), Actions.empty());
  }

  protected final Completable doOnLifecycle(Action1<? super Subscription> paramAction1, Action1<? super Throwable> paramAction11, Action0 paramAction01, Action0 paramAction02, Action0 paramAction03)
  {
    requireNonNull(paramAction1);
    requireNonNull(paramAction11);
    requireNonNull(paramAction01);
    requireNonNull(paramAction02);
    requireNonNull(paramAction03);
    return create(new OnSubscribe(paramAction01, paramAction02, paramAction11, paramAction1, paramAction03)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        Completable.this.unsafeSubscribe(new CompletableSubscriber(paramCompletableSubscriber)
        {
          // ERROR //
          public void onCompleted()
          {
            // Byte code:
            //   0: aload_0
            //   1: getfield 19	rx/Completable$19$1:this$1	Lrx/Completable$19;
            //   4: getfield 31	rx/Completable$19:val$onComplete	Lrx/functions/Action0;
            //   7: invokeinterface 35 1 0
            //   12: aload_0
            //   13: getfield 21	rx/Completable$19$1:val$s	Lrx/CompletableSubscriber;
            //   16: invokeinterface 37 1 0
            //   21: aload_0
            //   22: getfield 19	rx/Completable$19$1:this$1	Lrx/Completable$19;
            //   25: getfield 40	rx/Completable$19:val$onAfterComplete	Lrx/functions/Action0;
            //   28: invokeinterface 35 1 0
            //   33: return
            //   34: astore_1
            //   35: aload_0
            //   36: getfield 21	rx/Completable$19$1:val$s	Lrx/CompletableSubscriber;
            //   39: aload_1
            //   40: invokeinterface 44 2 0
            //   45: return
            //   46: astore_2
            //   47: aload_2
            //   48: invokestatic 47	rx/plugins/RxJavaHooks:onError	(Ljava/lang/Throwable;)V
            //   51: return
            //
            // Exception table:
            //   from	to	target	type
            //   0	12	34	java/lang/Throwable
            //   21	33	46	java/lang/Throwable
          }

          public void onError(Throwable paramThrowable)
          {
            try
            {
              Completable.19.this.val$onError.call(paramThrowable);
              this.val$s.onError(paramThrowable);
              return;
            }
            catch (Throwable localThrowable)
            {
              while (true)
                paramThrowable = new CompositeException(Arrays.asList(new Throwable[] { paramThrowable, localThrowable }));
            }
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            try
            {
              Completable.19.this.val$onSubscribe.call(paramSubscription);
              this.val$s.onSubscribe(Subscriptions.create(new Action0(paramSubscription)
              {
                public void call()
                {
                  try
                  {
                    Completable.19.this.val$onUnsubscribe.call();
                    this.val$d.unsubscribe();
                    return;
                  }
                  catch (Throwable localThrowable)
                  {
                    while (true)
                      RxJavaHooks.onError(localThrowable);
                  }
                }
              }));
              return;
            }
            catch (Throwable localThrowable)
            {
              paramSubscription.unsubscribe();
              this.val$s.onSubscribe(Subscriptions.unsubscribed());
              this.val$s.onError(localThrowable);
            }
          }
        });
      }
    });
  }

  public final Completable doOnSubscribe(Action1<? super Subscription> paramAction1)
  {
    return doOnLifecycle(paramAction1, Actions.empty(), Actions.empty(), Actions.empty(), Actions.empty());
  }

  public final Completable doOnTerminate(Action0 paramAction0)
  {
    return doOnLifecycle(Actions.empty(), new Action1(paramAction0)
    {
      public void call(Throwable paramThrowable)
      {
        this.val$onTerminate.call();
      }
    }
    , paramAction0, Actions.empty(), Actions.empty());
  }

  public final Completable doOnUnsubscribe(Action0 paramAction0)
  {
    return doOnLifecycle(Actions.empty(), Actions.empty(), Actions.empty(), Actions.empty(), paramAction0);
  }

  public final Throwable get()
  {
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    Throwable[] arrayOfThrowable = new Throwable[1];
    unsafeSubscribe(new CompletableSubscriber(localCountDownLatch, arrayOfThrowable)
    {
      public void onCompleted()
      {
        this.val$cdl.countDown();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$err[0] = paramThrowable;
        this.val$cdl.countDown();
      }

      public void onSubscribe(Subscription paramSubscription)
      {
      }
    });
    if (localCountDownLatch.getCount() == 0L)
      return arrayOfThrowable[0];
    try
    {
      localCountDownLatch.await();
      return arrayOfThrowable[0];
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    throw Exceptions.propagate(localInterruptedException);
  }

  public final Throwable get(long paramLong, TimeUnit paramTimeUnit)
  {
    requireNonNull(paramTimeUnit);
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    Throwable[] arrayOfThrowable = new Throwable[1];
    unsafeSubscribe(new CompletableSubscriber(localCountDownLatch, arrayOfThrowable)
    {
      public void onCompleted()
      {
        this.val$cdl.countDown();
      }

      public void onError(Throwable paramThrowable)
      {
        this.val$err[0] = paramThrowable;
        this.val$cdl.countDown();
      }

      public void onSubscribe(Subscription paramSubscription)
      {
      }
    });
    if (localCountDownLatch.getCount() == 0L)
      return arrayOfThrowable[0];
    try
    {
      boolean bool = localCountDownLatch.await(paramLong, paramTimeUnit);
      if (bool)
        return arrayOfThrowable[0];
    }
    catch (InterruptedException localInterruptedException)
    {
      throw Exceptions.propagate(localInterruptedException);
    }
    Exceptions.propagate(new TimeoutException());
    return null;
  }

  public final Completable lift(Operator paramOperator)
  {
    requireNonNull(paramOperator);
    return create(new OnSubscribe(paramOperator)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        try
        {
          CompletableSubscriber localCompletableSubscriber = (CompletableSubscriber)RxJavaHooks.onCompletableLift(this.val$onLift).call(paramCompletableSubscriber);
          Completable.this.unsafeSubscribe(localCompletableSubscriber);
          return;
        }
        catch (NullPointerException localNullPointerException)
        {
          throw localNullPointerException;
        }
        catch (Throwable localThrowable)
        {
        }
        throw Completable.toNpe(localThrowable);
      }
    });
  }

  public final Completable mergeWith(Completable paramCompletable)
  {
    requireNonNull(paramCompletable);
    return merge(new Completable[] { this, paramCompletable });
  }

  public final Completable observeOn(Scheduler paramScheduler)
  {
    requireNonNull(paramScheduler);
    return create(new OnSubscribe(paramScheduler)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        SubscriptionList localSubscriptionList = new SubscriptionList();
        Scheduler.Worker localWorker = this.val$scheduler.createWorker();
        localSubscriptionList.add(localWorker);
        paramCompletableSubscriber.onSubscribe(localSubscriptionList);
        Completable.this.unsafeSubscribe(new CompletableSubscriber(localWorker, paramCompletableSubscriber, localSubscriptionList)
        {
          public void onCompleted()
          {
            this.val$w.schedule(new Action0()
            {
              public void call()
              {
                try
                {
                  Completable.24.1.this.val$s.onCompleted();
                  return;
                }
                finally
                {
                  Completable.24.1.this.val$ad.unsubscribe();
                }
                throw localObject;
              }
            });
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$w.schedule(new Action0(paramThrowable)
            {
              public void call()
              {
                try
                {
                  Completable.24.1.this.val$s.onError(this.val$e);
                  return;
                }
                finally
                {
                  Completable.24.1.this.val$ad.unsubscribe();
                }
                throw localObject;
              }
            });
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            this.val$ad.add(paramSubscription);
          }
        });
      }
    });
  }

  public final Completable onErrorComplete()
  {
    return onErrorComplete(UtilityFunctions.alwaysTrue());
  }

  public final Completable onErrorComplete(Func1<? super Throwable, Boolean> paramFunc1)
  {
    requireNonNull(paramFunc1);
    return create(new OnSubscribe(paramFunc1)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        Completable.this.unsafeSubscribe(new CompletableSubscriber(paramCompletableSubscriber)
        {
          public void onCompleted()
          {
            this.val$s.onCompleted();
          }

          public void onError(Throwable paramThrowable)
          {
            try
            {
              boolean bool2 = ((Boolean)Completable.25.this.val$predicate.call(paramThrowable)).booleanValue();
              bool1 = bool2;
              if (bool1)
              {
                this.val$s.onCompleted();
                return;
              }
            }
            catch (Throwable localThrowable)
            {
              while (true)
              {
                Exceptions.throwIfFatal(localThrowable);
                paramThrowable = new CompositeException(Arrays.asList(new Throwable[] { paramThrowable, localThrowable }));
                boolean bool1 = false;
              }
              this.val$s.onError(paramThrowable);
            }
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            this.val$s.onSubscribe(paramSubscription);
          }
        });
      }
    });
  }

  public final Completable onErrorResumeNext(Func1<? super Throwable, ? extends Completable> paramFunc1)
  {
    requireNonNull(paramFunc1);
    return create(new OnSubscribe(paramFunc1)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        SerialSubscription localSerialSubscription = new SerialSubscription();
        Completable.this.unsafeSubscribe(new CompletableSubscriber(paramCompletableSubscriber, localSerialSubscription)
        {
          public void onCompleted()
          {
            this.val$s.onCompleted();
          }

          public void onError(Throwable paramThrowable)
          {
            Completable localCompletable;
            try
            {
              localCompletable = (Completable)Completable.26.this.val$errorMapper.call(paramThrowable);
              if (localCompletable == null)
              {
                CompositeException localCompositeException2 = new CompositeException(Arrays.asList(new Throwable[] { paramThrowable, new NullPointerException("The completable returned is null") }));
                this.val$s.onError(localCompositeException2);
                return;
              }
            }
            catch (Throwable localThrowable)
            {
              CompositeException localCompositeException1 = new CompositeException(Arrays.asList(new Throwable[] { paramThrowable, localThrowable }));
              this.val$s.onError(localCompositeException1);
              return;
            }
            localCompletable.unsafeSubscribe(new CompletableSubscriber()
            {
              public void onCompleted()
              {
                Completable.26.1.this.val$s.onCompleted();
              }

              public void onError(Throwable paramThrowable)
              {
                Completable.26.1.this.val$s.onError(paramThrowable);
              }

              public void onSubscribe(Subscription paramSubscription)
              {
                Completable.26.1.this.val$sd.set(paramSubscription);
              }
            });
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            this.val$sd.set(paramSubscription);
          }
        });
      }
    });
  }

  public final Completable repeat()
  {
    return fromObservable(toObservable().repeat());
  }

  public final Completable repeat(long paramLong)
  {
    return fromObservable(toObservable().repeat(paramLong));
  }

  public final Completable repeatWhen(Func1<? super Observable<? extends Void>, ? extends Observable<?>> paramFunc1)
  {
    requireNonNull(paramFunc1);
    return fromObservable(toObservable().repeatWhen(paramFunc1));
  }

  public final Completable retry()
  {
    return fromObservable(toObservable().retry());
  }

  public final Completable retry(long paramLong)
  {
    return fromObservable(toObservable().retry(paramLong));
  }

  public final Completable retry(Func2<Integer, Throwable, Boolean> paramFunc2)
  {
    return fromObservable(toObservable().retry(paramFunc2));
  }

  public final Completable retryWhen(Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> paramFunc1)
  {
    return fromObservable(toObservable().retryWhen(paramFunc1));
  }

  public final Completable startWith(Completable paramCompletable)
  {
    requireNonNull(paramCompletable);
    return concat(new Completable[] { paramCompletable, this });
  }

  public final <T> Observable<T> startWith(Observable<T> paramObservable)
  {
    requireNonNull(paramObservable);
    return toObservable().startWith(paramObservable);
  }

  public final Subscription subscribe()
  {
    MultipleAssignmentSubscription localMultipleAssignmentSubscription = new MultipleAssignmentSubscription();
    unsafeSubscribe(new CompletableSubscriber(localMultipleAssignmentSubscription)
    {
      public void onCompleted()
      {
        this.val$mad.unsubscribe();
      }

      public void onError(Throwable paramThrowable)
      {
        RxJavaHooks.onError(paramThrowable);
        this.val$mad.unsubscribe();
        Completable.deliverUncaughtException(paramThrowable);
      }

      public void onSubscribe(Subscription paramSubscription)
      {
        this.val$mad.set(paramSubscription);
      }
    });
    return localMultipleAssignmentSubscription;
  }

  public final Subscription subscribe(Action0 paramAction0)
  {
    requireNonNull(paramAction0);
    MultipleAssignmentSubscription localMultipleAssignmentSubscription = new MultipleAssignmentSubscription();
    unsafeSubscribe(new CompletableSubscriber(paramAction0, localMultipleAssignmentSubscription)
    {
      boolean done;

      public void onCompleted()
      {
        if (!this.done)
          this.done = true;
        try
        {
          this.val$onComplete.call();
          return;
        }
        catch (Throwable localThrowable)
        {
          RxJavaHooks.onError(localThrowable);
          Completable.deliverUncaughtException(localThrowable);
          return;
        }
        finally
        {
          this.val$mad.unsubscribe();
        }
        throw localObject;
      }

      public void onError(Throwable paramThrowable)
      {
        RxJavaHooks.onError(paramThrowable);
        this.val$mad.unsubscribe();
        Completable.deliverUncaughtException(paramThrowable);
      }

      public void onSubscribe(Subscription paramSubscription)
      {
        this.val$mad.set(paramSubscription);
      }
    });
    return localMultipleAssignmentSubscription;
  }

  public final Subscription subscribe(Action0 paramAction0, Action1<? super Throwable> paramAction1)
  {
    requireNonNull(paramAction0);
    requireNonNull(paramAction1);
    MultipleAssignmentSubscription localMultipleAssignmentSubscription = new MultipleAssignmentSubscription();
    unsafeSubscribe(new CompletableSubscriber(paramAction0, localMultipleAssignmentSubscription, paramAction1)
    {
      boolean done;

      // ERROR //
      void callOnError(Throwable paramThrowable)
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 31	rx/Completable$29:val$onError	Lrx/functions/Action1;
        //   4: aload_1
        //   5: invokeinterface 44 2 0
        //   10: aload_0
        //   11: getfield 29	rx/Completable$29:val$mad	Lrx/subscriptions/MultipleAssignmentSubscription;
        //   14: invokevirtual 49	rx/subscriptions/MultipleAssignmentSubscription:unsubscribe	()V
        //   17: return
        //   18: astore_3
        //   19: new 51	rx/exceptions/CompositeException
        //   22: dup
        //   23: iconst_2
        //   24: anewarray 38	java/lang/Throwable
        //   27: dup
        //   28: iconst_0
        //   29: aload_1
        //   30: aastore
        //   31: dup
        //   32: iconst_1
        //   33: aload_3
        //   34: aastore
        //   35: invokestatic 57	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
        //   38: invokespecial 60	rx/exceptions/CompositeException:<init>	(Ljava/util/Collection;)V
        //   41: astore 4
        //   43: aload 4
        //   45: invokestatic 65	rx/plugins/RxJavaHooks:onError	(Ljava/lang/Throwable;)V
        //   48: aload 4
        //   50: invokestatic 68	rx/Completable:deliverUncaughtException	(Ljava/lang/Throwable;)V
        //   53: aload_0
        //   54: getfield 29	rx/Completable$29:val$mad	Lrx/subscriptions/MultipleAssignmentSubscription;
        //   57: invokevirtual 49	rx/subscriptions/MultipleAssignmentSubscription:unsubscribe	()V
        //   60: return
        //   61: astore_2
        //   62: aload_0
        //   63: getfield 29	rx/Completable$29:val$mad	Lrx/subscriptions/MultipleAssignmentSubscription;
        //   66: invokevirtual 49	rx/subscriptions/MultipleAssignmentSubscription:unsubscribe	()V
        //   69: aload_2
        //   70: athrow
        //   71: astore_2
        //   72: goto -10 -> 62
        //
        // Exception table:
        //   from	to	target	type
        //   0	10	18	java/lang/Throwable
        //   0	10	61	finally
        //   19	43	61	finally
        //   43	53	71	finally
      }

      public void onCompleted()
      {
        if (!this.done)
          this.done = true;
        try
        {
          this.val$onComplete.call();
          this.val$mad.unsubscribe();
          return;
        }
        catch (Throwable localThrowable)
        {
          callOnError(localThrowable);
        }
      }

      public void onError(Throwable paramThrowable)
      {
        if (!this.done)
        {
          this.done = true;
          callOnError(paramThrowable);
          return;
        }
        RxJavaHooks.onError(paramThrowable);
        Completable.deliverUncaughtException(paramThrowable);
      }

      public void onSubscribe(Subscription paramSubscription)
      {
        this.val$mad.set(paramSubscription);
      }
    });
    return localMultipleAssignmentSubscription;
  }

  public final void subscribe(CompletableSubscriber paramCompletableSubscriber)
  {
    if (!(paramCompletableSubscriber instanceof SafeCompletableSubscriber))
      paramCompletableSubscriber = new SafeCompletableSubscriber(paramCompletableSubscriber);
    unsafeSubscribe(paramCompletableSubscriber);
  }

  public final <T> void subscribe(Subscriber<T> paramSubscriber)
  {
    paramSubscriber.onStart();
    if (!(paramSubscriber instanceof SafeSubscriber))
      paramSubscriber = new SafeSubscriber(paramSubscriber);
    unsafeSubscribe(paramSubscriber, false);
  }

  public final Completable subscribeOn(Scheduler paramScheduler)
  {
    requireNonNull(paramScheduler);
    return create(new OnSubscribe(paramScheduler)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        Scheduler.Worker localWorker = this.val$scheduler.createWorker();
        localWorker.schedule(new Action0(paramCompletableSubscriber, localWorker)
        {
          public void call()
          {
            try
            {
              Completable.this.unsafeSubscribe(this.val$s);
              return;
            }
            finally
            {
              this.val$w.unsubscribe();
            }
            throw localObject;
          }
        });
      }
    });
  }

  public final Completable timeout(long paramLong, TimeUnit paramTimeUnit)
  {
    return timeout0(paramLong, paramTimeUnit, Schedulers.computation(), null);
  }

  public final Completable timeout(long paramLong, TimeUnit paramTimeUnit, Completable paramCompletable)
  {
    requireNonNull(paramCompletable);
    return timeout0(paramLong, paramTimeUnit, Schedulers.computation(), paramCompletable);
  }

  public final Completable timeout(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler)
  {
    return timeout0(paramLong, paramTimeUnit, paramScheduler, null);
  }

  public final Completable timeout(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler, Completable paramCompletable)
  {
    requireNonNull(paramCompletable);
    return timeout0(paramLong, paramTimeUnit, paramScheduler, paramCompletable);
  }

  public final Completable timeout0(long paramLong, TimeUnit paramTimeUnit, Scheduler paramScheduler, Completable paramCompletable)
  {
    requireNonNull(paramTimeUnit);
    requireNonNull(paramScheduler);
    return create(new CompletableOnSubscribeTimeout(this, paramLong, paramTimeUnit, paramScheduler, paramCompletable));
  }

  public final <R> R to(Func1<? super Completable, R> paramFunc1)
  {
    return paramFunc1.call(this);
  }

  public final <T> Observable<T> toObservable()
  {
    return Observable.create(new Observable.OnSubscribe()
    {
      public void call(Subscriber<? super T> paramSubscriber)
      {
        Completable.this.unsafeSubscribe(paramSubscriber);
      }
    });
  }

  public final <T> Single<T> toSingle(Func0<? extends T> paramFunc0)
  {
    requireNonNull(paramFunc0);
    return Single.create(new Single.OnSubscribe(paramFunc0)
    {
      public void call(SingleSubscriber<? super T> paramSingleSubscriber)
      {
        Completable.this.unsafeSubscribe(new CompletableSubscriber(paramSingleSubscriber)
        {
          public void onCompleted()
          {
            Object localObject;
            try
            {
              localObject = Completable.33.this.val$completionValueFunc0.call();
              if (localObject == null)
              {
                this.val$s.onError(new NullPointerException("The value supplied is null"));
                return;
              }
            }
            catch (Throwable localThrowable)
            {
              this.val$s.onError(localThrowable);
              return;
            }
            this.val$s.onSuccess(localObject);
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$s.onError(paramThrowable);
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            this.val$s.add(paramSubscription);
          }
        });
      }
    });
  }

  public final <T> Single<T> toSingleDefault(T paramT)
  {
    requireNonNull(paramT);
    return toSingle(new Func0(paramT)
    {
      public T call()
      {
        return this.val$completionValue;
      }
    });
  }

  public final void unsafeSubscribe(CompletableSubscriber paramCompletableSubscriber)
  {
    requireNonNull(paramCompletableSubscriber);
    Throwable localThrowable2;
    try
    {
      RxJavaHooks.onCompletableStart(this, this.onSubscribe).call(paramCompletableSubscriber);
      return;
    }
    catch (NullPointerException localNullPointerException)
    {
      throw localNullPointerException;
    }
    catch (Throwable localThrowable1)
    {
      Exceptions.throwIfFatal(localThrowable1);
      localThrowable2 = RxJavaHooks.onCompletableError(localThrowable1);
      RxJavaHooks.onError(localThrowable2);
    }
    throw toNpe(localThrowable2);
  }

  public final <T> void unsafeSubscribe(Subscriber<T> paramSubscriber)
  {
    unsafeSubscribe(paramSubscriber, true);
  }

  public final Completable unsubscribeOn(Scheduler paramScheduler)
  {
    requireNonNull(paramScheduler);
    return create(new OnSubscribe(paramScheduler)
    {
      public void call(CompletableSubscriber paramCompletableSubscriber)
      {
        Completable.this.unsafeSubscribe(new CompletableSubscriber(paramCompletableSubscriber)
        {
          public void onCompleted()
          {
            this.val$s.onCompleted();
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$s.onError(paramThrowable);
          }

          public void onSubscribe(Subscription paramSubscription)
          {
            this.val$s.onSubscribe(Subscriptions.create(new Action0(paramSubscription)
            {
              public void call()
              {
                Scheduler.Worker localWorker = Completable.35.this.val$scheduler.createWorker();
                localWorker.schedule(new Action0(localWorker)
                {
                  public void call()
                  {
                    try
                    {
                      Completable.35.1.1.this.val$d.unsubscribe();
                      return;
                    }
                    finally
                    {
                      this.val$w.unsubscribe();
                    }
                    throw localObject;
                  }
                });
              }
            }));
          }
        });
      }
    });
  }

  public static abstract interface OnSubscribe extends Action1<CompletableSubscriber>
  {
  }

  public static abstract interface Operator extends Func1<CompletableSubscriber, CompletableSubscriber>
  {
  }

  public static abstract interface Transformer extends Func1<Completable, Completable>
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.Completable
 * JD-Core Version:    0.6.0
 */