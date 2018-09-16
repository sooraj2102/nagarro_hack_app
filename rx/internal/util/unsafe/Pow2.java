package rx.internal.util.unsafe;

public final class Pow2
{
  private Pow2()
  {
    throw new IllegalStateException("No instances!");
  }

  public static boolean isPowerOfTwo(int paramInt)
  {
    return (paramInt & paramInt - 1) == 0;
  }

  public static int roundToPowerOfTwo(int paramInt)
  {
    return 1 << 32 - Integer.numberOfLeadingZeros(paramInt - 1);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.Pow2
 * JD-Core Version:    0.6.0
 */