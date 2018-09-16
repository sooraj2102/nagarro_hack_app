package android.support.customtabs;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.BundleCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import java.util.ArrayList;

public final class CustomTabsIntent
{
  public static final String EXTRA_ACTION_BUTTON_BUNDLE = "android.support.customtabs.extra.ACTION_BUTTON_BUNDLE";
  public static final String EXTRA_CLOSE_BUTTON_ICON = "android.support.customtabs.extra.CLOSE_BUTTON_ICON";
  public static final String EXTRA_DEFAULT_SHARE_MENU_ITEM = "android.support.customtabs.extra.SHARE_MENU_ITEM";
  public static final String EXTRA_ENABLE_INSTANT_APPS = "android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS";
  public static final String EXTRA_ENABLE_URLBAR_HIDING = "android.support.customtabs.extra.ENABLE_URLBAR_HIDING";
  public static final String EXTRA_EXIT_ANIMATION_BUNDLE = "android.support.customtabs.extra.EXIT_ANIMATION_BUNDLE";
  public static final String EXTRA_MENU_ITEMS = "android.support.customtabs.extra.MENU_ITEMS";
  public static final String EXTRA_REMOTEVIEWS = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS";
  public static final String EXTRA_REMOTEVIEWS_CLICKED_ID = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_CLICKED_ID";
  public static final String EXTRA_REMOTEVIEWS_PENDINGINTENT = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT";
  public static final String EXTRA_REMOTEVIEWS_VIEW_IDS = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS";
  public static final String EXTRA_SECONDARY_TOOLBAR_COLOR = "android.support.customtabs.extra.SECONDARY_TOOLBAR_COLOR";
  public static final String EXTRA_SESSION = "android.support.customtabs.extra.SESSION";
  public static final String EXTRA_TINT_ACTION_BUTTON = "android.support.customtabs.extra.TINT_ACTION_BUTTON";
  public static final String EXTRA_TITLE_VISIBILITY_STATE = "android.support.customtabs.extra.TITLE_VISIBILITY";
  public static final String EXTRA_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
  public static final String EXTRA_TOOLBAR_ITEMS = "android.support.customtabs.extra.TOOLBAR_ITEMS";
  private static final String EXTRA_USER_OPT_OUT_FROM_CUSTOM_TABS = "android.support.customtabs.extra.user_opt_out";
  public static final String KEY_DESCRIPTION = "android.support.customtabs.customaction.DESCRIPTION";
  public static final String KEY_ICON = "android.support.customtabs.customaction.ICON";
  public static final String KEY_ID = "android.support.customtabs.customaction.ID";
  public static final String KEY_MENU_ITEM_TITLE = "android.support.customtabs.customaction.MENU_ITEM_TITLE";
  public static final String KEY_PENDING_INTENT = "android.support.customtabs.customaction.PENDING_INTENT";
  private static final int MAX_TOOLBAR_ITEMS = 5;
  public static final int NO_TITLE = 0;
  public static final int SHOW_PAGE_TITLE = 1;
  public static final int TOOLBAR_ACTION_BUTTON_ID;

  @NonNull
  public final Intent intent;

  @Nullable
  public final Bundle startAnimationBundle;

  private CustomTabsIntent(Intent paramIntent, Bundle paramBundle)
  {
    this.intent = paramIntent;
    this.startAnimationBundle = paramBundle;
  }

  public static int getMaxToolbarItems()
  {
    return 5;
  }

  public static Intent setAlwaysUseBrowserUI(Intent paramIntent)
  {
    if (paramIntent == null)
      paramIntent = new Intent("android.intent.action.VIEW");
    paramIntent.addFlags(268435456);
    paramIntent.putExtra("android.support.customtabs.extra.user_opt_out", true);
    return paramIntent;
  }

  public static boolean shouldAlwaysUseBrowserUI(Intent paramIntent)
  {
    boolean bool = paramIntent.getBooleanExtra("android.support.customtabs.extra.user_opt_out", false);
    int i = 0;
    if (bool)
    {
      int j = 0x10000000 & paramIntent.getFlags();
      i = 0;
      if (j != 0)
        i = 1;
    }
    return i;
  }

  public void launchUrl(Context paramContext, Uri paramUri)
  {
    this.intent.setData(paramUri);
    ContextCompat.startActivity(paramContext, this.intent, this.startAnimationBundle);
  }

  public static final class Builder
  {
    private ArrayList<Bundle> mActionButtons = null;
    private boolean mInstantAppsEnabled = true;
    private final Intent mIntent = new Intent("android.intent.action.VIEW");
    private ArrayList<Bundle> mMenuItems = null;
    private Bundle mStartAnimationBundle = null;

    public Builder()
    {
      this(null);
    }

    public Builder(@Nullable CustomTabsSession paramCustomTabsSession)
    {
      if (paramCustomTabsSession != null)
        this.mIntent.setPackage(paramCustomTabsSession.getComponentName().getPackageName());
      Bundle localBundle = new Bundle();
      IBinder localIBinder = null;
      if (paramCustomTabsSession == null);
      while (true)
      {
        BundleCompat.putBinder(localBundle, "android.support.customtabs.extra.SESSION", localIBinder);
        this.mIntent.putExtras(localBundle);
        return;
        localIBinder = paramCustomTabsSession.getBinder();
      }
    }

