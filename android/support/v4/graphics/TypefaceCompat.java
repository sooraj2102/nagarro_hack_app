package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat.FamilyResourceEntry;
import android.support.v4.content.res.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import android.support.v4.content.res.FontResourcesParserCompat.ProviderResourceEntry;
import android.support.v4.provider.FontsContractCompat;
import android.support.v4.provider.FontsContractCompat.FontInfo;
import android.support.v4.util.LruCache;
import android.widget.TextView;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class TypefaceCompat
{
  private static final String TAG = "TypefaceCompat";
  private static final LruCache<String, Typeface> sTypefaceCache;
  private static final TypefaceCompatImpl sTypefaceCompatImpl;

  static
  {
    if (Build.VERSION.SDK_INT >= 26)
      sTypefaceCompatImpl = new TypefaceCompatApi26Impl();
    while (true)
    {
      sTypefaceCache = new LruCache(16);
      return;
      if ((Build.VERSION.SDK_INT >= 24) && (TypefaceCompatApi24Impl.isUsable()))
      {
        sTypefaceCompatImpl = new TypefaceCompatApi24Impl();
        continue;
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        sTypefaceCompatImpl = new TypefaceCompatApi21Impl();
        continue;
      }
      sTypefaceCompatImpl = new TypefaceCompatBaseImpl();
    }
  }

  public static Typeface createFromFontInfo(Context paramContext, @Nullable CancellationSignal paramCancellationSignal, @NonNull FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    return sTypefaceCompatImpl.createFromFontInfo(paramContext, paramCancellationSignal, paramArrayOfFontInfo, paramInt);
  }

  public static Typeface createFromResourcesFamilyXml(Context paramContext, FontResourcesParserCompat.FamilyResourceEntry paramFamilyResourceEntry, Resources paramResources, int paramInt1, int paramInt2, @Nullable TextView paramTextView)
  {
    FontResourcesParserCompat.ProviderResourceEntry localProviderResourceEntry;
    if ((paramFamilyResourceEntry instanceof FontResourcesParserCompat.ProviderResourceEntry))
      localProviderResourceEntry = (FontResourcesParserCompat.ProviderResourceEntry)paramFamilyResourceEntry;
    for (Typeface localTypeface = FontsContractCompat.getFontSync(paramContext, localProviderResourceEntry.getRequest(), paramTextView, localProviderResourceEntry.getFetchStrategy(), localProviderResourceEntry.getTimeout(), paramInt2); ; localTypeface = sTypefaceCompatImpl.createFromFontFamilyFilesResourceEntry(paramContext, (FontResourcesParserCompat.FontFamilyFilesResourceEntry)paramFamilyResourceEntry, paramResources, paramInt2))
    {
      if (localTypeface != null)
        sTypefaceCache.put(createResourceUid(paramResources, paramInt1, paramInt2), localTypeface);
      return localTypeface;
    }
  }

  @Nullable
  public static Typeface createFromResourcesFontFile(Context paramContext, Resources paramResources, int paramInt1, String paramString, int paramInt2)
  {
    Typeface localTypeface = sTypefaceCompatImpl.createFromResourcesFontFile(paramContext, paramResources, paramInt1, paramString, paramInt2);
    if (localTypeface != null)
      sTypefaceCache.put(createResourceUid(paramResources, paramInt1, paramInt2), localTypeface);
    return localTypeface;
  }

  private static String createResourceUid(Resources paramResources, int paramInt1, int paramInt2)
  {
    return paramResources.getResourcePackageName(paramInt1) + "-" + paramInt1 + "-" + paramInt2;
  }

  public static Typeface findFromCache(Resources paramResources, int paramInt1, int paramInt2)
  {
    return (Typeface)sTypefaceCache.get(createResourceUid(paramResources, paramInt1, paramInt2));
  }

  static abstract interface TypefaceCompatImpl
  {
    public abstract Typeface createFromFontFamilyFilesResourceEntry(Context paramContext, FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, Resources paramResources, int paramInt);

    public abstract Typeface createFromFontInfo(Context paramContext, @Nullable CancellationSignal paramCancellationSignal, @NonNull FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt);

    public abstract Typeface createFromResourcesFontFile(Context paramContext, Resources paramResources, int paramInt1, String paramString, int paramInt2);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.graphics.TypefaceCompat
 * JD-Core Version:    0.6.0
 */