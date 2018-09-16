package android.support.v7.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R.styleable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.StaticLayout.Builder;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

class AppCompatTextViewAutoSizeHelper
{
  private static final int DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX = 1;
  private static final int DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 112;
  private static final int DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12;
  private static final String TAG = "ACTVAutoSizeHelper";
  private static final RectF TEMP_RECTF = new RectF();
  static final float UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE = -1.0F;
  private static final int VERY_WIDE = 1048576;
  private static Hashtable<String, Method> sTextViewMethodByNameCache = new Hashtable();
  private float mAutoSizeMaxTextSizeInPx = -1.0F;
  private float mAutoSizeMinTextSizeInPx = -1.0F;
  private float mAutoSizeStepGranularityInPx = -1.0F;
  private int[] mAutoSizeTextSizesInPx = new int[0];
  private int mAutoSizeTextType = 0;
  private final Context mContext;
  private boolean mHasPresetAutoSizeValues = false;
  private boolean mNeedsAutoSizeText = false;
  private TextPaint mTempTextPaint;
  private final TextView mTextView;

  AppCompatTextViewAutoSizeHelper(TextView paramTextView)
  {
    this.mTextView = paramTextView;
    this.mContext = this.mTextView.getContext();
  }

  private int[] cleanupAutoSizePresetSizes(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    if (i == 0);
    ArrayList localArrayList;
    do
    {
      return paramArrayOfInt;
      Arrays.sort(paramArrayOfInt);
      localArrayList = new ArrayList();
      for (int j = 0; j < i; j++)
      {
        int n = paramArrayOfInt[j];
        if ((n <= 0) || (Collections.binarySearch(localArrayList, Integer.valueOf(n)) >= 0))
          continue;
        localArrayList.add(Integer.valueOf(n));
      }
    }
    while (i == localArrayList.size());
    int k = localArrayList.size();
    int[] arrayOfInt = new int[k];
    for (int m = 0; m < k; m++)
      arrayOfInt[m] = ((Integer)localArrayList.get(m)).intValue();
    return arrayOfInt;
  }

  private void clearAutoSizeConfiguration()
  {
    this.mAutoSizeTextType = 0;
    this.mAutoSizeMinTextSizeInPx = -1.0F;
    this.mAutoSizeMaxTextSizeInPx = -1.0F;
    this.mAutoSizeStepGranularityInPx = -1.0F;
    this.mAutoSizeTextSizesInPx = new int[0];
    this.mNeedsAutoSizeText = false;
  }

  @TargetApi(23)
  private StaticLayout createStaticLayoutForMeasuring(CharSequence paramCharSequence, Layout.Alignment paramAlignment, int paramInt1, int paramInt2)
  {
    TextDirectionHeuristic localTextDirectionHeuristic = (TextDirectionHeuristic)invokeAndReturnWithDefault(this.mTextView, "getTextDirectionHeuristic", TextDirectionHeuristics.FIRSTSTRONG_LTR);
    StaticLayout.Builder localBuilder = StaticLayout.Builder.obtain(paramCharSequence, 0, paramCharSequence.length(), this.mTempTextPaint, paramInt1).setAlignment(paramAlignment).setLineSpacing(this.mTextView.getLineSpacingExtra(), this.mTextView.getLineSpacingMultiplier()).setIncludePad(this.mTextView.getIncludeFontPadding()).setBreakStrategy(this.mTextView.getBreakStrategy()).setHyphenationFrequency(this.mTextView.getHyphenationFrequency());
    if (paramInt2 == -1)
      paramInt2 = 2147483647;
    return localBuilder.setMaxLines(paramInt2).setTextDirection(localTextDirectionHeuristic).build();
  }

