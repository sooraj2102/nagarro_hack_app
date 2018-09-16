package rx.internal.util;

import java.util.Arrays;
import rx.functions.Action1;
import rx.internal.util.unsafe.Pow2;

public final class OpenHashSet<T>
{
  private static final int INT_PHI = -1640531527;
  T[] keys;
  final float loadFactor;
  int mask;
  int maxSize;
  int size;

  public OpenHashSet()
  {
    this(16, 0.75F);
  }

  public OpenHashSet(int paramInt)
  {
    this(paramInt, 0.75F);
  }

  public OpenHashSet(int paramInt, float paramFloat)
  {
    this.loadFactor = paramFloat;
    int i = Pow2.roundToPowerOfTwo(paramInt);
    this.mask = (i - 1);
    this.maxSize = (int)(paramFloat * i);
    this.keys = ((Object[])new Object[i]);
  }

  static int mix(int paramInt)
  {
    int i = paramInt * -1640531527;
    return i ^ i >>> 16;
  }

  public boolean add(T paramT)
  {
    Object[] arrayOfObject = this.keys;
    int i = this.mask;
    int j = i & mix(paramT.hashCode());
    Object localObject1 = arrayOfObject[j];
    if (localObject1 != null)
      if (localObject1.equals(paramT))
        return false;
    Object localObject2;
    do
    {
      j = i & j + 1;
      localObject2 = arrayOfObject[j];
      if (localObject2 != null)
        continue;
      arrayOfObject[j] = paramT;
      int k = 1 + this.size;
      this.size = k;
      if (k >= this.maxSize)
        rehash();
      return true;
    }
    while (!localObject2.equals(paramT));
    return false;
  }

  public void clear(Action1<? super T> paramAction1)
  {
    if (this.size == 0)
      return;
    for (Object localObject : this.keys)
    {
      if (localObject == null)
        continue;
      paramAction1.call(localObject);
    }
    Arrays.fill(???, null);
    this.size = 0;
  }

  public boolean isEmpty()
  {
    return this.size == 0;
  }

  void rehash()
  {
    Object[] arrayOfObject1 = this.keys;
    int i = arrayOfObject1.length;
    int j = i << 1;
    int k = j - 1;
    Object[] arrayOfObject2 = (Object[])new Object[j];
    int n;
    for (int m = this.size; ; m = n)
    {
      n = m - 1;
      if (m == 0)
        break;
      do
        i--;
      while (arrayOfObject1[i] == null);
      int i1 = k & mix(arrayOfObject1[i].hashCode());
      if (arrayOfObject2[i1] != null)
        do
          i1 = k & i1 + 1;
        while (arrayOfObject2[i1] != null);
      arrayOfObject2[i1] = arrayOfObject1[i];
    }
    this.mask = k;
    this.maxSize = (int)(j * this.loadFactor);
    this.keys = arrayOfObject2;
  }

  public boolean remove(T paramT)
  {
    Object[] arrayOfObject = this.keys;
    int i = this.mask;
    int j = i & mix(paramT.hashCode());
    Object localObject1 = arrayOfObject[j];
    if (localObject1 == null)
      return false;
    if (localObject1.equals(paramT))
      return removeEntry(j, arrayOfObject, i);
    Object localObject2;
    do
    {
      j = i & j + 1;
      localObject2 = arrayOfObject[j];
      if (localObject2 == null)
        break;
    }
    while (!localObject2.equals(paramT));
    return removeEntry(j, arrayOfObject, i);
  }

  boolean removeEntry(int paramInt1, T[] paramArrayOfT, int paramInt2)
  {
    this.size = (-1 + this.size);
    int i = paramInt1;
    for (paramInt1 = paramInt2 & paramInt1 + 1; ; paramInt1 = paramInt2 & paramInt1 + 1)
    {
      T ? = paramArrayOfT[paramInt1];
      if (? == null)
      {
        paramArrayOfT[i] = null;
        return true;
      }
      int j = paramInt2 & mix(?.hashCode());
      if (i <= paramInt1)
        if ((i < j) && (j <= paramInt1))
          continue;
      do
      {
        paramArrayOfT[i] = ?;
        break;
      }
      while ((i >= j) && (j > paramInt1));
    }
  }

  public void terminate()
  {
    this.size = 0;
    this.keys = ((Object[])new Object[0]);
  }

  public T[] values()
  {
    return this.keys;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.OpenHashSet
 * JD-Core Version:    0.6.0
 */