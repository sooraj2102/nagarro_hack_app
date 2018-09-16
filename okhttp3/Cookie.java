package okhttp3;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpDate;

public final class Cookie
{
  private static final Pattern DAY_OF_MONTH_PATTERN;
  private static final Pattern MONTH_PATTERN;
  private static final Pattern TIME_PATTERN;
  private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
  private final String domain;
  private final long expiresAt;
  private final boolean hostOnly;
  private final boolean httpOnly;
  private final String name;
  private final String path;
  private final boolean persistent;
  private final boolean secure;
  private final String value;

  static
  {
    MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
    DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
    TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");
  }

  private Cookie(String paramString1, String paramString2, long paramLong, String paramString3, String paramString4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    this.name = paramString1;
    this.value = paramString2;
    this.expiresAt = paramLong;
    this.domain = paramString3;
    this.path = paramString4;
    this.secure = paramBoolean1;
    this.httpOnly = paramBoolean2;
    this.hostOnly = paramBoolean3;
    this.persistent = paramBoolean4;
  }

  Cookie(Builder paramBuilder)
  {
    if (paramBuilder.name == null)
      throw new NullPointerException("builder.name == null");
    if (paramBuilder.value == null)
      throw new NullPointerException("builder.value == null");
    if (paramBuilder.domain == null)
      throw new NullPointerException("builder.domain == null");
    this.name = paramBuilder.name;
    this.value = paramBuilder.value;
    this.expiresAt = paramBuilder.expiresAt;
    this.domain = paramBuilder.domain;
    this.path = paramBuilder.path;
    this.secure = paramBuilder.secure;
    this.httpOnly = paramBuilder.httpOnly;
    this.persistent = paramBuilder.persistent;
    this.hostOnly = paramBuilder.hostOnly;
  }

  private static int dateCharacterOffset(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    label113: label119: for (int i = paramInt1; i < paramInt2; i++)
    {
      int j = paramString.charAt(i);
      int k;
      if (((j < 32) && (j != 9)) || (j >= 127) || ((j >= 48) && (j <= 57)) || ((j >= 97) && (j <= 122)) || ((j >= 65) && (j <= 90)) || (j == 58))
      {
        k = 1;
        if (paramBoolean)
          break label113;
      }
      for (int m = 1; ; m = 0)
      {
        if (k != m)
          break label119;
        return i;
        k = 0;
        break;
      }
    }
    return paramInt2;
  }

  private static boolean domainMatch(HttpUrl paramHttpUrl, String paramString)
  {
    String str = paramHttpUrl.host();
    if (str.equals(paramString));
    do
      return true;
    while ((str.endsWith(paramString)) && (str.charAt(-1 + (str.length() - paramString.length())) == '.') && (!Util.verifyAsIpAddress(str)));
    return false;
  }

