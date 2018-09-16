package android.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class CustomTabsService extends Service
{
  public static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
  public static final String KEY_URL = "android.support.customtabs.otherurls.URL";
  public static final int RESULT_FAILURE_DISALLOWED = -1;
  public static final int RESULT_FAILURE_MESSAGING_ERROR = -3;
  public static final int RESULT_FAILURE_REMOTE_ERROR = -2;
  public static final int RESULT_SUCCESS;
  private ICustomTabsService.Stub mBinder = new ICustomTabsService.Stub()
  {
    public Bundle extraCommand(String paramString, Bundle paramBundle)
    {
      return CustomTabsService.this.extraCommand(paramString, paramBundle);
    }

    public boolean mayLaunchUrl(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
    {
      return CustomTabsService.this.mayLaunchUrl(new CustomTabsSessionToken(paramICustomTabsCallback), paramUri, paramBundle, paramList);
    }

    public boolean newSession(ICustomTabsCallback paramICustomTabsCallback)
    {
      CustomTabsSessionToken localCustomTabsSessionToken = new CustomTabsSessionToken(paramICustomTabsCallback);
      try
      {
        1 local1 = new IBinder.DeathRecipient(localCustomTabsSessionToken)
        {
          public void binderDied()
          {
            CustomTabsService.this.cleanUpSession(this.val$sessionToken);
          }
        };
        synchronized (CustomTabsService.this.mDeathRecipientMap)
        {
          paramICustomTabsCallback.asBinder().linkToDeath(local1, 0);
          CustomTabsService.this.mDeathRecipientMap.put(paramICustomTabsCallback.asBinder(), local1);
          boolean bool = CustomTabsService.this.newSession(localCustomTabsSessionToken);
          return bool;
        }
      }
      catch (RemoteException localRemoteException)
      {
      }
      return false;
    }

    public int postMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
    {
      return CustomTabsService.this.postMessage(new CustomTabsSessionToken(paramICustomTabsCallback), paramString, paramBundle);
    }

    public boolean requestPostMessageChannel(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri)
    {
      return CustomTabsService.this.requestPostMessageChannel(new CustomTabsSessionToken(paramICustomTabsCallback), paramUri);
    }

    public boolean updateVisuals(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
    {
      return CustomTabsService.this.updateVisuals(new CustomTabsSessionToken(paramICustomTabsCallback), paramBundle);
    }

    public boolean warmup(long paramLong)
    {
      return CustomTabsService.this.warmup(paramLong);
    }
  };
  private final Map<IBinder, IBinder.DeathRecipient> mDeathRecipientMap = new ArrayMap();

  protected boolean cleanUpSession(CustomTabsSessionToken paramCustomTabsSessionToken)
  {
    try
    {
      synchronized (this.mDeathRecipientMap)
      {
        IBinder localIBinder = paramCustomTabsSessionToken.getCallbackBinder();
        localIBinder.unlinkToDeath((IBinder.DeathRecipient)this.mDeathRecipientMap.get(localIBinder), 0);
        this.mDeathRecipientMap.remove(localIBinder);
        return true;
      }
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
    }
    return false;
  }

  protected abstract Bundle extraCommand(String paramString, Bundle paramBundle);

  protected abstract boolean mayLaunchUrl(CustomTabsSessionToken paramCustomTabsSessionToken, Uri paramUri, Bundle paramBundle, List<Bundle> paramList);

  protected abstract boolean newSession(CustomTabsSessionToken paramCustomTabsSessionToken);

  public IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }

  protected abstract int postMessage(CustomTabsSessionToken paramCustomTabsSessionToken, String paramString, Bundle paramBundle);

  protected abstract boolean requestPostMessageChannel(CustomTabsSessionToken paramCustomTabsSessionToken, Uri paramUri);

  protected abstract boolean updateVisuals(CustomTabsSessionToken paramCustomTabsSessionToken, Bundle paramBundle);

  protected abstract boolean warmup(long paramLong);

  @Retention(RetentionPolicy.SOURCE)
  public static @interface Result
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.customtabs.CustomTabsService
 * JD-Core Version:    0.6.0
 */