package retrofit2.adapter.rxjava;

import retrofit2.Response;

public final class Result<T>
{
  private final Throwable error;
  private final Response<T> response;

  private Result(Response<T> paramResponse, Throwable paramThrowable)
  {
    this.response = paramResponse;
    this.error = paramThrowable;
  }

  public static <T> Result<T> error(Throwable paramThrowable)
  {
    if (paramThrowable == null)
      throw new NullPointerException("error == null");
    return new Result(null, paramThrowable);
  }

  public static <T> Result<T> response(Response<T> paramResponse)
  {
    if (paramResponse == null)
      throw new NullPointerException("response == null");
    return new Result(paramResponse, null);
  }

  public Throwable error()
  {
    return this.error;
  }

  public boolean isError()
  {
    return this.error != null;
  }

  public Response<T> response()
  {
    return this.response;
  }

  public String toString()
  {
    if (this.error != null)
      return "Result{isError=true, error=\"" + this.error + "\"}";
    return "Result{isError=false, response=" + this.response + '}';
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.adapter.rxjava.Result
 * JD-Core Version:    0.6.0
 */