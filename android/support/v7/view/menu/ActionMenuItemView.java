package android.support.v7.view.menu;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.widget.ActionMenuView.ActionMenuChildView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ForwardingListener;
import android.support.v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class ActionMenuItemView extends AppCompatTextView
  implements MenuView.ItemView, View.OnClickListener, ActionMenuView.ActionMenuChildView
{
  private static final int MAX_ICON_SIZE = 32;
  private static final String TAG = "ActionMenuItemView";
  private boolean mAllowTextWithIcon;
  private boolean mExpandedFormat;
  private ForwardingListener mForwardingListener;
  private Drawable mIcon;
  MenuItemImpl mItemData;
  MenuBuilder.ItemInvoker mItemInvoker;
  private int mMaxIconSize;
  private int mMinWidth;
  PopupCallback mPopupCallback;
  private int mSavedPaddingLeft;
  private CharSequence mTitle;

  public ActionMenuItemView(Context paramContext)
  {
    this(paramContext, null);
  }

  public ActionMenuItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public ActionMenuItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    Resources localResources = paramContext.getResources();
    this.mAllowTextWithIcon = shouldAllowTextWithIcon();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ActionMenuItemView, paramInt, 0);
    this.mMinWidth = localTypedArray.getDimensionPixelSize(R.styleable.ActionMenuItemView_android_minWidth, 0);
    localTypedArray.recycle();
    this.mMaxIconSize = (int)(0.5F + 32.0F * localResources.getDisplayMetrics().density);
    setOnClickListener(this);
    this.mSavedPaddingLeft = -1;
    setSaveEnabled(false);
  }

  private boolean shouldAllowTextWithIcon()
  {
    Configuration localConfiguration = getContext().getResources().getConfiguration();
    int i = localConfiguration.screenWidthDp;
    int j = localConfiguration.screenHeightDp;
    return (i >= 480) || ((i >= 640) && (j >= 480)) || (localConfiguration.orientation == 2);
  }

  private void updateTextButtonVisibility()
  {
    int i;
    label57: CharSequence localCharSequence1;
    label71: CharSequence localCharSequence2;
    CharSequence localCharSequence5;
    label101: label107: CharSequence localCharSequence3;
    CharSequence localCharSequence4;
    if (!TextUtils.isEmpty(this.mTitle))
    {
      i = 1;
      if (this.mIcon != null)
      {
        boolean bool1 = this.mItemData.showsTextAsAction();
        j = 0;
        if (!bool1)
          break label57;
        if (!this.mAllowTextWithIcon)
        {
          boolean bool2 = this.mExpandedFormat;
          j = 0;
          if (!bool2)
            break label57;
        }
      }
      int j = 1;
      int k = i & j;
      if (k == 0)
        break label143;
      localCharSequence1 = this.mTitle;
      setText(localCharSequence1);
      localCharSequence2 = this.mItemData.getContentDescription();
      if (!TextUtils.isEmpty(localCharSequence2))
        break label161;
      if (k == 0)
        break label149;
      localCharSequence5 = null;
      setContentDescription(localCharSequence5);
      localCharSequence3 = this.mItemData.getTooltipText();
      if (!TextUtils.isEmpty(localCharSequence3))
        break label182;
      localCharSequence4 = null;
      if (k == 0)
        break label170;
    }
    while (true)
    {
      TooltipCompat.setTooltipText(this, localCharSequence4);
      return;
      i = 0;
      break;
      label143: localCharSequence1 = null;
      break label71;
      label149: localCharSequence5 = this.mItemData.getTitle();
      break label101;
      label161: setContentDescription(localCharSequence2);
      break label107;
      label170: localCharSequence4 = this.mItemData.getTitle();
    }
    label182: TooltipCompat.setTooltipText(this, localCharSequence3);
  }

  public MenuItemImpl getItemData()
  {
    return this.mItemData;
  }

  public boolean hasText()
  {
    return !TextUtils.isEmpty(getText());
  }

  public void initialize(MenuItemImpl paramMenuItemImpl, int paramInt)
  {
    this.mItemData = paramMenuItemImpl;
    setIcon(paramMenuItemImpl.getIcon());
    setTitle(paramMenuItemImpl.getTitleForItemView(this));
    setId(paramMenuItemImpl.getItemId());
    if (paramMenuItemImpl.isVisible());
    for (int i = 0; ; i = 8)
    {
      setVisibility(i);
      setEnabled(paramMenuItemImpl.isEnabled());
      if ((paramMenuItemImpl.hasSubMenu()) && (this.mForwardingListener == null))
        this.mForwardingListener = new ActionMenuItemForwardingListener();
      return;
    }
  }

  public boolean needsDividerAfter()
  {
    return hasText();
  }

  public boolean needsDividerBefore()
  {
    return (hasText()) && (this.mItemData.getIcon() == null);
  }

  public void onClick(View paramView)
  {
    if (this.mItemInvoker != null)
      this.mItemInvoker.invokeItem(this.mItemData);
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    this.mAllowTextWithIcon = shouldAllowTextWithIcon();
    updateTextButtonVisibility();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    boolean bool = hasText();
    if ((bool) && (this.mSavedPaddingLeft >= 0))
      super.setPadding(this.mSavedPaddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());
    super.onMeasure(paramInt1, paramInt2);
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt1);
    int k = getMeasuredWidth();
    if (i == -2147483648);
    for (int m = Math.min(j, this.mMinWidth); ; m = this.mMinWidth)
    {
      if ((i != 1073741824) && (this.mMinWidth > 0) && (k < m))
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(m, 1073741824), paramInt2);
      if ((!bool) && (this.mIcon != null))
        super.setPadding((getMeasuredWidth() - this.mIcon.getBounds().width()) / 2, getPaddingTop(), getPaddingRight(), getPaddingBottom());
      return;
    }
  }

  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    super.onRestoreInstanceState(null);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mItemData.hasSubMenu()) && (this.mForwardingListener != null) && (this.mForwardingListener.onTouch(this, paramMotionEvent)))
      return true;
    return super.onTouchEvent(paramMotionEvent);
  }

  public boolean prefersCondensedTitle()
  {
    return true;
  }

  public void setCheckable(boolean paramBoolean)
  {
  }

  public void setChecked(boolean paramBoolean)
  {
  }

  public void setExpandedFormat(boolean paramBoolean)
  {
    if (this.mExpandedFormat != paramBoolean)
    {
      this.mExpandedFormat = paramBoolean;
      if (this.mItemData != null)
        this.mItemData.actionFormatChanged();
    }
  }

  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
    if (paramDrawable != null)
    {
      int i = paramDrawable.getIntrinsicWidth();
      int j = paramDrawable.getIntrinsicHeight();
      if (i > this.mMaxIconSize)
      {
        float f2 = this.mMaxIconSize / i;
        i = this.mMaxIconSize;
        j = (int)(f2 * i);
      }
      if (j > this.mMaxIconSize)
      {
        float f1 = this.mMaxIconSize / j;
        j = this.mMaxIconSize;
        i = (int)(f1 * i);
      }
      paramDrawable.setBounds(0, 0, i, j);
    }
    setCompoundDrawables(paramDrawable, null, null, null);
    updateTextButtonVisibility();
  }

  public void setItemInvoker(MenuBuilder.ItemInvoker paramItemInvoker)
  {
    this.mItemInvoker = paramItemInvoker;
  }

  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mSavedPaddingLeft = paramInt1;
    super.setPadding(paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void setPopupCallback(PopupCallback paramPopupCallback)
  {
    this.mPopupCallback = paramPopupCallback;
  }

  public void setShortcut(boolean paramBoolean, char paramChar)
  {
  }

  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    updateTextButtonVisibility();
  }

  public boolean showsIcon()
  {
    return true;
  }

  private class ActionMenuItemForwardingListener extends ForwardingListener
  {
    public ActionMenuItemForwardingListener()
    {
      super();
    }

    public ShowableListMenu getPopup()
    {
      if (ActionMenuItemView.this.mPopupCallback != null)
        return ActionMenuItemView.this.mPopupCallback.getPopup();
      return null;
    }

    protected boolean onForwardingStarted()
    {
      MenuBuilder.ItemInvoker localItemInvoker = ActionMenuItemView.this.mItemInvoker;
      int i = 0;
      if (localItemInvoker != null)
      {
        boolean bool1 = ActionMenuItemView.this.mItemInvoker.invokeItem(ActionMenuItemView.this.mItemData);
        i = 0;
        if (bool1)
        {
          ShowableListMenu localShowableListMenu = getPopup();
          i = 0;
          if (localShowableListMenu != null)
          {
            boolean bool2 = localShowableListMenu.isShowing();
            i = 0;
            if (bool2)
              i = 1;
          }
        }
      }
      return i;
    }
  }

  public static abstract class PopupCallback
  {
    public abstract ShowableListMenu getPopup();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.view.menu.ActionMenuItemView
 * JD-Core Version:    0.6.0
 */