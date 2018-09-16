package android.support.customtabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.BundleCompat;
import android.util.Log;

public class CustomTabsSessionToken
{
  private static final String TAG = "CustomTabsSessionToken";
  private final CustomTabsCallback mCallback;
  private final ICustomTabsCallback mCallbackBinder;

  CustomTabsSessionToken(ICustomTabsCallback paramICustomTabsCallback)
  {
    this.mCallbackBinder = paramICustomTabsCallback;
    this.mCallback = new CustomTabsCallback()
    {
      public void extraCallback(String paramString, Bundle paramBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.extraCallback(paramString, paramBundle);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
        }
      }

      public void onMessageChannelReady(Bundle paramBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.onMessageChannelReady(paramBundle);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
        }
      }

      public void onNavigationEvent(int paramInt, Bundle paramBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.onNavigationEvent(paramInt, paramBundle);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
        }
      }

      public void onPostMessage(String paramString, Bundle paramBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.onPostMessage(paramString, paramBundle);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
        }
      }
    };
  }

  public static CustomTabsSessionToken getSessionTokenFromIntent(Intent paramIntent)
  {
    IBinder localIBinder = BundleCompat.getBinder(paramIntent.getExtras(), "android.support.customtabs.extra.SESSION");
    if (localIBinder == null)
      return null;
    return new CustomTabsSessionToken(ICustomTabsCallback.Stub.asInterface(localIBinder));
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof CustomTabsSessionToken))
      return false;
    return ((CustomTabsSessionToken)paramObject).getCallbackBinder().equals(this.mCallbackBinder.asBinder());
  }

  public CustomTabsCallback getCallback()
  {
    return this.mCallback;
  }

  IBinder getCallbackBinder()
  {
    return this.mCallbackBinder.asBinder();
  }

  public int hashCode()
  {
    return getCallbackBinder().hashCode();
  }

  public boolean isAssociatedWith(CustomTabsSession paramCustomTabsSession)
  {
    return paramCustomTabsSession.getBinder().equals(this.mCallbackBinder);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.customtabs.CustomTabsSessionToken
 * JD-Core Version:    0.6.0
 */