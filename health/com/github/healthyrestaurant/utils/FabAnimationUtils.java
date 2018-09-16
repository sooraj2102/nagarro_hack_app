package dubeyanurag.com.github.healthyrestaurant.utils;

import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class FabAnimationUtils
{
  private static final long DEFAULT_DURATION = 300L;
  private static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

  public static void scaleIn(View paramView)
  {
    scaleIn(paramView, 300L, null);
  }

  public static void scaleIn(View paramView, long paramLong, ScaleCallback paramScaleCallback)
  {
    paramView.setVisibility(0);
    if (Build.VERSION.SDK_INT >= 14)
    {
      ViewCompat.animate(paramView).scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setDuration(paramLong).setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR).withLayer().setListener(new ViewPropertyAnimatorListener(paramScaleCallback)
      {
        public void onAnimationCancel(View paramView)
        {
        }

        public void onAnimationEnd(View paramView)
        {
          paramView.setVisibility(0);
          if (this.val$callback != null)
            this.val$callback.onAnimationEnd();
        }

        public void onAnimationStart(View paramView)
        {
          if (this.val$callback != null)
            this.val$callback.onAnimationStart();
        }
      }).start();
      return;
    }
    Animation localAnimation = AnimationUtils.loadAnimation(paramView.getContext(), 2130771981);
    localAnimation.setDuration(paramLong);
    localAnimation.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
    localAnimation.setAnimationListener(new Animation.AnimationListener(paramScaleCallback, paramView)
    {
      public void onAnimationEnd(Animation paramAnimation)
      {
        this.val$fab.setVisibility(0);
        if (this.val$callback != null)
          this.val$callback.onAnimationEnd();
      }

      public void onAnimationRepeat(Animation paramAnimation)
      {
      }

      public void onAnimationStart(Animation paramAnimation)
      {
        if (this.val$callback != null)
          this.val$callback.onAnimationStart();
      }
    });
    paramView.startAnimation(localAnimation);
  }

  public static void scaleOut(View paramView)
  {
    scaleOut(paramView, 300L, null);
  }

  public static void scaleOut(View paramView, long paramLong)
  {
    scaleOut(paramView, paramLong, null);
  }

  public static void scaleOut(View paramView, long paramLong, ScaleCallback paramScaleCallback)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      ViewCompat.animate(paramView).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR).setDuration(paramLong).withLayer().setListener(new ViewPropertyAnimatorListener(paramScaleCallback)
      {
        public void onAnimationCancel(View paramView)
        {
        }

        public void onAnimationEnd(View paramView)
        {
          paramView.setVisibility(4);
          if (this.val$callback != null)
            this.val$callback.onAnimationEnd();
        }

        public void onAnimationStart(View paramView)
        {
          if (this.val$callback != null)
            this.val$callback.onAnimationStart();
        }
      }).start();
      return;
    }
    Animation localAnimation = AnimationUtils.loadAnimation(paramView.getContext(), 2130771981);
    localAnimation.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
    localAnimation.setDuration(paramLong);
    localAnimation.setAnimationListener(new Animation.AnimationListener(paramScaleCallback, paramView)
    {
      public void onAnimationEnd(Animation paramAnimation)
      {
        this.val$fab.setVisibility(4);
        if (this.val$callback != null)
          this.val$callback.onAnimationEnd();
      }

      public void onAnimationRepeat(Animation paramAnimation)
      {
      }

      public void onAnimationStart(Animation paramAnimation)
      {
        if (this.val$callback != null)
          this.val$callback.onAnimationStart();
      }
    });
    paramView.startAnimation(localAnimation);
  }

  public static void scaleOut(View paramView, ScaleCallback paramScaleCallback)
  {
    scaleOut(paramView, 300L, paramScaleCallback);
  }

  public static abstract interface ScaleCallback
  {
    public abstract void onAnimationEnd();

    public abstract void onAnimationStart();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.utils.FabAnimationUtils
 * JD-Core Version:    0.6.0
 */