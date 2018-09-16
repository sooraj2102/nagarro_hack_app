package rx.internal.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.observables.ConnectableObservable;
import rx.observers.Subscribers;
import rx.subjects.Subject;
import rx.subscriptions.Subscriptions;

public final class OperatorMulticast<T, R> extends ConnectableObservable<R>
{
  final AtomicReference<Subject<? super T, ? extends R>> connectedSubject;
  final Object guard;
  Subscription guardedSubscription;
  final Observable<? extends T> source;
  final Func0<? extends Subject<? super T, ? extends R>> subjectFactory;
  Subscriber<T> subscription;
  final List<Subscriber<? super R>> waitingForConnect;

  private OperatorMulticast(Object paramObject, AtomicReference<Subject<? super T, ? extends R>> paramAtomicReference, List<Subscriber<? super R>> paramList, Observable<? extends T> paramObservable, Func0<? extends Subject<? super T, ? extends R>> paramFunc0)
  {
    super(new Observable.OnSubscribe(paramAtomicReference, paramList)
    {
      public void call(Subscriber<? super R> paramSubscriber)
      {
        synchronized (OperatorMulticast.this)
        {
          if (this.val$connectedSubject.get() == null)
          {
            this.val$waitingForConnect.add(paramSubscriber);
            return;
          }
          ((Subject)this.val$connectedSubject.get()).unsafeSubscribe(paramSubscriber);
        }
      }
    });
    this.guard = paramObject;
    this.connectedSubject = paramAtomicReference;
    this.waitingForConnect = paramList;
    this.source = paramObservable;
    this.subjectFactory = paramFunc0;
  }

  public OperatorMulticast(Observable<? extends T> paramObservable, Func0<? extends Subject<? super T, ? extends R>> paramFunc0)
  {
    this(new Object(), new AtomicReference(), new ArrayList(), paramObservable, paramFunc0);
  }

  public void connect(Action1<? super Subscription> paramAction1)
  {
    Subject localSubject;
    synchronized (this.guard)
    {
      if (this.subscription != null)
      {
        paramAction1.call(this.guardedSubscription);
        return;
      }
      localSubject = (Subject)this.subjectFactory.call();
      this.subscription = Subscribers.from(localSubject);
      AtomicReference localAtomicReference = new AtomicReference();
      localAtomicReference.set(Subscriptions.create(new Action0(localAtomicReference)
      {
        public void call()
        {
          synchronized (OperatorMulticast.this.guard)
          {
            if (OperatorMulticast.this.guardedSubscription == this.val$gs.get())
            {
              Subscriber localSubscriber = OperatorMulticast.this.subscription;
              OperatorMulticast.this.subscription = null;
              OperatorMulticast.this.guardedSubscription = null;
              OperatorMulticast.this.connectedSubject.set(null);
              if (localSubscriber != null)
                localSubscriber.unsubscribe();
              return;
            }
            return;
          }
        }
      }));
      this.guardedSubscription = ((Subscription)localAtomicReference.get());
      Iterator localIterator = this.waitingForConnect.iterator();
      if (localIterator.hasNext())
      {
        Subscriber localSubscriber2 = (Subscriber)localIterator.next();
        localSubject.unsafeSubscribe(new Subscriber(localSubscriber2, localSubscriber2)
        {
          public void onCompleted()
          {
            this.val$s.onCompleted();
          }

          public void onError(Throwable paramThrowable)
          {
            this.val$s.onError(paramThrowable);
          }

          public void onNext(R paramR)
          {
            this.val$s.onNext(paramR);
          }
        });
      }
    }
    this.waitingForConnect.clear();
    this.connectedSubject.set(localSubject);
    monitorexit;
    paramAction1.call(this.guardedSubscription);
    synchronized (this.guard)
    {
      Subscriber localSubscriber1 = this.subscription;
      if (localSubscriber1 != null)
      {
        this.source.subscribe(localSubscriber1);
        return;
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorMulticast
 * JD-Core Version:    0.6.0
 */