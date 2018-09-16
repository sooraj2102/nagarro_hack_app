package android.support.transition;

import android.view.ViewGroup;

public abstract class TransitionPropagation
{
  public abstract void captureValues(TransitionValues paramTransitionValues);

  public abstract String[] getPropagationProperties();

  public abstract long getStartDelay(ViewGroup paramViewGroup, Transition paramTransition, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.TransitionPropagation
 * JD-Core Version:    0.6.0
 */