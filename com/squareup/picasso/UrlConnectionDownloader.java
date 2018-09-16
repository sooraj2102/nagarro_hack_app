package com.squareup.picasso;

import android.content.Context;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Build.VERSION;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlConnectionDownloader
  implements Downloader
{
  private static final ThreadLocal<StringBuilder> CACHE_HEADER_BUILDER;
  private static final String FORCE_CACHE = "only-if-cached,max-age=2147483647";
  static final String RESPONSE_SOURCE = "X-Android-Response-Source";
  static volatile Object cache;
  private static final Object lock = new Object();
  private final Context context;

  static
  {
    CACHE_HEADER_BUILDER = new ThreadLocal()
    {
      protected StringBuilder initialValue()
      {
        return new StringBuilder();
      }
    };
  }

  public UrlConnectionDownloader(Context paramContext)
  {
    this.context = paramContext.getApplicationContext();
  }

  private static void installCacheIfNeeded(Context paramContext)
  {
    if (cache == null)
      try
      {
        synchronized (lock)
        {
          if (cache == null)
            cache = ResponseCacheIcs.install(paramContext);
          return;
        }
      }
      catch (IOException localIOException)
      {
      }
  }

  public Downloader.Response load(Uri paramUri, int paramInt)
    throws IOException
  {
    if (Build.VERSION.SDK_INT >= 14)
      installCacheIfNeeded(this.context);
    HttpURLConnection localHttpURLConnection = openConnection(paramUri);
    localHttpURLConnection.setUseCaches(true);
    if (paramInt != 0)
      if (!NetworkPolicy.isOfflineOnly(paramInt))
        break label105;
    label105: StringBuilder localStringBuilder;
    for (String str = "only-if-cached,max-age=2147483647"; ; str = localStringBuilder.toString())
    {
      localHttpURLConnection.setRequestProperty("Cache-Control", str);
      int i = localHttpURLConnection.getResponseCode();
      if (i < 300)
        break;
      localHttpURLConnection.disconnect();
      throw new Downloader.ResponseException(i + " " + localHttpURLConnection.getResponseMessage(), paramInt, i);
      localStringBuilder = (StringBuilder)CACHE_HEADER_BUILDER.get();
      localStringBuilder.setLength(0);
      if (!NetworkPolicy.shouldReadFromDiskCache(paramInt))
        localStringBuilder.append("no-cache");
      if (NetworkPolicy.shouldWriteToDiskCache(paramInt))
        continue;
      if (localStringBuilder.length() > 0)
        localStringBuilder.append(',');
      localStringBuilder.append("no-store");
    }
    long l = localHttpURLConnection.getHeaderFieldInt("Content-Length", -1);
    boolean bool = Utils.parseResponseSourceHeader(localHttpURLConnection.getHeaderField("X-Android-Response-Source"));
    return new Downloader.Response(localHttpURLConnection.getInputStream(), bool, l);
  }

  protected HttpURLConnection openConnection(Uri paramUri)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(paramUri.toString()).openConnection();
    localHttpURLConnection.setConnectTimeout(15000);
    localHttpURLConnection.setReadTimeout(20000);
    return localHttpURLConnection;
  }

  public void shutdown()
  {
    if ((Build.VERSION.SDK_INT >= 14) && (cache != null))
      ResponseCacheIcs.close(cache);
  }

  private static class ResponseCacheIcs
  {
    static void close(Object paramObject)
    {
      try
      {
        ((HttpResponseCache)paramObject).close();
        return;
      }
      catch (IOException localIOException)
      {
      }
    }

    static Object install(Context paramContext)
      throws IOException
    {
      File localFile = Utils.createDefaultCacheDir(paramContext);
      HttpResponseCache localHttpResponseCache = HttpResponseCache.getInstalled();
      if (localHttpResponseCache == null)
        localHttpResponseCache = HttpResponseCache.install(localFile, Utils.calculateDiskCacheSize(localFile));
      return localHttpResponseCache;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.UrlConnectionDownloader
 * JD-Core Version:    0.6.0
 */