  // ERROR //
  static Cookie parse(long paramLong, HttpUrl paramHttpUrl, String paramString)
  {
    // Byte code:
    //   0: aload_3
    //   1: invokevirtual 118	java/lang/String:length	()I
    //   4: istore 4
    //   6: aload_3
    //   7: iconst_0
    //   8: iload 4
    //   10: bipush 59
    //   12: invokestatic 133	okhttp3/internal/Util:delimiterOffset	(Ljava/lang/String;IIC)I
    //   15: istore 5
    //   17: aload_3
    //   18: iconst_0
    //   19: iload 5
    //   21: bipush 61
    //   23: invokestatic 133	okhttp3/internal/Util:delimiterOffset	(Ljava/lang/String;IIC)I
    //   26: istore 6
    //   28: iload 6
    //   30: iload 5
    //   32: if_icmpne +5 -> 37
    //   35: aconst_null
    //   36: areturn
    //   37: aload_3
    //   38: iconst_0
    //   39: iload 6
    //   41: invokestatic 137	okhttp3/internal/Util:trimSubstring	(Ljava/lang/String;II)Ljava/lang/String;
    //   44: astore 7
    //   46: aload 7
    //   48: invokevirtual 141	java/lang/String:isEmpty	()Z
    //   51: ifne +12 -> 63
    //   54: aload 7
    //   56: invokestatic 145	okhttp3/internal/Util:indexOfControlOrNonAscii	(Ljava/lang/String;)I
    //   59: iconst_m1
    //   60: if_icmpeq +5 -> 65
    //   63: aconst_null
    //   64: areturn
    //   65: aload_3
    //   66: iload 6
    //   68: iconst_1
    //   69: iadd
    //   70: iload 5
    //   72: invokestatic 137	okhttp3/internal/Util:trimSubstring	(Ljava/lang/String;II)Ljava/lang/String;
    //   75: astore 8
    //   77: aload 8
    //   79: invokestatic 145	okhttp3/internal/Util:indexOfControlOrNonAscii	(Ljava/lang/String;)I
    //   82: iconst_m1
    //   83: if_icmpeq +5 -> 88
    //   86: aconst_null
    //   87: areturn
    //   88: ldc2_w 146
    //   91: lstore 9
    //   93: ldc2_w 148
    //   96: lstore 11
    //   98: aconst_null
    //   99: astore 13
    //   101: aconst_null
    //   102: astore 14
    //   104: iconst_0
    //   105: istore 15
    //   107: iconst_0
    //   108: istore 16
    //   110: iconst_1
    //   111: istore 17
    //   113: iconst_0
    //   114: istore 18
    //   116: iload 5
    //   118: iconst_1
    //   119: iadd
    //   120: istore 19
    //   122: iload 19
    //   124: iload 4
    //   126: if_icmpge +209 -> 335
    //   129: aload_3
    //   130: iload 19
    //   132: iload 4
    //   134: bipush 59
    //   136: invokestatic 133	okhttp3/internal/Util:delimiterOffset	(Ljava/lang/String;IIC)I
    //   139: istore 24
    //   141: aload_3
    //   142: iload 19
    //   144: iload 24
    //   146: bipush 61
    //   148: invokestatic 133	okhttp3/internal/Util:delimiterOffset	(Ljava/lang/String;IIC)I
    //   151: istore 25
    //   153: aload_3
    //   154: iload 19
    //   156: iload 25
    //   158: invokestatic 137	okhttp3/internal/Util:trimSubstring	(Ljava/lang/String;II)Ljava/lang/String;
    //   161: astore 26
    //   163: iload 25
    //   165: iload 24
    //   167: if_icmpge +58 -> 225
    //   170: aload_3
    //   171: iload 25
    //   173: iconst_1
    //   174: iadd
    //   175: iload 24
    //   177: invokestatic 137	okhttp3/internal/Util:trimSubstring	(Ljava/lang/String;II)Ljava/lang/String;
    //   180: astore 27
    //   182: aload 26
    //   184: ldc 151
    //   186: invokevirtual 154	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   189: ifeq +43 -> 232
    //   192: aload 27
    //   194: invokevirtual 118	java/lang/String:length	()I
    //   197: istore 34
    //   199: aload 27
    //   201: iconst_0
    //   202: iload 34
    //   204: invokestatic 158	okhttp3/Cookie:parseExpires	(Ljava/lang/String;II)J
    //   207: lstore 35
    //   209: lload 35
    //   211: lstore 9
    //   213: iconst_1
    //   214: istore 18
    //   216: iload 24
    //   218: iconst_1
    //   219: iadd
    //   220: istore 19
    //   222: goto -100 -> 122
    //   225: ldc 160
    //   227: astore 27
    //   229: goto -47 -> 182
    //   232: aload 26
    //   234: ldc 162
    //   236: invokevirtual 154	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   239: ifeq +20 -> 259
    //   242: aload 27
    //   244: invokestatic 166	okhttp3/Cookie:parseMaxAge	(Ljava/lang/String;)J
    //   247: lstore 31
    //   249: lload 31
    //   251: lstore 11
    //   253: iconst_1
    //   254: istore 18
    //   256: goto -40 -> 216
    //   259: aload 26
    //   261: ldc 167
    //   263: invokevirtual 154	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   266: ifeq +20 -> 286
    //   269: aload 27
    //   271: invokestatic 171	okhttp3/Cookie:parseDomain	(Ljava/lang/String;)Ljava/lang/String;
    //   274: astore 29
    //   276: aload 29
    //   278: astore 13
    //   280: iconst_0
    //   281: istore 17
    //   283: goto -67 -> 216
    //   286: aload 26
    //   288: ldc 172
    //   290: invokevirtual 154	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   293: ifeq +10 -> 303
    //   296: aload 27
    //   298: astore 14
    //   300: goto -84 -> 216
    //   303: aload 26
    //   305: ldc 173
    //   307: invokevirtual 154	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   310: ifeq +9 -> 319
    //   313: iconst_1
    //   314: istore 15
    //   316: goto -100 -> 216
    //   319: aload 26
    //   321: ldc 175
    //   323: invokevirtual 154	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   326: ifeq -110 -> 216
    //   329: iconst_1
    //   330: istore 16
    //   332: goto -116 -> 216
    //   335: lload 11
    //   337: ldc2_w 176
    //   340: lcmp
    //   341: ifne +90 -> 431
    //   344: ldc2_w 176
    //   347: lstore 9
    //   349: aload 13
    //   351: ifnonnull +144 -> 495
    //   354: aload_2
    //   355: invokevirtual 106	okhttp3/HttpUrl:host	()Ljava/lang/String;
    //   358: astore 13
    //   360: aload 14
    //   362: ifnull +13 -> 375
    //   365: aload 14
    //   367: ldc 179
    //   369: invokevirtual 182	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   372: ifne +33 -> 405
    //   375: aload_2
    //   376: invokevirtual 185	okhttp3/HttpUrl:encodedPath	()Ljava/lang/String;
    //   379: astore 22
    //   381: aload 22
    //   383: bipush 47
    //   385: invokevirtual 189	java/lang/String:lastIndexOf	(I)I
    //   388: istore 23
    //   390: iload 23
    //   392: ifeq +114 -> 506
    //   395: aload 22
    //   397: iconst_0
    //   398: iload 23
    //   400: invokevirtual 193	java/lang/String:substring	(II)Ljava/lang/String;
    //   403: astore 14
    //   405: new 2	okhttp3/Cookie
    //   408: dup
    //   409: aload 7
    //   411: aload 8
    //   413: lload 9
    //   415: aload 13
    //   417: aload 14
    //   419: iload 15
    //   421: iload 16
    //   423: iload 17
    //   425: iload 18
    //   427: invokespecial 195	okhttp3/Cookie:<init>	(Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;ZZZZ)V
    //   430: areturn
    //   431: lload 11
    //   433: ldc2_w 148
    //   436: lcmp
    //   437: ifeq -88 -> 349
    //   440: lload 11
    //   442: ldc2_w 196
    //   445: lcmp
    //   446: ifgt +41 -> 487
    //   449: lload 11
    //   451: ldc2_w 198
    //   454: lmul
    //   455: lstore 20
    //   457: lload_0
    //   458: lload 20
    //   460: ladd
    //   461: lstore 9
    //   463: lload 9
    //   465: lload_0
    //   466: lcmp
    //   467: iflt +12 -> 479
    //   470: lload 9
    //   472: ldc2_w 146
    //   475: lcmp
    //   476: ifle -127 -> 349
    //   479: ldc2_w 146
    //   482: lstore 9
    //   484: goto -135 -> 349
    //   487: ldc2_w 200
    //   490: lstore 20
    //   492: goto -35 -> 457
    //   495: aload_2
    //   496: aload 13
    //   498: invokestatic 203	okhttp3/Cookie:domainMatch	(Lokhttp3/HttpUrl;Ljava/lang/String;)Z
    //   501: ifne -141 -> 360
    //   504: aconst_null
    //   505: areturn
    //   506: ldc 179
    //   508: astore 14
    //   510: goto -105 -> 405
    //   513: astore 30
    //   515: goto -299 -> 216
    //   518: astore 28
    //   520: goto -304 -> 216
    //   523: astore 33
    //   525: goto -309 -> 216
    //
    // Exception table:
    //   from	to	target	type
    //   242	249	513	java/lang/NumberFormatException
    //   269	276	518	java/lang/IllegalArgumentException
    //   192	209	523	java/lang/IllegalArgumentException
  }

