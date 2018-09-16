package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Call.Factory;
import okhttp3.Headers.Builder;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody.Part;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.QueryName;
import retrofit2.http.Url;

final class ServiceMethod<R, T>
{
  static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
  static final Pattern PARAM_NAME_REGEX;
  static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{([a-zA-Z][a-zA-Z0-9_-]*)\\}");
  private final HttpUrl baseUrl;
  final CallAdapter<R, T> callAdapter;
  final Call.Factory callFactory;
  private final MediaType contentType;
  private final boolean hasBody;
  private final okhttp3.Headers headers;
  private final String httpMethod;
  private final boolean isFormEncoded;
  private final boolean isMultipart;
  private final ParameterHandler<?>[] parameterHandlers;
  private final String relativeUrl;
  private final Converter<ResponseBody, R> responseConverter;

  static
  {
    PARAM_NAME_REGEX = Pattern.compile("[a-zA-Z][a-zA-Z0-9_-]*");
  }

  ServiceMethod(Builder<R, T> paramBuilder)
  {
    this.callFactory = paramBuilder.retrofit.callFactory();
    this.callAdapter = paramBuilder.callAdapter;
    this.baseUrl = paramBuilder.retrofit.baseUrl();
    this.responseConverter = paramBuilder.responseConverter;
    this.httpMethod = paramBuilder.httpMethod;
    this.relativeUrl = paramBuilder.relativeUrl;
    this.headers = paramBuilder.headers;
    this.contentType = paramBuilder.contentType;
    this.hasBody = paramBuilder.hasBody;
    this.isFormEncoded = paramBuilder.isFormEncoded;
    this.isMultipart = paramBuilder.isMultipart;
    this.parameterHandlers = paramBuilder.parameterHandlers;
  }

  static Class<?> boxIfPrimitive(Class<?> paramClass)
  {
    if (Boolean.TYPE == paramClass)
      paramClass = Boolean.class;
    do
    {
      return paramClass;
      if (Byte.TYPE == paramClass)
        return Byte.class;
      if (Character.TYPE == paramClass)
        return Character.class;
      if (Double.TYPE == paramClass)
        return Double.class;
      if (Float.TYPE == paramClass)
        return Float.class;
      if (Integer.TYPE == paramClass)
        return Integer.class;
      if (Long.TYPE == paramClass)
        return Long.class;
    }
    while (Short.TYPE != paramClass);
    return Short.class;
  }

  static Set<String> parsePathParameters(String paramString)
  {
    Matcher localMatcher = PARAM_URL_REGEX.matcher(paramString);
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    while (localMatcher.find())
      localLinkedHashSet.add(localMatcher.group(1));
    return localLinkedHashSet;
  }

  Request toRequest(Object[] paramArrayOfObject)
    throws IOException
  {
    RequestBuilder localRequestBuilder = new RequestBuilder(this.httpMethod, this.baseUrl, this.relativeUrl, this.headers, this.contentType, this.hasBody, this.isFormEncoded, this.isMultipart);
    ParameterHandler[] arrayOfParameterHandler = this.parameterHandlers;
    if (paramArrayOfObject != null);
    for (int i = paramArrayOfObject.length; i != arrayOfParameterHandler.length; i = 0)
      throw new IllegalArgumentException("Argument count (" + i + ") doesn't match expected count (" + arrayOfParameterHandler.length + ")");
    for (int j = 0; j < i; j++)
      arrayOfParameterHandler[j].apply(localRequestBuilder, paramArrayOfObject[j]);
    return localRequestBuilder.build();
  }

  R toResponse(ResponseBody paramResponseBody)
    throws IOException
  {
    return this.responseConverter.convert(paramResponseBody);
  }

  static final class Builder<T, R>
  {
    CallAdapter<T, R> callAdapter;
    MediaType contentType;
    boolean gotBody;
    boolean gotField;
    boolean gotPart;
    boolean gotPath;
    boolean gotQuery;
    boolean gotUrl;
    boolean hasBody;
    okhttp3.Headers headers;
    String httpMethod;
    boolean isFormEncoded;
    boolean isMultipart;
    final Method method;
    final Annotation[] methodAnnotations;
    final Annotation[][] parameterAnnotationsArray;
    ParameterHandler<?>[] parameterHandlers;
    final Type[] parameterTypes;
    String relativeUrl;
    Set<String> relativeUrlParamNames;
    Converter<ResponseBody, T> responseConverter;
    Type responseType;
    final Retrofit retrofit;

