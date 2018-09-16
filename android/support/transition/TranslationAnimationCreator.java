package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.view.View;

class TranslationAnimationCreator
{
  static Animator createAnimation(View paramView, TransitionValues paramTransitionValues, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, TimeInterpolator paramTimeInterpolator)
  {
    float f1 = paramView.getTranslationX();
    float f2 = paramView.getTranslationY();
    int[] arrayOfInt = (int[])(int[])paramTransitionValues.view.getTag(R.id.transition_position);
    if (arrayOfInt != null)
    {
      paramFloat1 = f1 + (arrayOfInt[0] - paramInt1);
      paramFloat2 = f2 + (arrayOfInt[1] - paramInt2);
    }
    int i = paramInt1 + Math.round(paramFloat1 - f1);
    int j = paramInt2 + Math.round(paramFloat2 - f2);
    paramView.setTranslationX(paramFloat1);
    paramView.setTranslationY(paramFloat2);
    if ((paramFloat1 == paramFloat3) && (paramFloat2 == paramFloat4))
      return null;
    PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[2];
    arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, new float[] { paramFloat1, paramFloat3 });
    arrayOfPropertyValuesHolder[1] = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[] { paramFloat2, paramFloat4 });
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(paramView, arrayOfPropertyValuesHolder);
    TransitionPositionListener localTransitionPositionListener = new TransitionPositionListener(paramView, paramTransitionValues.view, i, j, f1, f2, null);
    localObjectAnimator.addListener(localTransitionPositionListener);
    AnimatorUtils.addPauseListener(localObjectAnimator, localTransitionPositionListener);
    localObjectAnimator.setInterpolator(paramTimeInterpolator);
    return localObjectAnimator;
  }

  private static class TransitionPositionListener extends AnimatorListenerAdapter
  {
    private final View mMovingView;
    private float mPausedX;
    private float mPausedY;
    private final int mStartX;
    private final int mStartY;
    private final float mTerminalX;
    private final float mTerminalY;
    private int[] mTransitionPosition;
    private final View mViewInHierarchy;

    private TransitionPositionListener(View paramView1, View paramView2, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
    {
      this.mMovingView = paramView1;
      this.mViewInHierarchy = paramView2;
      this.mStartX = (paramInt1 - Math.round(this.mMovingView.getTranslationX()));
      this.mStartY = (paramInt2 - Math.round(this.mMovingView.getTranslationY()));
      this.mTerminalX = paramFloat1;
      this.mTerminalY = paramFloat2;
      this.mTransitionPosition = ((int[])(int[])this.mViewInHierarchy.getTag(R.id.transition_position));
      if (this.mTransitionPosition != null)
        this.mViewInHierarchy.setTag(R.id.transition_position, null);
    }

    public void onAnimationCancel(Animator paramAnimator)
    {
      if (this.mTransitionPosition == null)
        this.mTransitionPosition = new int[2];
      this.mTransitionPosition[0] = Math.round(this.mStartX + this.mMovingView.getTranslationX());
      this.mTransitionPosition[1] = Math.round(this.mStartY + this.mMovingView.getTranslationY());
      this.mViewInHierarchy.setTag(R.id.transition_position, this.mTransitionPosition);
    }

    public void onAnimationEnd(Animator paramAnimator)
    {
      this.mMovingView.setTranslationX(this.mTerminalX);
      this.mMovingView.setTranslationY(this.mTerminalY);
    }

    public void onAnimationPause(Animator paramAnimator)
    {
      this.mPausedX = this.mMovingView.getTranslationX();
      this.mPausedY = this.mMovingView.getTranslationY();
      this.mMovingView.setTranslationX(this.mTerminalX);
      this.mMovingView.setTranslationY(this.mTerminalY);
    }

    public void onAnimationResume(Animator paramAnimator)
    {
      this.mMovingView.setTranslationX(this.mPausedX);
      this.mMovingView.setTranslationY(this.mPausedY);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.TranslationAnimationCreator
 * JD-Core Version:    0.6.0
 */