package okhttp3.internal.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class CacheInterceptor
  implements Interceptor
{
  final InternalCache cache;

  public CacheInterceptor(InternalCache paramInternalCache)
  {
    this.cache = paramInternalCache;
  }

  private Response cacheWritingResponse(CacheRequest paramCacheRequest, Response paramResponse)
    throws IOException
  {
    if (paramCacheRequest == null);
    Sink localSink;
    do
    {
      return paramResponse;
      localSink = paramCacheRequest.body();
    }
    while (localSink == null);
    1 local1 = new Source(paramResponse.body().source(), paramCacheRequest, Okio.buffer(localSink))
    {
      boolean cacheRequestClosed;

      public void close()
        throws IOException
      {
        if ((!this.cacheRequestClosed) && (!Util.discard(this, 100, TimeUnit.MILLISECONDS)))
        {
          this.cacheRequestClosed = true;
          this.val$cacheRequest.abort();
        }
        this.val$source.close();
      }

      public long read(Buffer paramBuffer, long paramLong)
        throws IOException
      {
        long l;
        try
        {
          l = this.val$source.read(paramBuffer, paramLong);
          if (l == -1L)
          {
            if (!this.cacheRequestClosed)
            {
              this.cacheRequestClosed = true;
              this.val$cacheBody.close();
            }
            return -1L;
          }
        }
        catch (IOException localIOException)
        {
          if (!this.cacheRequestClosed)
          {
            this.cacheRequestClosed = true;
            this.val$cacheRequest.abort();
          }
          throw localIOException;
        }
        paramBuffer.copyTo(this.val$cacheBody.buffer(), paramBuffer.size() - l, l);
        this.val$cacheBody.emitCompleteSegments();
        return l;
      }

      public Timeout timeout()
      {
        return this.val$source.timeout();
      }
    };
    return paramResponse.newBuilder().body(new RealResponseBody(paramResponse.headers(), Okio.buffer(local1))).build();
  }

  private static Headers combine(Headers paramHeaders1, Headers paramHeaders2)
  {
    Headers.Builder localBuilder = new Headers.Builder();
    int i = 0;
    int j = paramHeaders1.size();
    if (i < j)
    {
      String str2 = paramHeaders1.name(i);
      String str3 = paramHeaders1.value(i);
      if (("Warning".equalsIgnoreCase(str2)) && (str3.startsWith("1")));
      while (true)
      {
        i++;
        break;
        if ((isEndToEnd(str2)) && (paramHeaders2.get(str2) != null))
          continue;
        Internal.instance.addLenient(localBuilder, str2, str3);
      }
    }
    int k = 0;
    int m = paramHeaders2.size();
    if (k < m)
    {
      String str1 = paramHeaders2.name(k);
      if ("Content-Length".equalsIgnoreCase(str1));
      while (true)
      {
        k++;
        break;
        if (!isEndToEnd(str1))
          continue;
        Internal.instance.addLenient(localBuilder, str1, paramHeaders2.value(k));
      }
    }
    return localBuilder.build();
  }

  static boolean isEndToEnd(String paramString)
  {
    return (!"Connection".equalsIgnoreCase(paramString)) && (!"Keep-Alive".equalsIgnoreCase(paramString)) && (!"Proxy-Authenticate".equalsIgnoreCase(paramString)) && (!"Proxy-Authorization".equalsIgnoreCase(paramString)) && (!"TE".equalsIgnoreCase(paramString)) && (!"Trailers".equalsIgnoreCase(paramString)) && (!"Transfer-Encoding".equalsIgnoreCase(paramString)) && (!"Upgrade".equalsIgnoreCase(paramString));
  }

  private CacheRequest maybeCache(Response paramResponse, Request paramRequest, InternalCache paramInternalCache)
    throws IOException
  {
    if (paramInternalCache == null);
    while (true)
    {
      return null;
      if (CacheStrategy.isCacheable(paramResponse, paramRequest))
        break;
      if (!HttpMethod.invalidatesCache(paramRequest.method()))
        continue;
      try
      {
        paramInternalCache.remove(paramRequest);
        return null;
      }
      catch (IOException localIOException)
      {
        return null;
      }
    }
    return paramInternalCache.put(paramResponse);
  }

  private static Response stripBody(Response paramResponse)
  {
    if ((paramResponse != null) && (paramResponse.body() != null))
      paramResponse = paramResponse.newBuilder().body(null).build();
    return paramResponse;
  }

  public Response intercept(Interceptor.Chain paramChain)
    throws IOException
  {
    Response localResponse1;
    Request localRequest;
    Response localResponse2;
    Response localResponse4;
    if (this.cache != null)
    {
      localResponse1 = this.cache.get(paramChain.request());
      CacheStrategy localCacheStrategy = new CacheStrategy.Factory(System.currentTimeMillis(), paramChain.request(), localResponse1).get();
      localRequest = localCacheStrategy.networkRequest;
      localResponse2 = localCacheStrategy.cacheResponse;
      if (this.cache != null)
        this.cache.trackResponse(localCacheStrategy);
      if ((localResponse1 != null) && (localResponse2 == null))
        Util.closeQuietly(localResponse1.body());
      if ((localRequest != null) || (localResponse2 != null))
        break label163;
      localResponse4 = new Response.Builder().request(paramChain.request()).protocol(Protocol.HTTP_1_1).code(504).message("Unsatisfiable Request (only-if-cached)").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1L).receivedResponseAtMillis(System.currentTimeMillis()).build();
    }
    label163: Response localResponse3;
    label346: 
    do
    {
      return localResponse4;
      localResponse1 = null;
      break;
      if (localRequest == null)
        return localResponse2.newBuilder().cacheResponse(stripBody(localResponse2)).build();
      try
      {
        localResponse3 = paramChain.proceed(localRequest);
        if ((localResponse3 == null) && (localResponse1 != null))
          Util.closeQuietly(localResponse1.body());
        if (localResponse2 == null)
          break label346;
        if (localResponse3.code() == 304)
        {
          Response localResponse5 = localResponse2.newBuilder().headers(combine(localResponse2.headers(), localResponse3.headers())).sentRequestAtMillis(localResponse3.sentRequestAtMillis()).receivedResponseAtMillis(localResponse3.receivedResponseAtMillis()).cacheResponse(stripBody(localResponse2)).networkResponse(stripBody(localResponse3)).build();
          localResponse3.body().close();
          this.cache.trackConditionalCacheHit();
          this.cache.update(localResponse2, localResponse5);
          return localResponse5;
        }
      }
      finally
      {
        if ((0 == 0) && (localResponse1 != null))
          Util.closeQuietly(localResponse1.body());
      }
      Util.closeQuietly(localResponse2.body());
      localResponse4 = localResponse3.newBuilder().cacheResponse(stripBody(localResponse2)).networkResponse(stripBody(localResponse3)).build();
    }
    while (!HttpHeaders.hasBody(localResponse4));
    return cacheWritingResponse(maybeCache(localResponse4, localResponse3.request(), this.cache), localResponse4);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.cache.CacheInterceptor
 * JD-Core Version:    0.6.0
 */