    Builder(Retrofit paramRetrofit, Method paramMethod)
    {
      this.retrofit = paramRetrofit;
      this.method = paramMethod;
      this.methodAnnotations = paramMethod.getAnnotations();
      this.parameterTypes = paramMethod.getGenericParameterTypes();
      this.parameterAnnotationsArray = paramMethod.getParameterAnnotations();
    }

    private CallAdapter<T, R> createCallAdapter()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 55	retrofit2/ServiceMethod$Builder:method	Ljava/lang/reflect/Method;
      //   4: invokevirtual 83	java/lang/reflect/Method:getGenericReturnType	()Ljava/lang/reflect/Type;
      //   7: astore_1
      //   8: aload_1
      //   9: invokestatic 89	retrofit2/Utils:hasUnresolvableType	(Ljava/lang/reflect/Type;)Z
      //   12: ifeq +18 -> 30
      //   15: aload_0
      //   16: ldc 91
      //   18: iconst_1
      //   19: anewarray 5	java/lang/Object
      //   22: dup
      //   23: iconst_0
      //   24: aload_1
      //   25: aastore
      //   26: invokespecial 95	retrofit2/ServiceMethod$Builder:methodError	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/RuntimeException;
      //   29: athrow
      //   30: aload_1
      //   31: getstatic 101	java/lang/Void:TYPE	Ljava/lang/Class;
      //   34: if_acmpne +14 -> 48
      //   37: aload_0
      //   38: ldc 103
      //   40: iconst_0
      //   41: anewarray 5	java/lang/Object
      //   44: invokespecial 95	retrofit2/ServiceMethod$Builder:methodError	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/RuntimeException;
      //   47: athrow
      //   48: aload_0
      //   49: getfield 55	retrofit2/ServiceMethod$Builder:method	Ljava/lang/reflect/Method;
      //   52: invokevirtual 61	java/lang/reflect/Method:getAnnotations	()[Ljava/lang/annotation/Annotation;
      //   55: astore_2
      //   56: aload_0
      //   57: getfield 53	retrofit2/ServiceMethod$Builder:retrofit	Lretrofit2/Retrofit;
      //   60: aload_1
      //   61: aload_2
      //   62: invokevirtual 108	retrofit2/Retrofit:callAdapter	(Ljava/lang/reflect/Type;[Ljava/lang/annotation/Annotation;)Lretrofit2/CallAdapter;
      //   65: astore 4
      //   67: aload 4
      //   69: areturn
      //   70: astore_3
      //   71: aload_0
      //   72: aload_3
      //   73: ldc 110
      //   75: iconst_1
      //   76: anewarray 5	java/lang/Object
      //   79: dup
      //   80: iconst_0
      //   81: aload_1
      //   82: aastore
      //   83: invokespecial 113	retrofit2/ServiceMethod$Builder:methodError	(Ljava/lang/Throwable;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/RuntimeException;
      //   86: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   56	67	70	java/lang/RuntimeException
    }

    private Converter<ResponseBody, T> createResponseConverter()
    {
      Annotation[] arrayOfAnnotation = this.method.getAnnotations();
      Object[] arrayOfObject;
      try
      {
        Converter localConverter = this.retrofit.responseBodyConverter(this.responseType, arrayOfAnnotation);
        return localConverter;
      }
      catch (RuntimeException localRuntimeException)
      {
        arrayOfObject = new Object[1];
        arrayOfObject[0] = this.responseType;
      }
      throw methodError(localRuntimeException, "Unable to create converter for %s", arrayOfObject);
    }

    private RuntimeException methodError(String paramString, Object[] paramArrayOfObject)
    {
      return methodError(null, paramString, paramArrayOfObject);
    }

    private RuntimeException methodError(Throwable paramThrowable, String paramString, Object[] paramArrayOfObject)
    {
      String str = String.format(paramString, paramArrayOfObject);
      return new IllegalArgumentException(str + "\n    for method " + this.method.getDeclaringClass().getSimpleName() + "." + this.method.getName(), paramThrowable);
    }

    private RuntimeException parameterError(int paramInt, String paramString, Object[] paramArrayOfObject)
    {
      return methodError(paramString + " (parameter #" + (paramInt + 1) + ")", paramArrayOfObject);
    }

    private RuntimeException parameterError(Throwable paramThrowable, int paramInt, String paramString, Object[] paramArrayOfObject)
    {
      return methodError(paramThrowable, paramString + " (parameter #" + (paramInt + 1) + ")", paramArrayOfObject);
    }

