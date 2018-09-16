package android.support.transition;

import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build.VERSION;
import android.util.Property;

class PropertyValuesHolderUtils
{
  private static final PropertyValuesHolderUtilsImpl IMPL;

  static
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new PropertyValuesHolderUtilsApi21();
      return;
    }
    IMPL = new PropertyValuesHolderUtilsApi14();
  }

  static PropertyValuesHolder ofPointF(Property<?, PointF> paramProperty, Path paramPath)
  {
    return IMPL.ofPointF(paramProperty, paramPath);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.PropertyValuesHolderUtils
 * JD-Core Version:    0.6.0
 */