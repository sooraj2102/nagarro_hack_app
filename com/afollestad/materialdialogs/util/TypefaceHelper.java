package com.afollestad.materialdialogs.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.SimpleArrayMap;

public class TypefaceHelper
{
  private static final SimpleArrayMap<String, Typeface> cache = new SimpleArrayMap();

  public static Typeface get(Context paramContext, String paramString)
  {
    synchronized (cache)
    {
      boolean bool = cache.containsKey(paramString);
      if (!bool)
        try
        {
          Typeface localTypeface2 = Typeface.createFromAsset(paramContext.getAssets(), String.format("fonts/%s", new Object[] { paramString }));
          cache.put(paramString, localTypeface2);
          return localTypeface2;
        }
        catch (RuntimeException localRuntimeException)
        {
          return null;
        }
    }
    Typeface localTypeface1 = (Typeface)cache.get(paramString);
    monitorexit;
    return localTypeface1;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.util.TypefaceHelper
 * JD-Core Version:    0.6.0
 */