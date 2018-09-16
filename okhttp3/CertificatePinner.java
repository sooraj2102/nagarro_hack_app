package okhttp3;

import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.List<Lokhttp3.CertificatePinner.Pin;>;
import java.util.Set;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.internal.Util;
import okhttp3.internal.tls.CertificateChainCleaner;
import okio.ByteString;

public final class CertificatePinner
{
  public static final CertificatePinner DEFAULT = new Builder().build();
  private final CertificateChainCleaner certificateChainCleaner;
  private final Set<Pin> pins;

  CertificatePinner(Set<Pin> paramSet, CertificateChainCleaner paramCertificateChainCleaner)
  {
    this.pins = paramSet;
    this.certificateChainCleaner = paramCertificateChainCleaner;
  }

  public static String pin(Certificate paramCertificate)
  {
    if (!(paramCertificate instanceof X509Certificate))
      throw new IllegalArgumentException("Certificate pinning requires X509 certificates");
    return "sha256/" + sha256((X509Certificate)paramCertificate).base64();
  }

  static ByteString sha1(X509Certificate paramX509Certificate)
  {
    return ByteString.of(paramX509Certificate.getPublicKey().getEncoded()).sha1();
  }

  static ByteString sha256(X509Certificate paramX509Certificate)
  {
    return ByteString.of(paramX509Certificate.getPublicKey().getEncoded()).sha256();
  }

  public void check(String paramString, List<Certificate> paramList)
    throws SSLPeerUnverifiedException
  {
    List localList = findMatchingPins(paramString);
    if (localList.isEmpty())
      return;
    if (this.certificateChainCleaner != null)
      paramList = this.certificateChainCleaner.clean(paramList, paramString);
    int i = 0;
    int j = paramList.size();
    while (true)
    {
      if (i >= j)
        break label198;
      X509Certificate localX509Certificate2 = (X509Certificate)paramList.get(i);
      ByteString localByteString1 = null;
      ByteString localByteString2 = null;
      int i2 = 0;
      int i3 = localList.size();
      if (i2 < i3)
      {
        Pin localPin2 = (Pin)localList.get(i2);
        if (localPin2.hashAlgorithm.equals("sha256/"))
        {
          if (localByteString2 == null)
            localByteString2 = sha256(localX509Certificate2);
          if (localPin2.hash.equals(localByteString2))
            break;
        }
        do
        {
          i2++;
          break;
          if (!localPin2.hashAlgorithm.equals("sha1/"))
            break label184;
          if (localByteString1 != null)
            continue;
          localByteString1 = sha1(localX509Certificate2);
        }
        while (!localPin2.hash.equals(localByteString1));
        return;
        label184: throw new AssertionError();
      }
      i++;
    }
    label198: StringBuilder localStringBuilder = new StringBuilder().append("Certificate pinning failure!").append("\n  Peer certificate chain:");
    int k = 0;
    int m = paramList.size();
    while (k < m)
    {
      X509Certificate localX509Certificate1 = (X509Certificate)paramList.get(k);
      localStringBuilder.append("\n    ").append(pin(localX509Certificate1)).append(": ").append(localX509Certificate1.getSubjectDN().getName());
      k++;
    }
    localStringBuilder.append("\n  Pinned certificates for ").append(paramString).append(":");
    int n = 0;
    int i1 = localList.size();
    while (n < i1)
    {
      Pin localPin1 = (Pin)localList.get(n);
      localStringBuilder.append("\n    ").append(localPin1);
      n++;
    }
    throw new SSLPeerUnverifiedException(localStringBuilder.toString());
  }

  public void check(String paramString, Certificate[] paramArrayOfCertificate)
    throws SSLPeerUnverifiedException
  {
    check(paramString, Arrays.asList(paramArrayOfCertificate));
  }

