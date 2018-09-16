package dubeyanurag.com.github.healthyrestaurant.utils;

import android.content.Context;
import android.util.Property;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class AnimUtils
{
  private static Interpolator fastOutLinearIn;
  private static Interpolator fastOutSlowIn;
  private static Interpolator linearOutSlowIn;

  public static Interpolator getFastOutLinearInInterpolator(Context paramContext)
  {
    if (fastOutLinearIn == null)
      fastOutLinearIn = AnimationUtils.loadInterpolator(paramContext, 17563663);
    return fastOutLinearIn;
  }

  public static Interpolator getFastOutSlowInInterpolator(Context paramContext)
  {
    if (fastOutSlowIn == null)
      fastOutSlowIn = AnimationUtils.loadInterpolator(paramContext, 17563661);
    return fastOutSlowIn;
  }

  public static Interpolator getLinearOutSlowInInterpolator(Context paramContext)
  {
    if (linearOutSlowIn == null)
      linearOutSlowIn = AnimationUtils.loadInterpolator(paramContext, 17563662);
    return linearOutSlowIn;
  }

  public static abstract class IntProperty<T> extends Property<T, Integer>
  {
    public IntProperty(String paramString)
    {
      super(paramString);
    }

    public final void set(T paramT, Integer paramInteger)
    {
      setValue(paramT, paramInteger.intValue());
    }

    public abstract void setValue(T paramT, int paramInt);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.utils.AnimUtils
 * JD-Core Version:    0.6.0
 */