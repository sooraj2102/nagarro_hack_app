package android.support.transition;

import F;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

public class ChangeTransform extends Transition
{
  private static final Property<PathAnimatorMatrix, float[]> NON_TRANSLATIONS_PROPERTY;
  private static final String PROPNAME_INTERMEDIATE_MATRIX = "android:changeTransform:intermediateMatrix";
  private static final String PROPNAME_INTERMEDIATE_PARENT_MATRIX = "android:changeTransform:intermediateParentMatrix";
  private static final String PROPNAME_MATRIX = "android:changeTransform:matrix";
  private static final String PROPNAME_PARENT = "android:changeTransform:parent";
  private static final String PROPNAME_PARENT_MATRIX = "android:changeTransform:parentMatrix";
  private static final String PROPNAME_TRANSFORMS = "android:changeTransform:transforms";
  private static final boolean SUPPORTS_VIEW_REMOVAL_SUPPRESSION;
  private static final Property<PathAnimatorMatrix, PointF> TRANSLATIONS_PROPERTY;
  private static final String[] sTransitionProperties;
  private boolean mReparent = true;
  private Matrix mTempMatrix = new Matrix();
  private boolean mUseOverlay = true;

  static
  {
    boolean bool = true;
    String[] arrayOfString = new String[3];
    arrayOfString[0] = "android:changeTransform:matrix";
    arrayOfString[bool] = "android:changeTransform:transforms";
    arrayOfString[2] = "android:changeTransform:parentMatrix";
    sTransitionProperties = arrayOfString;
    NON_TRANSLATIONS_PROPERTY = new Property([F.class, "nonTranslations")
    {
      public float[] get(ChangeTransform.PathAnimatorMatrix paramPathAnimatorMatrix)
      {
        return null;
      }

      public void set(ChangeTransform.PathAnimatorMatrix paramPathAnimatorMatrix, float[] paramArrayOfFloat)
      {
        paramPathAnimatorMatrix.setValues(paramArrayOfFloat);
      }
    };
    TRANSLATIONS_PROPERTY = new Property(PointF.class, "translations")
    {
      public PointF get(ChangeTransform.PathAnimatorMatrix paramPathAnimatorMatrix)
      {
        return null;
      }

      public void set(ChangeTransform.PathAnimatorMatrix paramPathAnimatorMatrix, PointF paramPointF)
      {
        paramPathAnimatorMatrix.setTranslation(paramPointF);
      }
    };
    if (Build.VERSION.SDK_INT >= 21);
    while (true)
    {
      SUPPORTS_VIEW_REMOVAL_SUPPRESSION = bool;
      return;
      bool = false;
    }
  }

  public ChangeTransform()
  {
  }

  public ChangeTransform(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.CHANGE_TRANSFORM);
    this.mUseOverlay = TypedArrayUtils.getNamedBoolean(localTypedArray, (XmlPullParser)paramAttributeSet, "reparentWithOverlay", 1, true);
    this.mReparent = TypedArrayUtils.getNamedBoolean(localTypedArray, (XmlPullParser)paramAttributeSet, "reparent", 0, true);
    localTypedArray.recycle();
  }

  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = paramTransitionValues.view;
    if (localView.getVisibility() == 8)
      return;
    paramTransitionValues.values.put("android:changeTransform:parent", localView.getParent());
    Transforms localTransforms = new Transforms(localView);
    paramTransitionValues.values.put("android:changeTransform:transforms", localTransforms);
    Matrix localMatrix1 = localView.getMatrix();
    if ((localMatrix1 == null) || (localMatrix1.isIdentity()));
    for (Object localObject = null; ; localObject = new Matrix(localMatrix1))
    {
      paramTransitionValues.values.put("android:changeTransform:matrix", localObject);
      if (!this.mReparent)
        break;
      Matrix localMatrix2 = new Matrix();
      ViewGroup localViewGroup = (ViewGroup)localView.getParent();
      ViewUtils.transformMatrixToGlobal(localViewGroup, localMatrix2);
      localMatrix2.preTranslate(-localViewGroup.getScrollX(), -localViewGroup.getScrollY());
      paramTransitionValues.values.put("android:changeTransform:parentMatrix", localMatrix2);
      paramTransitionValues.values.put("android:changeTransform:intermediateMatrix", localView.getTag(R.id.transition_transform));
      paramTransitionValues.values.put("android:changeTransform:intermediateParentMatrix", localView.getTag(R.id.parent_matrix));
      return;
    }
  }

  private void createGhostView(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    View localView = paramTransitionValues2.view;
    Matrix localMatrix = new Matrix((Matrix)paramTransitionValues2.values.get("android:changeTransform:parentMatrix"));
    ViewUtils.transformMatrixToLocal(paramViewGroup, localMatrix);
    GhostViewImpl localGhostViewImpl = GhostViewUtils.addGhost(localView, paramViewGroup, localMatrix);
    if (localGhostViewImpl == null);
    do
    {
      return;
      localGhostViewImpl.reserveEndViewTransition((ViewGroup)paramTransitionValues1.values.get("android:changeTransform:parent"), paramTransitionValues1.view);
      for (Object localObject = this; ((Transition)localObject).mParent != null; localObject = ((Transition)localObject).mParent);
      ((Transition)localObject).addListener(new GhostListener(localView, localGhostViewImpl));
    }
    while (!SUPPORTS_VIEW_REMOVAL_SUPPRESSION);
    if (paramTransitionValues1.view != paramTransitionValues2.view)
      ViewUtils.setTransitionAlpha(paramTransitionValues1.view, 0.0F);
    ViewUtils.setTransitionAlpha(localView, 1.0F);
  }

  private ObjectAnimator createTransformAnimator(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2, boolean paramBoolean)
  {
    Matrix localMatrix1 = (Matrix)paramTransitionValues1.values.get("android:changeTransform:matrix");
    Matrix localMatrix2 = (Matrix)paramTransitionValues2.values.get("android:changeTransform:matrix");
    if (localMatrix1 == null)
      localMatrix1 = MatrixUtils.IDENTITY_MATRIX;
    if (localMatrix2 == null)
      localMatrix2 = MatrixUtils.IDENTITY_MATRIX;
    if (localMatrix1.equals(localMatrix2))
      return null;
    Transforms localTransforms = (Transforms)paramTransitionValues2.values.get("android:changeTransform:transforms");
    View localView = paramTransitionValues2.view;
    setIdentityTransforms(localView);
    float[] arrayOfFloat1 = new float[9];
    localMatrix1.getValues(arrayOfFloat1);
    float[] arrayOfFloat2 = new float[9];
    localMatrix2.getValues(arrayOfFloat2);
    PathAnimatorMatrix localPathAnimatorMatrix = new PathAnimatorMatrix(localView, arrayOfFloat1);
    PropertyValuesHolder localPropertyValuesHolder = PropertyValuesHolder.ofObject(NON_TRANSLATIONS_PROPERTY, new FloatArrayEvaluator(new float[9]), new float[][] { arrayOfFloat1, arrayOfFloat2 });
    Path localPath = getPathMotion().getPath(arrayOfFloat1[2], arrayOfFloat1[5], arrayOfFloat2[2], arrayOfFloat2[5]);
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(localPathAnimatorMatrix, new PropertyValuesHolder[] { localPropertyValuesHolder, PropertyValuesHolderUtils.ofPointF(TRANSLATIONS_PROPERTY, localPath) });
    3 local3 = new AnimatorListenerAdapter(paramBoolean, localMatrix2, localView, localTransforms, localPathAnimatorMatrix)
    {
      private boolean mIsCanceled;
      private Matrix mTempMatrix = new Matrix();

      private void setCurrentMatrix(Matrix paramMatrix)
      {
        this.mTempMatrix.set(paramMatrix);
        this.val$view.setTag(R.id.transition_transform, this.mTempMatrix);
        this.val$transforms.restore(this.val$view);
      }

      public void onAnimationCancel(Animator paramAnimator)
      {
        this.mIsCanceled = true;
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        if (!this.mIsCanceled)
        {
          if ((!this.val$handleParentChange) || (!ChangeTransform.this.mUseOverlay))
            break label52;
          setCurrentMatrix(this.val$finalEndMatrix);
        }
        while (true)
        {
          ViewUtils.setAnimationMatrix(this.val$view, null);
          this.val$transforms.restore(this.val$view);
          return;
          label52: this.val$view.setTag(R.id.transition_transform, null);
          this.val$view.setTag(R.id.parent_matrix, null);
        }
      }

      public void onAnimationPause(Animator paramAnimator)
      {
        setCurrentMatrix(this.val$pathAnimatorMatrix.getMatrix());
      }

      public void onAnimationResume(Animator paramAnimator)
      {
        ChangeTransform.access$100(this.val$view);
      }
    };
    localObjectAnimator.addListener(local3);
    AnimatorUtils.addPauseListener(localObjectAnimator, local3);
    return localObjectAnimator;
  }

  private boolean parentsMatch(ViewGroup paramViewGroup1, ViewGroup paramViewGroup2)
  {
    int i;
    if ((!isValidTarget(paramViewGroup1)) || (!isValidTarget(paramViewGroup2)))
      if (paramViewGroup1 == paramViewGroup2)
        i = 1;
    TransitionValues localTransitionValues;
    do
    {
      return i;
      return false;
      localTransitionValues = getMatchedTransitionValues(paramViewGroup1, true);
      i = 0;
    }
    while (localTransitionValues == null);
    return paramViewGroup2 == localTransitionValues.view;
  }

  private static void setIdentityTransforms(View paramView)
  {
    setTransforms(paramView, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
  }

  private void setMatricesForParent(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    Matrix localMatrix1 = (Matrix)paramTransitionValues2.values.get("android:changeTransform:parentMatrix");
    paramTransitionValues2.view.setTag(R.id.parent_matrix, localMatrix1);
    Matrix localMatrix2 = this.mTempMatrix;
    localMatrix2.reset();
    localMatrix1.invert(localMatrix2);
    Matrix localMatrix3 = (Matrix)paramTransitionValues1.values.get("android:changeTransform:matrix");
    if (localMatrix3 == null)
    {
      localMatrix3 = new Matrix();
      paramTransitionValues1.values.put("android:changeTransform:matrix", localMatrix3);
    }
    localMatrix3.postConcat((Matrix)paramTransitionValues1.values.get("android:changeTransform:parentMatrix"));
    localMatrix3.postConcat(localMatrix2);
  }

  private static void setTransforms(View paramView, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8)
  {
    paramView.setTranslationX(paramFloat1);
    paramView.setTranslationY(paramFloat2);
    ViewCompat.setTranslationZ(paramView, paramFloat3);
    paramView.setScaleX(paramFloat4);
    paramView.setScaleY(paramFloat5);
    paramView.setRotationX(paramFloat6);
    paramView.setRotationY(paramFloat7);
    paramView.setRotation(paramFloat8);
  }

  public void captureEndValues(@NonNull TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }

  public void captureStartValues(@NonNull TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
    if (!SUPPORTS_VIEW_REMOVAL_SUPPRESSION)
      ((ViewGroup)paramTransitionValues.view.getParent()).startViewTransition(paramTransitionValues.view);
  }

  public Animator createAnimator(@NonNull ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    ObjectAnimator localObjectAnimator;
    if ((paramTransitionValues1 == null) || (paramTransitionValues2 == null) || (!paramTransitionValues1.values.containsKey("android:changeTransform:parent")) || (!paramTransitionValues2.values.containsKey("android:changeTransform:parent")))
      localObjectAnimator = null;
    ViewGroup localViewGroup1;
    do
    {
      return localObjectAnimator;
      localViewGroup1 = (ViewGroup)paramTransitionValues1.values.get("android:changeTransform:parent");
      ViewGroup localViewGroup2 = (ViewGroup)paramTransitionValues2.values.get("android:changeTransform:parent");
      if ((this.mReparent) && (!parentsMatch(localViewGroup1, localViewGroup2)));
      for (boolean bool = true; ; bool = false)
      {
        Matrix localMatrix1 = (Matrix)paramTransitionValues1.values.get("android:changeTransform:intermediateMatrix");
        if (localMatrix1 != null)
          paramTransitionValues1.values.put("android:changeTransform:matrix", localMatrix1);
        Matrix localMatrix2 = (Matrix)paramTransitionValues1.values.get("android:changeTransform:intermediateParentMatrix");
        if (localMatrix2 != null)
          paramTransitionValues1.values.put("android:changeTransform:parentMatrix", localMatrix2);
        if (bool)
          setMatricesForParent(paramTransitionValues1, paramTransitionValues2);
        localObjectAnimator = createTransformAnimator(paramTransitionValues1, paramTransitionValues2, bool);
        if ((!bool) || (localObjectAnimator == null) || (!this.mUseOverlay))
          break;
        createGhostView(paramViewGroup, paramTransitionValues1, paramTransitionValues2);
        return localObjectAnimator;
      }
    }
    while (SUPPORTS_VIEW_REMOVAL_SUPPRESSION);
    localViewGroup1.endViewTransition(paramTransitionValues1.view);
    return localObjectAnimator;
  }

  public boolean getReparent()
  {
    return this.mReparent;
  }

  public boolean getReparentWithOverlay()
  {
    return this.mUseOverlay;
  }

  public String[] getTransitionProperties()
  {
    return sTransitionProperties;
  }

  public void setReparent(boolean paramBoolean)
  {
    this.mReparent = paramBoolean;
  }

  public void setReparentWithOverlay(boolean paramBoolean)
  {
    this.mUseOverlay = paramBoolean;
  }

  private static class GhostListener extends TransitionListenerAdapter
  {
    private GhostViewImpl mGhostView;
    private View mView;

    GhostListener(View paramView, GhostViewImpl paramGhostViewImpl)
    {
      this.mView = paramView;
      this.mGhostView = paramGhostViewImpl;
    }

    public void onTransitionEnd(@NonNull Transition paramTransition)
    {
      paramTransition.removeListener(this);
      GhostViewUtils.removeGhost(this.mView);
      this.mView.setTag(R.id.transition_transform, null);
      this.mView.setTag(R.id.parent_matrix, null);
    }

    public void onTransitionPause(@NonNull Transition paramTransition)
    {
      this.mGhostView.setVisibility(4);
    }

    public void onTransitionResume(@NonNull Transition paramTransition)
    {
      this.mGhostView.setVisibility(0);
    }
  }

  private static class PathAnimatorMatrix
  {
    private final Matrix mMatrix = new Matrix();
    private float mTranslationX;
    private float mTranslationY;
    private final float[] mValues;
    private final View mView;

    PathAnimatorMatrix(View paramView, float[] paramArrayOfFloat)
    {
      this.mView = paramView;
      this.mValues = ((float[])paramArrayOfFloat.clone());
      this.mTranslationX = this.mValues[2];
      this.mTranslationY = this.mValues[5];
      setAnimationMatrix();
    }

    private void setAnimationMatrix()
    {
      this.mValues[2] = this.mTranslationX;
      this.mValues[5] = this.mTranslationY;
      this.mMatrix.setValues(this.mValues);
      ViewUtils.setAnimationMatrix(this.mView, this.mMatrix);
    }

    Matrix getMatrix()
    {
      return this.mMatrix;
    }

    void setTranslation(PointF paramPointF)
    {
      this.mTranslationX = paramPointF.x;
      this.mTranslationY = paramPointF.y;
      setAnimationMatrix();
    }

    void setValues(float[] paramArrayOfFloat)
    {
      System.arraycopy(paramArrayOfFloat, 0, this.mValues, 0, paramArrayOfFloat.length);
      setAnimationMatrix();
    }
  }

  private static class Transforms
  {
    final float mRotationX;
    final float mRotationY;
    final float mRotationZ;
    final float mScaleX;
    final float mScaleY;
    final float mTranslationX;
    final float mTranslationY;
    final float mTranslationZ;

    Transforms(View paramView)
    {
      this.mTranslationX = paramView.getTranslationX();
      this.mTranslationY = paramView.getTranslationY();
      this.mTranslationZ = ViewCompat.getTranslationZ(paramView);
      this.mScaleX = paramView.getScaleX();
      this.mScaleY = paramView.getScaleY();
      this.mRotationX = paramView.getRotationX();
      this.mRotationY = paramView.getRotationY();
      this.mRotationZ = paramView.getRotation();
    }

    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Transforms));
      Transforms localTransforms;
      do
      {
        return false;
        localTransforms = (Transforms)paramObject;
      }
      while ((localTransforms.mTranslationX != this.mTranslationX) || (localTransforms.mTranslationY != this.mTranslationY) || (localTransforms.mTranslationZ != this.mTranslationZ) || (localTransforms.mScaleX != this.mScaleX) || (localTransforms.mScaleY != this.mScaleY) || (localTransforms.mRotationX != this.mRotationX) || (localTransforms.mRotationY != this.mRotationY) || (localTransforms.mRotationZ != this.mRotationZ));
      return true;
    }

    public int hashCode()
    {
      int i;
      int k;
      label39: int n;
      label65: int i2;
      label93: int i4;
      label121: int i6;
      label149: int i7;
      if (this.mTranslationX != 0.0F)
      {
        i = Float.floatToIntBits(this.mTranslationX);
        int j = i * 31;
        if (this.mTranslationY == 0.0F)
          break label223;
        k = Float.floatToIntBits(this.mTranslationY);
        int m = 31 * (j + k);
        if (this.mTranslationZ == 0.0F)
          break label228;
        n = Float.floatToIntBits(this.mTranslationZ);
        int i1 = 31 * (m + n);
        if (this.mScaleX == 0.0F)
          break label234;
        i2 = Float.floatToIntBits(this.mScaleX);
        int i3 = 31 * (i1 + i2);
        if (this.mScaleY == 0.0F)
          break label240;
        i4 = Float.floatToIntBits(this.mScaleY);
        int i5 = 31 * (i3 + i4);
        if (this.mRotationX == 0.0F)
          break label246;
        i6 = Float.floatToIntBits(this.mRotationX);
        i7 = 31 * (i5 + i6);
        if (this.mRotationY == 0.0F)
          break label252;
      }
      label223: label228: label234: label240: label246: label252: for (int i8 = Float.floatToIntBits(this.mRotationY); ; i8 = 0)
      {
        int i9 = 31 * (i7 + i8);
        boolean bool = this.mRotationZ < 0.0F;
        int i10 = 0;
        if (bool)
          i10 = Float.floatToIntBits(this.mRotationZ);
        return i9 + i10;
        i = 0;
        break;
        k = 0;
        break label39;
        n = 0;
        break label65;
        i2 = 0;
        break label93;
        i4 = 0;
        break label121;
        i6 = 0;
        break label149;
      }
    }

    public void restore(View paramView)
    {
      ChangeTransform.access$200(paramView, this.mTranslationX, this.mTranslationY, this.mTranslationZ, this.mScaleX, this.mScaleY, this.mRotationX, this.mRotationY, this.mRotationZ);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ChangeTransform
 * JD-Core Version:    0.6.0
 */