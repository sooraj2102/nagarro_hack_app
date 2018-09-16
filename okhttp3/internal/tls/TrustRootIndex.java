package okhttp3.internal.tls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PublicKey;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public abstract class TrustRootIndex
{
  public static TrustRootIndex get(X509TrustManager paramX509TrustManager)
  {
    try
    {
      Method localMethod = paramX509TrustManager.getClass().getDeclaredMethod("findTrustAnchorByIssuerAndSignature", new Class[] { X509Certificate.class });
      localMethod.setAccessible(true);
      AndroidTrustRootIndex localAndroidTrustRootIndex = new AndroidTrustRootIndex(paramX509TrustManager, localMethod);
      return localAndroidTrustRootIndex;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
    }
    return get(paramX509TrustManager.getAcceptedIssuers());
  }

  public static TrustRootIndex get(X509Certificate[] paramArrayOfX509Certificate)
  {
    return new BasicTrustRootIndex(paramArrayOfX509Certificate);
  }

  public abstract X509Certificate findByIssuerAndSignature(X509Certificate paramX509Certificate);

  static final class AndroidTrustRootIndex extends TrustRootIndex
  {
    private final Method findByIssuerAndSignatureMethod;
    private final X509TrustManager trustManager;

    AndroidTrustRootIndex(X509TrustManager paramX509TrustManager, Method paramMethod)
    {
      this.findByIssuerAndSignatureMethod = paramMethod;
      this.trustManager = paramX509TrustManager;
    }

    public boolean equals(Object paramObject)
    {
      if (paramObject == this);
      AndroidTrustRootIndex localAndroidTrustRootIndex;
      do
      {
        return true;
        if (!(paramObject instanceof AndroidTrustRootIndex))
          return false;
        localAndroidTrustRootIndex = (AndroidTrustRootIndex)paramObject;
      }
      while ((this.trustManager.equals(localAndroidTrustRootIndex.trustManager)) && (this.findByIssuerAndSignatureMethod.equals(localAndroidTrustRootIndex.findByIssuerAndSignatureMethod)));
      return false;
    }

    public X509Certificate findByIssuerAndSignature(X509Certificate paramX509Certificate)
    {
      try
      {
        TrustAnchor localTrustAnchor = (TrustAnchor)this.findByIssuerAndSignatureMethod.invoke(this.trustManager, new Object[] { paramX509Certificate });
        Object localObject = null;
        if (localTrustAnchor != null)
        {
          X509Certificate localX509Certificate = localTrustAnchor.getTrustedCert();
          localObject = localX509Certificate;
        }
        return localObject;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError();
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
      }
      return null;
    }

    public int hashCode()
    {
      return this.trustManager.hashCode() + 31 * this.findByIssuerAndSignatureMethod.hashCode();
    }
  }

  static final class BasicTrustRootIndex extends TrustRootIndex
  {
    private final Map<X500Principal, Set<X509Certificate>> subjectToCaCerts = new LinkedHashMap();

    public BasicTrustRootIndex(X509Certificate[] paramArrayOfX509Certificate)
    {
      int i = paramArrayOfX509Certificate.length;
      for (int j = 0; j < i; j++)
      {
        X509Certificate localX509Certificate = paramArrayOfX509Certificate[j];
        X500Principal localX500Principal = localX509Certificate.getSubjectX500Principal();
        Object localObject = (Set)this.subjectToCaCerts.get(localX500Principal);
        if (localObject == null)
        {
          localObject = new LinkedHashSet(1);
          this.subjectToCaCerts.put(localX500Principal, localObject);
        }
        ((Set)localObject).add(localX509Certificate);
      }
    }

    public boolean equals(Object paramObject)
    {
      if (paramObject == this);
      do
        return true;
      while (((paramObject instanceof BasicTrustRootIndex)) && (((BasicTrustRootIndex)paramObject).subjectToCaCerts.equals(this.subjectToCaCerts)));
      return false;
    }

    public X509Certificate findByIssuerAndSignature(X509Certificate paramX509Certificate)
    {
      X500Principal localX500Principal = paramX509Certificate.getIssuerX500Principal();
      Set localSet = (Set)this.subjectToCaCerts.get(localX500Principal);
      if (localSet == null)
        return null;
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        X509Certificate localX509Certificate = (X509Certificate)localIterator.next();
        PublicKey localPublicKey = localX509Certificate.getPublicKey();
        try
        {
          paramX509Certificate.verify(localPublicKey);
          return localX509Certificate;
        }
        catch (Exception localException)
        {
        }
      }
      return null;
    }

    public int hashCode()
    {
      return this.subjectToCaCerts.hashCode();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.tls.TrustRootIndex
 * JD-Core Version:    0.6.0
 */