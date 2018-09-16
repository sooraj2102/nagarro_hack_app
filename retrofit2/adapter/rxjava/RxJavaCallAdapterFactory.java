package retrofit2.adapter.rxjava;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import retrofit2.CallAdapter;
import retrofit2.CallAdapter.Factory;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

public final class RxJavaCallAdapterFactory extends CallAdapter.Factory
{
  private final boolean isAsync;
  private final Scheduler scheduler;

  private RxJavaCallAdapterFactory(Scheduler paramScheduler, boolean paramBoolean)
  {
    this.scheduler = paramScheduler;
    this.isAsync = paramBoolean;
  }

  public static RxJavaCallAdapterFactory create()
  {
    return new RxJavaCallAdapterFactory(null, false);
  }

  public static RxJavaCallAdapterFactory createAsync()
  {
    return new RxJavaCallAdapterFactory(null, true);
  }

  public static RxJavaCallAdapterFactory createWithScheduler(Scheduler paramScheduler)
  {
    if (paramScheduler == null)
      throw new NullPointerException("scheduler == null");
    return new RxJavaCallAdapterFactory(paramScheduler, false);
  }

  public CallAdapter<?, ?> get(Type paramType, Annotation[] paramArrayOfAnnotation, Retrofit paramRetrofit)
  {
    Class localClass1 = getRawType(paramType);
    if (localClass1 == Single.class);
    boolean bool2;
    for (boolean bool1 = true; ; bool1 = false)
    {
      bool2 = "rx.Completable".equals(localClass1.getCanonicalName());
      if ((localClass1 == Observable.class) || (bool1) || (bool2))
        break;
      return null;
    }
    if (bool2)
      return new RxJavaCallAdapter(Void.class, this.scheduler, this.isAsync, false, true, false, true);
    boolean bool3 = false;
    boolean bool4 = false;
    if (!(paramType instanceof ParameterizedType))
    {
      if (bool1);
      for (String str = "Single"; ; str = "Observable")
        throw new IllegalStateException(str + " return type must be parameterized as " + str + "<Foo> or " + str + "<? extends Foo>");
    }
    Type localType1 = getParameterUpperBound(0, (ParameterizedType)paramType);
    Class localClass2 = getRawType(localType1);
    Type localType2;
    if (localClass2 == Response.class)
    {
      if (!(localType1 instanceof ParameterizedType))
        throw new IllegalStateException("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
      localType2 = getParameterUpperBound(0, (ParameterizedType)localType1);
    }
    while (true)
    {
      return new RxJavaCallAdapter(localType2, this.scheduler, this.isAsync, bool3, bool4, bool1, false);
      if (localClass2 == Result.class)
      {
        if (!(localType1 instanceof ParameterizedType))
          throw new IllegalStateException("Result must be parameterized as Result<Foo> or Result<? extends Foo>");
        localType2 = getParameterUpperBound(0, (ParameterizedType)localType1);
        bool3 = true;
        bool4 = false;
        continue;
      }
      localType2 = localType1;
      bool4 = true;
      bool3 = false;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
 * JD-Core Version:    0.6.0
 */