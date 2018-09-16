package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;

@RequiresApi(14)
abstract interface ViewGroupUtilsImpl
{
  public abstract ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup paramViewGroup);

  public abstract void suppressLayout(@NonNull ViewGroup paramViewGroup, boolean paramBoolean);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewGroupUtilsImpl
 * JD-Core Version:    0.6.0
 */