  @TargetApi(14)
  private StaticLayout createStaticLayoutForMeasuringPre23(CharSequence paramCharSequence, Layout.Alignment paramAlignment, int paramInt)
  {
    float f1;
    float f2;
    if (Build.VERSION.SDK_INT >= 16)
    {
      f1 = this.mTextView.getLineSpacingMultiplier();
      f2 = this.mTextView.getLineSpacingExtra();
    }
    for (boolean bool = this.mTextView.getIncludeFontPadding(); ; bool = ((Boolean)invokeAndReturnWithDefault(this.mTextView, "getIncludeFontPadding", Boolean.valueOf(true))).booleanValue())
    {
      return new StaticLayout(paramCharSequence, this.mTempTextPaint, paramInt, paramAlignment, f1, f2, bool);
      f1 = ((Float)invokeAndReturnWithDefault(this.mTextView, "getLineSpacingMultiplier", Float.valueOf(1.0F))).floatValue();
      f2 = ((Float)invokeAndReturnWithDefault(this.mTextView, "getLineSpacingExtra", Float.valueOf(0.0F))).floatValue();
    }
  }

  private int findLargestTextSizeWhichFits(RectF paramRectF)
  {
    int i = this.mAutoSizeTextSizesInPx.length;
    if (i == 0)
      throw new IllegalStateException("No available text sizes to choose from.");
    int j = 0;
    int k = 0 + 1;
    int m = i - 1;
    while (k <= m)
    {
      int n = (k + m) / 2;
      if (suggestedSizeFitsInSpace(this.mAutoSizeTextSizesInPx[n], paramRectF))
      {
        j = k;
        k = n + 1;
        continue;
      }
      m = n - 1;
      j = m;
    }
    return this.mAutoSizeTextSizesInPx[j];
  }

  @Nullable
  private Method getTextViewMethod(@NonNull String paramString)
  {
    try
    {
      Method localMethod = (Method)sTextViewMethodByNameCache.get(paramString);
      if (localMethod == null)
      {
        localMethod = TextView.class.getDeclaredMethod(paramString, new Class[0]);
        if (localMethod != null)
        {
          localMethod.setAccessible(true);
          sTextViewMethodByNameCache.put(paramString, localMethod);
        }
      }
      return localMethod;
    }
    catch (Exception localException)
    {
      Log.w("ACTVAutoSizeHelper", "Failed to retrieve TextView#" + paramString + "() method", localException);
    }
    return null;
  }

  private <T> T invokeAndReturnWithDefault(@NonNull Object paramObject, @NonNull String paramString, @NonNull T paramT)
  {
    int i = 0;
    try
    {
      Object localObject3 = getTextViewMethod(paramString).invoke(paramObject, new Object[0]);
      localObject2 = localObject3;
      if ((localObject2 == null) && (0 != 0))
        localObject2 = paramT;
      return localObject2;
    }
    catch (Exception localException)
    {
      do
      {
        do
        {
          i = 1;
          Log.w("ACTVAutoSizeHelper", "Failed to invoke TextView#" + paramString + "() method", localException);
          localObject2 = null;
        }
        while (0 != 0);
        Object localObject2 = null;
      }
      while (i == 0);
      return paramT;
    }
    finally
    {
      if ((0 != 0) || (i == 0));
    }
    throw localObject1;
  }

  private void setRawTextSize(float paramFloat)
  {
    boolean bool;
    if (paramFloat != this.mTextView.getPaint().getTextSize())
    {
      this.mTextView.getPaint().setTextSize(paramFloat);
      int i = Build.VERSION.SDK_INT;
      bool = false;
      if (i >= 18)
        bool = this.mTextView.isInLayout();
      if (this.mTextView.getLayout() != null)
        this.mNeedsAutoSizeText = false;
    }
    try
    {
      Method localMethod = getTextViewMethod("nullLayouts");
      if (localMethod != null)
        localMethod.invoke(this.mTextView, new Object[0]);
      if (!bool)
      {
        this.mTextView.requestLayout();
        this.mTextView.invalidate();
        return;
      }
    }
    catch (Exception localException)
    {
      while (true)
      {
        Log.w("ACTVAutoSizeHelper", "Failed to invoke TextView#nullLayouts() method", localException);
        continue;
        this.mTextView.forceLayout();
      }
    }
  }

