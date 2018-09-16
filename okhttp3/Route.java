package okhttp3;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

public final class Route
{
  final Address address;
  final InetSocketAddress inetSocketAddress;
  final Proxy proxy;

  public Route(Address paramAddress, Proxy paramProxy, InetSocketAddress paramInetSocketAddress)
  {
    if (paramAddress == null)
      throw new NullPointerException("address == null");
    if (paramProxy == null)
      throw new NullPointerException("proxy == null");
    if (paramInetSocketAddress == null)
      throw new NullPointerException("inetSocketAddress == null");
    this.address = paramAddress;
    this.proxy = paramProxy;
    this.inetSocketAddress = paramInetSocketAddress;
  }

  public Address address()
  {
    return this.address;
  }

  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof Route;
    int i = 0;
    if (bool1)
    {
      Route localRoute = (Route)paramObject;
      boolean bool2 = this.address.equals(localRoute.address);
      i = 0;
      if (bool2)
      {
        boolean bool3 = this.proxy.equals(localRoute.proxy);
        i = 0;
        if (bool3)
        {
          boolean bool4 = this.inetSocketAddress.equals(localRoute.inetSocketAddress);
          i = 0;
          if (bool4)
            i = 1;
        }
      }
    }
    return i;
  }

  public int hashCode()
  {
    return 31 * (31 * (527 + this.address.hashCode()) + this.proxy.hashCode()) + this.inetSocketAddress.hashCode();
  }

  public Proxy proxy()
  {
    return this.proxy;
  }

  public boolean requiresTunnel()
  {
    return (this.address.sslSocketFactory != null) && (this.proxy.type() == Proxy.Type.HTTP);
  }

  public InetSocketAddress socketAddress()
  {
    return this.inetSocketAddress;
  }

  public String toString()
  {
    return "Route{" + this.inetSocketAddress + "}";
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Route
 * JD-Core Version:    0.6.0
 */