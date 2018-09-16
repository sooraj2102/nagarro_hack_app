package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
class GhostViewApi21
  implements GhostViewImpl
{
  private static final String TAG = "GhostViewApi21";
  private static Method sAddGhostMethod;
  private static boolean sAddGhostMethodFetched;
  private static Class<?> sGhostViewClass;
  private static boolean sGhostViewClassFetched;
  private static Method sRemoveGhostMethod;
  private static boolean sRemoveGhostMethodFetched;
  private final View mGhostView;

  private GhostViewApi21(@NonNull View paramView)
  {
    this.mGhostView = paramView;
  }

  private static void fetchAddGhostMethod()
  {
    if (!sAddGhostMethodFetched);
    try
    {
      fetchGhostViewClass();
      sAddGhostMethod = sGhostViewClass.getDeclaredMethod("addGhost", new Class[] { View.class, ViewGroup.class, Matrix.class });
      sAddGhostMethod.setAccessible(true);
      sAddGhostMethodFetched = true;
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      while (true)
        Log.i("GhostViewApi21", "Failed to retrieve addGhost method", localNoSuchMethodException);
    }
  }

  private static void fetchGhostViewClass()
  {
    if (!sGhostViewClassFetched);
    try
    {
      sGhostViewClass = Class.forName("android.view.GhostView");
      sGhostViewClassFetched = true;
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      while (true)
        Log.i("GhostViewApi21", "Failed to retrieve GhostView class", localClassNotFoundException);
    }
  }

  private static void fetchRemoveGhostMethod()
  {
    if (!sRemoveGhostMethodFetched);
    try
    {
      fetchGhostViewClass();
      sRemoveGhostMethod = sGhostViewClass.getDeclaredMethod("removeGhost", new Class[] { View.class });
      sRemoveGhostMethod.setAccessible(true);
      sRemoveGhostMethodFetched = true;
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      while (true)
        Log.i("GhostViewApi21", "Failed to retrieve removeGhost method", localNoSuchMethodException);
    }
  }

  public void reserveEndViewTransition(ViewGroup paramViewGroup, View paramView)
  {
  }

  public void setVisibility(int paramInt)
  {
    this.mGhostView.setVisibility(paramInt);
  }

  static class Creator
    implements GhostViewImpl.Creator
  {
    public GhostViewImpl addGhost(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix)
    {
      GhostViewApi21.access$000();
      if (GhostViewApi21.sAddGhostMethod != null)
        try
        {
          GhostViewApi21 localGhostViewApi21 = new GhostViewApi21((View)GhostViewApi21.sAddGhostMethod.invoke(null, new Object[] { paramView, paramViewGroup, paramMatrix }), null);
          return localGhostViewApi21;
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          throw new RuntimeException(localInvocationTargetException.getCause());
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
        }
      return null;
    }

    public void removeGhost(View paramView)
    {
      GhostViewApi21.access$300();
      if (GhostViewApi21.sRemoveGhostMethod != null);
      try
      {
        GhostViewApi21.sRemoveGhostMethod.invoke(null, new Object[] { paramView });
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
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.GhostViewApi21
 * JD-Core Version:    0.6.0
 */