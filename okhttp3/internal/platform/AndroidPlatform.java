package okhttp3.internal.platform;

import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Protocol;
import okhttp3.internal.Util;
import okhttp3.internal.tls.CertificateChainCleaner;

class AndroidPlatform extends Platform
{
  private static final int MAX_LOG_LENGTH = 4000;
  private final CloseGuard closeGuard = CloseGuard.get();
  private final OptionalMethod<Socket> getAlpnSelectedProtocol;
  private final OptionalMethod<Socket> setAlpnProtocols;
  private final OptionalMethod<Socket> setHostname;
  private final OptionalMethod<Socket> setUseSessionTickets;
  private final Class<?> sslParametersClass;

  public AndroidPlatform(Class<?> paramClass, OptionalMethod<Socket> paramOptionalMethod1, OptionalMethod<Socket> paramOptionalMethod2, OptionalMethod<Socket> paramOptionalMethod3, OptionalMethod<Socket> paramOptionalMethod4)
  {
    this.sslParametersClass = paramClass;
    this.setUseSessionTickets = paramOptionalMethod1;
    this.setHostname = paramOptionalMethod2;
    this.getAlpnSelectedProtocol = paramOptionalMethod3;
    this.setAlpnProtocols = paramOptionalMethod4;
  }

  // ERROR //
  public static Platform buildIfSupported()
  {
    // Byte code:
    //   0: ldc 47
    //   2: invokestatic 53	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   5: astore 14
    //   7: aload 14
    //   9: astore_3
    //   10: iconst_1
    //   11: anewarray 49	java/lang/Class
    //   14: astore 4
    //   16: aload 4
    //   18: iconst_0
    //   19: getstatic 58	java/lang/Boolean:TYPE	Ljava/lang/Class;
    //   22: aastore
    //   23: new 60	okhttp3/internal/platform/OptionalMethod
    //   26: dup
    //   27: aconst_null
    //   28: ldc 61
    //   30: aload 4
    //   32: invokespecial 64	okhttp3/internal/platform/OptionalMethod:<init>	(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)V
    //   35: astore 5
    //   37: new 60	okhttp3/internal/platform/OptionalMethod
    //   40: dup
    //   41: aconst_null
    //   42: ldc 65
    //   44: iconst_1
    //   45: anewarray 49	java/lang/Class
    //   48: dup
    //   49: iconst_0
    //   50: ldc 67
    //   52: aastore
    //   53: invokespecial 64	okhttp3/internal/platform/OptionalMethod:<init>	(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)V
    //   56: astore 6
    //   58: ldc 69
    //   60: invokestatic 53	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   63: pop
    //   64: new 60	okhttp3/internal/platform/OptionalMethod
    //   67: dup
    //   68: ldc 71
    //   70: ldc 72
    //   72: iconst_0
    //   73: anewarray 49	java/lang/Class
    //   76: invokespecial 64	okhttp3/internal/platform/OptionalMethod:<init>	(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)V
    //   79: astore 11
    //   81: new 60	okhttp3/internal/platform/OptionalMethod
    //   84: dup
    //   85: aconst_null
    //   86: ldc 73
    //   88: iconst_1
    //   89: anewarray 49	java/lang/Class
    //   92: dup
    //   93: iconst_0
    //   94: ldc 71
    //   96: aastore
    //   97: invokespecial 64	okhttp3/internal/platform/OptionalMethod:<init>	(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)V
    //   100: astore 12
    //   102: aload 12
    //   104: astore 9
    //   106: aload 11
    //   108: astore 8
    //   110: new 2	okhttp3/internal/platform/AndroidPlatform
    //   113: dup
    //   114: aload_3
    //   115: aload 5
    //   117: aload 6
    //   119: aload 8
    //   121: aload 9
    //   123: invokespecial 75	okhttp3/internal/platform/AndroidPlatform:<init>	(Ljava/lang/Class;Lokhttp3/internal/platform/OptionalMethod;Lokhttp3/internal/platform/OptionalMethod;Lokhttp3/internal/platform/OptionalMethod;Lokhttp3/internal/platform/OptionalMethod;)V
    //   126: areturn
    //   127: astore_0
    //   128: ldc 77
    //   130: invokestatic 53	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   133: astore_2
    //   134: aload_2
    //   135: astore_3
    //   136: goto -126 -> 10
    //   139: astore_1
    //   140: aconst_null
    //   141: areturn
    //   142: astore 7
    //   144: aconst_null
    //   145: astore 8
    //   147: aconst_null
    //   148: astore 9
    //   150: goto -40 -> 110
    //   153: astore 13
    //   155: aload 11
    //   157: astore 8
    //   159: aconst_null
    //   160: astore 9
    //   162: goto -52 -> 110
    //
    // Exception table:
    //   from	to	target	type
    //   0	7	127	java/lang/ClassNotFoundException
    //   10	58	139	java/lang/ClassNotFoundException
    //   110	127	139	java/lang/ClassNotFoundException
    //   128	134	139	java/lang/ClassNotFoundException
    //   58	81	142	java/lang/ClassNotFoundException
    //   81	102	153	java/lang/ClassNotFoundException
  }

