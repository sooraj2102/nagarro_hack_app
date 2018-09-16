package android.support.customtabs;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IPostMessageService extends IInterface
{
  public abstract void onMessageChannelReady(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
    throws RemoteException;

  public abstract void onPostMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
    throws RemoteException;

  public static abstract class Stub extends Binder
    implements IPostMessageService
  {
    private static final String DESCRIPTOR = "android.support.customtabs.IPostMessageService";
    static final int TRANSACTION_onMessageChannelReady = 2;
    static final int TRANSACTION_onPostMessage = 3;

    public Stub()
    {
      attachInterface(this, "android.support.customtabs.IPostMessageService");
    }

    public static IPostMessageService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null)
        return null;
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.support.customtabs.IPostMessageService");
      if ((localIInterface != null) && ((localIInterface instanceof IPostMessageService)))
        return (IPostMessageService)localIInterface;
      return new Proxy(paramIBinder);
    }

    public IBinder asBinder()
    {
      return this;
    }

    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default:
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902:
        paramParcel2.writeString("android.support.customtabs.IPostMessageService");
        return true;
      case 2:
        paramParcel1.enforceInterface("android.support.customtabs.IPostMessageService");
        ICustomTabsCallback localICustomTabsCallback2 = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0);
        for (Bundle localBundle2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle2 = null)
        {
          onMessageChannelReady(localICustomTabsCallback2, localBundle2);
          paramParcel2.writeNoException();
          return true;
        }
      case 3:
      }
      paramParcel1.enforceInterface("android.support.customtabs.IPostMessageService");
      ICustomTabsCallback localICustomTabsCallback1 = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
      String str = paramParcel1.readString();
      if (paramParcel1.readInt() != 0);
      for (Bundle localBundle1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle1 = null)
      {
        onPostMessage(localICustomTabsCallback1, str, localBundle1);
        paramParcel2.writeNoException();
        return true;
      }
    }

    private static class Proxy
      implements IPostMessageService
    {
      private IBinder mRemote;

      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }

      public IBinder asBinder()
      {
        return this.mRemote;
      }

      public String getInterfaceDescriptor()
      {
        return "android.support.customtabs.IPostMessageService";
      }

      public void onMessageChannelReady(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.customtabs.IPostMessageService");
          IBinder localIBinder;
          if (paramICustomTabsCallback != null)
          {
            localIBinder = paramICustomTabsCallback.asBinder();
            localParcel1.writeStrongBinder(localIBinder);
            if (paramBundle == null)
              break label84;
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          while (true)
          {
            this.mRemote.transact(2, localParcel1, localParcel2, 0);
            localParcel2.readException();
            return;
            localIBinder = null;
            break;
            label84: localParcel1.writeInt(0);
          }
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
        throw localObject;
      }

      public void onPostMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.customtabs.IPostMessageService");
          IBinder localIBinder;
          if (paramICustomTabsCallback != null)
          {
            localIBinder = paramICustomTabsCallback.asBinder();
            localParcel1.writeStrongBinder(localIBinder);
            localParcel1.writeString(paramString);
            if (paramBundle == null)
              break label97;
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          while (true)
          {
            this.mRemote.transact(3, localParcel1, localParcel2, 0);
            localParcel2.readException();
            return;
            localIBinder = null;
            break;
            label97: localParcel1.writeInt(0);
          }
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
        throw localObject;
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.customtabs.IPostMessageService
 * JD-Core Version:    0.6.0
 */