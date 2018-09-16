package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Action.Builder;
import android.app.Notification.Builder;
import android.app.Notification.MessagingStyle;
import android.app.Notification.MessagingStyle.Message;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiresApi(24)
class NotificationCompatApi24
{
  public static void addAction(Notification.Builder paramBuilder, NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    if (paramAction.getRemoteInputs() != null)
    {
      RemoteInput[] arrayOfRemoteInput = RemoteInputCompatApi20.fromCompat(paramAction.getRemoteInputs());
      int i = arrayOfRemoteInput.length;
      for (int j = 0; j < i; j++)
        localBuilder.addRemoteInput(arrayOfRemoteInput[j]);
    }
    if (paramAction.getExtras() != null);
    for (Bundle localBundle = new Bundle(paramAction.getExtras()); ; localBundle = new Bundle())
    {
      localBundle.putBoolean("android.support.allowGeneratedReplies", paramAction.getAllowGeneratedReplies());
      localBuilder.setAllowGeneratedReplies(paramAction.getAllowGeneratedReplies());
      localBuilder.addExtras(localBundle);
      paramBuilder.addAction(localBuilder.build());
      return;
    }
  }

  public static void addMessagingStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, CharSequence paramCharSequence2, List<CharSequence> paramList1, List<Long> paramList, List<CharSequence> paramList2, List<String> paramList3, List<Uri> paramList4)
  {
    Notification.MessagingStyle localMessagingStyle = new Notification.MessagingStyle(paramCharSequence1).setConversationTitle(paramCharSequence2);
    for (int i = 0; i < paramList1.size(); i++)
    {
      Notification.MessagingStyle.Message localMessage = new Notification.MessagingStyle.Message((CharSequence)paramList1.get(i), ((Long)paramList.get(i)).longValue(), (CharSequence)paramList2.get(i));
      if (paramList3.get(i) != null)
        localMessage.setData((String)paramList3.get(i), (Uri)paramList4.get(i));
      localMessagingStyle.addMessage(localMessage);
    }
    localMessagingStyle.setBuilder(paramNotificationBuilderWithBuilderAccessor.getBuilder());
  }

  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    return getActionCompatFromAction(paramNotification.actions[paramInt], paramFactory, paramFactory1);
  }

  private static NotificationCompatBase.Action getActionCompatFromAction(Notification.Action paramAction, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = RemoteInputCompatApi20.toCompat(paramAction.getRemoteInputs(), paramFactory1);
    if ((paramAction.getExtras().getBoolean("android.support.allowGeneratedReplies")) || (paramAction.getAllowGeneratedReplies()));
    for (boolean bool = true; ; bool = false)
      return paramFactory.build(paramAction.icon, paramAction.title, paramAction.actionIntent, paramAction.getExtras(), arrayOfRemoteInput, null, bool);
  }

  private static Notification.Action getActionFromActionCompat(NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    if (paramAction.getExtras() != null);
    for (Bundle localBundle = new Bundle(paramAction.getExtras()); ; localBundle = new Bundle())
    {
      localBundle.putBoolean("android.support.allowGeneratedReplies", paramAction.getAllowGeneratedReplies());
      localBuilder.setAllowGeneratedReplies(paramAction.getAllowGeneratedReplies());
      localBuilder.addExtras(localBundle);
      RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = paramAction.getRemoteInputs();
      if (arrayOfRemoteInput == null)
        break;
      RemoteInput[] arrayOfRemoteInput1 = RemoteInputCompatApi20.fromCompat(arrayOfRemoteInput);
      int i = arrayOfRemoteInput1.length;
      for (int j = 0; j < i; j++)
        localBuilder.addRemoteInput(arrayOfRemoteInput1[j]);
    }
    return localBuilder.build();
  }

  public static NotificationCompatBase.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    NotificationCompatBase.Action[] arrayOfAction;
    if (paramArrayList == null)
      arrayOfAction = null;
    while (true)
    {
      return arrayOfAction;
      arrayOfAction = paramFactory.newArray(paramArrayList.size());
      for (int i = 0; i < arrayOfAction.length; i++)
        arrayOfAction[i] = getActionCompatFromAction((Notification.Action)paramArrayList.get(i), paramFactory, paramFactory1);
    }
  }

  public static ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompatBase.Action[] paramArrayOfAction)
  {
    ArrayList localArrayList;
    if (paramArrayOfAction == null)
      localArrayList = null;
    while (true)
    {
      return localArrayList;
      localArrayList = new ArrayList(paramArrayOfAction.length);
      int i = paramArrayOfAction.length;
      for (int j = 0; j < i; j++)
        localArrayList.add(getActionFromActionCompat(paramArrayOfAction[j]));
    }
  }

  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private int mGroupAlertBehavior;

    public Builder(Context paramContext, Notification paramNotification1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews1, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, String paramString1, ArrayList<String> paramArrayList, Bundle paramBundle, int paramInt5, int paramInt6, Notification paramNotification2, String paramString2, boolean paramBoolean5, String paramString3, CharSequence[] paramArrayOfCharSequence, RemoteViews paramRemoteViews2, RemoteViews paramRemoteViews3, RemoteViews paramRemoteViews4, int paramInt7)
    {
      Notification.Builder localBuilder1 = new Notification.Builder(paramContext).setWhen(paramNotification1.when).setShowWhen(paramBoolean2).setSmallIcon(paramNotification1.icon, paramNotification1.iconLevel).setContent(paramNotification1.contentView).setTicker(paramNotification1.tickerText, paramRemoteViews1).setSound(paramNotification1.sound, paramNotification1.audioStreamType).setVibrate(paramNotification1.vibrate).setLights(paramNotification1.ledARGB, paramNotification1.ledOnMS, paramNotification1.ledOffMS);
      boolean bool1;
      boolean bool2;
      label120: boolean bool3;
      label142: Notification.Builder localBuilder4;
      if ((0x2 & paramNotification1.flags) != 0)
      {
        bool1 = true;
        Notification.Builder localBuilder2 = localBuilder1.setOngoing(bool1);
        if ((0x8 & paramNotification1.flags) == 0)
          break label388;
        bool2 = true;
        Notification.Builder localBuilder3 = localBuilder2.setOnlyAlertOnce(bool2);
        if ((0x10 & paramNotification1.flags) == 0)
          break label394;
        bool3 = true;
        localBuilder4 = localBuilder3.setAutoCancel(bool3).setDefaults(paramNotification1.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification1.deleteIntent);
        if ((0x80 & paramNotification1.flags) == 0)
          break label400;
      }
      label388: label394: label400: for (boolean bool4 = true; ; bool4 = false)
      {
        this.b = localBuilder4.setFullScreenIntent(paramPendingIntent2, bool4).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1).setLocalOnly(paramBoolean4).setExtras(paramBundle).setGroup(paramString2).setGroupSummary(paramBoolean5).setSortKey(paramString3).setCategory(paramString1).setColor(paramInt5).setVisibility(paramInt6).setPublicVersion(paramNotification2).setRemoteInputHistory(paramArrayOfCharSequence);
        if (paramRemoteViews2 != null)
          this.b.setCustomContentView(paramRemoteViews2);
        if (paramRemoteViews3 != null)
          this.b.setCustomBigContentView(paramRemoteViews3);
        if (paramRemoteViews4 != null)
          this.b.setCustomHeadsUpContentView(paramRemoteViews4);
        Iterator localIterator = paramArrayList.iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          this.b.addPerson(str);
        }
        bool1 = false;
        break;
        bool2 = false;
        break label120;
        bool3 = false;
        break label142;
      }
      this.mGroupAlertBehavior = paramInt7;
    }

    private void removeSoundAndVibration(Notification paramNotification)
    {
      paramNotification.sound = null;
      paramNotification.vibrate = null;
      paramNotification.defaults = (0xFFFFFFFE & paramNotification.defaults);
      paramNotification.defaults = (0xFFFFFFFD & paramNotification.defaults);
    }

    public void addAction(NotificationCompatBase.Action paramAction)
    {
      NotificationCompatApi24.addAction(this.b, paramAction);
    }

    public Notification build()
    {
      Notification localNotification = this.b.build();
      if (this.mGroupAlertBehavior != 0)
      {
        if ((localNotification.getGroup() != null) && ((0x200 & localNotification.flags) != 0) && (this.mGroupAlertBehavior == 2))
          removeSoundAndVibration(localNotification);
        if ((localNotification.getGroup() != null) && ((0x200 & localNotification.flags) == 0) && (this.mGroupAlertBehavior == 1))
          removeSoundAndVibration(localNotification);
      }
      return localNotification;
    }

    public Notification.Builder getBuilder()
    {
      return this.b;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.app.NotificationCompatApi24
 * JD-Core Version:    0.6.0
 */