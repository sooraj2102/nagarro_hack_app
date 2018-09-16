package com.squareup.picasso;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import java.io.IOException;
import java.util.List;

class AssetRequestHandler extends RequestHandler
{
  protected static final String ANDROID_ASSET = "android_asset";
  private static final int ASSET_PREFIX_LENGTH = "file:///android_asset/".length();
  private final AssetManager assetManager;

  public AssetRequestHandler(Context paramContext)
  {
    this.assetManager = paramContext.getAssets();
  }

  static String getFilePath(Request paramRequest)
  {
    return paramRequest.uri.toString().substring(ASSET_PREFIX_LENGTH);
  }

  public boolean canHandleRequest(Request paramRequest)
  {
    Uri localUri = paramRequest.uri;
    boolean bool1 = "file".equals(localUri.getScheme());
    int i = 0;
    if (bool1)
    {
      boolean bool2 = localUri.getPathSegments().isEmpty();
      i = 0;
      if (!bool2)
      {
        boolean bool3 = "android_asset".equals(localUri.getPathSegments().get(0));
        i = 0;
        if (bool3)
          i = 1;
      }
    }
    return i;
  }

  public RequestHandler.Result load(Request paramRequest, int paramInt)
    throws IOException
  {
    return new RequestHandler.Result(this.assetManager.open(getFilePath(paramRequest)), Picasso.LoadedFrom.DISK);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.AssetRequestHandler
 * JD-Core Version:    0.6.0
 */