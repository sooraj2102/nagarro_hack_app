package okhttp3.internal.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.Authenticator;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.StreamAllocation;

public final class RetryAndFollowUpInterceptor
  implements Interceptor
{
  private static final int MAX_FOLLOW_UPS = 20;
  private Object callStackTrace;
  private volatile boolean canceled;
  private final OkHttpClient client;
  private final boolean forWebSocket;
  private StreamAllocation streamAllocation;

  public RetryAndFollowUpInterceptor(OkHttpClient paramOkHttpClient, boolean paramBoolean)
  {
    this.client = paramOkHttpClient;
    this.forWebSocket = paramBoolean;
  }

  private Address createAddress(HttpUrl paramHttpUrl)
  {
    boolean bool = paramHttpUrl.isHttps();
    SSLSocketFactory localSSLSocketFactory = null;
    HostnameVerifier localHostnameVerifier = null;
    CertificatePinner localCertificatePinner = null;
    if (bool)
    {
      localSSLSocketFactory = this.client.sslSocketFactory();
      localHostnameVerifier = this.client.hostnameVerifier();
      localCertificatePinner = this.client.certificatePinner();
    }
    return new Address(paramHttpUrl.host(), paramHttpUrl.port(), this.client.dns(), this.client.socketFactory(), localSSLSocketFactory, localHostnameVerifier, localCertificatePinner, this.client.proxyAuthenticator(), this.client.proxy(), this.client.protocols(), this.client.connectionSpecs(), this.client.proxySelector());
  }

  private Request followUpRequest(Response paramResponse)
    throws IOException
  {
    if (paramResponse == null)
      throw new IllegalStateException();
    RealConnection localRealConnection = this.streamAllocation.connection();
    Route localRoute;
    String str1;
    if (localRealConnection != null)
    {
      localRoute = localRealConnection.route();
      int i = paramResponse.code();
      str1 = paramResponse.request().method();
      switch (i)
      {
      default:
      case 407:
      case 401:
      case 307:
      case 308:
      case 300:
      case 301:
      case 302:
      case 303:
      case 408:
      }
    }
    label404: 
    do
    {
      HttpUrl localHttpUrl;
      do
      {
        String str2;
        do
        {
          do
          {
            return null;
            localRoute = null;
            break;
            if (localRoute != null);
            for (Proxy localProxy = localRoute.proxy(); localProxy.type() != Proxy.Type.HTTP; localProxy = this.client.proxy())
              throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
            return this.client.proxyAuthenticator().authenticate(localRoute, paramResponse);
            return this.client.authenticator().authenticate(localRoute, paramResponse);
          }
          while (((!str1.equals("GET")) && (!str1.equals("HEAD"))) || (!this.client.followRedirects()));
          str2 = paramResponse.header("Location");
        }
        while (str2 == null);
        localHttpUrl = paramResponse.request().url().resolve(str2);
      }
      while ((localHttpUrl == null) || ((!localHttpUrl.scheme().equals(paramResponse.request().url().scheme())) && (!this.client.followSslRedirects())));
      Request.Builder localBuilder = paramResponse.request().newBuilder();
      boolean bool;
      if (HttpMethod.permitsRequestBody(str1))
      {
        bool = HttpMethod.redirectsWithBody(str1);
        if (!HttpMethod.redirectsToGet(str1))
          break label404;
        localBuilder.method("GET", null);
        if (!bool)
        {
          localBuilder.removeHeader("Transfer-Encoding");
          localBuilder.removeHeader("Content-Length");
          localBuilder.removeHeader("Content-Type");
        }
      }
      if (!sameConnection(paramResponse, localHttpUrl))
        localBuilder.removeHeader("Authorization");
      return localBuilder.url(localHttpUrl).build();
      if (bool);
      for (RequestBody localRequestBody = paramResponse.request().body(); ; localRequestBody = null)
      {
        localBuilder.method(str1, localRequestBody);
        break;
      }
    }
    while ((paramResponse.request().body() instanceof UnrepeatableRequestBody));
    return paramResponse.request();
  }

  private boolean isRecoverable(IOException paramIOException, boolean paramBoolean)
  {
    int i = 1;
    if ((paramIOException instanceof ProtocolException));
    do
    {
      return false;
      if (!(paramIOException instanceof InterruptedIOException))
        continue;
      if (((paramIOException instanceof SocketTimeoutException)) && (!paramBoolean));
      while (true)
      {
        return i;
        i = 0;
      }
    }
    while ((((paramIOException instanceof SSLHandshakeException)) && ((paramIOException.getCause() instanceof CertificateException))) || ((paramIOException instanceof SSLPeerUnverifiedException)));
    return i;
  }

  private boolean recover(IOException paramIOException, boolean paramBoolean, Request paramRequest)
  {
    this.streamAllocation.streamFailed(paramIOException);
    if (!this.client.retryOnConnectionFailure());
    do
      return false;
    while (((paramBoolean) && ((paramRequest.body() instanceof UnrepeatableRequestBody))) || (!isRecoverable(paramIOException, paramBoolean)) || (!this.streamAllocation.hasMoreRoutes()));
    return true;
  }

  private boolean sameConnection(Response paramResponse, HttpUrl paramHttpUrl)
  {
    HttpUrl localHttpUrl = paramResponse.request().url();
    return (localHttpUrl.host().equals(paramHttpUrl.host())) && (localHttpUrl.port() == paramHttpUrl.port()) && (localHttpUrl.scheme().equals(paramHttpUrl.scheme()));
  }

  public void cancel()
  {
    this.canceled = true;
    StreamAllocation localStreamAllocation = this.streamAllocation;
    if (localStreamAllocation != null)
      localStreamAllocation.cancel();
  }

  // ERROR //
  public Response intercept(okhttp3.Interceptor.Chain paramChain)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 279 1 0
    //   6: astore_2
    //   7: aload_0
    //   8: new 100	okhttp3/internal/connection/StreamAllocation
    //   11: dup
    //   12: aload_0
    //   13: getfield 25	okhttp3/internal/http/RetryAndFollowUpInterceptor:client	Lokhttp3/OkHttpClient;
    //   16: invokevirtual 283	okhttp3/OkHttpClient:connectionPool	()Lokhttp3/ConnectionPool;
    //   19: aload_0
    //   20: aload_2
    //   21: invokevirtual 178	okhttp3/Request:url	()Lokhttp3/HttpUrl;
    //   24: invokespecial 285	okhttp3/internal/http/RetryAndFollowUpInterceptor:createAddress	(Lokhttp3/HttpUrl;)Lokhttp3/Address;
    //   27: aload_0
    //   28: getfield 287	okhttp3/internal/http/RetryAndFollowUpInterceptor:callStackTrace	Ljava/lang/Object;
    //   31: invokespecial 290	okhttp3/internal/connection/StreamAllocation:<init>	(Lokhttp3/ConnectionPool;Lokhttp3/Address;Ljava/lang/Object;)V
    //   34: putfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   37: iconst_0
    //   38: istore_3
    //   39: aconst_null
    //   40: astore 4
    //   42: aload_0
    //   43: getfield 270	okhttp3/internal/http/RetryAndFollowUpInterceptor:canceled	Z
    //   46: ifeq +21 -> 67
    //   49: aload_0
    //   50: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   53: invokevirtual 293	okhttp3/internal/connection/StreamAllocation:release	()V
    //   56: new 93	java/io/IOException
    //   59: dup
    //   60: ldc_w 295
    //   63: invokespecial 296	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   66: athrow
    //   67: aload_1
    //   68: checkcast 298	okhttp3/internal/http/RealInterceptorChain
    //   71: aload_2
    //   72: aload_0
    //   73: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   76: aconst_null
    //   77: aconst_null
    //   78: invokevirtual 302	okhttp3/internal/http/RealInterceptorChain:proceed	(Lokhttp3/Request;Lokhttp3/internal/connection/StreamAllocation;Lokhttp3/internal/http/HttpCodec;Lokhttp3/Connection;)Lokhttp3/Response;
    //   81: astore 9
    //   83: aload 9
    //   85: astore 10
    //   87: iconst_0
    //   88: ifeq +18 -> 106
    //   91: aload_0
    //   92: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   95: aconst_null
    //   96: invokevirtual 259	okhttp3/internal/connection/StreamAllocation:streamFailed	(Ljava/io/IOException;)V
    //   99: aload_0
    //   100: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   103: invokevirtual 293	okhttp3/internal/connection/StreamAllocation:release	()V
    //   106: aload 4
    //   108: ifnull +28 -> 136
    //   111: aload 10
    //   113: invokevirtual 305	okhttp3/Response:newBuilder	()Lokhttp3/Response$Builder;
    //   116: aload 4
    //   118: invokevirtual 305	okhttp3/Response:newBuilder	()Lokhttp3/Response$Builder;
    //   121: aconst_null
    //   122: invokevirtual 310	okhttp3/Response$Builder:body	(Lokhttp3/ResponseBody;)Lokhttp3/Response$Builder;
    //   125: invokevirtual 313	okhttp3/Response$Builder:build	()Lokhttp3/Response;
    //   128: invokevirtual 317	okhttp3/Response$Builder:priorResponse	(Lokhttp3/Response;)Lokhttp3/Response$Builder;
    //   131: invokevirtual 313	okhttp3/Response$Builder:build	()Lokhttp3/Response;
    //   134: astore 10
    //   136: aload_0
    //   137: aload 10
    //   139: invokespecial 319	okhttp3/internal/http/RetryAndFollowUpInterceptor:followUpRequest	(Lokhttp3/Response;)Lokhttp3/Request;
    //   142: astore 11
    //   144: aload 11
    //   146: ifnonnull +144 -> 290
    //   149: aload_0
    //   150: getfield 27	okhttp3/internal/http/RetryAndFollowUpInterceptor:forWebSocket	Z
    //   153: ifne +10 -> 163
    //   156: aload_0
    //   157: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   160: invokevirtual 293	okhttp3/internal/connection/StreamAllocation:release	()V
    //   163: aload 10
    //   165: areturn
    //   166: astore 8
    //   168: aload_0
    //   169: aload 8
    //   171: invokevirtual 323	okhttp3/internal/connection/RouteException:getLastConnectException	()Ljava/io/IOException;
    //   174: iconst_0
    //   175: aload_2
    //   176: invokespecial 325	okhttp3/internal/http/RetryAndFollowUpInterceptor:recover	(Ljava/io/IOException;ZLokhttp3/Request;)Z
    //   179: ifne +33 -> 212
    //   182: aload 8
    //   184: invokevirtual 323	okhttp3/internal/connection/RouteException:getLastConnectException	()Ljava/io/IOException;
    //   187: athrow
    //   188: astore 7
    //   190: iconst_1
    //   191: ifeq +18 -> 209
    //   194: aload_0
    //   195: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   198: aconst_null
    //   199: invokevirtual 259	okhttp3/internal/connection/StreamAllocation:streamFailed	(Ljava/io/IOException;)V
    //   202: aload_0
    //   203: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   206: invokevirtual 293	okhttp3/internal/connection/StreamAllocation:release	()V
    //   209: aload 7
    //   211: athrow
    //   212: iconst_0
    //   213: ifeq -171 -> 42
    //   216: aload_0
    //   217: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   220: aconst_null
    //   221: invokevirtual 259	okhttp3/internal/connection/StreamAllocation:streamFailed	(Ljava/io/IOException;)V
    //   224: aload_0
    //   225: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   228: invokevirtual 293	okhttp3/internal/connection/StreamAllocation:release	()V
    //   231: goto -189 -> 42
    //   234: astore 5
    //   236: aload 5
    //   238: instanceof 327
    //   241: ifne +21 -> 262
    //   244: iconst_1
    //   245: istore 6
    //   247: aload_0
    //   248: aload 5
    //   250: iload 6
    //   252: aload_2
    //   253: invokespecial 325	okhttp3/internal/http/RetryAndFollowUpInterceptor:recover	(Ljava/io/IOException;ZLokhttp3/Request;)Z
    //   256: ifne +12 -> 268
    //   259: aload 5
    //   261: athrow
    //   262: iconst_0
    //   263: istore 6
    //   265: goto -18 -> 247
    //   268: iconst_0
    //   269: ifeq -227 -> 42
    //   272: aload_0
    //   273: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   276: aconst_null
    //   277: invokevirtual 259	okhttp3/internal/connection/StreamAllocation:streamFailed	(Ljava/io/IOException;)V
    //   280: aload_0
    //   281: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   284: invokevirtual 293	okhttp3/internal/connection/StreamAllocation:release	()V
    //   287: goto -245 -> 42
    //   290: aload 10
    //   292: invokevirtual 330	okhttp3/Response:body	()Lokhttp3/ResponseBody;
    //   295: invokestatic 336	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   298: iinc 3 1
    //   301: iload_3
    //   302: bipush 20
    //   304: if_icmple +38 -> 342
    //   307: aload_0
    //   308: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   311: invokevirtual 293	okhttp3/internal/connection/StreamAllocation:release	()V
    //   314: new 141	java/net/ProtocolException
    //   317: dup
    //   318: new 338	java/lang/StringBuilder
    //   321: dup
    //   322: invokespecial 339	java/lang/StringBuilder:<init>	()V
    //   325: ldc_w 341
    //   328: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   331: iload_3
    //   332: invokevirtual 348	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   335: invokevirtual 351	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   338: invokespecial 146	java/net/ProtocolException:<init>	(Ljava/lang/String;)V
    //   341: athrow
    //   342: aload 11
    //   344: invokevirtual 235	okhttp3/Request:body	()Lokhttp3/RequestBody;
    //   347: instanceof 237
    //   350: ifeq +26 -> 376
    //   353: aload_0
    //   354: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   357: invokevirtual 293	okhttp3/internal/connection/StreamAllocation:release	()V
    //   360: new 353	java/net/HttpRetryException
    //   363: dup
    //   364: ldc_w 355
    //   367: aload 10
    //   369: invokevirtual 115	okhttp3/Response:code	()I
    //   372: invokespecial 358	java/net/HttpRetryException:<init>	(Ljava/lang/String;I)V
    //   375: athrow
    //   376: aload_0
    //   377: aload 10
    //   379: aload 11
    //   381: invokevirtual 178	okhttp3/Request:url	()Lokhttp3/HttpUrl;
    //   384: invokespecial 223	okhttp3/internal/http/RetryAndFollowUpInterceptor:sameConnection	(Lokhttp3/Response;Lokhttp3/HttpUrl;)Z
    //   387: ifne +51 -> 438
    //   390: aload_0
    //   391: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   394: invokevirtual 293	okhttp3/internal/connection/StreamAllocation:release	()V
    //   397: aload_0
    //   398: new 100	okhttp3/internal/connection/StreamAllocation
    //   401: dup
    //   402: aload_0
    //   403: getfield 25	okhttp3/internal/http/RetryAndFollowUpInterceptor:client	Lokhttp3/OkHttpClient;
    //   406: invokevirtual 283	okhttp3/OkHttpClient:connectionPool	()Lokhttp3/ConnectionPool;
    //   409: aload_0
    //   410: aload 11
    //   412: invokevirtual 178	okhttp3/Request:url	()Lokhttp3/HttpUrl;
    //   415: invokespecial 285	okhttp3/internal/http/RetryAndFollowUpInterceptor:createAddress	(Lokhttp3/HttpUrl;)Lokhttp3/Address;
    //   418: aload_0
    //   419: getfield 287	okhttp3/internal/http/RetryAndFollowUpInterceptor:callStackTrace	Ljava/lang/Object;
    //   422: invokespecial 290	okhttp3/internal/connection/StreamAllocation:<init>	(Lokhttp3/ConnectionPool;Lokhttp3/Address;Ljava/lang/Object;)V
    //   425: putfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   428: aload 11
    //   430: astore_2
    //   431: aload 10
    //   433: astore 4
    //   435: goto -393 -> 42
    //   438: aload_0
    //   439: getfield 98	okhttp3/internal/http/RetryAndFollowUpInterceptor:streamAllocation	Lokhttp3/internal/connection/StreamAllocation;
    //   442: invokevirtual 362	okhttp3/internal/connection/StreamAllocation:codec	()Lokhttp3/internal/http/HttpCodec;
    //   445: ifnull -17 -> 428
    //   448: new 95	java/lang/IllegalStateException
    //   451: dup
    //   452: new 338	java/lang/StringBuilder
    //   455: dup
    //   456: invokespecial 339	java/lang/StringBuilder:<init>	()V
    //   459: ldc_w 364
    //   462: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   465: aload 10
    //   467: invokevirtual 367	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   470: ldc_w 369
    //   473: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   476: invokevirtual 351	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   479: invokespecial 370	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   482: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   67	83	166	okhttp3/internal/connection/RouteException
    //   67	83	188	finally
    //   168	188	188	finally
    //   236	244	188	finally
    //   247	262	188	finally
    //   67	83	234	java/io/IOException
  }

  public boolean isCanceled()
  {
    return this.canceled;
  }

  public void setCallStackTrace(Object paramObject)
  {
    this.callStackTrace = paramObject;
  }

  public StreamAllocation streamAllocation()
  {
    return this.streamAllocation;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.http.RetryAndFollowUpInterceptor
 * JD-Core Version:    0.6.0
 */