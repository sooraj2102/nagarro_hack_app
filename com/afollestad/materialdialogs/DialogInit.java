package com.afollestad.materialdialogs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.afollestad.materialdialogs.internal.MDAdapter;
import com.afollestad.materialdialogs.internal.MDButton;
import com.afollestad.materialdialogs.internal.MDRootLayout;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import me.zhanghai.android.materialprogressbar.HorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.IndeterminateCircularProgressDrawable;
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;

class DialogInit
{
  private static void fixCanvasScalingWhenHardwareAccelerated(ProgressBar paramProgressBar)
  {
    if ((Build.VERSION.SDK_INT < 18) && (paramProgressBar.isHardwareAccelerated()) && (paramProgressBar.getLayerType() != 1))
      paramProgressBar.setLayerType(1, null);
  }

  @LayoutRes
  static int getInflateLayout(MaterialDialog.Builder paramBuilder)
  {
    if (paramBuilder.customView != null)
      return R.layout.md_dialog_custom;
    if ((paramBuilder.items != null) || (paramBuilder.adapter != null))
    {
      if (paramBuilder.checkBoxPrompt != null)
        return R.layout.md_dialog_list_check;
      return R.layout.md_dialog_list;
    }
    if (paramBuilder.progress > -2)
      return R.layout.md_dialog_progress;
    if (paramBuilder.indeterminateProgress)
    {
      if (paramBuilder.indeterminateIsHorizontalProgress)
        return R.layout.md_dialog_progress_indeterminate_horizontal;
      return R.layout.md_dialog_progress_indeterminate;
    }
    if (paramBuilder.inputCallback != null)
    {
      if (paramBuilder.checkBoxPrompt != null)
        return R.layout.md_dialog_input_check;
      return R.layout.md_dialog_input;
    }
    if (paramBuilder.checkBoxPrompt != null)
      return R.layout.md_dialog_basic_check;
    return R.layout.md_dialog_basic;
  }

  @StyleRes
  static int getTheme(@NonNull MaterialDialog.Builder paramBuilder)
  {
    Context localContext = paramBuilder.context;
    int i = R.attr.md_dark_theme;
    boolean bool1;
    boolean bool2;
    if (paramBuilder.theme == Theme.DARK)
    {
      bool1 = true;
      bool2 = DialogUtils.resolveBoolean(localContext, i, bool1);
      if (!bool2)
        break label59;
    }
    label59: for (Theme localTheme = Theme.DARK; ; localTheme = Theme.LIGHT)
    {
      paramBuilder.theme = localTheme;
      if (!bool2)
        break label67;
      return R.style.MD_Dark;
      bool1 = false;
      break;
    }
    label67: return R.style.MD_Light;
  }

