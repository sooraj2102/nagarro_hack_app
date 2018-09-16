package butterknife;

import android.support.annotation.IdRes;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ListenerClass(callbacks="Lbutterknife/OnPageChange$Callback;", remover="removeOnPageChangeListener", setter="addOnPageChangeListener", targetType="android.support.v4.view.ViewPager", type="android.support.v4.view.ViewPager.OnPageChangeListener")
@Retention(RetentionPolicy.CLASS)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface OnPageChange
{
  public abstract Callback callback();

  @IdRes
  public abstract int[] value();

  public static enum Callback
  {
    static
    {
      PAGE_SCROLLED = new Callback("PAGE_SCROLLED", 1);
      PAGE_SCROLL_STATE_CHANGED = new Callback("PAGE_SCROLL_STATE_CHANGED", 2);
      Callback[] arrayOfCallback = new Callback[3];
      arrayOfCallback[0] = PAGE_SELECTED;
      arrayOfCallback[1] = PAGE_SCROLLED;
      arrayOfCallback[2] = PAGE_SCROLL_STATE_CHANGED;
      $VALUES = arrayOfCallback;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     butterknife.OnPageChange
 * JD-Core Version:    0.6.0
 */