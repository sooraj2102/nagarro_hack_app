package com.squareup.picasso;

public enum MemoryPolicy
{
  final int index;

  static
  {
    MemoryPolicy[] arrayOfMemoryPolicy = new MemoryPolicy[2];
    arrayOfMemoryPolicy[0] = NO_CACHE;
    arrayOfMemoryPolicy[1] = NO_STORE;
    $VALUES = arrayOfMemoryPolicy;
  }

  private MemoryPolicy(int paramInt)
  {
    this.index = paramInt;
  }

  static boolean shouldReadFromMemoryCache(int paramInt)
  {
    return (paramInt & NO_CACHE.index) == 0;
  }

  static boolean shouldWriteToMemoryCache(int paramInt)
  {
    return (paramInt & NO_STORE.index) == 0;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.MemoryPolicy
 * JD-Core Version:    0.6.0
 */