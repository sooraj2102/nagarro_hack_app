package android.support.customtabs;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface ICustomTabsService extends IInterface
{
  public abstract Bundle extraCommand(String paramString, Bundle paramBundle)
    throws RemoteException;

  public abstract boolean mayLaunchUrl(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
    throws RemoteException;

  public abstract boolean newSession(ICustomTabsCallback paramICustomTabsCallback)
    throws RemoteException;

  public abstract int postMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
    throws RemoteException;

  public abstract boolean requestPostMessageChannel(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri)
    throws RemoteException;

  public abstract boolean updateVisuals(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
    throws RemoteException;

  public abstract boolean warmup(long paramLong)
    throws RemoteException;

  public static abstract class Stub extends Binder
    implements ICustomTabsService
  {
    private static final String DESCRIPTOR = "android.support.customtabs.ICustomTabsService";
    static final int TRANSACTION_extraCommand = 5;
    static final int TRANSACTION_mayLaunchUrl = 4;
    static final int TRANSACTION_newSession = 3;
    static final int TRANSACTION_postMessage = 8;
    static final int TRANSACTION_requestPostMessageChannel = 7;
    static final int TRANSACTION_updateVisuals = 6;
    static final int TRANSACTION_warmup = 2;

    public Stub()
    {
      attachInterface(this, "android.support.customtabs.ICustomTabsService");
    }

    public static ICustomTabsService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null)
        return null;
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.support.customtabs.ICustomTabsService");
      if ((localIInterface != null) && ((localIInterface instanceof ICustomTabsService)))
        return (ICustomTabsService)localIInterface;
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
        paramParcel2.writeString("android.support.customtabs.ICustomTabsService");
        return true;
      case 2:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        boolean bool5 = warmup(paramParcel1.readLong());
        paramParcel2.writeNoException();
        int i1 = 0;
        if (bool5)
          i1 = 1;
        paramParcel2.writeInt(i1);
        return true;
      case 3:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        boolean bool4 = newSession(ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        int n = 0;
        if (bool4)
          n = 1;
        paramParcel2.writeInt(n);
        return true;
      case 4:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        ICustomTabsCallback localICustomTabsCallback4 = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        Uri localUri2;
        if (paramParcel1.readInt() != 0)
        {
          localUri2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0)
            break label280;
        }
        for (Bundle localBundle5 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle5 = null)
        {
          boolean bool3 = mayLaunchUrl(localICustomTabsCallback4, localUri2, localBundle5, paramParcel1.createTypedArrayList(Bundle.CREATOR));
          paramParcel2.writeNoException();
          int m = 0;
          if (bool3)
            m = 1;
          paramParcel2.writeInt(m);
          return true;
          localUri2 = null;
          break;
        }
      case 5:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        String str2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0);
        for (Bundle localBundle3 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle3 = null)
        {
          Bundle localBundle4 = extraCommand(str2, localBundle3);
          paramParcel2.writeNoException();
          if (localBundle4 == null)
            break;
          paramParcel2.writeInt(1);
          localBundle4.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 6:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        ICustomTabsCallback localICustomTabsCallback3 = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0);
        for (Bundle localBundle2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle2 = null)
        {
          boolean bool2 = updateVisuals(localICustomTabsCallback3, localBundle2);
          paramParcel2.writeNoException();
          int k = 0;
          if (bool2)
            k = 1;
          paramParcel2.writeInt(k);
          return true;
        }
      case 7:
        label280: paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        ICustomTabsCallback localICustomTabsCallback2 = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0);
        for (Uri localUri1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1); ; localUri1 = null)
        {
          boolean bool1 = requestPostMessageChannel(localICustomTabsCallback2, localUri1);
          paramParcel2.writeNoException();
          int j = 0;
          if (bool1)
            j = 1;
          paramParcel2.writeInt(j);
          return true;
        }
      case 8:
      }
      paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
      ICustomTabsCallback localICustomTabsCallback1 = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
      String str1 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0);
      for (Bundle localBundle1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle1 = null)
      {
        int i = postMessage(localICustomTabsCallback1, str1, localBundle1);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(i);
        return true;
      }
    }

    private static class Proxy
      implements ICustomTabsService
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

      public Bundle extraCommand(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        while (true)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            localParcel1.writeString(paramString);
            if (paramBundle == null)
              continue;
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
            this.mRemote.transact(5, localParcel1, localParcel2, 0);
            localParcel2.readException();
            if (localParcel2.readInt() != 0)
            {
              localBundle = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
              return localBundle;
              localParcel1.writeInt(0);
              continue;
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          Bundle localBundle = null;
        }
      }

      public String getInterfaceDescriptor()
      {
        return "android.support.customtabs.ICustomTabsService";
      }

      public boolean mayLaunchUrl(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        while (true)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            if (paramICustomTabsCallback == null)
              continue;
            IBinder localIBinder = paramICustomTabsCallback.asBinder();
            localParcel1.writeStrongBinder(localIBinder);
            if (paramUri == null)
              continue;
            localParcel1.writeInt(1);
            paramUri.writeToParcel(localParcel1, 0);
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              localParcel1.writeTypedList(paramList);
              this.mRemote.transact(4, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int j = localParcel2.readInt();
              if (j == 0)
                break label165;
              return i;
              localIBinder = null;
              continue;
              localParcel1.writeInt(0);
              continue;
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          localParcel1.writeInt(0);
          continue;
          label165: i = 0;
        }
      }

      public boolean newSession(ICustomTabsCallback paramICustomTabsCallback)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
          if (paramICustomTabsCallback != null);
          for (IBinder localIBinder = paramICustomTabsCallback.asBinder(); ; localIBinder = null)
          {
            localParcel1.writeStrongBinder(localIBinder);
            this.mRemote.transact(3, localParcel1, localParcel2, 0);
            localParcel2.readException();
            int i = localParcel2.readInt();
            int j = 0;
            if (i != 0)
              j = 1;
            return j;
          }
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
        throw localObject;
      }

      public int postMessage(ICustomTabsCallback paramICustomTabsCallback, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
          IBinder localIBinder;
          if (paramICustomTabsCallback != null)
          {
            localIBinder = paramICustomTabsCallback.asBinder();
            localParcel1.writeStrongBinder(localIBinder);
            localParcel1.writeString(paramString);
            if (paramBundle == null)
              break label107;
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          while (true)
          {
            this.mRemote.transact(8, localParcel1, localParcel2, 0);
            localParcel2.readException();
            int i = localParcel2.readInt();
            return i;
            localIBinder = null;
            break;
            label107: localParcel1.writeInt(0);
          }
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
        throw localObject;
      }

      public boolean requestPostMessageChannel(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        while (true)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            if (paramICustomTabsCallback == null)
              continue;
            IBinder localIBinder = paramICustomTabsCallback.asBinder();
            localParcel1.writeStrongBinder(localIBinder);
            if (paramUri == null)
              continue;
            localParcel1.writeInt(1);
            paramUri.writeToParcel(localParcel1, 0);
            this.mRemote.transact(7, localParcel1, localParcel2, 0);
            localParcel2.readException();
            int j = localParcel2.readInt();
            if (j != 0)
            {
              return i;
              localIBinder = null;
              continue;
              localParcel1.writeInt(0);
              continue;
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          i = 0;
        }
      }

      public boolean updateVisuals(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        while (true)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            if (paramICustomTabsCallback == null)
              continue;
            IBinder localIBinder = paramICustomTabsCallback.asBinder();
            localParcel1.writeStrongBinder(localIBinder);
            if (paramBundle == null)
              continue;
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
            this.mRemote.transact(6, localParcel1, localParcel2, 0);
            localParcel2.readException();
            int j = localParcel2.readInt();
            if (j != 0)
            {
              return i;
              localIBinder = null;
              continue;
              localParcel1.writeInt(0);
              continue;
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          i = 0;
        }
      }

      public boolean warmup(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          int j = 0;
          if (i != 0)
            j = 1;
          return j;
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
 * Qualified Name:     android.support.customtabs.ICustomTabsService
 * JD-Core Version:    0.6.0
 */