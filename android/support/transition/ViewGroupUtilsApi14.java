package android.support.transition;

import android.animation.LayoutTransition;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RequiresApi(14)
class ViewGroupUtilsApi14
  implements ViewGroupUtilsImpl
{
  private static final int LAYOUT_TRANSITION_CHANGING = 4;
  private static final String TAG = "ViewGroupUtilsApi14";
  private static Method sCancelMethod;
  private static boolean sCancelMethodFetched;
  private static LayoutTransition sEmptyLayoutTransition;
  private static Field sLayoutSuppressedField;
  private static boolean sLayoutSuppressedFieldFetched;

  // ERROR //
  private static void cancelLayoutTransition(LayoutTransition paramLayoutTransition)
  {
    // Byte code:
    //   0: getstatic 39	android/support/transition/ViewGroupUtilsApi14:sCancelMethodFetched	Z
    //   3: ifne +28 -> 31
    //   6: ldc 41
    //   8: ldc 43
    //   10: iconst_0
    //   11: anewarray 45	java/lang/Class
    //   14: invokevirtual 49	java/lang/Class:getDeclaredMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   17: putstatic 51	android/support/transition/ViewGroupUtilsApi14:sCancelMethod	Ljava/lang/reflect/Method;
    //   20: getstatic 51	android/support/transition/ViewGroupUtilsApi14:sCancelMethod	Ljava/lang/reflect/Method;
    //   23: iconst_1
    //   24: invokevirtual 57	java/lang/reflect/Method:setAccessible	(Z)V
    //   27: iconst_1
    //   28: putstatic 39	android/support/transition/ViewGroupUtilsApi14:sCancelMethodFetched	Z
    //   31: getstatic 51	android/support/transition/ViewGroupUtilsApi14:sCancelMethod	Ljava/lang/reflect/Method;
    //   34: ifnull +15 -> 49
    //   37: getstatic 51	android/support/transition/ViewGroupUtilsApi14:sCancelMethod	Ljava/lang/reflect/Method;
    //   40: aload_0
    //   41: iconst_0
    //   42: anewarray 4	java/lang/Object
    //   45: invokevirtual 61	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   48: pop
    //   49: return
    //   50: astore 6
    //   52: ldc 16
    //   54: ldc 63
    //   56: invokestatic 69	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   59: pop
    //   60: goto -33 -> 27
    //   63: astore_3
    //   64: ldc 16
    //   66: ldc 63
    //   68: invokestatic 69	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   71: pop
    //   72: return
    //   73: astore_1
    //   74: ldc 16
    //   76: ldc 71
    //   78: invokestatic 69	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   81: pop
    //   82: return
    //
    // Exception table:
    //   from	to	target	type
    //   6	27	50	java/lang/NoSuchMethodException
    //   37	49	63	java/lang/IllegalAccessException
    //   37	49	73	java/lang/reflect/InvocationTargetException
  }

  public ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup paramViewGroup)
  {
    return ViewGroupOverlayApi14.createFrom(paramViewGroup);
  }

  public void suppressLayout(@NonNull ViewGroup paramViewGroup, boolean paramBoolean)
  {
    if (sEmptyLayoutTransition == null)
    {
      sEmptyLayoutTransition = new LayoutTransition()
      {
        public boolean isChangingLayout()
        {
          return true;
        }
      };
      sEmptyLayoutTransition.setAnimator(2, null);
      sEmptyLayoutTransition.setAnimator(0, null);
      sEmptyLayoutTransition.setAnimator(1, null);
      sEmptyLayoutTransition.setAnimator(3, null);
      sEmptyLayoutTransition.setAnimator(4, null);
    }
    if (paramBoolean)
    {
      LayoutTransition localLayoutTransition2 = paramViewGroup.getLayoutTransition();
      if (localLayoutTransition2 != null)
      {
        if (localLayoutTransition2.isRunning())
          cancelLayoutTransition(localLayoutTransition2);
        if (localLayoutTransition2 != sEmptyLayoutTransition)
          paramViewGroup.setTag(R.id.transition_layout_save, localLayoutTransition2);
      }
      paramViewGroup.setLayoutTransition(sEmptyLayoutTransition);
    }
    while (true)
    {
      return;
      paramViewGroup.setLayoutTransition(null);
      if (!sLayoutSuppressedFieldFetched);
      try
      {
        sLayoutSuppressedField = ViewGroup.class.getDeclaredField("mLayoutSuppressed");
        sLayoutSuppressedField.setAccessible(true);
        sLayoutSuppressedFieldFetched = true;
        Field localField = sLayoutSuppressedField;
        bool = false;
        if (localField == null);
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        try
        {
          boolean bool = sLayoutSuppressedField.getBoolean(paramViewGroup);
          if (bool)
            sLayoutSuppressedField.setBoolean(paramViewGroup, false);
          if (bool)
            paramViewGroup.requestLayout();
          LayoutTransition localLayoutTransition1 = (LayoutTransition)paramViewGroup.getTag(R.id.transition_layout_save);
          if (localLayoutTransition1 == null)
            continue;
          paramViewGroup.setTag(R.id.transition_layout_save, null);
          paramViewGroup.setLayoutTransition(localLayoutTransition1);
          return;
          localNoSuchFieldException = localNoSuchFieldException;
          Log.i("ViewGroupUtilsApi14", "Failed to access mLayoutSuppressed field by reflection");
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          while (true)
            Log.i("ViewGroupUtilsApi14", "Failed to get mLayoutSuppressed field by reflection");
        }
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ViewGroupUtilsApi14
 * JD-Core Version:    0.6.0
 */