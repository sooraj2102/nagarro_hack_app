package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R.styleable;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.widget.TextView;

@RequiresApi(9)
class AppCompatTextHelper
{

  @NonNull
  private final AppCompatTextViewAutoSizeHelper mAutoSizeTextHelper;
  private TintInfo mDrawableBottomTint;
  private TintInfo mDrawableLeftTint;
  private TintInfo mDrawableRightTint;
  private TintInfo mDrawableTopTint;
  private Typeface mFontTypeface;
  private int mStyle = 0;
  final TextView mView;

  AppCompatTextHelper(TextView paramTextView)
  {
    this.mView = paramTextView;
    this.mAutoSizeTextHelper = new AppCompatTextViewAutoSizeHelper(this.mView);
  }

  static AppCompatTextHelper create(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 17)
      return new AppCompatTextHelperV17(paramTextView);
    return new AppCompatTextHelper(paramTextView);
  }

  protected static TintInfo createTintInfo(Context paramContext, AppCompatDrawableManager paramAppCompatDrawableManager, int paramInt)
  {
    ColorStateList localColorStateList = paramAppCompatDrawableManager.getTintList(paramContext, paramInt);
    if (localColorStateList != null)
    {
      TintInfo localTintInfo = new TintInfo();
      localTintInfo.mHasTintList = true;
      localTintInfo.mTintList = localColorStateList;
      return localTintInfo;
    }
    return null;
  }

  private void setTextSizeInternal(int paramInt, float paramFloat)
  {
    this.mAutoSizeTextHelper.setTextSizeInternal(paramInt, paramFloat);
  }

  private void updateTypefaceAndStyle(Context paramContext, TintTypedArray paramTintTypedArray)
  {
    this.mStyle = paramTintTypedArray.getInt(R.styleable.TextAppearance_android_textStyle, this.mStyle);
    int i;
    if ((paramTintTypedArray.hasValue(R.styleable.TextAppearance_android_fontFamily)) || (paramTintTypedArray.hasValue(R.styleable.TextAppearance_fontFamily)))
    {
      this.mFontTypeface = null;
      if (!paramTintTypedArray.hasValue(R.styleable.TextAppearance_android_fontFamily))
        break label102;
      i = R.styleable.TextAppearance_android_fontFamily;
    }
    while (true)
    {
      if (!paramContext.isRestricted());
      try
      {
        this.mFontTypeface = paramTintTypedArray.getFont(i, this.mStyle, this.mView);
        label78: if (this.mFontTypeface == null)
          this.mFontTypeface = Typeface.create(paramTintTypedArray.getString(i), this.mStyle);
        return;
        label102: i = R.styleable.TextAppearance_fontFamily;
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        break label78;
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        break label78;
      }
    }
  }

  final void applyCompoundDrawableTint(Drawable paramDrawable, TintInfo paramTintInfo)
  {
    if ((paramDrawable != null) && (paramTintInfo != null))
      AppCompatDrawableManager.tintDrawable(paramDrawable, paramTintInfo, this.mView.getDrawableState());
  }

  void applyCompoundDrawablesTints()
  {
    if ((this.mDrawableLeftTint != null) || (this.mDrawableTopTint != null) || (this.mDrawableRightTint != null) || (this.mDrawableBottomTint != null))
    {
      Drawable[] arrayOfDrawable = this.mView.getCompoundDrawables();
      applyCompoundDrawableTint(arrayOfDrawable[0], this.mDrawableLeftTint);
      applyCompoundDrawableTint(arrayOfDrawable[1], this.mDrawableTopTint);
      applyCompoundDrawableTint(arrayOfDrawable[2], this.mDrawableRightTint);
      applyCompoundDrawableTint(arrayOfDrawable[3], this.mDrawableBottomTint);
    }
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void autoSizeText()
  {
    this.mAutoSizeTextHelper.autoSizeText();
  }

  int getAutoSizeMaxTextSize()
  {
    return this.mAutoSizeTextHelper.getAutoSizeMaxTextSize();
  }

  int getAutoSizeMinTextSize()
  {
    return this.mAutoSizeTextHelper.getAutoSizeMinTextSize();
  }

  int getAutoSizeStepGranularity()
  {
    return this.mAutoSizeTextHelper.getAutoSizeStepGranularity();
  }

  int[] getAutoSizeTextAvailableSizes()
  {
    return this.mAutoSizeTextHelper.getAutoSizeTextAvailableSizes();
  }

  int getAutoSizeTextType()
  {
    return this.mAutoSizeTextHelper.getAutoSizeTextType();
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  boolean isAutoSizeEnabled()
  {
    return this.mAutoSizeTextHelper.isAutoSizeEnabled();
  }

  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt)
  {
    Context localContext = this.mView.getContext();
    AppCompatDrawableManager localAppCompatDrawableManager = AppCompatDrawableManager.get();
    TintTypedArray localTintTypedArray1 = TintTypedArray.obtainStyledAttributes(localContext, paramAttributeSet, R.styleable.AppCompatTextHelper, paramInt, 0);
    int i = localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_textAppearance, -1);
    if (localTintTypedArray1.hasValue(R.styleable.AppCompatTextHelper_android_drawableLeft))
      this.mDrawableLeftTint = createTintInfo(localContext, localAppCompatDrawableManager, localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_drawableLeft, 0));
    if (localTintTypedArray1.hasValue(R.styleable.AppCompatTextHelper_android_drawableTop))
      this.mDrawableTopTint = createTintInfo(localContext, localAppCompatDrawableManager, localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_drawableTop, 0));
    if (localTintTypedArray1.hasValue(R.styleable.AppCompatTextHelper_android_drawableRight))
      this.mDrawableRightTint = createTintInfo(localContext, localAppCompatDrawableManager, localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_drawableRight, 0));
    if (localTintTypedArray1.hasValue(R.styleable.AppCompatTextHelper_android_drawableBottom))
      this.mDrawableBottomTint = createTintInfo(localContext, localAppCompatDrawableManager, localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_drawableBottom, 0));
    localTintTypedArray1.recycle();
    boolean bool1 = this.mView.getTransformationMethod() instanceof PasswordTransformationMethod;
    boolean bool2 = false;
    int j = 0;
    ColorStateList localColorStateList1 = null;
    ColorStateList localColorStateList2 = null;
    ColorStateList localColorStateList3 = null;
    if (i != -1)
    {
      TintTypedArray localTintTypedArray3 = TintTypedArray.obtainStyledAttributes(localContext, i, R.styleable.TextAppearance);
      bool2 = false;
      j = 0;
      if (!bool1)
      {
        boolean bool6 = localTintTypedArray3.hasValue(R.styleable.TextAppearance_textAllCaps);
        bool2 = false;
        j = 0;
        if (bool6)
        {
          j = 1;
          bool2 = localTintTypedArray3.getBoolean(R.styleable.TextAppearance_textAllCaps, false);
        }
      }
      updateTypefaceAndStyle(localContext, localTintTypedArray3);
      int k = Build.VERSION.SDK_INT;
      localColorStateList1 = null;
      localColorStateList2 = null;
      localColorStateList3 = null;
      if (k < 23)
      {
        boolean bool3 = localTintTypedArray3.hasValue(R.styleable.TextAppearance_android_textColor);
        localColorStateList1 = null;
        if (bool3)
          localColorStateList1 = localTintTypedArray3.getColorStateList(R.styleable.TextAppearance_android_textColor);
        boolean bool4 = localTintTypedArray3.hasValue(R.styleable.TextAppearance_android_textColorHint);
        localColorStateList2 = null;
        if (bool4)
          localColorStateList2 = localTintTypedArray3.getColorStateList(R.styleable.TextAppearance_android_textColorHint);
        boolean bool5 = localTintTypedArray3.hasValue(R.styleable.TextAppearance_android_textColorLink);
        localColorStateList3 = null;
        if (bool5)
          localColorStateList3 = localTintTypedArray3.getColorStateList(R.styleable.TextAppearance_android_textColorLink);
      }
      localTintTypedArray3.recycle();
    }
    TintTypedArray localTintTypedArray2 = TintTypedArray.obtainStyledAttributes(localContext, paramAttributeSet, R.styleable.TextAppearance, paramInt, 0);
    if ((!bool1) && (localTintTypedArray2.hasValue(R.styleable.TextAppearance_textAllCaps)))
    {
      j = 1;
      bool2 = localTintTypedArray2.getBoolean(R.styleable.TextAppearance_textAllCaps, false);
    }
    if (Build.VERSION.SDK_INT < 23)
    {
      if (localTintTypedArray2.hasValue(R.styleable.TextAppearance_android_textColor))
        localColorStateList1 = localTintTypedArray2.getColorStateList(R.styleable.TextAppearance_android_textColor);
      if (localTintTypedArray2.hasValue(R.styleable.TextAppearance_android_textColorHint))
        localColorStateList2 = localTintTypedArray2.getColorStateList(R.styleable.TextAppearance_android_textColorHint);
      if (localTintTypedArray2.hasValue(R.styleable.TextAppearance_android_textColorLink))
        localColorStateList3 = localTintTypedArray2.getColorStateList(R.styleable.TextAppearance_android_textColorLink);
    }
    updateTypefaceAndStyle(localContext, localTintTypedArray2);
    localTintTypedArray2.recycle();
    if (localColorStateList1 != null)
      this.mView.setTextColor(localColorStateList1);
    if (localColorStateList2 != null)
      this.mView.setHintTextColor(localColorStateList2);
    if (localColorStateList3 != null)
      this.mView.setLinkTextColor(localColorStateList3);
    if ((!bool1) && (j != 0))
      setAllCaps(bool2);
    if (this.mFontTypeface != null)
      this.mView.setTypeface(this.mFontTypeface, this.mStyle);
    this.mAutoSizeTextHelper.loadFromAttributes(paramAttributeSet, paramInt);
    int[] arrayOfInt;
    if ((Build.VERSION.SDK_INT >= 26) && (this.mAutoSizeTextHelper.getAutoSizeTextType() != 0))
    {
      arrayOfInt = this.mAutoSizeTextHelper.getAutoSizeTextAvailableSizes();
      if (arrayOfInt.length > 0)
      {
        if (this.mView.getAutoSizeStepGranularity() == -1.0F)
          break label660;
        this.mView.setAutoSizeTextTypeUniformWithConfiguration(this.mAutoSizeTextHelper.getAutoSizeMinTextSize(), this.mAutoSizeTextHelper.getAutoSizeMaxTextSize(), this.mAutoSizeTextHelper.getAutoSizeStepGranularity(), 0);
      }
    }
    return;
    label660: this.mView.setAutoSizeTextTypeUniformWithPresetSizes(arrayOfInt, 0);
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (Build.VERSION.SDK_INT < 26)
      autoSizeText();
  }

  void onSetTextAppearance(Context paramContext, int paramInt)
  {
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramInt, R.styleable.TextAppearance);
    if (localTintTypedArray.hasValue(R.styleable.TextAppearance_textAllCaps))
      setAllCaps(localTintTypedArray.getBoolean(R.styleable.TextAppearance_textAllCaps, false));
    if ((Build.VERSION.SDK_INT < 23) && (localTintTypedArray.hasValue(R.styleable.TextAppearance_android_textColor)))
    {
      ColorStateList localColorStateList = localTintTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColor);
      if (localColorStateList != null)
        this.mView.setTextColor(localColorStateList);
    }
    updateTypefaceAndStyle(paramContext, localTintTypedArray);
    localTintTypedArray.recycle();
    if (this.mFontTypeface != null)
      this.mView.setTypeface(this.mFontTypeface, this.mStyle);
  }

  void setAllCaps(boolean paramBoolean)
  {
    this.mView.setAllCaps(paramBoolean);
  }

  void setAutoSizeTextTypeUniformWithConfiguration(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IllegalArgumentException
  {
    this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithConfiguration(paramInt1, paramInt2, paramInt3, paramInt4);
  }

  void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] paramArrayOfInt, int paramInt)
    throws IllegalArgumentException
  {
    this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(paramArrayOfInt, paramInt);
  }

  void setAutoSizeTextTypeWithDefaults(int paramInt)
  {
    this.mAutoSizeTextHelper.setAutoSizeTextTypeWithDefaults(paramInt);
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void setTextSize(int paramInt, float paramFloat)
  {
    if ((Build.VERSION.SDK_INT < 26) && (!isAutoSizeEnabled()))
      setTextSizeInternal(paramInt, paramFloat);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.AppCompatTextHelper
 * JD-Core Version:    0.6.0
 */