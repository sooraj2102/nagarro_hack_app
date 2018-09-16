package android.support.transition;

import I;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public abstract class Transition
  implements Cloneable
{
  static final boolean DBG = false;
  private static final int[] DEFAULT_MATCH_ORDER = { 2, 1, 3, 4 };
  private static final String LOG_TAG = "Transition";
  private static final int MATCH_FIRST = 1;
  public static final int MATCH_ID = 3;
  private static final String MATCH_ID_STR = "id";
  public static final int MATCH_INSTANCE = 1;
  private static final String MATCH_INSTANCE_STR = "instance";
  public static final int MATCH_ITEM_ID = 4;
  private static final String MATCH_ITEM_ID_STR = "itemId";
  private static final int MATCH_LAST = 4;
  public static final int MATCH_NAME = 2;
  private static final String MATCH_NAME_STR = "name";
  private static final PathMotion STRAIGHT_PATH_MOTION = new PathMotion()
  {
    public Path getPath(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      Path localPath = new Path();
      localPath.moveTo(paramFloat1, paramFloat2);
      localPath.lineTo(paramFloat3, paramFloat4);
      return localPath;
    }
  };
  private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal();
  private ArrayList<Animator> mAnimators = new ArrayList();
  boolean mCanRemoveViews = false;
  private ArrayList<Animator> mCurrentAnimators = new ArrayList();
  long mDuration = -1L;
  private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
  private ArrayList<TransitionValues> mEndValuesList;
  private boolean mEnded = false;
  private EpicenterCallback mEpicenterCallback;
  private TimeInterpolator mInterpolator = null;
  private ArrayList<TransitionListener> mListeners = null;
  private int[] mMatchOrder = DEFAULT_MATCH_ORDER;
  private String mName = getClass().getName();
  private ArrayMap<String, String> mNameOverrides;
  private int mNumInstances = 0;
  TransitionSet mParent = null;
  private PathMotion mPathMotion = STRAIGHT_PATH_MOTION;
  private boolean mPaused = false;
  TransitionPropagation mPropagation;
  private ViewGroup mSceneRoot = null;
  private long mStartDelay = -1L;
  private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
  private ArrayList<TransitionValues> mStartValuesList;
  private ArrayList<View> mTargetChildExcludes = null;
  private ArrayList<View> mTargetExcludes = null;
  private ArrayList<Integer> mTargetIdChildExcludes = null;
  private ArrayList<Integer> mTargetIdExcludes = null;
  ArrayList<Integer> mTargetIds = new ArrayList();
  private ArrayList<String> mTargetNameExcludes = null;
  private ArrayList<String> mTargetNames = null;
  private ArrayList<Class> mTargetTypeChildExcludes = null;
  private ArrayList<Class> mTargetTypeExcludes = null;
  private ArrayList<Class> mTargetTypes = null;
  ArrayList<View> mTargets = new ArrayList();

  public Transition()
  {
  }

  public Transition(Context paramContext, AttributeSet paramAttributeSet)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.TRANSITION);
    XmlResourceParser localXmlResourceParser = (XmlResourceParser)paramAttributeSet;
    long l1 = TypedArrayUtils.getNamedInt(localTypedArray, localXmlResourceParser, "duration", 1, -1);
    if (l1 >= 0L)
      setDuration(l1);
    long l2 = TypedArrayUtils.getNamedInt(localTypedArray, localXmlResourceParser, "startDelay", 2, -1);
    if (l2 > 0L)
      setStartDelay(l2);
    int i = TypedArrayUtils.getNamedResourceId(localTypedArray, localXmlResourceParser, "interpolator", 0, 0);
    if (i > 0)
      setInterpolator(AnimationUtils.loadInterpolator(paramContext, i));
    String str = TypedArrayUtils.getNamedString(localTypedArray, localXmlResourceParser, "matchOrder", 3);
    if (str != null)
      setMatchOrder(parseMatchOrder(str));
    localTypedArray.recycle();
  }

  private void addUnmatched(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2)
  {
    for (int i = 0; i < paramArrayMap1.size(); i++)
    {
      TransitionValues localTransitionValues2 = (TransitionValues)paramArrayMap1.valueAt(i);
      if (!isValidTarget(localTransitionValues2.view))
        continue;
      this.mStartValuesList.add(localTransitionValues2);
      this.mEndValuesList.add(null);
    }
    for (int j = 0; j < paramArrayMap2.size(); j++)
    {
      TransitionValues localTransitionValues1 = (TransitionValues)paramArrayMap2.valueAt(j);
      if (!isValidTarget(localTransitionValues1.view))
        continue;
      this.mEndValuesList.add(localTransitionValues1);
      this.mStartValuesList.add(null);
    }
  }

  private static void addViewValues(TransitionValuesMaps paramTransitionValuesMaps, View paramView, TransitionValues paramTransitionValues)
  {
    paramTransitionValuesMaps.mViewValues.put(paramView, paramTransitionValues);
    int i = paramView.getId();
    String str;
    if (i >= 0)
    {
      if (paramTransitionValuesMaps.mIdValues.indexOfKey(i) >= 0)
        paramTransitionValuesMaps.mIdValues.put(i, null);
    }
    else
    {
      str = ViewCompat.getTransitionName(paramView);
      if (str != null)
      {
        if (!paramTransitionValuesMaps.mNameValues.containsKey(str))
          break label183;
        paramTransitionValuesMaps.mNameValues.put(str, null);
      }
    }
    long l;
    while (true)
    {
      if ((paramView.getParent() instanceof ListView))
      {
        ListView localListView = (ListView)paramView.getParent();
        if (localListView.getAdapter().hasStableIds())
        {
          l = localListView.getItemIdAtPosition(localListView.getPositionForView(paramView));
          if (paramTransitionValuesMaps.mItemIdValues.indexOfKey(l) < 0)
            break label197;
          View localView = (View)paramTransitionValuesMaps.mItemIdValues.get(l);
          if (localView != null)
          {
            ViewCompat.setHasTransientState(localView, false);
            paramTransitionValuesMaps.mItemIdValues.put(l, null);
          }
        }
      }
      return;
      paramTransitionValuesMaps.mIdValues.put(i, paramView);
      break;
      label183: paramTransitionValuesMaps.mNameValues.put(str, paramView);
    }
    label197: ViewCompat.setHasTransientState(paramView, true);
    paramTransitionValuesMaps.mItemIdValues.put(l, paramView);
  }

  private static boolean alreadyContains(int[] paramArrayOfInt, int paramInt)
  {
    int i = paramArrayOfInt[paramInt];
    for (int j = 0; j < paramInt; j++)
      if (paramArrayOfInt[j] == i)
        return true;
    return false;
  }

  private void captureHierarchy(View paramView, boolean paramBoolean)
  {
    if (paramView == null);
    while (true)
    {
      return;
      int i = paramView.getId();
      if (((this.mTargetIdExcludes != null) && (this.mTargetIdExcludes.contains(Integer.valueOf(i)))) || ((this.mTargetExcludes != null) && (this.mTargetExcludes.contains(paramView))))
        continue;
      if (this.mTargetTypeExcludes != null)
      {
        int n = this.mTargetTypeExcludes.size();
        for (int i1 = 0; ; i1++)
        {
          if (i1 >= n)
            break label100;
          if (((Class)this.mTargetTypeExcludes.get(i1)).isInstance(paramView))
            break;
        }
      }
      label100: TransitionValues localTransitionValues;
      if ((paramView.getParent() instanceof ViewGroup))
      {
        localTransitionValues = new TransitionValues();
        localTransitionValues.view = paramView;
        if (!paramBoolean)
          break label262;
        captureStartValues(localTransitionValues);
        label135: localTransitionValues.mTargetedTransitions.add(this);
        capturePropagationValues(localTransitionValues);
        if (!paramBoolean)
          break label271;
        addViewValues(this.mStartValues, paramView, localTransitionValues);
      }
      while (true)
      {
        if ((!(paramView instanceof ViewGroup)) || ((this.mTargetIdChildExcludes != null) && (this.mTargetIdChildExcludes.contains(Integer.valueOf(i)))) || ((this.mTargetChildExcludes != null) && (this.mTargetChildExcludes.contains(paramView))))
          break label282;
        if (this.mTargetTypeChildExcludes == null)
          break label284;
        int k = this.mTargetTypeChildExcludes.size();
        for (int m = 0; ; m++)
        {
          if (m >= k)
            break label284;
          if (((Class)this.mTargetTypeChildExcludes.get(m)).isInstance(paramView))
            break;
        }
        label262: captureEndValues(localTransitionValues);
        break label135;
        label271: addViewValues(this.mEndValues, paramView, localTransitionValues);
      }
      label282: continue;
      label284: ViewGroup localViewGroup = (ViewGroup)paramView;
      for (int j = 0; j < localViewGroup.getChildCount(); j++)
        captureHierarchy(localViewGroup.getChildAt(j), paramBoolean);
    }
  }

  private ArrayList<Integer> excludeId(ArrayList<Integer> paramArrayList, int paramInt, boolean paramBoolean)
  {
    if (paramInt > 0)
    {
      if (paramBoolean)
        paramArrayList = ArrayListManager.add(paramArrayList, Integer.valueOf(paramInt));
    }
    else
      return paramArrayList;
    return ArrayListManager.remove(paramArrayList, Integer.valueOf(paramInt));
  }

  private static <T> ArrayList<T> excludeObject(ArrayList<T> paramArrayList, T paramT, boolean paramBoolean)
  {
    if (paramT != null)
    {
      if (paramBoolean)
        paramArrayList = ArrayListManager.add(paramArrayList, paramT);
    }
    else
      return paramArrayList;
    return ArrayListManager.remove(paramArrayList, paramT);
  }

  private ArrayList<Class> excludeType(ArrayList<Class> paramArrayList, Class paramClass, boolean paramBoolean)
  {
    if (paramClass != null)
    {
      if (paramBoolean)
        paramArrayList = ArrayListManager.add(paramArrayList, paramClass);
    }
    else
      return paramArrayList;
    return ArrayListManager.remove(paramArrayList, paramClass);
  }

  private ArrayList<View> excludeView(ArrayList<View> paramArrayList, View paramView, boolean paramBoolean)
  {
    if (paramView != null)
    {
      if (paramBoolean)
        paramArrayList = ArrayListManager.add(paramArrayList, paramView);
    }
    else
      return paramArrayList;
    return ArrayListManager.remove(paramArrayList, paramView);
  }

  private static ArrayMap<Animator, AnimationInfo> getRunningAnimators()
  {
    ArrayMap localArrayMap = (ArrayMap)sRunningAnimators.get();
    if (localArrayMap == null)
    {
      localArrayMap = new ArrayMap();
      sRunningAnimators.set(localArrayMap);
    }
    return localArrayMap;
  }

  private static boolean isValidMatch(int paramInt)
  {
    return (paramInt >= 1) && (paramInt <= 4);
  }

  private static boolean isValueChanged(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2, String paramString)
  {
    Object localObject1 = paramTransitionValues1.values.get(paramString);
    Object localObject2 = paramTransitionValues2.values.get(paramString);
    if ((localObject1 == null) && (localObject2 == null))
      return false;
    if ((localObject1 == null) || (localObject2 == null))
      return true;
    return !localObject1.equals(localObject2);
  }

  private void matchIds(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2, SparseArray<View> paramSparseArray1, SparseArray<View> paramSparseArray2)
  {
    int i = paramSparseArray1.size();
    for (int j = 0; j < i; j++)
    {
      View localView1 = (View)paramSparseArray1.valueAt(j);
      if ((localView1 == null) || (!isValidTarget(localView1)))
        continue;
      View localView2 = (View)paramSparseArray2.get(paramSparseArray1.keyAt(j));
      if ((localView2 == null) || (!isValidTarget(localView2)))
        continue;
      TransitionValues localTransitionValues1 = (TransitionValues)paramArrayMap1.get(localView1);
      TransitionValues localTransitionValues2 = (TransitionValues)paramArrayMap2.get(localView2);
      if ((localTransitionValues1 == null) || (localTransitionValues2 == null))
        continue;
      this.mStartValuesList.add(localTransitionValues1);
      this.mEndValuesList.add(localTransitionValues2);
      paramArrayMap1.remove(localView1);
      paramArrayMap2.remove(localView2);
    }
  }

  private void matchInstances(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2)
  {
    for (int i = -1 + paramArrayMap1.size(); i >= 0; i--)
    {
      View localView = (View)paramArrayMap1.keyAt(i);
      if ((localView == null) || (!isValidTarget(localView)))
        continue;
      TransitionValues localTransitionValues1 = (TransitionValues)paramArrayMap2.remove(localView);
      if ((localTransitionValues1 == null) || (localTransitionValues1.view == null) || (!isValidTarget(localTransitionValues1.view)))
        continue;
      TransitionValues localTransitionValues2 = (TransitionValues)paramArrayMap1.removeAt(i);
      this.mStartValuesList.add(localTransitionValues2);
      this.mEndValuesList.add(localTransitionValues1);
    }
  }

  private void matchItemIds(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2, LongSparseArray<View> paramLongSparseArray1, LongSparseArray<View> paramLongSparseArray2)
  {
    int i = paramLongSparseArray1.size();
    for (int j = 0; j < i; j++)
    {
      View localView1 = (View)paramLongSparseArray1.valueAt(j);
      if ((localView1 == null) || (!isValidTarget(localView1)))
        continue;
      View localView2 = (View)paramLongSparseArray2.get(paramLongSparseArray1.keyAt(j));
      if ((localView2 == null) || (!isValidTarget(localView2)))
        continue;
      TransitionValues localTransitionValues1 = (TransitionValues)paramArrayMap1.get(localView1);
      TransitionValues localTransitionValues2 = (TransitionValues)paramArrayMap2.get(localView2);
      if ((localTransitionValues1 == null) || (localTransitionValues2 == null))
        continue;
      this.mStartValuesList.add(localTransitionValues1);
      this.mEndValuesList.add(localTransitionValues2);
      paramArrayMap1.remove(localView1);
      paramArrayMap2.remove(localView2);
    }
  }

  private void matchNames(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2, ArrayMap<String, View> paramArrayMap3, ArrayMap<String, View> paramArrayMap4)
  {
    int i = paramArrayMap3.size();
    for (int j = 0; j < i; j++)
    {
      View localView1 = (View)paramArrayMap3.valueAt(j);
      if ((localView1 == null) || (!isValidTarget(localView1)))
        continue;
      View localView2 = (View)paramArrayMap4.get(paramArrayMap3.keyAt(j));
      if ((localView2 == null) || (!isValidTarget(localView2)))
        continue;
      TransitionValues localTransitionValues1 = (TransitionValues)paramArrayMap1.get(localView1);
      TransitionValues localTransitionValues2 = (TransitionValues)paramArrayMap2.get(localView2);
      if ((localTransitionValues1 == null) || (localTransitionValues2 == null))
        continue;
      this.mStartValuesList.add(localTransitionValues1);
      this.mEndValuesList.add(localTransitionValues2);
      paramArrayMap1.remove(localView1);
      paramArrayMap2.remove(localView2);
    }
  }

  private void matchStartAndEnd(TransitionValuesMaps paramTransitionValuesMaps1, TransitionValuesMaps paramTransitionValuesMaps2)
  {
    ArrayMap localArrayMap1 = new ArrayMap(paramTransitionValuesMaps1.mViewValues);
    ArrayMap localArrayMap2 = new ArrayMap(paramTransitionValuesMaps2.mViewValues);
    int i = 0;
    if (i < this.mMatchOrder.length)
    {
      switch (this.mMatchOrder[i])
      {
      default:
      case 1:
      case 2:
      case 3:
      case 4:
      }
      while (true)
      {
        i++;
        break;
        matchInstances(localArrayMap1, localArrayMap2);
        continue;
        matchNames(localArrayMap1, localArrayMap2, paramTransitionValuesMaps1.mNameValues, paramTransitionValuesMaps2.mNameValues);
        continue;
        matchIds(localArrayMap1, localArrayMap2, paramTransitionValuesMaps1.mIdValues, paramTransitionValuesMaps2.mIdValues);
        continue;
        matchItemIds(localArrayMap1, localArrayMap2, paramTransitionValuesMaps1.mItemIdValues, paramTransitionValuesMaps2.mItemIdValues);
      }
    }
    addUnmatched(localArrayMap1, localArrayMap2);
  }

  private static int[] parseMatchOrder(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    Object localObject = new int[localStringTokenizer.countTokens()];
    int i = 0;
    if (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken().trim();
      if ("id".equalsIgnoreCase(str))
        localObject[i] = 3;
      while (true)
      {
        i++;
        break;
        if ("instance".equalsIgnoreCase(str))
        {
          localObject[i] = 1;
          continue;
        }
        if ("name".equalsIgnoreCase(str))
        {
          localObject[i] = 2;
          continue;
        }
        if ("itemId".equalsIgnoreCase(str))
        {
          localObject[i] = 4;
          continue;
        }
        if (!str.isEmpty())
          break label142;
        int[] arrayOfInt = new int[-1 + localObject.length];
        System.arraycopy(localObject, 0, arrayOfInt, 0, i);
        localObject = arrayOfInt;
        i--;
      }
      label142: throw new InflateException("Unknown match type in matchOrder: '" + str + "'");
    }
    return (I)localObject;
  }

  private void runAnimator(Animator paramAnimator, ArrayMap<Animator, AnimationInfo> paramArrayMap)
  {
    if (paramAnimator != null)
    {
      paramAnimator.addListener(new AnimatorListenerAdapter(paramArrayMap)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          this.val$runningAnimators.remove(paramAnimator);
          Transition.this.mCurrentAnimators.remove(paramAnimator);
        }

        public void onAnimationStart(Animator paramAnimator)
        {
          Transition.this.mCurrentAnimators.add(paramAnimator);
        }
      });
      animate(paramAnimator);
    }
  }

  @NonNull
  public Transition addListener(@NonNull TransitionListener paramTransitionListener)
  {
    if (this.mListeners == null)
      this.mListeners = new ArrayList();
    this.mListeners.add(paramTransitionListener);
    return this;
  }

  @NonNull
  public Transition addTarget(@IdRes int paramInt)
  {
    if (paramInt > 0)
      this.mTargetIds.add(Integer.valueOf(paramInt));
    return this;
  }

  @NonNull
  public Transition addTarget(@NonNull View paramView)
  {
    this.mTargets.add(paramView);
    return this;
  }

  @NonNull
  public Transition addTarget(@NonNull Class paramClass)
  {
    if (this.mTargetTypes == null)
      this.mTargetTypes = new ArrayList();
    this.mTargetTypes.add(paramClass);
    return this;
  }

  @NonNull
  public Transition addTarget(@NonNull String paramString)
  {
    if (this.mTargetNames == null)
      this.mTargetNames = new ArrayList();
    this.mTargetNames.add(paramString);
    return this;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void animate(Animator paramAnimator)
  {
    if (paramAnimator == null)
    {
      end();
      return;
    }
    if (getDuration() >= 0L)
      paramAnimator.setDuration(getDuration());
    if (getStartDelay() >= 0L)
      paramAnimator.setStartDelay(getStartDelay());
    if (getInterpolator() != null)
      paramAnimator.setInterpolator(getInterpolator());
    paramAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        Transition.this.end();
        paramAnimator.removeListener(this);
      }
    });
    paramAnimator.start();
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void cancel()
  {
    for (int i = -1 + this.mCurrentAnimators.size(); i >= 0; i--)
      ((Animator)this.mCurrentAnimators.get(i)).cancel();
    if ((this.mListeners != null) && (this.mListeners.size() > 0))
    {
      ArrayList localArrayList = (ArrayList)this.mListeners.clone();
      int j = localArrayList.size();
      for (int k = 0; k < j; k++)
        ((TransitionListener)localArrayList.get(k)).onTransitionCancel(this);
    }
  }

  public abstract void captureEndValues(@NonNull TransitionValues paramTransitionValues);

  void capturePropagationValues(TransitionValues paramTransitionValues)
  {
    String[] arrayOfString;
    if ((this.mPropagation != null) && (!paramTransitionValues.values.isEmpty()))
    {
      arrayOfString = this.mPropagation.getPropagationProperties();
      if (arrayOfString != null);
    }
    else
    {
      return;
    }
    int i = 1;
    for (int j = 0; ; j++)
    {
      if (j < arrayOfString.length)
      {
        if (paramTransitionValues.values.containsKey(arrayOfString[j]))
          continue;
        i = 0;
      }
      if (i != 0)
        break;
      this.mPropagation.captureValues(paramTransitionValues);
      return;
    }
  }

  public abstract void captureStartValues(@NonNull TransitionValues paramTransitionValues);

  void captureValues(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    clearValues(paramBoolean);
    if (((this.mTargetIds.size() > 0) || (this.mTargets.size() > 0)) && ((this.mTargetNames == null) || (this.mTargetNames.isEmpty())) && ((this.mTargetTypes == null) || (this.mTargetTypes.isEmpty())))
    {
      int i = 0;
      if (i < this.mTargetIds.size())
      {
        View localView3 = paramViewGroup.findViewById(((Integer)this.mTargetIds.get(i)).intValue());
        TransitionValues localTransitionValues2;
        if (localView3 != null)
        {
          localTransitionValues2 = new TransitionValues();
          localTransitionValues2.view = localView3;
          if (!paramBoolean)
            break label160;
          captureStartValues(localTransitionValues2);
          label123: localTransitionValues2.mTargetedTransitions.add(this);
          capturePropagationValues(localTransitionValues2);
          if (!paramBoolean)
            break label169;
          addViewValues(this.mStartValues, localView3, localTransitionValues2);
        }
        while (true)
        {
          i++;
          break;
          label160: captureEndValues(localTransitionValues2);
          break label123;
          label169: addViewValues(this.mEndValues, localView3, localTransitionValues2);
        }
      }
      int j = 0;
      if (j < this.mTargets.size())
      {
        View localView2 = (View)this.mTargets.get(j);
        TransitionValues localTransitionValues1 = new TransitionValues();
        localTransitionValues1.view = localView2;
        if (paramBoolean)
        {
          captureStartValues(localTransitionValues1);
          label238: localTransitionValues1.mTargetedTransitions.add(this);
          capturePropagationValues(localTransitionValues1);
          if (!paramBoolean)
            break label284;
          addViewValues(this.mStartValues, localView2, localTransitionValues1);
        }
        while (true)
        {
          j++;
          break;
          captureEndValues(localTransitionValues1);
          break label238;
          label284: addViewValues(this.mEndValues, localView2, localTransitionValues1);
        }
      }
    }
    else
    {
      captureHierarchy(paramViewGroup, paramBoolean);
    }
    if ((!paramBoolean) && (this.mNameOverrides != null))
    {
      int k = this.mNameOverrides.size();
      ArrayList localArrayList = new ArrayList(k);
      for (int m = 0; m < k; m++)
      {
        String str2 = (String)this.mNameOverrides.keyAt(m);
        localArrayList.add(this.mStartValues.mNameValues.remove(str2));
      }
      for (int n = 0; n < k; n++)
      {
        View localView1 = (View)localArrayList.get(n);
        if (localView1 == null)
          continue;
        String str1 = (String)this.mNameOverrides.valueAt(n);
        this.mStartValues.mNameValues.put(str1, localView1);
      }
    }
  }

  void clearValues(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mStartValues.mViewValues.clear();
      this.mStartValues.mIdValues.clear();
      this.mStartValues.mItemIdValues.clear();
      return;
    }
    this.mEndValues.mViewValues.clear();
    this.mEndValues.mIdValues.clear();
    this.mEndValues.mItemIdValues.clear();
  }

  public Transition clone()
  {
    try
    {
      Transition localTransition = (Transition)super.clone();
      localTransition.mAnimators = new ArrayList();
      localTransition.mStartValues = new TransitionValuesMaps();
      localTransition.mEndValues = new TransitionValuesMaps();
      localTransition.mStartValuesList = null;
      localTransition.mEndValuesList = null;
      return localTransition;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
    }
    return null;
  }

  @Nullable
  public Animator createAnimator(@NonNull ViewGroup paramViewGroup, @Nullable TransitionValues paramTransitionValues1, @Nullable TransitionValues paramTransitionValues2)
  {
    return null;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void createAnimators(ViewGroup paramViewGroup, TransitionValuesMaps paramTransitionValuesMaps1, TransitionValuesMaps paramTransitionValuesMaps2, ArrayList<TransitionValues> paramArrayList1, ArrayList<TransitionValues> paramArrayList2)
  {
    ArrayMap localArrayMap = getRunningAnimators();
    long l1 = 9223372036854775807L;
    SparseIntArray localSparseIntArray = new SparseIntArray();
    int i = paramArrayList1.size();
    int j = 0;
    label387: if (j < i)
    {
      TransitionValues localTransitionValues1 = (TransitionValues)paramArrayList1.get(j);
      TransitionValues localTransitionValues2 = (TransitionValues)paramArrayList2.get(j);
      if ((localTransitionValues1 != null) && (!localTransitionValues1.mTargetedTransitions.contains(this)))
        localTransitionValues1 = null;
      if ((localTransitionValues2 != null) && (!localTransitionValues2.mTargetedTransitions.contains(this)))
        localTransitionValues2 = null;
      if ((localTransitionValues1 == null) && (localTransitionValues2 == null));
      label299: label301: label311: label479: label485: label496: 
      while (true)
      {
        j++;
        break;
        if ((localTransitionValues1 == null) || (localTransitionValues2 == null) || (isTransitionRequired(localTransitionValues1, localTransitionValues2)));
        Animator localAnimator2;
        View localView;
        TransitionValues localTransitionValues3;
        for (int n = 1; ; n = 0)
        {
          if (n == 0)
            break label299;
          localAnimator2 = createAnimator(paramViewGroup, localTransitionValues1, localTransitionValues2);
          if (localAnimator2 == null)
            break;
          if (localTransitionValues2 == null)
            break label485;
          localView = localTransitionValues2.view;
          String[] arrayOfString = getTransitionProperties();
          localTransitionValues3 = null;
          if (localView == null)
            break label387;
          localTransitionValues3 = null;
          if (arrayOfString == null)
            break label387;
          int i1 = arrayOfString.length;
          localTransitionValues3 = null;
          if (i1 <= 0)
            break label387;
          localTransitionValues3 = new TransitionValues();
          localTransitionValues3.view = localView;
          TransitionValues localTransitionValues4 = (TransitionValues)paramTransitionValuesMaps2.mViewValues.get(localView);
          if (localTransitionValues4 == null)
            break label301;
          for (int i4 = 0; ; i4++)
          {
            int i5 = arrayOfString.length;
            if (i4 >= i5)
              break;
            localTransitionValues3.values.put(arrayOfString[i4], localTransitionValues4.values.get(arrayOfString[i4]));
          }
        }
        continue;
        int i2 = localArrayMap.size();
        int i3 = 0;
        if (i3 < i2)
        {
          AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.get((Animator)localArrayMap.keyAt(i3));
          if ((localAnimationInfo.mValues == null) || (localAnimationInfo.mView != localView) || (!localAnimationInfo.mName.equals(getName())) || (!localAnimationInfo.mValues.equals(localTransitionValues3)))
            break label479;
          localAnimator2 = null;
        }
        while (true)
        {
          if (localAnimator2 == null)
            break label496;
          if (this.mPropagation != null)
          {
            long l2 = this.mPropagation.getStartDelay(paramViewGroup, this, localTransitionValues1, localTransitionValues2);
            localSparseIntArray.put(this.mAnimators.size(), (int)l2);
            l1 = Math.min(l2, l1);
          }
          localArrayMap.put(localAnimator2, new AnimationInfo(localView, getName(), this, ViewUtils.getWindowId(paramViewGroup), localTransitionValues3));
          this.mAnimators.add(localAnimator2);
          break;
          i3++;
          break label311;
          localView = localTransitionValues1.view;
          localTransitionValues3 = null;
        }
      }
    }
    if (l1 != 0L)
      for (int k = 0; k < localSparseIntArray.size(); k++)
      {
        int m = localSparseIntArray.keyAt(k);
        Animator localAnimator1 = (Animator)this.mAnimators.get(m);
        localAnimator1.setStartDelay(localSparseIntArray.valueAt(k) - l1 + localAnimator1.getStartDelay());
      }
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void end()
  {
    this.mNumInstances = (-1 + this.mNumInstances);
    if (this.mNumInstances == 0)
    {
      if ((this.mListeners != null) && (this.mListeners.size() > 0))
      {
        ArrayList localArrayList = (ArrayList)this.mListeners.clone();
        int k = localArrayList.size();
        for (int m = 0; m < k; m++)
          ((TransitionListener)localArrayList.get(m)).onTransitionEnd(this);
      }
      for (int i = 0; i < this.mStartValues.mItemIdValues.size(); i++)
      {
        View localView2 = (View)this.mStartValues.mItemIdValues.valueAt(i);
        if (localView2 == null)
          continue;
        ViewCompat.setHasTransientState(localView2, false);
      }
      for (int j = 0; j < this.mEndValues.mItemIdValues.size(); j++)
      {
        View localView1 = (View)this.mEndValues.mItemIdValues.valueAt(j);
        if (localView1 == null)
          continue;
        ViewCompat.setHasTransientState(localView1, false);
      }
      this.mEnded = true;
    }
  }

  @NonNull
  public Transition excludeChildren(@IdRes int paramInt, boolean paramBoolean)
  {
    this.mTargetIdChildExcludes = excludeId(this.mTargetIdChildExcludes, paramInt, paramBoolean);
    return this;
  }

  @NonNull
  public Transition excludeChildren(@NonNull View paramView, boolean paramBoolean)
  {
    this.mTargetChildExcludes = excludeView(this.mTargetChildExcludes, paramView, paramBoolean);
    return this;
  }

  @NonNull
  public Transition excludeChildren(@NonNull Class paramClass, boolean paramBoolean)
  {
    this.mTargetTypeChildExcludes = excludeType(this.mTargetTypeChildExcludes, paramClass, paramBoolean);
    return this;
  }

  @NonNull
  public Transition excludeTarget(@IdRes int paramInt, boolean paramBoolean)
  {
    this.mTargetIdExcludes = excludeId(this.mTargetIdExcludes, paramInt, paramBoolean);
    return this;
  }

  @NonNull
  public Transition excludeTarget(@NonNull View paramView, boolean paramBoolean)
  {
    this.mTargetExcludes = excludeView(this.mTargetExcludes, paramView, paramBoolean);
    return this;
  }

  @NonNull
  public Transition excludeTarget(@NonNull Class paramClass, boolean paramBoolean)
  {
    this.mTargetTypeExcludes = excludeType(this.mTargetTypeExcludes, paramClass, paramBoolean);
    return this;
  }

  @NonNull
  public Transition excludeTarget(@NonNull String paramString, boolean paramBoolean)
  {
    this.mTargetNameExcludes = excludeObject(this.mTargetNameExcludes, paramString, paramBoolean);
    return this;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  void forceToEnd(ViewGroup paramViewGroup)
  {
    ArrayMap localArrayMap = getRunningAnimators();
    int i = localArrayMap.size();
    if (paramViewGroup != null)
    {
      WindowIdImpl localWindowIdImpl = ViewUtils.getWindowId(paramViewGroup);
      for (int j = i - 1; j >= 0; j--)
      {
        AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.valueAt(j);
        if ((localAnimationInfo.mView == null) || (localWindowIdImpl == null) || (!localWindowIdImpl.equals(localAnimationInfo.mWindowId)))
          continue;
        ((Animator)localArrayMap.keyAt(j)).end();
      }
    }
  }

  public long getDuration()
  {
    return this.mDuration;
  }

  @Nullable
  public Rect getEpicenter()
  {
    if (this.mEpicenterCallback == null)
      return null;
    return this.mEpicenterCallback.onGetEpicenter(this);
  }

  @Nullable
  public EpicenterCallback getEpicenterCallback()
  {
    return this.mEpicenterCallback;
  }

  @Nullable
  public TimeInterpolator getInterpolator()
  {
    return this.mInterpolator;
  }

  TransitionValues getMatchedTransitionValues(View paramView, boolean paramBoolean)
  {
    TransitionValues localTransitionValues1;
    if (this.mParent != null)
      localTransitionValues1 = this.mParent.getMatchedTransitionValues(paramView, paramBoolean);
    int j;
    int k;
    while (true)
    {
      return localTransitionValues1;
      if (paramBoolean);
      for (ArrayList localArrayList1 = this.mStartValuesList; localArrayList1 == null; localArrayList1 = this.mEndValuesList)
        return null;
      int i = localArrayList1.size();
      j = -1;
      k = 0;
      if (k < i)
      {
        TransitionValues localTransitionValues2 = (TransitionValues)localArrayList1.get(k);
        if (localTransitionValues2 == null)
          return null;
        if (localTransitionValues2.view != paramView)
          break;
        j = k;
      }
      else
      {
        localTransitionValues1 = null;
        if (j < 0)
          continue;
        if (!paramBoolean)
          break label129;
      }
    }
    label129: for (ArrayList localArrayList2 = this.mEndValuesList; ; localArrayList2 = this.mStartValuesList)
    {
      return (TransitionValues)localArrayList2.get(j);
      k++;
      break;
    }
  }

  @NonNull
  public String getName()
  {
    return this.mName;
  }

  @NonNull
  public PathMotion getPathMotion()
  {
    return this.mPathMotion;
  }

  @Nullable
  public TransitionPropagation getPropagation()
  {
    return this.mPropagation;
  }

  public long getStartDelay()
  {
    return this.mStartDelay;
  }

  @NonNull
  public List<Integer> getTargetIds()
  {
    return this.mTargetIds;
  }

  @Nullable
  public List<String> getTargetNames()
  {
    return this.mTargetNames;
  }

  @Nullable
  public List<Class> getTargetTypes()
  {
    return this.mTargetTypes;
  }

  @NonNull
  public List<View> getTargets()
  {
    return this.mTargets;
  }

  @Nullable
  public String[] getTransitionProperties()
  {
    return null;
  }

  @Nullable
  public TransitionValues getTransitionValues(@NonNull View paramView, boolean paramBoolean)
  {
    if (this.mParent != null)
      return this.mParent.getTransitionValues(paramView, paramBoolean);
    if (paramBoolean);
    for (TransitionValuesMaps localTransitionValuesMaps = this.mStartValues; ; localTransitionValuesMaps = this.mEndValues)
      return (TransitionValues)localTransitionValuesMaps.mViewValues.get(paramView);
  }

  public boolean isTransitionRequired(@Nullable TransitionValues paramTransitionValues1, @Nullable TransitionValues paramTransitionValues2)
  {
    int i = 0;
    String[] arrayOfString;
    int j;
    if (paramTransitionValues1 != null)
    {
      i = 0;
      if (paramTransitionValues2 != null)
      {
        arrayOfString = getTransitionProperties();
        if (arrayOfString == null)
          break label63;
        j = arrayOfString.length;
      }
    }
    for (int k = 0; ; k++)
    {
      i = 0;
      if (k < j)
      {
        if (!isValueChanged(paramTransitionValues1, paramTransitionValues2, arrayOfString[k]))
          continue;
        i = 1;
      }
      return i;
    }
    label63: Iterator localIterator = paramTransitionValues1.values.keySet().iterator();
    do
    {
      boolean bool = localIterator.hasNext();
      i = 0;
      if (!bool)
        break;
    }
    while (!isValueChanged(paramTransitionValues1, paramTransitionValues2, (String)localIterator.next()));
    return true;
  }

  boolean isValidTarget(View paramView)
  {
    int i = paramView.getId();
    if ((this.mTargetIdExcludes != null) && (this.mTargetIdExcludes.contains(Integer.valueOf(i))))
      return false;
    if ((this.mTargetExcludes != null) && (this.mTargetExcludes.contains(paramView)))
      return false;
    if (this.mTargetTypeExcludes != null)
    {
      int k = this.mTargetTypeExcludes.size();
      for (int m = 0; m < k; m++)
        if (((Class)this.mTargetTypeExcludes.get(m)).isInstance(paramView))
          return false;
    }
    if ((this.mTargetNameExcludes != null) && (ViewCompat.getTransitionName(paramView) != null) && (this.mTargetNameExcludes.contains(ViewCompat.getTransitionName(paramView))))
      return false;
    if ((this.mTargetIds.size() == 0) && (this.mTargets.size() == 0) && ((this.mTargetTypes == null) || (this.mTargetTypes.isEmpty())) && ((this.mTargetNames == null) || (this.mTargetNames.isEmpty())))
      return true;
    if ((this.mTargetIds.contains(Integer.valueOf(i))) || (this.mTargets.contains(paramView)))
      return true;
    if ((this.mTargetNames != null) && (this.mTargetNames.contains(ViewCompat.getTransitionName(paramView))))
      return true;
    if (this.mTargetTypes != null)
      for (int j = 0; j < this.mTargetTypes.size(); j++)
        if (((Class)this.mTargetTypes.get(j)).isInstance(paramView))
          return true;
    return false;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void pause(View paramView)
  {
    if (!this.mEnded)
    {
      ArrayMap localArrayMap = getRunningAnimators();
      int i = localArrayMap.size();
      WindowIdImpl localWindowIdImpl = ViewUtils.getWindowId(paramView);
      for (int j = i - 1; j >= 0; j--)
      {
        AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.valueAt(j);
        if ((localAnimationInfo.mView == null) || (!localWindowIdImpl.equals(localAnimationInfo.mWindowId)))
          continue;
        AnimatorUtils.pause((Animator)localArrayMap.keyAt(j));
      }
      if ((this.mListeners != null) && (this.mListeners.size() > 0))
      {
        ArrayList localArrayList = (ArrayList)this.mListeners.clone();
        int k = localArrayList.size();
        for (int m = 0; m < k; m++)
          ((TransitionListener)localArrayList.get(m)).onTransitionPause(this);
      }
      this.mPaused = true;
    }
  }

  void playTransition(ViewGroup paramViewGroup)
  {
    this.mStartValuesList = new ArrayList();
    this.mEndValuesList = new ArrayList();
    matchStartAndEnd(this.mStartValues, this.mEndValues);
    ArrayMap localArrayMap = getRunningAnimators();
    int i = localArrayMap.size();
    WindowIdImpl localWindowIdImpl = ViewUtils.getWindowId(paramViewGroup);
    int j = i - 1;
    if (j >= 0)
    {
      Animator localAnimator = (Animator)localArrayMap.keyAt(j);
      int k;
      if (localAnimator != null)
      {
        AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.get(localAnimator);
        if ((localAnimationInfo != null) && (localAnimationInfo.mView != null) && (localWindowIdImpl.equals(localAnimationInfo.mWindowId)))
        {
          TransitionValues localTransitionValues1 = localAnimationInfo.mValues;
          View localView = localAnimationInfo.mView;
          TransitionValues localTransitionValues2 = getTransitionValues(localView, true);
          TransitionValues localTransitionValues3 = getMatchedTransitionValues(localView, true);
          if (((localTransitionValues2 == null) && (localTransitionValues3 == null)) || (!localAnimationInfo.mTransition.isTransitionRequired(localTransitionValues1, localTransitionValues3)))
            break label204;
          k = 1;
          label172: if (k != 0)
          {
            if ((!localAnimator.isRunning()) && (!localAnimator.isStarted()))
              break label210;
            localAnimator.cancel();
          }
        }
      }
      while (true)
      {
        j--;
        break;
        label204: k = 0;
        break label172;
        label210: localArrayMap.remove(localAnimator);
      }
    }
    createAnimators(paramViewGroup, this.mStartValues, this.mEndValues, this.mStartValuesList, this.mEndValuesList);
    runAnimators();
  }

  @NonNull
  public Transition removeListener(@NonNull TransitionListener paramTransitionListener)
  {
    if (this.mListeners == null);
    do
    {
      return this;
      this.mListeners.remove(paramTransitionListener);
    }
    while (this.mListeners.size() != 0);
    this.mListeners = null;
    return this;
  }

  @NonNull
  public Transition removeTarget(@IdRes int paramInt)
  {
    if (paramInt > 0)
      this.mTargetIds.remove(Integer.valueOf(paramInt));
    return this;
  }

  @NonNull
  public Transition removeTarget(@NonNull View paramView)
  {
    this.mTargets.remove(paramView);
    return this;
  }

  @NonNull
  public Transition removeTarget(@NonNull Class paramClass)
  {
    if (this.mTargetTypes != null)
      this.mTargetTypes.remove(paramClass);
    return this;
  }

  @NonNull
  public Transition removeTarget(@NonNull String paramString)
  {
    if (this.mTargetNames != null)
      this.mTargetNames.remove(paramString);
    return this;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void resume(View paramView)
  {
    if (this.mPaused)
    {
      if (!this.mEnded)
      {
        ArrayMap localArrayMap = getRunningAnimators();
        int i = localArrayMap.size();
        WindowIdImpl localWindowIdImpl = ViewUtils.getWindowId(paramView);
        for (int j = i - 1; j >= 0; j--)
        {
          AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.valueAt(j);
          if ((localAnimationInfo.mView == null) || (!localWindowIdImpl.equals(localAnimationInfo.mWindowId)))
            continue;
          AnimatorUtils.resume((Animator)localArrayMap.keyAt(j));
        }
        if ((this.mListeners != null) && (this.mListeners.size() > 0))
        {
          ArrayList localArrayList = (ArrayList)this.mListeners.clone();
          int k = localArrayList.size();
          for (int m = 0; m < k; m++)
            ((TransitionListener)localArrayList.get(m)).onTransitionResume(this);
        }
      }
      this.mPaused = false;
    }
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void runAnimators()
  {
    start();
    ArrayMap localArrayMap = getRunningAnimators();
    Iterator localIterator = this.mAnimators.iterator();
    while (localIterator.hasNext())
    {
      Animator localAnimator = (Animator)localIterator.next();
      if (!localArrayMap.containsKey(localAnimator))
        continue;
      start();
      runAnimator(localAnimator, localArrayMap);
    }
    this.mAnimators.clear();
    end();
  }

  void setCanRemoveViews(boolean paramBoolean)
  {
    this.mCanRemoveViews = paramBoolean;
  }

  @NonNull
  public Transition setDuration(long paramLong)
  {
    this.mDuration = paramLong;
    return this;
  }

  public void setEpicenterCallback(@Nullable EpicenterCallback paramEpicenterCallback)
  {
    this.mEpicenterCallback = paramEpicenterCallback;
  }

  @NonNull
  public Transition setInterpolator(@Nullable TimeInterpolator paramTimeInterpolator)
  {
    this.mInterpolator = paramTimeInterpolator;
    return this;
  }

  public void setMatchOrder(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0))
    {
      this.mMatchOrder = DEFAULT_MATCH_ORDER;
      return;
    }
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      if (!isValidMatch(paramArrayOfInt[i]))
        throw new IllegalArgumentException("matches contains invalid value");
      if (!alreadyContains(paramArrayOfInt, i))
        continue;
      throw new IllegalArgumentException("matches contains a duplicate value");
    }
    this.mMatchOrder = ((int[])paramArrayOfInt.clone());
  }

  public void setPathMotion(@Nullable PathMotion paramPathMotion)
  {
    if (paramPathMotion == null)
    {
      this.mPathMotion = STRAIGHT_PATH_MOTION;
      return;
    }
    this.mPathMotion = paramPathMotion;
  }

  public void setPropagation(@Nullable TransitionPropagation paramTransitionPropagation)
  {
    this.mPropagation = paramTransitionPropagation;
  }

  Transition setSceneRoot(ViewGroup paramViewGroup)
  {
    this.mSceneRoot = paramViewGroup;
    return this;
  }

  @NonNull
  public Transition setStartDelay(long paramLong)
  {
    this.mStartDelay = paramLong;
    return this;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void start()
  {
    if (this.mNumInstances == 0)
    {
      if ((this.mListeners != null) && (this.mListeners.size() > 0))
      {
        ArrayList localArrayList = (ArrayList)this.mListeners.clone();
        int i = localArrayList.size();
        for (int j = 0; j < i; j++)
          ((TransitionListener)localArrayList.get(j)).onTransitionStart(this);
      }
      this.mEnded = false;
    }
    this.mNumInstances = (1 + this.mNumInstances);
  }

  public String toString()
  {
    return toString("");
  }

  String toString(String paramString)
  {
    String str1 = paramString + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": ";
    if (this.mDuration != -1L)
      str1 = str1 + "dur(" + this.mDuration + ") ";
    if (this.mStartDelay != -1L)
      str1 = str1 + "dly(" + this.mStartDelay + ") ";
    if (this.mInterpolator != null)
      str1 = str1 + "interp(" + this.mInterpolator + ") ";
    if ((this.mTargetIds.size() > 0) || (this.mTargets.size() > 0))
    {
      String str2 = str1 + "tgts(";
      if (this.mTargetIds.size() > 0)
        for (int j = 0; j < this.mTargetIds.size(); j++)
        {
          if (j > 0)
            str2 = str2 + ", ";
          str2 = str2 + this.mTargetIds.get(j);
        }
      if (this.mTargets.size() > 0)
        for (int i = 0; i < this.mTargets.size(); i++)
        {
          if (i > 0)
            str2 = str2 + ", ";
          str2 = str2 + this.mTargets.get(i);
        }
      str1 = str2 + ")";
    }
    return str1;
  }

  private static class AnimationInfo
  {
    String mName;
    Transition mTransition;
    TransitionValues mValues;
    View mView;
    WindowIdImpl mWindowId;

    AnimationInfo(View paramView, String paramString, Transition paramTransition, WindowIdImpl paramWindowIdImpl, TransitionValues paramTransitionValues)
    {
      this.mView = paramView;
      this.mName = paramString;
      this.mValues = paramTransitionValues;
      this.mWindowId = paramWindowIdImpl;
      this.mTransition = paramTransition;
    }
  }

  private static class ArrayListManager
  {
    static <T> ArrayList<T> add(ArrayList<T> paramArrayList, T paramT)
    {
      if (paramArrayList == null)
        paramArrayList = new ArrayList();
      if (!paramArrayList.contains(paramT))
        paramArrayList.add(paramT);
      return paramArrayList;
    }

    static <T> ArrayList<T> remove(ArrayList<T> paramArrayList, T paramT)
    {
      if (paramArrayList != null)
      {
        paramArrayList.remove(paramT);
        if (paramArrayList.isEmpty())
          paramArrayList = null;
      }
      return paramArrayList;
    }
  }

  public static abstract class EpicenterCallback
  {
    public abstract Rect onGetEpicenter(@NonNull Transition paramTransition);
  }

  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface MatchOrder
  {
  }

  public static abstract interface TransitionListener
  {
    public abstract void onTransitionCancel(@NonNull Transition paramTransition);

    public abstract void onTransitionEnd(@NonNull Transition paramTransition);

    public abstract void onTransitionPause(@NonNull Transition paramTransition);

    public abstract void onTransitionResume(@NonNull Transition paramTransition);

    public abstract void onTransitionStart(@NonNull Transition paramTransition);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.Transition
 * JD-Core Version:    0.6.0
 */