package com.squareup.picasso;

import android.graphics.Bitmap;

public abstract interface Cache
{
  public static final Cache NONE = new Cache()
  {
    public void clear()
    {
    }

    public void clearKeyUri(String paramString)
    {
    }

    public Bitmap get(String paramString)
    {
      return null;
    }

    public int maxSize()
    {
      return 0;
    }

    public void set(String paramString, Bitmap paramBitmap)
    {
    }

    public int size()
    {
      return 0;
    }
  };

  public abstract void clear();

  public abstract void clearKeyUri(String paramString);

  public abstract Bitmap get(String paramString);

  public abstract int maxSize();

  public abstract void set(String paramString, Bitmap paramBitmap);

  public abstract int size();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.Cache
 * JD-Core Version:    0.6.0
 */