package rx.subjects;

import java.util.ArrayList;
import java.util.List;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.internal.operators.NotificationLite;
import rx.internal.producers.SingleProducer;

public final class AsyncSubject<T> extends Subject<T, T>
{
  volatile Object lastValue;
  private final NotificationLite<T> nl = NotificationLite.instance();
  final SubjectSubscriptionManager<T> state;

  protected AsyncSubject(Observable.OnSubscribe<T> paramOnSubscribe, SubjectSubscriptionManager<T> paramSubjectSubscriptionManager)
  {
    super(paramOnSubscribe);
    this.state = paramSubjectSubscriptionManager;
  }

  public static <T> AsyncSubject<T> create()
  {
    SubjectSubscriptionManager localSubjectSubscriptionManager = new SubjectSubscriptionManager();
    localSubjectSubscriptionManager.onTerminated = new Action1(localSubjectSubscriptionManager)
    {
      public void call(SubjectSubscriptionManager.SubjectObserver<T> paramSubjectObserver)
      {
        Object localObject = this.val$state.getLatest();
        NotificationLite localNotificationLite = this.val$state.nl;
        if ((localObject == null) || (localNotificationLite.isCompleted(localObject)))
        {
          paramSubjectObserver.onCompleted();
          return;
        }
        if (localNotificationLite.isError(localObject))
        {
          paramSubjectObserver.onError(localNotificationLite.getError(localObject));
          return;
        }
        paramSubjectObserver.actual.setProducer(new SingleProducer(paramSubjectObserver.actual, localNotificationLite.getValue(localObject)));
      }
    };
    return new AsyncSubject(localSubjectSubscriptionManager, localSubjectSubscriptionManager);
  }

  public Throwable getThrowable()
  {
    Object localObject = this.state.getLatest();
    if (this.nl.isError(localObject))
      return this.nl.getError(localObject);
    return null;
  }

  public T getValue()
  {
    Object localObject1 = this.lastValue;
    Object localObject2 = this.state.getLatest();
    if ((!this.nl.isError(localObject2)) && (this.nl.isNext(localObject1)))
      return this.nl.getValue(localObject1);
    return null;
  }

  public boolean hasCompleted()
  {
    Object localObject = this.state.getLatest();
    return (localObject != null) && (!this.nl.isError(localObject));
  }

  public boolean hasObservers()
  {
    return this.state.observers().length > 0;
  }

  public boolean hasThrowable()
  {
    Object localObject = this.state.getLatest();
    return this.nl.isError(localObject);
  }

  public boolean hasValue()
  {
    Object localObject1 = this.lastValue;
    Object localObject2 = this.state.getLatest();
    return (!this.nl.isError(localObject2)) && (this.nl.isNext(localObject1));
  }

  public void onCompleted()
  {
    if (this.state.active)
    {
      Object localObject = this.lastValue;
      if (localObject == null)
        localObject = this.nl.completed();
      SubjectSubscriptionManager.SubjectObserver[] arrayOfSubjectObserver = this.state.terminate(localObject);
      int i = arrayOfSubjectObserver.length;
      int j = 0;
      if (j < i)
      {
        SubjectSubscriptionManager.SubjectObserver localSubjectObserver = arrayOfSubjectObserver[j];
        if (localObject == this.nl.completed())
          localSubjectObserver.onCompleted();
        while (true)
        {
          j++;
          break;
          localSubjectObserver.actual.setProducer(new SingleProducer(localSubjectObserver.actual, this.nl.getValue(localObject)));
        }
      }
    }
  }

  public void onError(Throwable paramThrowable)
  {
    if (this.state.active)
    {
      Object localObject = this.nl.error(paramThrowable);
      ArrayList localArrayList = null;
      SubjectSubscriptionManager.SubjectObserver[] arrayOfSubjectObserver = this.state.terminate(localObject);
      int i = arrayOfSubjectObserver.length;
      int j = 0;
      while (true)
        if (j < i)
        {
          SubjectSubscriptionManager.SubjectObserver localSubjectObserver = arrayOfSubjectObserver[j];
          try
          {
            localSubjectObserver.onError(paramThrowable);
            j++;
          }
          catch (Throwable localThrowable)
          {
            while (true)
            {
              if (localArrayList == null)
                localArrayList = new ArrayList();
              localArrayList.add(localThrowable);
            }
          }
        }
      Exceptions.throwIfAny(localArrayList);
    }
  }

  public void onNext(T paramT)
  {
    this.lastValue = this.nl.next(paramT);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.subjects.AsyncSubject
 * JD-Core Version:    0.6.0
 */