package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.os.Build.VERSION;
import android.widget.ImageView;

class ImageViewUtils
{
  private static final ImageViewUtilsImpl IMPL;

  static
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new ImageViewUtilsApi21();
      return;
    }
    IMPL = new ImageViewUtilsApi14();
  }

  static void animateTransform(ImageView paramImageView, Matrix paramMatrix)
  {
    IMPL.animateTransform(paramImageView, paramMatrix);
  }

  static void reserveEndAnimateTransform(ImageView paramImageView, Animator paramAnimator)
  {
    IMPL.reserveEndAnimateTransform(paramImageView, paramAnimator);
  }

  static void startAnimateTransform(ImageView paramImageView)
  {
    IMPL.startAnimateTransform(paramImageView);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ImageViewUtils
 * JD-Core Version:    0.6.0
 */