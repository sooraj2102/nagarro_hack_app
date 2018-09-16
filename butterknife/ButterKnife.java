package butterknife;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.Window;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ButterKnife
{

  @VisibleForTesting
  static final Map<Class<?>, Constructor<? extends Unbinder>> BINDINGS;
  private static final String TAG = "ButterKnife";
  private static boolean debug = false;

  static
  {
    BINDINGS = new LinkedHashMap();
  }

  private ButterKnife()
  {
    throw new AssertionError("No instances.");
  }

  @TargetApi(14)
  @RequiresApi(14)
  @UiThread
  public static <T extends View, V> void apply(@NonNull T paramT, @NonNull Property<? super T, V> paramProperty, V paramV)
  {
    paramProperty.set(paramT, paramV);
  }

  @UiThread
  public static <T extends View> void apply(@NonNull T paramT, @NonNull Action<? super T> paramAction)
  {
    paramAction.apply(paramT, 0);
  }

  @UiThread
  public static <T extends View, V> void apply(@NonNull T paramT, @NonNull Setter<? super T, V> paramSetter, V paramV)
  {
    paramSetter.set(paramT, paramV, 0);
  }

  @SafeVarargs
  @UiThread
  public static <T extends View> void apply(@NonNull T paramT, @NonNull Action<? super T>[] paramArrayOfAction)
  {
    int i = paramArrayOfAction.length;
    for (int j = 0; j < i; j++)
      paramArrayOfAction[j].apply(paramT, 0);
  }

  @TargetApi(14)
  @RequiresApi(14)
  @UiThread
  public static <T extends View, V> void apply(@NonNull List<T> paramList, @NonNull Property<? super T, V> paramProperty, V paramV)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      paramProperty.set(paramList.get(i), paramV);
      i++;
    }
  }

  @UiThread
  public static <T extends View> void apply(@NonNull List<T> paramList, @NonNull Action<? super T> paramAction)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      paramAction.apply((View)paramList.get(i), i);
      i++;
    }
  }

  @UiThread
  public static <T extends View, V> void apply(@NonNull List<T> paramList, @NonNull Setter<? super T, V> paramSetter, V paramV)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      paramSetter.set((View)paramList.get(i), paramV, i);
      i++;
    }
  }

  @SafeVarargs
  @UiThread
  public static <T extends View> void apply(@NonNull List<T> paramList, @NonNull Action<? super T>[] paramArrayOfAction)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      int k = paramArrayOfAction.length;
      for (int m = 0; m < k; m++)
        paramArrayOfAction[m].apply((View)paramList.get(i), i);
      i++;
    }
  }

  @TargetApi(14)
  @RequiresApi(14)
  @UiThread
  public static <T extends View, V> void apply(@NonNull T[] paramArrayOfT, @NonNull Property<? super T, V> paramProperty, V paramV)
  {
    int i = 0;
    int j = paramArrayOfT.length;
    while (i < j)
    {
      paramProperty.set(paramArrayOfT[i], paramV);
      i++;
    }
  }

  @UiThread
  public static <T extends View> void apply(@NonNull T[] paramArrayOfT, @NonNull Action<? super T> paramAction)
  {
    int i = 0;
    int j = paramArrayOfT.length;
    while (i < j)
    {
      paramAction.apply(paramArrayOfT[i], i);
      i++;
    }
  }

  @UiThread
  public static <T extends View, V> void apply(@NonNull T[] paramArrayOfT, @NonNull Setter<? super T, V> paramSetter, V paramV)
  {
    int i = 0;
    int j = paramArrayOfT.length;
    while (i < j)
    {
      paramSetter.set(paramArrayOfT[i], paramV, i);
      i++;
    }
  }

  @SafeVarargs
  @UiThread
  public static <T extends View> void apply(@NonNull T[] paramArrayOfT, @NonNull Action<? super T>[] paramArrayOfAction)
  {
    int i = 0;
    int j = paramArrayOfT.length;
    while (i < j)
    {
      int k = paramArrayOfAction.length;
      for (int m = 0; m < k; m++)
        paramArrayOfAction[m].apply(paramArrayOfT[i], i);
      i++;
    }
  }

  @NonNull
  @UiThread
  public static Unbinder bind(@NonNull Activity paramActivity)
  {
    return createBinding(paramActivity, paramActivity.getWindow().getDecorView());
  }

  @NonNull
  @UiThread
  public static Unbinder bind(@NonNull Dialog paramDialog)
  {
    return createBinding(paramDialog, paramDialog.getWindow().getDecorView());
  }

  @NonNull
  @UiThread
  public static Unbinder bind(@NonNull View paramView)
  {
    return createBinding(paramView, paramView);
  }

  @NonNull
  @UiThread
  public static Unbinder bind(@NonNull Object paramObject, @NonNull Activity paramActivity)
  {
    return createBinding(paramObject, paramActivity.getWindow().getDecorView());
  }

  @NonNull
  @UiThread
  public static Unbinder bind(@NonNull Object paramObject, @NonNull Dialog paramDialog)
  {
    return createBinding(paramObject, paramDialog.getWindow().getDecorView());
  }

  @NonNull
  @UiThread
  public static Unbinder bind(@NonNull Object paramObject, @NonNull View paramView)
  {
    return createBinding(paramObject, paramView);
  }

  private static Unbinder createBinding(@NonNull Object paramObject, @NonNull View paramView)
  {
    Class localClass = paramObject.getClass();
    if (debug)
      Log.d("ButterKnife", "Looking up binding for " + localClass.getName());
    Constructor localConstructor = findBindingConstructorForClass(localClass);
    if (localConstructor == null)
      return Unbinder.EMPTY;
    Throwable localThrowable;
    try
    {
      Unbinder localUnbinder = (Unbinder)localConstructor.newInstance(new Object[] { paramObject, paramView });
      return localUnbinder;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException("Unable to invoke " + localConstructor, localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new RuntimeException("Unable to invoke " + localConstructor, localInstantiationException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      localThrowable = localInvocationTargetException.getCause();
      if ((localThrowable instanceof RuntimeException))
        throw ((RuntimeException)localThrowable);
      if ((localThrowable instanceof Error))
        throw ((Error)localThrowable);
    }
    throw new RuntimeException("Unable to create binding instance.", localThrowable);
  }

  @CheckResult
  @Nullable
  @UiThread
  private static Constructor<? extends Unbinder> findBindingConstructorForClass(Class<?> paramClass)
  {
    Constructor localConstructor1 = (Constructor)BINDINGS.get(paramClass);
    if (localConstructor1 != null)
    {
      if (debug)
        Log.d("ButterKnife", "HIT: Cached in binding map.");
      return localConstructor1;
    }
    String str = paramClass.getName();
    if ((str.startsWith("android.")) || (str.startsWith("java.")))
    {
      if (debug)
        Log.d("ButterKnife", "MISS: Reached framework class. Abandoning search.");
      return null;
    }
    try
    {
      localConstructor2 = paramClass.getClassLoader().loadClass(str + "_ViewBinding").getConstructor(new Class[] { paramClass, View.class });
      if (debug)
        Log.d("ButterKnife", "HIT: Loaded binding class and constructor.");
      BINDINGS.put(paramClass, localConstructor2);
      return localConstructor2;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      while (true)
      {
        if (debug)
          Log.d("ButterKnife", "Not found. Trying superclass " + paramClass.getSuperclass().getName());
        Constructor localConstructor2 = findBindingConstructorForClass(paramClass.getSuperclass());
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
    }
    throw new RuntimeException("Unable to find binding constructor for " + str, localNoSuchMethodException);
  }

  @Deprecated
  @CheckResult
  public static <T extends View> T findById(@NonNull Activity paramActivity, @IdRes int paramInt)
  {
    return paramActivity.findViewById(paramInt);
  }

  @Deprecated
  @CheckResult
  public static <T extends View> T findById(@NonNull Dialog paramDialog, @IdRes int paramInt)
  {
    return paramDialog.findViewById(paramInt);
  }

  @Deprecated
  @CheckResult
  public static <T extends View> T findById(@NonNull View paramView, @IdRes int paramInt)
  {
    return paramView.findViewById(paramInt);
  }

  public static void setDebug(boolean paramBoolean)
  {
    debug = paramBoolean;
  }

  public static abstract interface Action<T extends View>
  {
    @UiThread
    public abstract void apply(@NonNull T paramT, int paramInt);
  }

  public static abstract interface Setter<T extends View, V>
  {
    @UiThread
    public abstract void set(@NonNull T paramT, V paramV, int paramInt);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     butterknife.ButterKnife
 * JD-Core Version:    0.6.0
 */