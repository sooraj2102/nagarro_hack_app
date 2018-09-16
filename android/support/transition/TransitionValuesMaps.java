package android.support.transition;

import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;

@RequiresApi(14)
class TransitionValuesMaps
{
  final SparseArray<View> mIdValues = new SparseArray();
  final LongSparseArray<View> mItemIdValues = new LongSparseArray();
  final ArrayMap<String, View> mNameValues = new ArrayMap();
  final ArrayMap<View, TransitionValues> mViewValues = new ArrayMap();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.TransitionValuesMaps
 * JD-Core Version:    0.6.0
 */