package dubeyanurag.com.github.healthyrestaurant.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils
{
  public static final Property<View, Integer> BACKGROUND_COLOR = new AnimUtils.IntProperty()
  {
    public Integer get(View paramView)
    {
      Drawable localDrawable = paramView.getBackground();
      if ((localDrawable instanceof ColorDrawable))
        return Integer.valueOf(((ColorDrawable)localDrawable).getColor());
      return Integer.valueOf(0);
    }

    public void setValue(View paramView, int paramInt)
    {
      paramView.setBackgroundColor(paramInt);
    }
  };

  public static int getContrastColor(int paramInt)
  {
    int i = Color.red(paramInt);
    int j = Color.green(paramInt);
    int k = Color.blue(paramInt);
    if (0.299D * i + (0.587D * j + 0.114D * k) > 186.0D)
      return -16777216;
    return -1;
  }

  public static String[] getLanguagesArray()
  {
    return new String[] { "All", "JavaScript", "Python", "PHP", "Java", "Go", "C++", "C", "HTML", "Ruby", "Rust", "CSS" };
  }

  public static void hideKeyboard(Activity paramActivity)
  {
    InputMethodManager localInputMethodManager = (InputMethodManager)paramActivity.getSystemService("input_method");
    View localView = paramActivity.getCurrentFocus();
    if (localView == null)
      localView = new View(paramActivity);
    localInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 0);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.utils.Utils
 * JD-Core Version:    0.6.0
 */