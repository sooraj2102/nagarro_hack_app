package android.support.transition;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;

@SuppressLint({"ViewConstructor"})
@RequiresApi(14)
class GhostViewApi14 extends View
  implements GhostViewImpl
{
  Matrix mCurrentMatrix;
  private int mDeltaX;
  private int mDeltaY;
  private final Matrix mMatrix = new Matrix();
  private final ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      GhostViewApi14.this.mCurrentMatrix = GhostViewApi14.this.mView.getMatrix();
      ViewCompat.postInvalidateOnAnimation(GhostViewApi14.this);
      if ((GhostViewApi14.this.mStartParent != null) && (GhostViewApi14.this.mStartView != null))
      {
        GhostViewApi14.this.mStartParent.endViewTransition(GhostViewApi14.this.mStartView);
        ViewCompat.postInvalidateOnAnimation(GhostViewApi14.this.mStartParent);
        GhostViewApi14.this.mStartParent = null;
        GhostViewApi14.this.mStartView = null;
      }
      return true;
    }
  };
  int mReferences;
  ViewGroup mStartParent;
  View mStartView;
  final View mView;

  GhostViewApi14(View paramView)
  {
    super(paramView.getContext());
    this.mView = paramView;
    setLayerType(2, null);
  }

  static GhostViewApi14 getGhostView(@NonNull View paramView)
  {
    return (GhostViewApi14)paramView.getTag(R.id.ghost_view);
  }

  private static void setGhostView(@NonNull View paramView, GhostViewApi14 paramGhostViewApi14)
  {
    paramView.setTag(R.id.ghost_view, paramGhostViewApi14);
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    setGhostView(this.mView, this);
    int[] arrayOfInt1 = new int[2];
    int[] arrayOfInt2 = new int[2];
    getLocationOnScreen(arrayOfInt1);
    this.mView.getLocationOnScreen(arrayOfInt2);
    arrayOfInt2[0] = (int)(arrayOfInt2[0] - this.mView.getTranslationX());
    arrayOfInt2[1] = (int)(arrayOfInt2[1] - this.mView.getTranslationY());
    this.mDeltaX = (arrayOfInt2[0] - arrayOfInt1[0]);
    this.mDeltaY = (arrayOfInt2[1] - arrayOfInt1[1]);
    this.mView.getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    this.mView.setVisibility(4);
  }

  protected void onDetachedFromWindow()
  {
    this.mView.getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
    this.mView.setVisibility(0);
    setGhostView(this.mView, null);
    super.onDetachedFromWindow();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    this.mMatrix.set(this.mCurrentMatrix);
    this.mMatrix.postTranslate(this.mDeltaX, this.mDeltaY);
    paramCanvas.setMatrix(this.mMatrix);
    this.mView.draw(paramCanvas);
  }

  public void reserveEndViewTransition(ViewGroup paramViewGroup, View paramView)
  {
    this.mStartParent = paramViewGroup;
    this.mStartView = paramView;
  }

  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    View localView = this.mView;
    if (paramInt == 0);
    for (int i = 4; ; i = 0)
    {
      localView.setVisibility(i);
      return;
    }
  }

  static class Creator
    implements GhostViewImpl.Creator
  {
    private static FrameLayout findFrameLayout(ViewGroup paramViewGroup)
    {
      while (!(paramViewGroup instanceof FrameLayout))
      {
        ViewParent localViewParent = paramViewGroup.getParent();
        if (!(localViewParent instanceof ViewGroup))
          return null;
        paramViewGroup = (ViewGroup)localViewParent;
      }
      return (FrameLayout)paramViewGroup;
    }

    public GhostViewImpl addGhost(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix)
    {
      GhostViewApi14 localGhostViewApi14 = GhostViewApi14.getGhostView(paramView);
      if (localGhostViewApi14 == null)
      {
        FrameLayout localFrameLayout = findFrameLayout(paramViewGroup);
        if (localFrameLayout == null)
          return null;
        localGhostViewApi14 = new GhostViewApi14(paramView);
        localFrameLayout.addView(localGhostViewApi14);
      }
      localGhostViewApi14.mReferences = (1 + localGhostViewApi14.mReferences);
      return localGhostViewApi14;
    }

    public void removeGhost(View paramView)
    {
      GhostViewApi14 localGhostViewApi14 = GhostViewApi14.getGhostView(paramView);
      if (localGhostViewApi14 != null)
      {
        localGhostViewApi14.mReferences = (-1 + localGhostViewApi14.mReferences);
        if (localGhostViewApi14.mReferences <= 0)
        {
          ViewParent localViewParent = localGhostViewApi14.getParent();
          if ((localViewParent instanceof ViewGroup))
          {
            ViewGroup localViewGroup = (ViewGroup)localViewParent;
            localViewGroup.endViewTransition(localGhostViewApi14);
            localViewGroup.removeView(localGhostViewApi14);
          }
        }
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.GhostViewApi14
 * JD-Core Version:    0.6.0
 */