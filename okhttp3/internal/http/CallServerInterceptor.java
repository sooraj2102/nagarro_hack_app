package okhttp3.internal.http;

import java.io.IOException;
import java.net.ProtocolException;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.StreamAllocation;
import okio.BufferedSink;
import okio.Okio;

public final class CallServerInterceptor
  implements Interceptor
{
  private final boolean forWebSocket;

  public CallServerInterceptor(boolean paramBoolean)
  {
    this.forWebSocket = paramBoolean;
  }

  public Response intercept(Interceptor.Chain paramChain)
    throws IOException
  {
    HttpCodec localHttpCodec = ((RealInterceptorChain)paramChain).httpStream();
    StreamAllocation localStreamAllocation = ((RealInterceptorChain)paramChain).streamAllocation();
    Request localRequest = paramChain.request();
    long l = System.currentTimeMillis();
    localHttpCodec.writeRequestHeaders(localRequest);
    boolean bool1 = HttpMethod.permitsRequestBody(localRequest.method());
    Response.Builder localBuilder = null;
    if (bool1)
    {
      RequestBody localRequestBody = localRequest.body();
      localBuilder = null;
      if (localRequestBody != null)
      {
        boolean bool2 = "100-continue".equalsIgnoreCase(localRequest.header("Expect"));
        localBuilder = null;
        if (bool2)
        {
          localHttpCodec.flushRequest();
          localBuilder = localHttpCodec.readResponseHeaders(true);
        }
        if (localBuilder == null)
        {
          BufferedSink localBufferedSink = Okio.buffer(localHttpCodec.createRequestBody(localRequest, localRequest.body().contentLength()));
          localRequest.body().writeTo(localBufferedSink);
          localBufferedSink.close();
        }
      }
    }
    localHttpCodec.finishRequest();
    if (localBuilder == null)
      localBuilder = localHttpCodec.readResponseHeaders(false);
    Response localResponse1 = localBuilder.request(localRequest).handshake(localStreamAllocation.connection().handshake()).sentRequestAtMillis(l).receivedResponseAtMillis(System.currentTimeMillis()).build();
    int i = localResponse1.code();
    if ((this.forWebSocket) && (i == 101));
    for (Response localResponse2 = localResponse1.newBuilder().body(Util.EMPTY_RESPONSE).build(); ; localResponse2 = localResponse1.newBuilder().body(localHttpCodec.openResponseBody(localResponse1)).build())
    {
      if (("close".equalsIgnoreCase(localResponse2.request().header("Connection"))) || ("close".equalsIgnoreCase(localResponse2.header("Connection"))))
        localStreamAllocation.noNewStreams();
      if (((i != 204) && (i != 205)) || (localResponse2.body().contentLength() <= 0L))
        break;
      throw new ProtocolException("HTTP " + i + " had non-zero Content-Length: " + localResponse2.body().contentLength());
    }
    return localResponse2;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.http.CallServerInterceptor
 * JD-Core Version:    0.6.0
 */