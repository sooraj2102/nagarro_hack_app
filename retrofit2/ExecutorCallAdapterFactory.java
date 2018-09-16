package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import okhttp3.Request;

final class ExecutorCallAdapterFactory extends CallAdapter.Factory
{
  final Executor callbackExecutor;

  ExecutorCallAdapterFactory(Executor paramExecutor)
  {
    this.callbackExecutor = paramExecutor;
  }

  public CallAdapter<?, ?> get(Type paramType, Annotation[] paramArrayOfAnnotation, Retrofit paramRetrofit)
  {
    if (getRawType(paramType) != Call.class)
      return null;
    return new CallAdapter(Utils.getCallResponseType(paramType))
    {
      public Call<Object> adapt(Call<Object> paramCall)
      {
        return new ExecutorCallAdapterFactory.ExecutorCallbackCall(ExecutorCallAdapterFactory.this.callbackExecutor, paramCall);
      }

      public Type responseType()
      {
        return this.val$responseType;
      }
    };
  }

  static final class ExecutorCallbackCall<T>
    implements Call<T>
  {
    final Executor callbackExecutor;
    final Call<T> delegate;

    ExecutorCallbackCall(Executor paramExecutor, Call<T> paramCall)
    {
      this.callbackExecutor = paramExecutor;
      this.delegate = paramCall;
    }

    public void cancel()
    {
      this.delegate.cancel();
    }

    public Call<T> clone()
    {
      return new ExecutorCallbackCall(this.callbackExecutor, this.delegate.clone());
    }

    public void enqueue(Callback<T> paramCallback)
    {
      if (paramCallback == null)
        throw new NullPointerException("callback == null");
      this.delegate.enqueue(new Callback(paramCallback)
      {
        public void onFailure(Call<T> paramCall, Throwable paramThrowable)
        {
          ExecutorCallAdapterFactory.ExecutorCallbackCall.this.callbackExecutor.execute(new Runnable(paramThrowable)
          {
            public void run()
            {
              ExecutorCallAdapterFactory.ExecutorCallbackCall.1.this.val$callback.onFailure(ExecutorCallAdapterFactory.ExecutorCallbackCall.this, this.val$t);
            }
          });
        }

        public void onResponse(Call<T> paramCall, Response<T> paramResponse)
        {
          ExecutorCallAdapterFactory.ExecutorCallbackCall.this.callbackExecutor.execute(new Runnable(paramResponse)
          {
            public void run()
            {
              if (ExecutorCallAdapterFactory.ExecutorCallbackCall.this.delegate.isCanceled())
              {
                ExecutorCallAdapterFactory.ExecutorCallbackCall.1.this.val$callback.onFailure(ExecutorCallAdapterFactory.ExecutorCallbackCall.this, new IOException("Canceled"));
                return;
              }
              ExecutorCallAdapterFactory.ExecutorCallbackCall.1.this.val$callback.onResponse(ExecutorCallAdapterFactory.ExecutorCallbackCall.this, this.val$response);
            }
          });
        }
      });
    }

    public Response<T> execute()
      throws IOException
    {
      return this.delegate.execute();
    }

    public boolean isCanceled()
    {
      return this.delegate.isCanceled();
    }

    public boolean isExecuted()
    {
      return this.delegate.isExecuted();
    }

    public Request request()
    {
      return this.delegate.request();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.ExecutorCallAdapterFactory
 * JD-Core Version:    0.6.0
 */