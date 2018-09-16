package me.zhanghai.android.materialprogressbar.internal;

import android.content.Context;
import android.content.res.TypedArray;

public class ThemeUtils
{
  public static int getColorFromAttrRes(int paramInt1, int paramInt2, Context paramContext)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(new int[] { paramInt1 });
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

  public static float getFloatFromAttrRes(int paramInt, float paramFloat, Context paramContext)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(new int[] { paramInt });
    try
    {
      float f = localTypedArray.getFloat(0, paramFloat);
      return f;
    }
    finally
    {
      localTypedArray.recycle();
    }
    throw localObject;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     me.zhanghai.android.materialprogressbar.internal.ThemeUtils
 * JD-Core Version:    0.6.0
 */