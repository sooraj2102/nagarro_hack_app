package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Matrix;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

@RequiresApi(14)
class ImageViewUtilsApi14
  implements ImageViewUtilsImpl
{
  public void animateTransform(ImageView paramImageView, Matrix paramMatrix)
  {
    paramImageView.setImageMatrix(paramMatrix);
  }

  public void reserveEndAnimateTransform(ImageView paramImageView, Animator paramAnimator)
  {
    paramAnimator.addListener(new AnimatorListenerAdapter(paramImageView)
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        ImageView.ScaleType localScaleType = (ImageView.ScaleType)this.val$view.getTag(R.id.save_scale_type);
        this.val$view.setScaleType(localScaleType);
        this.val$view.setTag(R.id.save_scale_type, null);
        if (localScaleType == ImageView.ScaleType.MATRIX)
        {
          this.val$view.setImageMatrix((Matrix)this.val$view.getTag(R.id.save_image_matrix));
          this.val$view.setTag(R.id.save_image_matrix, null);
        }
        paramAnimator.removeListener(this);
      }
    });
  }

  public void startAnimateTransform(ImageView paramImageView)
  {
    ImageView.ScaleType localScaleType = paramImageView.getScaleType();
    paramImageView.setTag(R.id.save_scale_type, localScaleType);
    if (localScaleType == ImageView.ScaleType.MATRIX)
      paramImageView.setTag(R.id.save_image_matrix, paramImageView.getImageMatrix());
    while (true)
    {
      paramImageView.setImageMatrix(MatrixUtils.IDENTITY_MATRIX);
      return;
      paramImageView.setScaleType(ImageView.ScaleType.MATRIX);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ImageViewUtilsApi14
 * JD-Core Version:    0.6.0
 */