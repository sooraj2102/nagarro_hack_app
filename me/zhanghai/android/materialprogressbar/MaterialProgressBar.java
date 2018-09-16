package me.zhanghai.android.materialprogressbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;
import me.zhanghai.android.materialprogressbar.internal.DrawableCompat;

public class MaterialProgressBar extends ProgressBar
{
  public static final int DETERMINATE_CIRCULAR_PROGRESS_STYLE_DYNAMIC = 1;
  public static final int DETERMINATE_CIRCULAR_PROGRESS_STYLE_NORMAL = 0;
  public static final int PROGRESS_STYLE_CIRCULAR = 0;
  public static final int PROGRESS_STYLE_HORIZONTAL = 1;
  private static final String TAG = MaterialProgressBar.class.getSimpleName();
  private int mProgressStyle;
  private TintInfo mProgressTintInfo = new TintInfo(null);
  private boolean mSuperInitialized = true;

  public MaterialProgressBar(Context paramContext)
  {
    super(paramContext);
    init(null, 0, 0);
  }

  public MaterialProgressBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramAttributeSet, 0, 0);
  }

  public MaterialProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramAttributeSet, paramInt, 0);
  }

  @TargetApi(21)
  public MaterialProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    init(paramAttributeSet, paramInt1, paramInt2);
  }

  private void applyIndeterminateTint()
  {
    Drawable localDrawable = getIndeterminateDrawable();
    if (localDrawable == null);
    do
      return;
    while ((!this.mProgressTintInfo.mHasIndeterminateTint) && (!this.mProgressTintInfo.mHasIndeterminateTintMode));
    localDrawable.mutate();
    applyTintForDrawable(localDrawable, this.mProgressTintInfo.mIndeterminateTint, this.mProgressTintInfo.mHasIndeterminateTint, this.mProgressTintInfo.mIndeterminateTintMode, this.mProgressTintInfo.mHasIndeterminateTintMode);
  }

  private void applyPrimaryProgressTint()
  {
    if (getProgressDrawable() == null);
    Drawable localDrawable;
    do
    {
      do
        return;
      while ((!this.mProgressTintInfo.mHasProgressTint) && (!this.mProgressTintInfo.mHasProgressTintMode));
      localDrawable = getTintTargetFromProgressDrawable(16908301, true);
    }
    while (localDrawable == null);
    applyTintForDrawable(localDrawable, this.mProgressTintInfo.mProgressTint, this.mProgressTintInfo.mHasProgressTint, this.mProgressTintInfo.mProgressTintMode, this.mProgressTintInfo.mHasProgressTintMode);
  }

  private void applyProgressBackgroundTint()
  {
    if (getProgressDrawable() == null);
    Drawable localDrawable;
    do
    {
      do
        return;
      while ((!this.mProgressTintInfo.mHasProgressBackgroundTint) && (!this.mProgressTintInfo.mHasProgressBackgroundTintMode));
      localDrawable = getTintTargetFromProgressDrawable(16908288, false);
    }
    while (localDrawable == null);
    applyTintForDrawable(localDrawable, this.mProgressTintInfo.mProgressBackgroundTint, this.mProgressTintInfo.mHasProgressBackgroundTint, this.mProgressTintInfo.mProgressBackgroundTintMode, this.mProgressTintInfo.mHasProgressBackgroundTintMode);
  }

  private void applyProgressTints()
  {
    if (getProgressDrawable() == null)
      return;
    applyPrimaryProgressTint();
    applyProgressBackgroundTint();
    applySecondaryProgressTint();
  }

  private void applySecondaryProgressTint()
  {
    if (getProgressDrawable() == null);
    Drawable localDrawable;
    do
    {
      do
        return;
      while ((!this.mProgressTintInfo.mHasSecondaryProgressTint) && (!this.mProgressTintInfo.mHasSecondaryProgressTintMode));
      localDrawable = getTintTargetFromProgressDrawable(16908303, false);
    }
    while (localDrawable == null);
    applyTintForDrawable(localDrawable, this.mProgressTintInfo.mSecondaryProgressTint, this.mProgressTintInfo.mHasSecondaryProgressTint, this.mProgressTintInfo.mSecondaryProgressTintMode, this.mProgressTintInfo.mHasSecondaryProgressTintMode);
  }

  @SuppressLint({"NewApi"})
  private void applyTintForDrawable(Drawable paramDrawable, ColorStateList paramColorStateList, boolean paramBoolean1, PorterDuff.Mode paramMode, boolean paramBoolean2)
  {
    if ((paramBoolean1) || (paramBoolean2))
    {
      if (paramBoolean1)
      {
        if (!(paramDrawable instanceof TintableDrawable))
          break label70;
        ((TintableDrawable)paramDrawable).setTintList(paramColorStateList);
      }
      if (paramBoolean2)
      {
        if (!(paramDrawable instanceof TintableDrawable))
          break label95;
        ((TintableDrawable)paramDrawable).setTintMode(paramMode);
      }
    }
    while (true)
    {
      if (paramDrawable.isStateful())
        paramDrawable.setState(getDrawableState());
      return;
      label70: Log.w(TAG, "Drawable did not implement TintableDrawable, it won't be tinted below Lollipop");
      if (Build.VERSION.SDK_INT < 21)
        break;
      paramDrawable.setTintList(paramColorStateList);
      break;
      label95: Log.w(TAG, "Drawable did not implement TintableDrawable, it won't be tinted below Lollipop");
      if (Build.VERSION.SDK_INT < 21)
        continue;
      paramDrawable.setTintMode(paramMode);
    }
  }

  private void fixCanvasScalingWhenHardwareAccelerated()
  {
    if ((Build.VERSION.SDK_INT < 18) && (isHardwareAccelerated()) && (getLayerType() != 1))
      setLayerType(1, null);
  }

  private Drawable getTintTargetFromProgressDrawable(int paramInt, boolean paramBoolean)
  {
    Drawable localDrawable1 = getProgressDrawable();
    Drawable localDrawable2;
    if (localDrawable1 == null)
      localDrawable2 = null;
    do
    {
      return localDrawable2;
      localDrawable1.mutate();
      boolean bool = localDrawable1 instanceof LayerDrawable;
      localDrawable2 = null;
      if (!bool)
        continue;
      localDrawable2 = ((LayerDrawable)localDrawable1).findDrawableByLayerId(paramInt);
    }
    while ((localDrawable2 != null) || (!paramBoolean));
    return localDrawable1;
  }

  private void init(AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    Context localContext = getContext();
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(localContext, paramAttributeSet, R.styleable.MaterialProgressBar, paramInt1, paramInt2);
    this.mProgressStyle = localTintTypedArray.getInt(R.styleable.MaterialProgressBar_mpb_progressStyle, 0);
    boolean bool1 = localTintTypedArray.getBoolean(R.styleable.MaterialProgressBar_mpb_setBothDrawables, false);
    boolean bool2 = localTintTypedArray.getBoolean(R.styleable.MaterialProgressBar_mpb_useIntrinsicPadding, true);
    int i = R.styleable.MaterialProgressBar_mpb_showProgressBackground;
    if (this.mProgressStyle == 1);
    boolean bool4;
    int j;
    for (boolean bool3 = true; ; bool3 = false)
    {
      bool4 = localTintTypedArray.getBoolean(i, bool3);
      j = localTintTypedArray.getInt(R.styleable.MaterialProgressBar_mpb_determinateCircularProgressStyle, 0);
      if (localTintTypedArray.hasValue(R.styleable.MaterialProgressBar_mpb_progressTint))
      {
        this.mProgressTintInfo.mProgressTint = localTintTypedArray.getColorStateList(R.styleable.MaterialProgressBar_mpb_progressTint);
        this.mProgressTintInfo.mHasProgressTint = true;
      }
      if (localTintTypedArray.hasValue(R.styleable.MaterialProgressBar_mpb_progressTintMode))
      {
        this.mProgressTintInfo.mProgressTintMode = DrawableCompat.parseTintMode(localTintTypedArray.getInt(R.styleable.MaterialProgressBar_mpb_progressTintMode, -1), null);
        this.mProgressTintInfo.mHasProgressTintMode = true;
      }
      if (localTintTypedArray.hasValue(R.styleable.MaterialProgressBar_mpb_secondaryProgressTint))
      {
        this.mProgressTintInfo.mSecondaryProgressTint = localTintTypedArray.getColorStateList(R.styleable.MaterialProgressBar_mpb_secondaryProgressTint);
        this.mProgressTintInfo.mHasSecondaryProgressTint = true;
      }
      if (localTintTypedArray.hasValue(R.styleable.MaterialProgressBar_mpb_secondaryProgressTintMode))
      {
        this.mProgressTintInfo.mSecondaryProgressTintMode = DrawableCompat.parseTintMode(localTintTypedArray.getInt(R.styleable.MaterialProgressBar_mpb_secondaryProgressTintMode, -1), null);
        this.mProgressTintInfo.mHasSecondaryProgressTintMode = true;
      }
      if (localTintTypedArray.hasValue(R.styleable.MaterialProgressBar_mpb_progressBackgroundTint))
      {
        this.mProgressTintInfo.mProgressBackgroundTint = localTintTypedArray.getColorStateList(R.styleable.MaterialProgressBar_mpb_progressBackgroundTint);
        this.mProgressTintInfo.mHasProgressBackgroundTint = true;
      }
      if (localTintTypedArray.hasValue(R.styleable.MaterialProgressBar_mpb_progressBackgroundTintMode))
      {
        this.mProgressTintInfo.mProgressBackgroundTintMode = DrawableCompat.parseTintMode(localTintTypedArray.getInt(R.styleable.MaterialProgressBar_mpb_progressBackgroundTintMode, -1), null);
        this.mProgressTintInfo.mHasProgressBackgroundTintMode = true;
      }
      if (localTintTypedArray.hasValue(R.styleable.MaterialProgressBar_mpb_indeterminateTint))
      {
        this.mProgressTintInfo.mIndeterminateTint = localTintTypedArray.getColorStateList(R.styleable.MaterialProgressBar_mpb_indeterminateTint);
        this.mProgressTintInfo.mHasIndeterminateTint = true;
      }
      if (localTintTypedArray.hasValue(R.styleable.MaterialProgressBar_mpb_indeterminateTintMode))
      {
        this.mProgressTintInfo.mIndeterminateTintMode = DrawableCompat.parseTintMode(localTintTypedArray.getInt(R.styleable.MaterialProgressBar_mpb_indeterminateTintMode, -1), null);
        this.mProgressTintInfo.mHasIndeterminateTintMode = true;
      }
      localTintTypedArray.recycle();
      switch (this.mProgressStyle)
      {
      default:
        throw new IllegalArgumentException("Unknown progress style: " + this.mProgressStyle);
      case 0:
      case 1:
      }
    }
    if (((isIndeterminate()) || (bool1)) && (!isInEditMode()))
      setIndeterminateDrawable(new IndeterminateCircularProgressDrawable(localContext));
    if ((!isIndeterminate()) || (bool1))
      setProgressDrawable(new CircularProgressDrawable(j, localContext));
    while (true)
    {
      setUseIntrinsicPadding(bool2);
      setShowProgressBackground(bool4);
      return;
      if (((isIndeterminate()) || (bool1)) && (!isInEditMode()))
        setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(localContext));
      if ((isIndeterminate()) && (!bool1))
        continue;
      setProgressDrawable(new HorizontalProgressDrawable(localContext));
    }
  }

  public Drawable getCurrentDrawable()
  {
    if (isIndeterminate())
      return getIndeterminateDrawable();
    return getProgressDrawable();
  }

  @Nullable
  public ColorStateList getIndeterminateTintList()
  {
    return this.mProgressTintInfo.mIndeterminateTint;
  }

  @Nullable
  public PorterDuff.Mode getIndeterminateTintMode()
  {
    return this.mProgressTintInfo.mIndeterminateTintMode;
  }

  @Nullable
  public ColorStateList getProgressBackgroundTintList()
  {
    return this.mProgressTintInfo.mProgressBackgroundTint;
  }

  @Nullable
  public PorterDuff.Mode getProgressBackgroundTintMode()
  {
    return this.mProgressTintInfo.mProgressBackgroundTintMode;
  }

  public int getProgressStyle()
  {
    return this.mProgressStyle;
  }

  @Nullable
  public ColorStateList getProgressTintList()
  {
    return this.mProgressTintInfo.mProgressTint;
  }

  @Nullable
  public PorterDuff.Mode getProgressTintMode()
  {
    return this.mProgressTintInfo.mProgressTintMode;
  }

  @Nullable
  public ColorStateList getSecondaryProgressTintList()
  {
    return this.mProgressTintInfo.mSecondaryProgressTint;
  }

  @Nullable
  public PorterDuff.Mode getSecondaryProgressTintMode()
  {
    return this.mProgressTintInfo.mSecondaryProgressTintMode;
  }

  public boolean getShowProgressBackground()
  {
    Drawable localDrawable = getCurrentDrawable();
    if ((localDrawable instanceof ShowBackgroundDrawable))
      return ((ShowBackgroundDrawable)localDrawable).getShowBackground();
    return false;
  }

  public boolean getUseIntrinsicPadding()
  {
    Drawable localDrawable = getCurrentDrawable();
    if ((localDrawable instanceof IntrinsicPaddingDrawable))
      return ((IntrinsicPaddingDrawable)localDrawable).getUseIntrinsicPadding();
    throw new IllegalStateException("Drawable does not implement IntrinsicPaddingDrawable");
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    fixCanvasScalingWhenHardwareAccelerated();
  }

  public void setIndeterminate(boolean paramBoolean)
  {
    monitorenter;
    try
    {
      super.setIndeterminate(paramBoolean);
      if ((this.mSuperInitialized) && (!(getCurrentDrawable() instanceof MaterialProgressDrawable)))
        Log.w(TAG, "Current drawable is not a MaterialProgressDrawable, you may want to set app:mpb_setBothDrawables");
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public void setIndeterminateDrawable(Drawable paramDrawable)
  {
    super.setIndeterminateDrawable(paramDrawable);
    if (this.mProgressTintInfo != null)
      applyIndeterminateTint();
  }

  public void setIndeterminateTintList(@Nullable ColorStateList paramColorStateList)
  {
    this.mProgressTintInfo.mIndeterminateTint = paramColorStateList;
    this.mProgressTintInfo.mHasIndeterminateTint = true;
    applyIndeterminateTint();
  }

  public void setIndeterminateTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    this.mProgressTintInfo.mIndeterminateTintMode = paramMode;
    this.mProgressTintInfo.mHasIndeterminateTintMode = true;
    applyIndeterminateTint();
  }

  public void setProgressBackgroundTintList(@Nullable ColorStateList paramColorStateList)
  {
    this.mProgressTintInfo.mProgressBackgroundTint = paramColorStateList;
    this.mProgressTintInfo.mHasProgressBackgroundTint = true;
    applyProgressBackgroundTint();
  }

  public void setProgressBackgroundTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    this.mProgressTintInfo.mProgressBackgroundTintMode = paramMode;
    this.mProgressTintInfo.mHasProgressBackgroundTintMode = true;
    applyProgressBackgroundTint();
  }

  public void setProgressDrawable(Drawable paramDrawable)
  {
    super.setProgressDrawable(paramDrawable);
    if (this.mProgressTintInfo != null)
      applyProgressTints();
  }

  public void setProgressTintList(@Nullable ColorStateList paramColorStateList)
  {
    this.mProgressTintInfo.mProgressTint = paramColorStateList;
    this.mProgressTintInfo.mHasProgressTint = true;
    applyPrimaryProgressTint();
  }

  public void setProgressTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    this.mProgressTintInfo.mProgressTintMode = paramMode;
    this.mProgressTintInfo.mHasProgressTintMode = true;
    applyPrimaryProgressTint();
  }

  public void setSecondaryProgressTintList(@Nullable ColorStateList paramColorStateList)
  {
    this.mProgressTintInfo.mSecondaryProgressTint = paramColorStateList;
    this.mProgressTintInfo.mHasSecondaryProgressTint = true;
    applySecondaryProgressTint();
  }

  public void setSecondaryProgressTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    this.mProgressTintInfo.mSecondaryProgressTintMode = paramMode;
    this.mProgressTintInfo.mHasSecondaryProgressTintMode = true;
    applySecondaryProgressTint();
  }

  public void setShowProgressBackground(boolean paramBoolean)
  {
    Drawable localDrawable1 = getCurrentDrawable();
    if ((localDrawable1 instanceof ShowBackgroundDrawable))
      ((ShowBackgroundDrawable)localDrawable1).setShowBackground(paramBoolean);
    Drawable localDrawable2 = getIndeterminateDrawable();
    if ((localDrawable2 instanceof ShowBackgroundDrawable))
      ((ShowBackgroundDrawable)localDrawable2).setShowBackground(paramBoolean);
  }

  public void setUseIntrinsicPadding(boolean paramBoolean)
  {
    Drawable localDrawable1 = getCurrentDrawable();
    if ((localDrawable1 instanceof IntrinsicPaddingDrawable))
      ((IntrinsicPaddingDrawable)localDrawable1).setUseIntrinsicPadding(paramBoolean);
    Drawable localDrawable2 = getIndeterminateDrawable();
    if ((localDrawable2 instanceof IntrinsicPaddingDrawable))
      ((IntrinsicPaddingDrawable)localDrawable2).setUseIntrinsicPadding(paramBoolean);
  }

  private static class TintInfo
  {
    public boolean mHasIndeterminateTint;
    public boolean mHasIndeterminateTintMode;
    public boolean mHasProgressBackgroundTint;
    public boolean mHasProgressBackgroundTintMode;
    public boolean mHasProgressTint;
    public boolean mHasProgressTintMode;
    public boolean mHasSecondaryProgressTint;
    public boolean mHasSecondaryProgressTintMode;
    public ColorStateList mIndeterminateTint;
    public PorterDuff.Mode mIndeterminateTintMode;
    public ColorStateList mProgressBackgroundTint;
    public PorterDuff.Mode mProgressBackgroundTintMode;
    public ColorStateList mProgressTint;
    public PorterDuff.Mode mProgressTintMode;
    public ColorStateList mSecondaryProgressTint;
    public PorterDuff.Mode mSecondaryProgressTintMode;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     me.zhanghai.android.materialprogressbar.MaterialProgressBar
 * JD-Core Version:    0.6.0
 */