package okhttp3.internal.ws;

import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Timeout;

final class WebSocketReader
{
  boolean closed;
  long frameBytesRead;
  final FrameCallback frameCallback;
  long frameLength;
  final boolean isClient;
  boolean isControlFrame;
  boolean isFinalFrame;
  boolean isMasked;
  final byte[] maskBuffer = new byte[8192];
  final byte[] maskKey = new byte[4];
  int opcode;
  final BufferedSource source;

  WebSocketReader(boolean paramBoolean, BufferedSource paramBufferedSource, FrameCallback paramFrameCallback)
  {
    if (paramBufferedSource == null)
      throw new NullPointerException("source == null");
    if (paramFrameCallback == null)
      throw new NullPointerException("frameCallback == null");
    this.isClient = paramBoolean;
    this.source = paramBufferedSource;
    this.frameCallback = paramFrameCallback;
  }

  private void readControlFrame()
    throws IOException
  {
    Buffer localBuffer = new Buffer();
    if (this.frameBytesRead < this.frameLength)
    {
      if (!this.isClient)
        break label147;
      this.source.readFully(localBuffer, this.frameLength);
    }
    switch (this.opcode)
    {
    default:
      throw new ProtocolException("Unknown control opcode: " + Integer.toHexString(this.opcode));
      int m;
      do
      {
        int k;
        WebSocketProtocol.toggleMask(this.maskBuffer, k, this.maskKey, this.frameBytesRead);
        localBuffer.write(this.maskBuffer, 0, k);
        this.frameBytesRead += k;
        if (this.frameBytesRead >= this.frameLength)
          break;
        int j = (int)Math.min(this.frameLength - this.frameBytesRead, this.maskBuffer.length);
        m = this.source.read(this.maskBuffer, 0, j);
      }
      while (m != -1);
      throw new EOFException();
    case 9:
      this.frameCallback.onReadPing(localBuffer.readByteString());
      return;
    case 10:
      label147: this.frameCallback.onReadPong(localBuffer.readByteString());
      return;
    case 8:
    }
    int i = 1005;
    String str1 = "";
    long l = localBuffer.size();
    if (l == 1L)
      throw new ProtocolException("Malformed close payload length of 1.");
    if (l != 0L)
    {
      i = localBuffer.readShort();
      str1 = localBuffer.readUtf8();
      String str2 = WebSocketProtocol.closeCodeExceptionMessage(i);
      if (str2 != null)
        throw new ProtocolException(str2);
    }
    this.frameCallback.onReadClose(i, str1);
    this.closed = true;
  }

  private void readHeader()
    throws IOException
  {
    boolean bool1 = true;
    if (this.closed)
      throw new IOException("closed");
    long l = this.source.timeout().timeoutNanos();
    this.source.timeout().clearTimeout();
    int j;
    while (true)
    {
      try
      {
        int i = this.source.readByte();
        j = i & 0xFF;
        this.source.timeout().timeout(l, TimeUnit.NANOSECONDS);
        this.opcode = (j & 0xF);
        if ((j & 0x80) != 0)
        {
          bool2 = bool1;
          this.isFinalFrame = bool2;
          if ((j & 0x8) == 0)
            break label177;
          bool3 = bool1;
          this.isControlFrame = bool3;
          if ((!this.isControlFrame) || (this.isFinalFrame))
            break;
          throw new ProtocolException("Control frames must be final.");
        }
      }
      finally
      {
        this.source.timeout().timeout(l, TimeUnit.NANOSECONDS);
      }
      boolean bool2 = false;
      continue;
      label177: boolean bool3 = false;
    }
    int k;
    int m;
    if ((j & 0x40) != 0)
    {
      k = bool1;
      if ((j & 0x20) == 0)
        break label247;
      m = bool1;
      label205: if ((j & 0x10) == 0)
        break label253;
    }
    label247: label253: for (int n = bool1; ; n = 0)
    {
      if ((k == 0) && (m == 0) && (n == 0))
        break label259;
      throw new ProtocolException("Reserved flags are unsupported.");
      k = 0;
      break;
      m = 0;
      break label205;
    }
    label259: int i1 = 0xFF & this.source.readByte();
    if ((i1 & 0x80) != 0)
    {
      this.isMasked = bool1;
      if (this.isMasked != this.isClient)
        break label332;
      if (!this.isClient)
        break label325;
    }
    label325: for (String str = "Server-sent frames must not be masked."; ; str = "Client-sent frames must be masked.")
    {
      throw new ProtocolException(str);
      bool1 = false;
      break;
    }
    label332: this.frameLength = (i1 & 0x7F);
    if (this.frameLength == 126L)
      this.frameLength = (0xFFFF & this.source.readShort());
    while (true)
    {
      this.frameBytesRead = 0L;
      if ((!this.isControlFrame) || (this.frameLength <= 125L))
        break;
      throw new ProtocolException("Control frame must be less than 125B.");
      if (this.frameLength != 127L)
        continue;
      this.frameLength = this.source.readLong();
      if (this.frameLength >= 0L)
        continue;
      throw new ProtocolException("Frame length 0x" + Long.toHexString(this.frameLength) + " > 0x7FFFFFFFFFFFFFFF");
    }
    if (this.isMasked)
      this.source.readFully(this.maskKey);
  }

