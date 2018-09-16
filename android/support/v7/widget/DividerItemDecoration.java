package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration
{
  private static final int[] ATTRS = { 16843284 };
  public static final int HORIZONTAL = 0;
  private static final String TAG = "DividerItem";
  public static final int VERTICAL = 1;
  private final Rect mBounds = new Rect();
  private Drawable mDivider;
  private int mOrientation;

  public DividerItemDecoration(Context paramContext, int paramInt)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(ATTRS);
    this.mDivider = localTypedArray.getDrawable(0);
    if (this.mDivider == null)
      Log.w("DividerItem", "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDrawable()");
    localTypedArray.recycle();
    setOrientation(paramInt);
  }

  private void drawHorizontal(Canvas paramCanvas, RecyclerView paramRecyclerView)
  {
    paramCanvas.save();
    int j;
    int i;
    if (paramRecyclerView.getClipToPadding())
    {
      j = paramRecyclerView.getPaddingTop();
      i = paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom();
      paramCanvas.clipRect(paramRecyclerView.getPaddingLeft(), j, paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight(), i);
    }
    while (true)
    {
      int k = paramRecyclerView.getChildCount();
      for (int m = 0; m < k; m++)
      {
        View localView = paramRecyclerView.getChildAt(m);
        paramRecyclerView.getLayoutManager().getDecoratedBoundsWithMargins(localView, this.mBounds);
        int n = this.mBounds.right + Math.round(localView.getTranslationX());
        int i1 = n - this.mDivider.getIntrinsicWidth();
        this.mDivider.setBounds(i1, j, n, i);
        this.mDivider.draw(paramCanvas);
      }
      i = paramRecyclerView.getHeight();
      j = 0;
    }
    paramCanvas.restore();
  }

  private void drawVertical(Canvas paramCanvas, RecyclerView paramRecyclerView)
  {
    paramCanvas.save();
    int j;
    int i;
    if (paramRecyclerView.getClipToPadding())
    {
      j = paramRecyclerView.getPaddingLeft();
      i = paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight();
      paramCanvas.clipRect(j, paramRecyclerView.getPaddingTop(), i, paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom());
    }
    while (true)
    {
      int k = paramRecyclerView.getChildCount();
      for (int m = 0; m < k; m++)
      {
        View localView = paramRecyclerView.getChildAt(m);
        paramRecyclerView.getDecoratedBoundsWithMargins(localView, this.mBounds);
        int n = this.mBounds.bottom + Math.round(localView.getTranslationY());
        int i1 = n - this.mDivider.getIntrinsicHeight();
        this.mDivider.setBounds(j, i1, i, n);
        this.mDivider.draw(paramCanvas);
      }
      i = paramRecyclerView.getWidth();
      j = 0;
    }
    paramCanvas.restore();
  }

  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if (this.mDivider == null)
    {
      paramRect.set(0, 0, 0, 0);
      return;
    }
    if (this.mOrientation == 1)
    {
      paramRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
      return;
    }
    paramRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
  }

  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if ((paramRecyclerView.getLayoutManager() == null) || (this.mDivider == null))
      return;
    if (this.mOrientation == 1)
    {
      drawVertical(paramCanvas, paramRecyclerView);
      return;
    }
    drawHorizontal(paramCanvas, paramRecyclerView);
  }

  public void setDrawable(@NonNull Drawable paramDrawable)
  {
    if (paramDrawable == null)
      throw new IllegalArgumentException("Drawable cannot be null.");
    this.mDivider = paramDrawable;
  }

  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1))
      throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
    this.mOrientation = paramInt;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.DividerItemDecoration
 * JD-Core Version:    0.6.0
 */