  @UiThread
  public static void init(MaterialDialog paramMaterialDialog)
  {
    MaterialDialog.Builder localBuilder = paramMaterialDialog.builder;
    paramMaterialDialog.setCancelable(localBuilder.cancelable);
    paramMaterialDialog.setCanceledOnTouchOutside(localBuilder.canceledOnTouchOutside);
    if (localBuilder.backgroundColor == 0)
      localBuilder.backgroundColor = DialogUtils.resolveColor(localBuilder.context, R.attr.md_background_color, DialogUtils.resolveColor(paramMaterialDialog.getContext(), R.attr.colorBackgroundFloating));
    if (localBuilder.backgroundColor != 0)
    {
      GradientDrawable localGradientDrawable = new GradientDrawable();
      localGradientDrawable.setCornerRadius(localBuilder.context.getResources().getDimension(R.dimen.md_bg_corner_radius));
      localGradientDrawable.setColor(localBuilder.backgroundColor);
      paramMaterialDialog.getWindow().setBackgroundDrawable(localGradientDrawable);
    }
    if (!localBuilder.positiveColorSet)
      localBuilder.positiveColor = DialogUtils.resolveActionTextColorStateList(localBuilder.context, R.attr.md_positive_color, localBuilder.positiveColor);
    if (!localBuilder.neutralColorSet)
      localBuilder.neutralColor = DialogUtils.resolveActionTextColorStateList(localBuilder.context, R.attr.md_neutral_color, localBuilder.neutralColor);
    if (!localBuilder.negativeColorSet)
      localBuilder.negativeColor = DialogUtils.resolveActionTextColorStateList(localBuilder.context, R.attr.md_negative_color, localBuilder.negativeColor);
    if (!localBuilder.widgetColorSet)
      localBuilder.widgetColor = DialogUtils.resolveColor(localBuilder.context, R.attr.md_widget_color, localBuilder.widgetColor);
    if (!localBuilder.titleColorSet)
    {
      int i11 = DialogUtils.resolveColor(paramMaterialDialog.getContext(), 16842806);
      localBuilder.titleColor = DialogUtils.resolveColor(localBuilder.context, R.attr.md_title_color, i11);
    }
    if (!localBuilder.contentColorSet)
    {
      int i10 = DialogUtils.resolveColor(paramMaterialDialog.getContext(), 16842808);
      localBuilder.contentColor = DialogUtils.resolveColor(localBuilder.context, R.attr.md_content_color, i10);
    }
    if (!localBuilder.itemColorSet)
      localBuilder.itemColor = DialogUtils.resolveColor(localBuilder.context, R.attr.md_item_color, localBuilder.contentColor);
    paramMaterialDialog.title = ((TextView)paramMaterialDialog.view.findViewById(R.id.md_title));
    paramMaterialDialog.icon = ((ImageView)paramMaterialDialog.view.findViewById(R.id.md_icon));
    paramMaterialDialog.titleFrame = paramMaterialDialog.view.findViewById(R.id.md_titleFrame);
    paramMaterialDialog.content = ((TextView)paramMaterialDialog.view.findViewById(R.id.md_content));
    paramMaterialDialog.recyclerView = ((RecyclerView)paramMaterialDialog.view.findViewById(R.id.md_contentRecyclerView));
    paramMaterialDialog.checkBoxPrompt = ((CheckBox)paramMaterialDialog.view.findViewById(R.id.md_promptCheckbox));
    paramMaterialDialog.positiveButton = ((MDButton)paramMaterialDialog.view.findViewById(R.id.md_buttonDefaultPositive));
    paramMaterialDialog.neutralButton = ((MDButton)paramMaterialDialog.view.findViewById(R.id.md_buttonDefaultNeutral));
    paramMaterialDialog.negativeButton = ((MDButton)paramMaterialDialog.view.findViewById(R.id.md_buttonDefaultNegative));
    if ((localBuilder.inputCallback != null) && (localBuilder.positiveText == null))
      localBuilder.positiveText = localBuilder.context.getText(17039370);
    MDButton localMDButton1 = paramMaterialDialog.positiveButton;
    int i;
    int j;
    label510: int k;
    label533: label635: boolean bool;
    label861: label1003: label1149: FrameLayout localFrameLayout;
    label930: label1486: label1509: Object localObject;
    int i6;
    ScrollView localScrollView;
    int i7;
    int i8;
    if (localBuilder.positiveText != null)
    {
      i = 0;
      localMDButton1.setVisibility(i);
      MDButton localMDButton2 = paramMaterialDialog.neutralButton;
      if (localBuilder.neutralText == null)
        break label1955;
      j = 0;
      localMDButton2.setVisibility(j);
      MDButton localMDButton3 = paramMaterialDialog.negativeButton;
      if (localBuilder.negativeText == null)
        break label1962;
      k = 0;
      localMDButton3.setVisibility(k);
      paramMaterialDialog.positiveButton.setFocusable(true);
      paramMaterialDialog.neutralButton.setFocusable(true);
      paramMaterialDialog.negativeButton.setFocusable(true);
      if (localBuilder.positiveFocus)
        paramMaterialDialog.positiveButton.requestFocus();
      if (localBuilder.neutralFocus)
        paramMaterialDialog.neutralButton.requestFocus();
      if (localBuilder.negativeFocus)
        paramMaterialDialog.negativeButton.requestFocus();
      if (localBuilder.icon == null)
        break label1969;
      paramMaterialDialog.icon.setVisibility(0);
      paramMaterialDialog.icon.setImageDrawable(localBuilder.icon);
      int m = localBuilder.maxIconSize;
      if (m == -1)
        m = DialogUtils.resolveDimension(localBuilder.context, R.attr.md_icon_max_size);
      if ((localBuilder.limitIconToDefaultSize) || (DialogUtils.resolveBoolean(localBuilder.context, R.attr.md_icon_limit_icon_to_default_size)))
        m = localBuilder.context.getResources().getDimensionPixelSize(R.dimen.md_icon_max_size);
      if (m > -1)
      {
        paramMaterialDialog.icon.setAdjustViewBounds(true);
        paramMaterialDialog.icon.setMaxHeight(m);
        paramMaterialDialog.icon.setMaxWidth(m);
        paramMaterialDialog.icon.requestLayout();
      }
      if (!localBuilder.dividerColorSet)
      {
        int i9 = DialogUtils.resolveColor(paramMaterialDialog.getContext(), R.attr.md_divider);
        localBuilder.dividerColor = DialogUtils.resolveColor(localBuilder.context, R.attr.md_divider_color, i9);
      }
      paramMaterialDialog.view.setDividerColor(localBuilder.dividerColor);
      if (paramMaterialDialog.title != null)
      {
        paramMaterialDialog.setTypeface(paramMaterialDialog.title, localBuilder.mediumFont);
        paramMaterialDialog.title.setTextColor(localBuilder.titleColor);
        paramMaterialDialog.title.setGravity(localBuilder.titleGravity.getGravityInt());
        if (Build.VERSION.SDK_INT >= 17)
          paramMaterialDialog.title.setTextAlignment(localBuilder.titleGravity.getTextAlignment());
        if (localBuilder.title != null)
          break label2018;
        paramMaterialDialog.titleFrame.setVisibility(8);
      }
      if (paramMaterialDialog.content != null)
      {
        paramMaterialDialog.content.setMovementMethod(new LinkMovementMethod());
        paramMaterialDialog.setTypeface(paramMaterialDialog.content, localBuilder.regularFont);
        paramMaterialDialog.content.setLineSpacing(0.0F, localBuilder.contentLineSpacingMultiplier);
        if (localBuilder.linkColor != null)
          break label2040;
        paramMaterialDialog.content.setLinkTextColor(DialogUtils.resolveColor(paramMaterialDialog.getContext(), 16842806));
        paramMaterialDialog.content.setTextColor(localBuilder.contentColor);
        paramMaterialDialog.content.setGravity(localBuilder.contentGravity.getGravityInt());
        if (Build.VERSION.SDK_INT >= 17)
          paramMaterialDialog.content.setTextAlignment(localBuilder.contentGravity.getTextAlignment());
        if (localBuilder.content == null)
          break label2054;
        paramMaterialDialog.content.setText(localBuilder.content);
        paramMaterialDialog.content.setVisibility(0);
      }
      if (paramMaterialDialog.checkBoxPrompt != null)
      {
        paramMaterialDialog.checkBoxPrompt.setText(localBuilder.checkBoxPrompt);
        paramMaterialDialog.checkBoxPrompt.setChecked(localBuilder.checkBoxPromptInitiallyChecked);
        paramMaterialDialog.checkBoxPrompt.setOnCheckedChangeListener(localBuilder.checkBoxPromptListener);
        paramMaterialDialog.setTypeface(paramMaterialDialog.checkBoxPrompt, localBuilder.regularFont);
        paramMaterialDialog.checkBoxPrompt.setTextColor(localBuilder.contentColor);
        MDTintHelper.setTint(paramMaterialDialog.checkBoxPrompt, localBuilder.widgetColor);
      }
      paramMaterialDialog.view.setButtonGravity(localBuilder.buttonsGravity);
      paramMaterialDialog.view.setButtonStackedGravity(localBuilder.btnStackedGravity);
      paramMaterialDialog.view.setStackingBehavior(localBuilder.stackingBehavior);
      if (Build.VERSION.SDK_INT < 14)
        break label2066;
      bool = DialogUtils.resolveBoolean(localBuilder.context, 16843660, true);
      if (bool)
        bool = DialogUtils.resolveBoolean(localBuilder.context, R.attr.textAllCaps, true);
      MDButton localMDButton4 = paramMaterialDialog.positiveButton;
      paramMaterialDialog.setTypeface(localMDButton4, localBuilder.mediumFont);
      localMDButton4.setAllCapsCompat(bool);
      localMDButton4.setText(localBuilder.positiveText);
      localMDButton4.setTextColor(localBuilder.positiveColor);
      paramMaterialDialog.positiveButton.setStackedSelector(paramMaterialDialog.getButtonSelector(DialogAction.POSITIVE, true));
      paramMaterialDialog.positiveButton.setDefaultSelector(paramMaterialDialog.getButtonSelector(DialogAction.POSITIVE, false));
      paramMaterialDialog.positiveButton.setTag(DialogAction.POSITIVE);
      paramMaterialDialog.positiveButton.setOnClickListener(paramMaterialDialog);
      paramMaterialDialog.positiveButton.setVisibility(0);
      MDButton localMDButton5 = paramMaterialDialog.negativeButton;
      paramMaterialDialog.setTypeface(localMDButton5, localBuilder.mediumFont);
      localMDButton5.setAllCapsCompat(bool);
      localMDButton5.setText(localBuilder.negativeText);
      localMDButton5.setTextColor(localBuilder.negativeColor);
      paramMaterialDialog.negativeButton.setStackedSelector(paramMaterialDialog.getButtonSelector(DialogAction.NEGATIVE, true));
      paramMaterialDialog.negativeButton.setDefaultSelector(paramMaterialDialog.getButtonSelector(DialogAction.NEGATIVE, false));
      paramMaterialDialog.negativeButton.setTag(DialogAction.NEGATIVE);
      paramMaterialDialog.negativeButton.setOnClickListener(paramMaterialDialog);
      paramMaterialDialog.negativeButton.setVisibility(0);
      MDButton localMDButton6 = paramMaterialDialog.neutralButton;
      paramMaterialDialog.setTypeface(localMDButton6, localBuilder.mediumFont);
      localMDButton6.setAllCapsCompat(bool);
      localMDButton6.setText(localBuilder.neutralText);
      localMDButton6.setTextColor(localBuilder.neutralColor);
      paramMaterialDialog.neutralButton.setStackedSelector(paramMaterialDialog.getButtonSelector(DialogAction.NEUTRAL, true));
      paramMaterialDialog.neutralButton.setDefaultSelector(paramMaterialDialog.getButtonSelector(DialogAction.NEUTRAL, false));
      paramMaterialDialog.neutralButton.setTag(DialogAction.NEUTRAL);
      paramMaterialDialog.neutralButton.setOnClickListener(paramMaterialDialog);
      paramMaterialDialog.neutralButton.setVisibility(0);
      if (localBuilder.listCallbackMultiChoice != null)
        paramMaterialDialog.selectedIndicesList = new ArrayList();
      if (paramMaterialDialog.recyclerView != null)
      {
        if (localBuilder.adapter != null)
          break label2139;
        if (localBuilder.listCallbackSingleChoice == null)
          break label2082;
        paramMaterialDialog.listType = MaterialDialog.ListType.SINGLE;
        DefaultRvAdapter localDefaultRvAdapter = new DefaultRvAdapter(paramMaterialDialog, MaterialDialog.ListType.getLayoutForType(paramMaterialDialog.listType));
        localBuilder.adapter = localDefaultRvAdapter;
      }
      setupProgressDialog(paramMaterialDialog);
      setupInputDialog(paramMaterialDialog);
      if (localBuilder.customView != null)
      {
        ((MDRootLayout)paramMaterialDialog.view.findViewById(R.id.md_root)).noTitleNoPadding();
        localFrameLayout = (FrameLayout)paramMaterialDialog.view.findViewById(R.id.md_customViewFrame);
        paramMaterialDialog.customViewFrame = localFrameLayout;
        localObject = localBuilder.customView;
        if (((View)localObject).getParent() != null)
          ((ViewGroup)((View)localObject).getParent()).removeView((View)localObject);
        if (localBuilder.wrapCustomViewInScroll)
        {
          Resources localResources = paramMaterialDialog.getContext().getResources();
          i6 = localResources.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
          localScrollView = new ScrollView(paramMaterialDialog.getContext());
          i7 = localResources.getDimensionPixelSize(R.dimen.md_content_padding_top);
          i8 = localResources.getDimensionPixelSize(R.dimen.md_content_padding_bottom);
          localScrollView.setClipToPadding(false);
          if (!(localObject instanceof EditText))
            break label2165;
          localScrollView.setPadding(i6, i7, i6, i8);
        }
      }
    }
    while (true)
    {
      localScrollView.addView((View)localObject, new FrameLayout.LayoutParams(-1, -2));
      localObject = localScrollView;
      localFrameLayout.addView((View)localObject, new ViewGroup.LayoutParams(-1, -2));
      if (localBuilder.showListener != null)
        paramMaterialDialog.setOnShowListener(localBuilder.showListener);
      if (localBuilder.cancelListener != null)
        paramMaterialDialog.setOnCancelListener(localBuilder.cancelListener);
      if (localBuilder.dismissListener != null)
        paramMaterialDialog.setOnDismissListener(localBuilder.dismissListener);
      if (localBuilder.keyListener != null)
        paramMaterialDialog.setOnKeyListener(localBuilder.keyListener);
      paramMaterialDialog.setOnShowListenerInternal();
      paramMaterialDialog.invalidateList();
      paramMaterialDialog.setViewInternal(paramMaterialDialog.view);
      paramMaterialDialog.checkIfListInitScroll();
      Display localDisplay = paramMaterialDialog.getWindow().getWindowManager().getDefaultDisplay();
      Point localPoint = new Point();
      localDisplay.getSize(localPoint);
      int n = localPoint.x;
      int i1 = localPoint.y;
      int i2 = localBuilder.context.getResources().getDimensionPixelSize(R.dimen.md_dialog_vertical_margin);
      int i3 = localBuilder.context.getResources().getDimensionPixelSize(R.dimen.md_dialog_horizontal_margin);
      int i4 = localBuilder.context.getResources().getDimensionPixelSize(R.dimen.md_dialog_max_width);
      int i5 = n - i3 * 2;
      paramMaterialDialog.view.setMaxHeight(i1 - i2 * 2);
      WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
      localLayoutParams.copyFrom(paramMaterialDialog.getWindow().getAttributes());
      localLayoutParams.width = Math.min(i4, i5);
      paramMaterialDialog.getWindow().setAttributes(localLayoutParams);
      return;
      i = 8;
      break;
      label1955: j = 8;
      break label510;
      label1962: k = 8;
      break label533;
      label1969: Drawable localDrawable = DialogUtils.resolveDrawable(localBuilder.context, R.attr.md_icon);
      if (localDrawable != null)
      {
        paramMaterialDialog.icon.setVisibility(0);
        paramMaterialDialog.icon.setImageDrawable(localDrawable);
        break label635;
      }
      paramMaterialDialog.icon.setVisibility(8);
      break label635;
      label2018: paramMaterialDialog.title.setText(localBuilder.title);
      paramMaterialDialog.titleFrame.setVisibility(0);
      break label861;
      label2040: paramMaterialDialog.content.setLinkTextColor(localBuilder.linkColor);
      break label930;
      label2054: paramMaterialDialog.content.setVisibility(8);
      break label1003;
      label2066: bool = DialogUtils.resolveBoolean(localBuilder.context, R.attr.textAllCaps, true);
      break label1149;
      label2082: if (localBuilder.listCallbackMultiChoice != null)
      {
        paramMaterialDialog.listType = MaterialDialog.ListType.MULTI;
        if (localBuilder.selectedIndices == null)
          break label1486;
        paramMaterialDialog.selectedIndicesList = new ArrayList(Arrays.asList(localBuilder.selectedIndices));
        localBuilder.selectedIndices = null;
        break label1486;
      }
      paramMaterialDialog.listType = MaterialDialog.ListType.REGULAR;
      break label1486;
      label2139: if (!(localBuilder.adapter instanceof MDAdapter))
        break label1509;
      ((MDAdapter)localBuilder.adapter).setDialog(paramMaterialDialog);
      break label1509;
      label2165: localScrollView.setPadding(0, i7, 0, i8);
      ((View)localObject).setPadding(i6, 0, i6, 0);
    }
  }

