package com.facebook.shimmer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

public class ShimmerFrameLayout extends FrameLayout
{
  private static final PorterDuffXfermode DST_IN_PORTER_DUFF_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
  private static final String TAG = "ShimmerFrameLayout";
  private Paint mAlphaPaint;
  private boolean mAnimationStarted;
  protected ValueAnimator mAnimator;
  private boolean mAutoStart;
  private int mDuration;
  private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;
  private Mask mMask;
  protected Bitmap mMaskBitmap;
  private int mMaskOffsetX;
  private int mMaskOffsetY;
  private Paint mMaskPaint;
  private MaskTranslation mMaskTranslation;
  private Bitmap mRenderMaskBitmap;
  private Bitmap mRenderUnmaskBitmap;
  private int mRepeatCount;
  private int mRepeatDelay;
  private int mRepeatMode;

  public ShimmerFrameLayout(Context paramContext)
  {
    this(paramContext, null, 0);
  }

  public ShimmerFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public ShimmerFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setWillNotDraw(false);
    this.mMask = new Mask(null);
    this.mAlphaPaint = new Paint();
    this.mMaskPaint = new Paint();
    this.mMaskPaint.setAntiAlias(true);
    this.mMaskPaint.setDither(true);
    this.mMaskPaint.setFilterBitmap(true);
    this.mMaskPaint.setXfermode(DST_IN_PORTER_DUFF_XFERMODE);
    useDefaults();
    TypedArray localTypedArray;
    if (paramAttributeSet != null)
      localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ShimmerFrameLayout, 0, 0);
    while (true)
    {
      try
      {
        if (!localTypedArray.hasValue(0))
          continue;
        setAutoStart(localTypedArray.getBoolean(0, false));
        if (!localTypedArray.hasValue(1))
          continue;
        setBaseAlpha(localTypedArray.getFloat(1, 0.0F));
        if (!localTypedArray.hasValue(2))
          continue;
        setDuration(localTypedArray.getInt(2, 0));
        if (!localTypedArray.hasValue(3))
          continue;
        setRepeatCount(localTypedArray.getInt(3, 0));
        if (!localTypedArray.hasValue(4))
          continue;
        setRepeatDelay(localTypedArray.getInt(4, 0));
        if (!localTypedArray.hasValue(5))
          continue;
        setRepeatMode(localTypedArray.getInt(5, 0));
        if (!localTypedArray.hasValue(6))
          continue;
        switch (localTypedArray.getInt(6, 0))
        {
        default:
          this.mMask.angle = MaskAngle.CW_0;
          if (!localTypedArray.hasValue(13))
            continue;
          switch (localTypedArray.getInt(13, 0))
          {
          default:
            this.mMask.shape = MaskShape.LINEAR;
            if (!localTypedArray.hasValue(7))
              continue;
            this.mMask.dropoff = localTypedArray.getFloat(7, 0.0F);
            if (!localTypedArray.hasValue(8))
              continue;
            this.mMask.fixedWidth = localTypedArray.getDimensionPixelSize(8, 0);
            if (!localTypedArray.hasValue(9))
              continue;
            this.mMask.fixedHeight = localTypedArray.getDimensionPixelSize(9, 0);
            if (!localTypedArray.hasValue(10))
              continue;
            this.mMask.intensity = localTypedArray.getFloat(10, 0.0F);
            if (!localTypedArray.hasValue(11))
              continue;
            this.mMask.relativeWidth = localTypedArray.getFloat(11, 0.0F);
            if (!localTypedArray.hasValue(12))
              continue;
            this.mMask.relativeHeight = localTypedArray.getFloat(12, 0.0F);
            if (!localTypedArray.hasValue(14))
              continue;
            this.mMask.tilt = localTypedArray.getFloat(14, 0.0F);
            return;
          case 1:
          }
        case 90:
          this.mMask.angle = MaskAngle.CW_90;
          continue;
        case 180:
        case 270:
        }
      }
      finally
      {
        localTypedArray.recycle();
      }
      this.mMask.angle = MaskAngle.CW_180;
      continue;
      this.mMask.angle = MaskAngle.CW_270;
      continue;
      this.mMask.shape = MaskShape.RADIAL;
    }
  }

  private static float clamp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return Math.min(paramFloat2, Math.max(paramFloat1, paramFloat3));
  }

  protected static Bitmap createBitmapAndGcIfNecessary(int paramInt1, int paramInt2)
  {
    try
    {
      Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
      return localBitmap;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      System.gc();
    }
    return Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
  }

  private boolean dispatchDrawUsingBitmap(Canvas paramCanvas)
  {
    Bitmap localBitmap1 = tryObtainRenderUnmaskBitmap();
    Bitmap localBitmap2 = tryObtainRenderMaskBitmap();
    if ((localBitmap1 == null) || (localBitmap2 == null))
      return false;
    drawUnmasked(new Canvas(localBitmap1));
    paramCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, this.mAlphaPaint);
    drawMasked(new Canvas(localBitmap2));
    paramCanvas.drawBitmap(localBitmap2, 0.0F, 0.0F, null);
    return true;
  }

  private void drawMasked(Canvas paramCanvas)
  {
    Bitmap localBitmap = getMaskBitmap();
    if (localBitmap == null)
      return;
    paramCanvas.clipRect(this.mMaskOffsetX, this.mMaskOffsetY, this.mMaskOffsetX + localBitmap.getWidth(), this.mMaskOffsetY + localBitmap.getHeight());
    super.dispatchDraw(paramCanvas);
    paramCanvas.drawBitmap(localBitmap, this.mMaskOffsetX, this.mMaskOffsetY, this.mMaskPaint);
  }

  private void drawUnmasked(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
  }

  private ViewTreeObserver.OnGlobalLayoutListener getLayoutListener()
  {
    return new ViewTreeObserver.OnGlobalLayoutListener()
    {
      public void onGlobalLayout()
      {
        boolean bool = ShimmerFrameLayout.this.mAnimationStarted;
        ShimmerFrameLayout.this.resetAll();
        if ((ShimmerFrameLayout.this.mAutoStart) || (bool))
          ShimmerFrameLayout.this.startShimmerAnimation();
      }
    };
  }

  private Bitmap getMaskBitmap()
  {
    if (this.mMaskBitmap != null)
      return this.mMaskBitmap;
    int i = this.mMask.maskWidth(getWidth());
    int j = this.mMask.maskHeight(getHeight());
    this.mMaskBitmap = createBitmapAndGcIfNecessary(i, j);
    Canvas localCanvas = new Canvas(this.mMaskBitmap);
    int i3;
    int i1;
    int i5;
    int i7;
    switch (3.$SwitchMap$com$facebook$shimmer$ShimmerFrameLayout$MaskShape[this.mMask.shape.ordinal()])
    {
    default:
      switch (3.$SwitchMap$com$facebook$shimmer$ShimmerFrameLayout$MaskAngle[this.mMask.angle.ordinal()])
      {
      default:
        i3 = 0;
        i1 = 0;
        i5 = i;
        i7 = 0;
      case 2:
      case 3:
      case 4:
      }
    case 2:
    }
    int k;
    int m;
    for (Object localObject = new LinearGradient(i3, i1, i5, i7, this.mMask.getGradientColors(), this.mMask.getGradientPositions(), Shader.TileMode.REPEAT); ; localObject = new RadialGradient(k, m, (float)(Math.max(i, j) / Math.sqrt(2.0D)), this.mMask.getGradientColors(), this.mMask.getGradientPositions(), Shader.TileMode.REPEAT))
    {
      localCanvas.rotate(this.mMask.tilt, i / 2, j / 2);
      Paint localPaint = new Paint();
      localPaint.setShader((Shader)localObject);
      int n = (int)(Math.sqrt(2.0D) * Math.max(i, j)) / 2;
      localCanvas.drawRect(-n, -n, i + n, j + n, localPaint);
      return this.mMaskBitmap;
      i7 = j;
      int i4 = 0;
      int i6 = 0;
      int i2 = 0;
      break;
      i4 = i;
      i6 = 0;
      i2 = 0;
      int i8 = 0;
      break;
      i2 = j;
      i4 = 0;
      i6 = 0;
      i8 = 0;
      break;
      k = i / 2;
      m = j / 2;
    }
  }

  private Animator getShimmerAnimation()
  {
    if (this.mAnimator != null)
      return this.mAnimator;
    int i = getWidth();
    int j = getHeight();
    3.$SwitchMap$com$facebook$shimmer$ShimmerFrameLayout$MaskShape[this.mMask.shape.ordinal()];
    switch (3.$SwitchMap$com$facebook$shimmer$ShimmerFrameLayout$MaskAngle[this.mMask.angle.ordinal()])
    {
    default:
      this.mMaskTranslation.set(-i, 0, i, 0);
    case 2:
    case 3:
    case 4:
    }
    while (true)
    {
      float[] arrayOfFloat = new float[2];
      arrayOfFloat[0] = 0.0F;
      arrayOfFloat[1] = (1.0F + this.mRepeatDelay / this.mDuration);
      this.mAnimator = ValueAnimator.ofFloat(arrayOfFloat);
      this.mAnimator.setDuration(this.mDuration + this.mRepeatDelay);
      this.mAnimator.setRepeatCount(this.mRepeatCount);
      this.mAnimator.setRepeatMode(this.mRepeatMode);
      this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramValueAnimator)
        {
          float f = Math.max(0.0F, Math.min(1.0F, ((Float)paramValueAnimator.getAnimatedValue()).floatValue()));
          ShimmerFrameLayout.this.setMaskOffsetX((int)(ShimmerFrameLayout.this.mMaskTranslation.fromX * (1.0F - f) + f * ShimmerFrameLayout.this.mMaskTranslation.toX));
          ShimmerFrameLayout.this.setMaskOffsetY((int)(ShimmerFrameLayout.this.mMaskTranslation.fromY * (1.0F - f) + f * ShimmerFrameLayout.this.mMaskTranslation.toY));
        }
      });
      return this.mAnimator;
      this.mMaskTranslation.set(0, -j, 0, j);
      continue;
      this.mMaskTranslation.set(i, 0, -i, 0);
      continue;
      this.mMaskTranslation.set(0, j, 0, -j);
    }
  }

  private void resetAll()
  {
    stopShimmerAnimation();
    resetMaskBitmap();
    resetRenderedView();
  }

  private void resetMaskBitmap()
  {
    if (this.mMaskBitmap != null)
    {
      this.mMaskBitmap.recycle();
      this.mMaskBitmap = null;
    }
  }

  private void resetRenderedView()
  {
    if (this.mRenderUnmaskBitmap != null)
    {
      this.mRenderUnmaskBitmap.recycle();
      this.mRenderUnmaskBitmap = null;
    }
    if (this.mRenderMaskBitmap != null)
    {
      this.mRenderMaskBitmap.recycle();
      this.mRenderMaskBitmap = null;
    }
  }

  private void setMaskOffsetX(int paramInt)
  {
    if (this.mMaskOffsetX == paramInt)
      return;
    this.mMaskOffsetX = paramInt;
    invalidate();
  }

  private void setMaskOffsetY(int paramInt)
  {
    if (this.mMaskOffsetY == paramInt)
      return;
    this.mMaskOffsetY = paramInt;
    invalidate();
  }

  private Bitmap tryCreateRenderBitmap()
  {
    int i = getWidth();
    int j = getHeight();
    try
    {
      Bitmap localBitmap = createBitmapAndGcIfNecessary(i, j);
      return localBitmap;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      StringBuilder localStringBuilder = new StringBuilder("ShimmerFrameLayout failed to create working bitmap");
      localStringBuilder.append(" (width = ");
      localStringBuilder.append(i);
      localStringBuilder.append(", height = ");
      localStringBuilder.append(j);
      localStringBuilder.append(")\n\n");
      StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
      int k = arrayOfStackTraceElement.length;
      for (int m = 0; m < k; m++)
      {
        localStringBuilder.append(arrayOfStackTraceElement[m].toString());
        localStringBuilder.append("\n");
      }
      Log.d("ShimmerFrameLayout", localStringBuilder.toString());
    }
    return null;
  }

  private Bitmap tryObtainRenderMaskBitmap()
  {
    if (this.mRenderMaskBitmap == null)
      this.mRenderMaskBitmap = tryCreateRenderBitmap();
    return this.mRenderMaskBitmap;
  }

  private Bitmap tryObtainRenderUnmaskBitmap()
  {
    if (this.mRenderUnmaskBitmap == null)
      this.mRenderUnmaskBitmap = tryCreateRenderBitmap();
    return this.mRenderUnmaskBitmap;
  }

  protected void dispatchDraw(Canvas paramCanvas)
  {
    if ((!this.mAnimationStarted) || (getWidth() <= 0) || (getHeight() <= 0))
    {
      super.dispatchDraw(paramCanvas);
      return;
    }
    dispatchDrawUsingBitmap(paramCanvas);
  }

  public MaskAngle getAngle()
  {
    return this.mMask.angle;
  }

  public float getBaseAlpha()
  {
    return this.mAlphaPaint.getAlpha() / 255.0F;
  }

  public float getDropoff()
  {
    return this.mMask.dropoff;
  }

  public int getDuration()
  {
    return this.mDuration;
  }

  public int getFixedHeight()
  {
    return this.mMask.fixedHeight;
  }

  public int getFixedWidth()
  {
    return this.mMask.fixedWidth;
  }

  public float getIntensity()
  {
    return this.mMask.intensity;
  }

  public MaskShape getMaskShape()
  {
    return this.mMask.shape;
  }

  public float getRelativeHeight()
  {
    return this.mMask.relativeHeight;
  }

  public float getRelativeWidth()
  {
    return this.mMask.relativeWidth;
  }

  public int getRepeatCount()
  {
    return this.mRepeatCount;
  }

  public int getRepeatDelay()
  {
    return this.mRepeatDelay;
  }

  public int getRepeatMode()
  {
    return this.mRepeatMode;
  }

  public float getTilt()
  {
    return this.mMask.tilt;
  }

  public boolean isAnimationStarted()
  {
    return this.mAnimationStarted;
  }

  public boolean isAutoStart()
  {
    return this.mAutoStart;
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mGlobalLayoutListener == null)
      this.mGlobalLayoutListener = getLayoutListener();
    getViewTreeObserver().addOnGlobalLayoutListener(this.mGlobalLayoutListener);
  }

  protected void onDetachedFromWindow()
  {
    stopShimmerAnimation();
    if (this.mGlobalLayoutListener != null)
    {
      getViewTreeObserver().removeGlobalOnLayoutListener(this.mGlobalLayoutListener);
      this.mGlobalLayoutListener = null;
    }
    super.onDetachedFromWindow();
  }

  public void setAngle(MaskAngle paramMaskAngle)
  {
    this.mMask.angle = paramMaskAngle;
    resetAll();
  }

  public void setAutoStart(boolean paramBoolean)
  {
    this.mAutoStart = paramBoolean;
    resetAll();
  }

  public void setBaseAlpha(float paramFloat)
  {
    this.mAlphaPaint.setAlpha((int)(255.0F * clamp(0.0F, 1.0F, paramFloat)));
    resetAll();
  }

  public void setDropoff(float paramFloat)
  {
    this.mMask.dropoff = paramFloat;
    resetAll();
  }

  public void setDuration(int paramInt)
  {
    this.mDuration = paramInt;
    resetAll();
  }

  public void setFixedHeight(int paramInt)
  {
    this.mMask.fixedHeight = paramInt;
    resetAll();
  }

  public void setFixedWidth(int paramInt)
  {
    this.mMask.fixedWidth = paramInt;
    resetAll();
  }

  public void setIntensity(float paramFloat)
  {
    this.mMask.intensity = paramFloat;
    resetAll();
  }

  public void setMaskShape(MaskShape paramMaskShape)
  {
    this.mMask.shape = paramMaskShape;
    resetAll();
  }

  public void setRelativeHeight(int paramInt)
  {
    this.mMask.relativeHeight = paramInt;
    resetAll();
  }

  public void setRelativeWidth(int paramInt)
  {
    this.mMask.relativeWidth = paramInt;
    resetAll();
  }

  public void setRepeatCount(int paramInt)
  {
    this.mRepeatCount = paramInt;
    resetAll();
  }

  public void setRepeatDelay(int paramInt)
  {
    this.mRepeatDelay = paramInt;
    resetAll();
  }

  public void setRepeatMode(int paramInt)
  {
    this.mRepeatMode = paramInt;
    resetAll();
  }

  public void setTilt(float paramFloat)
  {
    this.mMask.tilt = paramFloat;
    resetAll();
  }

  public void startShimmerAnimation()
  {
    if (this.mAnimationStarted)
      return;
    getShimmerAnimation().start();
    this.mAnimationStarted = true;
  }

  public void stopShimmerAnimation()
  {
    if (this.mAnimator != null)
    {
      this.mAnimator.end();
      this.mAnimator.removeAllUpdateListeners();
      this.mAnimator.cancel();
    }
    this.mAnimator = null;
    this.mAnimationStarted = false;
  }

  public void useDefaults()
  {
    setAutoStart(false);
    setDuration(1000);
    setRepeatCount(-1);
    setRepeatDelay(0);
    setRepeatMode(1);
    this.mMask.angle = MaskAngle.CW_0;
    this.mMask.shape = MaskShape.LINEAR;
    this.mMask.dropoff = 0.5F;
    this.mMask.fixedWidth = 0;
    this.mMask.fixedHeight = 0;
    this.mMask.intensity = 0.0F;
    this.mMask.relativeWidth = 1.0F;
    this.mMask.relativeHeight = 1.0F;
    this.mMask.tilt = 20.0F;
    this.mMaskTranslation = new MaskTranslation(null);
    setBaseAlpha(0.3F);
    resetAll();
  }

  private static class Mask
  {
    public ShimmerFrameLayout.MaskAngle angle;
    public float dropoff;
    public int fixedHeight;
    public int fixedWidth;
    public float intensity;
    public float relativeHeight;
    public float relativeWidth;
    public ShimmerFrameLayout.MaskShape shape;
    public float tilt;

    public int[] getGradientColors()
    {
      switch (ShimmerFrameLayout.3.$SwitchMap$com$facebook$shimmer$ShimmerFrameLayout$MaskShape[this.shape.ordinal()])
      {
      default:
        return new int[] { 0, -16777216, -16777216, 0 };
      case 2:
      }
      return new int[] { -16777216, -16777216, 0 };
    }

    public float[] getGradientPositions()
    {
      switch (ShimmerFrameLayout.3.$SwitchMap$com$facebook$shimmer$ShimmerFrameLayout$MaskShape[this.shape.ordinal()])
      {
      default:
        float[] arrayOfFloat2 = new float[4];
        arrayOfFloat2[0] = Math.max((1.0F - this.intensity - this.dropoff) / 2.0F, 0.0F);
        arrayOfFloat2[1] = Math.max((1.0F - this.intensity) / 2.0F, 0.0F);
        arrayOfFloat2[2] = Math.min((1.0F + this.intensity) / 2.0F, 1.0F);
        arrayOfFloat2[3] = Math.min((1.0F + this.intensity + this.dropoff) / 2.0F, 1.0F);
        return arrayOfFloat2;
      case 2:
      }
      float[] arrayOfFloat1 = new float[3];
      arrayOfFloat1[0] = 0.0F;
      arrayOfFloat1[1] = Math.min(this.intensity, 1.0F);
      arrayOfFloat1[2] = Math.min(this.intensity + this.dropoff, 1.0F);
      return arrayOfFloat1;
    }

    public int maskHeight(int paramInt)
    {
      if (this.fixedHeight > 0)
        return this.fixedHeight;
      return (int)(paramInt * this.relativeHeight);
    }

    public int maskWidth(int paramInt)
    {
      if (this.fixedWidth > 0)
        return this.fixedWidth;
      return (int)(paramInt * this.relativeWidth);
    }
  }

  public static enum MaskAngle
  {
    static
    {
      CW_180 = new MaskAngle("CW_180", 2);
      CW_270 = new MaskAngle("CW_270", 3);
      MaskAngle[] arrayOfMaskAngle = new MaskAngle[4];
      arrayOfMaskAngle[0] = CW_0;
      arrayOfMaskAngle[1] = CW_90;
      arrayOfMaskAngle[2] = CW_180;
      arrayOfMaskAngle[3] = CW_270;
      $VALUES = arrayOfMaskAngle;
    }
  }

  public static enum MaskShape
  {
    static
    {
      MaskShape[] arrayOfMaskShape = new MaskShape[2];
      arrayOfMaskShape[0] = LINEAR;
      arrayOfMaskShape[1] = RADIAL;
      $VALUES = arrayOfMaskShape;
    }
  }

  private static class MaskTranslation
  {
    public int fromX;
    public int fromY;
    public int toX;
    public int toY;

    public void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.fromX = paramInt1;
      this.fromY = paramInt2;
      this.toX = paramInt3;
      this.toY = paramInt4;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.facebook.shimmer.ShimmerFrameLayout
 * JD-Core Version:    0.6.0
 */