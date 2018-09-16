package com.afollestad.materialdialogs.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;

public class DialogUtils
{
  @ColorInt
  public static int adjustAlpha(@ColorInt int paramInt, float paramFloat)
  {
    return Color.argb(Math.round(paramFloat * Color.alpha(paramInt)), Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt));
  }

  public static ColorStateList getActionTextColorStateList(Context paramContext, @ColorRes int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    paramContext.getResources().getValue(paramInt, localTypedValue, true);
    if ((localTypedValue.type >= 28) && (localTypedValue.type <= 31))
      return getActionTextStateList(paramContext, localTypedValue.data);
    if (Build.VERSION.SDK_INT <= 22)
      return paramContext.getResources().getColorStateList(paramInt);
    return paramContext.getColorStateList(paramInt);
  }

  public static ColorStateList getActionTextStateList(Context paramContext, int paramInt)
  {
    int i = resolveColor(paramContext, 16842806);
    if (paramInt == 0)
      paramInt = i;
    int[][] arrayOfInt = { { -16842910 }, new int[0] };
    int[] arrayOfInt1 = new int[2];
    arrayOfInt1[0] = adjustAlpha(paramInt, 0.4F);
    arrayOfInt1[1] = paramInt;
    return new ColorStateList(arrayOfInt, arrayOfInt1);
  }

  @ColorInt
  public static int getColor(Context paramContext, @ColorRes int paramInt)
  {
    return ContextCompat.getColor(paramContext, paramInt);
  }

  public static int[] getColorArray(@NonNull Context paramContext, @ArrayRes int paramInt)
  {
    if (paramInt == 0)
      return null;
    TypedArray localTypedArray = paramContext.getResources().obtainTypedArray(paramInt);
    int[] arrayOfInt = new int[localTypedArray.length()];
    for (int i = 0; i < localTypedArray.length(); i++)
      arrayOfInt[i] = localTypedArray.getColor(i, 0);
    localTypedArray.recycle();
    return arrayOfInt;
  }

  @ColorInt
  public static int getDisabledColor(Context paramContext)
  {
    if (isColorDark(resolveColor(paramContext, 16842806)));
    for (int i = -16777216; ; i = -1)
      return adjustAlpha(i, 0.3F);
  }

  private static int gravityEnumToAttrInt(GravityEnum paramGravityEnum)
  {
    switch (2.$SwitchMap$com$afollestad$materialdialogs$GravityEnum[paramGravityEnum.ordinal()])
    {
    default:
      return 0;
    case 1:
      return 1;
    case 2:
    }
    return 2;
  }

  public static void hideKeyboard(@NonNull DialogInterface paramDialogInterface, @NonNull MaterialDialog.Builder paramBuilder)
  {
    MaterialDialog localMaterialDialog = (MaterialDialog)paramDialogInterface;
    if (localMaterialDialog.getInputEditText() == null);
    while (true)
    {
      return;
      InputMethodManager localInputMethodManager = (InputMethodManager)paramBuilder.getContext().getSystemService("input_method");
      if (localInputMethodManager == null)
        continue;
      View localView = localMaterialDialog.getCurrentFocus();
      if (localView != null);
      for (IBinder localIBinder = localView.getWindowToken(); localIBinder != null; localIBinder = localMaterialDialog.getView().getWindowToken())
      {
        localInputMethodManager.hideSoftInputFromWindow(localIBinder, 0);
        return;
      }
    }
  }

  public static boolean isColorDark(@ColorInt int paramInt)
  {
    return 1.0D - (0.299D * Color.red(paramInt) + 0.587D * Color.green(paramInt) + 0.114D * Color.blue(paramInt)) / 255.0D >= 0.5D;
  }

  public static <T> boolean isIn(@NonNull T paramT, @Nullable T[] paramArrayOfT)
  {
    if ((paramArrayOfT == null) || (paramArrayOfT.length == 0));
    while (true)
    {
      return false;
      int i = paramArrayOfT.length;
      for (int j = 0; j < i; j++)
        if (paramArrayOfT[j].equals(paramT))
          return true;
    }
  }

  public static ColorStateList resolveActionTextColorStateList(Context paramContext, @AttrRes int paramInt, ColorStateList paramColorStateList)
  {
    TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(new int[] { paramInt });
    try
    {
      TypedValue localTypedValue = localTypedArray.peekValue(0);
      if (localTypedValue == null)
        return paramColorStateList;
      if ((localTypedValue.type >= 28) && (localTypedValue.type <= 31))
      {
        ColorStateList localColorStateList2 = getActionTextStateList(paramContext, localTypedValue.data);
        return localColorStateList2;
      }
      ColorStateList localColorStateList1 = localTypedArray.getColorStateList(0);
      if (localColorStateList1 != null)
        return localColorStateList1;
      return paramColorStateList;
    }
    finally
    {
      localTypedArray.recycle();
    }
    throw localObject;
  }

  public static boolean resolveBoolean(Context paramContext, @AttrRes int paramInt)
  {
    return resolveBoolean(paramContext, paramInt, false);
  }

  public static boolean resolveBoolean(Context paramContext, @AttrRes int paramInt, boolean paramBoolean)
  {
    TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(new int[] { paramInt });
    try
    {
      boolean bool = localTypedArray.getBoolean(0, paramBoolean);
      return bool;
    }
    finally
    {
      localTypedArray.recycle();
    }
    throw localObject;
  }

  @ColorInt
  public static int resolveColor(Context paramContext, @AttrRes int paramInt)
  {
    return resolveColor(paramContext, paramInt, 0);
  }

  @ColorInt
  public static int resolveColor(Context paramContext, @AttrRes int paramInt1, int paramInt2)
  {
    TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(new int[] { paramInt1 });
    try
    {
      int i = localTypedArray.getColor(0, paramInt2);
      return i;
    }
    finally
    {
      localTypedArray.recycle();
    }
    throw localObject;
  }

  public static int resolveDimension(Context paramContext, @AttrRes int paramInt)
  {
    return resolveDimension(paramContext, paramInt, -1);
  }

  private static int resolveDimension(Context paramContext, @AttrRes int paramInt1, int paramInt2)
  {
    TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(new int[] { paramInt1 });
    try
    {
      int i = localTypedArray.getDimensionPixelSize(0, paramInt2);
      return i;
    }
    finally
    {
      localTypedArray.recycle();
    }
    throw localObject;
  }

  public static Drawable resolveDrawable(Context paramContext, @AttrRes int paramInt)
  {
    return resolveDrawable(paramContext, paramInt, null);
  }

  private static Drawable resolveDrawable(Context paramContext, @AttrRes int paramInt, Drawable paramDrawable)
  {
    TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(new int[] { paramInt });
    try
    {
      Drawable localDrawable1 = localTypedArray.getDrawable(0);
      Drawable localDrawable2 = localDrawable1;
      if ((localDrawable2 == null) && (paramDrawable != null))
        localDrawable2 = paramDrawable;
      return localDrawable2;
    }
    finally
    {
      localTypedArray.recycle();
    }
    throw localObject;
  }

  public static GravityEnum resolveGravityEnum(Context paramContext, @AttrRes int paramInt, GravityEnum paramGravityEnum)
  {
    TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(new int[] { paramInt });
    try
    {
      switch (localTypedArray.getInt(0, gravityEnumToAttrInt(paramGravityEnum)))
      {
      default:
        GravityEnum localGravityEnum3 = GravityEnum.START;
        return localGravityEnum3;
      case 1:
        GravityEnum localGravityEnum2 = GravityEnum.CENTER;
        return localGravityEnum2;
      case 2:
      }
      GravityEnum localGravityEnum1 = GravityEnum.END;
      return localGravityEnum1;
    }
    finally
    {
      localTypedArray.recycle();
    }
    throw localObject;
  }

  public static String resolveString(Context paramContext, @AttrRes int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(paramInt, localTypedValue, true);
    return (String)localTypedValue.string;
  }

  public static void setBackgroundCompat(View paramView, Drawable paramDrawable)
  {
    if (Build.VERSION.SDK_INT < 16)
    {
      paramView.setBackgroundDrawable(paramDrawable);
      return;
    }
    paramView.setBackground(paramDrawable);
  }

  public static void showKeyboard(@NonNull DialogInterface paramDialogInterface, @NonNull MaterialDialog.Builder paramBuilder)
  {
    MaterialDialog localMaterialDialog = (MaterialDialog)paramDialogInterface;
    if (localMaterialDialog.getInputEditText() == null)
      return;
    localMaterialDialog.getInputEditText().post(new Runnable(localMaterialDialog, paramBuilder)
    {
      public void run()
      {
        this.val$dialog.getInputEditText().requestFocus();
        InputMethodManager localInputMethodManager = (InputMethodManager)this.val$builder.getContext().getSystemService("input_method");
        if (localInputMethodManager != null)
          localInputMethodManager.showSoftInput(this.val$dialog.getInputEditText(), 1);
      }
    });
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.util.DialogUtils
 * JD-Core Version:    0.6.0
 */