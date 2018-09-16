package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;

@RequiresApi(26)
class NotificationCompatApi26
{
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder mB;

    Builder(Context paramContext, Notification paramNotification1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews1, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, String paramString1, ArrayList<String> paramArrayList, Bundle paramBundle, int paramInt5, int paramInt6, Notification paramNotification2, String paramString2, boolean paramBoolean5, String paramString3, CharSequence[] paramArrayOfCharSequence, RemoteViews paramRemoteViews2, RemoteViews paramRemoteViews3, RemoteViews paramRemoteViews4, String paramString4, int paramInt7, String paramString5, long paramLong, boolean paramBoolean6, boolean paramBoolean7, int paramInt8)
    {
      Notification.Builder localBuilder1 = new Notification.Builder(paramContext, paramString4).setWhen(paramNotification1.when).setShowWhen(paramBoolean2).setSmallIcon(paramNotification1.icon, paramNotification1.iconLevel).setContent(paramNotification1.contentView).setTicker(paramNotification1.tickerText, paramRemoteViews1).setSound(paramNotification1.sound, paramNotification1.audioStreamType).setVibrate(paramNotification1.vibrate).setLights(paramNotification1.ledARGB, paramNotification1.ledOnMS, paramNotification1.ledOffMS);
      boolean bool1;
      boolean bool2;
      label122: boolean bool3;
      label144: Notification.Builder localBuilder4;
      if ((0x2 & paramNotification1.flags) != 0)
      {
        bool1 = true;
        Notification.Builder localBuilder2 = localBuilder1.setOngoing(bool1);
        if ((0x8 & paramNotification1.flags) == 0)
          break label430;
        bool2 = true;
        Notification.Builder localBuilder3 = localBuilder2.setOnlyAlertOnce(bool2);
        if ((0x10 & paramNotification1.flags) == 0)
          break label436;
        bool3 = true;
        localBuilder4 = localBuilder3.setAutoCancel(bool3).setDefaults(paramNotification1.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification1.deleteIntent);
        if ((0x80 & paramNotification1.flags) == 0)
          break label442;
      }
      label430: label436: label442: for (boolean bool4 = true; ; bool4 = false)
      {
        this.mB = localBuilder4.setFullScreenIntent(paramPendingIntent2, bool4).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1).setLocalOnly(paramBoolean4).setExtras(paramBundle).setGroup(paramString2).setGroupSummary(paramBoolean5).setSortKey(paramString3).setCategory(paramString1).setColor(paramInt5).setVisibility(paramInt6).setPublicVersion(paramNotification2).setRemoteInputHistory(paramArrayOfCharSequence).setChannelId(paramString4).setBadgeIconType(paramInt7).setShortcutId(paramString5).setTimeoutAfter(paramLong).setGroupAlertBehavior(paramInt8);
        if (paramBoolean7)
          this.mB.setColorized(paramBoolean6);
        if (paramRemoteViews2 != null)
          this.mB.setCustomContentView(paramRemoteViews2);
        if (paramRemoteViews3 != null)
          this.mB.setCustomBigContentView(paramRemoteViews3);
        if (paramRemoteViews4 != null)
          this.mB.setCustomHeadsUpContentView(paramRemoteViews4);
        Iterator localIterator = paramArrayList.iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          this.mB.addPerson(str);
        }
        bool1 = false;
        break;
        bool2 = false;
        break label122;
        bool3 = false;
        break label144;
      }
    }

    public void addAction(NotificationCompatBase.Action paramAction)
    {
      NotificationCompatApi24.addAction(this.mB, paramAction);
    }

    public Notification build()
    {
      return this.mB.build();
    }

    public Notification.Builder getBuilder()
    {
      return this.mB;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.app.NotificationCompatApi26
 * JD-Core Version:    0.6.0
 */