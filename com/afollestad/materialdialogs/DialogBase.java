package com.afollestad.materialdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.afollestad.materialdialogs.internal.MDRootLayout;

class DialogBase extends Dialog
  implements DialogInterface.OnShowListener
{
  private DialogInterface.OnShowListener showListener;
  protected MDRootLayout view;

  DialogBase(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
  }

  public View findViewById(int paramInt)
  {
    return this.view.findViewById(paramInt);
  }

  public void onShow(DialogInterface paramDialogInterface)
  {
    if (this.showListener != null)
      this.showListener.onShow(paramDialogInterface);
  }

  @Deprecated
  public void setContentView(int paramInt)
    throws IllegalAccessError
  {
    throw new IllegalAccessError("setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.");
  }

  @Deprecated
  public void setContentView(@NonNull View paramView)
    throws IllegalAccessError
  {
    throw new IllegalAccessError("setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.");
  }

  @Deprecated
  public void setContentView(@NonNull View paramView, ViewGroup.LayoutParams paramLayoutParams)
    throws IllegalAccessError
  {
    throw new IllegalAccessError("setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.");
  }

  public final void setOnShowListener(DialogInterface.OnShowListener paramOnShowListener)
  {
    this.showListener = paramOnShowListener;
  }

  final void setOnShowListenerInternal()
  {
    super.setOnShowListener(this);
  }

  final void setViewInternal(View paramView)
  {
    super.setContentView(paramView);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.DialogBase
 * JD-Core Version:    0.6.0
 */