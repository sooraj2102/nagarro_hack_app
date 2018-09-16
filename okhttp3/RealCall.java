package okhttp3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.internal.connection.ConnectInterceptor;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.BridgeInterceptor;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import okhttp3.internal.platform.Platform;

final class RealCall
  implements Call
{
  final OkHttpClient client;
  private boolean executed;
  final boolean forWebSocket;
  final Request originalRequest;
  final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;

  RealCall(OkHttpClient paramOkHttpClient, Request paramRequest, boolean paramBoolean)
  {
    this.client = paramOkHttpClient;
    this.originalRequest = paramRequest;
    this.forWebSocket = paramBoolean;
    this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(paramOkHttpClient, paramBoolean);
  }

  private void captureCallStackTrace()
  {
    Object localObject = Platform.get().getStackTraceForCloseable("response.body().close()");
    this.retryAndFollowUpInterceptor.setCallStackTrace(localObject);
  }

  public void cancel()
  {
    this.retryAndFollowUpInterceptor.cancel();
  }

  public RealCall clone()
  {
    return new RealCall(this.client, this.originalRequest, this.forWebSocket);
  }

  public void enqueue(Callback paramCallback)
  {
    monitorenter;
    try
    {
      if (this.executed)
        throw new IllegalStateException("Already Executed");
    }
    finally
    {
      monitorexit;
    }
    this.executed = true;
    monitorexit;
    captureCallStackTrace();
    this.client.dispatcher().enqueue(new AsyncCall(paramCallback));
  }

  public Response execute()
    throws IOException
  {
    monitorenter;
    try
    {
      if (this.executed)
        throw new IllegalStateException("Already Executed");
    }
    finally
    {
      monitorexit;
    }
    this.executed = true;
    monitorexit;
    captureCallStackTrace();
    Response localResponse;
    try
    {
      this.client.dispatcher().executed(this);
      localResponse = getResponseWithInterceptorChain();
      if (localResponse == null)
        throw new IOException("Canceled");
    }
    finally
    {
      this.client.dispatcher().finished(this);
    }
    this.client.dispatcher().finished(this);
    return localResponse;
  }

  Response getResponseWithInterceptorChain()
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.client.interceptors());
    localArrayList.add(this.retryAndFollowUpInterceptor);
    localArrayList.add(new BridgeInterceptor(this.client.cookieJar()));
    localArrayList.add(new CacheInterceptor(this.client.internalCache()));
    localArrayList.add(new ConnectInterceptor(this.client));
    if (!this.forWebSocket)
      localArrayList.addAll(this.client.networkInterceptors());
    localArrayList.add(new CallServerInterceptor(this.forWebSocket));
    return new RealInterceptorChain(localArrayList, null, null, null, 0, this.originalRequest).proceed(this.originalRequest);
  }

  public boolean isCanceled()
  {
    return this.retryAndFollowUpInterceptor.isCanceled();
  }

  public boolean isExecuted()
  {
    monitorenter;
    try
    {
      boolean bool = this.executed;
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  String redactedUrl()
  {
    return this.originalRequest.url().redact();
  }

  public Request request()
  {
    return this.originalRequest;
  }

  StreamAllocation streamAllocation()
  {
    return this.retryAndFollowUpInterceptor.streamAllocation();
  }

  String toLoggableString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    String str1;
    StringBuilder localStringBuilder2;
    if (isCanceled())
    {
      str1 = "canceled ";
      localStringBuilder2 = localStringBuilder1.append(str1);
      if (!this.forWebSocket)
        break label63;
    }
    label63: for (String str2 = "web socket"; ; str2 = "call")
    {
      return str2 + " to " + redactedUrl();
      str1 = "";
      break;
    }
  }

  final class AsyncCall extends NamedRunnable
  {
    private final Callback responseCallback;

    AsyncCall(Callback arg2)
    {
      super(arrayOfObject);
      Object localObject;
      this.responseCallback = localObject;
    }

    protected void execute()
    {
      int i = 0;
      try
      {
        Response localResponse = RealCall.this.getResponseWithInterceptorChain();
        if (RealCall.this.retryAndFollowUpInterceptor.isCanceled())
        {
          i = 1;
          this.responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
        }
        while (true)
        {
          return;
          i = 1;
          this.responseCallback.onResponse(RealCall.this, localResponse);
        }
      }
      catch (IOException localIOException)
      {
        if (i != 0)
          Platform.get().log(4, "Callback failure for " + RealCall.this.toLoggableString(), localIOException);
        while (true)
        {
          return;
          this.responseCallback.onFailure(RealCall.this, localIOException);
        }
      }
      finally
      {
        RealCall.this.client.dispatcher().finished(this);
      }
      throw localObject;
    }

    RealCall get()
    {
      return RealCall.this;
    }

    String host()
    {
      return RealCall.this.originalRequest.url().host();
    }

    Request request()
    {
      return RealCall.this.originalRequest;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.RealCall
 * JD-Core Version:    0.6.0
 */