  public CertificateChainCleaner buildCertificateChainCleaner(X509TrustManager paramX509TrustManager)
  {
    try
    {
      Class localClass = Class.forName("android.net.http.X509TrustManagerExtensions");
      AndroidCertificateChainCleaner localAndroidCertificateChainCleaner = new AndroidCertificateChainCleaner(localClass.getConstructor(new Class[] { X509TrustManager.class }).newInstance(new Object[] { paramX509TrustManager }), localClass.getMethod("checkServerTrusted", new Class[] { [Ljava.security.cert.X509Certificate.class, String.class, String.class }));
      return localAndroidCertificateChainCleaner;
    }
    catch (Exception localException)
    {
    }
    return super.buildCertificateChainCleaner(paramX509TrustManager);
  }

  public void configureTlsExtensions(SSLSocket paramSSLSocket, String paramString, List<Protocol> paramList)
  {
    if (paramString != null)
    {
      OptionalMethod localOptionalMethod = this.setUseSessionTickets;
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = Boolean.valueOf(true);
      localOptionalMethod.invokeOptionalWithoutCheckedException(paramSSLSocket, arrayOfObject2);
      this.setHostname.invokeOptionalWithoutCheckedException(paramSSLSocket, new Object[] { paramString });
    }
    if ((this.setAlpnProtocols != null) && (this.setAlpnProtocols.isSupported(paramSSLSocket)))
    {
      Object[] arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = concatLengthPrefixed(paramList);
      this.setAlpnProtocols.invokeWithoutCheckedException(paramSSLSocket, arrayOfObject1);
    }
  }

  public void connectSocket(Socket paramSocket, InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    IOException localIOException;
    try
    {
      paramSocket.connect(paramInetSocketAddress, paramInt);
      return;
    }
    catch (AssertionError localAssertionError)
    {
      if (Util.isAndroidGetsocknameError(localAssertionError))
        throw new IOException(localAssertionError);
      throw localAssertionError;
    }
    catch (SecurityException localSecurityException)
    {
      localIOException = new IOException("Exception in connect");
      localIOException.initCause(localSecurityException);
    }
    throw localIOException;
  }

  public String getSelectedProtocol(SSLSocket paramSSLSocket)
  {
    if (this.getAlpnSelectedProtocol == null);
    do
      return null;
    while (!this.getAlpnSelectedProtocol.isSupported(paramSSLSocket));
    byte[] arrayOfByte = (byte[])(byte[])this.getAlpnSelectedProtocol.invokeWithoutCheckedException(paramSSLSocket, new Object[0]);
    if (arrayOfByte != null);
    for (String str = new String(arrayOfByte, Util.UTF_8); ; str = null)
      return str;
  }

  public Object getStackTraceForCloseable(String paramString)
  {
    return this.closeGuard.createAndOpen(paramString);
  }

  public boolean isCleartextTrafficPermitted(String paramString)
  {
    try
    {
      Class localClass = Class.forName("android.security.NetworkSecurityPolicy");
      Object localObject = localClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
      boolean bool = ((Boolean)localClass.getMethod("isCleartextTrafficPermitted", new Class[] { String.class }).invoke(localObject, new Object[] { paramString })).booleanValue();
      return bool;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      return super.isCleartextTrafficPermitted(paramString);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new AssertionError();
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      break label78;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      break label78;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      label70: label78: break label70;
    }
  }

  public void log(int paramInt, String paramString, Throwable paramThrowable)
  {
    int i = 5;
    int j;
    int k;
    label49: int m;
    if (paramInt == i)
    {
      if (paramThrowable != null)
        paramString = paramString + '\n' + Log.getStackTraceString(paramThrowable);
      j = 0;
      k = paramString.length();
      if (j >= k)
        return;
      m = paramString.indexOf('\n', j);
      if (m == -1)
        break label124;
    }
    while (true)
    {
      int n = Math.min(m, j + 4000);
      Log.println(i, "OkHttp", paramString.substring(j, n));
      j = n;
      if (j < m)
        continue;
      j++;
      break label49;
      i = 3;
      break;
      label124: m = k;
    }
  }

  public void logCloseableLeak(String paramString, Object paramObject)
  {
    if (!this.closeGuard.warnIfOpen(paramObject))
      log(5, paramString, null);
  }

  public X509TrustManager trustManager(SSLSocketFactory paramSSLSocketFactory)
  {
    Object localObject1 = readFieldOrNull(paramSSLSocketFactory, this.sslParametersClass, "sslParameters");
    if (localObject1 == null);
    try
    {
      Object localObject2 = readFieldOrNull(paramSSLSocketFactory, Class.forName("com.google.android.gms.org.conscrypt.SSLParametersImpl", false, paramSSLSocketFactory.getClass().getClassLoader()), "sslParameters");
      localObject1 = localObject2;
      X509TrustManager localX509TrustManager = (X509TrustManager)readFieldOrNull(localObject1, X509TrustManager.class, "x509TrustManager");
      if (localX509TrustManager != null)
        return localX509TrustManager;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      return super.trustManager(paramSSLSocketFactory);
    }
    return (X509TrustManager)readFieldOrNull(localObject1, X509TrustManager.class, "trustManager");
  }

  static final class AndroidCertificateChainCleaner extends CertificateChainCleaner
  {
    private final Method checkServerTrusted;
    private final Object x509TrustManagerExtensions;

    AndroidCertificateChainCleaner(Object paramObject, Method paramMethod)
    {
      this.x509TrustManagerExtensions = paramObject;
      this.checkServerTrusted = paramMethod;
    }

    public List<Certificate> clean(List<Certificate> paramList, String paramString)
      throws SSLPeerUnverifiedException
    {
      try
      {
        X509Certificate[] arrayOfX509Certificate = (X509Certificate[])paramList.toArray(new X509Certificate[paramList.size()]);
        List localList = (List)this.checkServerTrusted.invoke(this.x509TrustManagerExtensions, new Object[] { arrayOfX509Certificate, "RSA", paramString });
        return localList;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        SSLPeerUnverifiedException localSSLPeerUnverifiedException = new SSLPeerUnverifiedException(localInvocationTargetException.getMessage());
        localSSLPeerUnverifiedException.initCause(localInvocationTargetException);
        throw localSSLPeerUnverifiedException;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
      }
      throw new AssertionError(localIllegalAccessException);
    }

    public boolean equals(Object paramObject)
    {
      return paramObject instanceof AndroidCertificateChainCleaner;
    }

    public int hashCode()
    {
      return 0;
    }
  }

  static final class CloseGuard
  {
    private final Method getMethod;
    private final Method openMethod;
    private final Method warnIfOpenMethod;

    CloseGuard(Method paramMethod1, Method paramMethod2, Method paramMethod3)
    {
      this.getMethod = paramMethod1;
      this.openMethod = paramMethod2;
      this.warnIfOpenMethod = paramMethod3;
    }

    static CloseGuard get()
    {
      try
      {
        Class localClass = Class.forName("dalvik.system.CloseGuard");
        localMethod1 = localClass.getMethod("get", new Class[0]);
        localMethod2 = localClass.getMethod("open", new Class[] { String.class });
        Method localMethod4 = localClass.getMethod("warnIfOpen", new Class[0]);
        localMethod3 = localMethod4;
        return new CloseGuard(localMethod1, localMethod2, localMethod3);
      }
      catch (Exception localException)
      {
        while (true)
        {
          Method localMethod1 = null;
          Method localMethod2 = null;
          Method localMethod3 = null;
        }
      }
    }

    Object createAndOpen(String paramString)
    {
      if (this.getMethod != null)
        try
        {
          Object localObject = this.getMethod.invoke(null, new Object[0]);
          this.openMethod.invoke(localObject, new Object[] { paramString });
          return localObject;
        }
        catch (Exception localException)
        {
        }
      return null;
    }

    boolean warnIfOpen(Object paramObject)
    {
      int i = 0;
      if (paramObject != null);
      try
      {
        this.warnIfOpenMethod.invoke(paramObject, new Object[0]);
        i = 1;
        return i;
      }
      catch (Exception localException)
      {
      }
      return false;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.platform.AndroidPlatform
 * JD-Core Version:    0.6.0
 */