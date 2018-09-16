package android.support.design.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.StateSet;
import java.util.ArrayList;

final class StateListAnimator
{
  private final Animator.AnimatorListener mAnimationListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnimator)
    {
      if (StateListAnimator.this.mRunningAnimator == paramAnimator)
        StateListAnimator.this.mRunningAnimator = null;
    }
  };
  private Tuple mLastMatch = null;
  ValueAnimator mRunningAnimator = null;
  private final ArrayList<Tuple> mTuples = new ArrayList();

  private void cancel()
  {
    if (this.mRunningAnimator != null)
    {
      this.mRunningAnimator.cancel();
      this.mRunningAnimator = null;
    }
  }

  private void start(Tuple paramTuple)
  {
    this.mRunningAnimator = paramTuple.mAnimator;
    this.mRunningAnimator.start();
  }

  public void addState(int[] paramArrayOfInt, ValueAnimator paramValueAnimator)
  {
    Tuple localTuple = new Tuple(paramArrayOfInt, paramValueAnimator);
    paramValueAnimator.addListener(this.mAnimationListener);
    this.mTuples.add(localTuple);
  }

  public void jumpToCurrentState()
  {
    if (this.mRunningAnimator != null)
    {
      this.mRunningAnimator.end();
      this.mRunningAnimator = null;
    }
  }

  void setState(int[] paramArrayOfInt)
  {
    int i = this.mTuples.size();
    int j = 0;
    Object localObject = null;
    if (j < i)
    {
      Tuple localTuple = (Tuple)this.mTuples.get(j);
      if (StateSet.stateSetMatches(localTuple.mSpecs, paramArrayOfInt))
        localObject = localTuple;
    }
    else
    {
      if (localObject != this.mLastMatch)
        break label63;
    }
    label63: 
    do
    {
      return;
      j++;
      break;
      if (this.mLastMatch != null)
        cancel();
      this.mLastMatch = localObject;
    }
    while (localObject == null);
    start(localObject);
  }

  static class Tuple
  {
    final ValueAnimator mAnimator;
    final int[] mSpecs;

    Tuple(int[] paramArrayOfInt, ValueAnimator paramValueAnimator)
    {
      this.mSpecs = paramArrayOfInt;
      this.mAnimator = paramValueAnimator;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.design.widget.StateListAnimator
 * JD-Core Version:    0.6.0
 */