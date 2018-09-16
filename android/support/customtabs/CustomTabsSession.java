package android.support.customtabs;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import java.util.List;

public final class CustomTabsSession
{
  private static final String TAG = "CustomTabsSession";
  private final ICustomTabsCallback mCallback;
  private final ComponentName mComponentName;
  private final Object mLock = new Object();
  private final ICustomTabsService mService;

  CustomTabsSession(ICustomTabsService paramICustomTabsService, ICustomTabsCallback paramICustomTabsCallback, ComponentName paramComponentName)
  {
    this.mService = paramICustomTabsService;
    this.mCallback = paramICustomTabsCallback;
    this.mComponentName = paramComponentName;
  }

  IBinder getBinder()
  {
    return this.mCallback.asBinder();
  }

  ComponentName getComponentName()
  {
    return this.mComponentName;
  }

  public boolean mayLaunchUrl(Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
  {
    try
    {
      boolean bool = this.mService.mayLaunchUrl(this.mCallback, paramUri, paramBundle, paramList);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return false;
  }

  public int postMessage(String paramString, Bundle paramBundle)
  {
    synchronized (this.mLock)
    {
      try
      {
        int i = this.mService.postMessage(this.mCallback, paramString, paramBundle);
        return i;
      }
      catch (RemoteException localRemoteException)
      {
        return -2;
      }
    }
  }

  public boolean requestPostMessageChannel(Uri paramUri)
  {
    try
    {
      boolean bool = this.mService.requestPostMessageChannel(this.mCallback, paramUri);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return false;
  }

  public boolean setActionButton(@NonNull Bitmap paramBitmap, @NonNull String paramString)
  {
    Bundle localBundle1 = new Bundle();
    localBundle1.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
    localBundle1.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
    Bundle localBundle2 = new Bundle();
    localBundle2.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", localBundle1);
    try
    {
      boolean bool = this.mService.updateVisuals(this.mCallback, localBundle2);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return false;
  }

  public boolean setSecondaryToolbarViews(@Nullable RemoteViews paramRemoteViews, @Nullable int[] paramArrayOfInt, @Nullable PendingIntent paramPendingIntent)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("android.support.customtabs.extra.EXTRA_REMOTEVIEWS", paramRemoteViews);
    localBundle.putIntArray("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS", paramArrayOfInt);
    localBundle.putParcelable("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT", paramPendingIntent);
    try
    {
      boolean bool = this.mService.updateVisuals(this.mCallback, localBundle);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return false;
  }

  @Deprecated
  public boolean setToolbarItem(int paramInt, @NonNull Bitmap paramBitmap, @NonNull String paramString)
  {
    Bundle localBundle1 = new Bundle();
    localBundle1.putInt("android.support.customtabs.customaction.ID", paramInt);
    localBundle1.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
    localBundle1.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
    Bundle localBundle2 = new Bundle();
    localBundle2.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", localBundle1);
    try
    {
      boolean bool = this.mService.updateVisuals(this.mCallback, localBundle2);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return false;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.customtabs.CustomTabsSession
 * JD-Core Version:    0.6.0
 */