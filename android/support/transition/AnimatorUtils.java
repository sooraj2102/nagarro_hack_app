package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;

class AnimatorUtils
{
  private static final AnimatorUtilsImpl IMPL;

  static
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new AnimatorUtilsApi19();
      return;
    }
    IMPL = new AnimatorUtilsApi14();
  }

  static void addPauseListener(@NonNull Animator paramAnimator, @NonNull AnimatorListenerAdapter paramAnimatorListenerAdapter)
  {
    IMPL.addPauseListener(paramAnimator, paramAnimatorListenerAdapter);
  }

  static void pause(@NonNull Animator paramAnimator)
  {
    IMPL.pause(paramAnimator);
  }

  static void resume(@NonNull Animator paramAnimator)
  {
    IMPL.resume(paramAnimator);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.AnimatorUtils
 * JD-Core Version:    0.6.0
 */