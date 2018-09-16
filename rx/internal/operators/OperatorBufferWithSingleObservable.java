package rx.internal.operators;

import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.observers.SerializedSubscriber;
import rx.observers.Subscribers;

public final class OperatorBufferWithSingleObservable<T, TClosing>
  implements Observable.Operator<List<T>, T>
{
  final Func0<? extends Observable<? extends TClosing>> bufferClosingSelector;
  final int initialCapacity;

  public OperatorBufferWithSingleObservable(Observable<? extends TClosing> paramObservable, int paramInt)
  {
    this.bufferClosingSelector = new Func0(paramObservable)
    {
      public Observable<? extends TClosing> call()
      {
        return this.val$bufferClosing;
      }
    };
    this.initialCapacity = paramInt;
  }

  public OperatorBufferWithSingleObservable(Func0<? extends Observable<? extends TClosing>> paramFunc0, int paramInt)
  {
    this.bufferClosingSelector = paramFunc0;
    this.initialCapacity = paramInt;
  }

  public Subscriber<? super T> call(Subscriber<? super List<T>> paramSubscriber)
  {
    try
    {
      Observable localObservable = (Observable)this.bufferClosingSelector.call();
      BufferingSubscriber localBufferingSubscriber = new BufferingSubscriber(new SerializedSubscriber(paramSubscriber));
      2 local2 = new Subscriber(localBufferingSubscriber)
      {
        public void onCompleted()
        {
          this.val$s.onCompleted();
        }

        public void onError(Throwable paramThrowable)
        {
          this.val$s.onError(paramThrowable);
        }

        public void onNext(TClosing paramTClosing)
        {
          this.val$s.emit();
        }
      };
      paramSubscriber.add(local2);
      paramSubscriber.add(localBufferingSubscriber);
      localObservable.unsafeSubscribe(local2);
      return localBufferingSubscriber;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwOrReport(localThrowable, paramSubscriber);
    }
    return Subscribers.empty();
  }

  final class BufferingSubscriber extends Subscriber<T>
  {
    final Subscriber<? super List<T>> child;
    List<T> chunk;
    boolean done;

    public BufferingSubscriber()
    {
      Object localObject;
      this.child = localObject;
      this.chunk = new ArrayList(OperatorBufferWithSingleObservable.this.initialCapacity);
    }

    void emit()
    {
      monitorenter;
      try
      {
        if (this.done)
          return;
        List localList = this.chunk;
        this.chunk = new ArrayList(OperatorBufferWithSingleObservable.this.initialCapacity);
        monitorexit;
        try
        {
          this.child.onNext(localList);
          return;
        }
        catch (Throwable localThrowable)
        {
          unsubscribe();
          monitorenter;
          try
          {
            if (this.done)
              return;
          }
          finally
          {
            monitorexit;
          }
        }
      }
      finally
      {
        monitorexit;
      }
      this.done = true;
      monitorexit;
      Exceptions.throwOrReport(localThrowable, this.child);
    }

    public void onCompleted()
    {
      try
      {
        monitorenter;
        try
        {
          if (this.done)
            return;
          this.done = true;
          List localList = this.chunk;
          this.chunk = null;
          monitorexit;
          this.child.onNext(localList);
          this.child.onCompleted();
          unsubscribe();
          return;
        }
        finally
        {
          monitorexit;
        }
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwOrReport(localThrowable, this.child);
      }
    }

    public void onError(Throwable paramThrowable)
    {
      monitorenter;
      try
      {
        if (this.done)
          return;
        this.done = true;
        this.chunk = null;
        monitorexit;
        this.child.onError(paramThrowable);
        unsubscribe();
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void onNext(T paramT)
    {
      monitorenter;
      try
      {
        if (this.done)
          return;
        this.chunk.add(paramT);
        return;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorBufferWithSingleObservable
 * JD-Core Version:    0.6.0
 */