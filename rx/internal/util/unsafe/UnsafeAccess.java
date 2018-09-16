package rx.internal.util.unsafe;

import java.lang.reflect.Field;
import rx.internal.util.SuppressAnimalSniffer;
import sun.misc.Unsafe;

@SuppressAnimalSniffer
public final class UnsafeAccess
{
  private static final boolean DISABLED_BY_USER;
  public static final Unsafe UNSAFE;

  static
  {
    boolean bool = true;
    if (System.getProperty("rx.unsafe-disable") != null);
    while (true)
    {
      DISABLED_BY_USER = bool;
      try
      {
        Field localField = Unsafe.class.getDeclaredField("theUnsafe");
        localField.setAccessible(true);
        localUnsafe = (Unsafe)localField.get(null);
        UNSAFE = localUnsafe;
        return;
        bool = false;
      }
      catch (Throwable localThrowable)
      {
        while (true)
          Unsafe localUnsafe = null;
      }
    }
  }

  private UnsafeAccess()
  {
    throw new IllegalStateException("No instances!");
  }

  public static long addressOf(Class<?> paramClass, String paramString)
  {
    InternalError localInternalError;
    try
    {
      Field localField = paramClass.getDeclaredField(paramString);
      long l = UNSAFE.objectFieldOffset(localField);
      return l;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      localInternalError = new InternalError();
      localInternalError.initCause(localNoSuchFieldException);
    }
    throw localInternalError;
  }

  public static boolean compareAndSwapInt(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    return UNSAFE.compareAndSwapInt(paramObject, paramLong, paramInt1, paramInt2);
  }

  public static int getAndAddInt(Object paramObject, long paramLong, int paramInt)
  {
    int i;
    int j;
    do
    {
      i = UNSAFE.getIntVolatile(paramObject, paramLong);
      j = i + paramInt;
    }
    while (!UNSAFE.compareAndSwapInt(paramObject, paramLong, i, j));
    return i;
  }

  public static int getAndIncrementInt(Object paramObject, long paramLong)
  {
    int i;
    int j;
    do
    {
      i = UNSAFE.getIntVolatile(paramObject, paramLong);
      j = i + 1;
    }
    while (!UNSAFE.compareAndSwapInt(paramObject, paramLong, i, j));
    return i;
  }

  public static int getAndSetInt(Object paramObject, long paramLong, int paramInt)
  {
    int i;
    do
      i = UNSAFE.getIntVolatile(paramObject, paramLong);
    while (!UNSAFE.compareAndSwapInt(paramObject, paramLong, i, paramInt));
    return i;
  }

  public static boolean isUnsafeAvailable()
  {
    return (UNSAFE != null) && (!DISABLED_BY_USER);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.UnsafeAccess
 * JD-Core Version:    0.6.0
 */