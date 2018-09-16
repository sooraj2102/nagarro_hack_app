package android.support.v7.util;

public abstract interface ListUpdateCallback
{
  public abstract void onChanged(int paramInt1, int paramInt2, Object paramObject);

  public abstract void onInserted(int paramInt1, int paramInt2);

  public abstract void onMoved(int paramInt1, int paramInt2);

  public abstract void onRemoved(int paramInt1, int paramInt2);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.util.ListUpdateCallback
 * JD-Core Version:    0.6.0
 */