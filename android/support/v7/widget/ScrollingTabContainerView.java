package android.support.v7.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.appcompat.R.attr;
import android.support.v7.view.ActionBarPolicy;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class ScrollingTabContainerView extends HorizontalScrollView
  implements AdapterView.OnItemSelectedListener
{
  private static final int FADE_DURATION = 200;
  private static final String TAG = "ScrollingTabContainerView";
  private static final Interpolator sAlphaInterpolator = new DecelerateInterpolator();
  private boolean mAllowCollapse;
  private int mContentHeight;
  int mMaxTabWidth;
  private int mSelectedTabIndex;
  int mStackedTabMaxWidth;
  private TabClickListener mTabClickListener;
  LinearLayoutCompat mTabLayout;
  Runnable mTabSelector;
  private Spinner mTabSpinner;
  protected final VisibilityAnimListener mVisAnimListener = new VisibilityAnimListener();
  protected ViewPropertyAnimator mVisibilityAnim;

  public ScrollingTabContainerView(Context paramContext)
  {
    super(paramContext);
    setHorizontalScrollBarEnabled(false);
    ActionBarPolicy localActionBarPolicy = ActionBarPolicy.get(paramContext);
    setContentHeight(localActionBarPolicy.getTabContainerHeight());
    this.mStackedTabMaxWidth = localActionBarPolicy.getStackedTabMaxWidth();
    this.mTabLayout = createTabLayout();
    addView(this.mTabLayout, new ViewGroup.LayoutParams(-2, -1));
  }

  private Spinner createSpinner()
  {
    AppCompatSpinner localAppCompatSpinner = new AppCompatSpinner(getContext(), null, R.attr.actionDropDownStyle);
    localAppCompatSpinner.setLayoutParams(new LinearLayoutCompat.LayoutParams(-2, -1));
    localAppCompatSpinner.setOnItemSelectedListener(this);
    return localAppCompatSpinner;
  }

  private LinearLayoutCompat createTabLayout()
  {
    LinearLayoutCompat localLinearLayoutCompat = new LinearLayoutCompat(getContext(), null, R.attr.actionBarTabBarStyle);
    localLinearLayoutCompat.setMeasureWithLargestChildEnabled(true);
    localLinearLayoutCompat.setGravity(17);
    localLinearLayoutCompat.setLayoutParams(new LinearLayoutCompat.LayoutParams(-2, -1));
    return localLinearLayoutCompat;
  }

  private boolean isCollapsed()
  {
    return (this.mTabSpinner != null) && (this.mTabSpinner.getParent() == this);
  }

  private void performCollapse()
  {
    if (isCollapsed())
      return;
    if (this.mTabSpinner == null)
      this.mTabSpinner = createSpinner();
    removeView(this.mTabLayout);
    addView(this.mTabSpinner, new ViewGroup.LayoutParams(-2, -1));
    if (this.mTabSpinner.getAdapter() == null)
      this.mTabSpinner.setAdapter(new TabAdapter());
    if (this.mTabSelector != null)
    {
      removeCallbacks(this.mTabSelector);
      this.mTabSelector = null;
    }
    this.mTabSpinner.setSelection(this.mSelectedTabIndex);
  }

  private boolean performExpand()
  {
    if (!isCollapsed())
      return false;
    removeView(this.mTabSpinner);
    addView(this.mTabLayout, new ViewGroup.LayoutParams(-2, -1));
    setTabSelected(this.mTabSpinner.getSelectedItemPosition());
    return false;
  }

  public void addTab(ActionBar.Tab paramTab, int paramInt, boolean paramBoolean)
  {
    TabView localTabView = createTabView(paramTab, false);
    this.mTabLayout.addView(localTabView, paramInt, new LinearLayoutCompat.LayoutParams(0, -1, 1.0F));
    if (this.mTabSpinner != null)
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    if (paramBoolean)
      localTabView.setSelected(true);
    if (this.mAllowCollapse)
      requestLayout();
  }

  public void addTab(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    TabView localTabView = createTabView(paramTab, false);
    this.mTabLayout.addView(localTabView, new LinearLayoutCompat.LayoutParams(0, -1, 1.0F));
    if (this.mTabSpinner != null)
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    if (paramBoolean)
      localTabView.setSelected(true);
    if (this.mAllowCollapse)
      requestLayout();
  }

  public void animateToTab(int paramInt)
  {
    View localView = this.mTabLayout.getChildAt(paramInt);
    if (this.mTabSelector != null)
      removeCallbacks(this.mTabSelector);
    this.mTabSelector = new Runnable(localView)
    {
      public void run()
      {
        int i = this.val$tabView.getLeft() - (ScrollingTabContainerView.this.getWidth() - this.val$tabView.getWidth()) / 2;
        ScrollingTabContainerView.this.smoothScrollTo(i, 0);
        ScrollingTabContainerView.this.mTabSelector = null;
      }
    };
    post(this.mTabSelector);
  }

  public void animateToVisibility(int paramInt)
  {
    if (this.mVisibilityAnim != null)
      this.mVisibilityAnim.cancel();
    if (paramInt == 0)
    {
      if (getVisibility() != 0)
        setAlpha(0.0F);
      ViewPropertyAnimator localViewPropertyAnimator2 = animate().alpha(1.0F);
      localViewPropertyAnimator2.setDuration(200L);
      localViewPropertyAnimator2.setInterpolator(sAlphaInterpolator);
      localViewPropertyAnimator2.setListener(this.mVisAnimListener.withFinalVisibility(localViewPropertyAnimator2, paramInt));
      localViewPropertyAnimator2.start();
      return;
    }
    ViewPropertyAnimator localViewPropertyAnimator1 = animate().alpha(0.0F);
    localViewPropertyAnimator1.setDuration(200L);
    localViewPropertyAnimator1.setInterpolator(sAlphaInterpolator);
    localViewPropertyAnimator1.setListener(this.mVisAnimListener.withFinalVisibility(localViewPropertyAnimator1, paramInt));
    localViewPropertyAnimator1.start();
  }

  TabView createTabView(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    TabView localTabView = new TabView(getContext(), paramTab, paramBoolean);
    if (paramBoolean)
    {
      localTabView.setBackgroundDrawable(null);
      localTabView.setLayoutParams(new AbsListView.LayoutParams(-1, this.mContentHeight));
      return localTabView;
    }
    localTabView.setFocusable(true);
    if (this.mTabClickListener == null)
      this.mTabClickListener = new TabClickListener();
    localTabView.setOnClickListener(this.mTabClickListener);
    return localTabView;
  }

  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mTabSelector != null)
      post(this.mTabSelector);
  }

  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    ActionBarPolicy localActionBarPolicy = ActionBarPolicy.get(getContext());
    setContentHeight(localActionBarPolicy.getTabContainerHeight());
    this.mStackedTabMaxWidth = localActionBarPolicy.getStackedTabMaxWidth();
  }

  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mTabSelector != null)
      removeCallbacks(this.mTabSelector);
  }

  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    ((TabView)paramView).getTab().select();
  }

  public void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    boolean bool;
    label70: label85: int k;
    int m;
    if (i == 1073741824)
    {
      bool = true;
      setFillViewport(bool);
      int j = this.mTabLayout.getChildCount();
      if ((j <= 1) || ((i != 1073741824) && (i != -2147483648)))
        break label204;
      if (j <= 2)
        break label191;
      this.mMaxTabWidth = (int)(0.4F * View.MeasureSpec.getSize(paramInt1));
      this.mMaxTabWidth = Math.min(this.mMaxTabWidth, this.mStackedTabMaxWidth);
      k = View.MeasureSpec.makeMeasureSpec(this.mContentHeight, 1073741824);
      if ((bool) || (!this.mAllowCollapse))
        break label212;
      m = 1;
      label112: if (m == 0)
        break label226;
      this.mTabLayout.measure(0, k);
      if (this.mTabLayout.getMeasuredWidth() <= View.MeasureSpec.getSize(paramInt1))
        break label218;
      performCollapse();
    }
    while (true)
    {
      int n = getMeasuredWidth();
      super.onMeasure(paramInt1, k);
      int i1 = getMeasuredWidth();
      if ((bool) && (n != i1))
        setTabSelected(this.mSelectedTabIndex);
      return;
      bool = false;
      break;
      label191: this.mMaxTabWidth = (View.MeasureSpec.getSize(paramInt1) / 2);
      break label70;
      label204: this.mMaxTabWidth = -1;
      break label85;
      label212: m = 0;
      break label112;
      label218: performExpand();
      continue;
      label226: performExpand();
    }
  }

  public void onNothingSelected(AdapterView<?> paramAdapterView)
  {
  }

  public void removeAllTabs()
  {
    this.mTabLayout.removeAllViews();
    if (this.mTabSpinner != null)
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    if (this.mAllowCollapse)
      requestLayout();
  }

  public void removeTabAt(int paramInt)
  {
    this.mTabLayout.removeViewAt(paramInt);
    if (this.mTabSpinner != null)
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    if (this.mAllowCollapse)
      requestLayout();
  }

  public void setAllowCollapse(boolean paramBoolean)
  {
    this.mAllowCollapse = paramBoolean;
  }

  public void setContentHeight(int paramInt)
  {
    this.mContentHeight = paramInt;
    requestLayout();
  }

  public void setTabSelected(int paramInt)
  {
    this.mSelectedTabIndex = paramInt;
    int i = this.mTabLayout.getChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = this.mTabLayout.getChildAt(j);
      if (j == paramInt);
      for (boolean bool = true; ; bool = false)
      {
        localView.setSelected(bool);
        if (bool)
          animateToTab(paramInt);
        j++;
        break;
      }
    }
    if ((this.mTabSpinner != null) && (paramInt >= 0))
      this.mTabSpinner.setSelection(paramInt);
  }

  public void updateTab(int paramInt)
  {
    ((TabView)this.mTabLayout.getChildAt(paramInt)).update();
    if (this.mTabSpinner != null)
      ((TabAdapter)this.mTabSpinner.getAdapter()).notifyDataSetChanged();
    if (this.mAllowCollapse)
      requestLayout();
  }

  private class TabAdapter extends BaseAdapter
  {
    TabAdapter()
    {
    }

    public int getCount()
    {
      return ScrollingTabContainerView.this.mTabLayout.getChildCount();
    }

    public Object getItem(int paramInt)
    {
      return ((ScrollingTabContainerView.TabView)ScrollingTabContainerView.this.mTabLayout.getChildAt(paramInt)).getTab();
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
        return ScrollingTabContainerView.this.createTabView((ActionBar.Tab)getItem(paramInt), true);
      ((ScrollingTabContainerView.TabView)paramView).bindTab((ActionBar.Tab)getItem(paramInt));
      return paramView;
    }
  }

  private class TabClickListener
    implements View.OnClickListener
  {
    TabClickListener()
    {
    }

    public void onClick(View paramView)
    {
      ((ScrollingTabContainerView.TabView)paramView).getTab().select();
      int i = ScrollingTabContainerView.this.mTabLayout.getChildCount();
      int j = 0;
      if (j < i)
      {
        View localView = ScrollingTabContainerView.this.mTabLayout.getChildAt(j);
        if (localView == paramView);
        for (boolean bool = true; ; bool = false)
        {
          localView.setSelected(bool);
          j++;
          break;
        }
      }
    }
  }

  private class TabView extends LinearLayoutCompat
  {
    private final int[] BG_ATTRS = { 16842964 };
    private View mCustomView;
    private ImageView mIconView;
    private ActionBar.Tab mTab;
    private TextView mTextView;

    public TabView(Context paramTab, ActionBar.Tab paramBoolean, boolean arg4)
    {
      super(null, R.attr.actionBarTabStyle);
      this.mTab = paramBoolean;
      TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramTab, null, this.BG_ATTRS, R.attr.actionBarTabStyle, 0);
      if (localTintTypedArray.hasValue(0))
        setBackgroundDrawable(localTintTypedArray.getDrawable(0));
      localTintTypedArray.recycle();
      int i;
      if (i != 0)
        setGravity(8388627);
      update();
    }

    public void bindTab(ActionBar.Tab paramTab)
    {
      this.mTab = paramTab;
      update();
    }

    public ActionBar.Tab getTab()
    {
      return this.mTab;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(ActionBar.Tab.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.setClassName(ActionBar.Tab.class.getName());
    }

    public void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      if ((ScrollingTabContainerView.this.mMaxTabWidth > 0) && (getMeasuredWidth() > ScrollingTabContainerView.this.mMaxTabWidth))
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(ScrollingTabContainerView.this.mMaxTabWidth, 1073741824), paramInt2);
    }

    public void setSelected(boolean paramBoolean)
    {
      if (isSelected() != paramBoolean);
      for (int i = 1; ; i = 0)
      {
        super.setSelected(paramBoolean);
        if ((i != 0) && (paramBoolean))
          sendAccessibilityEvent(4);
        return;
      }
    }

    public void update()
    {
      ActionBar.Tab localTab = this.mTab;
      View localView = localTab.getCustomView();
      if (localView != null)
      {
        ViewParent localViewParent = localView.getParent();
        if (localViewParent != this)
        {
          if (localViewParent != null)
            ((ViewGroup)localViewParent).removeView(localView);
          addView(localView);
        }
        this.mCustomView = localView;
        if (this.mTextView != null)
          this.mTextView.setVisibility(8);
        if (this.mIconView != null)
        {
          this.mIconView.setVisibility(8);
          this.mIconView.setImageDrawable(null);
        }
        return;
      }
      if (this.mCustomView != null)
      {
        removeView(this.mCustomView);
        this.mCustomView = null;
      }
      Drawable localDrawable = localTab.getIcon();
      CharSequence localCharSequence1 = localTab.getText();
      int i;
      label213: CharSequence localCharSequence2;
      if (localDrawable != null)
      {
        if (this.mIconView == null)
        {
          AppCompatImageView localAppCompatImageView = new AppCompatImageView(getContext());
          LinearLayoutCompat.LayoutParams localLayoutParams2 = new LinearLayoutCompat.LayoutParams(-2, -2);
          localLayoutParams2.gravity = 16;
          localAppCompatImageView.setLayoutParams(localLayoutParams2);
          addView(localAppCompatImageView, 0);
          this.mIconView = localAppCompatImageView;
        }
        this.mIconView.setImageDrawable(localDrawable);
        this.mIconView.setVisibility(0);
        if (TextUtils.isEmpty(localCharSequence1))
          break label366;
        i = 1;
        if (i == 0)
          break label372;
        if (this.mTextView == null)
        {
          AppCompatTextView localAppCompatTextView = new AppCompatTextView(getContext(), null, R.attr.actionBarTabTextStyle);
          localAppCompatTextView.setEllipsize(TextUtils.TruncateAt.END);
          LinearLayoutCompat.LayoutParams localLayoutParams1 = new LinearLayoutCompat.LayoutParams(-2, -2);
          localLayoutParams1.gravity = 16;
          localAppCompatTextView.setLayoutParams(localLayoutParams1);
          addView(localAppCompatTextView);
          this.mTextView = localAppCompatTextView;
        }
        this.mTextView.setText(localCharSequence1);
        this.mTextView.setVisibility(0);
        label306: if (this.mIconView != null)
          this.mIconView.setContentDescription(localTab.getContentDescription());
        localCharSequence2 = null;
        if (i == 0)
          break label399;
      }
      while (true)
      {
        TooltipCompat.setTooltipText(this, localCharSequence2);
        return;
        if (this.mIconView == null)
          break;
        this.mIconView.setVisibility(8);
        this.mIconView.setImageDrawable(null);
        break;
        label366: i = 0;
        break label213;
        label372: if (this.mTextView == null)
          break label306;
        this.mTextView.setVisibility(8);
        this.mTextView.setText(null);
        break label306;
        label399: localCharSequence2 = localTab.getContentDescription();
      }
    }
  }

  protected class VisibilityAnimListener extends AnimatorListenerAdapter
  {
    private boolean mCanceled = false;
    private int mFinalVisibility;

    protected VisibilityAnimListener()
    {
    }

    public void onAnimationCancel(Animator paramAnimator)
    {
      this.mCanceled = true;
    }

    public void onAnimationEnd(Animator paramAnimator)
    {
      if (this.mCanceled)
        return;
      ScrollingTabContainerView.this.mVisibilityAnim = null;
      ScrollingTabContainerView.this.setVisibility(this.mFinalVisibility);
    }

    public void onAnimationStart(Animator paramAnimator)
    {
      ScrollingTabContainerView.this.setVisibility(0);
      this.mCanceled = false;
    }

    public VisibilityAnimListener withFinalVisibility(ViewPropertyAnimator paramViewPropertyAnimator, int paramInt)
    {
      this.mFinalVisibility = paramInt;
      ScrollingTabContainerView.this.mVisibilityAnim = paramViewPropertyAnimator;
      return this;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.ScrollingTabContainerView
 * JD-Core Version:    0.6.0
 */