package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewParent;

@RequiresApi(14)
class ViewUtilsApi14
  implements ViewUtilsImpl
{
  private float[] mMatrixValues;

  public void clearNonTransitionAlpha(@NonNull View paramView)
  {
    if (paramView.getVisibility() == 0)
      paramView.setTag(R.id.save_non_transition_alpha, null);
  }

  public ViewOverlayImpl getOverlay(@NonNull View paramView)
  {
    return ViewOverlayApi14.createFrom(paramView);
  }

  public float getTransitionAlpha(@NonNull View paramView)
  {
    Float localFloat = (Float)paramView.getTag(R.id.save_non_transition_alpha);
    if (localFloat != null)
      return paramView.getAlpha() / localFloat.floatValue();
    return paramView.getAlpha();
  }

  public WindowIdImpl getWindowId(@NonNull View paramView)
  {
    return new WindowIdApi14(paramView.getWindowToken());
  }

  public void saveNonTransitionAlpha(@NonNull View paramView)
  {
    if (paramView.getTag(R.id.save_non_transition_alpha) == null)
      paramView.setTag(R.id.save_non_transition_alpha, Float.valueOf(paramView.getAlpha()));
  }

  public void setAnimationMatrix(@NonNull View paramView, Matrix paramMatrix)
  {
    if ((paramMatrix == null) || (paramMatrix.isIdentity()))
    {
      paramView.setPivotX(paramView.getWidth() / 2);
      paramView.setPivotY(paramView.getHeight() / 2);
      paramView.setTranslationX(0.0F);
      paramView.setTranslationY(0.0F);
      paramView.setScaleX(1.0F);
      paramView.setScaleY(1.0F);
      paramView.setRotation(0.0F);
      return;
    }
    float[] arrayOfFloat = this.mMatrixValues;
    if (arrayOfFloat == null)
    {
      arrayOfFloat = new float[9];
      this.mMatrixValues = arrayOfFloat;
    }
    paramMatrix.getValues(arrayOfFloat);
    float f1 = arrayOfFloat[3];
    float f2 = (float)Math.sqrt(1.0F - f1 * f1);
    if (arrayOfFloat[0] < 0.0F);
    for (int i = -1; ; i = 1)
    {
      float f3 = f2 * i;
      float f4 = (float)Math.toDegrees(Math.atan2(f1, f3));
      float f5 = arrayOfFloat[0] / f3;
      float f6 = arrayOfFloat[4] / f3;
      float f7 = arrayOfFloat[2];
      float f8 = arrayOfFloat[5];
      paramView.setPivotX(0.0F);
      paramView.setPivotY(0.0F);
      paramView.setTranslationX(f7);
      paramView.setTranslationY(f8);
      paramView.setRotation(f4);
      paramView.setScaleX(f5);
      paramView.setScaleY(f6);
      return;
    }
  }

  public void setLeftTopRightBottom(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramView.setLeft(paramInt1);
    paramView.setTop(paramInt2);
    paramView.setRight(paramInt3);
    paramView.setBottom(paramInt4);
  }

  public void setTransitionAlpha(@NonNull View paramView, float paramFloat)
  {
    Float localFloat = (Float)paramView.getTag(R.id.save_non_transition_alpha);
    if (localFloat != null)
    {
      paramView.setAlpha(paramFloat * localFloat.floatValue());
      return;
    }
    paramView.setAlpha(paramFloat);
  }

  public void transformMatrixToGlobal(@NonNull View paramView, @NonNull Matrix paramMatrix)
  {
    ViewParent localViewParent = paramView.getParent();
    if ((localViewParent instanceof View))
    {
      View localView = (View)localViewParent;
      transformMatrixToGlobal(localView, paramMatrix);
      paramMatrix.preTranslate(-localView.getScrollX(), -localView.getScrollY());
    }
    paramMatrix.preTranslate(paramView.getLeft(), paramView.getTop());
    Matrix localMatrix = paramView.getMatrix();
    if (!localMatrix.isIdentity())
      paramMatrix.preConcat(localMatrix);
  }

  public void transformMatrixToLocal(@NonNull View paramView, @NonNull Matrix paramMatrix)
  {
    ViewParent localViewParent = paramView.getParent();
    if ((localViewParent instanceof View))
    {
      View localView = (View)localViewParent;
      transformMatrixToLocal(localView, paramMatrix);
      paramMatrix.postTranslate(localView.getScrollX(), localView.getScrollY());
    }
    paramMatrix.postTranslate(paramView.getLeft(), paramView.getTop());
    Matrix localMatrix1 = paramView.getMatrix();
    if (!localMatrix1.isIdentity())
    {
      Matrix localMatrix2 = new Matrix();
      if (localMatrix1.invert(localMatrix2))
        paramMatrix.postConcat(localMatrix2);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewUtilsApi14
 * JD-Core Version:    0.6.0
 */