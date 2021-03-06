package android.support.v7.widget;

import android.support.annotation.Nullable;
import android.support.v4.os.TraceCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

final class GapWorker
  implements Runnable
{
  static final ThreadLocal<GapWorker> sGapWorker = new ThreadLocal();
  static Comparator<Task> sTaskComparator = new Comparator()
  {
    public int compare(GapWorker.Task paramTask1, GapWorker.Task paramTask2)
    {
      int i = -1;
      int j;
      if (paramTask1.view == null)
      {
        j = 1;
        if (paramTask2.view != null)
          break label44;
      }
      label44: for (int k = 1; ; k = 0)
      {
        if (j == k)
          break label52;
        if (paramTask1.view != null)
          break label50;
        return 1;
        j = 0;
        break;
      }
      label50: return i;
      label52: if (paramTask1.immediate != paramTask2.immediate)
      {
        if (paramTask1.immediate);
        while (true)
        {
          return i;
          i = 1;
        }
      }
      int m = paramTask2.viewVelocity - paramTask1.viewVelocity;
      if (m != 0)
        return m;
      int n = paramTask1.distanceToItem - paramTask2.distanceToItem;
      if (n != 0)
        return n;
      return 0;
    }
  };
  long mFrameIntervalNs;
  long mPostTimeNs;
  ArrayList<RecyclerView> mRecyclerViews = new ArrayList();
  private ArrayList<Task> mTasks = new ArrayList();

  private void buildTaskList()
  {
    int i = this.mRecyclerViews.size();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      RecyclerView localRecyclerView2 = (RecyclerView)this.mRecyclerViews.get(k);
      if (localRecyclerView2.getWindowVisibility() != 0)
        continue;
      localRecyclerView2.mPrefetchRegistry.collectPrefetchPositionsFromView(localRecyclerView2, false);
      j += localRecyclerView2.mPrefetchRegistry.mCount;
    }
    this.mTasks.ensureCapacity(j);
    int m = 0;
    int n = 0;
    while (n < i)
    {
      RecyclerView localRecyclerView1 = (RecyclerView)this.mRecyclerViews.get(n);
      if (localRecyclerView1.getWindowVisibility() != 0)
      {
        n++;
        continue;
      }
      LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl = localRecyclerView1.mPrefetchRegistry;
      int i1 = Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDx) + Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDy);
      int i2 = 0;
      label143: Task localTask;
      label186: int i3;
      if (i2 < 2 * localLayoutPrefetchRegistryImpl.mCount)
      {
        if (m < this.mTasks.size())
          break label258;
        localTask = new Task();
        this.mTasks.add(localTask);
        i3 = localLayoutPrefetchRegistryImpl.mPrefetchArray[(i2 + 1)];
        if (i3 > i1)
          break label275;
      }
      label258: label275: for (boolean bool = true; ; bool = false)
      {
        localTask.immediate = bool;
        localTask.viewVelocity = i1;
        localTask.distanceToItem = i3;
        localTask.view = localRecyclerView1;
        localTask.position = localLayoutPrefetchRegistryImpl.mPrefetchArray[i2];
        m++;
        i2 += 2;
        break label143;
        break;
        localTask = (Task)this.mTasks.get(m);
        break label186;
      }
    }
    Collections.sort(this.mTasks, sTaskComparator);
  }

  private void flushTaskWithDeadline(Task paramTask, long paramLong)
  {
    long l;
    if (paramTask.immediate)
      l = 9223372036854775807L;
    while (true)
    {
      RecyclerView.ViewHolder localViewHolder = prefetchPositionWithDeadline(paramTask.view, paramTask.position, l);
      if ((localViewHolder != null) && (localViewHolder.mNestedRecyclerView != null) && (localViewHolder.isBound()) && (!localViewHolder.isInvalid()))
        prefetchInnerRecyclerViewWithDeadline((RecyclerView)localViewHolder.mNestedRecyclerView.get(), paramLong);
      return;
      l = paramLong;
    }
  }

  private void flushTasksWithDeadline(long paramLong)
  {
    for (int i = 0; ; i++)
    {
      Task localTask;
      if (i < this.mTasks.size())
      {
        localTask = (Task)this.mTasks.get(i);
        if (localTask.view != null);
      }
      else
      {
        return;
      }
      flushTaskWithDeadline(localTask, paramLong);
      localTask.clear();
    }
  }

  static boolean isPrefetchPositionAttached(RecyclerView paramRecyclerView, int paramInt)
  {
    int i = paramRecyclerView.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramRecyclerView.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder.mPosition == paramInt) && (!localViewHolder.isInvalid()))
        return true;
    }
    return false;
  }

  private void prefetchInnerRecyclerViewWithDeadline(@Nullable RecyclerView paramRecyclerView, long paramLong)
  {
    if (paramRecyclerView == null);
    LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl;
    do
    {
      return;
      if ((paramRecyclerView.mDataSetHasChangedAfterLayout) && (paramRecyclerView.mChildHelper.getUnfilteredChildCount() != 0))
        paramRecyclerView.removeAndRecycleViews();
      localLayoutPrefetchRegistryImpl = paramRecyclerView.mPrefetchRegistry;
      localLayoutPrefetchRegistryImpl.collectPrefetchPositionsFromView(paramRecyclerView, true);
    }
    while (localLayoutPrefetchRegistryImpl.mCount == 0);
    try
    {
      TraceCompat.beginSection("RV Nested Prefetch");
      paramRecyclerView.mState.prepareForNestedPrefetch(paramRecyclerView.mAdapter);
      for (int i = 0; i < 2 * localLayoutPrefetchRegistryImpl.mCount; i += 2)
        prefetchPositionWithDeadline(paramRecyclerView, localLayoutPrefetchRegistryImpl.mPrefetchArray[i], paramLong);
      return;
    }
    finally
    {
      TraceCompat.endSection();
    }
    throw localObject;
  }

  private RecyclerView.ViewHolder prefetchPositionWithDeadline(RecyclerView paramRecyclerView, int paramInt, long paramLong)
  {
    if (isPrefetchPositionAttached(paramRecyclerView, paramInt))
      return null;
    RecyclerView.Recycler localRecycler = paramRecyclerView.mRecycler;
    try
    {
      paramRecyclerView.onEnterLayoutOrScroll();
      RecyclerView.ViewHolder localViewHolder = localRecycler.tryGetViewHolderForPositionByDeadline(paramInt, false, paramLong);
      if (localViewHolder != null)
      {
        if ((!localViewHolder.isBound()) || (localViewHolder.isInvalid()))
          break label69;
        localRecycler.recycleView(localViewHolder.itemView);
      }
      while (true)
      {
        return localViewHolder;
        label69: localRecycler.addViewHolderToRecycledViewPool(localViewHolder, false);
      }
    }
    finally
    {
      paramRecyclerView.onExitLayoutOrScroll(false);
    }
    throw localObject;
  }

  public void add(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.add(paramRecyclerView);
  }

  void postFromTraversal(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    if ((paramRecyclerView.isAttachedToWindow()) && (this.mPostTimeNs == 0L))
    {
      this.mPostTimeNs = paramRecyclerView.getNanoTime();
      paramRecyclerView.post(this);
    }
    paramRecyclerView.mPrefetchRegistry.setPrefetchVector(paramInt1, paramInt2);
  }

  void prefetch(long paramLong)
  {
    buildTaskList();
    flushTasksWithDeadline(paramLong);
  }

  public void remove(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.remove(paramRecyclerView);
  }

  public void run()
  {
    try
    {
      TraceCompat.beginSection("RV Prefetch");
      boolean bool = this.mRecyclerViews.isEmpty();
      if (bool)
        return;
      int i = this.mRecyclerViews.size();
      long l1 = 0L;
      for (int j = 0; j < i; j++)
      {
        RecyclerView localRecyclerView = (RecyclerView)this.mRecyclerViews.get(j);
        if (localRecyclerView.getWindowVisibility() != 0)
          continue;
        long l2 = Math.max(localRecyclerView.getDrawingTime(), l1);
        l1 = l2;
      }
      if (l1 == 0L)
        return;
      prefetch(TimeUnit.MILLISECONDS.toNanos(l1) + this.mFrameIntervalNs);
      return;
    }
    finally
    {
      this.mPostTimeNs = 0L;
      TraceCompat.endSection();
    }
    throw localObject;
  }

  static class LayoutPrefetchRegistryImpl
    implements RecyclerView.LayoutManager.LayoutPrefetchRegistry
  {
    int mCount;
    int[] mPrefetchArray;
    int mPrefetchDx;
    int mPrefetchDy;

    public void addPosition(int paramInt1, int paramInt2)
    {
      if (paramInt1 < 0)
        throw new IllegalArgumentException("Layout positions must be non-negative");
      if (paramInt2 < 0)
        throw new IllegalArgumentException("Pixel distance must be non-negative");
      int i = 2 * this.mCount;
      if (this.mPrefetchArray == null)
      {
        this.mPrefetchArray = new int[4];
        Arrays.fill(this.mPrefetchArray, -1);
      }
      while (true)
      {
        this.mPrefetchArray[i] = paramInt1;
        this.mPrefetchArray[(i + 1)] = paramInt2;
        this.mCount = (1 + this.mCount);
        return;
        if (i < this.mPrefetchArray.length)
          continue;
        int[] arrayOfInt = this.mPrefetchArray;
        this.mPrefetchArray = new int[i * 2];
        System.arraycopy(arrayOfInt, 0, this.mPrefetchArray, 0, arrayOfInt.length);
      }
    }

    void clearPrefetchPositions()
    {
      if (this.mPrefetchArray != null)
        Arrays.fill(this.mPrefetchArray, -1);
      this.mCount = 0;
    }

    void collectPrefetchPositionsFromView(RecyclerView paramRecyclerView, boolean paramBoolean)
    {
      this.mCount = 0;
      if (this.mPrefetchArray != null)
        Arrays.fill(this.mPrefetchArray, -1);
      RecyclerView.LayoutManager localLayoutManager = paramRecyclerView.mLayout;
      if ((paramRecyclerView.mAdapter != null) && (localLayoutManager != null) && (localLayoutManager.isItemPrefetchEnabled()))
      {
        if (!paramBoolean)
          break label101;
        if (!paramRecyclerView.mAdapterHelper.hasPendingUpdates())
          localLayoutManager.collectInitialPrefetchPositions(paramRecyclerView.mAdapter.getItemCount(), this);
      }
      while (true)
      {
        if (this.mCount > localLayoutManager.mPrefetchMaxCountObserved)
        {
          localLayoutManager.mPrefetchMaxCountObserved = this.mCount;
          localLayoutManager.mPrefetchMaxObservedInInitialPrefetch = paramBoolean;
          paramRecyclerView.mRecycler.updateViewCacheSize();
        }
        return;
        label101: if (paramRecyclerView.hasPendingAdapterUpdates())
          continue;
        localLayoutManager.collectAdjacentPrefetchPositions(this.mPrefetchDx, this.mPrefetchDy, paramRecyclerView.mState, this);
      }
    }

    boolean lastPrefetchIncludedPosition(int paramInt)
    {
      if (this.mPrefetchArray != null)
      {
        int i = 2 * this.mCount;
        for (int j = 0; j < i; j += 2)
          if (this.mPrefetchArray[j] == paramInt)
            return true;
      }
      return false;
    }

    void setPrefetchVector(int paramInt1, int paramInt2)
    {
      this.mPrefetchDx = paramInt1;
      this.mPrefetchDy = paramInt2;
    }
  }

  static class Task
  {
    public int distanceToItem;
    public boolean immediate;
    public int position;
    public RecyclerView view;
    public int viewVelocity;

    public void clear()
    {
      this.immediate = false;
      this.viewVelocity = 0;
      this.distanceToItem = 0;
      this.view = null;
      this.position = 0;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.GapWorker
 * JD-Core Version:    0.6.0
 */