package dubeyanurag.com.github.healthyrestaurant.appilication;

import android.app.Application;

public class MyApplication extends Application
{
  private static MyApplication context;

  public static MyApplication getAppContext()
  {
    return context;
  }

  public void onCreate()
  {
    super.onCreate();
    context = this;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.appilication.MyApplication
 * JD-Core Version:    0.6.0
 */