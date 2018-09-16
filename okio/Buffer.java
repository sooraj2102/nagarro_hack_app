package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Buffer
  implements BufferedSource, BufferedSink, Cloneable
{
  private static final byte[] DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  static final int REPLACEMENT_CHARACTER = 65533;
  Segment head;
  long size;

  private ByteString digest(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
      if (this.head != null)
      {
        localMessageDigest.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
        for (Segment localSegment = this.head.next; localSegment != this.head; localSegment = localSegment.next)
          localMessageDigest.update(localSegment.data, localSegment.pos, localSegment.limit - localSegment.pos);
      }
      ByteString localByteString = ByteString.of(localMessageDigest.digest());
      return localByteString;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
    }
    throw new AssertionError();
  }

  private ByteString hmac(String paramString, ByteString paramByteString)
  {
    try
    {
      Mac localMac = Mac.getInstance(paramString);
      localMac.init(new SecretKeySpec(paramByteString.toByteArray(), paramString));
      if (this.head != null)
      {
        localMac.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
        for (Segment localSegment = this.head.next; localSegment != this.head; localSegment = localSegment.next)
          localMac.update(localSegment.data, localSegment.pos, localSegment.limit - localSegment.pos);
      }
      ByteString localByteString = ByteString.of(localMac.doFinal());
      return localByteString;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new AssertionError();
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
    }
    throw new IllegalArgumentException(localInvalidKeyException);
  }

  private boolean rangeEquals(Segment paramSegment, int paramInt1, ByteString paramByteString, int paramInt2, int paramInt3)
  {
    int i = paramSegment.limit;
    byte[] arrayOfByte = paramSegment.data;
    for (int j = paramInt2; j < paramInt3; j++)
    {
      if (paramInt1 == i)
      {
        paramSegment = paramSegment.next;
        arrayOfByte = paramSegment.data;
        paramInt1 = paramSegment.pos;
        i = paramSegment.limit;
      }
      if (arrayOfByte[paramInt1] != paramByteString.getByte(j))
        return false;
      paramInt1++;
    }
    return true;
  }

  private void readFrom(InputStream paramInputStream, long paramLong, boolean paramBoolean)
    throws IOException
  {
    if (paramInputStream == null)
      throw new IllegalArgumentException("in == null");
    while (true)
    {
      localSegment.limit = (j + localSegment.limit);
      this.size += j;
      paramLong -= j;
      if ((paramLong <= 0L) && (!paramBoolean))
        break;
      Segment localSegment = writableSegment(1);
      int i = (int)Math.min(paramLong, 8192 - localSegment.limit);
      int j = paramInputStream.read(localSegment.data, localSegment.limit, i);
      if (j != -1)
        continue;
      if (!paramBoolean)
        break label110;
    }
    return;
    label110: throw new EOFException();
  }

  public Buffer buffer()
  {
    return this;
  }

  public void clear()
  {
    try
    {
      skip(this.size);
      return;
    }
    catch (EOFException localEOFException)
    {
    }
    throw new AssertionError(localEOFException);
  }

  public Buffer clone()
  {
    Buffer localBuffer = new Buffer();
    if (this.size == 0L)
      return localBuffer;
    localBuffer.head = new Segment(this.head);
    Segment localSegment1 = localBuffer.head;
    Segment localSegment2 = localBuffer.head;
    Segment localSegment3 = localBuffer.head;
    localSegment2.prev = localSegment3;
    localSegment1.next = localSegment3;
    for (Segment localSegment4 = this.head.next; localSegment4 != this.head; localSegment4 = localSegment4.next)
      localBuffer.head.prev.push(new Segment(localSegment4));
    localBuffer.size = this.size;
    return localBuffer;
  }

  public void close()
  {
  }

  public long completeSegmentByteCount()
  {
    long l = this.size;
    if (l == 0L)
      return 0L;
    Segment localSegment = this.head.prev;
    if ((localSegment.limit < 8192) && (localSegment.owner))
      l -= localSegment.limit - localSegment.pos;
    return l;
  }

  public Buffer copyTo(OutputStream paramOutputStream)
    throws IOException
  {
    return copyTo(paramOutputStream, 0L, this.size);
  }

  public Buffer copyTo(OutputStream paramOutputStream, long paramLong1, long paramLong2)
    throws IOException
  {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("out == null");
    Util.checkOffsetAndCount(this.size, paramLong1, paramLong2);
    if (paramLong2 == 0L);
    while (true)
    {
      return this;
      for (Segment localSegment = this.head; paramLong1 >= localSegment.limit - localSegment.pos; localSegment = localSegment.next)
        paramLong1 -= localSegment.limit - localSegment.pos;
      while (paramLong2 > 0L)
      {
        int i = (int)(paramLong1 + localSegment.pos);
        int j = (int)Math.min(localSegment.limit - i, paramLong2);
        paramOutputStream.write(localSegment.data, i, j);
        paramLong2 -= j;
        paramLong1 = 0L;
        localSegment = localSegment.next;
      }
    }
  }

  public Buffer copyTo(Buffer paramBuffer, long paramLong1, long paramLong2)
  {
    if (paramBuffer == null)
      throw new IllegalArgumentException("out == null");
    Util.checkOffsetAndCount(this.size, paramLong1, paramLong2);
    if (paramLong2 == 0L)
      return this;
    paramBuffer.size = (paramLong2 + paramBuffer.size);
    for (Segment localSegment1 = this.head; paramLong1 >= localSegment1.limit - localSegment1.pos; localSegment1 = localSegment1.next)
      paramLong1 -= localSegment1.limit - localSegment1.pos;
    label92: Segment localSegment2;
    if (paramLong2 > 0L)
    {
      localSegment2 = new Segment(localSegment1);
      localSegment2.pos = (int)(paramLong1 + localSegment2.pos);
      localSegment2.limit = Math.min(localSegment2.pos + (int)paramLong2, localSegment2.limit);
      if (paramBuffer.head != null)
        break label202;
      localSegment2.prev = localSegment2;
      localSegment2.next = localSegment2;
      paramBuffer.head = localSegment2;
    }
    while (true)
    {
      paramLong2 -= localSegment2.limit - localSegment2.pos;
      paramLong1 = 0L;
      localSegment1 = localSegment1.next;
      break label92;
      break;
      label202: paramBuffer.head.prev.push(localSegment2);
    }
  }

  public BufferedSink emit()
  {
    return this;
  }

  public Buffer emitCompleteSegments()
  {
    return this;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject)
      return true;
    if (!(paramObject instanceof Buffer))
      return false;
    Buffer localBuffer = (Buffer)paramObject;
    if (this.size != localBuffer.size)
      return false;
    if (this.size == 0L)
      return true;
    Segment localSegment1 = this.head;
    Segment localSegment2 = localBuffer.head;
    int i = localSegment1.pos;
    int j = localSegment2.pos;
    long l1 = 0L;
    long l2;
    int m;
    int n;
    if (l1 < this.size)
    {
      l2 = Math.min(localSegment1.limit - i, localSegment2.limit - j);
      int k = 0;
      m = j;
      int i1;
      for (n = i; k < l2; n = i1)
      {
        byte[] arrayOfByte1 = localSegment1.data;
        i1 = n + 1;
        int i2 = arrayOfByte1[n];
        byte[] arrayOfByte2 = localSegment2.data;
        int i3 = m + 1;
        if (i2 != arrayOfByte2[m])
          return false;
        k++;
        m = i3;
      }
      if (n != localSegment1.limit)
        break label245;
      localSegment1 = localSegment1.next;
    }
    label245: for (i = localSegment1.pos; ; i = n)
    {
      if (m == localSegment2.limit)
        localSegment2 = localSegment2.next;
      for (j = localSegment2.pos; ; j = m)
      {
        l1 += l2;
        break;
        return true;
      }
    }
  }

  public boolean exhausted()
  {
    return this.size == 0L;
  }

  public void flush()
  {
  }

  public byte getByte(long paramLong)
  {
    Util.checkOffsetAndCount(this.size, paramLong, 1L);
    for (Segment localSegment = this.head; ; localSegment = localSegment.next)
    {
      int i = localSegment.limit - localSegment.pos;
      if (paramLong < i)
        return localSegment.data[(localSegment.pos + (int)paramLong)];
      paramLong -= i;
    }
  }

  public int hashCode()
  {
    Segment localSegment = this.head;
    if (localSegment == null)
      return 0;
    int i = 1;
    do
    {
      int j = localSegment.pos;
      int k = localSegment.limit;
      while (j < k)
      {
        i = i * 31 + localSegment.data[j];
        j++;
      }
      localSegment = localSegment.next;
    }
    while (localSegment != this.head);
    return i;
  }

  public ByteString hmacSha1(ByteString paramByteString)
  {
    return hmac("HmacSHA1", paramByteString);
  }

  public ByteString hmacSha256(ByteString paramByteString)
  {
    return hmac("HmacSHA256", paramByteString);
  }

  public long indexOf(byte paramByte)
  {
    return indexOf(paramByte, 0L);
  }

  public long indexOf(byte paramByte, long paramLong)
  {
    if (paramLong < 0L)
      throw new IllegalArgumentException("fromIndex < 0");
    Segment localSegment = this.head;
    if (localSegment == null)
      return -1L;
    if (this.size - paramLong < paramLong)
    {
      l1 = this.size;
      while (l1 > paramLong)
      {
        localSegment = localSegment.prev;
        l1 -= localSegment.limit - localSegment.pos;
      }
    }
    long l1 = 0L;
    while (true)
    {
      long l2 = l1 + (localSegment.limit - localSegment.pos);
      if (l2 >= paramLong)
        break;
      localSegment = localSegment.next;
      l1 = l2;
    }
    while (true)
    {
      l1 += localSegment.limit - localSegment.pos;
      paramLong = l1;
      localSegment = localSegment.next;
      if (l1 >= this.size)
        break;
      byte[] arrayOfByte = localSegment.data;
      int i = (int)(paramLong + localSegment.pos - l1);
      int j = localSegment.limit;
      while (i < j)
      {
        if (arrayOfByte[i] == paramByte)
          return l1 + (i - localSegment.pos);
        i++;
      }
    }
  }

  public long indexOf(ByteString paramByteString)
    throws IOException
  {
    return indexOf(paramByteString, 0L);
  }

  public long indexOf(ByteString paramByteString, long paramLong)
    throws IOException
  {
    if (paramByteString.size() == 0)
      throw new IllegalArgumentException("bytes is empty");
    if (paramLong < 0L)
      throw new IllegalArgumentException("fromIndex < 0");
    Segment localSegment = this.head;
    if (localSegment == null)
      return -1L;
    if (this.size - paramLong < paramLong)
    {
      l1 = this.size;
      while (l1 > paramLong)
      {
        localSegment = localSegment.prev;
        l1 -= localSegment.limit - localSegment.pos;
      }
    }
    long l1 = 0L;
    while (true)
    {
      long l2 = l1 + (localSegment.limit - localSegment.pos);
      if (l2 >= paramLong)
        break;
      localSegment = localSegment.next;
      l1 = l2;
    }
    int i = paramByteString.getByte(0);
    int j = paramByteString.size();
    long l3 = 1L + (this.size - j);
    while (l1 < l3)
    {
      byte[] arrayOfByte = localSegment.data;
      int k = (int)Math.min(localSegment.limit, l3 + localSegment.pos - l1);
      for (int m = (int)(paramLong + localSegment.pos - l1); m < k; m++)
        if ((arrayOfByte[m] == i) && (rangeEquals(localSegment, m + 1, paramByteString, 1, j)))
          return l1 + (m - localSegment.pos);
      l1 += localSegment.limit - localSegment.pos;
      paramLong = l1;
      localSegment = localSegment.next;
    }
    return -1L;
  }

  public long indexOfElement(ByteString paramByteString)
  {
    return indexOfElement(paramByteString, 0L);
  }

  public long indexOfElement(ByteString paramByteString, long paramLong)
  {
    if (paramLong < 0L)
      throw new IllegalArgumentException("fromIndex < 0");
    Segment localSegment = this.head;
    if (localSegment == null)
      return -1L;
    if (this.size - paramLong < paramLong)
    {
      l1 = this.size;
      while (l1 > paramLong)
      {
        localSegment = localSegment.prev;
        l1 -= localSegment.limit - localSegment.pos;
      }
    }
    long l1 = 0L;
    while (true)
    {
      long l2 = l1 + (localSegment.limit - localSegment.pos);
      if (l2 >= paramLong)
        break;
      localSegment = localSegment.next;
      l1 = l2;
    }
    if (paramByteString.size() == 2)
    {
      int i1 = paramByteString.getByte(0);
      int i2 = paramByteString.getByte(1);
      while (l1 < this.size)
      {
        byte[] arrayOfByte3 = localSegment.data;
        int i3 = (int)(paramLong + localSegment.pos - l1);
        int i4 = localSegment.limit;
        while (i3 < i4)
        {
          int i5 = arrayOfByte3[i3];
          if ((i5 == i1) || (i5 == i2))
            return l1 + (i3 - localSegment.pos);
          i3++;
        }
        l1 += localSegment.limit - localSegment.pos;
        paramLong = l1;
        localSegment = localSegment.next;
      }
    }
    byte[] arrayOfByte1 = paramByteString.internalArray();
    while (l1 < this.size)
    {
      byte[] arrayOfByte2 = localSegment.data;
      int i = (int)(paramLong + localSegment.pos - l1);
      int j = localSegment.limit;
      while (i < j)
      {
        int k = arrayOfByte2[i];
        int m = arrayOfByte1.length;
        for (int n = 0; n < m; n++)
          if (k == arrayOfByte1[n])
            return l1 + (i - localSegment.pos);
        i++;
      }
      l1 += localSegment.limit - localSegment.pos;
      paramLong = l1;
      localSegment = localSegment.next;
    }
    return -1L;
  }

  public InputStream inputStream()
  {
    return new InputStream()
    {
      public int available()
      {
        return (int)Math.min(Buffer.this.size, 2147483647L);
      }

      public void close()
      {
      }

      public int read()
      {
        if (Buffer.this.size > 0L)
          return 0xFF & Buffer.this.readByte();
        return -1;
      }

      public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      {
        return Buffer.this.read(paramArrayOfByte, paramInt1, paramInt2);
      }

      public String toString()
      {
        return Buffer.this + ".inputStream()";
      }
    };
  }

  public ByteString md5()
  {
    return digest("MD5");
  }

  public OutputStream outputStream()
  {
    return new OutputStream()
    {
      public void close()
      {
      }

      public void flush()
      {
      }

      public String toString()
      {
        return Buffer.this + ".outputStream()";
      }

      public void write(int paramInt)
      {
        Buffer.this.writeByte((byte)paramInt);
      }

      public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      {
        Buffer.this.write(paramArrayOfByte, paramInt1, paramInt2);
      }
    };
  }

  public boolean rangeEquals(long paramLong, ByteString paramByteString)
  {
    return rangeEquals(paramLong, paramByteString, 0, paramByteString.size());
  }

  public boolean rangeEquals(long paramLong, ByteString paramByteString, int paramInt1, int paramInt2)
  {
    if ((paramLong < 0L) || (paramInt1 < 0) || (paramInt2 < 0) || (this.size - paramLong < paramInt2) || (paramByteString.size() - paramInt1 < paramInt2))
      return false;
    for (int i = 0; ; i++)
    {
      if (i >= paramInt2)
        break label80;
      if (getByte(paramLong + i) != paramByteString.getByte(paramInt1 + i))
        break;
    }
    label80: return true;
  }

  public int read(byte[] paramArrayOfByte)
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
    Segment localSegment = this.head;
    int i;
    if (localSegment == null)
      i = -1;
    do
    {
      return i;
      i = Math.min(paramInt2, localSegment.limit - localSegment.pos);
      System.arraycopy(localSegment.data, localSegment.pos, paramArrayOfByte, paramInt1, i);
      localSegment.pos = (i + localSegment.pos);
      this.size -= i;
    }
    while (localSegment.pos != localSegment.limit);
    this.head = localSegment.pop();
    SegmentPool.recycle(localSegment);
    return i;
  }

  public long read(Buffer paramBuffer, long paramLong)
  {
    if (paramBuffer == null)
      throw new IllegalArgumentException("sink == null");
    if (paramLong < 0L)
      throw new IllegalArgumentException("byteCount < 0: " + paramLong);
    if (this.size == 0L)
      return -1L;
    if (paramLong > this.size)
      paramLong = this.size;
    paramBuffer.write(this, paramLong);
    return paramLong;
  }

  public long readAll(Sink paramSink)
    throws IOException
  {
    long l = this.size;
    if (l > 0L)
      paramSink.write(this, l);
    return l;
  }

  public byte readByte()
  {
    if (this.size == 0L)
      throw new IllegalStateException("size == 0");
    Segment localSegment = this.head;
    int i = localSegment.pos;
    int j = localSegment.limit;
    byte[] arrayOfByte = localSegment.data;
    int k = i + 1;
    int m = arrayOfByte[i];
    this.size -= 1L;
    if (k == j)
    {
      this.head = localSegment.pop();
      SegmentPool.recycle(localSegment);
      return m;
    }
    localSegment.pos = k;
    return m;
  }

  public byte[] readByteArray()
  {
    try
    {
      byte[] arrayOfByte = readByteArray(this.size);
      return arrayOfByte;
    }
    catch (EOFException localEOFException)
    {
    }
    throw new AssertionError(localEOFException);
  }

  public byte[] readByteArray(long paramLong)
    throws EOFException
  {
    Util.checkOffsetAndCount(this.size, 0L, paramLong);
    if (paramLong > 2147483647L)
      throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + paramLong);
    byte[] arrayOfByte = new byte[(int)paramLong];
    readFully(arrayOfByte);
    return arrayOfByte;
  }

  public ByteString readByteString()
  {
    return new ByteString(readByteArray());
  }

  public ByteString readByteString(long paramLong)
    throws EOFException
  {
    return new ByteString(readByteArray(paramLong));
  }

  public long readDecimalLong()
  {
    if (this.size == 0L)
      throw new IllegalStateException("size == 0");
    long l1 = 0L;
    int i = 0;
    int j = 0;
    int k = 0;
    long l2 = -7L;
    while (true)
    {
      Segment localSegment = this.head;
      byte[] arrayOfByte = localSegment.data;
      int m = localSegment.pos;
      int n = localSegment.limit;
      if (m < n)
      {
        int i1 = arrayOfByte[m];
        if ((i1 >= 48) && (i1 <= 57))
        {
          int i2 = 48 - i1;
          if ((l1 < -922337203685477580L) || ((l1 == -922337203685477580L) && (i2 < l2)))
          {
            Buffer localBuffer = new Buffer().writeDecimalLong(l1).writeByte(i1);
            if (j == 0)
              localBuffer.readByte();
            throw new NumberFormatException("Number too large: " + localBuffer.readUtf8());
          }
          l1 = l1 * 10L + i2;
        }
        while (true)
        {
          m++;
          i++;
          break;
          if ((i1 != 45) || (i != 0))
            break label225;
          j = 1;
          l2 -= 1L;
        }
        label225: if (i == 0)
          throw new NumberFormatException("Expected leading [0-9] or '-' character but was 0x" + Integer.toHexString(i1));
        k = 1;
      }
      if (m == n)
      {
        this.head = localSegment.pop();
        SegmentPool.recycle(localSegment);
      }
      while ((k != 0) || (this.head == null))
      {
        this.size -= i;
        if (j == 0)
          break label325;
        return l1;
        localSegment.pos = m;
      }
    }
    label325: return -l1;
  }

  public Buffer readFrom(InputStream paramInputStream)
    throws IOException
  {
    readFrom(paramInputStream, 9223372036854775807L, true);
    return this;
  }

  public Buffer readFrom(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    if (paramLong < 0L)
      throw new IllegalArgumentException("byteCount < 0: " + paramLong);
    readFrom(paramInputStream, paramLong, false);
    return this;
  }

  public void readFully(Buffer paramBuffer, long paramLong)
    throws EOFException
  {
    if (this.size < paramLong)
    {
      paramBuffer.write(this, this.size);
      throw new EOFException();
    }
    paramBuffer.write(this, paramLong);
  }

  public void readFully(byte[] paramArrayOfByte)
    throws EOFException
  {
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      int j = read(paramArrayOfByte, i, paramArrayOfByte.length - i);
      if (j == -1)
        throw new EOFException();
      i += j;
    }
  }

  public long readHexadecimalUnsignedLong()
  {
    if (this.size == 0L)
      throw new IllegalStateException("size == 0");
    long l = 0L;
    int i = 0;
    int j = 0;
    label288: label313: 
    while (true)
    {
      Segment localSegment = this.head;
      byte[] arrayOfByte = localSegment.data;
      int k = localSegment.pos;
      int m = localSegment.limit;
      int i1;
      if (k < m)
      {
        int n = arrayOfByte[k];
        if ((n >= 48) && (n <= 57))
          i1 = n - 48;
        while (true)
        {
          if ((0x0 & l) == 0L)
            break label288;
          Buffer localBuffer = new Buffer().writeHexadecimalUnsignedLong(l).writeByte(n);
          throw new NumberFormatException("Number too large: " + localBuffer.readUtf8());
          if ((n >= 97) && (n <= 102))
          {
            i1 = 10 + (n - 97);
            continue;
          }
          if ((n < 65) || (n > 70))
            break;
          i1 = 10 + (n - 65);
        }
        if (i == 0)
          throw new NumberFormatException("Expected leading [0-9a-fA-F] character but was 0x" + Integer.toHexString(n));
        j = 1;
      }
      if (k == m)
      {
        this.head = localSegment.pop();
        SegmentPool.recycle(localSegment);
      }
      while (true)
      {
        if ((j == 0) && (this.head != null))
          break label313;
        this.size -= i;
        return l;
        l = l << 4 | i1;
        k++;
        i++;
        break;
        localSegment.pos = k;
      }
    }
  }

  public int readInt()
  {
    if (this.size < 4L)
      throw new IllegalStateException("size < 4: " + this.size);
    Segment localSegment = this.head;
    int i = localSegment.pos;
    int j = localSegment.limit;
    if (j - i < 4)
      return (0xFF & readByte()) << 24 | (0xFF & readByte()) << 16 | (0xFF & readByte()) << 8 | 0xFF & readByte();
    byte[] arrayOfByte = localSegment.data;
    int k = i + 1;
    int m = (0xFF & arrayOfByte[i]) << 24;
    int n = k + 1;
    int i1 = m | (0xFF & arrayOfByte[k]) << 16;
    int i2 = n + 1;
    int i3 = i1 | (0xFF & arrayOfByte[n]) << 8;
    int i4 = i2 + 1;
    int i5 = i3 | 0xFF & arrayOfByte[i2];
    this.size -= 4L;
    if (i4 == j)
    {
      this.head = localSegment.pop();
      SegmentPool.recycle(localSegment);
      return i5;
    }
    localSegment.pos = i4;
    return i5;
  }

  public int readIntLe()
  {
    return Util.reverseBytesInt(readInt());
  }

  public long readLong()
  {
    if (this.size < 8L)
      throw new IllegalStateException("size < 8: " + this.size);
    Segment localSegment = this.head;
    int i = localSegment.pos;
    int j = localSegment.limit;
    if (j - i < 8)
      return (0xFFFFFFFF & readInt()) << 32 | 0xFFFFFFFF & readInt();
    byte[] arrayOfByte = localSegment.data;
    int k = i + 1;
    long l1 = (0xFF & arrayOfByte[i]) << 56;
    int m = k + 1;
    long l2 = l1 | (0xFF & arrayOfByte[k]) << 48;
    int n = m + 1;
    long l3 = l2 | (0xFF & arrayOfByte[m]) << 40;
    int i1 = n + 1;
    long l4 = l3 | (0xFF & arrayOfByte[n]) << 32;
    int i2 = i1 + 1;
    long l5 = l4 | (0xFF & arrayOfByte[i1]) << 24;
    int i3 = i2 + 1;
    long l6 = l5 | (0xFF & arrayOfByte[i2]) << 16;
    int i4 = i3 + 1;
    long l7 = l6 | (0xFF & arrayOfByte[i3]) << 8;
    int i5 = i4 + 1;
    long l8 = l7 | 0xFF & arrayOfByte[i4];
    this.size -= 8L;
    if (i5 == j)
    {
      this.head = localSegment.pop();
      SegmentPool.recycle(localSegment);
      return l8;
    }
    localSegment.pos = i5;
    return l8;
  }

  public long readLongLe()
  {
    return Util.reverseBytesLong(readLong());
  }

  public short readShort()
  {
    if (this.size < 2L)
      throw new IllegalStateException("size < 2: " + this.size);
    Segment localSegment = this.head;
    int i = localSegment.pos;
    int j = localSegment.limit;
    if (j - i < 2)
      return (short)((0xFF & readByte()) << 8 | 0xFF & readByte());
    byte[] arrayOfByte = localSegment.data;
    int k = i + 1;
    int m = (0xFF & arrayOfByte[i]) << 8;
    int n = k + 1;
    int i1 = m | 0xFF & arrayOfByte[k];
    this.size -= 2L;
    if (n == j)
    {
      this.head = localSegment.pop();
      SegmentPool.recycle(localSegment);
    }
    while (true)
    {
      return (short)i1;
      localSegment.pos = n;
    }
  }

  public short readShortLe()
  {
    return Util.reverseBytesShort(readShort());
  }

  public String readString(long paramLong, Charset paramCharset)
    throws EOFException
  {
    Util.checkOffsetAndCount(this.size, 0L, paramLong);
    if (paramCharset == null)
      throw new IllegalArgumentException("charset == null");
    if (paramLong > 2147483647L)
      throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + paramLong);
    String str;
    if (paramLong == 0L)
      str = "";
    Segment localSegment;
    do
    {
      return str;
      localSegment = this.head;
      if (paramLong + localSegment.pos > localSegment.limit)
        return new String(readByteArray(paramLong), paramCharset);
      str = new String(localSegment.data, localSegment.pos, (int)paramLong, paramCharset);
      localSegment.pos = (int)(paramLong + localSegment.pos);
      this.size -= paramLong;
    }
    while (localSegment.pos != localSegment.limit);
    this.head = localSegment.pop();
    SegmentPool.recycle(localSegment);
    return str;
  }

  public String readString(Charset paramCharset)
  {
    try
    {
      String str = readString(this.size, paramCharset);
      return str;
    }
    catch (EOFException localEOFException)
    {
    }
    throw new AssertionError(localEOFException);
  }

  public String readUtf8()
  {
    try
    {
      String str = readString(this.size, Util.UTF_8);
      return str;
    }
    catch (EOFException localEOFException)
    {
    }
    throw new AssertionError(localEOFException);
  }

  public String readUtf8(long paramLong)
    throws EOFException
  {
    return readString(paramLong, Util.UTF_8);
  }

  public int readUtf8CodePoint()
    throws EOFException
  {
    if (this.size == 0L)
      throw new EOFException();
    int i = getByte(0L);
    int j;
    int k;
    int m;
    if ((i & 0x80) == 0)
    {
      j = i & 0x7F;
      k = 1;
      m = 0;
    }
    while (this.size < k)
    {
      throw new EOFException("size < " + k + ": " + this.size + " (to read code point prefixed 0x" + Integer.toHexString(i) + ")");
      if ((i & 0xE0) == 192)
      {
        j = i & 0x1F;
        k = 2;
        m = 128;
        continue;
      }
      if ((i & 0xF0) == 224)
      {
        j = i & 0xF;
        k = 3;
        m = 2048;
        continue;
      }
      if ((i & 0xF8) == 240)
      {
        j = i & 0x7;
        k = 4;
        m = 65536;
        continue;
      }
      skip(1L);
      j = 65533;
    }
    do
    {
      return j;
      int n = 1;
      while (n < k)
      {
        int i1 = getByte(n);
        if ((i1 & 0xC0) == 128)
        {
          j = j << 6 | i1 & 0x3F;
          n++;
          continue;
        }
        skip(n);
        return 65533;
      }
      skip(k);
      if (j > 1114111)
        return 65533;
      if ((j >= 55296) && (j <= 57343))
        return 65533;
    }
    while (j >= m);
    return 65533;
  }

  public String readUtf8Line()
    throws EOFException
  {
    long l = indexOf(10);
    if (l == -1L)
    {
      if (this.size != 0L)
        return readUtf8(this.size);
      return null;
    }
    return readUtf8Line(l);
  }

  String readUtf8Line(long paramLong)
    throws EOFException
  {
    if ((paramLong > 0L) && (getByte(paramLong - 1L) == 13))
    {
      String str2 = readUtf8(paramLong - 1L);
      skip(2L);
      return str2;
    }
    String str1 = readUtf8(paramLong);
    skip(1L);
    return str1;
  }

  public String readUtf8LineStrict()
    throws EOFException
  {
    long l = indexOf(10);
    if (l == -1L)
    {
      Buffer localBuffer = new Buffer();
      copyTo(localBuffer, 0L, Math.min(32L, this.size));
      throw new EOFException("\\n not found: size=" + size() + " content=" + localBuffer.readByteString().hex() + "…");
    }
    return readUtf8Line(l);
  }

  public boolean request(long paramLong)
  {
    return this.size >= paramLong;
  }

  public void require(long paramLong)
    throws EOFException
  {
    if (this.size < paramLong)
      throw new EOFException();
  }

  List<Integer> segmentSizes()
  {
    Object localObject;
    if (this.head == null)
      localObject = Collections.emptyList();
    while (true)
    {
      return localObject;
      localObject = new ArrayList();
      ((List)localObject).add(Integer.valueOf(this.head.limit - this.head.pos));
      for (Segment localSegment = this.head.next; localSegment != this.head; localSegment = localSegment.next)
        ((List)localObject).add(Integer.valueOf(localSegment.limit - localSegment.pos));
    }
  }

  public int select(Options paramOptions)
  {
    Segment localSegment = this.head;
    if (localSegment == null)
      return paramOptions.indexOf(ByteString.EMPTY);
    ByteString[] arrayOfByteString = paramOptions.byteStrings;
    int i = 0;
    int j = arrayOfByteString.length;
    while (i < j)
    {
      ByteString localByteString = arrayOfByteString[i];
      if ((this.size >= localByteString.size()) && (rangeEquals(localSegment, localSegment.pos, localByteString, 0, localByteString.size())))
        try
        {
          skip(localByteString.size());
          return i;
        }
        catch (EOFException localEOFException)
        {
          throw new AssertionError(localEOFException);
        }
      i++;
    }
    return -1;
  }

  int selectPrefix(Options paramOptions)
  {
    Segment localSegment = this.head;
    ByteString[] arrayOfByteString = paramOptions.byteStrings;
    int i = 0;
    int j = arrayOfByteString.length;
    while (i < j)
    {
      ByteString localByteString = arrayOfByteString[i];
      int k = (int)Math.min(this.size, localByteString.size());
      if ((k == 0) || (rangeEquals(localSegment, localSegment.pos, localByteString, 0, k)))
        return i;
      i++;
    }
    return -1;
  }

  public ByteString sha1()
  {
    return digest("SHA-1");
  }

  public ByteString sha256()
  {
    return digest("SHA-256");
  }

  public long size()
  {
    return this.size;
  }

  public void skip(long paramLong)
    throws EOFException
  {
    while (paramLong > 0L)
    {
      if (this.head == null)
        throw new EOFException();
      int i = (int)Math.min(paramLong, this.head.limit - this.head.pos);
      this.size -= i;
      paramLong -= i;
      Segment localSegment1 = this.head;
      localSegment1.pos = (i + localSegment1.pos);
      if (this.head.pos != this.head.limit)
        continue;
      Segment localSegment2 = this.head;
      this.head = localSegment2.pop();
      SegmentPool.recycle(localSegment2);
    }
  }

  public ByteString snapshot()
  {
    if (this.size > 2147483647L)
      throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + this.size);
    return snapshot((int)this.size);
  }

  public ByteString snapshot(int paramInt)
  {
    if (paramInt == 0)
      return ByteString.EMPTY;
    return new SegmentedByteString(this, paramInt);
  }

  public Timeout timeout()
  {
    return Timeout.NONE;
  }

  public String toString()
  {
    return snapshot().toString();
  }

  Segment writableSegment(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > 8192))
      throw new IllegalArgumentException();
    Segment localSegment1;
    if (this.head == null)
    {
      this.head = SegmentPool.take();
      Segment localSegment2 = this.head;
      Segment localSegment3 = this.head;
      localSegment1 = this.head;
      localSegment3.prev = localSegment1;
      localSegment2.next = localSegment1;
    }
    do
    {
      return localSegment1;
      localSegment1 = this.head.prev;
    }
    while ((paramInt + localSegment1.limit <= 8192) && (localSegment1.owner));
    return localSegment1.push(SegmentPool.take());
  }

  public Buffer write(ByteString paramByteString)
  {
    if (paramByteString == null)
      throw new IllegalArgumentException("byteString == null");
    paramByteString.write(this);
    return this;
  }

  public Buffer write(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("source == null");
    return write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public Buffer write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("source == null");
    Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
    int i = paramInt1 + paramInt2;
    while (paramInt1 < i)
    {
      Segment localSegment = writableSegment(1);
      int j = Math.min(i - paramInt1, 8192 - localSegment.limit);
      System.arraycopy(paramArrayOfByte, paramInt1, localSegment.data, localSegment.limit, j);
      paramInt1 += j;
      localSegment.limit = (j + localSegment.limit);
    }
    this.size += paramInt2;
    return this;
  }

  public BufferedSink write(Source paramSource, long paramLong)
    throws IOException
  {
    while (paramLong > 0L)
    {
      long l = paramSource.read(this, paramLong);
      if (l == -1L)
        throw new EOFException();
      paramLong -= l;
    }
    return this;
  }

  public void write(Buffer paramBuffer, long paramLong)
  {
    if (paramBuffer == null)
      throw new IllegalArgumentException("source == null");
    if (paramBuffer == this)
      throw new IllegalArgumentException("source == this");
    Util.checkOffsetAndCount(paramBuffer.size, 0L, paramLong);
    Segment localSegment5;
    long l2;
    if (paramLong > 0L)
    {
      if (paramLong >= paramBuffer.head.limit - paramBuffer.head.pos)
        break label191;
      if (this.head == null)
        break label162;
      localSegment5 = this.head.prev;
      if ((localSegment5 == null) || (!localSegment5.owner))
        break label178;
      l2 = paramLong + localSegment5.limit;
      if (!localSegment5.shared)
        break label168;
    }
    label162: label168: for (int i = 0; ; i = localSegment5.pos)
    {
      if (l2 - i > 8192L)
        break label178;
      paramBuffer.head.writeTo(localSegment5, (int)paramLong);
      paramBuffer.size -= paramLong;
      this.size = (paramLong + this.size);
      return;
      localSegment5 = null;
      break;
    }
    label178: paramBuffer.head = paramBuffer.head.split((int)paramLong);
    label191: Segment localSegment1 = paramBuffer.head;
    long l1 = localSegment1.limit - localSegment1.pos;
    paramBuffer.head = localSegment1.pop();
    if (this.head == null)
    {
      this.head = localSegment1;
      Segment localSegment2 = this.head;
      Segment localSegment3 = this.head;
      Segment localSegment4 = this.head;
      localSegment3.prev = localSegment4;
      localSegment2.next = localSegment4;
    }
    while (true)
    {
      paramBuffer.size -= l1;
      this.size = (l1 + this.size);
      paramLong -= l1;
      break;
      this.head.prev.push(localSegment1).compact();
    }
  }

  public long writeAll(Source paramSource)
    throws IOException
  {
    if (paramSource == null)
      throw new IllegalArgumentException("source == null");
    long l1 = 0L;
    while (true)
    {
      long l2 = paramSource.read(this, 8192L);
      if (l2 == -1L)
        break;
      l1 += l2;
    }
    return l1;
  }

  public Buffer writeByte(int paramInt)
  {
    Segment localSegment = writableSegment(1);
    byte[] arrayOfByte = localSegment.data;
    int i = localSegment.limit;
    localSegment.limit = (i + 1);
    arrayOfByte[i] = (byte)paramInt;
    this.size = (1L + this.size);
    return this;
  }

  public Buffer writeDecimalLong(long paramLong)
  {
    if (paramLong == 0L)
      return writeByte(48);
    boolean bool = paramLong < 0L;
    int i = 0;
    if (bool)
    {
      paramLong = -paramLong;
      if (paramLong < 0L)
        return writeUtf8("-9223372036854775808");
      i = 1;
    }
    int j;
    if (paramLong < 100000000L)
      if (paramLong < 10000L)
        if (paramLong < 100L)
          if (paramLong < 10L)
            j = 1;
    Segment localSegment;
    byte[] arrayOfByte;
    int k;
    while (true)
    {
      if (i != 0)
        j++;
      localSegment = writableSegment(j);
      arrayOfByte = localSegment.data;
      k = j + localSegment.limit;
      while (paramLong != 0L)
      {
        int m = (int)(paramLong % 10L);
        k--;
        arrayOfByte[k] = DIGITS[m];
        paramLong /= 10L;
      }
      j = 2;
      continue;
      if (paramLong < 1000L)
      {
        j = 3;
        continue;
      }
      j = 4;
      continue;
      if (paramLong < 1000000L)
      {
        if (paramLong < 100000L)
        {
          j = 5;
          continue;
        }
        j = 6;
        continue;
      }
      if (paramLong < 10000000L)
      {
        j = 7;
        continue;
      }
      j = 8;
      continue;
      if (paramLong < 1000000000000L)
      {
        if (paramLong < 10000000000L)
        {
          if (paramLong < 1000000000L)
          {
            j = 9;
            continue;
          }
          j = 10;
          continue;
        }
        if (paramLong < 100000000000L)
        {
          j = 11;
          continue;
        }
        j = 12;
        continue;
      }
      if (paramLong < 1000000000000000L)
      {
        if (paramLong < 10000000000000L)
        {
          j = 13;
          continue;
        }
        if (paramLong < 100000000000000L)
        {
          j = 14;
          continue;
        }
        j = 15;
        continue;
      }
      if (paramLong < 100000000000000000L)
      {
        if (paramLong < 10000000000000000L)
        {
          j = 16;
          continue;
        }
        j = 17;
        continue;
      }
      if (paramLong < 1000000000000000000L)
      {
        j = 18;
        continue;
      }
      j = 19;
    }
    if (i != 0)
      arrayOfByte[(k - 1)] = 45;
    localSegment.limit = (j + localSegment.limit);
    this.size += j;
    return this;
  }

  public Buffer writeHexadecimalUnsignedLong(long paramLong)
  {
    if (paramLong == 0L)
      return writeByte(48);
    int i = 1 + Long.numberOfTrailingZeros(Long.highestOneBit(paramLong)) / 4;
    Segment localSegment = writableSegment(i);
    byte[] arrayOfByte = localSegment.data;
    int j = -1 + (i + localSegment.limit);
    int k = localSegment.limit;
    while (j >= k)
    {
      arrayOfByte[j] = DIGITS[(int)(0xF & paramLong)];
      paramLong >>>= 4;
      j--;
    }
    localSegment.limit = (i + localSegment.limit);
    this.size += i;
    return this;
  }

  public Buffer writeInt(int paramInt)
  {
    Segment localSegment = writableSegment(4);
    byte[] arrayOfByte = localSegment.data;
    int i = localSegment.limit;
    int j = i + 1;
    arrayOfByte[i] = (byte)(0xFF & paramInt >>> 24);
    int k = j + 1;
    arrayOfByte[j] = (byte)(0xFF & paramInt >>> 16);
    int m = k + 1;
    arrayOfByte[k] = (byte)(0xFF & paramInt >>> 8);
    int n = m + 1;
    arrayOfByte[m] = (byte)(paramInt & 0xFF);
    localSegment.limit = n;
    this.size = (4L + this.size);
    return this;
  }

  public Buffer writeIntLe(int paramInt)
  {
    return writeInt(Util.reverseBytesInt(paramInt));
  }

  public Buffer writeLong(long paramLong)
  {
    Segment localSegment = writableSegment(8);
    byte[] arrayOfByte = localSegment.data;
    int i = localSegment.limit;
    int j = i + 1;
    arrayOfByte[i] = (byte)(int)(0xFF & paramLong >>> 56);
    int k = j + 1;
    arrayOfByte[j] = (byte)(int)(0xFF & paramLong >>> 48);
    int m = k + 1;
    arrayOfByte[k] = (byte)(int)(0xFF & paramLong >>> 40);
    int n = m + 1;
    arrayOfByte[m] = (byte)(int)(0xFF & paramLong >>> 32);
    int i1 = n + 1;
    arrayOfByte[n] = (byte)(int)(0xFF & paramLong >>> 24);
    int i2 = i1 + 1;
    arrayOfByte[i1] = (byte)(int)(0xFF & paramLong >>> 16);
    int i3 = i2 + 1;
    arrayOfByte[i2] = (byte)(int)(0xFF & paramLong >>> 8);
    int i4 = i3 + 1;
    arrayOfByte[i3] = (byte)(int)(paramLong & 0xFF);
    localSegment.limit = i4;
    this.size = (8L + this.size);
    return this;
  }

  public Buffer writeLongLe(long paramLong)
  {
    return writeLong(Util.reverseBytesLong(paramLong));
  }

  public Buffer writeShort(int paramInt)
  {
    Segment localSegment = writableSegment(2);
    byte[] arrayOfByte = localSegment.data;
    int i = localSegment.limit;
    int j = i + 1;
    arrayOfByte[i] = (byte)(0xFF & paramInt >>> 8);
    int k = j + 1;
    arrayOfByte[j] = (byte)(paramInt & 0xFF);
    localSegment.limit = k;
    this.size = (2L + this.size);
    return this;
  }

  public Buffer writeShortLe(int paramInt)
  {
    return writeShort(Util.reverseBytesShort((short)paramInt));
  }

  public Buffer writeString(String paramString, int paramInt1, int paramInt2, Charset paramCharset)
  {
    if (paramString == null)
      throw new IllegalArgumentException("string == null");
    if (paramInt1 < 0)
      throw new IllegalAccessError("beginIndex < 0: " + paramInt1);
    if (paramInt2 < paramInt1)
      throw new IllegalArgumentException("endIndex < beginIndex: " + paramInt2 + " < " + paramInt1);
    if (paramInt2 > paramString.length())
      throw new IllegalArgumentException("endIndex > string.length: " + paramInt2 + " > " + paramString.length());
    if (paramCharset == null)
      throw new IllegalArgumentException("charset == null");
    if (paramCharset.equals(Util.UTF_8))
      return writeUtf8(paramString, paramInt1, paramInt2);
    byte[] arrayOfByte = paramString.substring(paramInt1, paramInt2).getBytes(paramCharset);
    return write(arrayOfByte, 0, arrayOfByte.length);
  }

  public Buffer writeString(String paramString, Charset paramCharset)
  {
    return writeString(paramString, 0, paramString.length(), paramCharset);
  }

  public Buffer writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    return writeTo(paramOutputStream, this.size);
  }

  public Buffer writeTo(OutputStream paramOutputStream, long paramLong)
    throws IOException
  {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("out == null");
    Util.checkOffsetAndCount(this.size, 0L, paramLong);
    Segment localSegment1 = this.head;
    while (paramLong > 0L)
    {
      int i = (int)Math.min(paramLong, localSegment1.limit - localSegment1.pos);
      paramOutputStream.write(localSegment1.data, localSegment1.pos, i);
      localSegment1.pos = (i + localSegment1.pos);
      this.size -= i;
      paramLong -= i;
      if (localSegment1.pos != localSegment1.limit)
        continue;
      Segment localSegment2 = localSegment1;
      localSegment1 = localSegment2.pop();
      this.head = localSegment1;
      SegmentPool.recycle(localSegment2);
    }
    return this;
  }

  public Buffer writeUtf8(String paramString)
  {
    return writeUtf8(paramString, 0, paramString.length());
  }

  public Buffer writeUtf8(String paramString, int paramInt1, int paramInt2)
  {
    if (paramString == null)
      throw new IllegalArgumentException("string == null");
    if (paramInt1 < 0)
      throw new IllegalAccessError("beginIndex < 0: " + paramInt1);
    if (paramInt2 < paramInt1)
      throw new IllegalArgumentException("endIndex < beginIndex: " + paramInt2 + " < " + paramInt1);
    if (paramInt2 > paramString.length())
      throw new IllegalArgumentException("endIndex > string.length: " + paramInt2 + " > " + paramString.length());
    int i = paramInt1;
    if (i < paramInt2)
    {
      int j = paramString.charAt(i);
      byte[] arrayOfByte;
      int i1;
      int i4;
      label221: int i6;
      int k;
      if (j < 128)
      {
        Segment localSegment = writableSegment(1);
        arrayOfByte = localSegment.data;
        i1 = localSegment.limit - i;
        int i2 = Math.min(paramInt2, 8192 - i1);
        int i3 = i + 1;
        arrayOfByte[(i1 + i)] = (byte)j;
        i4 = i3;
        if (i4 < i2)
        {
          i6 = paramString.charAt(i4);
          if (i6 < 128);
        }
        else
        {
          int i5 = i4 + i1 - localSegment.limit;
          localSegment.limit = (i5 + localSegment.limit);
          this.size += i5;
          k = i4;
        }
      }
      while (true)
      {
        i = k;
        break;
        int i7 = i4 + 1;
        arrayOfByte[(i1 + i4)] = (byte)i6;
        i4 = i7;
        break label221;
        if (j < 2048)
        {
          writeByte(0xC0 | j >> 6);
          writeByte(0x80 | j & 0x3F);
          k = i + 1;
          continue;
        }
        if ((j < 55296) || (j > 57343))
        {
          writeByte(0xE0 | j >> 12);
          writeByte(0x80 | 0x3F & j >> 6);
          writeByte(0x80 | j & 0x3F);
          k = i + 1;
          continue;
        }
        if (i + 1 < paramInt2);
        for (int m = paramString.charAt(i + 1); ; m = 0)
        {
          if ((j <= 56319) && (m >= 56320) && (m <= 57343))
            break label493;
          writeByte(63);
          i++;
          break;
        }
        label493: int n = 65536 + ((0xFFFF27FF & j) << 10 | 0xFFFF23FF & m);
        writeByte(0xF0 | n >> 18);
        writeByte(0x80 | 0x3F & n >> 12);
        writeByte(0x80 | 0x3F & n >> 6);
        writeByte(0x80 | n & 0x3F);
        k = i + 2;
      }
    }
    return this;
  }

  public Buffer writeUtf8CodePoint(int paramInt)
  {
    if (paramInt < 128)
    {
      writeByte(paramInt);
      return this;
    }
    if (paramInt < 2048)
    {
      writeByte(0xC0 | paramInt >> 6);
      writeByte(0x80 | paramInt & 0x3F);
      return this;
    }
    if (paramInt < 65536)
    {
      if ((paramInt >= 55296) && (paramInt <= 57343))
        throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(paramInt));
      writeByte(0xE0 | paramInt >> 12);
      writeByte(0x80 | 0x3F & paramInt >> 6);
      writeByte(0x80 | paramInt & 0x3F);
      return this;
    }
    if (paramInt <= 1114111)
    {
      writeByte(0xF0 | paramInt >> 18);
      writeByte(0x80 | 0x3F & paramInt >> 12);
      writeByte(0x80 | 0x3F & paramInt >> 6);
      writeByte(0x80 | paramInt & 0x3F);
      return this;
    }
    throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(paramInt));
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okio.Buffer
 * JD-Core Version:    0.6.0
 */