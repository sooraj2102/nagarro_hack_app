package okhttp3;

import okio.ByteString;

public abstract class WebSocketListener
{
  public void onClosed(WebSocket paramWebSocket, int paramInt, String paramString)
  {
  }

  public void onClosing(WebSocket paramWebSocket, int paramInt, String paramString)
  {
  }

  public void onFailure(WebSocket paramWebSocket, Throwable paramThrowable, Response paramResponse)
  {
  }

  public void onMessage(WebSocket paramWebSocket, String paramString)
  {
  }

  public void onMessage(WebSocket paramWebSocket, ByteString paramByteString)
  {
  }

  public void onOpen(WebSocket paramWebSocket, Response paramResponse)
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.WebSocketListener
 * JD-Core Version:    0.6.0
 */