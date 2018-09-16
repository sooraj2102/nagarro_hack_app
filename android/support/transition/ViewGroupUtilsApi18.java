package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(18)
class ViewGroupUtilsApi18 extends ViewGroupUtilsApi14
{
  private static final String TAG = "ViewUtilsApi18";
  private static Method sSuppressLayoutMethod;
  private static boolean sSuppressLayoutMethodFetched;

  private void fetchSuppressLayoutMethod()
  {
    if (!sSuppressLayoutMethodFetched);
    try
    {
      Class[] arrayOfClass = new Class[1];
      arrayOfClass[0] = Boolean.TYPE;
      sSuppressLayoutMethod = ViewGroup.class.getDeclaredMethod("suppressLayout", arrayOfClass);
      sSuppressLayoutMethod.setAccessible(true);
      sSuppressLayoutMethodFetched = true;
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      while (true)
        Log.i("ViewUtilsApi18", "Failed to retrieve suppressLayout method", localNoSuchMethodException);
    }
  }

  public ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup paramViewGroup)
  {
    return new ViewGroupOverlayApi18(paramViewGroup);
  }

  public void suppressLayout(@NonNull ViewGroup paramViewGroup, boolean paramBoolean)
  {
    fetchSuppressLayoutMethod();
    if (sSuppressLayoutMethod != null);
    try
    {
      Method localMethod = sSuppressLayoutMethod;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Boolean.valueOf(paramBoolean);
      localMethod.invoke(paramViewGroup, arrayOfObject);
      return;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Log.i("ViewUtilsApi18", "Failed to invoke suppressLayout method", localIllegalAccessException);
      return;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Log.i("ViewUtilsApi18", "Error invoking suppressLayout method", localInvocationTargetException);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewGroupUtilsApi18
 * JD-Core Version:    0.6.0
 */