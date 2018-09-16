package android.support.v4.content.res;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.compat.R.styleable;
import android.support.v4.provider.FontRequest;
import android.util.Base64;
import android.util.Xml;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class FontResourcesParserCompat
{
  private static final int DEFAULT_TIMEOUT_MILLIS = 500;
  public static final int FETCH_STRATEGY_ASYNC = 1;
  public static final int FETCH_STRATEGY_BLOCKING = 0;
  public static final int INFINITE_TIMEOUT_VALUE = -1;
  private static final int ITALIC = 1;
  private static final int NORMAL_WEIGHT = 400;

  @Nullable
  public static FamilyResourceEntry parse(XmlPullParser paramXmlPullParser, Resources paramResources)
    throws XmlPullParserException, IOException
  {
    int i;
    do
      i = paramXmlPullParser.next();
    while ((i != 2) && (i != 1));
    if (i != 2)
      throw new XmlPullParserException("No start tag found");
    return readFamilies(paramXmlPullParser, paramResources);
  }

  public static List<List<byte[]>> readCerts(Resources paramResources, @ArrayRes int paramInt)
  {
    ArrayList localArrayList = null;
    if (paramInt != 0)
    {
      TypedArray localTypedArray = paramResources.obtainTypedArray(paramInt);
      int i = localTypedArray.length();
      localArrayList = null;
      if (i > 0)
      {
        localArrayList = new ArrayList();
        int j;
        if (localTypedArray.getResourceId(0, 0) != 0)
          j = 1;
        while (true)
        {
          if (j != 0)
          {
            for (int k = 0; k < localTypedArray.length(); k++)
              localArrayList.add(toByteArrayList(paramResources.getStringArray(localTypedArray.getResourceId(k, 0))));
            j = 0;
            continue;
          }
          localArrayList.add(toByteArrayList(paramResources.getStringArray(paramInt)));
        }
      }
      localTypedArray.recycle();
    }
    if (localArrayList != null)
      return localArrayList;
    return Collections.emptyList();
  }

  @Nullable
  private static FamilyResourceEntry readFamilies(XmlPullParser paramXmlPullParser, Resources paramResources)
    throws XmlPullParserException, IOException
  {
    paramXmlPullParser.require(2, null, "font-family");
    if (paramXmlPullParser.getName().equals("font-family"))
      return readFamily(paramXmlPullParser, paramResources);
    skip(paramXmlPullParser);
    return null;
  }

  @Nullable
  private static FamilyResourceEntry readFamily(XmlPullParser paramXmlPullParser, Resources paramResources)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = paramResources.obtainAttributes(Xml.asAttributeSet(paramXmlPullParser), R.styleable.FontFamily);
    String str1 = localTypedArray.getString(R.styleable.FontFamily_fontProviderAuthority);
    String str2 = localTypedArray.getString(R.styleable.FontFamily_fontProviderPackage);
    String str3 = localTypedArray.getString(R.styleable.FontFamily_fontProviderQuery);
    int i = localTypedArray.getResourceId(R.styleable.FontFamily_fontProviderCerts, 0);
    int j = localTypedArray.getInteger(R.styleable.FontFamily_fontProviderFetchStrategy, 1);
    int k = localTypedArray.getInteger(R.styleable.FontFamily_fontProviderFetchTimeout, 500);
    localTypedArray.recycle();
    if ((str1 != null) && (str2 != null) && (str3 != null))
    {
      while (paramXmlPullParser.next() != 3)
        skip(paramXmlPullParser);
      return new ProviderResourceEntry(new FontRequest(str1, str2, str3, readCerts(paramResources, i)), j, k);
    }
    ArrayList localArrayList = new ArrayList();
    while (paramXmlPullParser.next() != 3)
    {
      if (paramXmlPullParser.getEventType() != 2)
        continue;
      if (paramXmlPullParser.getName().equals("font"))
      {
        localArrayList.add(readFont(paramXmlPullParser, paramResources));
        continue;
      }
      skip(paramXmlPullParser);
    }
    if (localArrayList.isEmpty())
      return null;
    return new FontFamilyFilesResourceEntry((FontFileResourceEntry[])localArrayList.toArray(new FontFileResourceEntry[localArrayList.size()]));
  }

  private static FontFileResourceEntry readFont(XmlPullParser paramXmlPullParser, Resources paramResources)
    throws XmlPullParserException, IOException
  {
    int i = 1;
    TypedArray localTypedArray = paramResources.obtainAttributes(Xml.asAttributeSet(paramXmlPullParser), R.styleable.FontFamilyFont);
    int j = localTypedArray.getInt(R.styleable.FontFamilyFont_fontWeight, 400);
    if (i == localTypedArray.getInt(R.styleable.FontFamilyFont_fontStyle, 0));
    int k;
    String str;
    while (true)
    {
      k = localTypedArray.getResourceId(R.styleable.FontFamilyFont_font, 0);
      str = localTypedArray.getString(R.styleable.FontFamilyFont_font);
      localTypedArray.recycle();
      while (paramXmlPullParser.next() != 3)
        skip(paramXmlPullParser);
      i = 0;
    }
    return new FontFileResourceEntry(str, j, i, k);
  }

  private static void skip(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i = 1;
    while (i > 0)
      switch (paramXmlPullParser.next())
      {
      default:
        break;
      case 2:
        i++;
        break;
      case 3:
        i--;
      }
  }

  private static List<byte[]> toByteArrayList(String[] paramArrayOfString)
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramArrayOfString.length;
    for (int j = 0; j < i; j++)
      localArrayList.add(Base64.decode(paramArrayOfString[j], 0));
    return localArrayList;
  }

  public static abstract interface FamilyResourceEntry
  {
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface FetchStrategy
  {
  }

  public static final class FontFamilyFilesResourceEntry
    implements FontResourcesParserCompat.FamilyResourceEntry
  {

    @NonNull
    private final FontResourcesParserCompat.FontFileResourceEntry[] mEntries;

    public FontFamilyFilesResourceEntry(@NonNull FontResourcesParserCompat.FontFileResourceEntry[] paramArrayOfFontFileResourceEntry)
    {
      this.mEntries = paramArrayOfFontFileResourceEntry;
    }

    @NonNull
    public FontResourcesParserCompat.FontFileResourceEntry[] getEntries()
    {
      return this.mEntries;
    }
  }

  public static final class FontFileResourceEntry
  {

    @NonNull
    private final String mFileName;
    private boolean mItalic;
    private int mResourceId;
    private int mWeight;

    public FontFileResourceEntry(@NonNull String paramString, int paramInt1, boolean paramBoolean, int paramInt2)
    {
      this.mFileName = paramString;
      this.mWeight = paramInt1;
      this.mItalic = paramBoolean;
      this.mResourceId = paramInt2;
    }

    @NonNull
    public String getFileName()
    {
      return this.mFileName;
    }

    public int getResourceId()
    {
      return this.mResourceId;
    }

    public int getWeight()
    {
      return this.mWeight;
    }

    public boolean isItalic()
    {
      return this.mItalic;
    }
  }

  public static final class ProviderResourceEntry
    implements FontResourcesParserCompat.FamilyResourceEntry
  {

    @NonNull
    private final FontRequest mRequest;
    private final int mStrategy;
    private final int mTimeoutMs;

    public ProviderResourceEntry(@NonNull FontRequest paramFontRequest, int paramInt1, int paramInt2)
    {
      this.mRequest = paramFontRequest;
      this.mStrategy = paramInt1;
      this.mTimeoutMs = paramInt2;
    }

    public int getFetchStrategy()
    {
      return this.mStrategy;
    }

    @NonNull
    public FontRequest getRequest()
    {
      return this.mRequest;
    }

    public int getTimeout()
    {
      return this.mTimeoutMs;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.content.res.FontResourcesParserCompat
 * JD-Core Version:    0.6.0
 */