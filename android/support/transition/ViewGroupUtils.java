package android.support.transition;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

class ViewGroupUtils
{
  private static final ViewGroupUtilsImpl IMPL;

  static
  {
    if (Build.VERSION.SDK_INT >= 18)
    {
      IMPL = new ViewGroupUtilsApi18();
      return;
    }
    IMPL = new ViewGroupUtilsApi14();
  }

  static ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup paramViewGroup)
  {
    return IMPL.getOverlay(paramViewGroup);
  }

  static void suppressLayout(@NonNull ViewGroup paramViewGroup, boolean paramBoolean)
  {
    IMPL.suppressLayout(paramViewGroup, paramBoolean);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewGroupUtils
 * JD-Core Version:    0.6.0
 */