package okhttp3.internal.ws;

import java.io.IOException;
import java.util.Random;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.Sink;
import okio.Timeout;

final class WebSocketWriter
{
  boolean activeWriter;
  final Buffer buffer = new Buffer();
  final FrameSink frameSink = new FrameSink();
  final boolean isClient;
  final byte[] maskBuffer;
  final byte[] maskKey;
  final Random random;
  final BufferedSink sink;
  boolean writerClosed;

  static
  {
    if (!WebSocketWriter.class.desiredAssertionStatus());
    for (boolean bool = true; ; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }

  WebSocketWriter(boolean paramBoolean, BufferedSink paramBufferedSink, Random paramRandom)
  {
    if (paramBufferedSink == null)
      throw new NullPointerException("sink == null");
    if (paramRandom == null)
      throw new NullPointerException("random == null");
    this.isClient = paramBoolean;
    this.sink = paramBufferedSink;
    this.random = paramRandom;
    if (paramBoolean);
    for (byte[] arrayOfByte1 = new byte[4]; ; arrayOfByte1 = null)
    {
      this.maskKey = arrayOfByte1;
      byte[] arrayOfByte2 = null;
      if (paramBoolean)
        arrayOfByte2 = new byte[8192];
      this.maskBuffer = arrayOfByte2;
      return;
    }
  }

  private void writeControlFrameSynchronized(int paramInt, ByteString paramByteString)
    throws IOException
  {
    assert (Thread.holdsLock(this));
    if (this.writerClosed)
      throw new IOException("closed");
    int i = paramByteString.size();
    if (i > 125L)
      throw new IllegalArgumentException("Payload size must be less than or equal to 125");
    int j = paramInt | 0x80;
    this.sink.writeByte(j);
    if (this.isClient)
    {
      int k = i | 0x80;
      this.sink.writeByte(k);
      this.random.nextBytes(this.maskKey);
      this.sink.write(this.maskKey);
      byte[] arrayOfByte = paramByteString.toByteArray();
      WebSocketProtocol.toggleMask(arrayOfByte, arrayOfByte.length, this.maskKey, 0L);
      this.sink.write(arrayOfByte);
    }
    while (true)
    {
      this.sink.flush();
      return;
      this.sink.writeByte(i);
      this.sink.write(paramByteString);
    }
  }

  Sink newMessageSink(int paramInt, long paramLong)
  {
    if (this.activeWriter)
      throw new IllegalStateException("Another message writer is active. Did you call close()?");
    this.activeWriter = true;
    this.frameSink.formatOpcode = paramInt;
    this.frameSink.contentLength = paramLong;
    this.frameSink.isFirstFrame = true;
    this.frameSink.closed = false;
    return this.frameSink;
  }

  // ERROR //
  void writeClose(int paramInt, ByteString paramByteString)
    throws IOException
  {
    // Byte code:
    //   0: getstatic 155	okio/ByteString:EMPTY	Lokio/ByteString;
    //   3: astore_3
    //   4: iload_1
    //   5: ifne +7 -> 12
    //   8: aload_2
    //   9: ifnull +44 -> 53
    //   12: iload_1
    //   13: ifeq +7 -> 20
    //   16: iload_1
    //   17: invokestatic 159	okhttp3/internal/ws/WebSocketProtocol:validateCloseCode	(I)V
    //   20: new 36	okio/Buffer
    //   23: dup
    //   24: invokespecial 37	okio/Buffer:<init>	()V
    //   27: astore 4
    //   29: aload 4
    //   31: iload_1
    //   32: invokevirtual 163	okio/Buffer:writeShort	(I)Lokio/Buffer;
    //   35: pop
    //   36: aload_2
    //   37: ifnull +10 -> 47
    //   40: aload 4
    //   42: aload_2
    //   43: invokevirtual 166	okio/Buffer:write	(Lokio/ByteString;)Lokio/Buffer;
    //   46: pop
    //   47: aload 4
    //   49: invokevirtual 170	okio/Buffer:readByteString	()Lokio/ByteString;
    //   52: astore_3
    //   53: aload_0
    //   54: monitorenter
    //   55: aload_0
    //   56: bipush 8
    //   58: aload_3
    //   59: invokespecial 172	okhttp3/internal/ws/WebSocketWriter:writeControlFrameSynchronized	(ILokio/ByteString;)V
    //   62: aload_0
    //   63: iconst_1
    //   64: putfield 80	okhttp3/internal/ws/WebSocketWriter:writerClosed	Z
    //   67: aload_0
    //   68: monitorexit
    //   69: return
    //   70: astore 6
    //   72: aload_0
    //   73: iconst_1
    //   74: putfield 80	okhttp3/internal/ws/WebSocketWriter:writerClosed	Z
    //   77: aload 6
    //   79: athrow
    //   80: astore 7
    //   82: aload_0
    //   83: monitorexit
    //   84: aload 7
    //   86: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   55	62	70	finally
    //   62	69	80	finally
    //   72	80	80	finally
    //   82	84	80	finally
  }

  void writeMessageFrameSynchronized(int paramInt, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    assert (Thread.holdsLock(this));
    if (this.writerClosed)
      throw new IOException("closed");
    int i;
    int j;
    label119: long l;
    if (paramBoolean1)
    {
      i = paramInt;
      if (paramBoolean2)
        i |= 128;
      this.sink.writeByte(i);
      boolean bool = this.isClient;
      j = 0;
      if (bool)
        j = 0x0 | 0x80;
      if (paramLong > 125L)
        break label210;
      int i2 = j | (int)paramLong;
      this.sink.writeByte(i2);
      if (!this.isClient)
        break label329;
      this.random.nextBytes(this.maskKey);
      this.sink.write(this.maskKey);
      l = 0L;
    }
    while (true)
    {
      if (l >= paramLong)
        break label343;
      int m = (int)Math.min(paramLong, this.maskBuffer.length);
      int n = this.buffer.read(this.maskBuffer, 0, m);
      if (n == -1)
      {
        throw new AssertionError();
        i = 0;
        break;
        label210: if (paramLong <= 65535L)
        {
          int i1 = j | 0x7E;
          this.sink.writeByte(i1);
          this.sink.writeShort((int)paramLong);
          break label119;
        }
        int k = j | 0x7F;
        this.sink.writeByte(k);
        this.sink.writeLong(paramLong);
        break label119;
      }
      WebSocketProtocol.toggleMask(this.maskBuffer, n, this.maskKey, l);
      this.sink.write(this.maskBuffer, 0, n);
      l += n;
    }
    label329: this.sink.write(this.buffer, paramLong);
    label343: this.sink.emit();
  }

  void writePing(ByteString paramByteString)
    throws IOException
  {
    monitorenter;
    try
    {
      writeControlFrameSynchronized(9, paramByteString);
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  void writePong(ByteString paramByteString)
    throws IOException
  {
    monitorenter;
    try
    {
      writeControlFrameSynchronized(10, paramByteString);
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  final class FrameSink
    implements Sink
  {
    boolean closed;
    long contentLength;
    int formatOpcode;
    boolean isFirstFrame;

    FrameSink()
    {
    }

    public void close()
      throws IOException
    {
      if (this.closed)
        throw new IOException("closed");
      synchronized (WebSocketWriter.this)
      {
        WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, true);
        this.closed = true;
        WebSocketWriter.this.activeWriter = false;
        return;
      }
    }

    public void flush()
      throws IOException
    {
      if (this.closed)
        throw new IOException("closed");
      synchronized (WebSocketWriter.this)
      {
        WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, false);
        this.isFirstFrame = false;
        return;
      }
    }

    public Timeout timeout()
    {
      return WebSocketWriter.this.sink.timeout();
    }

    public void write(Buffer paramBuffer, long paramLong)
      throws IOException
    {
      if (this.closed)
        throw new IOException("closed");
      WebSocketWriter.this.buffer.write(paramBuffer, paramLong);
      int i;
      if ((this.isFirstFrame) && (this.contentLength != -1L) && (WebSocketWriter.this.buffer.size() > this.contentLength - 8192L))
        i = 1;
      while (true)
      {
        long l = WebSocketWriter.this.buffer.completeSegmentByteCount();
        if ((l > 0L) && (i == 0));
        synchronized (WebSocketWriter.this)
        {
          WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, l, this.isFirstFrame, false);
          this.isFirstFrame = false;
          return;
          i = 0;
        }
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.ws.WebSocketWriter
 * JD-Core Version:    0.6.0
 */