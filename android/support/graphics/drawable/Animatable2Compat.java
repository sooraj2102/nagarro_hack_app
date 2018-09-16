package android.support.graphics.drawable;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2.AnimationCallback;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

public abstract interface Animatable2Compat extends Animatable
{
  public abstract void clearAnimationCallbacks();

  public abstract void registerAnimationCallback(@NonNull AnimationCallback paramAnimationCallback);

  public abstract boolean unregisterAnimationCallback(@NonNull AnimationCallback paramAnimationCallback);

  public static abstract class AnimationCallback
  {
    Animatable2.AnimationCallback mPlatformCallback;

    @RequiresApi(23)
    Animatable2.AnimationCallback getPlatformCallback()
    {
      if (this.mPlatformCallback == null)
        this.mPlatformCallback = new Animatable2.AnimationCallback()
        {
          public void onAnimationEnd(Drawable paramDrawable)
          {
            Animatable2Compat.AnimationCallback.this.onAnimationEnd(paramDrawable);
          }

          public void onAnimationStart(Drawable paramDrawable)
          {
            Animatable2Compat.AnimationCallback.this.onAnimationStart(paramDrawable);
          }
        };
      return this.mPlatformCallback;
    }

    public void onAnimationEnd(Drawable paramDrawable)
    {
    }

    public void onAnimationStart(Drawable paramDrawable)
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.graphics.drawable.Animatable2Compat
 * JD-Core Version:    0.6.0
 */