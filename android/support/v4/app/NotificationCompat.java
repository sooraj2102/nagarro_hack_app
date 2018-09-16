package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Builder;
import android.app.Notification.DecoratedCustomViewStyle;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.compat.R.color;
import android.support.compat.R.dimen;
import android.support.compat.R.drawable;
import android.support.compat.R.id;
import android.support.compat.R.integer;
import android.support.compat.R.layout;
import android.support.compat.R.string;
import android.support.v4.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.widget.RemoteViews;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NotificationCompat
{
  public static final int BADGE_ICON_LARGE = 2;
  public static final int BADGE_ICON_NONE = 0;
  public static final int BADGE_ICON_SMALL = 1;
  public static final String CATEGORY_ALARM = "alarm";
  public static final String CATEGORY_CALL = "call";
  public static final String CATEGORY_EMAIL = "email";
  public static final String CATEGORY_ERROR = "err";
  public static final String CATEGORY_EVENT = "event";
  public static final String CATEGORY_MESSAGE = "msg";
  public static final String CATEGORY_PROGRESS = "progress";
  public static final String CATEGORY_PROMO = "promo";
  public static final String CATEGORY_RECOMMENDATION = "recommendation";
  public static final String CATEGORY_REMINDER = "reminder";
  public static final String CATEGORY_SERVICE = "service";
  public static final String CATEGORY_SOCIAL = "social";
  public static final String CATEGORY_STATUS = "status";
  public static final String CATEGORY_SYSTEM = "sys";
  public static final String CATEGORY_TRANSPORT = "transport";

  @ColorInt
  public static final int COLOR_DEFAULT = 0;
  public static final int DEFAULT_ALL = -1;
  public static final int DEFAULT_LIGHTS = 4;
  public static final int DEFAULT_SOUND = 1;
  public static final int DEFAULT_VIBRATE = 2;
  public static final String EXTRA_AUDIO_CONTENTS_URI = "android.audioContents";
  public static final String EXTRA_BACKGROUND_IMAGE_URI = "android.backgroundImageUri";
  public static final String EXTRA_BIG_TEXT = "android.bigText";
  public static final String EXTRA_COMPACT_ACTIONS = "android.compactActions";
  public static final String EXTRA_CONVERSATION_TITLE = "android.conversationTitle";
  public static final String EXTRA_INFO_TEXT = "android.infoText";
  public static final String EXTRA_LARGE_ICON = "android.largeIcon";
  public static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big";
  public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";
  public static final String EXTRA_MESSAGES = "android.messages";
  public static final String EXTRA_PEOPLE = "android.people";
  public static final String EXTRA_PICTURE = "android.picture";
  public static final String EXTRA_PROGRESS = "android.progress";
  public static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate";
  public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
  public static final String EXTRA_REMOTE_INPUT_HISTORY = "android.remoteInputHistory";
  public static final String EXTRA_SELF_DISPLAY_NAME = "android.selfDisplayName";
  public static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer";
  public static final String EXTRA_SHOW_WHEN = "android.showWhen";
  public static final String EXTRA_SMALL_ICON = "android.icon";
  public static final String EXTRA_SUB_TEXT = "android.subText";
  public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
  public static final String EXTRA_TEMPLATE = "android.template";
  public static final String EXTRA_TEXT = "android.text";
  public static final String EXTRA_TEXT_LINES = "android.textLines";
  public static final String EXTRA_TITLE = "android.title";
  public static final String EXTRA_TITLE_BIG = "android.title.big";
  public static final int FLAG_AUTO_CANCEL = 16;
  public static final int FLAG_FOREGROUND_SERVICE = 64;
  public static final int FLAG_GROUP_SUMMARY = 512;

  @Deprecated
  public static final int FLAG_HIGH_PRIORITY = 128;
  public static final int FLAG_INSISTENT = 4;
  public static final int FLAG_LOCAL_ONLY = 256;
  public static final int FLAG_NO_CLEAR = 32;
  public static final int FLAG_ONGOING_EVENT = 2;
  public static final int FLAG_ONLY_ALERT_ONCE = 8;
  public static final int FLAG_SHOW_LIGHTS = 1;
  public static final int GROUP_ALERT_ALL = 0;
  public static final int GROUP_ALERT_CHILDREN = 2;
  public static final int GROUP_ALERT_SUMMARY = 1;
  static final NotificationCompatImpl IMPL;
  public static final int PRIORITY_DEFAULT = 0;
  public static final int PRIORITY_HIGH = 1;
  public static final int PRIORITY_LOW = -1;
  public static final int PRIORITY_MAX = 2;
  public static final int PRIORITY_MIN = -2;
  public static final int STREAM_DEFAULT = -1;
  public static final int VISIBILITY_PRIVATE = 0;
  public static final int VISIBILITY_PUBLIC = 1;
  public static final int VISIBILITY_SECRET = -1;

  static
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      IMPL = new NotificationCompatApi26Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 24)
    {
      IMPL = new NotificationCompatApi24Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new NotificationCompatApi21Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 20)
    {
      IMPL = new NotificationCompatApi20Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new NotificationCompatApi19Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new NotificationCompatApi16Impl();
      return;
    }
    IMPL = new NotificationCompatBaseImpl();
  }

  static void addActionsToBuilder(NotificationBuilderWithActions paramNotificationBuilderWithActions, ArrayList<Action> paramArrayList)
  {
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
      paramNotificationBuilderWithActions.addAction((Action)localIterator.next());
  }

  public static Action getAction(Notification paramNotification, int paramInt)
  {
    return IMPL.getAction(paramNotification, paramInt);
  }

  public static int getActionCount(Notification paramNotification)
  {
    int j;
    if (Build.VERSION.SDK_INT >= 19)
    {
      Notification.Action[] arrayOfAction = paramNotification.actions;
      j = 0;
      if (arrayOfAction != null)
        j = paramNotification.actions.length;
    }
    int i;
    do
    {
      return j;
      i = Build.VERSION.SDK_INT;
      j = 0;
    }
    while (i < 16);
    return NotificationCompatJellybean.getActionCount(paramNotification);
  }

  public static int getBadgeIconType(Notification paramNotification)
  {
    if (Build.VERSION.SDK_INT >= 26)
      return paramNotification.getBadgeIconType();
    return 0;
  }

  public static String getCategory(Notification paramNotification)
  {
    if (Build.VERSION.SDK_INT >= 21)
      return paramNotification.category;
    return null;
  }

  public static String getChannelId(Notification paramNotification)
  {
    if (Build.VERSION.SDK_INT >= 26)
      return paramNotification.getChannelId();
    return null;
  }

  public static Bundle getExtras(Notification paramNotification)
  {
    if (Build.VERSION.SDK_INT >= 19)
      return paramNotification.extras;
    if (Build.VERSION.SDK_INT >= 16)
      return NotificationCompatJellybean.getExtras(paramNotification);
    return null;
  }

  public static String getGroup(Notification paramNotification)
  {
    if (Build.VERSION.SDK_INT >= 20)
      return paramNotification.getGroup();
    if (Build.VERSION.SDK_INT >= 19)
      return paramNotification.extras.getString("android.support.groupKey");
    if (Build.VERSION.SDK_INT >= 16)
      return NotificationCompatJellybean.getExtras(paramNotification).getString("android.support.groupKey");
    return null;
  }

  public static int getGroupAlertBehavior(Notification paramNotification)
  {
    if (Build.VERSION.SDK_INT >= 26)
      return paramNotification.getGroupAlertBehavior();
    return 0;
  }

  public static boolean getLocalOnly(Notification paramNotification)
  {
    int j;
    if (Build.VERSION.SDK_INT >= 20)
    {
      int k = 0x100 & paramNotification.flags;
      j = 0;
      if (k != 0)
        j = 1;
    }
    int i;
    do
    {
      return j;
      if (Build.VERSION.SDK_INT >= 19)
        return paramNotification.extras.getBoolean("android.support.localOnly");
      i = Build.VERSION.SDK_INT;
      j = 0;
    }
    while (i < 16);
    return NotificationCompatJellybean.getExtras(paramNotification).getBoolean("android.support.localOnly");
  }

  static Notification[] getNotificationArrayFromBundle(Bundle paramBundle, String paramString)
  {
    Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray(paramString);
    if (((arrayOfParcelable instanceof Notification[])) || (arrayOfParcelable == null))
      return (Notification[])(Notification[])arrayOfParcelable;
    Notification[] arrayOfNotification = new Notification[arrayOfParcelable.length];
    for (int i = 0; i < arrayOfParcelable.length; i++)
      arrayOfNotification[i] = ((Notification)arrayOfParcelable[i]);
    paramBundle.putParcelableArray(paramString, arrayOfNotification);
    return arrayOfNotification;
  }

  public static String getShortcutId(Notification paramNotification)
  {
    if (Build.VERSION.SDK_INT >= 26)
      return paramNotification.getShortcutId();
    return null;
  }

  public static String getSortKey(Notification paramNotification)
  {
    if (Build.VERSION.SDK_INT >= 20)
      return paramNotification.getSortKey();
    if (Build.VERSION.SDK_INT >= 19)
      return paramNotification.extras.getString("android.support.sortKey");
    if (Build.VERSION.SDK_INT >= 16)
      return NotificationCompatJellybean.getExtras(paramNotification).getString("android.support.sortKey");
    return null;
  }

  public static long getTimeoutAfter(Notification paramNotification)
  {
    if (Build.VERSION.SDK_INT >= 26)
      return paramNotification.getTimeoutAfter();
    return 0L;
  }

  public static boolean isGroupSummary(Notification paramNotification)
  {
    int j;
    if (Build.VERSION.SDK_INT >= 20)
    {
      int k = 0x200 & paramNotification.flags;
      j = 0;
      if (k != 0)
        j = 1;
    }
    int i;
    do
    {
      return j;
      if (Build.VERSION.SDK_INT >= 19)
        return paramNotification.extras.getBoolean("android.support.isGroupSummary");
      i = Build.VERSION.SDK_INT;
      j = 0;
    }
    while (i < 16);
    return NotificationCompatJellybean.getExtras(paramNotification).getBoolean("android.support.isGroupSummary");
  }

  public static class Action extends NotificationCompatBase.Action
  {

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public static final NotificationCompatBase.Action.Factory FACTORY = new NotificationCompatBase.Action.Factory()
    {
      public NotificationCompatBase.Action build(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle, RemoteInputCompatBase.RemoteInput[] paramArrayOfRemoteInput1, RemoteInputCompatBase.RemoteInput[] paramArrayOfRemoteInput2, boolean paramBoolean)
      {
        return new NotificationCompat.Action(paramInt, paramCharSequence, paramPendingIntent, paramBundle, (RemoteInput[])(RemoteInput[])paramArrayOfRemoteInput1, (RemoteInput[])(RemoteInput[])paramArrayOfRemoteInput2, paramBoolean);
      }

      public NotificationCompat.Action[] newArray(int paramInt)
      {
        return new NotificationCompat.Action[paramInt];
      }
    };
    public PendingIntent actionIntent;
    public int icon;
    private boolean mAllowGeneratedReplies;
    private final RemoteInput[] mDataOnlyRemoteInputs;
    final Bundle mExtras;
    private final RemoteInput[] mRemoteInputs;
    public CharSequence title;

    public Action(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
    {
      this(paramInt, paramCharSequence, paramPendingIntent, new Bundle(), null, null, true);
    }

    Action(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle, RemoteInput[] paramArrayOfRemoteInput1, RemoteInput[] paramArrayOfRemoteInput2, boolean paramBoolean)
    {
      this.icon = paramInt;
      this.title = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      this.actionIntent = paramPendingIntent;
      if (paramBundle != null);
      while (true)
      {
        this.mExtras = paramBundle;
        this.mRemoteInputs = paramArrayOfRemoteInput1;
        this.mDataOnlyRemoteInputs = paramArrayOfRemoteInput2;
        this.mAllowGeneratedReplies = paramBoolean;
        return;
        paramBundle = new Bundle();
      }
    }

    public PendingIntent getActionIntent()
    {
      return this.actionIntent;
    }

    public boolean getAllowGeneratedReplies()
    {
      return this.mAllowGeneratedReplies;
    }

    public RemoteInput[] getDataOnlyRemoteInputs()
    {
      return this.mDataOnlyRemoteInputs;
    }

    public Bundle getExtras()
    {
      return this.mExtras;
    }

    public int getIcon()
    {
      return this.icon;
    }

    public RemoteInput[] getRemoteInputs()
    {
      return this.mRemoteInputs;
    }

    public CharSequence getTitle()
    {
      return this.title;
    }

    public static final class Builder
    {
      private boolean mAllowGeneratedReplies = true;
      private final Bundle mExtras;
      private final int mIcon;
      private final PendingIntent mIntent;
      private ArrayList<RemoteInput> mRemoteInputs;
      private final CharSequence mTitle;

      public Builder(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
      {
        this(paramInt, paramCharSequence, paramPendingIntent, new Bundle(), null, true);
      }

      private Builder(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle, RemoteInput[] paramArrayOfRemoteInput, boolean paramBoolean)
      {
        this.mIcon = paramInt;
        this.mTitle = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
        this.mIntent = paramPendingIntent;
        this.mExtras = paramBundle;
        if (paramArrayOfRemoteInput == null);
        for (ArrayList localArrayList = null; ; localArrayList = new ArrayList(Arrays.asList(paramArrayOfRemoteInput)))
        {
          this.mRemoteInputs = localArrayList;
          this.mAllowGeneratedReplies = paramBoolean;
          return;
        }
      }

      public Builder(NotificationCompat.Action paramAction)
      {
        this(paramAction.icon, paramAction.title, paramAction.actionIntent, new Bundle(paramAction.mExtras), paramAction.getRemoteInputs(), paramAction.getAllowGeneratedReplies());
      }

      public Builder addExtras(Bundle paramBundle)
      {
        if (paramBundle != null)
          this.mExtras.putAll(paramBundle);
        return this;
      }

      public Builder addRemoteInput(RemoteInput paramRemoteInput)
      {
        if (this.mRemoteInputs == null)
          this.mRemoteInputs = new ArrayList();
        this.mRemoteInputs.add(paramRemoteInput);
        return this;
      }

      public NotificationCompat.Action build()
      {
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        if (this.mRemoteInputs != null)
        {
          Iterator localIterator = this.mRemoteInputs.iterator();
          while (localIterator.hasNext())
          {
            RemoteInput localRemoteInput = (RemoteInput)localIterator.next();
            if (localRemoteInput.isDataOnly())
            {
              localArrayList1.add(localRemoteInput);
              continue;
            }
            localArrayList2.add(localRemoteInput);
          }
        }
        RemoteInput[] arrayOfRemoteInput1;
        if (localArrayList1.isEmpty())
        {
          arrayOfRemoteInput1 = null;
          if (!localArrayList2.isEmpty())
            break label162;
        }
        label162: for (RemoteInput[] arrayOfRemoteInput2 = null; ; arrayOfRemoteInput2 = (RemoteInput[])localArrayList2.toArray(new RemoteInput[localArrayList2.size()]))
        {
          return new NotificationCompat.Action(this.mIcon, this.mTitle, this.mIntent, this.mExtras, arrayOfRemoteInput2, arrayOfRemoteInput1, this.mAllowGeneratedReplies);
          arrayOfRemoteInput1 = (RemoteInput[])localArrayList1.toArray(new RemoteInput[localArrayList1.size()]);
          break;
        }
      }

      public Builder extend(NotificationCompat.Action.Extender paramExtender)
      {
        paramExtender.extend(this);
        return this;
      }

      public Bundle getExtras()
      {
        return this.mExtras;
      }

      public Builder setAllowGeneratedReplies(boolean paramBoolean)
      {
        this.mAllowGeneratedReplies = paramBoolean;
        return this;
      }
    }

    public static abstract interface Extender
    {
      public abstract NotificationCompat.Action.Builder extend(NotificationCompat.Action.Builder paramBuilder);
    }

    public static final class WearableExtender
      implements NotificationCompat.Action.Extender
    {
      private static final int DEFAULT_FLAGS = 1;
      private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
      private static final int FLAG_AVAILABLE_OFFLINE = 1;
      private static final int FLAG_HINT_DISPLAY_INLINE = 4;
      private static final int FLAG_HINT_LAUNCHES_ACTIVITY = 2;
      private static final String KEY_CANCEL_LABEL = "cancelLabel";
      private static final String KEY_CONFIRM_LABEL = "confirmLabel";
      private static final String KEY_FLAGS = "flags";
      private static final String KEY_IN_PROGRESS_LABEL = "inProgressLabel";
      private CharSequence mCancelLabel;
      private CharSequence mConfirmLabel;
      private int mFlags = 1;
      private CharSequence mInProgressLabel;

      public WearableExtender()
      {
      }

      public WearableExtender(NotificationCompat.Action paramAction)
      {
        Bundle localBundle = paramAction.getExtras().getBundle("android.wearable.EXTENSIONS");
        if (localBundle != null)
        {
          this.mFlags = localBundle.getInt("flags", 1);
          this.mInProgressLabel = localBundle.getCharSequence("inProgressLabel");
          this.mConfirmLabel = localBundle.getCharSequence("confirmLabel");
          this.mCancelLabel = localBundle.getCharSequence("cancelLabel");
        }
      }

      private void setFlag(int paramInt, boolean paramBoolean)
      {
        if (paramBoolean)
        {
          this.mFlags = (paramInt | this.mFlags);
          return;
        }
        this.mFlags &= (paramInt ^ 0xFFFFFFFF);
      }

      public WearableExtender clone()
      {
        WearableExtender localWearableExtender = new WearableExtender();
        localWearableExtender.mFlags = this.mFlags;
        localWearableExtender.mInProgressLabel = this.mInProgressLabel;
        localWearableExtender.mConfirmLabel = this.mConfirmLabel;
        localWearableExtender.mCancelLabel = this.mCancelLabel;
        return localWearableExtender;
      }

      public NotificationCompat.Action.Builder extend(NotificationCompat.Action.Builder paramBuilder)
      {
        Bundle localBundle = new Bundle();
        if (this.mFlags != 1)
          localBundle.putInt("flags", this.mFlags);
        if (this.mInProgressLabel != null)
          localBundle.putCharSequence("inProgressLabel", this.mInProgressLabel);
        if (this.mConfirmLabel != null)
          localBundle.putCharSequence("confirmLabel", this.mConfirmLabel);
        if (this.mCancelLabel != null)
          localBundle.putCharSequence("cancelLabel", this.mCancelLabel);
        paramBuilder.getExtras().putBundle("android.wearable.EXTENSIONS", localBundle);
        return paramBuilder;
      }

      public CharSequence getCancelLabel()
      {
        return this.mCancelLabel;
      }

      public CharSequence getConfirmLabel()
      {
        return this.mConfirmLabel;
      }

      public boolean getHintDisplayActionInline()
      {
        return (0x4 & this.mFlags) != 0;
      }

      public boolean getHintLaunchesActivity()
      {
        return (0x2 & this.mFlags) != 0;
      }

      public CharSequence getInProgressLabel()
      {
        return this.mInProgressLabel;
      }

      public boolean isAvailableOffline()
      {
        return (0x1 & this.mFlags) != 0;
      }

      public WearableExtender setAvailableOffline(boolean paramBoolean)
      {
        setFlag(1, paramBoolean);
        return this;
      }

      public WearableExtender setCancelLabel(CharSequence paramCharSequence)
      {
        this.mCancelLabel = paramCharSequence;
        return this;
      }

      public WearableExtender setConfirmLabel(CharSequence paramCharSequence)
      {
        this.mConfirmLabel = paramCharSequence;
        return this;
      }

      public WearableExtender setHintDisplayActionInline(boolean paramBoolean)
      {
        setFlag(4, paramBoolean);
        return this;
      }

      public WearableExtender setHintLaunchesActivity(boolean paramBoolean)
      {
        setFlag(2, paramBoolean);
        return this;
      }

      public WearableExtender setInProgressLabel(CharSequence paramCharSequence)
      {
        this.mInProgressLabel = paramCharSequence;
        return this;
      }
    }
  }

  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface BadgeIconType
  {
  }

  public static class BigPictureStyle extends NotificationCompat.Style
  {
    private Bitmap mBigLargeIcon;
    private boolean mBigLargeIconSet;
    private Bitmap mPicture;

    public BigPictureStyle()
    {
    }

    public BigPictureStyle(NotificationCompat.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      if (Build.VERSION.SDK_INT >= 16)
        NotificationCompatJellybean.addBigPictureStyle(paramNotificationBuilderWithBuilderAccessor, this.mBigContentTitle, this.mSummaryTextSet, this.mSummaryText, this.mPicture, this.mBigLargeIcon, this.mBigLargeIconSet);
    }

    public BigPictureStyle bigLargeIcon(Bitmap paramBitmap)
    {
      this.mBigLargeIcon = paramBitmap;
      this.mBigLargeIconSet = true;
      return this;
    }

    public BigPictureStyle bigPicture(Bitmap paramBitmap)
    {
      this.mPicture = paramBitmap;
      return this;
    }

    public BigPictureStyle setBigContentTitle(CharSequence paramCharSequence)
    {
      this.mBigContentTitle = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      return this;
    }

    public BigPictureStyle setSummaryText(CharSequence paramCharSequence)
    {
      this.mSummaryText = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      this.mSummaryTextSet = true;
      return this;
    }
  }

  public static class BigTextStyle extends NotificationCompat.Style
  {
    private CharSequence mBigText;

    public BigTextStyle()
    {
    }

    public BigTextStyle(NotificationCompat.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      if (Build.VERSION.SDK_INT >= 16)
        NotificationCompatJellybean.addBigTextStyle(paramNotificationBuilderWithBuilderAccessor, this.mBigContentTitle, this.mSummaryTextSet, this.mSummaryText, this.mBigText);
    }

    public BigTextStyle bigText(CharSequence paramCharSequence)
    {
      this.mBigText = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      return this;
    }

    public BigTextStyle setBigContentTitle(CharSequence paramCharSequence)
    {
      this.mBigContentTitle = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      return this;
    }

    public BigTextStyle setSummaryText(CharSequence paramCharSequence)
    {
      this.mSummaryText = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      this.mSummaryTextSet = true;
      return this;
    }
  }

  public static class Builder
  {
    private static final int MAX_CHARSEQUENCE_LENGTH = 5120;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public ArrayList<NotificationCompat.Action> mActions = new ArrayList();
    int mBadgeIcon = 0;
    RemoteViews mBigContentView;
    String mCategory;
    String mChannelId;
    int mColor = 0;
    boolean mColorized;
    boolean mColorizedSet;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public CharSequence mContentInfo;
    PendingIntent mContentIntent;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public CharSequence mContentText;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public CharSequence mContentTitle;
    RemoteViews mContentView;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public Context mContext;
    Bundle mExtras;
    PendingIntent mFullScreenIntent;
    private int mGroupAlertBehavior = 0;
    String mGroupKey;
    boolean mGroupSummary;
    RemoteViews mHeadsUpContentView;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public Bitmap mLargeIcon;
    boolean mLocalOnly = false;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public Notification mNotification = new Notification();

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public int mNumber;
    public ArrayList<String> mPeople;
    int mPriority;
    int mProgress;
    boolean mProgressIndeterminate;
    int mProgressMax;
    Notification mPublicVersion;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public CharSequence[] mRemoteInputHistory;
    String mShortcutId;
    boolean mShowWhen = true;
    String mSortKey;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public NotificationCompat.Style mStyle;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public CharSequence mSubText;
    RemoteViews mTickerView;
    long mTimeout;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public boolean mUseChronometer;
    int mVisibility = 0;

    @Deprecated
    public Builder(Context paramContext)
    {
      this(paramContext, null);
    }

    public Builder(@NonNull Context paramContext, @NonNull String paramString)
    {
      this.mContext = paramContext;
      this.mChannelId = paramString;
      this.mNotification.when = System.currentTimeMillis();
      this.mNotification.audioStreamType = -1;
      this.mPriority = 0;
      this.mPeople = new ArrayList();
    }

    protected static CharSequence limitCharSequenceLength(CharSequence paramCharSequence)
    {
      if (paramCharSequence == null);
      do
        return paramCharSequence;
      while (paramCharSequence.length() <= 5120);
      return paramCharSequence.subSequence(0, 5120);
    }

    private void setFlag(int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        Notification localNotification2 = this.mNotification;
        localNotification2.flags = (paramInt | localNotification2.flags);
        return;
      }
      Notification localNotification1 = this.mNotification;
      localNotification1.flags &= (paramInt ^ 0xFFFFFFFF);
    }

    public Builder addAction(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
    {
      this.mActions.add(new NotificationCompat.Action(paramInt, paramCharSequence, paramPendingIntent));
      return this;
    }

    public Builder addAction(NotificationCompat.Action paramAction)
    {
      this.mActions.add(paramAction);
      return this;
    }

    public Builder addExtras(Bundle paramBundle)
    {
      if (paramBundle != null)
      {
        if (this.mExtras == null)
          this.mExtras = new Bundle(paramBundle);
      }
      else
        return this;
      this.mExtras.putAll(paramBundle);
      return this;
    }

    public Builder addPerson(String paramString)
    {
      this.mPeople.add(paramString);
      return this;
    }

    public Notification build()
    {
      return NotificationCompat.IMPL.build(this, getExtender());
    }

    public Builder extend(NotificationCompat.Extender paramExtender)
    {
      paramExtender.extend(this);
      return this;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews getBigContentView()
    {
      return this.mBigContentView;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public int getColor()
    {
      return this.mColor;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews getContentView()
    {
      return this.mContentView;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    protected NotificationCompat.BuilderExtender getExtender()
    {
      return new NotificationCompat.BuilderExtender();
    }

    public Bundle getExtras()
    {
      if (this.mExtras == null)
        this.mExtras = new Bundle();
      return this.mExtras;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews getHeadsUpContentView()
    {
      return this.mHeadsUpContentView;
    }

    @Deprecated
    public Notification getNotification()
    {
      return build();
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public int getPriority()
    {
      return this.mPriority;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public long getWhenIfShowing()
    {
      if (this.mShowWhen)
        return this.mNotification.when;
      return 0L;
    }

    public Builder setAutoCancel(boolean paramBoolean)
    {
      setFlag(16, paramBoolean);
      return this;
    }

    public Builder setBadgeIconType(int paramInt)
    {
      this.mBadgeIcon = paramInt;
      return this;
    }

    public Builder setCategory(String paramString)
    {
      this.mCategory = paramString;
      return this;
    }

    public Builder setChannelId(@NonNull String paramString)
    {
      this.mChannelId = paramString;
      return this;
    }

    public Builder setColor(@ColorInt int paramInt)
    {
      this.mColor = paramInt;
      return this;
    }

    public Builder setColorized(boolean paramBoolean)
    {
      this.mColorized = paramBoolean;
      this.mColorizedSet = true;
      return this;
    }

    public Builder setContent(RemoteViews paramRemoteViews)
    {
      this.mNotification.contentView = paramRemoteViews;
      return this;
    }

    public Builder setContentInfo(CharSequence paramCharSequence)
    {
      this.mContentInfo = limitCharSequenceLength(paramCharSequence);
      return this;
    }

    public Builder setContentIntent(PendingIntent paramPendingIntent)
    {
      this.mContentIntent = paramPendingIntent;
      return this;
    }

    public Builder setContentText(CharSequence paramCharSequence)
    {
      this.mContentText = limitCharSequenceLength(paramCharSequence);
      return this;
    }

    public Builder setContentTitle(CharSequence paramCharSequence)
    {
      this.mContentTitle = limitCharSequenceLength(paramCharSequence);
      return this;
    }

    public Builder setCustomBigContentView(RemoteViews paramRemoteViews)
    {
      this.mBigContentView = paramRemoteViews;
      return this;
    }

    public Builder setCustomContentView(RemoteViews paramRemoteViews)
    {
      this.mContentView = paramRemoteViews;
      return this;
    }

    public Builder setCustomHeadsUpContentView(RemoteViews paramRemoteViews)
    {
      this.mHeadsUpContentView = paramRemoteViews;
      return this;
    }

    public Builder setDefaults(int paramInt)
    {
      this.mNotification.defaults = paramInt;
      if ((paramInt & 0x4) != 0)
      {
        Notification localNotification = this.mNotification;
        localNotification.flags = (0x1 | localNotification.flags);
      }
      return this;
    }

    public Builder setDeleteIntent(PendingIntent paramPendingIntent)
    {
      this.mNotification.deleteIntent = paramPendingIntent;
      return this;
    }

    public Builder setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
      return this;
    }

    public Builder setFullScreenIntent(PendingIntent paramPendingIntent, boolean paramBoolean)
    {
      this.mFullScreenIntent = paramPendingIntent;
      setFlag(128, paramBoolean);
      return this;
    }

    public Builder setGroup(String paramString)
    {
      this.mGroupKey = paramString;
      return this;
    }

    public Builder setGroupAlertBehavior(int paramInt)
    {
      this.mGroupAlertBehavior = paramInt;
      return this;
    }

    public Builder setGroupSummary(boolean paramBoolean)
    {
      this.mGroupSummary = paramBoolean;
      return this;
    }

    public Builder setLargeIcon(Bitmap paramBitmap)
    {
      this.mLargeIcon = paramBitmap;
      return this;
    }

    public Builder setLights(@ColorInt int paramInt1, int paramInt2, int paramInt3)
    {
      int i = 1;
      this.mNotification.ledARGB = paramInt1;
      this.mNotification.ledOnMS = paramInt2;
      this.mNotification.ledOffMS = paramInt3;
      int j;
      Notification localNotification;
      int k;
      if ((this.mNotification.ledOnMS != 0) && (this.mNotification.ledOffMS != 0))
      {
        j = i;
        localNotification = this.mNotification;
        k = 0xFFFFFFFE & this.mNotification.flags;
        if (j == 0)
          break label92;
      }
      while (true)
      {
        localNotification.flags = (i | k);
        return this;
        j = 0;
        break;
        label92: i = 0;
      }
    }

    public Builder setLocalOnly(boolean paramBoolean)
    {
      this.mLocalOnly = paramBoolean;
      return this;
    }

    public Builder setNumber(int paramInt)
    {
      this.mNumber = paramInt;
      return this;
    }

    public Builder setOngoing(boolean paramBoolean)
    {
      setFlag(2, paramBoolean);
      return this;
    }

    public Builder setOnlyAlertOnce(boolean paramBoolean)
    {
      setFlag(8, paramBoolean);
      return this;
    }

    public Builder setPriority(int paramInt)
    {
      this.mPriority = paramInt;
      return this;
    }

    public Builder setProgress(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.mProgressMax = paramInt1;
      this.mProgress = paramInt2;
      this.mProgressIndeterminate = paramBoolean;
      return this;
    }

    public Builder setPublicVersion(Notification paramNotification)
    {
      this.mPublicVersion = paramNotification;
      return this;
    }

    public Builder setRemoteInputHistory(CharSequence[] paramArrayOfCharSequence)
    {
      this.mRemoteInputHistory = paramArrayOfCharSequence;
      return this;
    }

    public Builder setShortcutId(String paramString)
    {
      this.mShortcutId = paramString;
      return this;
    }

    public Builder setShowWhen(boolean paramBoolean)
    {
      this.mShowWhen = paramBoolean;
      return this;
    }

    public Builder setSmallIcon(int paramInt)
    {
      this.mNotification.icon = paramInt;
      return this;
    }

    public Builder setSmallIcon(int paramInt1, int paramInt2)
    {
      this.mNotification.icon = paramInt1;
      this.mNotification.iconLevel = paramInt2;
      return this;
    }

    public Builder setSortKey(String paramString)
    {
      this.mSortKey = paramString;
      return this;
    }

    public Builder setSound(Uri paramUri)
    {
      this.mNotification.sound = paramUri;
      this.mNotification.audioStreamType = -1;
      return this;
    }

    public Builder setSound(Uri paramUri, int paramInt)
    {
      this.mNotification.sound = paramUri;
      this.mNotification.audioStreamType = paramInt;
      return this;
    }

    public Builder setStyle(NotificationCompat.Style paramStyle)
    {
      if (this.mStyle != paramStyle)
      {
        this.mStyle = paramStyle;
        if (this.mStyle != null)
          this.mStyle.setBuilder(this);
      }
      return this;
    }

    public Builder setSubText(CharSequence paramCharSequence)
    {
      this.mSubText = limitCharSequenceLength(paramCharSequence);
      return this;
    }

    public Builder setTicker(CharSequence paramCharSequence)
    {
      this.mNotification.tickerText = limitCharSequenceLength(paramCharSequence);
      return this;
    }

    public Builder setTicker(CharSequence paramCharSequence, RemoteViews paramRemoteViews)
    {
      this.mNotification.tickerText = limitCharSequenceLength(paramCharSequence);
      this.mTickerView = paramRemoteViews;
      return this;
    }

    public Builder setTimeoutAfter(long paramLong)
    {
      this.mTimeout = paramLong;
      return this;
    }

    public Builder setUsesChronometer(boolean paramBoolean)
    {
      this.mUseChronometer = paramBoolean;
      return this;
    }

    public Builder setVibrate(long[] paramArrayOfLong)
    {
      this.mNotification.vibrate = paramArrayOfLong;
      return this;
    }

    public Builder setVisibility(int paramInt)
    {
      this.mVisibility = paramInt;
      return this;
    }

    public Builder setWhen(long paramLong)
    {
      this.mNotification.when = paramLong;
      return this;
    }
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected static class BuilderExtender
  {
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      RemoteViews localRemoteViews1;
      Notification localNotification;
      if (paramBuilder.mStyle != null)
      {
        localRemoteViews1 = paramBuilder.mStyle.makeContentView(paramNotificationBuilderWithBuilderAccessor);
        localNotification = paramNotificationBuilderWithBuilderAccessor.build();
        if (localRemoteViews1 == null)
          break label116;
        localNotification.contentView = localRemoteViews1;
      }
      while (true)
      {
        if ((Build.VERSION.SDK_INT >= 16) && (paramBuilder.mStyle != null))
        {
          RemoteViews localRemoteViews3 = paramBuilder.mStyle.makeBigContentView(paramNotificationBuilderWithBuilderAccessor);
          if (localRemoteViews3 != null)
            localNotification.bigContentView = localRemoteViews3;
        }
        if ((Build.VERSION.SDK_INT >= 21) && (paramBuilder.mStyle != null))
        {
          RemoteViews localRemoteViews2 = paramBuilder.mStyle.makeHeadsUpContentView(paramNotificationBuilderWithBuilderAccessor);
          if (localRemoteViews2 != null)
            localNotification.headsUpContentView = localRemoteViews2;
        }
        return localNotification;
        localRemoteViews1 = null;
        break;
        label116: if (paramBuilder.mContentView == null)
          continue;
        localNotification.contentView = paramBuilder.mContentView;
      }
    }
  }

  public static final class CarExtender
    implements NotificationCompat.Extender
  {
    private static final String EXTRA_CAR_EXTENDER = "android.car.EXTENSIONS";
    private static final String EXTRA_COLOR = "app_color";
    private static final String EXTRA_CONVERSATION = "car_conversation";
    private static final String EXTRA_LARGE_ICON = "large_icon";
    private static final String TAG = "CarExtender";
    private int mColor = 0;
    private Bitmap mLargeIcon;
    private UnreadConversation mUnreadConversation;

    public CarExtender()
    {
    }

    public CarExtender(Notification paramNotification)
    {
      if (Build.VERSION.SDK_INT < 21);
      while (true)
      {
        return;
        if (NotificationCompat.getExtras(paramNotification) == null);
        for (Bundle localBundle1 = null; localBundle1 != null; localBundle1 = NotificationCompat.getExtras(paramNotification).getBundle("android.car.EXTENSIONS"))
        {
          this.mLargeIcon = ((Bitmap)localBundle1.getParcelable("large_icon"));
          this.mColor = localBundle1.getInt("app_color", 0);
          Bundle localBundle2 = localBundle1.getBundle("car_conversation");
          this.mUnreadConversation = ((UnreadConversation)NotificationCompat.IMPL.getUnreadConversationFromBundle(localBundle2, UnreadConversation.FACTORY, RemoteInput.FACTORY));
          return;
        }
      }
    }

    public NotificationCompat.Builder extend(NotificationCompat.Builder paramBuilder)
    {
      if (Build.VERSION.SDK_INT < 21)
        return paramBuilder;
      Bundle localBundle = new Bundle();
      if (this.mLargeIcon != null)
        localBundle.putParcelable("large_icon", this.mLargeIcon);
      if (this.mColor != 0)
        localBundle.putInt("app_color", this.mColor);
      if (this.mUnreadConversation != null)
        localBundle.putBundle("car_conversation", NotificationCompat.IMPL.getBundleForUnreadConversation(this.mUnreadConversation));
      paramBuilder.getExtras().putBundle("android.car.EXTENSIONS", localBundle);
      return paramBuilder;
    }

    @ColorInt
    public int getColor()
    {
      return this.mColor;
    }

    public Bitmap getLargeIcon()
    {
      return this.mLargeIcon;
    }

    public UnreadConversation getUnreadConversation()
    {
      return this.mUnreadConversation;
    }

    public CarExtender setColor(@ColorInt int paramInt)
    {
      this.mColor = paramInt;
      return this;
    }

    public CarExtender setLargeIcon(Bitmap paramBitmap)
    {
      this.mLargeIcon = paramBitmap;
      return this;
    }

    public CarExtender setUnreadConversation(UnreadConversation paramUnreadConversation)
    {
      this.mUnreadConversation = paramUnreadConversation;
      return this;
    }

    public static class UnreadConversation extends NotificationCompatBase.UnreadConversation
    {
      static final NotificationCompatBase.UnreadConversation.Factory FACTORY = new NotificationCompatBase.UnreadConversation.Factory()
      {
        public NotificationCompat.CarExtender.UnreadConversation build(String[] paramArrayOfString1, RemoteInputCompatBase.RemoteInput paramRemoteInput, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, String[] paramArrayOfString2, long paramLong)
        {
          return new NotificationCompat.CarExtender.UnreadConversation(paramArrayOfString1, (RemoteInput)paramRemoteInput, paramPendingIntent1, paramPendingIntent2, paramArrayOfString2, paramLong);
        }
      };
      private final long mLatestTimestamp;
      private final String[] mMessages;
      private final String[] mParticipants;
      private final PendingIntent mReadPendingIntent;
      private final RemoteInput mRemoteInput;
      private final PendingIntent mReplyPendingIntent;

      UnreadConversation(String[] paramArrayOfString1, RemoteInput paramRemoteInput, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, String[] paramArrayOfString2, long paramLong)
      {
        this.mMessages = paramArrayOfString1;
        this.mRemoteInput = paramRemoteInput;
        this.mReadPendingIntent = paramPendingIntent2;
        this.mReplyPendingIntent = paramPendingIntent1;
        this.mParticipants = paramArrayOfString2;
        this.mLatestTimestamp = paramLong;
      }

      public long getLatestTimestamp()
      {
        return this.mLatestTimestamp;
      }

      public String[] getMessages()
      {
        return this.mMessages;
      }

      public String getParticipant()
      {
        if (this.mParticipants.length > 0)
          return this.mParticipants[0];
        return null;
      }

      public String[] getParticipants()
      {
        return this.mParticipants;
      }

      public PendingIntent getReadPendingIntent()
      {
        return this.mReadPendingIntent;
      }

      public RemoteInput getRemoteInput()
      {
        return this.mRemoteInput;
      }

      public PendingIntent getReplyPendingIntent()
      {
        return this.mReplyPendingIntent;
      }

      public static class Builder
      {
        private long mLatestTimestamp;
        private final List<String> mMessages = new ArrayList();
        private final String mParticipant;
        private PendingIntent mReadPendingIntent;
        private RemoteInput mRemoteInput;
        private PendingIntent mReplyPendingIntent;

        public Builder(String paramString)
        {
          this.mParticipant = paramString;
        }

        public Builder addMessage(String paramString)
        {
          this.mMessages.add(paramString);
          return this;
        }

        public NotificationCompat.CarExtender.UnreadConversation build()
        {
          String[] arrayOfString1 = (String[])this.mMessages.toArray(new String[this.mMessages.size()]);
          String[] arrayOfString2 = new String[1];
          arrayOfString2[0] = this.mParticipant;
          return new NotificationCompat.CarExtender.UnreadConversation(arrayOfString1, this.mRemoteInput, this.mReplyPendingIntent, this.mReadPendingIntent, arrayOfString2, this.mLatestTimestamp);
        }

        public Builder setLatestTimestamp(long paramLong)
        {
          this.mLatestTimestamp = paramLong;
          return this;
        }

        public Builder setReadPendingIntent(PendingIntent paramPendingIntent)
        {
          this.mReadPendingIntent = paramPendingIntent;
          return this;
        }

        public Builder setReplyAction(PendingIntent paramPendingIntent, RemoteInput paramRemoteInput)
        {
          this.mRemoteInput = paramRemoteInput;
          this.mReplyPendingIntent = paramPendingIntent;
          return this;
        }
      }
    }
  }

  public static class DecoratedCustomViewStyle extends NotificationCompat.Style
  {
    private static final int MAX_ACTION_BUTTONS = 3;

    private RemoteViews createRemoteViews(RemoteViews paramRemoteViews, boolean paramBoolean)
    {
      RemoteViews localRemoteViews1 = applyStandardTemplate(true, R.layout.notification_template_custom_big, false);
      localRemoteViews1.removeAllViews(R.id.actions);
      int i = 0;
      if (paramBoolean)
      {
        ArrayList localArrayList = this.mBuilder.mActions;
        i = 0;
        if (localArrayList != null)
        {
          int k = Math.min(this.mBuilder.mActions.size(), 3);
          i = 0;
          if (k > 0)
          {
            i = 1;
            for (int m = 0; m < k; m++)
            {
              RemoteViews localRemoteViews2 = generateActionButton((NotificationCompat.Action)this.mBuilder.mActions.get(m));
              localRemoteViews1.addView(R.id.actions, localRemoteViews2);
            }
          }
        }
      }
      int j = 0;
      if (i != 0);
      while (true)
      {
        localRemoteViews1.setViewVisibility(R.id.actions, j);
        localRemoteViews1.setViewVisibility(R.id.action_divider, j);
        buildIntoRemoteViews(localRemoteViews1, paramRemoteViews);
        return localRemoteViews1;
        j = 8;
      }
    }

    private RemoteViews generateActionButton(NotificationCompat.Action paramAction)
    {
      int i;
      String str;
      if (paramAction.actionIntent == null)
      {
        i = 1;
        str = this.mBuilder.mContext.getPackageName();
        if (i == 0)
          break label129;
      }
      label129: for (int j = R.layout.notification_action_tombstone; ; j = R.layout.notification_action)
      {
        RemoteViews localRemoteViews = new RemoteViews(str, j);
        localRemoteViews.setImageViewBitmap(R.id.action_image, createColoredBitmap(paramAction.getIcon(), this.mBuilder.mContext.getResources().getColor(R.color.notification_action_color_filter)));
        localRemoteViews.setTextViewText(R.id.action_text, paramAction.title);
        if (i == 0)
          localRemoteViews.setOnClickPendingIntent(R.id.action_container, paramAction.actionIntent);
        if (Build.VERSION.SDK_INT >= 15)
          localRemoteViews.setContentDescription(R.id.action_container, paramAction.title);
        return localRemoteViews;
        i = 0;
        break;
      }
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      if (Build.VERSION.SDK_INT >= 24)
        paramNotificationBuilderWithBuilderAccessor.getBuilder().setStyle(new Notification.DecoratedCustomViewStyle());
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      if (Build.VERSION.SDK_INT >= 24);
      while (true)
      {
        return null;
        RemoteViews localRemoteViews1 = this.mBuilder.getBigContentView();
        if (localRemoteViews1 != null);
        for (RemoteViews localRemoteViews2 = localRemoteViews1; localRemoteViews2 != null; localRemoteViews2 = this.mBuilder.getContentView())
          return createRemoteViews(localRemoteViews2, true);
      }
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      if (Build.VERSION.SDK_INT >= 24);
      do
        return null;
      while (this.mBuilder.getContentView() == null);
      return createRemoteViews(this.mBuilder.getContentView(), false);
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeHeadsUpContentView(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      if (Build.VERSION.SDK_INT >= 24);
      while (true)
      {
        return null;
        RemoteViews localRemoteViews1 = this.mBuilder.getHeadsUpContentView();
        if (localRemoteViews1 != null);
        for (RemoteViews localRemoteViews2 = localRemoteViews1; localRemoteViews1 != null; localRemoteViews2 = this.mBuilder.getContentView())
          return createRemoteViews(localRemoteViews2, true);
      }
    }
  }

  public static abstract interface Extender
  {
    public abstract NotificationCompat.Builder extend(NotificationCompat.Builder paramBuilder);
  }

  public static class InboxStyle extends NotificationCompat.Style
  {
    private ArrayList<CharSequence> mTexts = new ArrayList();

    public InboxStyle()
    {
    }

    public InboxStyle(NotificationCompat.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }

    public InboxStyle addLine(CharSequence paramCharSequence)
    {
      this.mTexts.add(NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence));
      return this;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      if (Build.VERSION.SDK_INT >= 16)
        NotificationCompatJellybean.addInboxStyle(paramNotificationBuilderWithBuilderAccessor, this.mBigContentTitle, this.mSummaryTextSet, this.mSummaryText, this.mTexts);
    }

    public InboxStyle setBigContentTitle(CharSequence paramCharSequence)
    {
      this.mBigContentTitle = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      return this;
    }

    public InboxStyle setSummaryText(CharSequence paramCharSequence)
    {
      this.mSummaryText = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      this.mSummaryTextSet = true;
      return this;
    }
  }

  public static class MessagingStyle extends NotificationCompat.Style
  {
    public static final int MAXIMUM_RETAINED_MESSAGES = 25;
    CharSequence mConversationTitle;
    List<Message> mMessages = new ArrayList();
    CharSequence mUserDisplayName;

    MessagingStyle()
    {
    }

    public MessagingStyle(@NonNull CharSequence paramCharSequence)
    {
      this.mUserDisplayName = paramCharSequence;
    }

    public static MessagingStyle extractMessagingStyleFromNotification(Notification paramNotification)
    {
      Bundle localBundle = NotificationCompat.getExtras(paramNotification);
      if ((localBundle != null) && (!localBundle.containsKey("android.selfDisplayName")))
        return null;
      try
      {
        MessagingStyle localMessagingStyle = new MessagingStyle();
        localMessagingStyle.restoreFromCompatExtras(localBundle);
        return localMessagingStyle;
      }
      catch (ClassCastException localClassCastException)
      {
      }
      return null;
    }

    @Nullable
    private Message findLatestIncomingMessage()
    {
      for (int i = -1 + this.mMessages.size(); i >= 0; i--)
      {
        Message localMessage = (Message)this.mMessages.get(i);
        if (!TextUtils.isEmpty(localMessage.getSender()))
          return localMessage;
      }
      if (!this.mMessages.isEmpty())
        return (Message)this.mMessages.get(-1 + this.mMessages.size());
      return null;
    }

    private boolean hasMessagesWithoutSender()
    {
      for (int i = -1 + this.mMessages.size(); i >= 0; i--)
        if (((Message)this.mMessages.get(i)).getSender() == null)
          return true;
      return false;
    }

    @NonNull
    private TextAppearanceSpan makeFontColorSpan(int paramInt)
    {
      return new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(paramInt), null);
    }

    private CharSequence makeMessageLine(Message paramMessage)
    {
      BidiFormatter localBidiFormatter = BidiFormatter.getInstance();
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
      int i;
      int j;
      label32: Object localObject1;
      if (Build.VERSION.SDK_INT >= 21)
      {
        i = 1;
        if (i == 0)
          break label161;
        j = -16777216;
        localObject1 = paramMessage.getSender();
        if (TextUtils.isEmpty(paramMessage.getSender()))
        {
          if (this.mUserDisplayName != null)
            break label167;
          localObject1 = "";
          label59: if ((i != 0) && (this.mBuilder.getColor() != 0))
            j = this.mBuilder.getColor();
        }
        CharSequence localCharSequence = localBidiFormatter.unicodeWrap((CharSequence)localObject1);
        localSpannableStringBuilder.append(localCharSequence);
        localSpannableStringBuilder.setSpan(makeFontColorSpan(j), localSpannableStringBuilder.length() - localCharSequence.length(), localSpannableStringBuilder.length(), 33);
        if (paramMessage.getText() != null)
          break label176;
      }
      label161: label167: label176: for (Object localObject2 = ""; ; localObject2 = paramMessage.getText())
      {
        localSpannableStringBuilder.append("  ").append(localBidiFormatter.unicodeWrap((CharSequence)localObject2));
        return localSpannableStringBuilder;
        i = 0;
        break;
        j = -1;
        break label32;
        localObject1 = this.mUserDisplayName;
        break label59;
      }
    }

    public void addCompatExtras(Bundle paramBundle)
    {
      super.addCompatExtras(paramBundle);
      if (this.mUserDisplayName != null)
        paramBundle.putCharSequence("android.selfDisplayName", this.mUserDisplayName);
      if (this.mConversationTitle != null)
        paramBundle.putCharSequence("android.conversationTitle", this.mConversationTitle);
      if (!this.mMessages.isEmpty())
        paramBundle.putParcelableArray("android.messages", Message.getBundleArrayForMessages(this.mMessages));
    }

    public MessagingStyle addMessage(Message paramMessage)
    {
      this.mMessages.add(paramMessage);
      if (this.mMessages.size() > 25)
        this.mMessages.remove(0);
      return this;
    }

    public MessagingStyle addMessage(CharSequence paramCharSequence1, long paramLong, CharSequence paramCharSequence2)
    {
      this.mMessages.add(new Message(paramCharSequence1, paramLong, paramCharSequence2));
      if (this.mMessages.size() > 25)
        this.mMessages.remove(0);
      return this;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      if (Build.VERSION.SDK_INT >= 24)
      {
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        ArrayList localArrayList3 = new ArrayList();
        ArrayList localArrayList4 = new ArrayList();
        ArrayList localArrayList5 = new ArrayList();
        Iterator localIterator = this.mMessages.iterator();
        while (localIterator.hasNext())
        {
          Message localMessage1 = (Message)localIterator.next();
          localArrayList1.add(localMessage1.getText());
          localArrayList2.add(Long.valueOf(localMessage1.getTimestamp()));
          localArrayList3.add(localMessage1.getSender());
          localArrayList4.add(localMessage1.getDataMimeType());
          localArrayList5.add(localMessage1.getDataUri());
        }
        NotificationCompatApi24.addMessagingStyle(paramNotificationBuilderWithBuilderAccessor, this.mUserDisplayName, this.mConversationTitle, localArrayList1, localArrayList2, localArrayList3, localArrayList4, localArrayList5);
      }
      Message localMessage2;
      CharSequence localCharSequence2;
      label229: SpannableStringBuilder localSpannableStringBuilder;
      int i;
      label271: int j;
      label284: Message localMessage3;
      while (true)
      {
        return;
        localMessage2 = findLatestIncomingMessage();
        if (this.mConversationTitle == null)
          break;
        paramNotificationBuilderWithBuilderAccessor.getBuilder().setContentTitle(this.mConversationTitle);
        if (localMessage2 != null)
        {
          Notification.Builder localBuilder = paramNotificationBuilderWithBuilderAccessor.getBuilder();
          if (this.mConversationTitle == null)
            break label381;
          localCharSequence2 = makeMessageLine(localMessage2);
          localBuilder.setContentText(localCharSequence2);
        }
        if (Build.VERSION.SDK_INT < 16)
          continue;
        localSpannableStringBuilder = new SpannableStringBuilder();
        if ((this.mConversationTitle == null) && (!hasMessagesWithoutSender()))
          break label391;
        i = 1;
        j = -1 + this.mMessages.size();
        if (j < 0)
          break label407;
        localMessage3 = (Message)this.mMessages.get(j);
        if (i == 0)
          break label397;
      }
      label391: label397: for (CharSequence localCharSequence1 = makeMessageLine(localMessage3); ; localCharSequence1 = localMessage3.getText())
      {
        if (j != -1 + this.mMessages.size())
          localSpannableStringBuilder.insert(0, "\n");
        localSpannableStringBuilder.insert(0, localCharSequence1);
        j--;
        break label284;
        if (localMessage2 == null)
          break;
        paramNotificationBuilderWithBuilderAccessor.getBuilder().setContentTitle(localMessage2.getSender());
        break;
        label381: localCharSequence2 = localMessage2.getText();
        break label229;
        i = 0;
        break label271;
      }
      label407: NotificationCompatJellybean.addBigTextStyle(paramNotificationBuilderWithBuilderAccessor, null, false, null, localSpannableStringBuilder);
    }

    public CharSequence getConversationTitle()
    {
      return this.mConversationTitle;
    }

    public List<Message> getMessages()
    {
      return this.mMessages;
    }

    public CharSequence getUserDisplayName()
    {
      return this.mUserDisplayName;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    protected void restoreFromCompatExtras(Bundle paramBundle)
    {
      this.mMessages.clear();
      this.mUserDisplayName = paramBundle.getString("android.selfDisplayName");
      this.mConversationTitle = paramBundle.getString("android.conversationTitle");
      Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray("android.messages");
      if (arrayOfParcelable != null)
        this.mMessages = Message.getMessagesFromBundleArray(arrayOfParcelable);
    }

    public MessagingStyle setConversationTitle(CharSequence paramCharSequence)
    {
      this.mConversationTitle = paramCharSequence;
      return this;
    }

    public static final class Message
    {
      static final String KEY_DATA_MIME_TYPE = "type";
      static final String KEY_DATA_URI = "uri";
      static final String KEY_EXTRAS_BUNDLE = "extras";
      static final String KEY_SENDER = "sender";
      static final String KEY_TEXT = "text";
      static final String KEY_TIMESTAMP = "time";
      private String mDataMimeType;
      private Uri mDataUri;
      private Bundle mExtras = new Bundle();
      private final CharSequence mSender;
      private final CharSequence mText;
      private final long mTimestamp;

      public Message(CharSequence paramCharSequence1, long paramLong, CharSequence paramCharSequence2)
      {
        this.mText = paramCharSequence1;
        this.mTimestamp = paramLong;
        this.mSender = paramCharSequence2;
      }

      static Bundle[] getBundleArrayForMessages(List<Message> paramList)
      {
        Bundle[] arrayOfBundle = new Bundle[paramList.size()];
        int i = paramList.size();
        for (int j = 0; j < i; j++)
          arrayOfBundle[j] = ((Message)paramList.get(j)).toBundle();
        return arrayOfBundle;
      }

      static Message getMessageFromBundle(Bundle paramBundle)
      {
        try
        {
          if ((paramBundle.containsKey("text")) && (paramBundle.containsKey("time")))
          {
            localMessage = new Message(paramBundle.getCharSequence("text"), paramBundle.getLong("time"), paramBundle.getCharSequence("sender"));
            if ((paramBundle.containsKey("type")) && (paramBundle.containsKey("uri")))
              localMessage.setData(paramBundle.getString("type"), (Uri)paramBundle.getParcelable("uri"));
            if (!paramBundle.containsKey("extras"))
              break label114;
            localMessage.getExtras().putAll(paramBundle.getBundle("extras"));
            return localMessage;
          }
        }
        catch (ClassCastException localClassCastException)
        {
          return null;
        }
        Message localMessage = null;
        label114: return localMessage;
      }

      static List<Message> getMessagesFromBundleArray(Parcelable[] paramArrayOfParcelable)
      {
        ArrayList localArrayList = new ArrayList(paramArrayOfParcelable.length);
        for (int i = 0; i < paramArrayOfParcelable.length; i++)
        {
          if (!(paramArrayOfParcelable[i] instanceof Bundle))
            continue;
          Message localMessage = getMessageFromBundle((Bundle)paramArrayOfParcelable[i]);
          if (localMessage == null)
            continue;
          localArrayList.add(localMessage);
        }
        return localArrayList;
      }

      private Bundle toBundle()
      {
        Bundle localBundle = new Bundle();
        if (this.mText != null)
          localBundle.putCharSequence("text", this.mText);
        localBundle.putLong("time", this.mTimestamp);
        if (this.mSender != null)
          localBundle.putCharSequence("sender", this.mSender);
        if (this.mDataMimeType != null)
          localBundle.putString("type", this.mDataMimeType);
        if (this.mDataUri != null)
          localBundle.putParcelable("uri", this.mDataUri);
        if (this.mExtras != null)
          localBundle.putBundle("extras", this.mExtras);
        return localBundle;
      }

      public String getDataMimeType()
      {
        return this.mDataMimeType;
      }

      public Uri getDataUri()
      {
        return this.mDataUri;
      }

      public Bundle getExtras()
      {
        return this.mExtras;
      }

      public CharSequence getSender()
      {
        return this.mSender;
      }

      public CharSequence getText()
      {
        return this.mText;
      }

      public long getTimestamp()
      {
        return this.mTimestamp;
      }

      public Message setData(String paramString, Uri paramUri)
      {
        this.mDataMimeType = paramString;
        this.mDataUri = paramUri;
        return this;
      }
    }
  }

  @RequiresApi(16)
  static class NotificationCompatApi16Impl extends NotificationCompat.NotificationCompatBaseImpl
  {
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatJellybean.Builder localBuilder = new NotificationCompatJellybean.Builder(paramBuilder.mContext, paramBuilder.mNotification, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mTickerView, paramBuilder.mNumber, paramBuilder.mContentIntent, paramBuilder.mFullScreenIntent, paramBuilder.mLargeIcon, paramBuilder.mProgressMax, paramBuilder.mProgress, paramBuilder.mProgressIndeterminate, paramBuilder.mUseChronometer, paramBuilder.mPriority, paramBuilder.mSubText, paramBuilder.mLocalOnly, paramBuilder.mExtras, paramBuilder.mGroupKey, paramBuilder.mGroupSummary, paramBuilder.mSortKey, paramBuilder.mContentView, paramBuilder.mBigContentView);
      NotificationCompat.addActionsToBuilder(localBuilder, paramBuilder.mActions);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.apply(localBuilder);
      Notification localNotification = paramBuilderExtender.build(paramBuilder, localBuilder);
      if (paramBuilder.mStyle != null)
      {
        Bundle localBundle = NotificationCompat.getExtras(localNotification);
        if (localBundle != null)
          paramBuilder.mStyle.addCompatExtras(localBundle);
      }
      return localNotification;
    }

    public NotificationCompat.Action getAction(Notification paramNotification, int paramInt)
    {
      return (NotificationCompat.Action)NotificationCompatJellybean.getAction(paramNotification, paramInt, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }

    public NotificationCompat.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList)
    {
      return (NotificationCompat.Action[])(NotificationCompat.Action[])NotificationCompatJellybean.getActionsFromParcelableArrayList(paramArrayList, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }

    public ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompat.Action[] paramArrayOfAction)
    {
      return NotificationCompatJellybean.getParcelableArrayListForActions(paramArrayOfAction);
    }
  }

  @RequiresApi(19)
  static class NotificationCompatApi19Impl extends NotificationCompat.NotificationCompatApi16Impl
  {
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatKitKat.Builder localBuilder = new NotificationCompatKitKat.Builder(paramBuilder.mContext, paramBuilder.mNotification, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mTickerView, paramBuilder.mNumber, paramBuilder.mContentIntent, paramBuilder.mFullScreenIntent, paramBuilder.mLargeIcon, paramBuilder.mProgressMax, paramBuilder.mProgress, paramBuilder.mProgressIndeterminate, paramBuilder.mShowWhen, paramBuilder.mUseChronometer, paramBuilder.mPriority, paramBuilder.mSubText, paramBuilder.mLocalOnly, paramBuilder.mPeople, paramBuilder.mExtras, paramBuilder.mGroupKey, paramBuilder.mGroupSummary, paramBuilder.mSortKey, paramBuilder.mContentView, paramBuilder.mBigContentView);
      NotificationCompat.addActionsToBuilder(localBuilder, paramBuilder.mActions);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.apply(localBuilder);
      return paramBuilderExtender.build(paramBuilder, localBuilder);
    }

    public NotificationCompat.Action getAction(Notification paramNotification, int paramInt)
    {
      return (NotificationCompat.Action)NotificationCompatKitKat.getAction(paramNotification, paramInt, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }
  }

  @RequiresApi(20)
  static class NotificationCompatApi20Impl extends NotificationCompat.NotificationCompatApi19Impl
  {
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatApi20.Builder localBuilder = new NotificationCompatApi20.Builder(paramBuilder.mContext, paramBuilder.mNotification, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mTickerView, paramBuilder.mNumber, paramBuilder.mContentIntent, paramBuilder.mFullScreenIntent, paramBuilder.mLargeIcon, paramBuilder.mProgressMax, paramBuilder.mProgress, paramBuilder.mProgressIndeterminate, paramBuilder.mShowWhen, paramBuilder.mUseChronometer, paramBuilder.mPriority, paramBuilder.mSubText, paramBuilder.mLocalOnly, paramBuilder.mPeople, paramBuilder.mExtras, paramBuilder.mGroupKey, paramBuilder.mGroupSummary, paramBuilder.mSortKey, paramBuilder.mContentView, paramBuilder.mBigContentView, paramBuilder.mGroupAlertBehavior);
      NotificationCompat.addActionsToBuilder(localBuilder, paramBuilder.mActions);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.apply(localBuilder);
      Notification localNotification = paramBuilderExtender.build(paramBuilder, localBuilder);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.addCompatExtras(NotificationCompat.getExtras(localNotification));
      return localNotification;
    }

    public NotificationCompat.Action getAction(Notification paramNotification, int paramInt)
    {
      return (NotificationCompat.Action)NotificationCompatApi20.getAction(paramNotification, paramInt, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }

    public NotificationCompat.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList)
    {
      return (NotificationCompat.Action[])(NotificationCompat.Action[])NotificationCompatApi20.getActionsFromParcelableArrayList(paramArrayList, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }

    public ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompat.Action[] paramArrayOfAction)
    {
      return NotificationCompatApi20.getParcelableArrayListForActions(paramArrayOfAction);
    }
  }

  @RequiresApi(21)
  static class NotificationCompatApi21Impl extends NotificationCompat.NotificationCompatApi20Impl
  {
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatApi21.Builder localBuilder = new NotificationCompatApi21.Builder(paramBuilder.mContext, paramBuilder.mNotification, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mTickerView, paramBuilder.mNumber, paramBuilder.mContentIntent, paramBuilder.mFullScreenIntent, paramBuilder.mLargeIcon, paramBuilder.mProgressMax, paramBuilder.mProgress, paramBuilder.mProgressIndeterminate, paramBuilder.mShowWhen, paramBuilder.mUseChronometer, paramBuilder.mPriority, paramBuilder.mSubText, paramBuilder.mLocalOnly, paramBuilder.mCategory, paramBuilder.mPeople, paramBuilder.mExtras, paramBuilder.mColor, paramBuilder.mVisibility, paramBuilder.mPublicVersion, paramBuilder.mGroupKey, paramBuilder.mGroupSummary, paramBuilder.mSortKey, paramBuilder.mContentView, paramBuilder.mBigContentView, paramBuilder.mHeadsUpContentView, paramBuilder.mGroupAlertBehavior);
      NotificationCompat.addActionsToBuilder(localBuilder, paramBuilder.mActions);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.apply(localBuilder);
      Notification localNotification = paramBuilderExtender.build(paramBuilder, localBuilder);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.addCompatExtras(NotificationCompat.getExtras(localNotification));
      return localNotification;
    }

    public Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation paramUnreadConversation)
    {
      return NotificationCompatApi21.getBundleForUnreadConversation(paramUnreadConversation);
    }

    public NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle, NotificationCompatBase.UnreadConversation.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
    {
      return NotificationCompatApi21.getUnreadConversationFromBundle(paramBundle, paramFactory, paramFactory1);
    }
  }

  @RequiresApi(24)
  static class NotificationCompatApi24Impl extends NotificationCompat.NotificationCompatApi21Impl
  {
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatApi24.Builder localBuilder = new NotificationCompatApi24.Builder(paramBuilder.mContext, paramBuilder.mNotification, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mTickerView, paramBuilder.mNumber, paramBuilder.mContentIntent, paramBuilder.mFullScreenIntent, paramBuilder.mLargeIcon, paramBuilder.mProgressMax, paramBuilder.mProgress, paramBuilder.mProgressIndeterminate, paramBuilder.mShowWhen, paramBuilder.mUseChronometer, paramBuilder.mPriority, paramBuilder.mSubText, paramBuilder.mLocalOnly, paramBuilder.mCategory, paramBuilder.mPeople, paramBuilder.mExtras, paramBuilder.mColor, paramBuilder.mVisibility, paramBuilder.mPublicVersion, paramBuilder.mGroupKey, paramBuilder.mGroupSummary, paramBuilder.mSortKey, paramBuilder.mRemoteInputHistory, paramBuilder.mContentView, paramBuilder.mBigContentView, paramBuilder.mHeadsUpContentView, paramBuilder.mGroupAlertBehavior);
      NotificationCompat.addActionsToBuilder(localBuilder, paramBuilder.mActions);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.apply(localBuilder);
      Notification localNotification = paramBuilderExtender.build(paramBuilder, localBuilder);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.addCompatExtras(NotificationCompat.getExtras(localNotification));
      return localNotification;
    }

    public NotificationCompat.Action getAction(Notification paramNotification, int paramInt)
    {
      return (NotificationCompat.Action)NotificationCompatApi24.getAction(paramNotification, paramInt, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }

    public NotificationCompat.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList)
    {
      return (NotificationCompat.Action[])(NotificationCompat.Action[])NotificationCompatApi24.getActionsFromParcelableArrayList(paramArrayList, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }

    public ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompat.Action[] paramArrayOfAction)
    {
      return NotificationCompatApi24.getParcelableArrayListForActions(paramArrayOfAction);
    }
  }

  @RequiresApi(26)
  static class NotificationCompatApi26Impl extends NotificationCompat.NotificationCompatApi24Impl
  {
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatApi26.Builder localBuilder = new NotificationCompatApi26.Builder(paramBuilder.mContext, paramBuilder.mNotification, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mTickerView, paramBuilder.mNumber, paramBuilder.mContentIntent, paramBuilder.mFullScreenIntent, paramBuilder.mLargeIcon, paramBuilder.mProgressMax, paramBuilder.mProgress, paramBuilder.mProgressIndeterminate, paramBuilder.mShowWhen, paramBuilder.mUseChronometer, paramBuilder.mPriority, paramBuilder.mSubText, paramBuilder.mLocalOnly, paramBuilder.mCategory, paramBuilder.mPeople, paramBuilder.mExtras, paramBuilder.mColor, paramBuilder.mVisibility, paramBuilder.mPublicVersion, paramBuilder.mGroupKey, paramBuilder.mGroupSummary, paramBuilder.mSortKey, paramBuilder.mRemoteInputHistory, paramBuilder.mContentView, paramBuilder.mBigContentView, paramBuilder.mHeadsUpContentView, paramBuilder.mChannelId, paramBuilder.mBadgeIcon, paramBuilder.mShortcutId, paramBuilder.mTimeout, paramBuilder.mColorized, paramBuilder.mColorizedSet, paramBuilder.mGroupAlertBehavior);
      NotificationCompat.addActionsToBuilder(localBuilder, paramBuilder.mActions);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.apply(localBuilder);
      Notification localNotification = paramBuilderExtender.build(paramBuilder, localBuilder);
      if (paramBuilder.mStyle != null)
        paramBuilder.mStyle.addCompatExtras(NotificationCompat.getExtras(localNotification));
      return localNotification;
    }
  }

  static class NotificationCompatBaseImpl
    implements NotificationCompat.NotificationCompatImpl
  {
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      return paramBuilderExtender.build(paramBuilder, new BuilderBase(paramBuilder.mContext, paramBuilder.mNotification, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mTickerView, paramBuilder.mNumber, paramBuilder.mContentIntent, paramBuilder.mFullScreenIntent, paramBuilder.mLargeIcon, paramBuilder.mProgressMax, paramBuilder.mProgress, paramBuilder.mProgressIndeterminate));
    }

    public NotificationCompat.Action getAction(Notification paramNotification, int paramInt)
    {
      return null;
    }

    public NotificationCompat.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList)
    {
      return null;
    }

    public Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation paramUnreadConversation)
    {
      return null;
    }

    public ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompat.Action[] paramArrayOfAction)
    {
      return null;
    }

    public NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle, NotificationCompatBase.UnreadConversation.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
    {
      return null;
    }

    public static class BuilderBase
      implements NotificationBuilderWithBuilderAccessor
    {
      private Notification.Builder mBuilder;

      BuilderBase(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean)
      {
        Notification.Builder localBuilder1 = new Notification.Builder(paramContext).setWhen(paramNotification.when).setSmallIcon(paramNotification.icon, paramNotification.iconLevel).setContent(paramNotification.contentView).setTicker(paramNotification.tickerText, paramRemoteViews).setSound(paramNotification.sound, paramNotification.audioStreamType).setVibrate(paramNotification.vibrate).setLights(paramNotification.ledARGB, paramNotification.ledOnMS, paramNotification.ledOffMS);
        boolean bool1;
        boolean bool2;
        label115: boolean bool3;
        label137: Notification.Builder localBuilder4;
        if ((0x2 & paramNotification.flags) != 0)
        {
          bool1 = true;
          Notification.Builder localBuilder2 = localBuilder1.setOngoing(bool1);
          if ((0x8 & paramNotification.flags) == 0)
            break label232;
          bool2 = true;
          Notification.Builder localBuilder3 = localBuilder2.setOnlyAlertOnce(bool2);
          if ((0x10 & paramNotification.flags) == 0)
            break label238;
          bool3 = true;
          localBuilder4 = localBuilder3.setAutoCancel(bool3).setDefaults(paramNotification.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification.deleteIntent);
          if ((0x80 & paramNotification.flags) == 0)
            break label244;
        }
        label232: label238: label244: for (boolean bool4 = true; ; bool4 = false)
        {
          this.mBuilder = localBuilder4.setFullScreenIntent(paramPendingIntent2, bool4).setLargeIcon(paramBitmap).setNumber(paramInt1).setProgress(paramInt2, paramInt3, paramBoolean);
          return;
          bool1 = false;
          break;
          bool2 = false;
          break label115;
          bool3 = false;
          break label137;
        }
      }

      public Notification build()
      {
        return this.mBuilder.getNotification();
      }

      public Notification.Builder getBuilder()
      {
        return this.mBuilder;
      }
    }
  }

  static abstract interface NotificationCompatImpl
  {
    public abstract Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender);

    public abstract NotificationCompat.Action getAction(Notification paramNotification, int paramInt);

    public abstract NotificationCompat.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList);

    public abstract Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation paramUnreadConversation);

    public abstract ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompat.Action[] paramArrayOfAction);

    public abstract NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle, NotificationCompatBase.UnreadConversation.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1);
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface NotificationVisibility
  {
  }

  public static abstract class Style
  {
    CharSequence mBigContentTitle;

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    protected NotificationCompat.Builder mBuilder;
    CharSequence mSummaryText;
    boolean mSummaryTextSet = false;

    private int calculateTopPadding()
    {
      Resources localResources = this.mBuilder.mContext.getResources();
      int i = localResources.getDimensionPixelSize(R.dimen.notification_top_pad);
      int j = localResources.getDimensionPixelSize(R.dimen.notification_top_pad_large_text);
      float f = (constrain(localResources.getConfiguration().fontScale, 1.0F, 1.3F) - 1.0F) / 0.3F;
      return Math.round((1.0F - f) * i + f * j);
    }

    private static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
    {
      if (paramFloat1 < paramFloat2)
        return paramFloat2;
      if (paramFloat1 > paramFloat3)
        return paramFloat3;
      return paramFloat1;
    }

    private Bitmap createColoredBitmap(int paramInt1, int paramInt2, int paramInt3)
    {
      Drawable localDrawable = this.mBuilder.mContext.getResources().getDrawable(paramInt1);
      int i;
      if (paramInt3 == 0)
      {
        i = localDrawable.getIntrinsicWidth();
        if (paramInt3 != 0)
          break label107;
      }
      label107: for (int j = localDrawable.getIntrinsicHeight(); ; j = paramInt3)
      {
        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
        localDrawable.setBounds(0, 0, i, j);
        if (paramInt2 != 0)
          localDrawable.mutate().setColorFilter(new PorterDuffColorFilter(paramInt2, PorterDuff.Mode.SRC_IN));
        localDrawable.draw(new Canvas(localBitmap));
        return localBitmap;
        i = paramInt3;
        break;
      }
    }

    private Bitmap createIconWithBackground(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = R.drawable.notification_icon_background;
      if (paramInt4 == 0)
        paramInt4 = 0;
      Bitmap localBitmap = createColoredBitmap(i, paramInt4, paramInt2);
      Canvas localCanvas = new Canvas(localBitmap);
      Drawable localDrawable = this.mBuilder.mContext.getResources().getDrawable(paramInt1).mutate();
      localDrawable.setFilterBitmap(true);
      int j = (paramInt2 - paramInt3) / 2;
      localDrawable.setBounds(j, j, paramInt3 + j, paramInt3 + j);
      localDrawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_ATOP));
      localDrawable.draw(localCanvas);
      return localBitmap;
    }

    private void hideNormalContent(RemoteViews paramRemoteViews)
    {
      paramRemoteViews.setViewVisibility(R.id.title, 8);
      paramRemoteViews.setViewVisibility(R.id.text2, 8);
      paramRemoteViews.setViewVisibility(R.id.text, 8);
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public void addCompatExtras(Bundle paramBundle)
    {
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews applyStandardTemplate(boolean paramBoolean1, int paramInt, boolean paramBoolean2)
    {
      Resources localResources = this.mBuilder.mContext.getResources();
      RemoteViews localRemoteViews = new RemoteViews(this.mBuilder.mContext.getPackageName(), paramInt);
      int i;
      label93: label222: label231: int j;
      label135: int k;
      label312: label352: int m;
      label436: int i1;
      label564: label580: int i2;
      if (this.mBuilder.getPriority() < -1)
      {
        i = 1;
        if ((Build.VERSION.SDK_INT >= 16) && (Build.VERSION.SDK_INT < 21))
        {
          if (i == 0)
            break label620;
          localRemoteViews.setInt(R.id.notification_background, "setBackgroundResource", R.drawable.notification_bg_low);
          localRemoteViews.setInt(R.id.icon, "setBackgroundResource", R.drawable.notification_template_icon_low_bg);
        }
        if (this.mBuilder.mLargeIcon == null)
          break label688;
        if (Build.VERSION.SDK_INT < 16)
          break label649;
        localRemoteViews.setViewVisibility(R.id.icon, 0);
        localRemoteViews.setImageViewBitmap(R.id.icon, this.mBuilder.mLargeIcon);
        if ((paramBoolean1) && (this.mBuilder.mNotification.icon != 0))
        {
          int i8 = localResources.getDimensionPixelSize(R.dimen.notification_right_icon_size);
          int i9 = i8 - 2 * localResources.getDimensionPixelSize(R.dimen.notification_small_icon_background_padding);
          if (Build.VERSION.SDK_INT < 21)
            break label662;
          Bitmap localBitmap2 = createIconWithBackground(this.mBuilder.mNotification.icon, i8, i9, this.mBuilder.getColor());
          localRemoteViews.setImageViewBitmap(R.id.right_icon, localBitmap2);
          localRemoteViews.setViewVisibility(R.id.right_icon, 0);
        }
        if (this.mBuilder.mContentTitle != null)
          localRemoteViews.setTextViewText(R.id.title, this.mBuilder.mContentTitle);
        CharSequence localCharSequence1 = this.mBuilder.mContentText;
        j = 0;
        if (localCharSequence1 != null)
        {
          localRemoteViews.setTextViewText(R.id.text, this.mBuilder.mContentText);
          j = 1;
        }
        if ((Build.VERSION.SDK_INT >= 21) || (this.mBuilder.mLargeIcon == null))
          break label817;
        k = 1;
        if (this.mBuilder.mContentInfo == null)
          break label823;
        localRemoteViews.setTextViewText(R.id.info, this.mBuilder.mContentInfo);
        localRemoteViews.setViewVisibility(R.id.info, 0);
        j = 1;
        k = 1;
        CharSequence localCharSequence2 = this.mBuilder.mSubText;
        m = 0;
        if (localCharSequence2 != null)
        {
          int i4 = Build.VERSION.SDK_INT;
          m = 0;
          if (i4 >= 16)
          {
            localRemoteViews.setTextViewText(R.id.text, this.mBuilder.mSubText);
            if (this.mBuilder.mContentText == null)
              break label931;
            localRemoteViews.setTextViewText(R.id.text2, this.mBuilder.mContentText);
            localRemoteViews.setViewVisibility(R.id.text2, 0);
            m = 1;
          }
        }
        if ((m != 0) && (Build.VERSION.SDK_INT >= 16))
        {
          if (paramBoolean2)
          {
            float f = localResources.getDimensionPixelSize(R.dimen.notification_subtext_size);
            localRemoteViews.setTextViewTextSize(R.id.text, 0, f);
          }
          localRemoteViews.setViewPadding(R.id.line1, 0, 0, 0, 0);
        }
        if (this.mBuilder.getWhenIfShowing() != 0L)
        {
          if ((!this.mBuilder.mUseChronometer) || (Build.VERSION.SDK_INT < 16))
            break label947;
          localRemoteViews.setViewVisibility(R.id.chronometer, 0);
          localRemoteViews.setLong(R.id.chronometer, "setBase", this.mBuilder.getWhenIfShowing() + (SystemClock.elapsedRealtime() - System.currentTimeMillis()));
          localRemoteViews.setBoolean(R.id.chronometer, "setStarted", true);
          k = 1;
        }
        int n = R.id.right_side;
        if (k == 0)
          break label977;
        i1 = 0;
        localRemoteViews.setViewVisibility(n, i1);
        i2 = R.id.line3;
        if (j == 0)
          break label984;
      }
      label649: label662: label688: label817: label823: label977: label984: for (int i3 = 0; ; i3 = 8)
      {
        localRemoteViews.setViewVisibility(i2, i3);
        return localRemoteViews;
        i = 0;
        break;
        label620: localRemoteViews.setInt(R.id.notification_background, "setBackgroundResource", R.drawable.notification_bg);
        localRemoteViews.setInt(R.id.icon, "setBackgroundResource", R.drawable.notification_template_icon_bg);
        break label93;
        localRemoteViews.setViewVisibility(R.id.icon, 8);
        break label135;
        localRemoteViews.setImageViewBitmap(R.id.right_icon, createColoredBitmap(this.mBuilder.mNotification.icon, -1));
        break label222;
        if ((!paramBoolean1) || (this.mBuilder.mNotification.icon == 0))
          break label231;
        localRemoteViews.setViewVisibility(R.id.icon, 0);
        if (Build.VERSION.SDK_INT >= 21)
        {
          int i6 = localResources.getDimensionPixelSize(R.dimen.notification_large_icon_width) - localResources.getDimensionPixelSize(R.dimen.notification_big_circle_margin);
          int i7 = localResources.getDimensionPixelSize(R.dimen.notification_small_icon_size_as_large);
          Bitmap localBitmap1 = createIconWithBackground(this.mBuilder.mNotification.icon, i6, i7, this.mBuilder.getColor());
          localRemoteViews.setImageViewBitmap(R.id.icon, localBitmap1);
          break label231;
        }
        localRemoteViews.setImageViewBitmap(R.id.icon, createColoredBitmap(this.mBuilder.mNotification.icon, -1));
        break label231;
        k = 0;
        break label312;
        if (this.mBuilder.mNumber > 0)
        {
          int i5 = localResources.getInteger(R.integer.status_bar_notification_info_maxnum);
          if (this.mBuilder.mNumber > i5)
            localRemoteViews.setTextViewText(R.id.info, localResources.getString(R.string.status_bar_notification_info_overflow));
          while (true)
          {
            localRemoteViews.setViewVisibility(R.id.info, 0);
            j = 1;
            k = 1;
            break;
            NumberFormat localNumberFormat = NumberFormat.getIntegerInstance();
            localRemoteViews.setTextViewText(R.id.info, localNumberFormat.format(this.mBuilder.mNumber));
          }
        }
        localRemoteViews.setViewVisibility(R.id.info, 8);
        break label352;
        localRemoteViews.setViewVisibility(R.id.text2, 8);
        m = 0;
        break label436;
        localRemoteViews.setViewVisibility(R.id.time, 0);
        localRemoteViews.setLong(R.id.time, "setTime", this.mBuilder.getWhenIfShowing());
        break label564;
        i1 = 8;
        break label580;
      }
    }

    public Notification build()
    {
      NotificationCompat.Builder localBuilder = this.mBuilder;
      Notification localNotification = null;
      if (localBuilder != null)
        localNotification = this.mBuilder.build();
      return localNotification;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public void buildIntoRemoteViews(RemoteViews paramRemoteViews1, RemoteViews paramRemoteViews2)
    {
      hideNormalContent(paramRemoteViews1);
      paramRemoteViews1.removeAllViews(R.id.notification_main_column);
      paramRemoteViews1.addView(R.id.notification_main_column, paramRemoteViews2.clone());
      paramRemoteViews1.setViewVisibility(R.id.notification_main_column, 0);
      if (Build.VERSION.SDK_INT >= 21)
        paramRemoteViews1.setViewPadding(R.id.notification_main_column_container, 0, calculateTopPadding(), 0, 0);
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public Bitmap createColoredBitmap(int paramInt1, int paramInt2)
    {
      return createColoredBitmap(paramInt1, paramInt2, 0);
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      return null;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      return null;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeHeadsUpContentView(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      return null;
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    protected void restoreFromCompatExtras(Bundle paramBundle)
    {
    }

    public void setBuilder(NotificationCompat.Builder paramBuilder)
    {
      if (this.mBuilder != paramBuilder)
      {
        this.mBuilder = paramBuilder;
        if (this.mBuilder != null)
          this.mBuilder.setStyle(this);
      }
    }
  }

  public static final class WearableExtender
    implements NotificationCompat.Extender
  {
    private static final int DEFAULT_CONTENT_ICON_GRAVITY = 8388613;
    private static final int DEFAULT_FLAGS = 1;
    private static final int DEFAULT_GRAVITY = 80;
    private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
    private static final int FLAG_BIG_PICTURE_AMBIENT = 32;
    private static final int FLAG_CONTENT_INTENT_AVAILABLE_OFFLINE = 1;
    private static final int FLAG_HINT_AVOID_BACKGROUND_CLIPPING = 16;
    private static final int FLAG_HINT_CONTENT_INTENT_LAUNCHES_ACTIVITY = 64;
    private static final int FLAG_HINT_HIDE_ICON = 2;
    private static final int FLAG_HINT_SHOW_BACKGROUND_ONLY = 4;
    private static final int FLAG_START_SCROLL_BOTTOM = 8;
    private static final String KEY_ACTIONS = "actions";
    private static final String KEY_BACKGROUND = "background";
    private static final String KEY_BRIDGE_TAG = "bridgeTag";
    private static final String KEY_CONTENT_ACTION_INDEX = "contentActionIndex";
    private static final String KEY_CONTENT_ICON = "contentIcon";
    private static final String KEY_CONTENT_ICON_GRAVITY = "contentIconGravity";
    private static final String KEY_CUSTOM_CONTENT_HEIGHT = "customContentHeight";
    private static final String KEY_CUSTOM_SIZE_PRESET = "customSizePreset";
    private static final String KEY_DISMISSAL_ID = "dismissalId";
    private static final String KEY_DISPLAY_INTENT = "displayIntent";
    private static final String KEY_FLAGS = "flags";
    private static final String KEY_GRAVITY = "gravity";
    private static final String KEY_HINT_SCREEN_TIMEOUT = "hintScreenTimeout";
    private static final String KEY_PAGES = "pages";
    public static final int SCREEN_TIMEOUT_LONG = -1;
    public static final int SCREEN_TIMEOUT_SHORT = 0;
    public static final int SIZE_DEFAULT = 0;
    public static final int SIZE_FULL_SCREEN = 5;
    public static final int SIZE_LARGE = 4;
    public static final int SIZE_MEDIUM = 3;
    public static final int SIZE_SMALL = 2;
    public static final int SIZE_XSMALL = 1;
    public static final int UNSET_ACTION_INDEX = -1;
    private ArrayList<NotificationCompat.Action> mActions = new ArrayList();
    private Bitmap mBackground;
    private String mBridgeTag;
    private int mContentActionIndex = -1;
    private int mContentIcon;
    private int mContentIconGravity = 8388613;
    private int mCustomContentHeight;
    private int mCustomSizePreset = 0;
    private String mDismissalId;
    private PendingIntent mDisplayIntent;
    private int mFlags = 1;
    private int mGravity = 80;
    private int mHintScreenTimeout;
    private ArrayList<Notification> mPages = new ArrayList();

    public WearableExtender()
    {
    }

    public WearableExtender(Notification paramNotification)
    {
      Bundle localBundle1 = NotificationCompat.getExtras(paramNotification);
      if (localBundle1 != null);
      for (Bundle localBundle2 = localBundle1.getBundle("android.wearable.EXTENSIONS"); ; localBundle2 = null)
      {
        if (localBundle2 != null)
        {
          NotificationCompat.Action[] arrayOfAction = NotificationCompat.IMPL.getActionsFromParcelableArrayList(localBundle2.getParcelableArrayList("actions"));
          if (arrayOfAction != null)
            Collections.addAll(this.mActions, arrayOfAction);
          this.mFlags = localBundle2.getInt("flags", 1);
          this.mDisplayIntent = ((PendingIntent)localBundle2.getParcelable("displayIntent"));
          Notification[] arrayOfNotification = NotificationCompat.getNotificationArrayFromBundle(localBundle2, "pages");
          if (arrayOfNotification != null)
            Collections.addAll(this.mPages, arrayOfNotification);
          this.mBackground = ((Bitmap)localBundle2.getParcelable("background"));
          this.mContentIcon = localBundle2.getInt("contentIcon");
          this.mContentIconGravity = localBundle2.getInt("contentIconGravity", 8388613);
          this.mContentActionIndex = localBundle2.getInt("contentActionIndex", -1);
          this.mCustomSizePreset = localBundle2.getInt("customSizePreset", 0);
          this.mCustomContentHeight = localBundle2.getInt("customContentHeight");
          this.mGravity = localBundle2.getInt("gravity", 80);
          this.mHintScreenTimeout = localBundle2.getInt("hintScreenTimeout");
          this.mDismissalId = localBundle2.getString("dismissalId");
          this.mBridgeTag = localBundle2.getString("bridgeTag");
        }
        return;
      }
    }

    private void setFlag(int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mFlags = (paramInt | this.mFlags);
        return;
      }
      this.mFlags &= (paramInt ^ 0xFFFFFFFF);
    }

    public WearableExtender addAction(NotificationCompat.Action paramAction)
    {
      this.mActions.add(paramAction);
      return this;
    }

    public WearableExtender addActions(List<NotificationCompat.Action> paramList)
    {
      this.mActions.addAll(paramList);
      return this;
    }

    public WearableExtender addPage(Notification paramNotification)
    {
      this.mPages.add(paramNotification);
      return this;
    }

    public WearableExtender addPages(List<Notification> paramList)
    {
      this.mPages.addAll(paramList);
      return this;
    }

    public WearableExtender clearActions()
    {
      this.mActions.clear();
      return this;
    }

    public WearableExtender clearPages()
    {
      this.mPages.clear();
      return this;
    }

    public WearableExtender clone()
    {
      WearableExtender localWearableExtender = new WearableExtender();
      localWearableExtender.mActions = new ArrayList(this.mActions);
      localWearableExtender.mFlags = this.mFlags;
      localWearableExtender.mDisplayIntent = this.mDisplayIntent;
      localWearableExtender.mPages = new ArrayList(this.mPages);
      localWearableExtender.mBackground = this.mBackground;
      localWearableExtender.mContentIcon = this.mContentIcon;
      localWearableExtender.mContentIconGravity = this.mContentIconGravity;
      localWearableExtender.mContentActionIndex = this.mContentActionIndex;
      localWearableExtender.mCustomSizePreset = this.mCustomSizePreset;
      localWearableExtender.mCustomContentHeight = this.mCustomContentHeight;
      localWearableExtender.mGravity = this.mGravity;
      localWearableExtender.mHintScreenTimeout = this.mHintScreenTimeout;
      localWearableExtender.mDismissalId = this.mDismissalId;
      localWearableExtender.mBridgeTag = this.mBridgeTag;
      return localWearableExtender;
    }

    public NotificationCompat.Builder extend(NotificationCompat.Builder paramBuilder)
    {
      Bundle localBundle = new Bundle();
      if (!this.mActions.isEmpty())
        localBundle.putParcelableArrayList("actions", NotificationCompat.IMPL.getParcelableArrayListForActions((NotificationCompat.Action[])this.mActions.toArray(new NotificationCompat.Action[this.mActions.size()])));
      if (this.mFlags != 1)
        localBundle.putInt("flags", this.mFlags);
      if (this.mDisplayIntent != null)
        localBundle.putParcelable("displayIntent", this.mDisplayIntent);
      if (!this.mPages.isEmpty())
        localBundle.putParcelableArray("pages", (Parcelable[])this.mPages.toArray(new Notification[this.mPages.size()]));
      if (this.mBackground != null)
        localBundle.putParcelable("background", this.mBackground);
      if (this.mContentIcon != 0)
        localBundle.putInt("contentIcon", this.mContentIcon);
      if (this.mContentIconGravity != 8388613)
        localBundle.putInt("contentIconGravity", this.mContentIconGravity);
      if (this.mContentActionIndex != -1)
        localBundle.putInt("contentActionIndex", this.mContentActionIndex);
      if (this.mCustomSizePreset != 0)
        localBundle.putInt("customSizePreset", this.mCustomSizePreset);
      if (this.mCustomContentHeight != 0)
        localBundle.putInt("customContentHeight", this.mCustomContentHeight);
      if (this.mGravity != 80)
        localBundle.putInt("gravity", this.mGravity);
      if (this.mHintScreenTimeout != 0)
        localBundle.putInt("hintScreenTimeout", this.mHintScreenTimeout);
      if (this.mDismissalId != null)
        localBundle.putString("dismissalId", this.mDismissalId);
      if (this.mBridgeTag != null)
        localBundle.putString("bridgeTag", this.mBridgeTag);
      paramBuilder.getExtras().putBundle("android.wearable.EXTENSIONS", localBundle);
      return paramBuilder;
    }

    public List<NotificationCompat.Action> getActions()
    {
      return this.mActions;
    }

    public Bitmap getBackground()
    {
      return this.mBackground;
    }

    public String getBridgeTag()
    {
      return this.mBridgeTag;
    }

    public int getContentAction()
    {
      return this.mContentActionIndex;
    }

    public int getContentIcon()
    {
      return this.mContentIcon;
    }

    public int getContentIconGravity()
    {
      return this.mContentIconGravity;
    }

    public boolean getContentIntentAvailableOffline()
    {
      return (0x1 & this.mFlags) != 0;
    }

    public int getCustomContentHeight()
    {
      return this.mCustomContentHeight;
    }

    public int getCustomSizePreset()
    {
      return this.mCustomSizePreset;
    }

    public String getDismissalId()
    {
      return this.mDismissalId;
    }

    public PendingIntent getDisplayIntent()
    {
      return this.mDisplayIntent;
    }

    public int getGravity()
    {
      return this.mGravity;
    }

    public boolean getHintAmbientBigPicture()
    {
      return (0x20 & this.mFlags) != 0;
    }

    public boolean getHintAvoidBackgroundClipping()
    {
      return (0x10 & this.mFlags) != 0;
    }

    public boolean getHintContentIntentLaunchesActivity()
    {
      return (0x40 & this.mFlags) != 0;
    }

    public boolean getHintHideIcon()
    {
      return (0x2 & this.mFlags) != 0;
    }

    public int getHintScreenTimeout()
    {
      return this.mHintScreenTimeout;
    }

    public boolean getHintShowBackgroundOnly()
    {
      return (0x4 & this.mFlags) != 0;
    }

    public List<Notification> getPages()
    {
      return this.mPages;
    }

    public boolean getStartScrollBottom()
    {
      return (0x8 & this.mFlags) != 0;
    }

    public WearableExtender setBackground(Bitmap paramBitmap)
    {
      this.mBackground = paramBitmap;
      return this;
    }

    public WearableExtender setBridgeTag(String paramString)
    {
      this.mBridgeTag = paramString;
      return this;
    }

    public WearableExtender setContentAction(int paramInt)
    {
      this.mContentActionIndex = paramInt;
      return this;
    }

    public WearableExtender setContentIcon(int paramInt)
    {
      this.mContentIcon = paramInt;
      return this;
    }

    public WearableExtender setContentIconGravity(int paramInt)
    {
      this.mContentIconGravity = paramInt;
      return this;
    }

    public WearableExtender setContentIntentAvailableOffline(boolean paramBoolean)
    {
      setFlag(1, paramBoolean);
      return this;
    }

    public WearableExtender setCustomContentHeight(int paramInt)
    {
      this.mCustomContentHeight = paramInt;
      return this;
    }

    public WearableExtender setCustomSizePreset(int paramInt)
    {
      this.mCustomSizePreset = paramInt;
      return this;
    }

    public WearableExtender setDismissalId(String paramString)
    {
      this.mDismissalId = paramString;
      return this;
    }

    public WearableExtender setDisplayIntent(PendingIntent paramPendingIntent)
    {
      this.mDisplayIntent = paramPendingIntent;
      return this;
    }

    public WearableExtender setGravity(int paramInt)
    {
      this.mGravity = paramInt;
      return this;
    }

    public WearableExtender setHintAmbientBigPicture(boolean paramBoolean)
    {
      setFlag(32, paramBoolean);
      return this;
    }

    public WearableExtender setHintAvoidBackgroundClipping(boolean paramBoolean)
    {
      setFlag(16, paramBoolean);
      return this;
    }

    public WearableExtender setHintContentIntentLaunchesActivity(boolean paramBoolean)
    {
      setFlag(64, paramBoolean);
      return this;
    }

    public WearableExtender setHintHideIcon(boolean paramBoolean)
    {
      setFlag(2, paramBoolean);
      return this;
    }

    public WearableExtender setHintScreenTimeout(int paramInt)
    {
      this.mHintScreenTimeout = paramInt;
      return this;
    }

    public WearableExtender setHintShowBackgroundOnly(boolean paramBoolean)
    {
      setFlag(4, paramBoolean);
      return this;
    }

    public WearableExtender setStartScrollBottom(boolean paramBoolean)
    {
      setFlag(8, paramBoolean);
      return this;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.app.NotificationCompat
 * JD-Core Version:    0.6.0
 */