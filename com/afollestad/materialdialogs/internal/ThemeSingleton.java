package com.afollestad.materialdialogs.internal;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import com.afollestad.materialdialogs.GravityEnum;

public class ThemeSingleton
{
  private static ThemeSingleton singleton;

  @ColorInt
  public int backgroundColor = 0;

  @DrawableRes
  public int btnSelectorNegative = 0;

  @DrawableRes
  public int btnSelectorNeutral = 0;

  @DrawableRes
  public int btnSelectorPositive = 0;

  @DrawableRes
  public int btnSelectorStacked = 0;
  public GravityEnum btnStackedGravity = GravityEnum.END;
  public GravityEnum buttonsGravity = GravityEnum.START;

  @ColorInt
  public int contentColor = 0;
  public GravityEnum contentGravity = GravityEnum.START;
  public boolean darkTheme = false;

  @ColorInt
  public int dividerColor = 0;
  public Drawable icon = null;

  @ColorInt
  public int itemColor = 0;
  public GravityEnum itemsGravity = GravityEnum.START;
  public ColorStateList linkColor = null;

  @DrawableRes
  public int listSelector = 0;
  public ColorStateList negativeColor = null;
  public ColorStateList neutralColor = null;
  public ColorStateList positiveColor = null;

  @ColorInt
  public int titleColor = 0;
  public GravityEnum titleGravity = GravityEnum.START;

  @ColorInt
  public int widgetColor = 0;

  public static ThemeSingleton get()
  {
    return get(true);
  }

  public static ThemeSingleton get(boolean paramBoolean)
  {
    if ((singleton == null) && (paramBoolean))
      singleton = new ThemeSingleton();
    return singleton;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.internal.ThemeSingleton
 * JD-Core Version:    0.6.0
 */