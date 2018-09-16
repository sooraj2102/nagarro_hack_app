package android.support.design.widget;

import android.graphics.PorterDuff.Mode;

class ViewUtils
{
  static PorterDuff.Mode parseTintMode(int paramInt, PorterDuff.Mode paramMode)
  {
    switch (paramInt)
    {
    default:
      return paramMode;
    case 3:
      return PorterDuff.Mode.SRC_OVER;
    case 5:
      return PorterDuff.Mode.SRC_IN;
    case 9:
      return PorterDuff.Mode.SRC_ATOP;
    case 14:
      return PorterDuff.Mode.MULTIPLY;
    case 15:
    }
    return PorterDuff.Mode.SCREEN;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.design.widget.ViewUtils
 * JD-Core Version:    0.6.0
 */