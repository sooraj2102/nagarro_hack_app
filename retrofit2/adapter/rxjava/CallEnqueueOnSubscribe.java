package retrofit2.adapter.rxjava;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;

final class CallEnqueueOnSubscribe<T>
  implements Observable.OnSubscribe<Response<T>>
{
  private final Call<T> originalCall;

  CallEnqueueOnSubscribe(Call<T> paramCall)
  {
    this.originalCall = paramCall;
  }

  public void call(Subscriber<? super Response<T>> paramSubscriber)
  {
    Call localCall = this.originalCall.clone();
    CallArbiter localCallArbiter = new CallArbiter(localCall, paramSubscriber);
    paramSubscriber.add(localCallArbiter);
    paramSubscriber.setProducer(localCallArbiter);
    localCall.enqueue(new Callback(localCallArbiter)
    {
      public void onFailure(Call<T> paramCall, Throwable paramThrowable)
      {
        Exceptions.throwIfFatal(paramThrowable);
        this.val$arbiter.emitError(paramThrowable);
      }

      public void onResponse(Call<T> paramCall, Response<T> paramResponse)
      {
        this.val$arbiter.emitResponse(paramResponse);
      }
    });
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.adapter.rxjava.CallEnqueueOnSubscribe
 * JD-Core Version:    0.6.0
 */