package butterknife;

import android.support.annotation.RestrictTo;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface BindFont
{
  @TypefaceStyle
  public abstract int style();

  public abstract int value();

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
  public static @interface TypefaceStyle
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     butterknife.BindFont
 * JD-Core Version:    0.6.0
 */