package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;

@RequiresApi(14)
abstract interface GhostViewImpl
{
  public abstract void reserveEndViewTransition(ViewGroup paramViewGroup, View paramView);

  public abstract void setVisibility(int paramInt);

  public static abstract interface Creator
  {
    public abstract GhostViewImpl addGhost(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix);

    public abstract void removeGhost(View paramView);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.GhostViewImpl
 * JD-Core Version:    0.6.0
 */