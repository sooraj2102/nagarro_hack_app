package android.support.v7.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.TintContextWrapper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.View.OnClickListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

class AppCompatViewInflater
{
  private static final String LOG_TAG = "AppCompatViewInflater";
  private static final String[] sClassPrefixList;
  private static final Map<String, Constructor<? extends View>> sConstructorMap;
  private static final Class<?>[] sConstructorSignature = { Context.class, AttributeSet.class };
  private static final int[] sOnClickAttrs = { 16843375 };
  private final Object[] mConstructorArgs = new Object[2];

  static
  {
    sClassPrefixList = new String[] { "android.widget.", "android.view.", "android.webkit." };
    sConstructorMap = new ArrayMap();
  }

  private void checkOnClickListener(View paramView, AttributeSet paramAttributeSet)
  {
    Context localContext = paramView.getContext();
    if ((!(localContext instanceof ContextWrapper)) || ((Build.VERSION.SDK_INT >= 15) && (!ViewCompat.hasOnClickListeners(paramView))))
      return;
    TypedArray localTypedArray = localContext.obtainStyledAttributes(paramAttributeSet, sOnClickAttrs);
    String str = localTypedArray.getString(0);
    if (str != null)
      paramView.setOnClickListener(new DeclaredOnClickListener(paramView, str));
    localTypedArray.recycle();
  }

  private View createView(Context paramContext, String paramString1, String paramString2)
    throws ClassNotFoundException, InflateException
  {
    Constructor localConstructor = (Constructor)sConstructorMap.get(paramString1);
    if (localConstructor == null);
    try
    {
      ClassLoader localClassLoader = paramContext.getClassLoader();
      if (paramString2 != null);
      for (String str = paramString2 + paramString1; ; str = paramString1)
      {
        localConstructor = localClassLoader.loadClass(str).asSubclass(View.class).getConstructor(sConstructorSignature);
        sConstructorMap.put(paramString1, localConstructor);
        localConstructor.setAccessible(true);
        View localView = (View)localConstructor.newInstance(this.mConstructorArgs);
        return localView;
      }
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  private View createViewFromTag(Context paramContext, String paramString, AttributeSet paramAttributeSet)
  {
    if (paramString.equals("view"))
      paramString = paramAttributeSet.getAttributeValue(null, "class");
    try
    {
      this.mConstructorArgs[0] = paramContext;
      this.mConstructorArgs[1] = paramAttributeSet;
      if (-1 == paramString.indexOf('.'))
      {
        for (int i = 0; i < sClassPrefixList.length; i++)
        {
          View localView1 = createView(paramContext, paramString, sClassPrefixList[i]);
          if (localView1 != null)
            return localView1;
        }
        return null;
      }
      View localView2 = createView(paramContext, paramString, null);
      return localView2;
    }
    catch (Exception localException)
    {
      return null;
    }
    finally
    {
      this.mConstructorArgs[0] = null;
      this.mConstructorArgs[1] = null;
    }
    throw localObject;
  }

  private static Context themifyContext(Context paramContext, AttributeSet paramAttributeSet, boolean paramBoolean1, boolean paramBoolean2)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.View, 0, 0);
    int i = 0;
    if (paramBoolean1)
      i = localTypedArray.getResourceId(R.styleable.View_android_theme, 0);
    if ((paramBoolean2) && (i == 0))
    {
      i = localTypedArray.getResourceId(R.styleable.View_theme, 0);
      if (i != 0)
        Log.i("AppCompatViewInflater", "app:theme is now deprecated. Please move to using android:theme instead.");
    }
    localTypedArray.recycle();
    if ((i != 0) && ((!(paramContext instanceof ContextThemeWrapper)) || (((ContextThemeWrapper)paramContext).getThemeResId() != i)))
      paramContext = new ContextThemeWrapper(paramContext, i);
    return paramContext;
  }

