package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;

public class ChangeBounds extends Transition
{
  private static final Property<View, PointF> BOTTOM_RIGHT_ONLY_PROPERTY;
  private static final Property<ViewBounds, PointF> BOTTOM_RIGHT_PROPERTY;
  private static final Property<Drawable, PointF> DRAWABLE_ORIGIN_PROPERTY;
  private static final Property<View, PointF> POSITION_PROPERTY;
  private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
  private static final String PROPNAME_CLIP = "android:changeBounds:clip";
  private static final String PROPNAME_PARENT = "android:changeBounds:parent";
  private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
  private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
  private static final Property<View, PointF> TOP_LEFT_ONLY_PROPERTY;
  private static final Property<ViewBounds, PointF> TOP_LEFT_PROPERTY;
  private static RectEvaluator sRectEvaluator;
  private static final String[] sTransitionProperties = { "android:changeBounds:bounds", "android:changeBounds:clip", "android:changeBounds:parent", "android:changeBounds:windowX", "android:changeBounds:windowY" };
  private boolean mReparent = false;
  private boolean mResizeClip = false;
  private int[] mTempLocation = new int[2];

  static
  {
    DRAWABLE_ORIGIN_PROPERTY = new Property(PointF.class, "boundsOrigin")
    {
      private Rect mBounds = new Rect();

      public PointF get(Drawable paramDrawable)
      {
        paramDrawable.copyBounds(this.mBounds);
        return new PointF(this.mBounds.left, this.mBounds.top);
      }

      public void set(Drawable paramDrawable, PointF paramPointF)
      {
        paramDrawable.copyBounds(this.mBounds);
        this.mBounds.offsetTo(Math.round(paramPointF.x), Math.round(paramPointF.y));
        paramDrawable.setBounds(this.mBounds);
      }
    };
    TOP_LEFT_PROPERTY = new Property(PointF.class, "topLeft")
    {
      public PointF get(ChangeBounds.ViewBounds paramViewBounds)
      {
        return null;
      }

      public void set(ChangeBounds.ViewBounds paramViewBounds, PointF paramPointF)
      {
        paramViewBounds.setTopLeft(paramPointF);
      }
    };
    BOTTOM_RIGHT_PROPERTY = new Property(PointF.class, "bottomRight")
    {
      public PointF get(ChangeBounds.ViewBounds paramViewBounds)
      {
        return null;
      }

      public void set(ChangeBounds.ViewBounds paramViewBounds, PointF paramPointF)
      {
        paramViewBounds.setBottomRight(paramPointF);
      }
    };
    BOTTOM_RIGHT_ONLY_PROPERTY = new Property(PointF.class, "bottomRight")
    {
      public PointF get(View paramView)
      {
        return null;
      }

      public void set(View paramView, PointF paramPointF)
      {
        ViewUtils.setLeftTopRightBottom(paramView, paramView.getLeft(), paramView.getTop(), Math.round(paramPointF.x), Math.round(paramPointF.y));
      }
    };
    TOP_LEFT_ONLY_PROPERTY = new Property(PointF.class, "topLeft")
    {
      public PointF get(View paramView)
      {
        return null;
      }

      public void set(View paramView, PointF paramPointF)
      {
        ViewUtils.setLeftTopRightBottom(paramView, Math.round(paramPointF.x), Math.round(paramPointF.y), paramView.getRight(), paramView.getBottom());
      }
    };
    POSITION_PROPERTY = new Property(PointF.class, "position")
    {
      public PointF get(View paramView)
      {
        return null;
      }

      public void set(View paramView, PointF paramPointF)
      {
        int i = Math.round(paramPointF.x);
        int j = Math.round(paramPointF.y);
        ViewUtils.setLeftTopRightBottom(paramView, i, j, i + paramView.getWidth(), j + paramView.getHeight());
      }
    };
    sRectEvaluator = new RectEvaluator();
  }

  public ChangeBounds()
  {
  }