  public static Cookie parse(HttpUrl paramHttpUrl, String paramString)
  {
    return parse(System.currentTimeMillis(), paramHttpUrl, paramString);
  }

  public static List<Cookie> parseAll(HttpUrl paramHttpUrl, Headers paramHeaders)
  {
    List localList = paramHeaders.values("Set-Cookie");
    ArrayList localArrayList = null;
    int i = 0;
    int j = localList.size();
    if (i < j)
    {
      Cookie localCookie = parse(paramHttpUrl, (String)localList.get(i));
      if (localCookie == null);
      while (true)
      {
        i++;
        break;
        if (localArrayList == null)
          localArrayList = new ArrayList();
        localArrayList.add(localCookie);
      }
    }
    if (localArrayList != null)
      return Collections.unmodifiableList(localArrayList);
    return Collections.emptyList();
  }

  private static String parseDomain(String paramString)
  {
    if (paramString.endsWith("."))
      throw new IllegalArgumentException();
    if (paramString.startsWith("."))
      paramString = paramString.substring(1);
    String str = Util.domainToAscii(paramString);
    if (str == null)
      throw new IllegalArgumentException();
    return str;
  }

  private static long parseExpires(String paramString, int paramInt1, int paramInt2)
  {
    int i = dateCharacterOffset(paramString, paramInt1, paramInt2, false);
    int j = -1;
    int k = -1;
    int m = -1;
    int n = -1;
    int i1 = -1;
    int i2 = -1;
    Matcher localMatcher = TIME_PATTERN.matcher(paramString);
    if (i < paramInt2)
    {
      int i3 = dateCharacterOffset(paramString, i + 1, paramInt2, true);
      localMatcher.region(i, i3);
      if ((j == -1) && (localMatcher.usePattern(TIME_PATTERN).matches()))
      {
        j = Integer.parseInt(localMatcher.group(1));
        k = Integer.parseInt(localMatcher.group(2));
        m = Integer.parseInt(localMatcher.group(3));
      }
      while (true)
      {
        i = dateCharacterOffset(paramString, i3 + 1, paramInt2, false);
        break;
        if ((n == -1) && (localMatcher.usePattern(DAY_OF_MONTH_PATTERN).matches()))
        {
          n = Integer.parseInt(localMatcher.group(1));
          continue;
        }
        if ((i1 == -1) && (localMatcher.usePattern(MONTH_PATTERN).matches()))
        {
          String str = localMatcher.group(1).toLowerCase(Locale.US);
          i1 = MONTH_PATTERN.pattern().indexOf(str) / 4;
          continue;
        }
        if ((i2 != -1) || (!localMatcher.usePattern(YEAR_PATTERN).matches()))
          continue;
        i2 = Integer.parseInt(localMatcher.group(1));
      }
    }
    if ((i2 >= 70) && (i2 <= 99))
      i2 += 1900;
    if ((i2 >= 0) && (i2 <= 69))
      i2 += 2000;
    if (i2 < 1601)
      throw new IllegalArgumentException();
    if (i1 == -1)
      throw new IllegalArgumentException();
    if ((n < 1) || (n > 31))
      throw new IllegalArgumentException();
    if ((j < 0) || (j > 23))
      throw new IllegalArgumentException();
    if ((k < 0) || (k > 59))
      throw new IllegalArgumentException();
    if ((m < 0) || (m > 59))
      throw new IllegalArgumentException();
    GregorianCalendar localGregorianCalendar = new GregorianCalendar(Util.UTC);
    localGregorianCalendar.setLenient(false);
    localGregorianCalendar.set(1, i2);
    localGregorianCalendar.set(2, i1 - 1);
    localGregorianCalendar.set(5, n);
    localGregorianCalendar.set(11, j);
    localGregorianCalendar.set(12, k);
    localGregorianCalendar.set(13, m);
    localGregorianCalendar.set(14, 0);
    return localGregorianCalendar.getTimeInMillis();
  }

