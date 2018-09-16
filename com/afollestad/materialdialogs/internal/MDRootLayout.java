package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ScrollView;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.R.attr;
import com.afollestad.materialdialogs.R.dimen;
import com.afollestad.materialdialogs.R.id;
import com.afollestad.materialdialogs.R.styleable;
import com.afollestad.materialdialogs.StackingBehavior;
import com.afollestad.materialdialogs.util.DialogUtils;

public class MDRootLayout extends ViewGroup
{
  private static final int INDEX_NEGATIVE = 1;
  private static final int INDEX_NEUTRAL = 0;
  private static final int INDEX_POSITIVE = 2;
  private ViewTreeObserver.OnScrollChangedListener bottomOnScrollChangedListener;
  private int buttonBarHeight;
  private GravityEnum buttonGravity = GravityEnum.START;
  private int buttonHorizontalEdgeMargin;
  private int buttonPaddingFull;
  private final MDButton[] buttons = new MDButton[3];
  private View content;
  private Paint dividerPaint;
  private int dividerWidth;
  private boolean drawBottomDivider = false;
  private boolean drawTopDivider = false;
  private boolean isStacked = false;
  private int maxHeight;
  private boolean noTitleNoPadding;
  private int noTitlePaddingFull;
  private boolean reducePaddingNoTitleNoButtons;
  private StackingBehavior stackBehavior = StackingBehavior.ADAPTIVE;
  private View titleBar;
  private ViewTreeObserver.OnScrollChangedListener topOnScrollChangedListener;
  private boolean useFullPadding = true;

  public MDRootLayout(Context paramContext)
  {
    super(paramContext);
    init(paramContext, null, 0);
  }

