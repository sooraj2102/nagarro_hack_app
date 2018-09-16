package android.support.transition;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;

abstract interface ObjectAnimatorUtilsImpl
{
  public abstract <T> ObjectAnimator ofPointF(T paramT, Property<T, PointF> paramProperty, Path paramPath);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ObjectAnimatorUtilsImpl
 * JD-Core Version:    0.6.0
 */