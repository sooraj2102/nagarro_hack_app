package rx.internal.operators;

import java.util.NoSuchElementException;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.internal.producers.SingleProducer;
import rx.plugins.RxJavaHooks;

public final class OperatorSingle<T>
  implements Observable.Operator<T, T>
{
  private final T defaultValue;
  private final boolean hasDefaultValue;

  OperatorSingle()
  {
    this(false, null);
  }

  public OperatorSingle(T paramT)
  {
    this(true, paramT);
  }

  private OperatorSingle(boolean paramBoolean, T paramT)
  {
    this.hasDefaultValue = paramBoolean;
    this.defaultValue = paramT;
  }

  public static <T> OperatorSingle<T> instance()
  {
    return Holder.INSTANCE;
  }

  public Subscriber<? super T> call(Subscriber<? super T> paramSubscriber)
  {
    ParentSubscriber localParentSubscriber = new ParentSubscriber(paramSubscriber, this.hasDefaultValue, this.defaultValue);
    paramSubscriber.add(localParentSubscriber);
    return localParentSubscriber;
  }

  static final class Holder
  {
    static final OperatorSingle<?> INSTANCE = new OperatorSingle();
  }

  static final class ParentSubscriber<T> extends Subscriber<T>
  {
    private final Subscriber<? super T> child;
    private final T defaultValue;
    private final boolean hasDefaultValue;
    private boolean hasTooManyElements;
    private boolean isNonEmpty;
    private T value;

    ParentSubscriber(Subscriber<? super T> paramSubscriber, boolean paramBoolean, T paramT)
    {
      this.child = paramSubscriber;
      this.hasDefaultValue = paramBoolean;
      this.defaultValue = paramT;
      request(2L);
    }

    public void onCompleted()
    {
      if (!this.hasTooManyElements)
      {
        if (this.isNonEmpty)
          this.child.setProducer(new SingleProducer(this.child, this.value));
      }
      else
        return;
      if (this.hasDefaultValue)
      {
        this.child.setProducer(new SingleProducer(this.child, this.defaultValue));
        return;
      }
      this.child.onError(new NoSuchElementException("Sequence contains no elements"));
    }

    public void onError(Throwable paramThrowable)
    {
      if (this.hasTooManyElements)
      {
        RxJavaHooks.onError(paramThrowable);
        return;
      }
      this.child.onError(paramThrowable);
    }

    public void onNext(T paramT)
    {
      if (!this.hasTooManyElements)
      {
        if (this.isNonEmpty)
        {
          this.hasTooManyElements = true;
          this.child.onError(new IllegalArgumentException("Sequence contains too many elements"));
          unsubscribe();
        }
      }
      else
        return;
      this.value = paramT;
      this.isNonEmpty = true;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.OperatorSingle
 * JD-Core Version:    0.6.0
 */