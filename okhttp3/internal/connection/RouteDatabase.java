package okhttp3.internal.connection;

import java.util.LinkedHashSet;
import java.util.Set;
import okhttp3.Route;

public final class RouteDatabase
{
  private final Set<Route> failedRoutes = new LinkedHashSet();

  public void connected(Route paramRoute)
  {
    monitorenter;
    try
    {
      this.failedRoutes.remove(paramRoute);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public void failed(Route paramRoute)
  {
    monitorenter;
    try
    {
      this.failedRoutes.add(paramRoute);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean shouldPostpone(Route paramRoute)
  {
    monitorenter;
    try
    {
      boolean bool = this.failedRoutes.contains(paramRoute);
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
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.connection.RouteDatabase
 * JD-Core Version:    0.6.0
 */