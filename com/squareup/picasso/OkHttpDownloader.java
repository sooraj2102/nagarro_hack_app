package com.squareup.picasso;

import android.content.Context;
import android.net.Uri;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.CacheControl.Builder;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpDownloader
  implements Downloader
{
  private final OkHttpClient client;

  public OkHttpDownloader(Context paramContext)
  {
    this(Utils.createDefaultCacheDir(paramContext));
  }

  public OkHttpDownloader(Context paramContext, long paramLong)
  {
    this(Utils.createDefaultCacheDir(paramContext), paramLong);
  }

  public OkHttpDownloader(OkHttpClient paramOkHttpClient)
  {
    this.client = paramOkHttpClient;
  }

  public OkHttpDownloader(File paramFile)
  {
    this(paramFile, Utils.calculateDiskCacheSize(paramFile));
  }

  public OkHttpDownloader(File paramFile, long paramLong)
  {
    this(defaultOkHttpClient());
    try
    {
      this.client.setCache(new Cache(paramFile, paramLong));
      return;
    }
    catch (IOException localIOException)
    {
    }
  }

  private static OkHttpClient defaultOkHttpClient()
  {
    OkHttpClient localOkHttpClient = new OkHttpClient();
    localOkHttpClient.setConnectTimeout(15000L, TimeUnit.MILLISECONDS);
    localOkHttpClient.setReadTimeout(20000L, TimeUnit.MILLISECONDS);
    localOkHttpClient.setWriteTimeout(20000L, TimeUnit.MILLISECONDS);
    return localOkHttpClient;
  }

  protected final OkHttpClient getClient()
  {
    return this.client;
  }

  public Downloader.Response load(Uri paramUri, int paramInt)
    throws IOException
  {
    CacheControl localCacheControl = null;
    if (paramInt != 0)
      if (!NetworkPolicy.isOfflineOnly(paramInt))
        break label123;
    Response localResponse;
    label123: CacheControl.Builder localBuilder1;
    for (localCacheControl = CacheControl.FORCE_CACHE; ; localCacheControl = localBuilder1.build())
    {
      Request.Builder localBuilder = new Request.Builder().url(paramUri.toString());
      if (localCacheControl != null)
        localBuilder.cacheControl(localCacheControl);
      localResponse = this.client.newCall(localBuilder.build()).execute();
      int i = localResponse.code();
      if (i < 300)
        break;
      localResponse.body().close();
      throw new Downloader.ResponseException(i + " " + localResponse.message(), paramInt, i);
      localBuilder1 = new CacheControl.Builder();
      if (!NetworkPolicy.shouldReadFromDiskCache(paramInt))
        localBuilder1.noCache();
      if (NetworkPolicy.shouldWriteToDiskCache(paramInt))
        continue;
      localBuilder1.noStore();
    }
    if (localResponse.cacheResponse() != null);
    for (boolean bool = true; ; bool = false)
    {
      ResponseBody localResponseBody = localResponse.body();
      return new Downloader.Response(localResponseBody.byteStream(), bool, localResponseBody.contentLength());
    }
  }

  public void shutdown()
  {
    Cache localCache = this.client.getCache();
    if (localCache != null);
    try
    {
      localCache.close();
      return;
    }
    catch (IOException localIOException)
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.OkHttpDownloader
 * JD-Core Version:    0.6.0
 */