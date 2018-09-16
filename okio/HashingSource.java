package okio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class HashingSource extends ForwardingSource
{
  private final Mac mac;
  private final MessageDigest messageDigest;

  private HashingSource(Source paramSource, String paramString)
  {
    super(paramSource);
    try
    {
      this.messageDigest = MessageDigest.getInstance(paramString);
      this.mac = null;
      return;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
    }
    throw new AssertionError();
  }

  private HashingSource(Source paramSource, ByteString paramByteString, String paramString)
  {
    super(paramSource);
    try
    {
      this.mac = Mac.getInstance(paramString);
      this.mac.init(new SecretKeySpec(paramByteString.toByteArray(), paramString));
      this.messageDigest = null;
      return;
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

  public static HashingSource hmacSha1(Source paramSource, ByteString paramByteString)
  {
    return new HashingSource(paramSource, paramByteString, "HmacSHA1");
  }

  public static HashingSource hmacSha256(Source paramSource, ByteString paramByteString)
  {
    return new HashingSource(paramSource, paramByteString, "HmacSHA256");
  }

  public static HashingSource md5(Source paramSource)
  {
    return new HashingSource(paramSource, "MD5");
  }

  public static HashingSource sha1(Source paramSource)
  {
    return new HashingSource(paramSource, "SHA-1");
  }

  public static HashingSource sha256(Source paramSource)
  {
    return new HashingSource(paramSource, "SHA-256");
  }

  public ByteString hash()
  {
    if (this.messageDigest != null);
    for (byte[] arrayOfByte = this.messageDigest.digest(); ; arrayOfByte = this.mac.doFinal())
      return ByteString.of(arrayOfByte);
  }

  public long read(Buffer paramBuffer, long paramLong)
    throws IOException
  {
    long l1 = super.read(paramBuffer, paramLong);
    if (l1 != -1L)
    {
      long l2 = paramBuffer.size - l1;
      long l3 = paramBuffer.size;
      Segment localSegment = paramBuffer.head;
      while (l3 > l2)
      {
        localSegment = localSegment.prev;
        l3 -= localSegment.limit - localSegment.pos;
      }
      if (l3 < paramBuffer.size)
      {
        int i = (int)(l2 + localSegment.pos - l3);
        if (this.messageDigest != null)
          this.messageDigest.update(localSegment.data, i, localSegment.limit - i);
        while (true)
        {
          l3 += localSegment.limit - localSegment.pos;
          l2 = l3;
          localSegment = localSegment.next;
          break;
          this.mac.update(localSegment.data, i, localSegment.limit - i);
        }
      }
    }
    return l1;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okio.HashingSource
 * JD-Core Version:    0.6.0
 */