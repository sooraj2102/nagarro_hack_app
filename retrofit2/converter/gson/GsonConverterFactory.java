package retrofit2.converter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

public final class GsonConverterFactory extends Converter.Factory
{
  private final Gson gson;

  private GsonConverterFactory(Gson paramGson)
  {
    if (paramGson == null)
      throw new NullPointerException("gson == null");
    this.gson = paramGson;
  }

  public static GsonConverterFactory create()
  {
    return create(new Gson());
  }

  public static GsonConverterFactory create(Gson paramGson)
  {
    return new GsonConverterFactory(paramGson);
  }

  public Converter<?, RequestBody> requestBodyConverter(Type paramType, Annotation[] paramArrayOfAnnotation1, Annotation[] paramArrayOfAnnotation2, Retrofit paramRetrofit)
  {
    TypeAdapter localTypeAdapter = this.gson.getAdapter(TypeToken.get(paramType));
    return new GsonRequestBodyConverter(this.gson, localTypeAdapter);
  }

  public Converter<ResponseBody, ?> responseBodyConverter(Type paramType, Annotation[] paramArrayOfAnnotation, Retrofit paramRetrofit)
  {
    TypeAdapter localTypeAdapter = this.gson.getAdapter(TypeToken.get(paramType));
    return new GsonResponseBodyConverter(this.gson, localTypeAdapter);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.converter.gson.GsonConverterFactory
 * JD-Core Version:    0.6.0
 */