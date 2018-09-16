package android.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomTabsClient
{
  private final ICustomTabsService mService;
  private final ComponentName mServiceComponentName;

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  CustomTabsClient(ICustomTabsService paramICustomTabsService, ComponentName paramComponentName)
  {
    this.mService = paramICustomTabsService;
    this.mServiceComponentName = paramComponentName;
  }

  public static boolean bindCustomTabsService(Context paramContext, String paramString, CustomTabsServiceConnection paramCustomTabsServiceConnection)
  {
    Intent localIntent = new Intent("android.support.customtabs.action.CustomTabsService");
    if (!TextUtils.isEmpty(paramString))
      localIntent.setPackage(paramString);
    return paramContext.bindService(localIntent, paramCustomTabsServiceConnection, 33);
  }

  public static boolean connectAndInitialize(Context paramContext, String paramString)
  {
    if (paramString == null)
      return false;
    Context localContext = paramContext.getApplicationContext();
    1 local1 = new CustomTabsServiceConnection(localContext)
    {
      public final void onCustomTabsServiceConnected(ComponentName paramComponentName, CustomTabsClient paramCustomTabsClient)
      {
        paramCustomTabsClient.warmup(0L);
        this.val$applicationContext.unbindService(this);
      }

      public final void onServiceDisconnected(ComponentName paramComponentName)
      {
      }
    };
    try
    {
      boolean bool = bindCustomTabsService(localContext, paramString, local1);
      return bool;
    }
    catch (SecurityException localSecurityException)
    {
    }
    return false;
  }

  public static String getPackageName(Context paramContext, @Nullable List<String> paramList)
  {
    return getPackageName(paramContext, paramList, false);
  }

  public static String getPackageName(Context paramContext, @Nullable List<String> paramList, boolean paramBoolean)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    if (paramList == null);
    for (Object localObject = new ArrayList(); ; localObject = paramList)
    {
      Intent localIntent1 = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
      if (!paramBoolean)
      {
        ResolveInfo localResolveInfo = localPackageManager.resolveActivity(localIntent1, 0);
        if (localResolveInfo != null)
        {
          String str2 = localResolveInfo.activityInfo.packageName;
          ArrayList localArrayList = new ArrayList(1 + ((List)localObject).size());
          localArrayList.add(str2);
          if (paramList != null)
            localArrayList.addAll(paramList);
          localObject = localArrayList;
        }
      }
      Intent localIntent2 = new Intent("android.support.customtabs.action.CustomTabsService");
      Iterator localIterator = ((List)localObject).iterator();
      String str1;
      do
      {
        if (!localIterator.hasNext())
          break;
        str1 = (String)localIterator.next();
        localIntent2.setPackage(str1);
      }
      while (localPackageManager.resolveService(localIntent2, 0) == null);
      return str1;
    }
    return (String)null;
  }

  public Bundle extraCommand(String paramString, Bundle paramBundle)
  {
    try
    {
      Bundle localBundle = this.mService.extraCommand(paramString, paramBundle);
      return localBundle;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return null;
  }

  public CustomTabsSession newSession(CustomTabsCallback paramCustomTabsCallback)
  {
    2 local2 = new ICustomTabsCallback.Stub(paramCustomTabsCallback)
    {
      private Handler mHandler = new Handler(Looper.getMainLooper());

      public void extraCallback(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        if (this.val$callback == null)
          return;
        this.mHandler.post(new Runnable(paramString, paramBundle)
        {
          public void run()
          {
            CustomTabsClient.2.this.val$callback.extraCallback(this.val$callbackName, this.val$args);
          }
        });
      }

      public void onMessageChannelReady(Bundle paramBundle)
        throws RemoteException
      {
        if (this.val$callback == null)
          return;
        this.mHandler.post(new Runnable(paramBundle)
        {
          public void run()
          {
            CustomTabsClient.2.this.val$callback.onMessageChannelReady(this.val$extras);
          }
        });
      }

      public void onNavigationEvent(int paramInt, Bundle paramBundle)
      {
        if (this.val$callback == null)
          return;
        this.mHandler.post(new Runnable(paramInt, paramBundle)
        {
          public void run()
          {
            CustomTabsClient.2.this.val$callback.onNavigationEvent(this.val$navigationEvent, this.val$extras);
          }
        });
      }

      public void onPostMessage(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        if (this.val$callback == null)
          return;
        this.mHandler.post(new Runnable(paramString, paramBundle)
        {
          public void run()
          {
            CustomTabsClient.2.this.val$callback.onPostMessage(this.val$message, this.val$extras);
          }
        });
      }
    };
    try
    {
      boolean bool = this.mService.newSession(local2);
      if (!bool)
        return null;
    }
    catch (RemoteException localRemoteException)
    {
      return null;
    }
    return new CustomTabsSession(this.mService, local2, this.mServiceComponentName);
  }

  public boolean warmup(long paramLong)
  {
    try
    {
      boolean bool = this.mService.warmup(paramLong);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return false;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.customtabs.CustomTabsClient
 * JD-Core Version:    0.6.0
 */