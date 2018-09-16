package retrofit2.adapter.rxjava;

import retrofit2.Response;

@Deprecated
public final class HttpException extends retrofit2.HttpException
{
  public HttpException(Response<?> paramResponse)
  {
    super(paramResponse);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.adapter.rxjava.HttpException
 * JD-Core Version:    0.6.0
 */