package rx.subjects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.exceptions.MissingBackpressureException;
import rx.internal.operators.BackpressureUtils;

public final class PublishSubject<T> extends Subject<T, T>
{
  final PublishSubjectState<T> state;

  protected PublishSubject(PublishSubjectState<T> paramPublishSubjectState)
  {
    super(paramPublishSubjectState);
    this.state = paramPublishSubjectState;
  }

  public static <T> PublishSubject<T> create()
  {
    return new PublishSubject(new PublishSubjectState());
  }

  public Throwable getThrowable()
  {
    if (this.state.get() == PublishSubjectState.TERMINATED)
      return this.state.error;
    return null;
  }

  public boolean hasCompleted()
  {
    return (this.state.get() == PublishSubjectState.TERMINATED) && (this.state.error == null);
  }

  public boolean hasObservers()
  {
    return ((PublishSubjectProducer[])this.state.get()).length != 0;
  }

  public boolean hasThrowable()
  {
    return (this.state.get() == PublishSubjectState.TERMINATED) && (this.state.error != null);
  }

  public void onCompleted()
  {
    this.state.onCompleted();
  }

  public void onError(Throwable paramThrowable)
  {
    this.state.onError(paramThrowable);
  }

  public void onNext(T paramT)
  {
    this.state.onNext(paramT);
  }

  static final class PublishSubjectProducer<T> extends AtomicLong
    implements Producer, Subscription, Observer<T>
  {
    private static final long serialVersionUID = 6451806817170721536L;
    final Subscriber<? super T> actual;
    final PublishSubject.PublishSubjectState<T> parent;
    long produced;

    public PublishSubjectProducer(PublishSubject.PublishSubjectState<T> paramPublishSubjectState, Subscriber<? super T> paramSubscriber)
    {
      this.parent = paramPublishSubjectState;
      this.actual = paramSubscriber;
    }

    public boolean isUnsubscribed()
    {
      return get() == -9223372036854775808L;
    }

    public void onCompleted()
    {
      if (get() != -9223372036854775808L)
        this.actual.onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      if (get() != -9223372036854775808L)
        this.actual.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      long l1 = get();
      if (l1 != -9223372036854775808L)
      {
        long l2 = this.produced;
        if (l1 != l2)
        {
          this.produced = (1L + l2);
          this.actual.onNext(paramT);
        }
      }
      else
      {
        return;
      }
      unsubscribe();
      this.actual.onError(new MissingBackpressureException("PublishSubject: could not emit value due to lack of requests"));
    }

    public void request(long paramLong)
    {
      if (BackpressureUtils.validate(paramLong));
      long l;
      do
      {
        l = get();
        if (l == -9223372036854775808L)
          return;
      }
      while (!compareAndSet(l, BackpressureUtils.addCap(l, paramLong)));
    }

    public void unsubscribe()
    {
      if (getAndSet(-9223372036854775808L) != -9223372036854775808L)
        this.parent.remove(this);
    }
  }

