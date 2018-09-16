package com.squareup.picasso;

import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;

class NetworkRequestHandler extends RequestHandler
{
  static final int RETRY_COUNT = 2;
  private static final String SCHEME_HTTP = "http";
  private static final String SCHEME_HTTPS = "https";
  private final Downloader downloader;
  private final Stats stats;

  public NetworkRequestHandler(Downloader paramDownloader, Stats paramStats)
  {
    this.downloader = paramDownloader;
    this.stats = paramStats;
  }

  public boolean canHandleRequest(Request paramRequest)
  {
    String str = paramRequest.uri.getScheme();
    return ("http".equals(str)) || ("https".equals(str));
  }

  int getRetryCount()
  {
    return 2;
  }

  public RequestHandler.Result load(Request paramRequest, int paramInt)
    throws IOException
  {
    Downloader.Response localResponse = this.downloader.load(paramRequest.uri, paramRequest.networkPolicy);
    if (localResponse == null);
    Picasso.LoadedFrom localLoadedFrom;
    InputStream localInputStream;
    do
    {
      return null;
      if (localResponse.cached);
      for (localLoadedFrom = Picasso.LoadedFrom.DISK; ; localLoadedFrom = Picasso.LoadedFrom.NETWORK)
      {
        Bitmap localBitmap = localResponse.getBitmap();
        if (localBitmap == null)
          break;
        return new RequestHandler.Result(localBitmap, localLoadedFrom);
      }
      localInputStream = localResponse.getInputStream();
    }
    while (localInputStream == null);
    if ((localLoadedFrom == Picasso.LoadedFrom.DISK) && (localResponse.getContentLength() == 0L))
    {
      Utils.closeQuietly(localInputStream);
      throw new ContentLengthException("Received response with 0 content-length header.");
    }
    if ((localLoadedFrom == Picasso.LoadedFrom.NETWORK) && (localResponse.getContentLength() > 0L))
      this.stats.dispatchDownloadFinished(localResponse.getContentLength());
    return new RequestHandler.Result(localInputStream, localLoadedFrom);
  }

  boolean shouldRetry(boolean paramBoolean, NetworkInfo paramNetworkInfo)
  {
    return (paramNetworkInfo == null) || (paramNetworkInfo.isConnected());
  }

  boolean supportsReplay()
  {
    return true;
  }

  static class ContentLengthException extends IOException
  {
    public ContentLengthException(String paramString)
    {
      super();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.NetworkRequestHandler
 * JD-Core Version:    0.6.0
 */