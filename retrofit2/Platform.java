package retrofit2;

import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

class Platform
{
  private static final Platform PLATFORM = findPlatform();

  private static Platform findPlatform()
  {
    try
    {
      Class.forName("android.os.Build");
      if (Build.VERSION.SDK_INT != 0)
      {
        Android localAndroid = new Android();
        return localAndroid;
      }
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      try
      {
        Class.forName("java.util.Optional");
        Java8 localJava8 = new Java8();
        return localJava8;
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
      }
    }
    return new Platform();
  }

  static Platform get()
  {
    return PLATFORM;
  }

  CallAdapter.Factory defaultCallAdapterFactory(Executor paramExecutor)
  {
    if (paramExecutor != null)
      return new ExecutorCallAdapterFactory(paramExecutor);
    return DefaultCallAdapterFactory.INSTANCE;
  }

  Executor defaultCallbackExecutor()
  {
    return null;
  }

  Object invokeDefaultMethod(Method paramMethod, Class<?> paramClass, Object paramObject, Object[] paramArrayOfObject)
    throws Throwable
  {
    throw new UnsupportedOperationException();
  }

  boolean isDefaultMethod(Method paramMethod)
  {
    return false;
  }

  static class Android extends Platform
  {
    CallAdapter.Factory defaultCallAdapterFactory(Executor paramExecutor)
    {
      return new ExecutorCallAdapterFactory(paramExecutor);
    }

    public Executor defaultCallbackExecutor()
    {
      return new MainThreadExecutor();
    }

    static class MainThreadExecutor
      implements Executor
    {
      private final Handler handler = new Handler(Looper.getMainLooper());

      public void execute(Runnable paramRunnable)
      {
        this.handler.post(paramRunnable);
      }
    }
  }

  @IgnoreJRERequirement
  static class Java8 extends Platform
  {
    Object invokeDefaultMethod(Method paramMethod, Class<?> paramClass, Object paramObject, Object[] paramArrayOfObject)
      throws Throwable
    {
      Class[] arrayOfClass = new Class[2];
      arrayOfClass[0] = Class.class;
      arrayOfClass[1] = Integer.TYPE;
      Constructor localConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(arrayOfClass);
      localConstructor.setAccessible(true);
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramClass;
      arrayOfObject[1] = Integer.valueOf(-1);
      return ((MethodHandles.Lookup)localConstructor.newInstance(arrayOfObject)).unreflectSpecial(paramMethod, paramClass).bindTo(paramObject).invokeWithArguments(paramArrayOfObject);
    }

    boolean isDefaultMethod(Method paramMethod)
    {
      return paramMethod.isDefault();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.Platform
 * JD-Core Version:    0.6.0
 */