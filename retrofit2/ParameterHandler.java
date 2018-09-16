package retrofit2;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import okhttp3.Headers;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;

abstract class ParameterHandler<T>
{
  abstract void apply(RequestBuilder paramRequestBuilder, T paramT)
    throws IOException;

  final ParameterHandler<Object> array()
  {
    return new ParameterHandler()
    {
      void apply(RequestBuilder paramRequestBuilder, Object paramObject)
        throws IOException
      {
        if (paramObject == null);
        while (true)
        {
          return;
          int i = 0;
          int j = Array.getLength(paramObject);
          while (i < j)
          {
            ParameterHandler.this.apply(paramRequestBuilder, Array.get(paramObject, i));
            i++;
          }
        }
      }
    };
  }

  final ParameterHandler<Iterable<T>> iterable()
  {
    return new ParameterHandler()
    {
      void apply(RequestBuilder paramRequestBuilder, Iterable<T> paramIterable)
        throws IOException
      {
        if (paramIterable == null);
        while (true)
        {
          return;
          Iterator localIterator = paramIterable.iterator();
          while (localIterator.hasNext())
          {
            Object localObject = localIterator.next();
            ParameterHandler.this.apply(paramRequestBuilder, localObject);
          }
        }
      }
    };
  }

  static final class Body<T> extends ParameterHandler<T>
  {
    private final Converter<T, RequestBody> converter;

    Body(Converter<T, RequestBody> paramConverter)
    {
      this.converter = paramConverter;
    }

    void apply(RequestBuilder paramRequestBuilder, T paramT)
    {
      if (paramT == null)
        throw new IllegalArgumentException("Body parameter value must not be null.");
      try
      {
        RequestBody localRequestBody = (RequestBody)this.converter.convert(paramT);
        paramRequestBuilder.setBody(localRequestBody);
        return;
      }
      catch (IOException localIOException)
      {
      }
      throw new RuntimeException("Unable to convert " + paramT + " to RequestBody", localIOException);
    }
  }

  static final class Field<T> extends ParameterHandler<T>
  {
    private final boolean encoded;
    private final String name;
    private final Converter<T, String> valueConverter;

    Field(String paramString, Converter<T, String> paramConverter, boolean paramBoolean)
    {
      this.name = ((String)Utils.checkNotNull(paramString, "name == null"));
      this.valueConverter = paramConverter;
      this.encoded = paramBoolean;
    }

    void apply(RequestBuilder paramRequestBuilder, T paramT)
      throws IOException
    {
      if (paramT == null)
        return;
      paramRequestBuilder.addFormField(this.name, (String)this.valueConverter.convert(paramT), this.encoded);
    }
  }

  static final class FieldMap<T> extends ParameterHandler<Map<String, T>>
  {
    private final boolean encoded;
    private final Converter<T, String> valueConverter;

    FieldMap(Converter<T, String> paramConverter, boolean paramBoolean)
    {
      this.valueConverter = paramConverter;
      this.encoded = paramBoolean;
    }

