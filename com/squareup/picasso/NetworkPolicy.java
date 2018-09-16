package com.squareup.picasso;

public enum NetworkPolicy
{
  final int index;

  static
  {
    NetworkPolicy[] arrayOfNetworkPolicy = new NetworkPolicy[3];
    arrayOfNetworkPolicy[0] = NO_CACHE;
    arrayOfNetworkPolicy[1] = NO_STORE;
    arrayOfNetworkPolicy[2] = OFFLINE;
    $VALUES = arrayOfNetworkPolicy;
  }

  private NetworkPolicy(int paramInt)
  {
    this.index = paramInt;
  }

  public static boolean isOfflineOnly(int paramInt)
  {
    return (paramInt & OFFLINE.index) != 0;
  }

  public static boolean shouldReadFromDiskCache(int paramInt)
  {
    return (paramInt & NO_CACHE.index) == 0;
  }

  public static boolean shouldWriteToDiskCache(int paramInt)
  {
    return (paramInt & NO_STORE.index) == 0;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.NetworkPolicy
 * JD-Core Version:    0.6.0
 */