package android.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

public class PostMessageService extends Service
{
  private IPostMessageService.Stub mBinder = new IPostMessageService.Stub()
  {
    public void onMessageChannelReady(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
      throws RemoteException
    {
      paramICustomTabsCallback.onMessageChannelReady(paramBundle);
    }

    public void onPostMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
      throws RemoteException
    {
      paramICustomTabsCallback.onPostMessage(paramString, paramBundle);
    }
  };

  public IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.customtabs.PostMessageService
 * JD-Core Version:    0.6.0
 */