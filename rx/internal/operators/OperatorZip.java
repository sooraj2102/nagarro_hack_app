package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.MissingBackpressureException;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.functions.Func5;
import rx.functions.Func6;
import rx.functions.Func7;
import rx.functions.Func8;
import rx.functions.Func9;
import rx.functions.FuncN;
import rx.functions.Functions;
import rx.internal.util.RxRingBuffer;
import rx.subscriptions.CompositeSubscription;

public final class OperatorZip<R>
  implements Observable.Operator<R, Observable<?>[]>
{
  final FuncN<? extends R> zipFunction;

  public OperatorZip(Func2 paramFunc2)
  {
    this.zipFunction = Functions.fromFunc(paramFunc2);
  }

  public OperatorZip(Func3 paramFunc3)
  {
    this.zipFunction = Functions.fromFunc(paramFunc3);
  }

  public OperatorZip(Func4 paramFunc4)
  {
    this.zipFunction = Functions.fromFunc(paramFunc4);
  }

  public OperatorZip(Func5 paramFunc5)
  {
    this.zipFunction = Functions.fromFunc(paramFunc5);
  }

  public OperatorZip(Func6 paramFunc6)
  {
    this.zipFunction = Functions.fromFunc(paramFunc6);
  }

  public OperatorZip(Func7 paramFunc7)
  {
    this.zipFunction = Functions.fromFunc(paramFunc7);
  }

  public OperatorZip(Func8 paramFunc8)
  {
    this.zipFunction = Functions.fromFunc(paramFunc8);
  }

  public OperatorZip(Func9 paramFunc9)
  {
    this.zipFunction = Functions.fromFunc(paramFunc9);
  }

  public OperatorZip(FuncN<? extends R> paramFuncN)
  {
    this.zipFunction = paramFuncN;
  }

  public Subscriber<? super Observable[]> call(Subscriber<? super R> paramSubscriber)
  {
    Zip localZip = new Zip(paramSubscriber, this.zipFunction);
    ZipProducer localZipProducer = new ZipProducer(localZip);
    ZipSubscriber localZipSubscriber = new ZipSubscriber(paramSubscriber, localZip, localZipProducer);
    paramSubscriber.add(localZipSubscriber);
    paramSubscriber.setProducer(localZipProducer);
    return localZipSubscriber;
  }

  static final class Zip<R> extends AtomicLong
  {
    static final int THRESHOLD = 0;
    private static final long serialVersionUID = 5995274816189928317L;
    final Observer<? super R> child;
    private final CompositeSubscription childSubscription = new CompositeSubscription();
    int emitted;
    private AtomicLong requested;
    private volatile Object[] subscribers;
    private final FuncN<? extends R> zipFunction;

    public Zip(Subscriber<? super R> paramSubscriber, FuncN<? extends R> paramFuncN)
    {
      this.child = paramSubscriber;
      this.zipFunction = paramFuncN;
      paramSubscriber.add(this.childSubscription);
    }

    public void start(Observable[] paramArrayOfObservable, AtomicLong paramAtomicLong)
    {
      Object[] arrayOfObject = new Object[paramArrayOfObservable.length];
      for (int i = 0; i < paramArrayOfObservable.length; i++)
      {
        InnerSubscriber localInnerSubscriber = new InnerSubscriber();
        arrayOfObject[i] = localInnerSubscriber;
        this.childSubscription.add(localInnerSubscriber);
      }
      this.requested = paramAtomicLong;
      this.subscribers = arrayOfObject;
      for (int j = 0; j < paramArrayOfObservable.length; j++)
        paramArrayOfObservable[j].unsafeSubscribe((InnerSubscriber)arrayOfObject[j]);
    }

    void tick()
    {
      Object[] arrayOfObject1 = this.subscribers;
      if (arrayOfObject1 == null);
      do
        return;
      while (getAndIncrement() != 0L);
      int i = arrayOfObject1.length;
      Observer localObserver = this.child;
      AtomicLong localAtomicLong = this.requested;
      do
        while (true)
        {
          Object[] arrayOfObject2 = new Object[i];
          int j = 1;
          int k = 0;
          if (k < i)
          {
            RxRingBuffer localRxRingBuffer2 = ((InnerSubscriber)arrayOfObject1[k]).items;
            Object localObject = localRxRingBuffer2.peek();
            if (localObject == null)
              j = 0;
            while (true)
            {
              k++;
              break;
              if (localRxRingBuffer2.isCompleted(localObject))
              {
                localObserver.onCompleted();
                this.childSubscription.unsubscribe();
                return;
              }
              arrayOfObject2[k] = localRxRingBuffer2.getValue(localObject);
            }
          }
          if ((localAtomicLong.get() <= 0L) || (j == 0))
            break;
          while (true)
          {
            int n;
            try
            {
              localObserver.onNext(this.zipFunction.call(arrayOfObject2));
              localAtomicLong.decrementAndGet();
              this.emitted = (1 + this.emitted);
              int m = arrayOfObject1.length;
              n = 0;
              if (n >= m)
                break;
              RxRingBuffer localRxRingBuffer1 = ((InnerSubscriber)arrayOfObject1[n]).items;
              localRxRingBuffer1.poll();
              if (localRxRingBuffer1.isCompleted(localRxRingBuffer1.peek()))
              {
                localObserver.onCompleted();
                this.childSubscription.unsubscribe();
                return;
              }
            }
            catch (Throwable localThrowable)
            {
              Exceptions.throwOrReport(localThrowable, localObserver, arrayOfObject2);
              return;
            }
            n++;
          }
          if (this.emitted <= THRESHOLD)
            continue;
          int i1 = arrayOfObject1.length;
          for (int i2 = 0; i2 < i1; i2++)
            ((InnerSubscriber)arrayOfObject1[i2]).requestMore(this.emitted);
          this.emitted = 0;
        }
      while (decrementAndGet() > 0L);
    }

    final class InnerSubscriber extends Subscriber
    {
      final RxRingBuffer items = RxRingBuffer.getSpmcInstance();

      InnerSubscriber()
      {
      }

      public void onCompleted()
      {
        this.items.onCompleted();
        OperatorZip.Zip.this.tick();
      }

      public void onError(Throwable paramThrowable)
      {
        OperatorZip.Zip.this.child.onError(paramThrowable);
      }

      public void onNext(Object paramObject)
      {
        try
        {
          this.items.onNext(paramObject);
          OperatorZip.Zip.this.tick();
          return;
        }
        catch (MissingBackpressureException localMissingBackpressureException)
        {
          while (true)
            onError(localMissingBackpressureException);
        }
      }

      public void onStart()
      {
        request(RxRingBuffer.SIZE);
      }

      public void requestMore(long paramLong)
      {
        request(paramLong);
      }
    }
  }

  static final class ZipProducer<R> extends AtomicLong
    implements Producer
  {
    private static final long serialVersionUID = -1216676403723546796L;
    final OperatorZip.Zip<R> zipper;

    public ZipProducer(OperatorZip.Zip<R> paramZip)
    {
      this.zipper = paramZip;
    }

    public void request(long paramLong)
    {
      BackpressureUtils.getAndAddRequest(this, paramLong);
      this.zipper.tick();
    }
  }

  final class ZipSubscriber extends Subscriber<Observable[]>
  {
    final Subscriber<? super R> child;
    final OperatorZip.ZipProducer<R> producer;
    boolean started;
    final OperatorZip.Zip<R> zipper;

    public ZipSubscriber(OperatorZip.Zip<R> paramZipProducer, OperatorZip.ZipProducer<R> arg3)
    {
      this.child = paramZipProducer;
      Object localObject1;
      this.zipper = localObject1;
      Object localObject2;
      this.producer = localObject2;
    }

    public void onCompleted()
    {
      if (!this.started)
        this.child.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      this.child.onError(paramThrowable);
    }

    public void onNext(Observable[] paramArrayOfObservable)
    {
      if ((paramArrayOfObservable == null) || (paramArrayOfObservable.length == 0))
      {
        this.child.onCompleted();
        return;
      }
      this.started = true;
      this.zipper.start(paramArrayOfObservable, this.producer);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorZip
 * JD-Core Version:    0.6.0
 */