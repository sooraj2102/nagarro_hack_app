package android.support.transition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

public class Slide extends Visibility
{
  private static final String PROPNAME_SCREEN_POSITION = "android:slide:screenPosition";
  private static final TimeInterpolator sAccelerate;
  private static final CalculateSlide sCalculateBottom;
  private static final CalculateSlide sCalculateEnd;
  private static final CalculateSlide sCalculateLeft;
  private static final CalculateSlide sCalculateRight;
  private static final CalculateSlide sCalculateStart;
  private static final CalculateSlide sCalculateTop;
  private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
  private CalculateSlide mSlideCalculator = sCalculateBottom;
  private int mSlideEdge = 80;

  static
  {
    sAccelerate = new AccelerateInterpolator();
    sCalculateLeft = new CalculateSlideHorizontal()
    {
      public float getGoneX(ViewGroup paramViewGroup, View paramView)
      {
        return paramView.getTranslationX() - paramViewGroup.getWidth();
      }
    };
    sCalculateStart = new CalculateSlideHorizontal()
    {
      public float getGoneX(ViewGroup paramViewGroup, View paramView)
      {
        int i = 1;
        if (ViewCompat.getLayoutDirection(paramViewGroup) == i);
        while (i != 0)
        {
          return paramView.getTranslationX() + paramViewGroup.getWidth();
          i = 0;
        }
        return paramView.getTranslationX() - paramViewGroup.getWidth();
      }
    };
    sCalculateTop = new CalculateSlideVertical()
    {
      public float getGoneY(ViewGroup paramViewGroup, View paramView)
      {
        return paramView.getTranslationY() - paramViewGroup.getHeight();
      }
    };
    sCalculateRight = new CalculateSlideHorizontal()
    {
      public float getGoneX(ViewGroup paramViewGroup, View paramView)
      {
        return paramView.getTranslationX() + paramViewGroup.getWidth();
      }
    };
    sCalculateEnd = new CalculateSlideHorizontal()
    {
      public float getGoneX(ViewGroup paramViewGroup, View paramView)
      {
        int i = 1;
        if (ViewCompat.getLayoutDirection(paramViewGroup) == i);
        while (i != 0)
        {
          return paramView.getTranslationX() - paramViewGroup.getWidth();
          i = 0;
        }
        return paramView.getTranslationX() + paramViewGroup.getWidth();
      }
    };
    sCalculateBottom = new CalculateSlideVertical()
    {
      public float getGoneY(ViewGroup paramViewGroup, View paramView)
      {
        return paramView.getTranslationY() + paramViewGroup.getHeight();
      }
    };
  }

  public Slide()
  {
    setSlideEdge(80);
  }

  public Slide(int paramInt)
  {
    setSlideEdge(paramInt);
  }

  public Slide(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.SLIDE);
    int i = TypedArrayUtils.getNamedInt(localTypedArray, (XmlPullParser)paramAttributeSet, "slideEdge", 0, 80);
    localTypedArray.recycle();
    setSlideEdge(i);
  }

  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = paramTransitionValues.view;
    int[] arrayOfInt = new int[2];
    localView.getLocationOnScreen(arrayOfInt);
    paramTransitionValues.values.put("android:slide:screenPosition", arrayOfInt);
  }

  public void captureEndValues(@NonNull TransitionValues paramTransitionValues)
  {
    super.captureEndValues(paramTransitionValues);
    captureValues(paramTransitionValues);
  }

  public void captureStartValues(@NonNull TransitionValues paramTransitionValues)
  {
    super.captureStartValues(paramTransitionValues);
    captureValues(paramTransitionValues);
  }

  public int getSlideEdge()
  {
    return this.mSlideEdge;
  }

  public Animator onAppear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if (paramTransitionValues2 == null)
      return null;
    int[] arrayOfInt = (int[])(int[])paramTransitionValues2.values.get("android:slide:screenPosition");
    float f1 = paramView.getTranslationX();
    float f2 = paramView.getTranslationY();
    float f3 = this.mSlideCalculator.getGoneX(paramViewGroup, paramView);
    float f4 = this.mSlideCalculator.getGoneY(paramViewGroup, paramView);
    return TranslationAnimationCreator.createAnimation(paramView, paramTransitionValues2, arrayOfInt[0], arrayOfInt[1], f3, f4, f1, f2, sDecelerate);
  }

  public Animator onDisappear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if (paramTransitionValues1 == null)
      return null;
    int[] arrayOfInt = (int[])(int[])paramTransitionValues1.values.get("android:slide:screenPosition");
    float f1 = paramView.getTranslationX();
    float f2 = paramView.getTranslationY();
    float f3 = this.mSlideCalculator.getGoneX(paramViewGroup, paramView);
    float f4 = this.mSlideCalculator.getGoneY(paramViewGroup, paramView);
    return TranslationAnimationCreator.createAnimation(paramView, paramTransitionValues1, arrayOfInt[0], arrayOfInt[1], f1, f2, f3, f4, sAccelerate);
  }

  public void setSlideEdge(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new IllegalArgumentException("Invalid slide direction");
    case 3:
      this.mSlideCalculator = sCalculateLeft;
    case 48:
    case 5:
    case 80:
    case 8388611:
    case 8388613:
    }
    while (true)
    {
      this.mSlideEdge = paramInt;
      SidePropagation localSidePropagation = new SidePropagation();
      localSidePropagation.setSide(paramInt);
      setPropagation(localSidePropagation);
      return;
      this.mSlideCalculator = sCalculateTop;
      continue;
      this.mSlideCalculator = sCalculateRight;
      continue;
      this.mSlideCalculator = sCalculateBottom;
      continue;
      this.mSlideCalculator = sCalculateStart;
      continue;
      this.mSlideCalculator = sCalculateEnd;
    }
  }

  private static abstract interface CalculateSlide
  {
    public abstract float getGoneX(ViewGroup paramViewGroup, View paramView);

    public abstract float getGoneY(ViewGroup paramViewGroup, View paramView);
  }

  private static abstract class CalculateSlideHorizontal
    implements Slide.CalculateSlide
  {
    public float getGoneY(ViewGroup paramViewGroup, View paramView)
    {
      return paramView.getTranslationY();
    }
  }

  private static abstract class CalculateSlideVertical
    implements Slide.CalculateSlide
  {
    public float getGoneX(ViewGroup paramViewGroup, View paramView)
    {
      return paramView.getTranslationX();
    }
  }

  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface GravityFlag
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.Slide
 * JD-Core Version:    0.6.0
 */