    private okhttp3.Headers parseHeaders(String[] paramArrayOfString)
    {
      Headers.Builder localBuilder = new Headers.Builder();
      int i = paramArrayOfString.length;
      int j = 0;
      if (j < i)
      {
        String str1 = paramArrayOfString[j];
        int k = str1.indexOf(':');
        if ((k == -1) || (k == 0) || (k == -1 + str1.length()))
          throw methodError("@Headers value must be in the form \"Name: Value\". Found: \"%s\"", new Object[] { str1 });
        String str2 = str1.substring(0, k);
        String str3 = str1.substring(k + 1).trim();
        if ("Content-Type".equalsIgnoreCase(str2))
        {
          MediaType localMediaType = MediaType.parse(str3);
          if (localMediaType == null)
            throw methodError("Malformed content type: %s", new Object[] { str3 });
          this.contentType = localMediaType;
        }
        while (true)
        {
          j++;
          break;
          localBuilder.add(str2, str3);
        }
      }
      return localBuilder.build();
    }

    private void parseHttpMethodAndPath(String paramString1, String paramString2, boolean paramBoolean)
    {
      if (this.httpMethod != null)
      {
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = this.httpMethod;
        arrayOfObject[1] = paramString1;
        throw methodError("Only one HTTP method is allowed. Found: %s and %s.", arrayOfObject);
      }
      this.httpMethod = paramString1;
      this.hasBody = paramBoolean;
      if (paramString2.isEmpty())
        return;
      int i = paramString2.indexOf('?');
      if ((i != -1) && (i < -1 + paramString2.length()))
      {
        String str = paramString2.substring(i + 1);
        if (ServiceMethod.PARAM_URL_REGEX.matcher(str).find())
          throw methodError("URL query string \"%s\" must not have replace block. For dynamic query parameters use @Query.", new Object[] { str });
      }
      this.relativeUrl = paramString2;
      this.relativeUrlParamNames = ServiceMethod.parsePathParameters(paramString2);
    }

    private void parseMethodAnnotation(Annotation paramAnnotation)
    {
      if ((paramAnnotation instanceof DELETE))
        parseHttpMethodAndPath("DELETE", ((DELETE)paramAnnotation).value(), false);
      do
      {
        while (true)
        {
          return;
          if ((paramAnnotation instanceof GET))
          {
            parseHttpMethodAndPath("GET", ((GET)paramAnnotation).value(), false);
            return;
          }
          if (!(paramAnnotation instanceof HEAD))
            break;
          parseHttpMethodAndPath("HEAD", ((HEAD)paramAnnotation).value(), false);
          if (Void.class.equals(this.responseType))
            continue;
          throw methodError("HEAD method must use Void as response type.", new Object[0]);
        }
        if ((paramAnnotation instanceof PATCH))
        {
          parseHttpMethodAndPath("PATCH", ((PATCH)paramAnnotation).value(), true);
          return;
        }
        if ((paramAnnotation instanceof POST))
        {
          parseHttpMethodAndPath("POST", ((POST)paramAnnotation).value(), true);
          return;
        }
        if ((paramAnnotation instanceof PUT))
        {
          parseHttpMethodAndPath("PUT", ((PUT)paramAnnotation).value(), true);
          return;
        }
        if ((paramAnnotation instanceof OPTIONS))
        {
          parseHttpMethodAndPath("OPTIONS", ((OPTIONS)paramAnnotation).value(), false);
          return;
        }
        if ((paramAnnotation instanceof HTTP))
        {
          HTTP localHTTP = (HTTP)paramAnnotation;
          parseHttpMethodAndPath(localHTTP.method(), localHTTP.path(), localHTTP.hasBody());
          return;
        }
        if ((paramAnnotation instanceof retrofit2.http.Headers))
        {
          String[] arrayOfString = ((retrofit2.http.Headers)paramAnnotation).value();
          if (arrayOfString.length == 0)
            throw methodError("@Headers annotation is empty.", new Object[0]);
          this.headers = parseHeaders(arrayOfString);
          return;
        }
        if (!(paramAnnotation instanceof Multipart))
          continue;
        if (this.isFormEncoded)
          throw methodError("Only one encoding annotation is allowed.", new Object[0]);
        this.isMultipart = true;
        return;
      }
      while (!(paramAnnotation instanceof FormUrlEncoded));
      if (this.isMultipart)
        throw methodError("Only one encoding annotation is allowed.", new Object[0]);
      this.isFormEncoded = true;
    }

