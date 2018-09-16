package com.squareup.picasso;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.StatFs;
import android.provider.Settings.System;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ThreadFactory;

final class Utils
{
  static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15000;
  static final int DEFAULT_READ_TIMEOUT_MILLIS = 20000;
  static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20000;
  private static final int KEY_PADDING = 50;
  static final char KEY_SEPARATOR = '\n';
  static final StringBuilder MAIN_THREAD_KEY_BUILDER = new StringBuilder();
  private static final int MAX_DISK_CACHE_SIZE = 52428800;
  private static final int MIN_DISK_CACHE_SIZE = 5242880;
  static final String OWNER_DISPATCHER = "Dispatcher";
  static final String OWNER_HUNTER = "Hunter";
  static final String OWNER_MAIN = "Main";
  private static final String PICASSO_CACHE = "picasso-cache";
  static final String THREAD_IDLE_NAME = "Picasso-Idle";
  static final int THREAD_LEAK_CLEANING_MS = 1000;
  static final String THREAD_PREFIX = "Picasso-";
  static final String VERB_BATCHED = "batched";
  static final String VERB_CANCELED = "canceled";
  static final String VERB_CHANGED = "changed";
  static final String VERB_COMPLETED = "completed";
  static final String VERB_CREATED = "created";
  static final String VERB_DECODED = "decoded";
  static final String VERB_DELIVERED = "delivered";
  static final String VERB_ENQUEUED = "enqueued";
  static final String VERB_ERRORED = "errored";
  static final String VERB_EXECUTING = "executing";
  static final String VERB_IGNORED = "ignored";
  static final String VERB_JOINED = "joined";
  static final String VERB_PAUSED = "paused";
  static final String VERB_REMOVED = "removed";
  static final String VERB_REPLAYING = "replaying";
  static final String VERB_RESUMED = "resumed";
  static final String VERB_RETRYING = "retrying";
  static final String VERB_TRANSFORMED = "transformed";
  private static final String WEBP_FILE_HEADER_RIFF = "RIFF";
  private static final int WEBP_FILE_HEADER_SIZE = 12;
  private static final String WEBP_FILE_HEADER_WEBP = "WEBP";

  static long calculateDiskCacheSize(File paramFile)
  {
    long l = 5242880L;
    try
    {
      StatFs localStatFs = new StatFs(paramFile.getAbsolutePath());
      l = localStatFs.getBlockCount() * localStatFs.getBlockSize() / 50L;
      label32: return Math.max(Math.min(l, 52428800L), 5242880L);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      break label32;
    }
  }

  static int calculateMemoryCacheSize(Context paramContext)
  {
    ActivityManager localActivityManager = (ActivityManager)getService(paramContext, "activity");
    if ((0x100000 & paramContext.getApplicationInfo().flags) != 0);
    for (int i = 1; ; i = 0)
    {
      int j = localActivityManager.getMemoryClass();
      if ((i != 0) && (Build.VERSION.SDK_INT >= 11))
        j = ActivityManagerHoneycomb.getLargeMemoryClass(localActivityManager);
      return 1048576 * j / 7;
    }
  }

  static void checkMain()
  {
    if (!isMain())
      throw new IllegalStateException("Method call should happen from the main thread.");
  }

  static void checkNotMain()
  {
    if (isMain())
      throw new IllegalStateException("Method call should not happen from the main thread.");
  }

  static <T> T checkNotNull(T paramT, String paramString)
  {
    if (paramT == null)
      throw new NullPointerException(paramString);
    return paramT;
  }

  static void closeQuietly(InputStream paramInputStream)
  {
    if (paramInputStream == null)
      return;
    try
    {
      paramInputStream.close();
      return;
    }
    catch (IOException localIOException)
    {
    }
  }

  static File createDefaultCacheDir(Context paramContext)
  {
    File localFile = new File(paramContext.getApplicationContext().getCacheDir(), "picasso-cache");
    if (!localFile.exists())
      localFile.mkdirs();
    return localFile;
  }

  static Downloader createDefaultDownloader(Context paramContext)
  {
    try
    {
      Class.forName("com.squareup.okhttp.OkHttpClient");
      Downloader localDownloader = OkHttpLoaderCreator.create(paramContext);
      return localDownloader;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
    }
    return new UrlConnectionDownloader(paramContext);
  }

