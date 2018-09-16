package butterknife.internal;

import java.util.AbstractList;
import java.util.RandomAccess;

final class ImmutableList<T> extends AbstractList<T>
  implements RandomAccess
{
  private final T[] views;

  ImmutableList(T[] paramArrayOfT)
  {
    this.views = paramArrayOfT;
  }

  public boolean contains(Object paramObject)
  {
    Object[] arrayOfObject = this.views;
    int i = arrayOfObject.length;
    for (int j = 0; ; j++)
    {
      int k = 0;
      if (j < i)
      {
        if (arrayOfObject[j] != paramObject)
          continue;
        k = 1;
      }
      return k;
    }
  }

  public T get(int paramInt)
  {
    return this.views[paramInt];
  }

  public int size()
  {
    return this.views.length;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     butterknife.internal.ImmutableList
 * JD-Core Version:    0.6.0
 */