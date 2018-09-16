package android.support.v7.widget.helper;

import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.recyclerview.R.id;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class ItemTouchUIUtilImpl
{
  static class Api21Impl extends ItemTouchUIUtilImpl.BaseImpl
  {
    private float findMaxElevation(RecyclerView paramRecyclerView, View paramView)
    {
      int i = paramRecyclerView.getChildCount();
      float f1 = 0.0F;
      int j = 0;
      if (j < i)
      {
        View localView = paramRecyclerView.getChildAt(j);
        if (localView == paramView);
        while (true)
        {
          j++;
          break;
          float f2 = ViewCompat.getElevation(localView);
          if (f2 <= f1)
            continue;
          f1 = f2;
        }
      }
      return f1;
    }

    public void clearView(View paramView)
    {
      Object localObject = paramView.getTag(R.id.item_touch_helper_previous_elevation);
      if ((localObject != null) && ((localObject instanceof Float)))
        ViewCompat.setElevation(paramView, ((Float)localObject).floatValue());
      paramView.setTag(R.id.item_touch_helper_previous_elevation, null);
      super.clearView(paramView);
    }

    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if ((paramBoolean) && (paramView.getTag(R.id.item_touch_helper_previous_elevation) == null))
      {
        Float localFloat = Float.valueOf(ViewCompat.getElevation(paramView));
        ViewCompat.setElevation(paramView, 1.0F + findMaxElevation(paramRecyclerView, paramView));
        paramView.setTag(R.id.item_touch_helper_previous_elevation, localFloat);
      }
      super.onDraw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
  }

  static class BaseImpl
    implements ItemTouchUIUtil
  {
    public void clearView(View paramView)
    {
      paramView.setTranslationX(0.0F);
      paramView.setTranslationY(0.0F);
    }

    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      paramView.setTranslationX(paramFloat1);
      paramView.setTranslationY(paramFloat2);
    }

    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
    }

    public void onSelected(View paramView)
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.helper.ItemTouchUIUtilImpl
 * JD-Core Version:    0.6.0
 */