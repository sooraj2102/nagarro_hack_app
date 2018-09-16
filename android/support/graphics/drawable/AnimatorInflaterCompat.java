package android.support.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build.VERSION;
import android.support.annotation.AnimatorRes;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.graphics.PathParser;
import android.support.v4.graphics.PathParser.PathDataNode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class AnimatorInflaterCompat
{
  private static final boolean DBG_ANIMATOR_INFLATER = false;
  private static final int MAX_NUM_POINTS = 100;
  private static final String TAG = "AnimatorInflater";
  private static final int TOGETHER = 0;
  private static final int VALUE_TYPE_COLOR = 3;
  private static final int VALUE_TYPE_FLOAT = 0;
  private static final int VALUE_TYPE_INT = 1;
  private static final int VALUE_TYPE_PATH = 2;
  private static final int VALUE_TYPE_UNDEFINED = 4;

  private static Animator createAnimatorFromXml(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, float paramFloat)
    throws XmlPullParserException, IOException
  {
    return createAnimatorFromXml(paramContext, paramResources, paramTheme, paramXmlPullParser, Xml.asAttributeSet(paramXmlPullParser), null, 0, paramFloat);
  }

  private static Animator createAnimatorFromXml(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, AnimatorSet paramAnimatorSet, int paramInt, float paramFloat)
    throws XmlPullParserException, IOException
  {
    Object localObject = null;
    ArrayList localArrayList = null;
    int i = paramXmlPullParser.getDepth();
    while (true)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1))
        break;
      if (j != 2)
        continue;
      String str = paramXmlPullParser.getName();
      int n = 0;
      if (str.equals("objectAnimator"))
        localObject = loadObjectAnimator(paramContext, paramResources, paramTheme, paramAttributeSet, paramFloat, paramXmlPullParser);
      while (true)
      {
        if ((paramAnimatorSet == null) || (n != 0))
          break label278;
        if (localArrayList == null)
          localArrayList = new ArrayList();
        localArrayList.add(localObject);
        break;
        if (str.equals("animator"))
        {
          localObject = loadAnimator(paramContext, paramResources, paramTheme, paramAttributeSet, null, paramFloat, paramXmlPullParser);
          n = 0;
          continue;
        }
        if (str.equals("set"))
        {
          localObject = new AnimatorSet();
          TypedArray localTypedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_ANIMATOR_SET);
          int i1 = TypedArrayUtils.getNamedInt(localTypedArray, paramXmlPullParser, "ordering", 0, 0);
          createAnimatorFromXml(paramContext, paramResources, paramTheme, paramXmlPullParser, paramAttributeSet, (AnimatorSet)localObject, i1, paramFloat);
          localTypedArray.recycle();
          n = 0;
          continue;
        }
        if (!str.equals("propertyValuesHolder"))
          break label280;
        PropertyValuesHolder[] arrayOfPropertyValuesHolder = loadValues(paramContext, paramResources, paramTheme, paramXmlPullParser, Xml.asAttributeSet(paramXmlPullParser));
        if ((arrayOfPropertyValuesHolder != null) && (localObject != null) && ((localObject instanceof ValueAnimator)))
          ((ValueAnimator)localObject).setValues(arrayOfPropertyValuesHolder);
        n = 1;
      }
      label278: continue;
      label280: throw new RuntimeException("Unknown animator name: " + paramXmlPullParser.getName());
    }
    Animator[] arrayOfAnimator;
    if ((paramAnimatorSet != null) && (localArrayList != null))
    {
      arrayOfAnimator = new Animator[localArrayList.size()];
      int k = 0;
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        Animator localAnimator = (Animator)localIterator.next();
        int m = k + 1;
        arrayOfAnimator[k] = localAnimator;
        k = m;
      }
      if (paramInt == 0)
        paramAnimatorSet.playTogether(arrayOfAnimator);
    }
    else
    {
      return localObject;
    }
    paramAnimatorSet.playSequentially(arrayOfAnimator);
    return (Animator)localObject;
  }

  private static Keyframe createNewKeyframe(Keyframe paramKeyframe, float paramFloat)
  {
    if (paramKeyframe.getType() == Float.TYPE)
      return Keyframe.ofFloat(paramFloat);
    if (paramKeyframe.getType() == Integer.TYPE)
      return Keyframe.ofInt(paramFloat);
    return Keyframe.ofObject(paramFloat);
  }

  private static void distributeKeyframes(Keyframe[] paramArrayOfKeyframe, float paramFloat, int paramInt1, int paramInt2)
  {
    float f = paramFloat / (2 + (paramInt2 - paramInt1));
    for (int i = paramInt1; i <= paramInt2; i++)
      paramArrayOfKeyframe[i].setFraction(f + paramArrayOfKeyframe[(i - 1)].getFraction());
  }

  private static void dumpKeyframes(Object[] paramArrayOfObject, String paramString)
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0))
      return;
    Log.d("AnimatorInflater", paramString);
    int i = paramArrayOfObject.length;
    int j = 0;
    label23: Keyframe localKeyframe;
    Object localObject1;
    label76: StringBuilder localStringBuilder2;
    if (j < i)
    {
      localKeyframe = (Keyframe)paramArrayOfObject[j];
      StringBuilder localStringBuilder1 = new StringBuilder().append("Keyframe ").append(j).append(": fraction ");
      if (localKeyframe.getFraction() >= 0.0F)
        break label132;
      localObject1 = "null";
      localStringBuilder2 = localStringBuilder1.append(localObject1).append(", ").append(", value : ");
      if (!localKeyframe.hasValue())
        break label145;
    }
    label132: label145: for (Object localObject2 = localKeyframe.getValue(); ; localObject2 = "null")
    {
      Log.d("AnimatorInflater", localObject2);
      j++;
      break label23;
      break;
      localObject1 = Float.valueOf(localKeyframe.getFraction());
      break label76;
    }
  }

  private static PropertyValuesHolder getPVH(TypedArray paramTypedArray, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    TypedValue localTypedValue1 = paramTypedArray.peekValue(paramInt2);
    int i;
    int j;
    label27: int k;
    label42: int m;
    if (localTypedValue1 != null)
    {
      i = 1;
      if (i == 0)
        break label216;
      j = localTypedValue1.type;
      TypedValue localTypedValue2 = paramTypedArray.peekValue(paramInt3);
      if (localTypedValue2 == null)
        break label222;
      k = 1;
      if (k == 0)
        break label228;
      m = localTypedValue2.type;
      label54: if (paramInt1 == 4)
      {
        if (((i == 0) || (!isColorType(j))) && ((k == 0) || (!isColorType(m))))
          break label234;
        paramInt1 = 3;
      }
      label87: if (paramInt1 != 0)
        break label239;
    }
    PathParser.PathDataNode[] arrayOfPathDataNode1;
    PathParser.PathDataNode[] arrayOfPathDataNode2;
    PathDataEvaluator localPathDataEvaluator;
    label216: label222: label228: label234: label239: for (int n = 1; ; n = 0)
    {
      if (paramInt1 != 2)
        break label319;
      String str1 = paramTypedArray.getString(paramInt2);
      String str2 = paramTypedArray.getString(paramInt3);
      arrayOfPathDataNode1 = PathParser.createNodesFromPathData(str1);
      arrayOfPathDataNode2 = PathParser.createNodesFromPathData(str2);
      if (arrayOfPathDataNode1 == null)
      {
        localPropertyValuesHolder = null;
        if (arrayOfPathDataNode2 == null)
          break label268;
      }
      if (arrayOfPathDataNode1 == null)
        break label288;
      localPathDataEvaluator = new PathDataEvaluator(null);
      if (arrayOfPathDataNode2 == null)
        break label271;
      if (PathParser.canMorph(arrayOfPathDataNode1, arrayOfPathDataNode2))
        break label245;
      throw new InflateException(" Can't morph from " + str1 + " to " + str2);
      i = 0;
      break;
      j = 0;
      break label27;
      k = 0;
      break label42;
      m = 0;
      break label54;
      paramInt1 = 0;
      break label87;
    }
    label245: PropertyValuesHolder localPropertyValuesHolder = PropertyValuesHolder.ofObject(paramString, localPathDataEvaluator, new Object[] { arrayOfPathDataNode1, arrayOfPathDataNode2 });
    label268: label271: label288: 
    do
    {
      return localPropertyValuesHolder;
      return PropertyValuesHolder.ofObject(paramString, localPathDataEvaluator, new Object[] { arrayOfPathDataNode1 });
      localPropertyValuesHolder = null;
    }
    while (arrayOfPathDataNode2 == null);
    return PropertyValuesHolder.ofObject(paramString, new PathDataEvaluator(null), new Object[] { arrayOfPathDataNode2 });
    label319: int i1 = paramInt1;
    ArgbEvaluator localArgbEvaluator = null;
    if (i1 == 3)
      localArgbEvaluator = ArgbEvaluator.getInstance();
    float f2;
    float f3;
    if (n != 0)
      if (i != 0)
        if (j == 5)
        {
          f2 = paramTypedArray.getDimension(paramInt2, 0.0F);
          if (k == 0)
            break label441;
          if (m != 5)
            break label430;
          f3 = paramTypedArray.getDimension(paramInt3, 0.0F);
          label379: localPropertyValuesHolder = PropertyValuesHolder.ofFloat(paramString, new float[] { f2, f3 });
        }
    label399: label430: label441: label595: label625: 
    do
    {
      while (true)
      {
        if ((localPropertyValuesHolder == null) || (localArgbEvaluator == null))
          break label682;
        localPropertyValuesHolder.setEvaluator(localArgbEvaluator);
        return localPropertyValuesHolder;
        f2 = paramTypedArray.getFloat(paramInt2, 0.0F);
        break;
        f3 = paramTypedArray.getFloat(paramInt3, 0.0F);
        break label379;
        localPropertyValuesHolder = PropertyValuesHolder.ofFloat(paramString, new float[] { f2 });
        continue;
        float f1;
        if (m == 5)
          f1 = paramTypedArray.getDimension(paramInt3, 0.0F);
        while (true)
        {
          localPropertyValuesHolder = PropertyValuesHolder.ofFloat(paramString, new float[] { f1 });
          break;
          f1 = paramTypedArray.getFloat(paramInt3, 0.0F);
        }
        if (i == 0)
          break label643;
        int i3;
        int i4;
        if (j == 5)
        {
          i3 = (int)paramTypedArray.getDimension(paramInt2, 0.0F);
          if (k == 0)
            break label625;
          if (m != 5)
            break label595;
          i4 = (int)paramTypedArray.getDimension(paramInt3, 0.0F);
        }
        while (true)
        {
          localPropertyValuesHolder = PropertyValuesHolder.ofInt(paramString, new int[] { i3, i4 });
          break;
          if (isColorType(j))
          {
            i3 = paramTypedArray.getColor(paramInt2, 0);
            break label522;
          }
          i3 = paramTypedArray.getInt(paramInt2, 0);
          break label522;
          if (isColorType(m))
          {
            i4 = paramTypedArray.getColor(paramInt3, 0);
            continue;
          }
          i4 = paramTypedArray.getInt(paramInt3, 0);
        }
        localPropertyValuesHolder = PropertyValuesHolder.ofInt(paramString, new int[] { i3 });
      }
      localPropertyValuesHolder = null;
    }
    while (k == 0);
    label522: int i2;
    label643: if (m == 5)
      i2 = (int)paramTypedArray.getDimension(paramInt3, 0.0F);
    while (true)
    {
      localPropertyValuesHolder = PropertyValuesHolder.ofInt(paramString, new int[] { i2 });
      break label399;
      label682: break;
      if (isColorType(m))
      {
        i2 = paramTypedArray.getColor(paramInt3, 0);
        continue;
      }
      i2 = paramTypedArray.getInt(paramInt3, 0);
    }
  }

  private static int inferValueTypeFromValues(TypedArray paramTypedArray, int paramInt1, int paramInt2)
  {
    int i = 1;
    TypedValue localTypedValue1 = paramTypedArray.peekValue(paramInt1);
    int j;
    int k;
    label29: TypedValue localTypedValue2;
    if (localTypedValue1 != null)
    {
      j = i;
      if (j == 0)
        break label85;
      k = localTypedValue1.type;
      localTypedValue2 = paramTypedArray.peekValue(paramInt2);
      if (localTypedValue2 == null)
        break label91;
      label41: if (i == 0)
        break label96;
    }
    label85: label91: label96: for (int m = localTypedValue2.type; ; m = 0)
    {
      if (((j == 0) || (!isColorType(k))) && ((i == 0) || (!isColorType(m))))
        break label102;
      return 3;
      j = 0;
      break;
      k = 0;
      break label29;
      i = 0;
      break label41;
    }
    label102: return 0;
  }

  private static int inferValueTypeOfKeyframe(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, XmlPullParser paramXmlPullParser)
  {
    TypedArray localTypedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_KEYFRAME);
    TypedValue localTypedValue = TypedArrayUtils.peekNamedValue(localTypedArray, paramXmlPullParser, "value", 0);
    int i = 0;
    if (localTypedValue != null)
      i = 1;
    if ((i != 0) && (isColorType(localTypedValue.type)));
    for (int j = 3; ; j = 0)
    {
      localTypedArray.recycle();
      return j;
    }
  }

  private static boolean isColorType(int paramInt)
  {
    return (paramInt >= 28) && (paramInt <= 31);
  }

  public static Animator loadAnimator(Context paramContext, @AnimatorRes int paramInt)
    throws Resources.NotFoundException
  {
    if (Build.VERSION.SDK_INT >= 24)
      return AnimatorInflater.loadAnimator(paramContext, paramInt);
    return loadAnimator(paramContext, paramContext.getResources(), paramContext.getTheme(), paramInt);
  }

  public static Animator loadAnimator(Context paramContext, Resources paramResources, Resources.Theme paramTheme, @AnimatorRes int paramInt)
    throws Resources.NotFoundException
  {
    return loadAnimator(paramContext, paramResources, paramTheme, paramInt, 1.0F);
  }

  // ERROR //
  public static Animator loadAnimator(Context paramContext, Resources paramResources, Resources.Theme paramTheme, @AnimatorRes int paramInt, float paramFloat)
    throws Resources.NotFoundException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: iload_3
    //   5: invokevirtual 360	android/content/res/Resources:getAnimation	(I)Landroid/content/res/XmlResourceParser;
    //   8: astore 5
    //   10: aload_0
    //   11: aload_1
    //   12: aload_2
    //   13: aload 5
    //   15: fload 4
    //   17: invokestatic 362	android/support/graphics/drawable/AnimatorInflaterCompat:createAnimatorFromXml	(Landroid/content/Context;Landroid/content/res/Resources;Landroid/content/res/Resources$Theme;Lorg/xmlpull/v1/XmlPullParser;F)Landroid/animation/Animator;
    //   20: astore 13
    //   22: aload 5
    //   24: ifnull +10 -> 34
    //   27: aload 5
    //   29: invokeinterface 367 1 0
    //   34: aload 13
    //   36: areturn
    //   37: astore 10
    //   39: new 328	android/content/res/Resources$NotFoundException
    //   42: dup
    //   43: new 128	java/lang/StringBuilder
    //   46: dup
    //   47: invokespecial 129	java/lang/StringBuilder:<init>	()V
    //   50: ldc_w 369
    //   53: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: iload_3
    //   57: invokestatic 372	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   60: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: invokevirtual 138	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: invokespecial 373	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   69: astore 11
    //   71: aload 11
    //   73: aload 10
    //   75: invokevirtual 377	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   78: pop
    //   79: aload 11
    //   81: athrow
    //   82: astore 9
    //   84: aload 5
    //   86: ifnull +10 -> 96
    //   89: aload 5
    //   91: invokeinterface 367 1 0
    //   96: aload 9
    //   98: athrow
    //   99: astore 6
    //   101: new 328	android/content/res/Resources$NotFoundException
    //   104: dup
    //   105: new 128	java/lang/StringBuilder
    //   108: dup
    //   109: invokespecial 129	java/lang/StringBuilder:<init>	()V
    //   112: ldc_w 369
    //   115: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   118: iload_3
    //   119: invokestatic 372	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   122: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   125: invokevirtual 138	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   128: invokespecial 373	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   131: astore 7
    //   133: aload 7
    //   135: aload 6
    //   137: invokevirtual 377	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   140: pop
    //   141: aload 7
    //   143: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   3	22	37	org/xmlpull/v1/XmlPullParserException
    //   3	22	82	finally
    //   39	82	82	finally
    //   101	144	82	finally
    //   3	22	99	java/io/IOException
  }

  private static ValueAnimator loadAnimator(Context paramContext, Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, ValueAnimator paramValueAnimator, float paramFloat, XmlPullParser paramXmlPullParser)
    throws Resources.NotFoundException
  {
    TypedArray localTypedArray1 = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_ANIMATOR);
    TypedArray localTypedArray2 = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_PROPERTY_ANIMATOR);
    if (paramValueAnimator == null)
      paramValueAnimator = new ValueAnimator();
    parseAnimatorFromTypeArray(paramValueAnimator, localTypedArray1, localTypedArray2, paramFloat, paramXmlPullParser);
    int i = TypedArrayUtils.getNamedResourceId(localTypedArray1, paramXmlPullParser, "interpolator", 0, 0);
    if (i > 0)
      paramValueAnimator.setInterpolator(AnimationUtilsCompat.loadInterpolator(paramContext, i));
    localTypedArray1.recycle();
    if (localTypedArray2 != null)
      localTypedArray2.recycle();
    return paramValueAnimator;
  }

  private static Keyframe loadKeyframe(Context paramContext, Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, int paramInt, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_KEYFRAME);
    float f = TypedArrayUtils.getNamedFloat(localTypedArray, paramXmlPullParser, "fraction", 3, -1.0F);
    TypedValue localTypedValue = TypedArrayUtils.peekNamedValue(localTypedArray, paramXmlPullParser, "value", 0);
    int i;
    if (localTypedValue != null)
    {
      i = 1;
      if (paramInt == 4)
      {
        if ((i == 0) || (!isColorType(localTypedValue.type)))
          break label156;
        paramInt = 3;
      }
      label73: if (i == 0)
        break label206;
      localKeyframe = null;
      switch (paramInt)
      {
      case 2:
      default:
      case 0:
      case 1:
      case 3:
      }
    }
    while (true)
    {
      int j = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "interpolator", 1, 0);
      if (j > 0)
        localKeyframe.setInterpolator(AnimationUtilsCompat.loadInterpolator(paramContext, j));
      localTypedArray.recycle();
      return localKeyframe;
      i = 0;
      break;
      label156: paramInt = 0;
      break label73;
      localKeyframe = Keyframe.ofFloat(f, TypedArrayUtils.getNamedFloat(localTypedArray, paramXmlPullParser, "value", 0, 0.0F));
      continue;
      localKeyframe = Keyframe.ofInt(f, TypedArrayUtils.getNamedInt(localTypedArray, paramXmlPullParser, "value", 0, 0));
    }
    label206: if (paramInt == 0);
    for (Keyframe localKeyframe = Keyframe.ofFloat(f); ; localKeyframe = Keyframe.ofInt(f))
      break;
  }

  private static ObjectAnimator loadObjectAnimator(Context paramContext, Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, float paramFloat, XmlPullParser paramXmlPullParser)
    throws Resources.NotFoundException
  {
    ObjectAnimator localObjectAnimator = new ObjectAnimator();
    loadAnimator(paramContext, paramResources, paramTheme, paramAttributeSet, localObjectAnimator, paramFloat, paramXmlPullParser);
    return localObjectAnimator;
  }

  private static PropertyValuesHolder loadPvh(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, String paramString, int paramInt)
    throws XmlPullParserException, IOException
  {
    ArrayList localArrayList = null;
    while (true)
    {
      int i = paramXmlPullParser.next();
      if ((i == 3) || (i == 1))
        break;
      if (!paramXmlPullParser.getName().equals("keyframe"))
        continue;
      if (paramInt == 4)
        paramInt = inferValueTypeOfKeyframe(paramResources, paramTheme, Xml.asAttributeSet(paramXmlPullParser), paramXmlPullParser);
      Keyframe localKeyframe6 = loadKeyframe(paramContext, paramResources, paramTheme, Xml.asAttributeSet(paramXmlPullParser), paramInt, paramXmlPullParser);
      if (localKeyframe6 != null)
      {
        if (localArrayList == null)
          localArrayList = new ArrayList();
        localArrayList.add(localKeyframe6);
      }
      paramXmlPullParser.next();
    }
    PropertyValuesHolder localPropertyValuesHolder = null;
    if (localArrayList != null)
    {
      int j = localArrayList.size();
      localPropertyValuesHolder = null;
      if (j > 0)
      {
        Keyframe localKeyframe1 = (Keyframe)localArrayList.get(0);
        int k = j - 1;
        Keyframe localKeyframe2 = (Keyframe)localArrayList.get(k);
        float f1 = localKeyframe2.getFraction();
        label214: Keyframe[] arrayOfKeyframe;
        int m;
        label232: Keyframe localKeyframe3;
        if (f1 < 1.0F)
        {
          if (f1 < 0.0F)
            localKeyframe2.setFraction(1.0F);
        }
        else
        {
          float f2 = localKeyframe1.getFraction();
          if (f2 != 0.0F)
          {
            if (f2 >= 0.0F)
              break label303;
            localKeyframe1.setFraction(0.0F);
          }
          arrayOfKeyframe = new Keyframe[j];
          localArrayList.toArray(arrayOfKeyframe);
          m = 0;
          if (m >= j)
            break label422;
          localKeyframe3 = arrayOfKeyframe[m];
          if (localKeyframe3.getFraction() < 0.0F)
          {
            if (m != 0)
              break label325;
            localKeyframe3.setFraction(0.0F);
          }
        }
        while (true)
        {
          m++;
          break label232;
          int i3 = localArrayList.size();
          Keyframe localKeyframe5 = createNewKeyframe(localKeyframe2, 1.0F);
          localArrayList.add(i3, localKeyframe5);
          j++;
          break;
          label303: Keyframe localKeyframe4 = createNewKeyframe(localKeyframe1, 0.0F);
          localArrayList.add(0, localKeyframe4);
          j++;
          break label214;
          label325: if (m != j - 1)
            break label343;
          localKeyframe3.setFraction(1.0F);
        }
        label343: int n = m;
        int i1 = m;
        for (int i2 = n + 1; ; i2++)
        {
          if ((i2 >= j - 1) || (arrayOfKeyframe[i2].getFraction() >= 0.0F))
          {
            distributeKeyframes(arrayOfKeyframe, arrayOfKeyframe[(i1 + 1)].getFraction() - arrayOfKeyframe[(n - 1)].getFraction(), n, i1);
            break;
          }
          i1 = i2;
        }
        label422: localPropertyValuesHolder = PropertyValuesHolder.ofKeyframe(paramString, arrayOfKeyframe);
        if (paramInt == 3)
        {
          ArgbEvaluator localArgbEvaluator = ArgbEvaluator.getInstance();
          localPropertyValuesHolder.setEvaluator(localArgbEvaluator);
        }
      }
    }
    return localPropertyValuesHolder;
  }

  private static PropertyValuesHolder[] loadValues(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    ArrayList localArrayList = null;
    while (true)
    {
      int i = paramXmlPullParser.getEventType();
      if ((i == 3) || (i == 1))
        break;
      if (i != 2)
      {
        paramXmlPullParser.next();
        continue;
      }
      if (paramXmlPullParser.getName().equals("propertyValuesHolder"))
      {
        TypedArray localTypedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_PROPERTY_VALUES_HOLDER);
        String str = TypedArrayUtils.getNamedString(localTypedArray, paramXmlPullParser, "propertyName", 3);
        int m = TypedArrayUtils.getNamedInt(localTypedArray, paramXmlPullParser, "valueType", 2, 4);
        PropertyValuesHolder localPropertyValuesHolder = loadPvh(paramContext, paramResources, paramTheme, paramXmlPullParser, str, m);
        if (localPropertyValuesHolder == null)
          localPropertyValuesHolder = getPVH(localTypedArray, m, 0, 1, str);
        if (localPropertyValuesHolder != null)
        {
          if (localArrayList == null)
            localArrayList = new ArrayList();
          localArrayList.add(localPropertyValuesHolder);
        }
        localTypedArray.recycle();
      }
      paramXmlPullParser.next();
    }
    PropertyValuesHolder[] arrayOfPropertyValuesHolder = null;
    if (localArrayList != null)
    {
      int j = localArrayList.size();
      arrayOfPropertyValuesHolder = new PropertyValuesHolder[j];
      for (int k = 0; k < j; k++)
        arrayOfPropertyValuesHolder[k] = ((PropertyValuesHolder)localArrayList.get(k));
    }
    return arrayOfPropertyValuesHolder;
  }

  private static void parseAnimatorFromTypeArray(ValueAnimator paramValueAnimator, TypedArray paramTypedArray1, TypedArray paramTypedArray2, float paramFloat, XmlPullParser paramXmlPullParser)
  {
    long l1 = TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "duration", 1, 300);
    long l2 = TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "startOffset", 2, 0);
    int i = TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "valueType", 7, 4);
    if ((TypedArrayUtils.hasAttribute(paramXmlPullParser, "valueFrom")) && (TypedArrayUtils.hasAttribute(paramXmlPullParser, "valueTo")))
    {
      if (i == 4)
        i = inferValueTypeFromValues(paramTypedArray1, 5, 6);
      PropertyValuesHolder localPropertyValuesHolder = getPVH(paramTypedArray1, i, 5, 6, "");
      if (localPropertyValuesHolder != null)
        paramValueAnimator.setValues(new PropertyValuesHolder[] { localPropertyValuesHolder });
    }
    paramValueAnimator.setDuration(l1);
    paramValueAnimator.setStartDelay(l2);
    paramValueAnimator.setRepeatCount(TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "repeatCount", 3, 0));
    paramValueAnimator.setRepeatMode(TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "repeatMode", 4, 1));
    if (paramTypedArray2 != null)
      setupObjectAnimator(paramValueAnimator, paramTypedArray2, i, paramFloat, paramXmlPullParser);
  }

  private static void setupObjectAnimator(ValueAnimator paramValueAnimator, TypedArray paramTypedArray, int paramInt, float paramFloat, XmlPullParser paramXmlPullParser)
  {
    ObjectAnimator localObjectAnimator = (ObjectAnimator)paramValueAnimator;
    String str1 = TypedArrayUtils.getNamedString(paramTypedArray, paramXmlPullParser, "pathData", 1);
    if (str1 != null)
    {
      String str2 = TypedArrayUtils.getNamedString(paramTypedArray, paramXmlPullParser, "propertyXName", 2);
      String str3 = TypedArrayUtils.getNamedString(paramTypedArray, paramXmlPullParser, "propertyYName", 3);
      if (((paramInt == 2) || (paramInt != 4)) || ((str2 == null) && (str3 == null)))
        throw new InflateException(paramTypedArray.getPositionDescription() + " propertyXName or propertyYName is needed for PathData");
      setupPathMotion(PathParser.createPathFromPathData(str1), localObjectAnimator, 0.5F * paramFloat, str2, str3);
      return;
    }
    localObjectAnimator.setPropertyName(TypedArrayUtils.getNamedString(paramTypedArray, paramXmlPullParser, "propertyName", 0));
  }

  private static void setupPathMotion(Path paramPath, ObjectAnimator paramObjectAnimator, float paramFloat, String paramString1, String paramString2)
  {
    PathMeasure localPathMeasure1 = new PathMeasure(paramPath, false);
    float f1 = 0.0F;
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(Float.valueOf(0.0F));
    do
    {
      f1 += localPathMeasure1.getLength();
      localArrayList.add(Float.valueOf(f1));
    }
    while (localPathMeasure1.nextContour());
    PathMeasure localPathMeasure2 = new PathMeasure(paramPath, false);
    int i = Math.min(100, 1 + (int)(f1 / paramFloat));
    float[] arrayOfFloat1 = new float[i];
    float[] arrayOfFloat2 = new float[i];
    float[] arrayOfFloat3 = new float[2];
    int j = 0;
    float f2 = f1 / (i - 1);
    float f3 = 0.0F;
    for (int k = 0; k < i; k++)
    {
      localPathMeasure2.getPosTan(f3, arrayOfFloat3, null);
      localPathMeasure2.getPosTan(f3, arrayOfFloat3, null);
      arrayOfFloat1[k] = arrayOfFloat3[0];
      arrayOfFloat2[k] = arrayOfFloat3[1];
      f3 += f2;
      if ((j + 1 >= localArrayList.size()) || (f3 <= ((Float)localArrayList.get(j + 1)).floatValue()))
        continue;
      f3 -= ((Float)localArrayList.get(j + 1)).floatValue();
      j++;
      localPathMeasure2.nextContour();
    }
    PropertyValuesHolder localPropertyValuesHolder1 = null;
    if (paramString1 != null)
      localPropertyValuesHolder1 = PropertyValuesHolder.ofFloat(paramString1, arrayOfFloat1);
    PropertyValuesHolder localPropertyValuesHolder2 = null;
    if (paramString2 != null)
      localPropertyValuesHolder2 = PropertyValuesHolder.ofFloat(paramString2, arrayOfFloat2);
    if (localPropertyValuesHolder1 == null)
    {
      paramObjectAnimator.setValues(new PropertyValuesHolder[] { localPropertyValuesHolder2 });
      return;
    }
    if (localPropertyValuesHolder2 == null)
    {
      paramObjectAnimator.setValues(new PropertyValuesHolder[] { localPropertyValuesHolder1 });
      return;
    }
    paramObjectAnimator.setValues(new PropertyValuesHolder[] { localPropertyValuesHolder1, localPropertyValuesHolder2 });
  }

  private static class PathDataEvaluator
    implements TypeEvaluator<PathParser.PathDataNode[]>
  {
    private PathParser.PathDataNode[] mNodeArray;

    private PathDataEvaluator()
    {
    }

    PathDataEvaluator(PathParser.PathDataNode[] paramArrayOfPathDataNode)
    {
      this.mNodeArray = paramArrayOfPathDataNode;
    }

    public PathParser.PathDataNode[] evaluate(float paramFloat, PathParser.PathDataNode[] paramArrayOfPathDataNode1, PathParser.PathDataNode[] paramArrayOfPathDataNode2)
    {
      if (!PathParser.canMorph(paramArrayOfPathDataNode1, paramArrayOfPathDataNode2))
        throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
      if ((this.mNodeArray == null) || (!PathParser.canMorph(this.mNodeArray, paramArrayOfPathDataNode1)))
        this.mNodeArray = PathParser.deepCopyNodes(paramArrayOfPathDataNode1);
      for (int i = 0; i < paramArrayOfPathDataNode1.length; i++)
        this.mNodeArray[i].interpolatePathDataNode(paramArrayOfPathDataNode1[i], paramArrayOfPathDataNode2[i], paramFloat);
      return this.mNodeArray;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.graphics.drawable.AnimatorInflaterCompat
 * JD-Core Version:    0.6.0
 */