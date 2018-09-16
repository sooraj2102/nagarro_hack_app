package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;

public final class RealWebSocket
  implements WebSocket, WebSocketReader.FrameCallback
{
  private static final long CANCEL_AFTER_CLOSE_MILLIS = 60000L;
  private static final long MAX_QUEUE_SIZE = 16777216L;
  private static final List<Protocol> ONLY_HTTP1;
  private Call call;
  private ScheduledFuture<?> cancelFuture;
  private boolean enqueuedClose;
  private ScheduledExecutorService executor;
  private boolean failed;
  private final String key;
  final WebSocketListener listener;
  private final ArrayDeque<Object> messageAndCloseQueue = new ArrayDeque();
  private final Request originalRequest;
  int pingCount;
  int pongCount;
  private final ArrayDeque<ByteString> pongQueue = new ArrayDeque();
  private long queueSize;
  private final Random random;
  private WebSocketReader reader;
  private int receivedCloseCode = -1;
  private String receivedCloseReason;
  private Streams streams;
  private WebSocketWriter writer;
  private final Runnable writerRunnable;

  static
  {
    if (!RealWebSocket.class.desiredAssertionStatus());
    for (boolean bool = true; ; bool = false)
    {
      $assertionsDisabled = bool;
      ONLY_HTTP1 = Collections.singletonList(Protocol.HTTP_1_1);
      return;
    }
  }

  public RealWebSocket(Request paramRequest, WebSocketListener paramWebSocketListener, Random paramRandom)
  {
    if (!"GET".equals(paramRequest.method()))
      throw new IllegalArgumentException("Request must be GET: " + paramRequest.method());
    this.originalRequest = paramRequest;
    this.listener = paramWebSocketListener;
    this.random = paramRandom;
    byte[] arrayOfByte = new byte[16];
    paramRandom.nextBytes(arrayOfByte);
    this.key = ByteString.of(arrayOfByte).base64();
    this.writerRunnable = new Runnable()
    {
      public void run()
      {
        try
        {
          boolean bool;
          do
            bool = RealWebSocket.this.writeOneFrame();
          while (bool);
          return;
        }
        catch (IOException localIOException)
        {
          RealWebSocket.this.failWebSocket(localIOException, null);
        }
      }
    };
  }

  private void runWriter()
  {
    assert (Thread.holdsLock(this));
    if (this.executor != null)
      this.executor.execute(this.writerRunnable);
  }

  private boolean send(ByteString paramByteString, int paramInt)
  {
    monitorenter;
    while (true)
    {
      try
      {
        boolean bool1 = this.failed;
        i = 0;
        if (bool1)
          continue;
        boolean bool2 = this.enqueuedClose;
        i = 0;
        if (bool2)
          return i;
        if (this.queueSize + paramByteString.size() > 16777216L)
        {
          close(1001, null);
          i = 0;
          continue;
        }
      }
      finally
      {
        monitorexit;
      }
      this.queueSize += paramByteString.size();
      this.messageAndCloseQueue.add(new Message(paramInt, paramByteString));
      runWriter();
      int i = 1;
    }
  }

  void awaitTermination(int paramInt, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    this.executor.awaitTermination(paramInt, paramTimeUnit);
  }

  public void cancel()
  {
    this.call.cancel();
  }

  void checkResponse(Response paramResponse)
    throws ProtocolException
  {
    if (paramResponse.code() != 101)
      throw new ProtocolException("Expected HTTP 101 response but was '" + paramResponse.code() + " " + paramResponse.message() + "'");
    String str1 = paramResponse.header("Connection");
    if (!"Upgrade".equalsIgnoreCase(str1))
      throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '" + str1 + "'");
    String str2 = paramResponse.header("Upgrade");
    if (!"websocket".equalsIgnoreCase(str2))
      throw new ProtocolException("Expected 'Upgrade' header value 'websocket' but was '" + str2 + "'");
    String str3 = paramResponse.header("Sec-WebSocket-Accept");
    String str4 = ByteString.encodeUtf8(this.key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").sha1().base64();
    if (!str4.equals(str3))
      throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '" + str4 + "' but was '" + str3 + "'");
  }

  public boolean close(int paramInt, String paramString)
  {
    return close(paramInt, paramString, 60000L);
  }

  boolean close(int paramInt, String paramString, long paramLong)
  {
    int i = 1;
    monitorenter;
    ByteString localByteString;
    try
    {
      WebSocketProtocol.validateCloseCode(paramInt);
      localByteString = null;
      if (paramString != null)
      {
        localByteString = ByteString.encodeUtf8(paramString);
        if (localByteString.size() > 123L)
          throw new IllegalArgumentException("reason.size() > 123: " + paramString);
      }
    }
    finally
    {
      monitorexit;
    }
    if (!this.failed)
    {
      boolean bool = this.enqueuedClose;
      if (!bool);
    }
    else
    {
      i = 0;
    }
    while (true)
    {
      monitorexit;
      return i;
      this.enqueuedClose = true;
      this.messageAndCloseQueue.add(new Close(paramInt, localByteString, paramLong));
      runWriter();
    }
  }

  public void connect(OkHttpClient paramOkHttpClient)
  {
    OkHttpClient localOkHttpClient = paramOkHttpClient.newBuilder().protocols(ONLY_HTTP1).build();
    int i = localOkHttpClient.pingIntervalMillis();
    Request localRequest = this.originalRequest.newBuilder().header("Upgrade", "websocket").header("Connection", "Upgrade").header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").build();
    this.call = Internal.instance.newWebSocketCall(localOkHttpClient, localRequest);
    this.call.enqueue(new Callback(localRequest, i)
    {
      public void onFailure(Call paramCall, IOException paramIOException)
      {
        RealWebSocket.this.failWebSocket(paramIOException, null);
      }

      // ERROR //
      public void onResponse(Call paramCall, Response paramResponse)
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 21	okhttp3/internal/ws/RealWebSocket$2:this$0	Lokhttp3/internal/ws/RealWebSocket;
        //   4: aload_2
        //   5: invokevirtual 44	okhttp3/internal/ws/RealWebSocket:checkResponse	(Lokhttp3/Response;)V
        //   8: getstatic 50	okhttp3/internal/Internal:instance	Lokhttp3/internal/Internal;
        //   11: aload_1
        //   12: invokevirtual 54	okhttp3/internal/Internal:streamAllocation	(Lokhttp3/Call;)Lokhttp3/internal/connection/StreamAllocation;
        //   15: astore 4
        //   17: aload 4
        //   19: invokevirtual 59	okhttp3/internal/connection/StreamAllocation:noNewStreams	()V
        //   22: aload 4
        //   24: invokevirtual 63	okhttp3/internal/connection/StreamAllocation:connection	()Lokhttp3/internal/connection/RealConnection;
        //   27: aload 4
        //   29: invokevirtual 69	okhttp3/internal/connection/RealConnection:newWebSocketStreams	(Lokhttp3/internal/connection/StreamAllocation;)Lokhttp3/internal/ws/RealWebSocket$Streams;
        //   32: astore 5
        //   34: aload_0
        //   35: getfield 21	okhttp3/internal/ws/RealWebSocket$2:this$0	Lokhttp3/internal/ws/RealWebSocket;
        //   38: getfield 73	okhttp3/internal/ws/RealWebSocket:listener	Lokhttp3/WebSocketListener;
        //   41: aload_0
        //   42: getfield 21	okhttp3/internal/ws/RealWebSocket$2:this$0	Lokhttp3/internal/ws/RealWebSocket;
        //   45: aload_2
        //   46: invokevirtual 79	okhttp3/WebSocketListener:onOpen	(Lokhttp3/WebSocket;Lokhttp3/Response;)V
        //   49: new 81	java/lang/StringBuilder
        //   52: dup
        //   53: invokespecial 82	java/lang/StringBuilder:<init>	()V
        //   56: ldc 84
        //   58: invokevirtual 88	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   61: aload_0
        //   62: getfield 23	okhttp3/internal/ws/RealWebSocket$2:val$request	Lokhttp3/Request;
        //   65: invokevirtual 94	okhttp3/Request:url	()Lokhttp3/HttpUrl;
        //   68: invokevirtual 100	okhttp3/HttpUrl:redact	()Ljava/lang/String;
        //   71: invokevirtual 88	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   74: invokevirtual 103	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   77: astore 7
        //   79: aload_0
        //   80: getfield 21	okhttp3/internal/ws/RealWebSocket$2:this$0	Lokhttp3/internal/ws/RealWebSocket;
        //   83: aload 7
        //   85: aload_0
        //   86: getfield 25	okhttp3/internal/ws/RealWebSocket$2:val$pingIntervalMillis	I
        //   89: i2l
        //   90: aload 5
        //   92: invokevirtual 107	okhttp3/internal/ws/RealWebSocket:initReaderAndWriter	(Ljava/lang/String;JLokhttp3/internal/ws/RealWebSocket$Streams;)V
        //   95: aload 4
        //   97: invokevirtual 63	okhttp3/internal/connection/StreamAllocation:connection	()Lokhttp3/internal/connection/RealConnection;
        //   100: invokevirtual 111	okhttp3/internal/connection/RealConnection:socket	()Ljava/net/Socket;
        //   103: iconst_0
        //   104: invokevirtual 117	java/net/Socket:setSoTimeout	(I)V
        //   107: aload_0
        //   108: getfield 21	okhttp3/internal/ws/RealWebSocket$2:this$0	Lokhttp3/internal/ws/RealWebSocket;
        //   111: invokevirtual 120	okhttp3/internal/ws/RealWebSocket:loopReader	()V
        //   114: return
        //   115: astore_3
        //   116: aload_0
        //   117: getfield 21	okhttp3/internal/ws/RealWebSocket$2:this$0	Lokhttp3/internal/ws/RealWebSocket;
        //   120: aload_3
        //   121: aload_2
        //   122: invokevirtual 34	okhttp3/internal/ws/RealWebSocket:failWebSocket	(Ljava/lang/Exception;Lokhttp3/Response;)V
        //   125: aload_2
        //   126: invokestatic 126	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
        //   129: return
        //   130: astore 6
        //   132: aload_0
        //   133: getfield 21	okhttp3/internal/ws/RealWebSocket$2:this$0	Lokhttp3/internal/ws/RealWebSocket;
        //   136: aload 6
        //   138: aconst_null
        //   139: invokevirtual 34	okhttp3/internal/ws/RealWebSocket:failWebSocket	(Ljava/lang/Exception;Lokhttp3/Response;)V
        //   142: return
        //
        // Exception table:
        //   from	to	target	type
        //   0	8	115	java/net/ProtocolException
        //   34	114	130	java/lang/Exception
      }
    });
  }

  // ERROR //
  public void failWebSocket(Exception paramException, Response paramResponse)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 176	okhttp3/internal/ws/RealWebSocket:failed	Z
    //   6: ifeq +6 -> 12
    //   9: aload_0
    //   10: monitorexit
    //   11: return
    //   12: aload_0
    //   13: iconst_1
    //   14: putfield 176	okhttp3/internal/ws/RealWebSocket:failed	Z
    //   17: aload_0
    //   18: getfield 347	okhttp3/internal/ws/RealWebSocket:streams	Lokhttp3/internal/ws/RealWebSocket$Streams;
    //   21: astore 4
    //   23: aload_0
    //   24: aconst_null
    //   25: putfield 347	okhttp3/internal/ws/RealWebSocket:streams	Lokhttp3/internal/ws/RealWebSocket$Streams;
    //   28: aload_0
    //   29: getfield 349	okhttp3/internal/ws/RealWebSocket:cancelFuture	Ljava/util/concurrent/ScheduledFuture;
    //   32: ifnull +14 -> 46
    //   35: aload_0
    //   36: getfield 349	okhttp3/internal/ws/RealWebSocket:cancelFuture	Ljava/util/concurrent/ScheduledFuture;
    //   39: iconst_0
    //   40: invokeinterface 354 2 0
    //   45: pop
    //   46: aload_0
    //   47: getfield 166	okhttp3/internal/ws/RealWebSocket:executor	Ljava/util/concurrent/ScheduledExecutorService;
    //   50: ifnull +12 -> 62
    //   53: aload_0
    //   54: getfield 166	okhttp3/internal/ws/RealWebSocket:executor	Ljava/util/concurrent/ScheduledExecutorService;
    //   57: invokeinterface 357 1 0
    //   62: aload_0
    //   63: monitorexit
    //   64: aload_0
    //   65: getfield 129	okhttp3/internal/ws/RealWebSocket:listener	Lokhttp3/WebSocketListener;
    //   68: aload_0
    //   69: aload_1
    //   70: aload_2
    //   71: invokevirtual 363	okhttp3/WebSocketListener:onFailure	(Lokhttp3/WebSocket;Ljava/lang/Throwable;Lokhttp3/Response;)V
    //   74: aload 4
    //   76: invokestatic 369	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   79: return
    //   80: astore_3
    //   81: aload_0
    //   82: monitorexit
    //   83: aload_3
    //   84: athrow
    //   85: astore 5
    //   87: aload 4
    //   89: invokestatic 369	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   92: aload 5
    //   94: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   2	11	80	finally
    //   12	46	80	finally
    //   46	62	80	finally
    //   62	64	80	finally
    //   81	83	80	finally
    //   64	74	85	finally
  }

  public void initReaderAndWriter(String paramString, long paramLong, Streams paramStreams)
    throws IOException
  {
    monitorenter;
    try
    {
      this.streams = paramStreams;
      this.writer = new WebSocketWriter(paramStreams.client, paramStreams.sink, this.random);
      this.executor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(paramString, false));
      if (paramLong != 0L)
        this.executor.scheduleAtFixedRate(new PingRunnable(), paramLong, paramLong, TimeUnit.MILLISECONDS);
      if (!this.messageAndCloseQueue.isEmpty())
        runWriter();
      monitorexit;
      this.reader = new WebSocketReader(paramStreams.client, paramStreams.source, this);
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public void loopReader()
    throws IOException
  {
    while (this.receivedCloseCode == -1)
      this.reader.processNextFrame();
  }

  public void onReadClose(int paramInt, String paramString)
  {
    if (paramInt == -1)
      throw new IllegalArgumentException();
    monitorenter;
    try
    {
      if (this.receivedCloseCode != -1)
        throw new IllegalStateException("already closed");
    }
    finally
    {
      monitorexit;
    }
    this.receivedCloseCode = paramInt;
    this.receivedCloseReason = paramString;
    boolean bool1 = this.enqueuedClose;
    Streams localStreams = null;
    if (bool1)
    {
      boolean bool2 = this.messageAndCloseQueue.isEmpty();
      localStreams = null;
      if (bool2)
      {
        localStreams = this.streams;
        this.streams = null;
        if (this.cancelFuture != null)
          this.cancelFuture.cancel(false);
        this.executor.shutdown();
      }
    }
    monitorexit;
    try
    {
      this.listener.onClosing(this, paramInt, paramString);
      if (localStreams != null)
        this.listener.onClosed(this, paramInt, paramString);
      return;
    }
    finally
    {
      Util.closeQuietly(localStreams);
    }
    throw localObject2;
  }

  public void onReadMessage(String paramString)
    throws IOException
  {
    this.listener.onMessage(this, paramString);
  }

  public void onReadMessage(ByteString paramByteString)
    throws IOException
  {
    this.listener.onMessage(this, paramByteString);
  }

  public void onReadPing(ByteString paramByteString)
  {
    monitorenter;
    try
    {
      if (!this.failed)
      {
        if (!this.enqueuedClose)
          break label33;
        boolean bool = this.messageAndCloseQueue.isEmpty();
        if (!bool)
          break label33;
      }
      while (true)
      {
        return;
        label33: this.pongQueue.add(paramByteString);
        runWriter();
        this.pingCount = (1 + this.pingCount);
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public void onReadPong(ByteString paramByteString)
  {
    monitorenter;
    try
    {
      this.pongCount = (1 + this.pongCount);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  int pingCount()
  {
    monitorenter;
    try
    {
      int i = this.pingCount;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  boolean pong(ByteString paramByteString)
  {
    monitorenter;
    try
    {
      if (!this.failed)
      {
        if (!this.enqueuedClose)
          break label36;
        boolean bool = this.messageAndCloseQueue.isEmpty();
        if (!bool)
          break label36;
      }
      for (int i = 0; ; i = 1)
      {
        return i;
        label36: this.pongQueue.add(paramByteString);
        runWriter();
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  int pongCount()
  {
    monitorenter;
    try
    {
      int i = this.pongCount;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  boolean processNextFrame()
    throws IOException
  {
    try
    {
      this.reader.processNextFrame();
      int i = this.receivedCloseCode;
      int j = 0;
      if (i == -1)
        j = 1;
      return j;
    }
    catch (Exception localException)
    {
      failWebSocket(localException, null);
    }
    return false;
  }

  public long queueSize()
  {
    monitorenter;
    try
    {
      long l = this.queueSize;
      monitorexit;
      return l;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public Request request()
  {
    return this.originalRequest;
  }

  public boolean send(String paramString)
  {
    if (paramString == null)
      throw new NullPointerException("text == null");
    return send(ByteString.encodeUtf8(paramString), 1);
  }

  public boolean send(ByteString paramByteString)
  {
    if (paramByteString == null)
      throw new NullPointerException("bytes == null");
    return send(paramByteString, 2);
  }

  void tearDown()
    throws InterruptedException
  {
    if (this.cancelFuture != null)
      this.cancelFuture.cancel(false);
    this.executor.shutdown();
    this.executor.awaitTermination(10L, TimeUnit.SECONDS);
  }

  // ERROR //
  boolean writeOneFrame()
    throws IOException
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore_1
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 176	okhttp3/internal/ws/RealWebSocket:failed	Z
    //   8: ifeq +7 -> 15
    //   11: aload_0
    //   12: monitorexit
    //   13: iconst_0
    //   14: ireturn
    //   15: aload_0
    //   16: getfield 389	okhttp3/internal/ws/RealWebSocket:writer	Lokhttp3/internal/ws/WebSocketWriter;
    //   19: astore_3
    //   20: aload_0
    //   21: getfield 90	okhttp3/internal/ws/RealWebSocket:pongQueue	Ljava/util/ArrayDeque;
    //   24: invokevirtual 489	java/util/ArrayDeque:poll	()Ljava/lang/Object;
    //   27: checkcast 139	okio/ByteString
    //   30: astore 4
    //   32: aconst_null
    //   33: astore 5
    //   35: aconst_null
    //   36: astore 6
    //   38: aconst_null
    //   39: astore 7
    //   41: aload 4
    //   43: ifnonnull +56 -> 99
    //   46: aload_0
    //   47: getfield 92	okhttp3/internal/ws/RealWebSocket:messageAndCloseQueue	Ljava/util/ArrayDeque;
    //   50: invokevirtual 489	java/util/ArrayDeque:poll	()Ljava/lang/Object;
    //   53: astore 5
    //   55: aload 5
    //   57: instanceof 283
    //   60: ifeq +102 -> 162
    //   63: aload_0
    //   64: getfield 94	okhttp3/internal/ws/RealWebSocket:receivedCloseCode	I
    //   67: istore_1
    //   68: aload_0
    //   69: getfield 439	okhttp3/internal/ws/RealWebSocket:receivedCloseReason	Ljava/lang/String;
    //   72: astore 6
    //   74: iload_1
    //   75: iconst_m1
    //   76: if_icmpeq +43 -> 119
    //   79: aload_0
    //   80: getfield 347	okhttp3/internal/ws/RealWebSocket:streams	Lokhttp3/internal/ws/RealWebSocket$Streams;
    //   83: astore 7
    //   85: aload_0
    //   86: aconst_null
    //   87: putfield 347	okhttp3/internal/ws/RealWebSocket:streams	Lokhttp3/internal/ws/RealWebSocket$Streams;
    //   90: aload_0
    //   91: getfield 166	okhttp3/internal/ws/RealWebSocket:executor	Ljava/util/concurrent/ScheduledExecutorService;
    //   94: invokeinterface 357 1 0
    //   99: aload_0
    //   100: monitorexit
    //   101: aload 4
    //   103: ifnull +74 -> 177
    //   106: aload_3
    //   107: aload 4
    //   109: invokevirtual 492	okhttp3/internal/ws/WebSocketWriter:writePong	(Lokio/ByteString;)V
    //   112: aload 7
    //   114: invokestatic 369	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   117: iconst_1
    //   118: ireturn
    //   119: aload_0
    //   120: aload_0
    //   121: getfield 166	okhttp3/internal/ws/RealWebSocket:executor	Ljava/util/concurrent/ScheduledExecutorService;
    //   124: new 494	okhttp3/internal/ws/RealWebSocket$CancelRunnable
    //   127: dup
    //   128: aload_0
    //   129: invokespecial 495	okhttp3/internal/ws/RealWebSocket$CancelRunnable:<init>	(Lokhttp3/internal/ws/RealWebSocket;)V
    //   132: aload 5
    //   134: checkcast 283	okhttp3/internal/ws/RealWebSocket$Close
    //   137: getfield 498	okhttp3/internal/ws/RealWebSocket$Close:cancelAfterCloseMillis	J
    //   140: getstatic 407	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   143: invokeinterface 502 5 0
    //   148: putfield 349	okhttp3/internal/ws/RealWebSocket:cancelFuture	Ljava/util/concurrent/ScheduledFuture;
    //   151: aconst_null
    //   152: astore 7
    //   154: goto -55 -> 99
    //   157: astore_2
    //   158: aload_0
    //   159: monitorexit
    //   160: aload_2
    //   161: athrow
    //   162: aconst_null
    //   163: astore 6
    //   165: aconst_null
    //   166: astore 7
    //   168: aload 5
    //   170: ifnonnull -71 -> 99
    //   173: aload_0
    //   174: monitorexit
    //   175: iconst_0
    //   176: ireturn
    //   177: aload 5
    //   179: instanceof 190
    //   182: ifeq +92 -> 274
    //   185: aload 5
    //   187: checkcast 190	okhttp3/internal/ws/RealWebSocket$Message
    //   190: getfield 506	okhttp3/internal/ws/RealWebSocket$Message:data	Lokio/ByteString;
    //   193: astore 10
    //   195: aload_3
    //   196: aload 5
    //   198: checkcast 190	okhttp3/internal/ws/RealWebSocket$Message
    //   201: getfield 509	okhttp3/internal/ws/RealWebSocket$Message:formatOpcode	I
    //   204: aload 10
    //   206: invokevirtual 184	okio/ByteString:size	()I
    //   209: i2l
    //   210: invokevirtual 513	okhttp3/internal/ws/WebSocketWriter:newMessageSink	(IJ)Lokio/Sink;
    //   213: invokestatic 519	okio/Okio:buffer	(Lokio/Sink;)Lokio/BufferedSink;
    //   216: astore 11
    //   218: aload 11
    //   220: aload 10
    //   222: invokeinterface 525 2 0
    //   227: pop
    //   228: aload 11
    //   230: invokeinterface 527 1 0
    //   235: aload_0
    //   236: monitorenter
    //   237: aload_0
    //   238: aload_0
    //   239: getfield 180	okhttp3/internal/ws/RealWebSocket:queueSize	J
    //   242: aload 10
    //   244: invokevirtual 184	okio/ByteString:size	()I
    //   247: i2l
    //   248: lsub
    //   249: putfield 180	okhttp3/internal/ws/RealWebSocket:queueSize	J
    //   252: aload_0
    //   253: monitorexit
    //   254: goto -142 -> 112
    //   257: astore 13
    //   259: aload_0
    //   260: monitorexit
    //   261: aload 13
    //   263: athrow
    //   264: astore 8
    //   266: aload 7
    //   268: invokestatic 369	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   271: aload 8
    //   273: athrow
    //   274: aload 5
    //   276: instanceof 283
    //   279: ifeq +43 -> 322
    //   282: aload 5
    //   284: checkcast 283	okhttp3/internal/ws/RealWebSocket$Close
    //   287: astore 9
    //   289: aload_3
    //   290: aload 9
    //   292: getfield 529	okhttp3/internal/ws/RealWebSocket$Close:code	I
    //   295: aload 9
    //   297: getfield 532	okhttp3/internal/ws/RealWebSocket$Close:reason	Lokio/ByteString;
    //   300: invokevirtual 535	okhttp3/internal/ws/WebSocketWriter:writeClose	(ILokio/ByteString;)V
    //   303: aload 7
    //   305: ifnull -193 -> 112
    //   308: aload_0
    //   309: getfield 129	okhttp3/internal/ws/RealWebSocket:listener	Lokhttp3/WebSocketListener;
    //   312: aload_0
    //   313: iload_1
    //   314: aload 6
    //   316: invokevirtual 446	okhttp3/WebSocketListener:onClosed	(Lokhttp3/WebSocket;ILjava/lang/String;)V
    //   319: goto -207 -> 112
    //   322: new 163	java/lang/AssertionError
    //   325: dup
    //   326: invokespecial 164	java/lang/AssertionError:<init>	()V
    //   329: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   4	13	157	finally
    //   15	32	157	finally
    //   46	74	157	finally
    //   79	99	157	finally
    //   99	101	157	finally
    //   119	151	157	finally
    //   158	160	157	finally
    //   173	175	157	finally
    //   237	254	257	finally
    //   259	261	257	finally
    //   106	112	264	finally
    //   177	237	264	finally
    //   261	264	264	finally
    //   274	303	264	finally
    //   308	319	264	finally
    //   322	330	264	finally
  }

  void writePingFrame()
  {
    monitorenter;
    try
    {
      if (this.failed)
        return;
      WebSocketWriter localWebSocketWriter = this.writer;
      monitorexit;
      try
      {
        localWebSocketWriter.writePing(ByteString.EMPTY);
        return;
      }
      catch (IOException localIOException)
      {
        failWebSocket(localIOException, null);
        return;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  final class CancelRunnable
    implements Runnable
  {
    CancelRunnable()
    {
    }

    public void run()
    {
      RealWebSocket.this.cancel();
    }
  }

  static final class Close
  {
    final long cancelAfterCloseMillis;
    final int code;
    final ByteString reason;

    Close(int paramInt, ByteString paramByteString, long paramLong)
    {
      this.code = paramInt;
      this.reason = paramByteString;
      this.cancelAfterCloseMillis = paramLong;
    }
  }

  static final class Message
  {
    final ByteString data;
    final int formatOpcode;

    Message(int paramInt, ByteString paramByteString)
    {
      this.formatOpcode = paramInt;
      this.data = paramByteString;
    }
  }

  private final class PingRunnable
    implements Runnable
  {
    PingRunnable()
    {
    }

    public void run()
    {
      RealWebSocket.this.writePingFrame();
    }
  }

  public static abstract class Streams
    implements Closeable
  {
    public final boolean client;
    public final BufferedSink sink;
    public final BufferedSource source;

    public Streams(boolean paramBoolean, BufferedSource paramBufferedSource, BufferedSink paramBufferedSink)
    {
      this.client = paramBoolean;
      this.source = paramBufferedSource;
      this.sink = paramBufferedSink;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.ws.RealWebSocket
 * JD-Core Version:    0.6.0
 */