package android.support.v13.view;

import android.app.Activity;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;

public final class DragAndDropPermissionsCompat
{
  private static DragAndDropPermissionsCompatImpl IMPL;
  private Object mDragAndDropPermissions;

  static
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      IMPL = new Api24DragAndDropPermissionsCompatImpl();
      return;
    }
    IMPL = new BaseDragAndDropPermissionsCompatImpl();
  }

  private DragAndDropPermissionsCompat(Object paramObject)
  {
    this.mDragAndDropPermissions = paramObject;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static DragAndDropPermissionsCompat request(Activity paramActivity, DragEvent paramDragEvent)
  {
    Object localObject = IMPL.request(paramActivity, paramDragEvent);
    if (localObject != null)
      return new DragAndDropPermissionsCompat(localObject);
    return null;
  }

  public void release()
  {
    IMPL.release(this.mDragAndDropPermissions);
  }

  @RequiresApi(24)
  static class Api24DragAndDropPermissionsCompatImpl extends DragAndDropPermissionsCompat.BaseDragAndDropPermissionsCompatImpl
  {
    public void release(Object paramObject)
    {
      ((DragAndDropPermissions)paramObject).release();
    }

    public Object request(Activity paramActivity, DragEvent paramDragEvent)
    {
      return paramActivity.requestDragAndDropPermissions(paramDragEvent);
    }
  }

  static class BaseDragAndDropPermissionsCompatImpl
    implements DragAndDropPermissionsCompat.DragAndDropPermissionsCompatImpl
  {
    public void release(Object paramObject)
    {
    }

    public Object request(Activity paramActivity, DragEvent paramDragEvent)
    {
      return null;
    }
  }

  static abstract interface DragAndDropPermissionsCompatImpl
  {
    public abstract void release(Object paramObject);

    public abstract Object request(Activity paramActivity, DragEvent paramDragEvent);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v13.view.DragAndDropPermissionsCompat
 * JD-Core Version:    0.6.0
 */