package okhttp3.internal.http2;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.internal.http.RequestLine;
import okhttp3.internal.http.StatusLine;
import okio.ByteString;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class Http2Codec
  implements HttpCodec
{
  private static final ByteString CONNECTION = ByteString.encodeUtf8("connection");
  private static final ByteString ENCODING;
  private static final ByteString HOST = ByteString.encodeUtf8("host");
  private static final List<ByteString> HTTP_2_SKIPPED_REQUEST_HEADERS;
  private static final List<ByteString> HTTP_2_SKIPPED_RESPONSE_HEADERS;
  private static final ByteString KEEP_ALIVE = ByteString.encodeUtf8("keep-alive");
  private static final ByteString PROXY_CONNECTION = ByteString.encodeUtf8("proxy-connection");
  private static final ByteString TE;
  private static final ByteString TRANSFER_ENCODING = ByteString.encodeUtf8("transfer-encoding");
  private static final ByteString UPGRADE;
  private final OkHttpClient client;
  private final Http2Connection connection;
  private Http2Stream stream;
  final StreamAllocation streamAllocation;

  static
  {
    TE = ByteString.encodeUtf8("te");
    ENCODING = ByteString.encodeUtf8("encoding");
    UPGRADE = ByteString.encodeUtf8("upgrade");
    ByteString[] arrayOfByteString1 = new ByteString[12];
    arrayOfByteString1[0] = CONNECTION;
    arrayOfByteString1[1] = HOST;
    arrayOfByteString1[2] = KEEP_ALIVE;
    arrayOfByteString1[3] = PROXY_CONNECTION;
    arrayOfByteString1[4] = TE;
    arrayOfByteString1[5] = TRANSFER_ENCODING;
    arrayOfByteString1[6] = ENCODING;
    arrayOfByteString1[7] = UPGRADE;
    arrayOfByteString1[8] = Header.TARGET_METHOD;
    arrayOfByteString1[9] = Header.TARGET_PATH;
    arrayOfByteString1[10] = Header.TARGET_SCHEME;
    arrayOfByteString1[11] = Header.TARGET_AUTHORITY;
    HTTP_2_SKIPPED_REQUEST_HEADERS = Util.immutableList(arrayOfByteString1);
    ByteString[] arrayOfByteString2 = new ByteString[8];
    arrayOfByteString2[0] = CONNECTION;
    arrayOfByteString2[1] = HOST;
    arrayOfByteString2[2] = KEEP_ALIVE;
    arrayOfByteString2[3] = PROXY_CONNECTION;
    arrayOfByteString2[4] = TE;
    arrayOfByteString2[5] = TRANSFER_ENCODING;
    arrayOfByteString2[6] = ENCODING;
    arrayOfByteString2[7] = UPGRADE;
    HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableList(arrayOfByteString2);
  }

  public Http2Codec(OkHttpClient paramOkHttpClient, StreamAllocation paramStreamAllocation, Http2Connection paramHttp2Connection)
  {
    this.client = paramOkHttpClient;
    this.streamAllocation = paramStreamAllocation;
    this.connection = paramHttp2Connection;
  }

  public static List<Header> http2HeadersList(Request paramRequest)
  {
    Headers localHeaders = paramRequest.headers();
    ArrayList localArrayList = new ArrayList(4 + localHeaders.size());
    localArrayList.add(new Header(Header.TARGET_METHOD, paramRequest.method()));
    localArrayList.add(new Header(Header.TARGET_PATH, RequestLine.requestPath(paramRequest.url())));
    String str = paramRequest.header("Host");
    if (str != null)
      localArrayList.add(new Header(Header.TARGET_AUTHORITY, str));
    localArrayList.add(new Header(Header.TARGET_SCHEME, paramRequest.url().scheme()));
    int i = 0;
    int j = localHeaders.size();
    while (i < j)
    {
      ByteString localByteString = ByteString.encodeUtf8(localHeaders.name(i).toLowerCase(Locale.US));
      if (!HTTP_2_SKIPPED_REQUEST_HEADERS.contains(localByteString))
        localArrayList.add(new Header(localByteString, localHeaders.value(i)));
      i++;
    }
    return localArrayList;
  }

  public static Response.Builder readHttp2HeadersList(List<Header> paramList)
    throws IOException
  {
    StatusLine localStatusLine = null;
    Headers.Builder localBuilder = new Headers.Builder();
    int i = 0;
    int j = paramList.size();
    if (i < j)
    {
      Header localHeader = (Header)paramList.get(i);
      if (localHeader == null)
        if ((localStatusLine != null) && (localStatusLine.code == 100))
        {
          localStatusLine = null;
          localBuilder = new Headers.Builder();
        }
      while (true)
      {
        i++;
        break;
        ByteString localByteString = localHeader.name;
        String str = localHeader.value.utf8();
        if (localByteString.equals(Header.RESPONSE_STATUS))
        {
          localStatusLine = StatusLine.parse("HTTP/1.1 " + str);
          continue;
        }
        if (HTTP_2_SKIPPED_RESPONSE_HEADERS.contains(localByteString))
          continue;
        Internal.instance.addLenient(localBuilder, localByteString.utf8(), str);
      }
    }
    if (localStatusLine == null)
      throw new ProtocolException("Expected ':status' header not present");
    return new Response.Builder().protocol(Protocol.HTTP_2).code(localStatusLine.code).message(localStatusLine.message).headers(localBuilder.build());
  }

  public void cancel()
  {
    if (this.stream != null)
      this.stream.closeLater(ErrorCode.CANCEL);
  }

  public Sink createRequestBody(Request paramRequest, long paramLong)
  {
    return this.stream.getSink();
  }

  public void finishRequest()
    throws IOException
  {
    this.stream.getSink().close();
  }

  public void flushRequest()
    throws IOException
  {
    this.connection.flush();
  }

  public ResponseBody openResponseBody(Response paramResponse)
    throws IOException
  {
    StreamFinishingSource localStreamFinishingSource = new StreamFinishingSource(this.stream.getSource());
    return new RealResponseBody(paramResponse.headers(), Okio.buffer(localStreamFinishingSource));
  }

  public Response.Builder readResponseHeaders(boolean paramBoolean)
    throws IOException
  {
    Response.Builder localBuilder = readHttp2HeadersList(this.stream.takeResponseHeaders());
    if ((paramBoolean) && (Internal.instance.code(localBuilder) == 100))
      localBuilder = null;
    return localBuilder;
  }

  public void writeRequestHeaders(Request paramRequest)
    throws IOException
  {
    if (this.stream != null)
      return;
    if (paramRequest.body() != null);
    for (boolean bool = true; ; bool = false)
    {
      List localList = http2HeadersList(paramRequest);
      this.stream = this.connection.newStream(localList, bool);
      this.stream.readTimeout().timeout(this.client.readTimeoutMillis(), TimeUnit.MILLISECONDS);
      this.stream.writeTimeout().timeout(this.client.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
      return;
    }
  }

  class StreamFinishingSource extends ForwardingSource
  {
    public StreamFinishingSource(Source arg2)
    {
      super();
    }

    public void close()
      throws IOException
    {
      Http2Codec.this.streamAllocation.streamFinished(false, Http2Codec.this);
      super.close();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.http2.Http2Codec
 * JD-Core Version:    0.6.0
 */