package retrofit2.converter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T>
  implements Converter<ResponseBody, T>
{
  private final TypeAdapter<T> adapter;
  private final Gson gson;

  GsonResponseBodyConverter(Gson paramGson, TypeAdapter<T> paramTypeAdapter)
  {
    this.gson = paramGson;
    this.adapter = paramTypeAdapter;
  }

  public T convert(ResponseBody paramResponseBody)
    throws IOException
  {
    JsonReader localJsonReader = this.gson.newJsonReader(paramResponseBody.charStream());
    try
    {
      Object localObject2 = this.adapter.read(localJsonReader);
      return localObject2;
    }
    finally
    {
      paramResponseBody.close();
    }
    throw localObject1;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.converter.gson.GsonResponseBodyConverter
 * JD-Core Version:    0.6.0
 */