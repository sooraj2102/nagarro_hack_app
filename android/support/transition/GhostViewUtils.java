package android.support.transition;

import android.graphics.Matrix;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;

class GhostViewUtils
{
  private static final GhostViewImpl.Creator CREATOR;

  static
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      CREATOR = new GhostViewApi21.Creator();
      return;
    }
    CREATOR = new GhostViewApi14.Creator();
  }

  static GhostViewImpl addGhost(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix)
  {
    return CREATOR.addGhost(paramView, paramViewGroup, paramMatrix);
  }

  static void removeGhost(View paramView)
  {
    CREATOR.removeGhost(paramView);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.GhostViewUtils
 * JD-Core Version:    0.6.0
 */