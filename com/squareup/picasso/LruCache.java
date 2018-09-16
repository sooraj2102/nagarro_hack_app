package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class LruCache
  implements Cache
{
  private int evictionCount;
  private int hitCount;
  final LinkedHashMap<String, Bitmap> map;
  private final int maxSize;
  private int missCount;
  private int putCount;
  private int size;

  public LruCache(int paramInt)
  {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Max size must be positive.");
    this.maxSize = paramInt;
    this.map = new LinkedHashMap(0, 0.75F, true);
  }

  public LruCache(Context paramContext)
  {
    this(Utils.calculateMemoryCacheSize(paramContext));
  }

  // ERROR //
  private void trimToSize(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 50	com/squareup/picasso/LruCache:size	I
    //   6: iflt +20 -> 26
    //   9: aload_0
    //   10: getfield 38	com/squareup/picasso/LruCache:map	Ljava/util/LinkedHashMap;
    //   13: invokevirtual 54	java/util/LinkedHashMap:isEmpty	()Z
    //   16: ifeq +48 -> 64
    //   19: aload_0
    //   20: getfield 50	com/squareup/picasso/LruCache:size	I
    //   23: ifeq +41 -> 64
    //   26: new 56	java/lang/IllegalStateException
    //   29: dup
    //   30: new 58	java/lang/StringBuilder
    //   33: dup
    //   34: invokespecial 59	java/lang/StringBuilder:<init>	()V
    //   37: aload_0
    //   38: invokevirtual 63	java/lang/Object:getClass	()Ljava/lang/Class;
    //   41: invokevirtual 69	java/lang/Class:getName	()Ljava/lang/String;
    //   44: invokevirtual 73	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: ldc 75
    //   49: invokevirtual 73	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   52: invokevirtual 78	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokespecial 79	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   58: athrow
    //   59: astore_2
    //   60: aload_0
    //   61: monitorexit
    //   62: aload_2
    //   63: athrow
    //   64: aload_0
    //   65: getfield 50	com/squareup/picasso/LruCache:size	I
    //   68: iload_1
    //   69: if_icmple +13 -> 82
    //   72: aload_0
    //   73: getfield 38	com/squareup/picasso/LruCache:map	Ljava/util/LinkedHashMap;
    //   76: invokevirtual 54	java/util/LinkedHashMap:isEmpty	()Z
    //   79: ifeq +6 -> 85
    //   82: aload_0
    //   83: monitorexit
    //   84: return
    //   85: aload_0
    //   86: getfield 38	com/squareup/picasso/LruCache:map	Ljava/util/LinkedHashMap;
    //   89: invokevirtual 83	java/util/LinkedHashMap:entrySet	()Ljava/util/Set;
    //   92: invokeinterface 89 1 0
    //   97: invokeinterface 95 1 0
    //   102: checkcast 97	java/util/Map$Entry
    //   105: astore_3
    //   106: aload_3
    //   107: invokeinterface 100 1 0
    //   112: checkcast 102	java/lang/String
    //   115: astore 4
    //   117: aload_3
    //   118: invokeinterface 105 1 0
    //   123: checkcast 107	android/graphics/Bitmap
    //   126: astore 5
    //   128: aload_0
    //   129: getfield 38	com/squareup/picasso/LruCache:map	Ljava/util/LinkedHashMap;
    //   132: aload 4
    //   134: invokevirtual 111	java/util/LinkedHashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   137: pop
    //   138: aload_0
    //   139: aload_0
    //   140: getfield 50	com/squareup/picasso/LruCache:size	I
    //   143: aload 5
    //   145: invokestatic 115	com/squareup/picasso/Utils:getBitmapBytes	(Landroid/graphics/Bitmap;)I
    //   148: isub
    //   149: putfield 50	com/squareup/picasso/LruCache:size	I
    //   152: aload_0
    //   153: iconst_1
    //   154: aload_0
    //   155: getfield 117	com/squareup/picasso/LruCache:evictionCount	I
    //   158: iadd
    //   159: putfield 117	com/squareup/picasso/LruCache:evictionCount	I
    //   162: aload_0
    //   163: monitorexit
    //   164: goto -164 -> 0
    //
    // Exception table:
    //   from	to	target	type
    //   2	26	59	finally
    //   26	59	59	finally
    //   60	62	59	finally
    //   64	82	59	finally
    //   82	84	59	finally
    //   85	164	59	finally
  }

  public final void clear()
  {
    monitorenter;
    try
    {
      evictAll();
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public final void clearKeyUri(String paramString)
  {
    monitorenter;
    int i = 0;
    try
    {
      int j = paramString.length();
      Iterator localIterator = this.map.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str = (String)localEntry.getKey();
        Bitmap localBitmap = (Bitmap)localEntry.getValue();
        int k = str.indexOf('\n');
        if ((k != j) || (!str.substring(0, k).equals(paramString)))
          continue;
        localIterator.remove();
        this.size -= Utils.getBitmapBytes(localBitmap);
        i = 1;
      }
      if (i != 0)
        trimToSize(this.maxSize);
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public final void evictAll()
  {
    trimToSize(-1);
  }

  public final int evictionCount()
  {
    monitorenter;
    try
    {
      int i = this.evictionCount;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public Bitmap get(String paramString)
  {
    if (paramString == null)
      throw new NullPointerException("key == null");
    monitorenter;
    try
    {
      Bitmap localBitmap = (Bitmap)this.map.get(paramString);
      if (localBitmap != null)
      {
        this.hitCount = (1 + this.hitCount);
        return localBitmap;
      }
      this.missCount = (1 + this.missCount);
      return null;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public final int hitCount()
  {
    monitorenter;
    try
    {
      int i = this.hitCount;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public final int maxSize()
  {
    monitorenter;
    try
    {
      int i = this.maxSize;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public final int missCount()
  {
    monitorenter;
    try
    {
      int i = this.missCount;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public final int putCount()
  {
    monitorenter;
    try
    {
      int i = this.putCount;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public void set(String paramString, Bitmap paramBitmap)
  {
    if ((paramString == null) || (paramBitmap == null))
      throw new NullPointerException("key == null || bitmap == null");
    monitorenter;
    try
    {
      this.putCount = (1 + this.putCount);
      this.size += Utils.getBitmapBytes(paramBitmap);
      Bitmap localBitmap = (Bitmap)this.map.put(paramString, paramBitmap);
      if (localBitmap != null)
        this.size -= Utils.getBitmapBytes(localBitmap);
      monitorexit;
      trimToSize(this.maxSize);
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public final int size()
  {
    monitorenter;
    try
    {
      int i = this.size;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.LruCache
 * JD-Core Version:    0.6.0
 */