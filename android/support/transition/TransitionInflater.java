package android.support.transition;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.ViewGroup;
import java.io.IOException;
import java.lang.reflect.Constructor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TransitionInflater
{
  private static final ArrayMap<String, Constructor> CONSTRUCTORS;
  private static final Class<?>[] CONSTRUCTOR_SIGNATURE = { Context.class, AttributeSet.class };
  private final Context mContext;

  static
  {
    CONSTRUCTORS = new ArrayMap();
  }

  private TransitionInflater(@NonNull Context paramContext)
  {
    this.mContext = paramContext;
  }

  private Object createCustom(AttributeSet paramAttributeSet, Class paramClass, String paramString)
  {
    String str = paramAttributeSet.getAttributeValue(null, "class");
    if (str == null)
      throw new InflateException(paramString + " tag must have a 'class' attribute");
    try
    {
      synchronized (CONSTRUCTORS)
      {
        Constructor localConstructor = (Constructor)CONSTRUCTORS.get(str);
        if (localConstructor == null)
        {
          Class localClass = this.mContext.getClassLoader().loadClass(str).asSubclass(paramClass);
          if (localClass != null)
          {
            localConstructor = localClass.getConstructor(CONSTRUCTOR_SIGNATURE);
            localConstructor.setAccessible(true);
            CONSTRUCTORS.put(str, localConstructor);
          }
        }
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = this.mContext;
        arrayOfObject[1] = paramAttributeSet;
        Object localObject2 = localConstructor.newInstance(arrayOfObject);
        return localObject2;
      }
    }
    catch (Exception localException)
    {
    }
    throw new InflateException("Could not instantiate " + paramClass + " class " + str, localException);
  }

  private Transition createTransitionFromXml(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Transition paramTransition)
    throws XmlPullParserException, IOException
  {
    Object localObject = null;
    int i = paramXmlPullParser.getDepth();
    TransitionSet localTransitionSet;
    if ((paramTransition instanceof TransitionSet))
      localTransitionSet = (TransitionSet)paramTransition;
    while (true)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1))
        break;
      if (j != 2)
        continue;
      String str = paramXmlPullParser.getName();
      if ("fade".equals(str))
        localObject = new Fade(this.mContext, paramAttributeSet);
      while (true)
      {
        if (localObject == null)
          break label561;
        if (!paramXmlPullParser.isEmptyElementTag())
          createTransitionFromXml(paramXmlPullParser, paramAttributeSet, (Transition)localObject);
        if (localTransitionSet == null)
          break label595;
        localTransitionSet.addTransition((Transition)localObject);
        localObject = null;
        break;
        localObject = null;
        localTransitionSet = null;
        break;
        if ("changeBounds".equals(str))
        {
          localObject = new ChangeBounds(this.mContext, paramAttributeSet);
          continue;
        }
        if ("slide".equals(str))
        {
          localObject = new Slide(this.mContext, paramAttributeSet);
          continue;
        }
        if ("explode".equals(str))
        {
          localObject = new Explode(this.mContext, paramAttributeSet);
          continue;
        }
        if ("changeImageTransform".equals(str))
        {
          localObject = new ChangeImageTransform(this.mContext, paramAttributeSet);
          continue;
        }
        if ("changeTransform".equals(str))
        {
          localObject = new ChangeTransform(this.mContext, paramAttributeSet);
          continue;
        }
        if ("changeClipBounds".equals(str))
        {
          localObject = new ChangeClipBounds(this.mContext, paramAttributeSet);
          continue;
        }
        if ("autoTransition".equals(str))
        {
          localObject = new AutoTransition(this.mContext, paramAttributeSet);
          continue;
        }
        if ("changeScroll".equals(str))
        {
          localObject = new ChangeScroll(this.mContext, paramAttributeSet);
          continue;
        }
        if ("transitionSet".equals(str))
        {
          localObject = new TransitionSet(this.mContext, paramAttributeSet);
          continue;
        }
        if ("transition".equals(str))
        {
          localObject = (Transition)createCustom(paramAttributeSet, Transition.class, "transition");
          continue;
        }
        if ("targets".equals(str))
        {
          getTargetIds(paramXmlPullParser, paramAttributeSet, paramTransition);
          continue;
        }
        if ("arcMotion".equals(str))
        {
          if (paramTransition == null)
            throw new RuntimeException("Invalid use of arcMotion element");
          paramTransition.setPathMotion(new ArcMotion(this.mContext, paramAttributeSet));
          continue;
        }
        if ("pathMotion".equals(str))
        {
          if (paramTransition == null)
            throw new RuntimeException("Invalid use of pathMotion element");
          paramTransition.setPathMotion((PathMotion)createCustom(paramAttributeSet, PathMotion.class, "pathMotion"));
          continue;
        }
        if (!"patternPathMotion".equals(str))
          break label563;
        if (paramTransition == null)
          throw new RuntimeException("Invalid use of patternPathMotion element");
        paramTransition.setPathMotion(new PatternPathMotion(this.mContext, paramAttributeSet));
      }
      label561: continue;
      label563: throw new RuntimeException("Unknown scene name: " + paramXmlPullParser.getName());
      label595: if (paramTransition == null)
        continue;
      throw new InflateException("Could not add transition to another transition.");
    }
    return (Transition)localObject;
  }

  private TransitionManager createTransitionManagerFromXml(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, ViewGroup paramViewGroup)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    TransitionManager localTransitionManager = null;
    while (true)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1))
        break;
      if (j != 2)
        continue;
      String str = paramXmlPullParser.getName();
      if (str.equals("transitionManager"))
      {
        localTransitionManager = new TransitionManager();
        continue;
      }
      if ((str.equals("transition")) && (localTransitionManager != null))
      {
        loadTransition(paramAttributeSet, paramXmlPullParser, paramViewGroup, localTransitionManager);
        continue;
      }
      throw new RuntimeException("Unknown scene name: " + paramXmlPullParser.getName());
    }
    return localTransitionManager;
  }

  public static TransitionInflater from(Context paramContext)
  {
    return new TransitionInflater(paramContext);
  }

  private void getTargetIds(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Transition paramTransition)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    while (true)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1))
        break;
      if (j != 2)
        continue;
      if (paramXmlPullParser.getName().equals("target"))
      {
        TypedArray localTypedArray = this.mContext.obtainStyledAttributes(paramAttributeSet, Styleable.TRANSITION_TARGET);
        int k = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "targetId", 1, 0);
        if (k != 0)
          paramTransition.addTarget(k);
        while (true)
        {
          localTypedArray.recycle();
          break;
          int m = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "excludeId", 2, 0);
          if (m != 0)
          {
            paramTransition.excludeTarget(m, true);
            continue;
          }
          String str1 = TypedArrayUtils.getNamedString(localTypedArray, paramXmlPullParser, "targetName", 4);
          if (str1 != null)
          {
            paramTransition.addTarget(str1);
            continue;
          }
          String str2 = TypedArrayUtils.getNamedString(localTypedArray, paramXmlPullParser, "excludeName", 5);
          if (str2 != null)
          {
            paramTransition.excludeTarget(str2, true);
            continue;
          }
          String str3 = TypedArrayUtils.getNamedString(localTypedArray, paramXmlPullParser, "excludeClass", 3);
          if (str3 != null)
            try
            {
              paramTransition.excludeTarget(Class.forName(str3), true);
            }
            catch (ClassNotFoundException localClassNotFoundException)
            {
              localTypedArray.recycle();
              throw new RuntimeException("Could not create " + str3, localClassNotFoundException);
            }
          str3 = TypedArrayUtils.getNamedString(localTypedArray, paramXmlPullParser, "targetClass", 0);
          if (str3 == null)
            continue;
          paramTransition.addTarget(Class.forName(str3));
        }
      }
      throw new RuntimeException("Unknown scene name: " + paramXmlPullParser.getName());
    }
  }

  private void loadTransition(AttributeSet paramAttributeSet, XmlPullParser paramXmlPullParser, ViewGroup paramViewGroup, TransitionManager paramTransitionManager)
    throws Resources.NotFoundException
  {
    TypedArray localTypedArray = this.mContext.obtainStyledAttributes(paramAttributeSet, Styleable.TRANSITION_MANAGER);
    int i = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "transition", 2, -1);
    int j = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "fromScene", 0, -1);
    Scene localScene1;
    int k;
    if (j < 0)
    {
      localScene1 = null;
      k = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "toScene", 1, -1);
      if (k >= 0)
        break label134;
    }
    Transition localTransition;
    label134: for (Scene localScene2 = null; ; localScene2 = Scene.getSceneForLayout(paramViewGroup, k, this.mContext))
    {
      if (i < 0)
        break label163;
      localTransition = inflateTransition(i);
      if (localTransition == null)
        break label163;
      if (localScene2 != null)
        break label149;
      throw new RuntimeException("No toScene for transition ID " + i);
      localScene1 = Scene.getSceneForLayout(paramViewGroup, j, this.mContext);
      break;
    }
    label149: if (localScene1 == null)
      paramTransitionManager.setTransition(localScene2, localTransition);
    while (true)
    {
      label163: localTypedArray.recycle();
      return;
      paramTransitionManager.setTransition(localScene1, localScene2, localTransition);
    }
  }

  // ERROR //
  public Transition inflateTransition(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	android/support/transition/TransitionInflater:mContext	Landroid/content/Context;
    //   4: invokevirtual 353	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   7: iload_1
    //   8: invokevirtual 359	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   11: astore_2
    //   12: aload_0
    //   13: aload_2
    //   14: aload_2
    //   15: invokestatic 365	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   18: aconst_null
    //   19: invokespecial 147	android/support/transition/TransitionInflater:createTransitionFromXml	(Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;Landroid/support/transition/Transition;)Landroid/support/transition/Transition;
    //   22: astore 6
    //   24: aload_2
    //   25: invokeinterface 370 1 0
    //   30: aload 6
    //   32: areturn
    //   33: astore 5
    //   35: new 46	android/view/InflateException
    //   38: dup
    //   39: aload 5
    //   41: invokevirtual 373	org/xmlpull/v1/XmlPullParserException:getMessage	()Ljava/lang/String;
    //   44: aload 5
    //   46: invokespecial 108	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   49: athrow
    //   50: astore 4
    //   52: aload_2
    //   53: invokeinterface 370 1 0
    //   58: aload 4
    //   60: athrow
    //   61: astore_3
    //   62: new 46	android/view/InflateException
    //   65: dup
    //   66: new 48	java/lang/StringBuilder
    //   69: dup
    //   70: invokespecial 49	java/lang/StringBuilder:<init>	()V
    //   73: aload_2
    //   74: invokeinterface 376 1 0
    //   79: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: ldc_w 378
    //   85: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   88: aload_3
    //   89: invokevirtual 379	java/io/IOException:getMessage	()Ljava/lang/String;
    //   92: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: invokevirtual 59	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: aload_3
    //   99: invokespecial 108	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   102: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   12	24	33	org/xmlpull/v1/XmlPullParserException
    //   12	24	50	finally
    //   35	50	50	finally
    //   62	103	50	finally
    //   12	24	61	java/io/IOException
  }

  // ERROR //
  public TransitionManager inflateTransitionManager(int paramInt, ViewGroup paramViewGroup)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	android/support/transition/TransitionInflater:mContext	Landroid/content/Context;
    //   4: invokevirtual 353	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   7: iload_1
    //   8: invokevirtual 359	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   11: astore_3
    //   12: aload_0
    //   13: aload_3
    //   14: aload_3
    //   15: invokestatic 365	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   18: aload_2
    //   19: invokespecial 383	android/support/transition/TransitionInflater:createTransitionManagerFromXml	(Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;Landroid/view/ViewGroup;)Landroid/support/transition/TransitionManager;
    //   22: astore 11
    //   24: aload_3
    //   25: invokeinterface 370 1 0
    //   30: aload 11
    //   32: areturn
    //   33: astore 8
    //   35: new 46	android/view/InflateException
    //   38: dup
    //   39: aload 8
    //   41: invokevirtual 373	org/xmlpull/v1/XmlPullParserException:getMessage	()Ljava/lang/String;
    //   44: invokespecial 62	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   47: astore 9
    //   49: aload 9
    //   51: aload 8
    //   53: invokevirtual 387	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   56: pop
    //   57: aload 9
    //   59: athrow
    //   60: astore 7
    //   62: aload_3
    //   63: invokeinterface 370 1 0
    //   68: aload 7
    //   70: athrow
    //   71: astore 4
    //   73: new 46	android/view/InflateException
    //   76: dup
    //   77: new 48	java/lang/StringBuilder
    //   80: dup
    //   81: invokespecial 49	java/lang/StringBuilder:<init>	()V
    //   84: aload_3
    //   85: invokeinterface 376 1 0
    //   90: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: ldc_w 378
    //   96: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   99: aload 4
    //   101: invokevirtual 379	java/io/IOException:getMessage	()Ljava/lang/String;
    //   104: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   107: invokevirtual 59	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   110: invokespecial 62	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   113: astore 5
    //   115: aload 5
    //   117: aload 4
    //   119: invokevirtual 387	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   122: pop
    //   123: aload 5
    //   125: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   12	24	33	org/xmlpull/v1/XmlPullParserException
    //   12	24	60	finally
    //   35	60	60	finally
    //   73	126	60	finally
    //   12	24	71	java/io/IOException
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.TransitionInflater
 * JD-Core Version:    0.6.0
 */