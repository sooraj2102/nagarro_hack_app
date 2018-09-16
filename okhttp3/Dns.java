package okhttp3;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public abstract interface Dns
{
  public static final Dns SYSTEM = new Dns()
  {
    public List<InetAddress> lookup(String paramString)
      throws UnknownHostException
    {
      if (paramString == null)
        throw new UnknownHostException("hostname == null");
      return Arrays.asList(InetAddress.getAllByName(paramString));
    }
  };

  public abstract List<InetAddress> lookup(String paramString)
    throws UnknownHostException;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Dns
 * JD-Core Version:    0.6.0
 */