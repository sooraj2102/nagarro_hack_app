package me.zhanghai.android.materialprogressbar;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

abstract class BaseDrawable extends Drawable
  implements TintableDrawable
{
  protected int mAlpha = 255;
  protected ColorFilter mColorFilter;
  private DummyConstantState mConstantState = new DummyConstantState(null);
  protected PorterDuffColorFilter mTintFilter;
  protected ColorStateList mTintList;
  protected PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;

  private boolean updateTintFilter()
  {
    int i = 1;
    if ((this.mTintList == null) || (this.mTintMode == null))
    {
      if (this.mTintFilter != null);
      while (true)
      {
        this.mTintFilter = null;
        return i;
        i = 0;
      }
    }
    this.mTintFilter = new PorterDuffColorFilter(this.mTintList.getColorForState(getState(), 0), this.mTintMode);
    return i;
  }

  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    if ((localRect.width() == 0) || (localRect.height() == 0))
      return;
    int i = paramCanvas.save();
    paramCanvas.translate(localRect.left, localRect.top);
    onDraw(paramCanvas, localRect.width(), localRect.height());
    paramCanvas.restoreToCount(i);
  }

  public int getAlpha()
  {
    return this.mAlpha;
  }

  public ColorFilter getColorFilter()
  {
    return this.mColorFilter;
  }

  protected ColorFilter getColorFilterForDrawing()
  {
    if (this.mColorFilter != null)
      return this.mColorFilter;
    return this.mTintFilter;
  }

  public Drawable.ConstantState getConstantState()
  {
    return this.mConstantState;
  }

  public int getOpacity()
  {
    return -3;
  }

  public boolean isStateful()
  {
    return (this.mTintList != null) && (this.mTintList.isStateful());
  }

  protected abstract void onDraw(Canvas paramCanvas, int paramInt1, int paramInt2);

  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    return updateTintFilter();
  }

  public void setAlpha(int paramInt)
  {
    if (this.mAlpha != paramInt)
    {
      this.mAlpha = paramInt;
      invalidateSelf();
    }
  }

  public void setColorFilter(@Nullable ColorFilter paramColorFilter)
  {
    this.mColorFilter = paramColorFilter;
    invalidateSelf();
  }

  public void setTint(@ColorInt int paramInt)
  {
    setTintList(ColorStateList.valueOf(paramInt));
  }

  public void setTintList(@Nullable ColorStateList paramColorStateList)
  {
    this.mTintList = paramColorStateList;
    if (updateTintFilter())
      invalidateSelf();
  }

  public void setTintMode(@NonNull PorterDuff.Mode paramMode)
  {
    this.mTintMode = paramMode;
    if (updateTintFilter())
      invalidateSelf();
  }

  private class DummyConstantState extends Drawable.ConstantState
  {
    private DummyConstantState()
    {
    }

    public int getChangingConfigurations()
    {
      return 0;
    }

    @NonNull
    public Drawable newDrawable()
    {
      return BaseDrawable.this;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     me.zhanghai.android.materialprogressbar.BaseDrawable
 * JD-Core Version:    0.6.0
 */