  public MDRootLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet, 0);
  }

  @TargetApi(11)
  public MDRootLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet, paramInt);
  }

  @TargetApi(21)
  public MDRootLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    init(paramContext, paramAttributeSet, paramInt1);
  }

  private void addScrollListener(ViewGroup paramViewGroup, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (((!paramBoolean2) && (this.topOnScrollChangedListener == null)) || ((paramBoolean2) && (this.bottomOnScrollChangedListener == null)))
    {
      if ((paramViewGroup instanceof RecyclerView))
      {
        2 local2 = new RecyclerView.OnScrollListener(paramViewGroup, paramBoolean1, paramBoolean2)
        {
          public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
          {
            super.onScrolled(paramRecyclerView, paramInt1, paramInt2);
            MDButton[] arrayOfMDButton = MDRootLayout.this.buttons;
            int i = arrayOfMDButton.length;
            for (int j = 0; ; j++)
            {
              boolean bool = false;
              if (j < i)
              {
                MDButton localMDButton = arrayOfMDButton[j];
                if ((localMDButton == null) || (localMDButton.getVisibility() == 8))
                  continue;
                bool = true;
              }
              MDRootLayout.this.invalidateDividersForScrollingView(this.val$vg, this.val$setForTop, this.val$setForBottom, bool);
              MDRootLayout.this.invalidate();
              return;
            }
          }
        };
        ((RecyclerView)paramViewGroup).addOnScrollListener(local2);
        local2.onScrolled((RecyclerView)paramViewGroup, 0, 0);
      }
    }
    else
      return;
    3 local3 = new ViewTreeObserver.OnScrollChangedListener(paramViewGroup, paramBoolean1, paramBoolean2)
    {
      public void onScrollChanged()
      {
        MDButton[] arrayOfMDButton = MDRootLayout.this.buttons;
        int i = arrayOfMDButton.length;
        int j = 0;
        boolean bool = false;
        if (j < i)
        {
          MDButton localMDButton = arrayOfMDButton[j];
          if ((localMDButton != null) && (localMDButton.getVisibility() != 8))
            bool = true;
        }
        else
        {
          if (!(this.val$vg instanceof WebView))
            break label92;
          MDRootLayout.this.invalidateDividersForWebView((WebView)this.val$vg, this.val$setForTop, this.val$setForBottom, bool);
        }
        while (true)
        {
          MDRootLayout.this.invalidate();
          return;
          j++;
          break;
          label92: MDRootLayout.this.invalidateDividersForScrollingView(this.val$vg, this.val$setForTop, this.val$setForBottom, bool);
        }
      }
    };
    if (!paramBoolean2)
    {
      this.topOnScrollChangedListener = local3;
      paramViewGroup.getViewTreeObserver().addOnScrollChangedListener(this.topOnScrollChangedListener);
    }
    while (true)
    {
      local3.onScrollChanged();
      return;
      this.bottomOnScrollChangedListener = local3;
      paramViewGroup.getViewTreeObserver().addOnScrollChangedListener(this.bottomOnScrollChangedListener);
    }
  }

  private static boolean canAdapterViewScroll(AdapterView paramAdapterView)
  {
    int i = 1;
    if (paramAdapterView.getLastVisiblePosition() == -1)
      i = 0;
    label100: label103: 
    while (true)
    {
      return i;
      int j;
      if (paramAdapterView.getFirstVisiblePosition() == 0)
      {
        j = i;
        if (paramAdapterView.getLastVisiblePosition() != -1 + paramAdapterView.getCount())
          break label100;
      }
      for (int k = i; ; k = 0)
      {
        if ((j == 0) || (k == 0) || (paramAdapterView.getChildCount() <= 0) || (paramAdapterView.getChildAt(0).getTop() < paramAdapterView.getPaddingTop()) || (paramAdapterView.getChildAt(-1 + paramAdapterView.getChildCount()).getBottom() > paramAdapterView.getHeight() - paramAdapterView.getPaddingBottom()))
          break label103;
        return false;
        j = 0;
        break;
      }
    }
  }

  public static boolean canRecyclerViewScroll(RecyclerView paramRecyclerView)
  {
    return (paramRecyclerView != null) && (paramRecyclerView.getLayoutManager() != null) && (paramRecyclerView.getLayoutManager().canScrollVertically());
  }

  private static boolean canScrollViewScroll(ScrollView paramScrollView)
  {
    if (paramScrollView.getChildCount() == 0);
    int i;
    do
    {
      return false;
      i = paramScrollView.getChildAt(0).getMeasuredHeight();
    }
    while (paramScrollView.getMeasuredHeight() - paramScrollView.getPaddingTop() - paramScrollView.getPaddingBottom() >= i);
    return true;
  }

  private static boolean canWebViewScroll(WebView paramWebView)
  {
    return paramWebView.getMeasuredHeight() < paramWebView.getContentHeight() * paramWebView.getScale();
  }

  @Nullable
  private static View getBottomView(ViewGroup paramViewGroup)
  {
    if ((paramViewGroup == null) || (paramViewGroup.getChildCount() == 0));
    while (true)
    {
      return null;
      for (int i = -1 + paramViewGroup.getChildCount(); i >= 0; i--)
      {
        View localView = paramViewGroup.getChildAt(i);
        if ((localView.getVisibility() == 0) && (localView.getBottom() == paramViewGroup.getMeasuredHeight()))
          return localView;
      }
    }
  }

  @Nullable
  private static View getTopView(ViewGroup paramViewGroup)
  {
    if ((paramViewGroup == null) || (paramViewGroup.getChildCount() == 0));
    while (true)
    {
      return null;
      for (int i = -1 + paramViewGroup.getChildCount(); i >= 0; i--)
      {
        View localView = paramViewGroup.getChildAt(i);
        if ((localView.getVisibility() == 0) && (localView.getTop() == 0))
          return localView;
      }
    }
  }

  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    Resources localResources = paramContext.getResources();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MDRootLayout, paramInt, 0);
    this.reducePaddingNoTitleNoButtons = localTypedArray.getBoolean(R.styleable.MDRootLayout_md_reduce_padding_no_title_no_buttons, true);
    localTypedArray.recycle();
    this.noTitlePaddingFull = localResources.getDimensionPixelSize(R.dimen.md_notitle_vertical_padding);
    this.buttonPaddingFull = localResources.getDimensionPixelSize(R.dimen.md_button_frame_vertical_padding);
    this.buttonHorizontalEdgeMargin = localResources.getDimensionPixelSize(R.dimen.md_button_padding_frame_side);
    this.buttonBarHeight = localResources.getDimensionPixelSize(R.dimen.md_button_height);
    this.dividerPaint = new Paint();
    this.dividerWidth = localResources.getDimensionPixelSize(R.dimen.md_divider_height);
    this.dividerPaint.setColor(DialogUtils.resolveColor(paramContext, R.attr.md_divider_color));
    setWillNotDraw(false);
  }

  private void invalidateDividersForScrollingView(ViewGroup paramViewGroup, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool1 = true;
    boolean bool2;
    if ((paramBoolean1) && (paramViewGroup.getChildCount() > 0))
    {
      if ((this.titleBar != null) && (this.titleBar.getVisibility() != 8) && (paramViewGroup.getScrollY() + paramViewGroup.getPaddingTop() > paramViewGroup.getChildAt(0).getTop()))
      {
        bool2 = bool1;
        this.drawTopDivider = bool2;
      }
    }
    else if ((paramBoolean2) && (paramViewGroup.getChildCount() > 0))
      if ((!paramBoolean3) || (paramViewGroup.getScrollY() + paramViewGroup.getHeight() - paramViewGroup.getPaddingBottom() >= paramViewGroup.getChildAt(-1 + paramViewGroup.getChildCount()).getBottom()))
        break label122;
    while (true)
    {
      this.drawBottomDivider = bool1;
      return;
      bool2 = false;
      break;
      label122: bool1 = false;
    }
  }

  private void invalidateDividersForWebView(WebView paramWebView, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramBoolean1)
    {
      if ((this.titleBar != null) && (this.titleBar.getVisibility() != 8) && (paramWebView.getScrollY() + paramWebView.getPaddingTop() > 0))
      {
        bool2 = bool1;
        this.drawTopDivider = bool2;
      }
    }
    else if (paramBoolean2)
      if ((!paramBoolean3) || (paramWebView.getScrollY() + paramWebView.getMeasuredHeight() - paramWebView.getPaddingBottom() >= paramWebView.getContentHeight() * paramWebView.getScale()))
        break label99;
    while (true)
    {
      this.drawBottomDivider = bool1;
      return;
      bool2 = false;
      break;
      label99: bool1 = false;
    }
  }

  private void invertGravityIfNecessary()
  {
    if (Build.VERSION.SDK_INT < 17);
    do
      return;
    while (getResources().getConfiguration().getLayoutDirection() != 1);
    switch (4.$SwitchMap$com$afollestad$materialdialogs$GravityEnum[this.buttonGravity.ordinal()])
    {
    default:
      return;
    case 1:
      this.buttonGravity = GravityEnum.END;
      return;
    case 2:
    }
    this.buttonGravity = GravityEnum.START;
  }

  private static boolean isVisible(View paramView)
  {
    if ((paramView != null) && (paramView.getVisibility() != 8));
    for (int i = 1; ; i = 0)
    {
      if ((i != 0) && ((paramView instanceof MDButton)))
      {
        if (((MDButton)paramView).getText().toString().trim().length() <= 0)
          break;
        i = 1;
      }
      return i;
    }
    return false;
  }

  private void setUpDividersVisibility(View paramView, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramView == null);
    View localView1;
    View localView2;
    do
    {
      do
        while (true)
        {
          return;
          if ((paramView instanceof ScrollView))
          {
            ScrollView localScrollView = (ScrollView)paramView;
            if (canScrollViewScroll(localScrollView))
            {
              addScrollListener(localScrollView, paramBoolean1, paramBoolean2);
              return;
            }
            if (paramBoolean1)
              this.drawTopDivider = false;
            if (!paramBoolean2)
              continue;
            this.drawBottomDivider = false;
            return;
          }
          if ((paramView instanceof AdapterView))
          {
            AdapterView localAdapterView = (AdapterView)paramView;
            if (canAdapterViewScroll(localAdapterView))
            {
              addScrollListener(localAdapterView, paramBoolean1, paramBoolean2);
              return;
            }
            if (paramBoolean1)
              this.drawTopDivider = false;
            if (!paramBoolean2)
              continue;
            this.drawBottomDivider = false;
            return;
          }
          if ((paramView instanceof WebView))
          {
            paramView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(paramView, paramBoolean1, paramBoolean2)
            {
              public boolean onPreDraw()
              {
                if (this.val$view.getMeasuredHeight() != 0)
                {
                  if (MDRootLayout.access$000((WebView)this.val$view))
                    break label68;
                  if (this.val$setForTop)
                    MDRootLayout.access$102(MDRootLayout.this, false);
                  if (this.val$setForBottom)
                    MDRootLayout.access$202(MDRootLayout.this, false);
                }
                while (true)
                {
                  this.val$view.getViewTreeObserver().removeOnPreDrawListener(this);
                  return true;
                  label68: MDRootLayout.this.addScrollListener((ViewGroup)this.val$view, this.val$setForTop, this.val$setForBottom);
                }
              }
            });
            return;
          }
          if (!(paramView instanceof RecyclerView))
            break;
          boolean bool = canRecyclerViewScroll((RecyclerView)paramView);
          if (paramBoolean1)
            this.drawTopDivider = bool;
          if (paramBoolean2)
            this.drawBottomDivider = bool;
          if (!bool)
            continue;
          addScrollListener((ViewGroup)paramView, paramBoolean1, paramBoolean2);
          return;
        }
      while (!(paramView instanceof ViewGroup));
      localView1 = getTopView((ViewGroup)paramView);
      setUpDividersVisibility(localView1, paramBoolean1, paramBoolean2);
      localView2 = getBottomView((ViewGroup)paramView);
    }
    while (localView2 == localView1);
    setUpDividersVisibility(localView2, false, true);
  }

  public void noTitleNoPadding()
  {
    this.noTitleNoPadding = true;
  }

  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.content != null)
    {
      if (this.drawTopDivider)
      {
        int j = this.content.getTop();
        paramCanvas.drawRect(0.0F, j - this.dividerWidth, getMeasuredWidth(), j, this.dividerPaint);
      }
      if (this.drawBottomDivider)
      {
        int i = this.content.getBottom();
        paramCanvas.drawRect(0.0F, i, getMeasuredWidth(), i + this.dividerWidth, this.dividerPaint);
      }
    }
  }

  public void onFinishInflate()
  {
    super.onFinishInflate();
    int i = 0;
    if (i < getChildCount())
    {
      View localView = getChildAt(i);
      if (localView.getId() == R.id.md_titleFrame)
        this.titleBar = localView;
      while (true)
      {
        i++;
        break;
        if (localView.getId() == R.id.md_buttonDefaultNeutral)
        {
          this.buttons[0] = ((MDButton)localView);
          continue;
        }
        if (localView.getId() == R.id.md_buttonDefaultNegative)
        {
          this.buttons[1] = ((MDButton)localView);
          continue;
        }
        if (localView.getId() == R.id.md_buttonDefaultPositive)
        {
          this.buttons[2] = ((MDButton)localView);
          continue;
        }
        this.content = localView;
      }
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (isVisible(this.titleBar))
    {
      int i11 = this.titleBar.getMeasuredHeight();
      View localView2 = this.titleBar;
      int i12 = paramInt2 + i11;
      localView2.layout(paramInt1, paramInt2, paramInt3, i12);
      paramInt2 += i11;
    }
    int i;
    int j;
    int k;
    int m;
    int n;
    int i6;
    int i5;
    int i3;
    int i4;
    label351: int i2;
    int i1;
    while (true)
    {
      if (isVisible(this.content))
      {
        View localView1 = this.content;
        int i10 = paramInt2 + this.content.getMeasuredHeight();
        localView1.layout(paramInt1, paramInt2, paramInt3, i10);
      }
      if (this.isStacked)
      {
        int i7 = paramInt4 - this.buttonPaddingFull;
        for (MDButton localMDButton : this.buttons)
        {
          if (!isVisible(localMDButton))
            continue;
          localMDButton.layout(paramInt1, i7 - localMDButton.getMeasuredHeight(), paramInt3, i7);
          i7 -= localMDButton.getMeasuredHeight();
        }
        if ((this.noTitleNoPadding) || (!this.useFullPadding))
          continue;
        paramInt2 += this.noTitlePaddingFull;
        continue;
      }
      i = paramInt4;
      if (this.useFullPadding)
        i -= this.buttonPaddingFull;
      j = i - this.buttonBarHeight;
      k = this.buttonHorizontalEdgeMargin;
      m = -1;
      n = -1;
      if (isVisible(this.buttons[2]))
      {
        if (this.buttonGravity != GravityEnum.END)
          break label441;
        i6 = paramInt1 + k;
        i5 = i6 + this.buttons[2].getMeasuredWidth();
        this.buttons[2].layout(i6, j, i5, i);
        k += this.buttons[2].getMeasuredWidth();
      }
      if (isVisible(this.buttons[1]))
      {
        if (this.buttonGravity != GravityEnum.END)
          break label469;
        i3 = paramInt1 + k;
        i4 = i3 + this.buttons[1].getMeasuredWidth();
        this.buttons[1].layout(i3, j, i4, i);
      }
      if (!isVisible(this.buttons[0]))
        break;
      if (this.buttonGravity != GravityEnum.END)
        break label532;
      i2 = paramInt3 - this.buttonHorizontalEdgeMargin;
      i1 = i2 - this.buttons[0].getMeasuredWidth();
    }
    while (true)
    {
      this.buttons[0].layout(i1, j, i2, i);
      setUpDividersVisibility(this.content, true, true);
      return;
      label441: i5 = paramInt3 - k;
      i6 = i5 - this.buttons[2].getMeasuredWidth();
      n = i6;
      break;
      label469: if (this.buttonGravity == GravityEnum.START)
      {
        i4 = paramInt3 - k;
        i3 = i4 - this.buttons[1].getMeasuredWidth();
        break label351;
      }
      i3 = paramInt1 + this.buttonHorizontalEdgeMargin;
      i4 = i3 + this.buttons[1].getMeasuredWidth();
      m = i4;
      break label351;
      label532: if (this.buttonGravity != GravityEnum.START)
        break label567;
      i1 = paramInt1 + this.buttonHorizontalEdgeMargin;
      i2 = i1 + this.buttons[0].getMeasuredWidth();
    }
    label567: if ((m == -1) && (n != -1))
      m = n - this.buttons[0].getMeasuredWidth();
    while (true)
    {
      i1 = m;
      i2 = n;
      break;
      if ((n == -1) && (m != -1))
      {
        n = m + this.buttons[0].getMeasuredWidth();
        continue;
      }
      if (n != -1)
        continue;
      m = (paramInt3 - paramInt1) / 2 - this.buttons[0].getMeasuredWidth() / 2;
      n = m + this.buttons[0].getMeasuredWidth();
    }
  }

  public void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    if (j > this.maxHeight)
      j = this.maxHeight;
    this.useFullPadding = true;
    int k = 0;
    boolean bool;
    if (this.stackBehavior == StackingBehavior.ALWAYS)
      bool = true;
    int m;
    while (true)
    {
      this.isStacked = bool;
      m = 0;
      if (!bool)
        break;
      MDButton[] arrayOfMDButton1 = this.buttons;
      int i3 = arrayOfMDButton1.length;
      int i4 = 0;
      while (true)
        if (i4 < i3)
        {
          MDButton localMDButton1 = arrayOfMDButton1[i4];
          if ((localMDButton1 != null) && (isVisible(localMDButton1)))
          {
            localMDButton1.setStacked(true, false);
            measureChild(localMDButton1, paramInt1, paramInt2);
            m += localMDButton1.getMeasuredHeight();
            k = 1;
          }
          i4++;
          continue;
          if (this.stackBehavior == StackingBehavior.NEVER)
          {
            k = 0;
            bool = false;
            break;
          }
          int i5 = 0;
          for (MDButton localMDButton2 : this.buttons)
          {
            if ((localMDButton2 == null) || (!isVisible(localMDButton2)))
              continue;
            localMDButton2.setStacked(false, false);
            measureChild(localMDButton2, paramInt1, paramInt2);
            i5 += localMDButton2.getMeasuredWidth();
            k = 1;
          }
          if (i5 > i - 2 * getContext().getResources().getDimensionPixelSize(R.dimen.md_neutral_button_margin));
          for (bool = true; ; bool = false)
            break;
        }
    }
    int n = j;
    int i1;
    int i2;
    if (k != 0)
      if (this.isStacked)
      {
        n -= m;
        i1 = 0 + 2 * this.buttonPaddingFull;
        i2 = 0 + 2 * this.buttonPaddingFull;
        if (!isVisible(this.titleBar))
          break label491;
        this.titleBar.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), 0);
        n -= this.titleBar.getMeasuredHeight();
        label347: if (isVisible(this.content))
        {
          this.content.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(n - i2, -2147483648));
          if (this.content.getMeasuredHeight() > n - i1)
            break label533;
          if ((this.reducePaddingNoTitleNoButtons) && (!isVisible(this.titleBar)) && (k == 0))
            break label510;
          this.useFullPadding = true;
          n -= i1 + this.content.getMeasuredHeight();
        }
      }
    while (true)
    {
      setMeasuredDimension(i, j - n);
      return;
      n -= this.buttonBarHeight;
      i1 = 0 + 2 * this.buttonPaddingFull;
      i2 = 0;
      break;
      i1 = 0 + 2 * this.buttonPaddingFull;
      i2 = 0;
      break;
      label491: if (this.noTitleNoPadding)
        break label347;
      i1 += this.noTitlePaddingFull;
      break label347;
      label510: this.useFullPadding = false;
      n -= i2 + this.content.getMeasuredHeight();
      continue;
      label533: this.useFullPadding = false;
      n = 0;
    }
  }

  public void setButtonGravity(GravityEnum paramGravityEnum)
  {
    this.buttonGravity = paramGravityEnum;
    invertGravityIfNecessary();
  }

  public void setButtonStackedGravity(GravityEnum paramGravityEnum)
  {
    for (MDButton localMDButton : this.buttons)
    {
      if (localMDButton == null)
        continue;
      localMDButton.setStackedGravity(paramGravityEnum);
    }
  }

  public void setDividerColor(int paramInt)
  {
    this.dividerPaint.setColor(paramInt);
    invalidate();
  }

  public void setMaxHeight(int paramInt)
  {
    this.maxHeight = paramInt;
  }

  public void setStackingBehavior(StackingBehavior paramStackingBehavior)
  {
    this.stackBehavior = paramStackingBehavior;
    invalidate();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.internal.MDRootLayout
 * JD-Core Version:    0.6.0
 */