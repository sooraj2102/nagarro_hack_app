package dubeyanurag.com.github.healthyrestaurant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DbHandler
{
  public static void clearDb(Context paramContext)
  {
    if (paramContext != null)
    {
      SharedPreferences.Editor localEditor = paramContext.getSharedPreferences("Res", 0).edit();
      localEditor.clear();
      localEditor.commit();
    }
  }

  public static Boolean contains(Context paramContext, String paramString)
  {
    if (paramContext != null)
      return Boolean.valueOf(paramContext.getSharedPreferences("Res", 0).contains(paramString));
    return null;
  }

  public static Boolean getBoolean(Context paramContext, String paramString, Boolean paramBoolean)
  {
    if (paramContext != null)
      return Boolean.valueOf(paramContext.getSharedPreferences("Res", 0).getBoolean(paramString, paramBoolean.booleanValue()));
    return Boolean.valueOf(false);
  }

  public static int getInt(Context paramContext, String paramString, int paramInt)
  {
    int i = 0;
    if (paramContext != null)
      i = paramContext.getSharedPreferences("Res", 0).getInt(paramString, paramInt);
    return i;
  }

  public static String getString(Context paramContext, String paramString1, String paramString2)
  {
    if (paramContext != null)
      return paramContext.getSharedPreferences("Res", 0).getString(paramString1, paramString2);
    return null;
  }

  public static void putBoolean(Context paramContext, String paramString, Boolean paramBoolean)
  {
    if (paramContext != null)
    {
      SharedPreferences.Editor localEditor = paramContext.getSharedPreferences("Res", 0).edit();
      localEditor.putBoolean(paramString, paramBoolean.booleanValue());
      localEditor.commit();
    }
  }

  public static void putInt(Context paramContext, String paramString, int paramInt)
  {
    if (paramContext != null)
    {
      SharedPreferences.Editor localEditor = paramContext.getSharedPreferences("Res", 0).edit();
      localEditor.putInt(paramString, paramInt);
      localEditor.commit();
    }
  }

  public static void putString(Context paramContext, String paramString1, String paramString2)
  {
    if (paramContext != null)
    {
      SharedPreferences.Editor localEditor = paramContext.getSharedPreferences("Res", 0).edit();
      localEditor.putString(paramString1, paramString2);
      localEditor.commit();
    }
  }

  public static void remove(Context paramContext, String paramString)
  {
    if (paramContext != null)
    {
      SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("Res", 0);
      if (contains(paramContext, paramString).booleanValue())
        localSharedPreferences.edit().remove(paramString).commit();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.utils.DbHandler
 * JD-Core Version:    0.6.0
 */