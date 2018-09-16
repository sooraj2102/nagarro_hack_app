package android.support.v4.widget;

import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class PopupWindowCompat
{
  static final PopupWindowCompatBaseImpl IMPL;

  static
  {
    if (Build.VERSION.SDK_INT >= 23)
    {
      IMPL = new PopupWindowCompatApi23Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new PopupWindowCompatApi21Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new PopupWindowCompatApi19Impl();
      return;
    }
    IMPL = new PopupWindowCompatBaseImpl();
  }

  public static boolean getOverlapAnchor(PopupWindow paramPopupWindow)
  {
    return IMPL.getOverlapAnchor(paramPopupWindow);
  }

  public static int getWindowLayoutType(PopupWindow paramPopupWindow)
  {
    return IMPL.getWindowLayoutType(paramPopupWindow);
  }

  public static void setOverlapAnchor(PopupWindow paramPopupWindow, boolean paramBoolean)
  {
    IMPL.setOverlapAnchor(paramPopupWindow, paramBoolean);
  }

  public static void setWindowLayoutType(PopupWindow paramPopupWindow, int paramInt)
  {
    IMPL.setWindowLayoutType(paramPopupWindow, paramInt);
  }

  public static void showAsDropDown(PopupWindow paramPopupWindow, View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    IMPL.showAsDropDown(paramPopupWindow, paramView, paramInt1, paramInt2, paramInt3);
  }

  @RequiresApi(19)
  static class PopupWindowCompatApi19Impl extends PopupWindowCompat.PopupWindowCompatBaseImpl
  {
    public void showAsDropDown(PopupWindow paramPopupWindow, View paramView, int paramInt1, int paramInt2, int paramInt3)
    {
      paramPopupWindow.showAsDropDown(paramView, paramInt1, paramInt2, paramInt3);
    }
  }

  @RequiresApi(21)
  static class PopupWindowCompatApi21Impl extends PopupWindowCompat.PopupWindowCompatApi19Impl
  {
    private static final String TAG = "PopupWindowCompatApi21";
    private static Field sOverlapAnchorField;

    static
    {
      try
      {
        sOverlapAnchorField = PopupWindow.class.getDeclaredField("mOverlapAnchor");
        sOverlapAnchorField.setAccessible(true);
        return;
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        Log.i("PopupWindowCompatApi21", "Could not fetch mOverlapAnchor field from PopupWindow", localNoSuchFieldException);
      }
    }

    public boolean getOverlapAnchor(PopupWindow paramPopupWindow)
    {
      if (sOverlapAnchorField != null)
        try
        {
          boolean bool = ((Boolean)sOverlapAnchorField.get(paramPopupWindow)).booleanValue();
          return bool;
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          Log.i("PopupWindowCompatApi21", "Could not get overlap anchor field in PopupWindow", localIllegalAccessException);
        }
      return false;
    }

    public void setOverlapAnchor(PopupWindow paramPopupWindow, boolean paramBoolean)
    {
      if (sOverlapAnchorField != null);
      try
      {
        sOverlapAnchorField.set(paramPopupWindow, Boolean.valueOf(paramBoolean));
        return;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Log.i("PopupWindowCompatApi21", "Could not set overlap anchor field in PopupWindow", localIllegalAccessException);
      }
    }
  }

  @RequiresApi(23)
  static class PopupWindowCompatApi23Impl extends PopupWindowCompat.PopupWindowCompatApi21Impl
  {
    public boolean getOverlapAnchor(PopupWindow paramPopupWindow)
    {
      return paramPopupWindow.getOverlapAnchor();
    }

    public int getWindowLayoutType(PopupWindow paramPopupWindow)
    {
      return paramPopupWindow.getWindowLayoutType();
    }

    public void setOverlapAnchor(PopupWindow paramPopupWindow, boolean paramBoolean)
    {
      paramPopupWindow.setOverlapAnchor(paramBoolean);
    }

    public void setWindowLayoutType(PopupWindow paramPopupWindow, int paramInt)
    {
      paramPopupWindow.setWindowLayoutType(paramInt);
    }
  }

  static class PopupWindowCompatBaseImpl
  {
    private static Method sGetWindowLayoutTypeMethod;
    private static boolean sGetWindowLayoutTypeMethodAttempted;
    private static Method sSetWindowLayoutTypeMethod;
    private static boolean sSetWindowLayoutTypeMethodAttempted;

    public boolean getOverlapAnchor(PopupWindow paramPopupWindow)
    {
      return false;
    }

    public int getWindowLayoutType(PopupWindow paramPopupWindow)
    {
      if (!sGetWindowLayoutTypeMethodAttempted);
      try
      {
        sGetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("getWindowLayoutType", new Class[0]);
        sGetWindowLayoutTypeMethod.setAccessible(true);
        label27: sGetWindowLayoutTypeMethodAttempted = true;
        if (sGetWindowLayoutTypeMethod != null)
          try
          {
            int i = ((Integer)sGetWindowLayoutTypeMethod.invoke(paramPopupWindow, new Object[0])).intValue();
            return i;
          }
          catch (Exception localException1)
          {
          }
        return 0;
      }
      catch (Exception localException2)
      {
        break label27;
      }
    }

    public void setOverlapAnchor(PopupWindow paramPopupWindow, boolean paramBoolean)
    {
    }

    public void setWindowLayoutType(PopupWindow paramPopupWindow, int paramInt)
    {
      if (!sSetWindowLayoutTypeMethodAttempted);
      try
      {
        Class[] arrayOfClass = new Class[1];
        arrayOfClass[0] = Integer.TYPE;
        sSetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("setWindowLayoutType", arrayOfClass);
        sSetWindowLayoutTypeMethod.setAccessible(true);
        label38: sSetWindowLayoutTypeMethodAttempted = true;
        if (sSetWindowLayoutTypeMethod != null);
        try
        {
          Method localMethod = sSetWindowLayoutTypeMethod;
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = Integer.valueOf(paramInt);
          localMethod.invoke(paramPopupWindow, arrayOfObject);
          return;
        }
        catch (Exception localException1)
        {
          return;
        }
      }
      catch (Exception localException2)
      {
        break label38;
      }
    }

    public void showAsDropDown(PopupWindow paramPopupWindow, View paramView, int paramInt1, int paramInt2, int paramInt3)
    {
      if ((0x7 & GravityCompat.getAbsoluteGravity(paramInt3, ViewCompat.getLayoutDirection(paramView))) == 5)
        paramInt1 -= paramPopupWindow.getWidth() - paramView.getWidth();
      paramPopupWindow.showAsDropDown(paramView, paramInt1, paramInt2);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.widget.PopupWindowCompat
 * JD-Core Version:    0.6.0
 */