  static final class PublishSubjectState<T> extends AtomicReference<PublishSubject.PublishSubjectProducer<T>[]>
    implements Observable.OnSubscribe<T>, Observer<T>
  {
    static final PublishSubject.PublishSubjectProducer[] EMPTY = new PublishSubject.PublishSubjectProducer[0];
    static final PublishSubject.PublishSubjectProducer[] TERMINATED = new PublishSubject.PublishSubjectProducer[0];
    private static final long serialVersionUID = -7568940796666027140L;
    Throwable error;

    public PublishSubjectState()
    {
      lazySet(EMPTY);
    }

    boolean add(PublishSubject.PublishSubjectProducer<T> paramPublishSubjectProducer)
    {
      PublishSubject.PublishSubjectProducer[] arrayOfPublishSubjectProducer1;
      PublishSubject.PublishSubjectProducer[] arrayOfPublishSubjectProducer2;
      do
      {
        arrayOfPublishSubjectProducer1 = (PublishSubject.PublishSubjectProducer[])get();
        if (arrayOfPublishSubjectProducer1 == TERMINATED)
          return false;
        int i = arrayOfPublishSubjectProducer1.length;
        arrayOfPublishSubjectProducer2 = new PublishSubject.PublishSubjectProducer[i + 1];
        System.arraycopy(arrayOfPublishSubjectProducer1, 0, arrayOfPublishSubjectProducer2, 0, i);
        arrayOfPublishSubjectProducer2[i] = paramPublishSubjectProducer;
      }
      while (!compareAndSet(arrayOfPublishSubjectProducer1, arrayOfPublishSubjectProducer2));
      return true;
    }

    public void call(Subscriber<? super T> paramSubscriber)
    {
      PublishSubject.PublishSubjectProducer localPublishSubjectProducer = new PublishSubject.PublishSubjectProducer(this, paramSubscriber);
      paramSubscriber.add(localPublishSubjectProducer);
      paramSubscriber.setProducer(localPublishSubjectProducer);
      if (add(localPublishSubjectProducer))
      {
        if (localPublishSubjectProducer.isUnsubscribed())
          remove(localPublishSubjectProducer);
        return;
      }
      Throwable localThrowable = this.error;
      if (localThrowable != null)
      {
        paramSubscriber.onError(localThrowable);
        return;
      }
      paramSubscriber.onCompleted();
    }

    public void onCompleted()
    {
      PublishSubject.PublishSubjectProducer[] arrayOfPublishSubjectProducer = (PublishSubject.PublishSubjectProducer[])getAndSet(TERMINATED);
      int i = arrayOfPublishSubjectProducer.length;
      for (int j = 0; j < i; j++)
        arrayOfPublishSubjectProducer[j].onCompleted();
    }

    public void onError(Throwable paramThrowable)
    {
      this.error = paramThrowable;
      ArrayList localArrayList = null;
      PublishSubject.PublishSubjectProducer[] arrayOfPublishSubjectProducer = (PublishSubject.PublishSubjectProducer[])getAndSet(TERMINATED);
      int i = arrayOfPublishSubjectProducer.length;
      int j = 0;
      while (true)
        if (j < i)
        {
          PublishSubject.PublishSubjectProducer localPublishSubjectProducer = arrayOfPublishSubjectProducer[j];
          try
          {
            localPublishSubjectProducer.onError(paramThrowable);
            j++;
          }
          catch (Throwable localThrowable)
          {
            while (true)
            {
              if (localArrayList == null)
                localArrayList = new ArrayList(1);
              localArrayList.add(localThrowable);
            }
          }
        }
      Exceptions.throwIfAny(localArrayList);
    }

    public void onNext(T paramT)
    {
      PublishSubject.PublishSubjectProducer[] arrayOfPublishSubjectProducer = (PublishSubject.PublishSubjectProducer[])get();
      int i = arrayOfPublishSubjectProducer.length;
      for (int j = 0; j < i; j++)
        arrayOfPublishSubjectProducer[j].onNext(paramT);
    }

    void remove(PublishSubject.PublishSubjectProducer<T> paramPublishSubjectProducer)
    {
      PublishSubject.PublishSubjectProducer[] arrayOfPublishSubjectProducer1 = (PublishSubject.PublishSubjectProducer[])get();
      if ((arrayOfPublishSubjectProducer1 == TERMINATED) || (arrayOfPublishSubjectProducer1 == EMPTY));
      int i;
      int j;
      int k;
      label32: PublishSubject.PublishSubjectProducer[] arrayOfPublishSubjectProducer2;
      while (true)
      {
        return;
        i = arrayOfPublishSubjectProducer1.length;
        j = -1;
        k = 0;
        if (k < i)
        {
          if (arrayOfPublishSubjectProducer1[k] != paramPublishSubjectProducer)
            break;
          j = k;
        }
        else
        {
          if (j < 0)
            continue;
          if (i != 1)
            break label82;
          arrayOfPublishSubjectProducer2 = EMPTY;
        }
      }
      while (compareAndSet(arrayOfPublishSubjectProducer1, arrayOfPublishSubjectProducer2))
      {
        return;
        k++;
        break label32;
        label82: arrayOfPublishSubjectProducer2 = new PublishSubject.PublishSubjectProducer[i - 1];
        System.arraycopy(arrayOfPublishSubjectProducer1, 0, arrayOfPublishSubjectProducer2, 0, j);
        System.arraycopy(arrayOfPublishSubjectProducer1, j + 1, arrayOfPublishSubjectProducer2, j, -1 + (i - j));
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.subjects.PublishSubject
 * JD-Core Version:    0.6.0
 */