  private boolean setupAutoSizeText()
  {
    if ((supportsAutoSizeText()) && (this.mAutoSizeTextType == 1))
      if ((!this.mHasPresetAutoSizeValues) || (this.mAutoSizeTextSizesInPx.length == 0))
      {
        int i = 1;
        float f1 = Math.round(this.mAutoSizeMinTextSizeInPx);
        while (Math.round(f1 + this.mAutoSizeStepGranularityInPx) <= Math.round(this.mAutoSizeMaxTextSizeInPx))
        {
          i++;
          f1 += this.mAutoSizeStepGranularityInPx;
        }
        int[] arrayOfInt = new int[i];
        float f2 = this.mAutoSizeMinTextSizeInPx;
        for (int j = 0; j < i; j++)
        {
          arrayOfInt[j] = Math.round(f2);
          f2 += this.mAutoSizeStepGranularityInPx;
        }
        this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(arrayOfInt);
      }
    for (this.mNeedsAutoSizeText = true; ; this.mNeedsAutoSizeText = false)
      return this.mNeedsAutoSizeText;
  }

  private void setupAutoSizeUniformPresetSizes(TypedArray paramTypedArray)
  {
    int i = paramTypedArray.length();
    int[] arrayOfInt = new int[i];
    if (i > 0)
    {
      for (int j = 0; j < i; j++)
        arrayOfInt[j] = paramTypedArray.getDimensionPixelSize(j, -1);
      this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(arrayOfInt);
      setupAutoSizeUniformPresetSizesConfiguration();
    }
  }

  private boolean setupAutoSizeUniformPresetSizesConfiguration()
  {
    int i = this.mAutoSizeTextSizesInPx.length;
    if (i > 0);
    for (boolean bool = true; ; bool = false)
    {
      this.mHasPresetAutoSizeValues = bool;
      if (this.mHasPresetAutoSizeValues)
      {
        this.mAutoSizeTextType = 1;
        this.mAutoSizeMinTextSizeInPx = this.mAutoSizeTextSizesInPx[0];
        this.mAutoSizeMaxTextSizeInPx = this.mAutoSizeTextSizesInPx[(i - 1)];
        this.mAutoSizeStepGranularityInPx = -1.0F;
      }
      return this.mHasPresetAutoSizeValues;
    }
  }

  private boolean suggestedSizeFitsInSpace(int paramInt, RectF paramRectF)
  {
    CharSequence localCharSequence = this.mTextView.getText();
    int i;
    label43: Layout.Alignment localAlignment;
    if (Build.VERSION.SDK_INT >= 16)
    {
      i = this.mTextView.getMaxLines();
      if (this.mTempTextPaint != null)
        break label156;
      this.mTempTextPaint = new TextPaint();
      this.mTempTextPaint.set(this.mTextView.getPaint());
      this.mTempTextPaint.setTextSize(paramInt);
      localAlignment = (Layout.Alignment)invokeAndReturnWithDefault(this.mTextView, "getLayoutAlignment", Layout.Alignment.ALIGN_NORMAL);
      if (Build.VERSION.SDK_INT < 23)
        break label166;
    }
    label156: label166: for (StaticLayout localStaticLayout = createStaticLayoutForMeasuring(localCharSequence, localAlignment, Math.round(paramRectF.right), i); ; localStaticLayout = createStaticLayoutForMeasuringPre23(localCharSequence, localAlignment, Math.round(paramRectF.right)))
    {
      if ((i == -1) || ((localStaticLayout.getLineCount() <= i) && (localStaticLayout.getLineEnd(-1 + localStaticLayout.getLineCount()) == localCharSequence.length())))
        break label185;
      return false;
      i = -1;
      break;
      this.mTempTextPaint.reset();
      break label43;
    }
    label185: return localStaticLayout.getHeight() <= paramRectF.bottom;
  }

  private boolean supportsAutoSizeText()
  {
    return !(this.mTextView instanceof AppCompatEditText);
  }

