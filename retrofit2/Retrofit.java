package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import okhttp3.Call.Factory;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public final class Retrofit
{
  final List<CallAdapter.Factory> adapterFactories;
  final HttpUrl baseUrl;
  final Call.Factory callFactory;
  final Executor callbackExecutor;
  final List<Converter.Factory> converterFactories;
  private final Map<Method, ServiceMethod<?, ?>> serviceMethodCache = new ConcurrentHashMap();
  final boolean validateEagerly;

  Retrofit(Call.Factory paramFactory, HttpUrl paramHttpUrl, List<Converter.Factory> paramList, List<CallAdapter.Factory> paramList1, Executor paramExecutor, boolean paramBoolean)
  {
    this.callFactory = paramFactory;
    this.baseUrl = paramHttpUrl;
    this.converterFactories = Collections.unmodifiableList(paramList);
    this.adapterFactories = Collections.unmodifiableList(paramList1);
    this.callbackExecutor = paramExecutor;
    this.validateEagerly = paramBoolean;
  }

  private void eagerlyValidateMethods(Class<?> paramClass)
  {
    Platform localPlatform = Platform.get();
    for (Method localMethod : paramClass.getDeclaredMethods())
    {
      if (localPlatform.isDefaultMethod(localMethod))
        continue;
      loadServiceMethod(localMethod);
    }
  }

  public HttpUrl baseUrl()
  {
    return this.baseUrl;
  }

  public CallAdapter<?, ?> callAdapter(Type paramType, Annotation[] paramArrayOfAnnotation)
  {
    return nextCallAdapter(null, paramType, paramArrayOfAnnotation);
  }

  public List<CallAdapter.Factory> callAdapterFactories()
  {
    return this.adapterFactories;
  }

  public Call.Factory callFactory()
  {
    return this.callFactory;
  }

  public Executor callbackExecutor()
  {
    return this.callbackExecutor;
  }

  public List<Converter.Factory> converterFactories()
  {
    return this.converterFactories;
  }

  public <T> T create(Class<T> paramClass)
  {
    Utils.validateServiceInterface(paramClass);
    if (this.validateEagerly)
      eagerlyValidateMethods(paramClass);
    return Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, new InvocationHandler(paramClass)
    {
      private final Platform platform = Platform.get();

      public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
        throws Throwable
      {
        if (paramMethod.getDeclaringClass() == Object.class)
          return paramMethod.invoke(this, paramArrayOfObject);
        if (this.platform.isDefaultMethod(paramMethod))
          return this.platform.invokeDefaultMethod(paramMethod, this.val$service, paramObject, paramArrayOfObject);
        ServiceMethod localServiceMethod = Retrofit.this.loadServiceMethod(paramMethod);
        OkHttpCall localOkHttpCall = new OkHttpCall(localServiceMethod, paramArrayOfObject);
        return localServiceMethod.callAdapter.adapt(localOkHttpCall);
      }
    });
  }

  ServiceMethod<?, ?> loadServiceMethod(Method paramMethod)
  {
    ServiceMethod localServiceMethod1 = (ServiceMethod)this.serviceMethodCache.get(paramMethod);
    if (localServiceMethod1 != null)
      return localServiceMethod1;
    synchronized (this.serviceMethodCache)
    {
      ServiceMethod localServiceMethod2 = (ServiceMethod)this.serviceMethodCache.get(paramMethod);
      if (localServiceMethod2 == null)
      {
        localServiceMethod2 = new ServiceMethod.Builder(this, paramMethod).build();
        this.serviceMethodCache.put(paramMethod, localServiceMethod2);
      }
      return localServiceMethod2;
    }
  }

  public Builder newBuilder()
  {
    return new Builder(this);
  }

  public CallAdapter<?, ?> nextCallAdapter(CallAdapter.Factory paramFactory, Type paramType, Annotation[] paramArrayOfAnnotation)
  {
    Utils.checkNotNull(paramType, "returnType == null");
    Utils.checkNotNull(paramArrayOfAnnotation, "annotations == null");
    int i = 1 + this.adapterFactories.indexOf(paramFactory);
    int j = i;
    int k = this.adapterFactories.size();
    while (j < k)
    {
      CallAdapter localCallAdapter = ((CallAdapter.Factory)this.adapterFactories.get(j)).get(paramType, paramArrayOfAnnotation, this);
      if (localCallAdapter != null)
        return localCallAdapter;
      j++;
    }
    StringBuilder localStringBuilder = new StringBuilder("Could not locate call adapter for ").append(paramType).append(".\n");
    if (paramFactory != null)
    {
      localStringBuilder.append("  Skipped:");
      for (int i1 = 0; i1 < i; i1++)
        localStringBuilder.append("\n   * ").append(((CallAdapter.Factory)this.adapterFactories.get(i1)).getClass().getName());
      localStringBuilder.append('\n');
    }
    localStringBuilder.append("  Tried:");
    int m = i;
    int n = this.adapterFactories.size();
    while (m < n)
    {
      localStringBuilder.append("\n   * ").append(((CallAdapter.Factory)this.adapterFactories.get(m)).getClass().getName());
      m++;
    }
    throw new IllegalArgumentException(localStringBuilder.toString());
  }

  public <T> Converter<T, RequestBody> nextRequestBodyConverter(Converter.Factory paramFactory, Type paramType, Annotation[] paramArrayOfAnnotation1, Annotation[] paramArrayOfAnnotation2)
  {
    Utils.checkNotNull(paramType, "type == null");
    Utils.checkNotNull(paramArrayOfAnnotation1, "parameterAnnotations == null");
    Utils.checkNotNull(paramArrayOfAnnotation2, "methodAnnotations == null");
    int i = 1 + this.converterFactories.indexOf(paramFactory);
    int j = i;
    int k = this.converterFactories.size();
    while (j < k)
    {
      Converter localConverter = ((Converter.Factory)this.converterFactories.get(j)).requestBodyConverter(paramType, paramArrayOfAnnotation1, paramArrayOfAnnotation2, this);
      if (localConverter != null)
        return localConverter;
      j++;
    }
    StringBuilder localStringBuilder = new StringBuilder("Could not locate RequestBody converter for ").append(paramType).append(".\n");
    if (paramFactory != null)
    {
      localStringBuilder.append("  Skipped:");
      for (int i1 = 0; i1 < i; i1++)
        localStringBuilder.append("\n   * ").append(((Converter.Factory)this.converterFactories.get(i1)).getClass().getName());
      localStringBuilder.append('\n');
    }
    localStringBuilder.append("  Tried:");
    int m = i;
    int n = this.converterFactories.size();
    while (m < n)
    {
      localStringBuilder.append("\n   * ").append(((Converter.Factory)this.converterFactories.get(m)).getClass().getName());
      m++;
    }
    throw new IllegalArgumentException(localStringBuilder.toString());
  }

  public <T> Converter<ResponseBody, T> nextResponseBodyConverter(Converter.Factory paramFactory, Type paramType, Annotation[] paramArrayOfAnnotation)
  {
    Utils.checkNotNull(paramType, "type == null");
    Utils.checkNotNull(paramArrayOfAnnotation, "annotations == null");
    int i = 1 + this.converterFactories.indexOf(paramFactory);
    int j = i;
    int k = this.converterFactories.size();
    while (j < k)
    {
      Converter localConverter = ((Converter.Factory)this.converterFactories.get(j)).responseBodyConverter(paramType, paramArrayOfAnnotation, this);
      if (localConverter != null)
        return localConverter;
      j++;
    }
    StringBuilder localStringBuilder = new StringBuilder("Could not locate ResponseBody converter for ").append(paramType).append(".\n");
    if (paramFactory != null)
    {
      localStringBuilder.append("  Skipped:");
      for (int i1 = 0; i1 < i; i1++)
        localStringBuilder.append("\n   * ").append(((Converter.Factory)this.converterFactories.get(i1)).getClass().getName());
      localStringBuilder.append('\n');
    }
    localStringBuilder.append("  Tried:");
    int m = i;
    int n = this.converterFactories.size();
    while (m < n)
    {
      localStringBuilder.append("\n   * ").append(((Converter.Factory)this.converterFactories.get(m)).getClass().getName());
      m++;
    }
    throw new IllegalArgumentException(localStringBuilder.toString());
  }

  public <T> Converter<T, RequestBody> requestBodyConverter(Type paramType, Annotation[] paramArrayOfAnnotation1, Annotation[] paramArrayOfAnnotation2)
  {
    return nextRequestBodyConverter(null, paramType, paramArrayOfAnnotation1, paramArrayOfAnnotation2);
  }

  public <T> Converter<ResponseBody, T> responseBodyConverter(Type paramType, Annotation[] paramArrayOfAnnotation)
  {
    return nextResponseBodyConverter(null, paramType, paramArrayOfAnnotation);
  }

  public <T> Converter<T, String> stringConverter(Type paramType, Annotation[] paramArrayOfAnnotation)
  {
    Utils.checkNotNull(paramType, "type == null");
    Utils.checkNotNull(paramArrayOfAnnotation, "annotations == null");
    int i = 0;
    int j = this.converterFactories.size();
    while (i < j)
    {
      Converter localConverter = ((Converter.Factory)this.converterFactories.get(i)).stringConverter(paramType, paramArrayOfAnnotation, this);
      if (localConverter != null)
        return localConverter;
      i++;
    }
    return BuiltInConverters.ToStringConverter.INSTANCE;
  }

  public static final class Builder
  {
    private final List<CallAdapter.Factory> adapterFactories = new ArrayList();
    private HttpUrl baseUrl;
    private Call.Factory callFactory;
    private Executor callbackExecutor;
    private final List<Converter.Factory> converterFactories = new ArrayList();
    private final Platform platform;
    private boolean validateEagerly;

    public Builder()
    {
      this(Platform.get());
    }

    Builder(Platform paramPlatform)
    {
      this.platform = paramPlatform;
      this.converterFactories.add(new BuiltInConverters());
    }

    Builder(Retrofit paramRetrofit)
    {
      this.platform = Platform.get();
      this.callFactory = paramRetrofit.callFactory;
      this.baseUrl = paramRetrofit.baseUrl;
      this.converterFactories.addAll(paramRetrofit.converterFactories);
      this.adapterFactories.addAll(paramRetrofit.adapterFactories);
      this.adapterFactories.remove(-1 + this.adapterFactories.size());
      this.callbackExecutor = paramRetrofit.callbackExecutor;
      this.validateEagerly = paramRetrofit.validateEagerly;
    }

    public Builder addCallAdapterFactory(CallAdapter.Factory paramFactory)
    {
      this.adapterFactories.add(Utils.checkNotNull(paramFactory, "factory == null"));
      return this;
    }

    public Builder addConverterFactory(Converter.Factory paramFactory)
    {
      this.converterFactories.add(Utils.checkNotNull(paramFactory, "factory == null"));
      return this;
    }

    public Builder baseUrl(String paramString)
    {
      Utils.checkNotNull(paramString, "baseUrl == null");
      HttpUrl localHttpUrl = HttpUrl.parse(paramString);
      if (localHttpUrl == null)
        throw new IllegalArgumentException("Illegal URL: " + paramString);
      return baseUrl(localHttpUrl);
    }

    public Builder baseUrl(HttpUrl paramHttpUrl)
    {
      Utils.checkNotNull(paramHttpUrl, "baseUrl == null");
      List localList = paramHttpUrl.pathSegments();
      if (!"".equals(localList.get(-1 + localList.size())))
        throw new IllegalArgumentException("baseUrl must end in /: " + paramHttpUrl);
      this.baseUrl = paramHttpUrl;
      return this;
    }

    public Retrofit build()
    {
      if (this.baseUrl == null)
        throw new IllegalStateException("Base URL required.");
      Object localObject = this.callFactory;
      if (localObject == null)
        localObject = new OkHttpClient();
      Executor localExecutor = this.callbackExecutor;
      if (localExecutor == null)
        localExecutor = this.platform.defaultCallbackExecutor();
      ArrayList localArrayList1 = new ArrayList(this.adapterFactories);
      localArrayList1.add(this.platform.defaultCallAdapterFactory(localExecutor));
      ArrayList localArrayList2 = new ArrayList(this.converterFactories);
      return (Retrofit)new Retrofit((Call.Factory)localObject, this.baseUrl, localArrayList2, localArrayList1, localExecutor, this.validateEagerly);
    }

    public Builder callFactory(Call.Factory paramFactory)
    {
      this.callFactory = ((Call.Factory)Utils.checkNotNull(paramFactory, "factory == null"));
      return this;
    }

    public Builder callbackExecutor(Executor paramExecutor)
    {
      this.callbackExecutor = ((Executor)Utils.checkNotNull(paramExecutor, "executor == null"));
      return this;
    }

    public Builder client(OkHttpClient paramOkHttpClient)
    {
      return callFactory((Call.Factory)Utils.checkNotNull(paramOkHttpClient, "client == null"));
    }

    public Builder validateEagerly(boolean paramBoolean)
    {
      this.validateEagerly = paramBoolean;
      return this;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.Retrofit
 * JD-Core Version:    0.6.0
 */