  private void readMessage(Buffer paramBuffer)
    throws IOException
  {
    if (this.closed)
      throw new IOException("closed");
    if (this.frameBytesRead == this.frameLength)
    {
      if (this.isFinalFrame);
      do
      {
        return;
        readUntilNonControlFrame();
        if (this.opcode == 0)
          continue;
        throw new ProtocolException("Expected continuation opcode. Got: " + Integer.toHexString(this.opcode));
      }
      while ((this.isFinalFrame) && (this.frameLength == 0L));
    }
    long l1 = this.frameLength - this.frameBytesRead;
    long l2;
    if (this.isMasked)
    {
      long l3 = Math.min(l1, this.maskBuffer.length);
      l2 = this.source.read(this.maskBuffer, 0, (int)l3);
      if (l2 == -1L)
        throw new EOFException();
      WebSocketProtocol.toggleMask(this.maskBuffer, l2, this.maskKey, this.frameBytesRead);
      paramBuffer.write(this.maskBuffer, 0, (int)l2);
    }
    do
    {
      this.frameBytesRead = (l2 + this.frameBytesRead);
      break;
      l2 = this.source.read(paramBuffer, l1);
    }
    while (l2 != -1L);
    throw new EOFException();
  }

  private void readMessageFrame()
    throws IOException
  {
    int i = this.opcode;
    if ((i != 1) && (i != 2))
      throw new ProtocolException("Unknown opcode: " + Integer.toHexString(i));
    Buffer localBuffer = new Buffer();
    readMessage(localBuffer);
    if (i == 1)
    {
      this.frameCallback.onReadMessage(localBuffer.readUtf8());
      return;
    }
    this.frameCallback.onReadMessage(localBuffer.readByteString());
  }

  void processNextFrame()
    throws IOException
  {
    readHeader();
    if (this.isControlFrame)
    {
      readControlFrame();
      return;
    }
    readMessageFrame();
  }

  void readUntilNonControlFrame()
    throws IOException
  {
    while (true)
    {
      if (!this.closed)
      {
        readHeader();
        if (this.isControlFrame);
      }
      else
      {
        return;
      }
      readControlFrame();
    }
  }

  public static abstract interface FrameCallback
  {
    public abstract void onReadClose(int paramInt, String paramString);

    public abstract void onReadMessage(String paramString)
      throws IOException;

    public abstract void onReadMessage(ByteString paramByteString)
      throws IOException;

    public abstract void onReadPing(ByteString paramByteString);

    public abstract void onReadPong(ByteString paramByteString);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.ws.WebSocketReader
 * JD-Core Version:    0.6.0
 */