  public boolean equals(Object paramObject)
  {
    if (paramObject == this)
      return true;
    if (((paramObject instanceof CertificatePinner)) && (Util.equal(this.certificateChainCleaner, ((CertificatePinner)paramObject).certificateChainCleaner)) && (this.pins.equals(((CertificatePinner)paramObject).pins)));
    for (int i = 1; ; i = 0)
      return i;
  }

  List<Pin> findMatchingPins(String paramString)
  {
    Object localObject = Collections.emptyList();
    Iterator localIterator = this.pins.iterator();
    while (localIterator.hasNext())
    {
      Pin localPin = (Pin)localIterator.next();
      if (!localPin.matches(paramString))
        continue;
      if (((List)localObject).isEmpty())
        localObject = new ArrayList();
      ((List)localObject).add(localPin);
    }
    return (List<Pin>)localObject;
  }

  public int hashCode()
  {
    if (this.certificateChainCleaner != null);
    for (int i = this.certificateChainCleaner.hashCode(); ; i = 0)
      return i * 31 + this.pins.hashCode();
  }

  CertificatePinner withCertificateChainCleaner(CertificateChainCleaner paramCertificateChainCleaner)
  {
    if (Util.equal(this.certificateChainCleaner, paramCertificateChainCleaner))
      return this;
    return new CertificatePinner(this.pins, paramCertificateChainCleaner);
  }

  public static final class Builder
  {
    private final List<CertificatePinner.Pin> pins = new ArrayList();

    public Builder add(String paramString, String[] paramArrayOfString)
    {
      if (paramString == null)
        throw new NullPointerException("pattern == null");
      int i = paramArrayOfString.length;
      for (int j = 0; j < i; j++)
      {
        String str = paramArrayOfString[j];
        this.pins.add(new CertificatePinner.Pin(paramString, str));
      }
      return this;
    }

    public CertificatePinner build()
    {
      return new CertificatePinner(new LinkedHashSet(this.pins), null);
    }
  }

  static final class Pin
  {
    private static final String WILDCARD = "*.";
    final String canonicalHostname;
    final ByteString hash;
    final String hashAlgorithm;
    final String pattern;

    Pin(String paramString1, String paramString2)
    {
      this.pattern = paramString1;
      String str;
      if (paramString1.startsWith("*."))
      {
        str = HttpUrl.parse("http://" + paramString1.substring("*.".length())).host();
        this.canonicalHostname = str;
        if (!paramString2.startsWith("sha1/"))
          break label151;
        this.hashAlgorithm = "sha1/";
      }
      for (this.hash = ByteString.decodeBase64(paramString2.substring("sha1/".length())); ; this.hash = ByteString.decodeBase64(paramString2.substring("sha256/".length())))
      {
        if (this.hash != null)
          return;
        throw new IllegalArgumentException("pins must be base64: " + paramString2);
        str = HttpUrl.parse("http://" + paramString1).host();
        break;
        label151: if (!paramString2.startsWith("sha256/"))
          break label185;
        this.hashAlgorithm = "sha256/";
      }
      label185: throw new IllegalArgumentException("pins must start with 'sha256/' or 'sha1/': " + paramString2);
    }

    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Pin)) && (this.pattern.equals(((Pin)paramObject).pattern)) && (this.hashAlgorithm.equals(((Pin)paramObject).hashAlgorithm)) && (this.hash.equals(((Pin)paramObject).hash));
    }

    public int hashCode()
    {
      return 31 * (31 * (527 + this.pattern.hashCode()) + this.hashAlgorithm.hashCode()) + this.hash.hashCode();
    }

    boolean matches(String paramString)
    {
      if (this.pattern.startsWith("*."))
        return paramString.regionMatches(false, 1 + paramString.indexOf('.'), this.canonicalHostname, 0, this.canonicalHostname.length());
      return paramString.equals(this.canonicalHostname);
    }

    public String toString()
    {
      return this.hashAlgorithm + this.hash.base64();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.CertificatePinner
 * JD-Core Version:    0.6.0
 */