package okhttp3;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.internal.Util;

public final class Address
{
  final CertificatePinner certificatePinner;
  final List<ConnectionSpec> connectionSpecs;
  final Dns dns;
  final HostnameVerifier hostnameVerifier;
  final List<Protocol> protocols;
  final Proxy proxy;
  final Authenticator proxyAuthenticator;
  final ProxySelector proxySelector;
  final SocketFactory socketFactory;
  final SSLSocketFactory sslSocketFactory;
  final HttpUrl url;

  public Address(String paramString, int paramInt, Dns paramDns, SocketFactory paramSocketFactory, SSLSocketFactory paramSSLSocketFactory, HostnameVerifier paramHostnameVerifier, CertificatePinner paramCertificatePinner, Authenticator paramAuthenticator, Proxy paramProxy, List<Protocol> paramList, List<ConnectionSpec> paramList1, ProxySelector paramProxySelector)
  {
    HttpUrl.Builder localBuilder = new HttpUrl.Builder();
    if (paramSSLSocketFactory != null);
    for (String str = "https"; ; str = "http")
    {
      this.url = localBuilder.scheme(str).host(paramString).port(paramInt).build();
      if (paramDns != null)
        break;
      throw new NullPointerException("dns == null");
    }
    this.dns = paramDns;
    if (paramSocketFactory == null)
      throw new NullPointerException("socketFactory == null");
    this.socketFactory = paramSocketFactory;
    if (paramAuthenticator == null)
      throw new NullPointerException("proxyAuthenticator == null");
    this.proxyAuthenticator = paramAuthenticator;
    if (paramList == null)
      throw new NullPointerException("protocols == null");
    this.protocols = Util.immutableList(paramList);
    if (paramList1 == null)
      throw new NullPointerException("connectionSpecs == null");
    this.connectionSpecs = Util.immutableList(paramList1);
    if (paramProxySelector == null)
      throw new NullPointerException("proxySelector == null");
    this.proxySelector = paramProxySelector;
    this.proxy = paramProxy;
    this.sslSocketFactory = paramSSLSocketFactory;
    this.hostnameVerifier = paramHostnameVerifier;
    this.certificatePinner = paramCertificatePinner;
  }

  public CertificatePinner certificatePinner()
  {
    return this.certificatePinner;
  }

  public List<ConnectionSpec> connectionSpecs()
  {
    return this.connectionSpecs;
  }

  public Dns dns()
  {
    return this.dns;
  }

  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof Address;
    int i = 0;
    if (bool1)
    {
      Address localAddress = (Address)paramObject;
      boolean bool2 = this.url.equals(localAddress.url);
      i = 0;
      if (bool2)
      {
        boolean bool3 = this.dns.equals(localAddress.dns);
        i = 0;
        if (bool3)
        {
          boolean bool4 = this.proxyAuthenticator.equals(localAddress.proxyAuthenticator);
          i = 0;
          if (bool4)
          {
            boolean bool5 = this.protocols.equals(localAddress.protocols);
            i = 0;
            if (bool5)
            {
              boolean bool6 = this.connectionSpecs.equals(localAddress.connectionSpecs);
              i = 0;
              if (bool6)
              {
                boolean bool7 = this.proxySelector.equals(localAddress.proxySelector);
                i = 0;
                if (bool7)
                {
                  boolean bool8 = Util.equal(this.proxy, localAddress.proxy);
                  i = 0;
                  if (bool8)
                  {
                    boolean bool9 = Util.equal(this.sslSocketFactory, localAddress.sslSocketFactory);
                    i = 0;
                    if (bool9)
                    {
                      boolean bool10 = Util.equal(this.hostnameVerifier, localAddress.hostnameVerifier);
                      i = 0;
                      if (bool10)
                      {
                        boolean bool11 = Util.equal(this.certificatePinner, localAddress.certificatePinner);
                        i = 0;
                        if (bool11)
                          i = 1;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return i;
  }

  public int hashCode()
  {
    int i = 31 * (31 * (31 * (31 * (31 * (31 * (527 + this.url.hashCode()) + this.dns.hashCode()) + this.proxyAuthenticator.hashCode()) + this.protocols.hashCode()) + this.connectionSpecs.hashCode()) + this.proxySelector.hashCode());
    int j;
    int m;
    label112: int n;
    if (this.proxy != null)
    {
      j = this.proxy.hashCode();
      int k = 31 * (i + j);
      if (this.sslSocketFactory == null)
        break label181;
      m = this.sslSocketFactory.hashCode();
      n = 31 * (k + m);
      if (this.hostnameVerifier == null)
        break label187;
    }
    label181: label187: for (int i1 = this.hostnameVerifier.hashCode(); ; i1 = 0)
    {
      int i2 = 31 * (n + i1);
      CertificatePinner localCertificatePinner = this.certificatePinner;
      int i3 = 0;
      if (localCertificatePinner != null)
        i3 = this.certificatePinner.hashCode();
      return i2 + i3;
      j = 0;
      break;
      m = 0;
      break label112;
    }
  }

  public HostnameVerifier hostnameVerifier()
  {
    return this.hostnameVerifier;
  }

  public List<Protocol> protocols()
  {
    return this.protocols;
  }

  public Proxy proxy()
  {
    return this.proxy;
  }

  public Authenticator proxyAuthenticator()
  {
    return this.proxyAuthenticator;
  }

  public ProxySelector proxySelector()
  {
    return this.proxySelector;
  }

  public SocketFactory socketFactory()
  {
    return this.socketFactory;
  }

  public SSLSocketFactory sslSocketFactory()
  {
    return this.sslSocketFactory;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("Address{").append(this.url.host()).append(":").append(this.url.port());
    if (this.proxy != null)
      localStringBuilder.append(", proxy=").append(this.proxy);
    while (true)
    {
      localStringBuilder.append("}");
      return localStringBuilder.toString();
      localStringBuilder.append(", proxySelector=").append(this.proxySelector);
    }
  }

  public HttpUrl url()
  {
    return this.url;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Address
 * JD-Core Version:    0.6.0
 */