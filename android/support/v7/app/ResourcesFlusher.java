package android.support.v7.app;

import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.LongSparseArray;
import java.lang.reflect.Field;
import java.util.Map;

class ResourcesFlusher
{
  private static final String TAG = "ResourcesFlusher";
  private static Field sDrawableCacheField;
  private static boolean sDrawableCacheFieldFetched;
  private static Field sResourcesImplField;
  private static boolean sResourcesImplFieldFetched;
  private static Class sThemedResourceCacheClazz;
  private static boolean sThemedResourceCacheClazzFetched;
  private static Field sThemedResourceCache_mUnthemedEntriesField;
  private static boolean sThemedResourceCache_mUnthemedEntriesFieldFetched;

  static boolean flush(@NonNull Resources paramResources)
  {
    if (Build.VERSION.SDK_INT >= 24)
      return flushNougats(paramResources);
    if (Build.VERSION.SDK_INT >= 23)
      return flushMarshmallows(paramResources);
    if (Build.VERSION.SDK_INT >= 21)
      return flushLollipops(paramResources);
    return false;
  }

  @RequiresApi(21)
  private static boolean flushLollipops(@NonNull Resources paramResources)
  {
    if (!sDrawableCacheFieldFetched);
    try
    {
      sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
      sDrawableCacheField.setAccessible(true);
      sDrawableCacheFieldFetched = true;
      if (sDrawableCacheField == null);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      try
      {
        localMap = (Map)sDrawableCacheField.get(paramResources);
        if (localMap != null)
        {
          localMap.clear();
          return true;
          localNoSuchFieldException = localNoSuchFieldException;
          Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", localNoSuchFieldException);
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        while (true)
        {
          Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", localIllegalAccessException);
          Map localMap = null;
        }
      }
    }
    return false;
  }

  @RequiresApi(23)
  private static boolean flushMarshmallows(@NonNull Resources paramResources)
  {
    boolean bool = true;
    if (!sDrawableCacheFieldFetched);
    Object localObject1;
    try
    {
      sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
      sDrawableCacheField.setAccessible(true);
      sDrawableCacheFieldFetched = bool;
      Field localField = sDrawableCacheField;
      localObject1 = null;
      if (localField == null);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      try
      {
        Object localObject2 = sDrawableCacheField.get(paramResources);
        localObject1 = localObject2;
        if (localObject1 == null)
        {
          return false;
          localNoSuchFieldException = localNoSuchFieldException;
          Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", localNoSuchFieldException);
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        while (true)
        {
          Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", localIllegalAccessException);
          localObject1 = null;
        }
        if (localObject1 == null)
          break label102;
      }
    }
    if (flushThemedResourcesCache(localObject1));
    while (true)
    {
      return bool;
      label102: bool = false;
    }
  }

  @RequiresApi(24)
  private static boolean flushNougats(@NonNull Resources paramResources)
  {
    boolean bool = true;
    if (!sResourcesImplFieldFetched);
    while (true)
    {
      try
      {
        sResourcesImplField = Resources.class.getDeclaredField("mResourcesImpl");
        sResourcesImplField.setAccessible(true);
        sResourcesImplFieldFetched = bool;
        if (sResourcesImplField == null)
          return false;
      }
      catch (NoSuchFieldException localNoSuchFieldException2)
      {
        Log.e("ResourcesFlusher", "Could not retrieve Resources#mResourcesImpl field", localNoSuchFieldException2);
        continue;
      }
      try
      {
        Object localObject4 = sResourcesImplField.get(paramResources);
        localObject1 = localObject4;
        if (localObject1 == null)
          continue;
        if (sDrawableCacheFieldFetched);
      }
      catch (NoSuchFieldException localNoSuchFieldException1)
      {
        try
        {
          sDrawableCacheField = localObject1.getClass().getDeclaredField("mDrawableCache");
          sDrawableCacheField.setAccessible(true);
          sDrawableCacheFieldFetched = bool;
          Field localField = sDrawableCacheField;
          localObject2 = null;
          if (localField == null);
        }
        catch (NoSuchFieldException localNoSuchFieldException1)
        {
          try
          {
            while (true)
            {
              Object localObject3 = sDrawableCacheField.get(localObject1);
              localObject2 = localObject3;
              if ((localObject2 == null) || (!flushThemedResourcesCache(localObject2)))
                break;
              return bool;
              localIllegalAccessException1 = localIllegalAccessException1;
              Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mResourcesImpl", localIllegalAccessException1);
              Object localObject1 = null;
            }
            localNoSuchFieldException1 = localNoSuchFieldException1;
            Log.e("ResourcesFlusher", "Could not retrieve ResourcesImpl#mDrawableCache field", localNoSuchFieldException1);
          }
          catch (IllegalAccessException localIllegalAccessException2)
          {
            while (true)
            {
              Log.e("ResourcesFlusher", "Could not retrieve value from ResourcesImpl#mDrawableCache", localIllegalAccessException2);
              Object localObject2 = null;
              continue;
              bool = false;
            }
          }
        }
      }
    }
  }

  @RequiresApi(16)
  private static boolean flushThemedResourcesCache(@NonNull Object paramObject)
  {
    if (!sThemedResourceCacheClazzFetched);
    try
    {
      sThemedResourceCacheClazz = Class.forName("android.content.res.ThemedResourceCache");
      sThemedResourceCacheClazzFetched = true;
      if (sThemedResourceCacheClazz == null)
        return false;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      while (true)
        Log.e("ResourcesFlusher", "Could not find ThemedResourceCache class", localClassNotFoundException);
      if (!sThemedResourceCache_mUnthemedEntriesFieldFetched);
      try
      {
        sThemedResourceCache_mUnthemedEntriesField = sThemedResourceCacheClazz.getDeclaredField("mUnthemedEntries");
        sThemedResourceCache_mUnthemedEntriesField.setAccessible(true);
        sThemedResourceCache_mUnthemedEntriesFieldFetched = true;
        if (sThemedResourceCache_mUnthemedEntriesField == null)
          return false;
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        while (true)
          Log.e("ResourcesFlusher", "Could not retrieve ThemedResourceCache#mUnthemedEntries field", localNoSuchFieldException);
        try
        {
          localLongSparseArray = (LongSparseArray)sThemedResourceCache_mUnthemedEntriesField.get(paramObject);
          if (localLongSparseArray != null)
          {
            localLongSparseArray.clear();
            return true;
          }
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          while (true)
          {
            Log.e("ResourcesFlusher", "Could not retrieve value from ThemedResourceCache#mUnthemedEntries", localIllegalAccessException);
            LongSparseArray localLongSparseArray = null;
          }
        }
      }
    }
    return false;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.app.ResourcesFlusher
 * JD-Core Version:    0.6.0
 */