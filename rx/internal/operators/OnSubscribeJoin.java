package rx.internal.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.SerializedSubscriber;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.SerialSubscription;

public final class OnSubscribeJoin<TLeft, TRight, TLeftDuration, TRightDuration, R>
  implements Observable.OnSubscribe<R>
{
  final Observable<TLeft> left;
  final Func1<TLeft, Observable<TLeftDuration>> leftDurationSelector;
  final Func2<TLeft, TRight, R> resultSelector;
  final Observable<TRight> right;
  final Func1<TRight, Observable<TRightDuration>> rightDurationSelector;

  public OnSubscribeJoin(Observable<TLeft> paramObservable, Observable<TRight> paramObservable1, Func1<TLeft, Observable<TLeftDuration>> paramFunc1, Func1<TRight, Observable<TRightDuration>> paramFunc11, Func2<TLeft, TRight, R> paramFunc2)
  {
    this.left = paramObservable;
    this.right = paramObservable1;
    this.leftDurationSelector = paramFunc1;
    this.rightDurationSelector = paramFunc11;
    this.resultSelector = paramFunc2;
  }

  public void call(Subscriber<? super R> paramSubscriber)
  {
    new ResultSink(new SerializedSubscriber(paramSubscriber)).run();
  }

  final class ResultSink extends HashMap<Integer, TLeft>
  {
    private static final long serialVersionUID = 3491669543549085380L;
    final CompositeSubscription group;
    boolean leftDone;
    int leftId;
    boolean rightDone;
    int rightId;
    final Map<Integer, TRight> rightMap;
    final Subscriber<? super R> subscriber;

    public ResultSink()
    {
      Object localObject;
      this.subscriber = localObject;
      this.group = new CompositeSubscription();
      this.rightMap = new HashMap();
    }

    HashMap<Integer, TLeft> leftMap()
    {
      return this;
    }

    public void run()
    {
      this.subscriber.add(this.group);
      LeftSubscriber localLeftSubscriber = new LeftSubscriber();
      RightSubscriber localRightSubscriber = new RightSubscriber();
      this.group.add(localLeftSubscriber);
      this.group.add(localRightSubscriber);
      OnSubscribeJoin.this.left.unsafeSubscribe(localLeftSubscriber);
      OnSubscribeJoin.this.right.unsafeSubscribe(localRightSubscriber);
    }

    final class LeftSubscriber extends Subscriber<TLeft>
    {
      LeftSubscriber()
      {
      }

      protected void expire(int paramInt, Subscription paramSubscription)
      {
        synchronized (OnSubscribeJoin.ResultSink.this)
        {
          Object localObject2 = OnSubscribeJoin.ResultSink.this.leftMap().remove(Integer.valueOf(paramInt));
          int i = 0;
          if (localObject2 != null)
          {
            boolean bool1 = OnSubscribeJoin.ResultSink.this.leftMap().isEmpty();
            i = 0;
            if (bool1)
            {
              boolean bool2 = OnSubscribeJoin.ResultSink.this.leftDone;
              i = 0;
              if (bool2)
                i = 1;
            }
          }
          if (i != 0)
          {
            OnSubscribeJoin.ResultSink.this.subscriber.onCompleted();
            OnSubscribeJoin.ResultSink.this.subscriber.unsubscribe();
            return;
          }
        }
        OnSubscribeJoin.ResultSink.this.group.remove(paramSubscription);
      }

      public void onCompleted()
      {
        while (true)
        {
          synchronized (OnSubscribeJoin.ResultSink.this)
          {
            OnSubscribeJoin.ResultSink.this.leftDone = true;
            if (OnSubscribeJoin.ResultSink.this.rightDone)
              break label91;
            boolean bool = OnSubscribeJoin.ResultSink.this.leftMap().isEmpty();
            i = 0;
            if (bool)
              break label91;
            if (i != 0)
            {
              OnSubscribeJoin.ResultSink.this.subscriber.onCompleted();
              OnSubscribeJoin.ResultSink.this.subscriber.unsubscribe();
              return;
            }
          }
          OnSubscribeJoin.ResultSink.this.group.remove(this);
          return;
          label91: int i = 1;
        }
      }

      public void onError(Throwable paramThrowable)
      {
        OnSubscribeJoin.ResultSink.this.subscriber.onError(paramThrowable);
        OnSubscribeJoin.ResultSink.this.subscriber.unsubscribe();
      }

      public void onNext(TLeft paramTLeft)
      {
        ArrayList localArrayList;
        synchronized (OnSubscribeJoin.ResultSink.this)
        {
          OnSubscribeJoin.ResultSink localResultSink2 = OnSubscribeJoin.ResultSink.this;
          int i = localResultSink2.leftId;
          localResultSink2.leftId = (i + 1);
          OnSubscribeJoin.ResultSink.this.leftMap().put(Integer.valueOf(i), paramTLeft);
          int j = OnSubscribeJoin.ResultSink.this.rightId;
          try
          {
            Observable localObservable = (Observable)OnSubscribeJoin.this.leftDurationSelector.call(paramTLeft);
            LeftDurationSubscriber localLeftDurationSubscriber = new LeftDurationSubscriber(i);
            OnSubscribeJoin.ResultSink.this.group.add(localLeftDurationSubscriber);
            localObservable.unsafeSubscribe(localLeftDurationSubscriber);
            localArrayList = new ArrayList();
            synchronized (OnSubscribeJoin.ResultSink.this)
            {
              Iterator localIterator1 = OnSubscribeJoin.ResultSink.this.rightMap.entrySet().iterator();
              Map.Entry localEntry;
              do
              {
                if (!localIterator1.hasNext())
                  break;
                localEntry = (Map.Entry)localIterator1.next();
              }
              while (((Integer)localEntry.getKey()).intValue() >= j);
              localArrayList.add(localEntry.getValue());
            }
          }
          catch (Throwable localThrowable)
          {
            Exceptions.throwOrReport(localThrowable, this);
          }
          return;
        }
        monitorexit;
        Iterator localIterator2 = localArrayList.iterator();
        while (localIterator2.hasNext())
        {
          Object localObject3 = localIterator2.next();
          Object localObject4 = OnSubscribeJoin.this.resultSelector.call(paramTLeft, localObject3);
          OnSubscribeJoin.ResultSink.this.subscriber.onNext(localObject4);
        }
      }

      final class LeftDurationSubscriber extends Subscriber<TLeftDuration>
      {
        final int id;
        boolean once = true;

        public LeftDurationSubscriber(int arg2)
        {
          int i;
          this.id = i;
        }

        public void onCompleted()
        {
          if (this.once)
          {
            this.once = false;
            OnSubscribeJoin.ResultSink.LeftSubscriber.this.expire(this.id, this);
          }
        }

        public void onError(Throwable paramThrowable)
        {
          OnSubscribeJoin.ResultSink.LeftSubscriber.this.onError(paramThrowable);
        }

        public void onNext(TLeftDuration paramTLeftDuration)
        {
          onCompleted();
        }
      }
    }

    final class RightSubscriber extends Subscriber<TRight>
    {
      RightSubscriber()
      {
      }

      void expire(int paramInt, Subscription paramSubscription)
      {
        synchronized (OnSubscribeJoin.ResultSink.this)
        {
          Object localObject2 = OnSubscribeJoin.ResultSink.this.rightMap.remove(Integer.valueOf(paramInt));
          int i = 0;
          if (localObject2 != null)
          {
            boolean bool1 = OnSubscribeJoin.ResultSink.this.rightMap.isEmpty();
            i = 0;
            if (bool1)
            {
              boolean bool2 = OnSubscribeJoin.ResultSink.this.rightDone;
              i = 0;
              if (bool2)
                i = 1;
            }
          }
          if (i != 0)
          {
            OnSubscribeJoin.ResultSink.this.subscriber.onCompleted();
            OnSubscribeJoin.ResultSink.this.subscriber.unsubscribe();
            return;
          }
        }
        OnSubscribeJoin.ResultSink.this.group.remove(paramSubscription);
      }

      public void onCompleted()
      {
        while (true)
        {
          synchronized (OnSubscribeJoin.ResultSink.this)
          {
            OnSubscribeJoin.ResultSink.this.rightDone = true;
            if (OnSubscribeJoin.ResultSink.this.leftDone)
              break label93;
            boolean bool = OnSubscribeJoin.ResultSink.this.rightMap.isEmpty();
            i = 0;
            if (bool)
              break label93;
            if (i != 0)
            {
              OnSubscribeJoin.ResultSink.this.subscriber.onCompleted();
              OnSubscribeJoin.ResultSink.this.subscriber.unsubscribe();
              return;
            }
          }
          OnSubscribeJoin.ResultSink.this.group.remove(this);
          return;
          label93: int i = 1;
        }
      }

      public void onError(Throwable paramThrowable)
      {
        OnSubscribeJoin.ResultSink.this.subscriber.onError(paramThrowable);
        OnSubscribeJoin.ResultSink.this.subscriber.unsubscribe();
      }

      public void onNext(TRight paramTRight)
      {
        ArrayList localArrayList;
        synchronized (OnSubscribeJoin.ResultSink.this)
        {
          OnSubscribeJoin.ResultSink localResultSink2 = OnSubscribeJoin.ResultSink.this;
          int i = localResultSink2.rightId;
          localResultSink2.rightId = (i + 1);
          OnSubscribeJoin.ResultSink.this.rightMap.put(Integer.valueOf(i), paramTRight);
          int j = OnSubscribeJoin.ResultSink.this.leftId;
          SerialSubscription localSerialSubscription = new SerialSubscription();
          OnSubscribeJoin.ResultSink.this.group.add(localSerialSubscription);
          try
          {
            Observable localObservable = (Observable)OnSubscribeJoin.this.rightDurationSelector.call(paramTRight);
            RightDurationSubscriber localRightDurationSubscriber = new RightDurationSubscriber(i);
            OnSubscribeJoin.ResultSink.this.group.add(localRightDurationSubscriber);
            localObservable.unsafeSubscribe(localRightDurationSubscriber);
            localArrayList = new ArrayList();
            synchronized (OnSubscribeJoin.ResultSink.this)
            {
              Iterator localIterator1 = OnSubscribeJoin.ResultSink.this.leftMap().entrySet().iterator();
              Map.Entry localEntry;
              do
              {
                if (!localIterator1.hasNext())
                  break;
                localEntry = (Map.Entry)localIterator1.next();
              }
              while (((Integer)localEntry.getKey()).intValue() >= j);
              localArrayList.add(localEntry.getValue());
            }
          }
          catch (Throwable localThrowable)
          {
            Exceptions.throwOrReport(localThrowable, this);
          }
          return;
        }
        monitorexit;
        Iterator localIterator2 = localArrayList.iterator();
        while (localIterator2.hasNext())
        {
          Object localObject3 = localIterator2.next();
          Object localObject4 = OnSubscribeJoin.this.resultSelector.call(localObject3, paramTRight);
          OnSubscribeJoin.ResultSink.this.subscriber.onNext(localObject4);
        }
      }

      final class RightDurationSubscriber extends Subscriber<TRightDuration>
      {
        final int id;
        boolean once = true;

        public RightDurationSubscriber(int arg2)
        {
          int i;
          this.id = i;
        }

        public void onCompleted()
        {
          if (this.once)
          {
            this.once = false;
            OnSubscribeJoin.ResultSink.RightSubscriber.this.expire(this.id, this);
          }
        }

        public void onError(Throwable paramThrowable)
        {
          OnSubscribeJoin.ResultSink.RightSubscriber.this.onError(paramThrowable);
        }

        public void onNext(TRightDuration paramTRightDuration)
        {
          onCompleted();
        }
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeJoin
 * JD-Core Version:    0.6.0
 */