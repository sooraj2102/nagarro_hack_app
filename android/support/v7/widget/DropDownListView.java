package android.support.v7.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.appcompat.R.attr;
import android.view.MotionEvent;
import android.view.View;

class DropDownListView extends ListViewCompat
{
  private ViewPropertyAnimatorCompat mClickAnimation;
  private boolean mDrawsInPressedState;
  private boolean mHijackFocus;
  private boolean mListSelectionHidden;
  private ListViewAutoScrollHelper mScrollHelper;

  public DropDownListView(Context paramContext, boolean paramBoolean)
  {
    super(paramContext, null, R.attr.dropDownListViewStyle);
    this.mHijackFocus = paramBoolean;
    setCacheColorHint(0);
  }

  private void clearPressedItem()
  {
    this.mDrawsInPressedState = false;
    setPressed(false);
    drawableStateChanged();
    View localView = getChildAt(this.mMotionPosition - getFirstVisiblePosition());
    if (localView != null)
      localView.setPressed(false);
    if (this.mClickAnimation != null)
    {
      this.mClickAnimation.cancel();
      this.mClickAnimation = null;
    }
  }

  private void clickPressedItem(View paramView, int paramInt)
  {
    performItemClick(paramView, paramInt, getItemIdAtPosition(paramInt));
  }

  private void setPressedItem(View paramView, int paramInt, float paramFloat1, float paramFloat2)
  {
    this.mDrawsInPressedState = true;
    if (Build.VERSION.SDK_INT >= 21)
      drawableHotspotChanged(paramFloat1, paramFloat2);
    if (!isPressed())
      setPressed(true);
    layoutChildren();
    if (this.mMotionPosition != -1)
    {
      View localView = getChildAt(this.mMotionPosition - getFirstVisiblePosition());
      if ((localView != null) && (localView != paramView) && (localView.isPressed()))
        localView.setPressed(false);
    }
    this.mMotionPosition = paramInt;
    float f1 = paramFloat1 - paramView.getLeft();
    float f2 = paramFloat2 - paramView.getTop();
    if (Build.VERSION.SDK_INT >= 21)
      paramView.drawableHotspotChanged(f1, f2);
    if (!paramView.isPressed())
      paramView.setPressed(true);
    positionSelectorLikeTouchCompat(paramInt, paramView, paramFloat1, paramFloat2);
    setSelectorEnabled(false);
    refreshDrawableState();
  }

  public boolean hasFocus()
  {
    return (this.mHijackFocus) || (super.hasFocus());
  }

  public boolean hasWindowFocus()
  {
    return (this.mHijackFocus) || (super.hasWindowFocus());
  }

  public boolean isFocused()
  {
    return (this.mHijackFocus) || (super.isFocused());
  }

  public boolean isInTouchMode()
  {
    return ((this.mHijackFocus) && (this.mListSelectionHidden)) || (super.isInTouchMode());
  }

  public boolean onForwardedEvent(MotionEvent paramMotionEvent, int paramInt)
  {
    int i = 1;
    int j = paramMotionEvent.getActionMasked();
    int k = 0;
    switch (j)
    {
    default:
      if ((i == 0) || (k != 0))
        clearPressedItem();
      if (i == 0)
        break;
      if (this.mScrollHelper == null)
        this.mScrollHelper = new ListViewAutoScrollHelper(this);
      this.mScrollHelper.setEnabled(true);
      this.mScrollHelper.onTouch(this, paramMotionEvent);
    case 3:
    case 1:
    case 2:
    }
    do
    {
      return i;
      k = 0;
      i = 0;
      break;
      i = 0;
      int m = paramMotionEvent.findPointerIndex(paramInt);
      if (m < 0)
      {
        k = 0;
        i = 0;
        break;
      }
      int n = (int)paramMotionEvent.getX(m);
      int i1 = (int)paramMotionEvent.getY(m);
      int i2 = pointToPosition(n, i1);
      if (i2 == -1)
      {
        k = 1;
        break;
      }
      View localView = getChildAt(i2 - getFirstVisiblePosition());
      setPressedItem(localView, i2, n, i1);
      i = 1;
      k = 0;
      if (j != 1)
        break;
      clickPressedItem(localView, i2);
      k = 0;
      break;
    }
    while (this.mScrollHelper == null);
    this.mScrollHelper.setEnabled(false);
    return i;
  }

  void setListSelectionHidden(boolean paramBoolean)
  {
    this.mListSelectionHidden = paramBoolean;
  }

  protected boolean touchModeDrawsInPressedStateCompat()
  {
    return (this.mDrawsInPressedState) || (super.touchModeDrawsInPressedStateCompat());
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.DropDownListView
 * JD-Core Version:    0.6.0
 */