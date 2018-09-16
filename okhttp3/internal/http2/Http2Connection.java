package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Protocol;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.Util;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

public final class Http2Connection
  implements Closeable
{
  private static final int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
  static final ExecutorService executor;
  long bytesLeftInWriteWindow;
  final boolean client;
  final Set<Integer> currentPushRequests = new LinkedHashSet();
  final String hostname;
  int lastGoodStreamId;
  final Listener listener;
  private int nextPingId;
  int nextStreamId;
  Settings okHttpSettings = new Settings();
  final Settings peerSettings = new Settings();
  private Map<Integer, Ping> pings;
  private final ExecutorService pushExecutor;
  final PushObserver pushObserver;
  final ReaderRunnable readerRunnable;
  boolean receivedInitialPeerSettings = false;
  boolean shutdown;
  final Socket socket;
  final Map<Integer, Http2Stream> streams = new LinkedHashMap();
  long unacknowledgedBytesRead = 0L;
  final Http2Writer writer;

  static
  {
    if (!Http2Connection.class.desiredAssertionStatus());
    for (boolean bool = true; ; bool = false)
    {
      $assertionsDisabled = bool;
      executor = new ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp Http2Connection", true));
      return;
    }
  }

  Http2Connection(Builder paramBuilder)
  {
    this.pushObserver = paramBuilder.pushObserver;
    this.client = paramBuilder.client;
    this.listener = paramBuilder.listener;
    if (paramBuilder.client);
    for (int j = 1; ; j = i)
    {
      this.nextStreamId = j;
      if (paramBuilder.client)
        this.nextStreamId = (2 + this.nextStreamId);
      if (paramBuilder.client)
        i = 1;
      this.nextPingId = i;
      if (paramBuilder.client)
        this.okHttpSettings.set(7, 16777216);
      this.hostname = paramBuilder.hostname;
      TimeUnit localTimeUnit = TimeUnit.SECONDS;
      LinkedBlockingQueue localLinkedBlockingQueue = new LinkedBlockingQueue();
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.hostname;
      this.pushExecutor = new ThreadPoolExecutor(0, 1, 60L, localTimeUnit, localLinkedBlockingQueue, Util.threadFactory(Util.format("OkHttp %s Push Observer", arrayOfObject), true));
      this.peerSettings.set(7, 65535);
      this.peerSettings.set(5, 16384);
      this.bytesLeftInWriteWindow = this.peerSettings.getInitialWindowSize();
      this.socket = paramBuilder.socket;
      this.writer = new Http2Writer(paramBuilder.sink, this.client);
      this.readerRunnable = new ReaderRunnable(new Http2Reader(paramBuilder.source, this.client));
      return;
    }
  }

  private Http2Stream newStream(int paramInt, List<Header> paramList, boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean);
    for (boolean bool = true; ; bool = false)
      synchronized (this.writer)
      {
        monitorenter;
        try
        {
          if (!this.shutdown)
            break;
          throw new ConnectionShutdownException();
        }
        finally
        {
          monitorexit;
        }
      }
    int i = this.nextStreamId;
    this.nextStreamId = (2 + this.nextStreamId);
    Http2Stream localHttp2Stream = new Http2Stream(i, this, bool, false, paramList);
    if ((paramBoolean) && (this.bytesLeftInWriteWindow != 0L))
      if (localHttp2Stream.bytesLeftInWriteWindow == 0L)
        break label211;
    while (true)
    {
      if (localHttp2Stream.isOpen())
        this.streams.put(Integer.valueOf(i), localHttp2Stream);
      monitorexit;
      if (paramInt == 0)
        this.writer.synStream(bool, i, paramInt, paramList);
      while (true)
      {
        monitorexit;
        if (j != 0)
          this.writer.flush();
        return localHttp2Stream;
        j = 0;
        break;
        if (this.client)
          throw new IllegalArgumentException("client streams shouldn't have associated stream IDs");
        this.writer.pushPromise(paramInt, i, paramList);
      }
      label211: int j = 1;
    }
  }

  void addBytesToWriteWindow(long paramLong)
  {
    this.bytesLeftInWriteWindow = (paramLong + this.bytesLeftInWriteWindow);
    if (paramLong > 0L)
      notifyAll();
  }

  public void close()
    throws IOException
  {
    close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
  }

  // ERROR //
  void close(ErrorCode paramErrorCode1, ErrorCode paramErrorCode2)
    throws IOException
  {
    // Byte code:
    //   0: getstatic 57	okhttp3/internal/http2/Http2Connection:$assertionsDisabled	Z
    //   3: ifne +18 -> 21
    //   6: aload_0
    //   7: invokestatic 251	java/lang/Thread:holdsLock	(Ljava/lang/Object;)Z
    //   10: ifeq +11 -> 21
    //   13: new 253	java/lang/AssertionError
    //   16: dup
    //   17: invokespecial 254	java/lang/AssertionError:<init>	()V
    //   20: athrow
    //   21: aconst_null
    //   22: astore_3
    //   23: aload_0
    //   24: aload_1
    //   25: invokevirtual 257	okhttp3/internal/http2/Http2Connection:shutdown	(Lokhttp3/internal/http2/ErrorCode;)V
    //   28: aload_0
    //   29: monitorenter
    //   30: aload_0
    //   31: getfield 93	okhttp3/internal/http2/Http2Connection:streams	Ljava/util/Map;
    //   34: invokeinterface 260 1 0
    //   39: istore 6
    //   41: aconst_null
    //   42: astore 7
    //   44: iload 6
    //   46: ifne +43 -> 89
    //   49: aload_0
    //   50: getfield 93	okhttp3/internal/http2/Http2Connection:streams	Ljava/util/Map;
    //   53: invokeinterface 264 1 0
    //   58: aload_0
    //   59: getfield 93	okhttp3/internal/http2/Http2Connection:streams	Ljava/util/Map;
    //   62: invokeinterface 267 1 0
    //   67: anewarray 190	okhttp3/internal/http2/Http2Stream
    //   70: invokeinterface 273 2 0
    //   75: checkcast 275	[Lokhttp3/internal/http2/Http2Stream;
    //   78: astore 7
    //   80: aload_0
    //   81: getfield 93	okhttp3/internal/http2/Http2Connection:streams	Ljava/util/Map;
    //   84: invokeinterface 278 1 0
    //   89: aload_0
    //   90: getfield 280	okhttp3/internal/http2/Http2Connection:pings	Ljava/util/Map;
    //   93: astore 8
    //   95: aconst_null
    //   96: astore 9
    //   98: aload 8
    //   100: ifnull +39 -> 139
    //   103: aload_0
    //   104: getfield 280	okhttp3/internal/http2/Http2Connection:pings	Ljava/util/Map;
    //   107: invokeinterface 264 1 0
    //   112: aload_0
    //   113: getfield 280	okhttp3/internal/http2/Http2Connection:pings	Ljava/util/Map;
    //   116: invokeinterface 267 1 0
    //   121: anewarray 282	okhttp3/internal/http2/Ping
    //   124: invokeinterface 273 2 0
    //   129: checkcast 284	[Lokhttp3/internal/http2/Ping;
    //   132: astore 9
    //   134: aload_0
    //   135: aconst_null
    //   136: putfield 280	okhttp3/internal/http2/Http2Connection:pings	Ljava/util/Map;
    //   139: aload_0
    //   140: monitorexit
    //   141: aload 7
    //   143: ifnull +64 -> 207
    //   146: aload 7
    //   148: arraylength
    //   149: istore 14
    //   151: iconst_0
    //   152: istore 15
    //   154: iload 15
    //   156: iload 14
    //   158: if_icmpge +49 -> 207
    //   161: aload 7
    //   163: iload 15
    //   165: aaload
    //   166: astore 16
    //   168: aload 16
    //   170: aload_2
    //   171: invokevirtual 286	okhttp3/internal/http2/Http2Stream:close	(Lokhttp3/internal/http2/ErrorCode;)V
    //   174: iinc 15 1
    //   177: goto -23 -> 154
    //   180: astore 4
    //   182: aload 4
    //   184: astore_3
    //   185: goto -157 -> 28
    //   188: astore 5
    //   190: aload_0
    //   191: monitorexit
    //   192: aload 5
    //   194: athrow
    //   195: astore 17
    //   197: aload_3
    //   198: ifnull -24 -> 174
    //   201: aload 17
    //   203: astore_3
    //   204: goto -30 -> 174
    //   207: aload 9
    //   209: ifnull +32 -> 241
    //   212: aload 9
    //   214: arraylength
    //   215: istore 12
    //   217: iconst_0
    //   218: istore 13
    //   220: iload 13
    //   222: iload 12
    //   224: if_icmpge +17 -> 241
    //   227: aload 9
    //   229: iload 13
    //   231: aaload
    //   232: invokevirtual 289	okhttp3/internal/http2/Ping:cancel	()V
    //   235: iinc 13 1
    //   238: goto -18 -> 220
    //   241: aload_0
    //   242: getfield 163	okhttp3/internal/http2/Http2Connection:writer	Lokhttp3/internal/http2/Http2Writer;
    //   245: invokevirtual 291	okhttp3/internal/http2/Http2Writer:close	()V
    //   248: aload_0
    //   249: getfield 152	okhttp3/internal/http2/Http2Connection:socket	Ljava/net/Socket;
    //   252: invokevirtual 294	java/net/Socket:close	()V
    //   255: aload_3
    //   256: ifnull +25 -> 281
    //   259: aload_3
    //   260: athrow
    //   261: astore 10
    //   263: aload_3
    //   264: ifnonnull -16 -> 248
    //   267: aload 10
    //   269: astore_3
    //   270: goto -22 -> 248
    //   273: astore 11
    //   275: aload 11
    //   277: astore_3
    //   278: goto -23 -> 255
    //   281: return
    //
    // Exception table:
    //   from	to	target	type
    //   23	28	180	java/io/IOException
    //   30	41	188	finally
    //   49	89	188	finally
    //   89	95	188	finally
    //   103	139	188	finally
    //   139	141	188	finally
    //   190	192	188	finally
    //   168	174	195	java/io/IOException
    //   241	248	261	java/io/IOException
    //   248	255	273	java/io/IOException
  }

  public void flush()
    throws IOException
  {
    this.writer.flush();
  }

  public Protocol getProtocol()
  {
    return Protocol.HTTP_2;
  }

  Http2Stream getStream(int paramInt)
  {
    monitorenter;
    try
    {
      Http2Stream localHttp2Stream = (Http2Stream)this.streams.get(Integer.valueOf(paramInt));
      monitorexit;
      return localHttp2Stream;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean isShutdown()
  {
    monitorenter;
    try
    {
      boolean bool = this.shutdown;
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

  public int maxConcurrentStreams()
  {
    monitorenter;
    try
    {
      int i = this.peerSettings.getMaxConcurrentStreams(2147483647);
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

  public Http2Stream newStream(List<Header> paramList, boolean paramBoolean)
    throws IOException
  {
    return newStream(0, paramList, paramBoolean);
  }

  public int openStreamCount()
  {
    monitorenter;
    try
    {
      int i = this.streams.size();
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

  public Ping ping()
    throws IOException
  {
    Ping localPing = new Ping();
    monitorenter;
    try
    {
      if (this.shutdown)
        throw new ConnectionShutdownException();
    }
    finally
    {
      monitorexit;
    }
    int i = this.nextPingId;
    this.nextPingId = (2 + this.nextPingId);
    if (this.pings == null)
      this.pings = new LinkedHashMap();
    this.pings.put(Integer.valueOf(i), localPing);
    monitorexit;
    writePing(false, i, 1330343787, localPing);
    return localPing;
  }

  void pushDataLater(int paramInt1, BufferedSource paramBufferedSource, int paramInt2, boolean paramBoolean)
    throws IOException
  {
    Buffer localBuffer = new Buffer();
    paramBufferedSource.require(paramInt2);
    paramBufferedSource.read(localBuffer, paramInt2);
    if (localBuffer.size() != paramInt2)
      throw new IOException(localBuffer.size() + " != " + paramInt2);
    ExecutorService localExecutorService = this.pushExecutor;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.hostname;
    arrayOfObject[1] = Integer.valueOf(paramInt1);
    localExecutorService.execute(new NamedRunnable("OkHttp %s Push Data[%s]", arrayOfObject, paramInt1, localBuffer, paramInt2, paramBoolean)
    {
      public void execute()
      {
        try
        {
          boolean bool = Http2Connection.this.pushObserver.onData(this.val$streamId, this.val$buffer, this.val$byteCount, this.val$inFinished);
          if (bool)
            Http2Connection.this.writer.rstStream(this.val$streamId, ErrorCode.CANCEL);
          if ((bool) || (this.val$inFinished))
            synchronized (Http2Connection.this)
            {
              Http2Connection.this.currentPushRequests.remove(Integer.valueOf(this.val$streamId));
              return;
            }
        }
        catch (IOException localIOException)
        {
        }
      }
    });
  }

  void pushHeadersLater(int paramInt, List<Header> paramList, boolean paramBoolean)
  {
    ExecutorService localExecutorService = this.pushExecutor;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.hostname;
    arrayOfObject[1] = Integer.valueOf(paramInt);
    localExecutorService.execute(new NamedRunnable("OkHttp %s Push Headers[%s]", arrayOfObject, paramInt, paramList, paramBoolean)
    {
      public void execute()
      {
        boolean bool = Http2Connection.this.pushObserver.onHeaders(this.val$streamId, this.val$requestHeaders, this.val$inFinished);
        if (bool);
        try
        {
          Http2Connection.this.writer.rstStream(this.val$streamId, ErrorCode.CANCEL);
          if ((bool) || (this.val$inFinished))
            synchronized (Http2Connection.this)
            {
              Http2Connection.this.currentPushRequests.remove(Integer.valueOf(this.val$streamId));
              return;
            }
        }
        catch (IOException localIOException)
        {
        }
      }
    });
  }

  void pushRequestLater(int paramInt, List<Header> paramList)
  {
    monitorenter;
    try
    {
      if (this.currentPushRequests.contains(Integer.valueOf(paramInt)))
      {
        writeSynResetLater(paramInt, ErrorCode.PROTOCOL_ERROR);
        return;
      }
      this.currentPushRequests.add(Integer.valueOf(paramInt));
      monitorexit;
      ExecutorService localExecutorService = this.pushExecutor;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = this.hostname;
      arrayOfObject[1] = Integer.valueOf(paramInt);
      localExecutorService.execute(new NamedRunnable("OkHttp %s Push Request[%s]", arrayOfObject, paramInt, paramList)
      {
        public void execute()
        {
          if (Http2Connection.this.pushObserver.onRequest(this.val$streamId, this.val$requestHeaders))
            try
            {
              Http2Connection.this.writer.rstStream(this.val$streamId, ErrorCode.CANCEL);
              synchronized (Http2Connection.this)
              {
                Http2Connection.this.currentPushRequests.remove(Integer.valueOf(this.val$streamId));
                return;
              }
            }
            catch (IOException localIOException)
            {
            }
        }
      });
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  void pushResetLater(int paramInt, ErrorCode paramErrorCode)
  {
    ExecutorService localExecutorService = this.pushExecutor;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.hostname;
    arrayOfObject[1] = Integer.valueOf(paramInt);
    localExecutorService.execute(new NamedRunnable("OkHttp %s Push Reset[%s]", arrayOfObject, paramInt, paramErrorCode)
    {
      public void execute()
      {
        Http2Connection.this.pushObserver.onReset(this.val$streamId, this.val$errorCode);
        synchronized (Http2Connection.this)
        {
          Http2Connection.this.currentPushRequests.remove(Integer.valueOf(this.val$streamId));
          return;
        }
      }
    });
  }

  public Http2Stream pushStream(int paramInt, List<Header> paramList, boolean paramBoolean)
    throws IOException
  {
    if (this.client)
      throw new IllegalStateException("Client cannot push requests.");
    return newStream(paramInt, paramList, paramBoolean);
  }

  boolean pushedStream(int paramInt)
  {
    return (paramInt != 0) && ((paramInt & 0x1) == 0);
  }

  Ping removePing(int paramInt)
  {
    monitorenter;
    try
    {
      if (this.pings != null)
      {
        localPing = (Ping)this.pings.remove(Integer.valueOf(paramInt));
        return localPing;
      }
      Ping localPing = null;
    }
    finally
    {
      monitorexit;
    }
  }

  Http2Stream removeStream(int paramInt)
  {
    monitorenter;
    try
    {
      Http2Stream localHttp2Stream = (Http2Stream)this.streams.remove(Integer.valueOf(paramInt));
      notifyAll();
      monitorexit;
      return localHttp2Stream;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public void setSettings(Settings paramSettings)
    throws IOException
  {
    synchronized (this.writer)
    {
      monitorenter;
      try
      {
        if (this.shutdown)
          throw new ConnectionShutdownException();
      }
      finally
      {
        monitorexit;
      }
    }
    this.okHttpSettings.merge(paramSettings);
    this.writer.settings(paramSettings);
    monitorexit;
    monitorexit;
  }

  public void shutdown(ErrorCode paramErrorCode)
    throws IOException
  {
    synchronized (this.writer)
    {
      monitorenter;
    }
    try
    {
      if (this.shutdown)
      {
        monitorexit;
        return;
      }
      this.shutdown = true;
      int i = this.lastGoodStreamId;
      monitorexit;
      this.writer.goAway(i, paramErrorCode, Util.EMPTY_BYTE_ARRAY);
      return;
      localObject1 = finally;
      throw localObject1;
    }
    finally
    {
      monitorexit;
    }
    throw localObject2;
  }

  public void start()
    throws IOException
  {
    start(true);
  }

  void start(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean)
    {
      this.writer.connectionPreface();
      this.writer.settings(this.okHttpSettings);
      int i = this.okHttpSettings.getInitialWindowSize();
      if (i != 65535)
        this.writer.windowUpdate(0, i - 65535);
    }
    new Thread(this.readerRunnable).start();
  }

  public void writeData(int paramInt, boolean paramBoolean, Buffer paramBuffer, long paramLong)
    throws IOException
  {
    if (paramLong == 0L)
    {
      this.writer.data(paramBoolean, paramInt, paramBuffer, 0);
      return;
    }
    while (true)
    {
      try
      {
        int i = Math.min((int)Math.min(paramLong, this.bytesLeftInWriteWindow), this.writer.maxDataLength());
        this.bytesLeftInWriteWindow -= i;
        monitorexit;
        paramLong -= i;
        Http2Writer localHttp2Writer = this.writer;
        if ((!paramBoolean) || (paramLong != 0L))
          break label163;
        bool = true;
        localHttp2Writer.data(bool, paramInt, paramBuffer, i);
        if (paramLong <= 0L)
          break;
        monitorenter;
        try
        {
          if (this.bytesLeftInWriteWindow > 0L)
            continue;
          if (!this.streams.containsKey(Integer.valueOf(paramInt)))
            throw new IOException("stream closed");
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new InterruptedIOException();
        }
      }
      finally
      {
        monitorexit;
      }
      wait();
      continue;
      label163: boolean bool = false;
    }
  }

  void writePing(boolean paramBoolean, int paramInt1, int paramInt2, Ping paramPing)
    throws IOException
  {
    Http2Writer localHttp2Writer = this.writer;
    monitorenter;
    if (paramPing != null);
    try
    {
      paramPing.send();
      this.writer.ping(paramBoolean, paramInt1, paramInt2);
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  void writePingLater(boolean paramBoolean, int paramInt1, int paramInt2, Ping paramPing)
  {
    ExecutorService localExecutorService = executor;
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = this.hostname;
    arrayOfObject[1] = Integer.valueOf(paramInt1);
    arrayOfObject[2] = Integer.valueOf(paramInt2);
    localExecutorService.execute(new NamedRunnable("OkHttp %s ping %08x%08x", arrayOfObject, paramBoolean, paramInt1, paramInt2, paramPing)
    {
      public void execute()
      {
        try
        {
          Http2Connection.this.writePing(this.val$reply, this.val$payload1, this.val$payload2, this.val$ping);
          return;
        }
        catch (IOException localIOException)
        {
        }
      }
    });
  }

  void writeSynReply(int paramInt, boolean paramBoolean, List<Header> paramList)
    throws IOException
  {
    this.writer.synReply(paramBoolean, paramInt, paramList);
  }

  void writeSynReset(int paramInt, ErrorCode paramErrorCode)
    throws IOException
  {
    this.writer.rstStream(paramInt, paramErrorCode);
  }

  void writeSynResetLater(int paramInt, ErrorCode paramErrorCode)
  {
    ExecutorService localExecutorService = executor;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.hostname;
    arrayOfObject[1] = Integer.valueOf(paramInt);
    localExecutorService.execute(new NamedRunnable("OkHttp %s stream %d", arrayOfObject, paramInt, paramErrorCode)
    {
      public void execute()
      {
        try
        {
          Http2Connection.this.writeSynReset(this.val$streamId, this.val$errorCode);
          return;
        }
        catch (IOException localIOException)
        {
        }
      }
    });
  }

  void writeWindowUpdateLater(int paramInt, long paramLong)
  {
    ExecutorService localExecutorService = executor;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.hostname;
    arrayOfObject[1] = Integer.valueOf(paramInt);
    localExecutorService.execute(new NamedRunnable("OkHttp Window Update %s stream %d", arrayOfObject, paramInt, paramLong)
    {
      public void execute()
      {
        try
        {
          Http2Connection.this.writer.windowUpdate(this.val$streamId, this.val$unacknowledgedBytesRead);
          return;
        }
        catch (IOException localIOException)
        {
        }
      }
    });
  }

  public static class Builder
  {
    boolean client;
    String hostname;
    Http2Connection.Listener listener = Http2Connection.Listener.REFUSE_INCOMING_STREAMS;
    PushObserver pushObserver = PushObserver.CANCEL;
    BufferedSink sink;
    Socket socket;
    BufferedSource source;

    public Builder(boolean paramBoolean)
    {
      this.client = paramBoolean;
    }

    public Http2Connection build()
      throws IOException
    {
      return new Http2Connection(this);
    }

    public Builder listener(Http2Connection.Listener paramListener)
    {
      this.listener = paramListener;
      return this;
    }

    public Builder pushObserver(PushObserver paramPushObserver)
    {
      this.pushObserver = paramPushObserver;
      return this;
    }

    public Builder socket(Socket paramSocket)
      throws IOException
    {
      return socket(paramSocket, ((InetSocketAddress)paramSocket.getRemoteSocketAddress()).getHostName(), Okio.buffer(Okio.source(paramSocket)), Okio.buffer(Okio.sink(paramSocket)));
    }

    public Builder socket(Socket paramSocket, String paramString, BufferedSource paramBufferedSource, BufferedSink paramBufferedSink)
    {
      this.socket = paramSocket;
      this.hostname = paramString;
      this.source = paramBufferedSource;
      this.sink = paramBufferedSink;
      return this;
    }
  }

  public static abstract class Listener
  {
    public static final Listener REFUSE_INCOMING_STREAMS = new Listener()
    {
      public void onStream(Http2Stream paramHttp2Stream)
        throws IOException
      {
        paramHttp2Stream.close(ErrorCode.REFUSED_STREAM);
      }
    };

    public void onSettings(Http2Connection paramHttp2Connection)
    {
    }

    public abstract void onStream(Http2Stream paramHttp2Stream)
      throws IOException;
  }

  class ReaderRunnable extends NamedRunnable
    implements Http2Reader.Handler
  {
    final Http2Reader reader;

    ReaderRunnable(Http2Reader arg2)
    {
      super(arrayOfObject);
      Object localObject;
      this.reader = localObject;
    }

    private void applyAndAckSettings(Settings paramSettings)
    {
      ExecutorService localExecutorService = Http2Connection.executor;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Http2Connection.this.hostname;
      localExecutorService.execute(new NamedRunnable("OkHttp %s ACK Settings", arrayOfObject, paramSettings)
      {
        public void execute()
        {
          try
          {
            Http2Connection.this.writer.applyAndAckSettings(this.val$peerSettings);
            return;
          }
          catch (IOException localIOException)
          {
          }
        }
      });
    }

    public void ackSettings()
    {
    }

    public void alternateService(int paramInt1, String paramString1, ByteString paramByteString, String paramString2, int paramInt2, long paramLong)
    {
    }

    public void data(boolean paramBoolean, int paramInt1, BufferedSource paramBufferedSource, int paramInt2)
      throws IOException
    {
      if (Http2Connection.this.pushedStream(paramInt1))
        Http2Connection.this.pushDataLater(paramInt1, paramBufferedSource, paramInt2, paramBoolean);
      Http2Stream localHttp2Stream;
      do
      {
        return;
        localHttp2Stream = Http2Connection.this.getStream(paramInt1);
        if (localHttp2Stream == null)
        {
          Http2Connection.this.writeSynResetLater(paramInt1, ErrorCode.PROTOCOL_ERROR);
          paramBufferedSource.skip(paramInt2);
          return;
        }
        localHttp2Stream.receiveData(paramBufferedSource, paramInt2);
      }
      while (!paramBoolean);
      localHttp2Stream.receiveFin();
    }

    // ERROR //
    protected void execute()
    {
      // Byte code:
      //   0: getstatic 96	okhttp3/internal/http2/ErrorCode:INTERNAL_ERROR	Lokhttp3/internal/http2/ErrorCode;
      //   3: astore_1
      //   4: getstatic 96	okhttp3/internal/http2/ErrorCode:INTERNAL_ERROR	Lokhttp3/internal/http2/ErrorCode;
      //   7: astore_2
      //   8: aload_0
      //   9: getfield 29	okhttp3/internal/http2/Http2Connection$ReaderRunnable:reader	Lokhttp3/internal/http2/Http2Reader;
      //   12: aload_0
      //   13: invokevirtual 102	okhttp3/internal/http2/Http2Reader:readConnectionPreface	(Lokhttp3/internal/http2/Http2Reader$Handler;)V
      //   16: aload_0
      //   17: getfield 29	okhttp3/internal/http2/Http2Connection$ReaderRunnable:reader	Lokhttp3/internal/http2/Http2Reader;
      //   20: iconst_0
      //   21: aload_0
      //   22: invokevirtual 106	okhttp3/internal/http2/Http2Reader:nextFrame	(ZLokhttp3/internal/http2/Http2Reader$Handler;)Z
      //   25: ifne -9 -> 16
      //   28: getstatic 109	okhttp3/internal/http2/ErrorCode:NO_ERROR	Lokhttp3/internal/http2/ErrorCode;
      //   31: astore_1
      //   32: getstatic 112	okhttp3/internal/http2/ErrorCode:CANCEL	Lokhttp3/internal/http2/ErrorCode;
      //   35: astore 8
      //   37: aload_0
      //   38: getfield 14	okhttp3/internal/http2/Http2Connection$ReaderRunnable:this$0	Lokhttp3/internal/http2/Http2Connection;
      //   41: aload_1
      //   42: aload 8
      //   44: invokevirtual 116	okhttp3/internal/http2/Http2Connection:close	(Lokhttp3/internal/http2/ErrorCode;Lokhttp3/internal/http2/ErrorCode;)V
      //   47: aload_0
      //   48: getfield 29	okhttp3/internal/http2/Http2Connection$ReaderRunnable:reader	Lokhttp3/internal/http2/Http2Reader;
      //   51: invokestatic 122	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
      //   54: return
      //   55: astore 5
      //   57: getstatic 74	okhttp3/internal/http2/ErrorCode:PROTOCOL_ERROR	Lokhttp3/internal/http2/ErrorCode;
      //   60: astore_1
      //   61: getstatic 74	okhttp3/internal/http2/ErrorCode:PROTOCOL_ERROR	Lokhttp3/internal/http2/ErrorCode;
      //   64: astore 6
      //   66: aload_0
      //   67: getfield 14	okhttp3/internal/http2/Http2Connection$ReaderRunnable:this$0	Lokhttp3/internal/http2/Http2Connection;
      //   70: aload_1
      //   71: aload 6
      //   73: invokevirtual 116	okhttp3/internal/http2/Http2Connection:close	(Lokhttp3/internal/http2/ErrorCode;Lokhttp3/internal/http2/ErrorCode;)V
      //   76: aload_0
      //   77: getfield 29	okhttp3/internal/http2/Http2Connection$ReaderRunnable:reader	Lokhttp3/internal/http2/Http2Reader;
      //   80: invokestatic 122	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
      //   83: return
      //   84: astore_3
      //   85: aload_0
      //   86: getfield 14	okhttp3/internal/http2/Http2Connection$ReaderRunnable:this$0	Lokhttp3/internal/http2/Http2Connection;
      //   89: aload_1
      //   90: aload_2
      //   91: invokevirtual 116	okhttp3/internal/http2/Http2Connection:close	(Lokhttp3/internal/http2/ErrorCode;Lokhttp3/internal/http2/ErrorCode;)V
      //   94: aload_0
      //   95: getfield 29	okhttp3/internal/http2/Http2Connection$ReaderRunnable:reader	Lokhttp3/internal/http2/Http2Reader;
      //   98: invokestatic 122	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
      //   101: aload_3
      //   102: athrow
      //   103: astore 4
      //   105: goto -11 -> 94
      //   108: astore 7
      //   110: goto -34 -> 76
      //   113: astore 9
      //   115: goto -68 -> 47
      //
      // Exception table:
      //   from	to	target	type
      //   8	16	55	java/io/IOException
      //   16	37	55	java/io/IOException
      //   8	16	84	finally
      //   16	37	84	finally
      //   57	66	84	finally
      //   85	94	103	java/io/IOException
      //   66	76	108	java/io/IOException
      //   37	47	113	java/io/IOException
    }

    public void goAway(int paramInt, ErrorCode paramErrorCode, ByteString paramByteString)
    {
      if (paramByteString.size() > 0);
      synchronized (Http2Connection.this)
      {
        Http2Stream[] arrayOfHttp2Stream = (Http2Stream[])Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
        Http2Connection.this.shutdown = true;
        int i = arrayOfHttp2Stream.length;
        int j = 0;
        if (j < i)
        {
          Http2Stream localHttp2Stream = arrayOfHttp2Stream[j];
          if ((localHttp2Stream.getId() > paramInt) && (localHttp2Stream.isLocallyInitiated()))
          {
            localHttp2Stream.receiveRstStream(ErrorCode.REFUSED_STREAM);
            Http2Connection.this.removeStream(localHttp2Stream.getId());
          }
          j++;
        }
      }
    }

    public void headers(boolean paramBoolean, int paramInt1, int paramInt2, List<Header> paramList)
    {
      if (Http2Connection.this.pushedStream(paramInt1))
        Http2Connection.this.pushHeadersLater(paramInt1, paramList, paramBoolean);
      Http2Stream localHttp2Stream1;
      do
      {
        return;
        synchronized (Http2Connection.this)
        {
          if (Http2Connection.this.shutdown)
            return;
        }
        localHttp2Stream1 = Http2Connection.this.getStream(paramInt1);
        if (localHttp2Stream1 == null)
        {
          if (paramInt1 <= Http2Connection.this.lastGoodStreamId)
          {
            monitorexit;
            return;
          }
          if (paramInt1 % 2 == Http2Connection.this.nextStreamId % 2)
          {
            monitorexit;
            return;
          }
          Http2Stream localHttp2Stream2 = new Http2Stream(paramInt1, Http2Connection.this, false, paramBoolean, paramList);
          Http2Connection.this.lastGoodStreamId = paramInt1;
          Http2Connection.this.streams.put(Integer.valueOf(paramInt1), localHttp2Stream2);
          ExecutorService localExecutorService = Http2Connection.executor;
          Object[] arrayOfObject = new Object[2];
          arrayOfObject[0] = Http2Connection.this.hostname;
          arrayOfObject[1] = Integer.valueOf(paramInt1);
          localExecutorService.execute(new NamedRunnable("OkHttp %s stream %d", arrayOfObject, localHttp2Stream2)
          {
            public void execute()
            {
              try
              {
                Http2Connection.this.listener.onStream(this.val$newStream);
                return;
              }
              catch (IOException localIOException1)
              {
                Platform.get().log(4, "Http2Connection.Listener failure for " + Http2Connection.this.hostname, localIOException1);
                try
                {
                  this.val$newStream.close(ErrorCode.PROTOCOL_ERROR);
                  return;
                }
                catch (IOException localIOException2)
                {
                }
              }
            }
          });
          monitorexit;
          return;
        }
        monitorexit;
        localHttp2Stream1.receiveHeaders(paramList);
      }
      while (!paramBoolean);
      localHttp2Stream1.receiveFin();
    }

    public void ping(boolean paramBoolean, int paramInt1, int paramInt2)
    {
      if (paramBoolean)
      {
        Ping localPing = Http2Connection.this.removePing(paramInt1);
        if (localPing != null)
          localPing.receive();
        return;
      }
      Http2Connection.this.writePingLater(true, paramInt1, paramInt2, null);
    }

    public void priority(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
    }

    public void pushPromise(int paramInt1, int paramInt2, List<Header> paramList)
    {
      Http2Connection.this.pushRequestLater(paramInt2, paramList);
    }

    public void rstStream(int paramInt, ErrorCode paramErrorCode)
    {
      if (Http2Connection.this.pushedStream(paramInt))
        Http2Connection.this.pushResetLater(paramInt, paramErrorCode);
      Http2Stream localHttp2Stream;
      do
      {
        return;
        localHttp2Stream = Http2Connection.this.removeStream(paramInt);
      }
      while (localHttp2Stream == null);
      localHttp2Stream.receiveRstStream(paramErrorCode);
    }

    public void settings(boolean paramBoolean, Settings paramSettings)
    {
      long l = 0L;
      while (true)
      {
        Http2Stream[] arrayOfHttp2Stream;
        int m;
        synchronized (Http2Connection.this)
        {
          int i = Http2Connection.this.peerSettings.getInitialWindowSize();
          if (!paramBoolean)
            continue;
          Http2Connection.this.peerSettings.clear();
          Http2Connection.this.peerSettings.merge(paramSettings);
          applyAndAckSettings(paramSettings);
          int j = Http2Connection.this.peerSettings.getInitialWindowSize();
          arrayOfHttp2Stream = null;
          if (j == -1)
            continue;
          arrayOfHttp2Stream = null;
          if (j == i)
            continue;
          l = j - i;
          if (Http2Connection.this.receivedInitialPeerSettings)
            continue;
          Http2Connection.this.addBytesToWriteWindow(l);
          Http2Connection.this.receivedInitialPeerSettings = true;
          boolean bool = Http2Connection.this.streams.isEmpty();
          arrayOfHttp2Stream = null;
          if (bool)
            continue;
          arrayOfHttp2Stream = (Http2Stream[])Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
          ExecutorService localExecutorService = Http2Connection.executor;
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = Http2Connection.this.hostname;
          localExecutorService.execute(new NamedRunnable("OkHttp %s settings", arrayOfObject)
          {
            public void execute()
            {
              Http2Connection.this.listener.onSettings(Http2Connection.this);
            }
          });
          if ((arrayOfHttp2Stream == null) || (l == 0L))
            break;
          int k = arrayOfHttp2Stream.length;
          m = 0;
          if (m >= k)
            break;
        }
        synchronized (arrayOfHttp2Stream[m])
        {
          ???.addBytesToWriteWindow(l);
          m++;
          continue;
          localObject1 = finally;
          monitorexit;
          throw localObject1;
        }
      }
    }

    public void windowUpdate(int paramInt, long paramLong)
    {
      if (paramInt == 0)
        synchronized (Http2Connection.this)
        {
          Http2Connection localHttp2Connection2 = Http2Connection.this;
          localHttp2Connection2.bytesLeftInWriteWindow = (paramLong + localHttp2Connection2.bytesLeftInWriteWindow);
          Http2Connection.this.notifyAll();
          return;
        }
      Http2Stream localHttp2Stream = Http2Connection.this.getStream(paramInt);
      if (localHttp2Stream != null)
      {
        monitorenter;
        try
        {
          localHttp2Stream.addBytesToWriteWindow(paramLong);
          return;
        }
        finally
        {
          monitorexit;
        }
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.http2.Http2Connection
 * JD-Core Version:    0.6.0
 */