package android.support.v4.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;

public final class MimeTypeFilter
{
  public static String matches(@Nullable String paramString, @NonNull String[] paramArrayOfString)
  {
    String str;
    if (paramString == null)
    {
      str = null;
      return str;
    }
    String[] arrayOfString = paramString.split("/");
    int i = paramArrayOfString.length;
    for (int j = 0; ; j++)
    {
      if (j >= i)
        break label55;
      str = paramArrayOfString[j];
      if (mimeTypeAgainstFilter(arrayOfString, str.split("/")))
        break;
    }
    label55: return null;
  }

  public static String matches(@Nullable String[] paramArrayOfString, @NonNull String paramString)
  {
    String str;
    if (paramArrayOfString == null)
    {
      str = null;
      return str;
    }
    String[] arrayOfString = paramString.split("/");
    int i = paramArrayOfString.length;
    for (int j = 0; ; j++)
    {
      if (j >= i)
        break label55;
      str = paramArrayOfString[j];
      if (mimeTypeAgainstFilter(str.split("/"), arrayOfString))
        break;
    }
    label55: return null;
  }

  public static boolean matches(@Nullable String paramString1, @NonNull String paramString2)
  {
    if (paramString1 == null)
      return false;
    return mimeTypeAgainstFilter(paramString1.split("/"), paramString2.split("/"));
  }

  public static String[] matchesMany(@Nullable String[] paramArrayOfString, @NonNull String paramString)
  {
    int i = 0;
    if (paramArrayOfString == null)
      return new String[0];
    ArrayList localArrayList = new ArrayList();
    String[] arrayOfString = paramString.split("/");
    int j = paramArrayOfString.length;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      if (mimeTypeAgainstFilter(str.split("/"), arrayOfString))
        localArrayList.add(str);
      i++;
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }

  private static boolean mimeTypeAgainstFilter(@NonNull String[] paramArrayOfString1, @NonNull String[] paramArrayOfString2)
  {
    if (paramArrayOfString2.length != 2)
      throw new IllegalArgumentException("Ill-formatted MIME type filter. Must be type/subtype.");
    if ((paramArrayOfString2[0].isEmpty()) || (paramArrayOfString2[1].isEmpty()))
      throw new IllegalArgumentException("Ill-formatted MIME type filter. Type or subtype empty.");
    if (paramArrayOfString1.length != 2);
    do
      return false;
    while (((!"*".equals(paramArrayOfString2[0])) && (!paramArrayOfString2[0].equals(paramArrayOfString1[0]))) || ((!"*".equals(paramArrayOfString2[1])) && (!paramArrayOfString2[1].equals(paramArrayOfString1[1]))));
    return true;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.content.MimeTypeFilter
 * JD-Core Version:    0.6.0
 */