  public final View createView(View paramView, String paramString, @NonNull Context paramContext, @NonNull AttributeSet paramAttributeSet, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    Context localContext = paramContext;
    if ((paramBoolean1) && (paramView != null))
      paramContext = paramView.getContext();
    if ((paramBoolean2) || (paramBoolean3))
      paramContext = themifyContext(paramContext, paramAttributeSet, paramBoolean2, paramBoolean3);
    if (paramBoolean4)
      paramContext = TintContextWrapper.wrap(paramContext);
    int i = -1;
    Object localObject;
    switch (paramString.hashCode())
    {
    default:
      localObject = null;
      switch (i)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      }
    case -938935918:
    case 1125864064:
    case 2001146706:
    case 1666676343:
    case -339785223:
    case -937446323:
    case 1601505219:
    case 776382189:
    case -1455429095:
    case 1413872058:
    case -1346021293:
    case -1946472170:
    case -658531749:
    }
    while (true)
    {
      if ((localObject == null) && (localContext != paramContext))
        localObject = createViewFromTag(paramContext, paramString, paramAttributeSet);
      if (localObject != null)
        checkOnClickListener((View)localObject, paramAttributeSet);
      return localObject;
      if (!paramString.equals("TextView"))
        break;
      i = 0;
      break;
      if (!paramString.equals("ImageView"))
        break;
      i = 1;
      break;
      if (!paramString.equals("Button"))
        break;
      i = 2;
      break;
      if (!paramString.equals("EditText"))
        break;
      i = 3;
      break;
      if (!paramString.equals("Spinner"))
        break;
      i = 4;
      break;
      if (!paramString.equals("ImageButton"))
        break;
      i = 5;
      break;
      if (!paramString.equals("CheckBox"))
        break;
      i = 6;
      break;
      if (!paramString.equals("RadioButton"))
        break;
      i = 7;
      break;
      if (!paramString.equals("CheckedTextView"))
        break;
      i = 8;
      break;
      if (!paramString.equals("AutoCompleteTextView"))
        break;
      i = 9;
      break;
      if (!paramString.equals("MultiAutoCompleteTextView"))
        break;
      i = 10;
      break;
      if (!paramString.equals("RatingBar"))
        break;
      i = 11;
      break;
      if (!paramString.equals("SeekBar"))
        break;
      i = 12;
      break;
      localObject = new AppCompatTextView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatImageView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatButton(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatEditText(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatSpinner(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatImageButton(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatCheckBox(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatRadioButton(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatCheckedTextView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatAutoCompleteTextView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatMultiAutoCompleteTextView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatRatingBar(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatSeekBar(paramContext, paramAttributeSet);
    }
  }

  private static class DeclaredOnClickListener
    implements View.OnClickListener
  {
    private final View mHostView;
    private final String mMethodName;
    private Context mResolvedContext;
    private Method mResolvedMethod;

    public DeclaredOnClickListener(@NonNull View paramView, @NonNull String paramString)
    {
      this.mHostView = paramView;
      this.mMethodName = paramString;
    }

    @NonNull
    private void resolveMethod(@Nullable Context paramContext, @NonNull String paramString)
    {
      while (paramContext != null)
        try
        {
          if (!paramContext.isRestricted())
          {
            Method localMethod = paramContext.getClass().getMethod(this.mMethodName, new Class[] { View.class });
            if (localMethod != null)
            {
              this.mResolvedMethod = localMethod;
              this.mResolvedContext = paramContext;
              return;
            }
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          if ((paramContext instanceof ContextWrapper))
          {
            paramContext = ((ContextWrapper)paramContext).getBaseContext();
            continue;
          }
          paramContext = null;
        }
      int i = this.mHostView.getId();
      if (i == -1);
      for (String str = ""; ; str = " with id '" + this.mHostView.getContext().getResources().getResourceEntryName(i) + "'")
        throw new IllegalStateException("Could not find method " + this.mMethodName + "(View) in a parent or ancestor Context for android:onClick " + "attribute defined on view " + this.mHostView.getClass() + str);
    }

    public void onClick(@NonNull View paramView)
    {
      if (this.mResolvedMethod == null)
        resolveMethod(this.mHostView.getContext(), this.mMethodName);
      try
      {
        this.mResolvedMethod.invoke(this.mResolvedContext, new Object[] { paramView });
        return;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new IllegalStateException("Could not execute non-public method for android:onClick", localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
      }
      throw new IllegalStateException("Could not execute method for android:onClick", localInvocationTargetException);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.app.AppCompatViewInflater
 * JD-Core Version:    0.6.0
 */