  private static void setupInputDialog(MaterialDialog paramMaterialDialog)
  {
    MaterialDialog.Builder localBuilder = paramMaterialDialog.builder;
    paramMaterialDialog.input = ((EditText)paramMaterialDialog.view.findViewById(16908297));
    if (paramMaterialDialog.input == null)
      return;
    paramMaterialDialog.setTypeface(paramMaterialDialog.input, localBuilder.regularFont);
    if (localBuilder.inputPrefill != null)
      paramMaterialDialog.input.setText(localBuilder.inputPrefill);
    paramMaterialDialog.setInternalInputCallback();
    paramMaterialDialog.input.setHint(localBuilder.inputHint);
    paramMaterialDialog.input.setSingleLine();
    paramMaterialDialog.input.setTextColor(localBuilder.contentColor);
    paramMaterialDialog.input.setHintTextColor(DialogUtils.adjustAlpha(localBuilder.contentColor, 0.3F));
    MDTintHelper.setTint(paramMaterialDialog.input, paramMaterialDialog.builder.widgetColor);
    if (localBuilder.inputType != -1)
    {
      paramMaterialDialog.input.setInputType(localBuilder.inputType);
      if ((localBuilder.inputType != 144) && ((0x80 & localBuilder.inputType) == 128))
        paramMaterialDialog.input.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
    paramMaterialDialog.inputMinMax = ((TextView)paramMaterialDialog.view.findViewById(R.id.md_minMax));
    if ((localBuilder.inputMinLength > 0) || (localBuilder.inputMaxLength > -1))
    {
      int i = paramMaterialDialog.input.getText().toString().length();
      if (!localBuilder.inputAllowEmpty);
      for (boolean bool = true; ; bool = false)
      {
        paramMaterialDialog.invalidateInputMinMaxIndicator(i, bool);
        return;
      }
    }
    paramMaterialDialog.inputMinMax.setVisibility(8);
    paramMaterialDialog.inputMinMax = null;
  }

  private static void setupProgressDialog(MaterialDialog paramMaterialDialog)
  {
    MaterialDialog.Builder localBuilder = paramMaterialDialog.builder;
    if ((localBuilder.indeterminateProgress) || (localBuilder.progress > -2))
    {
      paramMaterialDialog.progressBar = ((ProgressBar)paramMaterialDialog.view.findViewById(16908301));
      if (paramMaterialDialog.progressBar != null);
    }
    label143: label479: label484: label496: label502: 
    while (true)
    {
      return;
      boolean bool;
      if (Build.VERSION.SDK_INT >= 14)
        if (localBuilder.indeterminateProgress)
          if (localBuilder.indeterminateIsHorizontalProgress)
          {
            IndeterminateHorizontalProgressDrawable localIndeterminateHorizontalProgressDrawable = new IndeterminateHorizontalProgressDrawable(localBuilder.getContext());
            localIndeterminateHorizontalProgressDrawable.setTint(localBuilder.widgetColor);
            paramMaterialDialog.progressBar.setProgressDrawable(localIndeterminateHorizontalProgressDrawable);
            paramMaterialDialog.progressBar.setIndeterminateDrawable(localIndeterminateHorizontalProgressDrawable);
            if ((!localBuilder.indeterminateProgress) || (localBuilder.indeterminateIsHorizontalProgress))
            {
              ProgressBar localProgressBar = paramMaterialDialog.progressBar;
              if ((!localBuilder.indeterminateProgress) || (!localBuilder.indeterminateIsHorizontalProgress))
                break label479;
              bool = true;
              localProgressBar.setIndeterminate(bool);
              paramMaterialDialog.progressBar.setProgress(0);
              paramMaterialDialog.progressBar.setMax(localBuilder.progressMax);
              paramMaterialDialog.progressLabel = ((TextView)paramMaterialDialog.view.findViewById(R.id.md_label));
              if (paramMaterialDialog.progressLabel != null)
              {
                paramMaterialDialog.progressLabel.setTextColor(localBuilder.contentColor);
                paramMaterialDialog.setTypeface(paramMaterialDialog.progressLabel, localBuilder.mediumFont);
                paramMaterialDialog.progressLabel.setText(localBuilder.progressPercentFormat.format(0L));
              }
              paramMaterialDialog.progressMinMax = ((TextView)paramMaterialDialog.view.findViewById(R.id.md_minMax));
              if (paramMaterialDialog.progressMinMax == null)
                break label496;
              paramMaterialDialog.progressMinMax.setTextColor(localBuilder.contentColor);
              paramMaterialDialog.setTypeface(paramMaterialDialog.progressMinMax, localBuilder.regularFont);
              if (!localBuilder.showMinMax)
                break label484;
              paramMaterialDialog.progressMinMax.setVisibility(0);
              TextView localTextView = paramMaterialDialog.progressMinMax;
              String str = localBuilder.progressNumberFormat;
              Object[] arrayOfObject = new Object[2];
              arrayOfObject[0] = Integer.valueOf(0);
              arrayOfObject[1] = Integer.valueOf(localBuilder.progressMax);
              localTextView.setText(String.format(str, arrayOfObject));
              ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramMaterialDialog.progressBar.getLayoutParams();
              localMarginLayoutParams.leftMargin = 0;
              localMarginLayoutParams.rightMargin = 0;
            }
          }
      while (true)
      {
        if (paramMaterialDialog.progressBar == null)
          break label502;
        fixCanvasScalingWhenHardwareAccelerated(paramMaterialDialog.progressBar);
        return;
        IndeterminateCircularProgressDrawable localIndeterminateCircularProgressDrawable = new IndeterminateCircularProgressDrawable(localBuilder.getContext());
        localIndeterminateCircularProgressDrawable.setTint(localBuilder.widgetColor);
        paramMaterialDialog.progressBar.setProgressDrawable(localIndeterminateCircularProgressDrawable);
        paramMaterialDialog.progressBar.setIndeterminateDrawable(localIndeterminateCircularProgressDrawable);
        break;
        HorizontalProgressDrawable localHorizontalProgressDrawable = new HorizontalProgressDrawable(localBuilder.getContext());
        localHorizontalProgressDrawable.setTint(localBuilder.widgetColor);
        paramMaterialDialog.progressBar.setProgressDrawable(localHorizontalProgressDrawable);
        paramMaterialDialog.progressBar.setIndeterminateDrawable(localHorizontalProgressDrawable);
        break;
        MDTintHelper.setTint(paramMaterialDialog.progressBar, localBuilder.widgetColor);
        break;
        bool = false;
        break label143;
        paramMaterialDialog.progressMinMax.setVisibility(8);
        continue;
        localBuilder.showMinMax = false;
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.DialogInit
 * JD-Core Version:    0.6.0
 */