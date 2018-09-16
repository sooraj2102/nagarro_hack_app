package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;

@RequiresApi(14)
abstract interface ViewUtilsImpl
{
  public abstract void clearNonTransitionAlpha(@NonNull View paramView);

  public abstract ViewOverlayImpl getOverlay(@NonNull View paramView);

  public abstract float getTransitionAlpha(@NonNull View paramView);

  public abstract WindowIdImpl getWindowId(@NonNull View paramView);

  public abstract void saveNonTransitionAlpha(@NonNull View paramView);

  public abstract void setAnimationMatrix(@NonNull View paramView, Matrix paramMatrix);

  public abstract void setLeftTopRightBottom(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void setTransitionAlpha(@NonNull View paramView, float paramFloat);

  public abstract void transformMatrixToGlobal(@NonNull View paramView, @NonNull Matrix paramMatrix);

  public abstract void transformMatrixToLocal(@NonNull View paramView, @NonNull Matrix paramMatrix);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewUtilsImpl
 * JD-Core Version:    0.6.0
 */