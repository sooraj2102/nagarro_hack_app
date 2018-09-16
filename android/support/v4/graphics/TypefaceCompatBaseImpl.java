package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import android.support.v4.content.res.FontResourcesParserCompat.FontFileResourceEntry;
import android.support.v4.provider.FontsContractCompat.FontInfo;
import java.io.File;
import java.io.InputStream;

@RequiresApi(14)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
class TypefaceCompatBaseImpl
  implements TypefaceCompat.TypefaceCompatImpl
{
  private static final String CACHE_FILE_PREFIX = "cached_font_";
  private static final String TAG = "TypefaceCompatBaseImpl";

  private FontResourcesParserCompat.FontFileResourceEntry findBestEntry(FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, int paramInt)
  {
    return (FontResourcesParserCompat.FontFileResourceEntry)findBestFont(paramFontFamilyFilesResourceEntry.getEntries(), paramInt, new StyleExtractor()
    {
      public int getWeight(FontResourcesParserCompat.FontFileResourceEntry paramFontFileResourceEntry)
      {
        return paramFontFileResourceEntry.getWeight();
      }

      public boolean isItalic(FontResourcesParserCompat.FontFileResourceEntry paramFontFileResourceEntry)
      {
        return paramFontFileResourceEntry.isItalic();
      }
    });
  }

  private static <T> T findBestFont(T[] paramArrayOfT, int paramInt, StyleExtractor<T> paramStyleExtractor)
  {
    int i;
    int j;
    label19: Object localObject;
    int k;
    int n;
    label33: T ?;
    int i1;
    if ((paramInt & 0x1) == 0)
    {
      i = 400;
      if ((paramInt & 0x2) == 0)
        break label119;
      j = 1;
      localObject = null;
      k = 2147483647;
      int m = paramArrayOfT.length;
      n = 0;
      if (n >= m)
        break label131;
      ? = paramArrayOfT[n];
      i1 = 2 * Math.abs(paramStyleExtractor.getWeight(?) - i);
      if (paramStyleExtractor.isItalic(?) != j)
        break label125;
    }
    label119: label125: for (int i2 = 0; ; i2 = 1)
    {
      int i3 = i1 + i2;
      if ((localObject == null) || (k > i3))
      {
        localObject = ?;
        k = i3;
      }
      n++;
      break label33;
      i = 700;
      break;
      j = 0;
      break label19;
    }
    label131: return localObject;
  }

  @Nullable
  public Typeface createFromFontFamilyFilesResourceEntry(Context paramContext, FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, Resources paramResources, int paramInt)
  {
    FontResourcesParserCompat.FontFileResourceEntry localFontFileResourceEntry = findBestEntry(paramFontFamilyFilesResourceEntry, paramInt);
    if (localFontFileResourceEntry == null)
      return null;
    return TypefaceCompat.createFromResourcesFontFile(paramContext, paramResources, localFontFileResourceEntry.getResourceId(), localFontFileResourceEntry.getFileName(), paramInt);
  }

  // ERROR //
  public Typeface createFromFontInfo(Context paramContext, @Nullable android.os.CancellationSignal paramCancellationSignal, @android.support.annotation.NonNull FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    // Byte code:
    //   0: aload_3
    //   1: arraylength
    //   2: iconst_1
    //   3: if_icmpge +5 -> 8
    //   6: aconst_null
    //   7: areturn
    //   8: aload_0
    //   9: aload_3
    //   10: iload 4
    //   12: invokevirtual 87	android/support/v4/graphics/TypefaceCompatBaseImpl:findBestInfo	([Landroid/support/v4/provider/FontsContractCompat$FontInfo;I)Landroid/support/v4/provider/FontsContractCompat$FontInfo;
    //   15: astore 5
    //   17: aconst_null
    //   18: astore 6
    //   20: aload_1
    //   21: invokevirtual 93	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   24: aload 5
    //   26: invokevirtual 99	android/support/v4/provider/FontsContractCompat$FontInfo:getUri	()Landroid/net/Uri;
    //   29: invokevirtual 105	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   32: astore 6
    //   34: aload_0
    //   35: aload_1
    //   36: aload 6
    //   38: invokevirtual 109	android/support/v4/graphics/TypefaceCompatBaseImpl:createFromInputStream	(Landroid/content/Context;Ljava/io/InputStream;)Landroid/graphics/Typeface;
    //   41: astore 9
    //   43: aload 6
    //   45: invokestatic 115	android/support/v4/graphics/TypefaceCompatUtil:closeQuietly	(Ljava/io/Closeable;)V
    //   48: aload 9
    //   50: areturn
    //   51: astore 8
    //   53: aload 6
    //   55: invokestatic 115	android/support/v4/graphics/TypefaceCompatUtil:closeQuietly	(Ljava/io/Closeable;)V
    //   58: aconst_null
    //   59: areturn
    //   60: astore 7
    //   62: aload 6
    //   64: invokestatic 115	android/support/v4/graphics/TypefaceCompatUtil:closeQuietly	(Ljava/io/Closeable;)V
    //   67: aload 7
    //   69: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   20	43	51	java/io/IOException
    //   20	43	60	finally
  }

  protected Typeface createFromInputStream(Context paramContext, InputStream paramInputStream)
  {
    File localFile = TypefaceCompatUtil.getTempFile(paramContext);
    if (localFile == null)
      return null;
    try
    {
      boolean bool = TypefaceCompatUtil.copyToFile(localFile, paramInputStream);
      if (!bool)
        return null;
      Typeface localTypeface = Typeface.createFromFile(localFile.getPath());
      return localTypeface;
    }
    catch (RuntimeException localRuntimeException)
    {
      return null;
    }
    finally
    {
      localFile.delete();
    }
    throw localObject;
  }

  @Nullable
  public Typeface createFromResourcesFontFile(Context paramContext, Resources paramResources, int paramInt1, String paramString, int paramInt2)
  {
    File localFile = TypefaceCompatUtil.getTempFile(paramContext);
    if (localFile == null)
      return null;
    try
    {
      boolean bool = TypefaceCompatUtil.copyToFile(localFile, paramResources, paramInt1);
      if (!bool)
        return null;
      Typeface localTypeface = Typeface.createFromFile(localFile.getPath());
      return localTypeface;
    }
    catch (RuntimeException localRuntimeException)
    {
      return null;
    }
    finally
    {
      localFile.delete();
    }
    throw localObject;
  }

  protected FontsContractCompat.FontInfo findBestInfo(FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    return (FontsContractCompat.FontInfo)findBestFont(paramArrayOfFontInfo, paramInt, new StyleExtractor()
    {
      public int getWeight(FontsContractCompat.FontInfo paramFontInfo)
      {
        return paramFontInfo.getWeight();
      }

      public boolean isItalic(FontsContractCompat.FontInfo paramFontInfo)
      {
        return paramFontInfo.isItalic();
      }
    });
  }

  private static abstract interface StyleExtractor<T>
  {
    public abstract int getWeight(T paramT);

    public abstract boolean isItalic(T paramT);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.graphics.TypefaceCompatBaseImpl
 * JD-Core Version:    0.6.0
 */