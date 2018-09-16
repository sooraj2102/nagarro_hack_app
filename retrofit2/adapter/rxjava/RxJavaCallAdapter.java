package retrofit2.adapter.rxjava;

import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;

final class RxJavaCallAdapter<R>
  implements CallAdapter<R, Object>
{
  private final boolean isAsync;
  private final boolean isBody;
  private final boolean isCompletable;
  private final boolean isResult;
  private final boolean isSingle;
  private final Type responseType;
  private final Scheduler scheduler;

  RxJavaCallAdapter(Type paramType, Scheduler paramScheduler, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    this.responseType = paramType;
    this.scheduler = paramScheduler;
    this.isAsync = paramBoolean1;
    this.isResult = paramBoolean2;
    this.isBody = paramBoolean3;
    this.isSingle = paramBoolean4;
    this.isCompletable = paramBoolean5;
  }

  public Object adapt(Call<R> paramCall)
  {
    Object localObject1;
    Object localObject2;
    label32: Object localObject3;
    if (this.isAsync)
    {
      localObject1 = new CallEnqueueOnSubscribe(paramCall);
      if (!this.isResult)
        break label85;
      localObject2 = new ResultOnSubscribe((Observable.OnSubscribe)localObject1);
      localObject3 = Observable.create((Observable.OnSubscribe)localObject2);
      if (this.scheduler != null)
        localObject3 = ((Observable)localObject3).subscribeOn(this.scheduler);
      if (!this.isSingle)
        break label109;
      localObject3 = ((Observable)localObject3).toSingle();
    }
    label85: label109: 
    do
    {
      return localObject3;
      localObject1 = new CallExecuteOnSubscribe(paramCall);
      break;
      if (this.isBody)
      {
        localObject2 = new BodyOnSubscribe((Observable.OnSubscribe)localObject1);
        break label32;
      }
      localObject2 = localObject1;
      break label32;
    }
    while (!this.isCompletable);
    return CompletableHelper.toCompletable((Observable)localObject3);
  }

  public Type responseType()
  {
    return this.responseType;
  }

  private static final class CompletableHelper
  {
    static Object toCompletable(Observable<?> paramObservable)
    {
      return paramObservable.toCompletable();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.adapter.rxjava.RxJavaCallAdapter
 * JD-Core Version:    0.6.0
 */