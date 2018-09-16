package okhttp3.internal;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Array;
import java.net.IDN;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

public final class Util
{
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  public static final RequestBody EMPTY_REQUEST;
  public static final ResponseBody EMPTY_RESPONSE;
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  public static final TimeZone UTC;
  private static final Charset UTF_16_BE;
  private static final ByteString UTF_16_BE_BOM;
  private static final Charset UTF_16_LE;
  private static final ByteString UTF_16_LE_BOM;
  private static final Charset UTF_32_BE;
  private static final ByteString UTF_32_BE_BOM;
  private static final Charset UTF_32_LE;
  private static final ByteString UTF_32_LE_BOM;
  public static final Charset UTF_8;
  private static final ByteString UTF_8_BOM;
  private static final Pattern VERIFY_AS_IP_ADDRESS;

  static
  {
    EMPTY_RESPONSE = ResponseBody.create(null, EMPTY_BYTE_ARRAY);
    EMPTY_REQUEST = RequestBody.create(null, EMPTY_BYTE_ARRAY);
    UTF_8_BOM = ByteString.decodeHex("efbbbf");
    UTF_16_BE_BOM = ByteString.decodeHex("feff");
    UTF_16_LE_BOM = ByteString.decodeHex("fffe");
    UTF_32_BE_BOM = ByteString.decodeHex("0000ffff");
    UTF_32_LE_BOM = ByteString.decodeHex("ffff0000");
    UTF_8 = Charset.forName("UTF-8");
    UTF_16_BE = Charset.forName("UTF-16BE");
    UTF_16_LE = Charset.forName("UTF-16LE");
    UTF_32_BE = Charset.forName("UTF-32BE");
    UTF_32_LE = Charset.forName("UTF-32LE");
    UTC = TimeZone.getTimeZone("GMT");
    VERIFY_AS_IP_ADDRESS = Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");
  }

  public static Charset bomAwareCharset(BufferedSource paramBufferedSource, Charset paramCharset)
    throws IOException
  {
    if (paramBufferedSource.rangeEquals(0L, UTF_8_BOM))
    {
      paramBufferedSource.skip(UTF_8_BOM.size());
      paramCharset = UTF_8;
    }
    do
    {
      return paramCharset;
      if (paramBufferedSource.rangeEquals(0L, UTF_16_BE_BOM))
      {
        paramBufferedSource.skip(UTF_16_BE_BOM.size());
        return UTF_16_BE;
      }
      if (paramBufferedSource.rangeEquals(0L, UTF_16_LE_BOM))
      {
        paramBufferedSource.skip(UTF_16_LE_BOM.size());
        return UTF_16_LE;
      }
      if (!paramBufferedSource.rangeEquals(0L, UTF_32_BE_BOM))
        continue;
      paramBufferedSource.skip(UTF_32_BE_BOM.size());
      return UTF_32_BE;
    }
    while (!paramBufferedSource.rangeEquals(0L, UTF_32_LE_BOM));
    paramBufferedSource.skip(UTF_32_LE_BOM.size());
    return UTF_32_LE;
  }

