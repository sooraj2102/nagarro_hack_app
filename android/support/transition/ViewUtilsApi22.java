package android.support.transition;

import android.annotation.SuppressLint;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(22)
class ViewUtilsApi22 extends ViewUtilsApi21
{
  private static final String TAG = "ViewUtilsApi22";
  private static Method sSetLeftTopRightBottomMethod;
  private static boolean sSetLeftTopRightBottomMethodFetched;

  @SuppressLint({"PrivateApi"})
  private void fetchSetLeftTopRightBottomMethod()
  {
    if (!sSetLeftTopRightBottomMethodFetched);
    try
    {
      Class[] arrayOfClass = new Class[4];
      arrayOfClass[0] = Integer.TYPE;
      arrayOfClass[1] = Integer.TYPE;
      arrayOfClass[2] = Integer.TYPE;
      arrayOfClass[3] = Integer.TYPE;
      sSetLeftTopRightBottomMethod = View.class.getDeclaredMethod("setLeftTopRightBottom", arrayOfClass);
      sSetLeftTopRightBottomMethod.setAccessible(true);
      sSetLeftTopRightBottomMethodFetched = true;
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      while (true)
        Log.i("ViewUtilsApi22", "Failed to retrieve setLeftTopRightBottom method", localNoSuchMethodException);
    }
  }

  public void setLeftTopRightBottom(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    fetchSetLeftTopRightBottomMethod();
    if (sSetLeftTopRightBottomMethod != null);
    try
    {
      Method localMethod = sSetLeftTopRightBottomMethod;
      Object[] arrayOfObject = new Object[4];
      arrayOfObject[0] = Integer.valueOf(paramInt1);
      arrayOfObject[1] = Integer.valueOf(paramInt2);
      arrayOfObject[2] = Integer.valueOf(paramInt3);
      arrayOfObject[3] = Integer.valueOf(paramInt4);
      localMethod.invoke(paramView, arrayOfObject);
      return;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new RuntimeException(localInvocationTargetException.getCause());
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewUtilsApi22
 * JD-Core Version:    0.6.0
 */