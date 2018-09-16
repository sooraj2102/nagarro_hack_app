package butterknife;

import android.support.annotation.UiThread;

public abstract interface Unbinder
{
  public static final Unbinder EMPTY = new Unbinder()
  {
    public void unbind()
    {
    }
  };

  @UiThread
  public abstract void unbind();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     butterknife.Unbinder
 * JD-Core Version:    0.6.0
 */