    private ParameterHandler<?> parseParameter(int paramInt, Type paramType, Annotation[] paramArrayOfAnnotation)
    {
      Object localObject = null;
      int i = paramArrayOfAnnotation.length;
      int j = 0;
      if (j < i)
      {
        ParameterHandler localParameterHandler = parseParameterAnnotation(paramInt, paramType, paramArrayOfAnnotation, paramArrayOfAnnotation[j]);
        if (localParameterHandler == null);
        while (true)
        {
          j++;
          break;
          if (localObject != null)
            throw parameterError(paramInt, "Multiple Retrofit annotations found, only one allowed.", new Object[0]);
          localObject = localParameterHandler;
        }
      }
      if (localObject == null)
        throw parameterError(paramInt, "No Retrofit annotation found.", new Object[0]);
      return localObject;
    }

    private ParameterHandler<?> parseParameterAnnotation(int paramInt, Type paramType, Annotation[] paramArrayOfAnnotation, Annotation paramAnnotation)
    {
      if ((paramAnnotation instanceof Url))
      {
        if (this.gotUrl)
          throw parameterError(paramInt, "Multiple @Url method annotations found.", new Object[0]);
        if (this.gotPath)
          throw parameterError(paramInt, "@Path parameters may not be used with @Url.", new Object[0]);
        if (this.gotQuery)
          throw parameterError(paramInt, "A @Url parameter must not come after a @Query", new Object[0]);
        if (this.relativeUrl != null)
        {
          Object[] arrayOfObject2 = new Object[1];
          arrayOfObject2[0] = this.httpMethod;
          throw parameterError(paramInt, "@Url cannot be used with @%s URL", arrayOfObject2);
        }
        this.gotUrl = true;
        if ((paramType == HttpUrl.class) || (paramType == String.class) || (paramType == URI.class) || (((paramType instanceof Class)) && ("android.net.Uri".equals(((Class)paramType).getName()))))
          return new ParameterHandler.RelativeUrl();
        throw parameterError(paramInt, "@Url must be okhttp3.HttpUrl, String, java.net.URI, or android.net.Uri type.", new Object[0]);
      }
      if ((paramAnnotation instanceof Path))
      {
        if (this.gotQuery)
          throw parameterError(paramInt, "A @Path parameter must not come after a @Query.", new Object[0]);
        if (this.gotUrl)
          throw parameterError(paramInt, "@Path parameters may not be used with @Url.", new Object[0]);
        if (this.relativeUrl == null)
        {
          Object[] arrayOfObject1 = new Object[1];
          arrayOfObject1[0] = this.httpMethod;
          throw parameterError(paramInt, "@Path can only be used with relative url on @%s", arrayOfObject1);
        }
        this.gotPath = true;
        Path localPath = (Path)paramAnnotation;
        String str5 = localPath.value();
        validatePathName(paramInt, str5);
        Converter localConverter21 = this.retrofit.stringConverter(paramType, paramArrayOfAnnotation);
        ParameterHandler.Path localPath1 = new ParameterHandler.Path(str5, localConverter21, localPath.encoded());
        return localPath1;
      }
      if ((paramAnnotation instanceof Query))
      {
        Query localQuery = (Query)paramAnnotation;
        String str4 = localQuery.value();
        boolean bool3 = localQuery.encoded();
        Class localClass13 = Utils.getRawType(paramType);
        this.gotQuery = true;
        if (Iterable.class.isAssignableFrom(localClass13))
        {
          if (!(paramType instanceof ParameterizedType))
            throw parameterError(paramInt, localClass13.getSimpleName() + " must include generic type (e.g., " + localClass13.getSimpleName() + "<String>)", new Object[0]);
          Type localType17 = Utils.getParameterUpperBound(0, (ParameterizedType)paramType);
          Converter localConverter20 = this.retrofit.stringConverter(localType17, paramArrayOfAnnotation);
          ParameterHandler.Query localQuery3 = new ParameterHandler.Query(str4, localConverter20, bool3);
          return localQuery3.iterable();
        }
        if (localClass13.isArray())
        {
          Class localClass14 = ServiceMethod.boxIfPrimitive(localClass13.getComponentType());
          Converter localConverter19 = this.retrofit.stringConverter(localClass14, paramArrayOfAnnotation);
          ParameterHandler.Query localQuery2 = new ParameterHandler.Query(str4, localConverter19, bool3);
          return localQuery2.array();
        }
        Converter localConverter18 = this.retrofit.stringConverter(paramType, paramArrayOfAnnotation);
        ParameterHandler.Query localQuery1 = new ParameterHandler.Query(str4, localConverter18, bool3);
        return localQuery1;
      }
      if ((paramAnnotation instanceof QueryName))
      {
        boolean bool2 = ((QueryName)paramAnnotation).encoded();
        Class localClass11 = Utils.getRawType(paramType);
        this.gotQuery = true;
        if (Iterable.class.isAssignableFrom(localClass11))
        {
          if (!(paramType instanceof ParameterizedType))
            throw parameterError(paramInt, localClass11.getSimpleName() + " must include generic type (e.g., " + localClass11.getSimpleName() + "<String>)", new Object[0]);
          Type localType16 = Utils.getParameterUpperBound(0, (ParameterizedType)paramType);
          Converter localConverter17 = this.retrofit.stringConverter(localType16, paramArrayOfAnnotation);
          ParameterHandler.QueryName localQueryName3 = new ParameterHandler.QueryName(localConverter17, bool2);
          return localQueryName3.iterable();
        }
        if (localClass11.isArray())
        {
          Class localClass12 = ServiceMethod.boxIfPrimitive(localClass11.getComponentType());
          Converter localConverter16 = this.retrofit.stringConverter(localClass12, paramArrayOfAnnotation);
          ParameterHandler.QueryName localQueryName2 = new ParameterHandler.QueryName(localConverter16, bool2);
          return localQueryName2.array();
        }
        Converter localConverter15 = this.retrofit.stringConverter(paramType, paramArrayOfAnnotation);
        ParameterHandler.QueryName localQueryName1 = new ParameterHandler.QueryName(localConverter15, bool2);
        return localQueryName1;
      }
      if ((paramAnnotation instanceof QueryMap))
      {
        Class localClass10 = Utils.getRawType(paramType);
        if (!Map.class.isAssignableFrom(localClass10))
          throw parameterError(paramInt, "@QueryMap parameter type must be Map.", new Object[0]);
        Type localType13 = Utils.getSupertype(paramType, localClass10, Map.class);
        if (!(localType13 instanceof ParameterizedType))
          throw parameterError(paramInt, "Map must include generic types (e.g., Map<String, String>)", new Object[0]);
        ParameterizedType localParameterizedType4 = (ParameterizedType)localType13;
        Type localType14 = Utils.getParameterUpperBound(0, localParameterizedType4);
        if (String.class != localType14)
          throw parameterError(paramInt, "@QueryMap keys must be of type String: " + localType14, new Object[0]);
        Type localType15 = Utils.getParameterUpperBound(1, localParameterizedType4);
        Converter localConverter14 = this.retrofit.stringConverter(localType15, paramArrayOfAnnotation);
        ParameterHandler.QueryMap localQueryMap = new ParameterHandler.QueryMap(localConverter14, ((QueryMap)paramAnnotation).encoded());
        return localQueryMap;
      }
      if ((paramAnnotation instanceof Header))
      {
        String str3 = ((Header)paramAnnotation).value();
        Class localClass8 = Utils.getRawType(paramType);
        if (Iterable.class.isAssignableFrom(localClass8))
        {
          if (!(paramType instanceof ParameterizedType))
            throw parameterError(paramInt, localClass8.getSimpleName() + " must include generic type (e.g., " + localClass8.getSimpleName() + "<String>)", new Object[0]);
          Type localType12 = Utils.getParameterUpperBound(0, (ParameterizedType)paramType);
          Converter localConverter13 = this.retrofit.stringConverter(localType12, paramArrayOfAnnotation);
          ParameterHandler.Header localHeader3 = new ParameterHandler.Header(str3, localConverter13);
          return localHeader3.iterable();
        }
        if (localClass8.isArray())
        {
          Class localClass9 = ServiceMethod.boxIfPrimitive(localClass8.getComponentType());
          Converter localConverter12 = this.retrofit.stringConverter(localClass9, paramArrayOfAnnotation);
          ParameterHandler.Header localHeader2 = new ParameterHandler.Header(str3, localConverter12);
          return localHeader2.array();
        }
        Converter localConverter11 = this.retrofit.stringConverter(paramType, paramArrayOfAnnotation);
        ParameterHandler.Header localHeader1 = new ParameterHandler.Header(str3, localConverter11);
        return localHeader1;
      }
      if ((paramAnnotation instanceof HeaderMap))
      {
        Class localClass7 = Utils.getRawType(paramType);
        if (!Map.class.isAssignableFrom(localClass7))
          throw parameterError(paramInt, "@HeaderMap parameter type must be Map.", new Object[0]);
        Type localType9 = Utils.getSupertype(paramType, localClass7, Map.class);
        if (!(localType9 instanceof ParameterizedType))
          throw parameterError(paramInt, "Map must include generic types (e.g., Map<String, String>)", new Object[0]);
        ParameterizedType localParameterizedType3 = (ParameterizedType)localType9;
        Type localType10 = Utils.getParameterUpperBound(0, localParameterizedType3);
        if (String.class != localType10)
          throw parameterError(paramInt, "@HeaderMap keys must be of type String: " + localType10, new Object[0]);
        Type localType11 = Utils.getParameterUpperBound(1, localParameterizedType3);
        Converter localConverter10 = this.retrofit.stringConverter(localType11, paramArrayOfAnnotation);
        ParameterHandler.HeaderMap localHeaderMap = new ParameterHandler.HeaderMap(localConverter10);
        return localHeaderMap;
      }
      if ((paramAnnotation instanceof Field))
      {
        if (!this.isFormEncoded)
          throw parameterError(paramInt, "@Field parameters can only be used with form encoding.", new Object[0]);
        Field localField = (Field)paramAnnotation;
        String str2 = localField.value();
        boolean bool1 = localField.encoded();
        this.gotField = true;
        Class localClass5 = Utils.getRawType(paramType);
        if (Iterable.class.isAssignableFrom(localClass5))
        {
          if (!(paramType instanceof ParameterizedType))
            throw parameterError(paramInt, localClass5.getSimpleName() + " must include generic type (e.g., " + localClass5.getSimpleName() + "<String>)", new Object[0]);
          Type localType8 = Utils.getParameterUpperBound(0, (ParameterizedType)paramType);
          Converter localConverter9 = this.retrofit.stringConverter(localType8, paramArrayOfAnnotation);
          ParameterHandler.Field localField3 = new ParameterHandler.Field(str2, localConverter9, bool1);
          return localField3.iterable();
        }
        if (localClass5.isArray())
        {
          Class localClass6 = ServiceMethod.boxIfPrimitive(localClass5.getComponentType());
          Converter localConverter8 = this.retrofit.stringConverter(localClass6, paramArrayOfAnnotation);
          ParameterHandler.Field localField2 = new ParameterHandler.Field(str2, localConverter8, bool1);
          return localField2.array();
        }
        Converter localConverter7 = this.retrofit.stringConverter(paramType, paramArrayOfAnnotation);
        ParameterHandler.Field localField1 = new ParameterHandler.Field(str2, localConverter7, bool1);
        return localField1;
      }
      if ((paramAnnotation instanceof FieldMap))
      {
        if (!this.isFormEncoded)
          throw parameterError(paramInt, "@FieldMap parameters can only be used with form encoding.", new Object[0]);
        Class localClass4 = Utils.getRawType(paramType);
        if (!Map.class.isAssignableFrom(localClass4))
          throw parameterError(paramInt, "@FieldMap parameter type must be Map.", new Object[0]);
        Type localType5 = Utils.getSupertype(paramType, localClass4, Map.class);
        if (!(localType5 instanceof ParameterizedType))
          throw parameterError(paramInt, "Map must include generic types (e.g., Map<String, String>)", new Object[0]);
        ParameterizedType localParameterizedType2 = (ParameterizedType)localType5;
        Type localType6 = Utils.getParameterUpperBound(0, localParameterizedType2);
        if (String.class != localType6)
          throw parameterError(paramInt, "@FieldMap keys must be of type String: " + localType6, new Object[0]);
        Type localType7 = Utils.getParameterUpperBound(1, localParameterizedType2);
        Converter localConverter6 = this.retrofit.stringConverter(localType7, paramArrayOfAnnotation);
        this.gotField = true;
        ParameterHandler.FieldMap localFieldMap = new ParameterHandler.FieldMap(localConverter6, ((FieldMap)paramAnnotation).encoded());
        return localFieldMap;
      }
      if ((paramAnnotation instanceof Part))
      {
        if (!this.isMultipart)
          throw parameterError(paramInt, "@Part parameters can only be used with multipart encoding.", new Object[0]);
        Part localPart = (Part)paramAnnotation;
        this.gotPart = true;
        String str1 = localPart.value();
        Class localClass2 = Utils.getRawType(paramType);
        if (str1.isEmpty())
        {
          if (Iterable.class.isAssignableFrom(localClass2))
          {
            if (!(paramType instanceof ParameterizedType))
              throw parameterError(paramInt, localClass2.getSimpleName() + " must include generic type (e.g., " + localClass2.getSimpleName() + "<String>)", new Object[0]);
            if (!MultipartBody.Part.class.isAssignableFrom(Utils.getRawType(Utils.getParameterUpperBound(0, (ParameterizedType)paramType))))
              throw parameterError(paramInt, "@Part annotation must supply a name or use MultipartBody.Part parameter type.", new Object[0]);
            return ParameterHandler.RawPart.INSTANCE.iterable();
          }
          if (localClass2.isArray())
          {
            if (!MultipartBody.Part.class.isAssignableFrom(localClass2.getComponentType()))
              throw parameterError(paramInt, "@Part annotation must supply a name or use MultipartBody.Part parameter type.", new Object[0]);
            return ParameterHandler.RawPart.INSTANCE.array();
          }
          if (MultipartBody.Part.class.isAssignableFrom(localClass2))
            return ParameterHandler.RawPart.INSTANCE;
          throw parameterError(paramInt, "@Part annotation must supply a name or use MultipartBody.Part parameter type.", new Object[0]);
        }
        String[] arrayOfString = new String[4];
        arrayOfString[0] = "Content-Disposition";
        arrayOfString[1] = ("form-data; name=\"" + str1 + "\"");
        arrayOfString[2] = "Content-Transfer-Encoding";
        arrayOfString[3] = localPart.encoding();
        okhttp3.Headers localHeaders = okhttp3.Headers.of(arrayOfString);
        if (Iterable.class.isAssignableFrom(localClass2))
        {
          if (!(paramType instanceof ParameterizedType))
            throw parameterError(paramInt, localClass2.getSimpleName() + " must include generic type (e.g., " + localClass2.getSimpleName() + "<String>)", new Object[0]);
          Type localType4 = Utils.getParameterUpperBound(0, (ParameterizedType)paramType);
          if (MultipartBody.Part.class.isAssignableFrom(Utils.getRawType(localType4)))
            throw parameterError(paramInt, "@Part parameters using the MultipartBody.Part must not include a part name in the annotation.", new Object[0]);
          Converter localConverter5 = this.retrofit.requestBodyConverter(localType4, paramArrayOfAnnotation, this.methodAnnotations);
          ParameterHandler.Part localPart3 = new ParameterHandler.Part(localHeaders, localConverter5);
          return localPart3.iterable();
        }
        if (localClass2.isArray())
        {
          Class localClass3 = ServiceMethod.boxIfPrimitive(localClass2.getComponentType());
          if (MultipartBody.Part.class.isAssignableFrom(localClass3))
            throw parameterError(paramInt, "@Part parameters using the MultipartBody.Part must not include a part name in the annotation.", new Object[0]);
          Converter localConverter4 = this.retrofit.requestBodyConverter(localClass3, paramArrayOfAnnotation, this.methodAnnotations);
          ParameterHandler.Part localPart2 = new ParameterHandler.Part(localHeaders, localConverter4);
          return localPart2.array();
        }
        if (MultipartBody.Part.class.isAssignableFrom(localClass2))
          throw parameterError(paramInt, "@Part parameters using the MultipartBody.Part must not include a part name in the annotation.", new Object[0]);
        Converter localConverter3 = this.retrofit.requestBodyConverter(paramType, paramArrayOfAnnotation, this.methodAnnotations);
        ParameterHandler.Part localPart1 = new ParameterHandler.Part(localHeaders, localConverter3);
        return localPart1;
      }
      if ((paramAnnotation instanceof PartMap))
      {
        if (!this.isMultipart)
          throw parameterError(paramInt, "@PartMap parameters can only be used with multipart encoding.", new Object[0]);
        this.gotPart = true;
        Class localClass1 = Utils.getRawType(paramType);
        if (!Map.class.isAssignableFrom(localClass1))
          throw parameterError(paramInt, "@PartMap parameter type must be Map.", new Object[0]);
        Type localType1 = Utils.getSupertype(paramType, localClass1, Map.class);
        if (!(localType1 instanceof ParameterizedType))
          throw parameterError(paramInt, "Map must include generic types (e.g., Map<String, String>)", new Object[0]);
        ParameterizedType localParameterizedType1 = (ParameterizedType)localType1;
        Type localType2 = Utils.getParameterUpperBound(0, localParameterizedType1);
        if (String.class != localType2)
          throw parameterError(paramInt, "@PartMap keys must be of type String: " + localType2, new Object[0]);
        Type localType3 = Utils.getParameterUpperBound(1, localParameterizedType1);
        if (MultipartBody.Part.class.isAssignableFrom(Utils.getRawType(localType3)))
          throw parameterError(paramInt, "@PartMap values cannot be MultipartBody.Part. Use @Part List<Part> or a different value type instead.", new Object[0]);
        Converter localConverter2 = this.retrofit.requestBodyConverter(localType3, paramArrayOfAnnotation, this.methodAnnotations);
        PartMap localPartMap = (PartMap)paramAnnotation;
        ParameterHandler.PartMap localPartMap1 = new ParameterHandler.PartMap(localConverter2, localPartMap.encoding());
        return localPartMap1;
      }
      if ((paramAnnotation instanceof Body))
      {
        if ((this.isFormEncoded) || (this.isMultipart))
          throw parameterError(paramInt, "@Body parameters cannot be used with form or multi-part encoding.", new Object[0]);
        if (this.gotBody)
          throw parameterError(paramInt, "Multiple @Body method annotations found.", new Object[0]);
        try
        {
          Converter localConverter1 = this.retrofit.requestBodyConverter(paramType, paramArrayOfAnnotation, this.methodAnnotations);
          this.gotBody = true;
          ParameterHandler.Body localBody = new ParameterHandler.Body(localConverter1);
          return localBody;
        }
        catch (RuntimeException localRuntimeException)
        {
          throw parameterError(localRuntimeException, paramInt, "Unable to create @Body converter for %s", new Object[] { paramType });
        }
      }
      return null;
    }

