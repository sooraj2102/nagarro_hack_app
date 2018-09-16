package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class TypefaceCompatUtil
{
  private static final String CACHE_FILE_PREFIX = ".font";
  private static final String TAG = "TypefaceCompatUtil";

  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null);
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException localIOException)
    {
    }
  }

  @RequiresApi(19)
  public static ByteBuffer copyToDirectBuffer(Context paramContext, Resources paramResources, int paramInt)
  {
    File localFile = getTempFile(paramContext);
    if (localFile == null)
      return null;
    try
    {
      boolean bool = copyToFile(localFile, paramResources, paramInt);
      if (!bool)
        return null;
      ByteBuffer localByteBuffer = mmap(localFile);
      return localByteBuffer;
    }
    finally
    {
      localFile.delete();
    }
    throw localObject;
  }

  public static boolean copyToFile(File paramFile, Resources paramResources, int paramInt)
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = paramResources.openRawResource(paramInt);
      boolean bool = copyToFile(paramFile, localInputStream);
      return bool;
    }
    finally
    {
      closeQuietly(localInputStream);
    }
    throw localObject;
  }

  // ERROR //
  public static boolean copyToFile(File paramFile, InputStream paramInputStream)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: new 63	java/io/FileOutputStream
    //   5: dup
    //   6: aload_0
    //   7: iconst_0
    //   8: invokespecial 66	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   11: astore_3
    //   12: sipush 1024
    //   15: newarray byte
    //   17: astore 7
    //   19: aload_1
    //   20: aload 7
    //   22: invokevirtual 72	java/io/InputStream:read	([B)I
    //   25: istore 8
    //   27: iload 8
    //   29: iconst_m1
    //   30: if_icmpeq +54 -> 84
    //   33: aload_3
    //   34: aload 7
    //   36: iconst_0
    //   37: iload 8
    //   39: invokevirtual 76	java/io/FileOutputStream:write	([BII)V
    //   42: goto -23 -> 19
    //   45: astore 5
    //   47: aload_3
    //   48: astore_2
    //   49: ldc 15
    //   51: new 78	java/lang/StringBuilder
    //   54: dup
    //   55: invokespecial 79	java/lang/StringBuilder:<init>	()V
    //   58: ldc 81
    //   60: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: aload 5
    //   65: invokevirtual 89	java/io/IOException:getMessage	()Ljava/lang/String;
    //   68: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: invokevirtual 92	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   74: invokestatic 98	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   77: pop
    //   78: aload_2
    //   79: invokestatic 61	android/support/v4/graphics/TypefaceCompatUtil:closeQuietly	(Ljava/io/Closeable;)V
    //   82: iconst_0
    //   83: ireturn
    //   84: aload_3
    //   85: invokestatic 61	android/support/v4/graphics/TypefaceCompatUtil:closeQuietly	(Ljava/io/Closeable;)V
    //   88: iconst_1
    //   89: ireturn
    //   90: astore 4
    //   92: aload_2
    //   93: invokestatic 61	android/support/v4/graphics/TypefaceCompatUtil:closeQuietly	(Ljava/io/Closeable;)V
    //   96: aload 4
    //   98: athrow
    //   99: astore 4
    //   101: aload_3
    //   102: astore_2
    //   103: goto -11 -> 92
    //   106: astore 5
    //   108: aconst_null
    //   109: astore_2
    //   110: goto -61 -> 49
    //
    // Exception table:
    //   from	to	target	type
    //   12	19	45	java/io/IOException
    //   19	27	45	java/io/IOException
    //   33	42	45	java/io/IOException
    //   2	12	90	finally
    //   49	78	90	finally
    //   12	19	99	finally
    //   19	27	99	finally
    //   33	42	99	finally
    //   2	12	106	java/io/IOException
  }

  public static File getTempFile(Context paramContext)
  {
    String str = ".font" + Process.myPid() + "-" + Process.myTid() + "-";
    int i = 0;
    while (i < 100)
    {
      File localFile = new File(paramContext.getCacheDir(), str + i);
      try
      {
        boolean bool = localFile.createNewFile();
        if (bool)
          return localFile;
      }
      catch (IOException localIOException)
      {
        i++;
      }
    }
    return null;
  }

  // ERROR //
  @RequiresApi(19)
  public static ByteBuffer mmap(Context paramContext, android.os.CancellationSignal paramCancellationSignal, android.net.Uri paramUri)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 131	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: astore_3
    //   5: aload_3
    //   6: aload_2
    //   7: ldc 133
    //   9: aload_1
    //   10: invokevirtual 139	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
    //   13: astore 5
    //   15: new 141	java/io/FileInputStream
    //   18: dup
    //   19: aload 5
    //   21: invokevirtual 147	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   24: invokespecial 150	java/io/FileInputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   27: astore 6
    //   29: aload 6
    //   31: invokevirtual 154	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   34: astore 17
    //   36: aload 17
    //   38: invokevirtual 160	java/nio/channels/FileChannel:size	()J
    //   41: lstore 18
    //   43: aload 17
    //   45: getstatic 166	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   48: lconst_0
    //   49: lload 18
    //   51: invokevirtual 170	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   54: astore 20
    //   56: aload 6
    //   58: ifnull +12 -> 70
    //   61: iconst_0
    //   62: ifeq +73 -> 135
    //   65: aload 6
    //   67: invokevirtual 171	java/io/FileInputStream:close	()V
    //   70: aload 5
    //   72: ifnull +12 -> 84
    //   75: iconst_0
    //   76: ifeq +86 -> 162
    //   79: aload 5
    //   81: invokevirtual 172	android/os/ParcelFileDescriptor:close	()V
    //   84: aload 20
    //   86: areturn
    //   87: astore 22
    //   89: aconst_null
    //   90: aload 22
    //   92: invokevirtual 176	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   95: goto -25 -> 70
    //   98: astore 12
    //   100: aload 12
    //   102: athrow
    //   103: astore 13
    //   105: aload 12
    //   107: astore 10
    //   109: aload 13
    //   111: astore 9
    //   113: aload 5
    //   115: ifnull +13 -> 128
    //   118: aload 10
    //   120: ifnull +115 -> 235
    //   123: aload 5
    //   125: invokevirtual 172	android/os/ParcelFileDescriptor:close	()V
    //   128: aload 9
    //   130: athrow
    //   131: astore 4
    //   133: aconst_null
    //   134: areturn
    //   135: aload 6
    //   137: invokevirtual 171	java/io/FileInputStream:close	()V
    //   140: goto -70 -> 70
    //   143: astore 9
    //   145: aconst_null
    //   146: astore 10
    //   148: goto -35 -> 113
    //   151: astore 21
    //   153: aconst_null
    //   154: aload 21
    //   156: invokevirtual 176	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   159: aload 20
    //   161: areturn
    //   162: aload 5
    //   164: invokevirtual 172	android/os/ParcelFileDescriptor:close	()V
    //   167: aload 20
    //   169: areturn
    //   170: astore 15
    //   172: aload 15
    //   174: athrow
    //   175: astore 16
    //   177: aload 15
    //   179: astore 8
    //   181: aload 16
    //   183: astore 7
    //   185: aload 6
    //   187: ifnull +13 -> 200
    //   190: aload 8
    //   192: ifnull +23 -> 215
    //   195: aload 6
    //   197: invokevirtual 171	java/io/FileInputStream:close	()V
    //   200: aload 7
    //   202: athrow
    //   203: astore 14
    //   205: aload 8
    //   207: aload 14
    //   209: invokevirtual 176	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   212: goto -12 -> 200
    //   215: aload 6
    //   217: invokevirtual 171	java/io/FileInputStream:close	()V
    //   220: goto -20 -> 200
    //   223: astore 11
    //   225: aload 10
    //   227: aload 11
    //   229: invokevirtual 176	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   232: goto -104 -> 128
    //   235: aload 5
    //   237: invokevirtual 172	android/os/ParcelFileDescriptor:close	()V
    //   240: goto -112 -> 128
    //   243: astore 7
    //   245: aconst_null
    //   246: astore 8
    //   248: goto -63 -> 185
    //
    // Exception table:
    //   from	to	target	type
    //   65	70	87	java/lang/Throwable
    //   15	29	98	java/lang/Throwable
    //   89	95	98	java/lang/Throwable
    //   135	140	98	java/lang/Throwable
    //   200	203	98	java/lang/Throwable
    //   205	212	98	java/lang/Throwable
    //   215	220	98	java/lang/Throwable
    //   100	103	103	finally
    //   5	15	131	java/io/IOException
    //   79	84	131	java/io/IOException
    //   123	128	131	java/io/IOException
    //   128	131	131	java/io/IOException
    //   153	159	131	java/io/IOException
    //   162	167	131	java/io/IOException
    //   225	232	131	java/io/IOException
    //   235	240	131	java/io/IOException
    //   15	29	143	finally
    //   65	70	143	finally
    //   89	95	143	finally
    //   135	140	143	finally
    //   195	200	143	finally
    //   200	203	143	finally
    //   205	212	143	finally
    //   215	220	143	finally
    //   79	84	151	java/lang/Throwable
    //   29	56	170	java/lang/Throwable
    //   172	175	175	finally
    //   195	200	203	java/lang/Throwable
    //   123	128	223	java/lang/Throwable
    //   29	56	243	finally
  }

  // ERROR //
  @RequiresApi(19)
  private static ByteBuffer mmap(File paramFile)
  {
    // Byte code:
    //   0: new 141	java/io/FileInputStream
    //   3: dup
    //   4: aload_0
    //   5: invokespecial 179	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   8: astore_1
    //   9: aload_1
    //   10: invokevirtual 154	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   13: astore 8
    //   15: aload 8
    //   17: invokevirtual 160	java/nio/channels/FileChannel:size	()J
    //   20: lstore 9
    //   22: aload 8
    //   24: getstatic 166	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   27: lconst_0
    //   28: lload 9
    //   30: invokevirtual 170	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   33: astore 11
    //   35: aload_1
    //   36: ifnull +11 -> 47
    //   39: iconst_0
    //   40: ifeq +21 -> 61
    //   43: aload_1
    //   44: invokevirtual 171	java/io/FileInputStream:close	()V
    //   47: aload 11
    //   49: areturn
    //   50: astore 12
    //   52: aconst_null
    //   53: aload 12
    //   55: invokevirtual 176	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   58: aload 11
    //   60: areturn
    //   61: aload_1
    //   62: invokevirtual 171	java/io/FileInputStream:close	()V
    //   65: aload 11
    //   67: areturn
    //   68: astore 6
    //   70: aload 6
    //   72: athrow
    //   73: astore 7
    //   75: aload 6
    //   77: astore_3
    //   78: aload 7
    //   80: astore_2
    //   81: aload_1
    //   82: ifnull +11 -> 93
    //   85: aload_3
    //   86: ifnull +20 -> 106
    //   89: aload_1
    //   90: invokevirtual 171	java/io/FileInputStream:close	()V
    //   93: aload_2
    //   94: athrow
    //   95: astore 5
    //   97: aload_3
    //   98: aload 5
    //   100: invokevirtual 176	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   103: goto -10 -> 93
    //   106: aload_1
    //   107: invokevirtual 171	java/io/FileInputStream:close	()V
    //   110: goto -17 -> 93
    //   113: astore_2
    //   114: aconst_null
    //   115: astore_3
    //   116: goto -35 -> 81
    //   119: astore 4
    //   121: aconst_null
    //   122: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   43	47	50	java/lang/Throwable
    //   9	35	68	java/lang/Throwable
    //   70	73	73	finally
    //   89	93	95	java/lang/Throwable
    //   9	35	113	finally
    //   0	9	119	java/io/IOException
    //   43	47	119	java/io/IOException
    //   52	58	119	java/io/IOException
    //   61	65	119	java/io/IOException
    //   89	93	119	java/io/IOException
    //   93	95	119	java/io/IOException
    //   97	103	119	java/io/IOException
    //   106	110	119	java/io/IOException
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.graphics.TypefaceCompatUtil
 * JD-Core Version:    0.6.0
 */