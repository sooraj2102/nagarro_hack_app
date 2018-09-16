package com.afollestad.materialdialogs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;
import java.util.ArrayList;
import java.util.List;

class DefaultRvAdapter extends RecyclerView.Adapter<DefaultVH>
{
  private InternalListCallback callback;
  private final MaterialDialog dialog;
  private final GravityEnum itemGravity;

  @LayoutRes
  private final int layout;

  DefaultRvAdapter(MaterialDialog paramMaterialDialog, @LayoutRes int paramInt)
  {
    this.dialog = paramMaterialDialog;
    this.layout = paramInt;
    this.itemGravity = paramMaterialDialog.builder.itemsGravity;
  }

  @TargetApi(17)
  private boolean isRTL()
  {
    int i = 1;
    if (Build.VERSION.SDK_INT < 17)
      return false;
    if (this.dialog.getBuilder().getContext().getResources().getConfiguration().getLayoutDirection() == i);
    while (true)
    {
      return i;
      i = 0;
    }
  }

  @TargetApi(17)
  private void setupGravity(ViewGroup paramViewGroup)
  {
    ((LinearLayout)paramViewGroup).setGravity(0x10 | this.itemGravity.getGravityInt());
    if (paramViewGroup.getChildCount() == 2)
    {
      if ((this.itemGravity != GravityEnum.END) || (isRTL()) || (!(paramViewGroup.getChildAt(0) instanceof CompoundButton)))
        break label123;
      CompoundButton localCompoundButton2 = (CompoundButton)paramViewGroup.getChildAt(0);
      paramViewGroup.removeView(localCompoundButton2);
      TextView localTextView2 = (TextView)paramViewGroup.getChildAt(0);
      paramViewGroup.removeView(localTextView2);
      localTextView2.setPadding(localTextView2.getPaddingRight(), localTextView2.getPaddingTop(), localTextView2.getPaddingLeft(), localTextView2.getPaddingBottom());
      paramViewGroup.addView(localTextView2);
      paramViewGroup.addView(localCompoundButton2);
    }
    label123: 
    do
      return;
    while ((this.itemGravity != GravityEnum.START) || (!isRTL()) || (!(paramViewGroup.getChildAt(1) instanceof CompoundButton)));
    CompoundButton localCompoundButton1 = (CompoundButton)paramViewGroup.getChildAt(1);
    paramViewGroup.removeView(localCompoundButton1);
    TextView localTextView1 = (TextView)paramViewGroup.getChildAt(0);
    paramViewGroup.removeView(localTextView1);
    localTextView1.setPadding(localTextView1.getPaddingRight(), localTextView1.getPaddingTop(), localTextView1.getPaddingRight(), localTextView1.getPaddingBottom());
    paramViewGroup.addView(localCompoundButton1);
    paramViewGroup.addView(localTextView1);
  }

  public int getItemCount()
  {
    if (this.dialog.builder.items != null)
      return this.dialog.builder.items.size();
    return 0;
  }

