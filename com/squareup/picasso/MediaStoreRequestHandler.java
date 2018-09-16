package com.squareup.picasso;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video.Thumbnails;
import java.io.IOException;

class MediaStoreRequestHandler extends ContentStreamRequestHandler
{
  private static final String[] CONTENT_ORIENTATION = { "orientation" };

  MediaStoreRequestHandler(Context paramContext)
  {
    super(paramContext);
  }

  static int getExifOrientation(ContentResolver paramContentResolver, Uri paramUri)
  {
    Cursor localCursor = null;
    try
    {
      localCursor = paramContentResolver.query(paramUri, CONTENT_ORIENTATION, null, null, null);
      if (localCursor != null)
      {
        boolean bool = localCursor.moveToFirst();
        if (bool);
      }
      else
      {
        if (localCursor != null)
          localCursor.close();
        j = 0;
        return j;
      }
      int i = localCursor.getInt(0);
      int j = i;
      return j;
    }
    catch (RuntimeException localRuntimeException)
    {
      return 0;
    }
    finally
    {
      if (localCursor != null)
        localCursor.close();
    }
    throw localObject;
  }

  static PicassoKind getPicassoKind(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= PicassoKind.MICRO.width) && (paramInt2 <= PicassoKind.MICRO.height))
      return PicassoKind.MICRO;
    if ((paramInt1 <= PicassoKind.MINI.width) && (paramInt2 <= PicassoKind.MINI.height))
      return PicassoKind.MINI;
    return PicassoKind.FULL;
  }

  public boolean canHandleRequest(Request paramRequest)
  {
    Uri localUri = paramRequest.uri;
    return ("content".equals(localUri.getScheme())) && ("media".equals(localUri.getAuthority()));
  }

  public RequestHandler.Result load(Request paramRequest, int paramInt)
    throws IOException
  {
    ContentResolver localContentResolver = this.context.getContentResolver();
    int i = getExifOrientation(localContentResolver, paramRequest.uri);
    String str = localContentResolver.getType(paramRequest.uri);
    int j;
    if ((str != null) && (str.startsWith("video/")))
      j = 1;
    while (paramRequest.hasSize())
    {
      PicassoKind localPicassoKind = getPicassoKind(paramRequest.targetWidth, paramRequest.targetHeight);
      if ((j == 0) && (localPicassoKind == PicassoKind.FULL))
      {
        return new RequestHandler.Result(null, getInputStream(paramRequest), Picasso.LoadedFrom.DISK, i);
        j = 0;
        continue;
      }
      long l = ContentUris.parseId(paramRequest.uri);
      BitmapFactory.Options localOptions = createBitmapOptions(paramRequest);
      localOptions.inJustDecodeBounds = true;
      calculateInSampleSize(paramRequest.targetWidth, paramRequest.targetHeight, localPicassoKind.width, localPicassoKind.height, localOptions, paramRequest);
      int k;
      if (j != 0)
        if (localPicassoKind == PicassoKind.FULL)
          k = 1;
      for (Bitmap localBitmap = MediaStore.Video.Thumbnails.getThumbnail(localContentResolver, l, k, localOptions); ; localBitmap = MediaStore.Images.Thumbnails.getThumbnail(localContentResolver, l, localPicassoKind.androidKind, localOptions))
      {
        if (localBitmap == null)
          break label226;
        return new RequestHandler.Result(localBitmap, null, Picasso.LoadedFrom.DISK, i);
        k = localPicassoKind.androidKind;
        break;
      }
    }
    label226: return new RequestHandler.Result(null, getInputStream(paramRequest), Picasso.LoadedFrom.DISK, i);
  }

  static enum PicassoKind
  {
    final int androidKind;
    final int height;
    final int width;

    static
    {
      FULL = new PicassoKind("FULL", 2, 2, -1, -1);
      PicassoKind[] arrayOfPicassoKind = new PicassoKind[3];
      arrayOfPicassoKind[0] = MICRO;
      arrayOfPicassoKind[1] = MINI;
      arrayOfPicassoKind[2] = FULL;
      $VALUES = arrayOfPicassoKind;
    }

    private PicassoKind(int paramInt1, int paramInt2, int paramInt3)
    {
      this.androidKind = paramInt1;
      this.width = paramInt2;
      this.height = paramInt3;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.MediaStoreRequestHandler
 * JD-Core Version:    0.6.0
 */