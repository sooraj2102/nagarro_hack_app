package okhttp3;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MediaType
{
  private static final Pattern PARAMETER;
  private static final String QUOTED = "\"([^\"]*)\"";
  private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
  private static final Pattern TYPE_SUBTYPE = Pattern.compile("([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)/([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)");
  private final String charset;
  private final String mediaType;
  private final String subtype;
  private final String type;

  static
  {
    PARAMETER = Pattern.compile(";\\s*(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)=(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)|\"([^\"]*)\"))?");
  }

  private MediaType(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this.mediaType = paramString1;
    this.type = paramString2;
    this.subtype = paramString3;
    this.charset = paramString4;
  }

  public static MediaType parse(String paramString)
  {
    Matcher localMatcher1 = TYPE_SUBTYPE.matcher(paramString);
    if (!localMatcher1.lookingAt());
    String str1;
    String str2;
    Object localObject;
    while (true)
    {
      return null;
      str1 = localMatcher1.group(1).toLowerCase(Locale.US);
      str2 = localMatcher1.group(2).toLowerCase(Locale.US);
      localObject = null;
      Matcher localMatcher2 = PARAMETER.matcher(paramString);
      int i = localMatcher1.end();
      if (i >= paramString.length())
        break;
      localMatcher2.region(i, paramString.length());
      if (!localMatcher2.lookingAt())
        continue;
      String str3 = localMatcher2.group(1);
      if ((str3 == null) || (!str3.equalsIgnoreCase("charset")));
      while (true)
      {
        i = localMatcher2.end();
        break;
        String str4 = localMatcher2.group(2);
        String str5;
        if (str4 != null)
          if ((str4.startsWith("'")) && (str4.endsWith("'")) && (str4.length() > 2))
            str5 = str4.substring(1, -1 + str4.length());
        while ((localObject != null) && (!str5.equalsIgnoreCase((String)localObject)))
        {
          throw new IllegalArgumentException("Multiple different charsets: " + paramString);
          str5 = str4;
          continue;
          str5 = localMatcher2.group(3);
        }
        localObject = str5;
      }
    }
    return (MediaType)new MediaType(paramString, str1, str2, (String)localObject);
  }

  public Charset charset()
  {
    if (this.charset != null)
      return Charset.forName(this.charset);
    return null;
  }

  public Charset charset(Charset paramCharset)
  {
    if (this.charset != null)
      paramCharset = Charset.forName(this.charset);
    return paramCharset;
  }

  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof MediaType)) && (((MediaType)paramObject).mediaType.equals(this.mediaType));
  }

  public int hashCode()
  {
    return this.mediaType.hashCode();
  }

  public String subtype()
  {
    return this.subtype;
  }

  public String toString()
  {
    return this.mediaType;
  }

  public String type()
  {
    return this.type;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.MediaType
 * JD-Core Version:    0.6.0
 */