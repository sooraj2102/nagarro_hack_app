package retrofit2;

public class HttpException extends Exception
{
  private final int code;
  private final String message;
  private final transient Response<?> response;

  public HttpException(Response<?> paramResponse)
  {
    super(getMessage(paramResponse));
    this.code = paramResponse.code();
    this.message = paramResponse.message();
    this.response = paramResponse;
  }

  private static String getMessage(Response<?> paramResponse)
  {
    if (paramResponse == null)
      throw new NullPointerException("response == null");
    return "HTTP " + paramResponse.code() + " " + paramResponse.message();
  }

  public int code()
  {
    return this.code;
  }

  public String message()
  {
    return this.message;
  }

  public Response<?> response()
  {
    return this.response;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.HttpException
 * JD-Core Version:    0.6.0
 */