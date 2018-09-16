package dubeyanurag.com.github.healthyrestaurant.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.NestedScrollView.OnScrollChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Property;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dubeyanurag.com.github.healthyrestaurant.adapters.OptionsAdapter;
import dubeyanurag.com.github.healthyrestaurant.interfaces.APIEndPoints;
import dubeyanurag.com.github.healthyrestaurant.models.MenuRequest;
import dubeyanurag.com.github.healthyrestaurant.models.MenuResponseModel;
import dubeyanurag.com.github.healthyrestaurant.networking.ServiceGenerator;
import dubeyanurag.com.github.healthyrestaurant.utils.AnimUtils;
import dubeyanurag.com.github.healthyrestaurant.utils.DbHandler;
import dubeyanurag.com.github.healthyrestaurant.utils.FabAnimationUtils;
import dubeyanurag.com.github.healthyrestaurant.utils.Utils;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
  OptionsAdapter adapter;

  @BindView(2131296310)
  ViewGroup confirmSaveContainer;
  List<MenuResponseModel> data;

  @BindView(2131296341)
  FloatingActionButton fab;
  String lat = "28.6738226";
  String lon = "77.4415473";

  @BindView(2131296419)
  LinearLayout progressLayout;

  @BindView(2131296427)
  RecyclerView recyclerView;

  @BindView(2131296431)
  View resultsScrim;

  @BindView(2131296412)
  RadioGroup rg;

  @BindView(2131296436)
  Button saveConfirmed;

  @BindView(2131296444)
  NestedScrollView scrollView;

  @BindView(2131296411)
  AppCompatSpinner spinner;

  @BindView(2131296308)
  TextView tv;

  @RequiresApi(api=24)
  private void applyFilter()
  {
    switch (this.spinner.getSelectedItemPosition())
    {
    default:
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    }
    while (true)
    {
      if (this.rg.getCheckedRadioButtonId() == 2131296331)
        Collections.reverse(this.data);
      return;
      bubbleSortSaturated(this.data);
      setupRecyclerview(this.data);
      continue;
      bubbleSortCarbohydrates(this.data);
      setupRecyclerview(this.data);
      continue;
      bubbleSortCalore(this.data);
      setupRecyclerview(this.data);
      continue;
      bubbleSortFibre(this.data);
      setupRecyclerview(this.data);
      continue;
      bubbleSortChoelstrol(this.data);
      setupRecyclerview(this.data);
      continue;
      bubbleSortProtein(this.data);
      setupRecyclerview(this.data);
      continue;
      bubbleSortSugar(this.data);
      setupRecyclerview(this.data);
    }
  }

  private void hide()
  {
    if (this.confirmSaveContainer.getVisibility() == 0)
      hideFilterContainer();
  }

  private void hideFilterContainer()
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    Animator[] arrayOfAnimator = new Animator[2];
    arrayOfAnimator[0] = ViewAnimationUtils.createCircularReveal(this.confirmSaveContainer, this.confirmSaveContainer.getWidth() / 2, this.confirmSaveContainer.getHeight() / 2, this.confirmSaveContainer.getWidth() / 2, this.fab.getWidth() / 2);
    arrayOfAnimator[1] = ObjectAnimator.ofArgb(this.resultsScrim, Utils.BACKGROUND_COLOR, new int[] { 0 });
    localAnimatorSet.playTogether(arrayOfAnimator);
    localAnimatorSet.setDuration(150L);
    localAnimatorSet.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
    localAnimatorSet.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        MainActivity.this.confirmSaveContainer.setVisibility(8);
        MainActivity.this.resultsScrim.setVisibility(8);
        MainActivity.this.fab.setVisibility(0);
        FabAnimationUtils.scaleIn(MainActivity.this.fab);
      }
    });
    localAnimatorSet.start();
  }

  @RequiresApi(api=24)
  private void saveAndhide()
  {
    if (this.confirmSaveContainer.getVisibility() == 0)
    {
      applyFilter();
      hideFilterContainer();
    }
  }

  private void setupFilter()
  {
    this.fab.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        MainActivity.this.show();
      }
    });
    this.resultsScrim.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        MainActivity.this.hide();
      }
    });
    this.saveConfirmed.setOnClickListener(new View.OnClickListener()
    {
      @RequiresApi(api=24)
      public void onClick(View paramView)
      {
        MainActivity.this.saveAndhide();
      }
    });
    this.scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
    {
      public void onScrollChange(NestedScrollView paramNestedScrollView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        if ((paramInt2 > paramInt4) && (MainActivity.this.fab.getVisibility() == 0))
          FabAnimationUtils.scaleOut(MainActivity.this.fab);
        if ((paramInt2 < paramInt4) && (MainActivity.this.fab.getVisibility() != 0))
        {
          MainActivity.this.fab.setVisibility(0);
          FabAnimationUtils.scaleIn(MainActivity.this.fab);
        }
        if ((paramNestedScrollView.getChildAt(-1 + paramNestedScrollView.getChildCount()) != null) && (paramInt2 >= paramNestedScrollView.getChildAt(-1 + paramNestedScrollView.getChildCount()).getMeasuredHeight() - paramNestedScrollView.getMeasuredHeight()) && (paramInt2 > paramInt4));
      }
    });
  }

  private void setupRecyclerview(List<MenuResponseModel> paramList)
  {
    this.data = paramList;
    LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(this);
    this.recyclerView.setLayoutManager(localLinearLayoutManager);
    this.adapter = new OptionsAdapter(this);
    this.adapter.setData(paramList);
    this.recyclerView.setAdapter(this.adapter);
    this.recyclerView.setNestedScrollingEnabled(false);
  }

  private void show()
  {
    FabAnimationUtils.scaleOut(this.fab);
    this.fab.setVisibility(4);
    this.confirmSaveContainer.setVisibility(0);
    this.resultsScrim.setVisibility(0);
    this.confirmSaveContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        MainActivity.this.confirmSaveContainer.getViewTreeObserver().removeOnPreDrawListener(this);
        Animator localAnimator1 = ViewAnimationUtils.createCircularReveal(MainActivity.this.confirmSaveContainer, MainActivity.this.confirmSaveContainer.getWidth() / 2, MainActivity.this.confirmSaveContainer.getHeight() / 2, MainActivity.this.fab.getWidth() / 2, MainActivity.this.confirmSaveContainer.getWidth() / 2);
        localAnimator1.setDuration(250L);
        localAnimator1.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(MainActivity.this));
        localAnimator1.start();
        int i = (MainActivity.this.fab.getLeft() + MainActivity.this.fab.getRight()) / 2;
        int j = (MainActivity.this.fab.getTop() + MainActivity.this.fab.getBottom()) / 2;
        Animator localAnimator2 = ViewAnimationUtils.createCircularReveal(MainActivity.this.resultsScrim, i, j, 0.0F, (float)Math.hypot(i, j));
        localAnimator2.setDuration(400L);
        localAnimator2.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(MainActivity.this));
        localAnimator2.start();
        View localView = MainActivity.this.resultsScrim;
        Property localProperty = Utils.BACKGROUND_COLOR;
        int[] arrayOfInt = new int[2];
        arrayOfInt[0] = 0;
        arrayOfInt[1] = ContextCompat.getColor(MainActivity.this, 2131099751);
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofArgb(localView, localProperty, arrayOfInt);
        localObjectAnimator.setDuration(800L);
        localObjectAnimator.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(MainActivity.this));
        localObjectAnimator.start();
        return false;
      }
    });
  }

  void bubbleSortCalore(List<MenuResponseModel> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i - 1; j++)
      for (int k = 0; k < -1 + (i - j); k++)
      {
        if (((MenuResponseModel)paramList.get(k)).getCalories().floatValue() <= ((MenuResponseModel)paramList.get(k + 1)).getCalories().floatValue())
          continue;
        MenuResponseModel localMenuResponseModel = (MenuResponseModel)paramList.get(k);
        paramList.set(k, paramList.get(k + 1));
        paramList.set(k + 1, localMenuResponseModel);
      }
  }

  void bubbleSortCarbohydrates(List<MenuResponseModel> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i - 1; j++)
      for (int k = 0; k < -1 + (i - j); k++)
      {
        if (((MenuResponseModel)paramList.get(k)).getCarbohydrate().floatValue() <= ((MenuResponseModel)paramList.get(k + 1)).getCarbohydrate().floatValue())
          continue;
        MenuResponseModel localMenuResponseModel = (MenuResponseModel)paramList.get(k);
        paramList.set(k, paramList.get(k + 1));
        paramList.set(k + 1, localMenuResponseModel);
      }
  }

  void bubbleSortChoelstrol(List<MenuResponseModel> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i - 1; j++)
      for (int k = 0; k < -1 + (i - j); k++)
      {
        if (((MenuResponseModel)paramList.get(k)).getCholesterol().floatValue() <= ((MenuResponseModel)paramList.get(k + 1)).getCholesterol().floatValue())
          continue;
        MenuResponseModel localMenuResponseModel = (MenuResponseModel)paramList.get(k);
        paramList.set(k, paramList.get(k + 1));
        paramList.set(k + 1, localMenuResponseModel);
      }
  }

  void bubbleSortFibre(List<MenuResponseModel> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i - 1; j++)
      for (int k = 0; k < -1 + (i - j); k++)
      {
        if (((MenuResponseModel)paramList.get(k)).getFiber().floatValue() <= ((MenuResponseModel)paramList.get(k + 1)).getFiber().floatValue())
          continue;
        MenuResponseModel localMenuResponseModel = (MenuResponseModel)paramList.get(k);
        paramList.set(k, paramList.get(k + 1));
        paramList.set(k + 1, localMenuResponseModel);
      }
  }

  void bubbleSortProtein(List<MenuResponseModel> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i - 1; j++)
      for (int k = 0; k < -1 + (i - j); k++)
      {
        if (((MenuResponseModel)paramList.get(k)).getProtein().floatValue() <= ((MenuResponseModel)paramList.get(k + 1)).getProtein().floatValue())
          continue;
        MenuResponseModel localMenuResponseModel = (MenuResponseModel)paramList.get(k);
        paramList.set(k, paramList.get(k + 1));
        paramList.set(k + 1, localMenuResponseModel);
      }
  }

  void bubbleSortSaturated(List<MenuResponseModel> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i - 1; j++)
      for (int k = 0; k < -1 + (i - j); k++)
      {
        if (((MenuResponseModel)paramList.get(k)).getSaturatedFat().floatValue() <= ((MenuResponseModel)paramList.get(k + 1)).getSaturatedFat().floatValue())
          continue;
        MenuResponseModel localMenuResponseModel = (MenuResponseModel)paramList.get(k);
        paramList.set(k, paramList.get(k + 1));
        paramList.set(k + 1, localMenuResponseModel);
      }
  }

  void bubbleSortSugar(List<MenuResponseModel> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i - 1; j++)
      for (int k = 0; k < -1 + (i - j); k++)
      {
        if (((MenuResponseModel)paramList.get(k)).getSugar().floatValue() <= ((MenuResponseModel)paramList.get(k + 1)).getSugar().floatValue())
          continue;
        MenuResponseModel localMenuResponseModel = (MenuResponseModel)paramList.get(k);
        paramList.set(k, paramList.get(k + 1));
        paramList.set(k + 1, localMenuResponseModel);
      }
  }

  @OnClick({2131296308})
  public void newAct()
  {
    startActivity(new Intent(this, NlpActivity.class));
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2131427355);
    ButterKnife.bind(this);
    setupFilter();
    this.tv.setText(DbHandler.getString(this, "name", "") + " your daily calorie intake goal should be " + DbHandler.getString(this, "caloriesNeeded", "1905") + "\nClick here to know more about food you ate.");
    Call localCall = ((APIEndPoints)ServiceGenerator.createService(APIEndPoints.class)).requestRestaurants(new MenuRequest(this.lat, this.lon, "100", Integer.valueOf(0)));
    this.progressLayout.setVisibility(0);
    localCall.enqueue(new Callback()
    {
      public void onFailure(Call<List<MenuResponseModel>> paramCall, Throwable paramThrowable)
      {
        MainActivity.this.progressLayout.setVisibility(8);
        Toast.makeText(MainActivity.this, "Please check connectivity", 1).show();
      }

      public void onResponse(Call<List<MenuResponseModel>> paramCall, Response<List<MenuResponseModel>> paramResponse)
      {
        List localList = (List)paramResponse.body();
        if (paramResponse.code() == 200)
        {
          MainActivity.this.progressLayout.setVisibility(8);
          if (DbHandler.getBoolean(MainActivity.this, "heart", Boolean.valueOf(false)).booleanValue())
            MainActivity.this.bubbleSortChoelstrol(localList);
          if (DbHandler.getBoolean(MainActivity.this, "diebatic", Boolean.valueOf(false)).booleanValue())
            MainActivity.this.bubbleSortCarbohydrates(localList);
          MainActivity.this.setupRecyclerview(localList);
          return;
        }
        MainActivity.this.progressLayout.setVisibility(8);
        Toast.makeText(MainActivity.this, "Please check connectivity", 1).show();
      }
    });
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.activities.MainActivity
 * JD-Core Version:    0.6.0
 */