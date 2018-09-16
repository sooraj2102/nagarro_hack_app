package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.appcompat.R.attr;

class ThemeUtils
{
  private static final int[] APPCOMPAT_CHECK_ATTRS;

  static
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = R.attr.colorPrimary;
    APPCOMPAT_CHECK_ATTRS = arrayOfInt;
  }

  static void checkAppCompatTheme(Context paramContext)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
    boolean bool = localTypedArray.hasValue(0);
    int i = 0;
    if (!bool)
      i = 1;
    localTypedArray.recycle();
    if (i != 0)
      throw new IllegalArgumentException("You need to use a Theme.AppCompat theme (or descendant) with the design library.");
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.design.widget.ThemeUtils
 * JD-Core Version:    0.6.0
 */