  static String createKey(Request paramRequest)
  {
    String str = createKey(paramRequest, MAIN_THREAD_KEY_BUILDER);
    MAIN_THREAD_KEY_BUILDER.setLength(0);
    return str;
  }

  static String createKey(Request paramRequest, StringBuilder paramStringBuilder)
  {
    if (paramRequest.stableKey != null)
    {
      paramStringBuilder.ensureCapacity(50 + paramRequest.stableKey.length());
      paramStringBuilder.append(paramRequest.stableKey);
      paramStringBuilder.append('\n');
      if (paramRequest.rotationDegrees != 0.0F)
      {
        paramStringBuilder.append("rotation:").append(paramRequest.rotationDegrees);
        if (paramRequest.hasRotationPivot)
          paramStringBuilder.append('@').append(paramRequest.rotationPivotX).append('x').append(paramRequest.rotationPivotY);
        paramStringBuilder.append('\n');
      }
      if (paramRequest.hasSize())
      {
        paramStringBuilder.append("resize:").append(paramRequest.targetWidth).append('x').append(paramRequest.targetHeight);
        paramStringBuilder.append('\n');
      }
      if (!paramRequest.centerCrop)
        break label283;
      paramStringBuilder.append("centerCrop").append('\n');
    }
    while (true)
    {
      if (paramRequest.transformations == null)
        break label306;
      int i = 0;
      int j = paramRequest.transformations.size();
      while (i < j)
      {
        paramStringBuilder.append(((Transformation)paramRequest.transformations.get(i)).key());
        paramStringBuilder.append('\n');
        i++;
      }
      if (paramRequest.uri != null)
      {
        String str = paramRequest.uri.toString();
        paramStringBuilder.ensureCapacity(50 + str.length());
        paramStringBuilder.append(str);
        break;
      }
      paramStringBuilder.ensureCapacity(50);
      paramStringBuilder.append(paramRequest.resourceId);
      break;
      label283: if (!paramRequest.centerInside)
        continue;
      paramStringBuilder.append("centerInside").append('\n');
    }
    label306: return paramStringBuilder.toString();
  }

  static void flushStackLocalLeaks(Looper paramLooper)
  {
    1 local1 = new Handler(paramLooper)
    {
      public void handleMessage(Message paramMessage)
      {
        sendMessageDelayed(obtainMessage(), 1000L);
      }
    };
    local1.sendMessageDelayed(local1.obtainMessage(), 1000L);
  }

  static int getBitmapBytes(Bitmap paramBitmap)
  {
    if (Build.VERSION.SDK_INT >= 12);
    for (int i = BitmapHoneycombMR1.getByteCount(paramBitmap); i < 0; i = paramBitmap.getRowBytes() * paramBitmap.getHeight())
      throw new IllegalStateException("Negative size: " + paramBitmap);
    return i;
  }

  static String getLogIdsForHunter(BitmapHunter paramBitmapHunter)
  {
    return getLogIdsForHunter(paramBitmapHunter, "");
  }

