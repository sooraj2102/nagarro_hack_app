package rx.subjects;

import java.util.concurrent.TimeUnit;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Scheduler.Worker;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.internal.operators.NotificationLite;
import rx.schedulers.TestScheduler;

public final class TestSubject<T> extends Subject<T, T>
{
  private final Scheduler.Worker innerScheduler;
  private final SubjectSubscriptionManager<T> state;

  protected TestSubject(Observable.OnSubscribe<T> paramOnSubscribe, SubjectSubscriptionManager<T> paramSubjectSubscriptionManager, TestScheduler paramTestScheduler)
  {
    super(paramOnSubscribe);
    this.state = paramSubjectSubscriptionManager;
    this.innerScheduler = paramTestScheduler.createWorker();
  }

  public static <T> TestSubject<T> create(TestScheduler paramTestScheduler)
  {
    SubjectSubscriptionManager localSubjectSubscriptionManager = new SubjectSubscriptionManager();
    localSubjectSubscriptionManager.onAdded = new Action1(localSubjectSubscriptionManager)
    {
      public void call(SubjectSubscriptionManager.SubjectObserver<T> paramSubjectObserver)
      {
        paramSubjectObserver.emitFirst(this.val$state.getLatest(), this.val$state.nl);
      }
    };
    localSubjectSubscriptionManager.onTerminated = localSubjectSubscriptionManager.onAdded;
    return new TestSubject(localSubjectSubscriptionManager, localSubjectSubscriptionManager, paramTestScheduler);
  }

  public boolean hasObservers()
  {
    return this.state.observers().length > 0;
  }

  void internalOnCompleted()
  {
    if (this.state.active)
    {
      SubjectSubscriptionManager.SubjectObserver[] arrayOfSubjectObserver = this.state.terminate(NotificationLite.instance().completed());
      int i = arrayOfSubjectObserver.length;
      for (int j = 0; j < i; j++)
        arrayOfSubjectObserver[j].onCompleted();
    }
  }

  void internalOnError(Throwable paramThrowable)
  {
    if (this.state.active)
    {
      SubjectSubscriptionManager.SubjectObserver[] arrayOfSubjectObserver = this.state.terminate(NotificationLite.instance().error(paramThrowable));
      int i = arrayOfSubjectObserver.length;
      for (int j = 0; j < i; j++)
        arrayOfSubjectObserver[j].onError(paramThrowable);
    }
  }

  void internalOnNext(T paramT)
  {
    SubjectSubscriptionManager.SubjectObserver[] arrayOfSubjectObserver = this.state.observers();
    int i = arrayOfSubjectObserver.length;
    for (int j = 0; j < i; j++)
      arrayOfSubjectObserver[j].onNext(paramT);
  }

  public void onCompleted()
  {
    onCompleted(0L);
  }

  public void onCompleted(long paramLong)
  {
    this.innerScheduler.schedule(new Action0()
    {
      public void call()
      {
        TestSubject.this.internalOnCompleted();
      }
    }
    , paramLong, TimeUnit.MILLISECONDS);
  }

  public void onError(Throwable paramThrowable)
  {
    onError(paramThrowable, 0L);
  }

  public void onError(Throwable paramThrowable, long paramLong)
  {
    this.innerScheduler.schedule(new Action0(paramThrowable)
    {
      public void call()
      {
        TestSubject.this.internalOnError(this.val$e);
      }
    }
    , paramLong, TimeUnit.MILLISECONDS);
  }

  public void onNext(T paramT)
  {
    onNext(paramT, 0L);
  }

  public void onNext(T paramT, long paramLong)
  {
    this.innerScheduler.schedule(new Action0(paramT)
    {
      public void call()
      {
        TestSubject.this.internalOnNext(this.val$v);
      }
    }
    , paramLong, TimeUnit.MILLISECONDS);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.subjects.TestSubject
 * JD-Core Version:    0.6.0
 */