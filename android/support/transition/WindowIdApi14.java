package android.support.transition;

import android.os.IBinder;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class WindowIdApi14
  implements WindowIdImpl
{
  private final IBinder mToken;

  WindowIdApi14(IBinder paramIBinder)
  {
    this.mToken = paramIBinder;
  }

  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof WindowIdApi14)) && (((WindowIdApi14)paramObject).mToken.equals(this.mToken));
  }

  public int hashCode()
  {
    return this.mToken.hashCode();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.WindowIdApi14
 * JD-Core Version:    0.6.0
 */