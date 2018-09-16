package android.support.transition;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public class TransitionSet extends Transition
{
  public static final int ORDERING_SEQUENTIAL = 1;
  public static final int ORDERING_TOGETHER;
  private int mCurrentListeners;
  private boolean mPlayTogether = true;
  private boolean mStarted = false;
  private ArrayList<Transition> mTransitions = new ArrayList();

  public TransitionSet()
  {
  }

  public TransitionSet(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.TRANSITION_SET);
    setOrdering(TypedArrayUtils.getNamedInt(localTypedArray, (XmlResourceParser)paramAttributeSet, "transitionOrdering", 0, 0));
    localTypedArray.recycle();
  }

  private void setupStartEndListeners()
  {
    TransitionSetListener localTransitionSetListener = new TransitionSetListener(this);
    Iterator localIterator = this.mTransitions.iterator();
    while (localIterator.hasNext())
      ((Transition)localIterator.next()).addListener(localTransitionSetListener);
    this.mCurrentListeners = this.mTransitions.size();
  }

  @NonNull
  public TransitionSet addListener(@NonNull Transition.TransitionListener paramTransitionListener)
  {
    return (TransitionSet)super.addListener(paramTransitionListener);
  }

  @NonNull
  public TransitionSet addTarget(@IdRes int paramInt)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).addTarget(paramInt);
    return (TransitionSet)super.addTarget(paramInt);
  }

  @NonNull
  public TransitionSet addTarget(@NonNull View paramView)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).addTarget(paramView);
    return (TransitionSet)super.addTarget(paramView);
  }

  @NonNull
  public TransitionSet addTarget(@NonNull Class paramClass)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).addTarget(paramClass);
    return (TransitionSet)super.addTarget(paramClass);
  }

  @NonNull
  public TransitionSet addTarget(@NonNull String paramString)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).addTarget(paramString);
    return (TransitionSet)super.addTarget(paramString);
  }

  @NonNull
  public TransitionSet addTransition(@NonNull Transition paramTransition)
  {
    this.mTransitions.add(paramTransition);
    paramTransition.mParent = this;
    if (this.mDuration >= 0L)
      paramTransition.setDuration(this.mDuration);
    return this;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void cancel()
  {
    super.cancel();
    int i = this.mTransitions.size();
    for (int j = 0; j < i; j++)
      ((Transition)this.mTransitions.get(j)).cancel();
  }

  public void captureEndValues(@NonNull TransitionValues paramTransitionValues)
  {
    if (isValidTarget(paramTransitionValues.view))
    {
      Iterator localIterator = this.mTransitions.iterator();
      while (localIterator.hasNext())
      {
        Transition localTransition = (Transition)localIterator.next();
        if (!localTransition.isValidTarget(paramTransitionValues.view))
          continue;
        localTransition.captureEndValues(paramTransitionValues);
        paramTransitionValues.mTargetedTransitions.add(localTransition);
      }
    }
  }

  void capturePropagationValues(TransitionValues paramTransitionValues)
  {
    super.capturePropagationValues(paramTransitionValues);
    int i = this.mTransitions.size();
    for (int j = 0; j < i; j++)
      ((Transition)this.mTransitions.get(j)).capturePropagationValues(paramTransitionValues);
  }

  public void captureStartValues(@NonNull TransitionValues paramTransitionValues)
  {
    if (isValidTarget(paramTransitionValues.view))
    {
      Iterator localIterator = this.mTransitions.iterator();
      while (localIterator.hasNext())
      {
        Transition localTransition = (Transition)localIterator.next();
        if (!localTransition.isValidTarget(paramTransitionValues.view))
          continue;
        localTransition.captureStartValues(paramTransitionValues);
        paramTransitionValues.mTargetedTransitions.add(localTransition);
      }
    }
  }

  public Transition clone()
  {
    TransitionSet localTransitionSet = (TransitionSet)super.clone();
    localTransitionSet.mTransitions = new ArrayList();
    int i = this.mTransitions.size();
    for (int j = 0; j < i; j++)
      localTransitionSet.addTransition(((Transition)this.mTransitions.get(j)).clone());
    return localTransitionSet;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void createAnimators(ViewGroup paramViewGroup, TransitionValuesMaps paramTransitionValuesMaps1, TransitionValuesMaps paramTransitionValuesMaps2, ArrayList<TransitionValues> paramArrayList1, ArrayList<TransitionValues> paramArrayList2)
  {
    long l1 = getStartDelay();
    int i = this.mTransitions.size();
    int j = 0;
    if (j < i)
    {
      Transition localTransition = (Transition)this.mTransitions.get(j);
      if ((l1 > 0L) && ((this.mPlayTogether) || (j == 0)))
      {
        long l2 = localTransition.getStartDelay();
        if (l2 <= 0L)
          break label101;
        localTransition.setStartDelay(l1 + l2);
      }
      while (true)
      {
        localTransition.createAnimators(paramViewGroup, paramTransitionValuesMaps1, paramTransitionValuesMaps2, paramArrayList1, paramArrayList2);
        j++;
        break;
        label101: localTransition.setStartDelay(l1);
      }
    }
  }

  @NonNull
  public Transition excludeTarget(int paramInt, boolean paramBoolean)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).excludeTarget(paramInt, paramBoolean);
    return super.excludeTarget(paramInt, paramBoolean);
  }

  @NonNull
  public Transition excludeTarget(@NonNull View paramView, boolean paramBoolean)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).excludeTarget(paramView, paramBoolean);
    return super.excludeTarget(paramView, paramBoolean);
  }

  @NonNull
  public Transition excludeTarget(@NonNull Class paramClass, boolean paramBoolean)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).excludeTarget(paramClass, paramBoolean);
    return super.excludeTarget(paramClass, paramBoolean);
  }

  @NonNull
  public Transition excludeTarget(@NonNull String paramString, boolean paramBoolean)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).excludeTarget(paramString, paramBoolean);
    return super.excludeTarget(paramString, paramBoolean);
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void forceToEnd(ViewGroup paramViewGroup)
  {
    super.forceToEnd(paramViewGroup);
    int i = this.mTransitions.size();
    for (int j = 0; j < i; j++)
      ((Transition)this.mTransitions.get(j)).forceToEnd(paramViewGroup);
  }

  public int getOrdering()
  {
    if (this.mPlayTogether)
      return 0;
    return 1;
  }

  public Transition getTransitionAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mTransitions.size()))
      return null;
    return (Transition)this.mTransitions.get(paramInt);
  }

  public int getTransitionCount()
  {
    return this.mTransitions.size();
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void pause(View paramView)
  {
    super.pause(paramView);
    int i = this.mTransitions.size();
    for (int j = 0; j < i; j++)
      ((Transition)this.mTransitions.get(j)).pause(paramView);
  }

  @NonNull
  public TransitionSet removeListener(@NonNull Transition.TransitionListener paramTransitionListener)
  {
    return (TransitionSet)super.removeListener(paramTransitionListener);
  }

  @NonNull
  public TransitionSet removeTarget(@IdRes int paramInt)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).removeTarget(paramInt);
    return (TransitionSet)super.removeTarget(paramInt);
  }

  @NonNull
  public TransitionSet removeTarget(@NonNull View paramView)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).removeTarget(paramView);
    return (TransitionSet)super.removeTarget(paramView);
  }

  @NonNull
  public TransitionSet removeTarget(@NonNull Class paramClass)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).removeTarget(paramClass);
    return (TransitionSet)super.removeTarget(paramClass);
  }

  @NonNull
  public TransitionSet removeTarget(@NonNull String paramString)
  {
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).removeTarget(paramString);
    return (TransitionSet)super.removeTarget(paramString);
  }

  @NonNull
  public TransitionSet removeTransition(@NonNull Transition paramTransition)
  {
    this.mTransitions.remove(paramTransition);
    paramTransition.mParent = null;
    return this;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void resume(View paramView)
  {
    super.resume(paramView);
    int i = this.mTransitions.size();
    for (int j = 0; j < i; j++)
      ((Transition)this.mTransitions.get(j)).resume(paramView);
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void runAnimators()
  {
    if (this.mTransitions.isEmpty())
    {
      start();
      end();
    }
    while (true)
    {
      return;
      setupStartEndListeners();
      if (!this.mPlayTogether)
      {
        for (int i = 1; i < this.mTransitions.size(); i++)
          ((Transition)this.mTransitions.get(i - 1)).addListener(new TransitionListenerAdapter((Transition)this.mTransitions.get(i))
          {
            public void onTransitionEnd(@NonNull Transition paramTransition)
            {
              this.val$nextTransition.runAnimators();
              paramTransition.removeListener(this);
            }
          });
        Transition localTransition = (Transition)this.mTransitions.get(0);
        if (localTransition == null)
          continue;
        localTransition.runAnimators();
        return;
      }
      Iterator localIterator = this.mTransitions.iterator();
      while (localIterator.hasNext())
        ((Transition)localIterator.next()).runAnimators();
    }
  }

  void setCanRemoveViews(boolean paramBoolean)
  {
    super.setCanRemoveViews(paramBoolean);
    int i = this.mTransitions.size();
    for (int j = 0; j < i; j++)
      ((Transition)this.mTransitions.get(j)).setCanRemoveViews(paramBoolean);
  }

  @NonNull
  public TransitionSet setDuration(long paramLong)
  {
    super.setDuration(paramLong);
    if (this.mDuration >= 0L)
    {
      int i = this.mTransitions.size();
      for (int j = 0; j < i; j++)
        ((Transition)this.mTransitions.get(j)).setDuration(paramLong);
    }
    return this;
  }

  public void setEpicenterCallback(Transition.EpicenterCallback paramEpicenterCallback)
  {
    super.setEpicenterCallback(paramEpicenterCallback);
    int i = this.mTransitions.size();
    for (int j = 0; j < i; j++)
      ((Transition)this.mTransitions.get(j)).setEpicenterCallback(paramEpicenterCallback);
  }

  @NonNull
  public TransitionSet setInterpolator(@Nullable TimeInterpolator paramTimeInterpolator)
  {
    return (TransitionSet)super.setInterpolator(paramTimeInterpolator);
  }

  @NonNull
  public TransitionSet setOrdering(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new AndroidRuntimeException("Invalid parameter for TransitionSet ordering: " + paramInt);
    case 1:
      this.mPlayTogether = false;
      return this;
    case 0:
    }
    this.mPlayTogether = true;
    return this;
  }

  public void setPathMotion(PathMotion paramPathMotion)
  {
    super.setPathMotion(paramPathMotion);
    for (int i = 0; i < this.mTransitions.size(); i++)
      ((Transition)this.mTransitions.get(i)).setPathMotion(paramPathMotion);
  }

  TransitionSet setSceneRoot(ViewGroup paramViewGroup)
  {
    super.setSceneRoot(paramViewGroup);
    int i = this.mTransitions.size();
    for (int j = 0; j < i; j++)
      ((Transition)this.mTransitions.get(j)).setSceneRoot(paramViewGroup);
    return this;
  }

  @NonNull
  public TransitionSet setStartDelay(long paramLong)
  {
    return (TransitionSet)super.setStartDelay(paramLong);
  }

  String toString(String paramString)
  {
    String str = super.toString(paramString);
    for (int i = 0; i < this.mTransitions.size(); i++)
      str = str + "\n" + ((Transition)this.mTransitions.get(i)).toString(new StringBuilder().append(paramString).append("  ").toString());
    return str;
  }

  static class TransitionSetListener extends TransitionListenerAdapter
  {
    TransitionSet mTransitionSet;

    TransitionSetListener(TransitionSet paramTransitionSet)
    {
      this.mTransitionSet = paramTransitionSet;
    }

    public void onTransitionEnd(@NonNull Transition paramTransition)
    {
      TransitionSet.access$106(this.mTransitionSet);
      if (this.mTransitionSet.mCurrentListeners == 0)
      {
        TransitionSet.access$002(this.mTransitionSet, false);
        this.mTransitionSet.end();
      }
      paramTransition.removeListener(this);
    }

    public void onTransitionStart(@NonNull Transition paramTransition)
    {
      if (!this.mTransitionSet.mStarted)
      {
        this.mTransitionSet.start();
        TransitionSet.access$002(this.mTransitionSet, true);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.TransitionSet
 * JD-Core Version:    0.6.0
 */