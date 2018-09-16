package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.List;
import okhttp3.Address;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.StreamResetException;

public final class StreamAllocation
{
  public final Address address;
  private final Object callStackTrace;
  private boolean canceled;
  private HttpCodec codec;
  private RealConnection connection;
  private final ConnectionPool connectionPool;
  private int refusedStreamCount;
  private boolean released;
  private Route route;
  private final RouteSelector routeSelector;

  static
  {
    if (!StreamAllocation.class.desiredAssertionStatus());
    for (boolean bool = true; ; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }

  public StreamAllocation(ConnectionPool paramConnectionPool, Address paramAddress, Object paramObject)
  {
    this.connectionPool = paramConnectionPool;
    this.address = paramAddress;
    this.routeSelector = new RouteSelector(paramAddress, routeDatabase());
    this.callStackTrace = paramObject;
  }

  private Socket deallocate(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    assert (Thread.holdsLock(this.connectionPool));
    if (paramBoolean3)
      this.codec = null;
    if (paramBoolean2)
      this.released = true;
    RealConnection localRealConnection = this.connection;
    Socket localSocket = null;
    if (localRealConnection != null)
    {
      if (paramBoolean1)
        this.connection.noNewStreams = true;
      HttpCodec localHttpCodec = this.codec;
      localSocket = null;
      if (localHttpCodec == null)
        if (!this.released)
        {
          boolean bool3 = this.connection.noNewStreams;
          localSocket = null;
          if (!bool3);
        }
        else
        {
          release(this.connection);
          boolean bool1 = this.connection.allocations.isEmpty();
          localSocket = null;
          if (bool1)
          {
            this.connection.idleAtNanos = System.nanoTime();
            boolean bool2 = Internal.instance.connectionBecameIdle(this.connectionPool, this.connection);
            localSocket = null;
            if (bool2)
              localSocket = this.connection.socket();
          }
          this.connection = null;
        }
    }
    return localSocket;
  }

  private RealConnection findConnection(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    throws IOException
  {
    synchronized (this.connectionPool)
    {
      if (this.released)
        throw new IllegalStateException("released");
    }
    if (this.codec != null)
      throw new IllegalStateException("codec != null");
    if (this.canceled)
      throw new IOException("Canceled");
    RealConnection localRealConnection1 = this.connection;
    if ((localRealConnection1 != null) && (!localRealConnection1.noNewStreams))
    {
      monitorexit;
      return localRealConnection1;
    }
    Internal.instance.get(this.connectionPool, this.address, this);
    if (this.connection != null)
    {
      RealConnection localRealConnection3 = this.connection;
      monitorexit;
      return localRealConnection3;
    }
    Route localRoute = this.route;
    monitorexit;
    if (localRoute == null)
      localRoute = this.routeSelector.next();
    RealConnection localRealConnection2;
    synchronized (this.connectionPool)
    {
      this.route = localRoute;
      this.refusedStreamCount = 0;
      localRealConnection2 = new RealConnection(this.connectionPool, localRoute);
      acquire(localRealConnection2);
      if (this.canceled)
        throw new IOException("Canceled");
    }
    monitorexit;
    localRealConnection2.connect(paramInt1, paramInt2, paramInt3, paramBoolean);
    routeDatabase().connected(localRealConnection2.route());
    synchronized (this.connectionPool)
    {
      Internal.instance.put(this.connectionPool, localRealConnection2);
      boolean bool = localRealConnection2.isMultiplexed();
      Socket localSocket = null;
      if (bool)
      {
        localSocket = Internal.instance.deduplicate(this.connectionPool, this.address, this);
        localRealConnection2 = this.connection;
      }
      Util.closeQuietly(localSocket);
      return localRealConnection2;
    }
  }

  private RealConnection findHealthyConnection(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    RealConnection localRealConnection;
    while (true)
    {
      localRealConnection = findConnection(paramInt1, paramInt2, paramInt3, paramBoolean1);
      synchronized (this.connectionPool)
      {
        if (localRealConnection.successCount == 0)
          return localRealConnection;
        if (localRealConnection.isHealthy(paramBoolean2))
          break;
        noNewStreams();
      }
    }
    return localRealConnection;
  }

  private void release(RealConnection paramRealConnection)
  {
    int i = 0;
    int j = paramRealConnection.allocations.size();
    while (i < j)
    {
      if (((Reference)paramRealConnection.allocations.get(i)).get() == this)
      {
        paramRealConnection.allocations.remove(i);
        return;
      }
      i++;
    }
    throw new IllegalStateException();
  }

  private RouteDatabase routeDatabase()
  {
    return Internal.instance.routeDatabase(this.connectionPool);
  }

  public void acquire(RealConnection paramRealConnection)
  {
    assert (Thread.holdsLock(this.connectionPool));
    if (this.connection != null)
      throw new IllegalStateException();
    this.connection = paramRealConnection;
    paramRealConnection.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
  }

  public void cancel()
  {
    RealConnection localRealConnection;
    do
      synchronized (this.connectionPool)
      {
        this.canceled = true;
        HttpCodec localHttpCodec = this.codec;
        localRealConnection = this.connection;
        if (localHttpCodec == null)
          continue;
        localHttpCodec.cancel();
        return;
      }
    while (localRealConnection == null);
    localRealConnection.cancel();
  }

  public HttpCodec codec()
  {
    synchronized (this.connectionPool)
    {
      HttpCodec localHttpCodec = this.codec;
      return localHttpCodec;
    }
  }

  public RealConnection connection()
  {
    monitorenter;
    try
    {
      RealConnection localRealConnection = this.connection;
      monitorexit;
      return localRealConnection;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean hasMoreRoutes()
  {
    return (this.route != null) || (this.routeSelector.hasNext());
  }

  public HttpCodec newStream(OkHttpClient paramOkHttpClient, boolean paramBoolean)
  {
    int i = paramOkHttpClient.connectTimeoutMillis();
    int j = paramOkHttpClient.readTimeoutMillis();
    int k = paramOkHttpClient.writeTimeoutMillis();
    boolean bool = paramOkHttpClient.retryOnConnectionFailure();
    try
    {
      HttpCodec localHttpCodec = findHealthyConnection(i, j, k, bool, paramBoolean).newCodec(paramOkHttpClient, this);
      synchronized (this.connectionPool)
      {
        this.codec = localHttpCodec;
        return localHttpCodec;
      }
    }
    catch (IOException localIOException)
    {
    }
    throw new RouteException(localIOException);
  }

  public void noNewStreams()
  {
    synchronized (this.connectionPool)
    {
      Socket localSocket = deallocate(true, false, false);
      Util.closeQuietly(localSocket);
      return;
    }
  }

  public void release()
  {
    synchronized (this.connectionPool)
    {
      Socket localSocket = deallocate(false, true, false);
      Util.closeQuietly(localSocket);
      return;
    }
  }

  public Socket releaseAndAcquire(RealConnection paramRealConnection)
  {
    assert (Thread.holdsLock(this.connectionPool));
    if ((this.codec != null) || (this.connection.allocations.size() != 1))
      throw new IllegalStateException();
    Reference localReference = (Reference)this.connection.allocations.get(0);
    Socket localSocket = deallocate(true, false, false);
    this.connection = paramRealConnection;
    paramRealConnection.allocations.add(localReference);
    return localSocket;
  }

  public void streamFailed(IOException paramIOException)
  {
    synchronized (this.connectionPool)
    {
      boolean bool1;
      if ((paramIOException instanceof StreamResetException))
      {
        StreamResetException localStreamResetException = (StreamResetException)paramIOException;
        if (localStreamResetException.errorCode == ErrorCode.REFUSED_STREAM)
          this.refusedStreamCount = (1 + this.refusedStreamCount);
        if (localStreamResetException.errorCode == ErrorCode.REFUSED_STREAM)
        {
          int i = this.refusedStreamCount;
          bool1 = false;
          if (i <= 1);
        }
        else
        {
          bool1 = true;
          this.route = null;
        }
      }
      do
      {
        boolean bool2;
        do
        {
          RealConnection localRealConnection;
          do
          {
            Socket localSocket = deallocate(bool1, false, true);
            Util.closeQuietly(localSocket);
            return;
            localRealConnection = this.connection;
            bool1 = false;
          }
          while (localRealConnection == null);
          if (!this.connection.isMultiplexed())
            break;
          bool2 = paramIOException instanceof ConnectionShutdownException;
          bool1 = false;
        }
        while (!bool2);
        bool1 = true;
      }
      while (this.connection.successCount != 0);
      if ((this.route != null) && (paramIOException != null))
        this.routeSelector.connectFailed(this.route, paramIOException);
      this.route = null;
    }
  }

  // ERROR //
  public void streamFinished(boolean paramBoolean, HttpCodec paramHttpCodec)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 40	okhttp3/internal/connection/StreamAllocation:connectionPool	Lokhttp3/ConnectionPool;
    //   4: astore_3
    //   5: aload_3
    //   6: monitorenter
    //   7: aload_2
    //   8: ifnull +11 -> 19
    //   11: aload_2
    //   12: aload_0
    //   13: getfield 68	okhttp3/internal/connection/StreamAllocation:codec	Lokhttp3/internal/http/HttpCodec;
    //   16: if_acmpeq +51 -> 67
    //   19: new 120	java/lang/IllegalStateException
    //   22: dup
    //   23: new 283	java/lang/StringBuilder
    //   26: dup
    //   27: invokespecial 284	java/lang/StringBuilder:<init>	()V
    //   30: ldc_w 286
    //   33: invokevirtual 290	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   36: aload_0
    //   37: getfield 68	okhttp3/internal/connection/StreamAllocation:codec	Lokhttp3/internal/http/HttpCodec;
    //   40: invokevirtual 293	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   43: ldc_w 295
    //   46: invokevirtual 290	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: aload_2
    //   50: invokevirtual 293	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   53: invokevirtual 299	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   56: invokespecial 124	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   59: athrow
    //   60: astore 4
    //   62: aload_3
    //   63: monitorexit
    //   64: aload 4
    //   66: athrow
    //   67: iload_1
    //   68: ifne +21 -> 89
    //   71: aload_0
    //   72: getfield 72	okhttp3/internal/connection/StreamAllocation:connection	Lokhttp3/internal/connection/RealConnection;
    //   75: astore 6
    //   77: aload 6
    //   79: iconst_1
    //   80: aload 6
    //   82: getfield 185	okhttp3/internal/connection/RealConnection:successCount	I
    //   85: iadd
    //   86: putfield 185	okhttp3/internal/connection/RealConnection:successCount	I
    //   89: aload_0
    //   90: iload_1
    //   91: iconst_0
    //   92: iconst_1
    //   93: invokespecial 259	okhttp3/internal/connection/StreamAllocation:deallocate	(ZZZ)Ljava/net/Socket;
    //   96: astore 5
    //   98: aload_3
    //   99: monitorexit
    //   100: aload 5
    //   102: invokestatic 178	okhttp3/internal/Util:closeQuietly	(Ljava/net/Socket;)V
    //   105: return
    //
    // Exception table:
    //   from	to	target	type
    //   11	19	60	finally
    //   19	60	60	finally
    //   62	64	60	finally
    //   71	89	60	finally
    //   89	100	60	finally
  }

  public String toString()
  {
    RealConnection localRealConnection = connection();
    if (localRealConnection != null)
      return localRealConnection.toString();
    return this.address.toString();
  }

  public static final class StreamAllocationReference extends WeakReference<StreamAllocation>
  {
    public final Object callStackTrace;

    StreamAllocationReference(StreamAllocation paramStreamAllocation, Object paramObject)
    {
      super();
      this.callStackTrace = paramObject;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.connection.StreamAllocation
 * JD-Core Version:    0.6.0
 */