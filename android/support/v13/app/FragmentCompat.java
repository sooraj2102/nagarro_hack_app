package android.support.v13.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import java.util.Arrays;

public class FragmentCompat
{
  static final FragmentCompatImpl IMPL;

  static
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      IMPL = new FragmentCompatApi24Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 23)
    {
      IMPL = new FragmentCompatApi23Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 15)
    {
      IMPL = new FragmentCompatApi15Impl();
      return;
    }
    IMPL = new FragmentCompatBaseImpl();
  }

  public static void requestPermissions(@NonNull Fragment paramFragment, @NonNull String[] paramArrayOfString, int paramInt)
  {
    IMPL.requestPermissions(paramFragment, paramArrayOfString, paramInt);
  }

  @Deprecated
  public static void setMenuVisibility(Fragment paramFragment, boolean paramBoolean)
  {
    paramFragment.setMenuVisibility(paramBoolean);
  }

  public static void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
  {
    IMPL.setUserVisibleHint(paramFragment, paramBoolean);
  }

  public static boolean shouldShowRequestPermissionRationale(@NonNull Fragment paramFragment, @NonNull String paramString)
  {
    return IMPL.shouldShowRequestPermissionRationale(paramFragment, paramString);
  }

  @RequiresApi(15)
  static class FragmentCompatApi15Impl extends FragmentCompat.FragmentCompatBaseImpl
  {
    public void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
    {
      paramFragment.setUserVisibleHint(paramBoolean);
    }
  }

  @RequiresApi(23)
  static class FragmentCompatApi23Impl extends FragmentCompat.FragmentCompatApi15Impl
  {
    public void requestPermissions(Fragment paramFragment, String[] paramArrayOfString, int paramInt)
    {
      paramFragment.requestPermissions(paramArrayOfString, paramInt);
    }

    public boolean shouldShowRequestPermissionRationale(Fragment paramFragment, String paramString)
    {
      return paramFragment.shouldShowRequestPermissionRationale(paramString);
    }
  }

  @RequiresApi(24)
  static class FragmentCompatApi24Impl extends FragmentCompat.FragmentCompatApi23Impl
  {
    public void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
    {
      paramFragment.setUserVisibleHint(paramBoolean);
    }
  }

  static class FragmentCompatBaseImpl
    implements FragmentCompat.FragmentCompatImpl
  {
    public void requestPermissions(Fragment paramFragment, String[] paramArrayOfString, int paramInt)
    {
      new Handler(Looper.getMainLooper()).post(new Runnable(paramArrayOfString, paramFragment, paramInt)
      {
        public void run()
        {
          int[] arrayOfInt = new int[this.val$permissions.length];
          Activity localActivity = this.val$fragment.getActivity();
          if (localActivity != null)
          {
            PackageManager localPackageManager = localActivity.getPackageManager();
            String str = localActivity.getPackageName();
            int i = this.val$permissions.length;
            for (int j = 0; j < i; j++)
              arrayOfInt[j] = localPackageManager.checkPermission(this.val$permissions[j], str);
          }
          Arrays.fill(arrayOfInt, -1);
          ((FragmentCompat.OnRequestPermissionsResultCallback)this.val$fragment).onRequestPermissionsResult(this.val$requestCode, this.val$permissions, arrayOfInt);
        }
      });
    }

    public void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
    {
    }

    public boolean shouldShowRequestPermissionRationale(Fragment paramFragment, String paramString)
    {
      return false;
    }
  }

  static abstract interface FragmentCompatImpl
  {
    public abstract void requestPermissions(Fragment paramFragment, String[] paramArrayOfString, int paramInt);

    public abstract void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean);

    public abstract boolean shouldShowRequestPermissionRationale(Fragment paramFragment, String paramString);
  }

  public static abstract interface OnRequestPermissionsResultCallback
  {
    public abstract void onRequestPermissionsResult(int paramInt, @NonNull String[] paramArrayOfString, @NonNull int[] paramArrayOfInt);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v13.app.FragmentCompat
 * JD-Core Version:    0.6.0
 */