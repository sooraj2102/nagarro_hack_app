package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import java.io.File;

public class ContextCompat
{
  private static final String TAG = "ContextCompat";
  private static final Object sLock = new Object();
  private static TypedValue sTempValue;

  private static File buildPath(File paramFile, String[] paramArrayOfString)
  {
    int i = paramArrayOfString.length;
    int j = 0;
    Object localObject1 = paramFile;
    String str;
    Object localObject2;
    if (j < i)
    {
      str = paramArrayOfString[j];
      if (localObject1 == null)
        localObject2 = new File(str);
    }
    while (true)
    {
      j++;
      localObject1 = localObject2;
      break;
      if (str != null)
      {
        localObject2 = new File((File)localObject1, str);
        continue;
        return localObject1;
      }
      localObject2 = localObject1;
    }
  }

  public static int checkSelfPermission(@NonNull Context paramContext, @NonNull String paramString)
  {
    if (paramString == null)
      throw new IllegalArgumentException("permission is null");
    return paramContext.checkPermission(paramString, Process.myPid(), Process.myUid());
  }

  public static Context createDeviceProtectedStorageContext(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 24)
      return paramContext.createDeviceProtectedStorageContext();
    return null;
  }

  private static File createFilesDir(File paramFile)
  {
    monitorenter;
    try
    {
      if ((!paramFile.exists()) && (!paramFile.mkdirs()))
      {
        boolean bool = paramFile.exists();
        if (!bool)
          break label31;
      }
      while (true)
      {
        return paramFile;
        label31: Log.w("ContextCompat", "Unable to create files subdir " + paramFile.getPath());
        paramFile = null;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public static File getCodeCacheDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 21)
      return paramContext.getCodeCacheDir();
    return createFilesDir(new File(paramContext.getApplicationInfo().dataDir, "code_cache"));
  }

  @ColorInt
  public static final int getColor(Context paramContext, @ColorRes int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23)
      return paramContext.getColor(paramInt);
    return paramContext.getResources().getColor(paramInt);
  }

  public static final ColorStateList getColorStateList(Context paramContext, @ColorRes int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23)
      return paramContext.getColorStateList(paramInt);
    return paramContext.getResources().getColorStateList(paramInt);
  }

  public static File getDataDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 24)
      return paramContext.getDataDir();
    String str = paramContext.getApplicationInfo().dataDir;
    if (str != null)
      return new File(str);
    return null;
  }

  public static final Drawable getDrawable(Context paramContext, @DrawableRes int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21)
      return paramContext.getDrawable(paramInt);
    if (Build.VERSION.SDK_INT >= 16)
      return paramContext.getResources().getDrawable(paramInt);
    synchronized (sLock)
    {
      if (sTempValue == null)
        sTempValue = new TypedValue();
      paramContext.getResources().getValue(paramInt, sTempValue, true);
      int i = sTempValue.resourceId;
      return paramContext.getResources().getDrawable(i);
    }
  }

  public static File[] getExternalCacheDirs(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 19)
      return paramContext.getExternalCacheDirs();
    File[] arrayOfFile = new File[1];
    arrayOfFile[0] = paramContext.getExternalCacheDir();
    return arrayOfFile;
  }

  public static File[] getExternalFilesDirs(Context paramContext, String paramString)
  {
    if (Build.VERSION.SDK_INT >= 19)
      return paramContext.getExternalFilesDirs(paramString);
    File[] arrayOfFile = new File[1];
    arrayOfFile[0] = paramContext.getExternalFilesDir(paramString);
    return arrayOfFile;
  }

  public static final File getNoBackupFilesDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 21)
      return paramContext.getNoBackupFilesDir();
    return createFilesDir(new File(paramContext.getApplicationInfo().dataDir, "no_backup"));
  }

  public static File[] getObbDirs(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 19)
      return paramContext.getObbDirs();
    File[] arrayOfFile = new File[1];
    arrayOfFile[0] = paramContext.getObbDir();
    return arrayOfFile;
  }

  public static boolean isDeviceProtectedStorage(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 24)
      return paramContext.isDeviceProtectedStorage();
    return false;
  }

  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent)
  {
    return startActivities(paramContext, paramArrayOfIntent, null);
  }

  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent, Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT >= 16)
      paramContext.startActivities(paramArrayOfIntent, paramBundle);
    while (true)
    {
      return true;
      paramContext.startActivities(paramArrayOfIntent);
    }
  }

  public static void startActivity(Context paramContext, Intent paramIntent, @Nullable Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramContext.startActivity(paramIntent, paramBundle);
      return;
    }
    paramContext.startActivity(paramIntent);
  }

  public static void startForegroundService(Context paramContext, Intent paramIntent)
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      paramContext.startForegroundService(paramIntent);
      return;
    }
    paramContext.startService(paramIntent);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.content.ContextCompat
 * JD-Core Version:    0.6.0
 */