  static String getLogIdsForHunter(BitmapHunter paramBitmapHunter, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramString);
    Action localAction = paramBitmapHunter.getAction();
    if (localAction != null)
      localStringBuilder.append(localAction.request.logId());
    List localList = paramBitmapHunter.getActions();
    if (localList != null)
    {
      int i = 0;
      int j = localList.size();
      while (i < j)
      {
        if ((i > 0) || (localAction != null))
          localStringBuilder.append(", ");
        localStringBuilder.append(((Action)localList.get(i)).request.logId());
        i++;
      }
    }
    return localStringBuilder.toString();
  }

  static int getResourceId(Resources paramResources, Request paramRequest)
    throws FileNotFoundException
  {
    if ((paramRequest.resourceId != 0) || (paramRequest.uri == null))
      return paramRequest.resourceId;
    String str1 = paramRequest.uri.getAuthority();
    if (str1 == null)
      throw new FileNotFoundException("No package provided: " + paramRequest.uri);
    List localList = paramRequest.uri.getPathSegments();
    if ((localList == null) || (localList.isEmpty()))
      throw new FileNotFoundException("No path segments: " + paramRequest.uri);
    if (localList.size() == 1)
      try
      {
        int i = Integer.parseInt((String)localList.get(0));
        return i;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new FileNotFoundException("Last path segment is not a resource ID: " + paramRequest.uri);
      }
    if (localList.size() == 2)
    {
      String str2 = (String)localList.get(0);
      return paramResources.getIdentifier((String)localList.get(1), str2, str1);
    }
    throw new FileNotFoundException("More than two path segments: " + paramRequest.uri);
  }

  static Resources getResources(Context paramContext, Request paramRequest)
    throws FileNotFoundException
  {
    if ((paramRequest.resourceId != 0) || (paramRequest.uri == null))
      return paramContext.getResources();
    String str = paramRequest.uri.getAuthority();
    if (str == null)
      throw new FileNotFoundException("No package provided: " + paramRequest.uri);
    try
    {
      Resources localResources = paramContext.getPackageManager().getResourcesForApplication(str);
      return localResources;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    throw new FileNotFoundException("Unable to obtain resources for package: " + paramRequest.uri);
  }

  static <T> T getService(Context paramContext, String paramString)
  {
    return paramContext.getSystemService(paramString);
  }

  static boolean hasPermission(Context paramContext, String paramString)
  {
    return paramContext.checkCallingOrSelfPermission(paramString) == 0;
  }

  static boolean isAirplaneModeOn(Context paramContext)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    try
    {
      int i = Settings.System.getInt(localContentResolver, "airplane_mode_on", 0);
      int j = 0;
      if (i != 0)
        j = 1;
      return j;
    }
    catch (NullPointerException localNullPointerException)
    {
    }
    return false;
  }

  static boolean isMain()
  {
    return Looper.getMainLooper().getThread() == Thread.currentThread();
  }

  static boolean isWebPFile(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[12];
    int i = paramInputStream.read(arrayOfByte, 0, 12);
    int j = 0;
    if (i == 12)
    {
      if (("RIFF".equals(new String(arrayOfByte, 0, 4, "US-ASCII"))) && ("WEBP".equals(new String(arrayOfByte, 8, 4, "US-ASCII"))))
        j = 1;
    }
    else
      return j;
    return false;
  }

  static void log(String paramString1, String paramString2, String paramString3)
  {
    log(paramString1, paramString2, paramString3, "");
  }

  static void log(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    Log.d("Picasso", String.format("%1$-11s %2$-12s %3$s %4$s", new Object[] { paramString1, paramString2, paramString3, paramString4 }));
  }

  static boolean parseResponseSourceHeader(String paramString)
  {
    int i = 1;
    if (paramString == null);
    String[] arrayOfString;
    do
    {
      return false;
      arrayOfString = paramString.split(" ", 2);
      if ("CACHE".equals(arrayOfString[0]))
        return i;
    }
    while (arrayOfString.length == i);
    try
    {
      if ("CONDITIONAL_CACHE".equals(arrayOfString[0]))
      {
        int j = Integer.parseInt(arrayOfString[1]);
        if (j != 304);
      }
      while (true)
      {
        return i;
        i = 0;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return false;
  }

  static byte[] toByteArray(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte[4096];
    while (true)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (-1 == i)
        break;
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    return localByteArrayOutputStream.toByteArray();
  }

  @TargetApi(11)
  private static class ActivityManagerHoneycomb
  {
    static int getLargeMemoryClass(ActivityManager paramActivityManager)
    {
      return paramActivityManager.getLargeMemoryClass();
    }
  }

  @TargetApi(12)
  private static class BitmapHoneycombMR1
  {
    static int getByteCount(Bitmap paramBitmap)
    {
      return paramBitmap.getByteCount();
    }
  }

  private static class OkHttpLoaderCreator
  {
    static Downloader create(Context paramContext)
    {
      return new OkHttpDownloader(paramContext);
    }
  }

  private static class PicassoThread extends Thread
  {
    public PicassoThread(Runnable paramRunnable)
    {
      super();
    }

    public void run()
    {
      Process.setThreadPriority(10);
      super.run();
    }
  }

  static class PicassoThreadFactory
    implements ThreadFactory
  {
    public Thread newThread(Runnable paramRunnable)
    {
      return new Utils.PicassoThread(paramRunnable);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.Utils
 * JD-Core Version:    0.6.0
 */