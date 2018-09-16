package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ImageView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
class ImageViewUtilsApi21
  implements ImageViewUtilsImpl
{
  private static final String TAG = "ImageViewUtilsApi21";
  private static Method sAnimateTransformMethod;
  private static boolean sAnimateTransformMethodFetched;

  private void fetchAnimateTransformMethod()
  {
    if (!sAnimateTransformMethodFetched);
    try
    {
      sAnimateTransformMethod = ImageView.class.getDeclaredMethod("animateTransform", new Class[] { Matrix.class });
      sAnimateTransformMethod.setAccessible(true);
      sAnimateTransformMethodFetched = true;
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      while (true)
        Log.i("ImageViewUtilsApi21", "Failed to retrieve animateTransform method", localNoSuchMethodException);
    }
  }

  public void animateTransform(ImageView paramImageView, Matrix paramMatrix)
  {
    fetchAnimateTransformMethod();
    if (sAnimateTransformMethod != null);
    try
    {
      sAnimateTransformMethod.invoke(paramImageView, new Object[] { paramMatrix });
      return;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new RuntimeException(localInvocationTargetException.getCause());
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
    }
  }

  public void reserveEndAnimateTransform(ImageView paramImageView, Animator paramAnimator)
  {
  }

  public void startAnimateTransform(ImageView paramImageView)
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ImageViewUtilsApi21
 * JD-Core Version:    0.6.0
 */