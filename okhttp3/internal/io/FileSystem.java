package okhttp3.internal.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import okio.Okio;
import okio.Sink;
import okio.Source;

public abstract interface FileSystem
{
  public static final FileSystem SYSTEM = new FileSystem()
  {
    public Sink appendingSink(File paramFile)
      throws FileNotFoundException
    {
      try
      {
        Sink localSink = Okio.appendingSink(paramFile);
        return localSink;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        paramFile.getParentFile().mkdirs();
      }
      return Okio.appendingSink(paramFile);
    }

    public void delete(File paramFile)
      throws IOException
    {
      if ((!paramFile.delete()) && (paramFile.exists()))
        throw new IOException("failed to delete " + paramFile);
    }

    public void deleteContents(File paramFile)
      throws IOException
    {
      File[] arrayOfFile = paramFile.listFiles();
      if (arrayOfFile == null)
        throw new IOException("not a readable directory: " + paramFile);
      int i = arrayOfFile.length;
      for (int j = 0; j < i; j++)
      {
        File localFile = arrayOfFile[j];
        if (localFile.isDirectory())
          deleteContents(localFile);
        if (localFile.delete())
          continue;
        throw new IOException("failed to delete " + localFile);
      }
    }

    public boolean exists(File paramFile)
    {
      return paramFile.exists();
    }

    public void rename(File paramFile1, File paramFile2)
      throws IOException
    {
      delete(paramFile2);
      if (!paramFile1.renameTo(paramFile2))
        throw new IOException("failed to rename " + paramFile1 + " to " + paramFile2);
    }

    public Sink sink(File paramFile)
      throws FileNotFoundException
    {
      try
      {
        Sink localSink = Okio.sink(paramFile);
        return localSink;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        paramFile.getParentFile().mkdirs();
      }
      return Okio.sink(paramFile);
    }

    public long size(File paramFile)
    {
      return paramFile.length();
    }

    public Source source(File paramFile)
      throws FileNotFoundException
    {
      return Okio.source(paramFile);
    }
  };

  public abstract Sink appendingSink(File paramFile)
    throws FileNotFoundException;

  public abstract void delete(File paramFile)
    throws IOException;

  public abstract void deleteContents(File paramFile)
    throws IOException;

  public abstract boolean exists(File paramFile);

  public abstract void rename(File paramFile1, File paramFile2)
    throws IOException;

  public abstract Sink sink(File paramFile)
    throws FileNotFoundException;

  public abstract long size(File paramFile);

  public abstract Source source(File paramFile)
    throws FileNotFoundException;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.io.FileSystem
 * JD-Core Version:    0.6.0
 */