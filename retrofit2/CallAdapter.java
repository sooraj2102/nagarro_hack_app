package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract interface CallAdapter<R, T>
{
  public abstract T adapt(Call<R> paramCall);

  public abstract Type responseType();

  public static abstract class Factory
  {
    protected static Type getParameterUpperBound(int paramInt, ParameterizedType paramParameterizedType)
    {
      return Utils.getParameterUpperBound(paramInt, paramParameterizedType);
    }

    protected static Class<?> getRawType(Type paramType)
    {
      return Utils.getRawType(paramType);
    }

    public abstract CallAdapter<?, ?> get(Type paramType, Annotation[] paramArrayOfAnnotation, Retrofit paramRetrofit);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.CallAdapter
 * JD-Core Version:    0.6.0
 */