package okhttp3.internal.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import okhttp3.Address;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.Route;
import okhttp3.internal.Util;

public final class RouteSelector
{
  private final Address address;
  private List<InetSocketAddress> inetSocketAddresses = Collections.emptyList();
  private InetSocketAddress lastInetSocketAddress;
  private Proxy lastProxy;
  private int nextInetSocketAddressIndex;
  private int nextProxyIndex;
  private final List<Route> postponedRoutes = new ArrayList();
  private List<Proxy> proxies = Collections.emptyList();
  private final RouteDatabase routeDatabase;

  public RouteSelector(Address paramAddress, RouteDatabase paramRouteDatabase)
  {
    this.address = paramAddress;
    this.routeDatabase = paramRouteDatabase;
    resetNextProxy(paramAddress.url(), paramAddress.proxy());
  }

  static String getHostString(InetSocketAddress paramInetSocketAddress)
  {
    InetAddress localInetAddress = paramInetSocketAddress.getAddress();
    if (localInetAddress == null)
      return paramInetSocketAddress.getHostName();
    return localInetAddress.getHostAddress();
  }

  private boolean hasNextInetSocketAddress()
  {
    return this.nextInetSocketAddressIndex < this.inetSocketAddresses.size();
  }

  private boolean hasNextPostponed()
  {
    return !this.postponedRoutes.isEmpty();
  }

  private boolean hasNextProxy()
  {
    return this.nextProxyIndex < this.proxies.size();
  }

  private InetSocketAddress nextInetSocketAddress()
    throws IOException
  {
    if (!hasNextInetSocketAddress())
      throw new SocketException("No route to " + this.address.url().host() + "; exhausted inet socket addresses: " + this.inetSocketAddresses);
    List localList = this.inetSocketAddresses;
    int i = this.nextInetSocketAddressIndex;
    this.nextInetSocketAddressIndex = (i + 1);
    return (InetSocketAddress)localList.get(i);
  }

  private Route nextPostponed()
  {
    return (Route)this.postponedRoutes.remove(0);
  }

  private Proxy nextProxy()
    throws IOException
  {
    if (!hasNextProxy())
      throw new SocketException("No route to " + this.address.url().host() + "; exhausted proxy configurations: " + this.proxies);
    List localList = this.proxies;
    int i = this.nextProxyIndex;
    this.nextProxyIndex = (i + 1);
    Proxy localProxy = (Proxy)localList.get(i);
    resetNextInetSocketAddress(localProxy);
    return localProxy;
  }

  private void resetNextInetSocketAddress(Proxy paramProxy)
    throws IOException
  {
    this.inetSocketAddresses = new ArrayList();
    String str;
    if ((paramProxy.type() == Proxy.Type.DIRECT) || (paramProxy.type() == Proxy.Type.SOCKS))
      str = this.address.url().host();
    InetSocketAddress localInetSocketAddress;
    for (int i = this.address.url().port(); (i < 1) || (i > 65535); i = localInetSocketAddress.getPort())
    {
      throw new SocketException("No route to " + str + ":" + i + "; port is out of range");
      SocketAddress localSocketAddress = paramProxy.address();
      if (!(localSocketAddress instanceof InetSocketAddress))
        throw new IllegalArgumentException("Proxy.address() is not an InetSocketAddress: " + localSocketAddress.getClass());
      localInetSocketAddress = (InetSocketAddress)localSocketAddress;
      str = getHostString(localInetSocketAddress);
    }
    if (paramProxy.type() == Proxy.Type.SOCKS)
      this.inetSocketAddresses.add(InetSocketAddress.createUnresolved(str, i));
    while (true)
    {
      this.nextInetSocketAddressIndex = 0;
      return;
      List localList = this.address.dns().lookup(str);
      int j = 0;
      int k = localList.size();
      while (j < k)
      {
        InetAddress localInetAddress = (InetAddress)localList.get(j);
        this.inetSocketAddresses.add(new InetSocketAddress(localInetAddress, i));
        j++;
      }
    }
  }

  private void resetNextProxy(HttpUrl paramHttpUrl, Proxy paramProxy)
  {
    if (paramProxy != null)
    {
      this.proxies = Collections.singletonList(paramProxy);
      this.nextProxyIndex = 0;
      return;
    }
    List localList1 = this.address.proxySelector().select(paramHttpUrl.uri());
    if ((localList1 != null) && (!localList1.isEmpty()));
    Proxy[] arrayOfProxy;
    for (List localList2 = Util.immutableList(localList1); ; localList2 = Util.immutableList(arrayOfProxy))
    {
      this.proxies = localList2;
      break;
      arrayOfProxy = new Proxy[1];
      arrayOfProxy[0] = Proxy.NO_PROXY;
    }
  }

  public void connectFailed(Route paramRoute, IOException paramIOException)
  {
    if ((paramRoute.proxy().type() != Proxy.Type.DIRECT) && (this.address.proxySelector() != null))
      this.address.proxySelector().connectFailed(this.address.url().uri(), paramRoute.proxy().address(), paramIOException);
    this.routeDatabase.failed(paramRoute);
  }

  public boolean hasNext()
  {
    return (hasNextInetSocketAddress()) || (hasNextProxy()) || (hasNextPostponed());
  }

  public Route next()
    throws IOException
  {
    Route localRoute;
    if (!hasNextInetSocketAddress())
      if (!hasNextProxy())
      {
        if (!hasNextPostponed())
          throw new NoSuchElementException();
        localRoute = nextPostponed();
      }
    do
    {
      return localRoute;
      this.lastProxy = nextProxy();
      this.lastInetSocketAddress = nextInetSocketAddress();
      localRoute = new Route(this.address, this.lastProxy, this.lastInetSocketAddress);
    }
    while (!this.routeDatabase.shouldPostpone(localRoute));
    this.postponedRoutes.add(localRoute);
    return next();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.connection.RouteSelector
 * JD-Core Version:    0.6.0
 */