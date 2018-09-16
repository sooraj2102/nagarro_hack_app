package android.support.v13.app;

import android.app.Activity;
import android.support.v13.view.DragAndDropPermissionsCompat;
import android.view.DragEvent;

public class ActivityCompat extends android.support.v4.app.ActivityCompat
{
  public static DragAndDropPermissionsCompat requestDragAndDropPermissions(Activity paramActivity, DragEvent paramDragEvent)
  {
    return DragAndDropPermissionsCompat.request(paramActivity, paramDragEvent);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v13.app.ActivityCompat
 * JD-Core Version:    0.6.0
 */