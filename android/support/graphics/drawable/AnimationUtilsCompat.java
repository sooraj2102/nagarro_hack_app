package android.support.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class AnimationUtilsCompat
{
  private static Interpolator createInterpolatorFromXml(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject = null;
    int i = paramXmlPullParser.getDepth();
    while (true)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1))
        break;
      if (j != 2)
        continue;
      AttributeSet localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
      String str = paramXmlPullParser.getName();
      if (str.equals("linearInterpolator"))
      {
        localObject = new LinearInterpolator();
        continue;
      }
      if (str.equals("accelerateInterpolator"))
      {
        localObject = new AccelerateInterpolator(paramContext, localAttributeSet);
        continue;
      }
      if (str.equals("decelerateInterpolator"))
      {
        localObject = new DecelerateInterpolator(paramContext, localAttributeSet);
        continue;
      }
      if (str.equals("accelerateDecelerateInterpolator"))
      {
        localObject = new AccelerateDecelerateInterpolator();
        continue;
      }
      if (str.equals("cycleInterpolator"))
      {
        localObject = new CycleInterpolator(paramContext, localAttributeSet);
        continue;
      }
      if (str.equals("anticipateInterpolator"))
      {
        localObject = new AnticipateInterpolator(paramContext, localAttributeSet);
        continue;
      }
      if (str.equals("overshootInterpolator"))
      {
        localObject = new OvershootInterpolator(paramContext, localAttributeSet);
        continue;
      }
      if (str.equals("anticipateOvershootInterpolator"))
      {
        localObject = new AnticipateOvershootInterpolator(paramContext, localAttributeSet);
        continue;
      }
      if (str.equals("bounceInterpolator"))
      {
        localObject = new BounceInterpolator();
        continue;
      }
      if (str.equals("pathInterpolator"))
      {
        localObject = new PathInterpolatorCompat(paramContext, localAttributeSet, paramXmlPullParser);
        continue;
      }
      throw new RuntimeException("Unknown interpolator name: " + paramXmlPullParser.getName());
    }
    return (Interpolator)localObject;
  }

  // ERROR //
  public static Interpolator loadInterpolator(Context paramContext, int paramInt)
    throws android.content.res.Resources.NotFoundException
  {
    // Byte code:
    //   0: getstatic 124	android/os/Build$VERSION:SDK_INT	I
    //   3: bipush 21
    //   5: if_icmplt +13 -> 18
    //   8: aload_0
    //   9: iload_1
    //   10: invokestatic 128	android/view/animation/AnimationUtils:loadInterpolator	(Landroid/content/Context;I)Landroid/view/animation/Interpolator;
    //   13: astore 11
    //   15: aload 11
    //   17: areturn
    //   18: aconst_null
    //   19: astore_2
    //   20: iload_1
    //   21: ldc 129
    //   23: if_icmpne +25 -> 48
    //   26: new 131	android/support/v4/view/animation/FastOutLinearInInterpolator
    //   29: dup
    //   30: invokespecial 132	android/support/v4/view/animation/FastOutLinearInInterpolator:<init>	()V
    //   33: astore 11
    //   35: iconst_0
    //   36: ifeq -21 -> 15
    //   39: aconst_null
    //   40: invokeinterface 137 1 0
    //   45: aload 11
    //   47: areturn
    //   48: iload_1
    //   49: ldc 138
    //   51: if_icmpne +25 -> 76
    //   54: new 140	android/support/v4/view/animation/FastOutSlowInInterpolator
    //   57: dup
    //   58: invokespecial 141	android/support/v4/view/animation/FastOutSlowInInterpolator:<init>	()V
    //   61: astore 11
    //   63: iconst_0
    //   64: ifeq -49 -> 15
    //   67: aconst_null
    //   68: invokeinterface 137 1 0
    //   73: aload 11
    //   75: areturn
    //   76: iload_1
    //   77: ldc 142
    //   79: if_icmpne +25 -> 104
    //   82: new 144	android/support/v4/view/animation/LinearOutSlowInInterpolator
    //   85: dup
    //   86: invokespecial 145	android/support/v4/view/animation/LinearOutSlowInInterpolator:<init>	()V
    //   89: astore 11
    //   91: iconst_0
    //   92: ifeq -77 -> 15
    //   95: aconst_null
    //   96: invokeinterface 137 1 0
    //   101: aload 11
    //   103: areturn
    //   104: aload_0
    //   105: invokevirtual 151	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   108: iload_1
    //   109: invokevirtual 157	android/content/res/Resources:getAnimation	(I)Landroid/content/res/XmlResourceParser;
    //   112: astore_2
    //   113: aload_0
    //   114: aload_0
    //   115: invokevirtual 151	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   118: aload_0
    //   119: invokevirtual 161	android/content/Context:getTheme	()Landroid/content/res/Resources$Theme;
    //   122: aload_2
    //   123: invokestatic 163	android/support/graphics/drawable/AnimationUtilsCompat:createInterpolatorFromXml	(Landroid/content/Context;Landroid/content/res/Resources;Landroid/content/res/Resources$Theme;Lorg/xmlpull/v1/XmlPullParser;)Landroid/view/animation/Interpolator;
    //   126: astore 10
    //   128: aload 10
    //   130: astore 11
    //   132: aload_2
    //   133: ifnull -118 -> 15
    //   136: aload_2
    //   137: invokeinterface 137 1 0
    //   142: aload 11
    //   144: areturn
    //   145: astore 7
    //   147: new 118	android/content/res/Resources$NotFoundException
    //   150: dup
    //   151: new 101	java/lang/StringBuilder
    //   154: dup
    //   155: invokespecial 102	java/lang/StringBuilder:<init>	()V
    //   158: ldc 165
    //   160: invokevirtual 108	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   163: iload_1
    //   164: invokestatic 171	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   167: invokevirtual 108	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   173: invokespecial 172	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   176: astore 8
    //   178: aload 8
    //   180: aload 7
    //   182: invokevirtual 176	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   185: pop
    //   186: aload 8
    //   188: athrow
    //   189: astore 6
    //   191: aload_2
    //   192: ifnull +9 -> 201
    //   195: aload_2
    //   196: invokeinterface 137 1 0
    //   201: aload 6
    //   203: athrow
    //   204: astore_3
    //   205: new 118	android/content/res/Resources$NotFoundException
    //   208: dup
    //   209: new 101	java/lang/StringBuilder
    //   212: dup
    //   213: invokespecial 102	java/lang/StringBuilder:<init>	()V
    //   216: ldc 165
    //   218: invokevirtual 108	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   221: iload_1
    //   222: invokestatic 171	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   225: invokevirtual 108	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   228: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   231: invokespecial 172	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   234: astore 4
    //   236: aload 4
    //   238: aload_3
    //   239: invokevirtual 176	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   242: pop
    //   243: aload 4
    //   245: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   26	35	145	org/xmlpull/v1/XmlPullParserException
    //   54	63	145	org/xmlpull/v1/XmlPullParserException
    //   82	91	145	org/xmlpull/v1/XmlPullParserException
    //   104	128	145	org/xmlpull/v1/XmlPullParserException
    //   26	35	189	finally
    //   54	63	189	finally
    //   82	91	189	finally
    //   104	128	189	finally
    //   147	189	189	finally
    //   205	246	189	finally
    //   26	35	204	java/io/IOException
    //   54	63	204	java/io/IOException
    //   82	91	204	java/io/IOException
    //   104	128	204	java/io/IOException
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.graphics.drawable.AnimationUtilsCompat
 * JD-Core Version:    0.6.0
 */