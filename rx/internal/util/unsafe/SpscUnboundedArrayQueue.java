package rx.internal.util.unsafe;

import java.util.Iterator;
import rx.internal.util.SuppressAnimalSniffer;
import sun.misc.Unsafe;

@SuppressAnimalSniffer
public class SpscUnboundedArrayQueue<E> extends SpscUnboundedArrayQueueConsumerField<E>
  implements QueueProgressIndicators
{
  private static final long C_INDEX_OFFSET;
  private static final Object HAS_NEXT;
  static final int MAX_LOOK_AHEAD_STEP = Integer.getInteger("jctools.spsc.max.lookahead.step", 4096).intValue();
  private static final long P_INDEX_OFFSET;
  private static final long REF_ARRAY_BASE;
  private static final int REF_ELEMENT_SHIFT;

  // ERROR //
  static
  {
    // Byte code:
    //   0: ldc 23
    //   2: sipush 4096
    //   5: invokestatic 29	java/lang/Integer:getInteger	(Ljava/lang/String;I)Ljava/lang/Integer;
    //   8: invokevirtual 33	java/lang/Integer:intValue	()I
    //   11: putstatic 35	rx/internal/util/unsafe/SpscUnboundedArrayQueue:MAX_LOOK_AHEAD_STEP	I
    //   14: new 37	java/lang/Object
    //   17: dup
    //   18: invokespecial 40	java/lang/Object:<init>	()V
    //   21: putstatic 42	rx/internal/util/unsafe/SpscUnboundedArrayQueue:HAS_NEXT	Ljava/lang/Object;
    //   24: getstatic 48	rx/internal/util/unsafe/UnsafeAccess:UNSAFE	Lsun/misc/Unsafe;
    //   27: ldc 50
    //   29: invokevirtual 56	sun/misc/Unsafe:arrayIndexScale	(Ljava/lang/Class;)I
    //   32: istore_0
    //   33: iconst_4
    //   34: iload_0
    //   35: if_icmpne +60 -> 95
    //   38: iconst_2
    //   39: putstatic 58	rx/internal/util/unsafe/SpscUnboundedArrayQueue:REF_ELEMENT_SHIFT	I
    //   42: getstatic 48	rx/internal/util/unsafe/UnsafeAccess:UNSAFE	Lsun/misc/Unsafe;
    //   45: ldc 50
    //   47: invokevirtual 61	sun/misc/Unsafe:arrayBaseOffset	(Ljava/lang/Class;)I
    //   50: i2l
    //   51: putstatic 63	rx/internal/util/unsafe/SpscUnboundedArrayQueue:REF_ARRAY_BASE	J
    //   54: ldc 65
    //   56: ldc 67
    //   58: invokevirtual 73	java/lang/Class:getDeclaredField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   61: astore 4
    //   63: getstatic 48	rx/internal/util/unsafe/UnsafeAccess:UNSAFE	Lsun/misc/Unsafe;
    //   66: aload 4
    //   68: invokevirtual 77	sun/misc/Unsafe:objectFieldOffset	(Ljava/lang/reflect/Field;)J
    //   71: putstatic 79	rx/internal/util/unsafe/SpscUnboundedArrayQueue:P_INDEX_OFFSET	J
    //   74: ldc 5
    //   76: ldc 81
    //   78: invokevirtual 73	java/lang/Class:getDeclaredField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   81: astore 8
    //   83: getstatic 48	rx/internal/util/unsafe/UnsafeAccess:UNSAFE	Lsun/misc/Unsafe;
    //   86: aload 8
    //   88: invokevirtual 77	sun/misc/Unsafe:objectFieldOffset	(Ljava/lang/reflect/Field;)J
    //   91: putstatic 83	rx/internal/util/unsafe/SpscUnboundedArrayQueue:C_INDEX_OFFSET	J
    //   94: return
    //   95: bipush 8
    //   97: iload_0
    //   98: if_icmpne +10 -> 108
    //   101: iconst_3
    //   102: putstatic 58	rx/internal/util/unsafe/SpscUnboundedArrayQueue:REF_ELEMENT_SHIFT	I
    //   105: goto -63 -> 42
    //   108: new 85	java/lang/IllegalStateException
    //   111: dup
    //   112: ldc 87
    //   114: invokespecial 90	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   117: athrow
    //   118: astore_1
    //   119: new 92	java/lang/InternalError
    //   122: dup
    //   123: invokespecial 93	java/lang/InternalError:<init>	()V
    //   126: astore_2
    //   127: aload_2
    //   128: aload_1
    //   129: invokevirtual 97	java/lang/InternalError:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   132: pop
    //   133: aload_2
    //   134: athrow
    //   135: astore 5
    //   137: new 92	java/lang/InternalError
    //   140: dup
    //   141: invokespecial 93	java/lang/InternalError:<init>	()V
    //   144: astore 6
    //   146: aload 6
    //   148: aload 5
    //   150: invokevirtual 97	java/lang/InternalError:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   153: pop
    //   154: aload 6
    //   156: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   54	74	118	java/lang/NoSuchFieldException
    //   74	94	135	java/lang/NoSuchFieldException
  }

  public SpscUnboundedArrayQueue(int paramInt)
  {
    int i = Pow2.roundToPowerOfTwo(paramInt);
    long l = i - 1;
    Object[] arrayOfObject = (Object[])new Object[i + 1];
    this.producerBuffer = arrayOfObject;
    this.producerMask = l;
    adjustLookAheadStep(i);
    this.consumerBuffer = arrayOfObject;
    this.consumerMask = l;
    this.producerLookAhead = (l - 1L);
    soProducerIndex(0L);
  }

  private void adjustLookAheadStep(int paramInt)
  {
    this.producerLookAheadStep = Math.min(paramInt / 4, MAX_LOOK_AHEAD_STEP);
  }

  private static long calcDirectOffset(long paramLong)
  {
    return REF_ARRAY_BASE + (paramLong << REF_ELEMENT_SHIFT);
  }

  private static long calcWrappedOffset(long paramLong1, long paramLong2)
  {
    return calcDirectOffset(paramLong1 & paramLong2);
  }

  private long lvConsumerIndex()
  {
    return UnsafeAccess.UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
  }

  private static <E> Object lvElement(E[] paramArrayOfE, long paramLong)
  {
    return UnsafeAccess.UNSAFE.getObjectVolatile(paramArrayOfE, paramLong);
  }

  private E[] lvNext(E[] paramArrayOfE)
  {
    return (Object[])(Object[])lvElement(paramArrayOfE, calcDirectOffset(-1 + paramArrayOfE.length));
  }

  private long lvProducerIndex()
  {
    return UnsafeAccess.UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
  }

  private E newBufferPeek(E[] paramArrayOfE, long paramLong1, long paramLong2)
  {
    this.consumerBuffer = paramArrayOfE;
    return lvElement(paramArrayOfE, calcWrappedOffset(paramLong1, paramLong2));
  }

  private E newBufferPoll(E[] paramArrayOfE, long paramLong1, long paramLong2)
  {
    this.consumerBuffer = paramArrayOfE;
    long l = calcWrappedOffset(paramLong1, paramLong2);
    Object localObject = lvElement(paramArrayOfE, l);
    if (localObject == null)
      return null;
    soElement(paramArrayOfE, l, null);
    soConsumerIndex(1L + paramLong1);
    return localObject;
  }

  private void resize(E[] paramArrayOfE, long paramLong1, long paramLong2, E paramE, long paramLong3)
  {
    Object[] arrayOfObject = (Object[])new Object[paramArrayOfE.length];
    this.producerBuffer = arrayOfObject;
    this.producerLookAhead = (paramLong1 + paramLong3 - 1L);
    soElement(arrayOfObject, paramLong2, paramE);
    soNext(paramArrayOfE, arrayOfObject);
    soElement(paramArrayOfE, paramLong2, HAS_NEXT);
    soProducerIndex(paramLong1 + 1L);
  }

  private void soConsumerIndex(long paramLong)
  {
    UnsafeAccess.UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, paramLong);
  }

  private static void soElement(Object[] paramArrayOfObject, long paramLong, Object paramObject)
  {
    UnsafeAccess.UNSAFE.putOrderedObject(paramArrayOfObject, paramLong, paramObject);
  }

  private void soNext(E[] paramArrayOfE1, E[] paramArrayOfE2)
  {
    soElement(paramArrayOfE1, calcDirectOffset(-1 + paramArrayOfE1.length), paramArrayOfE2);
  }

  private void soProducerIndex(long paramLong)
  {
    UnsafeAccess.UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, paramLong);
  }

  private boolean writeToQueue(E[] paramArrayOfE, E paramE, long paramLong1, long paramLong2)
  {
    soElement(paramArrayOfE, paramLong2, paramE);
    soProducerIndex(1L + paramLong1);
    return true;
  }

  public long currentConsumerIndex()
  {
    return lvConsumerIndex();
  }

  public long currentProducerIndex()
  {
    return lvProducerIndex();
  }

  public final Iterator<E> iterator()
  {
    throw new UnsupportedOperationException();
  }

  public final boolean offer(E paramE)
  {
    if (paramE == null)
      throw new NullPointerException("Null is not a valid element");
    Object[] arrayOfObject = this.producerBuffer;
    long l1 = this.producerIndex;
    long l2 = this.producerMask;
    long l3 = calcWrappedOffset(l1, l2);
    if (l1 < this.producerLookAhead)
      return writeToQueue(arrayOfObject, paramE, l1, l3);
    int i = this.producerLookAheadStep;
    if (lvElement(arrayOfObject, calcWrappedOffset(l1 + i, l2)) == null)
    {
      this.producerLookAhead = (l1 + i - 1L);
      return writeToQueue(arrayOfObject, paramE, l1, l3);
    }
    if (lvElement(arrayOfObject, calcWrappedOffset(1L + l1, l2)) != null)
      return writeToQueue(arrayOfObject, paramE, l1, l3);
    resize(arrayOfObject, l1, l3, paramE, l2);
    return true;
  }

  public final E peek()
  {
    Object[] arrayOfObject = this.consumerBuffer;
    long l1 = this.consumerIndex;
    long l2 = this.consumerMask;
    Object localObject = lvElement(arrayOfObject, calcWrappedOffset(l1, l2));
    if (localObject == HAS_NEXT)
      localObject = newBufferPeek(lvNext(arrayOfObject), l1, l2);
    return localObject;
  }

  public final E poll()
  {
    Object[] arrayOfObject = this.consumerBuffer;
    long l1 = this.consumerIndex;
    long l2 = this.consumerMask;
    long l3 = calcWrappedOffset(l1, l2);
    Object localObject = lvElement(arrayOfObject, l3);
    if (localObject == HAS_NEXT);
    for (int i = 1; (localObject != null) && (i == 0); i = 0)
    {
      soElement(arrayOfObject, l3, null);
      soConsumerIndex(1L + l1);
      return localObject;
    }
    if (i != 0)
      return newBufferPoll(lvNext(arrayOfObject), l1, l2);
    return null;
  }

  public final int size()
  {
    long l1 = lvConsumerIndex();
    long l2;
    long l3;
    do
    {
      l2 = l1;
      l3 = lvProducerIndex();
      l1 = lvConsumerIndex();
    }
    while (l2 != l1);
    return (int)(l3 - l1);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.unsafe.SpscUnboundedArrayQueue
 * JD-Core Version:    0.6.0
 */