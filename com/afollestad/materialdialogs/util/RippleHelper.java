package com.afollestad.materialdialogs.util;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.support.annotation.ColorInt;

@TargetApi(21)
public class RippleHelper
{
  public static void applyColor(Drawable paramDrawable, @ColorInt int paramInt)
  {
    if ((paramDrawable instanceof RippleDrawable))
      ((RippleDrawable)paramDrawable).setColor(ColorStateList.valueOf(paramInt));
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.util.RippleHelper
 * JD-Core Version:    0.6.0
 */