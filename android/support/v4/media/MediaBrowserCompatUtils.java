package android.support.v4.media;

import android.os.Bundle;
import android.support.annotation.RestrictTo;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class MediaBrowserCompatUtils
{
  public static boolean areSameOptions(Bundle paramBundle1, Bundle paramBundle2)
  {
    if (paramBundle1 == paramBundle2);
    do
      while (true)
      {
        return true;
        if (paramBundle1 == null)
          if ((paramBundle2.getInt("android.media.browse.extra.PAGE", -1) != -1) || (paramBundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1) != -1))
            return false;
        if (paramBundle2 != null)
          break;
        if ((paramBundle1.getInt("android.media.browse.extra.PAGE", -1) != -1) || (paramBundle1.getInt("android.media.browse.extra.PAGE_SIZE", -1) != -1))
          return false;
      }
    while ((paramBundle1.getInt("android.media.browse.extra.PAGE", -1) == paramBundle2.getInt("android.media.browse.extra.PAGE", -1)) && (paramBundle1.getInt("android.media.browse.extra.PAGE_SIZE", -1) == paramBundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1)));
    return false;
  }

  public static boolean hasDuplicatedItems(Bundle paramBundle1, Bundle paramBundle2)
  {
    int i;
    int j;
    label12: int k;
    label19: int m;
    label26: int n;
    int i1;
    label44: int i2;
    int i3;
    if (paramBundle1 == null)
    {
      i = -1;
      if (paramBundle2 != null)
        break label89;
      j = -1;
      if (paramBundle1 != null)
        break label100;
      k = -1;
      if (paramBundle2 != null)
        break label112;
      m = -1;
      if ((i != -1) && (k != -1))
        break label124;
      n = 0;
      i1 = 2147483647;
      if ((j != -1) && (m != -1))
        break label142;
      i2 = 0;
      i3 = 2147483647;
      label62: if ((n > i2) || (i2 > i1))
        break label160;
    }
    label89: label100: label112: label124: 
    do
    {
      return true;
      i = paramBundle1.getInt("android.media.browse.extra.PAGE", -1);
      break;
      j = paramBundle2.getInt("android.media.browse.extra.PAGE", -1);
      break label12;
      k = paramBundle1.getInt("android.media.browse.extra.PAGE_SIZE", -1);
      break label19;
      m = paramBundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1);
      break label26;
      n = k * i;
      i1 = -1 + (n + k);
      break label44;
      i2 = m * j;
      i3 = -1 + (i2 + m);
      break label62;
    }
    while ((n <= i3) && (i3 <= i1));
    label142: label160: return false;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.media.MediaBrowserCompatUtils
 * JD-Core Version:    0.6.0
 */