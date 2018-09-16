package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func1;

public final class OnSubscribeToMultimap<T, K, V>
  implements Observable.OnSubscribe<Map<K, Collection<V>>>, Func0<Map<K, Collection<V>>>
{
  private final Func1<? super K, ? extends Collection<V>> collectionFactory;
  private final Func1<? super T, ? extends K> keySelector;
  private final Func0<? extends Map<K, Collection<V>>> mapFactory;
  private final Observable<T> source;
  private final Func1<? super T, ? extends V> valueSelector;

  public OnSubscribeToMultimap(Observable<T> paramObservable, Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11)
  {
    this(paramObservable, paramFunc1, paramFunc11, null, DefaultMultimapCollectionFactory.instance());
  }

  public OnSubscribeToMultimap(Observable<T> paramObservable, Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11, Func0<? extends Map<K, Collection<V>>> paramFunc0)
  {
    this(paramObservable, paramFunc1, paramFunc11, paramFunc0, DefaultMultimapCollectionFactory.instance());
  }

  public OnSubscribeToMultimap(Observable<T> paramObservable, Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11, Func0<? extends Map<K, Collection<V>>> paramFunc0, Func1<? super K, ? extends Collection<V>> paramFunc12)
  {
    this.source = paramObservable;
    this.keySelector = paramFunc1;
    this.valueSelector = paramFunc11;
    if (paramFunc0 == null);
    for (this.mapFactory = this; ; this.mapFactory = paramFunc0)
    {
      this.collectionFactory = paramFunc12;
      return;
    }
  }

  public Map<K, Collection<V>> call()
  {
    return new HashMap();
  }

  public void call(Subscriber<? super Map<K, Collection<V>>> paramSubscriber)
  {
    try
    {
      Map localMap = (Map)this.mapFactory.call();
      new ToMultimapSubscriber(paramSubscriber, localMap, this.keySelector, this.valueSelector, this.collectionFactory).subscribeTo(this.source);
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwIfFatal(localThrowable);
      paramSubscriber.onError(localThrowable);
    }
  }

  private static final class DefaultMultimapCollectionFactory<K, V>
    implements Func1<K, Collection<V>>
  {
    private static final DefaultMultimapCollectionFactory<Object, Object> INSTANCE = new DefaultMultimapCollectionFactory();

    static <K, V> DefaultMultimapCollectionFactory<K, V> instance()
    {
      return INSTANCE;
    }

    public Collection<V> call(K paramK)
    {
      return new ArrayList();
    }
  }

  private static final class ToMultimapSubscriber<T, K, V> extends DeferredScalarSubscriberSafe<T, Map<K, Collection<V>>>
  {
    private final Func1<? super K, ? extends Collection<V>> collectionFactory;
    private final Func1<? super T, ? extends K> keySelector;
    private final Func1<? super T, ? extends V> valueSelector;

    ToMultimapSubscriber(Subscriber<? super Map<K, Collection<V>>> paramSubscriber, Map<K, Collection<V>> paramMap, Func1<? super T, ? extends K> paramFunc1, Func1<? super T, ? extends V> paramFunc11, Func1<? super K, ? extends Collection<V>> paramFunc12)
    {
      super();
      this.value = paramMap;
      this.hasValue = true;
      this.keySelector = paramFunc1;
      this.valueSelector = paramFunc11;
      this.collectionFactory = paramFunc12;
    }

    public void onNext(T paramT)
    {
      if (this.done)
        return;
      try
      {
        Object localObject1 = this.keySelector.call(paramT);
        Object localObject2 = this.valueSelector.call(paramT);
        Collection localCollection = (Collection)((Map)this.value).get(localObject1);
        if (localCollection == null)
        {
          localCollection = (Collection)this.collectionFactory.call(localObject1);
          ((Map)this.value).put(localObject1, localCollection);
        }
        localCollection.add(localObject2);
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
 * Qualified Name:     rx.internal.operators.OnSubscribeToMultimap
 * JD-Core Version:    0.6.0
 */