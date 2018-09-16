package okio;

import B;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ByteString
  implements Serializable, Comparable<ByteString>
{
  public static final ByteString EMPTY;
  static final char[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  private static final long serialVersionUID = 1L;
  final byte[] data;
  transient int hashCode;
  transient String utf8;

  static
  {
    EMPTY = of(new byte[0]);
  }

  ByteString(byte[] paramArrayOfByte)
  {
    this.data = paramArrayOfByte;
  }

  static int codePointIndexToCharIndex(String paramString, int paramInt)
  {
    int i = 0;
    int j = 0;
    int k = paramString.length();
    while (i < k)
    {
      if (j == paramInt)
        return i;
      int m = paramString.codePointAt(i);
      if (((Character.isISOControl(m)) && (m != 10) && (m != 13)) || (m == 65533))
        return -1;
      j++;
      i += Character.charCount(m);
    }
    return paramString.length();
  }

  public static ByteString decodeBase64(String paramString)
  {
    if (paramString == null)
      throw new IllegalArgumentException("base64 == null");
    byte[] arrayOfByte = Base64.decode(paramString);
    if (arrayOfByte != null)
      return new ByteString(arrayOfByte);
    return null;
  }

  public static ByteString decodeHex(String paramString)
  {
    if (paramString == null)
      throw new IllegalArgumentException("hex == null");
    if (paramString.length() % 2 != 0)
      throw new IllegalArgumentException("Unexpected hex string: " + paramString);
    byte[] arrayOfByte = new byte[paramString.length() / 2];
    for (int i = 0; i < arrayOfByte.length; i++)
      arrayOfByte[i] = (byte)((decodeHexDigit(paramString.charAt(i * 2)) << 4) + decodeHexDigit(paramString.charAt(1 + i * 2)));
    return of(arrayOfByte);
  }

  private static int decodeHexDigit(char paramChar)
  {
    if ((paramChar >= '0') && (paramChar <= '9'))
      return paramChar - '0';
    if ((paramChar >= 'a') && (paramChar <= 'f'))
      return 10 + (paramChar - 'a');
    if ((paramChar >= 'A') && (paramChar <= 'F'))
      return 10 + (paramChar - 'A');
    throw new IllegalArgumentException("Unexpected hex digit: " + paramChar);
  }

  private ByteString digest(String paramString)
  {
    try
    {
      ByteString localByteString = of(MessageDigest.getInstance(paramString).digest(this.data));
      return localByteString;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
    }
    throw new AssertionError(localNoSuchAlgorithmException);
  }

  public static ByteString encodeString(String paramString, Charset paramCharset)
  {
    if (paramString == null)
      throw new IllegalArgumentException("s == null");
    if (paramCharset == null)
      throw new IllegalArgumentException("charset == null");
    return new ByteString(paramString.getBytes(paramCharset));
  }

  public static ByteString encodeUtf8(String paramString)
  {
    if (paramString == null)
      throw new IllegalArgumentException("s == null");
    ByteString localByteString = new ByteString(paramString.getBytes(Util.UTF_8));
    localByteString.utf8 = paramString;
    return localByteString;
  }

  private ByteString hmac(String paramString, ByteString paramByteString)
  {
    try
    {
      Mac localMac = Mac.getInstance(paramString);
      localMac.init(new SecretKeySpec(paramByteString.toByteArray(), paramString));
      ByteString localByteString = of(localMac.doFinal(this.data));
      return localByteString;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new AssertionError(localNoSuchAlgorithmException);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
    }
    throw new IllegalArgumentException(localInvalidKeyException);
  }

  public static ByteString of(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer == null)
      throw new IllegalArgumentException("data == null");
    byte[] arrayOfByte = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(arrayOfByte);
    return new ByteString(arrayOfByte);
  }

  public static ByteString of(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("data == null");
    return new ByteString((byte[])paramArrayOfByte.clone());
  }

  public static ByteString of(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("data == null");
    Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    return new ByteString(arrayOfByte);
  }

  public static ByteString read(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    if (paramInputStream == null)
      throw new IllegalArgumentException("in == null");
    if (paramInt < 0)
      throw new IllegalArgumentException("byteCount < 0: " + paramInt);
    byte[] arrayOfByte = new byte[paramInt];
    int i = 0;
    while (i < paramInt)
    {
      int j = paramInputStream.read(arrayOfByte, i, paramInt - i);
      if (j == -1)
        throw new EOFException();
      i += j;
    }
    return new ByteString(arrayOfByte);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException
  {
    ByteString localByteString = read(paramObjectInputStream, paramObjectInputStream.readInt());
    try
    {
      Field localField = ByteString.class.getDeclaredField("data");
      localField.setAccessible(true);
      localField.set(this, localByteString.data);
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      throw new AssertionError();
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
    }
    throw new AssertionError();
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.data.length);
    paramObjectOutputStream.write(this.data);
  }

  public ByteBuffer asByteBuffer()
  {
    return ByteBuffer.wrap(this.data).asReadOnlyBuffer();
  }

  public String base64()
  {
    return Base64.encode(this.data);
  }

  public String base64Url()
  {
    return Base64.encodeUrl(this.data);
  }

  public int compareTo(ByteString paramByteString)
  {
    int i = size();
    int j = paramByteString.size();
    int k = 0;
    int m = Math.min(i, j);
    while (true)
      if (k < m)
      {
        int n = 0xFF & getByte(k);
        int i1 = 0xFF & paramByteString.getByte(k);
        if (n == i1)
        {
          k++;
          continue;
        }
        if (n >= i1)
          break;
      }
    do
    {
      return -1;
      return 1;
      if (i == j)
        return 0;
    }
    while (i < j);
    return 1;
  }

  public final boolean endsWith(ByteString paramByteString)
  {
    return rangeEquals(size() - paramByteString.size(), paramByteString, 0, paramByteString.size());
  }

  public final boolean endsWith(byte[] paramArrayOfByte)
  {
    return rangeEquals(size() - paramArrayOfByte.length, paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public boolean equals(Object paramObject)
  {
    if (paramObject == this)
      return true;
    if (((paramObject instanceof ByteString)) && (((ByteString)paramObject).size() == this.data.length) && (((ByteString)paramObject).rangeEquals(0, this.data, 0, this.data.length)));
    for (int i = 1; ; i = 0)
      return i;
  }

  public byte getByte(int paramInt)
  {
    return this.data[paramInt];
  }

  public int hashCode()
  {
    int i = this.hashCode;
    if (i != 0)
      return i;
    int j = Arrays.hashCode(this.data);
    this.hashCode = j;
    return j;
  }

  public String hex()
  {
    char[] arrayOfChar = new char[2 * this.data.length];
    byte[] arrayOfByte = this.data;
    int i = arrayOfByte.length;
    int j = 0;
    int k = 0;
    while (j < i)
    {
      int m = arrayOfByte[j];
      int n = k + 1;
      arrayOfChar[k] = HEX_DIGITS[(0xF & m >> 4)];
      k = n + 1;
      arrayOfChar[n] = HEX_DIGITS[(m & 0xF)];
      j++;
    }
    return new String(arrayOfChar);
  }

  public ByteString hmacSha1(ByteString paramByteString)
  {
    return hmac("HmacSHA1", paramByteString);
  }

  public ByteString hmacSha256(ByteString paramByteString)
  {
    return hmac("HmacSHA256", paramByteString);
  }

  public final int indexOf(ByteString paramByteString)
  {
    return indexOf(paramByteString.internalArray(), 0);
  }

  public final int indexOf(ByteString paramByteString, int paramInt)
  {
    return indexOf(paramByteString.internalArray(), paramInt);
  }

  public final int indexOf(byte[] paramArrayOfByte)
  {
    return indexOf(paramArrayOfByte, 0);
  }

  public int indexOf(byte[] paramArrayOfByte, int paramInt)
  {
    int i = Math.max(paramInt, 0);
    int j = this.data.length - paramArrayOfByte.length;
    while (i <= j)
    {
      if (Util.arrayRangeEquals(this.data, i, paramArrayOfByte, 0, paramArrayOfByte.length))
        return i;
      i++;
    }
    return -1;
  }

  byte[] internalArray()
  {
    return this.data;
  }

  public final int lastIndexOf(ByteString paramByteString)
  {
    return lastIndexOf(paramByteString.internalArray(), size());
  }

  public final int lastIndexOf(ByteString paramByteString, int paramInt)
  {
    return lastIndexOf(paramByteString.internalArray(), paramInt);
  }

  public final int lastIndexOf(byte[] paramArrayOfByte)
  {
    return lastIndexOf(paramArrayOfByte, size());
  }

  public int lastIndexOf(byte[] paramArrayOfByte, int paramInt)
  {
    for (int i = Math.min(paramInt, this.data.length - paramArrayOfByte.length); i >= 0; i--)
      if (Util.arrayRangeEquals(this.data, i, paramArrayOfByte, 0, paramArrayOfByte.length))
        return i;
    return -1;
  }

  public ByteString md5()
  {
    return digest("MD5");
  }

  public boolean rangeEquals(int paramInt1, ByteString paramByteString, int paramInt2, int paramInt3)
  {
    return paramByteString.rangeEquals(paramInt2, this.data, paramInt1, paramInt3);
  }

  public boolean rangeEquals(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    return (paramInt1 >= 0) && (paramInt1 <= this.data.length - paramInt3) && (paramInt2 >= 0) && (paramInt2 <= paramArrayOfByte.length - paramInt3) && (Util.arrayRangeEquals(this.data, paramInt1, paramArrayOfByte, paramInt2, paramInt3));
  }

  public ByteString sha1()
  {
    return digest("SHA-1");
  }

  public ByteString sha256()
  {
    return digest("SHA-256");
  }

  public int size()
  {
    return this.data.length;
  }

  public final boolean startsWith(ByteString paramByteString)
  {
    return rangeEquals(0, paramByteString, 0, paramByteString.size());
  }

  public final boolean startsWith(byte[] paramArrayOfByte)
  {
    return rangeEquals(0, paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public String string(Charset paramCharset)
  {
    if (paramCharset == null)
      throw new IllegalArgumentException("charset == null");
    return new String(this.data, paramCharset);
  }

  public ByteString substring(int paramInt)
  {
    return substring(paramInt, this.data.length);
  }

  public ByteString substring(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("beginIndex < 0");
    if (paramInt2 > this.data.length)
      throw new IllegalArgumentException("endIndex > length(" + this.data.length + ")");
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException("endIndex < beginIndex");
    if ((paramInt1 == 0) && (paramInt2 == this.data.length))
      return this;
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.data, paramInt1, arrayOfByte, 0, i);
    return new ByteString(arrayOfByte);
  }

  public ByteString toAsciiLowercase()
  {
    int i = 0;
    while (i < this.data.length)
    {
      int j = this.data[i];
      if ((j < 65) || (j > 90))
      {
        i++;
        continue;
      }
      byte[] arrayOfByte = (byte[])this.data.clone();
      int k = i + 1;
      arrayOfByte[i] = (byte)(j + 32);
      int m = k;
      if (m < arrayOfByte.length)
      {
        int n = arrayOfByte[m];
        if ((n < 65) || (n > 90));
        while (true)
        {
          m++;
          break;
          arrayOfByte[m] = (byte)(n + 32);
        }
      }
      this = new ByteString(arrayOfByte);
    }
    return this;
  }

  public ByteString toAsciiUppercase()
  {
    int i = 0;
    while (i < this.data.length)
    {
      int j = this.data[i];
      if ((j < 97) || (j > 122))
      {
        i++;
        continue;
      }
      byte[] arrayOfByte = (byte[])this.data.clone();
      int k = i + 1;
      arrayOfByte[i] = (byte)(j - 32);
      int m = k;
      if (m < arrayOfByte.length)
      {
        int n = arrayOfByte[m];
        if ((n < 97) || (n > 122));
        while (true)
        {
          m++;
          break;
          arrayOfByte[m] = (byte)(n - 32);
        }
      }
      this = new ByteString(arrayOfByte);
    }
    return this;
  }

  public byte[] toByteArray()
  {
    return (byte[])this.data.clone();
  }

  public String toString()
  {
    if (this.data.length == 0)
      return "[size=0]";
    String str1 = utf8();
    int i = codePointIndexToCharIndex(str1, 64);
    if (i == -1)
    {
      if (this.data.length <= 64)
        return "[hex=" + hex() + "]";
      return "[size=" + this.data.length + " hex=" + substring(0, 64).hex() + "…]";
    }
    String str2 = str1.substring(0, i).replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r");
    if (i < str1.length())
      return "[size=" + this.data.length + " text=" + str2 + "…]";
    return "[text=" + str2 + "]";
  }

  public String utf8()
  {
    String str1 = this.utf8;
    if (str1 != null)
      return str1;
    String str2 = new String(this.data, Util.UTF_8);
    this.utf8 = str2;
    return str2;
  }

  public void write(OutputStream paramOutputStream)
    throws IOException
  {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("out == null");
    paramOutputStream.write(this.data);
  }

  void write(Buffer paramBuffer)
  {
    paramBuffer.write(this.data, 0, this.data.length);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okio.ByteString
 * JD-Core Version:    0.6.0
 */