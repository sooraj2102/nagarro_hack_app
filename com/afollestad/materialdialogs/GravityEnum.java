package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build.VERSION;

public enum GravityEnum
{
  private static final boolean HAS_RTL;

  static
  {
    int i = 1;
    START = new GravityEnum("START", 0);
    CENTER = new GravityEnum("CENTER", i);
    END = new GravityEnum("END", 2);
    GravityEnum[] arrayOfGravityEnum = new GravityEnum[3];
    arrayOfGravityEnum[0] = START;
    arrayOfGravityEnum[i] = CENTER;
    arrayOfGravityEnum[2] = END;
    $VALUES = arrayOfGravityEnum;
    if (Build.VERSION.SDK_INT >= 17);
    while (true)
    {
      HAS_RTL = i;
      return;
      i = 0;
    }
  }

  @SuppressLint({"RtlHardcoded"})
  public int getGravityInt()
  {
    switch (1.$SwitchMap$com$afollestad$materialdialogs$GravityEnum[ordinal()])
    {
    default:
      throw new IllegalStateException("Invalid gravity constant");
    case 1:
      if (HAS_RTL)
        return 8388611;
      return 3;
    case 2:
      return 1;
    case 3:
    }
    if (HAS_RTL)
      return 8388613;
    return 5;
  }

  @TargetApi(17)
  public int getTextAlignment()
  {
    switch (1.$SwitchMap$com$afollestad$materialdialogs$GravityEnum[ordinal()])
    {
    default:
      return 5;
    case 2:
      return 4;
    case 3:
    }
    return 6;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.GravityEnum
 * JD-Core Version:    0.6.0
 */