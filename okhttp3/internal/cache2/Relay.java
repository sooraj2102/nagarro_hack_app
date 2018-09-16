package okhttp3.internal.cache2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

final class Relay
{
  private static final long FILE_HEADER_SIZE = 32L;
  static final ByteString PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
  static final ByteString PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
  private static final int SOURCE_FILE = 2;
  private static final int SOURCE_UPSTREAM = 1;
  final Buffer buffer = new Buffer();
  final long bufferMaxSize;
  boolean complete;
  RandomAccessFile file;
  private final ByteString metadata;
  int sourceCount;
  Source upstream;
  final Buffer upstreamBuffer = new Buffer();
  long upstreamPos;
  Thread upstreamReader;

  private Relay(RandomAccessFile paramRandomAccessFile, Source paramSource, long paramLong1, ByteString paramByteString, long paramLong2)
  {
    this.file = paramRandomAccessFile;
    this.upstream = paramSource;
    if (paramSource == null);
    for (boolean bool = true; ; bool = false)
    {
      this.complete = bool;
      this.upstreamPos = paramLong1;
      this.metadata = paramByteString;
      this.bufferMaxSize = paramLong2;
      return;
    }
  }

  public static Relay edit(File paramFile, Source paramSource, ByteString paramByteString, long paramLong)
    throws IOException
  {
    RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "rw");
    Relay localRelay = new Relay(localRandomAccessFile, paramSource, 0L, paramByteString, paramLong);
    localRandomAccessFile.setLength(0L);
    localRelay.writeHeader(PREFIX_DIRTY, -1L, -1L);
    return localRelay;
  }

  public static Relay read(File paramFile)
    throws IOException
  {
    RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "rw");
    FileOperator localFileOperator = new FileOperator(localRandomAccessFile.getChannel());
    Buffer localBuffer1 = new Buffer();
    localFileOperator.read(0L, localBuffer1, 32L);
    if (!localBuffer1.readByteString(PREFIX_CLEAN.size()).equals(PREFIX_CLEAN))
      throw new IOException("unreadable cache file");
    long l1 = localBuffer1.readLong();
    long l2 = localBuffer1.readLong();
    Buffer localBuffer2 = new Buffer();
    localFileOperator.read(32L + l1, localBuffer2, l2);
    return new Relay(localRandomAccessFile, null, l1, localBuffer2.readByteString(), 0L);
  }

  private void writeHeader(ByteString paramByteString, long paramLong1, long paramLong2)
    throws IOException
  {
    Buffer localBuffer = new Buffer();
    localBuffer.write(paramByteString);
    localBuffer.writeLong(paramLong1);
    localBuffer.writeLong(paramLong2);
    if (localBuffer.size() != 32L)
      throw new IllegalArgumentException();
    new FileOperator(this.file.getChannel()).write(0L, localBuffer, 32L);
  }

  private void writeMetadata(long paramLong)
    throws IOException
  {
    Buffer localBuffer = new Buffer();
    localBuffer.write(this.metadata);
    new FileOperator(this.file.getChannel()).write(32L + paramLong, localBuffer, this.metadata.size());
  }

  void commit(long paramLong)
    throws IOException
  {
    writeMetadata(paramLong);
    this.file.getChannel().force(false);
    writeHeader(PREFIX_CLEAN, paramLong, this.metadata.size());
    this.file.getChannel().force(false);
    monitorenter;
    try
    {
      this.complete = true;
      monitorexit;
      Util.closeQuietly(this.upstream);
      this.upstream = null;
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  boolean isClosed()
  {
    return this.file == null;
  }

  public ByteString metadata()
  {
    return this.metadata;
  }

  public Source newSource()
  {
    monitorenter;
    try
    {
      if (this.file == null)
        return null;
      this.sourceCount = (1 + this.sourceCount);
      return new RelaySource();
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  class RelaySource
    implements Source
  {
    private FileOperator fileOperator = new FileOperator(Relay.this.file.getChannel());
    private long sourcePos;
    private final Timeout timeout = new Timeout();

    RelaySource()
    {
    }

    public void close()
      throws IOException
    {
      if (this.fileOperator == null);
      while (true)
      {
        return;
        this.fileOperator = null;
        synchronized (Relay.this)
        {
          Relay localRelay2 = Relay.this;
          localRelay2.sourceCount = (-1 + localRelay2.sourceCount);
          int i = Relay.this.sourceCount;
          RandomAccessFile localRandomAccessFile = null;
          if (i == 0)
          {
            localRandomAccessFile = Relay.this.file;
            Relay.this.file = null;
          }
          if (localRandomAccessFile == null)
            continue;
          Util.closeQuietly(localRandomAccessFile);
          return;
        }
      }
    }

    // ERROR //
    public long read(Buffer paramBuffer, long paramLong)
      throws IOException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 45	okhttp3/internal/cache2/Relay$RelaySource:fileOperator	Lokhttp3/internal/cache2/FileOperator;
      //   4: ifnonnull +13 -> 17
      //   7: new 62	java/lang/IllegalStateException
      //   10: dup
      //   11: ldc 64
      //   13: invokespecial 67	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
      //   16: athrow
      //   17: aload_0
      //   18: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   21: astore 4
      //   23: aload 4
      //   25: monitorenter
      //   26: aload_0
      //   27: getfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   30: lstore 6
      //   32: aload_0
      //   33: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   36: getfield 72	okhttp3/internal/cache2/Relay:upstreamPos	J
      //   39: lstore 8
      //   41: lload 6
      //   43: lload 8
      //   45: lcmp
      //   46: ifne +119 -> 165
      //   49: aload_0
      //   50: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   53: getfield 76	okhttp3/internal/cache2/Relay:complete	Z
      //   56: ifeq +10 -> 66
      //   59: aload 4
      //   61: monitorexit
      //   62: ldc2_w 77
      //   65: lreturn
      //   66: aload_0
      //   67: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   70: getfield 82	okhttp3/internal/cache2/Relay:upstreamReader	Ljava/lang/Thread;
      //   73: ifnull +25 -> 98
      //   76: aload_0
      //   77: getfield 26	okhttp3/internal/cache2/Relay$RelaySource:timeout	Lokio/Timeout;
      //   80: aload_0
      //   81: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   84: invokevirtual 86	okio/Timeout:waitUntilNotified	(Ljava/lang/Object;)V
      //   87: goto -61 -> 26
      //   90: astore 5
      //   92: aload 4
      //   94: monitorexit
      //   95: aload 5
      //   97: athrow
      //   98: aload_0
      //   99: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   102: invokestatic 92	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   105: putfield 82	okhttp3/internal/cache2/Relay:upstreamReader	Ljava/lang/Thread;
      //   108: iconst_1
      //   109: istore 10
      //   111: aload 4
      //   113: monitorexit
      //   114: iload 10
      //   116: iconst_2
      //   117: if_icmpne +133 -> 250
      //   120: lload_2
      //   121: lload 8
      //   123: aload_0
      //   124: getfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   127: lsub
      //   128: invokestatic 98	java/lang/Math:min	(JJ)J
      //   131: lstore 26
      //   133: aload_0
      //   134: getfield 45	okhttp3/internal/cache2/Relay$RelaySource:fileOperator	Lokhttp3/internal/cache2/FileOperator;
      //   137: ldc2_w 99
      //   140: aload_0
      //   141: getfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   144: ladd
      //   145: aload_1
      //   146: lload 26
      //   148: invokevirtual 103	okhttp3/internal/cache2/FileOperator:read	(JLokio/Buffer;J)V
      //   151: aload_0
      //   152: lload 26
      //   154: aload_0
      //   155: getfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   158: ladd
      //   159: putfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   162: lload 26
      //   164: lreturn
      //   165: lload 8
      //   167: aload_0
      //   168: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   171: getfield 107	okhttp3/internal/cache2/Relay:buffer	Lokio/Buffer;
      //   174: invokevirtual 113	okio/Buffer:size	()J
      //   177: lsub
      //   178: lstore 28
      //   180: aload_0
      //   181: getfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   184: lload 28
      //   186: lcmp
      //   187: ifge +12 -> 199
      //   190: iconst_2
      //   191: istore 10
      //   193: aload 4
      //   195: monitorexit
      //   196: goto -82 -> 114
      //   199: lload_2
      //   200: lload 8
      //   202: aload_0
      //   203: getfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   206: lsub
      //   207: invokestatic 98	java/lang/Math:min	(JJ)J
      //   210: lstore 30
      //   212: aload_0
      //   213: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   216: getfield 107	okhttp3/internal/cache2/Relay:buffer	Lokio/Buffer;
      //   219: aload_1
      //   220: aload_0
      //   221: getfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   224: lload 28
      //   226: lsub
      //   227: lload 30
      //   229: invokevirtual 117	okio/Buffer:copyTo	(Lokio/Buffer;JJ)Lokio/Buffer;
      //   232: pop
      //   233: aload_0
      //   234: lload 30
      //   236: aload_0
      //   237: getfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   240: ladd
      //   241: putfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   244: aload 4
      //   246: monitorexit
      //   247: lload 30
      //   249: lreturn
      //   250: aload_0
      //   251: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   254: getfield 121	okhttp3/internal/cache2/Relay:upstream	Lokio/Source;
      //   257: aload_0
      //   258: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   261: getfield 124	okhttp3/internal/cache2/Relay:upstreamBuffer	Lokio/Buffer;
      //   264: aload_0
      //   265: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   268: getfield 127	okhttp3/internal/cache2/Relay:bufferMaxSize	J
      //   271: invokeinterface 129 4 0
      //   276: lstore 14
      //   278: lload 14
      //   280: ldc2_w 77
      //   283: lcmp
      //   284: ifne +51 -> 335
      //   287: aload_0
      //   288: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   291: lload 8
      //   293: invokevirtual 133	okhttp3/internal/cache2/Relay:commit	(J)V
      //   296: aload_0
      //   297: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   300: astore 16
      //   302: aload 16
      //   304: monitorenter
      //   305: aload_0
      //   306: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   309: aconst_null
      //   310: putfield 82	okhttp3/internal/cache2/Relay:upstreamReader	Ljava/lang/Thread;
      //   313: aload_0
      //   314: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   317: invokevirtual 136	java/lang/Object:notifyAll	()V
      //   320: aload 16
      //   322: monitorexit
      //   323: ldc2_w 77
      //   326: lreturn
      //   327: astore 17
      //   329: aload 16
      //   331: monitorexit
      //   332: aload 17
      //   334: athrow
      //   335: lload 14
      //   337: lload_2
      //   338: invokestatic 98	java/lang/Math:min	(JJ)J
      //   341: lstore 18
      //   343: aload_0
      //   344: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   347: getfield 124	okhttp3/internal/cache2/Relay:upstreamBuffer	Lokio/Buffer;
      //   350: aload_1
      //   351: lconst_0
      //   352: lload 18
      //   354: invokevirtual 117	okio/Buffer:copyTo	(Lokio/Buffer;JJ)Lokio/Buffer;
      //   357: pop
      //   358: aload_0
      //   359: lload 18
      //   361: aload_0
      //   362: getfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   365: ladd
      //   366: putfield 69	okhttp3/internal/cache2/Relay$RelaySource:sourcePos	J
      //   369: aload_0
      //   370: getfield 45	okhttp3/internal/cache2/Relay$RelaySource:fileOperator	Lokhttp3/internal/cache2/FileOperator;
      //   373: ldc2_w 99
      //   376: lload 8
      //   378: ladd
      //   379: aload_0
      //   380: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   383: getfield 124	okhttp3/internal/cache2/Relay:upstreamBuffer	Lokio/Buffer;
      //   386: invokevirtual 140	okio/Buffer:clone	()Lokio/Buffer;
      //   389: lload 14
      //   391: invokevirtual 143	okhttp3/internal/cache2/FileOperator:write	(JLokio/Buffer;J)V
      //   394: aload_0
      //   395: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   398: astore 21
      //   400: aload 21
      //   402: monitorenter
      //   403: aload_0
      //   404: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   407: getfield 107	okhttp3/internal/cache2/Relay:buffer	Lokio/Buffer;
      //   410: aload_0
      //   411: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   414: getfield 124	okhttp3/internal/cache2/Relay:upstreamBuffer	Lokio/Buffer;
      //   417: lload 14
      //   419: invokevirtual 146	okio/Buffer:write	(Lokio/Buffer;J)V
      //   422: aload_0
      //   423: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   426: getfield 107	okhttp3/internal/cache2/Relay:buffer	Lokio/Buffer;
      //   429: invokevirtual 113	okio/Buffer:size	()J
      //   432: aload_0
      //   433: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   436: getfield 127	okhttp3/internal/cache2/Relay:bufferMaxSize	J
      //   439: lcmp
      //   440: ifle +31 -> 471
      //   443: aload_0
      //   444: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   447: getfield 107	okhttp3/internal/cache2/Relay:buffer	Lokio/Buffer;
      //   450: aload_0
      //   451: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   454: getfield 107	okhttp3/internal/cache2/Relay:buffer	Lokio/Buffer;
      //   457: invokevirtual 113	okio/Buffer:size	()J
      //   460: aload_0
      //   461: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   464: getfield 127	okhttp3/internal/cache2/Relay:bufferMaxSize	J
      //   467: lsub
      //   468: invokevirtual 149	okio/Buffer:skip	(J)V
      //   471: aload_0
      //   472: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   475: astore 23
      //   477: aload 23
      //   479: lload 14
      //   481: aload 23
      //   483: getfield 72	okhttp3/internal/cache2/Relay:upstreamPos	J
      //   486: ladd
      //   487: putfield 72	okhttp3/internal/cache2/Relay:upstreamPos	J
      //   490: aload 21
      //   492: monitorexit
      //   493: aload_0
      //   494: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   497: astore 24
      //   499: aload 24
      //   501: monitorenter
      //   502: aload_0
      //   503: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   506: aconst_null
      //   507: putfield 82	okhttp3/internal/cache2/Relay:upstreamReader	Ljava/lang/Thread;
      //   510: aload_0
      //   511: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   514: invokevirtual 136	java/lang/Object:notifyAll	()V
      //   517: aload 24
      //   519: monitorexit
      //   520: lload 18
      //   522: lreturn
      //   523: astore 22
      //   525: aload 21
      //   527: monitorexit
      //   528: aload 22
      //   530: athrow
      //   531: astore 11
      //   533: aload_0
      //   534: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   537: astore 12
      //   539: aload 12
      //   541: monitorenter
      //   542: aload_0
      //   543: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   546: aconst_null
      //   547: putfield 82	okhttp3/internal/cache2/Relay:upstreamReader	Ljava/lang/Thread;
      //   550: aload_0
      //   551: getfield 18	okhttp3/internal/cache2/Relay$RelaySource:this$0	Lokhttp3/internal/cache2/Relay;
      //   554: invokevirtual 136	java/lang/Object:notifyAll	()V
      //   557: aload 12
      //   559: monitorexit
      //   560: aload 11
      //   562: athrow
      //   563: astore 25
      //   565: aload 24
      //   567: monitorexit
      //   568: aload 25
      //   570: athrow
      //   571: astore 13
      //   573: aload 12
      //   575: monitorexit
      //   576: aload 13
      //   578: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   26	41	90	finally
      //   49	62	90	finally
      //   66	87	90	finally
      //   92	95	90	finally
      //   98	108	90	finally
      //   111	114	90	finally
      //   165	190	90	finally
      //   193	196	90	finally
      //   199	247	90	finally
      //   305	323	327	finally
      //   329	332	327	finally
      //   403	471	523	finally
      //   471	493	523	finally
      //   525	528	523	finally
      //   250	278	531	finally
      //   287	296	531	finally
      //   335	403	531	finally
      //   528	531	531	finally
      //   502	520	563	finally
      //   565	568	563	finally
      //   542	560	571	finally
      //   573	576	571	finally
    }

    public Timeout timeout()
    {
      return this.timeout;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.cache2.Relay
 * JD-Core Version:    0.6.0
 */