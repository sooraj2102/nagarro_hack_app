package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownServiceException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.Authenticator;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2Connection.Builder;
import okhttp3.internal.http2.Http2Connection.Listener;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket.Streams;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;

public final class RealConnection extends Http2Connection.Listener
  implements Connection
{
  public int allocationLimit = 1;
  public final List<Reference<StreamAllocation>> allocations = new ArrayList();
  private final ConnectionPool connectionPool;
  private Handshake handshake;
  private Http2Connection http2Connection;
  public long idleAtNanos = 9223372036854775807L;
  public boolean noNewStreams;
  private Protocol protocol;
  private Socket rawSocket;
  private final Route route;
  private BufferedSink sink;
  private Socket socket;
  private BufferedSource source;
  public int successCount;

  public RealConnection(ConnectionPool paramConnectionPool, Route paramRoute)
  {
    this.connectionPool = paramConnectionPool;
    this.route = paramRoute;
  }

  private void connectSocket(int paramInt1, int paramInt2)
    throws IOException
  {
    Proxy localProxy = this.route.proxy();
    Address localAddress = this.route.address();
    Socket localSocket;
    if ((localProxy.type() == Proxy.Type.DIRECT) || (localProxy.type() == Proxy.Type.HTTP))
      localSocket = localAddress.socketFactory().createSocket();
    ConnectException localConnectException2;
    while (true)
    {
      this.rawSocket = localSocket;
      this.rawSocket.setSoTimeout(paramInt2);
      try
      {
        Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), paramInt1);
        this.source = Okio.buffer(Okio.source(this.rawSocket));
        this.sink = Okio.buffer(Okio.sink(this.rawSocket));
        return;
        localSocket = new Socket(localProxy);
      }
      catch (ConnectException localConnectException1)
      {
        localConnectException2 = new ConnectException("Failed to connect to " + this.route.socketAddress());
        localConnectException2.initCause(localConnectException1);
      }
    }
    throw localConnectException2;
  }

  private void connectTls(ConnectionSpecSelector paramConnectionSpecSelector)
    throws IOException
  {
    Address localAddress = this.route.address();
    SSLSocketFactory localSSLSocketFactory = localAddress.sslSocketFactory();
    SSLSocket localSSLSocket = null;
    ConnectionSpec localConnectionSpec;
    Handshake localHandshake;
    try
    {
      localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(this.rawSocket, localAddress.url().host(), localAddress.url().port(), true);
      localConnectionSpec = paramConnectionSpecSelector.configureSecureSocket(localSSLSocket);
      if (localConnectionSpec.supportsTlsExtensions())
        Platform.get().configureTlsExtensions(localSSLSocket, localAddress.url().host(), localAddress.protocols());
      localSSLSocket.startHandshake();
      localHandshake = Handshake.get(localSSLSocket.getSession());
      if (!localAddress.hostnameVerifier().verify(localAddress.url().host(), localSSLSocket.getSession()))
      {
        X509Certificate localX509Certificate = (X509Certificate)localHandshake.peerCertificates().get(0);
        throw new SSLPeerUnverifiedException("Hostname " + localAddress.url().host() + " not verified:\n    certificate: " + CertificatePinner.pin(localX509Certificate) + "\n    DN: " + localX509Certificate.getSubjectDN().getName() + "\n    subjectAltNames: " + OkHostnameVerifier.allSubjectAltNames(localX509Certificate));
      }
    }
    catch (AssertionError localAssertionError)
    {
      if (!Util.isAndroidGetsocknameError(localAssertionError))
        break label392;
      throw new IOException(localAssertionError);
    }
    finally
    {
      if (localSSLSocket != null)
        Platform.get().afterHandshake(localSSLSocket);
      if (0 == 0)
        Util.closeQuietly(localSSLSocket);
    }
    localAddress.certificatePinner().check(localAddress.url().host(), localHandshake.peerCertificates());
    String str;
    if (localConnectionSpec.supportsTlsExtensions())
    {
      str = Platform.get().getSelectedProtocol(localSSLSocket);
      this.socket = localSSLSocket;
      this.source = Okio.buffer(Okio.source(this.socket));
      this.sink = Okio.buffer(Okio.sink(this.socket));
      this.handshake = localHandshake;
      if (str == null)
        break label384;
    }
    label384: for (Protocol localProtocol = Protocol.get(str); ; localProtocol = Protocol.HTTP_1_1)
    {
      this.protocol = localProtocol;
      if (localSSLSocket != null)
        Platform.get().afterHandshake(localSSLSocket);
      if (1 == 0)
        Util.closeQuietly(localSSLSocket);
      return;
      str = null;
      break;
    }
    label392: throw localAssertionError;
  }

  private void connectTunnel(int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    Request localRequest = createTunnelRequest();
    HttpUrl localHttpUrl = localRequest.url();
    int i = 0;
    while (true)
    {
      i++;
      if (i > 21)
        throw new ProtocolException("Too many tunnel connections attempted: " + 21);
      connectSocket(paramInt1, paramInt2);
      localRequest = createTunnel(paramInt2, paramInt3, localRequest, localHttpUrl);
      if (localRequest == null)
        return;
      Util.closeQuietly(this.rawSocket);
      this.rawSocket = null;
      this.sink = null;
      this.source = null;
    }
  }

  private Request createTunnel(int paramInt1, int paramInt2, Request paramRequest, HttpUrl paramHttpUrl)
    throws IOException
  {
    String str = "CONNECT " + Util.hostHeader(paramHttpUrl, true) + " HTTP/1.1";
    Request localRequest;
    while (true)
    {
      Http1Codec localHttp1Codec = new Http1Codec(null, null, this.source, this.sink);
      this.source.timeout().timeout(paramInt1, TimeUnit.MILLISECONDS);
      this.sink.timeout().timeout(paramInt2, TimeUnit.MILLISECONDS);
      localHttp1Codec.writeRequest(paramRequest.headers(), str);
      localHttp1Codec.finishRequest();
      Response localResponse = localHttp1Codec.readResponseHeaders(false).request(paramRequest).build();
      long l = HttpHeaders.contentLength(localResponse);
      if (l == -1L)
        l = 0L;
      Source localSource = localHttp1Codec.newFixedLengthSource(l);
      Util.skipAll(localSource, 2147483647, TimeUnit.MILLISECONDS);
      localSource.close();
      switch (localResponse.code())
      {
      default:
        throw new IOException("Unexpected response code for CONNECT: " + localResponse.code());
      case 200:
        if (this.source.buffer().exhausted())
        {
          boolean bool = this.sink.buffer().exhausted();
          localRequest = null;
          if (bool)
            break;
        }
        else
        {
          throw new IOException("TLS tunnel buffered too many bytes!");
        }
      case 407:
        paramRequest = this.route.address().proxyAuthenticator().authenticate(this.route, localResponse);
        if (paramRequest == null)
          throw new IOException("Failed to authenticate with proxy");
        if (!"close".equalsIgnoreCase(localResponse.header("Connection")))
          continue;
        localRequest = paramRequest;
      }
    }
    return localRequest;
  }

  private Request createTunnelRequest()
  {
    return new Request.Builder().url(this.route.address().url()).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
  }

  private void establishProtocol(ConnectionSpecSelector paramConnectionSpecSelector)
    throws IOException
  {
    if (this.route.address().sslSocketFactory() == null)
    {
      this.protocol = Protocol.HTTP_1_1;
      this.socket = this.rawSocket;
    }
    do
    {
      return;
      connectTls(paramConnectionSpecSelector);
    }
    while (this.protocol != Protocol.HTTP_2);
    this.socket.setSoTimeout(0);
    this.http2Connection = new Http2Connection.Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).build();
    this.http2Connection.start();
  }

  public static RealConnection testConnection(ConnectionPool paramConnectionPool, Route paramRoute, Socket paramSocket, long paramLong)
  {
    RealConnection localRealConnection = new RealConnection(paramConnectionPool, paramRoute);
    localRealConnection.socket = paramSocket;
    localRealConnection.idleAtNanos = paramLong;
    return localRealConnection;
  }

  public void cancel()
  {
    Util.closeQuietly(this.rawSocket);
  }

  public void connect(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (this.protocol != null)
      throw new IllegalStateException("already connected");
    List localList = this.route.address().connectionSpecs();
    ConnectionSpecSelector localConnectionSpecSelector = new ConnectionSpecSelector(localList);
    SSLSocketFactory localSSLSocketFactory = this.route.address().sslSocketFactory();
    RouteException localRouteException = null;
    if (localSSLSocketFactory == null)
    {
      if (!localList.contains(ConnectionSpec.CLEARTEXT))
        throw new RouteException(new UnknownServiceException("CLEARTEXT communication not enabled for client"));
      String str = this.route.address().url().host();
      boolean bool = Platform.get().isCleartextTrafficPermitted(str);
      localRouteException = null;
      if (!bool)
        throw new RouteException(new UnknownServiceException("CLEARTEXT communication to " + str + " not permitted by network security policy"));
    }
    try
    {
      if (this.route.requiresTunnel())
      {
        connectTunnel(paramInt1, paramInt2, paramInt3);
        establishProtocol(localConnectionSpecSelector);
        if (this.http2Connection == null);
      }
    }
    catch (IOException localIOException)
    {
      synchronized (this.connectionPool)
      {
        while (true)
        {
          this.allocationLimit = this.http2Connection.maxConcurrentStreams();
          return;
          connectSocket(paramInt1, paramInt2);
          continue;
          localIOException = localIOException;
          Util.closeQuietly(this.socket);
          Util.closeQuietly(this.rawSocket);
          this.socket = null;
          this.rawSocket = null;
          this.source = null;
          this.sink = null;
          this.handshake = null;
          this.protocol = null;
          this.http2Connection = null;
          if (localRouteException != null)
            break;
          localRouteException = new RouteException(localIOException);
          if ((paramBoolean) && (localConnectionSpecSelector.connectionFailed(localIOException)))
            continue;
          throw localRouteException;
        }
        localRouteException.addConnectException(localIOException);
      }
    }
  }

  public Handshake handshake()
  {
    return this.handshake;
  }

  public boolean isEligible(Address paramAddress)
  {
    return (this.allocations.size() < this.allocationLimit) && (paramAddress.equals(route().address())) && (!this.noNewStreams);
  }

  public boolean isHealthy(boolean paramBoolean)
  {
    int i = 1;
    if ((this.socket.isClosed()) || (this.socket.isInputShutdown()) || (this.socket.isOutputShutdown()))
      i = 0;
    do
      while (true)
      {
        return i;
        if (this.http2Connection == null)
          break;
        if (this.http2Connection.isShutdown())
          return false;
      }
    while (!paramBoolean);
    try
    {
      int j = this.socket.getSoTimeout();
      try
      {
        this.socket.setSoTimeout(1);
        boolean bool = this.source.exhausted();
        if (bool)
          return false;
        return i;
      }
      finally
      {
        this.socket.setSoTimeout(j);
      }
    }
    catch (IOException localIOException)
    {
      return false;
    }
    catch (SocketTimeoutException localSocketTimeoutException)
    {
    }
    return i;
  }

  public boolean isMultiplexed()
  {
    return this.http2Connection != null;
  }

  public HttpCodec newCodec(OkHttpClient paramOkHttpClient, StreamAllocation paramStreamAllocation)
    throws SocketException
  {
    if (this.http2Connection != null)
      return new Http2Codec(paramOkHttpClient, paramStreamAllocation, this.http2Connection);
    this.socket.setSoTimeout(paramOkHttpClient.readTimeoutMillis());
    this.source.timeout().timeout(paramOkHttpClient.readTimeoutMillis(), TimeUnit.MILLISECONDS);
    this.sink.timeout().timeout(paramOkHttpClient.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
    return new Http1Codec(paramOkHttpClient, paramStreamAllocation, this.source, this.sink);
  }

  public RealWebSocket.Streams newWebSocketStreams(StreamAllocation paramStreamAllocation)
  {
    return new RealWebSocket.Streams(true, this.source, this.sink, paramStreamAllocation)
    {
      public void close()
        throws IOException
      {
        this.val$streamAllocation.streamFinished(true, this.val$streamAllocation.codec());
      }
    };
  }

  public void onSettings(Http2Connection paramHttp2Connection)
  {
    synchronized (this.connectionPool)
    {
      this.allocationLimit = paramHttp2Connection.maxConcurrentStreams();
      return;
    }
  }

  public void onStream(Http2Stream paramHttp2Stream)
    throws IOException
  {
    paramHttp2Stream.close(ErrorCode.REFUSED_STREAM);
  }

  public Protocol protocol()
  {
    return this.protocol;
  }

  public Route route()
  {
    return this.route;
  }

  public Socket socket()
  {
    return this.socket;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("Connection{").append(this.route.address().url().host()).append(":").append(this.route.address().url().port()).append(", proxy=").append(this.route.proxy()).append(" hostAddress=").append(this.route.socketAddress()).append(" cipherSuite=");
    if (this.handshake != null);
    for (Object localObject = this.handshake.cipherSuite(); ; localObject = "none")
      return localObject + " protocol=" + this.protocol + '}';
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.connection.RealConnection
 * JD-Core Version:    0.6.0
 */