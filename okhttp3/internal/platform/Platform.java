package okhttp3.internal.platform;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal.tls.BasicCertificateChainCleaner;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.TrustRootIndex;
import okio.Buffer;

public class Platform
{
  public static final int INFO = 4;
  private static final Platform PLATFORM = findPlatform();
  public static final int WARN = 5;
  private static final Logger logger = Logger.getLogger(OkHttpClient.class.getName());

  public static List<String> alpnProtocolNames(List<Protocol> paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    int i = 0;
    int j = paramList.size();
    if (i < j)
    {
      Protocol localProtocol = (Protocol)paramList.get(i);
      if (localProtocol == Protocol.HTTP_1_0);
      while (true)
      {
        i++;
        break;
        localArrayList.add(localProtocol.toString());
      }
    }
    return localArrayList;
  }

  static byte[] concatLengthPrefixed(List<Protocol> paramList)
  {
    Buffer localBuffer = new Buffer();
    int i = 0;
    int j = paramList.size();
    if (i < j)
    {
      Protocol localProtocol = (Protocol)paramList.get(i);
      if (localProtocol == Protocol.HTTP_1_0);
      while (true)
      {
        i++;
        break;
        localBuffer.writeByte(localProtocol.toString().length());
        localBuffer.writeUtf8(localProtocol.toString());
      }
    }
    return localBuffer.readByteArray();
  }

  private static Platform findPlatform()
  {
    Platform localPlatform1 = AndroidPlatform.buildIfSupported();
    if (localPlatform1 != null)
      return localPlatform1;
    Jdk9Platform localJdk9Platform = Jdk9Platform.buildIfSupported();
    if (localJdk9Platform != null)
      return localJdk9Platform;
    Platform localPlatform2 = JdkWithJettyBootPlatform.buildIfSupported();
    if (localPlatform2 != null)
      return localPlatform2;
    return new Platform();
  }

  public static Platform get()
  {
    return PLATFORM;
  }

  static <T> T readFieldOrNull(Object paramObject, Class<T> paramClass, String paramString)
  {
    Class localClass = paramObject.getClass();
    Object localObject1;
    while (true)
      if (localClass != Object.class)
      {
        try
        {
          Field localField = localClass.getDeclaredField(paramString);
          localField.setAccessible(true);
          Object localObject3 = localField.get(paramObject);
          localObject1 = null;
          if (localObject3 == null)
            break;
          if (!paramClass.isInstance(localObject3))
            return null;
          Object localObject4 = paramClass.cast(localObject3);
          return localObject4;
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new AssertionError();
        }
        catch (NoSuchFieldException localNoSuchFieldException)
        {
          localClass = localClass.getSuperclass();
        }
        continue;
      }
      else
      {
        boolean bool = paramString.equals("delegate");
        localObject1 = null;
        if (bool)
          break;
        Object localObject2 = readFieldOrNull(paramObject, Object.class, "delegate");
        localObject1 = null;
        if (localObject2 == null)
          break;
        localObject1 = readFieldOrNull(localObject2, paramClass, paramString);
      }
    return localObject1;
  }

  public void afterHandshake(SSLSocket paramSSLSocket)
  {
  }

  public CertificateChainCleaner buildCertificateChainCleaner(X509TrustManager paramX509TrustManager)
  {
    return new BasicCertificateChainCleaner(TrustRootIndex.get(paramX509TrustManager));
  }

  public void configureTlsExtensions(SSLSocket paramSSLSocket, String paramString, List<Protocol> paramList)
  {
  }

  public void connectSocket(Socket paramSocket, InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    paramSocket.connect(paramInetSocketAddress, paramInt);
  }

  public String getPrefix()
  {
    return "OkHttp";
  }

  public String getSelectedProtocol(SSLSocket paramSSLSocket)
  {
    return null;
  }

  public Object getStackTraceForCloseable(String paramString)
  {
    if (logger.isLoggable(Level.FINE))
      return new Throwable(paramString);
    return null;
  }

  public boolean isCleartextTrafficPermitted(String paramString)
  {
    return true;
  }

  public void log(int paramInt, String paramString, Throwable paramThrowable)
  {
    if (paramInt == 5);
    for (Level localLevel = Level.WARNING; ; localLevel = Level.INFO)
    {
      logger.log(localLevel, paramString, paramThrowable);
      return;
    }
  }

  public void logCloseableLeak(String paramString, Object paramObject)
  {
    if (paramObject == null)
      paramString = paramString + " To see where this was allocated, set the OkHttpClient logger level to FINE: Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);";
    log(5, paramString, (Throwable)paramObject);
  }

  public X509TrustManager trustManager(SSLSocketFactory paramSSLSocketFactory)
  {
    try
    {
      Object localObject = readFieldOrNull(paramSSLSocketFactory, Class.forName("sun.security.ssl.SSLContextImpl"), "context");
      if (localObject == null)
        return null;
      X509TrustManager localX509TrustManager = (X509TrustManager)readFieldOrNull(localObject, X509TrustManager.class, "trustManager");
      return localX509TrustManager;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
    }
    return null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.platform.Platform
 * JD-Core Version:    0.6.0
 */