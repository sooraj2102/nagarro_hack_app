package android.support.transition;

import android.view.View;
import java.util.Map;

public abstract class VisibilityPropagation extends TransitionPropagation
{
  private static final String PROPNAME_VIEW_CENTER = "android:visibilityPropagation:center";
  private static final String PROPNAME_VISIBILITY = "android:visibilityPropagation:visibility";
  private static final String[] VISIBILITY_PROPAGATION_VALUES = { "android:visibilityPropagation:visibility", "android:visibilityPropagation:center" };

  private static int getViewCoordinate(TransitionValues paramTransitionValues, int paramInt)
  {
    if (paramTransitionValues == null)
      return -1;
    int[] arrayOfInt = (int[])(int[])paramTransitionValues.values.get("android:visibilityPropagation:center");
    if (arrayOfInt == null)
      return -1;
    return arrayOfInt[paramInt];
  }

  public void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = paramTransitionValues.view;
    Integer localInteger = (Integer)paramTransitionValues.values.get("android:visibility:visibility");
    if (localInteger == null)
      localInteger = Integer.valueOf(localView.getVisibility());
    paramTransitionValues.values.put("android:visibilityPropagation:visibility", localInteger);
    int[] arrayOfInt = new int[2];
    localView.getLocationOnScreen(arrayOfInt);
    arrayOfInt[0] += Math.round(localView.getTranslationX());
    arrayOfInt[0] += localView.getWidth() / 2;
    arrayOfInt[1] += Math.round(localView.getTranslationY());
    arrayOfInt[1] += localView.getHeight() / 2;
    paramTransitionValues.values.put("android:visibilityPropagation:center", arrayOfInt);
  }

  public String[] getPropagationProperties()
  {
    return VISIBILITY_PROPAGATION_VALUES;
  }

  public int getViewVisibility(TransitionValues paramTransitionValues)
  {
    if (paramTransitionValues == null);
    Integer localInteger;
    do
    {
      return 8;
      localInteger = (Integer)paramTransitionValues.values.get("android:visibilityPropagation:visibility");
    }
    while (localInteger == null);
    return localInteger.intValue();
  }

  public int getViewX(TransitionValues paramTransitionValues)
  {
    return getViewCoordinate(paramTransitionValues, 0);
  }

  public int getViewY(TransitionValues paramTransitionValues)
  {
    return getViewCoordinate(paramTransitionValues, 1);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.VisibilityPropagation
 * JD-Core Version:    0.6.0
 */