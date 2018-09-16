package android.support.v4.widget;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;

public final class ListViewCompat
{
  public static boolean canScrollList(@NonNull ListView paramListView, int paramInt)
  {
    boolean bool;
    if (Build.VERSION.SDK_INT >= 19)
      bool = paramListView.canScrollList(paramInt);
    int k;
    int m;
    do
    {
      int j;
      while (true)
      {
        return bool;
        int i = paramListView.getChildCount();
        bool = false;
        if (i == 0)
          continue;
        j = paramListView.getFirstVisiblePosition();
        if (paramInt <= 0)
          break;
        int n = paramListView.getChildAt(i - 1).getBottom();
        if (j + i >= paramListView.getCount())
        {
          int i1 = paramListView.getHeight() - paramListView.getListPaddingBottom();
          bool = false;
          if (n <= i1)
            continue;
        }
        return true;
      }
      k = paramListView.getChildAt(0).getTop();
      if (j > 0)
        break;
      m = paramListView.getListPaddingTop();
      bool = false;
    }
    while (k >= m);
    return true;
  }

  public static void scrollListBy(@NonNull ListView paramListView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 19)
      paramListView.scrollListBy(paramInt);
    int i;
    View localView;
    do
    {
      do
      {
        return;
        i = paramListView.getFirstVisiblePosition();
      }
      while (i == -1);
      localView = paramListView.getChildAt(0);
    }
    while (localView == null);
    paramListView.setSelectionFromTop(i, localView.getTop() - paramInt);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.widget.ListViewCompat
 * JD-Core Version:    0.6.0
 */