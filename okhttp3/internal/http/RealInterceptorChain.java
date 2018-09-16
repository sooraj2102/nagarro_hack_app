package okhttp3.internal.http;

import java.io.IOException;
import java.util.List;
import okhttp3.Address;
import okhttp3.Connection;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.connection.StreamAllocation;

public final class RealInterceptorChain
  implements Interceptor.Chain
{
  private int calls;
  private final Connection connection;
  private final HttpCodec httpCodec;
  private final int index;
  private final List<Interceptor> interceptors;
  private final Request request;
  private final StreamAllocation streamAllocation;

  public RealInterceptorChain(List<Interceptor> paramList, StreamAllocation paramStreamAllocation, HttpCodec paramHttpCodec, Connection paramConnection, int paramInt, Request paramRequest)
  {
    this.interceptors = paramList;
    this.connection = paramConnection;
    this.streamAllocation = paramStreamAllocation;
    this.httpCodec = paramHttpCodec;
    this.index = paramInt;
    this.request = paramRequest;
  }

  private boolean sameConnection(HttpUrl paramHttpUrl)
  {
    return (paramHttpUrl.host().equals(this.connection.route().address().url().host())) && (paramHttpUrl.port() == this.connection.route().address().url().port());
  }

  public Connection connection()
  {
    return this.connection;
  }

  public HttpCodec httpStream()
  {
    return this.httpCodec;
  }

  public Response proceed(Request paramRequest)
    throws IOException
  {
    return proceed(paramRequest, this.streamAllocation, this.httpCodec, this.connection);
  }

  public Response proceed(Request paramRequest, StreamAllocation paramStreamAllocation, HttpCodec paramHttpCodec, Connection paramConnection)
    throws IOException
  {
    if (this.index >= this.interceptors.size())
      throw new AssertionError();
    this.calls = (1 + this.calls);
    if ((this.httpCodec != null) && (!sameConnection(paramRequest.url())))
      throw new IllegalStateException("network interceptor " + this.interceptors.get(-1 + this.index) + " must retain the same host and port");
    if ((this.httpCodec != null) && (this.calls > 1))
      throw new IllegalStateException("network interceptor " + this.interceptors.get(-1 + this.index) + " must call proceed() exactly once");
    RealInterceptorChain localRealInterceptorChain = new RealInterceptorChain(this.interceptors, paramStreamAllocation, paramHttpCodec, paramConnection, 1 + this.index, paramRequest);
    Interceptor localInterceptor = (Interceptor)this.interceptors.get(this.index);
    Response localResponse = localInterceptor.intercept(localRealInterceptorChain);
    if ((paramHttpCodec != null) && (1 + this.index < this.interceptors.size()) && (localRealInterceptorChain.calls != 1))
      throw new IllegalStateException("network interceptor " + localInterceptor + " must call proceed() exactly once");
    if (localResponse == null)
      throw new NullPointerException("interceptor " + localInterceptor + " returned null");
    return localResponse;
  }

  public Request request()
  {
    return this.request;
  }

  public StreamAllocation streamAllocation()
  {
    return this.streamAllocation;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.http.RealInterceptorChain
 * JD-Core Version:    0.6.0
 */