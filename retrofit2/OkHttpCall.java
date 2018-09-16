package retrofit2;

import java.io.IOException;
import okhttp3.Call.Factory;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

final class OkHttpCall<T>
  implements Call<T>
{
  private final Object[] args;
  private volatile boolean canceled;
  private Throwable creationFailure;
  private boolean executed;
  private okhttp3.Call rawCall;
  private final ServiceMethod<T, ?> serviceMethod;

  OkHttpCall(ServiceMethod<T, ?> paramServiceMethod, Object[] paramArrayOfObject)
  {
    this.serviceMethod = paramServiceMethod;
    this.args = paramArrayOfObject;
  }

  private okhttp3.Call createRawCall()
    throws IOException
  {
    Request localRequest = this.serviceMethod.toRequest(this.args);
    okhttp3.Call localCall = this.serviceMethod.callFactory.newCall(localRequest);
    if (localCall == null)
      throw new NullPointerException("Call.Factory returned null.");
    return localCall;
  }

  public void cancel()
  {
    this.canceled = true;
    monitorenter;
    try
    {
      okhttp3.Call localCall = this.rawCall;
      monitorexit;
      if (localCall != null)
        localCall.cancel();
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public OkHttpCall<T> clone()
  {
    return new OkHttpCall(this.serviceMethod, this.args);
  }

  public void enqueue(Callback<T> paramCallback)
  {
    if (paramCallback == null)
      throw new NullPointerException("callback == null");
    monitorenter;
    try
    {
      if (this.executed)
        throw new IllegalStateException("Already executed.");
    }
    finally
    {
      monitorexit;
    }
    this.executed = true;
    Object localObject2 = this.rawCall;
    Object localObject3 = this.creationFailure;
    if ((localObject2 == null) && (localObject3 == null));
    try
    {
      okhttp3.Call localCall = createRawCall();
      this.rawCall = localCall;
      localObject2 = localCall;
      monitorexit;
      if (localObject3 != null)
      {
        paramCallback.onFailure(this, (Throwable)localObject3);
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      while (true)
      {
        this.creationFailure = localThrowable;
        localObject3 = localThrowable;
      }
      if (this.canceled)
        ((okhttp3.Call)localObject2).cancel();
      ((okhttp3.Call)localObject2).enqueue(new okhttp3.Callback(paramCallback)
      {
        private void callFailure(Throwable paramThrowable)
        {
          try
          {
            this.val$callback.onFailure(OkHttpCall.this, paramThrowable);
            return;
          }
          catch (Throwable localThrowable)
          {
            localThrowable.printStackTrace();
          }
        }

        private void callSuccess(Response<T> paramResponse)
        {
          try
          {
            this.val$callback.onResponse(OkHttpCall.this, paramResponse);
            return;
          }
          catch (Throwable localThrowable)
          {
            localThrowable.printStackTrace();
          }
        }

        public void onFailure(okhttp3.Call paramCall, IOException paramIOException)
        {
          try
          {
            this.val$callback.onFailure(OkHttpCall.this, paramIOException);
            return;
          }
          catch (Throwable localThrowable)
          {
            localThrowable.printStackTrace();
          }
        }

        public void onResponse(okhttp3.Call paramCall, okhttp3.Response paramResponse)
          throws IOException
        {
          try
          {
            Response localResponse = OkHttpCall.this.parseResponse(paramResponse);
            callSuccess(localResponse);
            return;
          }
          catch (Throwable localThrowable)
          {
            callFailure(localThrowable);
          }
        }
      });
    }
  }

  public Response<T> execute()
    throws IOException
  {
    monitorenter;
    try
    {
      if (this.executed)
        throw new IllegalStateException("Already executed.");
    }
    finally
    {
      monitorexit;
    }
    this.executed = true;
    if (this.creationFailure != null)
    {
      if ((this.creationFailure instanceof IOException))
        throw ((IOException)this.creationFailure);
      throw ((RuntimeException)this.creationFailure);
    }
    okhttp3.Call localCall = this.rawCall;
    if (localCall == null);
    try
    {
      localCall = createRawCall();
      this.rawCall = localCall;
      monitorexit;
      if (this.canceled)
        localCall.cancel();
      return parseResponse(localCall.execute());
    }
    catch (IOException localIOException)
    {
      localObject2 = localIOException;
      this.creationFailure = ((Throwable)localObject2);
      throw ((Throwable)localObject2);
    }
    catch (RuntimeException localRuntimeException)
    {
      while (true)
        Object localObject2 = localRuntimeException;
    }
  }

  public boolean isCanceled()
  {
    int i = 1;
    if (this.canceled)
      return i;
    monitorenter;
    while (true)
    {
      try
      {
        if ((this.rawCall != null) && (this.rawCall.isCanceled()))
          return i;
      }
      finally
      {
        monitorexit;
      }
      i = 0;
    }
  }

  public boolean isExecuted()
  {
    monitorenter;
    try
    {
      boolean bool = this.executed;
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  Response<T> parseResponse(okhttp3.Response paramResponse)
    throws IOException
  {
    ResponseBody localResponseBody = paramResponse.body();
    okhttp3.Response localResponse = paramResponse.newBuilder().body(new NoContentResponseBody(localResponseBody.contentType(), localResponseBody.contentLength())).build();
    int i = localResponse.code();
    if ((i < 200) || (i >= 300))
      try
      {
        Response localResponse1 = Response.error(Utils.buffer(localResponseBody), localResponse);
        return localResponse1;
      }
      finally
      {
        localResponseBody.close();
      }
    if ((i == 204) || (i == 205))
    {
      localResponseBody.close();
      return Response.success(null, localResponse);
    }
    ExceptionCatchingRequestBody localExceptionCatchingRequestBody = new ExceptionCatchingRequestBody(localResponseBody);
    try
    {
      Response localResponse2 = Response.success(this.serviceMethod.toResponse(localExceptionCatchingRequestBody), localResponse);
      return localResponse2;
    }
    catch (RuntimeException localRuntimeException)
    {
      localExceptionCatchingRequestBody.throwIfCaught();
    }
    throw localRuntimeException;
  }

  public Request request()
  {
    monitorenter;
    while (true)
    {
      Object localObject2;
      try
      {
        okhttp3.Call localCall1 = this.rawCall;
        if (localCall1 == null)
          continue;
        Request localRequest1 = localCall1.request();
        localObject2 = localRequest1;
        return localObject2;
        if (this.creationFailure == null)
          break label70;
        if ((this.creationFailure instanceof IOException))
          throw new RuntimeException("Unable to create request.", this.creationFailure);
      }
      finally
      {
        monitorexit;
      }
      throw ((RuntimeException)this.creationFailure);
      try
      {
        label70: okhttp3.Call localCall2 = createRawCall();
        this.rawCall = localCall2;
        Request localRequest2 = localCall2.request();
        localObject2 = localRequest2;
      }
      catch (RuntimeException localRuntimeException)
      {
        this.creationFailure = localRuntimeException;
        throw localRuntimeException;
      }
      catch (IOException localIOException)
      {
        this.creationFailure = localIOException;
      }
    }
    throw new RuntimeException("Unable to create request.", localIOException);
  }

  static final class ExceptionCatchingRequestBody extends ResponseBody
  {
    private final ResponseBody delegate;
    IOException thrownException;

    ExceptionCatchingRequestBody(ResponseBody paramResponseBody)
    {
      this.delegate = paramResponseBody;
    }

    public void close()
    {
      this.delegate.close();
    }

    public long contentLength()
    {
      return this.delegate.contentLength();
    }

    public MediaType contentType()
    {
      return this.delegate.contentType();
    }

    public BufferedSource source()
    {
      return Okio.buffer(new ForwardingSource(this.delegate.source())
      {
        public long read(Buffer paramBuffer, long paramLong)
          throws IOException
        {
          try
          {
            long l = super.read(paramBuffer, paramLong);
            return l;
          }
          catch (IOException localIOException)
          {
            OkHttpCall.ExceptionCatchingRequestBody.this.thrownException = localIOException;
          }
          throw localIOException;
        }
      });
    }

    void throwIfCaught()
      throws IOException
    {
      if (this.thrownException != null)
        throw this.thrownException;
    }
  }

  static final class NoContentResponseBody extends ResponseBody
  {
    private final long contentLength;
    private final MediaType contentType;

    NoContentResponseBody(MediaType paramMediaType, long paramLong)
    {
      this.contentType = paramMediaType;
      this.contentLength = paramLong;
    }

    public long contentLength()
    {
      return this.contentLength;
    }

    public MediaType contentType()
    {
      return this.contentType;
    }

    public BufferedSource source()
    {
      throw new IllegalStateException("Cannot read raw response body of a converted body.");
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.OkHttpCall
 * JD-Core Version:    0.6.0
 */