  private void validateAndSetAutoSizeTextTypeUniformConfiguration(float paramFloat1, float paramFloat2, float paramFloat3)
    throws IllegalArgumentException
  {
    if (paramFloat1 <= 0.0F)
      throw new IllegalArgumentException("Minimum auto-size text size (" + paramFloat1 + "px) is less or equal to (0px)");
    if (paramFloat2 <= paramFloat1)
      throw new IllegalArgumentException("Maximum auto-size text size (" + paramFloat2 + "px) is less or equal to minimum auto-size " + "text size (" + paramFloat1 + "px)");
    if (paramFloat3 <= 0.0F)
      throw new IllegalArgumentException("The auto-size step granularity (" + paramFloat3 + "px) is less or equal to (0px)");
    this.mAutoSizeTextType = 1;
    this.mAutoSizeMinTextSizeInPx = paramFloat1;
    this.mAutoSizeMaxTextSizeInPx = paramFloat2;
    this.mAutoSizeStepGranularityInPx = paramFloat3;
    this.mHasPresetAutoSizeValues = false;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void autoSizeText()
  {
    if (!isAutoSizeEnabled());
    int i;
    while (true)
    {
      return;
      if (!this.mNeedsAutoSizeText)
        break;
      if ((this.mTextView.getMeasuredHeight() <= 0) || (this.mTextView.getMeasuredWidth() <= 0))
        continue;
      if (!((Boolean)invokeAndReturnWithDefault(this.mTextView, "getHorizontallyScrolling", Boolean.valueOf(false))).booleanValue())
        break label160;
      i = 1048576;
    }
    while (true)
    {
      int j = this.mTextView.getHeight() - this.mTextView.getCompoundPaddingBottom() - this.mTextView.getCompoundPaddingTop();
      if ((i <= 0) || (j <= 0))
        break;
      synchronized (TEMP_RECTF)
      {
        TEMP_RECTF.setEmpty();
        TEMP_RECTF.right = i;
        TEMP_RECTF.bottom = j;
        float f = findLargestTextSizeWhichFits(TEMP_RECTF);
        if (f != this.mTextView.getTextSize())
          setTextSizeInternal(0, f);
        this.mNeedsAutoSizeText = true;
        return;
        label160: i = this.mTextView.getMeasuredWidth() - this.mTextView.getTotalPaddingLeft() - this.mTextView.getTotalPaddingRight();
      }
    }
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  int getAutoSizeMaxTextSize()
  {
    return Math.round(this.mAutoSizeMaxTextSizeInPx);
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  int getAutoSizeMinTextSize()
  {
    return Math.round(this.mAutoSizeMinTextSizeInPx);
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  int getAutoSizeStepGranularity()
  {
    return Math.round(this.mAutoSizeStepGranularityInPx);
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  int[] getAutoSizeTextAvailableSizes()
  {
    return this.mAutoSizeTextSizesInPx;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  int getAutoSizeTextType()
  {
    return this.mAutoSizeTextType;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  boolean isAutoSizeEnabled()
  {
    return (supportsAutoSizeText()) && (this.mAutoSizeTextType != 0);
  }

  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt)
  {
    float f1 = -1.0F;
    float f2 = -1.0F;
    float f3 = -1.0F;
    TypedArray localTypedArray1 = this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AppCompatTextView, paramInt, 0);
    if (localTypedArray1.hasValue(R.styleable.AppCompatTextView_autoSizeTextType))
      this.mAutoSizeTextType = localTypedArray1.getInt(R.styleable.AppCompatTextView_autoSizeTextType, 0);
    if (localTypedArray1.hasValue(R.styleable.AppCompatTextView_autoSizeStepGranularity))
      f3 = localTypedArray1.getDimension(R.styleable.AppCompatTextView_autoSizeStepGranularity, -1.0F);
    if (localTypedArray1.hasValue(R.styleable.AppCompatTextView_autoSizeMinTextSize))
      f1 = localTypedArray1.getDimension(R.styleable.AppCompatTextView_autoSizeMinTextSize, -1.0F);
    if (localTypedArray1.hasValue(R.styleable.AppCompatTextView_autoSizeMaxTextSize))
      f2 = localTypedArray1.getDimension(R.styleable.AppCompatTextView_autoSizeMaxTextSize, -1.0F);
    if (localTypedArray1.hasValue(R.styleable.AppCompatTextView_autoSizePresetSizes))
    {
      int i = localTypedArray1.getResourceId(R.styleable.AppCompatTextView_autoSizePresetSizes, 0);
      if (i > 0)
      {
        TypedArray localTypedArray2 = localTypedArray1.getResources().obtainTypedArray(i);
        setupAutoSizeUniformPresetSizes(localTypedArray2);
        localTypedArray2.recycle();
      }
    }
    localTypedArray1.recycle();
    if (supportsAutoSizeText())
    {
      if (this.mAutoSizeTextType == 1)
      {
        if (!this.mHasPresetAutoSizeValues)
        {
          DisplayMetrics localDisplayMetrics = this.mContext.getResources().getDisplayMetrics();
          if (f1 == -1.0F)
            f1 = TypedValue.applyDimension(2, 12.0F, localDisplayMetrics);
          if (f2 == -1.0F)
            f2 = TypedValue.applyDimension(2, 112.0F, localDisplayMetrics);
          if (f3 == -1.0F)
            f3 = 1.0F;
          validateAndSetAutoSizeTextTypeUniformConfiguration(f1, f2, f3);
        }
        setupAutoSizeText();
      }
      return;
    }
    this.mAutoSizeTextType = 0;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void setAutoSizeTextTypeUniformWithConfiguration(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IllegalArgumentException
  {
    if (supportsAutoSizeText())
    {
      DisplayMetrics localDisplayMetrics = this.mContext.getResources().getDisplayMetrics();
      validateAndSetAutoSizeTextTypeUniformConfiguration(TypedValue.applyDimension(paramInt4, paramInt1, localDisplayMetrics), TypedValue.applyDimension(paramInt4, paramInt2, localDisplayMetrics), TypedValue.applyDimension(paramInt4, paramInt3, localDisplayMetrics));
      if (setupAutoSizeText())
        autoSizeText();
    }
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] paramArrayOfInt, int paramInt)
    throws IllegalArgumentException
  {
    if (supportsAutoSizeText())
    {
      int i = paramArrayOfInt.length;
      if (i > 0)
      {
        int[] arrayOfInt = new int[i];
        if (paramInt == 0)
          arrayOfInt = Arrays.copyOf(paramArrayOfInt, i);
        while (true)
        {
          this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(arrayOfInt);
          if (setupAutoSizeUniformPresetSizesConfiguration())
            break;
          throw new IllegalArgumentException("None of the preset sizes is valid: " + Arrays.toString(paramArrayOfInt));
          DisplayMetrics localDisplayMetrics = this.mContext.getResources().getDisplayMetrics();
          for (int j = 0; j < i; j++)
            arrayOfInt[j] = Math.round(TypedValue.applyDimension(paramInt, paramArrayOfInt[j], localDisplayMetrics));
        }
      }
      this.mHasPresetAutoSizeValues = false;
      if (setupAutoSizeText())
        autoSizeText();
    }
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void setAutoSizeTextTypeWithDefaults(int paramInt)
  {
    if (supportsAutoSizeText())
      switch (paramInt)
      {
      default:
        throw new IllegalArgumentException("Unknown auto-size text type: " + paramInt);
      case 0:
        clearAutoSizeConfiguration();
      case 1:
      }
    do
    {
      return;
      DisplayMetrics localDisplayMetrics = this.mContext.getResources().getDisplayMetrics();
      validateAndSetAutoSizeTextTypeUniformConfiguration(TypedValue.applyDimension(2, 12.0F, localDisplayMetrics), TypedValue.applyDimension(2, 112.0F, localDisplayMetrics), 1.0F);
    }
    while (!setupAutoSizeText());
    autoSizeText();
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void setTextSizeInternal(int paramInt, float paramFloat)
  {
    if (this.mContext == null);
    for (Resources localResources = Resources.getSystem(); ; localResources = this.mContext.getResources())
    {
      setRawTextSize(TypedValue.applyDimension(paramInt, paramFloat, localResources.getDisplayMetrics()));
      return;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.AppCompatTextViewAutoSizeHelper
 * JD-Core Version:    0.6.0
 */