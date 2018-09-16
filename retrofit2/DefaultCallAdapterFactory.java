package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

final class DefaultCallAdapterFactory extends CallAdapter.Factory
{
  static final CallAdapter.Factory INSTANCE = new DefaultCallAdapterFactory();

  public CallAdapter<?, ?> get(Type paramType, Annotation[] paramArrayOfAnnotation, Retrofit paramRetrofit)
  {
    if (getRawType(paramType) != Call.class)
      return null;
    return new CallAdapter(Utils.getCallResponseType(paramType))
    {
      public Call<Object> adapt(Call<Object> paramCall)
      {
        return paramCall;
      }

      public Type responseType()
      {
        return this.val$responseType;
      }
    };
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.DefaultCallAdapterFactory
 * JD-Core Version:    0.6.0
 */