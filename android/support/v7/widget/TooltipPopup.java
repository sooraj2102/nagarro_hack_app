package android.support.v7.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R.dimen;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.style;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
class TooltipPopup
{
  private static final String TAG = "TooltipPopup";
  private final View mContentView;
  private final Context mContext;
  private final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
  private final TextView mMessageView;
  private final int[] mTmpAnchorPos = new int[2];
  private final int[] mTmpAppPos = new int[2];
  private final Rect mTmpDisplayFrame = new Rect();

  TooltipPopup(Context paramContext)
  {
    this.mContext = paramContext;
    this.mContentView = LayoutInflater.from(this.mContext).inflate(R.layout.tooltip, null);
    this.mMessageView = ((TextView)this.mContentView.findViewById(R.id.message));
    this.mLayoutParams.setTitle(getClass().getSimpleName());
    this.mLayoutParams.packageName = this.mContext.getPackageName();
    this.mLayoutParams.type = 1002;
    this.mLayoutParams.width = -2;
    this.mLayoutParams.height = -2;
    this.mLayoutParams.format = -3;
    this.mLayoutParams.windowAnimations = R.style.Animation_AppCompat_Tooltip;
    this.mLayoutParams.flags = 24;
  }

  private void computePosition(View paramView, int paramInt1, int paramInt2, boolean paramBoolean, WindowManager.LayoutParams paramLayoutParams)
  {
    int i = this.mContext.getResources().getDimensionPixelOffset(R.dimen.tooltip_precise_anchor_threshold);
    int j;
    int k;
    int m;
    label63: Resources localResources1;
    if (paramView.getWidth() >= i)
    {
      j = paramInt1;
      if (paramView.getHeight() < i)
        break label129;
      int i8 = this.mContext.getResources().getDimensionPixelOffset(R.dimen.tooltip_precise_anchor_extra_offset);
      k = paramInt2 + i8;
      m = paramInt2 - i8;
      paramLayoutParams.gravity = 49;
      localResources1 = this.mContext.getResources();
      if (!paramBoolean)
        break label141;
    }
    int i1;
    View localView;
    label129: label141: for (int n = R.dimen.tooltip_y_offset_touch; ; n = R.dimen.tooltip_y_offset_non_touch)
    {
      i1 = localResources1.getDimensionPixelOffset(n);
      localView = getAppRootView(paramView);
      if (localView != null)
        break label149;
      Log.e("TooltipPopup", "Cannot find app view");
      return;
      j = paramView.getWidth() / 2;
      break;
      k = paramView.getHeight();
      m = 0;
      break label63;
    }
    label149: localView.getWindowVisibleDisplayFrame(this.mTmpDisplayFrame);
    Resources localResources2;
    int i7;
    if ((this.mTmpDisplayFrame.left < 0) && (this.mTmpDisplayFrame.top < 0))
    {
      localResources2 = this.mContext.getResources();
      int i6 = localResources2.getIdentifier("status_bar_height", "dimen", "android");
      if (i6 == 0)
        break label400;
      i7 = localResources2.getDimensionPixelSize(i6);
    }
    int i3;
    int i4;
    int i5;
    while (true)
    {
      DisplayMetrics localDisplayMetrics = localResources2.getDisplayMetrics();
      this.mTmpDisplayFrame.set(0, i7, localDisplayMetrics.widthPixels, localDisplayMetrics.heightPixels);
      localView.getLocationOnScreen(this.mTmpAppPos);
      paramView.getLocationOnScreen(this.mTmpAnchorPos);
      int[] arrayOfInt1 = this.mTmpAnchorPos;
      arrayOfInt1[0] -= this.mTmpAppPos[0];
      int[] arrayOfInt2 = this.mTmpAnchorPos;
      arrayOfInt2[1] -= this.mTmpAppPos[1];
      paramLayoutParams.x = (j + this.mTmpAnchorPos[0] - this.mTmpDisplayFrame.width() / 2);
      int i2 = View.MeasureSpec.makeMeasureSpec(0, 0);
      this.mContentView.measure(i2, i2);
      i3 = this.mContentView.getMeasuredHeight();
      i4 = m + this.mTmpAnchorPos[1] - i1 - i3;
      i5 = i1 + (k + this.mTmpAnchorPos[1]);
      if (!paramBoolean)
        break;
      if (i4 >= 0)
      {
        paramLayoutParams.y = i4;
        return;
        label400: i7 = 0;
        continue;
      }
      paramLayoutParams.y = i5;
      return;
    }
    if (i5 + i3 <= this.mTmpDisplayFrame.height())
    {
      paramLayoutParams.y = i5;
      return;
    }
    paramLayoutParams.y = i4;
  }

  private static View getAppRootView(View paramView)
  {
    for (Context localContext = paramView.getContext(); (localContext instanceof ContextWrapper); localContext = ((ContextWrapper)localContext).getBaseContext())
      if ((localContext instanceof Activity))
        return ((Activity)localContext).getWindow().getDecorView();
    return paramView.getRootView();
  }

  void hide()
  {
    if (!isShowing())
      return;
    ((WindowManager)this.mContext.getSystemService("window")).removeView(this.mContentView);
  }

  boolean isShowing()
  {
    return this.mContentView.getParent() != null;
  }

  void show(View paramView, int paramInt1, int paramInt2, boolean paramBoolean, CharSequence paramCharSequence)
  {
    if (isShowing())
      hide();
    this.mMessageView.setText(paramCharSequence);
    computePosition(paramView, paramInt1, paramInt2, paramBoolean, this.mLayoutParams);
    ((WindowManager)this.mContext.getSystemService("window")).addView(this.mContentView, this.mLayoutParams);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.TooltipPopup
 * JD-Core Version:    0.6.0
 */