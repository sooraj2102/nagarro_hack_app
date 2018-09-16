package android.support.v4.graphics;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.fonts.FontVariationAxis;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import android.support.v4.content.res.FontResourcesParserCompat.FontFileResourceEntry;
import android.util.Log;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

@RequiresApi(26)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class TypefaceCompatApi26Impl extends TypefaceCompatApi21Impl
{
  private static final String ABORT_CREATION_METHOD = "abortCreation";
  private static final String ADD_FONT_FROM_ASSET_MANAGER_METHOD = "addFontFromAssetManager";
  private static final String ADD_FONT_FROM_BUFFER_METHOD = "addFontFromBuffer";
  private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
  private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
  private static final String FREEZE_METHOD = "freeze";
  private static final int RESOLVE_BY_FONT_TABLE = -1;
  private static final String TAG = "TypefaceCompatApi26Impl";
  private static final Method sAbortCreation;
  private static final Method sAddFontFromAssetManager;
  private static final Method sAddFontFromBuffer;
  private static final Method sCreateFromFamiliesWithDefault;
  private static final Class sFontFamily;
  private static final Constructor sFontFamilyCtor;
  private static final Method sFreeze;

  static
  {
    try
    {
      localClass = Class.forName("android.graphics.FontFamily");
      localConstructor = localClass.getConstructor(new Class[0]);
      Class[] arrayOfClass1 = new Class[8];
      arrayOfClass1[0] = AssetManager.class;
      arrayOfClass1[1] = String.class;
      arrayOfClass1[2] = Integer.TYPE;
      arrayOfClass1[3] = Boolean.TYPE;
      arrayOfClass1[4] = Integer.TYPE;
      arrayOfClass1[5] = Integer.TYPE;
      arrayOfClass1[6] = Integer.TYPE;
      arrayOfClass1[7] = [Landroid.graphics.fonts.FontVariationAxis.class;
      localMethod2 = localClass.getMethod("addFontFromAssetManager", arrayOfClass1);
      Class[] arrayOfClass2 = new Class[5];
      arrayOfClass2[0] = ByteBuffer.class;
      arrayOfClass2[1] = Integer.TYPE;
      arrayOfClass2[2] = [Landroid.graphics.fonts.FontVariationAxis.class;
      arrayOfClass2[3] = Integer.TYPE;
      arrayOfClass2[4] = Integer.TYPE;
      localMethod3 = localClass.getMethod("addFontFromBuffer", arrayOfClass2);
      localMethod5 = localClass.getMethod("freeze", new Class[0]);
      localMethod1 = localClass.getMethod("abortCreation", new Class[0]);
      Object localObject = Array.newInstance(localClass, 1);
      Class[] arrayOfClass3 = new Class[3];
      arrayOfClass3[0] = localObject.getClass();
      arrayOfClass3[1] = Integer.TYPE;
      arrayOfClass3[2] = Integer.TYPE;
      localMethod4 = Typeface.class.getDeclaredMethod("createFromFamiliesWithDefault", arrayOfClass3);
      localMethod4.setAccessible(true);
      sFontFamilyCtor = localConstructor;
      sFontFamily = localClass;
      sAddFontFromAssetManager = localMethod2;
      sAddFontFromBuffer = localMethod3;
      sFreeze = localMethod5;
      sAbortCreation = localMethod1;
      sCreateFromFamiliesWithDefault = localMethod4;
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      while (true)
      {
        Log.e("TypefaceCompatApi26Impl", "Unable to collect necessary methods for class " + localClassNotFoundException.getClass().getName(), localClassNotFoundException);
        Method localMethod1 = null;
        Method localMethod2 = null;
        Method localMethod3 = null;
        Method localMethod4 = null;
        Class localClass = null;
        Constructor localConstructor = null;
        Method localMethod5 = null;
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      label254: break label254;
    }
  }

  private static boolean abortCreation(Object paramObject)
  {
    try
    {
      boolean bool = ((Boolean)sAbortCreation.invoke(paramObject, new Object[0])).booleanValue();
      return bool;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      label21: break label21;
    }
  }

  private static boolean addFontFromAssetManager(Context paramContext, Object paramObject, String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      Method localMethod = sAddFontFromAssetManager;
      Object[] arrayOfObject = new Object[8];
      arrayOfObject[0] = paramContext.getAssets();
      arrayOfObject[1] = paramString;
      arrayOfObject[2] = Integer.valueOf(0);
      arrayOfObject[3] = Boolean.valueOf(false);
      arrayOfObject[4] = Integer.valueOf(paramInt1);
      arrayOfObject[5] = Integer.valueOf(paramInt2);
      arrayOfObject[6] = Integer.valueOf(paramInt3);
      arrayOfObject[7] = null;
      boolean bool = ((Boolean)localMethod.invoke(paramObject, arrayOfObject)).booleanValue();
      return bool;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      label95: break label95;
    }
  }

  private static boolean addFontFromBuffer(Object paramObject, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      Method localMethod = sAddFontFromBuffer;
      Object[] arrayOfObject = new Object[5];
      arrayOfObject[0] = paramByteBuffer;
      arrayOfObject[1] = Integer.valueOf(paramInt1);
      arrayOfObject[2] = null;
      arrayOfObject[3] = Integer.valueOf(paramInt2);
      arrayOfObject[4] = Integer.valueOf(paramInt3);
      boolean bool = ((Boolean)localMethod.invoke(paramObject, arrayOfObject)).booleanValue();
      return bool;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      label67: break label67;
    }
  }

  private static Typeface createFromFamiliesWithDefault(Object paramObject)
  {
    try
    {
      Object localObject = Array.newInstance(sFontFamily, 1);
      Array.set(localObject, 0, paramObject);
      Method localMethod = sCreateFromFamiliesWithDefault;
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = localObject;
      arrayOfObject[1] = Integer.valueOf(-1);
      arrayOfObject[2] = Integer.valueOf(-1);
      Typeface localTypeface = (Typeface)localMethod.invoke(null, arrayOfObject);
      return localTypeface;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      label61: break label61;
    }
  }

  private static boolean freeze(Object paramObject)
  {
    try
    {
      boolean bool = ((Boolean)sFreeze.invoke(paramObject, new Object[0])).booleanValue();
      return bool;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      label21: break label21;
    }
  }

  private static boolean isFontFamilyPrivateAPIAvailable()
  {
    if (sAddFontFromAssetManager == null)
      Log.w("TypefaceCompatApi26Impl", "Unable to collect necessary private methods.Fallback to legacy implementation.");
    return sAddFontFromAssetManager != null;
  }

  private static Object newFamily()
  {
    try
    {
      Object localObject = sFontFamilyCtor.newInstance(new Object[0]);
      return localObject;
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new RuntimeException(localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      break label14;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      label14: break label14;
    }
  }

  public Typeface createFromFontFamilyFilesResourceEntry(Context paramContext, FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, Resources paramResources, int paramInt)
  {
    if (!isFontFamilyPrivateAPIAvailable())
      return super.createFromFontFamilyFilesResourceEntry(paramContext, paramFontFamilyFilesResourceEntry, paramResources, paramInt);
    Object localObject = newFamily();
    for (FontResourcesParserCompat.FontFileResourceEntry localFontFileResourceEntry : paramFontFamilyFilesResourceEntry.getEntries())
    {
      String str = localFontFileResourceEntry.getFileName();
      int k = localFontFileResourceEntry.getWeight();
      if (localFontFileResourceEntry.isItalic());
      for (int m = 1; !addFontFromAssetManager(paramContext, localObject, str, 0, k, m); m = 0)
      {
        abortCreation(localObject);
        return null;
      }
    }
    if (!freeze(localObject))
      return null;
    return createFromFamiliesWithDefault(localObject);
  }

  // ERROR //
  public Typeface createFromFontInfo(Context paramContext, @Nullable android.os.CancellationSignal paramCancellationSignal, @android.support.annotation.NonNull android.support.v4.provider.FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    // Byte code:
    //   0: aload_3
    //   1: arraylength
    //   2: iconst_1
    //   3: if_icmpge +9 -> 12
    //   6: aconst_null
    //   7: astore 27
    //   9: aload 27
    //   11: areturn
    //   12: invokestatic 201	android/support/v4/graphics/TypefaceCompatApi26Impl:isFontFamilyPrivateAPIAvailable	()Z
    //   15: ifne +159 -> 174
    //   18: aload_0
    //   19: aload_3
    //   20: iload 4
    //   22: invokevirtual 243	android/support/v4/graphics/TypefaceCompatApi26Impl:findBestInfo	([Landroid/support/v4/provider/FontsContractCompat$FontInfo;I)Landroid/support/v4/provider/FontsContractCompat$FontInfo;
    //   25: astore 17
    //   27: aload_1
    //   28: invokevirtual 247	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   31: astore 18
    //   33: aload 18
    //   35: aload 17
    //   37: invokevirtual 253	android/support/v4/provider/FontsContractCompat$FontInfo:getUri	()Landroid/net/Uri;
    //   40: ldc 255
    //   42: aload_2
    //   43: invokevirtual 261	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
    //   46: astore 20
    //   48: new 263	android/graphics/Typeface$Builder
    //   51: dup
    //   52: aload 20
    //   54: invokevirtual 269	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   57: invokespecial 272	android/graphics/Typeface$Builder:<init>	(Ljava/io/FileDescriptor;)V
    //   60: aload 17
    //   62: invokevirtual 273	android/support/v4/provider/FontsContractCompat$FontInfo:getWeight	()I
    //   65: invokevirtual 277	android/graphics/Typeface$Builder:setWeight	(I)Landroid/graphics/Typeface$Builder;
    //   68: aload 17
    //   70: invokevirtual 278	android/support/v4/provider/FontsContractCompat$FontInfo:isItalic	()Z
    //   73: invokevirtual 282	android/graphics/Typeface$Builder:setItalic	(Z)Landroid/graphics/Typeface$Builder;
    //   76: invokevirtual 286	android/graphics/Typeface$Builder:build	()Landroid/graphics/Typeface;
    //   79: astore 26
    //   81: aload 26
    //   83: astore 27
    //   85: aload 20
    //   87: ifnull -78 -> 9
    //   90: iconst_0
    //   91: ifeq +22 -> 113
    //   94: aload 20
    //   96: invokevirtual 289	android/os/ParcelFileDescriptor:close	()V
    //   99: aload 27
    //   101: areturn
    //   102: astore 28
    //   104: aconst_null
    //   105: aload 28
    //   107: invokevirtual 292	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   110: aload 27
    //   112: areturn
    //   113: aload 20
    //   115: invokevirtual 289	android/os/ParcelFileDescriptor:close	()V
    //   118: aload 27
    //   120: areturn
    //   121: astore 24
    //   123: aload 24
    //   125: athrow
    //   126: astore 25
    //   128: aload 24
    //   130: astore 22
    //   132: aload 25
    //   134: astore 21
    //   136: aload 20
    //   138: ifnull +13 -> 151
    //   141: aload 22
    //   143: ifnull +23 -> 166
    //   146: aload 20
    //   148: invokevirtual 289	android/os/ParcelFileDescriptor:close	()V
    //   151: aload 21
    //   153: athrow
    //   154: astore 23
    //   156: aload 22
    //   158: aload 23
    //   160: invokevirtual 292	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   163: goto -12 -> 151
    //   166: aload 20
    //   168: invokevirtual 289	android/os/ParcelFileDescriptor:close	()V
    //   171: goto -20 -> 151
    //   174: aload_1
    //   175: aload_3
    //   176: aload_2
    //   177: invokestatic 298	android/support/v4/provider/FontsContractCompat:prepareFontData	(Landroid/content/Context;[Landroid/support/v4/provider/FontsContractCompat$FontInfo;Landroid/os/CancellationSignal;)Ljava/util/Map;
    //   180: astore 5
    //   182: invokestatic 205	android/support/v4/graphics/TypefaceCompatApi26Impl:newFamily	()Ljava/lang/Object;
    //   185: astore 6
    //   187: iconst_0
    //   188: istore 7
    //   190: aload_3
    //   191: arraylength
    //   192: istore 8
    //   194: iconst_0
    //   195: istore 9
    //   197: iload 9
    //   199: iload 8
    //   201: if_icmpge +98 -> 299
    //   204: aload_3
    //   205: iload 9
    //   207: aaload
    //   208: astore 11
    //   210: aload 5
    //   212: aload 11
    //   214: invokevirtual 253	android/support/v4/provider/FontsContractCompat$FontInfo:getUri	()Landroid/net/Uri;
    //   217: invokeinterface 304 2 0
    //   222: checkcast 81	java/nio/ByteBuffer
    //   225: astore 12
    //   227: aload 12
    //   229: ifnonnull +9 -> 238
    //   232: iinc 9 1
    //   235: goto -38 -> 197
    //   238: aload 11
    //   240: invokevirtual 307	android/support/v4/provider/FontsContractCompat$FontInfo:getTtcIndex	()I
    //   243: istore 13
    //   245: aload 11
    //   247: invokevirtual 273	android/support/v4/provider/FontsContractCompat$FontInfo:getWeight	()I
    //   250: istore 14
    //   252: aload 11
    //   254: invokevirtual 278	android/support/v4/provider/FontsContractCompat$FontInfo:isItalic	()Z
    //   257: ifeq +30 -> 287
    //   260: iconst_1
    //   261: istore 15
    //   263: aload 6
    //   265: aload 12
    //   267: iload 13
    //   269: iload 14
    //   271: iload 15
    //   273: invokestatic 309	android/support/v4/graphics/TypefaceCompatApi26Impl:addFontFromBuffer	(Ljava/lang/Object;Ljava/nio/ByteBuffer;III)Z
    //   276: ifne +17 -> 293
    //   279: aload 6
    //   281: invokestatic 227	android/support/v4/graphics/TypefaceCompatApi26Impl:abortCreation	(Ljava/lang/Object;)Z
    //   284: pop
    //   285: aconst_null
    //   286: areturn
    //   287: iconst_0
    //   288: istore 15
    //   290: goto -27 -> 263
    //   293: iconst_1
    //   294: istore 7
    //   296: goto -64 -> 232
    //   299: iload 7
    //   301: ifne +11 -> 312
    //   304: aload 6
    //   306: invokestatic 227	android/support/v4/graphics/TypefaceCompatApi26Impl:abortCreation	(Ljava/lang/Object;)Z
    //   309: pop
    //   310: aconst_null
    //   311: areturn
    //   312: aload 6
    //   314: invokestatic 229	android/support/v4/graphics/TypefaceCompatApi26Impl:freeze	(Ljava/lang/Object;)Z
    //   317: ifne +5 -> 322
    //   320: aconst_null
    //   321: areturn
    //   322: aload 6
    //   324: invokestatic 231	android/support/v4/graphics/TypefaceCompatApi26Impl:createFromFamiliesWithDefault	(Ljava/lang/Object;)Landroid/graphics/Typeface;
    //   327: areturn
    //   328: astore 21
    //   330: aconst_null
    //   331: astore 22
    //   333: goto -197 -> 136
    //   336: astore 19
    //   338: aconst_null
    //   339: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   94	99	102	java/lang/Throwable
    //   48	81	121	java/lang/Throwable
    //   123	126	126	finally
    //   146	151	154	java/lang/Throwable
    //   48	81	328	finally
    //   33	48	336	java/io/IOException
    //   94	99	336	java/io/IOException
    //   104	110	336	java/io/IOException
    //   113	118	336	java/io/IOException
    //   146	151	336	java/io/IOException
    //   151	154	336	java/io/IOException
    //   156	163	336	java/io/IOException
    //   166	171	336	java/io/IOException
  }

  @Nullable
  public Typeface createFromResourcesFontFile(Context paramContext, Resources paramResources, int paramInt1, String paramString, int paramInt2)
  {
    if (!isFontFamilyPrivateAPIAvailable())
      return super.createFromResourcesFontFile(paramContext, paramResources, paramInt1, paramString, paramInt2);
    Object localObject = newFamily();
    if (!addFontFromAssetManager(paramContext, localObject, paramString, 0, -1, -1))
    {
      abortCreation(localObject);
      return null;
    }
    if (!freeze(localObject))
      return null;
    return createFromFamiliesWithDefault(localObject);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.graphics.TypefaceCompatApi26Impl
 * JD-Core Version:    0.6.0
 */