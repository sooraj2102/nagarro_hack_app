package okio;

final class SegmentPool
{
  static final long MAX_SIZE = 65536L;
  static long byteCount;
  static Segment next;

  static void recycle(Segment paramSegment)
  {
    if ((paramSegment.next != null) || (paramSegment.prev != null))
      throw new IllegalArgumentException();
    if (paramSegment.shared)
      return;
    monitorenter;
    try
    {
      if (8192L + byteCount > 65536L)
        return;
    }
    finally
    {
      monitorexit;
    }
    byteCount = 8192L + byteCount;
    paramSegment.next = next;
    paramSegment.limit = 0;
    paramSegment.pos = 0;
    next = paramSegment;
    monitorexit;
  }

  static Segment take()
  {
    monitorenter;
    try
    {
      if (next != null)
      {
        Segment localSegment = next;
        next = localSegment.next;
        localSegment.next = null;
        byteCount -= 8192L;
        return localSegment;
      }
      return new Segment();
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okio.SegmentPool
 * JD-Core Version:    0.6.0
 */