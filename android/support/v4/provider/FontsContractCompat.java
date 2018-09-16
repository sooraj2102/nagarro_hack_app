package android.support.v4.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build.VERSION;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.GuardedBy;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.graphics.TypefaceCompatUtil;
import android.support.v4.util.LruCache;
import android.support.v4.util.Preconditions;
import android.support.v4.util.SimpleArrayMap;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FontsContractCompat
{
  private static final int BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS = 10000;

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static final String PARCEL_FONT_RESULTS = "font_results";

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static final int RESULT_CODE_PROVIDER_NOT_FOUND = -1;

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static final int RESULT_CODE_WRONG_CERTIFICATES = -2;
  private static final String TAG = "FontsContractCompat";
  private static final SelfDestructiveThread sBackgroundThread;
  private static final Comparator<byte[]> sByteArrayComparator;
  private static final Object sLock;

  @GuardedBy("sLock")
  private static final SimpleArrayMap<String, ArrayList<SelfDestructiveThread.ReplyCallback<Typeface>>> sPendingReplies;
  private static final LruCache<String, Typeface> sTypefaceCache = new LruCache(16);

  static
  {
    sBackgroundThread = new SelfDestructiveThread("fonts", 10, 10000);
    sLock = new Object();
    sPendingReplies = new SimpleArrayMap();
    sByteArrayComparator = new Comparator()
    {
      public int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
      {
        if (paramArrayOfByte1.length != paramArrayOfByte2.length)
          return paramArrayOfByte1.length - paramArrayOfByte2.length;
        for (int i = 0; i < paramArrayOfByte1.length; i++)
          if (paramArrayOfByte1[i] != paramArrayOfByte2[i])
            return paramArrayOfByte1[i] - paramArrayOfByte2[i];
        return 0;
      }
    };
  }

  public static Typeface buildTypeface(@NonNull Context paramContext, @Nullable CancellationSignal paramCancellationSignal, @NonNull FontInfo[] paramArrayOfFontInfo)
  {
    return TypefaceCompat.createFromFontInfo(paramContext, paramCancellationSignal, paramArrayOfFontInfo, 0);
  }

  private static List<byte[]> convertToByteArrayList(Signature[] paramArrayOfSignature)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayOfSignature.length; i++)
      localArrayList.add(paramArrayOfSignature[i].toByteArray());
    return localArrayList;
  }

  private static boolean equalsByteArrayList(List<byte[]> paramList1, List<byte[]> paramList2)
  {
    if (paramList1.size() != paramList2.size())
      return false;
    for (int i = 0; i < paramList1.size(); i++)
      if (!Arrays.equals((byte[])paramList1.get(i), (byte[])paramList2.get(i)))
        return false;
    return true;
  }

  @NonNull
  public static FontFamilyResult fetchFonts(@NonNull Context paramContext, @Nullable CancellationSignal paramCancellationSignal, @NonNull FontRequest paramFontRequest)
    throws PackageManager.NameNotFoundException
  {
    ProviderInfo localProviderInfo = getProvider(paramContext.getPackageManager(), paramFontRequest, paramContext.getResources());
    if (localProviderInfo == null)
      return new FontFamilyResult(1, null);
    return new FontFamilyResult(0, getFontFromProvider(paramContext, paramFontRequest, localProviderInfo.authority, paramCancellationSignal));
  }

  private static List<List<byte[]>> getCertificates(FontRequest paramFontRequest, Resources paramResources)
  {
    if (paramFontRequest.getCertificates() != null)
      return paramFontRequest.getCertificates();
    return FontResourcesParserCompat.readCerts(paramResources, paramFontRequest.getCertificatesArrayResId());
  }

  @NonNull
  @VisibleForTesting
  static FontInfo[] getFontFromProvider(Context paramContext, FontRequest paramFontRequest, String paramString, CancellationSignal paramCancellationSignal)
  {
    Object localObject1 = new ArrayList();
    Uri localUri1 = new Uri.Builder().scheme("content").authority(paramString).build();
    Uri localUri2 = new Uri.Builder().scheme("content").authority(paramString).appendPath("file").build();
    Object localObject2 = null;
    try
    {
      int i = Build.VERSION.SDK_INT;
      localObject2 = null;
      int j;
      ArrayList localArrayList;
      if (i > 16)
      {
        ContentResolver localContentResolver1 = paramContext.getContentResolver();
        String[] arrayOfString1 = { "_id", "file_id", "font_ttc_index", "font_variation_settings", "font_weight", "font_italic", "result_code" };
        String[] arrayOfString2 = new String[1];
        arrayOfString2[0] = paramFontRequest.getQuery();
        localObject2 = localContentResolver1.query(localUri1, arrayOfString1, "query = ?", arrayOfString2, null, paramCancellationSignal);
        if ((localObject2 != null) && (((Cursor)localObject2).getCount() > 0))
        {
          j = ((Cursor)localObject2).getColumnIndex("result_code");
          localArrayList = new ArrayList();
        }
      }
      else
      {
        while (true)
        {
          int m;
          try
          {
            int k = ((Cursor)localObject2).getColumnIndex("_id");
            m = ((Cursor)localObject2).getColumnIndex("file_id");
            int n = ((Cursor)localObject2).getColumnIndex("font_ttc_index");
            int i1 = ((Cursor)localObject2).getColumnIndex("font_weight");
            int i2 = ((Cursor)localObject2).getColumnIndex("font_italic");
            if (!((Cursor)localObject2).moveToNext())
              break label527;
            if (j == -1)
              break label478;
            i3 = ((Cursor)localObject2).getInt(j);
            if (n == -1)
              break label484;
            i4 = ((Cursor)localObject2).getInt(n);
            if (m != -1)
              break label490;
            localObject5 = ContentUris.withAppendedId(localUri1, ((Cursor)localObject2).getLong(k));
            if (i1 == -1)
              break label513;
            i5 = ((Cursor)localObject2).getInt(i1);
            if ((i2 == -1) || (((Cursor)localObject2).getInt(i2) != 1))
              break label521;
            bool = true;
            localArrayList.add(new FontInfo((Uri)localObject5, i4, i5, bool, i3));
            continue;
          }
          finally
          {
          }
          label377: if (localObject2 != null)
            ((Cursor)localObject2).close();
          throw localObject3;
          ContentResolver localContentResolver2 = paramContext.getContentResolver();
          String[] arrayOfString3 = { "_id", "file_id", "font_ttc_index", "font_variation_settings", "font_weight", "font_italic", "result_code" };
          String[] arrayOfString4 = new String[1];
          arrayOfString4[0] = paramFontRequest.getQuery();
          Cursor localCursor = localContentResolver2.query(localUri1, arrayOfString3, "query = ?", arrayOfString4, null);
          localObject2 = localCursor;
          break;
          label478: int i3 = 0;
          continue;
          label484: int i4 = 0;
          continue;
          label490: Uri localUri3 = ContentUris.withAppendedId(localUri2, ((Cursor)localObject2).getLong(m));
          Object localObject5 = localUri3;
          continue;
          label513: int i5 = 400;
          continue;
          label521: boolean bool = false;
        }
        label527: localObject1 = localArrayList;
      }
      if (localObject2 != null)
        ((Cursor)localObject2).close();
      FontInfo[] arrayOfFontInfo = new FontInfo[0];
      return (FontInfo[])((ArrayList)localObject1).toArray(arrayOfFontInfo);
    }
    finally
    {
      break label377;
    }
  }

  private static Typeface getFontInternal(Context paramContext, FontRequest paramFontRequest, int paramInt)
  {
    try
    {
      FontFamilyResult localFontFamilyResult = fetchFonts(paramContext, null, paramFontRequest);
      int i = localFontFamilyResult.getStatusCode();
      Typeface localTypeface = null;
      if (i == 0)
        localTypeface = TypefaceCompat.createFromFontInfo(paramContext, null, localFontFamilyResult.getFonts(), paramInt);
      return localTypeface;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    return null;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static Typeface getFontSync(Context paramContext, FontRequest paramFontRequest, @Nullable TextView paramTextView, int paramInt1, int paramInt2, int paramInt3)
  {
    String str = paramFontRequest.getIdentifier() + "-" + paramInt3;
    Typeface localTypeface1 = (Typeface)sTypefaceCache.get(str);
    if (localTypeface1 != null)
      return localTypeface1;
    if (paramInt1 == 0);
    for (int i = 1; (i != 0) && (paramInt2 == -1); i = 0)
      return getFontInternal(paramContext, paramFontRequest, paramInt3);
    1 local1 = new Callable(paramContext, paramFontRequest, paramInt3, str)
    {
      public Typeface call()
        throws Exception
      {
        Typeface localTypeface = FontsContractCompat.access$000(this.val$context, this.val$request, this.val$style);
        if (localTypeface != null)
          FontsContractCompat.sTypefaceCache.put(this.val$id, localTypeface);
        return localTypeface;
      }
    };
    if (i != 0)
      try
      {
        Typeface localTypeface2 = (Typeface)sBackgroundThread.postAndWait(local1, paramInt2);
        return localTypeface2;
      }
      catch (InterruptedException localInterruptedException)
      {
        return null;
      }
    2 local2 = new SelfDestructiveThread.ReplyCallback(new WeakReference(paramTextView), paramTextView, paramInt3)
    {
      public void onReply(Typeface paramTypeface)
      {
        if ((TextView)this.val$textViewWeak.get() != null)
          this.val$targetView.setTypeface(paramTypeface, this.val$style);
      }
    };
    synchronized (sLock)
    {
      if (sPendingReplies.containsKey(str))
      {
        ((ArrayList)sPendingReplies.get(str)).add(local2);
        return null;
      }
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(local2);
    sPendingReplies.put(str, localArrayList);
    monitorexit;
    sBackgroundThread.postAndReply(local1, new SelfDestructiveThread.ReplyCallback(str)
    {
      public void onReply(Typeface paramTypeface)
      {
        synchronized (FontsContractCompat.sLock)
        {
          ArrayList localArrayList = (ArrayList)FontsContractCompat.sPendingReplies.get(this.val$id);
          FontsContractCompat.sPendingReplies.remove(this.val$id);
          int i = 0;
          if (i < localArrayList.size())
          {
            ((SelfDestructiveThread.ReplyCallback)localArrayList.get(i)).onReply(paramTypeface);
            i++;
          }
        }
      }
    });
    return null;
  }

  @Nullable
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  @VisibleForTesting
  public static ProviderInfo getProvider(@NonNull PackageManager paramPackageManager, @NonNull FontRequest paramFontRequest, @Nullable Resources paramResources)
    throws PackageManager.NameNotFoundException
  {
    String str = paramFontRequest.getProviderAuthority();
    ProviderInfo localProviderInfo = paramPackageManager.resolveContentProvider(str, 0);
    if (localProviderInfo == null)
      throw new PackageManager.NameNotFoundException("No package found for authority: " + str);
    if (!localProviderInfo.packageName.equals(paramFontRequest.getProviderPackage()))
      throw new PackageManager.NameNotFoundException("Found content provider " + str + ", but package was not " + paramFontRequest.getProviderPackage());
    List localList1 = convertToByteArrayList(paramPackageManager.getPackageInfo(localProviderInfo.packageName, 64).signatures);
    Collections.sort(localList1, sByteArrayComparator);
    List localList2 = getCertificates(paramFontRequest, paramResources);
    for (int i = 0; i < localList2.size(); i++)
    {
      ArrayList localArrayList = new ArrayList((Collection)localList2.get(i));
      Collections.sort(localArrayList, sByteArrayComparator);
      if (equalsByteArrayList(localList1, localArrayList))
        return localProviderInfo;
    }
    return null;
  }

  @RequiresApi(19)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static Map<Uri, ByteBuffer> prepareFontData(Context paramContext, FontInfo[] paramArrayOfFontInfo, CancellationSignal paramCancellationSignal)
  {
    HashMap localHashMap = new HashMap();
    int i = paramArrayOfFontInfo.length;
    int j = 0;
    if (j < i)
    {
      FontInfo localFontInfo = paramArrayOfFontInfo[j];
      if (localFontInfo.getResultCode() != 0);
      while (true)
      {
        j++;
        break;
        Uri localUri = localFontInfo.getUri();
        if (localHashMap.containsKey(localUri))
          continue;
        localHashMap.put(localUri, TypefaceCompatUtil.mmap(paramContext, paramCancellationSignal, localUri));
      }
    }
    return Collections.unmodifiableMap(localHashMap);
  }

  public static void requestFont(@NonNull Context paramContext, @NonNull FontRequest paramFontRequest, @NonNull FontRequestCallback paramFontRequestCallback, @NonNull Handler paramHandler)
  {
    paramHandler.post(new Runnable(paramContext, paramFontRequest, new Handler(), paramFontRequestCallback)
    {
      public void run()
      {
        FontsContractCompat.FontFamilyResult localFontFamilyResult;
        try
        {
          localFontFamilyResult = FontsContractCompat.fetchFonts(this.val$context, null, this.val$request);
          if (localFontFamilyResult.getStatusCode() == 0)
            break label117;
          switch (localFontFamilyResult.getStatusCode())
          {
          default:
            this.val$callerThreadHandler.post(new Runnable()
            {
              public void run()
              {
                FontsContractCompat.4.this.val$callback.onTypefaceRequestFailed(-3);
              }
            });
            return;
          case 1:
          case 2:
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          this.val$callerThreadHandler.post(new Runnable()
          {
            public void run()
            {
              FontsContractCompat.4.this.val$callback.onTypefaceRequestFailed(-1);
            }
          });
          return;
        }
        this.val$callerThreadHandler.post(new Runnable()
        {
          public void run()
          {
            FontsContractCompat.4.this.val$callback.onTypefaceRequestFailed(-2);
          }
        });
        return;
        this.val$callerThreadHandler.post(new Runnable()
        {
          public void run()
          {
            FontsContractCompat.4.this.val$callback.onTypefaceRequestFailed(-3);
          }
        });
        return;
        label117: FontsContractCompat.FontInfo[] arrayOfFontInfo = localFontFamilyResult.getFonts();
        if ((arrayOfFontInfo == null) || (arrayOfFontInfo.length == 0))
        {
          this.val$callerThreadHandler.post(new Runnable()
          {
            public void run()
            {
              FontsContractCompat.4.this.val$callback.onTypefaceRequestFailed(1);
            }
          });
          return;
        }
        int i = arrayOfFontInfo.length;
        for (int j = 0; j < i; j++)
        {
          FontsContractCompat.FontInfo localFontInfo = arrayOfFontInfo[j];
          if (localFontInfo.getResultCode() == 0)
            continue;
          int k = localFontInfo.getResultCode();
          if (k < 0)
          {
            this.val$callerThreadHandler.post(new Runnable()
            {
              public void run()
              {
                FontsContractCompat.4.this.val$callback.onTypefaceRequestFailed(-3);
              }
            });
            return;
          }
          this.val$callerThreadHandler.post(new Runnable(k)
          {
            public void run()
            {
              FontsContractCompat.4.this.val$callback.onTypefaceRequestFailed(this.val$resultCode);
            }
          });
          return;
        }
        Typeface localTypeface = FontsContractCompat.buildTypeface(this.val$context, null, arrayOfFontInfo);
        if (localTypeface == null)
        {
          this.val$callerThreadHandler.post(new Runnable()
          {
            public void run()
            {
              FontsContractCompat.4.this.val$callback.onTypefaceRequestFailed(-3);
            }
          });
          return;
        }
        this.val$callerThreadHandler.post(new Runnable(localTypeface)
        {
          public void run()
          {
            FontsContractCompat.4.this.val$callback.onTypefaceRetrieved(this.val$typeface);
          }
        });
      }
    });
  }

  public static final class Columns
    implements BaseColumns
  {
    public static final String FILE_ID = "file_id";
    public static final String ITALIC = "font_italic";
    public static final String RESULT_CODE = "result_code";
    public static final int RESULT_CODE_FONT_NOT_FOUND = 1;
    public static final int RESULT_CODE_FONT_UNAVAILABLE = 2;
    public static final int RESULT_CODE_MALFORMED_QUERY = 3;
    public static final int RESULT_CODE_OK = 0;
    public static final String TTC_INDEX = "font_ttc_index";
    public static final String VARIATION_SETTINGS = "font_variation_settings";
    public static final String WEIGHT = "font_weight";
  }

  public static class FontFamilyResult
  {
    public static final int STATUS_OK = 0;
    public static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2;
    public static final int STATUS_WRONG_CERTIFICATES = 1;
    private final FontsContractCompat.FontInfo[] mFonts;
    private final int mStatusCode;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public FontFamilyResult(int paramInt, @Nullable FontsContractCompat.FontInfo[] paramArrayOfFontInfo)
    {
      this.mStatusCode = paramInt;
      this.mFonts = paramArrayOfFontInfo;
    }

    public FontsContractCompat.FontInfo[] getFonts()
    {
      return this.mFonts;
    }

    public int getStatusCode()
    {
      return this.mStatusCode;
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    static @interface FontResultStatus
    {
    }
  }

  public static class FontInfo
  {
    private final boolean mItalic;
    private final int mResultCode;
    private final int mTtcIndex;
    private final Uri mUri;
    private final int mWeight;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public FontInfo(@NonNull Uri paramUri, @IntRange(from=0L) int paramInt1, @IntRange(from=1L, to=1000L) int paramInt2, boolean paramBoolean, int paramInt3)
    {
      this.mUri = ((Uri)Preconditions.checkNotNull(paramUri));
      this.mTtcIndex = paramInt1;
      this.mWeight = paramInt2;
      this.mItalic = paramBoolean;
      this.mResultCode = paramInt3;
    }

    public int getResultCode()
    {
      return this.mResultCode;
    }

    @IntRange(from=0L)
    public int getTtcIndex()
    {
      return this.mTtcIndex;
    }

    @NonNull
    public Uri getUri()
    {
      return this.mUri;
    }

    @IntRange(from=1L, to=1000L)
    public int getWeight()
    {
      return this.mWeight;
    }

    public boolean isItalic()
    {
      return this.mItalic;
    }
  }

  public static class FontRequestCallback
  {
    public static final int FAIL_REASON_FONT_LOAD_ERROR = -3;
    public static final int FAIL_REASON_FONT_NOT_FOUND = 1;
    public static final int FAIL_REASON_FONT_UNAVAILABLE = 2;
    public static final int FAIL_REASON_MALFORMED_QUERY = 3;
    public static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1;
    public static final int FAIL_REASON_WRONG_CERTIFICATES = -2;

    public void onTypefaceRequestFailed(int paramInt)
    {
    }

    public void onTypefaceRetrieved(Typeface paramTypeface)
    {
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    static @interface FontRequestFailReason
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.provider.FontsContractCompat
 * JD-Core Version:    0.6.0
 */