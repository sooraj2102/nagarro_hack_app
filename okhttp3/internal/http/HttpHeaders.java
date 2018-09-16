package okhttp3.internal.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Set<Ljava.lang.String;>;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Challenge;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

public final class HttpHeaders
{
  private static final Pattern PARAMETER = Pattern.compile(" +([^ \"=]*)=(:?\"([^\"]*)\"|([^ \"=]*)) *(:?,|$)");
  private static final String QUOTED_STRING = "\"([^\"]*)\"";
  private static final String TOKEN = "([^ \"=]*)";

  public static long contentLength(Headers paramHeaders)
  {
    return stringToLong(paramHeaders.get("Content-Length"));
  }

  public static long contentLength(Response paramResponse)
  {
    return contentLength(paramResponse.headers());
  }

  public static boolean hasBody(Response paramResponse)
  {
    if (paramResponse.request().method().equals("HEAD"));
    do
    {
      return false;
      int i = paramResponse.code();
      if (((i < 100) || (i >= 200)) && (i != 204) && (i != 304))
        return true;
    }
    while ((contentLength(paramResponse) == -1L) && (!"chunked".equalsIgnoreCase(paramResponse.header("Transfer-Encoding"))));
    return true;
  }

  public static boolean hasVaryAll(Headers paramHeaders)
  {
    return varyFields(paramHeaders).contains("*");
  }

  public static boolean hasVaryAll(Response paramResponse)
  {
    return hasVaryAll(paramResponse.headers());
  }

  public static List<Challenge> parseChallenges(Headers paramHeaders, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramHeaders.values(paramString).iterator();
    label149: 
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      int i = str1.indexOf(' ');
      if (i == -1)
        continue;
      Matcher localMatcher = PARAMETER.matcher(str1);
      for (int j = i; ; j = localMatcher.end())
      {
        if (!localMatcher.find(j))
          break label149;
        if (!str1.regionMatches(true, localMatcher.start(1), "realm", 0, 5))
          continue;
        String str2 = str1.substring(0, i);
        String str3 = localMatcher.group(3);
        if (str3 == null)
          continue;
        localArrayList.add(new Challenge(str2, str3));
        break;
      }
    }
    return localArrayList;
  }

  public static int parseSeconds(String paramString, int paramInt)
  {
    try
    {
      long l = Long.parseLong(paramString);
      if (l > 2147483647L)
        return 2147483647;
      if (l < 0L)
        return 0;
      return (int)l;
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return paramInt;
  }

  public static void receiveHeaders(CookieJar paramCookieJar, HttpUrl paramHttpUrl, Headers paramHeaders)
  {
    if (paramCookieJar == CookieJar.NO_COOKIES);
    List localList;
    do
    {
      return;
      localList = Cookie.parseAll(paramHttpUrl, paramHeaders);
    }
    while (localList.isEmpty());
    paramCookieJar.saveFromResponse(paramHttpUrl, localList);
  }

  public static int skipUntil(String paramString1, int paramInt, String paramString2)
  {
    while (true)
    {
      if ((paramInt >= paramString1.length()) || (paramString2.indexOf(paramString1.charAt(paramInt)) != -1))
        return paramInt;
      paramInt++;
    }
  }

  public static int skipWhitespace(String paramString, int paramInt)
  {
    while (true)
    {
      if (paramInt < paramString.length())
      {
        int i = paramString.charAt(paramInt);
        if ((i == 32) || (i == 9));
      }
      else
      {
        return paramInt;
      }
      paramInt++;
    }
  }

  private static long stringToLong(String paramString)
  {
    if (paramString == null)
      return -1L;
    try
    {
      long l = Long.parseLong(paramString);
      return l;
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return -1L;
  }

  public static Set<String> varyFields(Headers paramHeaders)
  {
    Object localObject = Collections.emptySet();
    int i = 0;
    int j = paramHeaders.size();
    if (i < j)
    {
      if (!"Vary".equalsIgnoreCase(paramHeaders.name(i)));
      while (true)
      {
        i++;
        break;
        String str = paramHeaders.value(i);
        if (((Set)localObject).isEmpty())
          localObject = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        String[] arrayOfString = str.split(",");
        int k = arrayOfString.length;
        for (int m = 0; m < k; m++)
          ((Set)localObject).add(arrayOfString[m].trim());
      }
    }
    return (Set<String>)localObject;
  }

  private static Set<String> varyFields(Response paramResponse)
  {
    return varyFields(paramResponse.headers());
  }

  public static Headers varyHeaders(Headers paramHeaders1, Headers paramHeaders2)
  {
    Set localSet = varyFields(paramHeaders2);
    if (localSet.isEmpty())
      return new Headers.Builder().build();
    Headers.Builder localBuilder = new Headers.Builder();
    int i = 0;
    int j = paramHeaders1.size();
    while (i < j)
    {
      String str = paramHeaders1.name(i);
      if (localSet.contains(str))
        localBuilder.add(str, paramHeaders1.value(i));
      i++;
    }
    return localBuilder.build();
  }

  public static Headers varyHeaders(Response paramResponse)
  {
    return varyHeaders(paramResponse.networkResponse().request().headers(), paramResponse.headers());
  }

  public static boolean varyMatches(Response paramResponse, Headers paramHeaders, Request paramRequest)
  {
    Iterator localIterator = varyFields(paramResponse).iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!Util.equal(paramHeaders.values(str), paramRequest.headers(str)))
        return false;
    }
    return true;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.http.HttpHeaders
 * JD-Core Version:    0.6.0
 */