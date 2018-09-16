package dubeyanurag.com.github.healthyrestaurant.networking;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient.Builder;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator
{
  public static String API_BASE_URL = "http://139.59.69.105:8000/api/";
  private static Retrofit.Builder builder;
  private static Retrofit.Builder builder2;
  private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(160L, TimeUnit.SECONDS).connectTimeout(160L, TimeUnit.SECONDS);

  static
  {
    builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());
  }

  public static <S> S createService(Class<S> paramClass)
  {
    return builder.client(httpClient.build()).build().create(paramClass);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.networking.ServiceGenerator
 * JD-Core Version:    0.6.0
 */