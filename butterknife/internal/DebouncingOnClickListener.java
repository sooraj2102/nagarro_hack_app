package butterknife.internal;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class DebouncingOnClickListener
  implements View.OnClickListener
{
  private static final Runnable ENABLE_AGAIN;
  static boolean enabled = true;

  static
  {
    ENABLE_AGAIN = new Runnable()
    {
      public void run()
      {
        DebouncingOnClickListener.enabled = true;
      }
    };
  }

  public abstract void doClick(View paramView);

  public final void onClick(View paramView)
  {
    if (enabled)
    {
      enabled = false;
      paramView.post(ENABLE_AGAIN);
      doClick(paramView);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     butterknife.internal.DebouncingOnClickListener
 * JD-Core Version:    0.6.0
 */