package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;

@RequiresApi(14)
abstract interface ViewGroupOverlayImpl extends ViewOverlayImpl
{
  public abstract void add(@NonNull View paramView);

  public abstract void remove(@NonNull View paramView);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewGroupOverlayImpl
 * JD-Core Version:    0.6.0
 */