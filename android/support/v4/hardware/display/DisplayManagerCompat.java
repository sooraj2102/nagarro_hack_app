package android.support.v4.hardware.display;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.WindowManager;
import java.util.WeakHashMap;

public abstract class DisplayManagerCompat
{
  public static final String DISPLAY_CATEGORY_PRESENTATION = "android.hardware.display.category.PRESENTATION";
  private static final WeakHashMap<Context, DisplayManagerCompat> sInstances = new WeakHashMap();

  public static DisplayManagerCompat getInstance(Context paramContext)
  {
    synchronized (sInstances)
    {
      Object localObject2 = (DisplayManagerCompat)sInstances.get(paramContext);
      if (localObject2 == null)
      {
        if (Build.VERSION.SDK_INT >= 17)
        {
          localObject2 = new DisplayManagerCompatApi17Impl(paramContext);
          sInstances.put(paramContext, localObject2);
        }
      }
      else
        return localObject2;
      localObject2 = new DisplayManagerCompatApi14Impl(paramContext);
    }
  }

  public abstract Display getDisplay(int paramInt);

  public abstract Display[] getDisplays();

  public abstract Display[] getDisplays(String paramString);

  private static class DisplayManagerCompatApi14Impl extends DisplayManagerCompat
  {
    private final WindowManager mWindowManager;

    DisplayManagerCompatApi14Impl(Context paramContext)
    {
      this.mWindowManager = ((WindowManager)paramContext.getSystemService("window"));
    }

    public Display getDisplay(int paramInt)
    {
      Display localDisplay = this.mWindowManager.getDefaultDisplay();
      if (localDisplay.getDisplayId() == paramInt)
        return localDisplay;
      return null;
    }

    public Display[] getDisplays()
    {
      Display[] arrayOfDisplay = new Display[1];
      arrayOfDisplay[0] = this.mWindowManager.getDefaultDisplay();
      return arrayOfDisplay;
    }

    public Display[] getDisplays(String paramString)
    {
      if (paramString == null)
        return getDisplays();
      return new Display[0];
    }
  }

  @RequiresApi(17)
  private static class DisplayManagerCompatApi17Impl extends DisplayManagerCompat
  {
    private final DisplayManager mDisplayManager;

    DisplayManagerCompatApi17Impl(Context paramContext)
    {
      this.mDisplayManager = ((DisplayManager)paramContext.getSystemService("display"));
    }

    public Display getDisplay(int paramInt)
    {
      return this.mDisplayManager.getDisplay(paramInt);
    }

    public Display[] getDisplays()
    {
      return this.mDisplayManager.getDisplays();
    }

    public Display[] getDisplays(String paramString)
    {
      return this.mDisplayManager.getDisplays(paramString);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.hardware.display.DisplayManagerCompat
 * JD-Core Version:    0.6.0
 */