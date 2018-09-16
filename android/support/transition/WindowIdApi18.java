package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowId;

@RequiresApi(18)
class WindowIdApi18
  implements WindowIdImpl
{
  private final WindowId mWindowId;

  WindowIdApi18(@NonNull View paramView)
  {
    this.mWindowId = paramView.getWindowId();
  }

  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof WindowIdApi18)) && (((WindowIdApi18)paramObject).mWindowId.equals(this.mWindowId));
  }

  public int hashCode()
  {
    return this.mWindowId.hashCode();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.WindowIdApi18
 * JD-Core Version:    0.6.0
 */