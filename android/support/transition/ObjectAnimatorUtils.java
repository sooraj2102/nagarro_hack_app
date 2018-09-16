package android.support.transition;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build.VERSION;
import android.util.Property;

class ObjectAnimatorUtils
{
  private static final ObjectAnimatorUtilsImpl IMPL;

  static
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new ObjectAnimatorUtilsApi21();
      return;
    }
    IMPL = new ObjectAnimatorUtilsApi14();
  }

  static <T> ObjectAnimator ofPointF(T paramT, Property<T, PointF> paramProperty, Path paramPath)
  {
    return IMPL.ofPointF(paramT, paramProperty, paramPath);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ObjectAnimatorUtils
 * JD-Core Version:    0.6.0
 */