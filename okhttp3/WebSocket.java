package okhttp3;

import okio.ByteString;

public abstract interface WebSocket
{
  public abstract void cancel();

  public abstract boolean close(int paramInt, String paramString);

  public abstract long queueSize();

  public abstract Request request();

  public abstract boolean send(String paramString);

  public abstract boolean send(ByteString paramByteString);

  public static abstract interface Factory
  {
    public abstract WebSocket newWebSocket(Request paramRequest, WebSocketListener paramWebSocketListener);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.WebSocket
 * JD-Core Version:    0.6.0
 */