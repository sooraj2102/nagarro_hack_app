package android.support.v7.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public abstract class SnapHelper extends RecyclerView.OnFlingListener
{
  static final float MILLISECONDS_PER_INCH = 100.0F;
  private Scroller mGravityScroller;
  RecyclerView mRecyclerView;
  private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener()
  {
    boolean mScrolled = false;

    public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
    {
      super.onScrollStateChanged(paramRecyclerView, paramInt);
      if ((paramInt == 0) && (this.mScrolled))
      {
        this.mScrolled = false;
        SnapHelper.this.snapToTargetExistingView();
      }
    }

    public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
    {
      if ((paramInt1 != 0) || (paramInt2 != 0))
        this.mScrolled = true;
    }
  };

  private void destroyCallbacks()
  {
    this.mRecyclerView.removeOnScrollListener(this.mScrollListener);
    this.mRecyclerView.setOnFlingListener(null);
  }

  private void setupCallbacks()
    throws IllegalStateException
  {
    if (this.mRecyclerView.getOnFlingListener() != null)
      throw new IllegalStateException("An instance of OnFlingListener already set.");
    this.mRecyclerView.addOnScrollListener(this.mScrollListener);
    this.mRecyclerView.setOnFlingListener(this);
  }

  private boolean snapFromFling(@NonNull RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider));
    RecyclerView.SmoothScroller localSmoothScroller;
    int i;
    do
    {
      do
      {
        return false;
        localSmoothScroller = createScroller(paramLayoutManager);
      }
      while (localSmoothScroller == null);
      i = findTargetSnapPosition(paramLayoutManager, paramInt1, paramInt2);
    }
    while (i == -1);
    localSmoothScroller.setTargetPosition(i);
    paramLayoutManager.startSmoothScroll(localSmoothScroller);
    return true;
  }

  public void attachToRecyclerView(@Nullable RecyclerView paramRecyclerView)
    throws IllegalStateException
  {
    if (this.mRecyclerView == paramRecyclerView);
    do
    {
      return;
      if (this.mRecyclerView != null)
        destroyCallbacks();
      this.mRecyclerView = paramRecyclerView;
    }
    while (this.mRecyclerView == null);
    setupCallbacks();
    this.mGravityScroller = new Scroller(this.mRecyclerView.getContext(), new DecelerateInterpolator());
    snapToTargetExistingView();
  }

  @Nullable
  public abstract int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView);

  public int[] calculateScrollDistance(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = new int[2];
    this.mGravityScroller.fling(0, 0, paramInt1, paramInt2, -2147483648, 2147483647, -2147483648, 2147483647);
    arrayOfInt[0] = this.mGravityScroller.getFinalX();
    arrayOfInt[1] = this.mGravityScroller.getFinalY();
    return arrayOfInt;
  }

  @Nullable
  protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager paramLayoutManager)
  {
    return createSnapScroller(paramLayoutManager);
  }

  @Deprecated
  @Nullable
  protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider))
      return null;
    return new LinearSmoothScroller(this.mRecyclerView.getContext())
    {
      protected float calculateSpeedPerPixel(DisplayMetrics paramDisplayMetrics)
      {
        return 100.0F / paramDisplayMetrics.densityDpi;
      }

      protected void onTargetFound(View paramView, RecyclerView.State paramState, RecyclerView.SmoothScroller.Action paramAction)
      {
        int[] arrayOfInt = SnapHelper.this.calculateDistanceToFinalSnap(SnapHelper.this.mRecyclerView.getLayoutManager(), paramView);
        int i = arrayOfInt[0];
        int j = arrayOfInt[1];
        int k = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(j)));
        if (k > 0)
          paramAction.update(i, j, k, this.mDecelerateInterpolator);
      }
    };
  }

  @Nullable
  public abstract View findSnapView(RecyclerView.LayoutManager paramLayoutManager);

  public abstract int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2);

  public boolean onFling(int paramInt1, int paramInt2)
  {
    RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
    if (localLayoutManager == null);
    int i;
    do
    {
      do
        return false;
      while (this.mRecyclerView.getAdapter() == null);
      i = this.mRecyclerView.getMinFlingVelocity();
    }
    while (((Math.abs(paramInt2) <= i) && (Math.abs(paramInt1) <= i)) || (!snapFromFling(localLayoutManager, paramInt1, paramInt2)));
    return true;
  }

  void snapToTargetExistingView()
  {
    if (this.mRecyclerView == null);
    int[] arrayOfInt;
    do
    {
      RecyclerView.LayoutManager localLayoutManager;
      View localView;
      do
      {
        do
        {
          return;
          localLayoutManager = this.mRecyclerView.getLayoutManager();
        }
        while (localLayoutManager == null);
        localView = findSnapView(localLayoutManager);
      }
      while (localView == null);
      arrayOfInt = calculateDistanceToFinalSnap(localLayoutManager, localView);
    }
    while ((arrayOfInt[0] == 0) && (arrayOfInt[1] == 0));
    this.mRecyclerView.smoothScrollBy(arrayOfInt[0], arrayOfInt[1]);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.SnapHelper
 * JD-Core Version:    0.6.0
 */