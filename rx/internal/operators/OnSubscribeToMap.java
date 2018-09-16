package rx.internal.operators;

import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func1;

public final class OnSubscribeToMap<T, K, V>
  implements Observable.OnSubscribe<Map<K, V>>, Func0<Map<K, V>>
{
  final Func1<? super T, ? extends K> keySelector;
  final Func0<? extends Map<K, V>> mapFactory;
  final Observable<T> source;
  final Func1<? super T, ? extends V> valueSelector;

  public OnSubscribeToMap(Observable<T> paramObservable, Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11)
  {
    this(paramObservable, paramFunc1, paramFunc11, null);
  }

  public OnSubscribeToMap(Observable<T> paramObservable, Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11, Func0<? extends Map<K, V>> paramFunc0)
  {
    this.source = paramObservable;
    this.keySelector = paramFunc1;
    this.valueSelector = paramFunc11;
    if (paramFunc0 == null)
    {
      this.mapFactory = this;
      return;
    }
    this.mapFactory = paramFunc0;
  }

  public Map<K, V> call()
  {
    return new HashMap();
  }

  public void call(Subscriber<? super Map<K, V>> paramSubscriber)
  {
    try
    {
      Map localMap = (Map)this.mapFactory.call();
      new ToMapSubscriber(paramSubscriber, localMap, this.keySelector, this.valueSelector).subscribeTo(this.source);
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwOrReport(localThrowable, paramSubscriber);
    }
  }

  static final class ToMapSubscriber<T, K, V> extends DeferredScalarSubscriberSafe<T, Map<K, V>>
  {
    final Func1<? super T, ? extends K> keySelector;
    final Func1<? super T, ? extends V> valueSelector;

    ToMapSubscriber(Subscriber<? super Map<K, V>> paramSubscriber, Map<K, V> paramMap, Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11)
    {
      super();
      this.value = paramMap;
      this.hasValue = true;
      this.keySelector = paramFunc1;
      this.valueSelector = paramFunc11;
    }

    public void onNext(T paramT)
    {
      if (this.done)
        return;
      try
      {
        Object localObject1 = this.keySelector.call(paramT);
        Object localObject2 = this.valueSelector.call(paramT);
        ((Map)this.value).put(localObject1, localObject2);
        return;
      }
      catch (Throwable localThrowable)
      {
        Exceptions.throwIfFatal(localThrowable);
        unsubscribe();
        onError(localThrowable);
      }
    }

    public void onStart()
    {
      request(9223372036854775807L);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OnSubscribeToMap
 * JD-Core Version:    0.6.0
 */