    public Builder addDefaultShareMenuItem()
    {
      this.mIntent.putExtra("android.support.customtabs.extra.SHARE_MENU_ITEM", true);
      return this;
    }

    public Builder addMenuItem(@NonNull String paramString, @NonNull PendingIntent paramPendingIntent)
    {
      if (this.mMenuItems == null)
        this.mMenuItems = new ArrayList();
      Bundle localBundle = new Bundle();
      localBundle.putString("android.support.customtabs.customaction.MENU_ITEM_TITLE", paramString);
      localBundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", paramPendingIntent);
      this.mMenuItems.add(localBundle);
      return this;
    }

    @Deprecated
    public Builder addToolbarItem(int paramInt, @NonNull Bitmap paramBitmap, @NonNull String paramString, PendingIntent paramPendingIntent)
      throws IllegalStateException
    {
      if (this.mActionButtons == null)
        this.mActionButtons = new ArrayList();
      if (this.mActionButtons.size() >= 5)
        throw new IllegalStateException("Exceeded maximum toolbar item count of 5");
      Bundle localBundle = new Bundle();
      localBundle.putInt("android.support.customtabs.customaction.ID", paramInt);
      localBundle.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
      localBundle.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
      localBundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", paramPendingIntent);
      this.mActionButtons.add(localBundle);
      return this;
    }

    public CustomTabsIntent build()
    {
      if (this.mMenuItems != null)
        this.mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.MENU_ITEMS", this.mMenuItems);
      if (this.mActionButtons != null)
        this.mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.TOOLBAR_ITEMS", this.mActionButtons);
      this.mIntent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", this.mInstantAppsEnabled);
      return new CustomTabsIntent(this.mIntent, this.mStartAnimationBundle, null);
    }

    public Builder enableUrlBarHiding()
    {
      this.mIntent.putExtra("android.support.customtabs.extra.ENABLE_URLBAR_HIDING", true);
      return this;
    }

    public Builder setActionButton(@NonNull Bitmap paramBitmap, @NonNull String paramString, @NonNull PendingIntent paramPendingIntent)
    {
      return setActionButton(paramBitmap, paramString, paramPendingIntent, false);
    }

    public Builder setActionButton(@NonNull Bitmap paramBitmap, @NonNull String paramString, @NonNull PendingIntent paramPendingIntent, boolean paramBoolean)
    {
      Bundle localBundle = new Bundle();
      localBundle.putInt("android.support.customtabs.customaction.ID", 0);
      localBundle.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
      localBundle.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
      localBundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", paramPendingIntent);
      this.mIntent.putExtra("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", localBundle);
      this.mIntent.putExtra("android.support.customtabs.extra.TINT_ACTION_BUTTON", paramBoolean);
      return this;
    }

    public Builder setCloseButtonIcon(@NonNull Bitmap paramBitmap)
    {
      this.mIntent.putExtra("android.support.customtabs.extra.CLOSE_BUTTON_ICON", paramBitmap);
      return this;
    }

    public Builder setExitAnimations(@NonNull Context paramContext, @AnimRes int paramInt1, @AnimRes int paramInt2)
    {
      Bundle localBundle = ActivityOptionsCompat.makeCustomAnimation(paramContext, paramInt1, paramInt2).toBundle();
      this.mIntent.putExtra("android.support.customtabs.extra.EXIT_ANIMATION_BUNDLE", localBundle);
      return this;
    }

    public Builder setInstantAppsEnabled(boolean paramBoolean)
    {
      this.mInstantAppsEnabled = paramBoolean;
      return this;
    }

    public Builder setSecondaryToolbarColor(@ColorInt int paramInt)
    {
      this.mIntent.putExtra("android.support.customtabs.extra.SECONDARY_TOOLBAR_COLOR", paramInt);
      return this;
    }

    public Builder setSecondaryToolbarViews(@NonNull RemoteViews paramRemoteViews, @Nullable int[] paramArrayOfInt, @Nullable PendingIntent paramPendingIntent)
    {
      this.mIntent.putExtra("android.support.customtabs.extra.EXTRA_REMOTEVIEWS", paramRemoteViews);
      this.mIntent.putExtra("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS", paramArrayOfInt);
      this.mIntent.putExtra("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT", paramPendingIntent);
      return this;
    }

    public Builder setShowTitle(boolean paramBoolean)
    {
      Intent localIntent = this.mIntent;
      if (paramBoolean);
      for (int i = 1; ; i = 0)
      {
        localIntent.putExtra("android.support.customtabs.extra.TITLE_VISIBILITY", i);
        return this;
      }
    }

    public Builder setStartAnimations(@NonNull Context paramContext, @AnimRes int paramInt1, @AnimRes int paramInt2)
    {
      this.mStartAnimationBundle = ActivityOptionsCompat.makeCustomAnimation(paramContext, paramInt1, paramInt2).toBundle();
      return this;
    }

    public Builder setToolbarColor(@ColorInt int paramInt)
    {
      this.mIntent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", paramInt);
      return this;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.customtabs.CustomTabsIntent
 * JD-Core Version:    0.6.0
 */