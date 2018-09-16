package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

class TransitionUtils
{
  private static final int MAX_IMAGE_SIZE = 1048576;

  static View copyViewImage(ViewGroup paramViewGroup, View paramView1, View paramView2)
  {
    Matrix localMatrix = new Matrix();
    localMatrix.setTranslate(-paramView2.getScrollX(), -paramView2.getScrollY());
    ViewUtils.transformMatrixToGlobal(paramView1, localMatrix);
    ViewUtils.transformMatrixToLocal(paramViewGroup, localMatrix);
    RectF localRectF = new RectF(0.0F, 0.0F, paramView1.getWidth(), paramView1.getHeight());
    localMatrix.mapRect(localRectF);
    int i = Math.round(localRectF.left);
    int j = Math.round(localRectF.top);
    int k = Math.round(localRectF.right);
    int m = Math.round(localRectF.bottom);
    ImageView localImageView = new ImageView(paramView1.getContext());
    localImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    Bitmap localBitmap = createViewBitmap(paramView1, localMatrix, localRectF);
    if (localBitmap != null)
      localImageView.setImageBitmap(localBitmap);
    localImageView.measure(View.MeasureSpec.makeMeasureSpec(k - i, 1073741824), View.MeasureSpec.makeMeasureSpec(m - j, 1073741824));
    localImageView.layout(i, j, k, m);
    return localImageView;
  }

  private static Bitmap createViewBitmap(View paramView, Matrix paramMatrix, RectF paramRectF)
  {
    int i = Math.round(paramRectF.width());
    int j = Math.round(paramRectF.height());
    Bitmap localBitmap = null;
    if (i > 0)
    {
      localBitmap = null;
      if (j > 0)
      {
        float f = Math.min(1.0F, 1048576.0F / (i * j));
        int k = (int)(f * i);
        int m = (int)(f * j);
        paramMatrix.postTranslate(-paramRectF.left, -paramRectF.top);
        paramMatrix.postScale(f, f);
        localBitmap = Bitmap.createBitmap(k, m, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        localCanvas.concat(paramMatrix);
        paramView.draw(localCanvas);
      }
    }
    return localBitmap;
  }

  static Animator mergeAnimators(Animator paramAnimator1, Animator paramAnimator2)
  {
    if (paramAnimator1 == null)
      return paramAnimator2;
    if (paramAnimator2 == null)
      return paramAnimator1;
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { paramAnimator1, paramAnimator2 });
    return localAnimatorSet;
  }

  static class MatrixEvaluator
    implements TypeEvaluator<Matrix>
  {
    final float[] mTempEndValues = new float[9];
    final Matrix mTempMatrix = new Matrix();
    final float[] mTempStartValues = new float[9];

    public Matrix evaluate(float paramFloat, Matrix paramMatrix1, Matrix paramMatrix2)
    {
      paramMatrix1.getValues(this.mTempStartValues);
      paramMatrix2.getValues(this.mTempEndValues);
      for (int i = 0; i < 9; i++)
      {
        float f = this.mTempEndValues[i] - this.mTempStartValues[i];
        this.mTempEndValues[i] = (this.mTempStartValues[i] + paramFloat * f);
      }
      this.mTempMatrix.setValues(this.mTempEndValues);
      return this.mTempMatrix;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.TransitionUtils
 * JD-Core Version:    0.6.0
 */