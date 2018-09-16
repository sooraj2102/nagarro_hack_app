package android.support.v4.graphics;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

class PaintCompatApi14
{
  private static final String EM_STRING = "m";
  private static final String TOFU_STRING = "í¬¿í¿½";
  private static final ThreadLocal<Pair<Rect, Rect>> sRectThreadLocal = new ThreadLocal();

  static boolean hasGlyph(@NonNull Paint paramPaint, @NonNull String paramString)
  {
    int i = paramString.length();
    int j;
    if ((i == 1) && (Character.isWhitespace(paramString.charAt(0))))
      j = 1;
    float f1;
    float f3;
    boolean bool3;
    do
    {
      boolean bool2;
      do
      {
        float f2;
        boolean bool1;
        do
        {
          return j;
          f1 = paramPaint.measureText("í¬¿í¿½");
          f2 = paramPaint.measureText("m");
          f3 = paramPaint.measureText(paramString);
          bool1 = f3 < 0.0F;
          j = 0;
        }
        while (!bool1);
        if (paramString.codePointCount(0, paramString.length()) <= 1)
          break;
        bool2 = f3 < 2.0F * f2;
        j = 0;
      }
      while (bool2);
      float f4 = 0.0F;
      int m = 0;
      while (m < i)
      {
        int n = Character.charCount(paramString.codePointAt(m));
        f4 += paramPaint.measureText(paramString, m, m + n);
        m += n;
      }
      bool3 = f3 < f4;
      j = 0;
    }
    while (!bool3);
    if (f3 != f1)
      return true;
    Pair localPair = obtainEmptyRects();
    paramPaint.getTextBounds("í¬¿í¿½", 0, "í¬¿í¿½".length(), (Rect)localPair.first);
    paramPaint.getTextBounds(paramString, 0, i, (Rect)localPair.second);
    if (!((Rect)localPair.first).equals(localPair.second));
    for (int k = 1; ; k = 0)
      return k;
  }

  private static Pair<Rect, Rect> obtainEmptyRects()
  {
    Pair localPair1 = (Pair)sRectThreadLocal.get();
    if (localPair1 == null)
    {
      Pair localPair2 = new Pair(new Rect(), new Rect());
      sRectThreadLocal.set(localPair2);
      return localPair2;
    }
    ((Rect)localPair1.first).setEmpty();
    ((Rect)localPair1.second).setEmpty();
    return localPair1;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.graphics.PaintCompatApi14
 * JD-Core Version:    0.6.0
 */