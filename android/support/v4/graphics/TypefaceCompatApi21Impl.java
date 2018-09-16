package android.support.v4.graphics;

import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import java.io.File;

@RequiresApi(21)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
class TypefaceCompatApi21Impl extends TypefaceCompatBaseImpl
{
  private static final String TAG = "TypefaceCompatApi21Impl";

  private File getFile(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    try
    {
      String str = Os.readlink("/proc/self/fd/" + paramParcelFileDescriptor.getFd());
      if (OsConstants.S_ISREG(Os.stat(str).st_mode))
      {
        File localFile = new File(str);
        return localFile;
      }
      return null;
    }
    catch (ErrnoException localErrnoException)
    {
    }
    return null;
  }

  // ERROR //
  public android.graphics.Typeface createFromFontInfo(android.content.Context paramContext, android.os.CancellationSignal paramCancellationSignal, @android.support.annotation.NonNull android.support.v4.provider.FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    // Byte code:
    //   0: aload_3
    //   1: arraylength
    //   2: iconst_1
    //   3: if_icmpge +9 -> 12
    //   6: aconst_null
    //   7: astore 20
    //   9: aload 20
    //   11: areturn
    //   12: aload_0
    //   13: aload_3
    //   14: iload 4
    //   16: invokevirtual 82	android/support/v4/graphics/TypefaceCompatApi21Impl:findBestInfo	([Landroid/support/v4/provider/FontsContractCompat$FontInfo;I)Landroid/support/v4/provider/FontsContractCompat$FontInfo;
    //   19: astore 5
    //   21: aload_1
    //   22: invokevirtual 88	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   25: astore 6
    //   27: aload 6
    //   29: aload 5
    //   31: invokevirtual 94	android/support/v4/provider/FontsContractCompat$FontInfo:getUri	()Landroid/net/Uri;
    //   34: ldc 96
    //   36: aload_2
    //   37: invokevirtual 102	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
    //   40: astore 8
    //   42: aload_0
    //   43: aload 8
    //   45: invokespecial 104	android/support/v4/graphics/TypefaceCompatApi21Impl:getFile	(Landroid/os/ParcelFileDescriptor;)Ljava/io/File;
    //   48: astore 14
    //   50: aload 14
    //   52: ifnull +11 -> 63
    //   55: aload 14
    //   57: invokevirtual 108	java/io/File:canRead	()Z
    //   60: ifne +192 -> 252
    //   63: new 110	java/io/FileInputStream
    //   66: dup
    //   67: aload 8
    //   69: invokevirtual 114	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   72: invokespecial 117	java/io/FileInputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   75: astore 15
    //   77: aconst_null
    //   78: astore 16
    //   80: aload_0
    //   81: aload_1
    //   82: aload 15
    //   84: invokespecial 121	android/support/v4/graphics/TypefaceCompatBaseImpl:createFromInputStream	(Landroid/content/Context;Ljava/io/InputStream;)Landroid/graphics/Typeface;
    //   87: astore 19
    //   89: aload 19
    //   91: astore 20
    //   93: aload 15
    //   95: ifnull +12 -> 107
    //   98: iconst_0
    //   99: ifeq +84 -> 183
    //   102: aload 15
    //   104: invokevirtual 124	java/io/FileInputStream:close	()V
    //   107: aload 8
    //   109: ifnull -100 -> 9
    //   112: iconst_0
    //   113: ifeq +86 -> 199
    //   116: aload 8
    //   118: invokevirtual 125	android/os/ParcelFileDescriptor:close	()V
    //   121: aload 20
    //   123: areturn
    //   124: astore 21
    //   126: aconst_null
    //   127: aload 21
    //   129: invokevirtual 129	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   132: aload 20
    //   134: areturn
    //   135: astore 7
    //   137: aconst_null
    //   138: areturn
    //   139: astore 22
    //   141: aconst_null
    //   142: aload 22
    //   144: invokevirtual 129	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   147: goto -40 -> 107
    //   150: astore 12
    //   152: aload 12
    //   154: athrow
    //   155: astore 13
    //   157: aload 12
    //   159: astore 10
    //   161: aload 13
    //   163: astore 9
    //   165: aload 8
    //   167: ifnull +13 -> 180
    //   170: aload 10
    //   172: ifnull +139 -> 311
    //   175: aload 8
    //   177: invokevirtual 125	android/os/ParcelFileDescriptor:close	()V
    //   180: aload 9
    //   182: athrow
    //   183: aload 15
    //   185: invokevirtual 124	java/io/FileInputStream:close	()V
    //   188: goto -81 -> 107
    //   191: astore 9
    //   193: aconst_null
    //   194: astore 10
    //   196: goto -31 -> 165
    //   199: aload 8
    //   201: invokevirtual 125	android/os/ParcelFileDescriptor:close	()V
    //   204: aload 20
    //   206: areturn
    //   207: astore 16
    //   209: aload 16
    //   211: athrow
    //   212: astore 17
    //   214: aload 15
    //   216: ifnull +13 -> 229
    //   219: aload 16
    //   221: ifnull +23 -> 244
    //   224: aload 15
    //   226: invokevirtual 124	java/io/FileInputStream:close	()V
    //   229: aload 17
    //   231: athrow
    //   232: astore 18
    //   234: aload 16
    //   236: aload 18
    //   238: invokevirtual 129	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   241: goto -12 -> 229
    //   244: aload 15
    //   246: invokevirtual 124	java/io/FileInputStream:close	()V
    //   249: goto -20 -> 229
    //   252: aload 14
    //   254: invokestatic 135	android/graphics/Typeface:createFromFile	(Ljava/io/File;)Landroid/graphics/Typeface;
    //   257: astore 23
    //   259: aload 23
    //   261: astore 20
    //   263: aload 8
    //   265: ifnull -256 -> 9
    //   268: iconst_0
    //   269: ifeq +22 -> 291
    //   272: aload 8
    //   274: invokevirtual 125	android/os/ParcelFileDescriptor:close	()V
    //   277: aload 20
    //   279: areturn
    //   280: astore 24
    //   282: aconst_null
    //   283: aload 24
    //   285: invokevirtual 129	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   288: aload 20
    //   290: areturn
    //   291: aload 8
    //   293: invokevirtual 125	android/os/ParcelFileDescriptor:close	()V
    //   296: aload 20
    //   298: areturn
    //   299: astore 11
    //   301: aload 10
    //   303: aload 11
    //   305: invokevirtual 129	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   308: goto -128 -> 180
    //   311: aload 8
    //   313: invokevirtual 125	android/os/ParcelFileDescriptor:close	()V
    //   316: goto -136 -> 180
    //
    // Exception table:
    //   from	to	target	type
    //   116	121	124	java/lang/Throwable
    //   27	42	135	java/io/IOException
    //   116	121	135	java/io/IOException
    //   126	132	135	java/io/IOException
    //   175	180	135	java/io/IOException
    //   180	183	135	java/io/IOException
    //   199	204	135	java/io/IOException
    //   272	277	135	java/io/IOException
    //   282	288	135	java/io/IOException
    //   291	296	135	java/io/IOException
    //   301	308	135	java/io/IOException
    //   311	316	135	java/io/IOException
    //   102	107	139	java/lang/Throwable
    //   42	50	150	java/lang/Throwable
    //   55	63	150	java/lang/Throwable
    //   63	77	150	java/lang/Throwable
    //   141	147	150	java/lang/Throwable
    //   183	188	150	java/lang/Throwable
    //   229	232	150	java/lang/Throwable
    //   234	241	150	java/lang/Throwable
    //   244	249	150	java/lang/Throwable
    //   252	259	150	java/lang/Throwable
    //   152	155	155	finally
    //   42	50	191	finally
    //   55	63	191	finally
    //   63	77	191	finally
    //   102	107	191	finally
    //   141	147	191	finally
    //   183	188	191	finally
    //   224	229	191	finally
    //   229	232	191	finally
    //   234	241	191	finally
    //   244	249	191	finally
    //   252	259	191	finally
    //   80	89	207	java/lang/Throwable
    //   80	89	212	finally
    //   209	212	212	finally
    //   224	229	232	java/lang/Throwable
    //   272	277	280	java/lang/Throwable
    //   175	180	299	java/lang/Throwable
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.graphics.TypefaceCompatApi21Impl
 * JD-Core Version:    0.6.0
 */