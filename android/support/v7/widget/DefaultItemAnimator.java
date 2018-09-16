package android.support.v7.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewPropertyAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultItemAnimator extends SimpleItemAnimator
{
  private static final boolean DEBUG;
  private static TimeInterpolator sDefaultInterpolator;
  ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList();
  ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList();
  ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList();
  ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList();
  ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList();
  ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList();
  private ArrayList<ChangeInfo> mPendingChanges = new ArrayList();
  private ArrayList<MoveInfo> mPendingMoves = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList();
  ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList();

  private void animateRemoveImpl(RecyclerView.ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    ViewPropertyAnimator localViewPropertyAnimator = localView.animate();
    this.mRemoveAnimations.add(paramViewHolder);
    localViewPropertyAnimator.setDuration(getRemoveDuration()).alpha(0.0F).setListener(new AnimatorListenerAdapter(paramViewHolder, localViewPropertyAnimator, localView)
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        this.val$animation.setListener(null);
        this.val$view.setAlpha(1.0F);
        DefaultItemAnimator.this.dispatchRemoveFinished(this.val$holder);
        DefaultItemAnimator.this.mRemoveAnimations.remove(this.val$holder);
        DefaultItemAnimator.this.dispatchFinishedWhenDone();
      }

      public void onAnimationStart(Animator paramAnimator)
      {
        DefaultItemAnimator.this.dispatchRemoveStarting(this.val$holder);
      }
    }).start();
  }

  private void endChangeAnimation(List<ChangeInfo> paramList, RecyclerView.ViewHolder paramViewHolder)
  {
    for (int i = -1 + paramList.size(); i >= 0; i--)
    {
      ChangeInfo localChangeInfo = (ChangeInfo)paramList.get(i);
      if ((!endChangeAnimationIfNecessary(localChangeInfo, paramViewHolder)) || (localChangeInfo.oldHolder != null) || (localChangeInfo.newHolder != null))
        continue;
      paramList.remove(localChangeInfo);
    }
  }

  private void endChangeAnimationIfNecessary(ChangeInfo paramChangeInfo)
  {
    if (paramChangeInfo.oldHolder != null)
      endChangeAnimationIfNecessary(paramChangeInfo, paramChangeInfo.oldHolder);
    if (paramChangeInfo.newHolder != null)
      endChangeAnimationIfNecessary(paramChangeInfo, paramChangeInfo.newHolder);
  }

  private boolean endChangeAnimationIfNecessary(ChangeInfo paramChangeInfo, RecyclerView.ViewHolder paramViewHolder)
  {
    boolean bool = false;
    if (paramChangeInfo.newHolder == paramViewHolder)
      paramChangeInfo.newHolder = null;
    while (true)
    {
      paramViewHolder.itemView.setAlpha(1.0F);
      paramViewHolder.itemView.setTranslationX(0.0F);
      paramViewHolder.itemView.setTranslationY(0.0F);
      dispatchChangeFinished(paramViewHolder, bool);
      return true;
      if (paramChangeInfo.oldHolder != paramViewHolder)
        break;
      paramChangeInfo.oldHolder = null;
      bool = true;
    }
    return false;
  }

  private void resetAnimation(RecyclerView.ViewHolder paramViewHolder)
  {
    if (sDefaultInterpolator == null)
      sDefaultInterpolator = new ValueAnimator().getInterpolator();
    paramViewHolder.itemView.animate().setInterpolator(sDefaultInterpolator);
    endAnimation(paramViewHolder);
  }

  public boolean animateAdd(RecyclerView.ViewHolder paramViewHolder)
  {
    resetAnimation(paramViewHolder);
    paramViewHolder.itemView.setAlpha(0.0F);
    this.mPendingAdditions.add(paramViewHolder);
    return true;
  }

  void animateAddImpl(RecyclerView.ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    ViewPropertyAnimator localViewPropertyAnimator = localView.animate();
    this.mAddAnimations.add(paramViewHolder);
    localViewPropertyAnimator.alpha(1.0F).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter(paramViewHolder, localView, localViewPropertyAnimator)
    {
      public void onAnimationCancel(Animator paramAnimator)
      {
        this.val$view.setAlpha(1.0F);
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        this.val$animation.setListener(null);
        DefaultItemAnimator.this.dispatchAddFinished(this.val$holder);
        DefaultItemAnimator.this.mAddAnimations.remove(this.val$holder);
        DefaultItemAnimator.this.dispatchFinishedWhenDone();
      }

      public void onAnimationStart(Animator paramAnimator)
      {
        DefaultItemAnimator.this.dispatchAddStarting(this.val$holder);
      }
    }).start();
  }

  public boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramViewHolder1 == paramViewHolder2)
      return animateMove(paramViewHolder1, paramInt1, paramInt2, paramInt3, paramInt4);
    float f1 = paramViewHolder1.itemView.getTranslationX();
    float f2 = paramViewHolder1.itemView.getTranslationY();
    float f3 = paramViewHolder1.itemView.getAlpha();
    resetAnimation(paramViewHolder1);
    int i = (int)(paramInt3 - paramInt1 - f1);
    int j = (int)(paramInt4 - paramInt2 - f2);
    paramViewHolder1.itemView.setTranslationX(f1);
    paramViewHolder1.itemView.setTranslationY(f2);
    paramViewHolder1.itemView.setAlpha(f3);
    if (paramViewHolder2 != null)
    {
      resetAnimation(paramViewHolder2);
      paramViewHolder2.itemView.setTranslationX(-i);
      paramViewHolder2.itemView.setTranslationY(-j);
      paramViewHolder2.itemView.setAlpha(0.0F);
    }
    this.mPendingChanges.add(new ChangeInfo(paramViewHolder1, paramViewHolder2, paramInt1, paramInt2, paramInt3, paramInt4));
    return true;
  }

  void animateChangeImpl(ChangeInfo paramChangeInfo)
  {
    RecyclerView.ViewHolder localViewHolder1 = paramChangeInfo.oldHolder;
    View localView1;
    RecyclerView.ViewHolder localViewHolder2;
    if (localViewHolder1 == null)
    {
      localView1 = null;
      localViewHolder2 = paramChangeInfo.newHolder;
      if (localViewHolder2 == null)
        break label187;
    }
    label187: for (View localView2 = localViewHolder2.itemView; ; localView2 = null)
    {
      if (localView1 != null)
      {
        ViewPropertyAnimator localViewPropertyAnimator2 = localView1.animate().setDuration(getChangeDuration());
        this.mChangeAnimations.add(paramChangeInfo.oldHolder);
        localViewPropertyAnimator2.translationX(paramChangeInfo.toX - paramChangeInfo.fromX);
        localViewPropertyAnimator2.translationY(paramChangeInfo.toY - paramChangeInfo.fromY);
        localViewPropertyAnimator2.alpha(0.0F).setListener(new AnimatorListenerAdapter(paramChangeInfo, localViewPropertyAnimator2, localView1)
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            this.val$oldViewAnim.setListener(null);
            this.val$view.setAlpha(1.0F);
            this.val$view.setTranslationX(0.0F);
            this.val$view.setTranslationY(0.0F);
            DefaultItemAnimator.this.dispatchChangeFinished(this.val$changeInfo.oldHolder, true);
            DefaultItemAnimator.this.mChangeAnimations.remove(this.val$changeInfo.oldHolder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
          }

          public void onAnimationStart(Animator paramAnimator)
          {
            DefaultItemAnimator.this.dispatchChangeStarting(this.val$changeInfo.oldHolder, true);
          }
        }).start();
      }
      if (localView2 != null)
      {
        ViewPropertyAnimator localViewPropertyAnimator1 = localView2.animate();
        this.mChangeAnimations.add(paramChangeInfo.newHolder);
        localViewPropertyAnimator1.translationX(0.0F).translationY(0.0F).setDuration(getChangeDuration()).alpha(1.0F).setListener(new AnimatorListenerAdapter(paramChangeInfo, localViewPropertyAnimator1, localView2)
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            this.val$newViewAnimation.setListener(null);
            this.val$newView.setAlpha(1.0F);
            this.val$newView.setTranslationX(0.0F);
            this.val$newView.setTranslationY(0.0F);
            DefaultItemAnimator.this.dispatchChangeFinished(this.val$changeInfo.newHolder, false);
            DefaultItemAnimator.this.mChangeAnimations.remove(this.val$changeInfo.newHolder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
          }

          public void onAnimationStart(Animator paramAnimator)
          {
            DefaultItemAnimator.this.dispatchChangeStarting(this.val$changeInfo.newHolder, false);
          }
        }).start();
      }
      return;
      localView1 = localViewHolder1.itemView;
      break;
    }
  }

  public boolean animateMove(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    View localView = paramViewHolder.itemView;
    int i = paramInt1 + (int)paramViewHolder.itemView.getTranslationX();
    int j = paramInt2 + (int)paramViewHolder.itemView.getTranslationY();
    resetAnimation(paramViewHolder);
    int k = paramInt3 - i;
    int m = paramInt4 - j;
    if ((k == 0) && (m == 0))
    {
      dispatchMoveFinished(paramViewHolder);
      return false;
    }
    if (k != 0)
      localView.setTranslationX(-k);
    if (m != 0)
      localView.setTranslationY(-m);
    this.mPendingMoves.add(new MoveInfo(paramViewHolder, i, j, paramInt3, paramInt4));
    return true;
  }

  void animateMoveImpl(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    View localView = paramViewHolder.itemView;
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    if (i != 0)
      localView.animate().translationX(0.0F);
    if (j != 0)
      localView.animate().translationY(0.0F);
    ViewPropertyAnimator localViewPropertyAnimator = localView.animate();
    this.mMoveAnimations.add(paramViewHolder);
    localViewPropertyAnimator.setDuration(getMoveDuration()).setListener(new AnimatorListenerAdapter(paramViewHolder, i, localView, j, localViewPropertyAnimator)
    {
      public void onAnimationCancel(Animator paramAnimator)
      {
        if (this.val$deltaX != 0)
          this.val$view.setTranslationX(0.0F);
        if (this.val$deltaY != 0)
          this.val$view.setTranslationY(0.0F);
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        this.val$animation.setListener(null);
        DefaultItemAnimator.this.dispatchMoveFinished(this.val$holder);
        DefaultItemAnimator.this.mMoveAnimations.remove(this.val$holder);
        DefaultItemAnimator.this.dispatchFinishedWhenDone();
      }

      public void onAnimationStart(Animator paramAnimator)
      {
        DefaultItemAnimator.this.dispatchMoveStarting(this.val$holder);
      }
    }).start();
  }

  public boolean animateRemove(RecyclerView.ViewHolder paramViewHolder)
  {
    resetAnimation(paramViewHolder);
    this.mPendingRemovals.add(paramViewHolder);
    return true;
  }

  public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder paramViewHolder, @NonNull List<Object> paramList)
  {
    return (!paramList.isEmpty()) || (super.canReuseUpdatedViewHolder(paramViewHolder, paramList));
  }

  void cancelAll(List<RecyclerView.ViewHolder> paramList)
  {
    for (int i = -1 + paramList.size(); i >= 0; i--)
      ((RecyclerView.ViewHolder)paramList.get(i)).itemView.animate().cancel();
  }

  void dispatchFinishedWhenDone()
  {
    if (!isRunning())
      dispatchAnimationsFinished();
  }

  public void endAnimation(RecyclerView.ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    localView.animate().cancel();
    for (int i = -1 + this.mPendingMoves.size(); i >= 0; i--)
    {
      if (((MoveInfo)this.mPendingMoves.get(i)).holder != paramViewHolder)
        continue;
      localView.setTranslationY(0.0F);
      localView.setTranslationX(0.0F);
      dispatchMoveFinished(paramViewHolder);
      this.mPendingMoves.remove(i);
    }
    endChangeAnimation(this.mPendingChanges, paramViewHolder);
    if (this.mPendingRemovals.remove(paramViewHolder))
    {
      localView.setAlpha(1.0F);
      dispatchRemoveFinished(paramViewHolder);
    }
    if (this.mPendingAdditions.remove(paramViewHolder))
    {
      localView.setAlpha(1.0F);
      dispatchAddFinished(paramViewHolder);
    }
    for (int j = -1 + this.mChangesList.size(); j >= 0; j--)
    {
      ArrayList localArrayList3 = (ArrayList)this.mChangesList.get(j);
      endChangeAnimation(localArrayList3, paramViewHolder);
      if (!localArrayList3.isEmpty())
        continue;
      this.mChangesList.remove(j);
    }
    int k = -1 + this.mMovesList.size();
    if (k >= 0)
    {
      ArrayList localArrayList2 = (ArrayList)this.mMovesList.get(k);
      for (int n = -1 + localArrayList2.size(); ; n--)
      {
        if (n >= 0)
        {
          if (((MoveInfo)localArrayList2.get(n)).holder != paramViewHolder)
            continue;
          localView.setTranslationY(0.0F);
          localView.setTranslationX(0.0F);
          dispatchMoveFinished(paramViewHolder);
          localArrayList2.remove(n);
          if (localArrayList2.isEmpty())
            this.mMovesList.remove(k);
        }
        k--;
        break;
      }
    }
    for (int m = -1 + this.mAdditionsList.size(); m >= 0; m--)
    {
      ArrayList localArrayList1 = (ArrayList)this.mAdditionsList.get(m);
      if (!localArrayList1.remove(paramViewHolder))
        continue;
      localView.setAlpha(1.0F);
      dispatchAddFinished(paramViewHolder);
      if (!localArrayList1.isEmpty())
        continue;
      this.mAdditionsList.remove(m);
    }
    if ((!this.mRemoveAnimations.remove(paramViewHolder)) || ((!this.mAddAnimations.remove(paramViewHolder)) || ((!this.mChangeAnimations.remove(paramViewHolder)) || (this.mMoveAnimations.remove(paramViewHolder)))));
    dispatchFinishedWhenDone();
  }

  public void endAnimations()
  {
    for (int i = -1 + this.mPendingMoves.size(); i >= 0; i--)
    {
      MoveInfo localMoveInfo2 = (MoveInfo)this.mPendingMoves.get(i);
      View localView2 = localMoveInfo2.holder.itemView;
      localView2.setTranslationY(0.0F);
      localView2.setTranslationX(0.0F);
      dispatchMoveFinished(localMoveInfo2.holder);
      this.mPendingMoves.remove(i);
    }
    for (int j = -1 + this.mPendingRemovals.size(); j >= 0; j--)
    {
      dispatchRemoveFinished((RecyclerView.ViewHolder)this.mPendingRemovals.get(j));
      this.mPendingRemovals.remove(j);
    }
    for (int k = -1 + this.mPendingAdditions.size(); k >= 0; k--)
    {
      RecyclerView.ViewHolder localViewHolder2 = (RecyclerView.ViewHolder)this.mPendingAdditions.get(k);
      localViewHolder2.itemView.setAlpha(1.0F);
      dispatchAddFinished(localViewHolder2);
      this.mPendingAdditions.remove(k);
    }
    for (int m = -1 + this.mPendingChanges.size(); m >= 0; m--)
      endChangeAnimationIfNecessary((ChangeInfo)this.mPendingChanges.get(m));
    this.mPendingChanges.clear();
    if (!isRunning())
      return;
    for (int n = -1 + this.mMovesList.size(); n >= 0; n--)
    {
      ArrayList localArrayList3 = (ArrayList)this.mMovesList.get(n);
      for (int i5 = -1 + localArrayList3.size(); i5 >= 0; i5--)
      {
        MoveInfo localMoveInfo1 = (MoveInfo)localArrayList3.get(i5);
        View localView1 = localMoveInfo1.holder.itemView;
        localView1.setTranslationY(0.0F);
        localView1.setTranslationX(0.0F);
        dispatchMoveFinished(localMoveInfo1.holder);
        localArrayList3.remove(i5);
        if (!localArrayList3.isEmpty())
          continue;
        this.mMovesList.remove(localArrayList3);
      }
    }
    for (int i1 = -1 + this.mAdditionsList.size(); i1 >= 0; i1--)
    {
      ArrayList localArrayList2 = (ArrayList)this.mAdditionsList.get(i1);
      for (int i4 = -1 + localArrayList2.size(); i4 >= 0; i4--)
      {
        RecyclerView.ViewHolder localViewHolder1 = (RecyclerView.ViewHolder)localArrayList2.get(i4);
        localViewHolder1.itemView.setAlpha(1.0F);
        dispatchAddFinished(localViewHolder1);
        localArrayList2.remove(i4);
        if (!localArrayList2.isEmpty())
          continue;
        this.mAdditionsList.remove(localArrayList2);
      }
    }
    for (int i2 = -1 + this.mChangesList.size(); i2 >= 0; i2--)
    {
      ArrayList localArrayList1 = (ArrayList)this.mChangesList.get(i2);
      for (int i3 = -1 + localArrayList1.size(); i3 >= 0; i3--)
      {
        endChangeAnimationIfNecessary((ChangeInfo)localArrayList1.get(i3));
        if (!localArrayList1.isEmpty())
          continue;
        this.mChangesList.remove(localArrayList1);
      }
    }
    cancelAll(this.mRemoveAnimations);
    cancelAll(this.mMoveAnimations);
    cancelAll(this.mAddAnimations);
    cancelAll(this.mChangeAnimations);
    dispatchAnimationsFinished();
  }

  public boolean isRunning()
  {
    return (!this.mPendingAdditions.isEmpty()) || (!this.mPendingChanges.isEmpty()) || (!this.mPendingMoves.isEmpty()) || (!this.mPendingRemovals.isEmpty()) || (!this.mMoveAnimations.isEmpty()) || (!this.mRemoveAnimations.isEmpty()) || (!this.mAddAnimations.isEmpty()) || (!this.mChangeAnimations.isEmpty()) || (!this.mMovesList.isEmpty()) || (!this.mAdditionsList.isEmpty()) || (!this.mChangesList.isEmpty());
  }

  public void runPendingAnimations()
  {
    int i;
    int j;
    label24: int k;
    if (!this.mPendingRemovals.isEmpty())
    {
      i = 1;
      if (this.mPendingMoves.isEmpty())
        break label72;
      j = 1;
      if (this.mPendingChanges.isEmpty())
        break label77;
      k = 1;
      label36: if (this.mPendingAdditions.isEmpty())
        break label82;
    }
    label72: label77: label82: for (int m = 1; ; m = 0)
    {
      if ((i != 0) || (j != 0) || (m != 0) || (k != 0))
        break label88;
      return;
      i = 0;
      break;
      j = 0;
      break label24;
      k = 0;
      break label36;
    }
    label88: Iterator localIterator = this.mPendingRemovals.iterator();
    while (localIterator.hasNext())
      animateRemoveImpl((RecyclerView.ViewHolder)localIterator.next());
    this.mPendingRemovals.clear();
    1 local1;
    label211: 2 local2;
    label291: ArrayList localArrayList3;
    3 local3;
    long l1;
    label366: long l2;
    label376: long l3;
    if (j != 0)
    {
      ArrayList localArrayList1 = new ArrayList();
      localArrayList1.addAll(this.mPendingMoves);
      this.mMovesList.add(localArrayList1);
      this.mPendingMoves.clear();
      local1 = new Runnable(localArrayList1)
      {
        public void run()
        {
          Iterator localIterator = this.val$moves.iterator();
          while (localIterator.hasNext())
          {
            DefaultItemAnimator.MoveInfo localMoveInfo = (DefaultItemAnimator.MoveInfo)localIterator.next();
            DefaultItemAnimator.this.animateMoveImpl(localMoveInfo.holder, localMoveInfo.fromX, localMoveInfo.fromY, localMoveInfo.toX, localMoveInfo.toY);
          }
          this.val$moves.clear();
          DefaultItemAnimator.this.mMovesList.remove(this.val$moves);
        }
      };
      if (i != 0)
        ViewCompat.postOnAnimationDelayed(((MoveInfo)localArrayList1.get(0)).holder.itemView, local1, getRemoveDuration());
    }
    else
    {
      if (k != 0)
      {
        ArrayList localArrayList2 = new ArrayList();
        localArrayList2.addAll(this.mPendingChanges);
        this.mChangesList.add(localArrayList2);
        this.mPendingChanges.clear();
        local2 = new Runnable(localArrayList2)
        {
          public void run()
          {
            Iterator localIterator = this.val$changes.iterator();
            while (localIterator.hasNext())
            {
              DefaultItemAnimator.ChangeInfo localChangeInfo = (DefaultItemAnimator.ChangeInfo)localIterator.next();
              DefaultItemAnimator.this.animateChangeImpl(localChangeInfo);
            }
            this.val$changes.clear();
            DefaultItemAnimator.this.mChangesList.remove(this.val$changes);
          }
        };
        if (i == 0)
          break label428;
        ViewCompat.postOnAnimationDelayed(((ChangeInfo)localArrayList2.get(0)).oldHolder.itemView, local2, getRemoveDuration());
      }
      if (m == 0)
        break label436;
      localArrayList3 = new ArrayList();
      localArrayList3.addAll(this.mPendingAdditions);
      this.mAdditionsList.add(localArrayList3);
      this.mPendingAdditions.clear();
      local3 = new Runnable(localArrayList3)
      {
        public void run()
        {
          Iterator localIterator = this.val$additions.iterator();
          while (localIterator.hasNext())
          {
            RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)localIterator.next();
            DefaultItemAnimator.this.animateAddImpl(localViewHolder);
          }
          this.val$additions.clear();
          DefaultItemAnimator.this.mAdditionsList.remove(this.val$additions);
        }
      };
      if ((i == 0) && (j == 0) && (k == 0))
        break label456;
      if (i == 0)
        break label438;
      l1 = getRemoveDuration();
      if (j == 0)
        break label444;
      l2 = getMoveDuration();
      if (k == 0)
        break label450;
      l3 = getChangeDuration();
    }
    while (true)
    {
      long l4 = l1 + Math.max(l2, l3);
      ViewCompat.postOnAnimationDelayed(((RecyclerView.ViewHolder)localArrayList3.get(0)).itemView, local3, l4);
      return;
      local1.run();
      break label211;
      label428: local2.run();
      break label291;
      label436: break;
      label438: l1 = 0L;
      break label366;
      label444: l2 = 0L;
      break label376;
      label450: l3 = 0L;
    }
    label456: local3.run();
  }

  private static class ChangeInfo
  {
    public int fromX;
    public int fromY;
    public RecyclerView.ViewHolder newHolder;
    public RecyclerView.ViewHolder oldHolder;
    public int toX;
    public int toY;

    private ChangeInfo(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      this.oldHolder = paramViewHolder1;
      this.newHolder = paramViewHolder2;
    }

    ChangeInfo(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this(paramViewHolder1, paramViewHolder2);
      this.fromX = paramInt1;
      this.fromY = paramInt2;
      this.toX = paramInt3;
      this.toY = paramInt4;
    }

    public String toString()
    {
      return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
    }
  }

  private static class MoveInfo
  {
    public int fromX;
    public int fromY;
    public RecyclerView.ViewHolder holder;
    public int toX;
    public int toY;

    MoveInfo(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.holder = paramViewHolder;
      this.fromX = paramInt1;
      this.fromY = paramInt2;
      this.toX = paramInt3;
      this.toY = paramInt4;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.DefaultItemAnimator
 * JD-Core Version:    0.6.0
 */