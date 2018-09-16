package me.zhanghai.android.materialprogressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import me.zhanghai.android.materialprogressbar.internal.ThemeUtils;

class BaseProgressLayerDrawable<ProgressDrawableType extends IntrinsicPaddingDrawable,  extends ShowBackgroundDrawable,  extends TintableDrawable, BackgroundDrawableType extends IntrinsicPaddingDrawable,  extends ShowBackgroundDrawable,  extends TintableDrawable> extends LayerDrawable
  implements IntrinsicPaddingDrawable, MaterialProgressDrawable, ShowBackgroundDrawable, TintableDrawable
{
  private float mBackgroundAlpha;
  private BackgroundDrawableType mBackgroundDrawable;
  private ProgressDrawableType mProgressDrawable;
  private ProgressDrawableType mSecondaryProgressDrawable;

  public BaseProgressLayerDrawable(Drawable[] paramArrayOfDrawable, Context paramContext)
  {
    super(paramArrayOfDrawable);
    this.mBackgroundAlpha = ThemeUtils.getFloatFromAttrRes(16842803, 0.0F, paramContext);
    setId(0, 16908288);
    this.mBackgroundDrawable = ((IntrinsicPaddingDrawable)getDrawable(0));
    setId(1, 16908303);
    this.mSecondaryProgressDrawable = ((IntrinsicPaddingDrawable)getDrawable(1));
    setId(2, 16908301);
    this.mProgressDrawable = ((IntrinsicPaddingDrawable)getDrawable(2));
    setTint(ThemeUtils.getColorFromAttrRes(R.attr.colorControlActivated, -16777216, paramContext));
  }

  public boolean getShowBackground()
  {
    return ((ShowBackgroundDrawable)this.mBackgroundDrawable).getShowBackground();
  }

  public boolean getUseIntrinsicPadding()
  {
    return this.mBackgroundDrawable.getUseIntrinsicPadding();
  }

  public void setShowBackground(boolean paramBoolean)
  {
    ShowBackgroundDrawable localShowBackgroundDrawable;
    if (((ShowBackgroundDrawable)this.mBackgroundDrawable).getShowBackground() != paramBoolean)
    {
      ((ShowBackgroundDrawable)this.mBackgroundDrawable).setShowBackground(paramBoolean);
      localShowBackgroundDrawable = (ShowBackgroundDrawable)this.mSecondaryProgressDrawable;
      if (paramBoolean)
        break label51;
    }
    label51: for (boolean bool = true; ; bool = false)
    {
      localShowBackgroundDrawable.setShowBackground(bool);
      return;
    }
  }

  @SuppressLint({"NewApi"})
  public void setTint(@ColorInt int paramInt)
  {
    int i = ColorUtils.setAlphaComponent(paramInt, Math.round(Color.alpha(paramInt) * this.mBackgroundAlpha));
    ((TintableDrawable)this.mBackgroundDrawable).setTint(i);
    ((TintableDrawable)this.mSecondaryProgressDrawable).setTint(i);
    ((TintableDrawable)this.mProgressDrawable).setTint(paramInt);
  }

  @SuppressLint({"NewApi"})
  public void setTintList(@Nullable ColorStateList paramColorStateList)
  {
    if (paramColorStateList != null)
      if (!paramColorStateList.isOpaque())
        Log.w(getClass().getSimpleName(), "setTintList() called with a non-opaque ColorStateList, its original alpha will be discarded");
    for (ColorStateList localColorStateList = paramColorStateList.withAlpha(Math.round(255.0F * this.mBackgroundAlpha)); ; localColorStateList = null)
    {
      ((TintableDrawable)this.mBackgroundDrawable).setTintList(localColorStateList);
      ((TintableDrawable)this.mSecondaryProgressDrawable).setTintList(localColorStateList);
      ((TintableDrawable)this.mProgressDrawable).setTintList(paramColorStateList);
      return;
    }
  }

  @SuppressLint({"NewApi"})
  public void setTintMode(@NonNull PorterDuff.Mode paramMode)
  {
    ((TintableDrawable)this.mBackgroundDrawable).setTintMode(paramMode);
    ((TintableDrawable)this.mSecondaryProgressDrawable).setTintMode(paramMode);
    ((TintableDrawable)this.mProgressDrawable).setTintMode(paramMode);
  }

  public void setUseIntrinsicPadding(boolean paramBoolean)
  {
    this.mBackgroundDrawable.setUseIntrinsicPadding(paramBoolean);
    this.mSecondaryProgressDrawable.setUseIntrinsicPadding(paramBoolean);
    this.mProgressDrawable.setUseIntrinsicPadding(paramBoolean);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     me.zhanghai.android.materialprogressbar.BaseProgressLayerDrawable
 * JD-Core Version:    0.6.0
 */