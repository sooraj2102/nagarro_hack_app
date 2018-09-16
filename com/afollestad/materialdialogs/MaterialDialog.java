package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager.BadTokenException;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.internal.MDButton;
import com.afollestad.materialdialogs.internal.MDRootLayout;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.afollestad.materialdialogs.util.RippleHelper;
import com.afollestad.materialdialogs.util.TypefaceHelper;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MaterialDialog extends DialogBase
  implements View.OnClickListener, DefaultRvAdapter.InternalListCallback
{
  protected final Builder builder;
  CheckBox checkBoxPrompt;
  protected TextView content;
  FrameLayout customViewFrame;
  private final Handler handler = new Handler();
  protected ImageView icon;
  EditText input;
  TextView inputMinMax;
  ListType listType;
  MDButton negativeButton;
  MDButton neutralButton;
  MDButton positiveButton;
  ProgressBar progressBar;
  TextView progressLabel;
  TextView progressMinMax;
  RecyclerView recyclerView;
  List<Integer> selectedIndicesList;
  protected TextView title;
  View titleFrame;

  @SuppressLint({"InflateParams"})
  protected MaterialDialog(Builder paramBuilder)
  {
    super(paramBuilder.context, DialogInit.getTheme(paramBuilder));
    this.builder = paramBuilder;
    this.view = ((MDRootLayout)LayoutInflater.from(paramBuilder.context).inflate(DialogInit.getInflateLayout(paramBuilder), null));
    DialogInit.init(this);
  }

  private boolean sendMultiChoiceCallback()
  {
    if (this.builder.listCallbackMultiChoice == null)
      return false;
    Collections.sort(this.selectedIndicesList);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.selectedIndicesList.iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      if ((localInteger.intValue() < 0) || (localInteger.intValue() > -1 + this.builder.items.size()))
        continue;
      localArrayList.add(this.builder.items.get(localInteger.intValue()));
    }
    return this.builder.listCallbackMultiChoice.onSelection(this, (Integer[])this.selectedIndicesList.toArray(new Integer[this.selectedIndicesList.size()]), (CharSequence[])localArrayList.toArray(new CharSequence[localArrayList.size()]));
  }

  private boolean sendSingleChoiceCallback(View paramView)
  {
    if (this.builder.listCallbackSingleChoice == null)
      return false;
    int i = this.builder.selectedIndex;
    CharSequence localCharSequence = null;
    if (i >= 0)
    {
      int j = this.builder.selectedIndex;
      int k = this.builder.items.size();
      localCharSequence = null;
      if (j < k)
        localCharSequence = (CharSequence)this.builder.items.get(this.builder.selectedIndex);
    }
    return this.builder.listCallbackSingleChoice.onSelection(this, paramView, this.builder.selectedIndex, localCharSequence);
  }

  final void checkIfListInitScroll()
  {
    if (this.recyclerView == null)
      return;
    this.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
    {
      public void onGlobalLayout()
      {
        if (Build.VERSION.SDK_INT < 16)
          MaterialDialog.this.recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        while (true)
        {
          if ((MaterialDialog.this.listType == MaterialDialog.ListType.SINGLE) || (MaterialDialog.this.listType == MaterialDialog.ListType.MULTI))
          {
            if (MaterialDialog.this.listType != MaterialDialog.ListType.SINGLE)
              break label126;
            if (MaterialDialog.this.builder.selectedIndex >= 0)
              break;
          }
          return;
          MaterialDialog.this.recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        for (int i = MaterialDialog.this.builder.selectedIndex; ; i = ((Integer)MaterialDialog.this.selectedIndicesList.get(0)).intValue())
        {
          int j = i;
          MaterialDialog.this.recyclerView.post(new Runnable(j)
          {
            public void run()
            {
              MaterialDialog.this.recyclerView.requestFocus();
              MaterialDialog.this.builder.layoutManager.scrollToPosition(this.val$fSelectedIndex);
            }
          });
          return;
          label126: if ((MaterialDialog.this.selectedIndicesList == null) || (MaterialDialog.this.selectedIndicesList.size() == 0))
            break;
          Collections.sort(MaterialDialog.this.selectedIndicesList);
        }
      }
    });
  }

  public void clearSelectedIndices()
  {
    clearSelectedIndices(true);
  }

  public void clearSelectedIndices(boolean paramBoolean)
  {
    if ((this.listType == null) || (this.listType != ListType.MULTI))
      throw new IllegalStateException("You can only use clearSelectedIndices() with multi choice list dialogs.");
    if ((this.builder.adapter != null) && ((this.builder.adapter instanceof DefaultRvAdapter)))
    {
      if (this.selectedIndicesList != null)
        this.selectedIndicesList.clear();
      this.builder.adapter.notifyDataSetChanged();
      if ((paramBoolean) && (this.builder.listCallbackMultiChoice != null))
        sendMultiChoiceCallback();
      return;
    }
    throw new IllegalStateException("You can only use clearSelectedIndices() with the default adapter implementation.");
  }

  public void dismiss()
  {
    if (this.input != null)
      DialogUtils.hideKeyboard(this, this.builder);
    super.dismiss();
  }

  public final MDButton getActionButton(@NonNull DialogAction paramDialogAction)
  {
    switch (4.$SwitchMap$com$afollestad$materialdialogs$DialogAction[paramDialogAction.ordinal()])
    {
    default:
      return this.positiveButton;
    case 1:
      return this.neutralButton;
    case 2:
    }
    return this.negativeButton;
  }

  public final Builder getBuilder()
  {
    return this.builder;
  }

  Drawable getButtonSelector(DialogAction paramDialogAction, boolean paramBoolean)
  {
    Drawable localDrawable;
    if (paramBoolean)
      if (this.builder.btnSelectorStacked != 0)
        localDrawable = ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.btnSelectorStacked, null);
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  return localDrawable;
                  localDrawable = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_btn_stacked_selector);
                }
                while (localDrawable != null);
                return DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_stacked_selector);
                switch (4.$SwitchMap$com$afollestad$materialdialogs$DialogAction[paramDialogAction.ordinal()])
                {
                default:
                  if (this.builder.btnSelectorPositive != 0)
                    return ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.btnSelectorPositive, null);
                  localDrawable = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_btn_positive_selector);
                case 1:
                case 2:
                }
              }
              while (localDrawable != null);
              localDrawable = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_positive_selector);
            }
            while (Build.VERSION.SDK_INT < 21);
            RippleHelper.applyColor(localDrawable, this.builder.buttonRippleColor);
            return localDrawable;
            if (this.builder.btnSelectorNeutral != 0)
              return ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.btnSelectorNeutral, null);
            localDrawable = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_btn_neutral_selector);
          }
          while (localDrawable != null);
          localDrawable = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_neutral_selector);
        }
        while (Build.VERSION.SDK_INT < 21);
        RippleHelper.applyColor(localDrawable, this.builder.buttonRippleColor);
        return localDrawable;
        if (this.builder.btnSelectorNegative != 0)
          return ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.btnSelectorNegative, null);
        localDrawable = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_btn_negative_selector);
      }
      while (localDrawable != null);
      localDrawable = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_negative_selector);
    }
    while (Build.VERSION.SDK_INT < 21);
    RippleHelper.applyColor(localDrawable, this.builder.buttonRippleColor);
    return localDrawable;
  }

  @Nullable
  public final TextView getContentView()
  {
    return this.content;
  }

  public final int getCurrentProgress()
  {
    if (this.progressBar == null)
      return -1;
    return this.progressBar.getProgress();
  }

  @Nullable
  public final View getCustomView()
  {
    return this.builder.customView;
  }

  public ImageView getIconView()
  {
    return this.icon;
  }

  @Nullable
  public final EditText getInputEditText()
  {
    return this.input;
  }

  @Nullable
  public final ArrayList<CharSequence> getItems()
  {
    return this.builder.items;
  }

  final Drawable getListSelector()
  {
    Drawable localDrawable;
    if (this.builder.listSelector != 0)
      localDrawable = ResourcesCompat.getDrawable(this.builder.context.getResources(), this.builder.listSelector, null);
    do
    {
      return localDrawable;
      localDrawable = DialogUtils.resolveDrawable(this.builder.context, R.attr.md_list_selector);
    }
    while (localDrawable != null);
    return DialogUtils.resolveDrawable(getContext(), R.attr.md_list_selector);
  }

  public final int getMaxProgress()
  {
    if (this.progressBar == null)
      return -1;
    return this.progressBar.getMax();
  }

  public ProgressBar getProgressBar()
  {
    return this.progressBar;
  }

  public RecyclerView getRecyclerView()
  {
    return this.recyclerView;
  }

  public int getSelectedIndex()
  {
    if (this.builder.listCallbackSingleChoice != null)
      return this.builder.selectedIndex;
    return -1;
  }

  @Nullable
  public Integer[] getSelectedIndices()
  {
    if (this.builder.listCallbackMultiChoice != null)
      return (Integer[])this.selectedIndicesList.toArray(new Integer[this.selectedIndicesList.size()]);
    return null;
  }

  @Nullable
  public Object getTag()
  {
    return this.builder.tag;
  }

  public final TextView getTitleView()
  {
    return this.title;
  }

  public final View getView()
  {
    return this.view;
  }

  public final boolean hasActionButtons()
  {
    return numberOfActionButtons() > 0;
  }

  public final void incrementProgress(int paramInt)
  {
    setProgress(paramInt + getCurrentProgress());
  }

  void invalidateInputMinMaxIndicator(int paramInt, boolean paramBoolean)
  {
    boolean bool1 = true;
    boolean bool2;
    label124: int i;
    label138: int j;
    label152: MDButton localMDButton;
    if (this.inputMinMax != null)
    {
      if (this.builder.inputMaxLength <= 0)
        break label201;
      TextView localTextView = this.inputMinMax;
      Locale localLocale = Locale.getDefault();
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Integer.valueOf(paramInt);
      arrayOfObject[bool1] = Integer.valueOf(this.builder.inputMaxLength);
      localTextView.setText(String.format(localLocale, "%d/%d", arrayOfObject));
      this.inputMinMax.setVisibility(0);
      if (((!paramBoolean) || (paramInt != 0)) && ((this.builder.inputMaxLength <= 0) || (paramInt <= this.builder.inputMaxLength)) && (paramInt >= this.builder.inputMinLength))
        break label213;
      bool2 = bool1;
      if (!bool2)
        break label219;
      i = this.builder.inputRangeErrorColor;
      if (!bool2)
        break label231;
      j = this.builder.inputRangeErrorColor;
      if (this.builder.inputMaxLength > 0)
        this.inputMinMax.setTextColor(i);
      MDTintHelper.setTint(this.input, j);
      localMDButton = getActionButton(DialogAction.POSITIVE);
      if (bool2)
        break label243;
    }
    while (true)
    {
      localMDButton.setEnabled(bool1);
      return;
      label201: this.inputMinMax.setVisibility(8);
      break;
      label213: bool2 = false;
      break label124;
      label219: i = this.builder.contentColor;
      break label138;
      label231: j = this.builder.widgetColor;
      break label152;
      label243: bool1 = false;
    }
  }

  final void invalidateList()
  {
    if (this.recyclerView == null);
    do
    {
      do
        return;
      while (((this.builder.items == null) || (this.builder.items.size() == 0)) && (this.builder.adapter == null));
      if (this.builder.layoutManager == null)
        this.builder.layoutManager = new LinearLayoutManager(getContext());
      if (this.recyclerView.getLayoutManager() == null)
        this.recyclerView.setLayoutManager(this.builder.layoutManager);
      this.recyclerView.setAdapter(this.builder.adapter);
    }
    while (this.listType == null);
    ((DefaultRvAdapter)this.builder.adapter).setCallback(this);
  }

  public final boolean isCancelled()
  {
    return !isShowing();
  }

  public final boolean isIndeterminateProgress()
  {
    return this.builder.indeterminateProgress;
  }

  public boolean isPromptCheckBoxChecked()
  {
    return (this.checkBoxPrompt != null) && (this.checkBoxPrompt.isChecked());
  }

  @UiThread
  public final void notifyItemChanged(@IntRange(from=0L, to=2147483647L) int paramInt)
  {
    this.builder.adapter.notifyItemChanged(paramInt);
  }

  @UiThread
  public final void notifyItemInserted(@IntRange(from=0L, to=2147483647L) int paramInt)
  {
    this.builder.adapter.notifyItemInserted(paramInt);
  }

  @UiThread
  public final void notifyItemsChanged()
  {
    this.builder.adapter.notifyDataSetChanged();
  }

  public final int numberOfActionButtons()
  {
    CharSequence localCharSequence = this.builder.positiveText;
    int i = 0;
    if (localCharSequence != null)
    {
      int j = this.positiveButton.getVisibility();
      i = 0;
      if (j == 0)
        i = 0 + 1;
    }
    if ((this.builder.neutralText != null) && (this.neutralButton.getVisibility() == 0))
      i++;
    if ((this.builder.negativeText != null) && (this.negativeButton.getVisibility() == 0))
      i++;
    return i;
  }

  public final void onClick(View paramView)
  {
    DialogAction localDialogAction = (DialogAction)paramView.getTag();
    switch (4.$SwitchMap$com$afollestad$materialdialogs$DialogAction[localDialogAction.ordinal()])
    {
    default:
    case 3:
    case 2:
    case 1:
    }
    while (true)
    {
      if (this.builder.onAnyCallback != null)
        this.builder.onAnyCallback.onClick(this, localDialogAction);
      return;
      if (this.builder.callback != null)
      {
        this.builder.callback.onAny(this);
        this.builder.callback.onPositive(this);
      }
      if (this.builder.onPositiveCallback != null)
        this.builder.onPositiveCallback.onClick(this, localDialogAction);
      if (!this.builder.alwaysCallSingleChoiceCallback)
        sendSingleChoiceCallback(paramView);
      if (!this.builder.alwaysCallMultiChoiceCallback)
        sendMultiChoiceCallback();
      if ((this.builder.inputCallback != null) && (this.input != null) && (!this.builder.alwaysCallInputCallback))
        this.builder.inputCallback.onInput(this, this.input.getText());
      if (!this.builder.autoDismiss)
        continue;
      dismiss();
      continue;
      if (this.builder.callback != null)
      {
        this.builder.callback.onAny(this);
        this.builder.callback.onNegative(this);
      }
      if (this.builder.onNegativeCallback != null)
        this.builder.onNegativeCallback.onClick(this, localDialogAction);
      if (!this.builder.autoDismiss)
        continue;
      cancel();
      continue;
      if (this.builder.callback != null)
      {
        this.builder.callback.onAny(this);
        this.builder.callback.onNeutral(this);
      }
      if (this.builder.onNeutralCallback != null)
        this.builder.onNeutralCallback.onClick(this, localDialogAction);
      if (!this.builder.autoDismiss)
        continue;
      dismiss();
    }
  }

  public boolean onItemSelected(MaterialDialog paramMaterialDialog, View paramView, int paramInt, CharSequence paramCharSequence, boolean paramBoolean)
  {
    if (!paramView.isEnabled());
    CheckBox localCheckBox;
    int j;
    while (true)
    {
      return false;
      if ((this.listType == null) || (this.listType == ListType.REGULAR))
      {
        if (this.builder.autoDismiss)
          dismiss();
        if ((!paramBoolean) && (this.builder.listCallback != null))
          this.builder.listCallback.onSelection(this, paramView, paramInt, (CharSequence)this.builder.items.get(paramInt));
        if ((!paramBoolean) || (this.builder.listLongCallback == null))
          break;
        return this.builder.listLongCallback.onLongSelection(this, paramView, paramInt, (CharSequence)this.builder.items.get(paramInt));
      }
      else
      {
        if (this.listType != ListType.MULTI)
          break label320;
        localCheckBox = (CheckBox)paramView.findViewById(R.id.md_control);
        if (!localCheckBox.isEnabled())
          continue;
        if (this.selectedIndicesList.contains(Integer.valueOf(paramInt)))
          break label222;
        j = 1;
        label178: if (j == 0)
          break label254;
        this.selectedIndicesList.add(Integer.valueOf(paramInt));
        if (!this.builder.alwaysCallMultiChoiceCallback)
          break label245;
        if (!sendMultiChoiceCallback())
          break label228;
        localCheckBox.setChecked(true);
      }
    }
    label222: label483: 
    while (true)
    {
      return true;
      j = 0;
      break label178;
      label228: this.selectedIndicesList.remove(Integer.valueOf(paramInt));
      continue;
      label245: localCheckBox.setChecked(true);
      continue;
      label254: this.selectedIndicesList.remove(Integer.valueOf(paramInt));
      if (this.builder.alwaysCallMultiChoiceCallback)
      {
        if (sendMultiChoiceCallback())
        {
          localCheckBox.setChecked(false);
          continue;
        }
        this.selectedIndicesList.add(Integer.valueOf(paramInt));
        continue;
      }
      localCheckBox.setChecked(false);
      continue;
      if (this.listType != ListType.SINGLE)
        continue;
      RadioButton localRadioButton = (RadioButton)paramView.findViewById(R.id.md_control);
      if (!localRadioButton.isEnabled())
        break;
      boolean bool = true;
      int i = this.builder.selectedIndex;
      if ((this.builder.autoDismiss) && (this.builder.positiveText == null))
      {
        dismiss();
        bool = false;
        this.builder.selectedIndex = paramInt;
        sendSingleChoiceCallback(paramView);
      }
      while (true)
      {
        if (!bool)
          break label483;
        this.builder.selectedIndex = paramInt;
        localRadioButton.setChecked(true);
        this.builder.adapter.notifyItemChanged(i);
        this.builder.adapter.notifyItemChanged(paramInt);
        break;
        if (!this.builder.alwaysCallSingleChoiceCallback)
          continue;
        this.builder.selectedIndex = paramInt;
        bool = sendSingleChoiceCallback(paramView);
        this.builder.selectedIndex = i;
      }
    }
  }

  public final void onShow(DialogInterface paramDialogInterface)
  {
    if (this.input != null)
    {
      DialogUtils.showKeyboard(this, this.builder);
      if (this.input.getText().length() > 0)
        this.input.setSelection(this.input.getText().length());
    }
    super.onShow(paramDialogInterface);
  }

  public void selectAllIndices()
  {
    selectAllIndices(true);
  }

  public void selectAllIndices(boolean paramBoolean)
  {
    if ((this.listType == null) || (this.listType != ListType.MULTI))
      throw new IllegalStateException("You can only use selectAllIndices() with multi choice list dialogs.");
    if ((this.builder.adapter != null) && ((this.builder.adapter instanceof DefaultRvAdapter)))
    {
      if (this.selectedIndicesList == null)
        this.selectedIndicesList = new ArrayList();
      for (int i = 0; i < this.builder.adapter.getItemCount(); i++)
      {
        if (this.selectedIndicesList.contains(Integer.valueOf(i)))
          continue;
        this.selectedIndicesList.add(Integer.valueOf(i));
      }
      this.builder.adapter.notifyDataSetChanged();
      if ((paramBoolean) && (this.builder.listCallbackMultiChoice != null))
        sendMultiChoiceCallback();
      return;
    }
    throw new IllegalStateException("You can only use selectAllIndices() with the default adapter implementation.");
  }

  public final void setActionButton(DialogAction paramDialogAction, @StringRes int paramInt)
  {
    setActionButton(paramDialogAction, getContext().getText(paramInt));
  }

  @UiThread
  public final void setActionButton(@NonNull DialogAction paramDialogAction, CharSequence paramCharSequence)
  {
    int i = 8;
    switch (4.$SwitchMap$com$afollestad$materialdialogs$DialogAction[paramDialogAction.ordinal()])
    {
    default:
      this.builder.positiveText = paramCharSequence;
      this.positiveButton.setText(paramCharSequence);
      MDButton localMDButton3 = this.positiveButton;
      if (paramCharSequence == null);
      while (true)
      {
        localMDButton3.setVisibility(i);
        return;
        i = 0;
      }
    case 1:
      this.builder.neutralText = paramCharSequence;
      this.neutralButton.setText(paramCharSequence);
      MDButton localMDButton2 = this.neutralButton;
      if (paramCharSequence == null);
      while (true)
      {
        localMDButton2.setVisibility(i);
        return;
        i = 0;
      }
    case 2:
    }
    this.builder.negativeText = paramCharSequence;
    this.negativeButton.setText(paramCharSequence);
    MDButton localMDButton1 = this.negativeButton;
    if (paramCharSequence == null);
    while (true)
    {
      localMDButton1.setVisibility(i);
      return;
      i = 0;
    }
  }

  @UiThread
  public final void setContent(@StringRes int paramInt)
  {
    setContent(this.builder.context.getString(paramInt));
  }

  @UiThread
  public final void setContent(@StringRes int paramInt, @Nullable Object[] paramArrayOfObject)
  {
    setContent(this.builder.context.getString(paramInt, paramArrayOfObject));
  }

  @UiThread
  public final void setContent(CharSequence paramCharSequence)
  {
    this.content.setText(paramCharSequence);
    TextView localTextView = this.content;
    if (TextUtils.isEmpty(paramCharSequence));
    for (int i = 8; ; i = 0)
    {
      localTextView.setVisibility(i);
      return;
    }
  }

  @UiThread
  public void setIcon(@DrawableRes int paramInt)
  {
    this.icon.setImageResource(paramInt);
    ImageView localImageView = this.icon;
    if (paramInt != 0);
    for (int i = 0; ; i = 8)
    {
      localImageView.setVisibility(i);
      return;
    }
  }

  @UiThread
  public void setIcon(Drawable paramDrawable)
  {
    this.icon.setImageDrawable(paramDrawable);
    ImageView localImageView = this.icon;
    if (paramDrawable != null);
    for (int i = 0; ; i = 8)
    {
      localImageView.setVisibility(i);
      return;
    }
  }

  @UiThread
  public void setIconAttribute(@AttrRes int paramInt)
  {
    setIcon(DialogUtils.resolveDrawable(this.builder.context, paramInt));
  }

  void setInternalInputCallback()
  {
    if (this.input == null)
      return;
    this.input.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramEditable)
      {
      }

      public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
      {
      }

      public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
      {
        boolean bool1 = true;
        int i = paramCharSequence.toString().length();
        boolean bool2 = MaterialDialog.this.builder.inputAllowEmpty;
        boolean bool3 = false;
        MDButton localMDButton;
        if (!bool2)
        {
          if (i != 0)
            break label112;
          bool3 = bool1;
          localMDButton = MaterialDialog.this.getActionButton(DialogAction.POSITIVE);
          if (bool3)
            break label118;
        }
        while (true)
        {
          localMDButton.setEnabled(bool1);
          MaterialDialog.this.invalidateInputMinMaxIndicator(i, bool3);
          if (MaterialDialog.this.builder.alwaysCallInputCallback)
            MaterialDialog.this.builder.inputCallback.onInput(MaterialDialog.this, paramCharSequence);
          return;
          label112: bool3 = false;
          break;
          label118: bool1 = false;
        }
      }
    });
  }

  @UiThread
  public final void setItems(CharSequence[] paramArrayOfCharSequence)
  {
    if (this.builder.adapter == null)
      throw new IllegalStateException("This MaterialDialog instance does not yet have an adapter set to it. You cannot use setItems().");
    if (paramArrayOfCharSequence != null)
    {
      this.builder.items = new ArrayList(paramArrayOfCharSequence.length);
      Collections.addAll(this.builder.items, paramArrayOfCharSequence);
    }
    while (!(this.builder.adapter instanceof DefaultRvAdapter))
    {
      throw new IllegalStateException("When using a custom adapter, setItems() cannot be used. Set items through the adapter instead.");
      this.builder.items = null;
    }
    notifyItemsChanged();
  }

  public final void setMaxProgress(int paramInt)
  {
    if (this.builder.progress <= -2)
      throw new IllegalStateException("Cannot use setMaxProgress() on this dialog.");
    this.progressBar.setMax(paramInt);
  }

  public final void setProgress(int paramInt)
  {
    if (this.builder.progress <= -2)
    {
      Log.w("MaterialDialog", "Calling setProgress(int) on an indeterminate progress dialog has no effect!");
      return;
    }
    this.progressBar.setProgress(paramInt);
    this.handler.post(new Runnable()
    {
      public void run()
      {
        if (MaterialDialog.this.progressLabel != null)
          MaterialDialog.this.progressLabel.setText(MaterialDialog.this.builder.progressPercentFormat.format(MaterialDialog.this.getCurrentProgress() / MaterialDialog.this.getMaxProgress()));
        if (MaterialDialog.this.progressMinMax != null)
        {
          TextView localTextView = MaterialDialog.this.progressMinMax;
          String str = MaterialDialog.this.builder.progressNumberFormat;
          Object[] arrayOfObject = new Object[2];
          arrayOfObject[0] = Integer.valueOf(MaterialDialog.this.getCurrentProgress());
          arrayOfObject[1] = Integer.valueOf(MaterialDialog.this.getMaxProgress());
          localTextView.setText(String.format(str, arrayOfObject));
        }
      }
    });
  }

  public final void setProgressNumberFormat(String paramString)
  {
    this.builder.progressNumberFormat = paramString;
    setProgress(getCurrentProgress());
  }

  public final void setProgressPercentFormat(NumberFormat paramNumberFormat)
  {
    this.builder.progressPercentFormat = paramNumberFormat;
    setProgress(getCurrentProgress());
  }

  public void setPromptCheckBoxChecked(boolean paramBoolean)
  {
    if (this.checkBoxPrompt != null)
      this.checkBoxPrompt.setChecked(paramBoolean);
  }

  @UiThread
  public void setSelectedIndex(int paramInt)
  {
    this.builder.selectedIndex = paramInt;
    if ((this.builder.adapter != null) && ((this.builder.adapter instanceof DefaultRvAdapter)))
    {
      this.builder.adapter.notifyDataSetChanged();
      return;
    }
    throw new IllegalStateException("You can only use setSelectedIndex() with the default adapter implementation.");
  }

  @UiThread
  public void setSelectedIndices(@NonNull Integer[] paramArrayOfInteger)
  {
    this.selectedIndicesList = new ArrayList(Arrays.asList(paramArrayOfInteger));
    if ((this.builder.adapter != null) && ((this.builder.adapter instanceof DefaultRvAdapter)))
    {
      this.builder.adapter.notifyDataSetChanged();
      return;
    }
    throw new IllegalStateException("You can only use setSelectedIndices() with the default adapter implementation.");
  }

  @UiThread
  public final void setTitle(@StringRes int paramInt)
  {
    setTitle(this.builder.context.getString(paramInt));
  }

  @UiThread
  public final void setTitle(@StringRes int paramInt, @Nullable Object[] paramArrayOfObject)
  {
    setTitle(this.builder.context.getString(paramInt, paramArrayOfObject));
  }

  @UiThread
  public final void setTitle(CharSequence paramCharSequence)
  {
    this.title.setText(paramCharSequence);
  }

  public final void setTypeface(TextView paramTextView, Typeface paramTypeface)
  {
    if (paramTypeface == null)
      return;
    paramTextView.setPaintFlags(0x80 | paramTextView.getPaintFlags());
    paramTextView.setTypeface(paramTypeface);
  }

  @UiThread
  public void show()
  {
    try
    {
      super.show();
      return;
    }
    catch (WindowManager.BadTokenException localBadTokenException)
    {
    }
    throw new DialogException("Bad window token, you cannot show a dialog before an Activity is created or after it's hidden.");
  }

  public static class Builder
  {
    protected RecyclerView.Adapter<?> adapter;
    protected boolean alwaysCallInputCallback;
    protected boolean alwaysCallMultiChoiceCallback = false;
    protected boolean alwaysCallSingleChoiceCallback = false;
    protected boolean autoDismiss = true;
    protected int backgroundColor;

    @DrawableRes
    protected int btnSelectorNegative;

    @DrawableRes
    protected int btnSelectorNeutral;

    @DrawableRes
    protected int btnSelectorPositive;

    @DrawableRes
    protected int btnSelectorStacked;
    protected GravityEnum btnStackedGravity = GravityEnum.END;
    protected int buttonRippleColor = 0;
    protected GravityEnum buttonsGravity = GravityEnum.START;
    protected MaterialDialog.ButtonCallback callback;
    protected DialogInterface.OnCancelListener cancelListener;
    protected boolean cancelable = true;
    protected boolean canceledOnTouchOutside = true;
    protected CharSequence checkBoxPrompt;
    protected boolean checkBoxPromptInitiallyChecked;
    protected CompoundButton.OnCheckedChangeListener checkBoxPromptListener;
    protected ColorStateList choiceWidgetColor;
    protected CharSequence content;
    protected int contentColor = -1;
    protected boolean contentColorSet = false;
    protected GravityEnum contentGravity = GravityEnum.START;
    protected float contentLineSpacingMultiplier = 1.2F;
    protected final Context context;
    protected View customView;
    protected Integer[] disabledIndices = null;
    protected DialogInterface.OnDismissListener dismissListener;
    protected int dividerColor;
    protected boolean dividerColorSet = false;
    protected Drawable icon;
    protected boolean indeterminateIsHorizontalProgress;
    protected boolean indeterminateProgress;
    protected boolean inputAllowEmpty;
    protected MaterialDialog.InputCallback inputCallback;
    protected CharSequence inputHint;
    protected int inputMaxLength = -1;
    protected int inputMinLength = -1;
    protected CharSequence inputPrefill;
    protected int inputRangeErrorColor = 0;
    protected int inputType = -1;
    protected int itemColor;
    protected boolean itemColorSet = false;
    protected int[] itemIds;
    protected ArrayList<CharSequence> items;
    protected GravityEnum itemsGravity = GravityEnum.START;
    protected DialogInterface.OnKeyListener keyListener;
    protected RecyclerView.LayoutManager layoutManager;
    protected boolean limitIconToDefaultSize;
    protected ColorStateList linkColor;
    protected MaterialDialog.ListCallback listCallback;
    protected MaterialDialog.ListCallbackMultiChoice listCallbackMultiChoice;
    protected MaterialDialog.ListCallbackSingleChoice listCallbackSingleChoice;
    protected MaterialDialog.ListLongCallback listLongCallback;

    @DrawableRes
    protected int listSelector;
    protected int maxIconSize = -1;
    protected Typeface mediumFont;
    protected ColorStateList negativeColor;
    protected boolean negativeColorSet = false;
    protected boolean negativeFocus;
    protected CharSequence negativeText;
    protected ColorStateList neutralColor;
    protected boolean neutralColorSet = false;
    protected boolean neutralFocus;
    protected CharSequence neutralText;
    protected MaterialDialog.SingleButtonCallback onAnyCallback;
    protected MaterialDialog.SingleButtonCallback onNegativeCallback;
    protected MaterialDialog.SingleButtonCallback onNeutralCallback;
    protected MaterialDialog.SingleButtonCallback onPositiveCallback;
    protected ColorStateList positiveColor;
    protected boolean positiveColorSet = false;
    protected boolean positiveFocus;
    protected CharSequence positiveText;
    protected int progress = -2;
    protected int progressMax = 0;
    protected String progressNumberFormat;
    protected NumberFormat progressPercentFormat;
    protected Typeface regularFont;
    protected int selectedIndex = -1;
    protected Integer[] selectedIndices = null;
    protected DialogInterface.OnShowListener showListener;
    protected boolean showMinMax;
    protected StackingBehavior stackingBehavior;
    protected Object tag;
    protected Theme theme = Theme.LIGHT;
    protected CharSequence title;
    protected int titleColor = -1;
    protected boolean titleColorSet = false;
    protected GravityEnum titleGravity = GravityEnum.START;
    protected int widgetColor;
    protected boolean widgetColorSet = false;
    protected boolean wrapCustomViewInScroll;

    public Builder(@NonNull Context paramContext)
    {
      this.context = paramContext;
      int i = DialogUtils.getColor(paramContext, R.color.md_material_blue_600);
      this.widgetColor = DialogUtils.resolveColor(paramContext, R.attr.colorAccent, i);
      if (Build.VERSION.SDK_INT >= 21)
        this.widgetColor = DialogUtils.resolveColor(paramContext, 16843829, this.widgetColor);
      this.positiveColor = DialogUtils.getActionTextStateList(paramContext, this.widgetColor);
      this.negativeColor = DialogUtils.getActionTextStateList(paramContext, this.widgetColor);
      this.neutralColor = DialogUtils.getActionTextStateList(paramContext, this.widgetColor);
      this.linkColor = DialogUtils.getActionTextStateList(paramContext, DialogUtils.resolveColor(paramContext, R.attr.md_link_color, this.widgetColor));
      int j = Build.VERSION.SDK_INT;
      int k = 0;
      if (j >= 21)
        k = DialogUtils.resolveColor(paramContext, 16843820);
      this.buttonRippleColor = DialogUtils.resolveColor(paramContext, R.attr.md_btn_ripple_color, DialogUtils.resolveColor(paramContext, R.attr.colorControlHighlight, k));
      this.progressPercentFormat = NumberFormat.getPercentInstance();
      this.progressNumberFormat = "%1d/%2d";
      Theme localTheme;
      if (DialogUtils.isColorDark(DialogUtils.resolveColor(paramContext, 16842806)))
        localTheme = Theme.LIGHT;
      while (true)
      {
        this.theme = localTheme;
        checkSingleton();
        this.titleGravity = DialogUtils.resolveGravityEnum(paramContext, R.attr.md_title_gravity, this.titleGravity);
        this.contentGravity = DialogUtils.resolveGravityEnum(paramContext, R.attr.md_content_gravity, this.contentGravity);
        this.btnStackedGravity = DialogUtils.resolveGravityEnum(paramContext, R.attr.md_btnstacked_gravity, this.btnStackedGravity);
        this.itemsGravity = DialogUtils.resolveGravityEnum(paramContext, R.attr.md_items_gravity, this.itemsGravity);
        this.buttonsGravity = DialogUtils.resolveGravityEnum(paramContext, R.attr.md_buttons_gravity, this.buttonsGravity);
        String str1 = DialogUtils.resolveString(paramContext, R.attr.md_medium_font);
        String str2 = DialogUtils.resolveString(paramContext, R.attr.md_regular_font);
        try
        {
          typeface(str1, str2);
          label471: if (this.mediumFont == null);
          try
          {
            if (Build.VERSION.SDK_INT >= 21)
            {
              this.mediumFont = Typeface.create("sans-serif-medium", 0);
              if (this.regularFont != null);
            }
          }
          catch (Throwable localThrowable3)
          {
            try
            {
              while (true)
              {
                this.regularFont = Typeface.create("sans-serif", 0);
                return;
                localTheme = Theme.DARK;
                break;
                this.mediumFont = Typeface.create("sans-serif", 1);
              }
              localThrowable3 = localThrowable3;
              this.mediumFont = Typeface.DEFAULT_BOLD;
            }
            catch (Throwable localThrowable2)
            {
              do
                this.regularFont = Typeface.SANS_SERIF;
              while (this.regularFont != null);
              this.regularFont = Typeface.DEFAULT;
              return;
            }
          }
        }
        catch (Throwable localThrowable1)
        {
          break label471;
        }
      }
    }

    private void checkSingleton()
    {
      if (ThemeSingleton.get(false) == null)
        return;
      ThemeSingleton localThemeSingleton = ThemeSingleton.get();
      if (localThemeSingleton.darkTheme)
        this.theme = Theme.DARK;
      if (localThemeSingleton.titleColor != 0)
        this.titleColor = localThemeSingleton.titleColor;
      if (localThemeSingleton.contentColor != 0)
        this.contentColor = localThemeSingleton.contentColor;
      if (localThemeSingleton.positiveColor != null)
        this.positiveColor = localThemeSingleton.positiveColor;
      if (localThemeSingleton.neutralColor != null)
        this.neutralColor = localThemeSingleton.neutralColor;
      if (localThemeSingleton.negativeColor != null)
        this.negativeColor = localThemeSingleton.negativeColor;
      if (localThemeSingleton.itemColor != 0)
        this.itemColor = localThemeSingleton.itemColor;
      if (localThemeSingleton.icon != null)
        this.icon = localThemeSingleton.icon;
      if (localThemeSingleton.backgroundColor != 0)
        this.backgroundColor = localThemeSingleton.backgroundColor;
      if (localThemeSingleton.dividerColor != 0)
        this.dividerColor = localThemeSingleton.dividerColor;
      if (localThemeSingleton.btnSelectorStacked != 0)
        this.btnSelectorStacked = localThemeSingleton.btnSelectorStacked;
      if (localThemeSingleton.listSelector != 0)
        this.listSelector = localThemeSingleton.listSelector;
      if (localThemeSingleton.btnSelectorPositive != 0)
        this.btnSelectorPositive = localThemeSingleton.btnSelectorPositive;
      if (localThemeSingleton.btnSelectorNeutral != 0)
        this.btnSelectorNeutral = localThemeSingleton.btnSelectorNeutral;
      if (localThemeSingleton.btnSelectorNegative != 0)
        this.btnSelectorNegative = localThemeSingleton.btnSelectorNegative;
      if (localThemeSingleton.widgetColor != 0)
        this.widgetColor = localThemeSingleton.widgetColor;
      if (localThemeSingleton.linkColor != null)
        this.linkColor = localThemeSingleton.linkColor;
      this.titleGravity = localThemeSingleton.titleGravity;
      this.contentGravity = localThemeSingleton.contentGravity;
      this.btnStackedGravity = localThemeSingleton.btnStackedGravity;
      this.itemsGravity = localThemeSingleton.itemsGravity;
      this.buttonsGravity = localThemeSingleton.buttonsGravity;
    }

    public Builder adapter(@NonNull RecyclerView.Adapter<?> paramAdapter, @Nullable RecyclerView.LayoutManager paramLayoutManager)
    {
      if (this.customView != null)
        throw new IllegalStateException("You cannot set adapter() when you're using a custom view.");
      if ((paramLayoutManager != null) && (!(paramLayoutManager instanceof LinearLayoutManager)) && (!(paramLayoutManager instanceof GridLayoutManager)))
        throw new IllegalStateException("You can currently only use LinearLayoutManager and GridLayoutManager with this library.");
      this.adapter = paramAdapter;
      this.layoutManager = paramLayoutManager;
      return this;
    }

    public Builder alwaysCallInputCallback()
    {
      this.alwaysCallInputCallback = true;
      return this;
    }

    public Builder alwaysCallMultiChoiceCallback()
    {
      this.alwaysCallMultiChoiceCallback = true;
      return this;
    }

    public Builder alwaysCallSingleChoiceCallback()
    {
      this.alwaysCallSingleChoiceCallback = true;
      return this;
    }

    public Builder autoDismiss(boolean paramBoolean)
    {
      this.autoDismiss = paramBoolean;
      return this;
    }

    public Builder backgroundColor(@ColorInt int paramInt)
    {
      this.backgroundColor = paramInt;
      return this;
    }

    public Builder backgroundColorAttr(@AttrRes int paramInt)
    {
      return backgroundColor(DialogUtils.resolveColor(this.context, paramInt));
    }

    public Builder backgroundColorRes(@ColorRes int paramInt)
    {
      return backgroundColor(DialogUtils.getColor(this.context, paramInt));
    }

    public Builder btnSelector(@DrawableRes int paramInt)
    {
      this.btnSelectorPositive = paramInt;
      this.btnSelectorNeutral = paramInt;
      this.btnSelectorNegative = paramInt;
      return this;
    }

    public Builder btnSelector(@DrawableRes int paramInt, @NonNull DialogAction paramDialogAction)
    {
      switch (MaterialDialog.4.$SwitchMap$com$afollestad$materialdialogs$DialogAction[paramDialogAction.ordinal()])
      {
      default:
        this.btnSelectorPositive = paramInt;
        return this;
      case 1:
        this.btnSelectorNeutral = paramInt;
        return this;
      case 2:
      }
      this.btnSelectorNegative = paramInt;
      return this;
    }

    public Builder btnSelectorStacked(@DrawableRes int paramInt)
    {
      this.btnSelectorStacked = paramInt;
      return this;
    }

    public Builder btnStackedGravity(@NonNull GravityEnum paramGravityEnum)
    {
      this.btnStackedGravity = paramGravityEnum;
      return this;
    }

    @UiThread
    public MaterialDialog build()
    {
      return new MaterialDialog(this);
    }

    public Builder buttonRippleColor(@ColorInt int paramInt)
    {
      this.buttonRippleColor = paramInt;
      return this;
    }

    public Builder buttonRippleColorAttr(@AttrRes int paramInt)
    {
      return buttonRippleColor(DialogUtils.resolveColor(this.context, paramInt));
    }

    public Builder buttonRippleColorRes(@ColorRes int paramInt)
    {
      return buttonRippleColor(DialogUtils.getColor(this.context, paramInt));
    }

    public Builder buttonsGravity(@NonNull GravityEnum paramGravityEnum)
    {
      this.buttonsGravity = paramGravityEnum;
      return this;
    }

    public Builder callback(@NonNull MaterialDialog.ButtonCallback paramButtonCallback)
    {
      this.callback = paramButtonCallback;
      return this;
    }

    public Builder cancelListener(@NonNull DialogInterface.OnCancelListener paramOnCancelListener)
    {
      this.cancelListener = paramOnCancelListener;
      return this;
    }

    public Builder cancelable(boolean paramBoolean)
    {
      this.cancelable = paramBoolean;
      this.canceledOnTouchOutside = paramBoolean;
      return this;
    }

    public Builder canceledOnTouchOutside(boolean paramBoolean)
    {
      this.canceledOnTouchOutside = paramBoolean;
      return this;
    }

    public Builder checkBoxPrompt(@NonNull CharSequence paramCharSequence, boolean paramBoolean, @Nullable CompoundButton.OnCheckedChangeListener paramOnCheckedChangeListener)
    {
      this.checkBoxPrompt = paramCharSequence;
      this.checkBoxPromptInitiallyChecked = paramBoolean;
      this.checkBoxPromptListener = paramOnCheckedChangeListener;
      return this;
    }

    public Builder checkBoxPromptRes(@StringRes int paramInt, boolean paramBoolean, @Nullable CompoundButton.OnCheckedChangeListener paramOnCheckedChangeListener)
    {
      return checkBoxPrompt(this.context.getResources().getText(paramInt), paramBoolean, paramOnCheckedChangeListener);
    }

    public Builder choiceWidgetColor(@Nullable ColorStateList paramColorStateList)
    {
      this.choiceWidgetColor = paramColorStateList;
      return this;
    }

    public Builder content(@StringRes int paramInt)
    {
      return content(paramInt, false);
    }

    public Builder content(@StringRes int paramInt, boolean paramBoolean)
    {
      Object localObject = this.context.getText(paramInt);
      if (paramBoolean)
        localObject = Html.fromHtml(((CharSequence)localObject).toString().replace("\n", "<br/>"));
      return (Builder)content((CharSequence)localObject);
    }

    public Builder content(@StringRes int paramInt, Object[] paramArrayOfObject)
    {
      return content(Html.fromHtml(String.format(this.context.getString(paramInt), paramArrayOfObject).replace("\n", "<br/>")));
    }

    public Builder content(@NonNull CharSequence paramCharSequence)
    {
      if (this.customView != null)
        throw new IllegalStateException("You cannot set content() when you're using a custom view.");
      this.content = paramCharSequence;
      return this;
    }

    public Builder contentColor(@ColorInt int paramInt)
    {
      this.contentColor = paramInt;
      this.contentColorSet = true;
      return this;
    }

    public Builder contentColorAttr(@AttrRes int paramInt)
    {
      contentColor(DialogUtils.resolveColor(this.context, paramInt));
      return this;
    }

    public Builder contentColorRes(@ColorRes int paramInt)
    {
      contentColor(DialogUtils.getColor(this.context, paramInt));
      return this;
    }

    public Builder contentGravity(@NonNull GravityEnum paramGravityEnum)
    {
      this.contentGravity = paramGravityEnum;
      return this;
    }

    public Builder contentLineSpacing(float paramFloat)
    {
      this.contentLineSpacingMultiplier = paramFloat;
      return this;
    }

    public Builder customView(@LayoutRes int paramInt, boolean paramBoolean)
    {
      return customView(LayoutInflater.from(this.context).inflate(paramInt, null), paramBoolean);
    }

    public Builder customView(@NonNull View paramView, boolean paramBoolean)
    {
      if (this.content != null)
        throw new IllegalStateException("You cannot use customView() when you have content set.");
      if (this.items != null)
        throw new IllegalStateException("You cannot use customView() when you have items set.");
      if (this.inputCallback != null)
        throw new IllegalStateException("You cannot use customView() with an input dialog");
      if ((this.progress > -2) || (this.indeterminateProgress))
        throw new IllegalStateException("You cannot use customView() with a progress dialog");
      if ((paramView.getParent() != null) && ((paramView.getParent() instanceof ViewGroup)))
        ((ViewGroup)paramView.getParent()).removeView(paramView);
      this.customView = paramView;
      this.wrapCustomViewInScroll = paramBoolean;
      return this;
    }

    public Builder dismissListener(@NonNull DialogInterface.OnDismissListener paramOnDismissListener)
    {
      this.dismissListener = paramOnDismissListener;
      return this;
    }

    public Builder dividerColor(@ColorInt int paramInt)
    {
      this.dividerColor = paramInt;
      this.dividerColorSet = true;
      return this;
    }

    public Builder dividerColorAttr(@AttrRes int paramInt)
    {
      return dividerColor(DialogUtils.resolveColor(this.context, paramInt));
    }

    public Builder dividerColorRes(@ColorRes int paramInt)
    {
      return dividerColor(DialogUtils.getColor(this.context, paramInt));
    }

    public final Context getContext()
    {
      return this.context;
    }

    public final int getItemColor()
    {
      return this.itemColor;
    }

    public final Typeface getRegularFont()
    {
      return this.regularFont;
    }

    public Builder icon(@NonNull Drawable paramDrawable)
    {
      this.icon = paramDrawable;
      return this;
    }

    public Builder iconAttr(@AttrRes int paramInt)
    {
      this.icon = DialogUtils.resolveDrawable(this.context, paramInt);
      return this;
    }

    public Builder iconRes(@DrawableRes int paramInt)
    {
      this.icon = ResourcesCompat.getDrawable(this.context.getResources(), paramInt, null);
      return this;
    }

    public Builder input(@StringRes int paramInt1, @StringRes int paramInt2, @NonNull MaterialDialog.InputCallback paramInputCallback)
    {
      return input(paramInt1, paramInt2, true, paramInputCallback);
    }

    public Builder input(@StringRes int paramInt1, @StringRes int paramInt2, boolean paramBoolean, @NonNull MaterialDialog.InputCallback paramInputCallback)
    {
      CharSequence localCharSequence1;
      CharSequence localCharSequence2;
      if (paramInt1 == 0)
      {
        localCharSequence1 = null;
        localCharSequence2 = null;
        if (paramInt2 != 0)
          break label39;
      }
      while (true)
      {
        return input(localCharSequence1, localCharSequence2, paramBoolean, paramInputCallback);
        localCharSequence1 = this.context.getText(paramInt1);
        break;
        label39: localCharSequence2 = this.context.getText(paramInt2);
      }
    }

    public Builder input(@Nullable CharSequence paramCharSequence1, @Nullable CharSequence paramCharSequence2, @NonNull MaterialDialog.InputCallback paramInputCallback)
    {
      return input(paramCharSequence1, paramCharSequence2, true, paramInputCallback);
    }

    public Builder input(@Nullable CharSequence paramCharSequence1, @Nullable CharSequence paramCharSequence2, boolean paramBoolean, @NonNull MaterialDialog.InputCallback paramInputCallback)
    {
      if (this.customView != null)
        throw new IllegalStateException("You cannot set content() when you're using a custom view.");
      this.inputCallback = paramInputCallback;
      this.inputHint = paramCharSequence1;
      this.inputPrefill = paramCharSequence2;
      this.inputAllowEmpty = paramBoolean;
      return this;
    }

    public Builder inputRange(@IntRange(from=0L, to=2147483647L) int paramInt1, @IntRange(from=-1L, to=2147483647L) int paramInt2)
    {
      return inputRange(paramInt1, paramInt2, 0);
    }

    public Builder inputRange(@IntRange(from=0L, to=2147483647L) int paramInt1, @IntRange(from=-1L, to=2147483647L) int paramInt2, @ColorInt int paramInt3)
    {
      if (paramInt1 < 0)
        throw new IllegalArgumentException("Min length for input dialogs cannot be less than 0.");
      this.inputMinLength = paramInt1;
      this.inputMaxLength = paramInt2;
      if (paramInt3 == 0);
      for (this.inputRangeErrorColor = DialogUtils.getColor(this.context, R.color.md_edittext_error); ; this.inputRangeErrorColor = paramInt3)
      {
        if (this.inputMinLength > 0)
          this.inputAllowEmpty = false;
        return this;
      }
    }

    public Builder inputRangeRes(@IntRange(from=0L, to=2147483647L) int paramInt1, @IntRange(from=-1L, to=2147483647L) int paramInt2, @ColorRes int paramInt3)
    {
      return inputRange(paramInt1, paramInt2, DialogUtils.getColor(this.context, paramInt3));
    }

    public Builder inputType(int paramInt)
    {
      this.inputType = paramInt;
      return this;
    }

    public Builder items(@ArrayRes int paramInt)
    {
      items(this.context.getResources().getTextArray(paramInt));
      return this;
    }

    public Builder items(@NonNull Collection paramCollection)
    {
      if (paramCollection.size() > 0)
      {
        CharSequence[] arrayOfCharSequence = new CharSequence[paramCollection.size()];
        int i = 0;
        Iterator localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          arrayOfCharSequence[i] = localIterator.next().toString();
          i++;
        }
        items(arrayOfCharSequence);
      }
      do
        return this;
      while (paramCollection.size() != 0);
      this.items = new ArrayList();
      return this;
    }

    public Builder items(@NonNull CharSequence[] paramArrayOfCharSequence)
    {
      if (this.customView != null)
        throw new IllegalStateException("You cannot set items() when you're using a custom view.");
      this.items = new ArrayList();
      Collections.addAll(this.items, paramArrayOfCharSequence);
      return this;
    }

    public Builder itemsCallback(@NonNull MaterialDialog.ListCallback paramListCallback)
    {
      this.listCallback = paramListCallback;
      this.listCallbackSingleChoice = null;
      this.listCallbackMultiChoice = null;
      return this;
    }

    public Builder itemsCallbackMultiChoice(@Nullable Integer[] paramArrayOfInteger, @NonNull MaterialDialog.ListCallbackMultiChoice paramListCallbackMultiChoice)
    {
      this.selectedIndices = paramArrayOfInteger;
      this.listCallback = null;
      this.listCallbackSingleChoice = null;
      this.listCallbackMultiChoice = paramListCallbackMultiChoice;
      return this;
    }

    public Builder itemsCallbackSingleChoice(int paramInt, @NonNull MaterialDialog.ListCallbackSingleChoice paramListCallbackSingleChoice)
    {
      this.selectedIndex = paramInt;
      this.listCallback = null;
      this.listCallbackSingleChoice = paramListCallbackSingleChoice;
      this.listCallbackMultiChoice = null;
      return this;
    }

    public Builder itemsColor(@ColorInt int paramInt)
    {
      this.itemColor = paramInt;
      this.itemColorSet = true;
      return this;
    }

    public Builder itemsColorAttr(@AttrRes int paramInt)
    {
      return itemsColor(DialogUtils.resolveColor(this.context, paramInt));
    }

    public Builder itemsColorRes(@ColorRes int paramInt)
    {
      return itemsColor(DialogUtils.getColor(this.context, paramInt));
    }

    public Builder itemsDisabledIndices(@Nullable Integer[] paramArrayOfInteger)
    {
      this.disabledIndices = paramArrayOfInteger;
      return this;
    }

    public Builder itemsGravity(@NonNull GravityEnum paramGravityEnum)
    {
      this.itemsGravity = paramGravityEnum;
      return this;
    }

    public Builder itemsIds(@ArrayRes int paramInt)
    {
      return itemsIds(this.context.getResources().getIntArray(paramInt));
    }

    public Builder itemsIds(@NonNull int[] paramArrayOfInt)
    {
      this.itemIds = paramArrayOfInt;
      return this;
    }

    public Builder itemsLongCallback(@NonNull MaterialDialog.ListLongCallback paramListLongCallback)
    {
      this.listLongCallback = paramListLongCallback;
      this.listCallbackSingleChoice = null;
      this.listCallbackMultiChoice = null;
      return this;
    }

    public Builder keyListener(@NonNull DialogInterface.OnKeyListener paramOnKeyListener)
    {
      this.keyListener = paramOnKeyListener;
      return this;
    }

    public Builder limitIconToDefaultSize()
    {
      this.limitIconToDefaultSize = true;
      return this;
    }

    public Builder linkColor(@ColorInt int paramInt)
    {
      return linkColor(DialogUtils.getActionTextStateList(this.context, paramInt));
    }

    public Builder linkColor(@NonNull ColorStateList paramColorStateList)
    {
      this.linkColor = paramColorStateList;
      return this;
    }

    public Builder linkColorAttr(@AttrRes int paramInt)
    {
      return linkColor(DialogUtils.resolveActionTextColorStateList(this.context, paramInt, null));
    }

    public Builder linkColorRes(@ColorRes int paramInt)
    {
      return linkColor(DialogUtils.getActionTextColorStateList(this.context, paramInt));
    }

    public Builder listSelector(@DrawableRes int paramInt)
    {
      this.listSelector = paramInt;
      return this;
    }

    public Builder maxIconSize(int paramInt)
    {
      this.maxIconSize = paramInt;
      return this;
    }

    public Builder maxIconSizeRes(@DimenRes int paramInt)
    {
      return maxIconSize((int)this.context.getResources().getDimension(paramInt));
    }

    public Builder negativeColor(@ColorInt int paramInt)
    {
      return negativeColor(DialogUtils.getActionTextStateList(this.context, paramInt));
    }

    public Builder negativeColor(@NonNull ColorStateList paramColorStateList)
    {
      this.negativeColor = paramColorStateList;
      this.negativeColorSet = true;
      return this;
    }

    public Builder negativeColorAttr(@AttrRes int paramInt)
    {
      return negativeColor(DialogUtils.resolveActionTextColorStateList(this.context, paramInt, null));
    }

    public Builder negativeColorRes(@ColorRes int paramInt)
    {
      return negativeColor(DialogUtils.getActionTextColorStateList(this.context, paramInt));
    }

    public Builder negativeFocus(boolean paramBoolean)
    {
      this.negativeFocus = paramBoolean;
      return this;
    }

    public Builder negativeText(@StringRes int paramInt)
    {
      if (paramInt == 0)
        return this;
      return negativeText(this.context.getText(paramInt));
    }

    public Builder negativeText(@NonNull CharSequence paramCharSequence)
    {
      this.negativeText = paramCharSequence;
      return this;
    }

    public Builder neutralColor(@ColorInt int paramInt)
    {
      return neutralColor(DialogUtils.getActionTextStateList(this.context, paramInt));
    }

    public Builder neutralColor(@NonNull ColorStateList paramColorStateList)
    {
      this.neutralColor = paramColorStateList;
      this.neutralColorSet = true;
      return this;
    }

    public Builder neutralColorAttr(@AttrRes int paramInt)
    {
      return neutralColor(DialogUtils.resolveActionTextColorStateList(this.context, paramInt, null));
    }

    public Builder neutralColorRes(@ColorRes int paramInt)
    {
      return neutralColor(DialogUtils.getActionTextColorStateList(this.context, paramInt));
    }

    public Builder neutralFocus(boolean paramBoolean)
    {
      this.neutralFocus = paramBoolean;
      return this;
    }

    public Builder neutralText(@StringRes int paramInt)
    {
      if (paramInt == 0)
        return this;
      return neutralText(this.context.getText(paramInt));
    }

    public Builder neutralText(@NonNull CharSequence paramCharSequence)
    {
      this.neutralText = paramCharSequence;
      return this;
    }

    public Builder onAny(@NonNull MaterialDialog.SingleButtonCallback paramSingleButtonCallback)
    {
      this.onAnyCallback = paramSingleButtonCallback;
      return this;
    }

    public Builder onNegative(@NonNull MaterialDialog.SingleButtonCallback paramSingleButtonCallback)
    {
      this.onNegativeCallback = paramSingleButtonCallback;
      return this;
    }

    public Builder onNeutral(@NonNull MaterialDialog.SingleButtonCallback paramSingleButtonCallback)
    {
      this.onNeutralCallback = paramSingleButtonCallback;
      return this;
    }

    public Builder onPositive(@NonNull MaterialDialog.SingleButtonCallback paramSingleButtonCallback)
    {
      this.onPositiveCallback = paramSingleButtonCallback;
      return this;
    }

    public Builder positiveColor(@ColorInt int paramInt)
    {
      return positiveColor(DialogUtils.getActionTextStateList(this.context, paramInt));
    }

    public Builder positiveColor(@NonNull ColorStateList paramColorStateList)
    {
      this.positiveColor = paramColorStateList;
      this.positiveColorSet = true;
      return this;
    }

    public Builder positiveColorAttr(@AttrRes int paramInt)
    {
      return positiveColor(DialogUtils.resolveActionTextColorStateList(this.context, paramInt, null));
    }

    public Builder positiveColorRes(@ColorRes int paramInt)
    {
      return positiveColor(DialogUtils.getActionTextColorStateList(this.context, paramInt));
    }

    public Builder positiveFocus(boolean paramBoolean)
    {
      this.positiveFocus = paramBoolean;
      return this;
    }

    public Builder positiveText(@StringRes int paramInt)
    {
      if (paramInt == 0)
        return this;
      positiveText(this.context.getText(paramInt));
      return this;
    }

    public Builder positiveText(@NonNull CharSequence paramCharSequence)
    {
      this.positiveText = paramCharSequence;
      return this;
    }

    public Builder progress(boolean paramBoolean, int paramInt)
    {
      if (this.customView != null)
        throw new IllegalStateException("You cannot set progress() when you're using a custom view.");
      if (paramBoolean)
      {
        this.indeterminateProgress = true;
        this.progress = -2;
        return this;
      }
      this.indeterminateIsHorizontalProgress = false;
      this.indeterminateProgress = false;
      this.progress = -1;
      this.progressMax = paramInt;
      return this;
    }

    public Builder progress(boolean paramBoolean1, int paramInt, boolean paramBoolean2)
    {
      this.showMinMax = paramBoolean2;
      return progress(paramBoolean1, paramInt);
    }

    public Builder progressIndeterminateStyle(boolean paramBoolean)
    {
      this.indeterminateIsHorizontalProgress = paramBoolean;
      return this;
    }

    public Builder progressNumberFormat(@NonNull String paramString)
    {
      this.progressNumberFormat = paramString;
      return this;
    }

    public Builder progressPercentFormat(@NonNull NumberFormat paramNumberFormat)
    {
      this.progressPercentFormat = paramNumberFormat;
      return this;
    }

    @UiThread
    public MaterialDialog show()
    {
      MaterialDialog localMaterialDialog = build();
      localMaterialDialog.show();
      return localMaterialDialog;
    }

    public Builder showListener(@NonNull DialogInterface.OnShowListener paramOnShowListener)
    {
      this.showListener = paramOnShowListener;
      return this;
    }

    public Builder stackingBehavior(@NonNull StackingBehavior paramStackingBehavior)
    {
      this.stackingBehavior = paramStackingBehavior;
      return this;
    }

    public Builder tag(@Nullable Object paramObject)
    {
      this.tag = paramObject;
      return this;
    }

    public Builder theme(@NonNull Theme paramTheme)
    {
      this.theme = paramTheme;
      return this;
    }

    public Builder title(@StringRes int paramInt)
    {
      title(this.context.getText(paramInt));
      return this;
    }

    public Builder title(@NonNull CharSequence paramCharSequence)
    {
      this.title = paramCharSequence;
      return this;
    }

    public Builder titleColor(@ColorInt int paramInt)
    {
      this.titleColor = paramInt;
      this.titleColorSet = true;
      return this;
    }

    public Builder titleColorAttr(@AttrRes int paramInt)
    {
      return titleColor(DialogUtils.resolveColor(this.context, paramInt));
    }

    public Builder titleColorRes(@ColorRes int paramInt)
    {
      return titleColor(DialogUtils.getColor(this.context, paramInt));
    }

    public Builder titleGravity(@NonNull GravityEnum paramGravityEnum)
    {
      this.titleGravity = paramGravityEnum;
      return this;
    }

    public Builder typeface(@Nullable Typeface paramTypeface1, @Nullable Typeface paramTypeface2)
    {
      this.mediumFont = paramTypeface1;
      this.regularFont = paramTypeface2;
      return this;
    }

    public Builder typeface(@Nullable String paramString1, @Nullable String paramString2)
    {
      if ((paramString1 != null) && (!paramString1.trim().isEmpty()))
      {
        this.mediumFont = TypefaceHelper.get(this.context, paramString1);
        if (this.mediumFont == null)
          throw new IllegalArgumentException("No font asset found for \"" + paramString1 + "\"");
      }
      if ((paramString2 != null) && (!paramString2.trim().isEmpty()))
      {
        this.regularFont = TypefaceHelper.get(this.context, paramString2);
        if (this.regularFont == null)
          throw new IllegalArgumentException("No font asset found for \"" + paramString2 + "\"");
      }
      return this;
    }

    public Builder widgetColor(@ColorInt int paramInt)
    {
      this.widgetColor = paramInt;
      this.widgetColorSet = true;
      return this;
    }

    public Builder widgetColorAttr(@AttrRes int paramInt)
    {
      return widgetColor(DialogUtils.resolveColor(this.context, paramInt));
    }

    public Builder widgetColorRes(@ColorRes int paramInt)
    {
      return widgetColor(DialogUtils.getColor(this.context, paramInt));
    }
  }

  @Deprecated
  public static abstract class ButtonCallback
  {
    protected final Object clone()
      throws CloneNotSupportedException
    {
      return super.clone();
    }

    public final boolean equals(Object paramObject)
    {
      return super.equals(paramObject);
    }

    protected final void finalize()
      throws Throwable
    {
      super.finalize();
    }

    public final int hashCode()
    {
      return super.hashCode();
    }

    @Deprecated
    public void onAny(MaterialDialog paramMaterialDialog)
    {
    }

    @Deprecated
    public void onNegative(MaterialDialog paramMaterialDialog)
    {
    }

    @Deprecated
    public void onNeutral(MaterialDialog paramMaterialDialog)
    {
    }

    @Deprecated
    public void onPositive(MaterialDialog paramMaterialDialog)
    {
    }

    public final String toString()
    {
      return super.toString();
    }
  }

  private static class DialogException extends WindowManager.BadTokenException
  {
    DialogException(String paramString)
    {
      super();
    }
  }

  public static abstract interface InputCallback
  {
    public abstract void onInput(@NonNull MaterialDialog paramMaterialDialog, CharSequence paramCharSequence);
  }

  public static abstract interface ListCallback
  {
    public abstract void onSelection(MaterialDialog paramMaterialDialog, View paramView, int paramInt, CharSequence paramCharSequence);
  }

  public static abstract interface ListCallbackMultiChoice
  {
    public abstract boolean onSelection(MaterialDialog paramMaterialDialog, Integer[] paramArrayOfInteger, CharSequence[] paramArrayOfCharSequence);
  }

  public static abstract interface ListCallbackSingleChoice
  {
    public abstract boolean onSelection(MaterialDialog paramMaterialDialog, View paramView, int paramInt, CharSequence paramCharSequence);
  }

  public static abstract interface ListLongCallback
  {
    public abstract boolean onLongSelection(MaterialDialog paramMaterialDialog, View paramView, int paramInt, CharSequence paramCharSequence);
  }

  static enum ListType
  {
    static
    {
      MULTI = new ListType("MULTI", 2);
      ListType[] arrayOfListType = new ListType[3];
      arrayOfListType[0] = REGULAR;
      arrayOfListType[1] = SINGLE;
      arrayOfListType[2] = MULTI;
      $VALUES = arrayOfListType;
    }

    public static int getLayoutForType(ListType paramListType)
    {
      switch (MaterialDialog.4.$SwitchMap$com$afollestad$materialdialogs$MaterialDialog$ListType[paramListType.ordinal()])
      {
      default:
        throw new IllegalArgumentException("Not a valid list type");
      case 1:
        return R.layout.md_listitem;
      case 2:
        return R.layout.md_listitem_singlechoice;
      case 3:
      }
      return R.layout.md_listitem_multichoice;
    }
  }

  public static abstract interface SingleButtonCallback
  {
    public abstract void onClick(@NonNull MaterialDialog paramMaterialDialog, @NonNull DialogAction paramDialogAction);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.MaterialDialog
 * JD-Core Version:    0.6.0
 */