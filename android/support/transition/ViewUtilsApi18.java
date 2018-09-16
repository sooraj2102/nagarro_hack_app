package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;

@RequiresApi(18)
class ViewUtilsApi18 extends ViewUtilsApi14
{
  public ViewOverlayImpl getOverlay(@NonNull View paramView)
  {
    return new ViewOverlayApi18(paramView);
  }

  public WindowIdImpl getWindowId(@NonNull View paramView)
  {
    return new WindowIdApi18(paramView);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewUtilsApi18
 * JD-Core Version:    0.6.0
 */