    void apply(RequestBuilder paramRequestBuilder, Map<String, T> paramMap)
      throws IOException
    {
      if (paramMap == null)
        throw new IllegalArgumentException("Field map was null.");
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str = (String)localEntry.getKey();
        if (str == null)
          throw new IllegalArgumentException("Field map contained null key.");
        Object localObject = localEntry.getValue();
        if (localObject == null)
          throw new IllegalArgumentException("Field map contained null value for key '" + str + "'.");
        paramRequestBuilder.addFormField(str, (String)this.valueConverter.convert(localObject), this.encoded);
      }
    }
  }

  static final class Header<T> extends ParameterHandler<T>
  {
    private final String name;
    private final Converter<T, String> valueConverter;

    Header(String paramString, Converter<T, String> paramConverter)
    {
      this.name = ((String)Utils.checkNotNull(paramString, "name == null"));
      this.valueConverter = paramConverter;
    }

    void apply(RequestBuilder paramRequestBuilder, T paramT)
      throws IOException
    {
      if (paramT == null)
        return;
      paramRequestBuilder.addHeader(this.name, (String)this.valueConverter.convert(paramT));
    }
  }

  static final class HeaderMap<T> extends ParameterHandler<Map<String, T>>
  {
    private final Converter<T, String> valueConverter;

    HeaderMap(Converter<T, String> paramConverter)
    {
      this.valueConverter = paramConverter;
    }

    void apply(RequestBuilder paramRequestBuilder, Map<String, T> paramMap)
      throws IOException
    {
      if (paramMap == null)
        throw new IllegalArgumentException("Header map was null.");
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str = (String)localEntry.getKey();
        if (str == null)
          throw new IllegalArgumentException("Header map contained null key.");
        Object localObject = localEntry.getValue();
        if (localObject == null)
          throw new IllegalArgumentException("Header map contained null value for key '" + str + "'.");
        paramRequestBuilder.addHeader(str, (String)this.valueConverter.convert(localObject));
      }
    }
  }

  static final class Part<T> extends ParameterHandler<T>
  {
    private final Converter<T, RequestBody> converter;
    private final Headers headers;

    Part(Headers paramHeaders, Converter<T, RequestBody> paramConverter)
    {
      this.headers = paramHeaders;
      this.converter = paramConverter;
    }

    void apply(RequestBuilder paramRequestBuilder, T paramT)
    {
      if (paramT == null)
        return;
      try
      {
        RequestBody localRequestBody = (RequestBody)this.converter.convert(paramT);
        paramRequestBuilder.addPart(this.headers, localRequestBody);
        return;
      }
      catch (IOException localIOException)
      {
      }
      throw new RuntimeException("Unable to convert " + paramT + " to RequestBody", localIOException);
    }
  }

  static final class PartMap<T> extends ParameterHandler<Map<String, T>>
  {
    private final String transferEncoding;
    private final Converter<T, RequestBody> valueConverter;

    PartMap(Converter<T, RequestBody> paramConverter, String paramString)
    {
      this.valueConverter = paramConverter;
      this.transferEncoding = paramString;
    }

    void apply(RequestBuilder paramRequestBuilder, Map<String, T> paramMap)
      throws IOException
    {
      if (paramMap == null)
        throw new IllegalArgumentException("Part map was null.");
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str = (String)localEntry.getKey();
        if (str == null)
          throw new IllegalArgumentException("Part map contained null key.");
        Object localObject = localEntry.getValue();
        if (localObject == null)
          throw new IllegalArgumentException("Part map contained null value for key '" + str + "'.");
        String[] arrayOfString = new String[4];
        arrayOfString[0] = "Content-Disposition";
        arrayOfString[1] = ("form-data; name=\"" + str + "\"");
        arrayOfString[2] = "Content-Transfer-Encoding";
        arrayOfString[3] = this.transferEncoding;
        paramRequestBuilder.addPart(Headers.of(arrayOfString), (RequestBody)this.valueConverter.convert(localObject));
      }
    }
  }

  static final class Path<T> extends ParameterHandler<T>
  {
    private final boolean encoded;
    private final String name;
    private final Converter<T, String> valueConverter;

    Path(String paramString, Converter<T, String> paramConverter, boolean paramBoolean)
    {
      this.name = ((String)Utils.checkNotNull(paramString, "name == null"));
      this.valueConverter = paramConverter;
      this.encoded = paramBoolean;
    }

    void apply(RequestBuilder paramRequestBuilder, T paramT)
      throws IOException
    {
      if (paramT == null)
        throw new IllegalArgumentException("Path parameter \"" + this.name + "\" value must not be null.");
      paramRequestBuilder.addPathParam(this.name, (String)this.valueConverter.convert(paramT), this.encoded);
    }
  }

  static final class Query<T> extends ParameterHandler<T>
  {
    private final boolean encoded;
    private final String name;
    private final Converter<T, String> valueConverter;

    Query(String paramString, Converter<T, String> paramConverter, boolean paramBoolean)
    {
      this.name = ((String)Utils.checkNotNull(paramString, "name == null"));
      this.valueConverter = paramConverter;
      this.encoded = paramBoolean;
    }

    void apply(RequestBuilder paramRequestBuilder, T paramT)
      throws IOException
    {
      if (paramT == null)
        return;
      paramRequestBuilder.addQueryParam(this.name, (String)this.valueConverter.convert(paramT), this.encoded);
    }
  }

  static final class QueryMap<T> extends ParameterHandler<Map<String, T>>
  {
    private final boolean encoded;
    private final Converter<T, String> valueConverter;

    QueryMap(Converter<T, String> paramConverter, boolean paramBoolean)
    {
      this.valueConverter = paramConverter;
      this.encoded = paramBoolean;
    }

    void apply(RequestBuilder paramRequestBuilder, Map<String, T> paramMap)
      throws IOException
    {
      if (paramMap == null)
        throw new IllegalArgumentException("Query map was null.");
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str = (String)localEntry.getKey();
        if (str == null)
          throw new IllegalArgumentException("Query map contained null key.");
        Object localObject = localEntry.getValue();
        if (localObject == null)
          throw new IllegalArgumentException("Query map contained null value for key '" + str + "'.");
        paramRequestBuilder.addQueryParam(str, (String)this.valueConverter.convert(localObject), this.encoded);
      }
    }
  }

  static final class QueryName<T> extends ParameterHandler<T>
  {
    private final boolean encoded;
    private final Converter<T, String> nameConverter;

    QueryName(Converter<T, String> paramConverter, boolean paramBoolean)
    {
      this.nameConverter = paramConverter;
      this.encoded = paramBoolean;
    }

    void apply(RequestBuilder paramRequestBuilder, T paramT)
      throws IOException
    {
      if (paramT == null)
        return;
      paramRequestBuilder.addQueryParam((String)this.nameConverter.convert(paramT), null, this.encoded);
    }
  }

  static final class RawPart extends ParameterHandler<MultipartBody.Part>
  {
    static final RawPart INSTANCE = new RawPart();

    void apply(RequestBuilder paramRequestBuilder, MultipartBody.Part paramPart)
      throws IOException
    {
      if (paramPart != null)
        paramRequestBuilder.addPart(paramPart);
    }
  }

  static final class RelativeUrl extends ParameterHandler<Object>
  {
    void apply(RequestBuilder paramRequestBuilder, Object paramObject)
    {
      paramRequestBuilder.setRelativeUrl(paramObject);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.ParameterHandler
 * JD-Core Version:    0.6.0
 */