package okhttp3.internal.cache;

import J;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.internal.Util;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class DiskLruCache
  implements Closeable, Flushable
{
  static final long ANY_SEQUENCE_NUMBER = -1L;
  private static final String CLEAN = "CLEAN";
  private static final String DIRTY = "DIRTY";
  static final String JOURNAL_FILE = "journal";
  static final String JOURNAL_FILE_BACKUP = "journal.bkp";
  static final String JOURNAL_FILE_TEMP = "journal.tmp";
  static final Pattern LEGAL_KEY_PATTERN;
  static final String MAGIC = "libcore.io.DiskLruCache";
  private static final String READ = "READ";
  private static final String REMOVE = "REMOVE";
  static final String VERSION_1 = "1";
  private final int appVersion;
  private final Runnable cleanupRunnable = new Runnable()
  {
    public void run()
    {
      int i = 1;
      synchronized (DiskLruCache.this)
      {
        if (!DiskLruCache.this.initialized)
        {
          if ((i | DiskLruCache.this.closed) != 0)
            return;
        }
        else
          i = 0;
      }
      try
      {
        DiskLruCache.this.trimToSize();
      }
      catch (IOException localIOException1)
      {
        try
        {
          while (true)
          {
            if (DiskLruCache.this.journalRebuildRequired())
            {
              DiskLruCache.this.rebuildJournal();
              DiskLruCache.this.redundantOpCount = 0;
            }
            monitorexit;
            return;
            localObject = finally;
            monitorexit;
            throw localObject;
            localIOException1 = localIOException1;
            DiskLruCache.this.mostRecentTrimFailed = true;
          }
        }
        catch (IOException localIOException2)
        {
          while (true)
          {
            DiskLruCache.this.mostRecentRebuildFailed = true;
            DiskLruCache.this.journalWriter = Okio.buffer(Okio.blackhole());
          }
        }
      }
    }
  };
  boolean closed;
  final File directory;
  private final Executor executor;
  final FileSystem fileSystem;
  boolean hasJournalErrors;
  boolean initialized;
  private final File journalFile;
  private final File journalFileBackup;
  private final File journalFileTmp;
  BufferedSink journalWriter;
  final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap(0, 0.75F, true);
  private long maxSize;
  boolean mostRecentRebuildFailed;
  boolean mostRecentTrimFailed;
  private long nextSequenceNumber = 0L;
  int redundantOpCount;
  private long size = 0L;
  final int valueCount;

  static
  {
    if (!DiskLruCache.class.desiredAssertionStatus());
    for (boolean bool = true; ; bool = false)
    {
      $assertionsDisabled = bool;
      LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,120}");
      return;
    }
  }

  DiskLruCache(FileSystem paramFileSystem, File paramFile, int paramInt1, int paramInt2, long paramLong, Executor paramExecutor)
  {
    this.fileSystem = paramFileSystem;
    this.directory = paramFile;
    this.appVersion = paramInt1;
    this.journalFile = new File(paramFile, "journal");
    this.journalFileTmp = new File(paramFile, "journal.tmp");
    this.journalFileBackup = new File(paramFile, "journal.bkp");
    this.valueCount = paramInt2;
    this.maxSize = paramLong;
    this.executor = paramExecutor;
  }

  private void checkNotClosed()
  {
    monitorenter;
    try
    {
      if (isClosed())
        throw new IllegalStateException("cache is closed");
    }
    finally
    {
      monitorexit;
    }
    monitorexit;
  }

  public static DiskLruCache create(FileSystem paramFileSystem, File paramFile, int paramInt1, int paramInt2, long paramLong)
  {
    if (paramLong <= 0L)
      throw new IllegalArgumentException("maxSize <= 0");
    if (paramInt2 <= 0)
      throw new IllegalArgumentException("valueCount <= 0");
    return new DiskLruCache(paramFileSystem, paramFile, paramInt1, paramInt2, paramLong, new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory("OkHttp DiskLruCache", true)));
  }

  private BufferedSink newJournalWriter()
    throws FileNotFoundException
  {
    return Okio.buffer(new FaultHidingSink(this.fileSystem.appendingSink(this.journalFile))
    {
      static
      {
        if (!DiskLruCache.class.desiredAssertionStatus());
        for (boolean bool = true; ; bool = false)
        {
          $assertionsDisabled = bool;
          return;
        }
      }

      protected void onException(IOException paramIOException)
      {
        assert (Thread.holdsLock(DiskLruCache.this));
        DiskLruCache.this.hasJournalErrors = true;
      }
    });
  }

  private void processJournal()
    throws IOException
  {
    this.fileSystem.delete(this.journalFileTmp);
    Iterator localIterator = this.lruEntries.values().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      if (localEntry.currentEditor == null)
      {
        for (int j = 0; j < this.valueCount; j++)
          this.size += localEntry.lengths[j];
        continue;
      }
      localEntry.currentEditor = null;
      for (int i = 0; i < this.valueCount; i++)
      {
        this.fileSystem.delete(localEntry.cleanFiles[i]);
        this.fileSystem.delete(localEntry.dirtyFiles[i]);
      }
      localIterator.remove();
    }
  }

  // ERROR //
  private void readJournal()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 114	okhttp3/internal/cache/DiskLruCache:fileSystem	Lokhttp3/internal/io/FileSystem;
    //   4: aload_0
    //   5: getfield 125	okhttp3/internal/cache/DiskLruCache:journalFile	Ljava/io/File;
    //   8: invokeinterface 255 2 0
    //   13: invokestatic 258	okio/Okio:buffer	(Lokio/Source;)Lokio/BufferedSource;
    //   16: astore_1
    //   17: aload_1
    //   18: invokeinterface 264 1 0
    //   23: astore_3
    //   24: aload_1
    //   25: invokeinterface 264 1 0
    //   30: astore 4
    //   32: aload_1
    //   33: invokeinterface 264 1 0
    //   38: astore 5
    //   40: aload_1
    //   41: invokeinterface 264 1 0
    //   46: astore 6
    //   48: aload_1
    //   49: invokeinterface 264 1 0
    //   54: astore 7
    //   56: ldc 34
    //   58: aload_3
    //   59: invokevirtual 270	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   62: ifeq +54 -> 116
    //   65: ldc 41
    //   67: aload 4
    //   69: invokevirtual 270	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   72: ifeq +44 -> 116
    //   75: aload_0
    //   76: getfield 118	okhttp3/internal/cache/DiskLruCache:appVersion	I
    //   79: invokestatic 276	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   82: aload 5
    //   84: invokevirtual 270	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   87: ifeq +29 -> 116
    //   90: aload_0
    //   91: getfield 131	okhttp3/internal/cache/DiskLruCache:valueCount	I
    //   94: invokestatic 276	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   97: aload 6
    //   99: invokevirtual 270	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   102: ifeq +14 -> 116
    //   105: ldc_w 278
    //   108: aload 7
    //   110: invokevirtual 270	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   113: ifne +77 -> 190
    //   116: new 205	java/io/IOException
    //   119: dup
    //   120: new 280	java/lang/StringBuilder
    //   123: dup
    //   124: invokespecial 281	java/lang/StringBuilder:<init>	()V
    //   127: ldc_w 283
    //   130: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   133: aload_3
    //   134: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: ldc_w 289
    //   140: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: aload 4
    //   145: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: ldc_w 289
    //   151: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: aload 6
    //   156: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: ldc_w 289
    //   162: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: aload 7
    //   167: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: ldc_w 291
    //   173: invokevirtual 287	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   176: invokevirtual 293	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   179: invokespecial 294	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   182: athrow
    //   183: astore_2
    //   184: aload_1
    //   185: invokestatic 298	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   188: aload_2
    //   189: athrow
    //   190: iconst_0
    //   191: istore 8
    //   193: aload_0
    //   194: aload_1
    //   195: invokeinterface 264 1 0
    //   200: invokespecial 301	okhttp3/internal/cache/DiskLruCache:readJournalLine	(Ljava/lang/String;)V
    //   203: iinc 8 1
    //   206: goto -13 -> 193
    //   209: astore 9
    //   211: aload_0
    //   212: iload 8
    //   214: aload_0
    //   215: getfield 103	okhttp3/internal/cache/DiskLruCache:lruEntries	Ljava/util/LinkedHashMap;
    //   218: invokevirtual 304	java/util/LinkedHashMap:size	()I
    //   221: isub
    //   222: putfield 306	okhttp3/internal/cache/DiskLruCache:redundantOpCount	I
    //   225: aload_1
    //   226: invokeinterface 309 1 0
    //   231: ifne +12 -> 243
    //   234: aload_0
    //   235: invokevirtual 312	okhttp3/internal/cache/DiskLruCache:rebuildJournal	()V
    //   238: aload_1
    //   239: invokestatic 298	okhttp3/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   242: return
    //   243: aload_0
    //   244: aload_0
    //   245: invokespecial 314	okhttp3/internal/cache/DiskLruCache:newJournalWriter	()Lokio/BufferedSink;
    //   248: putfield 316	okhttp3/internal/cache/DiskLruCache:journalWriter	Lokio/BufferedSink;
    //   251: goto -13 -> 238
    //
    // Exception table:
    //   from	to	target	type
    //   17	116	183	finally
    //   116	183	183	finally
    //   193	203	183	finally
    //   211	238	183	finally
    //   243	251	183	finally
    //   193	203	209	java/io/EOFException
  }

  private void readJournalLine(String paramString)
    throws IOException
  {
    int i = paramString.indexOf(' ');
    if (i == -1)
      throw new IOException("unexpected journal line: " + paramString);
    int j = i + 1;
    int k = paramString.indexOf(' ', j);
    String str;
    if (k == -1)
    {
      str = paramString.substring(j);
      if ((i != "REMOVE".length()) || (!paramString.startsWith("REMOVE")))
        break label104;
      this.lruEntries.remove(str);
    }
    label104: 
    do
    {
      return;
      str = paramString.substring(j, k);
      Entry localEntry = (Entry)this.lruEntries.get(str);
      if (localEntry == null)
      {
        localEntry = new Entry(str);
        this.lruEntries.put(str, localEntry);
      }
      if ((k != -1) && (i == "CLEAN".length()) && (paramString.startsWith("CLEAN")))
      {
        String[] arrayOfString = paramString.substring(k + 1).split(" ");
        localEntry.readable = true;
        localEntry.currentEditor = null;
        localEntry.setLengths(arrayOfString);
        return;
      }
      if ((k != -1) || (i != "DIRTY".length()) || (!paramString.startsWith("DIRTY")))
        continue;
      localEntry.currentEditor = new Editor(localEntry);
      return;
    }
    while ((k == -1) && (i == "READ".length()) && (paramString.startsWith("READ")));
    throw new IOException("unexpected journal line: " + paramString);
  }

  private void validateKey(String paramString)
  {
    if (!LEGAL_KEY_PATTERN.matcher(paramString).matches())
      throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,120}: \"" + paramString + "\"");
  }

  public void close()
    throws IOException
  {
    monitorenter;
    while (true)
    {
      int j;
      try
      {
        if ((this.initialized) && (!this.closed))
          continue;
        this.closed = true;
        return;
        Entry[] arrayOfEntry = (Entry[])this.lruEntries.values().toArray(new Entry[this.lruEntries.size()]);
        int i = arrayOfEntry.length;
        j = 0;
        if (j >= i)
          continue;
        Entry localEntry = arrayOfEntry[j];
        if (localEntry.currentEditor != null)
        {
          localEntry.currentEditor.abort();
          break label118;
          trimToSize();
          this.journalWriter.close();
          this.journalWriter = null;
          this.closed = true;
          continue;
        }
      }
      finally
      {
        monitorexit;
      }
      label118: j++;
    }
  }

  void completeEdit(Editor paramEditor, boolean paramBoolean)
    throws IOException
  {
    monitorenter;
    Entry localEntry;
    try
    {
      localEntry = paramEditor.entry;
      if (localEntry.currentEditor != paramEditor)
        throw new IllegalStateException();
    }
    finally
    {
      monitorexit;
    }
    if ((paramBoolean) && (!localEntry.readable))
      for (int j = 0; j < this.valueCount; j++)
      {
        if (paramEditor.written[j] == 0)
        {
          paramEditor.abort();
          throw new IllegalStateException("Newly created entry didn't create value for index " + j);
        }
        if (this.fileSystem.exists(localEntry.dirtyFiles[j]))
          continue;
        paramEditor.abort();
        monitorexit;
        return;
      }
    for (int i = 0; ; i++)
      if (i < this.valueCount)
      {
        File localFile1 = localEntry.dirtyFiles[i];
        if (paramBoolean)
        {
          if (!this.fileSystem.exists(localFile1))
            continue;
          File localFile2 = localEntry.cleanFiles[i];
          this.fileSystem.rename(localFile1, localFile2);
          long l2 = localEntry.lengths[i];
          long l3 = this.fileSystem.size(localFile2);
          localEntry.lengths[i] = l3;
          this.size = (l3 + (this.size - l2));
        }
        else
        {
          this.fileSystem.delete(localFile1);
        }
      }
      else
      {
        this.redundantOpCount = (1 + this.redundantOpCount);
        localEntry.currentEditor = null;
        if ((paramBoolean | localEntry.readable))
        {
          localEntry.readable = true;
          this.journalWriter.writeUtf8("CLEAN").writeByte(32);
          this.journalWriter.writeUtf8(localEntry.key);
          localEntry.writeLengths(this.journalWriter);
          this.journalWriter.writeByte(10);
          if (paramBoolean)
          {
            long l1 = this.nextSequenceNumber;
            this.nextSequenceNumber = (1L + l1);
            localEntry.sequenceNumber = l1;
          }
        }
        while (true)
        {
          this.journalWriter.flush();
          if ((this.size <= this.maxSize) && (!journalRebuildRequired()))
            break;
          this.executor.execute(this.cleanupRunnable);
          break;
          this.lruEntries.remove(localEntry.key);
          this.journalWriter.writeUtf8("REMOVE").writeByte(32);
          this.journalWriter.writeUtf8(localEntry.key);
          this.journalWriter.writeByte(10);
        }
      }
  }

  public void delete()
    throws IOException
  {
    close();
    this.fileSystem.deleteContents(this.directory);
  }

  public Editor edit(String paramString)
    throws IOException
  {
    return edit(paramString, -1L);
  }

  Editor edit(String paramString, long paramLong)
    throws IOException
  {
    monitorenter;
    while (true)
    {
      Entry localEntry;
      try
      {
        initialize();
        checkNotClosed();
        validateKey(paramString);
        localEntry = (Entry)this.lruEntries.get(paramString);
        if (paramLong == -1L)
          continue;
        localEditor1 = null;
        if (localEntry == null)
          continue;
        long l = localEntry.sequenceNumber;
        boolean bool1 = l < paramLong;
        localEditor1 = null;
        if (bool1)
          return localEditor1;
        if (localEntry == null)
          continue;
        Editor localEditor2 = localEntry.currentEditor;
        localEditor1 = null;
        if (localEditor2 != null)
          continue;
        if ((this.mostRecentTrimFailed) || (this.mostRecentRebuildFailed))
        {
          this.executor.execute(this.cleanupRunnable);
          localEditor1 = null;
          continue;
        }
      }
      finally
      {
        monitorexit;
      }
      this.journalWriter.writeUtf8("DIRTY").writeByte(32).writeUtf8(paramString).writeByte(10);
      this.journalWriter.flush();
      boolean bool2 = this.hasJournalErrors;
      Editor localEditor1 = null;
      if (bool2)
        continue;
      if (localEntry == null)
      {
        localEntry = new Entry(paramString);
        this.lruEntries.put(paramString, localEntry);
      }
      localEditor1 = new Editor(localEntry);
      localEntry.currentEditor = localEditor1;
    }
  }

  public void evictAll()
    throws IOException
  {
    int i = 0;
    monitorenter;
    try
    {
      initialize();
      Entry[] arrayOfEntry = (Entry[])this.lruEntries.values().toArray(new Entry[this.lruEntries.size()]);
      int j = arrayOfEntry.length;
      while (i < j)
      {
        removeEntry(arrayOfEntry[i]);
        i++;
      }
      this.mostRecentTrimFailed = false;
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public void flush()
    throws IOException
  {
    monitorenter;
    try
    {
      boolean bool = this.initialized;
      if (!bool);
      while (true)
      {
        return;
        checkNotClosed();
        trimToSize();
        this.journalWriter.flush();
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public Snapshot get(String paramString)
    throws IOException
  {
    monitorenter;
    try
    {
      initialize();
      checkNotClosed();
      validateKey(paramString);
      Entry localEntry = (Entry)this.lruEntries.get(paramString);
      Snapshot localSnapshot;
      if (localEntry != null)
      {
        boolean bool = localEntry.readable;
        if (bool);
      }
      else
      {
        localSnapshot = null;
      }
      while (true)
      {
        return localSnapshot;
        localSnapshot = localEntry.snapshot();
        if (localSnapshot == null)
        {
          localSnapshot = null;
          continue;
        }
        this.redundantOpCount = (1 + this.redundantOpCount);
        this.journalWriter.writeUtf8("READ").writeByte(32).writeUtf8(paramString).writeByte(10);
        if (!journalRebuildRequired())
          continue;
        this.executor.execute(this.cleanupRunnable);
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public File getDirectory()
  {
    return this.directory;
  }

  public long getMaxSize()
  {
    monitorenter;
    try
    {
      long l = this.maxSize;
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

  public void initialize()
    throws IOException
  {
    monitorenter;
    try
    {
      if ((!$assertionsDisabled) && (!Thread.holdsLock(this)))
        throw new AssertionError();
    }
    finally
    {
      monitorexit;
    }
    boolean bool1 = this.initialized;
    if (bool1);
    while (true)
    {
      monitorexit;
      return;
      if (this.fileSystem.exists(this.journalFileBackup))
      {
        if (this.fileSystem.exists(this.journalFile))
          this.fileSystem.delete(this.journalFileBackup);
      }
      else
      {
        boolean bool2 = this.fileSystem.exists(this.journalFile);
        if (bool2)
          try
          {
            readJournal();
            processJournal();
            this.initialized = true;
          }
          catch (IOException localIOException)
          {
            Platform.get().log(5, "DiskLruCache " + this.directory + " is corrupt: " + localIOException.getMessage() + ", removing", localIOException);
          }
      }
      try
      {
        delete();
        this.closed = false;
        rebuildJournal();
        this.initialized = true;
        continue;
        this.fileSystem.rename(this.journalFileBackup, this.journalFile);
      }
      finally
      {
        this.closed = false;
      }
    }
  }

  public boolean isClosed()
  {
    monitorenter;
    try
    {
      boolean bool = this.closed;
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

  boolean journalRebuildRequired()
  {
    return (this.redundantOpCount >= 2000) && (this.redundantOpCount >= this.lruEntries.size());
  }

  void rebuildJournal()
    throws IOException
  {
    monitorenter;
    BufferedSink localBufferedSink;
    while (true)
    {
      Entry localEntry;
      try
      {
        if (this.journalWriter == null)
          continue;
        this.journalWriter.close();
        localBufferedSink = Okio.buffer(this.fileSystem.sink(this.journalFileTmp));
        try
        {
          localBufferedSink.writeUtf8("libcore.io.DiskLruCache").writeByte(10);
          localBufferedSink.writeUtf8("1").writeByte(10);
          localBufferedSink.writeDecimalLong(this.appVersion).writeByte(10);
          localBufferedSink.writeDecimalLong(this.valueCount).writeByte(10);
          localBufferedSink.writeByte(10);
          Iterator localIterator = this.lruEntries.values().iterator();
          if (!localIterator.hasNext())
            break;
          localEntry = (Entry)localIterator.next();
          if (localEntry.currentEditor != null)
          {
            localBufferedSink.writeUtf8("DIRTY").writeByte(32);
            localBufferedSink.writeUtf8(localEntry.key);
            localBufferedSink.writeByte(10);
            continue;
          }
        }
        finally
        {
          localBufferedSink.close();
        }
      }
      finally
      {
        monitorexit;
      }
      localBufferedSink.writeUtf8("CLEAN").writeByte(32);
      localBufferedSink.writeUtf8(localEntry.key);
      localEntry.writeLengths(localBufferedSink);
      localBufferedSink.writeByte(10);
    }
    localBufferedSink.close();
    if (this.fileSystem.exists(this.journalFile))
      this.fileSystem.rename(this.journalFile, this.journalFileBackup);
    this.fileSystem.rename(this.journalFileTmp, this.journalFile);
    this.fileSystem.delete(this.journalFileBackup);
    this.journalWriter = newJournalWriter();
    this.hasJournalErrors = false;
    this.mostRecentRebuildFailed = false;
    monitorexit;
  }

  public boolean remove(String paramString)
    throws IOException
  {
    monitorenter;
    try
    {
      initialize();
      checkNotClosed();
      validateKey(paramString);
      Entry localEntry = (Entry)this.lruEntries.get(paramString);
      boolean bool = false;
      if (localEntry == null);
      while (true)
      {
        return bool;
        bool = removeEntry(localEntry);
        if ((!bool) || (this.size > this.maxSize))
          continue;
        this.mostRecentTrimFailed = false;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  boolean removeEntry(Entry paramEntry)
    throws IOException
  {
    if (paramEntry.currentEditor != null)
      paramEntry.currentEditor.detach();
    for (int i = 0; i < this.valueCount; i++)
    {
      this.fileSystem.delete(paramEntry.cleanFiles[i]);
      this.size -= paramEntry.lengths[i];
      paramEntry.lengths[i] = 0L;
    }
    this.redundantOpCount = (1 + this.redundantOpCount);
    this.journalWriter.writeUtf8("REMOVE").writeByte(32).writeUtf8(paramEntry.key).writeByte(10);
    this.lruEntries.remove(paramEntry.key);
    if (journalRebuildRequired())
      this.executor.execute(this.cleanupRunnable);
    return true;
  }

  public void setMaxSize(long paramLong)
  {
    monitorenter;
    try
    {
      this.maxSize = paramLong;
      if (this.initialized)
        this.executor.execute(this.cleanupRunnable);
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

  public long size()
    throws IOException
  {
    monitorenter;
    try
    {
      initialize();
      long l = this.size;
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

  public Iterator<Snapshot> snapshots()
    throws IOException
  {
    monitorenter;
    try
    {
      initialize();
      3 local3 = new Iterator()
      {
        final Iterator<DiskLruCache.Entry> delegate = new ArrayList(DiskLruCache.this.lruEntries.values()).iterator();
        DiskLruCache.Snapshot nextSnapshot;
        DiskLruCache.Snapshot removeSnapshot;

        public boolean hasNext()
        {
          if (this.nextSnapshot != null)
            return true;
          synchronized (DiskLruCache.this)
          {
            if (DiskLruCache.this.closed)
              return false;
            while (this.delegate.hasNext())
            {
              DiskLruCache.Snapshot localSnapshot = ((DiskLruCache.Entry)this.delegate.next()).snapshot();
              if (localSnapshot == null)
                continue;
              this.nextSnapshot = localSnapshot;
              return true;
            }
          }
          monitorexit;
          return false;
        }

        public DiskLruCache.Snapshot next()
        {
          if (!hasNext())
            throw new NoSuchElementException();
          this.removeSnapshot = this.nextSnapshot;
          this.nextSnapshot = null;
          return this.removeSnapshot;
        }

        // ERROR //
        public void remove()
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 75	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
          //   4: ifnonnull +13 -> 17
          //   7: new 80	java/lang/IllegalStateException
          //   10: dup
          //   11: ldc 82
          //   13: invokespecial 85	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
          //   16: athrow
          //   17: aload_0
          //   18: getfield 24	okhttp3/internal/cache/DiskLruCache$3:this$0	Lokhttp3/internal/cache/DiskLruCache;
          //   21: aload_0
          //   22: getfield 75	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
          //   25: invokestatic 91	okhttp3/internal/cache/DiskLruCache$Snapshot:access$000	(Lokhttp3/internal/cache/DiskLruCache$Snapshot;)Ljava/lang/String;
          //   28: invokevirtual 94	okhttp3/internal/cache/DiskLruCache:remove	(Ljava/lang/String;)Z
          //   31: pop
          //   32: aload_0
          //   33: aconst_null
          //   34: putfield 75	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
          //   37: return
          //   38: astore_2
          //   39: aload_0
          //   40: aconst_null
          //   41: putfield 75	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
          //   44: return
          //   45: astore_1
          //   46: aload_0
          //   47: aconst_null
          //   48: putfield 75	okhttp3/internal/cache/DiskLruCache$3:removeSnapshot	Lokhttp3/internal/cache/DiskLruCache$Snapshot;
          //   51: aload_1
          //   52: athrow
          //
          // Exception table:
          //   from	to	target	type
          //   17	32	38	java/io/IOException
          //   17	32	45	finally
        }
      };
      monitorexit;
      return local3;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  void trimToSize()
    throws IOException
  {
    while (this.size > this.maxSize)
      removeEntry((Entry)this.lruEntries.values().iterator().next());
    this.mostRecentTrimFailed = false;
  }

  public final class Editor
  {
    private boolean done;
    final DiskLruCache.Entry entry;
    final boolean[] written;

    Editor(DiskLruCache.Entry arg2)
    {
      Object localObject;
      this.entry = localObject;
      if (localObject.readable);
      for (boolean[] arrayOfBoolean = null; ; arrayOfBoolean = new boolean[DiskLruCache.this.valueCount])
      {
        this.written = arrayOfBoolean;
        return;
      }
    }

    public void abort()
      throws IOException
    {
      synchronized (DiskLruCache.this)
      {
        if (this.done)
          throw new IllegalStateException();
      }
      if (this.entry.currentEditor == this)
        DiskLruCache.this.completeEdit(this, false);
      this.done = true;
      monitorexit;
    }

    public void abortUnlessCommitted()
    {
      synchronized (DiskLruCache.this)
      {
        if (!this.done)
        {
          Editor localEditor = this.entry.currentEditor;
          if (localEditor != this);
        }
      }
      try
      {
        DiskLruCache.this.completeEdit(this, false);
        label36: monitorexit;
        return;
        localObject = finally;
        monitorexit;
        throw localObject;
      }
      catch (IOException localIOException)
      {
        break label36;
      }
    }

    public void commit()
      throws IOException
    {
      synchronized (DiskLruCache.this)
      {
        if (this.done)
          throw new IllegalStateException();
      }
      if (this.entry.currentEditor == this)
        DiskLruCache.this.completeEdit(this, true);
      this.done = true;
      monitorexit;
    }

    void detach()
    {
      int i;
      if (this.entry.currentEditor == this)
        i = 0;
      while (true)
      {
        if (i < DiskLruCache.this.valueCount);
        try
        {
          DiskLruCache.this.fileSystem.delete(this.entry.dirtyFiles[i]);
          label45: i++;
          continue;
          this.entry.currentEditor = null;
          return;
        }
        catch (IOException localIOException)
        {
          break label45;
        }
      }
    }

    public Sink newSink(int paramInt)
    {
      synchronized (DiskLruCache.this)
      {
        if (this.done)
          throw new IllegalStateException();
      }
      if (this.entry.currentEditor != this)
      {
        Sink localSink3 = Okio.blackhole();
        monitorexit;
        return localSink3;
      }
      if (!this.entry.readable)
        this.written[paramInt] = true;
      File localFile = this.entry.dirtyFiles[paramInt];
      Sink localSink1;
      try
      {
        Sink localSink2 = DiskLruCache.this.fileSystem.sink(localFile);
        1 local1 = new FaultHidingSink(localSink2)
        {
          protected void onException(IOException paramIOException)
          {
            synchronized (DiskLruCache.this)
            {
              DiskLruCache.Editor.this.detach();
              return;
            }
          }
        };
        monitorexit;
        return local1;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        localSink1 = Okio.blackhole();
        monitorexit;
      }
      return localSink1;
    }

    public Source newSource(int paramInt)
    {
      synchronized (DiskLruCache.this)
      {
        if (this.done)
          throw new IllegalStateException();
      }
      if ((!this.entry.readable) || (this.entry.currentEditor != this))
      {
        monitorexit;
        return null;
      }
      try
      {
        Source localSource = DiskLruCache.this.fileSystem.source(this.entry.cleanFiles[paramInt]);
        monitorexit;
        return localSource;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        monitorexit;
      }
      return null;
    }
  }

  private final class Entry
  {
    final File[] cleanFiles;
    DiskLruCache.Editor currentEditor;
    final File[] dirtyFiles;
    final String key;
    final long[] lengths;
    boolean readable;
    long sequenceNumber;

    Entry(String arg2)
    {
      String str;
      this.key = str;
      this.lengths = new long[DiskLruCache.this.valueCount];
      this.cleanFiles = new File[DiskLruCache.this.valueCount];
      this.dirtyFiles = new File[DiskLruCache.this.valueCount];
      StringBuilder localStringBuilder = new StringBuilder(str).append('.');
      int i = localStringBuilder.length();
      for (int j = 0; j < DiskLruCache.this.valueCount; j++)
      {
        localStringBuilder.append(j);
        this.cleanFiles[j] = new File(DiskLruCache.this.directory, localStringBuilder.toString());
        localStringBuilder.append(".tmp");
        this.dirtyFiles[j] = new File(DiskLruCache.this.directory, localStringBuilder.toString());
        localStringBuilder.setLength(i);
      }
    }

    private IOException invalidLengths(String[] paramArrayOfString)
      throws IOException
    {
      throw new IOException("unexpected journal line: " + Arrays.toString(paramArrayOfString));
    }

    void setLengths(String[] paramArrayOfString)
      throws IOException
    {
      if (paramArrayOfString.length != DiskLruCache.this.valueCount)
        throw invalidLengths(paramArrayOfString);
      int i = 0;
      try
      {
        while (i < paramArrayOfString.length)
        {
          this.lengths[i] = Long.parseLong(paramArrayOfString[i]);
          i++;
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw invalidLengths(paramArrayOfString);
      }
    }

    DiskLruCache.Snapshot snapshot()
    {
      if (!Thread.holdsLock(DiskLruCache.this))
        throw new AssertionError();
      Source[] arrayOfSource = new Source[DiskLruCache.this.valueCount];
      long[] arrayOfLong = (long[])this.lengths.clone();
      int i = 0;
      try
      {
        while (i < DiskLruCache.this.valueCount)
        {
          arrayOfSource[i] = DiskLruCache.this.fileSystem.source(this.cleanFiles[i]);
          i++;
        }
        DiskLruCache.Snapshot localSnapshot = new DiskLruCache.Snapshot(DiskLruCache.this, this.key, this.sequenceNumber, arrayOfSource, arrayOfLong);
        return localSnapshot;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        for (int j = 0; (j < DiskLruCache.this.valueCount) && (arrayOfSource[j] != null); j++)
          Util.closeQuietly(arrayOfSource[j]);
      }
      try
      {
        DiskLruCache.this.removeEntry(this);
        label152: return null;
      }
      catch (IOException localIOException)
      {
        break label152;
      }
    }

    void writeLengths(BufferedSink paramBufferedSink)
      throws IOException
    {
      for (long l : this.lengths)
        paramBufferedSink.writeByte(32).writeDecimalLong(l);
    }
  }

  public final class Snapshot
    implements Closeable
  {
    private final String key;
    private final long[] lengths;
    private final long sequenceNumber;
    private final Source[] sources;

    Snapshot(String paramLong, long arg3, Source[] paramArrayOfLong, long[] arg6)
    {
      this.key = paramLong;
      this.sequenceNumber = ???;
      this.sources = paramArrayOfLong;
      Object localObject;
      this.lengths = localObject;
    }

    public void close()
    {
      Source[] arrayOfSource = this.sources;
      int i = arrayOfSource.length;
      for (int j = 0; j < i; j++)
        Util.closeQuietly(arrayOfSource[j]);
    }

    public DiskLruCache.Editor edit()
      throws IOException
    {
      return DiskLruCache.this.edit(this.key, this.sequenceNumber);
    }

    public long getLength(int paramInt)
    {
      return this.lengths[paramInt];
    }

    public Source getSource(int paramInt)
    {
      return this.sources[paramInt];
    }

    public String key()
    {
      return this.key;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.cache.DiskLruCache
 * JD-Core Version:    0.6.0
 */