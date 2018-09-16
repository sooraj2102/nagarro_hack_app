package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(19)
class ViewUtilsApi19 extends ViewUtilsApi18
{
  private static final String TAG = "ViewUtilsApi19";
  private static Method sGetTransitionAlphaMethod;
  private static boolean sGetTransitionAlphaMethodFetched;
  private static Method sSetTransitionAlphaMethod;
  private static boolean sSetTransitionAlphaMethodFetched;

  private void fetchGetTransitionAlphaMethod()
  {
    if (!sGetTransitionAlphaMethodFetched);
    try
    {
      sGetTransitionAlphaMethod = View.class.getDeclaredMethod("getTransitionAlpha", new Class[0]);
      sGetTransitionAlphaMethod.setAccessible(true);
      sGetTransitionAlphaMethodFetched = true;
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      while (true)
        Log.i("ViewUtilsApi19", "Failed to retrieve getTransitionAlpha method", localNoSuchMethodException);
    }
  }

  private void fetchSetTransitionAlphaMethod()
  {
    if (!sSetTransitionAlphaMethodFetched);
    try
    {
      Class[] arrayOfClass = new Class[1];
      arrayOfClass[0] = Float.TYPE;
      sSetTransitionAlphaMethod = View.class.getDeclaredMethod("setTransitionAlpha", arrayOfClass);
      sSetTransitionAlphaMethod.setAccessible(true);
      sSetTransitionAlphaMethodFetched = true;
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      while (true)
        Log.i("ViewUtilsApi19", "Failed to retrieve setTransitionAlpha method", localNoSuchMethodException);
    }
  }

  public void clearNonTransitionAlpha(@NonNull View paramView)
  {
  }

  public float getTransitionAlpha(@NonNull View paramView)
  {
    fetchGetTransitionAlphaMethod();
    if (sGetTransitionAlphaMethod != null)
      try
      {
        float f = ((Float)sGetTransitionAlphaMethod.invoke(paramView, new Object[0])).floatValue();
        return f;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new RuntimeException(localInvocationTargetException.getCause());
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
      }
    return super.getTransitionAlpha(paramView);
  }

  public void saveNonTransitionAlpha(@NonNull View paramView)
  {
  }

  public void setTransitionAlpha(@NonNull View paramView, float paramFloat)
  {
    fetchSetTransitionAlphaMethod();
    if (sSetTransitionAlphaMethod != null);
    try
    {
      Method localMethod = sSetTransitionAlphaMethod;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Float.valueOf(paramFloat);
      localMethod.invoke(paramView, arrayOfObject);
      return;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new RuntimeException(localInvocationTargetException.getCause());
      paramView.setAlpha(paramFloat);
      return;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewUtilsApi19
 * JD-Core Version:    0.6.0
 */