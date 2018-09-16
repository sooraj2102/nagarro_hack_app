package com.squareup.picasso;

import android.app.Notification;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

abstract class RemoteViewsAction extends Action<RemoteViewsTarget>
{
  final RemoteViews remoteViews;
  private RemoteViewsTarget target;
  final int viewId;

  RemoteViewsAction(Picasso paramPicasso, Request paramRequest, RemoteViews paramRemoteViews, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, String paramString)
  {
    super(paramPicasso, null, paramRequest, paramInt3, paramInt4, paramInt2, null, paramString, paramObject, false);
    this.remoteViews = paramRemoteViews;
    this.viewId = paramInt1;
  }

  void complete(Bitmap paramBitmap, Picasso.LoadedFrom paramLoadedFrom)
  {
    this.remoteViews.setImageViewBitmap(this.viewId, paramBitmap);
    update();
  }

  public void error()
  {
    if (this.errorResId != 0)
      setImageResource(this.errorResId);
  }

  RemoteViewsTarget getTarget()
  {
    if (this.target == null)
      this.target = new RemoteViewsTarget(this.remoteViews, this.viewId);
    return this.target;
  }

  void setImageResource(int paramInt)
  {
    this.remoteViews.setImageViewResource(this.viewId, paramInt);
    update();
  }

  abstract void update();

  static class AppWidgetAction extends RemoteViewsAction
  {
    private final int[] appWidgetIds;

    AppWidgetAction(Picasso paramPicasso, Request paramRequest, RemoteViews paramRemoteViews, int paramInt1, int[] paramArrayOfInt, int paramInt2, int paramInt3, String paramString, Object paramObject, int paramInt4)
    {
      super(paramRequest, paramRemoteViews, paramInt1, paramInt4, paramInt2, paramInt3, paramObject, paramString);
      this.appWidgetIds = paramArrayOfInt;
    }

    void update()
    {
      AppWidgetManager.getInstance(this.picasso.context).updateAppWidget(this.appWidgetIds, this.remoteViews);
    }
  }

  static class NotificationAction extends RemoteViewsAction
  {
    private final Notification notification;
    private final int notificationId;

    NotificationAction(Picasso paramPicasso, Request paramRequest, RemoteViews paramRemoteViews, int paramInt1, int paramInt2, Notification paramNotification, int paramInt3, int paramInt4, String paramString, Object paramObject, int paramInt5)
    {
      super(paramRequest, paramRemoteViews, paramInt1, paramInt5, paramInt3, paramInt4, paramObject, paramString);
      this.notificationId = paramInt2;
      this.notification = paramNotification;
    }

    void update()
    {
      ((NotificationManager)Utils.getService(this.picasso.context, "notification")).notify(this.notificationId, this.notification);
    }
  }

  static class RemoteViewsTarget
  {
    final RemoteViews remoteViews;
    final int viewId;

    RemoteViewsTarget(RemoteViews paramRemoteViews, int paramInt)
    {
      this.remoteViews = paramRemoteViews;
      this.viewId = paramInt;
    }

    public boolean equals(Object paramObject)
    {
      if (this == paramObject);
      RemoteViewsTarget localRemoteViewsTarget;
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
          return false;
        localRemoteViewsTarget = (RemoteViewsTarget)paramObject;
      }
      while ((this.viewId == localRemoteViewsTarget.viewId) && (this.remoteViews.equals(localRemoteViewsTarget.remoteViews)));
      return false;
    }

    public int hashCode()
    {
      return 31 * this.remoteViews.hashCode() + this.viewId;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.RemoteViewsAction
 * JD-Core Version:    0.6.0
 */