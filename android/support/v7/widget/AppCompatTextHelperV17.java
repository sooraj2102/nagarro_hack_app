package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.widget.TextView;

@RequiresApi(17)
class AppCompatTextHelperV17 extends AppCompatTextHelper
{
  private TintInfo mDrawableEndTint;
  private TintInfo mDrawableStartTint;

  AppCompatTextHelperV17(TextView paramTextView)
  {
    super(paramTextView);
  }

  void applyCompoundDrawablesTints()
  {
    super.applyCompoundDrawablesTints();
    if ((this.mDrawableStartTint != null) || (this.mDrawableEndTint != null))
    {
      Drawable[] arrayOfDrawable = this.mView.getCompoundDrawablesRelative();
      applyCompoundDrawableTint(arrayOfDrawable[0], this.mDrawableStartTint);
      applyCompoundDrawableTint(arrayOfDrawable[2], this.mDrawableEndTint);
    }
  }

  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt)
  {
    super.loadFromAttributes(paramAttributeSet, paramInt);
    Context localContext = this.mView.getContext();
    AppCompatDrawableManager localAppCompatDrawableManager = AppCompatDrawableManager.get();
    TypedArray localTypedArray = localContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AppCompatTextHelper, paramInt, 0);
    if (localTypedArray.hasValue(R.styleable.AppCompatTextHelper_android_drawableStart))
      this.mDrawableStartTint = createTintInfo(localContext, localAppCompatDrawableManager, localTypedArray.getResourceId(R.styleable.AppCompatTextHelper_android_drawableStart, 0));
    if (localTypedArray.hasValue(R.styleable.AppCompatTextHelper_android_drawableEnd))
      this.mDrawableEndTint = createTintInfo(localContext, localAppCompatDrawableManager, localTypedArray.getResourceId(R.styleable.AppCompatTextHelper_android_drawableEnd, 0));
    localTypedArray.recycle();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.AppCompatTextHelperV17
 * JD-Core Version:    0.6.0
 */