package android.support.v7.widget;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class LinearSnapHelper extends SnapHelper
{
  private static final float INVALID_DISTANCE = 1.0F;

  @Nullable
  private OrientationHelper mHorizontalHelper;

  @Nullable
  private OrientationHelper mVerticalHelper;

  private float computeDistancePerChild(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int i = 2147483647;
    int j = -2147483648;
    int k = paramLayoutManager.getChildCount();
    if (k == 0)
      return 1.0F;
    int m = 0;
    if (m < k)
    {
      View localView = paramLayoutManager.getChildAt(m);
      int i2 = paramLayoutManager.getPosition(localView);
      if (i2 == -1);
      while (true)
      {
        m++;
        break;
        if (i2 < i)
        {
          i = i2;
          localObject1 = localView;
        }
        if (i2 <= j)
          continue;
        j = i2;
        localObject2 = localView;
      }
    }
    if ((localObject1 == null) || (localObject2 == null))
      return 1.0F;
    int n = Math.min(paramOrientationHelper.getDecoratedStart(localObject1), paramOrientationHelper.getDecoratedStart(localObject2));
    int i1 = Math.max(paramOrientationHelper.getDecoratedEnd(localObject1), paramOrientationHelper.getDecoratedEnd(localObject2)) - n;
    if (i1 == 0)
      return 1.0F;
    return 1.0F * i1 / (1 + (j - i));
  }

  private int distanceToCenter(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView, OrientationHelper paramOrientationHelper)
  {
    int i = paramOrientationHelper.getDecoratedStart(paramView) + paramOrientationHelper.getDecoratedMeasurement(paramView) / 2;
    if (paramLayoutManager.getClipToPadding());
    for (int j = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2; ; j = paramOrientationHelper.getEnd() / 2)
      return i - j;
  }

  private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = calculateScrollDistance(paramInt1, paramInt2);
    float f = computeDistancePerChild(paramLayoutManager, paramOrientationHelper);
    if (f <= 0.0F)
      return 0;
    if (Math.abs(arrayOfInt[0]) > Math.abs(arrayOfInt[1]));
    for (int i = arrayOfInt[0]; ; i = arrayOfInt[1])
      return Math.round(i / f);
  }

  @Nullable
  private View findCenterView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    int i = paramLayoutManager.getChildCount();
    if (i == 0)
    {
      localObject = null;
      return localObject;
    }
    Object localObject = null;
    if (paramLayoutManager.getClipToPadding());
    for (int j = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2; ; j = paramOrientationHelper.getEnd() / 2)
    {
      int k = 2147483647;
      for (int m = 0; m < i; m++)
      {
        View localView = paramLayoutManager.getChildAt(m);
        int n = Math.abs(paramOrientationHelper.getDecoratedStart(localView) + paramOrientationHelper.getDecoratedMeasurement(localView) / 2 - j);
        if (n >= k)
          continue;
        k = n;
        localObject = localView;
      }
      break;
    }
  }

  @NonNull
  private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mHorizontalHelper == null) || (this.mHorizontalHelper.mLayoutManager != paramLayoutManager))
      this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(paramLayoutManager);
    return this.mHorizontalHelper;
  }

  @NonNull
  private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mVerticalHelper == null) || (this.mVerticalHelper.mLayoutManager != paramLayoutManager))
      this.mVerticalHelper = OrientationHelper.createVerticalHelper(paramLayoutManager);
    return this.mVerticalHelper;
  }

  public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView)
  {
    int[] arrayOfInt = new int[2];
    if (paramLayoutManager.canScrollHorizontally())
      arrayOfInt[0] = distanceToCenter(paramLayoutManager, paramView, getHorizontalHelper(paramLayoutManager));
    while (paramLayoutManager.canScrollVertically())
    {
      arrayOfInt[1] = distanceToCenter(paramLayoutManager, paramView, getVerticalHelper(paramLayoutManager));
      return arrayOfInt;
      arrayOfInt[0] = 0;
    }
    arrayOfInt[1] = 0;
    return arrayOfInt;
  }

  public View findSnapView(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager.canScrollVertically())
      return findCenterView(paramLayoutManager, getVerticalHelper(paramLayoutManager));
    if (paramLayoutManager.canScrollHorizontally())
      return findCenterView(paramLayoutManager, getHorizontalHelper(paramLayoutManager));
    return null;
  }

  public int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    int i = -1;
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider));
    label143: label192: label198: label203: 
    while (true)
    {
      return i;
      int j = paramLayoutManager.getItemCount();
      if (j == 0)
        continue;
      View localView = findSnapView(paramLayoutManager);
      if (localView == null)
        continue;
      int k = paramLayoutManager.getPosition(localView);
      if (k == i)
        continue;
      PointF localPointF = ((RecyclerView.SmoothScroller.ScrollVectorProvider)paramLayoutManager).computeScrollVectorForPosition(j - 1);
      if (localPointF == null)
        continue;
      int m;
      label107: int n;
      if (paramLayoutManager.canScrollHorizontally())
      {
        m = estimateNextPositionDiffForFling(paramLayoutManager, getHorizontalHelper(paramLayoutManager), paramInt1, 0);
        if (localPointF.x < 0.0F)
          m = -m;
        if (!paramLayoutManager.canScrollVertically())
          break label192;
        n = estimateNextPositionDiffForFling(paramLayoutManager, getVerticalHelper(paramLayoutManager), 0, paramInt2);
        if (localPointF.y < 0.0F)
          n = -n;
        if (!paramLayoutManager.canScrollVertically())
          break label198;
      }
      for (int i1 = n; ; i1 = m)
      {
        if (i1 == 0)
          break label203;
        i = k + i1;
        if (i < 0)
          i = 0;
        if (i < j)
          break;
        return j - 1;
        m = 0;
        break label107;
        n = 0;
        break label143;
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.LinearSnapHelper
 * JD-Core Version:    0.6.0
 */