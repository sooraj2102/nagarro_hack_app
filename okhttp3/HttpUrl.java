package okhttp3;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import okhttp3.internal.Util;
import okio.Buffer;

public final class HttpUrl
{
  static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
  static final String FRAGMENT_ENCODE_SET = "";
  static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";
  private static final char[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
  static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
  static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
  static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
  static final String QUERY_COMPONENT_ENCODE_SET = " \"'<>#&=";
  static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
  static final String QUERY_ENCODE_SET = " \"'<>#";
  static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
  private final String fragment;
  final String host;
  private final String password;
  private final List<String> pathSegments;
  final int port;
  private final List<String> queryNamesAndValues;
  final String scheme;
  private final String url;
  private final String username;

  HttpUrl(Builder paramBuilder)
  {
    this.scheme = paramBuilder.scheme;
    this.username = percentDecode(paramBuilder.encodedUsername, false);
    this.password = percentDecode(paramBuilder.encodedPassword, false);
    this.host = paramBuilder.host;
    this.port = paramBuilder.effectivePort();
    this.pathSegments = percentDecode(paramBuilder.encodedPathSegments, false);
    if (paramBuilder.encodedQueryNamesAndValues != null);
    for (List localList = percentDecode(paramBuilder.encodedQueryNamesAndValues, true); ; localList = null)
    {
      this.queryNamesAndValues = localList;
      String str1 = paramBuilder.encodedFragment;
      String str2 = null;
      if (str1 != null)
        str2 = percentDecode(paramBuilder.encodedFragment, false);
      this.fragment = str2;
      this.url = paramBuilder.toString();
      return;
    }
  }

  static String canonicalize(String paramString1, int paramInt1, int paramInt2, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    int i = paramInt1;
    while (i < paramInt2)
    {
      int j = paramString1.codePointAt(i);
      if ((j < 32) || (j == 127) || ((j >= 128) && (paramBoolean4)) || (paramString2.indexOf(j) != -1) || ((j == 37) && ((!paramBoolean1) || ((paramBoolean2) && (!percentEncoded(paramString1, i, paramInt2))))) || ((j == 43) && (paramBoolean3)))
      {
        Buffer localBuffer = new Buffer();
        localBuffer.writeUtf8(paramString1, paramInt1, i);
        canonicalize(localBuffer, paramString1, i, paramInt2, paramString2, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4);
        return localBuffer.readUtf8();
      }
      i += Character.charCount(j);
    }
    return paramString1.substring(paramInt1, paramInt2);
  }

  static String canonicalize(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    return canonicalize(paramString1, 0, paramString1.length(), paramString2, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4);
  }

  static void canonicalize(Buffer paramBuffer, String paramString1, int paramInt1, int paramInt2, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    Buffer localBuffer = null;
    int i = paramInt1;
    if (i < paramInt2)
    {
      int j = paramString1.codePointAt(i);
      if ((paramBoolean1) && ((j == 9) || (j == 10) || (j == 12) || (j == 13)));
      while (true)
      {
        i += Character.charCount(j);
        break;
        if ((j == 43) && (paramBoolean3))
        {
          if (paramBoolean1);
          for (String str = "+"; ; str = "%2B")
          {
            paramBuffer.writeUtf8(str);
            break;
          }
        }
        if ((j < 32) || (j == 127) || ((j >= 128) && (paramBoolean4)) || (paramString2.indexOf(j) != -1) || ((j == 37) && ((!paramBoolean1) || ((paramBoolean2) && (!percentEncoded(paramString1, i, paramInt2))))))
        {
          if (localBuffer == null)
            localBuffer = new Buffer();
          localBuffer.writeUtf8CodePoint(j);
          while (!localBuffer.exhausted())
          {
            int k = 0xFF & localBuffer.readByte();
            paramBuffer.writeByte(37);
            paramBuffer.writeByte(HEX_DIGITS[(0xF & k >> 4)]);
            paramBuffer.writeByte(HEX_DIGITS[(k & 0xF)]);
          }
          continue;
        }
        paramBuffer.writeUtf8CodePoint(j);
      }
    }
  }

  static int decodeHexDigit(char paramChar)
  {
    if ((paramChar >= '0') && (paramChar <= '9'))
      return paramChar - '0';
    if ((paramChar >= 'a') && (paramChar <= 'f'))
      return 10 + (paramChar - 'a');
    if ((paramChar >= 'A') && (paramChar <= 'F'))
      return 10 + (paramChar - 'A');
    return -1;
  }

  public static int defaultPort(String paramString)
  {
    if (paramString.equals("http"))
      return 80;
    if (paramString.equals("https"))
      return 443;
    return -1;
  }

  public static HttpUrl get(URI paramURI)
  {
    return parse(paramURI.toString());
  }

  public static HttpUrl get(URL paramURL)
  {
    return parse(paramURL.toString());
  }

  static HttpUrl getChecked(String paramString)
    throws MalformedURLException, UnknownHostException
  {
    Builder localBuilder = new Builder();
    HttpUrl.Builder.ParseResult localParseResult = localBuilder.parse(null, paramString);
    switch (1.$SwitchMap$okhttp3$HttpUrl$Builder$ParseResult[localParseResult.ordinal()])
    {
    default:
      throw new MalformedURLException("Invalid URL: " + localParseResult + " for " + paramString);
    case 1:
      return localBuilder.build();
    case 2:
    }
    throw new UnknownHostException("Invalid host: " + paramString);
  }

  static void namesAndValuesToQueryString(StringBuilder paramStringBuilder, List<String> paramList)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      String str1 = (String)paramList.get(i);
      String str2 = (String)paramList.get(i + 1);
      if (i > 0)
        paramStringBuilder.append('&');
      paramStringBuilder.append(str1);
      if (str2 != null)
      {
        paramStringBuilder.append('=');
        paramStringBuilder.append(str2);
      }
      i += 2;
    }
  }

  public static HttpUrl parse(String paramString)
  {
    Builder localBuilder = new Builder();
    HttpUrl.Builder.ParseResult localParseResult1 = localBuilder.parse(null, paramString);
    HttpUrl.Builder.ParseResult localParseResult2 = HttpUrl.Builder.ParseResult.SUCCESS;
    HttpUrl localHttpUrl = null;
    if (localParseResult1 == localParseResult2)
      localHttpUrl = localBuilder.build();
    return localHttpUrl;
  }

  static void pathSegmentsToString(StringBuilder paramStringBuilder, List<String> paramList)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      paramStringBuilder.append('/');
      paramStringBuilder.append((String)paramList.get(i));
      i++;
    }
  }

  static String percentDecode(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    for (int i = paramInt1; i < paramInt2; i++)
    {
      int j = paramString.charAt(i);
      if ((j != 37) && ((j != 43) || (!paramBoolean)))
        continue;
      Buffer localBuffer = new Buffer();
      localBuffer.writeUtf8(paramString, paramInt1, i);
      percentDecode(localBuffer, paramString, i, paramInt2, paramBoolean);
      return localBuffer.readUtf8();
    }
    return paramString.substring(paramInt1, paramInt2);
  }

  static String percentDecode(String paramString, boolean paramBoolean)
  {
    return percentDecode(paramString, 0, paramString.length(), paramBoolean);
  }

  private List<String> percentDecode(List<String> paramList, boolean paramBoolean)
  {
    int i = paramList.size();
    ArrayList localArrayList = new ArrayList(i);
    int j = 0;
    if (j < i)
    {
      String str1 = (String)paramList.get(j);
      if (str1 != null);
      for (String str2 = percentDecode(str1, paramBoolean); ; str2 = null)
      {
        localArrayList.add(str2);
        j++;
        break;
      }
    }
    return Collections.unmodifiableList(localArrayList);
  }

  static void percentDecode(Buffer paramBuffer, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramInt1;
    if (i < paramInt2)
    {
      int j = paramString.codePointAt(i);
      if ((j == 37) && (i + 2 < paramInt2))
      {
        int k = decodeHexDigit(paramString.charAt(i + 1));
        int m = decodeHexDigit(paramString.charAt(i + 2));
        if ((k == -1) || (m == -1))
          break label120;
        paramBuffer.writeByte(m + (k << 4));
        i += 2;
      }
      while (true)
      {
        i += Character.charCount(j);
        break;
        if ((j == 43) && (paramBoolean))
        {
          paramBuffer.writeByte(32);
          continue;
        }
        label120: paramBuffer.writeUtf8CodePoint(j);
      }
    }
  }

  static boolean percentEncoded(String paramString, int paramInt1, int paramInt2)
  {
    return (paramInt1 + 2 < paramInt2) && (paramString.charAt(paramInt1) == '%') && (decodeHexDigit(paramString.charAt(paramInt1 + 1)) != -1) && (decodeHexDigit(paramString.charAt(paramInt1 + 2)) != -1);
  }

  static List<String> queryStringToNamesAndValues(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (i <= paramString.length())
    {
      int j = paramString.indexOf('&', i);
      if (j == -1)
        j = paramString.length();
      int k = paramString.indexOf('=', i);
      if ((k == -1) || (k > j))
      {
        localArrayList.add(paramString.substring(i, j));
        localArrayList.add(null);
      }
      while (true)
      {
        i = j + 1;
        break;
        localArrayList.add(paramString.substring(i, k));
        localArrayList.add(paramString.substring(k + 1, j));
      }
    }
    return localArrayList;
  }

  public String encodedFragment()
  {
    if (this.fragment == null)
      return null;
    int i = 1 + this.url.indexOf('#');
    return this.url.substring(i);
  }

  public String encodedPassword()
  {
    if (this.password.isEmpty())
      return "";
    int i = 1 + this.url.indexOf(':', 3 + this.scheme.length());
    int j = this.url.indexOf('@');
    return this.url.substring(i, j);
  }

  public String encodedPath()
  {
    int i = this.url.indexOf('/', 3 + this.scheme.length());
    int j = Util.delimiterOffset(this.url, i, this.url.length(), "?#");
    return this.url.substring(i, j);
  }

  public List<String> encodedPathSegments()
  {
    int i = this.url.indexOf('/', 3 + this.scheme.length());
    int j = Util.delimiterOffset(this.url, i, this.url.length(), "?#");
    ArrayList localArrayList = new ArrayList();
    int n;
    for (int k = i; k < j; k = n)
    {
      int m = k + 1;
      n = Util.delimiterOffset(this.url, m, j, '/');
      localArrayList.add(this.url.substring(m, n));
    }
    return localArrayList;
  }

  public String encodedQuery()
  {
    if (this.queryNamesAndValues == null)
      return null;
    int i = 1 + this.url.indexOf('?');
    int j = Util.delimiterOffset(this.url, i + 1, this.url.length(), '#');
    return this.url.substring(i, j);
  }

  public String encodedUsername()
  {
    if (this.username.isEmpty())
      return "";
    int i = 3 + this.scheme.length();
    int j = Util.delimiterOffset(this.url, i, this.url.length(), ":@");
    return this.url.substring(i, j);
  }

  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof HttpUrl)) && (((HttpUrl)paramObject).url.equals(this.url));
  }

  public String fragment()
  {
    return this.fragment;
  }

  public int hashCode()
  {
    return this.url.hashCode();
  }

  public String host()
  {
    return this.host;
  }

  public boolean isHttps()
  {
    return this.scheme.equals("https");
  }

  public Builder newBuilder()
  {
    Builder localBuilder = new Builder();
    localBuilder.scheme = this.scheme;
    localBuilder.encodedUsername = encodedUsername();
    localBuilder.encodedPassword = encodedPassword();
    localBuilder.host = this.host;
    if (this.port != defaultPort(this.scheme));
    for (int i = this.port; ; i = -1)
    {
      localBuilder.port = i;
      localBuilder.encodedPathSegments.clear();
      localBuilder.encodedPathSegments.addAll(encodedPathSegments());
      localBuilder.encodedQuery(encodedQuery());
      localBuilder.encodedFragment = encodedFragment();
      return localBuilder;
    }
  }

  public Builder newBuilder(String paramString)
  {
    Builder localBuilder = new Builder();
    if (localBuilder.parse(this, paramString) == HttpUrl.Builder.ParseResult.SUCCESS)
      return localBuilder;
    return null;
  }

  public String password()
  {
    return this.password;
  }

  public List<String> pathSegments()
  {
    return this.pathSegments;
  }

  public int pathSize()
  {
    return this.pathSegments.size();
  }

  public int port()
  {
    return this.port;
  }

  public String query()
  {
    if (this.queryNamesAndValues == null)
      return null;
    StringBuilder localStringBuilder = new StringBuilder();
    namesAndValuesToQueryString(localStringBuilder, this.queryNamesAndValues);
    return localStringBuilder.toString();
  }

  public String queryParameter(String paramString)
  {
    if (this.queryNamesAndValues == null);
    while (true)
    {
      return null;
      int i = 0;
      int j = this.queryNamesAndValues.size();
      while (i < j)
      {
        if (paramString.equals(this.queryNamesAndValues.get(i)))
          return (String)this.queryNamesAndValues.get(i + 1);
        i += 2;
      }
    }
  }

  public String queryParameterName(int paramInt)
  {
    if (this.queryNamesAndValues == null)
      throw new IndexOutOfBoundsException();
    return (String)this.queryNamesAndValues.get(paramInt * 2);
  }

  public Set<String> queryParameterNames()
  {
    if (this.queryNamesAndValues == null)
      return Collections.emptySet();
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    int i = 0;
    int j = this.queryNamesAndValues.size();
    while (i < j)
    {
      localLinkedHashSet.add(this.queryNamesAndValues.get(i));
      i += 2;
    }
    return Collections.unmodifiableSet(localLinkedHashSet);
  }

  public String queryParameterValue(int paramInt)
  {
    if (this.queryNamesAndValues == null)
      throw new IndexOutOfBoundsException();
    return (String)this.queryNamesAndValues.get(1 + paramInt * 2);
  }

  public List<String> queryParameterValues(String paramString)
  {
    if (this.queryNamesAndValues == null)
      return Collections.emptyList();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int j = this.queryNamesAndValues.size();
    while (i < j)
    {
      if (paramString.equals(this.queryNamesAndValues.get(i)))
        localArrayList.add(this.queryNamesAndValues.get(i + 1));
      i += 2;
    }
    return Collections.unmodifiableList(localArrayList);
  }

  public int querySize()
  {
    if (this.queryNamesAndValues != null)
      return this.queryNamesAndValues.size() / 2;
    return 0;
  }

  public String redact()
  {
    return newBuilder("/...").username("").password("").build().toString();
  }

  public HttpUrl resolve(String paramString)
  {
    Builder localBuilder = newBuilder(paramString);
    if (localBuilder != null)
      return localBuilder.build();
    return null;
  }

  public String scheme()
  {
    return this.scheme;
  }

  public String toString()
  {
    return this.url;
  }

  public URI uri()
  {
    String str = newBuilder().reencodeForUri().toString();
    try
    {
      URI localURI1 = new URI(str);
      return localURI1;
    }
    catch (URISyntaxException localURISyntaxException)
    {
      try
      {
        URI localURI2 = URI.create(str.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]", ""));
        return localURI2;
      }
      catch (Exception localException)
      {
      }
    }
    throw new RuntimeException(localURISyntaxException);
  }

  public URL url()
  {
    try
    {
      URL localURL = new URL(this.url);
      return localURL;
    }
    catch (MalformedURLException localMalformedURLException)
    {
    }
    throw new RuntimeException(localMalformedURLException);
  }

  public String username()
  {
    return this.username;
  }

  public static final class Builder
  {
    String encodedFragment;
    String encodedPassword = "";
    final List<String> encodedPathSegments = new ArrayList();
    List<String> encodedQueryNamesAndValues;
    String encodedUsername = "";
    String host;
    int port = -1;
    String scheme;

    public Builder()
    {
      this.encodedPathSegments.add("");
    }

    private Builder addPathSegments(String paramString, boolean paramBoolean)
    {
      int i = 0;
      int j = Util.delimiterOffset(paramString, i, paramString.length(), "/\\");
      if (j < paramString.length());
      for (boolean bool = true; ; bool = false)
      {
        push(paramString, i, j, bool, paramBoolean);
        i = j + 1;
        if (i <= paramString.length())
          break;
        return this;
      }
    }

    private static String canonicalizeHost(String paramString, int paramInt1, int paramInt2)
    {
      String str = HttpUrl.percentDecode(paramString, paramInt1, paramInt2, false);
      if (str.contains(":"))
      {
        if ((str.startsWith("[")) && (str.endsWith("]")));
        for (InetAddress localInetAddress = decodeIpv6(str, 1, -1 + str.length()); localInetAddress == null; localInetAddress = decodeIpv6(str, 0, str.length()))
          return null;
        byte[] arrayOfByte = localInetAddress.getAddress();
        if (arrayOfByte.length == 16)
          return inet6AddressToAscii(arrayOfByte);
        throw new AssertionError();
      }
      return Util.domainToAscii(str);
    }

    private static boolean decodeIpv4Suffix(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      if (i < paramInt2)
        if (j != paramArrayOfByte.length);
      label20: 
      do
      {
        while (true)
        {
          return false;
          if (j == paramInt3)
            break;
          if (paramString.charAt(i) != '.')
            continue;
          i++;
        }
        int k = 0;
        int m = i;
        while (true)
        {
          int i1;
          if (i < paramInt2)
          {
            i1 = paramString.charAt(i);
            if ((i1 >= 48) && (i1 <= 57));
          }
          else
          {
            if (i - m == 0)
              break label20;
            int n = j + 1;
            paramArrayOfByte[j] = (byte)k;
            j = n;
            break;
          }
          if ((k == 0) && (m != i))
            break label20;
          k = -48 + (i1 + k * 10);
          if (k > 255)
            break label20;
          i++;
        }
      }
      while (j != paramInt3 + 4);
      return true;
    }

    private static InetAddress decodeIpv6(String paramString, int paramInt1, int paramInt2)
    {
      byte[] arrayOfByte = new byte[16];
      int i = 0;
      int j = -1;
      int k = -1;
      int m = paramInt1;
      while (true)
      {
        if (m < paramInt2)
        {
          if (i == arrayOfByte.length)
            return null;
          if ((m + 2 <= paramInt2) && (paramString.regionMatches(m, "::", 0, 2)))
          {
            if (j != -1)
              return null;
            m += 2;
            i += 2;
            j = i;
            if (m != paramInt2)
              break label113;
          }
        }
        else
        {
          if (i == arrayOfByte.length)
            break;
          if (j != -1)
            break label256;
          return null;
        }
        label113: int n;
        if (i != 0)
        {
          if (paramString.regionMatches(m, ":", 0, 1))
            m++;
        }
        else
        {
          n = 0;
          k = m;
        }
        while (true)
        {
          int i3;
          if (m < paramInt2)
          {
            i3 = HttpUrl.decodeHexDigit(paramString.charAt(m));
            if (i3 != -1);
          }
          else
          {
            int i1 = m - k;
            if ((i1 != 0) && (i1 <= 4))
              break label216;
            return null;
            if (paramString.regionMatches(m, ".", 0, 1))
            {
              if (!decodeIpv4Suffix(paramString, k, paramInt2, arrayOfByte, i - 2))
                return null;
              i += 2;
              break;
            }
            return null;
          }
          n = i3 + (n << 4);
          m++;
        }
        label216: int i2 = i + 1;
        arrayOfByte[i] = (byte)(0xFF & n >>> 8);
        i = i2 + 1;
        arrayOfByte[i2] = (byte)(n & 0xFF);
        continue;
        label256: System.arraycopy(arrayOfByte, j, arrayOfByte, arrayOfByte.length - (i - j), i - j);
        Arrays.fill(arrayOfByte, j, j + (arrayOfByte.length - i), 0);
      }
      try
      {
        InetAddress localInetAddress = InetAddress.getByAddress(arrayOfByte);
        return localInetAddress;
      }
      catch (UnknownHostException localUnknownHostException)
      {
      }
      throw new AssertionError();
    }

    private static String inet6AddressToAscii(byte[] paramArrayOfByte)
    {
      int i = -1;
      int j = 0;
      for (int k = 0; k < paramArrayOfByte.length; k += 2)
      {
        int n = k;
        while ((k < 16) && (paramArrayOfByte[k] == 0) && (paramArrayOfByte[(k + 1)] == 0))
          k += 2;
        int i1 = k - n;
        if (i1 <= j)
          continue;
        i = n;
        j = i1;
      }
      Buffer localBuffer = new Buffer();
      int m = 0;
      while (m < paramArrayOfByte.length)
      {
        if (m == i)
        {
          localBuffer.writeByte(58);
          m += j;
          if (m != 16)
            continue;
          localBuffer.writeByte(58);
          continue;
        }
        if (m > 0)
          localBuffer.writeByte(58);
        localBuffer.writeHexadecimalUnsignedLong((0xFF & paramArrayOfByte[m]) << 8 | 0xFF & paramArrayOfByte[(m + 1)]);
        m += 2;
      }
      return localBuffer.readUtf8();
    }

    private boolean isDot(String paramString)
    {
      return (paramString.equals(".")) || (paramString.equalsIgnoreCase("%2e"));
    }

    private boolean isDotDot(String paramString)
    {
      return (paramString.equals("..")) || (paramString.equalsIgnoreCase("%2e.")) || (paramString.equalsIgnoreCase(".%2e")) || (paramString.equalsIgnoreCase("%2e%2e"));
    }

    private static int parsePort(String paramString, int paramInt1, int paramInt2)
    {
      try
      {
        int i = Integer.parseInt(HttpUrl.canonicalize(paramString, paramInt1, paramInt2, "", false, false, false, true));
        if ((i > 0) && (i <= 65535))
          return i;
        return -1;
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
      return -1;
    }

    private void pop()
    {
      if ((((String)this.encodedPathSegments.remove(-1 + this.encodedPathSegments.size())).isEmpty()) && (!this.encodedPathSegments.isEmpty()))
      {
        this.encodedPathSegments.set(-1 + this.encodedPathSegments.size(), "");
        return;
      }
      this.encodedPathSegments.add("");
    }

    private static int portColonOffset(String paramString, int paramInt1, int paramInt2)
    {
      int i = paramInt1;
      if (i < paramInt2);
      switch (paramString.charAt(i))
      {
      default:
      case '[':
        while (true)
        {
          i++;
          break label46;
          break;
          label46: i++;
          if (i >= paramInt2)
            continue;
          if (paramString.charAt(i) != ']')
            continue;
        }
        i = paramInt2;
      case ':':
      }
      return i;
    }

    private void push(String paramString, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    {
      String str = HttpUrl.canonicalize(paramString, paramInt1, paramInt2, " \"<>^`{}|/\\?#", paramBoolean2, false, false, true);
      if (isDot(str));
      while (true)
      {
        return;
        if (isDotDot(str))
        {
          pop();
          return;
        }
        if (((String)this.encodedPathSegments.get(-1 + this.encodedPathSegments.size())).isEmpty())
          this.encodedPathSegments.set(-1 + this.encodedPathSegments.size(), str);
        while (paramBoolean1)
        {
          this.encodedPathSegments.add("");
          return;
          this.encodedPathSegments.add(str);
        }
      }
    }

    private void removeAllCanonicalQueryParameters(String paramString)
    {
      for (int i = -2 + this.encodedQueryNamesAndValues.size(); ; i -= 2)
      {
        if (i >= 0)
        {
          if (!paramString.equals(this.encodedQueryNamesAndValues.get(i)))
            continue;
          this.encodedQueryNamesAndValues.remove(i + 1);
          this.encodedQueryNamesAndValues.remove(i);
          if (!this.encodedQueryNamesAndValues.isEmpty())
            continue;
          this.encodedQueryNamesAndValues = null;
        }
        return;
      }
    }

    private void resolvePath(String paramString, int paramInt1, int paramInt2)
    {
      if (paramInt1 == paramInt2)
        return;
      int i = paramString.charAt(paramInt1);
      label51: int j;
      label54: int k;
      if ((i == 47) || (i == 92))
      {
        this.encodedPathSegments.clear();
        this.encodedPathSegments.add("");
        paramInt1++;
        j = paramInt1;
        if (j < paramInt2)
        {
          k = Util.delimiterOffset(paramString, j, paramInt2, "/\\");
          if (k >= paramInt2)
            break label133;
        }
      }
      label133: for (boolean bool = true; ; bool = false)
      {
        push(paramString, j, k, bool, true);
        j = k;
        if (!bool)
          break label54;
        j++;
        break label54;
        break;
        this.encodedPathSegments.set(-1 + this.encodedPathSegments.size(), "");
        break label51;
      }
    }

    private static int schemeDelimiterOffset(String paramString, int paramInt1, int paramInt2)
    {
      int j;
      if (paramInt2 - paramInt1 < 2)
        j = -1;
      while (true)
      {
        return j;
        int i = paramString.charAt(paramInt1);
        if (((i < 97) || (i > 122)) && ((i < 65) || (i > 90)))
          return -1;
        j = paramInt1 + 1;
        while (true)
          if (j < paramInt2)
          {
            int k = paramString.charAt(j);
            if (((k >= 97) && (k <= 122)) || ((k >= 65) && (k <= 90)) || ((k >= 48) && (k <= 57)) || (k == 43) || (k == 45) || (k == 46))
            {
              j++;
              continue;
            }
            if (k == 58)
              break;
            return -1;
          }
      }
      return -1;
    }

    private static int slashCount(String paramString, int paramInt1, int paramInt2)
    {
      int i = 0;
      while (paramInt1 < paramInt2)
      {
        int j = paramString.charAt(paramInt1);
        if ((j != 92) && (j != 47))
          break;
        i++;
        paramInt1++;
      }
      return i;
    }

    public Builder addEncodedPathSegment(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("encodedPathSegment == null");
      push(paramString, 0, paramString.length(), false, true);
      return this;
    }

    public Builder addEncodedPathSegments(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("encodedPathSegments == null");
      return addPathSegments(paramString, true);
    }

    public Builder addEncodedQueryParameter(String paramString1, String paramString2)
    {
      if (paramString1 == null)
        throw new NullPointerException("encodedName == null");
      if (this.encodedQueryNamesAndValues == null)
        this.encodedQueryNamesAndValues = new ArrayList();
      this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(paramString1, " \"'<>#&=", true, false, true, true));
      List localList = this.encodedQueryNamesAndValues;
      if (paramString2 != null);
      for (String str = HttpUrl.canonicalize(paramString2, " \"'<>#&=", true, false, true, true); ; str = null)
      {
        localList.add(str);
        return this;
      }
    }

    public Builder addPathSegment(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("pathSegment == null");
      push(paramString, 0, paramString.length(), false, false);
      return this;
    }

    public Builder addPathSegments(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("pathSegments == null");
      return addPathSegments(paramString, false);
    }

    public Builder addQueryParameter(String paramString1, String paramString2)
    {
      if (paramString1 == null)
        throw new NullPointerException("name == null");
      if (this.encodedQueryNamesAndValues == null)
        this.encodedQueryNamesAndValues = new ArrayList();
      this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(paramString1, " \"'<>#&=", false, false, true, true));
      List localList = this.encodedQueryNamesAndValues;
      if (paramString2 != null);
      for (String str = HttpUrl.canonicalize(paramString2, " \"'<>#&=", false, false, true, true); ; str = null)
      {
        localList.add(str);
        return this;
      }
    }

    public HttpUrl build()
    {
      if (this.scheme == null)
        throw new IllegalStateException("scheme == null");
      if (this.host == null)
        throw new IllegalStateException("host == null");
      return new HttpUrl(this);
    }

    int effectivePort()
    {
      if (this.port != -1)
        return this.port;
      return HttpUrl.defaultPort(this.scheme);
    }

    public Builder encodedFragment(String paramString)
    {
      if (paramString != null);
      for (String str = HttpUrl.canonicalize(paramString, "", true, false, false, false); ; str = null)
      {
        this.encodedFragment = str;
        return this;
      }
    }

    public Builder encodedPassword(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("encodedPassword == null");
      this.encodedPassword = HttpUrl.canonicalize(paramString, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
      return this;
    }

    public Builder encodedPath(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("encodedPath == null");
      if (!paramString.startsWith("/"))
        throw new IllegalArgumentException("unexpected encodedPath: " + paramString);
      resolvePath(paramString, 0, paramString.length());
      return this;
    }

    public Builder encodedQuery(String paramString)
    {
      if (paramString != null);
      for (List localList = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(paramString, " \"'<>#", true, false, true, true)); ; localList = null)
      {
        this.encodedQueryNamesAndValues = localList;
        return this;
      }
    }

    public Builder encodedUsername(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("encodedUsername == null");
      this.encodedUsername = HttpUrl.canonicalize(paramString, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
      return this;
    }

    public Builder fragment(String paramString)
    {
      if (paramString != null);
      for (String str = HttpUrl.canonicalize(paramString, "", false, false, false, false); ; str = null)
      {
        this.encodedFragment = str;
        return this;
      }
    }

    public Builder host(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("host == null");
      String str = canonicalizeHost(paramString, 0, paramString.length());
      if (str == null)
        throw new IllegalArgumentException("unexpected host: " + paramString);
      this.host = str;
      return this;
    }

    ParseResult parse(HttpUrl paramHttpUrl, String paramString)
    {
      int i = Util.skipLeadingAsciiWhitespace(paramString, 0, paramString.length());
      int j = Util.skipTrailingAsciiWhitespace(paramString, i, paramString.length());
      label63: int k;
      int m;
      int i1;
      int i2;
      if (schemeDelimiterOffset(paramString, i, j) != -1)
        if (paramString.regionMatches(true, i, "https:", 0, 6))
        {
          this.scheme = "https";
          i += "https:".length();
          k = 0;
          m = 0;
          int n = slashCount(paramString, i, j);
          if ((n < 2) && (paramHttpUrl != null) && (paramHttpUrl.scheme.equals(this.scheme)))
            break label645;
          i1 = i + n;
          i2 = Util.delimiterOffset(paramString, i1, j, "@/\\?#");
          if (i2 == j)
            break label312;
        }
      int i5;
      label312: for (int i3 = paramString.charAt(i2); ; i3 = -1)
        switch (i3)
        {
        default:
          break;
        case -1:
        case 35:
        case 47:
        case 63:
        case 92:
          i5 = portColonOffset(paramString, i1, i2);
          if (i5 + 1 >= i2)
            break label484;
          this.host = canonicalizeHost(paramString, i1, i5);
          this.port = parsePort(paramString, i5 + 1, i2);
          if (this.port != -1)
            break label507;
          return ParseResult.INVALID_PORT;
          if (paramString.regionMatches(true, i, "http:", 0, 5))
          {
            this.scheme = "http";
            i += "http:".length();
            break label63;
          }
          return ParseResult.UNSUPPORTED_SCHEME;
          if (paramHttpUrl != null)
          {
            this.scheme = paramHttpUrl.scheme;
            break label63;
          }
          return ParseResult.MISSING_SCHEME;
        case 64:
        }
      if (m == 0)
      {
        int i4 = Util.delimiterOffset(paramString, i1, i2, ':');
        String str = HttpUrl.canonicalize(paramString, i1, i4, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
        if (k != 0)
          str = this.encodedUsername + "%40" + str;
        this.encodedUsername = str;
        if (i4 != i2)
        {
          m = 1;
          this.encodedPassword = HttpUrl.canonicalize(paramString, i4 + 1, i2, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
        }
        k = 1;
      }
      while (true)
      {
        i1 = i2 + 1;
        break;
        this.encodedPassword = (this.encodedPassword + "%40" + HttpUrl.canonicalize(paramString, i1, i2, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true));
      }
      label484: this.host = canonicalizeHost(paramString, i1, i5);
      this.port = HttpUrl.defaultPort(this.scheme);
      label507: if (this.host == null)
        return ParseResult.INVALID_HOST;
      i = i2;
      while (true)
      {
        int i6 = Util.delimiterOffset(paramString, i, j, "?#");
        resolvePath(paramString, i, i6);
        int i7 = i6;
        if ((i7 < j) && (paramString.charAt(i7) == '?'))
        {
          int i8 = Util.delimiterOffset(paramString, i7, j, '#');
          this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(paramString, i7 + 1, i8, " \"'<>#", true, false, true, true));
          i7 = i8;
        }
        if ((i7 < j) && (paramString.charAt(i7) == '#'))
          this.encodedFragment = HttpUrl.canonicalize(paramString, i7 + 1, j, "", true, false, false, false);
        return ParseResult.SUCCESS;
        label645: this.encodedUsername = paramHttpUrl.encodedUsername();
        this.encodedPassword = paramHttpUrl.encodedPassword();
        this.host = paramHttpUrl.host;
        this.port = paramHttpUrl.port;
        this.encodedPathSegments.clear();
        this.encodedPathSegments.addAll(paramHttpUrl.encodedPathSegments());
        if ((i != j) && (paramString.charAt(i) != '#'))
          continue;
        encodedQuery(paramHttpUrl.encodedQuery());
      }
    }

    public Builder password(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("password == null");
      this.encodedPassword = HttpUrl.canonicalize(paramString, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
      return this;
    }

    public Builder port(int paramInt)
    {
      if ((paramInt <= 0) || (paramInt > 65535))
        throw new IllegalArgumentException("unexpected port: " + paramInt);
      this.port = paramInt;
      return this;
    }

    public Builder query(String paramString)
    {
      if (paramString != null);
      for (List localList = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(paramString, " \"'<>#", false, false, true, true)); ; localList = null)
      {
        this.encodedQueryNamesAndValues = localList;
        return this;
      }
    }

    Builder reencodeForUri()
    {
      int i = 0;
      int j = this.encodedPathSegments.size();
      while (i < j)
      {
        String str2 = (String)this.encodedPathSegments.get(i);
        this.encodedPathSegments.set(i, HttpUrl.canonicalize(str2, "[]", true, true, false, true));
        i++;
      }
      if (this.encodedQueryNamesAndValues != null)
      {
        int k = 0;
        int m = this.encodedQueryNamesAndValues.size();
        while (k < m)
        {
          String str1 = (String)this.encodedQueryNamesAndValues.get(k);
          if (str1 != null)
            this.encodedQueryNamesAndValues.set(k, HttpUrl.canonicalize(str1, "\\^`{|}", true, true, true, true));
          k++;
        }
      }
      if (this.encodedFragment != null)
        this.encodedFragment = HttpUrl.canonicalize(this.encodedFragment, " \"#<>\\^`{|}", true, true, false, false);
      return this;
    }

    public Builder removeAllEncodedQueryParameters(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("encodedName == null");
      if (this.encodedQueryNamesAndValues == null)
        return this;
      removeAllCanonicalQueryParameters(HttpUrl.canonicalize(paramString, " \"'<>#&=", true, false, true, true));
      return this;
    }

    public Builder removeAllQueryParameters(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("name == null");
      if (this.encodedQueryNamesAndValues == null)
        return this;
      removeAllCanonicalQueryParameters(HttpUrl.canonicalize(paramString, " \"'<>#&=", false, false, true, true));
      return this;
    }

    public Builder removePathSegment(int paramInt)
    {
      this.encodedPathSegments.remove(paramInt);
      if (this.encodedPathSegments.isEmpty())
        this.encodedPathSegments.add("");
      return this;
    }

    public Builder scheme(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("scheme == null");
      if (paramString.equalsIgnoreCase("http"))
      {
        this.scheme = "http";
        return this;
      }
      if (paramString.equalsIgnoreCase("https"))
      {
        this.scheme = "https";
        return this;
      }
      throw new IllegalArgumentException("unexpected scheme: " + paramString);
    }

    public Builder setEncodedPathSegment(int paramInt, String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("encodedPathSegment == null");
      String str = HttpUrl.canonicalize(paramString, 0, paramString.length(), " \"<>^`{}|/\\?#", true, false, false, true);
      this.encodedPathSegments.set(paramInt, str);
      if ((isDot(str)) || (isDotDot(str)))
        throw new IllegalArgumentException("unexpected path segment: " + paramString);
      return this;
    }

    public Builder setEncodedQueryParameter(String paramString1, String paramString2)
    {
      removeAllEncodedQueryParameters(paramString1);
      addEncodedQueryParameter(paramString1, paramString2);
      return this;
    }

    public Builder setPathSegment(int paramInt, String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("pathSegment == null");
      String str = HttpUrl.canonicalize(paramString, 0, paramString.length(), " \"<>^`{}|/\\?#", false, false, false, true);
      if ((isDot(str)) || (isDotDot(str)))
        throw new IllegalArgumentException("unexpected path segment: " + paramString);
      this.encodedPathSegments.set(paramInt, str);
      return this;
    }

    public Builder setQueryParameter(String paramString1, String paramString2)
    {
      removeAllQueryParameters(paramString1);
      addQueryParameter(paramString1, paramString2);
      return this;
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(this.scheme);
      localStringBuilder.append("://");
      if ((!this.encodedUsername.isEmpty()) || (!this.encodedPassword.isEmpty()))
      {
        localStringBuilder.append(this.encodedUsername);
        if (!this.encodedPassword.isEmpty())
        {
          localStringBuilder.append(':');
          localStringBuilder.append(this.encodedPassword);
        }
        localStringBuilder.append('@');
      }
      if (this.host.indexOf(':') != -1)
      {
        localStringBuilder.append('[');
        localStringBuilder.append(this.host);
        localStringBuilder.append(']');
      }
      while (true)
      {
        int i = effectivePort();
        if (i != HttpUrl.defaultPort(this.scheme))
        {
          localStringBuilder.append(':');
          localStringBuilder.append(i);
        }
        HttpUrl.pathSegmentsToString(localStringBuilder, this.encodedPathSegments);
        if (this.encodedQueryNamesAndValues != null)
        {
          localStringBuilder.append('?');
          HttpUrl.namesAndValuesToQueryString(localStringBuilder, this.encodedQueryNamesAndValues);
        }
        if (this.encodedFragment != null)
        {
          localStringBuilder.append('#');
          localStringBuilder.append(this.encodedFragment);
        }
        return localStringBuilder.toString();
        localStringBuilder.append(this.host);
      }
    }

    public Builder username(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("username == null");
      this.encodedUsername = HttpUrl.canonicalize(paramString, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
      return this;
    }

    static enum ParseResult
    {
      static
      {
        MISSING_SCHEME = new ParseResult("MISSING_SCHEME", 1);
        UNSUPPORTED_SCHEME = new ParseResult("UNSUPPORTED_SCHEME", 2);
        INVALID_PORT = new ParseResult("INVALID_PORT", 3);
        INVALID_HOST = new ParseResult("INVALID_HOST", 4);
        ParseResult[] arrayOfParseResult = new ParseResult[5];
        arrayOfParseResult[0] = SUCCESS;
        arrayOfParseResult[1] = MISSING_SCHEME;
        arrayOfParseResult[2] = UNSUPPORTED_SCHEME;
        arrayOfParseResult[3] = INVALID_PORT;
        arrayOfParseResult[4] = INVALID_HOST;
        $VALUES = arrayOfParseResult;
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.HttpUrl
 * JD-Core Version:    0.6.0
 */