  public static void checkOffsetAndCount(long paramLong1, long paramLong2, long paramLong3)
  {
    if (((paramLong2 | paramLong3) < 0L) || (paramLong2 > paramLong1) || (paramLong1 - paramLong2 < paramLong3))
      throw new ArrayIndexOutOfBoundsException();
  }

  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null);
    try
    {
      paramCloseable.close();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
    }
  }

  public static void closeQuietly(ServerSocket paramServerSocket)
  {
    if (paramServerSocket != null);
    try
    {
      paramServerSocket.close();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
    }
  }

  public static void closeQuietly(Socket paramSocket)
  {
    if (paramSocket != null);
    try
    {
      paramSocket.close();
      return;
    }
    catch (AssertionError localAssertionError)
    {
      while (isAndroidGetsocknameError(localAssertionError));
      throw localAssertionError;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
    }
  }

  public static String[] concat(String[] paramArrayOfString, String paramString)
  {
    String[] arrayOfString = new String[1 + paramArrayOfString.length];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramArrayOfString.length);
    arrayOfString[(-1 + arrayOfString.length)] = paramString;
    return arrayOfString;
  }

  private static boolean containsInvalidHostnameAsciiCodes(String paramString)
  {
    for (int i = 0; i < paramString.length(); i++)
    {
      int j = paramString.charAt(i);
      if ((j <= 31) || (j >= 127));
      do
        return true;
      while (" #%/:?@[\\]".indexOf(j) != -1);
    }
    return false;
  }

  public static int delimiterOffset(String paramString, int paramInt1, int paramInt2, char paramChar)
  {
    for (int i = paramInt1; i < paramInt2; i++)
      if (paramString.charAt(i) == paramChar)
        return i;
    return paramInt2;
  }

  public static int delimiterOffset(String paramString1, int paramInt1, int paramInt2, String paramString2)
  {
    for (int i = paramInt1; i < paramInt2; i++)
      if (paramString2.indexOf(paramString1.charAt(i)) != -1)
        return i;
    return paramInt2;
  }

  public static boolean discard(Source paramSource, int paramInt, TimeUnit paramTimeUnit)
  {
    try
    {
      boolean bool = skipAll(paramSource, paramInt, paramTimeUnit);
      return bool;
    }
    catch (IOException localIOException)
    {
    }
    return false;
  }

  public static String domainToAscii(String paramString)
  {
    String str;
    try
    {
      str = IDN.toASCII(paramString).toLowerCase(Locale.US);
      if (str.isEmpty())
        return null;
      boolean bool = containsInvalidHostnameAsciiCodes(str);
      if (bool)
        return null;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      str = null;
    }
    return str;
  }

  public static boolean equal(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)));
  }

  public static String format(String paramString, Object[] paramArrayOfObject)
  {
    return String.format(Locale.US, paramString, paramArrayOfObject);
  }

  public static String hostHeader(HttpUrl paramHttpUrl, boolean paramBoolean)
  {
    if (paramHttpUrl.host().contains(":"));
    for (String str = "[" + paramHttpUrl.host() + "]"; ; str = paramHttpUrl.host())
    {
      if ((paramBoolean) || (paramHttpUrl.port() != HttpUrl.defaultPort(paramHttpUrl.scheme())))
        str = str + ":" + paramHttpUrl.port();
      return str;
    }
  }

  public static <T> List<T> immutableList(List<T> paramList)
  {
    return Collections.unmodifiableList(new ArrayList(paramList));
  }

  public static <T> List<T> immutableList(T[] paramArrayOfT)
  {
    return Collections.unmodifiableList(Arrays.asList((Object[])paramArrayOfT.clone()));
  }

  public static <T> int indexOf(T[] paramArrayOfT, T paramT)
  {
    int i = 0;
    int j = paramArrayOfT.length;
    while (i < j)
    {
      if (equal(paramArrayOfT[i], paramT))
        return i;
      i++;
    }
    return -1;
  }

  public static int indexOfControlOrNonAscii(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    while (i < j)
    {
      int k = paramString.charAt(i);
      if ((k <= 31) || (k >= 127))
        return i;
      i++;
    }
    return -1;
  }

  private static <T> List<T> intersect(T[] paramArrayOfT1, T[] paramArrayOfT2)
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramArrayOfT1.length;
    int j = 0;
    if (j < i)
    {
      T ? = paramArrayOfT1[j];
      int k = paramArrayOfT2.length;
      for (int m = 0; ; m++)
      {
        if (m < k)
        {
          T ? = paramArrayOfT2[m];
          if (!?.equals(?))
            continue;
          localArrayList.add(?);
        }
        j++;
        break;
      }
    }
    return localArrayList;
  }

  public static <T> T[] intersect(Class<T> paramClass, T[] paramArrayOfT1, T[] paramArrayOfT2)
  {
    List localList = intersect(paramArrayOfT1, paramArrayOfT2);
    return localList.toArray((Object[])(Object[])Array.newInstance(paramClass, localList.size()));
  }

  public static boolean isAndroidGetsocknameError(AssertionError paramAssertionError)
  {
    return (paramAssertionError.getCause() != null) && (paramAssertionError.getMessage() != null) && (paramAssertionError.getMessage().contains("getsockname failed"));
  }

  public static boolean skipAll(Source paramSource, int paramInt, TimeUnit paramTimeUnit)
    throws IOException
  {
    long l1 = System.nanoTime();
    long l2;
    if (paramSource.timeout().hasDeadline())
      l2 = paramSource.timeout().deadlineNanoTime() - l1;
    while (true)
    {
      paramSource.timeout().deadlineNanoTime(l1 + Math.min(l2, paramTimeUnit.toNanos(paramInt)));
      try
      {
        Buffer localBuffer = new Buffer();
        while (paramSource.read(localBuffer, 8192L) != -1L)
          localBuffer.clear();
      }
      catch (InterruptedIOException localInterruptedIOException)
      {
        if (l2 == 9223372036854775807L)
          paramSource.timeout().clearDeadline();
        while (true)
        {
          return false;
          l2 = 9223372036854775807L;
          break;
          if (l2 == 9223372036854775807L)
            paramSource.timeout().clearDeadline();
          while (true)
          {
            return true;
            paramSource.timeout().deadlineNanoTime(l1 + l2);
          }
          paramSource.timeout().deadlineNanoTime(l1 + l2);
        }
      }
      finally
      {
        if (l2 != 9223372036854775807L)
          break label197;
      }
    }
    paramSource.timeout().clearDeadline();
    while (true)
    {
      throw localObject;
      label197: paramSource.timeout().deadlineNanoTime(l1 + l2);
    }
  }

  public static int skipLeadingAsciiWhitespace(String paramString, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++)
      switch (paramString.charAt(i))
      {
      default:
        return i;
      case '\t':
      case '\n':
      case '\f':
      case '\r':
      case ' ':
      }
    return paramInt2;
  }

  public static int skipTrailingAsciiWhitespace(String paramString, int paramInt1, int paramInt2)
  {
    for (int i = paramInt2 - 1; ; i--)
    {
      if (i >= paramInt1);
      switch (paramString.charAt(i))
      {
      default:
        paramInt1 = i + 1;
        return paramInt1;
      case '\t':
      case '\n':
      case '\f':
      case '\r':
      case ' ':
      }
    }
  }

  public static ThreadFactory threadFactory(String paramString, boolean paramBoolean)
  {
    return new ThreadFactory(paramString, paramBoolean)
    {
      public Thread newThread(Runnable paramRunnable)
      {
        Thread localThread = new Thread(paramRunnable, this.val$name);
        localThread.setDaemon(this.val$daemon);
        return localThread;
      }
    };
  }

  public static String toHumanReadableAscii(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    while (i < j)
    {
      int k = paramString.codePointAt(i);
      if ((k > 31) && (k < 127))
      {
        i += Character.charCount(k);
        continue;
      }
      Buffer localBuffer = new Buffer();
      localBuffer.writeUtf8(paramString, 0, i);
      int m = i;
      if (m < j)
      {
        int n = paramString.codePointAt(m);
        if ((n > 31) && (n < 127));
        for (int i1 = n; ; i1 = 63)
        {
          localBuffer.writeUtf8CodePoint(i1);
          m += Character.charCount(n);
          break;
        }
      }
      paramString = localBuffer.readUtf8();
    }
    return paramString;
  }

  public static String trimSubstring(String paramString, int paramInt1, int paramInt2)
  {
    int i = skipLeadingAsciiWhitespace(paramString, paramInt1, paramInt2);
    return paramString.substring(i, skipTrailingAsciiWhitespace(paramString, i, paramInt2));
  }

  public static boolean verifyAsIpAddress(String paramString)
  {
    return VERIFY_AS_IP_ADDRESS.matcher(paramString).matches();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.Util
 * JD-Core Version:    0.6.0
 */