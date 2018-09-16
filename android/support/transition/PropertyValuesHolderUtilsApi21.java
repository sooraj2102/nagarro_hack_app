package android.support.transition;

import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.RequiresApi;
import android.util.Property;

@RequiresApi(21)
class PropertyValuesHolderUtilsApi21
  implements PropertyValuesHolderUtilsImpl
{
  public PropertyValuesHolder ofPointF(Property<?, PointF> paramProperty, Path paramPath)
  {
    return PropertyValuesHolder.ofObject(paramProperty, null, paramPath);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.PropertyValuesHolderUtilsApi21
 * JD-Core Version:    0.6.0
 */