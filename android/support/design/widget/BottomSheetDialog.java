package android.support.design.widget;

import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.R.attr;
import android.support.design.R.id;
import android.support.design.R.layout;
import android.support.design.R.style;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;

public class BottomSheetDialog extends AppCompatDialog
{
  private BottomSheetBehavior<FrameLayout> mBehavior;
  private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback()
  {
    public void onSlide(@NonNull View paramView, float paramFloat)
    {
    }

    public void onStateChanged(@NonNull View paramView, int paramInt)
    {
      if (paramInt == 5)
        BottomSheetDialog.this.cancel();
    }
  };
  boolean mCancelable = true;
  private boolean mCanceledOnTouchOutside = true;
  private boolean mCanceledOnTouchOutsideSet;

  public BottomSheetDialog(@NonNull Context paramContext)
  {
    this(paramContext, 0);
  }

  public BottomSheetDialog(@NonNull Context paramContext, @StyleRes int paramInt)
  {
    super(paramContext, getThemeResId(paramContext, paramInt));
    supportRequestWindowFeature(1);
  }

  protected BottomSheetDialog(@NonNull Context paramContext, boolean paramBoolean, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    super(paramContext, paramBoolean, paramOnCancelListener);
    supportRequestWindowFeature(1);
    this.mCancelable = paramBoolean;
  }

  private static int getThemeResId(Context paramContext, int paramInt)
  {
    if (paramInt == 0)
    {
      TypedValue localTypedValue = new TypedValue();
      if (paramContext.getTheme().resolveAttribute(R.attr.bottomSheetDialogTheme, localTypedValue, true))
        paramInt = localTypedValue.resourceId;
    }
    else
    {
      return paramInt;
    }
    return R.style.Theme_Design_Light_BottomSheetDialog;
  }

  private View wrapInBottomSheet(int paramInt, View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    FrameLayout localFrameLayout1 = (FrameLayout)View.inflate(getContext(), R.layout.design_bottom_sheet_dialog, null);
    CoordinatorLayout localCoordinatorLayout = (CoordinatorLayout)localFrameLayout1.findViewById(R.id.coordinator);
    if ((paramInt != 0) && (paramView == null))
      paramView = getLayoutInflater().inflate(paramInt, localCoordinatorLayout, false);
    FrameLayout localFrameLayout2 = (FrameLayout)localCoordinatorLayout.findViewById(R.id.design_bottom_sheet);
    this.mBehavior = BottomSheetBehavior.from(localFrameLayout2);
    this.mBehavior.setBottomSheetCallback(this.mBottomSheetCallback);
    this.mBehavior.setHideable(this.mCancelable);
    if (paramLayoutParams == null)
      localFrameLayout2.addView(paramView);
    while (true)
    {
      localCoordinatorLayout.findViewById(R.id.touch_outside).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if ((BottomSheetDialog.this.mCancelable) && (BottomSheetDialog.this.isShowing()) && (BottomSheetDialog.this.shouldWindowCloseOnTouchOutside()))
            BottomSheetDialog.this.cancel();
        }
      });
      ViewCompat.setAccessibilityDelegate(localFrameLayout2, new AccessibilityDelegateCompat()
      {
        public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
        {
          super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
          if (BottomSheetDialog.this.mCancelable)
          {
            paramAccessibilityNodeInfoCompat.addAction(1048576);
            paramAccessibilityNodeInfoCompat.setDismissable(true);
            return;
          }
          paramAccessibilityNodeInfoCompat.setDismissable(false);
        }

        public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
        {
          if ((paramInt == 1048576) && (BottomSheetDialog.this.mCancelable))
          {
            BottomSheetDialog.this.cancel();
            return true;
          }
          return super.performAccessibilityAction(paramView, paramInt, paramBundle);
        }
      });
      localFrameLayout2.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          return true;
        }
      });
      return localFrameLayout1;
      localFrameLayout2.addView(paramView, paramLayoutParams);
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Window localWindow = getWindow();
    if (localWindow != null)
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        localWindow.clearFlags(67108864);
        localWindow.addFlags(-2147483648);
      }
      localWindow.setLayout(-1, -1);
    }
  }

  protected void onStart()
  {
    super.onStart();
    if (this.mBehavior != null)
      this.mBehavior.setState(4);
  }

  public void setCancelable(boolean paramBoolean)
  {
    super.setCancelable(paramBoolean);
    if (this.mCancelable != paramBoolean)
    {
      this.mCancelable = paramBoolean;
      if (this.mBehavior != null)
        this.mBehavior.setHideable(paramBoolean);
    }
  }

  public void setCanceledOnTouchOutside(boolean paramBoolean)
  {
    super.setCanceledOnTouchOutside(paramBoolean);
    if ((paramBoolean) && (!this.mCancelable))
      this.mCancelable = true;
    this.mCanceledOnTouchOutside = paramBoolean;
    this.mCanceledOnTouchOutsideSet = true;
  }

  public void setContentView(@LayoutRes int paramInt)
  {
    super.setContentView(wrapInBottomSheet(paramInt, null, null));
  }

  public void setContentView(View paramView)
  {
    super.setContentView(wrapInBottomSheet(0, paramView, null));
  }

  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.setContentView(wrapInBottomSheet(0, paramView, paramLayoutParams));
  }

  boolean shouldWindowCloseOnTouchOutside()
  {
    if (!this.mCanceledOnTouchOutsideSet)
    {
      if (Build.VERSION.SDK_INT >= 11)
        break label30;
      this.mCanceledOnTouchOutside = true;
    }
    while (true)
    {
      this.mCanceledOnTouchOutsideSet = true;
      return this.mCanceledOnTouchOutside;
      label30: TypedArray localTypedArray = getContext().obtainStyledAttributes(new int[] { 16843611 });
      this.mCanceledOnTouchOutside = localTypedArray.getBoolean(0, true);
      localTypedArray.recycle();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.design.widget.BottomSheetDialog
 * JD-Core Version:    0.6.0
 */