  public void onBindViewHolder(DefaultVH paramDefaultVH, int paramInt)
  {
    View localView1 = paramDefaultVH.itemView;
    boolean bool1 = DialogUtils.isIn(Integer.valueOf(paramInt), this.dialog.builder.disabledIndices);
    int i;
    boolean bool2;
    label60: label210: ViewGroup localViewGroup;
    if (bool1)
    {
      i = DialogUtils.adjustAlpha(this.dialog.builder.itemColor, 0.4F);
      View localView2 = paramDefaultVH.itemView;
      if (bool1)
        break label271;
      bool2 = true;
      localView2.setEnabled(bool2);
      switch (1.$SwitchMap$com$afollestad$materialdialogs$MaterialDialog$ListType[this.dialog.listType.ordinal()])
      {
      default:
        paramDefaultVH.title.setText((CharSequence)this.dialog.builder.items.get(paramInt));
        paramDefaultVH.title.setTextColor(i);
        this.dialog.setTypeface(paramDefaultVH.title, this.dialog.builder.regularFont);
        setupGravity((ViewGroup)localView1);
        if (this.dialog.builder.itemIds != null)
        {
          if (paramInt < this.dialog.builder.itemIds.length)
            localView1.setId(this.dialog.builder.itemIds[paramInt]);
        }
        else
        {
          if (Build.VERSION.SDK_INT < 21)
            break;
          localViewGroup = (ViewGroup)localView1;
          if (localViewGroup.getChildCount() != 2)
            break;
          if (!(localViewGroup.getChildAt(0) instanceof CompoundButton))
            break label498;
          localViewGroup.getChildAt(0).setBackground(null);
        }
      case 1:
      case 2:
      }
    }
    label271: label303: label331: label362: label380: 
    do
    {
      return;
      i = this.dialog.builder.itemColor;
      break;
      bool2 = false;
      break label60;
      RadioButton localRadioButton = (RadioButton)paramDefaultVH.control;
      boolean bool5;
      if (this.dialog.builder.selectedIndex == paramInt)
      {
        bool5 = true;
        if (this.dialog.builder.choiceWidgetColor == null)
          break label362;
        MDTintHelper.setTint(localRadioButton, this.dialog.builder.choiceWidgetColor);
        localRadioButton.setChecked(bool5);
        if (bool1)
          break label380;
      }
      for (boolean bool6 = true; ; bool6 = false)
      {
        localRadioButton.setEnabled(bool6);
        break;
        bool5 = false;
        break label303;
        MDTintHelper.setTint(localRadioButton, this.dialog.builder.widgetColor);
        break label331;
      }
      CheckBox localCheckBox = (CheckBox)paramDefaultVH.control;
      boolean bool3 = this.dialog.selectedIndicesList.contains(Integer.valueOf(paramInt));
      if (this.dialog.builder.choiceWidgetColor != null)
      {
        MDTintHelper.setTint(localCheckBox, this.dialog.builder.choiceWidgetColor);
        localCheckBox.setChecked(bool3);
        if (bool1)
          break label484;
      }
      for (boolean bool4 = true; ; bool4 = false)
      {
        localCheckBox.setEnabled(bool4);
        break;
        MDTintHelper.setTint(localCheckBox, this.dialog.builder.widgetColor);
        break label441;
      }
      localView1.setId(-1);
      break label210;
    }
    while (!(localViewGroup.getChildAt(1) instanceof CompoundButton));
    label441: label484: label498: localViewGroup.getChildAt(1).setBackground(null);
  }

  public DefaultVH onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(this.layout, paramViewGroup, false);
    DialogUtils.setBackgroundCompat(localView, this.dialog.getListSelector());
    return new DefaultVH(localView, this);
  }

  void setCallback(InternalListCallback paramInternalListCallback)
  {
    this.callback = paramInternalListCallback;
  }

  static class DefaultVH extends RecyclerView.ViewHolder
    implements View.OnClickListener, View.OnLongClickListener
  {
    final DefaultRvAdapter adapter;
    final CompoundButton control;
    final TextView title;

    DefaultVH(View paramView, DefaultRvAdapter paramDefaultRvAdapter)
    {
      super();
      this.control = ((CompoundButton)paramView.findViewById(R.id.md_control));
      this.title = ((TextView)paramView.findViewById(R.id.md_title));
      this.adapter = paramDefaultRvAdapter;
      paramView.setOnClickListener(this);
      if (paramDefaultRvAdapter.dialog.builder.listLongCallback != null)
        paramView.setOnLongClickListener(this);
    }

    public void onClick(View paramView)
    {
      if ((this.adapter.callback != null) && (getAdapterPosition() != -1))
      {
        ArrayList localArrayList = this.adapter.dialog.builder.items;
        CharSequence localCharSequence = null;
        if (localArrayList != null)
        {
          int i = getAdapterPosition();
          int j = this.adapter.dialog.builder.items.size();
          localCharSequence = null;
          if (i < j)
            localCharSequence = (CharSequence)this.adapter.dialog.builder.items.get(getAdapterPosition());
        }
        this.adapter.callback.onItemSelected(this.adapter.dialog, paramView, getAdapterPosition(), localCharSequence, false);
      }
    }

    public boolean onLongClick(View paramView)
    {
      if ((this.adapter.callback != null) && (getAdapterPosition() != -1))
      {
        ArrayList localArrayList = this.adapter.dialog.builder.items;
        CharSequence localCharSequence = null;
        if (localArrayList != null)
        {
          int i = getAdapterPosition();
          int j = this.adapter.dialog.builder.items.size();
          localCharSequence = null;
          if (i < j)
            localCharSequence = (CharSequence)this.adapter.dialog.builder.items.get(getAdapterPosition());
        }
        return this.adapter.callback.onItemSelected(this.adapter.dialog, paramView, getAdapterPosition(), localCharSequence, true);
      }
      return false;
    }
  }

  static abstract interface InternalListCallback
  {
    public abstract boolean onItemSelected(MaterialDialog paramMaterialDialog, View paramView, int paramInt, CharSequence paramCharSequence, boolean paramBoolean);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.DefaultRvAdapter
 * JD-Core Version:    0.6.0
 */