  public ChangeBounds(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.CHANGE_BOUNDS);
    boolean bool = TypedArrayUtils.getNamedBoolean(localTypedArray, (XmlResourceParser)paramAttributeSet, "resizeClip", 0, false);
    localTypedArray.recycle();
    setResizeClip(bool);
  }

  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = paramTransitionValues.view;
    if ((ViewCompat.isLaidOut(localView)) || (localView.getWidth() != 0) || (localView.getHeight() != 0))
    {
      paramTransitionValues.values.put("android:changeBounds:bounds", new Rect(localView.getLeft(), localView.getTop(), localView.getRight(), localView.getBottom()));
      paramTransitionValues.values.put("android:changeBounds:parent", paramTransitionValues.view.getParent());
      if (this.mReparent)
      {
        paramTransitionValues.view.getLocationInWindow(this.mTempLocation);
        paramTransitionValues.values.put("android:changeBounds:windowX", Integer.valueOf(this.mTempLocation[0]));
        paramTransitionValues.values.put("android:changeBounds:windowY", Integer.valueOf(this.mTempLocation[1]));
      }
      if (this.mResizeClip)
        paramTransitionValues.values.put("android:changeBounds:clip", ViewCompat.getClipBounds(localView));
    }
  }

  private boolean parentMatches(View paramView1, View paramView2)
  {
    int i = 1;
    TransitionValues localTransitionValues;
    if (this.mReparent)
    {
      localTransitionValues = getMatchedTransitionValues(paramView1, true);
      if (localTransitionValues != null)
        break label33;
      if (paramView1 == paramView2)
        i = 1;
    }
    else
    {
      return i;
    }
    return false;
    label33: return paramView2 == localTransitionValues.view;
  }

  public void captureEndValues(@NonNull TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }

  public void captureStartValues(@NonNull TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }

  @Nullable
  public Animator createAnimator(@NonNull ViewGroup paramViewGroup, @Nullable TransitionValues paramTransitionValues1, @Nullable TransitionValues paramTransitionValues2)
  {
    Object localObject;
    if ((paramTransitionValues1 == null) || (paramTransitionValues2 == null))
      localObject = null;
    while (true)
    {
      return localObject;
      Map localMap1 = paramTransitionValues1.values;
      Map localMap2 = paramTransitionValues2.values;
      ViewGroup localViewGroup1 = (ViewGroup)localMap1.get("android:changeBounds:parent");
      ViewGroup localViewGroup2 = (ViewGroup)localMap2.get("android:changeBounds:parent");
      if ((localViewGroup1 == null) || (localViewGroup2 == null))
        return null;
      View localView = paramTransitionValues2.view;
      if (parentMatches(localViewGroup1, localViewGroup2))
      {
        Rect localRect1 = (Rect)paramTransitionValues1.values.get("android:changeBounds:bounds");
        Rect localRect2 = (Rect)paramTransitionValues2.values.get("android:changeBounds:bounds");
        int n = localRect1.left;
        int i1 = localRect2.left;
        int i2 = localRect1.top;
        int i3 = localRect2.top;
        int i4 = localRect1.right;
        int i5 = localRect2.right;
        int i6 = localRect1.bottom;
        int i7 = localRect2.bottom;
        int i8 = i4 - n;
        int i9 = i6 - i2;
        int i10 = i5 - i1;
        int i11 = i7 - i3;
        Rect localRect3 = (Rect)paramTransitionValues1.values.get("android:changeBounds:clip");
        Rect localRect4 = (Rect)paramTransitionValues2.values.get("android:changeBounds:clip");
        int i12;
        if ((i8 == 0) || (i9 == 0))
        {
          i12 = 0;
          if (i10 != 0)
          {
            i12 = 0;
            if (i11 == 0);
          }
        }
        else
        {
          if (n == i1)
          {
            i12 = 0;
            if (i2 == i3);
          }
          else
          {
            i12 = 0 + 1;
          }
          if ((i4 != i5) || (i6 != i7))
            i12++;
        }
        if (((localRect3 != null) && (!localRect3.equals(localRect4))) || ((localRect3 == null) && (localRect4 != null)))
          i12++;
        if (i12 <= 0)
          break;
        if (!this.mResizeClip)
        {
          ViewUtils.setLeftTopRightBottom(localView, n, i2, i4, i6);
          if (i12 == 2)
            if ((i8 == i10) && (i9 == i11))
            {
              Path localPath7 = getPathMotion().getPath(n, i2, i1, i3);
              localObject = ObjectAnimatorUtils.ofPointF(localView, POSITION_PROPERTY, localPath7);
            }
        }
        while ((localView.getParent() instanceof ViewGroup))
        {
          ViewGroup localViewGroup3 = (ViewGroup)localView.getParent();
          ViewGroupUtils.suppressLayout(localViewGroup3, true);
          9 local9 = new TransitionListenerAdapter(localViewGroup3)
          {
            boolean mCanceled = false;

            public void onTransitionCancel(@NonNull Transition paramTransition)
            {
              ViewGroupUtils.suppressLayout(this.val$parent, false);
              this.mCanceled = true;
            }

            public void onTransitionEnd(@NonNull Transition paramTransition)
            {
              if (!this.mCanceled)
                ViewGroupUtils.suppressLayout(this.val$parent, false);
              paramTransition.removeListener(this);
            }

            public void onTransitionPause(@NonNull Transition paramTransition)
            {
              ViewGroupUtils.suppressLayout(this.val$parent, false);
            }

            public void onTransitionResume(@NonNull Transition paramTransition)
            {
              ViewGroupUtils.suppressLayout(this.val$parent, true);
            }
          };
          addListener(local9);
          return localObject;
          ViewBounds localViewBounds = new ViewBounds(localView);
          Path localPath5 = getPathMotion().getPath(n, i2, i1, i3);
          ObjectAnimator localObjectAnimator4 = ObjectAnimatorUtils.ofPointF(localViewBounds, TOP_LEFT_PROPERTY, localPath5);
          Path localPath6 = getPathMotion().getPath(i4, i6, i5, i7);
          ObjectAnimator localObjectAnimator5 = ObjectAnimatorUtils.ofPointF(localViewBounds, BOTTOM_RIGHT_PROPERTY, localPath6);
          AnimatorSet localAnimatorSet = new AnimatorSet();
          localAnimatorSet.playTogether(new Animator[] { localObjectAnimator4, localObjectAnimator5 });
          localObject = localAnimatorSet;
          localAnimatorSet.addListener(new AnimatorListenerAdapter(localViewBounds)
          {
            private ChangeBounds.ViewBounds mViewBounds = this.val$viewBounds;
          });
          continue;
          if ((n != i1) || (i2 != i3))
          {
            Path localPath3 = getPathMotion().getPath(n, i2, i1, i3);
            localObject = ObjectAnimatorUtils.ofPointF(localView, TOP_LEFT_ONLY_PROPERTY, localPath3);
            continue;
          }
          Path localPath4 = getPathMotion().getPath(i4, i6, i5, i7);
          localObject = ObjectAnimatorUtils.ofPointF(localView, BOTTOM_RIGHT_ONLY_PROPERTY, localPath4);
          continue;
          int i13 = Math.max(i8, i10);
          int i14 = Math.max(i9, i11);
          ViewUtils.setLeftTopRightBottom(localView, n, i2, n + i13, i2 + i14);
          ObjectAnimator localObjectAnimator2;
          if (n == i1)
          {
            localObjectAnimator2 = null;
            if (i2 == i3);
          }
          else
          {
            Path localPath2 = getPathMotion().getPath(n, i2, i1, i3);
            localObjectAnimator2 = ObjectAnimatorUtils.ofPointF(localView, POSITION_PROPERTY, localPath2);
          }
          Rect localRect5 = localRect4;
          if (localRect3 == null)
            localRect3 = new Rect(0, 0, i8, i9);
          if (localRect4 == null)
            localRect4 = new Rect(0, 0, i10, i11);
          boolean bool = localRect3.equals(localRect4);
          ObjectAnimator localObjectAnimator3 = null;
          if (!bool)
          {
            ViewCompat.setClipBounds(localView, localRect3);
            localObjectAnimator3 = ObjectAnimator.ofObject(localView, "clipBounds", sRectEvaluator, new Object[] { localRect3, localRect4 });
            8 local8 = new AnimatorListenerAdapter(localView, localRect5, i1, i3, i5, i7)
            {
              private boolean mIsCanceled;

              public void onAnimationCancel(Animator paramAnimator)
              {
                this.mIsCanceled = true;
              }

              public void onAnimationEnd(Animator paramAnimator)
              {
                if (!this.mIsCanceled)
                {
                  ViewCompat.setClipBounds(this.val$view, this.val$finalClip);
                  ViewUtils.setLeftTopRightBottom(this.val$view, this.val$endLeft, this.val$endTop, this.val$endRight, this.val$endBottom);
                }
              }
            };
            localObjectAnimator3.addListener(local8);
          }
          localObject = TransitionUtils.mergeAnimators(localObjectAnimator2, localObjectAnimator3);
        }
        continue;
      }
      else
      {
        int i = ((Integer)paramTransitionValues1.values.get("android:changeBounds:windowX")).intValue();
        int j = ((Integer)paramTransitionValues1.values.get("android:changeBounds:windowY")).intValue();
        int k = ((Integer)paramTransitionValues2.values.get("android:changeBounds:windowX")).intValue();
        int m = ((Integer)paramTransitionValues2.values.get("android:changeBounds:windowY")).intValue();
        if ((i == k) && (j == m))
          break;
        paramViewGroup.getLocationInWindow(this.mTempLocation);
        Bitmap localBitmap = Bitmap.createBitmap(localView.getWidth(), localView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        localView.draw(localCanvas);
        BitmapDrawable localBitmapDrawable = new BitmapDrawable(localBitmap);
        float f = ViewUtils.getTransitionAlpha(localView);
        ViewUtils.setTransitionAlpha(localView, 0.0F);
        ViewUtils.getOverlay(paramViewGroup).add(localBitmapDrawable);
        Path localPath1 = getPathMotion().getPath(i - this.mTempLocation[0], j - this.mTempLocation[1], k - this.mTempLocation[0], m - this.mTempLocation[1]);
        ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(localBitmapDrawable, new PropertyValuesHolder[] { PropertyValuesHolderUtils.ofPointF(DRAWABLE_ORIGIN_PROPERTY, localPath1) });
        localObjectAnimator1.addListener(new AnimatorListenerAdapter(paramViewGroup, localBitmapDrawable, localView, f)
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            ViewUtils.getOverlay(this.val$sceneRoot).remove(this.val$drawable);
            ViewUtils.setTransitionAlpha(this.val$view, this.val$transitionAlpha);
          }
        });
        return localObjectAnimator1;
      }
    }
    return (Animator)null;
  }

  public boolean getResizeClip()
  {
    return this.mResizeClip;
  }

  @Nullable
  public String[] getTransitionProperties()
  {
    return sTransitionProperties;
  }

  public void setResizeClip(boolean paramBoolean)
  {
    this.mResizeClip = paramBoolean;
  }

  private static class ViewBounds
  {
    private int mBottom;
    private int mBottomRightCalls;
    private int mLeft;
    private int mRight;
    private int mTop;
    private int mTopLeftCalls;
    private View mView;

    ViewBounds(View paramView)
    {
      this.mView = paramView;
    }

    private void setLeftTopRightBottom()
    {
      ViewUtils.setLeftTopRightBottom(this.mView, this.mLeft, this.mTop, this.mRight, this.mBottom);
      this.mTopLeftCalls = 0;
      this.mBottomRightCalls = 0;
    }

    void setBottomRight(PointF paramPointF)
    {
      this.mRight = Math.round(paramPointF.x);
      this.mBottom = Math.round(paramPointF.y);
      this.mBottomRightCalls = (1 + this.mBottomRightCalls);
      if (this.mTopLeftCalls == this.mBottomRightCalls)
        setLeftTopRightBottom();
    }

    void setTopLeft(PointF paramPointF)
    {
      this.mLeft = Math.round(paramPointF.x);
      this.mTop = Math.round(paramPointF.y);
      this.mTopLeftCalls = (1 + this.mTopLeftCalls);
      if (this.mTopLeftCalls == this.mBottomRightCalls)
        setLeftTopRightBottom();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ChangeBounds
 * JD-Core Version:    0.6.0
 */