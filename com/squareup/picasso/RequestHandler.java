package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.net.NetworkInfo;
import java.io.IOException;
import java.io.InputStream;

public abstract class RequestHandler
{
  static void calculateInSampleSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BitmapFactory.Options paramOptions, Request paramRequest)
  {
    int i = 1;
    if ((paramInt4 > paramInt2) || (paramInt3 > paramInt1))
      if (paramInt2 != 0)
        break label43;
    for (i = (int)Math.floor(paramInt3 / paramInt1); ; i = (int)Math.floor(paramInt4 / paramInt2))
    {
      paramOptions.inSampleSize = i;
      paramOptions.inJustDecodeBounds = false;
      return;
      label43: if (paramInt1 != 0)
        break;
    }
    int j = (int)Math.floor(paramInt4 / paramInt2);
    int k = (int)Math.floor(paramInt3 / paramInt1);
    if (paramRequest.centerInside);
    for (i = Math.max(j, k); ; i = Math.min(j, k))
      break;
  }

  static void calculateInSampleSize(int paramInt1, int paramInt2, BitmapFactory.Options paramOptions, Request paramRequest)
  {
    calculateInSampleSize(paramInt1, paramInt2, paramOptions.outWidth, paramOptions.outHeight, paramOptions, paramRequest);
  }

  static BitmapFactory.Options createBitmapOptions(Request paramRequest)
  {
    boolean bool = paramRequest.hasSize();
    if (paramRequest.config != null);
    for (int i = 1; ; i = 0)
    {
      BitmapFactory.Options localOptions;
      if (!bool)
      {
        localOptions = null;
        if (i == 0);
      }
      else
      {
        localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = bool;
        if (i != 0)
          localOptions.inPreferredConfig = paramRequest.config;
      }
      return localOptions;
    }
  }

  static boolean requiresInSampleSize(BitmapFactory.Options paramOptions)
  {
    return (paramOptions != null) && (paramOptions.inJustDecodeBounds);
  }

  public abstract boolean canHandleRequest(Request paramRequest);

  int getRetryCount()
  {
    return 0;
  }

  public abstract Result load(Request paramRequest, int paramInt)
    throws IOException;

  boolean shouldRetry(boolean paramBoolean, NetworkInfo paramNetworkInfo)
  {
    return false;
  }

  boolean supportsReplay()
  {
    return false;
  }

  public static final class Result
  {
    private final Bitmap bitmap;
    private final int exifOrientation;
    private final Picasso.LoadedFrom loadedFrom;
    private final InputStream stream;

    public Result(Bitmap paramBitmap, Picasso.LoadedFrom paramLoadedFrom)
    {
      this((Bitmap)Utils.checkNotNull(paramBitmap, "bitmap == null"), null, paramLoadedFrom, 0);
    }

    Result(Bitmap paramBitmap, InputStream paramInputStream, Picasso.LoadedFrom paramLoadedFrom, int paramInt)
    {
      int j;
      if (paramBitmap != null)
      {
        j = i;
        if (paramInputStream == null)
          break label41;
      }
      while (true)
      {
        if ((i ^ j) != 0)
          break label47;
        throw new AssertionError();
        j = 0;
        break;
        label41: i = 0;
      }
      label47: this.bitmap = paramBitmap;
      this.stream = paramInputStream;
      this.loadedFrom = ((Picasso.LoadedFrom)Utils.checkNotNull(paramLoadedFrom, "loadedFrom == null"));
      this.exifOrientation = paramInt;
    }

    public Result(InputStream paramInputStream, Picasso.LoadedFrom paramLoadedFrom)
    {
      this(null, (InputStream)Utils.checkNotNull(paramInputStream, "stream == null"), paramLoadedFrom, 0);
    }

    public Bitmap getBitmap()
    {
      return this.bitmap;
    }

    int getExifOrientation()
    {
      return this.exifOrientation;
    }

    public Picasso.LoadedFrom getLoadedFrom()
    {
      return this.loadedFrom;
    }

    public InputStream getStream()
    {
      return this.stream;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.RequestHandler
 * JD-Core Version:    0.6.0
 */