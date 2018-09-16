package okhttp3;

import java.net.Socket;

public abstract interface Connection
{
  public abstract Handshake handshake();

  public abstract Protocol protocol();

  public abstract Route route();

  public abstract Socket socket();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Connection
 * JD-Core Version:    0.6.0
 */