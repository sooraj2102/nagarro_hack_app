package rx.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;

public final class BlockingOperatorMostRecent
{
  private BlockingOperatorMostRecent()
  {
    throw new IllegalStateException("No instances!");
  }

  public static <T> Iterable<T> mostRecent(Observable<? extends T> paramObservable, T paramT)
  {
    return new Iterable(paramT, paramObservable)
    {
      public Iterator<T> iterator()
      {
        BlockingOperatorMostRecent.MostRecentObserver localMostRecentObserver = new BlockingOperatorMostRecent.MostRecentObserver(this.val$initialValue);
        this.val$source.subscribe(localMostRecentObserver);
        return localMostRecentObserver.getIterable();
      }
    };
  }

  static final class MostRecentObserver<T> extends Subscriber<T>
  {
    final NotificationLite<T> nl = NotificationLite.instance();
    volatile Object value = this.nl.next(paramT);

    MostRecentObserver(T paramT)
    {
    }

    public Iterator<T> getIterable()
    {
      return new Iterator()
      {
        private Object buf;

        public boolean hasNext()
        {
          this.buf = BlockingOperatorMostRecent.MostRecentObserver.this.value;
          return !BlockingOperatorMostRecent.MostRecentObserver.this.nl.isCompleted(this.buf);
        }

        public T next()
        {
          try
          {
            if (this.buf == null)
              this.buf = BlockingOperatorMostRecent.MostRecentObserver.this.value;
            if (BlockingOperatorMostRecent.MostRecentObserver.this.nl.isCompleted(this.buf))
              throw new NoSuchElementException();
          }
          finally
          {
            this.buf = null;
          }
          if (BlockingOperatorMostRecent.MostRecentObserver.this.nl.isError(this.buf))
            throw Exceptions.propagate(BlockingOperatorMostRecent.MostRecentObserver.this.nl.getError(this.buf));
          Object localObject2 = BlockingOperatorMostRecent.MostRecentObserver.this.nl.getValue(this.buf);
          this.buf = null;
          return localObject2;
        }

        public void remove()
        {
          throw new UnsupportedOperationException("Read only iterator");
        }
      };
    }

    public void onCompleted()
    {
      this.value = this.nl.completed();
    }

    public void onError(Throwable paramThrowable)
    {
      this.value = this.nl.error(paramThrowable);
    }

    public void onNext(T paramT)
    {
      this.value = this.nl.next(paramT);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.operators.BlockingOperatorMostRecent
 * JD-Core Version:    0.6.0
 */