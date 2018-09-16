package android.support.v4.widget;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public abstract interface AutoSizeableTextView
{
  public abstract int getAutoSizeMaxTextSize();

  public abstract int getAutoSizeMinTextSize();

  public abstract int getAutoSizeStepGranularity();

  public abstract int[] getAutoSizeTextAvailableSizes();

  public abstract int getAutoSizeTextType();

  public abstract void setAutoSizeTextTypeUniformWithConfiguration(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IllegalArgumentException;

  public abstract void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] paramArrayOfInt, int paramInt)
    throws IllegalArgumentException;

  public abstract void setAutoSizeTextTypeWithDefaults(int paramInt);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.widget.AutoSizeableTextView
 * JD-Core Version:    0.6.0
 */