package android.support.v4.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

public final class NavUtils
{
  public static final String PARENT_ACTIVITY = "android.support.PARENT_ACTIVITY";
  private static final String TAG = "NavUtils";

  public static Intent getParentActivityIntent(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      Intent localIntent2 = paramActivity.getParentActivityIntent();
      if (localIntent2 != null)
        return localIntent2;
    }
    String str = getParentActivityName(paramActivity);
    if (str == null)
      return null;
    ComponentName localComponentName = new ComponentName(paramActivity, str);
    Object localObject;
    try
    {
      if (getParentActivityName(paramActivity, localComponentName) == null)
      {
        localObject = Intent.makeMainActivity(localComponentName);
      }
      else
      {
        Intent localIntent1 = new Intent().setComponent(localComponentName);
        localObject = localIntent1;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e("NavUtils", "getParentActivityIntent: bad parentActivityName '" + str + "' in manifest");
      return null;
    }
    return (Intent)localObject;
  }

  public static Intent getParentActivityIntent(Context paramContext, ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    String str = getParentActivityName(paramContext, paramComponentName);
    if (str == null)
      return null;
    ComponentName localComponentName = new ComponentName(paramComponentName.getPackageName(), str);
    if (getParentActivityName(paramContext, localComponentName) == null)
      return Intent.makeMainActivity(localComponentName);
    return new Intent().setComponent(localComponentName);
  }

  public static Intent getParentActivityIntent(Context paramContext, Class<?> paramClass)
    throws PackageManager.NameNotFoundException
  {
    String str = getParentActivityName(paramContext, new ComponentName(paramContext, paramClass));
    if (str == null)
      return null;
    ComponentName localComponentName = new ComponentName(paramContext, str);
    if (getParentActivityName(paramContext, localComponentName) == null)
      return Intent.makeMainActivity(localComponentName);
    return new Intent().setComponent(localComponentName);
  }

  @Nullable
  public static String getParentActivityName(Activity paramActivity)
  {
    try
    {
      String str = getParentActivityName(paramActivity, paramActivity.getComponentName());
      return str;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    throw new IllegalArgumentException(localNameNotFoundException);
  }

  @Nullable
  public static String getParentActivityName(Context paramContext, ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    ActivityInfo localActivityInfo = paramContext.getPackageManager().getActivityInfo(paramComponentName, 128);
    if (Build.VERSION.SDK_INT >= 16)
    {
      String str2 = localActivityInfo.parentActivityName;
      if (str2 != null)
        return str2;
    }
    if (localActivityInfo.metaData == null)
      return null;
    String str1 = localActivityInfo.metaData.getString("android.support.PARENT_ACTIVITY");
    if (str1 == null)
      return null;
    if (str1.charAt(0) == '.')
      str1 = paramContext.getPackageName() + str1;
    return str1;
  }

  public static void navigateUpFromSameTask(Activity paramActivity)
  {
    Intent localIntent = getParentActivityIntent(paramActivity);
    if (localIntent == null)
      throw new IllegalArgumentException("Activity " + paramActivity.getClass().getSimpleName() + " does not have a parent activity name specified." + " (Did you forget to add the android.support.PARENT_ACTIVITY <meta-data> " + " element in your manifest?)");
    navigateUpTo(paramActivity, localIntent);
  }

  public static void navigateUpTo(Activity paramActivity, Intent paramIntent)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramActivity.navigateUpTo(paramIntent);
      return;
    }
    paramIntent.addFlags(67108864);
    paramActivity.startActivity(paramIntent);
    paramActivity.finish();
  }

  public static boolean shouldUpRecreateTask(Activity paramActivity, Intent paramIntent)
  {
    if (Build.VERSION.SDK_INT >= 16)
      return paramActivity.shouldUpRecreateTask(paramIntent);
    String str = paramActivity.getIntent().getAction();
    return (str != null) && (!str.equals("android.intent.action.MAIN"));
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.app.NavUtils
 * JD-Core Version:    0.6.0
 */