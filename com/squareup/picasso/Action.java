package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

abstract class Action<T>
{
  boolean cancelled;
  final Drawable errorDrawable;
  final int errorResId;
  final String key;
  final int memoryPolicy;
  final int networkPolicy;
  final boolean noFade;
  final Picasso picasso;
  final Request request;
  final Object tag;
  final WeakReference<T> target;
  boolean willReplay;

  Action(Picasso paramPicasso, T paramT, Request paramRequest, int paramInt1, int paramInt2, int paramInt3, Drawable paramDrawable, String paramString, Object paramObject, boolean paramBoolean)
  {
    this.picasso = paramPicasso;
    this.request = paramRequest;
    RequestWeakReference localRequestWeakReference;
    if (paramT == null)
    {
      localRequestWeakReference = null;
      this.target = localRequestWeakReference;
      this.memoryPolicy = paramInt1;
      this.networkPolicy = paramInt2;
      this.noFade = paramBoolean;
      this.errorResId = paramInt3;
      this.errorDrawable = paramDrawable;
      this.key = paramString;
      if (paramObject == null)
        break label93;
    }
    while (true)
    {
      this.tag = paramObject;
      return;
      localRequestWeakReference = new RequestWeakReference(this, paramT, paramPicasso.referenceQueue);
      break;
      label93: paramObject = this;
    }
  }

  void cancel()
  {
    this.cancelled = true;
  }

  abstract void complete(Bitmap paramBitmap, Picasso.LoadedFrom paramLoadedFrom);

  abstract void error();

  String getKey()
  {
    return this.key;
  }

  int getMemoryPolicy()
  {
    return this.memoryPolicy;
  }

  int getNetworkPolicy()
  {
    return this.networkPolicy;
  }

  Picasso getPicasso()
  {
    return this.picasso;
  }

  Picasso.Priority getPriority()
  {
    return this.request.priority;
  }

  Request getRequest()
  {
    return this.request;
  }

  Object getTag()
  {
    return this.tag;
  }

  T getTarget()
  {
    if (this.target == null)
      return null;
    return this.target.get();
  }

  boolean isCancelled()
  {
    return this.cancelled;
  }

  boolean willReplay()
  {
    return this.willReplay;
  }

  static class RequestWeakReference<M> extends WeakReference<M>
  {
    final Action action;

    public RequestWeakReference(Action paramAction, M paramM, ReferenceQueue<? super M> paramReferenceQueue)
    {
      super(paramReferenceQueue);
      this.action = paramAction;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.Action
 * JD-Core Version:    0.6.0
 */