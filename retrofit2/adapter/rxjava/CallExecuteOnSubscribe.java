package retrofit2.adapter.rxjava;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;

final class CallExecuteOnSubscribe<T>
  implements Observable.OnSubscribe<Response<T>>
{
  private final Call<T> originalCall;

  CallExecuteOnSubscribe(Call<T> paramCall)
  {
    this.originalCall = paramCall;
  }

  public void call(Subscriber<? super Response<T>> paramSubscriber)
  {
    Call localCall = this.originalCall.clone();
    CallArbiter localCallArbiter = new CallArbiter(localCall, paramSubscriber);
    paramSubscriber.add(localCallArbiter);
    paramSubscriber.setProducer(localCallArbiter);
    try
    {
      Response localResponse = localCall.execute();
      localCallArbiter.emitResponse(localResponse);
      return;
    }
    catch (Throwable localThrowable)
    {
      Exceptions.throwIfFatal(localThrowable);
      localCallArbiter.emitError(localThrowable);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.adapter.rxjava.CallExecuteOnSubscribe
 * JD-Core Version:    0.6.0
 */