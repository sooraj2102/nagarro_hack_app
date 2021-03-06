package android.support.v4.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FontRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.TypefaceCompat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public final class ResourcesCompat
{
  private static final String TAG = "ResourcesCompat";

  @ColorInt
  public static int getColor(@NonNull Resources paramResources, @ColorRes int paramInt, @Nullable Resources.Theme paramTheme)
    throws Resources.NotFoundException
  {
    if (Build.VERSION.SDK_INT >= 23)
      return paramResources.getColor(paramInt, paramTheme);
    return paramResources.getColor(paramInt);
  }

  @Nullable
  public static ColorStateList getColorStateList(@NonNull Resources paramResources, @ColorRes int paramInt, @Nullable Resources.Theme paramTheme)
    throws Resources.NotFoundException
  {
    if (Build.VERSION.SDK_INT >= 23)
      return paramResources.getColorStateList(paramInt, paramTheme);
    return paramResources.getColorStateList(paramInt);
  }

  @Nullable
  public static Drawable getDrawable(@NonNull Resources paramResources, @DrawableRes int paramInt, @Nullable Resources.Theme paramTheme)
    throws Resources.NotFoundException
  {
    if (Build.VERSION.SDK_INT >= 21)
      return paramResources.getDrawable(paramInt, paramTheme);
    return paramResources.getDrawable(paramInt);
  }

  @Nullable
  public static Drawable getDrawableForDensity(@NonNull Resources paramResources, @DrawableRes int paramInt1, int paramInt2, @Nullable Resources.Theme paramTheme)
    throws Resources.NotFoundException
  {
    if (Build.VERSION.SDK_INT >= 21)
      return paramResources.getDrawableForDensity(paramInt1, paramInt2, paramTheme);
    if (Build.VERSION.SDK_INT >= 15)
      return paramResources.getDrawableForDensity(paramInt1, paramInt2);
    return paramResources.getDrawable(paramInt1);
  }

  @Nullable
  public static Typeface getFont(@NonNull Context paramContext, @FontRes int paramInt)
    throws Resources.NotFoundException
  {
    if (paramContext.isRestricted())
      return null;
    return loadFont(paramContext, paramInt, new TypedValue(), 0, null);
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static Typeface getFont(@NonNull Context paramContext, @FontRes int paramInt1, TypedValue paramTypedValue, int paramInt2, @Nullable TextView paramTextView)
    throws Resources.NotFoundException
  {
    if (paramContext.isRestricted())
      return null;
    return loadFont(paramContext, paramInt1, paramTypedValue, paramInt2, paramTextView);
  }

  private static Typeface loadFont(@NonNull Context paramContext, int paramInt1, TypedValue paramTypedValue, int paramInt2, @Nullable TextView paramTextView)
  {
    Resources localResources = paramContext.getResources();
    localResources.getValue(paramInt1, paramTypedValue, true);
    Typeface localTypeface = loadFont(paramContext, localResources, paramTypedValue, paramInt1, paramInt2, paramTextView);
    if (localTypeface != null)
      return localTypeface;
    throw new Resources.NotFoundException("Font resource ID #0x" + Integer.toHexString(paramInt1));
  }

  private static Typeface loadFont(@NonNull Context paramContext, Resources paramResources, TypedValue paramTypedValue, int paramInt1, int paramInt2, @Nullable TextView paramTextView)
  {
    if (paramTypedValue.string == null)
      throw new Resources.NotFoundException("Resource \"" + paramResources.getResourceName(paramInt1) + "\" (" + Integer.toHexString(paramInt1) + ") is not a Font: " + paramTypedValue);
    String str = paramTypedValue.string.toString();
    Typeface localTypeface1;
    if (!str.startsWith("res/"))
      localTypeface1 = null;
    do
    {
      return localTypeface1;
      localTypeface1 = TypefaceCompat.findFromCache(paramResources, paramInt1, paramInt2);
    }
    while (localTypeface1 != null);
    try
    {
      if (str.toLowerCase().endsWith(".xml"))
      {
        FontResourcesParserCompat.FamilyResourceEntry localFamilyResourceEntry = FontResourcesParserCompat.parse(paramResources.getXml(paramInt1), paramResources);
        if (localFamilyResourceEntry == null)
        {
          Log.e("ResourcesCompat", "Failed to find font-family tag");
          return null;
        }
        return TypefaceCompat.createFromResourcesFamilyXml(paramContext, localFamilyResourceEntry, paramResources, paramInt1, paramInt2, paramTextView);
      }
      Typeface localTypeface2 = TypefaceCompat.createFromResourcesFontFile(paramContext, paramResources, paramInt1, str, paramInt2);
      return localTypeface2;
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      Log.e("ResourcesCompat", "Failed to parse xml resource " + str, localXmlPullParserException);
      return null;
    }
    catch (IOException localIOException)
    {
      while (true)
        Log.e("ResourcesCompat", "Failed to read xml resource " + str, localIOException);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.content.res.ResourcesCompat
 * JD-Core Version:    0.6.0
 */