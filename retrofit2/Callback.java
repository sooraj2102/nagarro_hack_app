package retrofit2;

public abstract interface Callback<T>
{
  public abstract void onFailure(Call<T> paramCall, Throwable paramThrowable);

  public abstract void onResponse(Call<T> paramCall, Response<T> paramResponse);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.Callback
 * JD-Core Version:    0.6.0
 */