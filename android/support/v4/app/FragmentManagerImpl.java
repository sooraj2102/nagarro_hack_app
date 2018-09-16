package android.support.v4.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class FragmentManagerImpl extends FragmentManager
  implements LayoutInflater.Factory2
{
  static final Interpolator ACCELERATE_CUBIC;
  static final Interpolator ACCELERATE_QUINT;
  static final int ANIM_DUR = 220;
  public static final int ANIM_STYLE_CLOSE_ENTER = 3;
  public static final int ANIM_STYLE_CLOSE_EXIT = 4;
  public static final int ANIM_STYLE_FADE_ENTER = 5;
  public static final int ANIM_STYLE_FADE_EXIT = 6;
  public static final int ANIM_STYLE_OPEN_ENTER = 1;
  public static final int ANIM_STYLE_OPEN_EXIT = 2;
  static boolean DEBUG = false;
  static final Interpolator DECELERATE_CUBIC;
  static final Interpolator DECELERATE_QUINT;
  static final String TAG = "FragmentManager";
  static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
  static final String TARGET_STATE_TAG = "android:target_state";
  static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
  static final String VIEW_STATE_TAG = "android:view_state";
  static Field sAnimationListenerField = null;
  SparseArray<Fragment> mActive;
  final ArrayList<Fragment> mAdded = new ArrayList();
  ArrayList<Integer> mAvailBackStackIndices;
  ArrayList<BackStackRecord> mBackStack;
  ArrayList<FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
  ArrayList<BackStackRecord> mBackStackIndices;
  FragmentContainer mContainer;
  ArrayList<Fragment> mCreatedMenus;
  int mCurState = 0;
  boolean mDestroyed;
  Runnable mExecCommit = new Runnable()
  {
    public void run()
    {
      FragmentManagerImpl.this.execPendingActions();
    }
  };
  boolean mExecutingActions;
  boolean mHavePendingDeferredStart;
  FragmentHostCallback mHost;
  private final CopyOnWriteArrayList<Pair<FragmentManager.FragmentLifecycleCallbacks, Boolean>> mLifecycleCallbacks = new CopyOnWriteArrayList();
  boolean mNeedMenuInvalidate;
  int mNextFragmentIndex = 0;
  String mNoTransactionsBecause;
  Fragment mParent;
  ArrayList<OpGenerator> mPendingActions;
  ArrayList<StartEnterTransitionListener> mPostponedTransactions;
  Fragment mPrimaryNav;
  FragmentManagerNonConfig mSavedNonConfig;
  SparseArray<Parcelable> mStateArray = null;
  Bundle mStateBundle = null;
  boolean mStateSaved;
  ArrayList<Fragment> mTmpAddedFragments;
  ArrayList<Boolean> mTmpIsPop;
  ArrayList<BackStackRecord> mTmpRecords;

  static
  {
    DECELERATE_QUINT = new DecelerateInterpolator(2.5F);
    DECELERATE_CUBIC = new DecelerateInterpolator(1.5F);
    ACCELERATE_QUINT = new AccelerateInterpolator(2.5F);
    ACCELERATE_CUBIC = new AccelerateInterpolator(1.5F);
  }

  private void addAddedFragments(ArraySet<Fragment> paramArraySet)
  {
    if (this.mCurState < 1);
    while (true)
    {
      return;
      int i = Math.min(this.mCurState, 4);
      int j = this.mAdded.size();
      for (int k = 0; k < j; k++)
      {
        Fragment localFragment = (Fragment)this.mAdded.get(k);
        if (localFragment.mState >= i)
          continue;
        moveToState(localFragment, i, localFragment.getNextAnim(), localFragment.getNextTransition(), false);
        if ((localFragment.mView == null) || (localFragment.mHidden) || (!localFragment.mIsNewlyAdded))
          continue;
        paramArraySet.add(localFragment);
      }
    }
  }

  private void animateRemoveFragment(@NonNull Fragment paramFragment, @NonNull AnimationOrAnimator paramAnimationOrAnimator, int paramInt)
  {
    View localView = paramFragment.mView;
    paramFragment.setStateAfterAnimating(paramInt);
    if (paramAnimationOrAnimator.animation != null)
    {
      Animation localAnimation = paramAnimationOrAnimator.animation;
      paramFragment.setAnimatingAway(paramFragment.mView);
      localAnimation.setAnimationListener(new AnimationListenerWrapper(getAnimationListener(localAnimation), paramFragment)
      {
        public void onAnimationEnd(Animation paramAnimation)
        {
          super.onAnimationEnd(paramAnimation);
          if (this.val$fragment.getAnimatingAway() != null)
          {
            this.val$fragment.setAnimatingAway(null);
            FragmentManagerImpl.this.moveToState(this.val$fragment, this.val$fragment.getStateAfterAnimating(), 0, 0, false);
          }
        }
      });
      setHWLayerAnimListenerIfAlpha(localView, paramAnimationOrAnimator);
      paramFragment.mView.startAnimation(localAnimation);
      return;
    }
    Animator localAnimator = paramAnimationOrAnimator.animator;
    paramFragment.setAnimator(paramAnimationOrAnimator.animator);
    ViewGroup localViewGroup = paramFragment.mContainer;
    if (localViewGroup != null)
      localViewGroup.startViewTransition(localView);
    localAnimator.addListener(new AnimatorListenerAdapter(localViewGroup, localView, paramFragment)
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        if (this.val$container != null)
          this.val$container.endViewTransition(this.val$viewToAnimate);
        if (this.val$fragment.getAnimator() != null)
        {
          this.val$fragment.setAnimator(null);
          FragmentManagerImpl.this.moveToState(this.val$fragment, this.val$fragment.getStateAfterAnimating(), 0, 0, false);
        }
      }
    });
    localAnimator.setTarget(paramFragment.mView);
    setHWLayerAnimListenerIfAlpha(paramFragment.mView, paramAnimationOrAnimator);
    localAnimator.start();
  }

  private void burpActive()
  {
    if (this.mActive != null)
      for (int i = -1 + this.mActive.size(); i >= 0; i--)
      {
        if (this.mActive.valueAt(i) != null)
          continue;
        this.mActive.delete(this.mActive.keyAt(i));
      }
  }

  private void checkStateLoss()
  {
    if (this.mStateSaved)
      throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
    if (this.mNoTransactionsBecause != null)
      throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
  }

  private void cleanupExec()
  {
    this.mExecutingActions = false;
    this.mTmpIsPop.clear();
    this.mTmpRecords.clear();
  }

  private void completeExecute(BackStackRecord paramBackStackRecord, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int j;
    label95: Fragment localFragment;
    if (paramBoolean1)
    {
      paramBackStackRecord.executePopOps(paramBoolean3);
      ArrayList localArrayList1 = new ArrayList(1);
      ArrayList localArrayList2 = new ArrayList(1);
      localArrayList1.add(paramBackStackRecord);
      localArrayList2.add(Boolean.valueOf(paramBoolean1));
      if (paramBoolean2)
        FragmentTransition.startTransitions(this, localArrayList1, localArrayList2, 0, 1, true);
      if (paramBoolean3)
        moveToState(this.mCurState, true);
      if (this.mActive == null)
        return;
      int i = this.mActive.size();
      j = 0;
      if (j >= i)
        return;
      localFragment = (Fragment)this.mActive.valueAt(j);
      if ((localFragment != null) && (localFragment.mView != null) && (localFragment.mIsNewlyAdded) && (paramBackStackRecord.interactsWith(localFragment.mContainerId)))
      {
        if (localFragment.mPostponedAlpha > 0.0F)
          localFragment.mView.setAlpha(localFragment.mPostponedAlpha);
        if (!paramBoolean3)
          break label196;
        localFragment.mPostponedAlpha = 0.0F;
      }
    }
    while (true)
    {
      j++;
      break label95;
      paramBackStackRecord.executeOps();
      break;
      label196: localFragment.mPostponedAlpha = -1.0F;
      localFragment.mIsNewlyAdded = false;
    }
  }

  private void dispatchStateChange(int paramInt)
  {
    try
    {
      this.mExecutingActions = true;
      moveToState(paramInt, false);
      this.mExecutingActions = false;
      execPendingActions();
      return;
    }
    finally
    {
      this.mExecutingActions = false;
    }
    throw localObject;
  }

  private void endAnimatingAwayFragments()
  {
    int i;
    int j;
    label11: Fragment localFragment;
    if (this.mActive == null)
    {
      i = 0;
      j = 0;
      if (j >= i)
        return;
      localFragment = (Fragment)this.mActive.valueAt(j);
      if (localFragment != null)
      {
        if (localFragment.getAnimatingAway() == null)
          break label105;
        int k = localFragment.getStateAfterAnimating();
        View localView = localFragment.getAnimatingAway();
        localFragment.setAnimatingAway(null);
        Animation localAnimation = localView.getAnimation();
        if (localAnimation != null)
        {
          localAnimation.cancel();
          localView.clearAnimation();
        }
        moveToState(localFragment, k, 0, 0, false);
      }
    }
    while (true)
    {
      j++;
      break label11;
      i = this.mActive.size();
      break;
      label105: if (localFragment.getAnimator() == null)
        continue;
      localFragment.getAnimator().end();
    }
  }

  private void ensureExecReady(boolean paramBoolean)
  {
    if (this.mExecutingActions)
      throw new IllegalStateException("FragmentManager is already executing transactions");
    if (Looper.myLooper() != this.mHost.getHandler().getLooper())
      throw new IllegalStateException("Must be called from main thread of fragment host");
    if (!paramBoolean)
      checkStateLoss();
    if (this.mTmpRecords == null)
    {
      this.mTmpRecords = new ArrayList();
      this.mTmpIsPop = new ArrayList();
    }
    this.mExecutingActions = true;
    try
    {
      executePostponedTransaction(null, null);
      return;
    }
    finally
    {
      this.mExecutingActions = false;
    }
    throw localObject;
  }

  private static void executeOps(ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    if (i < paramInt2)
    {
      BackStackRecord localBackStackRecord = (BackStackRecord)paramArrayList.get(i);
      boolean bool;
      if (((Boolean)paramArrayList1.get(i)).booleanValue())
      {
        localBackStackRecord.bumpBackStackNesting(-1);
        if (i == paramInt2 - 1)
        {
          bool = true;
          label52: localBackStackRecord.executePopOps(bool);
        }
      }
      while (true)
      {
        i++;
        break;
        bool = false;
        break label52;
        localBackStackRecord.bumpBackStackNesting(1);
        localBackStackRecord.executeOps();
      }
    }
  }

  private void executeOpsTogether(ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1, int paramInt1, int paramInt2)
  {
    boolean bool = ((BackStackRecord)paramArrayList.get(paramInt1)).mReorderingAllowed;
    int i = 0;
    Fragment localFragment;
    int j;
    label55: BackStackRecord localBackStackRecord2;
    if (this.mTmpAddedFragments == null)
    {
      this.mTmpAddedFragments = new ArrayList();
      this.mTmpAddedFragments.addAll(this.mAdded);
      localFragment = getPrimaryNavigationFragment();
      j = paramInt1;
      if (j >= paramInt2)
        break label155;
      localBackStackRecord2 = (BackStackRecord)paramArrayList.get(j);
      if (((Boolean)paramArrayList1.get(j)).booleanValue())
        break label133;
      localFragment = localBackStackRecord2.expandOps(this.mTmpAddedFragments, localFragment);
      label101: if ((i == 0) && (!localBackStackRecord2.mAddToBackStack))
        break label149;
    }
    label133: label149: for (i = 1; ; i = 0)
    {
      j++;
      break label55;
      this.mTmpAddedFragments.clear();
      break;
      localFragment = localBackStackRecord2.trackAddedFragmentsInPop(this.mTmpAddedFragments, localFragment);
      break label101;
    }
    label155: this.mTmpAddedFragments.clear();
    if (!bool)
      FragmentTransition.startTransitions(this, paramArrayList, paramArrayList1, paramInt1, paramInt2, false);
    executeOps(paramArrayList, paramArrayList1, paramInt1, paramInt2);
    int k = paramInt2;
    if (bool)
    {
      ArraySet localArraySet = new ArraySet();
      addAddedFragments(localArraySet);
      k = postponePostponableTransactions(paramArrayList, paramArrayList1, paramInt1, paramInt2, localArraySet);
      makeRemovedFragmentsInvisible(localArraySet);
    }
    if ((k != paramInt1) && (bool))
    {
      FragmentTransition.startTransitions(this, paramArrayList, paramArrayList1, paramInt1, k, true);
      moveToState(this.mCurState, true);
    }
    for (int m = paramInt1; m < paramInt2; m++)
    {
      BackStackRecord localBackStackRecord1 = (BackStackRecord)paramArrayList.get(m);
      if ((((Boolean)paramArrayList1.get(m)).booleanValue()) && (localBackStackRecord1.mIndex >= 0))
      {
        freeBackStackIndex(localBackStackRecord1.mIndex);
        localBackStackRecord1.mIndex = -1;
      }
      localBackStackRecord1.runOnCommitRunnables();
    }
    if (i != 0)
      reportBackStackChanged();
  }

  private void executePostponedTransaction(ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1)
  {
    int i;
    int j;
    label12: StartEnterTransitionListener localStartEnterTransitionListener;
    if (this.mPostponedTransactions == null)
    {
      i = 0;
      j = 0;
      if (j >= i)
        return;
      localStartEnterTransitionListener = (StartEnterTransitionListener)this.mPostponedTransactions.get(j);
      if ((paramArrayList == null) || (localStartEnterTransitionListener.mIsBack))
        break label98;
      int m = paramArrayList.indexOf(localStartEnterTransitionListener.mRecord);
      if ((m == -1) || (!((Boolean)paramArrayList1.get(m)).booleanValue()))
        break label98;
      localStartEnterTransitionListener.cancelTransaction();
    }
    while (true)
    {
      j++;
      break label12;
      i = this.mPostponedTransactions.size();
      break;
      label98: if ((!localStartEnterTransitionListener.isReady()) && ((paramArrayList == null) || (!localStartEnterTransitionListener.mRecord.interactsWith(paramArrayList, 0, paramArrayList.size()))))
        continue;
      this.mPostponedTransactions.remove(j);
      j--;
      i--;
      if ((paramArrayList != null) && (!localStartEnterTransitionListener.mIsBack))
      {
        int k = paramArrayList.indexOf(localStartEnterTransitionListener.mRecord);
        if ((k != -1) && (((Boolean)paramArrayList1.get(k)).booleanValue()))
        {
          localStartEnterTransitionListener.cancelTransaction();
          continue;
        }
      }
      localStartEnterTransitionListener.completeTransaction();
    }
  }

  private Fragment findFragmentUnder(Fragment paramFragment)
  {
    ViewGroup localViewGroup = paramFragment.mContainer;
    View localView = paramFragment.mView;
    Fragment localFragment;
    if ((localViewGroup == null) || (localView == null))
    {
      localFragment = null;
      return localFragment;
    }
    for (int i = -1 + this.mAdded.indexOf(paramFragment); ; i--)
    {
      if (i < 0)
        break label78;
      localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment.mContainer == localViewGroup) && (localFragment.mView != null))
        break;
    }
    label78: return null;
  }

  private void forcePostponedTransactions()
  {
    if (this.mPostponedTransactions != null)
      while (!this.mPostponedTransactions.isEmpty())
        ((StartEnterTransitionListener)this.mPostponedTransactions.remove(0)).completeTransaction();
  }

  private boolean generateOpsForPendingActions(ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1)
  {
    boolean bool = false;
    monitorenter;
    try
    {
      if ((this.mPendingActions == null) || (this.mPendingActions.size() == 0))
        return false;
      int i = this.mPendingActions.size();
      for (int j = 0; j < i; j++)
        bool |= ((OpGenerator)this.mPendingActions.get(j)).generateOps(paramArrayList, paramArrayList1);
      this.mPendingActions.clear();
      this.mHost.getHandler().removeCallbacks(this.mExecCommit);
      return bool;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  private static Animation.AnimationListener getAnimationListener(Animation paramAnimation)
  {
    try
    {
      if (sAnimationListenerField == null)
      {
        sAnimationListenerField = Animation.class.getDeclaredField("mListener");
        sAnimationListenerField.setAccessible(true);
      }
      Animation.AnimationListener localAnimationListener = (Animation.AnimationListener)sAnimationListenerField.get(paramAnimation);
      return localAnimationListener;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      Log.e("FragmentManager", "No field with the name mListener is found in Animation class", localNoSuchFieldException);
      return null;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Log.e("FragmentManager", "Cannot access Animation's mListener field", localIllegalAccessException);
    }
    return null;
  }

  static AnimationOrAnimator makeFadeAnimation(Context paramContext, float paramFloat1, float paramFloat2)
  {
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
    localAlphaAnimation.setInterpolator(DECELERATE_CUBIC);
    localAlphaAnimation.setDuration(220L);
    return new AnimationOrAnimator(localAlphaAnimation, null);
  }

  static AnimationOrAnimator makeOpenCloseAnimation(Context paramContext, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    AnimationSet localAnimationSet = new AnimationSet(false);
    ScaleAnimation localScaleAnimation = new ScaleAnimation(paramFloat1, paramFloat2, paramFloat1, paramFloat2, 1, 0.5F, 1, 0.5F);
    localScaleAnimation.setInterpolator(DECELERATE_QUINT);
    localScaleAnimation.setDuration(220L);
    localAnimationSet.addAnimation(localScaleAnimation);
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat3, paramFloat4);
    localAlphaAnimation.setInterpolator(DECELERATE_CUBIC);
    localAlphaAnimation.setDuration(220L);
    localAnimationSet.addAnimation(localAlphaAnimation);
    return new AnimationOrAnimator(localAnimationSet, null);
  }

  private void makeRemovedFragmentsInvisible(ArraySet<Fragment> paramArraySet)
  {
    int i = paramArraySet.size();
    for (int j = 0; j < i; j++)
    {
      Fragment localFragment = (Fragment)paramArraySet.valueAt(j);
      if (localFragment.mAdded)
        continue;
      View localView = localFragment.getView();
      localFragment.mPostponedAlpha = localView.getAlpha();
      localView.setAlpha(0.0F);
    }
  }

  static boolean modifiesAlpha(Animator paramAnimator)
  {
    if (paramAnimator == null)
      return false;
    if ((paramAnimator instanceof ValueAnimator))
    {
      PropertyValuesHolder[] arrayOfPropertyValuesHolder = ((ValueAnimator)paramAnimator).getValues();
      for (int j = 0; j < arrayOfPropertyValuesHolder.length; j++)
        if ("alpha".equals(arrayOfPropertyValuesHolder[j].getPropertyName()))
          return true;
    }
    if ((paramAnimator instanceof AnimatorSet))
    {
      ArrayList localArrayList = ((AnimatorSet)paramAnimator).getChildAnimations();
      for (int i = 0; i < localArrayList.size(); i++)
        if (modifiesAlpha((Animator)localArrayList.get(i)))
          return true;
    }
    return false;
  }

  static boolean modifiesAlpha(AnimationOrAnimator paramAnimationOrAnimator)
  {
    if ((paramAnimationOrAnimator.animation instanceof AlphaAnimation))
      return true;
    if ((paramAnimationOrAnimator.animation instanceof AnimationSet))
    {
      List localList = ((AnimationSet)paramAnimationOrAnimator.animation).getAnimations();
      for (int i = 0; i < localList.size(); i++)
        if ((localList.get(i) instanceof AlphaAnimation))
          return true;
      return false;
    }
    return modifiesAlpha(paramAnimationOrAnimator.animator);
  }

  private boolean popBackStackImmediate(String paramString, int paramInt1, int paramInt2)
  {
    execPendingActions();
    ensureExecReady(true);
    if ((this.mPrimaryNav != null) && (paramInt1 < 0) && (paramString == null))
    {
      FragmentManager localFragmentManager = this.mPrimaryNav.peekChildFragmentManager();
      if ((localFragmentManager != null) && (localFragmentManager.popBackStackImmediate()))
        return true;
    }
    boolean bool = popBackStackState(this.mTmpRecords, this.mTmpIsPop, paramString, paramInt1, paramInt2);
    if (bool)
      this.mExecutingActions = true;
    try
    {
      removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
      cleanupExec();
      doPendingDeferredStart();
      return bool;
    }
    finally
    {
      cleanupExec();
    }
    throw localObject;
  }

  private int postponePostponableTransactions(ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1, int paramInt1, int paramInt2, ArraySet<Fragment> paramArraySet)
  {
    int i = paramInt2;
    int j = paramInt2 - 1;
    if (j >= paramInt1)
    {
      BackStackRecord localBackStackRecord = (BackStackRecord)paramArrayList.get(j);
      boolean bool = ((Boolean)paramArrayList1.get(j)).booleanValue();
      int k;
      if ((localBackStackRecord.isPostponed()) && (!localBackStackRecord.interactsWith(paramArrayList, j + 1, paramInt2)))
      {
        k = 1;
        label67: if (k != 0)
        {
          if (this.mPostponedTransactions == null)
            this.mPostponedTransactions = new ArrayList();
          StartEnterTransitionListener localStartEnterTransitionListener = new StartEnterTransitionListener(localBackStackRecord, bool);
          this.mPostponedTransactions.add(localStartEnterTransitionListener);
          localBackStackRecord.setOnStartPostponedListener(localStartEnterTransitionListener);
          if (!bool)
            break label173;
          localBackStackRecord.executeOps();
        }
      }
      while (true)
      {
        i--;
        if (j != i)
        {
          paramArrayList.remove(j);
          paramArrayList.add(i, localBackStackRecord);
        }
        addAddedFragments(paramArraySet);
        j--;
        break;
        k = 0;
        break label67;
        label173: localBackStackRecord.executePopOps(false);
      }
    }
    return i;
  }

  private void removeRedundantOperationsAndExecute(ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()));
    int i;
    int j;
    do
    {
      return;
      if ((paramArrayList1 == null) || (paramArrayList.size() != paramArrayList1.size()))
        throw new IllegalStateException("Internal error with the back stack records");
      executePostponedTransaction(paramArrayList, paramArrayList1);
      i = paramArrayList.size();
      j = 0;
      for (int k = 0; k < i; k++)
      {
        if (((BackStackRecord)paramArrayList.get(k)).mReorderingAllowed)
          continue;
        if (j != k)
          executeOpsTogether(paramArrayList, paramArrayList1, j, k);
        int m = k + 1;
        if (((Boolean)paramArrayList1.get(k)).booleanValue())
          while ((m < i) && (((Boolean)paramArrayList1.get(m)).booleanValue()) && (!((BackStackRecord)paramArrayList.get(m)).mReorderingAllowed))
            m++;
        executeOpsTogether(paramArrayList, paramArrayList1, k, m);
        j = m;
        k = m - 1;
      }
    }
    while (j == i);
    executeOpsTogether(paramArrayList, paramArrayList1, j, i);
  }

  public static int reverseTransit(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return 0;
    case 4097:
      return 8194;
    case 8194:
      return 4097;
    case 4099:
    }
    return 4099;
  }

  private void scheduleCommit()
  {
    int i = 1;
    monitorenter;
    label44: label73: label81: label97: label100: 
    while (true)
    {
      int j;
      try
      {
        if ((this.mPostponedTransactions != null) && (!this.mPostponedTransactions.isEmpty()))
        {
          j = i;
          if ((this.mPendingActions == null) || (this.mPendingActions.size() != i))
            break label97;
          break label81;
          this.mHost.getHandler().removeCallbacks(this.mExecCommit);
          this.mHost.getHandler().post(this.mExecCommit);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      while (true)
      {
        if (j != 0)
          break label100;
        if (i == 0)
          break label73;
        break label44;
        j = 0;
        break;
        i = 0;
      }
    }
  }

  private static void setHWLayerAnimListenerIfAlpha(View paramView, AnimationOrAnimator paramAnimationOrAnimator)
  {
    if ((paramView == null) || (paramAnimationOrAnimator == null));
    do
      return;
    while (!shouldRunOnHWLayer(paramView, paramAnimationOrAnimator));
    if (paramAnimationOrAnimator.animator != null)
    {
      paramAnimationOrAnimator.animator.addListener(new AnimatorOnHWLayerIfNeededListener(paramView));
      return;
    }
    Animation.AnimationListener localAnimationListener = getAnimationListener(paramAnimationOrAnimator.animation);
    paramView.setLayerType(2, null);
    paramAnimationOrAnimator.animation.setAnimationListener(new AnimateOnHWLayerIfNeededListener(paramView, localAnimationListener));
  }

  private static void setRetaining(FragmentManagerNonConfig paramFragmentManagerNonConfig)
  {
    if (paramFragmentManagerNonConfig == null);
    while (true)
    {
      return;
      List localList1 = paramFragmentManagerNonConfig.getFragments();
      if (localList1 != null)
      {
        Iterator localIterator2 = localList1.iterator();
        while (localIterator2.hasNext())
          ((Fragment)localIterator2.next()).mRetaining = true;
      }
      List localList2 = paramFragmentManagerNonConfig.getChildNonConfigs();
      if (localList2 == null)
        continue;
      Iterator localIterator1 = localList2.iterator();
      while (localIterator1.hasNext())
        setRetaining((FragmentManagerNonConfig)localIterator1.next());
    }
  }

  static boolean shouldRunOnHWLayer(View paramView, AnimationOrAnimator paramAnimationOrAnimator)
  {
    if ((paramView == null) || (paramAnimationOrAnimator == null));
    do
      return false;
    while ((Build.VERSION.SDK_INT < 19) || (paramView.getLayerType() != 0) || (!ViewCompat.hasOverlappingRendering(paramView)) || (!modifiesAlpha(paramAnimationOrAnimator)));
    return true;
  }

  private void throwException(RuntimeException paramRuntimeException)
  {
    Log.e("FragmentManager", paramRuntimeException.getMessage());
    Log.e("FragmentManager", "Activity state:");
    PrintWriter localPrintWriter = new PrintWriter(new LogWriter("FragmentManager"));
    if (this.mHost != null);
    while (true)
    {
      try
      {
        this.mHost.onDump("  ", null, localPrintWriter, new String[0]);
        throw paramRuntimeException;
      }
      catch (Exception localException2)
      {
        Log.e("FragmentManager", "Failed dumping state", localException2);
        continue;
      }
      try
      {
        dump("  ", null, localPrintWriter, new String[0]);
      }
      catch (Exception localException1)
      {
        Log.e("FragmentManager", "Failed dumping state", localException1);
      }
    }
  }

  public static int transitToStyleIndex(int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    default:
      return -1;
    case 4097:
      if (paramBoolean)
        return 1;
      return 2;
    case 8194:
      if (paramBoolean)
        return 3;
      return 4;
    case 4099:
    }
    if (paramBoolean)
      return 5;
    return 6;
  }

  void addBackStackState(BackStackRecord paramBackStackRecord)
  {
    if (this.mBackStack == null)
      this.mBackStack = new ArrayList();
    this.mBackStack.add(paramBackStackRecord);
  }

  public void addFragment(Fragment paramFragment, boolean paramBoolean)
  {
    if (DEBUG)
      Log.v("FragmentManager", "add: " + paramFragment);
    makeActive(paramFragment);
    if (!paramFragment.mDetached)
      if (this.mAdded.contains(paramFragment))
        throw new IllegalStateException("Fragment already added: " + paramFragment);
    synchronized (this.mAdded)
    {
      this.mAdded.add(paramFragment);
      paramFragment.mAdded = true;
      paramFragment.mRemoving = false;
      if (paramFragment.mView == null)
        paramFragment.mHiddenChanged = false;
      if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
        this.mNeedMenuInvalidate = true;
      if (paramBoolean)
        moveToState(paramFragment);
      return;
    }
  }

  public void addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners == null)
      this.mBackStackChangeListeners = new ArrayList();
    this.mBackStackChangeListeners.add(paramOnBackStackChangedListener);
  }

  public int allocBackStackIndex(BackStackRecord paramBackStackRecord)
  {
    monitorenter;
    try
    {
      if ((this.mAvailBackStackIndices == null) || (this.mAvailBackStackIndices.size() <= 0))
      {
        if (this.mBackStackIndices == null)
          this.mBackStackIndices = new ArrayList();
        int i = this.mBackStackIndices.size();
        if (DEBUG)
          Log.v("FragmentManager", "Setting back stack index " + i + " to " + paramBackStackRecord);
        this.mBackStackIndices.add(paramBackStackRecord);
        return i;
      }
      int j = ((Integer)this.mAvailBackStackIndices.remove(-1 + this.mAvailBackStackIndices.size())).intValue();
      if (DEBUG)
        Log.v("FragmentManager", "Adding back stack index " + j + " with " + paramBackStackRecord);
      this.mBackStackIndices.set(j, paramBackStackRecord);
      return j;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public void attachController(FragmentHostCallback paramFragmentHostCallback, FragmentContainer paramFragmentContainer, Fragment paramFragment)
  {
    if (this.mHost != null)
      throw new IllegalStateException("Already attached");
    this.mHost = paramFragmentHostCallback;
    this.mContainer = paramFragmentContainer;
    this.mParent = paramFragment;
  }

  public void attachFragment(Fragment paramFragment)
  {
    if (DEBUG)
      Log.v("FragmentManager", "attach: " + paramFragment);
    if (paramFragment.mDetached)
    {
      paramFragment.mDetached = false;
      if (!paramFragment.mAdded)
      {
        if (this.mAdded.contains(paramFragment))
          throw new IllegalStateException("Fragment already added: " + paramFragment);
        if (DEBUG)
          Log.v("FragmentManager", "add from attach: " + paramFragment);
      }
    }
    synchronized (this.mAdded)
    {
      this.mAdded.add(paramFragment);
      paramFragment.mAdded = true;
      if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
        this.mNeedMenuInvalidate = true;
      return;
    }
  }

  public FragmentTransaction beginTransaction()
  {
    return new BackStackRecord(this);
  }

  void completeShowHideFragment(Fragment paramFragment)
  {
    boolean bool;
    AnimationOrAnimator localAnimationOrAnimator;
    if (paramFragment.mView != null)
    {
      int i = paramFragment.getNextTransition();
      if (paramFragment.mHidden)
        break label135;
      bool = true;
      localAnimationOrAnimator = loadAnimation(paramFragment, i, bool, paramFragment.getNextTransitionStyle());
      if ((localAnimationOrAnimator == null) || (localAnimationOrAnimator.animator == null))
        break label194;
      localAnimationOrAnimator.animator.setTarget(paramFragment.mView);
      if (!paramFragment.mHidden)
        break label183;
      if (!paramFragment.isHideReplaced())
        break label140;
      paramFragment.setHideReplaced(false);
    }
    while (true)
    {
      setHWLayerAnimListenerIfAlpha(paramFragment.mView, localAnimationOrAnimator);
      localAnimationOrAnimator.animator.start();
      if ((paramFragment.mAdded) && (paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
        this.mNeedMenuInvalidate = true;
      paramFragment.mHiddenChanged = false;
      paramFragment.onHiddenChanged(paramFragment.mHidden);
      return;
      label135: bool = false;
      break;
      label140: ViewGroup localViewGroup = paramFragment.mContainer;
      View localView = paramFragment.mView;
      localViewGroup.startViewTransition(localView);
      localAnimationOrAnimator.animator.addListener(new AnimatorListenerAdapter(localViewGroup, localView, paramFragment)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          this.val$container.endViewTransition(this.val$animatingView);
          paramAnimator.removeListener(this);
          if (this.val$fragment.mView != null)
            this.val$fragment.mView.setVisibility(8);
        }
      });
      continue;
      label183: paramFragment.mView.setVisibility(0);
    }
    label194: if (localAnimationOrAnimator != null)
    {
      setHWLayerAnimListenerIfAlpha(paramFragment.mView, localAnimationOrAnimator);
      paramFragment.mView.startAnimation(localAnimationOrAnimator.animation);
      localAnimationOrAnimator.animation.start();
    }
    if ((paramFragment.mHidden) && (!paramFragment.isHideReplaced()));
    for (int j = 8; ; j = 0)
    {
      paramFragment.mView.setVisibility(j);
      if (!paramFragment.isHideReplaced())
        break;
      paramFragment.setHideReplaced(false);
      break;
    }
  }

  public void detachFragment(Fragment paramFragment)
  {
    if (DEBUG)
      Log.v("FragmentManager", "detach: " + paramFragment);
    if (!paramFragment.mDetached)
    {
      paramFragment.mDetached = true;
      if (paramFragment.mAdded)
        if (DEBUG)
          Log.v("FragmentManager", "remove from detach: " + paramFragment);
    }
    synchronized (this.mAdded)
    {
      this.mAdded.remove(paramFragment);
      if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
        this.mNeedMenuInvalidate = true;
      paramFragment.mAdded = false;
      return;
    }
  }

  public void dispatchActivityCreated()
  {
    this.mStateSaved = false;
    dispatchStateChange(2);
  }

  public void dispatchConfigurationChanged(Configuration paramConfiguration)
  {
    for (int i = 0; i < this.mAdded.size(); i++)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment == null)
        continue;
      localFragment.performConfigurationChanged(paramConfiguration);
    }
  }

  public boolean dispatchContextItemSelected(MenuItem paramMenuItem)
  {
    for (int i = 0; i < this.mAdded.size(); i++)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment != null) && (localFragment.performContextItemSelected(paramMenuItem)))
        return true;
    }
    return false;
  }

  public void dispatchCreate()
  {
    this.mStateSaved = false;
    dispatchStateChange(1);
  }

  public boolean dispatchCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    int i = 0;
    ArrayList localArrayList = null;
    for (int j = 0; j < this.mAdded.size(); j++)
    {
      Fragment localFragment2 = (Fragment)this.mAdded.get(j);
      if ((localFragment2 == null) || (!localFragment2.performCreateOptionsMenu(paramMenu, paramMenuInflater)))
        continue;
      i = 1;
      if (localArrayList == null)
        localArrayList = new ArrayList();
      localArrayList.add(localFragment2);
    }
    if (this.mCreatedMenus != null)
      for (int k = 0; k < this.mCreatedMenus.size(); k++)
      {
        Fragment localFragment1 = (Fragment)this.mCreatedMenus.get(k);
        if ((localArrayList != null) && (localArrayList.contains(localFragment1)))
          continue;
        localFragment1.onDestroyOptionsMenu();
      }
    this.mCreatedMenus = localArrayList;
    return i;
  }

  public void dispatchDestroy()
  {
    this.mDestroyed = true;
    execPendingActions();
    dispatchStateChange(0);
    this.mHost = null;
    this.mContainer = null;
    this.mParent = null;
  }

  public void dispatchDestroyView()
  {
    dispatchStateChange(1);
  }

  public void dispatchLowMemory()
  {
    for (int i = 0; i < this.mAdded.size(); i++)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment == null)
        continue;
      localFragment.performLowMemory();
    }
  }

  public void dispatchMultiWindowModeChanged(boolean paramBoolean)
  {
    for (int i = -1 + this.mAdded.size(); i >= 0; i--)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment == null)
        continue;
      localFragment.performMultiWindowModeChanged(paramBoolean);
    }
  }

  void dispatchOnFragmentActivityCreated(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentActivityCreated(paramFragment, paramBundle, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentActivityCreated(this, paramFragment, paramBundle);
    }
  }

  void dispatchOnFragmentAttached(Fragment paramFragment, Context paramContext, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentAttached(paramFragment, paramContext, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentAttached(this, paramFragment, paramContext);
    }
  }

  void dispatchOnFragmentCreated(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentCreated(paramFragment, paramBundle, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentCreated(this, paramFragment, paramBundle);
    }
  }

  void dispatchOnFragmentDestroyed(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentDestroyed(paramFragment, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentDestroyed(this, paramFragment);
    }
  }

  void dispatchOnFragmentDetached(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentDetached(paramFragment, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentDetached(this, paramFragment);
    }
  }

  void dispatchOnFragmentPaused(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentPaused(paramFragment, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentPaused(this, paramFragment);
    }
  }

  void dispatchOnFragmentPreAttached(Fragment paramFragment, Context paramContext, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentPreAttached(paramFragment, paramContext, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentPreAttached(this, paramFragment, paramContext);
    }
  }

  void dispatchOnFragmentPreCreated(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentPreCreated(paramFragment, paramBundle, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentPreCreated(this, paramFragment, paramBundle);
    }
  }

  void dispatchOnFragmentResumed(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentResumed(paramFragment, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentResumed(this, paramFragment);
    }
  }

  void dispatchOnFragmentSaveInstanceState(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentSaveInstanceState(paramFragment, paramBundle, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentSaveInstanceState(this, paramFragment, paramBundle);
    }
  }

  void dispatchOnFragmentStarted(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentStarted(paramFragment, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentStarted(this, paramFragment);
    }
  }

  void dispatchOnFragmentStopped(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentStopped(paramFragment, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentStopped(this, paramFragment);
    }
  }

  void dispatchOnFragmentViewCreated(Fragment paramFragment, View paramView, Bundle paramBundle, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentViewCreated(paramFragment, paramView, paramBundle, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentViewCreated(this, paramFragment, paramView, paramBundle);
    }
  }

  void dispatchOnFragmentViewDestroyed(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mParent != null)
    {
      FragmentManager localFragmentManager = this.mParent.getFragmentManager();
      if ((localFragmentManager instanceof FragmentManagerImpl))
        ((FragmentManagerImpl)localFragmentManager).dispatchOnFragmentViewDestroyed(paramFragment, true);
    }
    Iterator localIterator = this.mLifecycleCallbacks.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramBoolean) && (!((Boolean)localPair.second).booleanValue()))
        continue;
      ((FragmentManager.FragmentLifecycleCallbacks)localPair.first).onFragmentViewDestroyed(this, paramFragment);
    }
  }

  public boolean dispatchOptionsItemSelected(MenuItem paramMenuItem)
  {
    for (int i = 0; i < this.mAdded.size(); i++)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment != null) && (localFragment.performOptionsItemSelected(paramMenuItem)))
        return true;
    }
    return false;
  }

  public void dispatchOptionsMenuClosed(Menu paramMenu)
  {
    for (int i = 0; i < this.mAdded.size(); i++)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment == null)
        continue;
      localFragment.performOptionsMenuClosed(paramMenu);
    }
  }

  public void dispatchPause()
  {
    dispatchStateChange(4);
  }

  public void dispatchPictureInPictureModeChanged(boolean paramBoolean)
  {
    for (int i = -1 + this.mAdded.size(); i >= 0; i--)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment == null)
        continue;
      localFragment.performPictureInPictureModeChanged(paramBoolean);
    }
  }

  public boolean dispatchPrepareOptionsMenu(Menu paramMenu)
  {
    int i = 0;
    for (int j = 0; j < this.mAdded.size(); j++)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(j);
      if ((localFragment == null) || (!localFragment.performPrepareOptionsMenu(paramMenu)))
        continue;
      i = 1;
    }
    return i;
  }

  public void dispatchReallyStop()
  {
    dispatchStateChange(2);
  }

  public void dispatchResume()
  {
    this.mStateSaved = false;
    dispatchStateChange(5);
  }

  public void dispatchStart()
  {
    this.mStateSaved = false;
    dispatchStateChange(4);
  }

  public void dispatchStop()
  {
    this.mStateSaved = true;
    dispatchStateChange(3);
  }

  void doPendingDeferredStart()
  {
    if (this.mHavePendingDeferredStart)
    {
      boolean bool = false;
      for (int i = 0; i < this.mActive.size(); i++)
      {
        Fragment localFragment = (Fragment)this.mActive.valueAt(i);
        if ((localFragment == null) || (localFragment.mLoaderManager == null))
          continue;
        bool |= localFragment.mLoaderManager.hasRunningLoaders();
      }
      if (!bool)
      {
        this.mHavePendingDeferredStart = false;
        startPendingDeferredFragments();
      }
    }
  }

  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = paramString + "    ";
    if (this.mActive != null)
    {
      int i6 = this.mActive.size();
      if (i6 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Active Fragments in ");
        paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
        paramPrintWriter.println(":");
        for (int i7 = 0; i7 < i6; i7++)
        {
          Fragment localFragment3 = (Fragment)this.mActive.valueAt(i7);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i7);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localFragment3);
          if (localFragment3 == null)
            continue;
          localFragment3.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        }
      }
    }
    int i = this.mAdded.size();
    if (i > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Added Fragments:");
      for (int i5 = 0; i5 < i; i5++)
      {
        Fragment localFragment2 = (Fragment)this.mAdded.get(i5);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  #");
        paramPrintWriter.print(i5);
        paramPrintWriter.print(": ");
        paramPrintWriter.println(localFragment2.toString());
      }
    }
    if (this.mCreatedMenus != null)
    {
      int i3 = this.mCreatedMenus.size();
      if (i3 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Fragments Created Menus:");
        for (int i4 = 0; i4 < i3; i4++)
        {
          Fragment localFragment1 = (Fragment)this.mCreatedMenus.get(i4);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i4);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localFragment1.toString());
        }
      }
    }
    if (this.mBackStack != null)
    {
      int i1 = this.mBackStack.size();
      if (i1 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Back Stack:");
        for (int i2 = 0; i2 < i1; i2++)
        {
          BackStackRecord localBackStackRecord2 = (BackStackRecord)this.mBackStack.get(i2);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i2);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localBackStackRecord2.toString());
          localBackStackRecord2.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        }
      }
    }
    monitorenter;
    try
    {
      if (this.mBackStackIndices != null)
      {
        int m = this.mBackStackIndices.size();
        if (m > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Back Stack Indices:");
          for (int n = 0; n < m; n++)
          {
            BackStackRecord localBackStackRecord1 = (BackStackRecord)this.mBackStackIndices.get(n);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(n);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(localBackStackRecord1);
          }
        }
      }
      if ((this.mAvailBackStackIndices != null) && (this.mAvailBackStackIndices.size() > 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mAvailBackStackIndices: ");
        paramPrintWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
      }
      monitorexit;
      if (this.mPendingActions != null)
      {
        int j = this.mPendingActions.size();
        if (j > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Pending Actions:");
          for (int k = 0; k < j; k++)
          {
            OpGenerator localOpGenerator = (OpGenerator)this.mPendingActions.get(k);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(k);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(localOpGenerator);
          }
        }
      }
    }
    finally
    {
      monitorexit;
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("FragmentManager misc state:");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mHost=");
    paramPrintWriter.println(this.mHost);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mContainer=");
    paramPrintWriter.println(this.mContainer);
    if (this.mParent != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mParent=");
      paramPrintWriter.println(this.mParent);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mCurState=");
    paramPrintWriter.print(this.mCurState);
    paramPrintWriter.print(" mStateSaved=");
    paramPrintWriter.print(this.mStateSaved);
    paramPrintWriter.print(" mDestroyed=");
    paramPrintWriter.println(this.mDestroyed);
    if (this.mNeedMenuInvalidate)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNeedMenuInvalidate=");
      paramPrintWriter.println(this.mNeedMenuInvalidate);
    }
    if (this.mNoTransactionsBecause != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNoTransactionsBecause=");
      paramPrintWriter.println(this.mNoTransactionsBecause);
    }
  }

  public void enqueueAction(OpGenerator paramOpGenerator, boolean paramBoolean)
  {
    if (!paramBoolean)
      checkStateLoss();
    monitorenter;
    try
    {
      if ((this.mDestroyed) || (this.mHost == null))
      {
        if (paramBoolean)
          return;
        throw new IllegalStateException("Activity has been destroyed");
      }
    }
    finally
    {
      monitorexit;
    }
    if (this.mPendingActions == null)
      this.mPendingActions = new ArrayList();
    this.mPendingActions.add(paramOpGenerator);
    scheduleCommit();
    monitorexit;
  }

  void ensureInflatedFragmentView(Fragment paramFragment)
  {
    if ((paramFragment.mFromLayout) && (!paramFragment.mPerformedCreateView))
    {
      paramFragment.mView = paramFragment.performCreateView(paramFragment.performGetLayoutInflater(paramFragment.mSavedFragmentState), null, paramFragment.mSavedFragmentState);
      if (paramFragment.mView != null)
      {
        paramFragment.mInnerView = paramFragment.mView;
        paramFragment.mView.setSaveFromParentEnabled(false);
        if (paramFragment.mHidden)
          paramFragment.mView.setVisibility(8);
        paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
        dispatchOnFragmentViewCreated(paramFragment, paramFragment.mView, paramFragment.mSavedFragmentState, false);
      }
    }
    else
    {
      return;
    }
    paramFragment.mInnerView = null;
  }

  public boolean execPendingActions()
  {
    ensureExecReady(true);
    int i = 0;
    while (true)
    {
      if (generateOpsForPendingActions(this.mTmpRecords, this.mTmpIsPop))
        this.mExecutingActions = true;
      try
      {
        removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
        cleanupExec();
        i = 1;
      }
      finally
      {
        cleanupExec();
      }
    }
    burpActive();
    return i;
  }

  public void execSingleAction(OpGenerator paramOpGenerator, boolean paramBoolean)
  {
    if ((paramBoolean) && ((this.mHost == null) || (this.mDestroyed)))
      return;
    ensureExecReady(paramBoolean);
    if (paramOpGenerator.generateOps(this.mTmpRecords, this.mTmpIsPop))
      this.mExecutingActions = true;
    try
    {
      removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
      cleanupExec();
      doPendingDeferredStart();
      return;
    }
    finally
    {
      cleanupExec();
    }
    throw localObject;
  }

  public boolean executePendingTransactions()
  {
    boolean bool = execPendingActions();
    forcePostponedTransactions();
    return bool;
  }

  public Fragment findFragmentById(int paramInt)
  {
    Fragment localFragment;
    for (int i = -1 + this.mAdded.size(); i >= 0; i--)
    {
      localFragment = (Fragment)this.mAdded.get(i);
      if ((localFragment != null) && (localFragment.mFragmentId == paramInt))
        return localFragment;
    }
    if (this.mActive != null)
      for (int j = -1 + this.mActive.size(); ; j--)
      {
        if (j < 0)
          break label104;
        localFragment = (Fragment)this.mActive.valueAt(j);
        if ((localFragment != null) && (localFragment.mFragmentId == paramInt))
          break;
      }
    label104: return null;
  }

  public Fragment findFragmentByTag(String paramString)
  {
    Fragment localFragment;
    if (paramString != null)
      for (int j = -1 + this.mAdded.size(); j >= 0; j--)
      {
        localFragment = (Fragment)this.mAdded.get(j);
        if ((localFragment != null) && (paramString.equals(localFragment.mTag)))
          return localFragment;
      }
    if ((this.mActive != null) && (paramString != null))
      for (int i = -1 + this.mActive.size(); ; i--)
      {
        if (i < 0)
          break label114;
        localFragment = (Fragment)this.mActive.valueAt(i);
        if ((localFragment != null) && (paramString.equals(localFragment.mTag)))
          break;
      }
    label114: return null;
  }

  public Fragment findFragmentByWho(String paramString)
  {
    if ((this.mActive != null) && (paramString != null))
      for (int i = -1 + this.mActive.size(); i >= 0; i--)
      {
        Fragment localFragment1 = (Fragment)this.mActive.valueAt(i);
        if (localFragment1 == null)
          continue;
        Fragment localFragment2 = localFragment1.findFragmentByWho(paramString);
        if (localFragment2 != null)
          return localFragment2;
      }
    return null;
  }

  public void freeBackStackIndex(int paramInt)
  {
    monitorenter;
    try
    {
      this.mBackStackIndices.set(paramInt, null);
      if (this.mAvailBackStackIndices == null)
        this.mAvailBackStackIndices = new ArrayList();
      if (DEBUG)
        Log.v("FragmentManager", "Freeing back stack index " + paramInt);
      this.mAvailBackStackIndices.add(Integer.valueOf(paramInt));
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  int getActiveFragmentCount()
  {
    if (this.mActive == null)
      return 0;
    return this.mActive.size();
  }

  List<Fragment> getActiveFragments()
  {
    ArrayList localArrayList;
    if (this.mActive == null)
      localArrayList = null;
    while (true)
    {
      return localArrayList;
      int i = this.mActive.size();
      localArrayList = new ArrayList(i);
      for (int j = 0; j < i; j++)
        localArrayList.add(this.mActive.valueAt(j));
    }
  }

  public FragmentManager.BackStackEntry getBackStackEntryAt(int paramInt)
  {
    return (FragmentManager.BackStackEntry)this.mBackStack.get(paramInt);
  }

  public int getBackStackEntryCount()
  {
    if (this.mBackStack != null)
      return this.mBackStack.size();
    return 0;
  }

  public Fragment getFragment(Bundle paramBundle, String paramString)
  {
    int i = paramBundle.getInt(paramString, -1);
    Fragment localFragment;
    if (i == -1)
      localFragment = null;
    do
    {
      return localFragment;
      localFragment = (Fragment)this.mActive.get(i);
    }
    while (localFragment != null);
    throwException(new IllegalStateException("Fragment no longer exists for key " + paramString + ": index " + i));
    return localFragment;
  }

  public List<Fragment> getFragments()
  {
    if (this.mAdded.isEmpty())
      return Collections.EMPTY_LIST;
    synchronized (this.mAdded)
    {
      List localList = (List)this.mAdded.clone();
      return localList;
    }
  }

  LayoutInflater.Factory2 getLayoutInflaterFactory()
  {
    return this;
  }

  public Fragment getPrimaryNavigationFragment()
  {
    return this.mPrimaryNav;
  }

  public void hideFragment(Fragment paramFragment)
  {
    boolean bool = true;
    if (DEBUG)
      Log.v("FragmentManager", "hide: " + paramFragment);
    if (!paramFragment.mHidden)
    {
      paramFragment.mHidden = bool;
      if (paramFragment.mHiddenChanged)
        break label59;
    }
    while (true)
    {
      paramFragment.mHiddenChanged = bool;
      return;
      label59: bool = false;
    }
  }

  public boolean isDestroyed()
  {
    return this.mDestroyed;
  }

  boolean isStateAtLeast(int paramInt)
  {
    return this.mCurState >= paramInt;
  }

  public boolean isStateSaved()
  {
    return this.mStateSaved;
  }

  AnimationOrAnimator loadAnimation(Fragment paramFragment, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    int i = paramFragment.getNextAnim();
    Animation localAnimation1 = paramFragment.onCreateAnimation(paramInt1, paramBoolean, i);
    if (localAnimation1 != null)
      return new AnimationOrAnimator(localAnimation1, null);
    Animator localAnimator1 = paramFragment.onCreateAnimator(paramInt1, paramBoolean, i);
    if (localAnimator1 != null)
      return new AnimationOrAnimator(localAnimator1, null);
    boolean bool;
    int k;
    if (i != 0)
    {
      bool = "anim".equals(this.mHost.getContext().getResources().getResourceTypeName(i));
      k = 0;
      if (!bool);
    }
    try
    {
      Animation localAnimation3 = AnimationUtils.loadAnimation(this.mHost.getContext(), i);
      if (localAnimation3 != null)
      {
        AnimationOrAnimator localAnimationOrAnimator2 = new AnimationOrAnimator(localAnimation3, null);
        return localAnimationOrAnimator2;
      }
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      throw localNotFoundException;
      k = 1;
      if (k == 0)
        try
        {
          Animator localAnimator2 = AnimatorInflater.loadAnimator(this.mHost.getContext(), i);
          if (localAnimator2 != null)
          {
            AnimationOrAnimator localAnimationOrAnimator1 = new AnimationOrAnimator(localAnimator2, null);
            return localAnimationOrAnimator1;
          }
        }
        catch (RuntimeException localRuntimeException1)
        {
          if (bool)
            throw localRuntimeException1;
          Animation localAnimation2 = AnimationUtils.loadAnimation(this.mHost.getContext(), i);
          if (localAnimation2 != null)
            return new AnimationOrAnimator(localAnimation2, null);
        }
      if (paramInt1 == 0)
        return null;
      int j = transitToStyleIndex(paramInt1, paramBoolean);
      if (j < 0)
        return null;
      switch (j)
      {
      default:
        if ((paramInt2 == 0) && (this.mHost.onHasWindowAnimations()))
          paramInt2 = this.mHost.onGetWindowAnimations();
        if (paramInt2 != 0)
          break;
        return null;
      case 1:
        return makeOpenCloseAnimation(this.mHost.getContext(), 1.125F, 1.0F, 0.0F, 1.0F);
      case 2:
        return makeOpenCloseAnimation(this.mHost.getContext(), 1.0F, 0.975F, 1.0F, 0.0F);
      case 3:
        return makeOpenCloseAnimation(this.mHost.getContext(), 0.975F, 1.0F, 0.0F, 1.0F);
      case 4:
        return makeOpenCloseAnimation(this.mHost.getContext(), 1.0F, 1.075F, 1.0F, 0.0F);
      case 5:
        return makeFadeAnimation(this.mHost.getContext(), 0.0F, 1.0F);
      case 6:
        return makeFadeAnimation(this.mHost.getContext(), 1.0F, 0.0F);
      }
      return null;
    }
    catch (RuntimeException localRuntimeException2)
    {
      while (true)
        k = 0;
    }
  }

  void makeActive(Fragment paramFragment)
  {
    if (paramFragment.mIndex >= 0);
    do
    {
      return;
      int i = this.mNextFragmentIndex;
      this.mNextFragmentIndex = (i + 1);
      paramFragment.setIndex(i, this.mParent);
      if (this.mActive == null)
        this.mActive = new SparseArray();
      this.mActive.put(paramFragment.mIndex, paramFragment);
    }
    while (!DEBUG);
    Log.v("FragmentManager", "Allocated fragment index " + paramFragment);
  }

  void makeInactive(Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0)
      return;
    if (DEBUG)
      Log.v("FragmentManager", "Freeing fragment index " + paramFragment);
    this.mActive.put(paramFragment.mIndex, null);
    this.mHost.inactivateFragment(paramFragment.mWho);
    paramFragment.initState();
  }

  void moveFragmentToExpectedState(Fragment paramFragment)
  {
    if (paramFragment == null);
    label256: 
    while (true)
    {
      return;
      int i = this.mCurState;
      AnimationOrAnimator localAnimationOrAnimator;
      if (paramFragment.mRemoving)
      {
        if (paramFragment.isInBackStack())
          i = Math.min(i, 1);
      }
      else
      {
        moveToState(paramFragment, i, paramFragment.getNextTransition(), paramFragment.getNextTransitionStyle(), false);
        if (paramFragment.mView != null)
        {
          Fragment localFragment = findFragmentUnder(paramFragment);
          if (localFragment != null)
          {
            View localView = localFragment.mView;
            ViewGroup localViewGroup = paramFragment.mContainer;
            int j = localViewGroup.indexOfChild(localView);
            int k = localViewGroup.indexOfChild(paramFragment.mView);
            if (k < j)
            {
              localViewGroup.removeViewAt(k);
              localViewGroup.addView(paramFragment.mView, j);
            }
          }
          if ((paramFragment.mIsNewlyAdded) && (paramFragment.mContainer != null))
          {
            if (paramFragment.mPostponedAlpha > 0.0F)
              paramFragment.mView.setAlpha(paramFragment.mPostponedAlpha);
            paramFragment.mPostponedAlpha = 0.0F;
            paramFragment.mIsNewlyAdded = false;
            localAnimationOrAnimator = loadAnimation(paramFragment, paramFragment.getNextTransition(), true, paramFragment.getNextTransitionStyle());
            if (localAnimationOrAnimator != null)
            {
              setHWLayerAnimListenerIfAlpha(paramFragment.mView, localAnimationOrAnimator);
              if (localAnimationOrAnimator.animation == null)
                break label235;
              paramFragment.mView.startAnimation(localAnimationOrAnimator.animation);
            }
          }
        }
      }
      while (true)
      {
        if (!paramFragment.mHiddenChanged)
          break label256;
        completeShowHideFragment(paramFragment);
        return;
        i = Math.min(i, 0);
        break;
        label235: localAnimationOrAnimator.animator.setTarget(paramFragment.mView);
        localAnimationOrAnimator.animator.start();
      }
    }
  }

  void moveToState(int paramInt, boolean paramBoolean)
  {
    if ((this.mHost == null) && (paramInt != 0))
      throw new IllegalStateException("No activity");
    if ((!paramBoolean) && (paramInt == this.mCurState));
    do
    {
      do
      {
        return;
        this.mCurState = paramInt;
      }
      while (this.mActive == null);
      boolean bool = false;
      int i = this.mAdded.size();
      for (int j = 0; j < i; j++)
      {
        Fragment localFragment2 = (Fragment)this.mAdded.get(j);
        moveFragmentToExpectedState(localFragment2);
        if (localFragment2.mLoaderManager == null)
          continue;
        bool |= localFragment2.mLoaderManager.hasRunningLoaders();
      }
      int k = this.mActive.size();
      for (int m = 0; m < k; m++)
      {
        Fragment localFragment1 = (Fragment)this.mActive.valueAt(m);
        if ((localFragment1 == null) || ((!localFragment1.mRemoving) && (!localFragment1.mDetached)) || (localFragment1.mIsNewlyAdded))
          continue;
        moveFragmentToExpectedState(localFragment1);
        if (localFragment1.mLoaderManager == null)
          continue;
        bool |= localFragment1.mLoaderManager.hasRunningLoaders();
      }
      if (bool)
        continue;
      startPendingDeferredFragments();
    }
    while ((!this.mNeedMenuInvalidate) || (this.mHost == null) || (this.mCurState != 5));
    this.mHost.onSupportInvalidateOptionsMenu();
    this.mNeedMenuInvalidate = false;
  }

  void moveToState(Fragment paramFragment)
  {
    moveToState(paramFragment, this.mCurState, 0, 0, false);
  }

  void moveToState(Fragment paramFragment, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (((!paramFragment.mAdded) || (paramFragment.mDetached)) && (paramInt1 > 1))
      paramInt1 = 1;
    if (paramFragment.mRemoving)
    {
      int m = paramFragment.mState;
      if (paramInt1 > m)
      {
        if ((paramFragment.mState != 0) || (!paramFragment.isInBackStack()))
          break label101;
        paramInt1 = 1;
      }
    }
    if ((paramFragment.mDeferStart) && (paramFragment.mState < 4) && (paramInt1 > 3))
      paramInt1 = 3;
    if (paramFragment.mState <= paramInt1)
      if ((!paramFragment.mFromLayout) || (paramFragment.mInLayout));
    while (true)
    {
      return;
      label101: paramInt1 = paramFragment.mState;
      break;
      if ((paramFragment.getAnimatingAway() != null) || (paramFragment.getAnimator() != null))
      {
        paramFragment.setAnimatingAway(null);
        paramFragment.setAnimator(null);
        moveToState(paramFragment, paramFragment.getStateAfterAnimating(), 0, 0, true);
      }
      switch (paramFragment.mState)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      }
      while (paramFragment.mState != paramInt1)
      {
        Log.w("FragmentManager", "moveToState: Fragment state for " + paramFragment + " not updated inline; " + "expected state " + paramInt1 + " found " + paramFragment.mState);
        paramFragment.mState = paramInt1;
        return;
        label627: ViewGroup localViewGroup;
        if (paramInt1 > 0)
        {
          if (DEBUG)
            Log.v("FragmentManager", "moveto CREATED: " + paramFragment);
          if (paramFragment.mSavedFragmentState != null)
          {
            paramFragment.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
            paramFragment.mSavedViewState = paramFragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
            paramFragment.mTarget = getFragment(paramFragment.mSavedFragmentState, "android:target_state");
            if (paramFragment.mTarget != null)
              paramFragment.mTargetRequestCode = paramFragment.mSavedFragmentState.getInt("android:target_req_state", 0);
            paramFragment.mUserVisibleHint = paramFragment.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
            if (!paramFragment.mUserVisibleHint)
            {
              paramFragment.mDeferStart = true;
              if (paramInt1 > 3)
                paramInt1 = 3;
            }
          }
          paramFragment.mHost = this.mHost;
          paramFragment.mParentFragment = this.mParent;
          FragmentManagerImpl localFragmentManagerImpl;
          if (this.mParent != null)
            localFragmentManagerImpl = this.mParent.mChildFragmentManager;
          while (true)
          {
            paramFragment.mFragmentManager = localFragmentManagerImpl;
            if (paramFragment.mTarget == null)
              break;
            if (this.mActive.get(paramFragment.mTarget.mIndex) != paramFragment.mTarget)
            {
              throw new IllegalStateException("Fragment " + paramFragment + " declared target fragment " + paramFragment.mTarget + " that does not belong to this FragmentManager!");
              localFragmentManagerImpl = this.mHost.getFragmentManagerImpl();
              continue;
            }
            if (paramFragment.mTarget.mState >= 1)
              break;
            moveToState(paramFragment.mTarget, 1, 0, 0, true);
          }
          dispatchOnFragmentPreAttached(paramFragment, this.mHost.getContext(), false);
          paramFragment.mCalled = false;
          paramFragment.onAttach(this.mHost.getContext());
          if (!paramFragment.mCalled)
            throw new SuperNotCalledException("Fragment " + paramFragment + " did not call through to super.onAttach()");
          if (paramFragment.mParentFragment == null)
          {
            this.mHost.onAttachFragment(paramFragment);
            dispatchOnFragmentAttached(paramFragment, this.mHost.getContext(), false);
            if (paramFragment.mIsCreated)
              break label1190;
            dispatchOnFragmentPreCreated(paramFragment, paramFragment.mSavedFragmentState, false);
            paramFragment.performCreate(paramFragment.mSavedFragmentState);
            dispatchOnFragmentCreated(paramFragment, paramFragment.mSavedFragmentState, false);
            paramFragment.mRetaining = false;
          }
        }
        else
        {
          ensureInflatedFragmentView(paramFragment);
          if (paramInt1 > 1)
          {
            if (DEBUG)
              Log.v("FragmentManager", "moveto ACTIVITY_CREATED: " + paramFragment);
            if (!paramFragment.mFromLayout)
            {
              int k = paramFragment.mContainerId;
              localViewGroup = null;
              if (k != 0)
              {
                if (paramFragment.mContainerId == -1)
                  throwException(new IllegalArgumentException("Cannot create fragment " + paramFragment + " for a container view with no id"));
                localViewGroup = (ViewGroup)this.mContainer.onFindViewById(paramFragment.mContainerId);
                if ((localViewGroup != null) || (paramFragment.mRestored));
              }
            }
          }
        }
        try
        {
          String str2 = paramFragment.getResources().getResourceName(paramFragment.mContainerId);
          str1 = str2;
          throwException(new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(paramFragment.mContainerId) + " (" + str1 + ") for fragment " + paramFragment));
          paramFragment.mContainer = localViewGroup;
          paramFragment.mView = paramFragment.performCreateView(paramFragment.performGetLayoutInflater(paramFragment.mSavedFragmentState), localViewGroup, paramFragment.mSavedFragmentState);
          if (paramFragment.mView != null)
          {
            paramFragment.mInnerView = paramFragment.mView;
            paramFragment.mView.setSaveFromParentEnabled(false);
            if (localViewGroup != null)
              localViewGroup.addView(paramFragment.mView);
            if (paramFragment.mHidden)
              paramFragment.mView.setVisibility(8);
            paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
            dispatchOnFragmentViewCreated(paramFragment, paramFragment.mView, paramFragment.mSavedFragmentState, false);
            if ((paramFragment.mView.getVisibility() == 0) && (paramFragment.mContainer != null))
            {
              bool3 = true;
              paramFragment.mIsNewlyAdded = bool3;
              paramFragment.performActivityCreated(paramFragment.mSavedFragmentState);
              dispatchOnFragmentActivityCreated(paramFragment, paramFragment.mSavedFragmentState, false);
              if (paramFragment.mView != null)
                paramFragment.restoreViewState(paramFragment.mSavedFragmentState);
              paramFragment.mSavedFragmentState = null;
              if (paramInt1 > 2)
                paramFragment.mState = 3;
              if (paramInt1 > 3)
              {
                if (DEBUG)
                  Log.v("FragmentManager", "moveto STARTED: " + paramFragment);
                paramFragment.performStart();
                dispatchOnFragmentStarted(paramFragment, false);
              }
              if (paramInt1 <= 4)
                continue;
              if (DEBUG)
                Log.v("FragmentManager", "moveto RESUMED: " + paramFragment);
              paramFragment.performResume();
              dispatchOnFragmentResumed(paramFragment, false);
              paramFragment.mSavedFragmentState = null;
              paramFragment.mSavedViewState = null;
              continue;
              paramFragment.mParentFragment.onAttachFragment(paramFragment);
              break label627;
              label1190: paramFragment.restoreChildFragmentState(paramFragment.mSavedFragmentState);
              paramFragment.mState = 1;
            }
          }
        }
        catch (Resources.NotFoundException localNotFoundException)
        {
          while (true)
          {
            String str1 = "unknown";
            continue;
            boolean bool3 = false;
            continue;
            paramFragment.mInnerView = null;
          }
        }
        if (paramFragment.mState <= paramInt1)
          continue;
        switch (paramFragment.mState)
        {
        default:
          break;
        case 1:
          if (paramInt1 >= 1)
            continue;
          if (this.mDestroyed)
          {
            if (paramFragment.getAnimatingAway() == null)
              break label1706;
            View localView = paramFragment.getAnimatingAway();
            paramFragment.setAnimatingAway(null);
            localView.clearAnimation();
          }
        case 5:
        case 4:
        case 3:
        case 2:
          while (true)
          {
            label1279: if ((paramFragment.getAnimatingAway() == null) && (paramFragment.getAnimator() == null))
              break label1732;
            paramFragment.setStateAfterAnimating(paramInt1);
            paramInt1 = 1;
            break;
            if (paramInt1 < 5)
            {
              if (DEBUG)
                Log.v("FragmentManager", "movefrom RESUMED: " + paramFragment);
              paramFragment.performPause();
              dispatchOnFragmentPaused(paramFragment, false);
            }
            if (paramInt1 < 4)
            {
              if (DEBUG)
                Log.v("FragmentManager", "movefrom STARTED: " + paramFragment);
              paramFragment.performStop();
              dispatchOnFragmentStopped(paramFragment, false);
            }
            if (paramInt1 < 3)
            {
              if (DEBUG)
                Log.v("FragmentManager", "movefrom STOPPED: " + paramFragment);
              paramFragment.performReallyStop();
            }
            if (paramInt1 >= 2)
              break label1279;
            if (DEBUG)
              Log.v("FragmentManager", "movefrom ACTIVITY_CREATED: " + paramFragment);
            if ((paramFragment.mView != null) && (this.mHost.onShouldSaveFragmentState(paramFragment)) && (paramFragment.mSavedViewState == null))
              saveFragmentViewState(paramFragment);
            paramFragment.performDestroyView();
            dispatchOnFragmentViewDestroyed(paramFragment, false);
            if ((paramFragment.mView != null) && (paramFragment.mContainer != null))
            {
              paramFragment.mView.clearAnimation();
              paramFragment.mContainer.endViewTransition(paramFragment.mView);
              int i = this.mCurState;
              AnimationOrAnimator localAnimationOrAnimator = null;
              if (i > 0)
              {
                boolean bool1 = this.mDestroyed;
                localAnimationOrAnimator = null;
                if (!bool1)
                {
                  int j = paramFragment.mView.getVisibility();
                  localAnimationOrAnimator = null;
                  if (j == 0)
                  {
                    boolean bool2 = paramFragment.mPostponedAlpha < 0.0F;
                    localAnimationOrAnimator = null;
                    if (!bool2)
                      localAnimationOrAnimator = loadAnimation(paramFragment, paramInt2, false, paramInt3);
                  }
                }
              }
              paramFragment.mPostponedAlpha = 0.0F;
              if (localAnimationOrAnimator != null)
                animateRemoveFragment(paramFragment, localAnimationOrAnimator, paramInt1);
              paramFragment.mContainer.removeView(paramFragment.mView);
            }
            paramFragment.mContainer = null;
            paramFragment.mView = null;
            paramFragment.mInnerView = null;
            paramFragment.mInLayout = false;
            break label1279;
            label1706: if (paramFragment.getAnimator() == null)
              continue;
            Animator localAnimator = paramFragment.getAnimator();
            paramFragment.setAnimator(null);
            localAnimator.cancel();
          }
          label1732: if (DEBUG)
            Log.v("FragmentManager", "movefrom CREATED: " + paramFragment);
          if (!paramFragment.mRetaining)
          {
            paramFragment.performDestroy();
            dispatchOnFragmentDestroyed(paramFragment, false);
          }
          while (true)
          {
            paramFragment.performDetach();
            dispatchOnFragmentDetached(paramFragment, false);
            if (paramBoolean)
              break;
            if (paramFragment.mRetaining)
              break label1819;
            makeInactive(paramFragment);
            break;
            paramFragment.mState = 0;
          }
          label1819: paramFragment.mHost = null;
          paramFragment.mParentFragment = null;
          paramFragment.mFragmentManager = null;
        }
      }
    }
  }

  public void noteStateNotSaved()
  {
    this.mSavedNonConfig = null;
    this.mStateSaved = false;
    int i = this.mAdded.size();
    for (int j = 0; j < i; j++)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(j);
      if (localFragment == null)
        continue;
      localFragment.noteStateNotSaved();
    }
  }

  public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    if (!"fragment".equals(paramString))
      return null;
    String str1 = paramAttributeSet.getAttributeValue(null, "class");
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, FragmentTag.Fragment);
    if (str1 == null)
      str1 = localTypedArray.getString(0);
    int i = localTypedArray.getResourceId(1, -1);
    String str2 = localTypedArray.getString(2);
    localTypedArray.recycle();
    if (!Fragment.isSupportFragmentClass(this.mHost.getContext(), str1))
      return null;
    if (paramView != null);
    for (int j = paramView.getId(); (j == -1) && (i == -1) && (str2 == null); j = 0)
      throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + str1);
    Fragment localFragment;
    int k;
    if (i != -1)
    {
      localFragment = findFragmentById(i);
      if ((localFragment == null) && (str2 != null))
        localFragment = findFragmentByTag(str2);
      if ((localFragment == null) && (j != -1))
        localFragment = findFragmentById(j);
      if (DEBUG)
        Log.v("FragmentManager", "onCreateView: id=0x" + Integer.toHexString(i) + " fname=" + str1 + " existing=" + localFragment);
      if (localFragment != null)
        break label452;
      localFragment = this.mContainer.instantiate(paramContext, str1, null);
      localFragment.mFromLayout = true;
      if (i == 0)
        break label445;
      k = i;
      label302: localFragment.mFragmentId = k;
      localFragment.mContainerId = j;
      localFragment.mTag = str2;
      localFragment.mInLayout = true;
      localFragment.mFragmentManager = this;
      localFragment.mHost = this.mHost;
      localFragment.onInflate(this.mHost.getContext(), paramAttributeSet, localFragment.mSavedFragmentState);
      addFragment(localFragment, true);
      label370: if ((this.mCurState >= 1) || (!localFragment.mFromLayout))
        break label583;
      moveToState(localFragment, 1, 0, 0, false);
    }
    while (true)
    {
      if (localFragment.mView != null)
        break label592;
      throw new IllegalStateException("Fragment " + str1 + " did not create a view.");
      localFragment = null;
      break;
      label445: k = j;
      break label302;
      label452: if (localFragment.mInLayout)
        throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(i) + ", tag " + str2 + ", or parent id 0x" + Integer.toHexString(j) + " with another fragment for " + str1);
      localFragment.mInLayout = true;
      localFragment.mHost = this.mHost;
      if (localFragment.mRetaining)
        break label370;
      localFragment.onInflate(this.mHost.getContext(), paramAttributeSet, localFragment.mSavedFragmentState);
      break label370;
      label583: moveToState(localFragment);
    }
    label592: if (i != 0)
      localFragment.mView.setId(i);
    if (localFragment.mView.getTag() == null)
      localFragment.mView.setTag(str2);
    return localFragment.mView;
  }

  public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    return onCreateView(null, paramString, paramContext, paramAttributeSet);
  }

  public void performPendingDeferredStart(Fragment paramFragment)
  {
    if (paramFragment.mDeferStart)
    {
      if (this.mExecutingActions)
        this.mHavePendingDeferredStart = true;
    }
    else
      return;
    paramFragment.mDeferStart = false;
    moveToState(paramFragment, this.mCurState, 0, 0, false);
  }

  public void popBackStack()
  {
    enqueueAction(new PopBackStackState(null, -1, 0), false);
  }

  public void popBackStack(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Bad id: " + paramInt1);
    enqueueAction(new PopBackStackState(null, paramInt1, paramInt2), false);
  }

  public void popBackStack(String paramString, int paramInt)
  {
    enqueueAction(new PopBackStackState(paramString, -1, paramInt), false);
  }

  public boolean popBackStackImmediate()
  {
    checkStateLoss();
    return popBackStackImmediate(null, -1, 0);
  }

  public boolean popBackStackImmediate(int paramInt1, int paramInt2)
  {
    checkStateLoss();
    execPendingActions();
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Bad id: " + paramInt1);
    return popBackStackImmediate(null, paramInt1, paramInt2);
  }

  public boolean popBackStackImmediate(String paramString, int paramInt)
  {
    checkStateLoss();
    return popBackStackImmediate(paramString, -1, paramInt);
  }

  boolean popBackStackState(ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1, String paramString, int paramInt1, int paramInt2)
  {
    if (this.mBackStack == null)
      break label124;
    while (true)
    {
      label7: return false;
      if ((paramString != null) || (paramInt1 >= 0) || ((paramInt2 & 0x1) != 0))
        break;
      int k = -1 + this.mBackStack.size();
      if (k < 0)
        continue;
      paramArrayList.add(this.mBackStack.remove(k));
      paramArrayList1.add(Boolean.valueOf(true));
    }
    while (true)
    {
      return true;
      int i = -1;
      if ((paramString != null) || (paramInt1 >= 0))
        for (i = -1 + this.mBackStack.size(); ; i--)
        {
          BackStackRecord localBackStackRecord2;
          if (i >= 0)
          {
            localBackStackRecord2 = (BackStackRecord)this.mBackStack.get(i);
            if ((paramString == null) || (!paramString.equals(localBackStackRecord2.getName())));
          }
          else
          {
            label124: if (i < 0)
              break label7;
            if ((paramInt2 & 0x1) == 0)
              break label216;
            i--;
            while (i >= 0)
            {
              BackStackRecord localBackStackRecord1 = (BackStackRecord)this.mBackStack.get(i);
              if (((paramString == null) || (!paramString.equals(localBackStackRecord1.getName()))) && ((paramInt1 < 0) || (paramInt1 != localBackStackRecord1.mIndex)))
                break;
              i--;
            }
          }
          if ((paramInt1 >= 0) && (paramInt1 == localBackStackRecord2.mIndex))
            break;
        }
      label216: if (i == -1 + this.mBackStack.size())
        break label7;
      for (int j = -1 + this.mBackStack.size(); j > i; j--)
      {
        paramArrayList.add(this.mBackStack.remove(j));
        paramArrayList1.add(Boolean.valueOf(true));
      }
    }
  }

  public void putFragment(Bundle paramBundle, String paramString, Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0)
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    paramBundle.putInt(paramString, paramFragment.mIndex);
  }

  public void registerFragmentLifecycleCallbacks(FragmentManager.FragmentLifecycleCallbacks paramFragmentLifecycleCallbacks, boolean paramBoolean)
  {
    this.mLifecycleCallbacks.add(new Pair(paramFragmentLifecycleCallbacks, Boolean.valueOf(paramBoolean)));
  }

  public void removeFragment(Fragment paramFragment)
  {
    if (DEBUG)
      Log.v("FragmentManager", "remove: " + paramFragment + " nesting=" + paramFragment.mBackStackNesting);
    int i;
    if (!paramFragment.isInBackStack())
      i = 1;
    while (true)
    {
      if ((!paramFragment.mDetached) || (i != 0));
      synchronized (this.mAdded)
      {
        this.mAdded.remove(paramFragment);
        if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
          this.mNeedMenuInvalidate = true;
        paramFragment.mAdded = false;
        paramFragment.mRemoving = true;
        return;
        i = 0;
      }
    }
  }

  public void removeOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners != null)
      this.mBackStackChangeListeners.remove(paramOnBackStackChangedListener);
  }

  void reportBackStackChanged()
  {
    if (this.mBackStackChangeListeners != null)
      for (int i = 0; i < this.mBackStackChangeListeners.size(); i++)
        ((FragmentManager.OnBackStackChangedListener)this.mBackStackChangeListeners.get(i)).onBackStackChanged();
  }

  void restoreAllState(Parcelable paramParcelable, FragmentManagerNonConfig paramFragmentManagerNonConfig)
  {
    if (paramParcelable == null);
    FragmentManagerState localFragmentManagerState;
    do
    {
      return;
      localFragmentManagerState = (FragmentManagerState)paramParcelable;
    }
    while (localFragmentManagerState.mActive == null);
    List localList1 = null;
    if (paramFragmentManagerNonConfig != null)
    {
      List localList3 = paramFragmentManagerNonConfig.getFragments();
      localList1 = paramFragmentManagerNonConfig.getChildNonConfigs();
      int i2;
      if (localList3 != null)
        i2 = localList3.size();
      for (int i3 = 0; ; i3++)
      {
        if (i3 >= i2)
          break label298;
        Fragment localFragment4 = (Fragment)localList3.get(i3);
        if (DEBUG)
          Log.v("FragmentManager", "restoreAllState: re-attaching retained " + localFragment4);
        int i4 = 0;
        while (true)
          if ((i4 < localFragmentManagerState.mActive.length) && (localFragmentManagerState.mActive[i4].mIndex != localFragment4.mIndex))
          {
            i4++;
            continue;
            i2 = 0;
            break;
          }
        if (i4 == localFragmentManagerState.mActive.length)
          throwException(new IllegalStateException("Could not find active fragment with index " + localFragment4.mIndex));
        FragmentState localFragmentState2 = localFragmentManagerState.mActive[i4];
        localFragmentState2.mInstance = localFragment4;
        localFragment4.mSavedViewState = null;
        localFragment4.mBackStackNesting = 0;
        localFragment4.mInLayout = false;
        localFragment4.mAdded = false;
        localFragment4.mTarget = null;
        if (localFragmentState2.mSavedFragmentState == null)
          continue;
        localFragmentState2.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
        localFragment4.mSavedViewState = localFragmentState2.mSavedFragmentState.getSparseParcelableArray("android:view_state");
        localFragment4.mSavedFragmentState = localFragmentState2.mSavedFragmentState;
      }
    }
    label298: this.mActive = new SparseArray(localFragmentManagerState.mActive.length);
    for (int i = 0; i < localFragmentManagerState.mActive.length; i++)
    {
      FragmentState localFragmentState1 = localFragmentManagerState.mActive[i];
      if (localFragmentState1 == null)
        continue;
      FragmentManagerNonConfig localFragmentManagerNonConfig = null;
      if (localList1 != null)
      {
        int i1 = localList1.size();
        localFragmentManagerNonConfig = null;
        if (i < i1)
          localFragmentManagerNonConfig = (FragmentManagerNonConfig)localList1.get(i);
      }
      Fragment localFragment3 = localFragmentState1.instantiate(this.mHost, this.mContainer, this.mParent, localFragmentManagerNonConfig);
      if (DEBUG)
        Log.v("FragmentManager", "restoreAllState: active #" + i + ": " + localFragment3);
      this.mActive.put(localFragment3.mIndex, localFragment3);
      localFragmentState1.mInstance = null;
    }
    if (paramFragmentManagerNonConfig != null)
    {
      List localList2 = paramFragmentManagerNonConfig.getFragments();
      if (localList2 != null);
      for (int m = localList2.size(); ; m = 0)
        for (int n = 0; n < m; n++)
        {
          Fragment localFragment2 = (Fragment)localList2.get(n);
          if (localFragment2.mTargetIndex < 0)
            continue;
          localFragment2.mTarget = ((Fragment)this.mActive.get(localFragment2.mTargetIndex));
          if (localFragment2.mTarget != null)
            continue;
          Log.w("FragmentManager", "Re-attaching retained fragment " + localFragment2 + " target no longer exists: " + localFragment2.mTargetIndex);
        }
    }
    this.mAdded.clear();
    if (localFragmentManagerState.mAdded != null)
    {
      int k = 0;
      while (k < localFragmentManagerState.mAdded.length)
      {
        Fragment localFragment1 = (Fragment)this.mActive.get(localFragmentManagerState.mAdded[k]);
        if (localFragment1 == null)
          throwException(new IllegalStateException("No instantiated fragment for index #" + localFragmentManagerState.mAdded[k]));
        localFragment1.mAdded = true;
        if (DEBUG)
          Log.v("FragmentManager", "restoreAllState: added #" + k + ": " + localFragment1);
        if (this.mAdded.contains(localFragment1))
          throw new IllegalStateException("Already added!");
        synchronized (this.mAdded)
        {
          this.mAdded.add(localFragment1);
          k++;
        }
      }
    }
    if (localFragmentManagerState.mBackStack != null)
    {
      this.mBackStack = new ArrayList(localFragmentManagerState.mBackStack.length);
      for (int j = 0; j < localFragmentManagerState.mBackStack.length; j++)
      {
        BackStackRecord localBackStackRecord = localFragmentManagerState.mBackStack[j].instantiate(this);
        if (DEBUG)
        {
          Log.v("FragmentManager", "restoreAllState: back stack #" + j + " (index " + localBackStackRecord.mIndex + "): " + localBackStackRecord);
          PrintWriter localPrintWriter = new PrintWriter(new LogWriter("FragmentManager"));
          localBackStackRecord.dump("  ", localPrintWriter, false);
          localPrintWriter.close();
        }
        this.mBackStack.add(localBackStackRecord);
        if (localBackStackRecord.mIndex < 0)
          continue;
        setBackStackIndex(localBackStackRecord.mIndex, localBackStackRecord);
      }
    }
    this.mBackStack = null;
    if (localFragmentManagerState.mPrimaryNavActiveIndex >= 0)
      this.mPrimaryNav = ((Fragment)this.mActive.get(localFragmentManagerState.mPrimaryNavActiveIndex));
    this.mNextFragmentIndex = localFragmentManagerState.mNextFragmentIndex;
  }

  FragmentManagerNonConfig retainNonConfig()
  {
    setRetaining(this.mSavedNonConfig);
    return this.mSavedNonConfig;
  }

  Parcelable saveAllState()
  {
    forcePostponedTransactions();
    endAnimatingAwayFragments();
    execPendingActions();
    this.mStateSaved = true;
    this.mSavedNonConfig = null;
    if ((this.mActive == null) || (this.mActive.size() <= 0));
    FragmentState[] arrayOfFragmentState;
    while (true)
    {
      return null;
      int i = this.mActive.size();
      arrayOfFragmentState = new FragmentState[i];
      int j = 0;
      int k = 0;
      if (k < i)
      {
        Fragment localFragment = (Fragment)this.mActive.valueAt(k);
        FragmentState localFragmentState;
        if (localFragment != null)
        {
          if (localFragment.mIndex < 0)
            throwException(new IllegalStateException("Failure saving state: active " + localFragment + " has cleared index: " + localFragment.mIndex));
          j = 1;
          localFragmentState = new FragmentState(localFragment);
          arrayOfFragmentState[k] = localFragmentState;
          if ((localFragment.mState <= 0) || (localFragmentState.mSavedFragmentState != null))
            break label364;
          localFragmentState.mSavedFragmentState = saveFragmentBasicState(localFragment);
          if (localFragment.mTarget != null)
          {
            if (localFragment.mTarget.mIndex < 0)
              throwException(new IllegalStateException("Failure saving state: " + localFragment + " has target not in fragment manager: " + localFragment.mTarget));
            if (localFragmentState.mSavedFragmentState == null)
              localFragmentState.mSavedFragmentState = new Bundle();
            putFragment(localFragmentState.mSavedFragmentState, "android:target_state", localFragment.mTarget);
            if (localFragment.mTargetRequestCode != 0)
              localFragmentState.mSavedFragmentState.putInt("android:target_req_state", localFragment.mTargetRequestCode);
          }
        }
        while (true)
        {
          if (DEBUG)
            Log.v("FragmentManager", "Saved state of " + localFragment + ": " + localFragmentState.mSavedFragmentState);
          k++;
          break;
          label364: localFragmentState.mSavedFragmentState = localFragment.mSavedFragmentState;
        }
      }
      if (j != 0)
        break;
      if (!DEBUG)
        continue;
      Log.v("FragmentManager", "saveAllState: no fragments!");
      return null;
    }
    int m = this.mAdded.size();
    int[] arrayOfInt = null;
    if (m > 0)
    {
      arrayOfInt = new int[m];
      for (int i2 = 0; i2 < m; i2++)
      {
        arrayOfInt[i2] = ((Fragment)this.mAdded.get(i2)).mIndex;
        if (arrayOfInt[i2] < 0)
          throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(i2) + " has cleared index: " + arrayOfInt[i2]));
        if (!DEBUG)
          continue;
        Log.v("FragmentManager", "saveAllState: adding fragment #" + i2 + ": " + this.mAdded.get(i2));
      }
    }
    ArrayList localArrayList = this.mBackStack;
    BackStackState[] arrayOfBackStackState = null;
    if (localArrayList != null)
    {
      int n = this.mBackStack.size();
      arrayOfBackStackState = null;
      if (n > 0)
      {
        arrayOfBackStackState = new BackStackState[n];
        for (int i1 = 0; i1 < n; i1++)
        {
          arrayOfBackStackState[i1] = new BackStackState((BackStackRecord)this.mBackStack.get(i1));
          if (!DEBUG)
            continue;
          Log.v("FragmentManager", "saveAllState: adding back stack #" + i1 + ": " + this.mBackStack.get(i1));
        }
      }
    }
    FragmentManagerState localFragmentManagerState = new FragmentManagerState();
    localFragmentManagerState.mActive = arrayOfFragmentState;
    localFragmentManagerState.mAdded = arrayOfInt;
    localFragmentManagerState.mBackStack = arrayOfBackStackState;
    if (this.mPrimaryNav != null)
      localFragmentManagerState.mPrimaryNavActiveIndex = this.mPrimaryNav.mIndex;
    localFragmentManagerState.mNextFragmentIndex = this.mNextFragmentIndex;
    saveNonConfig();
    return localFragmentManagerState;
  }

  Bundle saveFragmentBasicState(Fragment paramFragment)
  {
    if (this.mStateBundle == null)
      this.mStateBundle = new Bundle();
    paramFragment.performSaveInstanceState(this.mStateBundle);
    dispatchOnFragmentSaveInstanceState(paramFragment, this.mStateBundle, false);
    boolean bool = this.mStateBundle.isEmpty();
    Bundle localBundle = null;
    if (!bool)
    {
      localBundle = this.mStateBundle;
      this.mStateBundle = null;
    }
    if (paramFragment.mView != null)
      saveFragmentViewState(paramFragment);
    if (paramFragment.mSavedViewState != null)
    {
      if (localBundle == null)
        localBundle = new Bundle();
      localBundle.putSparseParcelableArray("android:view_state", paramFragment.mSavedViewState);
    }
    if (!paramFragment.mUserVisibleHint)
    {
      if (localBundle == null)
        localBundle = new Bundle();
      localBundle.putBoolean("android:user_visible_hint", paramFragment.mUserVisibleHint);
    }
    return localBundle;
  }

  public Fragment.SavedState saveFragmentInstanceState(Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0)
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    int i = paramFragment.mState;
    Fragment.SavedState localSavedState = null;
    if (i > 0)
    {
      Bundle localBundle = saveFragmentBasicState(paramFragment);
      localSavedState = null;
      if (localBundle != null)
        localSavedState = new Fragment.SavedState(localBundle);
    }
    return localSavedState;
  }

  void saveFragmentViewState(Fragment paramFragment)
  {
    if (paramFragment.mInnerView == null)
      return;
    if (this.mStateArray == null)
      this.mStateArray = new SparseArray();
    while (true)
    {
      paramFragment.mInnerView.saveHierarchyState(this.mStateArray);
      if (this.mStateArray.size() <= 0)
        break;
      paramFragment.mSavedViewState = this.mStateArray;
      this.mStateArray = null;
      return;
      this.mStateArray.clear();
    }
  }

  void saveNonConfig()
  {
    SparseArray localSparseArray = this.mActive;
    ArrayList localArrayList1 = null;
    ArrayList localArrayList2 = null;
    if (localSparseArray != null)
      for (int i = 0; i < this.mActive.size(); i++)
      {
        Fragment localFragment = (Fragment)this.mActive.valueAt(i);
        if (localFragment == null)
          continue;
        int k;
        if (localFragment.mRetainInstance)
        {
          if (localArrayList2 == null)
            localArrayList2 = new ArrayList();
          localArrayList2.add(localFragment);
          if (localFragment.mTarget == null)
            break label204;
          k = localFragment.mTarget.mIndex;
          localFragment.mTargetIndex = k;
          if (DEBUG)
            Log.v("FragmentManager", "retainNonConfig: keeping retained " + localFragment);
        }
        if (localFragment.mChildFragmentManager != null)
          localFragment.mChildFragmentManager.saveNonConfig();
        for (FragmentManagerNonConfig localFragmentManagerNonConfig = localFragment.mChildFragmentManager.mSavedNonConfig; ; localFragmentManagerNonConfig = localFragment.mChildNonConfig)
        {
          if ((localArrayList1 != null) || (localFragmentManagerNonConfig == null))
            break label220;
          localArrayList1 = new ArrayList(this.mActive.size());
          for (int j = 0; j < i; j++)
            localArrayList1.add(null);
          label204: k = -1;
          break;
        }
        label220: if (localArrayList1 == null)
          continue;
        localArrayList1.add(localFragmentManagerNonConfig);
      }
    if ((localArrayList2 == null) && (localArrayList1 == null))
    {
      this.mSavedNonConfig = null;
      return;
    }
    this.mSavedNonConfig = new FragmentManagerNonConfig(localArrayList2, localArrayList1);
  }

  public void setBackStackIndex(int paramInt, BackStackRecord paramBackStackRecord)
  {
    monitorenter;
    try
    {
      if (this.mBackStackIndices == null)
        this.mBackStackIndices = new ArrayList();
      int i = this.mBackStackIndices.size();
      if (paramInt < i)
      {
        if (DEBUG)
          Log.v("FragmentManager", "Setting back stack index " + paramInt + " to " + paramBackStackRecord);
        this.mBackStackIndices.set(paramInt, paramBackStackRecord);
      }
      while (true)
      {
        return;
        while (i < paramInt)
        {
          this.mBackStackIndices.add(null);
          if (this.mAvailBackStackIndices == null)
            this.mAvailBackStackIndices = new ArrayList();
          if (DEBUG)
            Log.v("FragmentManager", "Adding available back stack index " + i);
          this.mAvailBackStackIndices.add(Integer.valueOf(i));
          i++;
        }
        if (DEBUG)
          Log.v("FragmentManager", "Adding back stack index " + paramInt + " with " + paramBackStackRecord);
        this.mBackStackIndices.add(paramBackStackRecord);
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public void setPrimaryNavigationFragment(Fragment paramFragment)
  {
    if ((paramFragment != null) && ((this.mActive.get(paramFragment.mIndex) != paramFragment) || ((paramFragment.mHost != null) && (paramFragment.getFragmentManager() != this))))
      throw new IllegalArgumentException("Fragment " + paramFragment + " is not an active fragment of FragmentManager " + this);
    this.mPrimaryNav = paramFragment;
  }

  public void showFragment(Fragment paramFragment)
  {
    if (DEBUG)
      Log.v("FragmentManager", "show: " + paramFragment);
    if (paramFragment.mHidden)
    {
      paramFragment.mHidden = false;
      boolean bool1 = paramFragment.mHiddenChanged;
      boolean bool2 = false;
      if (!bool1)
        bool2 = true;
      paramFragment.mHiddenChanged = bool2;
    }
  }

  void startPendingDeferredFragments()
  {
    if (this.mActive == null);
    while (true)
    {
      return;
      for (int i = 0; i < this.mActive.size(); i++)
      {
        Fragment localFragment = (Fragment)this.mActive.valueAt(i);
        if (localFragment == null)
          continue;
        performPendingDeferredStart(localFragment);
      }
    }
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("FragmentManager{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" in ");
    if (this.mParent != null)
      DebugUtils.buildShortClassTag(this.mParent, localStringBuilder);
    while (true)
    {
      localStringBuilder.append("}}");
      return localStringBuilder.toString();
      DebugUtils.buildShortClassTag(this.mHost, localStringBuilder);
    }
  }

  public void unregisterFragmentLifecycleCallbacks(FragmentManager.FragmentLifecycleCallbacks paramFragmentLifecycleCallbacks)
  {
    CopyOnWriteArrayList localCopyOnWriteArrayList = this.mLifecycleCallbacks;
    monitorenter;
    for (int i = 0; ; i++)
      try
      {
        int j = this.mLifecycleCallbacks.size();
        if (i < j)
        {
          if (((Pair)this.mLifecycleCallbacks.get(i)).first != paramFragmentLifecycleCallbacks)
            continue;
          this.mLifecycleCallbacks.remove(i);
        }
        return;
      }
      finally
      {
        monitorexit;
      }
  }

  private static class AnimateOnHWLayerIfNeededListener extends FragmentManagerImpl.AnimationListenerWrapper
  {
    View mView;

    AnimateOnHWLayerIfNeededListener(View paramView, Animation.AnimationListener paramAnimationListener)
    {
      super(null);
      this.mView = paramView;
    }

    @CallSuper
    public void onAnimationEnd(Animation paramAnimation)
    {
      if ((ViewCompat.isAttachedToWindow(this.mView)) || (Build.VERSION.SDK_INT >= 24))
        this.mView.post(new Runnable()
        {
          public void run()
          {
            FragmentManagerImpl.AnimateOnHWLayerIfNeededListener.this.mView.setLayerType(0, null);
          }
        });
      while (true)
      {
        super.onAnimationEnd(paramAnimation);
        return;
        this.mView.setLayerType(0, null);
      }
    }
  }

  private static class AnimationListenerWrapper
    implements Animation.AnimationListener
  {
    private final Animation.AnimationListener mWrapped;

    private AnimationListenerWrapper(Animation.AnimationListener paramAnimationListener)
    {
      this.mWrapped = paramAnimationListener;
    }

    @CallSuper
    public void onAnimationEnd(Animation paramAnimation)
    {
      if (this.mWrapped != null)
        this.mWrapped.onAnimationEnd(paramAnimation);
    }

    @CallSuper
    public void onAnimationRepeat(Animation paramAnimation)
    {
      if (this.mWrapped != null)
        this.mWrapped.onAnimationRepeat(paramAnimation);
    }

    @CallSuper
    public void onAnimationStart(Animation paramAnimation)
    {
      if (this.mWrapped != null)
        this.mWrapped.onAnimationStart(paramAnimation);
    }
  }

  private static class AnimationOrAnimator
  {
    public final Animation animation;
    public final Animator animator;

    private AnimationOrAnimator(Animator paramAnimator)
    {
      this.animation = null;
      this.animator = paramAnimator;
      if (paramAnimator == null)
        throw new IllegalStateException("Animator cannot be null");
    }

    private AnimationOrAnimator(Animation paramAnimation)
    {
      this.animation = paramAnimation;
      this.animator = null;
      if (paramAnimation == null)
        throw new IllegalStateException("Animation cannot be null");
    }
  }

  private static class AnimatorOnHWLayerIfNeededListener extends AnimatorListenerAdapter
  {
    View mView;

    AnimatorOnHWLayerIfNeededListener(View paramView)
    {
      this.mView = paramView;
    }

    public void onAnimationEnd(Animator paramAnimator)
    {
      this.mView.setLayerType(0, null);
      paramAnimator.removeListener(this);
    }

    public void onAnimationStart(Animator paramAnimator)
    {
      this.mView.setLayerType(2, null);
    }
  }

  static class FragmentTag
  {
    public static final int[] Fragment = { 16842755, 16842960, 16842961 };
    public static final int Fragment_id = 1;
    public static final int Fragment_name = 0;
    public static final int Fragment_tag = 2;
  }

  static abstract interface OpGenerator
  {
    public abstract boolean generateOps(ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1);
  }

  private class PopBackStackState
    implements FragmentManagerImpl.OpGenerator
  {
    final int mFlags;
    final int mId;
    final String mName;

    PopBackStackState(String paramInt1, int paramInt2, int arg4)
    {
      this.mName = paramInt1;
      this.mId = paramInt2;
      int i;
      this.mFlags = i;
    }

    public boolean generateOps(ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1)
    {
      if ((FragmentManagerImpl.this.mPrimaryNav != null) && (this.mId < 0) && (this.mName == null))
      {
        FragmentManager localFragmentManager = FragmentManagerImpl.this.mPrimaryNav.peekChildFragmentManager();
        if ((localFragmentManager != null) && (localFragmentManager.popBackStackImmediate()))
          return false;
      }
      return FragmentManagerImpl.this.popBackStackState(paramArrayList, paramArrayList1, this.mName, this.mId, this.mFlags);
    }
  }

  static class StartEnterTransitionListener
    implements Fragment.OnStartEnterTransitionListener
  {
    private final boolean mIsBack;
    private int mNumPostponed;
    private final BackStackRecord mRecord;

    StartEnterTransitionListener(BackStackRecord paramBackStackRecord, boolean paramBoolean)
    {
      this.mIsBack = paramBoolean;
      this.mRecord = paramBackStackRecord;
    }

    public void cancelTransaction()
    {
      this.mRecord.mManager.completeExecute(this.mRecord, this.mIsBack, false, false);
    }

    public void completeTransaction()
    {
      if (this.mNumPostponed > 0);
      for (int i = 1; ; i = 0)
      {
        FragmentManagerImpl localFragmentManagerImpl1 = this.mRecord.mManager;
        int j = localFragmentManagerImpl1.mAdded.size();
        for (int k = 0; k < j; k++)
        {
          Fragment localFragment = (Fragment)localFragmentManagerImpl1.mAdded.get(k);
          localFragment.setOnStartEnterTransitionListener(null);
          if ((i == 0) || (!localFragment.isPostponed()))
            continue;
          localFragment.startPostponedEnterTransition();
        }
      }
      FragmentManagerImpl localFragmentManagerImpl2 = this.mRecord.mManager;
      BackStackRecord localBackStackRecord = this.mRecord;
      boolean bool1 = this.mIsBack;
      boolean bool2 = false;
      if (i == 0)
        bool2 = true;
      localFragmentManagerImpl2.completeExecute(localBackStackRecord, bool1, bool2, true);
    }

    public boolean isReady()
    {
      return this.mNumPostponed == 0;
    }

    public void onStartEnterTransition()
    {
      this.mNumPostponed = (-1 + this.mNumPostponed);
      if (this.mNumPostponed != 0)
        return;
      this.mRecord.mManager.scheduleCommit();
    }

    public void startListening()
    {
      this.mNumPostponed = (1 + this.mNumPostponed);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.app.FragmentManagerImpl
 * JD-Core Version:    0.6.0
 */