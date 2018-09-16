package com.afollestad.materialdialogs.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.R.attr;
import com.afollestad.materialdialogs.R.drawable;
import com.afollestad.materialdialogs.util.DialogUtils;
import java.lang.reflect.Field;

@SuppressLint({"PrivateResource"})
public class MDTintHelper
{
  private static ColorStateList createEditTextColorStateList(@NonNull Context paramContext, @ColorInt int paramInt)
  {
    int[][] arrayOfInt = new int[3][];
    int[] arrayOfInt1 = new int[3];
    arrayOfInt[0] = { -16842910 };
    arrayOfInt1[0] = DialogUtils.resolveColor(paramContext, R.attr.colorControlNormal);
    int i = 0 + 1;
    arrayOfInt[i] = { -16842919, -16842908 };
    arrayOfInt1[i] = DialogUtils.resolveColor(paramContext, R.attr.colorControlNormal);
    int j = i + 1;
    arrayOfInt[j] = new int[0];
    arrayOfInt1[j] = paramInt;
    return new ColorStateList(arrayOfInt, arrayOfInt1);
  }

  private static void setCursorTint(@NonNull EditText paramEditText, @ColorInt int paramInt)
  {
    try
    {
      Field localField1 = TextView.class.getDeclaredField("mCursorDrawableRes");
      localField1.setAccessible(true);
      int i = localField1.getInt(paramEditText);
      Field localField2 = TextView.class.getDeclaredField("mEditor");
      localField2.setAccessible(true);
      Object localObject = localField2.get(paramEditText);
      Field localField3 = localObject.getClass().getDeclaredField("mCursorDrawable");
      localField3.setAccessible(true);
      Drawable[] arrayOfDrawable = new Drawable[2];
      arrayOfDrawable[0] = ContextCompat.getDrawable(paramEditText.getContext(), i);
      arrayOfDrawable[1] = ContextCompat.getDrawable(paramEditText.getContext(), i);
      arrayOfDrawable[0].setColorFilter(paramInt, PorterDuff.Mode.SRC_IN);
      arrayOfDrawable[1].setColorFilter(paramInt, PorterDuff.Mode.SRC_IN);
      localField3.set(localObject, arrayOfDrawable);
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      Log.d("MDTintHelper", "Device issue with cursor tinting: " + localNoSuchFieldException.getMessage());
      localNoSuchFieldException.printStackTrace();
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public static void setTint(@NonNull CheckBox paramCheckBox, @ColorInt int paramInt)
  {
    int i = DialogUtils.getDisabledColor(paramCheckBox.getContext());
    int[][] arrayOfInt = { { 16842910, -16842912 }, { 16842910, 16842912 }, { -16842910, -16842912 }, { -16842910, 16842912 } };
    int[] arrayOfInt1 = new int[4];
    arrayOfInt1[0] = DialogUtils.resolveColor(paramCheckBox.getContext(), R.attr.colorControlNormal);
    arrayOfInt1[1] = paramInt;
    arrayOfInt1[2] = i;
    arrayOfInt1[3] = i;
    setTint(paramCheckBox, new ColorStateList(arrayOfInt, arrayOfInt1));
  }

  public static void setTint(@NonNull CheckBox paramCheckBox, @NonNull ColorStateList paramColorStateList)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramCheckBox.setButtonTintList(paramColorStateList);
      return;
    }
    Drawable localDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(paramCheckBox.getContext(), R.drawable.abc_btn_check_material));
    DrawableCompat.setTintList(localDrawable, paramColorStateList);
    paramCheckBox.setButtonDrawable(localDrawable);
  }

  public static void setTint(@NonNull EditText paramEditText, @ColorInt int paramInt)
  {
    ColorStateList localColorStateList = createEditTextColorStateList(paramEditText.getContext(), paramInt);
    if ((paramEditText instanceof AppCompatEditText))
      ((AppCompatEditText)paramEditText).setSupportBackgroundTintList(localColorStateList);
    while (true)
    {
      setCursorTint(paramEditText, paramInt);
      return;
      if (Build.VERSION.SDK_INT < 21)
        continue;
      paramEditText.setBackgroundTintList(localColorStateList);
    }
  }

  public static void setTint(@NonNull ProgressBar paramProgressBar, @ColorInt int paramInt)
  {
    setTint(paramProgressBar, paramInt, false);
  }

  private static void setTint(@NonNull ProgressBar paramProgressBar, @ColorInt int paramInt, boolean paramBoolean)
  {
    ColorStateList localColorStateList = ColorStateList.valueOf(paramInt);
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramProgressBar.setProgressTintList(localColorStateList);
      paramProgressBar.setSecondaryProgressTintList(localColorStateList);
      if (!paramBoolean)
        paramProgressBar.setIndeterminateTintList(localColorStateList);
    }
    PorterDuff.Mode localMode;
    do
    {
      return;
      localMode = PorterDuff.Mode.SRC_IN;
      if (Build.VERSION.SDK_INT <= 10)
        localMode = PorterDuff.Mode.MULTIPLY;
      if ((paramBoolean) || (paramProgressBar.getIndeterminateDrawable() == null))
        continue;
      paramProgressBar.getIndeterminateDrawable().setColorFilter(paramInt, localMode);
    }
    while (paramProgressBar.getProgressDrawable() == null);
    paramProgressBar.getProgressDrawable().setColorFilter(paramInt, localMode);
  }

  public static void setTint(@NonNull RadioButton paramRadioButton, @ColorInt int paramInt)
  {
    int i = DialogUtils.getDisabledColor(paramRadioButton.getContext());
    int[][] arrayOfInt = { { 16842910, -16842912 }, { 16842910, 16842912 }, { -16842910, -16842912 }, { -16842910, 16842912 } };
    int[] arrayOfInt1 = new int[4];
    arrayOfInt1[0] = DialogUtils.resolveColor(paramRadioButton.getContext(), R.attr.colorControlNormal);
    arrayOfInt1[1] = paramInt;
    arrayOfInt1[2] = i;
    arrayOfInt1[3] = i;
    setTint(paramRadioButton, new ColorStateList(arrayOfInt, arrayOfInt1));
  }

  public static void setTint(@NonNull RadioButton paramRadioButton, @NonNull ColorStateList paramColorStateList)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramRadioButton.setButtonTintList(paramColorStateList);
      return;
    }
    Drawable localDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(paramRadioButton.getContext(), R.drawable.abc_btn_radio_material));
    DrawableCompat.setTintList(localDrawable, paramColorStateList);
    paramRadioButton.setButtonDrawable(localDrawable);
  }

  public static void setTint(@NonNull SeekBar paramSeekBar, @ColorInt int paramInt)
  {
    ColorStateList localColorStateList = ColorStateList.valueOf(paramInt);
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramSeekBar.setThumbTintList(localColorStateList);
      paramSeekBar.setProgressTintList(localColorStateList);
    }
    PorterDuff.Mode localMode;
    do
    {
      while (true)
      {
        return;
        if (Build.VERSION.SDK_INT <= 10)
          break;
        Drawable localDrawable1 = DrawableCompat.wrap(paramSeekBar.getProgressDrawable());
        paramSeekBar.setProgressDrawable(localDrawable1);
        DrawableCompat.setTintList(localDrawable1, localColorStateList);
        if (Build.VERSION.SDK_INT < 16)
          continue;
        Drawable localDrawable2 = DrawableCompat.wrap(paramSeekBar.getThumb());
        DrawableCompat.setTintList(localDrawable2, localColorStateList);
        paramSeekBar.setThumb(localDrawable2);
        return;
      }
      localMode = PorterDuff.Mode.SRC_IN;
      if (Build.VERSION.SDK_INT <= 10)
        localMode = PorterDuff.Mode.MULTIPLY;
      if (paramSeekBar.getIndeterminateDrawable() == null)
        continue;
      paramSeekBar.getIndeterminateDrawable().setColorFilter(paramInt, localMode);
    }
    while (paramSeekBar.getProgressDrawable() == null);
    paramSeekBar.getProgressDrawable().setColorFilter(paramInt, localMode);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.internal.MDTintHelper
 * JD-Core Version:    0.6.0
 */