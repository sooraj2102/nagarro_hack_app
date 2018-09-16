package rx.plugins;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import rx.annotations.Experimental;

public class RxJavaPlugins
{
  static final RxJavaErrorHandler DEFAULT_ERROR_HANDLER;
  private static final RxJavaPlugins INSTANCE = new RxJavaPlugins();
  private final AtomicReference<RxJavaCompletableExecutionHook> completableExecutionHook = new AtomicReference();
  private final AtomicReference<RxJavaErrorHandler> errorHandler = new AtomicReference();
  private final AtomicReference<RxJavaObservableExecutionHook> observableExecutionHook = new AtomicReference();
  private final AtomicReference<RxJavaSchedulersHook> schedulersHook = new AtomicReference();
  private final AtomicReference<RxJavaSingleExecutionHook> singleExecutionHook = new AtomicReference();

  static
  {
    DEFAULT_ERROR_HANDLER = new RxJavaErrorHandler()
    {
    };
  }

  @Deprecated
  public static RxJavaPlugins getInstance()
  {
    return INSTANCE;
  }

  static Object getPluginImplementationViaProperty(Class<?> paramClass, Properties paramProperties)
  {
    Properties localProperties = (Properties)paramProperties.clone();
    String str1 = paramClass.getSimpleName();
    String str2 = localProperties.getProperty("rxjava.plugin." + str1 + ".implementation");
    if (str2 == null)
    {
      Iterator localIterator = localProperties.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str3 = localEntry.getKey().toString();
        if ((!str3.startsWith("rxjava.plugin.")) || (!str3.endsWith(".class")) || (!str1.equals(localEntry.getValue().toString())))
          continue;
        String str4 = str3.substring(0, str3.length() - ".class".length()).substring("rxjava.plugin.".length());
        String str5 = "rxjava.plugin." + str4 + ".impl";
        str2 = localProperties.getProperty(str5);
        if (str2 != null)
          break;
        throw new IllegalStateException("Implementing class declaration for " + str1 + " missing: " + str5);
      }
    }
    if (str2 != null)
      try
      {
        Object localObject = Class.forName(str2).asSubclass(paramClass).newInstance();
        return localObject;
      }
      catch (ClassCastException localClassCastException)
      {
        IllegalStateException localIllegalStateException4 = new IllegalStateException(str1 + " implementation is not an instance of " + str1 + ": " + str2, localClassCastException);
        throw localIllegalStateException4;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        IllegalStateException localIllegalStateException3 = new IllegalStateException(str1 + " implementation class not found: " + str2, localClassNotFoundException);
        throw localIllegalStateException3;
      }
      catch (InstantiationException localInstantiationException)
      {
        IllegalStateException localIllegalStateException2 = new IllegalStateException(str1 + " implementation not able to be instantiated: " + str2, localInstantiationException);
        throw localIllegalStateException2;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        IllegalStateException localIllegalStateException1 = new IllegalStateException(str1 + " implementation not able to be accessed: " + str2, localIllegalAccessException);
        throw localIllegalStateException1;
      }
    return null;
  }

  @Experimental
  public RxJavaCompletableExecutionHook getCompletableExecutionHook()
  {
    Object localObject;
    if (this.completableExecutionHook.get() == null)
    {
      localObject = getPluginImplementationViaProperty(RxJavaCompletableExecutionHook.class, System.getProperties());
      if (localObject != null)
        break label51;
      this.completableExecutionHook.compareAndSet(null, new RxJavaCompletableExecutionHook()
      {
      });
    }
    while (true)
    {
      return (RxJavaCompletableExecutionHook)this.completableExecutionHook.get();
      label51: this.completableExecutionHook.compareAndSet(null, (RxJavaCompletableExecutionHook)localObject);
    }
  }

  public RxJavaErrorHandler getErrorHandler()
  {
    Object localObject;
    if (this.errorHandler.get() == null)
    {
      localObject = getPluginImplementationViaProperty(RxJavaErrorHandler.class, System.getProperties());
      if (localObject != null)
        break label46;
      this.errorHandler.compareAndSet(null, DEFAULT_ERROR_HANDLER);
    }
    while (true)
    {
      return (RxJavaErrorHandler)this.errorHandler.get();
      label46: this.errorHandler.compareAndSet(null, (RxJavaErrorHandler)localObject);
    }
  }

  public RxJavaObservableExecutionHook getObservableExecutionHook()
  {
    Object localObject;
    if (this.observableExecutionHook.get() == null)
    {
      localObject = getPluginImplementationViaProperty(RxJavaObservableExecutionHook.class, System.getProperties());
      if (localObject != null)
        break label46;
      this.observableExecutionHook.compareAndSet(null, RxJavaObservableExecutionHookDefault.getInstance());
    }
    while (true)
    {
      return (RxJavaObservableExecutionHook)this.observableExecutionHook.get();
      label46: this.observableExecutionHook.compareAndSet(null, (RxJavaObservableExecutionHook)localObject);
    }
  }

  public RxJavaSchedulersHook getSchedulersHook()
  {
    Object localObject;
    if (this.schedulersHook.get() == null)
    {
      localObject = getPluginImplementationViaProperty(RxJavaSchedulersHook.class, System.getProperties());
      if (localObject != null)
        break label46;
      this.schedulersHook.compareAndSet(null, RxJavaSchedulersHook.getDefaultInstance());
    }
    while (true)
    {
      return (RxJavaSchedulersHook)this.schedulersHook.get();
      label46: this.schedulersHook.compareAndSet(null, (RxJavaSchedulersHook)localObject);
    }
  }

  public RxJavaSingleExecutionHook getSingleExecutionHook()
  {
    Object localObject;
    if (this.singleExecutionHook.get() == null)
    {
      localObject = getPluginImplementationViaProperty(RxJavaSingleExecutionHook.class, System.getProperties());
      if (localObject != null)
        break label46;
      this.singleExecutionHook.compareAndSet(null, RxJavaSingleExecutionHookDefault.getInstance());
    }
    while (true)
    {
      return (RxJavaSingleExecutionHook)this.singleExecutionHook.get();
      label46: this.singleExecutionHook.compareAndSet(null, (RxJavaSingleExecutionHook)localObject);
    }
  }

  @Experimental
  public void registerCompletableExecutionHook(RxJavaCompletableExecutionHook paramRxJavaCompletableExecutionHook)
  {
    if (!this.completableExecutionHook.compareAndSet(null, paramRxJavaCompletableExecutionHook))
      throw new IllegalStateException("Another strategy was already registered: " + this.singleExecutionHook.get());
  }

  public void registerErrorHandler(RxJavaErrorHandler paramRxJavaErrorHandler)
  {
    if (!this.errorHandler.compareAndSet(null, paramRxJavaErrorHandler))
      throw new IllegalStateException("Another strategy was already registered: " + this.errorHandler.get());
  }

  public void registerObservableExecutionHook(RxJavaObservableExecutionHook paramRxJavaObservableExecutionHook)
  {
    if (!this.observableExecutionHook.compareAndSet(null, paramRxJavaObservableExecutionHook))
      throw new IllegalStateException("Another strategy was already registered: " + this.observableExecutionHook.get());
  }

  public void registerSchedulersHook(RxJavaSchedulersHook paramRxJavaSchedulersHook)
  {
    if (!this.schedulersHook.compareAndSet(null, paramRxJavaSchedulersHook))
      throw new IllegalStateException("Another strategy was already registered: " + this.schedulersHook.get());
  }

  public void registerSingleExecutionHook(RxJavaSingleExecutionHook paramRxJavaSingleExecutionHook)
  {
    if (!this.singleExecutionHook.compareAndSet(null, paramRxJavaSingleExecutionHook))
      throw new IllegalStateException("Another strategy was already registered: " + this.singleExecutionHook.get());
  }

  @Experimental
  public void reset()
  {
    INSTANCE.errorHandler.set(null);
    INSTANCE.observableExecutionHook.set(null);
    INSTANCE.singleExecutionHook.set(null);
    INSTANCE.completableExecutionHook.set(null);
    INSTANCE.schedulersHook.set(null);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.plugins.RxJavaPlugins
 * JD-Core Version:    0.6.0
 */