package android.support.v4.media.session;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataEditor;
import android.media.Rating;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.media.RemoteControlClient.OnMetadataUpdateListener;
import android.media.RemoteControlClient.OnPlaybackPositionUpdateListener;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.MediaMetadataCompat.Builder;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.VolumeProviderCompat.Callback;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaSessionCompat
{
  static final String ACTION_ARGUMENT_CAPTIONING_ENABLED = "android.support.v4.media.session.action.ARGUMENT_CAPTIONING_ENABLED";
  static final String ACTION_ARGUMENT_EXTRAS = "android.support.v4.media.session.action.ARGUMENT_EXTRAS";
  static final String ACTION_ARGUMENT_MEDIA_ID = "android.support.v4.media.session.action.ARGUMENT_MEDIA_ID";
  static final String ACTION_ARGUMENT_QUERY = "android.support.v4.media.session.action.ARGUMENT_QUERY";
  static final String ACTION_ARGUMENT_RATING = "android.support.v4.media.session.action.ARGUMENT_RATING";
  static final String ACTION_ARGUMENT_REPEAT_MODE = "android.support.v4.media.session.action.ARGUMENT_REPEAT_MODE";
  static final String ACTION_ARGUMENT_SHUFFLE_MODE = "android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE";
  static final String ACTION_ARGUMENT_SHUFFLE_MODE_ENABLED = "android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE_ENABLED";
  static final String ACTION_ARGUMENT_URI = "android.support.v4.media.session.action.ARGUMENT_URI";
  public static final String ACTION_FLAG_AS_INAPPROPRIATE = "android.support.v4.media.session.action.FLAG_AS_INAPPROPRIATE";
  public static final String ACTION_FOLLOW = "android.support.v4.media.session.action.FOLLOW";
  static final String ACTION_PLAY_FROM_URI = "android.support.v4.media.session.action.PLAY_FROM_URI";
  static final String ACTION_PREPARE = "android.support.v4.media.session.action.PREPARE";
  static final String ACTION_PREPARE_FROM_MEDIA_ID = "android.support.v4.media.session.action.PREPARE_FROM_MEDIA_ID";
  static final String ACTION_PREPARE_FROM_SEARCH = "android.support.v4.media.session.action.PREPARE_FROM_SEARCH";
  static final String ACTION_PREPARE_FROM_URI = "android.support.v4.media.session.action.PREPARE_FROM_URI";
  static final String ACTION_SET_CAPTIONING_ENABLED = "android.support.v4.media.session.action.SET_CAPTIONING_ENABLED";
  static final String ACTION_SET_RATING = "android.support.v4.media.session.action.SET_RATING";
  static final String ACTION_SET_REPEAT_MODE = "android.support.v4.media.session.action.SET_REPEAT_MODE";
  static final String ACTION_SET_SHUFFLE_MODE = "android.support.v4.media.session.action.SET_SHUFFLE_MODE";
  static final String ACTION_SET_SHUFFLE_MODE_ENABLED = "android.support.v4.media.session.action.SET_SHUFFLE_MODE_ENABLED";
  public static final String ACTION_SKIP_AD = "android.support.v4.media.session.action.SKIP_AD";
  public static final String ACTION_UNFOLLOW = "android.support.v4.media.session.action.UNFOLLOW";
  public static final String ARGUMENT_MEDIA_ATTRIBUTE = "android.support.v4.media.session.ARGUMENT_MEDIA_ATTRIBUTE";
  public static final String ARGUMENT_MEDIA_ATTRIBUTE_VALUE = "android.support.v4.media.session.ARGUMENT_MEDIA_ATTRIBUTE_VALUE";
  static final String EXTRA_BINDER = "android.support.v4.media.session.EXTRA_BINDER";
  public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;
  public static final int FLAG_HANDLES_QUEUE_COMMANDS = 4;
  public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 2;
  private static final int MAX_BITMAP_SIZE_IN_DP = 320;
  public static final int MEDIA_ATTRIBUTE_ALBUM = 1;
  public static final int MEDIA_ATTRIBUTE_ARTIST = 0;
  public static final int MEDIA_ATTRIBUTE_PLAYLIST = 2;
  static final String TAG = "MediaSessionCompat";
  static int sMaxBitmapSize;
  private final ArrayList<OnActiveChangeListener> mActiveListeners = new ArrayList();
  private final MediaControllerCompat mController;
  private final MediaSessionImpl mImpl;

  private MediaSessionCompat(Context paramContext, MediaSessionImpl paramMediaSessionImpl)
  {
    this.mImpl = paramMediaSessionImpl;
    if ((Build.VERSION.SDK_INT >= 21) && (!MediaSessionCompatApi21.hasCallback(paramMediaSessionImpl.getMediaSession())))
      setCallback(new Callback()
      {
      });
    this.mController = new MediaControllerCompat(paramContext, this);
  }

  public MediaSessionCompat(Context paramContext, String paramString)
  {
    this(paramContext, paramString, null, null);
  }

  public MediaSessionCompat(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
  {
    if (paramContext == null)
      throw new IllegalArgumentException("context must not be null");
    if (TextUtils.isEmpty(paramString))
      throw new IllegalArgumentException("tag must not be null or empty");
    if (paramComponentName == null)
    {
      paramComponentName = MediaButtonReceiver.getMediaButtonReceiverComponent(paramContext);
      if (paramComponentName == null)
        Log.w("MediaSessionCompat", "Couldn't find a unique registered media button receiver in the given context.");
    }
    if ((paramComponentName != null) && (paramPendingIntent == null))
    {
      Intent localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
      localIntent.setComponent(paramComponentName);
      paramPendingIntent = PendingIntent.getBroadcast(paramContext, 0, localIntent, 0);
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.mImpl = new MediaSessionImplApi21(paramContext, paramString);
      setCallback(new Callback()
      {
      });
      this.mImpl.setMediaButtonReceiver(paramPendingIntent);
    }
    while (true)
    {
      this.mController = new MediaControllerCompat(paramContext, this);
      if (sMaxBitmapSize == 0)
        sMaxBitmapSize = (int)TypedValue.applyDimension(1, 320.0F, paramContext.getResources().getDisplayMetrics());
      return;
      if (Build.VERSION.SDK_INT >= 19)
      {
        this.mImpl = new MediaSessionImplApi19(paramContext, paramString, paramComponentName, paramPendingIntent);
        continue;
      }
      if (Build.VERSION.SDK_INT >= 18)
      {
        this.mImpl = new MediaSessionImplApi18(paramContext, paramString, paramComponentName, paramPendingIntent);
        continue;
      }
      this.mImpl = new MediaSessionImplBase(paramContext, paramString, paramComponentName, paramPendingIntent);
    }
  }

  public static MediaSessionCompat fromMediaSession(Context paramContext, Object paramObject)
  {
    if ((paramContext != null) && (paramObject != null) && (Build.VERSION.SDK_INT >= 21))
      return new MediaSessionCompat(paramContext, new MediaSessionImplApi21(paramObject));
    return null;
  }

  private static PlaybackStateCompat getStateWithUpdatedPosition(PlaybackStateCompat paramPlaybackStateCompat, MediaMetadataCompat paramMediaMetadataCompat)
  {
    if ((paramPlaybackStateCompat == null) || (paramPlaybackStateCompat.getPosition() == -1L));
    long l1;
    do
    {
      do
        return paramPlaybackStateCompat;
      while ((paramPlaybackStateCompat.getState() != 3) && (paramPlaybackStateCompat.getState() != 4) && (paramPlaybackStateCompat.getState() != 5));
      l1 = paramPlaybackStateCompat.getLastPositionUpdateTime();
    }
    while (l1 <= 0L);
    long l2 = SystemClock.elapsedRealtime();
    long l3 = ()(paramPlaybackStateCompat.getPlaybackSpeed() * (float)(l2 - l1)) + paramPlaybackStateCompat.getPosition();
    long l4 = -1L;
    if ((paramMediaMetadataCompat != null) && (paramMediaMetadataCompat.containsKey("android.media.metadata.DURATION")))
      l4 = paramMediaMetadataCompat.getLong("android.media.metadata.DURATION");
    if ((l4 >= 0L) && (l3 > l4))
      l3 = l4;
    while (true)
    {
      return new PlaybackStateCompat.Builder(paramPlaybackStateCompat).setState(paramPlaybackStateCompat.getState(), l3, paramPlaybackStateCompat.getPlaybackSpeed(), l2).build();
      if (l3 >= 0L)
        continue;
      l3 = 0L;
    }
  }

  public void addOnActiveChangeListener(OnActiveChangeListener paramOnActiveChangeListener)
  {
    if (paramOnActiveChangeListener == null)
      throw new IllegalArgumentException("Listener may not be null");
    this.mActiveListeners.add(paramOnActiveChangeListener);
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public String getCallingPackage()
  {
    return this.mImpl.getCallingPackage();
  }

  public MediaControllerCompat getController()
  {
    return this.mController;
  }

  public Object getMediaSession()
  {
    return this.mImpl.getMediaSession();
  }

  public Object getRemoteControlClient()
  {
    return this.mImpl.getRemoteControlClient();
  }

  public Token getSessionToken()
  {
    return this.mImpl.getSessionToken();
  }

  public boolean isActive()
  {
    return this.mImpl.isActive();
  }

  public void release()
  {
    this.mImpl.release();
  }

  public void removeOnActiveChangeListener(OnActiveChangeListener paramOnActiveChangeListener)
  {
    if (paramOnActiveChangeListener == null)
      throw new IllegalArgumentException("Listener may not be null");
    this.mActiveListeners.remove(paramOnActiveChangeListener);
  }

  public void sendSessionEvent(String paramString, Bundle paramBundle)
  {
    if (TextUtils.isEmpty(paramString))
      throw new IllegalArgumentException("event cannot be null or empty");
    this.mImpl.sendSessionEvent(paramString, paramBundle);
  }

  public void setActive(boolean paramBoolean)
  {
    this.mImpl.setActive(paramBoolean);
    Iterator localIterator = this.mActiveListeners.iterator();
    while (localIterator.hasNext())
      ((OnActiveChangeListener)localIterator.next()).onActiveChanged();
  }

  public void setCallback(Callback paramCallback)
  {
    setCallback(paramCallback, null);
  }

  public void setCallback(Callback paramCallback, Handler paramHandler)
  {
    MediaSessionImpl localMediaSessionImpl = this.mImpl;
    if (paramHandler != null);
    while (true)
    {
      localMediaSessionImpl.setCallback(paramCallback, paramHandler);
      return;
      paramHandler = new Handler();
    }
  }

  public void setCaptioningEnabled(boolean paramBoolean)
  {
    this.mImpl.setCaptioningEnabled(paramBoolean);
  }

  public void setExtras(Bundle paramBundle)
  {
    this.mImpl.setExtras(paramBundle);
  }

  public void setFlags(int paramInt)
  {
    this.mImpl.setFlags(paramInt);
  }

  public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
  {
    this.mImpl.setMediaButtonReceiver(paramPendingIntent);
  }

  public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
  {
    this.mImpl.setMetadata(paramMediaMetadataCompat);
  }

  public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
  {
    this.mImpl.setPlaybackState(paramPlaybackStateCompat);
  }

  public void setPlaybackToLocal(int paramInt)
  {
    this.mImpl.setPlaybackToLocal(paramInt);
  }

  public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
  {
    if (paramVolumeProviderCompat == null)
      throw new IllegalArgumentException("volumeProvider may not be null!");
    this.mImpl.setPlaybackToRemote(paramVolumeProviderCompat);
  }

  public void setQueue(List<QueueItem> paramList)
  {
    this.mImpl.setQueue(paramList);
  }

  public void setQueueTitle(CharSequence paramCharSequence)
  {
    this.mImpl.setQueueTitle(paramCharSequence);
  }

  public void setRatingType(int paramInt)
  {
    this.mImpl.setRatingType(paramInt);
  }

  public void setRepeatMode(int paramInt)
  {
    this.mImpl.setRepeatMode(paramInt);
  }

  public void setSessionActivity(PendingIntent paramPendingIntent)
  {
    this.mImpl.setSessionActivity(paramPendingIntent);
  }

  public void setShuffleMode(int paramInt)
  {
    this.mImpl.setShuffleMode(paramInt);
  }

  @Deprecated
  public void setShuffleModeEnabled(boolean paramBoolean)
  {
    this.mImpl.setShuffleModeEnabled(paramBoolean);
  }

  public static abstract class Callback
  {
    private CallbackHandler mCallbackHandler = null;
    final Object mCallbackObj;
    private boolean mMediaPlayPauseKeyPending;
    private WeakReference<MediaSessionCompat.MediaSessionImpl> mSessionImpl;

    public Callback()
    {
      if (Build.VERSION.SDK_INT >= 24)
      {
        this.mCallbackObj = MediaSessionCompatApi24.createCallback(new StubApi24());
        return;
      }
      if (Build.VERSION.SDK_INT >= 23)
      {
        this.mCallbackObj = MediaSessionCompatApi23.createCallback(new StubApi23());
        return;
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        this.mCallbackObj = MediaSessionCompatApi21.createCallback(new StubApi21());
        return;
      }
      this.mCallbackObj = null;
    }

    private void handleMediaPlayPauseKeySingleTapIfPending()
    {
      int i = 1;
      if (!this.mMediaPlayPauseKeyPending);
      int j;
      label67: int k;
      label81: label115: label121: label127: 
      do
      {
        MediaSessionCompat.MediaSessionImpl localMediaSessionImpl;
        do
        {
          return;
          this.mMediaPlayPauseKeyPending = false;
          this.mCallbackHandler.removeMessages(i);
          localMediaSessionImpl = (MediaSessionCompat.MediaSessionImpl)this.mSessionImpl.get();
        }
        while (localMediaSessionImpl == null);
        PlaybackStateCompat localPlaybackStateCompat = localMediaSessionImpl.getPlaybackState();
        long l;
        if (localPlaybackStateCompat == null)
        {
          l = 0L;
          if ((localPlaybackStateCompat == null) || (localPlaybackStateCompat.getState() != 3))
            break label115;
          j = i;
          if ((0x204 & l) == 0L)
            break label121;
          k = i;
          if ((0x202 & l) == 0L)
            break label127;
        }
        while (true)
        {
          if ((j == 0) || (i == 0))
            break label132;
          onPause();
          return;
          l = localPlaybackStateCompat.getActions();
          break;
          j = 0;
          break label67;
          k = 0;
          break label81;
          i = 0;
        }
      }
      while ((j != 0) || (k == 0));
      label132: onPlay();
    }

    private void setSessionImpl(MediaSessionCompat.MediaSessionImpl paramMediaSessionImpl, Handler paramHandler)
    {
      this.mSessionImpl = new WeakReference(paramMediaSessionImpl);
      if (this.mCallbackHandler != null)
        this.mCallbackHandler.removeCallbacksAndMessages(null);
      this.mCallbackHandler = new CallbackHandler(paramHandler.getLooper());
    }

    public void onAddQueueItem(MediaDescriptionCompat paramMediaDescriptionCompat)
    {
    }

    public void onAddQueueItem(MediaDescriptionCompat paramMediaDescriptionCompat, int paramInt)
    {
    }

    public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
    {
    }

    public void onCustomAction(String paramString, Bundle paramBundle)
    {
    }

    public void onFastForward()
    {
    }

    public boolean onMediaButtonEvent(Intent paramIntent)
    {
      int i = 1;
      MediaSessionCompat.MediaSessionImpl localMediaSessionImpl = (MediaSessionCompat.MediaSessionImpl)this.mSessionImpl.get();
      if ((localMediaSessionImpl == null) || (this.mCallbackHandler == null))
        i = 0;
      while (true)
      {
        return i;
        KeyEvent localKeyEvent = (KeyEvent)paramIntent.getParcelableExtra("android.intent.extra.KEY_EVENT");
        if ((localKeyEvent == null) || (localKeyEvent.getAction() != 0))
          return false;
        switch (localKeyEvent.getKeyCode())
        {
        default:
          handleMediaPlayPauseKeySingleTapIfPending();
          return false;
        case 79:
        case 85:
        }
        if (localKeyEvent.getRepeatCount() > 0)
        {
          handleMediaPlayPauseKeySingleTapIfPending();
          return i;
        }
        if (!this.mMediaPlayPauseKeyPending)
          break;
        this.mCallbackHandler.removeMessages(i);
        this.mMediaPlayPauseKeyPending = false;
        PlaybackStateCompat localPlaybackStateCompat = localMediaSessionImpl.getPlaybackState();
        long l;
        if (localPlaybackStateCompat == null)
          l = 0L;
        while ((0x20 & l) != 0L)
        {
          onSkipToNext();
          return i;
          l = localPlaybackStateCompat.getActions();
        }
      }
      this.mMediaPlayPauseKeyPending = i;
      this.mCallbackHandler.sendEmptyMessageDelayed(i, ViewConfiguration.getDoubleTapTimeout());
      return i;
    }

    public void onPause()
    {
    }

    public void onPlay()
    {
    }

    public void onPlayFromMediaId(String paramString, Bundle paramBundle)
    {
    }

    public void onPlayFromSearch(String paramString, Bundle paramBundle)
    {
    }

    public void onPlayFromUri(Uri paramUri, Bundle paramBundle)
    {
    }

    public void onPrepare()
    {
    }

    public void onPrepareFromMediaId(String paramString, Bundle paramBundle)
    {
    }

    public void onPrepareFromSearch(String paramString, Bundle paramBundle)
    {
    }

    public void onPrepareFromUri(Uri paramUri, Bundle paramBundle)
    {
    }

    public void onRemoveQueueItem(MediaDescriptionCompat paramMediaDescriptionCompat)
    {
    }

    @Deprecated
    public void onRemoveQueueItemAt(int paramInt)
    {
    }

    public void onRewind()
    {
    }

    public void onSeekTo(long paramLong)
    {
    }

    public void onSetCaptioningEnabled(boolean paramBoolean)
    {
    }

    public void onSetRating(RatingCompat paramRatingCompat)
    {
    }

    public void onSetRating(RatingCompat paramRatingCompat, Bundle paramBundle)
    {
    }

    public void onSetRepeatMode(int paramInt)
    {
    }

    public void onSetShuffleMode(int paramInt)
    {
    }

    @Deprecated
    public void onSetShuffleModeEnabled(boolean paramBoolean)
    {
    }

    public void onSkipToNext()
    {
    }

    public void onSkipToPrevious()
    {
    }

    public void onSkipToQueueItem(long paramLong)
    {
    }

    public void onStop()
    {
    }

    private class CallbackHandler extends Handler
    {
      private static final int MSG_MEDIA_PLAY_PAUSE_KEY_DOUBLE_TAP_TIMEOUT = 1;

      CallbackHandler(Looper arg2)
      {
        super();
      }

      public void handleMessage(Message paramMessage)
      {
        if (paramMessage.what == 1)
          MediaSessionCompat.Callback.this.handleMediaPlayPauseKeySingleTapIfPending();
      }
    }

    @RequiresApi(21)
    private class StubApi21
      implements MediaSessionCompatApi21.Callback
    {
      StubApi21()
      {
      }

      public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
      {
        try
        {
          if (paramString.equals("android.support.v4.media.session.command.GET_EXTRA_BINDER"))
          {
            MediaSessionCompat.MediaSessionImplApi21 localMediaSessionImplApi212 = (MediaSessionCompat.MediaSessionImplApi21)MediaSessionCompat.Callback.this.mSessionImpl.get();
            if (localMediaSessionImplApi212 == null)
              return;
            Bundle localBundle = new Bundle();
            IMediaSession localIMediaSession = localMediaSessionImplApi212.getSessionToken().getExtraBinder();
            IBinder localIBinder = null;
            if (localIMediaSession == null);
            while (true)
            {
              BundleCompat.putBinder(localBundle, "android.support.v4.media.session.EXTRA_BINDER", localIBinder);
              paramResultReceiver.send(0, localBundle);
              return;
              localIBinder = localIMediaSession.asBinder();
            }
          }
          if (paramString.equals("android.support.v4.media.session.command.ADD_QUEUE_ITEM"))
          {
            paramBundle.setClassLoader(MediaDescriptionCompat.class.getClassLoader());
            MediaSessionCompat.Callback.this.onAddQueueItem((MediaDescriptionCompat)paramBundle.getParcelable("android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION"));
            return;
          }
        }
        catch (BadParcelableException localBadParcelableException)
        {
          Log.e("MediaSessionCompat", "Could not unparcel the extra data.");
          return;
        }
        if (paramString.equals("android.support.v4.media.session.command.ADD_QUEUE_ITEM_AT"))
        {
          paramBundle.setClassLoader(MediaDescriptionCompat.class.getClassLoader());
          MediaSessionCompat.Callback.this.onAddQueueItem((MediaDescriptionCompat)paramBundle.getParcelable("android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION"), paramBundle.getInt("android.support.v4.media.session.command.ARGUMENT_INDEX"));
          return;
        }
        if (paramString.equals("android.support.v4.media.session.command.REMOVE_QUEUE_ITEM"))
        {
          paramBundle.setClassLoader(MediaDescriptionCompat.class.getClassLoader());
          MediaSessionCompat.Callback.this.onRemoveQueueItem((MediaDescriptionCompat)paramBundle.getParcelable("android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION"));
          return;
        }
        if (paramString.equals("android.support.v4.media.session.command.REMOVE_QUEUE_ITEM_AT"))
        {
          MediaSessionCompat.MediaSessionImplApi21 localMediaSessionImplApi211 = (MediaSessionCompat.MediaSessionImplApi21)MediaSessionCompat.Callback.this.mSessionImpl.get();
          if ((localMediaSessionImplApi211 != null) && (MediaSessionCompat.MediaSessionImplApi21.access$200(localMediaSessionImplApi211) != null))
          {
            int i = paramBundle.getInt("android.support.v4.media.session.command.ARGUMENT_INDEX", -1);
            MediaSessionCompat.QueueItem localQueueItem = null;
            if (i >= 0)
            {
              int j = MediaSessionCompat.MediaSessionImplApi21.access$200(localMediaSessionImplApi211).size();
              localQueueItem = null;
              if (i < j)
                localQueueItem = (MediaSessionCompat.QueueItem)MediaSessionCompat.MediaSessionImplApi21.access$200(localMediaSessionImplApi211).get(i);
            }
            if (localQueueItem != null)
            {
              MediaSessionCompat.Callback.this.onRemoveQueueItem(localQueueItem.getDescription());
              return;
            }
          }
        }
        else
        {
          MediaSessionCompat.Callback.this.onCommand(paramString, paramBundle, paramResultReceiver);
        }
      }

      public void onCustomAction(String paramString, Bundle paramBundle)
      {
        if (paramString.equals("android.support.v4.media.session.action.PLAY_FROM_URI"))
        {
          Uri localUri2 = (Uri)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_URI");
          Bundle localBundle5 = (Bundle)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          MediaSessionCompat.Callback.this.onPlayFromUri(localUri2, localBundle5);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE"))
        {
          MediaSessionCompat.Callback.this.onPrepare();
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE_FROM_MEDIA_ID"))
        {
          String str2 = paramBundle.getString("android.support.v4.media.session.action.ARGUMENT_MEDIA_ID");
          Bundle localBundle4 = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          MediaSessionCompat.Callback.this.onPrepareFromMediaId(str2, localBundle4);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE_FROM_SEARCH"))
        {
          String str1 = paramBundle.getString("android.support.v4.media.session.action.ARGUMENT_QUERY");
          Bundle localBundle3 = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          MediaSessionCompat.Callback.this.onPrepareFromSearch(str1, localBundle3);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE_FROM_URI"))
        {
          Uri localUri1 = (Uri)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_URI");
          Bundle localBundle2 = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          MediaSessionCompat.Callback.this.onPrepareFromUri(localUri1, localBundle2);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.SET_CAPTIONING_ENABLED"))
        {
          boolean bool2 = paramBundle.getBoolean("android.support.v4.media.session.action.ARGUMENT_CAPTIONING_ENABLED");
          MediaSessionCompat.Callback.this.onSetCaptioningEnabled(bool2);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.SET_REPEAT_MODE"))
        {
          int j = paramBundle.getInt("android.support.v4.media.session.action.ARGUMENT_REPEAT_MODE");
          MediaSessionCompat.Callback.this.onSetRepeatMode(j);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.SET_SHUFFLE_MODE_ENABLED"))
        {
          boolean bool1 = paramBundle.getBoolean("android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE_ENABLED");
          MediaSessionCompat.Callback.this.onSetShuffleModeEnabled(bool1);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.SET_SHUFFLE_MODE"))
        {
          int i = paramBundle.getInt("android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE");
          MediaSessionCompat.Callback.this.onSetShuffleMode(i);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.SET_RATING"))
        {
          paramBundle.setClassLoader(RatingCompat.class.getClassLoader());
          RatingCompat localRatingCompat = (RatingCompat)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_RATING");
          Bundle localBundle1 = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          MediaSessionCompat.Callback.this.onSetRating(localRatingCompat, localBundle1);
          return;
        }
        MediaSessionCompat.Callback.this.onCustomAction(paramString, paramBundle);
      }

      public void onFastForward()
      {
        MediaSessionCompat.Callback.this.onFastForward();
      }

      public boolean onMediaButtonEvent(Intent paramIntent)
      {
        return MediaSessionCompat.Callback.this.onMediaButtonEvent(paramIntent);
      }

      public void onPause()
      {
        MediaSessionCompat.Callback.this.onPause();
      }

      public void onPlay()
      {
        MediaSessionCompat.Callback.this.onPlay();
      }

      public void onPlayFromMediaId(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromMediaId(paramString, paramBundle);
      }

      public void onPlayFromSearch(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromSearch(paramString, paramBundle);
      }

      public void onRewind()
      {
        MediaSessionCompat.Callback.this.onRewind();
      }

      public void onSeekTo(long paramLong)
      {
        MediaSessionCompat.Callback.this.onSeekTo(paramLong);
      }

      public void onSetRating(Object paramObject)
      {
        MediaSessionCompat.Callback.this.onSetRating(RatingCompat.fromRating(paramObject));
      }

      public void onSetRating(Object paramObject, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onSetRating(RatingCompat.fromRating(paramObject), paramBundle);
      }

      public void onSkipToNext()
      {
        MediaSessionCompat.Callback.this.onSkipToNext();
      }

      public void onSkipToPrevious()
      {
        MediaSessionCompat.Callback.this.onSkipToPrevious();
      }

      public void onSkipToQueueItem(long paramLong)
      {
        MediaSessionCompat.Callback.this.onSkipToQueueItem(paramLong);
      }

      public void onStop()
      {
        MediaSessionCompat.Callback.this.onStop();
      }
    }

    @RequiresApi(23)
    private class StubApi23 extends MediaSessionCompat.Callback.StubApi21
      implements MediaSessionCompatApi23.Callback
    {
      StubApi23()
      {
        super();
      }

      public void onPlayFromUri(Uri paramUri, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromUri(paramUri, paramBundle);
      }
    }

    @RequiresApi(24)
    private class StubApi24 extends MediaSessionCompat.Callback.StubApi23
      implements MediaSessionCompatApi24.Callback
    {
      StubApi24()
      {
        super();
      }

      public void onPrepare()
      {
        MediaSessionCompat.Callback.this.onPrepare();
      }

      public void onPrepareFromMediaId(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPrepareFromMediaId(paramString, paramBundle);
      }

      public void onPrepareFromSearch(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPrepareFromSearch(paramString, paramBundle);
      }

      public void onPrepareFromUri(Uri paramUri, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPrepareFromUri(paramUri, paramBundle);
      }
    }
  }

  static abstract interface MediaSessionImpl
  {
    public abstract String getCallingPackage();

    public abstract Object getMediaSession();

    public abstract PlaybackStateCompat getPlaybackState();

    public abstract Object getRemoteControlClient();

    public abstract MediaSessionCompat.Token getSessionToken();

    public abstract boolean isActive();

    public abstract void release();

    public abstract void sendSessionEvent(String paramString, Bundle paramBundle);

    public abstract void setActive(boolean paramBoolean);

    public abstract void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler);

    public abstract void setCaptioningEnabled(boolean paramBoolean);

    public abstract void setExtras(Bundle paramBundle);

    public abstract void setFlags(int paramInt);

    public abstract void setMediaButtonReceiver(PendingIntent paramPendingIntent);

    public abstract void setMetadata(MediaMetadataCompat paramMediaMetadataCompat);

    public abstract void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat);

    public abstract void setPlaybackToLocal(int paramInt);

    public abstract void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat);

    public abstract void setQueue(List<MediaSessionCompat.QueueItem> paramList);

    public abstract void setQueueTitle(CharSequence paramCharSequence);

    public abstract void setRatingType(int paramInt);

    public abstract void setRepeatMode(int paramInt);

    public abstract void setSessionActivity(PendingIntent paramPendingIntent);

    public abstract void setShuffleMode(int paramInt);

    public abstract void setShuffleModeEnabled(boolean paramBoolean);
  }

  @RequiresApi(18)
  static class MediaSessionImplApi18 extends MediaSessionCompat.MediaSessionImplBase
  {
    private static boolean sIsMbrPendingIntentSupported = true;

    MediaSessionImplApi18(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
    {
      super(paramString, paramComponentName, paramPendingIntent);
    }

    int getRccTransportControlFlagsFromActions(long paramLong)
    {
      int i = super.getRccTransportControlFlagsFromActions(paramLong);
      if ((0x100 & paramLong) != 0L)
        i |= 256;
      return i;
    }

    void registerMediaButtonEventReceiver(PendingIntent paramPendingIntent, ComponentName paramComponentName)
    {
      if (sIsMbrPendingIntentSupported);
      try
      {
        this.mAudioManager.registerMediaButtonEventReceiver(paramPendingIntent);
        if (!sIsMbrPendingIntentSupported)
          super.registerMediaButtonEventReceiver(paramPendingIntent, paramComponentName);
        return;
      }
      catch (NullPointerException localNullPointerException)
      {
        while (true)
        {
          Log.w("MediaSessionCompat", "Unable to register media button event receiver with PendingIntent, falling back to ComponentName.");
          sIsMbrPendingIntentSupported = false;
        }
      }
    }

    public void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler)
    {
      super.setCallback(paramCallback, paramHandler);
      if (paramCallback == null)
      {
        this.mRcc.setPlaybackPositionUpdateListener(null);
        return;
      }
      1 local1 = new RemoteControlClient.OnPlaybackPositionUpdateListener()
      {
        public void onPlaybackPositionUpdate(long paramLong)
        {
          MediaSessionCompat.MediaSessionImplApi18.this.postToHandler(18, Long.valueOf(paramLong));
        }
      };
      this.mRcc.setPlaybackPositionUpdateListener(local1);
    }

    void setRccState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      long l1 = paramPlaybackStateCompat.getPosition();
      float f = paramPlaybackStateCompat.getPlaybackSpeed();
      long l2 = paramPlaybackStateCompat.getLastPositionUpdateTime();
      long l3 = SystemClock.elapsedRealtime();
      if ((paramPlaybackStateCompat.getState() == 3) && (l1 > 0L))
      {
        long l4 = 0L;
        if (l2 > 0L)
        {
          l4 = l3 - l2;
          if ((f > 0.0F) && (f != 1.0F))
            l4 = ()(f * (float)l4);
        }
        l1 += l4;
      }
      this.mRcc.setPlaybackState(getRccStateFromState(paramPlaybackStateCompat.getState()), l1, f);
    }

    void unregisterMediaButtonEventReceiver(PendingIntent paramPendingIntent, ComponentName paramComponentName)
    {
      if (sIsMbrPendingIntentSupported)
      {
        this.mAudioManager.unregisterMediaButtonEventReceiver(paramPendingIntent);
        return;
      }
      super.unregisterMediaButtonEventReceiver(paramPendingIntent, paramComponentName);
    }
  }

  @RequiresApi(19)
  static class MediaSessionImplApi19 extends MediaSessionCompat.MediaSessionImplApi18
  {
    MediaSessionImplApi19(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
    {
      super(paramString, paramComponentName, paramPendingIntent);
    }

    RemoteControlClient.MetadataEditor buildRccMetadata(Bundle paramBundle)
    {
      RemoteControlClient.MetadataEditor localMetadataEditor = super.buildRccMetadata(paramBundle);
      long l;
      if (this.mState == null)
      {
        l = 0L;
        if ((0x80 & l) != 0L)
          localMetadataEditor.addEditableKey(268435457);
        if (paramBundle != null)
          break label48;
      }
      label48: 
      do
      {
        return localMetadataEditor;
        l = this.mState.getActions();
        break;
        if (paramBundle.containsKey("android.media.metadata.YEAR"))
          localMetadataEditor.putLong(8, paramBundle.getLong("android.media.metadata.YEAR"));
        if (!paramBundle.containsKey("android.media.metadata.RATING"))
          continue;
        localMetadataEditor.putObject(101, paramBundle.getParcelable("android.media.metadata.RATING"));
      }
      while (!paramBundle.containsKey("android.media.metadata.USER_RATING"));
      localMetadataEditor.putObject(268435457, paramBundle.getParcelable("android.media.metadata.USER_RATING"));
      return localMetadataEditor;
    }

    int getRccTransportControlFlagsFromActions(long paramLong)
    {
      int i = super.getRccTransportControlFlagsFromActions(paramLong);
      if ((0x80 & paramLong) != 0L)
        i |= 512;
      return i;
    }

    public void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler)
    {
      super.setCallback(paramCallback, paramHandler);
      if (paramCallback == null)
      {
        this.mRcc.setMetadataUpdateListener(null);
        return;
      }
      1 local1 = new RemoteControlClient.OnMetadataUpdateListener()
      {
        public void onMetadataUpdate(int paramInt, Object paramObject)
        {
          if ((paramInt == 268435457) && ((paramObject instanceof Rating)))
            MediaSessionCompat.MediaSessionImplApi19.this.postToHandler(19, RatingCompat.fromRating(paramObject));
        }
      };
      this.mRcc.setMetadataUpdateListener(local1);
    }
  }

  @RequiresApi(21)
  static class MediaSessionImplApi21
    implements MediaSessionCompat.MediaSessionImpl
  {
    boolean mCaptioningEnabled;
    private boolean mDestroyed = false;
    private final RemoteCallbackList<IMediaControllerCallback> mExtraControllerCallbacks = new RemoteCallbackList();
    private MediaMetadataCompat mMetadata;
    private PlaybackStateCompat mPlaybackState;
    private List<MediaSessionCompat.QueueItem> mQueue;
    int mRatingType;
    int mRepeatMode;
    private final Object mSessionObj;
    int mShuffleMode;
    boolean mShuffleModeEnabled;
    private final MediaSessionCompat.Token mToken;

    public MediaSessionImplApi21(Context paramContext, String paramString)
    {
      this.mSessionObj = MediaSessionCompatApi21.createSession(paramContext, paramString);
      this.mToken = new MediaSessionCompat.Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj), new ExtraSession());
    }

    public MediaSessionImplApi21(Object paramObject)
    {
      this.mSessionObj = MediaSessionCompatApi21.verifySession(paramObject);
      this.mToken = new MediaSessionCompat.Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj), new ExtraSession());
    }

    public String getCallingPackage()
    {
      if (Build.VERSION.SDK_INT < 24)
        return null;
      return MediaSessionCompatApi24.getCallingPackage(this.mSessionObj);
    }

    public Object getMediaSession()
    {
      return this.mSessionObj;
    }

    public PlaybackStateCompat getPlaybackState()
    {
      return this.mPlaybackState;
    }

    public Object getRemoteControlClient()
    {
      return null;
    }

    public MediaSessionCompat.Token getSessionToken()
    {
      return this.mToken;
    }

    public boolean isActive()
    {
      return MediaSessionCompatApi21.isActive(this.mSessionObj);
    }

    public void release()
    {
      this.mDestroyed = true;
      MediaSessionCompatApi21.release(this.mSessionObj);
    }

    public void sendSessionEvent(String paramString, Bundle paramBundle)
    {
      int i;
      if (Build.VERSION.SDK_INT < 23)
        i = -1 + this.mExtraControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mExtraControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onEvent(paramString, paramBundle);
          label44: i--;
          continue;
          this.mExtraControllerCallbacks.finishBroadcast();
          MediaSessionCompatApi21.sendSessionEvent(this.mSessionObj, paramString, paramBundle);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label44;
        }
      }
    }

    public void setActive(boolean paramBoolean)
    {
      MediaSessionCompatApi21.setActive(this.mSessionObj, paramBoolean);
    }

    public void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler)
    {
      Object localObject1 = this.mSessionObj;
      if (paramCallback == null);
      for (Object localObject2 = null; ; localObject2 = paramCallback.mCallbackObj)
      {
        MediaSessionCompatApi21.setCallback(localObject1, localObject2, paramHandler);
        if (paramCallback != null)
          paramCallback.setSessionImpl(this, paramHandler);
        return;
      }
    }

    public void setCaptioningEnabled(boolean paramBoolean)
    {
      int i;
      if (this.mCaptioningEnabled != paramBoolean)
      {
        this.mCaptioningEnabled = paramBoolean;
        i = -1 + this.mExtraControllerCallbacks.beginBroadcast();
      }
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mExtraControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onCaptioningEnabledChanged(paramBoolean);
          label46: i--;
          continue;
          this.mExtraControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label46;
        }
      }
    }

    public void setExtras(Bundle paramBundle)
    {
      MediaSessionCompatApi21.setExtras(this.mSessionObj, paramBundle);
    }

    public void setFlags(int paramInt)
    {
      MediaSessionCompatApi21.setFlags(this.mSessionObj, paramInt);
    }

    public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
    {
      MediaSessionCompatApi21.setMediaButtonReceiver(this.mSessionObj, paramPendingIntent);
    }

    public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      this.mMetadata = paramMediaMetadataCompat;
      Object localObject1 = this.mSessionObj;
      if (paramMediaMetadataCompat == null);
      for (Object localObject2 = null; ; localObject2 = paramMediaMetadataCompat.getMediaMetadata())
      {
        MediaSessionCompatApi21.setMetadata(localObject1, localObject2);
        return;
      }
    }

    public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      this.mPlaybackState = paramPlaybackStateCompat;
      int i = -1 + this.mExtraControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mExtraControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onPlaybackStateChanged(paramPlaybackStateCompat);
          label40: i--;
          continue;
          this.mExtraControllerCallbacks.finishBroadcast();
          Object localObject1 = this.mSessionObj;
          if (paramPlaybackStateCompat == null);
          for (Object localObject2 = null; ; localObject2 = paramPlaybackStateCompat.getPlaybackState())
          {
            MediaSessionCompatApi21.setPlaybackState(localObject1, localObject2);
            return;
          }
        }
        catch (RemoteException localRemoteException)
        {
          break label40;
        }
      }
    }

    public void setPlaybackToLocal(int paramInt)
    {
      MediaSessionCompatApi21.setPlaybackToLocal(this.mSessionObj, paramInt);
    }

    public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
    {
      MediaSessionCompatApi21.setPlaybackToRemote(this.mSessionObj, paramVolumeProviderCompat.getVolumeProvider());
    }

    public void setQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      this.mQueue = paramList;
      ArrayList localArrayList = null;
      if (paramList != null)
      {
        localArrayList = new ArrayList();
        Iterator localIterator = paramList.iterator();
        while (localIterator.hasNext())
          localArrayList.add(((MediaSessionCompat.QueueItem)localIterator.next()).getQueueItem());
      }
      MediaSessionCompatApi21.setQueue(this.mSessionObj, localArrayList);
    }

    public void setQueueTitle(CharSequence paramCharSequence)
    {
      MediaSessionCompatApi21.setQueueTitle(this.mSessionObj, paramCharSequence);
    }

    public void setRatingType(int paramInt)
    {
      if (Build.VERSION.SDK_INT < 22)
      {
        this.mRatingType = paramInt;
        return;
      }
      MediaSessionCompatApi22.setRatingType(this.mSessionObj, paramInt);
    }

    public void setRepeatMode(int paramInt)
    {
      int i;
      if (this.mRepeatMode != paramInt)
      {
        this.mRepeatMode = paramInt;
        i = -1 + this.mExtraControllerCallbacks.beginBroadcast();
      }
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mExtraControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onRepeatModeChanged(paramInt);
          label46: i--;
          continue;
          this.mExtraControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label46;
        }
      }
    }

    public void setSessionActivity(PendingIntent paramPendingIntent)
    {
      MediaSessionCompatApi21.setSessionActivity(this.mSessionObj, paramPendingIntent);
    }

    public void setShuffleMode(int paramInt)
    {
      int i;
      if (this.mShuffleMode != paramInt)
      {
        this.mShuffleMode = paramInt;
        i = -1 + this.mExtraControllerCallbacks.beginBroadcast();
      }
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mExtraControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onShuffleModeChanged(paramInt);
          label46: i--;
          continue;
          this.mExtraControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label46;
        }
      }
    }

    public void setShuffleModeEnabled(boolean paramBoolean)
    {
      int i;
      if (this.mShuffleModeEnabled != paramBoolean)
      {
        this.mShuffleModeEnabled = paramBoolean;
        i = -1 + this.mExtraControllerCallbacks.beginBroadcast();
      }
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mExtraControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onShuffleModeChangedDeprecated(paramBoolean);
          label46: i--;
          continue;
          this.mExtraControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label46;
        }
      }
    }

    class ExtraSession extends IMediaSession.Stub
    {
      ExtraSession()
      {
      }

      public void addQueueItem(MediaDescriptionCompat paramMediaDescriptionCompat)
      {
        throw new AssertionError();
      }

      public void addQueueItemAt(MediaDescriptionCompat paramMediaDescriptionCompat, int paramInt)
      {
        throw new AssertionError();
      }

      public void adjustVolume(int paramInt1, int paramInt2, String paramString)
      {
        throw new AssertionError();
      }

      public void fastForward()
        throws RemoteException
      {
        throw new AssertionError();
      }

      public Bundle getExtras()
      {
        throw new AssertionError();
      }

      public long getFlags()
      {
        throw new AssertionError();
      }

      public PendingIntent getLaunchPendingIntent()
      {
        throw new AssertionError();
      }

      public MediaMetadataCompat getMetadata()
      {
        throw new AssertionError();
      }

      public String getPackageName()
      {
        throw new AssertionError();
      }

      public PlaybackStateCompat getPlaybackState()
      {
        return MediaSessionCompat.access$500(MediaSessionCompat.MediaSessionImplApi21.this.mPlaybackState, MediaSessionCompat.MediaSessionImplApi21.this.mMetadata);
      }

      public List<MediaSessionCompat.QueueItem> getQueue()
      {
        return null;
      }

      public CharSequence getQueueTitle()
      {
        throw new AssertionError();
      }

      public int getRatingType()
      {
        return MediaSessionCompat.MediaSessionImplApi21.this.mRatingType;
      }

      public int getRepeatMode()
      {
        return MediaSessionCompat.MediaSessionImplApi21.this.mRepeatMode;
      }

      public int getShuffleMode()
      {
        return MediaSessionCompat.MediaSessionImplApi21.this.mShuffleMode;
      }

      public String getTag()
      {
        throw new AssertionError();
      }

      public ParcelableVolumeInfo getVolumeAttributes()
      {
        throw new AssertionError();
      }

      public boolean isCaptioningEnabled()
      {
        return MediaSessionCompat.MediaSessionImplApi21.this.mCaptioningEnabled;
      }

      public boolean isShuffleModeEnabledDeprecated()
      {
        return MediaSessionCompat.MediaSessionImplApi21.this.mShuffleModeEnabled;
      }

      public boolean isTransportControlEnabled()
      {
        throw new AssertionError();
      }

      public void next()
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void pause()
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void play()
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void playFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void playFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void playFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void prepare()
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void prepareFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void prepareFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void prepareFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void previous()
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void rate(RatingCompat paramRatingCompat)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void rateWithExtras(RatingCompat paramRatingCompat, Bundle paramBundle)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void registerCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        if (!MediaSessionCompat.MediaSessionImplApi21.this.mDestroyed)
          MediaSessionCompat.MediaSessionImplApi21.this.mExtraControllerCallbacks.register(paramIMediaControllerCallback);
      }

      public void removeQueueItem(MediaDescriptionCompat paramMediaDescriptionCompat)
      {
        throw new AssertionError();
      }

      public void removeQueueItemAt(int paramInt)
      {
        throw new AssertionError();
      }

      public void rewind()
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void seekTo(long paramLong)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void sendCommand(String paramString, Bundle paramBundle, MediaSessionCompat.ResultReceiverWrapper paramResultReceiverWrapper)
      {
        throw new AssertionError();
      }

      public void sendCustomAction(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public boolean sendMediaButton(KeyEvent paramKeyEvent)
      {
        throw new AssertionError();
      }

      public void setCaptioningEnabled(boolean paramBoolean)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void setRepeatMode(int paramInt)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void setShuffleMode(int paramInt)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void setShuffleModeEnabledDeprecated(boolean paramBoolean)
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void setVolumeTo(int paramInt1, int paramInt2, String paramString)
      {
        throw new AssertionError();
      }

      public void skipToQueueItem(long paramLong)
      {
        throw new AssertionError();
      }

      public void stop()
        throws RemoteException
      {
        throw new AssertionError();
      }

      public void unregisterCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        MediaSessionCompat.MediaSessionImplApi21.this.mExtraControllerCallbacks.unregister(paramIMediaControllerCallback);
      }
    }
  }

  static class MediaSessionImplBase
    implements MediaSessionCompat.MediaSessionImpl
  {
    static final int RCC_PLAYSTATE_NONE;
    final AudioManager mAudioManager;
    volatile MediaSessionCompat.Callback mCallback;
    boolean mCaptioningEnabled;
    private final Context mContext;
    final RemoteCallbackList<IMediaControllerCallback> mControllerCallbacks = new RemoteCallbackList();
    boolean mDestroyed = false;
    Bundle mExtras;
    int mFlags;
    private MessageHandler mHandler;
    boolean mIsActive = false;
    private boolean mIsMbrRegistered = false;
    private boolean mIsRccRegistered = false;
    int mLocalStream;
    final Object mLock = new Object();
    private final ComponentName mMediaButtonReceiverComponentName;
    private final PendingIntent mMediaButtonReceiverIntent;
    MediaMetadataCompat mMetadata;
    final String mPackageName;
    List<MediaSessionCompat.QueueItem> mQueue;
    CharSequence mQueueTitle;
    int mRatingType;
    final RemoteControlClient mRcc;
    int mRepeatMode;
    PendingIntent mSessionActivity;
    int mShuffleMode;
    boolean mShuffleModeEnabled;
    PlaybackStateCompat mState;
    private final MediaSessionStub mStub;
    final String mTag;
    private final MediaSessionCompat.Token mToken;
    private VolumeProviderCompat.Callback mVolumeCallback = new VolumeProviderCompat.Callback()
    {
      public void onVolumeChanged(VolumeProviderCompat paramVolumeProviderCompat)
      {
        if (MediaSessionCompat.MediaSessionImplBase.this.mVolumeProvider != paramVolumeProviderCompat)
          return;
        ParcelableVolumeInfo localParcelableVolumeInfo = new ParcelableVolumeInfo(MediaSessionCompat.MediaSessionImplBase.this.mVolumeType, MediaSessionCompat.MediaSessionImplBase.this.mLocalStream, paramVolumeProviderCompat.getVolumeControl(), paramVolumeProviderCompat.getMaxVolume(), paramVolumeProviderCompat.getCurrentVolume());
        MediaSessionCompat.MediaSessionImplBase.this.sendVolumeInfoChanged(localParcelableVolumeInfo);
      }
    };
    VolumeProviderCompat mVolumeProvider;
    int mVolumeType;

    public MediaSessionImplBase(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
    {
      if (paramComponentName == null)
        throw new IllegalArgumentException("MediaButtonReceiver component may not be null.");
      this.mContext = paramContext;
      this.mPackageName = paramContext.getPackageName();
      this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
      this.mTag = paramString;
      this.mMediaButtonReceiverComponentName = paramComponentName;
      this.mMediaButtonReceiverIntent = paramPendingIntent;
      this.mStub = new MediaSessionStub();
      this.mToken = new MediaSessionCompat.Token(this.mStub);
      this.mRatingType = 0;
      this.mVolumeType = 1;
      this.mLocalStream = 3;
      this.mRcc = new RemoteControlClient(paramPendingIntent);
    }

    private void sendCaptioningEnabled(boolean paramBoolean)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onCaptioningEnabledChanged(paramBoolean);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    private void sendEvent(String paramString, Bundle paramBundle)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onEvent(paramString, paramBundle);
          label36: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label36;
        }
      }
    }

    private void sendExtras(Bundle paramBundle)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onExtrasChanged(paramBundle);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    private void sendMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onMetadataChanged(paramMediaMetadataCompat);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    private void sendQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onQueueChanged(paramList);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    private void sendQueueTitle(CharSequence paramCharSequence)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onQueueTitleChanged(paramCharSequence);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    private void sendRepeatMode(int paramInt)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onRepeatModeChanged(paramInt);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    private void sendSessionDestroyed()
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onSessionDestroyed();
          label32: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          this.mControllerCallbacks.kill();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label32;
        }
      }
    }

    private void sendShuffleMode(int paramInt)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onShuffleModeChanged(paramInt);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    private void sendShuffleModeEnabled(boolean paramBoolean)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onShuffleModeChangedDeprecated(paramBoolean);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    private void sendState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onPlaybackStateChanged(paramPlaybackStateCompat);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    void adjustVolume(int paramInt1, int paramInt2)
    {
      if (this.mVolumeType == 2)
      {
        if (this.mVolumeProvider != null)
          this.mVolumeProvider.onAdjustVolume(paramInt1);
        return;
      }
      this.mAudioManager.adjustStreamVolume(this.mLocalStream, paramInt1, paramInt2);
    }

    RemoteControlClient.MetadataEditor buildRccMetadata(Bundle paramBundle)
    {
      RemoteControlClient.MetadataEditor localMetadataEditor = this.mRcc.editMetadata(true);
      if (paramBundle == null)
        return localMetadataEditor;
      if (paramBundle.containsKey("android.media.metadata.ART"))
      {
        Bitmap localBitmap2 = (Bitmap)paramBundle.getParcelable("android.media.metadata.ART");
        if (localBitmap2 != null)
          localBitmap2 = localBitmap2.copy(localBitmap2.getConfig(), false);
        localMetadataEditor.putBitmap(100, localBitmap2);
      }
      while (true)
      {
        if (paramBundle.containsKey("android.media.metadata.ALBUM"))
          localMetadataEditor.putString(1, paramBundle.getString("android.media.metadata.ALBUM"));
        if (paramBundle.containsKey("android.media.metadata.ALBUM_ARTIST"))
          localMetadataEditor.putString(13, paramBundle.getString("android.media.metadata.ALBUM_ARTIST"));
        if (paramBundle.containsKey("android.media.metadata.ARTIST"))
          localMetadataEditor.putString(2, paramBundle.getString("android.media.metadata.ARTIST"));
        if (paramBundle.containsKey("android.media.metadata.AUTHOR"))
          localMetadataEditor.putString(3, paramBundle.getString("android.media.metadata.AUTHOR"));
        if (paramBundle.containsKey("android.media.metadata.COMPILATION"))
          localMetadataEditor.putString(15, paramBundle.getString("android.media.metadata.COMPILATION"));
        if (paramBundle.containsKey("android.media.metadata.COMPOSER"))
          localMetadataEditor.putString(4, paramBundle.getString("android.media.metadata.COMPOSER"));
        if (paramBundle.containsKey("android.media.metadata.DATE"))
          localMetadataEditor.putString(5, paramBundle.getString("android.media.metadata.DATE"));
        if (paramBundle.containsKey("android.media.metadata.DISC_NUMBER"))
          localMetadataEditor.putLong(14, paramBundle.getLong("android.media.metadata.DISC_NUMBER"));
        if (paramBundle.containsKey("android.media.metadata.DURATION"))
          localMetadataEditor.putLong(9, paramBundle.getLong("android.media.metadata.DURATION"));
        if (paramBundle.containsKey("android.media.metadata.GENRE"))
          localMetadataEditor.putString(6, paramBundle.getString("android.media.metadata.GENRE"));
        if (paramBundle.containsKey("android.media.metadata.TITLE"))
          localMetadataEditor.putString(7, paramBundle.getString("android.media.metadata.TITLE"));
        if (paramBundle.containsKey("android.media.metadata.TRACK_NUMBER"))
          localMetadataEditor.putLong(0, paramBundle.getLong("android.media.metadata.TRACK_NUMBER"));
        if (!paramBundle.containsKey("android.media.metadata.WRITER"))
          break;
        localMetadataEditor.putString(11, paramBundle.getString("android.media.metadata.WRITER"));
        return localMetadataEditor;
        if (!paramBundle.containsKey("android.media.metadata.ALBUM_ART"))
          continue;
        Bitmap localBitmap1 = (Bitmap)paramBundle.getParcelable("android.media.metadata.ALBUM_ART");
        if (localBitmap1 != null)
          localBitmap1 = localBitmap1.copy(localBitmap1.getConfig(), false);
        localMetadataEditor.putBitmap(100, localBitmap1);
      }
    }

    public String getCallingPackage()
    {
      return null;
    }

    public Object getMediaSession()
    {
      return null;
    }

    public PlaybackStateCompat getPlaybackState()
    {
      synchronized (this.mLock)
      {
        PlaybackStateCompat localPlaybackStateCompat = this.mState;
        return localPlaybackStateCompat;
      }
    }

    int getRccStateFromState(int paramInt)
    {
      switch (paramInt)
      {
      default:
        return -1;
      case 6:
      case 8:
        return 8;
      case 7:
        return 9;
      case 4:
        return 4;
      case 0:
        return 0;
      case 2:
        return 2;
      case 3:
        return 3;
      case 5:
        return 5;
      case 9:
        return 7;
      case 10:
      case 11:
        return 6;
      case 1:
      }
      return 1;
    }

    int getRccTransportControlFlagsFromActions(long paramLong)
    {
      boolean bool = (1L & paramLong) < 0L;
      int i = 0;
      if (bool)
        i = 0x0 | 0x20;
      if ((0x2 & paramLong) != 0L)
        i |= 16;
      if ((0x4 & paramLong) != 0L)
        i |= 4;
      if ((0x8 & paramLong) != 0L)
        i |= 2;
      if ((0x10 & paramLong) != 0L)
        i |= 1;
      if ((0x20 & paramLong) != 0L)
        i |= 128;
      if ((0x40 & paramLong) != 0L)
        i |= 64;
      if ((0x200 & paramLong) != 0L)
        i |= 8;
      return i;
    }

    public Object getRemoteControlClient()
    {
      return null;
    }

    public MediaSessionCompat.Token getSessionToken()
    {
      return this.mToken;
    }

    public boolean isActive()
    {
      return this.mIsActive;
    }

    void postToHandler(int paramInt)
    {
      postToHandler(paramInt, null);
    }

    void postToHandler(int paramInt1, int paramInt2)
    {
      postToHandler(paramInt1, null, paramInt2);
    }

    void postToHandler(int paramInt, Object paramObject)
    {
      postToHandler(paramInt, paramObject, null);
    }

    void postToHandler(int paramInt1, Object paramObject, int paramInt2)
    {
      synchronized (this.mLock)
      {
        if (this.mHandler != null)
          this.mHandler.post(paramInt1, paramObject, paramInt2);
        return;
      }
    }

    void postToHandler(int paramInt, Object paramObject, Bundle paramBundle)
    {
      synchronized (this.mLock)
      {
        if (this.mHandler != null)
          this.mHandler.post(paramInt, paramObject, paramBundle);
        return;
      }
    }

    void registerMediaButtonEventReceiver(PendingIntent paramPendingIntent, ComponentName paramComponentName)
    {
      this.mAudioManager.registerMediaButtonEventReceiver(paramComponentName);
    }

    public void release()
    {
      this.mIsActive = false;
      this.mDestroyed = true;
      update();
      sendSessionDestroyed();
    }

    public void sendSessionEvent(String paramString, Bundle paramBundle)
    {
      sendEvent(paramString, paramBundle);
    }

    void sendVolumeInfoChanged(ParcelableVolumeInfo paramParcelableVolumeInfo)
    {
      int i = -1 + this.mControllerCallbacks.beginBroadcast();
      while (true)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0)
          localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
        try
        {
          localIMediaControllerCallback.onVolumeInfoChanged(paramParcelableVolumeInfo);
          label33: i--;
          continue;
          this.mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          break label33;
        }
      }
    }

    public void setActive(boolean paramBoolean)
    {
      if (paramBoolean == this.mIsActive);
      do
      {
        return;
        this.mIsActive = paramBoolean;
      }
      while (!update());
      setMetadata(this.mMetadata);
      setPlaybackState(this.mState);
    }

    public void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler)
    {
      this.mCallback = paramCallback;
      if (paramCallback != null)
      {
        if (paramHandler == null)
          paramHandler = new Handler();
        synchronized (this.mLock)
        {
          if (this.mHandler != null)
            this.mHandler.removeCallbacksAndMessages(null);
          this.mHandler = new MessageHandler(paramHandler.getLooper());
          this.mCallback.setSessionImpl(this, paramHandler);
          return;
        }
      }
    }

    public void setCaptioningEnabled(boolean paramBoolean)
    {
      if (this.mCaptioningEnabled != paramBoolean)
      {
        this.mCaptioningEnabled = paramBoolean;
        sendCaptioningEnabled(paramBoolean);
      }
    }

    public void setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
      sendExtras(paramBundle);
    }

    public void setFlags(int paramInt)
    {
      synchronized (this.mLock)
      {
        this.mFlags = paramInt;
        update();
        return;
      }
    }

    public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
    {
    }

    public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      if (paramMediaMetadataCompat != null)
        paramMediaMetadataCompat = new MediaMetadataCompat.Builder(paramMediaMetadataCompat, MediaSessionCompat.sMaxBitmapSize).build();
      synchronized (this.mLock)
      {
        this.mMetadata = paramMediaMetadataCompat;
        sendMetadata(paramMediaMetadataCompat);
        if (!this.mIsActive)
          return;
      }
      if (paramMediaMetadataCompat == null);
      for (Bundle localBundle = null; ; localBundle = paramMediaMetadataCompat.getBundle())
      {
        buildRccMetadata(localBundle).apply();
        return;
      }
    }

    public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      synchronized (this.mLock)
      {
        this.mState = paramPlaybackStateCompat;
        sendState(paramPlaybackStateCompat);
        if (!this.mIsActive)
          return;
      }
      if (paramPlaybackStateCompat == null)
      {
        this.mRcc.setPlaybackState(0);
        this.mRcc.setTransportControlFlags(0);
        return;
      }
      setRccState(paramPlaybackStateCompat);
      this.mRcc.setTransportControlFlags(getRccTransportControlFlagsFromActions(paramPlaybackStateCompat.getActions()));
    }

    public void setPlaybackToLocal(int paramInt)
    {
      if (this.mVolumeProvider != null)
        this.mVolumeProvider.setCallback(null);
      this.mVolumeType = 1;
      sendVolumeInfoChanged(new ParcelableVolumeInfo(this.mVolumeType, this.mLocalStream, 2, this.mAudioManager.getStreamMaxVolume(this.mLocalStream), this.mAudioManager.getStreamVolume(this.mLocalStream)));
    }

    public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
    {
      if (paramVolumeProviderCompat == null)
        throw new IllegalArgumentException("volumeProvider may not be null");
      if (this.mVolumeProvider != null)
        this.mVolumeProvider.setCallback(null);
      this.mVolumeType = 2;
      this.mVolumeProvider = paramVolumeProviderCompat;
      sendVolumeInfoChanged(new ParcelableVolumeInfo(this.mVolumeType, this.mLocalStream, this.mVolumeProvider.getVolumeControl(), this.mVolumeProvider.getMaxVolume(), this.mVolumeProvider.getCurrentVolume()));
      paramVolumeProviderCompat.setCallback(this.mVolumeCallback);
    }

    public void setQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      this.mQueue = paramList;
      sendQueue(paramList);
    }

    public void setQueueTitle(CharSequence paramCharSequence)
    {
      this.mQueueTitle = paramCharSequence;
      sendQueueTitle(paramCharSequence);
    }

    public void setRatingType(int paramInt)
    {
      this.mRatingType = paramInt;
    }

    void setRccState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      this.mRcc.setPlaybackState(getRccStateFromState(paramPlaybackStateCompat.getState()));
    }

    public void setRepeatMode(int paramInt)
    {
      if (this.mRepeatMode != paramInt)
      {
        this.mRepeatMode = paramInt;
        sendRepeatMode(paramInt);
      }
    }

    public void setSessionActivity(PendingIntent paramPendingIntent)
    {
      synchronized (this.mLock)
      {
        this.mSessionActivity = paramPendingIntent;
        return;
      }
    }

    public void setShuffleMode(int paramInt)
    {
      if (this.mShuffleMode != paramInt)
      {
        this.mShuffleMode = paramInt;
        sendShuffleMode(paramInt);
      }
    }

    public void setShuffleModeEnabled(boolean paramBoolean)
    {
      if (this.mShuffleModeEnabled != paramBoolean)
      {
        this.mShuffleModeEnabled = paramBoolean;
        sendShuffleModeEnabled(paramBoolean);
      }
    }

    void setVolumeTo(int paramInt1, int paramInt2)
    {
      if (this.mVolumeType == 2)
      {
        if (this.mVolumeProvider != null)
          this.mVolumeProvider.onSetVolumeTo(paramInt1);
        return;
      }
      this.mAudioManager.setStreamVolume(this.mLocalStream, paramInt1, paramInt2);
    }

    void unregisterMediaButtonEventReceiver(PendingIntent paramPendingIntent, ComponentName paramComponentName)
    {
      this.mAudioManager.unregisterMediaButtonEventReceiver(paramComponentName);
    }

    boolean update()
    {
      int i;
      if (this.mIsActive)
        if ((!this.mIsMbrRegistered) && ((0x1 & this.mFlags) != 0))
        {
          registerMediaButtonEventReceiver(this.mMediaButtonReceiverIntent, this.mMediaButtonReceiverComponentName);
          this.mIsMbrRegistered = true;
          if ((this.mIsRccRegistered) || ((0x2 & this.mFlags) == 0))
            break label112;
          this.mAudioManager.registerRemoteControlClient(this.mRcc);
          this.mIsRccRegistered = true;
          i = 1;
        }
      label112: boolean bool1;
      do
      {
        int j;
        do
        {
          boolean bool2;
          do
          {
            return i;
            if ((!this.mIsMbrRegistered) || ((0x1 & this.mFlags) != 0))
              break;
            unregisterMediaButtonEventReceiver(this.mMediaButtonReceiverIntent, this.mMediaButtonReceiverComponentName);
            this.mIsMbrRegistered = false;
            break;
            bool2 = this.mIsRccRegistered;
            i = 0;
          }
          while (!bool2);
          j = 0x2 & this.mFlags;
          i = 0;
        }
        while (j != 0);
        this.mRcc.setPlaybackState(0);
        this.mAudioManager.unregisterRemoteControlClient(this.mRcc);
        this.mIsRccRegistered = false;
        return false;
        if (this.mIsMbrRegistered)
        {
          unregisterMediaButtonEventReceiver(this.mMediaButtonReceiverIntent, this.mMediaButtonReceiverComponentName);
          this.mIsMbrRegistered = false;
        }
        bool1 = this.mIsRccRegistered;
        i = 0;
      }
      while (!bool1);
      this.mRcc.setPlaybackState(0);
      this.mAudioManager.unregisterRemoteControlClient(this.mRcc);
      this.mIsRccRegistered = false;
      return false;
    }

    private static final class Command
    {
      public final String command;
      public final Bundle extras;
      public final ResultReceiver stub;

      public Command(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
      {
        this.command = paramString;
        this.extras = paramBundle;
        this.stub = paramResultReceiver;
      }
    }

    class MediaSessionStub extends IMediaSession.Stub
    {
      MediaSessionStub()
      {
      }

      public void addQueueItem(MediaDescriptionCompat paramMediaDescriptionCompat)
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(25, paramMediaDescriptionCompat);
      }

      public void addQueueItemAt(MediaDescriptionCompat paramMediaDescriptionCompat, int paramInt)
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(26, paramMediaDescriptionCompat, paramInt);
      }

      public void adjustVolume(int paramInt1, int paramInt2, String paramString)
      {
        MediaSessionCompat.MediaSessionImplBase.this.adjustVolume(paramInt1, paramInt2);
      }

      public void fastForward()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(16);
      }

      public Bundle getExtras()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          Bundle localBundle = MediaSessionCompat.MediaSessionImplBase.this.mExtras;
          return localBundle;
        }
      }

      public long getFlags()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          long l = MediaSessionCompat.MediaSessionImplBase.this.mFlags;
          return l;
        }
      }

      public PendingIntent getLaunchPendingIntent()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          PendingIntent localPendingIntent = MediaSessionCompat.MediaSessionImplBase.this.mSessionActivity;
          return localPendingIntent;
        }
      }

      public MediaMetadataCompat getMetadata()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mMetadata;
      }

      public String getPackageName()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mPackageName;
      }

      public PlaybackStateCompat getPlaybackState()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          PlaybackStateCompat localPlaybackStateCompat = MediaSessionCompat.MediaSessionImplBase.this.mState;
          MediaMetadataCompat localMediaMetadataCompat = MediaSessionCompat.MediaSessionImplBase.this.mMetadata;
          return MediaSessionCompat.access$500(localPlaybackStateCompat, localMediaMetadataCompat);
        }
      }

      public List<MediaSessionCompat.QueueItem> getQueue()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          List localList = MediaSessionCompat.MediaSessionImplBase.this.mQueue;
          return localList;
        }
      }

      public CharSequence getQueueTitle()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mQueueTitle;
      }

      public int getRatingType()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mRatingType;
      }

      public int getRepeatMode()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mRepeatMode;
      }

      public int getShuffleMode()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mShuffleMode;
      }

      public String getTag()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mTag;
      }

      public ParcelableVolumeInfo getVolumeAttributes()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          int i = MediaSessionCompat.MediaSessionImplBase.this.mVolumeType;
          int j = MediaSessionCompat.MediaSessionImplBase.this.mLocalStream;
          VolumeProviderCompat localVolumeProviderCompat = MediaSessionCompat.MediaSessionImplBase.this.mVolumeProvider;
          if (i == 2)
          {
            k = localVolumeProviderCompat.getVolumeControl();
            m = localVolumeProviderCompat.getMaxVolume();
            n = localVolumeProviderCompat.getCurrentVolume();
            return new ParcelableVolumeInfo(i, j, k, m, n);
          }
          int k = 2;
          int m = MediaSessionCompat.MediaSessionImplBase.this.mAudioManager.getStreamMaxVolume(j);
          int n = MediaSessionCompat.MediaSessionImplBase.this.mAudioManager.getStreamVolume(j);
        }
      }

      public boolean isCaptioningEnabled()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mCaptioningEnabled;
      }

      public boolean isShuffleModeEnabledDeprecated()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mShuffleModeEnabled;
      }

      public boolean isTransportControlEnabled()
      {
        return (0x2 & MediaSessionCompat.MediaSessionImplBase.this.mFlags) != 0;
      }

      public void next()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(14);
      }

      public void pause()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(12);
      }

      public void play()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(7);
      }

      public void playFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(8, paramString, paramBundle);
      }

      public void playFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(9, paramString, paramBundle);
      }

      public void playFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(10, paramUri, paramBundle);
      }

      public void prepare()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(3);
      }

      public void prepareFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(4, paramString, paramBundle);
      }

      public void prepareFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(5, paramString, paramBundle);
      }

      public void prepareFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(6, paramUri, paramBundle);
      }

      public void previous()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(15);
      }

      public void rate(RatingCompat paramRatingCompat)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(19, paramRatingCompat);
      }

      public void rateWithExtras(RatingCompat paramRatingCompat, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(31, paramRatingCompat, paramBundle);
      }

      public void registerCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        if (MediaSessionCompat.MediaSessionImplBase.this.mDestroyed);
        try
        {
          paramIMediaControllerCallback.onSessionDestroyed();
          return;
          MediaSessionCompat.MediaSessionImplBase.this.mControllerCallbacks.register(paramIMediaControllerCallback);
          return;
        }
        catch (Exception localException)
        {
        }
      }

      public void removeQueueItem(MediaDescriptionCompat paramMediaDescriptionCompat)
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(27, paramMediaDescriptionCompat);
      }

      public void removeQueueItemAt(int paramInt)
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(28, paramInt);
      }

      public void rewind()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(17);
      }

      public void seekTo(long paramLong)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(18, Long.valueOf(paramLong));
      }

      public void sendCommand(String paramString, Bundle paramBundle, MediaSessionCompat.ResultReceiverWrapper paramResultReceiverWrapper)
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(1, new MediaSessionCompat.MediaSessionImplBase.Command(paramString, paramBundle, MediaSessionCompat.ResultReceiverWrapper.access$400(paramResultReceiverWrapper)));
      }

      public void sendCustomAction(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(20, paramString, paramBundle);
      }

      public boolean sendMediaButton(KeyEvent paramKeyEvent)
      {
        if ((0x1 & MediaSessionCompat.MediaSessionImplBase.this.mFlags) != 0);
        for (int i = 1; ; i = 0)
        {
          if (i != 0)
            MediaSessionCompat.MediaSessionImplBase.this.postToHandler(21, paramKeyEvent);
          return i;
        }
      }

      public void setCaptioningEnabled(boolean paramBoolean)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(29, Boolean.valueOf(paramBoolean));
      }

      public void setRepeatMode(int paramInt)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(23, paramInt);
      }

      public void setShuffleMode(int paramInt)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(30, paramInt);
      }

      public void setShuffleModeEnabledDeprecated(boolean paramBoolean)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(24, Boolean.valueOf(paramBoolean));
      }

      public void setVolumeTo(int paramInt1, int paramInt2, String paramString)
      {
        MediaSessionCompat.MediaSessionImplBase.this.setVolumeTo(paramInt1, paramInt2);
      }

      public void skipToQueueItem(long paramLong)
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(11, Long.valueOf(paramLong));
      }

      public void stop()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(13);
      }

      public void unregisterCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        MediaSessionCompat.MediaSessionImplBase.this.mControllerCallbacks.unregister(paramIMediaControllerCallback);
      }
    }

    class MessageHandler extends Handler
    {
      private static final int KEYCODE_MEDIA_PAUSE = 127;
      private static final int KEYCODE_MEDIA_PLAY = 126;
      private static final int MSG_ADD_QUEUE_ITEM = 25;
      private static final int MSG_ADD_QUEUE_ITEM_AT = 26;
      private static final int MSG_ADJUST_VOLUME = 2;
      private static final int MSG_COMMAND = 1;
      private static final int MSG_CUSTOM_ACTION = 20;
      private static final int MSG_FAST_FORWARD = 16;
      private static final int MSG_MEDIA_BUTTON = 21;
      private static final int MSG_NEXT = 14;
      private static final int MSG_PAUSE = 12;
      private static final int MSG_PLAY = 7;
      private static final int MSG_PLAY_MEDIA_ID = 8;
      private static final int MSG_PLAY_SEARCH = 9;
      private static final int MSG_PLAY_URI = 10;
      private static final int MSG_PREPARE = 3;
      private static final int MSG_PREPARE_MEDIA_ID = 4;
      private static final int MSG_PREPARE_SEARCH = 5;
      private static final int MSG_PREPARE_URI = 6;
      private static final int MSG_PREVIOUS = 15;
      private static final int MSG_RATE = 19;
      private static final int MSG_RATE_EXTRA = 31;
      private static final int MSG_REMOVE_QUEUE_ITEM = 27;
      private static final int MSG_REMOVE_QUEUE_ITEM_AT = 28;
      private static final int MSG_REWIND = 17;
      private static final int MSG_SEEK_TO = 18;
      private static final int MSG_SET_CAPTIONING_ENABLED = 29;
      private static final int MSG_SET_REPEAT_MODE = 23;
      private static final int MSG_SET_SHUFFLE_MODE = 30;
      private static final int MSG_SET_SHUFFLE_MODE_ENABLED = 24;
      private static final int MSG_SET_VOLUME = 22;
      private static final int MSG_SKIP_TO_ITEM = 11;
      private static final int MSG_STOP = 13;

      public MessageHandler(Looper arg2)
      {
        super();
      }

      private void onMediaButtonEvent(KeyEvent paramKeyEvent, MediaSessionCompat.Callback paramCallback)
      {
        if ((paramKeyEvent == null) || (paramKeyEvent.getAction() != 0));
        long l;
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      return;
                      if (MediaSessionCompat.MediaSessionImplBase.this.mState == null)
                        l = 0L;
                      while (true)
                        switch (paramKeyEvent.getKeyCode())
                        {
                        default:
                          return;
                        case 79:
                        case 85:
                          Log.w("MediaSessionCompat", "KEYCODE_MEDIA_PLAY_PAUSE and KEYCODE_HEADSETHOOK are handled already");
                          return;
                          l = MediaSessionCompat.MediaSessionImplBase.this.mState.getActions();
                        case 126:
                        case 127:
                        case 87:
                        case 88:
                        case 86:
                        case 90:
                        case 89:
                        }
                    }
                    while ((0x4 & l) == 0L);
                    paramCallback.onPlay();
                    return;
                  }
                  while ((0x2 & l) == 0L);
                  paramCallback.onPause();
                  return;
                }
                while ((0x20 & l) == 0L);
                paramCallback.onSkipToNext();
                return;
              }
              while ((0x10 & l) == 0L);
              paramCallback.onSkipToPrevious();
              return;
            }
            while ((1L & l) == 0L);
            paramCallback.onStop();
            return;
          }
          while ((0x40 & l) == 0L);
          paramCallback.onFastForward();
          return;
        }
        while ((0x8 & l) == 0L);
        paramCallback.onRewind();
      }

      public void handleMessage(Message paramMessage)
      {
        MediaSessionCompat.Callback localCallback = MediaSessionCompat.MediaSessionImplBase.this.mCallback;
        if (localCallback == null);
        do
        {
          KeyEvent localKeyEvent;
          Intent localIntent;
          do
          {
            return;
            switch (paramMessage.what)
            {
            default:
              return;
            case 1:
              MediaSessionCompat.MediaSessionImplBase.Command localCommand = (MediaSessionCompat.MediaSessionImplBase.Command)paramMessage.obj;
              localCallback.onCommand(localCommand.command, localCommand.extras, localCommand.stub);
              return;
            case 21:
              localKeyEvent = (KeyEvent)paramMessage.obj;
              localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
              localIntent.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent);
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 31:
            case 20:
            case 25:
            case 26:
            case 27:
            case 28:
            case 2:
            case 22:
            case 29:
            case 23:
            case 24:
            case 30:
            }
          }
          while (localCallback.onMediaButtonEvent(localIntent));
          onMediaButtonEvent(localKeyEvent, localCallback);
          return;
          localCallback.onPrepare();
          return;
          localCallback.onPrepareFromMediaId((String)paramMessage.obj, paramMessage.getData());
          return;
          localCallback.onPrepareFromSearch((String)paramMessage.obj, paramMessage.getData());
          return;
          localCallback.onPrepareFromUri((Uri)paramMessage.obj, paramMessage.getData());
          return;
          localCallback.onPlay();
          return;
          localCallback.onPlayFromMediaId((String)paramMessage.obj, paramMessage.getData());
          return;
          localCallback.onPlayFromSearch((String)paramMessage.obj, paramMessage.getData());
          return;
          localCallback.onPlayFromUri((Uri)paramMessage.obj, paramMessage.getData());
          return;
          localCallback.onSkipToQueueItem(((Long)paramMessage.obj).longValue());
          return;
          localCallback.onPause();
          return;
          localCallback.onStop();
          return;
          localCallback.onSkipToNext();
          return;
          localCallback.onSkipToPrevious();
          return;
          localCallback.onFastForward();
          return;
          localCallback.onRewind();
          return;
          localCallback.onSeekTo(((Long)paramMessage.obj).longValue());
          return;
          localCallback.onSetRating((RatingCompat)paramMessage.obj);
          return;
          localCallback.onSetRating((RatingCompat)paramMessage.obj, paramMessage.getData());
          return;
          localCallback.onCustomAction((String)paramMessage.obj, paramMessage.getData());
          return;
          localCallback.onAddQueueItem((MediaDescriptionCompat)paramMessage.obj);
          return;
          localCallback.onAddQueueItem((MediaDescriptionCompat)paramMessage.obj, paramMessage.arg1);
          return;
          localCallback.onRemoveQueueItem((MediaDescriptionCompat)paramMessage.obj);
          return;
        }
        while (MediaSessionCompat.MediaSessionImplBase.this.mQueue == null);
        if ((paramMessage.arg1 >= 0) && (paramMessage.arg1 < MediaSessionCompat.MediaSessionImplBase.this.mQueue.size()));
        for (MediaSessionCompat.QueueItem localQueueItem = (MediaSessionCompat.QueueItem)MediaSessionCompat.MediaSessionImplBase.this.mQueue.get(paramMessage.arg1); localQueueItem != null; localQueueItem = null)
        {
          localCallback.onRemoveQueueItem(localQueueItem.getDescription());
          return;
        }
        MediaSessionCompat.MediaSessionImplBase.this.adjustVolume(paramMessage.arg1, 0);
        return;
        MediaSessionCompat.MediaSessionImplBase.this.setVolumeTo(paramMessage.arg1, 0);
        return;
        localCallback.onSetCaptioningEnabled(((Boolean)paramMessage.obj).booleanValue());
        return;
        localCallback.onSetRepeatMode(paramMessage.arg1);
        return;
        localCallback.onSetShuffleModeEnabled(((Boolean)paramMessage.obj).booleanValue());
        return;
        localCallback.onSetShuffleMode(paramMessage.arg1);
      }

      public void post(int paramInt)
      {
        post(paramInt, null);
      }

      public void post(int paramInt, Object paramObject)
      {
        obtainMessage(paramInt, paramObject).sendToTarget();
      }

      public void post(int paramInt1, Object paramObject, int paramInt2)
      {
        obtainMessage(paramInt1, paramInt2, 0, paramObject).sendToTarget();
      }

      public void post(int paramInt, Object paramObject, Bundle paramBundle)
      {
        Message localMessage = obtainMessage(paramInt, paramObject);
        localMessage.setData(paramBundle);
        localMessage.sendToTarget();
      }
    }
  }

  public static abstract interface OnActiveChangeListener
  {
    public abstract void onActiveChanged();
  }

  public static final class QueueItem
    implements Parcelable
  {
    public static final Parcelable.Creator<QueueItem> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.QueueItem createFromParcel(Parcel paramParcel)
      {
        return new MediaSessionCompat.QueueItem(paramParcel);
      }

      public MediaSessionCompat.QueueItem[] newArray(int paramInt)
      {
        return new MediaSessionCompat.QueueItem[paramInt];
      }
    };
    public static final int UNKNOWN_ID = -1;
    private final MediaDescriptionCompat mDescription;
    private final long mId;
    private Object mItem;

    QueueItem(Parcel paramParcel)
    {
      this.mDescription = ((MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(paramParcel));
      this.mId = paramParcel.readLong();
    }

    public QueueItem(MediaDescriptionCompat paramMediaDescriptionCompat, long paramLong)
    {
      this(null, paramMediaDescriptionCompat, paramLong);
    }

    private QueueItem(Object paramObject, MediaDescriptionCompat paramMediaDescriptionCompat, long paramLong)
    {
      if (paramMediaDescriptionCompat == null)
        throw new IllegalArgumentException("Description cannot be null.");
      if (paramLong == -1L)
        throw new IllegalArgumentException("Id cannot be QueueItem.UNKNOWN_ID");
      this.mDescription = paramMediaDescriptionCompat;
      this.mId = paramLong;
      this.mItem = paramObject;
    }

    public static QueueItem fromQueueItem(Object paramObject)
    {
      if ((paramObject == null) || (Build.VERSION.SDK_INT < 21))
        return null;
      return new QueueItem(paramObject, MediaDescriptionCompat.fromMediaDescription(MediaSessionCompatApi21.QueueItem.getDescription(paramObject)), MediaSessionCompatApi21.QueueItem.getQueueId(paramObject));
    }

    public static List<QueueItem> fromQueueItemList(List<?> paramList)
    {
      ArrayList localArrayList;
      if ((paramList == null) || (Build.VERSION.SDK_INT < 21))
        localArrayList = null;
      while (true)
      {
        return localArrayList;
        localArrayList = new ArrayList();
        Iterator localIterator = paramList.iterator();
        while (localIterator.hasNext())
          localArrayList.add(fromQueueItem(localIterator.next()));
      }
    }

    public int describeContents()
    {
      return 0;
    }

    public MediaDescriptionCompat getDescription()
    {
      return this.mDescription;
    }

    public long getQueueId()
    {
      return this.mId;
    }

    public Object getQueueItem()
    {
      if ((this.mItem != null) || (Build.VERSION.SDK_INT < 21))
        return this.mItem;
      this.mItem = MediaSessionCompatApi21.QueueItem.createItem(this.mDescription.getMediaDescription(), this.mId);
      return this.mItem;
    }

    public String toString()
    {
      return "MediaSession.QueueItem {Description=" + this.mDescription + ", Id=" + this.mId + " }";
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      this.mDescription.writeToParcel(paramParcel, paramInt);
      paramParcel.writeLong(this.mId);
    }
  }

  static final class ResultReceiverWrapper
    implements Parcelable
  {
    public static final Parcelable.Creator<ResultReceiverWrapper> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.ResultReceiverWrapper createFromParcel(Parcel paramParcel)
      {
        return new MediaSessionCompat.ResultReceiverWrapper(paramParcel);
      }

      public MediaSessionCompat.ResultReceiverWrapper[] newArray(int paramInt)
      {
        return new MediaSessionCompat.ResultReceiverWrapper[paramInt];
      }
    };
    private ResultReceiver mResultReceiver;

    ResultReceiverWrapper(Parcel paramParcel)
    {
      this.mResultReceiver = ((ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel));
    }

    public ResultReceiverWrapper(ResultReceiver paramResultReceiver)
    {
      this.mResultReceiver = paramResultReceiver;
    }

    public int describeContents()
    {
      return 0;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      this.mResultReceiver.writeToParcel(paramParcel, paramInt);
    }
  }

  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface SessionFlags
  {
  }

  public static final class Token
    implements Parcelable
  {
    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.Token createFromParcel(Parcel paramParcel)
      {
        if (Build.VERSION.SDK_INT >= 21);
        for (Object localObject = paramParcel.readParcelable(null); ; localObject = paramParcel.readStrongBinder())
          return new MediaSessionCompat.Token(localObject);
      }

      public MediaSessionCompat.Token[] newArray(int paramInt)
      {
        return new MediaSessionCompat.Token[paramInt];
      }
    };
    private final IMediaSession mExtraBinder;
    private final Object mInner;

    Token(Object paramObject)
    {
      this(paramObject, null);
    }

    Token(Object paramObject, IMediaSession paramIMediaSession)
    {
      this.mInner = paramObject;
      this.mExtraBinder = paramIMediaSession;
    }

    public static Token fromToken(Object paramObject)
    {
      return fromToken(paramObject, null);
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public static Token fromToken(Object paramObject, IMediaSession paramIMediaSession)
    {
      if ((paramObject != null) && (Build.VERSION.SDK_INT >= 21))
        return new Token(MediaSessionCompatApi21.verifyToken(paramObject), paramIMediaSession);
      return null;
    }

    public int describeContents()
    {
      return 0;
    }

    public boolean equals(Object paramObject)
    {
      if (this == paramObject);
      Token localToken;
      while (true)
      {
        return true;
        if (!(paramObject instanceof Token))
          return false;
        localToken = (Token)paramObject;
        if (this.mInner != null)
          break;
        if (localToken.mInner != null)
          return false;
      }
      if (localToken.mInner == null)
        return false;
      return this.mInner.equals(localToken.mInner);
    }

    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public IMediaSession getExtraBinder()
    {
      return this.mExtraBinder;
    }

    public Object getToken()
    {
      return this.mInner;
    }

    public int hashCode()
    {
      if (this.mInner == null)
        return 0;
      return this.mInner.hashCode();
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramParcel.writeParcelable((Parcelable)this.mInner, paramInt);
        return;
      }
      paramParcel.writeStrongBinder((IBinder)this.mInner);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.media.session.MediaSessionCompat
 * JD-Core Version:    0.6.0
 */