package com.afollestad.materialdialogs.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.TextView;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.R.dimen;
import com.afollestad.materialdialogs.util.DialogUtils;

@SuppressLint({"AppCompatCustomView"})
public class MDButton extends TextView
{
  private Drawable defaultBackground;
  private boolean stacked = false;
  private Drawable stackedBackground;
  private int stackedEndPadding;
  private GravityEnum stackedGravity;

  public MDButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }

  public MDButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext);
  }

  private void init(Context paramContext)
  {
    this.stackedEndPadding = paramContext.getResources().getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
    this.stackedGravity = GravityEnum.END;
  }

  public void setAllCapsCompat(boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      setAllCaps(paramBoolean);
      return;
    }
    if (paramBoolean)
    {
      setTransformationMethod(new AllCapsTransformationMethod(getContext()));
      return;
    }
    setTransformationMethod(null);
  }

  public void setDefaultSelector(Drawable paramDrawable)
  {
    this.defaultBackground = paramDrawable;
    if (!this.stacked)
      setStacked(false, true);
  }

  void setStacked(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i;
    int j;
    if ((this.stacked != paramBoolean1) || (paramBoolean2))
    {
      if (!paramBoolean1)
        break label105;
      i = 0x10 | this.stackedGravity.getGravityInt();
      setGravity(i);
      if (Build.VERSION.SDK_INT >= 17)
      {
        if (!paramBoolean1)
          break label111;
        j = this.stackedGravity.getTextAlignment();
        label53: setTextAlignment(j);
      }
      if (!paramBoolean1)
        break label117;
    }
    label105: label111: label117: for (Drawable localDrawable = this.stackedBackground; ; localDrawable = this.defaultBackground)
    {
      DialogUtils.setBackgroundCompat(this, localDrawable);
      if (paramBoolean1)
        setPadding(this.stackedEndPadding, getPaddingTop(), this.stackedEndPadding, getPaddingBottom());
      this.stacked = paramBoolean1;
      return;
      i = 17;
      break;
      j = 4;
      break label53;
    }
  }

  public void setStackedGravity(GravityEnum paramGravityEnum)
  {
    this.stackedGravity = paramGravityEnum;
  }

  public void setStackedSelector(Drawable paramDrawable)
  {
    this.stackedBackground = paramDrawable;
    if (this.stacked)
      setStacked(true, true);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.internal.MDButton
 * JD-Core Version:    0.6.0
 */