package android.support.v7.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.session.MediaSessionCompat.Token;

@Deprecated
public class NotificationCompat extends android.support.v4.app.NotificationCompat
{
  @Deprecated
  public static MediaSessionCompat.Token getMediaSession(Notification paramNotification)
  {
    Bundle localBundle = getExtras(paramNotification);
    if (localBundle != null)
      if (Build.VERSION.SDK_INT >= 21)
      {
        Parcelable localParcelable = localBundle.getParcelable("android.mediaSession");
        if (localParcelable != null)
          return MediaSessionCompat.Token.fromToken(localParcelable);
      }
      else
      {
        IBinder localIBinder = BundleCompat.getBinder(localBundle, "android.mediaSession");
        if (localIBinder != null)
        {
          Parcel localParcel = Parcel.obtain();
          localParcel.writeStrongBinder(localIBinder);
          localParcel.setDataPosition(0);
          MediaSessionCompat.Token localToken = (MediaSessionCompat.Token)MediaSessionCompat.Token.CREATOR.createFromParcel(localParcel);
          localParcel.recycle();
          return localToken;
        }
      }
    return null;
  }

  @Deprecated
  public static class Builder extends android.support.v4.app.NotificationCompat.Builder
  {
    @Deprecated
    public Builder(Context paramContext)
    {
      super();
    }
  }

  @Deprecated
  public static class DecoratedCustomViewStyle extends android.support.v4.app.NotificationCompat.DecoratedCustomViewStyle
  {
  }

  @Deprecated
  public static class DecoratedMediaCustomViewStyle extends android.support.v4.media.app.NotificationCompat.DecoratedMediaCustomViewStyle
  {
  }

  @Deprecated
  public static class MediaStyle extends android.support.v4.media.app.NotificationCompat.MediaStyle
  {
    @Deprecated
    public MediaStyle()
    {
    }

    @Deprecated
    public MediaStyle(android.support.v4.app.NotificationCompat.Builder paramBuilder)
    {
      super();
    }

    @Deprecated
    public MediaStyle setCancelButtonIntent(PendingIntent paramPendingIntent)
    {
      return (MediaStyle)super.setCancelButtonIntent(paramPendingIntent);
    }

    @Deprecated
    public MediaStyle setMediaSession(MediaSessionCompat.Token paramToken)
    {
      return (MediaStyle)super.setMediaSession(paramToken);
    }

    @Deprecated
    public MediaStyle setShowActionsInCompactView(int[] paramArrayOfInt)
    {
      return (MediaStyle)super.setShowActionsInCompactView(paramArrayOfInt);
    }

    @Deprecated
    public MediaStyle setShowCancelButton(boolean paramBoolean)
    {
      return (MediaStyle)super.setShowCancelButton(paramBoolean);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.app.NotificationCompat
 * JD-Core Version:    0.6.0
 */