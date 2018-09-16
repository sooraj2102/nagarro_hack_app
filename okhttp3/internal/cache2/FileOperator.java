package okhttp3.internal.cache2;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import okio.Buffer;

final class FileOperator
{
  private static final int BUFFER_SIZE = 8192;
  private final byte[] byteArray = new byte[8192];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(this.byteArray);
  private final FileChannel fileChannel;

  public FileOperator(FileChannel paramFileChannel)
  {
    this.fileChannel = paramFileChannel;
  }

  public void read(long paramLong1, Buffer paramBuffer, long paramLong2)
    throws IOException
  {
    if (paramLong2 < 0L)
      throw new IndexOutOfBoundsException();
    try
    {
      while (true)
      {
        int i = this.byteBuffer.position();
        paramBuffer.write(this.byteArray, 0, i);
        paramLong1 += i;
        paramLong2 -= i;
        this.byteBuffer.clear();
        if (paramLong2 <= 0L)
          break;
        this.byteBuffer.limit((int)Math.min(8192L, paramLong2));
        if (this.fileChannel.read(this.byteBuffer, paramLong1) != -1)
          continue;
        throw new EOFException();
      }
    }
    finally
    {
      this.byteBuffer.clear();
    }
  }

  public void write(long paramLong1, Buffer paramBuffer, long paramLong2)
    throws IOException
  {
    if ((paramLong2 < 0L) || (paramLong2 > paramBuffer.size()))
      throw new IndexOutOfBoundsException();
    while (paramLong2 > 0L)
      try
      {
        int i = (int)Math.min(8192L, paramLong2);
        paramBuffer.read(this.byteArray, 0, i);
        this.byteBuffer.limit(i);
        boolean bool;
        do
        {
          paramLong1 += this.fileChannel.write(this.byteBuffer, paramLong1);
          bool = this.byteBuffer.hasRemaining();
        }
        while (bool);
        paramLong2 -= i;
        this.byteBuffer.clear();
        continue;
      }
      finally
      {
        this.byteBuffer.clear();
      }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.cache2.FileOperator
 * JD-Core Version:    0.6.0
 */