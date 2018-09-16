package android.support.transition;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
abstract interface ViewOverlayImpl
{
  public abstract void add(@NonNull Drawable paramDrawable);

  public abstract void clear();

  public abstract void remove(@NonNull Drawable paramDrawable);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewOverlayImpl
 * JD-Core Version:    0.6.0
 */