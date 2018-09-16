package com.squareup.picasso;

public abstract interface Callback
{
  public abstract void onError();

  public abstract void onSuccess();

  public static class EmptyCallback
    implements Callback
  {
    public void onError()
    {
    }

    public void onSuccess()
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.Callback
 * JD-Core Version:    0.6.0
 */