    private void validatePathName(int paramInt, String paramString)
    {
      if (!ServiceMethod.PARAM_NAME_REGEX.matcher(paramString).matches())
      {
        Object[] arrayOfObject2 = new Object[2];
        arrayOfObject2[0] = ServiceMethod.PARAM_URL_REGEX.pattern();
        arrayOfObject2[1] = paramString;
        throw parameterError(paramInt, "@Path parameter name must match %s. Found: %s", arrayOfObject2);
      }
      if (!this.relativeUrlParamNames.contains(paramString))
      {
        Object[] arrayOfObject1 = new Object[2];
        arrayOfObject1[0] = this.relativeUrl;
        arrayOfObject1[1] = paramString;
        throw parameterError(paramInt, "URL \"%s\" does not contain \"{%s}\".", arrayOfObject1);
      }
    }

    public ServiceMethod build()
    {
      this.callAdapter = createCallAdapter();
      this.responseType = this.callAdapter.responseType();
      if ((this.responseType == Response.class) || (this.responseType == okhttp3.Response.class))
        throw methodError("'" + Utils.getRawType(this.responseType).getName() + "' is not a valid response body type. Did you mean ResponseBody?", new Object[0]);
      this.responseConverter = createResponseConverter();
      Annotation[] arrayOfAnnotation1 = this.methodAnnotations;
      int i = arrayOfAnnotation1.length;
      for (int j = 0; j < i; j++)
        parseMethodAnnotation(arrayOfAnnotation1[j]);
      if (this.httpMethod == null)
        throw methodError("HTTP method annotation is required (e.g., @GET, @POST, etc.).", new Object[0]);
      if (!this.hasBody)
      {
        if (this.isMultipart)
          throw methodError("Multipart can only be specified on HTTP methods with request body (e.g., @POST).", new Object[0]);
        if (this.isFormEncoded)
          throw methodError("FormUrlEncoded can only be specified on HTTP methods with request body (e.g., @POST).", new Object[0]);
      }
      int k = this.parameterAnnotationsArray.length;
      this.parameterHandlers = new ParameterHandler[k];
      for (int m = 0; m < k; m++)
      {
        Type localType = this.parameterTypes[m];
        if (Utils.hasUnresolvableType(localType))
          throw parameterError(m, "Parameter type must not include a type variable or wildcard: %s", new Object[] { localType });
        Annotation[] arrayOfAnnotation2 = this.parameterAnnotationsArray[m];
        if (arrayOfAnnotation2 == null)
          throw parameterError(m, "No Retrofit annotation found.", new Object[0]);
        this.parameterHandlers[m] = parseParameter(m, localType, arrayOfAnnotation2);
      }
      if ((this.relativeUrl == null) && (!this.gotUrl))
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = this.httpMethod;
        throw methodError("Missing either @%s URL or @Url parameter.", arrayOfObject);
      }
      if ((!this.isFormEncoded) && (!this.isMultipart) && (!this.hasBody) && (this.gotBody))
        throw methodError("Non-body HTTP method cannot contain @Body.", new Object[0]);
      if ((this.isFormEncoded) && (!this.gotField))
        throw methodError("Form-encoded method must contain at least one @Field.", new Object[0]);
      if ((this.isMultipart) && (!this.gotPart))
        throw methodError("Multipart method must contain at least one @Part.", new Object[0]);
      return new ServiceMethod(this);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.ServiceMethod
 * JD-Core Version:    0.6.0
 */