package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.RestrictTo;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.OverScroller;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.List;

public class NestedScrollView extends FrameLayout
  implements NestedScrollingParent, NestedScrollingChild2, ScrollingView
{
  private static final AccessibilityDelegate ACCESSIBILITY_DELEGATE = new AccessibilityDelegate();
  static final int ANIMATED_SCROLL_GAP = 250;
  private static final int INVALID_POINTER = -1;
  static final float MAX_SCROLL_FACTOR = 0.5F;
  private static final int[] SCROLLVIEW_STYLEABLE = { 16843130 };
  private static final String TAG = "NestedScrollView";
  private int mActivePointerId = -1;
  private final NestedScrollingChildHelper mChildHelper;
  private View mChildToScrollTo = null;
  private EdgeEffect mEdgeGlowBottom;
  private EdgeEffect mEdgeGlowTop;
  private boolean mFillViewport;
  private boolean mIsBeingDragged = false;
  private boolean mIsLaidOut = false;
  private boolean mIsLayoutDirty = true;
  private int mLastMotionY;
  private long mLastScroll;
  private int mLastScrollerY;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  private int mNestedYOffset;
  private OnScrollChangeListener mOnScrollChangeListener;
  private final NestedScrollingParentHelper mParentHelper;
  private SavedState mSavedState;
  private final int[] mScrollConsumed = new int[2];
  private final int[] mScrollOffset = new int[2];
  private OverScroller mScroller;
  private boolean mSmoothScrollingEnabled = true;
  private final Rect mTempRect = new Rect();
  private int mTouchSlop;
  private VelocityTracker mVelocityTracker;
  private float mVerticalScrollFactor;

  public NestedScrollView(Context paramContext)
  {
    this(paramContext, null);
  }

  public NestedScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public NestedScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initScrollView();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, SCROLLVIEW_STYLEABLE, paramInt, 0);
    setFillViewport(localTypedArray.getBoolean(0, false));
    localTypedArray.recycle();
    this.mParentHelper = new NestedScrollingParentHelper(this);
    this.mChildHelper = new NestedScrollingChildHelper(this);
    setNestedScrollingEnabled(true);
    ViewCompat.setAccessibilityDelegate(this, ACCESSIBILITY_DELEGATE);
  }

  private boolean canScroll()
  {
    View localView = getChildAt(0);
    int i = 0;
    if (localView != null)
    {
      int j = localView.getHeight();
      int k = getHeight();
      int m = j + getPaddingTop() + getPaddingBottom();
      i = 0;
      if (k < m)
        i = 1;
    }
    return i;
  }

  private static int clamp(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt2 >= paramInt3) || (paramInt1 < 0))
      paramInt1 = 0;
    do
      return paramInt1;
    while (paramInt2 + paramInt1 <= paramInt3);
    return paramInt3 - paramInt2;
  }

  private void doScrollY(int paramInt)
  {
    if (paramInt != 0)
    {
      if (this.mSmoothScrollingEnabled)
        smoothScrollBy(0, paramInt);
    }
    else
      return;
    scrollBy(0, paramInt);
  }

  private void endDrag()
  {
    this.mIsBeingDragged = false;
    recycleVelocityTracker();
    stopNestedScroll(0);
    if (this.mEdgeGlowTop != null)
    {
      this.mEdgeGlowTop.onRelease();
      this.mEdgeGlowBottom.onRelease();
    }
  }

  private void ensureGlows()
  {
    if (getOverScrollMode() != 2)
    {
      if (this.mEdgeGlowTop == null)
      {
        Context localContext = getContext();
        this.mEdgeGlowTop = new EdgeEffect(localContext);
        this.mEdgeGlowBottom = new EdgeEffect(localContext);
      }
      return;
    }
    this.mEdgeGlowTop = null;
    this.mEdgeGlowBottom = null;
  }

  private View findFocusableViewInBounds(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = getFocusables(2);
    Object localObject = null;
    int i = 0;
    int j = localArrayList.size();
    int k = 0;
    if (k < j)
    {
      View localView = (View)localArrayList.get(k);
      int m = localView.getTop();
      int n = localView.getBottom();
      int i1;
      if ((paramInt1 < n) && (m < paramInt2))
      {
        if ((paramInt1 >= m) || (n >= paramInt2))
          break label106;
        i1 = 1;
        label87: if (localObject != null)
          break label112;
        localObject = localView;
        i = i1;
      }
      while (true)
      {
        k++;
        break;
        label106: i1 = 0;
        break label87;
        label112: if (((paramBoolean) && (m < localObject.getTop())) || ((!paramBoolean) && (n > localObject.getBottom())));
        for (int i2 = 1; ; i2 = 0)
        {
          if (i == 0)
            break label171;
          if ((i1 == 0) || (i2 == 0))
            break;
          localObject = localView;
          break;
        }
        label171: if (i1 != 0)
        {
          localObject = localView;
          i = 1;
          continue;
        }
        if (i2 == 0)
          continue;
        localObject = localView;
      }
    }
    return localObject;
  }

  private void flingWithNestedDispatch(int paramInt)
  {
    int i = getScrollY();
    if (((i > 0) || (paramInt > 0)) && ((i < getScrollRange()) || (paramInt < 0)));
    for (boolean bool = true; ; bool = false)
    {
      if (!dispatchNestedPreFling(0.0F, paramInt))
      {
        dispatchNestedFling(0.0F, paramInt, bool);
        fling(paramInt);
      }
      return;
    }
  }

  private float getVerticalScrollFactorCompat()
  {
    if (this.mVerticalScrollFactor == 0.0F)
    {
      TypedValue localTypedValue = new TypedValue();
      Context localContext = getContext();
      if (!localContext.getTheme().resolveAttribute(16842829, localTypedValue, true))
        throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
      this.mVerticalScrollFactor = localTypedValue.getDimension(localContext.getResources().getDisplayMetrics());
    }
    return this.mVerticalScrollFactor;
  }

  private boolean inChild(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    int j = 0;
    if (i > 0)
    {
      int k = getScrollY();
      View localView = getChildAt(0);
      int m = localView.getTop() - k;
      j = 0;
      if (paramInt2 >= m)
      {
        int n = localView.getBottom() - k;
        j = 0;
        if (paramInt2 < n)
        {
          int i1 = localView.getLeft();
          j = 0;
          if (paramInt1 >= i1)
          {
            int i2 = localView.getRight();
            j = 0;
            if (paramInt1 < i2)
              j = 1;
          }
        }
      }
    }
    return j;
  }

  private void initOrResetVelocityTracker()
  {
    if (this.mVelocityTracker == null)
    {
      this.mVelocityTracker = VelocityTracker.obtain();
      return;
    }
    this.mVelocityTracker.clear();
  }

  private void initScrollView()
  {
    this.mScroller = new OverScroller(getContext());
    setFocusable(true);
    setDescendantFocusability(262144);
    setWillNotDraw(false);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(getContext());
    this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    this.mMinimumVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
  }

  private void initVelocityTrackerIfNotExists()
  {
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain();
  }

  private boolean isOffScreen(View paramView)
  {
    boolean bool = isWithinDeltaOfScreen(paramView, 0, getHeight());
    int i = 0;
    if (!bool)
      i = 1;
    return i;
  }

  private static boolean isViewDescendantOf(View paramView1, View paramView2)
  {
    if (paramView1 == paramView2);
    ViewParent localViewParent;
    do
    {
      return true;
      localViewParent = paramView1.getParent();
    }
    while (((localViewParent instanceof ViewGroup)) && (isViewDescendantOf((View)localViewParent, paramView2)));
    return false;
  }

  private boolean isWithinDeltaOfScreen(View paramView, int paramInt1, int paramInt2)
  {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    return (paramInt1 + this.mTempRect.bottom >= getScrollY()) && (this.mTempRect.top - paramInt1 <= paramInt2 + getScrollY());
  }

  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionIndex();
    if (paramMotionEvent.getPointerId(i) == this.mActivePointerId)
      if (i != 0)
        break label57;
    label57: for (int j = 1; ; j = 0)
    {
      this.mLastMotionY = (int)paramMotionEvent.getY(j);
      this.mActivePointerId = paramMotionEvent.getPointerId(j);
      if (this.mVelocityTracker != null)
        this.mVelocityTracker.clear();
      return;
    }
  }

  private void recycleVelocityTracker()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }

  private boolean scrollAndFocus(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 1;
    int j = getHeight();
    int k = getScrollY();
    int m = k + j;
    if (paramInt1 == 33);
    for (boolean bool = true; ; bool = false)
    {
      Object localObject = findFocusableViewInBounds(bool, paramInt2, paramInt3);
      if (localObject == null)
        localObject = this;
      if ((paramInt2 < k) || (paramInt3 > m))
        break;
      i = 0;
      if (localObject != findFocus())
        ((View)localObject).requestFocus(paramInt1);
      return i;
    }
    if (bool);
    for (int n = paramInt2 - k; ; n = paramInt3 - m)
    {
      doScrollY(n);
      break;
    }
  }

  private void scrollToChild(View paramView)
  {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    int i = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
    if (i != 0)
      scrollBy(0, i);
  }

  private boolean scrollToChildRect(Rect paramRect, boolean paramBoolean)
  {
    int i = computeScrollDeltaToGetChildRectOnScreen(paramRect);
    if (i != 0);
    for (int j = 1; ; j = 0)
    {
      if (j != 0)
      {
        if (!paramBoolean)
          break;
        scrollBy(0, i);
      }
      return j;
    }
    smoothScrollBy(0, i);
    return j;
  }

  public void addView(View paramView)
  {
    if (getChildCount() > 0)
      throw new IllegalStateException("ScrollView can host only one direct child");
    super.addView(paramView);
  }

  public void addView(View paramView, int paramInt)
  {
    if (getChildCount() > 0)
      throw new IllegalStateException("ScrollView can host only one direct child");
    super.addView(paramView, paramInt);
  }

  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (getChildCount() > 0)
      throw new IllegalStateException("ScrollView can host only one direct child");
    super.addView(paramView, paramInt, paramLayoutParams);
  }

  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (getChildCount() > 0)
      throw new IllegalStateException("ScrollView can host only one direct child");
    super.addView(paramView, paramLayoutParams);
  }

  public boolean arrowScroll(int paramInt)
  {
    View localView1 = findFocus();
    if (localView1 == this)
      localView1 = null;
    View localView2 = FocusFinder.getInstance().findNextFocus(this, localView1, paramInt);
    int i = getMaxScrollAmount();
    int n;
    if ((localView2 != null) && (isWithinDeltaOfScreen(localView2, i, getHeight())))
    {
      localView2.getDrawingRect(this.mTempRect);
      offsetDescendantRectToMyCoords(localView2, this.mTempRect);
      doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      localView2.requestFocus(paramInt);
      if ((localView1 != null) && (localView1.isFocused()) && (isOffScreen(localView1)))
      {
        int i2 = getDescendantFocusability();
        setDescendantFocusability(131072);
        requestFocus();
        setDescendantFocusability(i2);
      }
      n = 1;
    }
    int j;
    while (true)
    {
      return n;
      j = i;
      if ((paramInt != 33) || (getScrollY() >= j))
        break;
      j = getScrollY();
      label155: n = 0;
      if (j == 0)
        continue;
      if (paramInt != 130)
        break label243;
    }
    label243: for (int i1 = j; ; i1 = -j)
    {
      doScrollY(i1);
      break;
      if ((paramInt != 130) || (getChildCount() <= 0))
        break label155;
      int k = getChildAt(0).getBottom();
      int m = getScrollY() + getHeight() - getPaddingBottom();
      if (k - m >= i)
        break label155;
      j = k - m;
      break label155;
    }
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int computeHorizontalScrollExtent()
  {
    return super.computeHorizontalScrollExtent();
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int computeHorizontalScrollOffset()
  {
    return super.computeHorizontalScrollOffset();
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int computeHorizontalScrollRange()
  {
    return super.computeHorizontalScrollRange();
  }

  public void computeScroll()
  {
    if (this.mScroller.computeScrollOffset())
    {
      this.mScroller.getCurrX();
      int i = this.mScroller.getCurrY();
      int j = i - this.mLastScrollerY;
      if (dispatchNestedPreScroll(0, j, this.mScrollConsumed, null, 1))
        j -= this.mScrollConsumed[1];
      int k;
      int m;
      int i2;
      if (j != 0)
      {
        k = getScrollRange();
        m = getScrollY();
        overScrollByCompat(0, j, getScrollX(), m, 0, k, 0, 0, false);
        int n = getScrollY() - m;
        if (!dispatchNestedScroll(0, n, 0, j - n, null, 1))
        {
          int i1 = getOverScrollMode();
          if ((i1 != 0) && ((i1 != 1) || (k <= 0)))
            break label186;
          i2 = 1;
          if (i2 != 0)
          {
            ensureGlows();
            if ((i > 0) || (m <= 0))
              break label192;
            this.mEdgeGlowTop.onAbsorb((int)this.mScroller.getCurrVelocity());
          }
        }
      }
      while (true)
      {
        this.mLastScrollerY = i;
        ViewCompat.postInvalidateOnAnimation(this);
        return;
        label186: i2 = 0;
        break;
        label192: if ((i < k) || (m >= k))
          continue;
        this.mEdgeGlowBottom.onAbsorb((int)this.mScroller.getCurrVelocity());
      }
    }
    if (hasNestedScrollingParent(1))
      stopNestedScroll(1);
    this.mLastScrollerY = 0;
  }

  protected int computeScrollDeltaToGetChildRectOnScreen(Rect paramRect)
  {
    if (getChildCount() == 0);
    int i;
    int j;
    int k;
    do
    {
      return 0;
      i = getHeight();
      j = getScrollY();
      k = j + i;
      int m = getVerticalFadingEdgeLength();
      if (paramRect.top > 0)
        j += m;
      if (paramRect.bottom < getChildAt(0).getHeight())
        k -= m;
      if ((paramRect.bottom <= k) || (paramRect.top <= j))
        continue;
      if (paramRect.height() > i);
      for (int i1 = 0 + (paramRect.top - j); ; i1 = 0 + (paramRect.bottom - k))
        return Math.min(i1, getChildAt(0).getBottom() - k);
    }
    while ((paramRect.top >= j) || (paramRect.bottom >= k));
    if (paramRect.height() > i);
    for (int n = 0 - (k - paramRect.bottom); ; n = 0 - (j - paramRect.top))
      return Math.max(n, -getScrollY());
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int computeVerticalScrollExtent()
  {
    return super.computeVerticalScrollExtent();
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int computeVerticalScrollOffset()
  {
    return Math.max(0, super.computeVerticalScrollOffset());
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int computeVerticalScrollRange()
  {
    int i = getChildCount();
    int j = getHeight() - getPaddingBottom() - getPaddingTop();
    if (i == 0)
      return j;
    int k = getChildAt(0).getBottom();
    int m = getScrollY();
    int n = Math.max(0, k - j);
    if (m < 0)
      k -= m;
    while (true)
    {
      return k;
      if (m <= n)
        continue;
      k += m - n;
    }
  }

  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    return (super.dispatchKeyEvent(paramKeyEvent)) || (executeKeyEvent(paramKeyEvent));
  }

  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return this.mChildHelper.dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }

  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    return this.mChildHelper.dispatchNestedPreFling(paramFloat1, paramFloat2);
  }

  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return this.mChildHelper.dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }

  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3)
  {
    return this.mChildHelper.dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, paramInt3);
  }

  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    return this.mChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
  }

  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5)
  {
    return this.mChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5);
  }

  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (this.mEdgeGlowTop != null)
    {
      int i = getScrollY();
      if (!this.mEdgeGlowTop.isFinished())
      {
        int n = paramCanvas.save();
        int i1 = getWidth() - getPaddingLeft() - getPaddingRight();
        paramCanvas.translate(getPaddingLeft(), Math.min(0, i));
        this.mEdgeGlowTop.setSize(i1, getHeight());
        if (this.mEdgeGlowTop.draw(paramCanvas))
          ViewCompat.postInvalidateOnAnimation(this);
        paramCanvas.restoreToCount(n);
      }
      if (!this.mEdgeGlowBottom.isFinished())
      {
        int j = paramCanvas.save();
        int k = getWidth() - getPaddingLeft() - getPaddingRight();
        int m = getHeight();
        paramCanvas.translate(-k + getPaddingLeft(), m + Math.max(getScrollRange(), i));
        paramCanvas.rotate(180.0F, k, 0.0F);
        this.mEdgeGlowBottom.setSize(k, m);
        if (this.mEdgeGlowBottom.draw(paramCanvas))
          ViewCompat.postInvalidateOnAnimation(this);
        paramCanvas.restoreToCount(j);
      }
    }
  }

  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    this.mTempRect.setEmpty();
    if (!canScroll())
    {
      boolean bool2 = isFocused();
      int m = 0;
      if (bool2)
      {
        int n = paramKeyEvent.getKeyCode();
        m = 0;
        if (n != 4)
        {
          View localView1 = findFocus();
          if (localView1 == this)
            localView1 = null;
          View localView2 = FocusFinder.getInstance().findNextFocus(this, localView1, 130);
          m = 0;
          if (localView2 != null)
          {
            m = 0;
            if (localView2 != this)
            {
              boolean bool3 = localView2.requestFocus(130);
              m = 0;
              if (bool3)
                m = 1;
            }
          }
        }
      }
      return m;
    }
    int i = paramKeyEvent.getAction();
    boolean bool1 = false;
    int j;
    if (i == 0)
    {
      j = paramKeyEvent.getKeyCode();
      bool1 = false;
    }
    switch (j)
    {
    default:
    case 19:
    case 20:
      while (true)
      {
        return bool1;
        if (!paramKeyEvent.isAltPressed())
        {
          bool1 = arrowScroll(33);
          continue;
        }
        bool1 = fullScroll(33);
        continue;
        if (!paramKeyEvent.isAltPressed())
        {
          bool1 = arrowScroll(130);
          continue;
        }
        bool1 = fullScroll(130);
      }
    case 62:
    }
    if (paramKeyEvent.isShiftPressed());
    for (int k = 33; ; k = 130)
    {
      pageScroll(k);
      bool1 = false;
      break;
    }
  }

  public void fling(int paramInt)
  {
    if (getChildCount() > 0)
    {
      startNestedScroll(2, 1);
      this.mScroller.fling(getScrollX(), getScrollY(), 0, paramInt, 0, 0, -2147483648, 2147483647, 0, 0);
      this.mLastScrollerY = getScrollY();
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }

  public boolean fullScroll(int paramInt)
  {
    if (paramInt == 130);
    for (int i = 1; ; i = 0)
    {
      int j = getHeight();
      this.mTempRect.top = 0;
      this.mTempRect.bottom = j;
      if (i != 0)
      {
        int k = getChildCount();
        if (k > 0)
        {
          View localView = getChildAt(k - 1);
          this.mTempRect.bottom = (localView.getBottom() + getPaddingBottom());
          this.mTempRect.top = (this.mTempRect.bottom - j);
        }
      }
      return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
    }
  }

  protected float getBottomFadingEdgeStrength()
  {
    if (getChildCount() == 0)
      return 0.0F;
    int i = getVerticalFadingEdgeLength();
    int j = getHeight() - getPaddingBottom();
    int k = getChildAt(0).getBottom() - getScrollY() - j;
    if (k < i)
      return k / i;
    return 1.0F;
  }

  public int getMaxScrollAmount()
  {
    return (int)(0.5F * getHeight());
  }

  public int getNestedScrollAxes()
  {
    return this.mParentHelper.getNestedScrollAxes();
  }

  int getScrollRange()
  {
    int i = getChildCount();
    int j = 0;
    if (i > 0)
      j = Math.max(0, getChildAt(0).getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
    return j;
  }

  protected float getTopFadingEdgeStrength()
  {
    if (getChildCount() == 0)
      return 0.0F;
    int i = getVerticalFadingEdgeLength();
    int j = getScrollY();
    if (j < i)
      return j / i;
    return 1.0F;
  }

  public boolean hasNestedScrollingParent()
  {
    return this.mChildHelper.hasNestedScrollingParent();
  }

  public boolean hasNestedScrollingParent(int paramInt)
  {
    return this.mChildHelper.hasNestedScrollingParent(paramInt);
  }

  public boolean isFillViewport()
  {
    return this.mFillViewport;
  }

  public boolean isNestedScrollingEnabled()
  {
    return this.mChildHelper.isNestedScrollingEnabled();
  }

  public boolean isSmoothScrollingEnabled()
  {
    return this.mSmoothScrollingEnabled;
  }

  protected void measureChild(View paramView, int paramInt1, int paramInt2)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight(), localLayoutParams.width), View.MeasureSpec.makeMeasureSpec(0, 0));
  }

  protected void measureChildWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, paramInt2 + (getPaddingLeft() + getPaddingRight() + localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin), localMarginLayoutParams.width), View.MeasureSpec.makeMeasureSpec(localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin, 0));
  }

  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mIsLaidOut = false;
  }

  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((0x2 & paramMotionEvent.getSource()) != 0)
      switch (paramMotionEvent.getAction())
      {
      default:
      case 8:
      }
    while (true)
    {
      return false;
      if (this.mIsBeingDragged)
        continue;
      float f = paramMotionEvent.getAxisValue(9);
      if (f == 0.0F)
        continue;
      int i = (int)(f * getVerticalScrollFactorCompat());
      int j = getScrollRange();
      int k = getScrollY();
      int m = k - i;
      if (m < 0)
        m = 0;
      while (m != k)
      {
        super.scrollTo(getScrollX(), m);
        return true;
        if (m <= j)
          continue;
        m = j;
      }
    }
  }

  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    int i = paramMotionEvent.getAction();
    if ((i == 2) && (this.mIsBeingDragged))
      return bool;
    switch (i & 0xFF)
    {
    case 4:
    case 5:
    default:
    case 2:
    case 0:
    case 1:
    case 3:
    case 6:
    }
    while (true)
    {
      return this.mIsBeingDragged;
      int k = this.mActivePointerId;
      if (k == -1)
        continue;
      int m = paramMotionEvent.findPointerIndex(k);
      if (m == -1)
      {
        Log.e("NestedScrollView", "Invalid pointerId=" + k + " in onInterceptTouchEvent");
        continue;
      }
      int n = (int)paramMotionEvent.getY(m);
      if ((Math.abs(n - this.mLastMotionY) <= this.mTouchSlop) || ((0x2 & getNestedScrollAxes()) != 0))
        continue;
      this.mIsBeingDragged = bool;
      this.mLastMotionY = n;
      initVelocityTrackerIfNotExists();
      this.mVelocityTracker.addMovement(paramMotionEvent);
      this.mNestedYOffset = 0;
      ViewParent localViewParent = getParent();
      if (localViewParent == null)
        continue;
      localViewParent.requestDisallowInterceptTouchEvent(bool);
      continue;
      int j = (int)paramMotionEvent.getY();
      if (!inChild((int)paramMotionEvent.getX(), j))
      {
        this.mIsBeingDragged = false;
        recycleVelocityTracker();
        continue;
      }
      this.mLastMotionY = j;
      this.mActivePointerId = paramMotionEvent.getPointerId(0);
      initOrResetVelocityTracker();
      this.mVelocityTracker.addMovement(paramMotionEvent);
      this.mScroller.computeScrollOffset();
      if (!this.mScroller.isFinished());
      while (true)
      {
        this.mIsBeingDragged = bool;
        startNestedScroll(2, 0);
        break;
        bool = false;
      }
      this.mIsBeingDragged = false;
      this.mActivePointerId = -1;
      recycleVelocityTracker();
      if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange()))
        ViewCompat.postInvalidateOnAnimation(this);
      stopNestedScroll(0);
      continue;
      onSecondaryPointerUp(paramMotionEvent);
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mIsLayoutDirty = false;
    if ((this.mChildToScrollTo != null) && (isViewDescendantOf(this.mChildToScrollTo, this)))
      scrollToChild(this.mChildToScrollTo);
    this.mChildToScrollTo = null;
    int i;
    if (!this.mIsLaidOut)
    {
      if (this.mSavedState != null)
      {
        scrollTo(getScrollX(), this.mSavedState.scrollPosition);
        this.mSavedState = null;
      }
      if (getChildCount() <= 0)
        break label158;
      i = getChildAt(0).getMeasuredHeight();
      int j = Math.max(0, i - (paramInt4 - paramInt2 - getPaddingBottom() - getPaddingTop()));
      if (getScrollY() <= j)
        break label164;
      scrollTo(getScrollX(), j);
    }
    while (true)
    {
      scrollTo(getScrollX(), getScrollY());
      this.mIsLaidOut = true;
      return;
      label158: i = 0;
      break;
      label164: if (getScrollY() >= 0)
        continue;
      scrollTo(getScrollX(), 0);
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (!this.mFillViewport);
    View localView;
    int i;
    do
    {
      do
        return;
      while ((View.MeasureSpec.getMode(paramInt2) == 0) || (getChildCount() <= 0));
      localView = getChildAt(0);
      i = getMeasuredHeight();
    }
    while (localView.getMeasuredHeight() >= i);
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
    localView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight(), localLayoutParams.width), View.MeasureSpec.makeMeasureSpec(i - getPaddingTop() - getPaddingBottom(), 1073741824));
  }

  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      flingWithNestedDispatch((int)paramFloat2);
      return true;
    }
    return false;
  }

  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
  {
    return dispatchNestedPreFling(paramFloat1, paramFloat2);
  }

  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt, null);
  }

  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getScrollY();
    scrollBy(0, paramInt4);
    int j = getScrollY() - i;
    dispatchNestedScroll(0, j, 0, paramInt4 - j, null);
  }

  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    this.mParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    startNestedScroll(2);
  }

  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    super.scrollTo(paramInt1, paramInt2);
  }

  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    View localView;
    if (paramInt == 2)
    {
      paramInt = 130;
      if (paramRect != null)
        break label40;
      localView = FocusFinder.getInstance().findNextFocus(this, null, paramInt);
      label23: if (localView != null)
        break label53;
    }
    label40: label53: 
    do
    {
      return false;
      if (paramInt != 1)
        break;
      paramInt = 33;
      break;
      localView = FocusFinder.getInstance().findNextFocusFromRect(this, paramRect, paramInt);
      break label23;
    }
    while (isOffScreen(localView));
    return localView.requestFocus(paramInt, paramRect);
  }

  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    this.mSavedState = localSavedState;
    requestLayout();
  }

  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.scrollPosition = getScrollY();
    return localSavedState;
  }

  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mOnScrollChangeListener != null)
      this.mOnScrollChangeListener.onScrollChange(this, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    View localView = findFocus();
    if ((localView == null) || (this == localView));
    do
      return;
    while (!isWithinDeltaOfScreen(localView, 0, paramInt4));
    localView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(localView, this.mTempRect);
    doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
  }

  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    return (paramInt & 0x2) != 0;
  }

  public void onStopNestedScroll(View paramView)
  {
    this.mParentHelper.onStopNestedScroll(paramView);
    stopNestedScroll();
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    initVelocityTrackerIfNotExists();
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    int i = paramMotionEvent.getActionMasked();
    if (i == 0)
      this.mNestedYOffset = 0;
    localMotionEvent.offsetLocation(0.0F, this.mNestedYOffset);
    switch (i)
    {
    case 4:
    default:
    case 0:
    case 2:
    case 1:
    case 3:
    case 5:
    case 6:
    }
    while (true)
    {
      if (this.mVelocityTracker != null)
        this.mVelocityTracker.addMovement(localMotionEvent);
      localMotionEvent.recycle();
      return true;
      if (getChildCount() == 0)
        return false;
      if (!this.mScroller.isFinished());
      for (boolean bool = true; ; bool = false)
      {
        this.mIsBeingDragged = bool;
        if (bool)
        {
          ViewParent localViewParent2 = getParent();
          if (localViewParent2 != null)
            localViewParent2.requestDisallowInterceptTouchEvent(true);
        }
        if (!this.mScroller.isFinished())
          this.mScroller.abortAnimation();
        this.mLastMotionY = (int)paramMotionEvent.getY();
        this.mActivePointerId = paramMotionEvent.getPointerId(0);
        startNestedScroll(2, 0);
        break;
      }
      int m = paramMotionEvent.findPointerIndex(this.mActivePointerId);
      if (m == -1)
      {
        Log.e("NestedScrollView", "Invalid pointerId=" + this.mActivePointerId + " in onTouchEvent");
        continue;
      }
      int n = (int)paramMotionEvent.getY(m);
      int i1 = this.mLastMotionY - n;
      if (dispatchNestedPreScroll(0, i1, this.mScrollConsumed, this.mScrollOffset, 0))
      {
        i1 -= this.mScrollConsumed[1];
        localMotionEvent.offsetLocation(0.0F, this.mScrollOffset[1]);
        this.mNestedYOffset += this.mScrollOffset[1];
      }
      label386: int i2;
      int i3;
      if ((!this.mIsBeingDragged) && (Math.abs(i1) > this.mTouchSlop))
      {
        ViewParent localViewParent1 = getParent();
        if (localViewParent1 != null)
          localViewParent1.requestDisallowInterceptTouchEvent(true);
        this.mIsBeingDragged = true;
        if (i1 > 0)
          i1 -= this.mTouchSlop;
      }
      else
      {
        if (!this.mIsBeingDragged)
          continue;
        this.mLastMotionY = (n - this.mScrollOffset[1]);
        i2 = getScrollY();
        i3 = getScrollRange();
        int i4 = getOverScrollMode();
        if ((i4 != 0) && ((i4 != 1) || (i3 <= 0)))
          break label566;
      }
      label566: for (int i5 = 1; ; i5 = 0)
      {
        if ((overScrollByCompat(0, i1, 0, getScrollY(), 0, i3, 0, 0, true)) && (!hasNestedScrollingParent(0)))
          this.mVelocityTracker.clear();
        int i6 = getScrollY() - i2;
        if (!dispatchNestedScroll(0, i6, 0, i1 - i6, this.mScrollOffset, 0))
          break label572;
        this.mLastMotionY -= this.mScrollOffset[1];
        localMotionEvent.offsetLocation(0.0F, this.mScrollOffset[1]);
        this.mNestedYOffset += this.mScrollOffset[1];
        break;
        i1 += this.mTouchSlop;
        break label386;
      }
      label572: if (i5 == 0)
        continue;
      ensureGlows();
      int i7 = i2 + i1;
      if (i7 < 0)
      {
        EdgeEffectCompat.onPull(this.mEdgeGlowTop, i1 / getHeight(), paramMotionEvent.getX(m) / getWidth());
        if (!this.mEdgeGlowBottom.isFinished())
          this.mEdgeGlowBottom.onRelease();
      }
      while ((this.mEdgeGlowTop != null) && ((!this.mEdgeGlowTop.isFinished()) || (!this.mEdgeGlowBottom.isFinished())))
      {
        ViewCompat.postInvalidateOnAnimation(this);
        break;
        if (i7 <= i3)
          continue;
        EdgeEffectCompat.onPull(this.mEdgeGlowBottom, i1 / getHeight(), 1.0F - paramMotionEvent.getX(m) / getWidth());
        if (this.mEdgeGlowTop.isFinished())
          continue;
        this.mEdgeGlowTop.onRelease();
      }
      VelocityTracker localVelocityTracker = this.mVelocityTracker;
      localVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
      int k = (int)localVelocityTracker.getYVelocity(this.mActivePointerId);
      if (Math.abs(k) > this.mMinimumVelocity)
        flingWithNestedDispatch(-k);
      while (true)
      {
        this.mActivePointerId = -1;
        endDrag();
        break;
        if (!this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange()))
          continue;
        ViewCompat.postInvalidateOnAnimation(this);
      }
      if ((this.mIsBeingDragged) && (getChildCount() > 0) && (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())))
        ViewCompat.postInvalidateOnAnimation(this);
      this.mActivePointerId = -1;
      endDrag();
      continue;
      int j = paramMotionEvent.getActionIndex();
      this.mLastMotionY = (int)paramMotionEvent.getY(j);
      this.mActivePointerId = paramMotionEvent.getPointerId(j);
      continue;
      onSecondaryPointerUp(paramMotionEvent);
      this.mLastMotionY = (int)paramMotionEvent.getY(paramMotionEvent.findPointerIndex(this.mActivePointerId));
    }
  }

  boolean overScrollByCompat(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    int i = getOverScrollMode();
    int j;
    int k;
    label34: int m;
    label53: int n;
    label72: int i1;
    int i2;
    int i3;
    int i5;
    boolean bool1;
    label137: boolean bool2;
    if (computeHorizontalScrollRange() > computeHorizontalScrollExtent())
    {
      j = 1;
      if (computeVerticalScrollRange() <= computeVerticalScrollExtent())
        break label213;
      k = 1;
      if ((i != 0) && ((i != 1) || (j == 0)))
        break label219;
      m = 1;
      if ((i != 0) && ((i != 1) || (k == 0)))
        break label225;
      n = 1;
      i1 = paramInt3 + paramInt1;
      if (m == 0)
        paramInt7 = 0;
      i2 = paramInt4 + paramInt2;
      if (n == 0)
        paramInt8 = 0;
      i3 = -paramInt7;
      int i4 = paramInt7 + paramInt5;
      i5 = -paramInt8;
      int i6 = paramInt8 + paramInt6;
      if (i1 <= i4)
        break label231;
      i1 = i4;
      bool1 = true;
      if (i2 <= i6)
        break label251;
      i2 = i6;
      bool2 = true;
    }
    while (true)
    {
      if ((bool2) && (!hasNestedScrollingParent(1)))
        this.mScroller.springBack(i1, i2, 0, 0, 0, getScrollRange());
      onOverScrolled(i1, i2, bool1, bool2);
      if ((!bool1) && (!bool2))
        break label271;
      return true;
      j = 0;
      break;
      label213: k = 0;
      break label34;
      label219: m = 0;
      break label53;
      label225: n = 0;
      break label72;
      label231: bool1 = false;
      if (i1 >= i3)
        break label137;
      i1 = i3;
      bool1 = true;
      break label137;
      label251: bool2 = false;
      if (i2 >= i5)
        continue;
      i2 = i5;
      bool2 = true;
    }
    label271: return false;
  }

  public boolean pageScroll(int paramInt)
  {
    int i;
    int j;
    if (paramInt == 130)
    {
      i = 1;
      j = getHeight();
      if (i == 0)
        break label124;
      this.mTempRect.top = (j + getScrollY());
      int k = getChildCount();
      if (k > 0)
      {
        View localView = getChildAt(k - 1);
        if (j + this.mTempRect.top > localView.getBottom())
          this.mTempRect.top = (localView.getBottom() - j);
      }
    }
    while (true)
    {
      this.mTempRect.bottom = (j + this.mTempRect.top);
      return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
      i = 0;
      break;
      label124: this.mTempRect.top = (getScrollY() - j);
      if (this.mTempRect.top >= 0)
        continue;
      this.mTempRect.top = 0;
    }
  }

  public void requestChildFocus(View paramView1, View paramView2)
  {
    if (!this.mIsLayoutDirty)
      scrollToChild(paramView2);
    while (true)
    {
      super.requestChildFocus(paramView1, paramView2);
      return;
      this.mChildToScrollTo = paramView2;
    }
  }

  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    paramRect.offset(paramView.getLeft() - paramView.getScrollX(), paramView.getTop() - paramView.getScrollY());
    return scrollToChildRect(paramRect, paramBoolean);
  }

  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    if (paramBoolean)
      recycleVelocityTracker();
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }

  public void requestLayout()
  {
    this.mIsLayoutDirty = true;
    super.requestLayout();
  }

  public void scrollTo(int paramInt1, int paramInt2)
  {
    if (getChildCount() > 0)
    {
      View localView = getChildAt(0);
      int i = clamp(paramInt1, getWidth() - getPaddingRight() - getPaddingLeft(), localView.getWidth());
      int j = clamp(paramInt2, getHeight() - getPaddingBottom() - getPaddingTop(), localView.getHeight());
      if ((i != getScrollX()) || (j != getScrollY()))
        super.scrollTo(i, j);
    }
  }

  public void setFillViewport(boolean paramBoolean)
  {
    if (paramBoolean != this.mFillViewport)
    {
      this.mFillViewport = paramBoolean;
      requestLayout();
    }
  }

  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    this.mChildHelper.setNestedScrollingEnabled(paramBoolean);
  }

  public void setOnScrollChangeListener(OnScrollChangeListener paramOnScrollChangeListener)
  {
    this.mOnScrollChangeListener = paramOnScrollChangeListener;
  }

  public void setSmoothScrollingEnabled(boolean paramBoolean)
  {
    this.mSmoothScrollingEnabled = paramBoolean;
  }

  public boolean shouldDelayChildPressedState()
  {
    return true;
  }

  public final void smoothScrollBy(int paramInt1, int paramInt2)
  {
    if (getChildCount() == 0)
      return;
    if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250L)
    {
      int i = getHeight() - getPaddingBottom() - getPaddingTop();
      int j = Math.max(0, getChildAt(0).getHeight() - i);
      int k = getScrollY();
      int m = Math.max(0, Math.min(k + paramInt2, j)) - k;
      this.mScroller.startScroll(getScrollX(), k, 0, m);
      ViewCompat.postInvalidateOnAnimation(this);
    }
    while (true)
    {
      this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
      return;
      if (!this.mScroller.isFinished())
        this.mScroller.abortAnimation();
      scrollBy(paramInt1, paramInt2);
    }
  }

  public final void smoothScrollTo(int paramInt1, int paramInt2)
  {
    smoothScrollBy(paramInt1 - getScrollX(), paramInt2 - getScrollY());
  }

  public boolean startNestedScroll(int paramInt)
  {
    return this.mChildHelper.startNestedScroll(paramInt);
  }

  public boolean startNestedScroll(int paramInt1, int paramInt2)
  {
    return this.mChildHelper.startNestedScroll(paramInt1, paramInt2);
  }

  public void stopNestedScroll()
  {
    this.mChildHelper.stopNestedScroll();
  }

  public void stopNestedScroll(int paramInt)
  {
    this.mChildHelper.stopNestedScroll(paramInt);
  }

  static class AccessibilityDelegate extends AccessibilityDelegateCompat
  {
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      NestedScrollView localNestedScrollView = (NestedScrollView)paramView;
      paramAccessibilityEvent.setClassName(ScrollView.class.getName());
      if (localNestedScrollView.getScrollRange() > 0);
      for (boolean bool = true; ; bool = false)
      {
        paramAccessibilityEvent.setScrollable(bool);
        paramAccessibilityEvent.setScrollX(localNestedScrollView.getScrollX());
        paramAccessibilityEvent.setScrollY(localNestedScrollView.getScrollY());
        AccessibilityRecordCompat.setMaxScrollX(paramAccessibilityEvent, localNestedScrollView.getScrollX());
        AccessibilityRecordCompat.setMaxScrollY(paramAccessibilityEvent, localNestedScrollView.getScrollRange());
        return;
      }
    }

    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      NestedScrollView localNestedScrollView = (NestedScrollView)paramView;
      paramAccessibilityNodeInfoCompat.setClassName(ScrollView.class.getName());
      if (localNestedScrollView.isEnabled())
      {
        int i = localNestedScrollView.getScrollRange();
        if (i > 0)
        {
          paramAccessibilityNodeInfoCompat.setScrollable(true);
          if (localNestedScrollView.getScrollY() > 0)
            paramAccessibilityNodeInfoCompat.addAction(8192);
          if (localNestedScrollView.getScrollY() < i)
            paramAccessibilityNodeInfoCompat.addAction(4096);
        }
      }
    }

    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      if (super.performAccessibilityAction(paramView, paramInt, paramBundle))
        return true;
      NestedScrollView localNestedScrollView = (NestedScrollView)paramView;
      if (!localNestedScrollView.isEnabled())
        return false;
      switch (paramInt)
      {
      default:
        return false;
      case 4096:
        int k = Math.min(localNestedScrollView.getHeight() - localNestedScrollView.getPaddingBottom() - localNestedScrollView.getPaddingTop() + localNestedScrollView.getScrollY(), localNestedScrollView.getScrollRange());
        if (k != localNestedScrollView.getScrollY())
        {
          localNestedScrollView.smoothScrollTo(0, k);
          return true;
        }
        return false;
      case 8192:
      }
      int i = localNestedScrollView.getHeight() - localNestedScrollView.getPaddingBottom() - localNestedScrollView.getPaddingTop();
      int j = Math.max(localNestedScrollView.getScrollY() - i, 0);
      if (j != localNestedScrollView.getScrollY())
      {
        localNestedScrollView.smoothScrollTo(0, j);
        return true;
      }
      return false;
    }
  }

  public static abstract interface OnScrollChangeListener
  {
    public abstract void onScrollChange(NestedScrollView paramNestedScrollView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }

  static class SavedState extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public NestedScrollView.SavedState createFromParcel(Parcel paramParcel)
      {
        return new NestedScrollView.SavedState(paramParcel);
      }

      public NestedScrollView.SavedState[] newArray(int paramInt)
      {
        return new NestedScrollView.SavedState[paramInt];
      }
    };
    public int scrollPosition;

    SavedState(Parcel paramParcel)
    {
      super();
      this.scrollPosition = paramParcel.readInt();
    }

    SavedState(Parcelable paramParcelable)
    {
      super();
    }

    public String toString()
    {
      return "HorizontalScrollView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " scrollPosition=" + this.scrollPosition + "}";
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.scrollPosition);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.widget.NestedScrollView
 * JD-Core Version:    0.6.0
 */