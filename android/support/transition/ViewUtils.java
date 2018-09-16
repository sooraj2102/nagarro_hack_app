package android.support.transition;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.util.Property;
import android.view.View;
import java.lang.reflect.Field;

class ViewUtils
{
  static final Property<View, Rect> CLIP_BOUNDS;
  private static final ViewUtilsImpl IMPL;
  private static final String TAG = "ViewUtils";
  static final Property<View, Float> TRANSITION_ALPHA;
  private static final int VISIBILITY_MASK = 12;
  private static Field sViewFlagsField;
  private static boolean sViewFlagsFieldFetched;

  static
  {
    if (Build.VERSION.SDK_INT >= 22)
      IMPL = new ViewUtilsApi22();
    while (true)
    {
      TRANSITION_ALPHA = new Property(Float.class, "translationAlpha")
      {
        public Float get(View paramView)
        {
          return Float.valueOf(ViewUtils.getTransitionAlpha(paramView));
        }

        public void set(View paramView, Float paramFloat)
        {
          ViewUtils.setTransitionAlpha(paramView, paramFloat.floatValue());
        }
      };
      CLIP_BOUNDS = new Property(Rect.class, "clipBounds")
      {
        public Rect get(View paramView)
        {
          return ViewCompat.getClipBounds(paramView);
        }

        public void set(View paramView, Rect paramRect)
        {
          ViewCompat.setClipBounds(paramView, paramRect);
        }
      };
      return;
      if (Build.VERSION.SDK_INT >= 21)
      {
        IMPL = new ViewUtilsApi21();
        continue;
      }
      if (Build.VERSION.SDK_INT >= 19)
      {
        IMPL = new ViewUtilsApi19();
        continue;
      }
      if (Build.VERSION.SDK_INT >= 18)
      {
        IMPL = new ViewUtilsApi18();
        continue;
      }
      IMPL = new ViewUtilsApi14();
    }
  }

  static void clearNonTransitionAlpha(@NonNull View paramView)
  {
    IMPL.clearNonTransitionAlpha(paramView);
  }

  private static void fetchViewFlagsField()
  {
    if (!sViewFlagsFieldFetched);
    try
    {
      sViewFlagsField = View.class.getDeclaredField("mViewFlags");
      sViewFlagsField.setAccessible(true);
      sViewFlagsFieldFetched = true;
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      while (true)
        Log.i("ViewUtils", "fetchViewFlagsField: ");
    }
  }

  static ViewOverlayImpl getOverlay(@NonNull View paramView)
  {
    return IMPL.getOverlay(paramView);
  }

  static float getTransitionAlpha(@NonNull View paramView)
  {
    return IMPL.getTransitionAlpha(paramView);
  }

  static WindowIdImpl getWindowId(@NonNull View paramView)
  {
    return IMPL.getWindowId(paramView);
  }

  static void saveNonTransitionAlpha(@NonNull View paramView)
  {
    IMPL.saveNonTransitionAlpha(paramView);
  }

  static void setAnimationMatrix(@NonNull View paramView, @Nullable Matrix paramMatrix)
  {
    IMPL.setAnimationMatrix(paramView, paramMatrix);
  }

  static void setLeftTopRightBottom(@NonNull View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    IMPL.setLeftTopRightBottom(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  static void setTransitionAlpha(@NonNull View paramView, float paramFloat)
  {
    IMPL.setTransitionAlpha(paramView, paramFloat);
  }

  static void setTransitionVisibility(@NonNull View paramView, int paramInt)
  {
    fetchViewFlagsField();
    if (sViewFlagsField != null);
    try
    {
      int i = sViewFlagsField.getInt(paramView);
      sViewFlagsField.setInt(paramView, paramInt | i & 0xFFFFFFF3);
      return;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
    }
  }

  static void transformMatrixToGlobal(@NonNull View paramView, @NonNull Matrix paramMatrix)
  {
    IMPL.transformMatrixToGlobal(paramView, paramMatrix);
  }

  static void transformMatrixToLocal(@NonNull View paramView, @NonNull Matrix paramMatrix)
  {
    IMPL.transformMatrixToLocal(paramView, paramMatrix);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewUtils
 * JD-Core Version:    0.6.0
 */