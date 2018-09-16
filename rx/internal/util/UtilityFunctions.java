package rx.internal.util;

import rx.functions.Func1;

public final class UtilityFunctions
{
  private UtilityFunctions()
  {
    throw new IllegalStateException("No instances!");
  }

  public static <T> Func1<? super T, Boolean> alwaysFalse()
  {
    return AlwaysFalse.INSTANCE;
  }

  public static <T> Func1<? super T, Boolean> alwaysTrue()
  {
    return AlwaysTrue.INSTANCE;
  }

  public static <T> Func1<T, T> identity()
  {
    return new Func1()
    {
      public T call(T paramT)
      {
        return paramT;
      }
    };
  }

  static enum AlwaysFalse
    implements Func1<Object, Boolean>
  {
    static
    {
      AlwaysFalse[] arrayOfAlwaysFalse = new AlwaysFalse[1];
      arrayOfAlwaysFalse[0] = INSTANCE;
      $VALUES = arrayOfAlwaysFalse;
    }

    public Boolean call(Object paramObject)
    {
      return Boolean.valueOf(false);
    }
  }

  static enum AlwaysTrue
    implements Func1<Object, Boolean>
  {
    static
    {
      AlwaysTrue[] arrayOfAlwaysTrue = new AlwaysTrue[1];
      arrayOfAlwaysTrue[0] = INSTANCE;
      $VALUES = arrayOfAlwaysTrue;
    }

    public Boolean call(Object paramObject)
    {
      return Boolean.valueOf(true);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.UtilityFunctions
 * JD-Core Version:    0.6.0
 */