  private static long parseMaxAge(String paramString)
  {
    long l1 = -9223372036854775808L;
    try
    {
      long l2 = Long.parseLong(paramString);
      long l3 = l2;
      if (l3 <= 0L)
        l3 = l1;
      return l3;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      if (paramString.matches("-?\\d+"))
      {
        if (paramString.startsWith("-"));
        while (true)
        {
          return l1;
          l1 = 9223372036854775807L;
        }
      }
    }
    throw localNumberFormatException;
  }

  private static boolean pathMatch(HttpUrl paramHttpUrl, String paramString)
  {
    String str = paramHttpUrl.encodedPath();
    if (str.equals(paramString));
    do
      return true;
    while ((str.startsWith(paramString)) && ((paramString.endsWith("/")) || (str.charAt(paramString.length()) == '/')));
    return false;
  }

  public String domain()
  {
    return this.domain;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Cookie));
    Cookie localCookie;
    do
    {
      return false;
      localCookie = (Cookie)paramObject;
    }
    while ((!localCookie.name.equals(this.name)) || (!localCookie.value.equals(this.value)) || (!localCookie.domain.equals(this.domain)) || (!localCookie.path.equals(this.path)) || (localCookie.expiresAt != this.expiresAt) || (localCookie.secure != this.secure) || (localCookie.httpOnly != this.httpOnly) || (localCookie.persistent != this.persistent) || (localCookie.hostOnly != this.hostOnly));
    return true;
  }

  public long expiresAt()
  {
    return this.expiresAt;
  }

  public int hashCode()
  {
    int i = 31 * (31 * (31 * (31 * (31 * (527 + this.name.hashCode()) + this.value.hashCode()) + this.domain.hashCode()) + this.path.hashCode()) + (int)(this.expiresAt ^ this.expiresAt >>> 32));
    int j;
    int m;
    label91: int i1;
    label110: int i2;
    int i3;
    if (this.secure)
    {
      j = 0;
      int k = 31 * (i + j);
      if (!this.httpOnly)
        break label145;
      m = 0;
      int n = 31 * (k + m);
      if (!this.persistent)
        break label151;
      i1 = 0;
      i2 = 31 * (n + i1);
      boolean bool = this.hostOnly;
      i3 = 0;
      if (!bool)
        break label157;
    }
    while (true)
    {
      return i2 + i3;
      j = 1;
      break;
      label145: m = 1;
      break label91;
      label151: i1 = 1;
      break label110;
      label157: i3 = 1;
    }
  }

  public boolean hostOnly()
  {
    return this.hostOnly;
  }

  public boolean httpOnly()
  {
    return this.httpOnly;
  }

  public boolean matches(HttpUrl paramHttpUrl)
  {
    boolean bool;
    if (this.hostOnly)
    {
      bool = paramHttpUrl.host().equals(this.domain);
      if (bool)
        break label37;
    }
    label37: 
    do
    {
      return false;
      bool = domainMatch(paramHttpUrl, this.domain);
      break;
    }
    while ((!pathMatch(paramHttpUrl, this.path)) || ((this.secure) && (!paramHttpUrl.isHttps())));
    return true;
  }

  public String name()
  {
    return this.name;
  }

  public String path()
  {
    return this.path;
  }

  public boolean persistent()
  {
    return this.persistent;
  }

  public boolean secure()
  {
    return this.secure;
  }

  public String toString()
  {
    return toString(false);
  }

  String toString(boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this.name);
    localStringBuilder.append('=');
    localStringBuilder.append(this.value);
    if (this.persistent)
    {
      if (this.expiresAt != -9223372036854775808L)
        break label144;
      localStringBuilder.append("; max-age=0");
    }
    while (true)
    {
      if (!this.hostOnly)
      {
        localStringBuilder.append("; domain=");
        if (paramBoolean)
          localStringBuilder.append(".");
        localStringBuilder.append(this.domain);
      }
      localStringBuilder.append("; path=").append(this.path);
      if (this.secure)
        localStringBuilder.append("; secure");
      if (this.httpOnly)
        localStringBuilder.append("; httponly");
      return localStringBuilder.toString();
      label144: localStringBuilder.append("; expires=").append(HttpDate.format(new Date(this.expiresAt)));
    }
  }

  public String value()
  {
    return this.value;
  }

  public static final class Builder
  {
    String domain;
    long expiresAt = 253402300799999L;
    boolean hostOnly;
    boolean httpOnly;
    String name;
    String path = "/";
    boolean persistent;
    boolean secure;
    String value;

    private Builder domain(String paramString, boolean paramBoolean)
    {
      if (paramString == null)
        throw new NullPointerException("domain == null");
      String str = Util.domainToAscii(paramString);
      if (str == null)
        throw new IllegalArgumentException("unexpected domain: " + paramString);
      this.domain = str;
      this.hostOnly = paramBoolean;
      return this;
    }

    public Cookie build()
    {
      return new Cookie(this);
    }

    public Builder domain(String paramString)
    {
      return domain(paramString, false);
    }

    public Builder expiresAt(long paramLong)
    {
      if (paramLong <= 0L)
        paramLong = -9223372036854775808L;
      if (paramLong > 253402300799999L)
        paramLong = 253402300799999L;
      this.expiresAt = paramLong;
      this.persistent = true;
      return this;
    }

    public Builder hostOnlyDomain(String paramString)
    {
      return domain(paramString, true);
    }

    public Builder httpOnly()
    {
      this.httpOnly = true;
      return this;
    }

    public Builder name(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("name == null");
      if (!paramString.trim().equals(paramString))
        throw new IllegalArgumentException("name is not trimmed");
      this.name = paramString;
      return this;
    }

    public Builder path(String paramString)
    {
      if (!paramString.startsWith("/"))
        throw new IllegalArgumentException("path must start with '/'");
      this.path = paramString;
      return this;
    }

    public Builder secure()
    {
      this.secure = true;
      return this;
    }

    public Builder value(String paramString)
    {
      if (paramString == null)
        throw new NullPointerException("value == null");
      if (!paramString.trim().equals(paramString))
        throw new IllegalArgumentException("value is not trimmed");
      this.value = paramString;
      return this;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Cookie
 * JD-Core Version:    0.6.0
 */